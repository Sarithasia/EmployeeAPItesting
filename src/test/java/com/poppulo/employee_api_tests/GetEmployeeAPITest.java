package com.poppulo.employee_api_tests;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.poppulo.employee.POJO.SupportData;
import com.poppulo.employee.POJO.User;
import com.poppulo.employee.POJO.UserDataResponse;
import com.poppulo.employee_api_basetest.BaseTest;
import com.poppulo.employee_api_methods.EmployeeAPI;
import com.poppulo.util.DataUtil;
import com.poppulo.util.ExcelReader;

import io.restassured.response.Response;

public class GetEmployeeAPITest extends BaseTest {
	private static ExcelReader excel;
	EmployeeAPI EmployeeAPI;

	public GetEmployeeAPITest() throws IOException {
		super();
		EmployeeAPI = new EmployeeAPI();
		setFilePath(prop.getProperty("filePathToExcelForGet"));
		setSheetName(prop.getProperty("UpdateUserSheetName"));
		excel = new ExcelReader(getFilePath());
		

	}

	
	@DataProvider(name = "getdata")
	public static Object[][] Data(Method m) 
	{
		return DataUtil.getData(m, getSheetName(), excel);
	}
	
	@Test(priority = 1,  dataProvider = "getdata")

	public void testGetUserDataSuccess(HashMap<String, String> data) {

		Response response = EmployeeAPI.getEmployee(2, "getUserEndPoint");

		// Validate using POJO fields
		UserDataResponse userDataResponse = response.as(UserDataResponse.class);
		User user = userDataResponse.getData();

		SupportData supportData = userDataResponse.getSupport();

		// Assertions using POJO data'
		SoftAssert softAssert = new SoftAssert();
		try {
			int expectedId = Double.valueOf(data.get("id")).intValue();
			softAssert.assertEquals(user.getId(), expectedId, "ID mismatch");
		} catch (NumberFormatException e) {
			softAssert.fail("Invalid ID format in test data: " + data.get("id"));
		}
		softAssert.assertEquals(response.getStatusCode(), 200, "Incorrect status code for successful get request");
		softAssert.assertEquals(user.getEmail(), data.get("email"), "Email mismatch");
		softAssert.assertEquals(user.getFirst_name(), data.get("first_name"), "First name mismatch");
		softAssert.assertEquals(user.getLast_name(), data.get("last_name"), "Last name mismatch");
		softAssert.assertEquals(user.getAvatar(), data.get("avatar"), "Avatar mismatch");
		softAssert.assertEquals(supportData.getUrl(), data.get("url"), "Support URL mismatch");
		softAssert.assertEquals(supportData.getText(), data.get("text"), "Support text mismatch");
		softAssert.assertAll();
	}

	@Test(priority = 2)
	public void testGetNonExistentUser() {
		Response response = EmployeeAPI.getEmployee(999, "getUserEndPoint");
		Assert.assertEquals(404, response.getStatusCode(), "Incorrect status code for no content ");
	}

}
