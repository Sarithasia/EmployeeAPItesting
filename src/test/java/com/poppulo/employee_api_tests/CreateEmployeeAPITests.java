package com.poppulo.employee_api_tests;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.poppulo.employee.POJO.EmployeeRequest;
import com.poppulo.employee_api_basetest.BaseTest;
import com.poppulo.employee_api_methods.EmployeeAPI;
import com.poppulo.util.DataUtil;
import com.poppulo.util.ExcelReader;
import com.poppulo.util.ExtentReportListener;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CreateEmployeeAPITests extends BaseTest 
{
	Logger log = LogManager.getLogger(CreateEmployeeAPITests.class);

	int EMPLOYEE_COUNT_IN_EXCEL;
	EmployeeAPI EmployeeAPI;
	
	private static ExcelReader excel;
	SoftAssert softassert;
	HashSet<Integer> id = new HashSet<>();

	public CreateEmployeeAPITests() throws IOException 
	{
		super();

		setFilePath(prop.getProperty("filePathToExcelForCreation"));
		setSheetName(prop.getProperty("CreateUserSheetName"));
		excel = new ExcelReader(getFilePath());
		softassert = new SoftAssert();

		EmployeeAPI = new EmployeeAPI();
	}

	@DataProvider(name = "data")
	public static Object[][] Data(Method m) 
	{
		return DataUtil.getData(m, BaseTest.getSheetName(), excel);
	}

	@Test(priority = 1, dataProvider = "data")
	public void ValidateCustomerApiWithValidData(HashMap<String, String> employeeData) {

		log.info("Starting test with data: {}", employeeData);

		EmployeeRequest request = new EmployeeRequest(employeeData.get("name"), employeeData.get("job"));
		// Send POST request
		Response response = EmployeeAPI.createEmployee(request, "createUserEndPoint");

		log.debug("Response status code: {}", response.getStatusCode());
		log.debug("Response body: {}", response.getBody().asString());

		// Print response and status code
		response.prettyPrint();

		// Validate the response status code
		String employeeId = response.jsonPath().getString("id");
		Assert.assertEquals(response.getStatusCode(), 201,
				"Expected status code 201 but got " + response.getStatusCode());
		Assert.assertNotNull(employeeId);
		log.info("Test successfully completed.");

	}

//
	@Test(priority = 2, dataProvider = "data")
	public void ValidateCustomerApiWithInValidData(HashMap<String, String> employeeData) {

		// Send POST request

		EmployeeRequest request = new EmployeeRequest(employeeData.get("name"), employeeData.get("job"));
		// Send POST request
		Response response = EmployeeAPI.createEmployee(request, "createUserEndPoint");
		log.debug("Response status code: {}", response.getStatusCode());
		log.debug("Response body: {}", response.getBody().asString());
		// Print response and status code
		response.prettyPrint();

		// Validate the response status code
		softassert.assertEquals(response.getStatusCode(), 400,
				"Able to create employee with invalid data,Expected status code 400 but got "
						+ response.getStatusCode());
		log.info("Test successfully completed.");

	}

	@Test(priority = 3, dataProvider = "data", threadPoolSize = 2)
	public void ValidateMultipleEmployeeCReationWithSameDataAndVerifyUniqueIdForEachEmployee(
			HashMap<String, String> employeeData) {

		EMPLOYEE_COUNT_IN_EXCEL = employeeData.size();

		// Send POST request

		EmployeeRequest request = new EmployeeRequest(employeeData.get("name"), employeeData.get("job"));
		// Send POST request
		Response response = EmployeeAPI.createEmployee(request, "createUserEndPoint");
		id.add(response.jsonPath().getInt("id"));
		log.debug("Response status code: {}", response.getStatusCode());
		log.debug("Response body: {}", response.getBody().asString());
		// Print response and status code
		response.prettyPrint();

		// Validate the response status code
		softassert.assertEquals(response.getStatusCode(), 400,
				"Able to create employee with invalid data,Expected status code 400 but got "
						+ response.getStatusCode());
		log.info("Test successfully completed.");

	}

	@Test(priority = 4, dependsOnMethods = "ValidateMultipleEmployeeCReationWithSameDataAndVerifyUniqueIdForEachEmployee")
	public void verifyUniqueEmployeeIds() {
		// Verify HashSet size to ensure all IDs are unique
		softassert.assertEquals(id.size(), EMPLOYEE_COUNT_IN_EXCEL,
				"Number of unique IDs not match the number of test data rows");

		softassert.assertAll(); // Assert all soft asserts
		log.info("Test successfully completed.");

	}

}
