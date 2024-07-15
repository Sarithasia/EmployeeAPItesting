package com.poppulo.employee_api_methods;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.asserts.SoftAssert;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ResponseHandler {
    private Logger log = LogManager.getLogger(ResponseHandler.class);

    public void handleApiResponseCode(Response response, int expectedStatusCode,String msg) {
        SoftAssert softAssert = new SoftAssert();

        response.prettyPrint();
        log.debug("Response status code: {}", response.getStatusCode());
        log.debug("Response body: {}", response.getBody().asString());

        softAssert.assertEquals(response.getStatusCode(), expectedStatusCode,msg+
                " Expected status code " + expectedStatusCode + " but found " + response.getStatusCode());

        softAssert.assertAll();
        log.info("handleApiResponseCode method Test successfully completed.");
    }
    
    public void handleApiResponseCode(Response response, int expectedStatusCode) {
        SoftAssert softAssert = new SoftAssert();

        response.prettyPrint();
        log.debug("Response status code: {}", response.getStatusCode());
        log.debug("Response body: {}", response.getBody().asString());

        softAssert.assertEquals(response.getStatusCode(), expectedStatusCode,
                " Expected status code " + expectedStatusCode + " but found " + response.getStatusCode());

        softAssert.assertAll();
        log.info("handleApiResponseCode method successfully completed.");
    }
    
    public void validateNotNullResponseData(Response response,int expectedStatusCode, String jsonPath) {
        SoftAssert softAssert = new SoftAssert();
    	log.debug("Response status code: {}", response.getStatusCode());
        log.debug("Response body: {}", response.getBody().asString());

        softAssert.assertEquals(response.getStatusCode(), expectedStatusCode,
                "Expected status code " + expectedStatusCode + " but found " + response.getStatusCode());
        // Extract data from response using JSON path
        JsonPath jsonResponse = response.jsonPath();
        Object responseData = jsonResponse.get(jsonPath);

        // Assert that fetched data is not null
        softAssert.assertNotNull(responseData, "Data fetched from response using path '" + jsonPath + "' is null");
        softAssert.assertAll();

        log.info("TestvalidateNotNullResponseData successfully completed.");
    }

    public void handleApiResponseWithBody(Response response, int expectedStatusCode, Object expectedBody) {
        
        SoftAssert softAssert = new SoftAssert();
        log.debug("Response status code: {}", response.getStatusCode());
        log.debug("Response body: {}", response.getBody().asString());

        softAssert.assertEquals(response.getStatusCode(), expectedStatusCode,
                "Expected status code " + expectedStatusCode + " but found " + response.getStatusCode());

       
        softAssert.assertAll();
        log.info("API Test successfully completed.");
    }
    public static void handleResponseTime(Response response,long startTime, int expectedStatusCode, long maxResponseTimeMillis) {

        SoftAssert softAssert = new SoftAssert();

        // Log API response time
        long responseTime = logResponseTime(response,startTime);

        // Validate response status code
        softAssert.assertEquals(response.getStatusCode(), expectedStatusCode,
                "Expected status code " + expectedStatusCode + " but found " + response.getStatusCode());

        // Validate response time
        softAssert.assertTrue(responseTime <= maxResponseTimeMillis,
                "Response time exceeds threshold of " + maxResponseTimeMillis + " milliseconds, actual response time is: " + responseTime);

        softAssert.assertAll(); // Assert all soft asserts
    }

    private static long logResponseTime(Response response,long startTime) {

        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        return responseTime;

    }
}
