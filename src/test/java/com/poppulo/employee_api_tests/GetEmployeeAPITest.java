package com.poppulo.employee_api_tests;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.poppulo.employee.POJO.SupportData;
import com.poppulo.employee.POJO.User;
import com.poppulo.employee.POJO.UserDataResponse;
import com.poppulo.employee_api_basetest.BaseTest;
import com.poppulo.employee_api_methods.RequestHandler;
import com.poppulo.employee_api_methods.ResponseHandler;
import com.poppulo.util.DataUtil;
import com.poppulo.util.ExcelReader;

import io.restassured.response.Response;

public class GetEmployeeAPITest extends BaseTest 
{
	private  ExcelReader excel;
	RequestHandler RequestHandler;
	ResponseHandler ResponseHandler;

	DataUtil DataUtil;
	Logger log ;
	SoftAssert	softAssert;
	
	public GetEmployeeAPITest() throws IOException
	{
		super();
		 log = LogManager.getLogger(GetEmployeeAPITest.class);
		setFilePath(System.getProperty("user.dir")+prop.getProperty("filePathToExcelForGet"));
		setSheetName(prop.getProperty("GetUserSheetName"));
		excel = new ExcelReader(getFilePath());
		DataUtil =new DataUtil();



	}
	
	@BeforeMethod()
	public void Initialization() throws IOException 
	{	
		     
				
		 softAssert = new SoftAssert();
		 RequestHandler = new RequestHandler();
		 ResponseHandler =new ResponseHandler();

	}

	
	@DataProvider(name = "getdata")
	public  Object[][] Data(Method m) throws IOException 
	{
		return DataUtil.getData(m, getSheetName(), excel);
	}
	
	@Test(priority = 1, dataProvider = "getdata")

	public void TCG01_ValidateGetRequestForValidUser(HashMap<String, String> data) 
	{
		

		log.info("Starting test with data: {}", data);

		Response response = RequestHandler.getEmployee(2, "getUserEndPoint");
		response.prettyPrint();

		log.debug("Response status code: {}", response.getStatusCode());
		log.debug("Response body: {}", response.getBody().asString());

		// Validate using POJO fields
		UserDataResponse userDataResponse = response.as(UserDataResponse.class);
		User user = userDataResponse.getData();

		SupportData supportData = userDataResponse.getSupport();

		// Assertions using POJO data'
		try 
		{
			int expectedId = Double.valueOf(data.get("id")).intValue();
			softAssert.assertEquals(user.getId(), expectedId, "ID mismatch");
		} 
		catch (NumberFormatException e) 
		{
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
		log.info("Test successfully completed.");

	}

	@Test(priority = 2)
	public void TCG02_ValidateGetRequestForNonExistentUser() 
	{
		
		Response response = RequestHandler.getEmployee(999, "getUserEndPoint");
		ResponseHandler.handleApiResponseCode(response,404,"Incorrect status code for no content ");		
		log.info("Test successfully completed.");

	}
	
	@Test(priority = 3)

	public void TCG03_ValidateGetAPILatency() {
		
        // Send API request and measure response time
        long startTime = System.currentTimeMillis();
		Response response = RequestHandler.getEmployee(2, "getUserEndPoint");
		ResponseHandler.handleResponseTime(response,startTime, 200, 2000);

        softAssert.assertAll();
    }
	

}
