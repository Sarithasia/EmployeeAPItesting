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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.poppulo.employee.POJO.EmployeeRequest;
import com.poppulo.employee_api_basetest.BaseTest;
import com.poppulo.employee_api_methods.RequestHandler;
import com.poppulo.employee_api_methods.ResponseHandler;

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
	Logger log ;
	int EMPLOYEE_COUNT_IN_EXCEL;
	RequestHandler RequestHandler;
	ResponseHandler ResponseHandler;
	
	private   ExcelReader excel;
	SoftAssert softassert;
	HashSet<Integer> id = new HashSet<>();
	DataUtil DataUtil;

	public CreateEmployeeAPITests() throws IOException 
	{
		super();
		 log = LogManager.getLogger(CreateEmployeeAPITests.class);
		DataUtil =new DataUtil();		
		setFilePath(System.getProperty("user.dir")+prop.getProperty("filePathToExcelForCreation"));
		setSheetName(prop.getProperty("CreateUserSheetName"));
		excel = new ExcelReader(getFilePath());

	}
	
	@BeforeMethod()
	public void Initialization() throws IOException 
	{
		
		softassert = new SoftAssert();
		RequestHandler = new RequestHandler();
		ResponseHandler =new ResponseHandler();
	}

	@DataProvider(name = "data")
	public  Object[][] Data(Method m) throws IOException 
	{
		return DataUtil.getData(m, getSheetName(), excel);
	}

	@Test(priority = 1, dataProvider= "data")
	public void TC001_ValidateCustomerApiWithValidData(HashMap<String, String> employeeData) {

		log.info("Starting test with data: {}", employeeData);

		EmployeeRequest request = new EmployeeRequest(employeeData.get("name"), employeeData.get("job"));
		Response response = RequestHandler.createEmployee(request, "createUserEndPoint");
		ResponseHandler.validateNotNullResponseData(response, 201,"id");

	}

//
	@Test(priority = 2, dataProvider = "data")
	public void TC002_ValidateCustomerApiWithInValidData(HashMap<String, String> employeeData) {


		EmployeeRequest request = new EmployeeRequest(employeeData.get("name"), employeeData.get("job"));
		// Send POST request
		Response response = RequestHandler.createEmployee(request, "createUserEndPoint");
		ResponseHandler.handleApiResponseCode(response,400,"Able to create employee with invalid data");

		log.info("Test successfully completed.");

	}

	@Test(priority = 3, dataProvider = "data")
	public void TC003_ValidateMultipleEmployeeCReationWithSameDataAndVerifyUniqueIdForEachEmployee(
			HashMap<String, String> employeeData) {

		EMPLOYEE_COUNT_IN_EXCEL = employeeData.size();

		// Send POST request

		EmployeeRequest request = new EmployeeRequest(employeeData.get("name"), employeeData.get("job"));
		// Send POST request
		Response response = RequestHandler.createEmployee(request, "createUserEndPoint");
		id.add(response.jsonPath().getInt("id"));
		ResponseHandler.handleApiResponseCode(response, 201);
		log.info("Test successfully completed.");

	}

	@Test(priority = 4, dependsOnMethods = "TC003_ValidateMultipleEmployeeCReationWithSameDataAndVerifyUniqueIdForEachEmployee")
	public void TC004_ValidateUniqueEmployeeIds() {
		// Verify HashSet size to ensure all IDs are unique
		softassert.assertEquals(id.size(), EMPLOYEE_COUNT_IN_EXCEL,
				"Number of unique IDs not match the number of test data rows");

		softassert.assertAll(); // Assert all soft asserts
		log.info("Test successfully completed.");

	}
	
	@Test(priority = 5, dataProvider = "data")
	public void TC004__ValidateCreateAPILatency(HashMap<String, String> employeeData) {
		
        // Send API request and measure response time
        long startTime = System.currentTimeMillis();
        EmployeeRequest request = new EmployeeRequest(employeeData.get("name"), employeeData.get("job"));
		Response response = RequestHandler.createEmployee(request, "createUserEndPoint");
		ResponseHandler.handleResponseTime(response,startTime, 201, 2000);
		log.info("Test successfully completed.");


        
    }


}
