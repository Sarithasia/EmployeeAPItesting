package com.poppulo.employee_api_tests;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.poppulo.employee_api_basetest.BaseTest;
import com.poppulo.employee_api_methods.EmployeeAPI;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class DeleteEmployeeTest extends BaseTest {
	SoftAssert soft;
	EmployeeAPI EmployeeAPI;

	Logger log = LogManager.getLogger(DeleteEmployeeTest.class);

	public DeleteEmployeeTest() throws IOException 
	{
		super();
		
	}
	
	@BeforeMethod()
	public void Initialization() throws IOException 
	{	
		     				
		soft = new SoftAssert();
		EmployeeAPI = new EmployeeAPI();
	}
	
	
	@Test(priority = 1)
	public void TCD001_ValidateValidEmployeeDeletion() {
		int validEmployeeId = 971;

		Response response = EmployeeAPI.deleteEmployee("deleteUserEndPoint", validEmployeeId);

		response.prettyPrint();

		Assert.assertEquals(response.getStatusCode(), 204, "Expected status code 204 (Not Found)");

	}

	@Test(priority = 2)
	public void TCD002_ValidatetNonExistentEmployeeDeletion() {
		int nonExistentEmployeeId = -9999; // Replace with a non-existent employee ID
		Response response = EmployeeAPI.deleteEmployee("deleteUserEndPoint", nonExistentEmployeeId);
		response.prettyPrint();

		Assert.assertEquals(response.getStatusCode(), 404,
				"Expected status code 404 (Not Found),Able to delete NonExistent employee");
	}

	@Test(priority = 3)
	public void TCD003_ValidatetDeletionOfEmployeeWithInvalidIdFormat() {
		String invalidEmployeeId = "a$%bc"; // Invalid employee ID format
		Response response = EmployeeAPI.deleteEmployee("deleteUserEndPoint", invalidEmployeeId);
		response.prettyPrint();

		Assert.assertEquals(response.getStatusCode(), 400,
				"Expected status code 400 (Bad Request),Able to delete  employee with InvalidIdFormat");
	}

	@Test(priority = 4)
	public void TCD004_ValidateDeleteEmployeeWithEmptyId() {
		String emptyEmployeeId = ""; // Empty employee ID
		Response response = EmployeeAPI.deleteEmployee("deleteUserEndPoint", emptyEmployeeId);
		response.prettyPrint();

		Assert.assertEquals(response.getStatusCode(), 400,
				"Expected status code 400 (Bad Request),Able to delete  employee with empty id");
	}

	@Test(priority = 5)
	public void TCD005_ValidateDeleteEmployeeWithMalformedRequest() {
		int employeeIdToDelete = 2;

		// Sending DELETE request without required headers

		Response response = RestAssured.given()
				// .header("Content-Type", "application/json") // Missing header intentionally
				.delete("/employees/" + employeeIdToDelete);
		response.prettyPrint();
		Assert.assertEquals(response.getStatusCode(), 400,
				"Expected status code 400 (Bad Request),Able to delete  employee with malformed delete request");
	}

	@Test(priority = 6)
	public void TCD006_ValidateSimultaneousDeleteOperations() {

		// Assume we have multiple employee IDs to delete simultaneously
		int[] employeeIds = { 123, 124, 125 };

		// Perform delete operations for each employee ID
		for (int id : employeeIds) {
			Response response = EmployeeAPI.deleteEmployee("deleteUserEndPoint", id);
			response.prettyPrint();

			soft.assertEquals(response.getStatusCode(), 204, "Expected status code 200 (OK) for employee ID: " + id);
		}

		// Verify that all employees have been successfully deleted
		for (int id : employeeIds) {
			int status = EmployeeAPI.getEmployee(id, "createUserEndPoint").getStatusCode();
			soft.assertEquals(status, 200,
					"Employees are listing even after deletion,Expected status code 200 (Not Found) for employee ID: "
							+ id);

		}
		soft.assertAll();
	}
	@Test(priority = 7)

	public void TCD007_ValidateDeletetAPILatency() {
		
        // Send API request and measure response time
        long startTime = System.currentTimeMillis();
        int EmployeeId =  2; // Replace with a non-existent employee ID
		Response response = EmployeeAPI.deleteEmployee("deleteUserEndPoint", EmployeeId);

        long endTime = System.currentTimeMillis();

        // Calculate response time in milliseconds
        long responseTime = endTime - startTime;
        System.out.println("API Response Time: " + responseTime + " milliseconds");

        // Validate response status code
        soft.assertEquals(response.getStatusCode(), 204, "Expected status code 204 but found " + response.getStatusCode());

        soft.assertTrue(responseTime <= 2000, "Response time exceeds threshold of 2000 milliseconds,actual response is :"+responseTime);
        soft.assertAll();
    }

}
