package com.poppulo.employee_api_tests;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.poppulo.employee_api_basetest.BaseTest;
import com.poppulo.employee_api_methods.EmployeeAPI;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class DeleteEmployeeTest extends BaseTest {
	SoftAssert soft;
	EmployeeAPI EmployeeAPI;

	public DeleteEmployeeTest() throws IOException {
		super();
		EmployeeAPI = new EmployeeAPI();
		soft = new SoftAssert();

	}

	@Test(priority = 1)
	public void testVerifyEmployeeDeletion() {
		int validEmployeeId = 971;

		Response response = EmployeeAPI.deleteEmployee("deleteUserEndPoint", validEmployeeId);

		response.prettyPrint();

		Assert.assertEquals(response.getStatusCode(), 204, "Expected status code 404 (Not Found)");

	}

	@Test(priority = 2)
	public void testDeleteNonExistentEmployee() {
		int nonExistentEmployeeId = -9999; // Replace with a non-existent employee ID
		Response response = EmployeeAPI.deleteEmployee("deleteUserEndPoint", nonExistentEmployeeId);
		Assert.assertEquals(response.getStatusCode(), 404,
				"Expected status code 404 (Not Found),Able to delete NonExistent employee");
	}

	@Test(priority = 3)
	public void testDeleteEmployeeWithInvalidIdFormat() {
		String invalidEmployeeId = "a$%bc"; // Invalid employee ID format
		Response response = EmployeeAPI.deleteEmployee("deleteUserEndPoint", invalidEmployeeId);

		Assert.assertEquals(response.getStatusCode(), 400,
				"Expected status code 400 (Bad Request),Able to delete  employee with InvalidIdFormat");
	}

	@Test(priority = 4)
	public void testDeleteEmployeeWithEmptyId() {
		String emptyEmployeeId = ""; // Empty employee ID
		Response response = EmployeeAPI.deleteEmployee("deleteUserEndPoint", emptyEmployeeId);

		Assert.assertEquals(response.getStatusCode(), 400,
				"Expected status code 400 (Bad Request),Able to delete  employee with empty id");
	}

	@Test(priority = 5)
	public void testDeleteEmployeeWithMalformedRequest() {
		int employeeIdToDelete = 2;

		// Sending DELETE request without required headers

		Response response = RestAssured.given()
				// .header("Content-Type", "application/json") // Missing header intentionally
				.delete("/employees/" + employeeIdToDelete);

		Assert.assertEquals(response.getStatusCode(), 400,
				"Expected status code 400 (Bad Request),Able to delete  employee with malformed delete request");
	}

	@Test(priority = 6)
	public void testSimultaneousDeleteOperations() {

		// Assume we have multiple employee IDs to delete simultaneously
		int[] employeeIds = { 123, 124, 125 };

		// Perform delete operations for each employee ID
		for (int id : employeeIds) {
			Response response = EmployeeAPI.deleteEmployee("deleteUserEndPoint", id);

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

}
