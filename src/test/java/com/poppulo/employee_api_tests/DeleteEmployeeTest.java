package com.poppulo.employee_api_tests;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.poppulo.employee_api_basetest.BaseTest;
import com.poppulo.employee_api_methods.RequestHandler;
import com.poppulo.employee_api_methods.ResponseHandler;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class DeleteEmployeeTest extends BaseTest {
	SoftAssert soft;
	RequestHandler RequestHandler;
	ResponseHandler ResponseHandler;


	Logger log ;

	public DeleteEmployeeTest() throws IOException 
	{
		super();
		log = LogManager.getLogger(DeleteEmployeeTest.class);

	}
	
	@BeforeMethod()
	public void Initialization() throws IOException 
	{	
		     				
		soft = new SoftAssert();
		RequestHandler = new RequestHandler();
		ResponseHandler =new ResponseHandler();

	}
	
	
	@Test(priority = 1)
	public void TCD001_ValidateValidEmployeeDeletion() {
		int validEmployeeId = 971;

		Response response = RequestHandler.deleteEmployee("deleteUserEndPoint", validEmployeeId);
		log.info("Starting test TCD001_ValidateValidEmployeeDeletion");
		ResponseHandler.handleApiResponseCode(response,204);
		log.info("TCD001_ValidateValidEmployeeDeletion: Test successfully completed.");

	}

	@Test(priority = 2)
	public void TCD002_ValidatetNonExistentEmployeeDeletion() {
		int nonExistentEmployeeId = -9999; // Replace with a non-existent employee ID
		Response response = RequestHandler.deleteEmployee("deleteUserEndPoint", nonExistentEmployeeId);
		ResponseHandler.handleApiResponseCode(response,204);
		log.info("Test successfully completed.");

	}

	@Test(priority = 3)
	public void TCD003_ValidatetDeletionOfEmployeeWithInvalidIdFormat() {
		String invalidEmployeeId = "a$%bc"; // Invalid employee ID format
		Response response = RequestHandler.deleteEmployee("deleteUserEndPoint", invalidEmployeeId);
		ResponseHandler.handleApiResponseCode(response,204,"Able to delete  employee with InvalidIdFormat");

		log.info("Test successfully completed.");

	}

	@Test(priority = 4)
	public void TCD004_ValidateDeleteEmployeeWithEmptyId() {
		String emptyEmployeeId = ""; // Empty employee ID
		Response response = RequestHandler.deleteEmployee("deleteUserEndPoint", emptyEmployeeId);
		ResponseHandler.handleApiResponseCode(response,204,"Able to delete  employee with empty id");	
		log.info("Test successfully completed.");

	}

	@Test(priority = 5)
	public void TCD005_ValidateDeleteEmployeeWithMalformedRequest() {
		int employeeIdToDelete = 2;

		// Sending DELETE request without required headers

		Response response = RestAssured.given()
				// .header("Content-Type", "application/json") // Missing header intentionally
				.delete("/employees/" + employeeIdToDelete);
		ResponseHandler.handleApiResponseCode(response,400,"Able to delete  employee with malformed delete request");	
		log.info("Test successfully completed.");

	}

	@Test(priority = 6)
	public void TCD006_ValidateSimultaneousDeleteOperations() {

		// Assume we have multiple employee IDs to delete simultaneously
		int[] employeeIds = { 123, 124, 125 };

		// Perform delete operations for each employee ID
		for (int id : employeeIds) {
			Response response = RequestHandler.deleteEmployee("deleteUserEndPoint", id);
			response.prettyPrint();
			log.debug("Response status code: {}", response.getStatusCode());
			log.debug("Response body: {}", response.getBody().asString());

			soft.assertEquals(response.getStatusCode(), 204, "Expected status code 200 (OK) for employee ID: " + id);
		}

		// Verify that all employees have been successfully deleted
		for (int id : employeeIds) {
			int status = RequestHandler.getEmployee(id, "createUserEndPoint").getStatusCode();
			soft.assertEquals(status, 200,
					"Employees are listing even after deletion,Expected status code 200 (Not Found) for employee ID: "
							+ id);

		}
		soft.assertAll();
		log.info("Test successfully completed.");

	}
	@Test(priority = 7)

	public void TCD007_ValidateDeletetAPILatency() {
		
        // Send API request and measure response time
        long startTime = System.currentTimeMillis();
        int EmployeeId =  2; // Replace with a non-existent employee ID
		Response response = RequestHandler.deleteEmployee("deleteUserEndPoint", EmployeeId);
		ResponseHandler.handleResponseTime(response,startTime, 204, 2000);
		log.info("Test successfully completed.");

    }

}
