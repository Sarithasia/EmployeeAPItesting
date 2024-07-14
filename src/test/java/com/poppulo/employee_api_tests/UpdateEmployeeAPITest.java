package com.poppulo.employee_api_tests;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import com.poppulo.employee.POJO.EmployeeRequest;
import com.poppulo.employee.POJO.EmployeeResponse;
import com.poppulo.employee_api_basetest.BaseTest;
import com.poppulo.employee_api_methods.EmployeeAPI;
import com.poppulo.util.DataUtil;
import com.poppulo.util.ExcelReader;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import io.restassured.response.Response;

public class UpdateEmployeeAPITest extends BaseTest {

	EmployeeAPI EmployeeAPI;
	public   ExcelReader excel;
	DataUtil  DataUtil;
	SoftAssert softassert;

	Logger log ;

	public UpdateEmployeeAPITest() throws IOException 
	{
		
		super();	
		DataUtil =new DataUtil();
		 log = LogManager.getLogger(UpdateEmployeeAPITest.class);
		setFilePath(System.getProperty("user.dir")+prop.getProperty("filePathToExcelForUpdation"));
		setSheetName(prop.getProperty("UpdateUserSheetName"));
		excel = new ExcelReader(getFilePath());

	}
	
	@BeforeMethod()
	public void Initialization() throws IOException 
	{
		
		softassert = new SoftAssert();

		EmployeeAPI = new EmployeeAPI();
	}

	@DataProvider(name = "updateData")
	public  Object[][] Data(Method m) throws IOException 
	{
		return DataUtil.getData(m, getSheetName(), excel);
	}

	@Test(priority = 1, dataProvider = "updateData")
	public void TCUO1_validateValidEmployeeUpdate(HashMap<String, String> UpdateData) {
		int employeeId = 2;
		
		EmployeeRequest request = new EmployeeRequest(UpdateData.get("name"), UpdateData.get("job"));
		Response response = EmployeeAPI.updateEmployee("updateUserEndPoint", employeeId, request);
		response.prettyPrint();
		log.debug("Response status code: {}", response.getStatusCode());
		log.debug("Response body: {}", response.getBody().asString());
		softassert.assertEquals(response.getStatusCode(), 200);

		// Map response body to EmployeeResponse POJO
		EmployeeResponse updatedEmployee = response.as(EmployeeResponse.class);

		softassert.assertEquals(updatedEmployee.getName(), UpdateData.get("name"));
		softassert.assertEquals(updatedEmployee.getJob(), UpdateData.get("job"));
		softassert.assertAll();
		log.info("Test successfully completed.");

	}

	@Test(priority = 2, dataProvider = "updateData")
	public void TCU02_validateUpdationWithInvalidEmployeeId(HashMap<String, String> updateData) {

		int employeeId = -99;
		
		EmployeeRequest request = new EmployeeRequest(updateData.get("name"), updateData.get("job"));
		Response response = EmployeeAPI.updateEmployee(prop.getProperty("updateUserEndPoint"), employeeId, request);
		response.prettyPrint();
		log.debug("Response status code: {}", response.getStatusCode());
		log.debug("Response body: {}", response.getBody().asString());
		Assert.assertEquals(response.getStatusCode(), 404, "Able to update invalid employeeID details");
		log.info("Test successfully completed.");

	}

	@Test(dataProvider = "updateData", priority = 3)
	public void TCU03_validateDataUpdationOfValidEmployeeWithInvalidData(HashMap<String, String> updateData) {
		int employeeId = 2;
		
		EmployeeRequest request = new EmployeeRequest(updateData.get("name"), updateData.get("job"));
		Response response = EmployeeAPI.updateEmployee(prop.getProperty("updateUserEndPoint"), employeeId, request);
		response.prettyPrint();
		log.debug("Response status code: {}", response.getStatusCode());
		log.debug("Response body: {}", response.getBody().asString());

		// Verify response status code
		Assert.assertEquals(response.getStatusCode(), 404, "Able to update valid employee with invalid details");
		log.info("Test successfully completed.");

	}

	@Test(priority = 4, enabled = false)
	public void TCU04_validateEmptyEmployeeDataUpdate(HashMap<String, String> updateData) 
	{
		int employeeId = 2;
		String name = " ";
		String job = " ";

		EmployeeRequest request = new EmployeeRequest(name, job);
		Response response = EmployeeAPI.updateEmployee(prop.getProperty("updateUserEndPoint"), employeeId, request);
		response.prettyPrint();
		log.debug("Response status code: {}", response.getStatusCode());
		log.debug("Response body: {}", response.getBody().asString());
		Assert.assertEquals(response.getStatusCode(), 404, "Able to update valid employee with invalid details");
		log.info("Test successfully completed.");

	}
	
	@Test(priority = 5, dataProvider = "updateData")
	public void TC005__ValidateUpdateAPILatency(HashMap<String, String> updateData) {
		
        
        int employeeId = 2;
     // Send API request and measure response time
        long startTime = System.currentTimeMillis();
        EmployeeRequest request = new EmployeeRequest(updateData.get("name"), updateData.get("job"));
		Response response = EmployeeAPI.updateEmployee("updateUserEndPoint", employeeId, request);
        long endTime = System.currentTimeMillis();
		log.debug("Response status code: {}", response.getStatusCode());
		log.debug("Response body: {}", response.getBody().asString());
        // Calculate response time in milliseconds
        long responseTime = endTime - startTime;
        System.out.println("API Response Time: " + responseTime + " milliseconds");

        // Validate response status code
        softassert.assertEquals(response.getStatusCode(), 200, "Expected status code 200 but found " + response.getStatusCode());
        softassert.assertTrue(responseTime <= 2000, "Response time exceeds threshold of 2000 milliseconds,actual response is : "+responseTime);
		softassert.assertAll(); // Assert all soft asserts
		log.info("Test successfully completed.");

        
    }

}
