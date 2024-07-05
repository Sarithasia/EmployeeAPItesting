package com.poppulo.employee_api_tests;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.testng.Assert;

import com.poppulo.employee.POJO.EmployeeRequest;
import com.poppulo.employee.POJO.EmployeeResponse;
import com.poppulo.employee_api_basetest.BaseTest;
import com.poppulo.employee_api_methods.EmployeeAPI;
import com.poppulo.util.DataUtil;
import com.poppulo.util.ExcelReader;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import io.restassured.response.Response;

public class UpdateEmployeeAPITest extends BaseTest {

	EmployeeAPI EmployeeAPI;
	public static  ExcelReader excel;
	DataUtil  DataUtil;



	public UpdateEmployeeAPITest() throws IOException {
		
		super();	
		DataUtil =new DataUtil();

		setFilePath(prop.getProperty("filePathToExcelForUpdation"));
		setSheetName(prop.getProperty("UpdateUserSheetName"));
		excel = new ExcelReader(getFilePath());
		EmployeeAPI = new EmployeeAPI();

	}

	@DataProvider(name = "updateData")
	public  Object[][] Data(Method m) throws IOException 
	{
		return DataUtil.getData(m, getSheetName(), excel);
	}

	@Test(priority = 1, dataProvider = "updateData")
	public void validateValidEmployeeUpdate(HashMap<String, String> UpdateData) {
		int employeeId = 2;
		String name = UpdateData.get("name");
		String job = UpdateData.get("job");

		EmployeeRequest request = new EmployeeRequest(name, job);
		Response response = EmployeeAPI.updateEmployee("updateUserEndPoint", employeeId, request);

		// Verify response status code
		Assert.assertEquals(response.getStatusCode(), 200);

		// Map response body to EmployeeResponse POJO
		EmployeeResponse updatedEmployee = response.as(EmployeeResponse.class);

		Assert.assertEquals(updatedEmployee.getName(), name);
		Assert.assertEquals(updatedEmployee.getJob(), job);
	}

	@Test(priority = 2, dataProvider = "updateData")
	public void validateInvalidEmployeeIdUpdate(HashMap<String, String> updateData) {

		int employeeId = -99;
		String name = updateData.get("name");
		String job = updateData.get("job");

		EmployeeRequest request = new EmployeeRequest(name, job);
		Response response = EmployeeAPI.updateEmployee(prop.getProperty("updateUserEndPoint"), employeeId, request);

		Assert.assertEquals(response.getStatusCode(), 404, "Able to update invalid employeeID details");
	}

	@Test(dataProvider = "updateData", priority = 3)
	public void validateInvalidEmployeeDataUpdate(HashMap<String, String> updateData) {
		int employeeId = 2;
		String name = updateData.get("name");
		String job = updateData.get("job");

		EmployeeRequest request = new EmployeeRequest(name, job);
		Response response = EmployeeAPI.updateEmployee(prop.getProperty("updateUserEndPoint"), employeeId, request);

		// Verify response status code
		Assert.assertEquals(response.getStatusCode(), 404, "Able to update valid employee with invalid details");
	}

	@Test(priority = 4, enabled = false)
	public void validateEmptyEmployeeDataUpdate(HashMap<String, String> updateData) {
		int employeeId = 2;
		String name = " ";
		String job = " ";

		EmployeeRequest request = new EmployeeRequest(name, job);
		Response response = EmployeeAPI.updateEmployee(prop.getProperty("updateUserEndPoint"), employeeId, request);

		// Verify response status code
		Assert.assertEquals(response.getStatusCode(), 404, "Able to update valid employee with invalid details");
	}

}
