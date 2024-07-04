package com.poppulo.employee_api_methods;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;

import com.poppulo.employee_api_basetest.BaseTest;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class EmployeeAPI extends BaseTest {
	public EmployeeAPI() throws IOException {
		super();
	}

	public Response createEmployee(Object payload, String endpointKey) {
		String endpoint = prop.getProperty(endpointKey);

		Response response = given().contentType(ContentType.JSON).body(payload).when().post(endpoint);
		return response;

	}

	public Response getEmployee(int id, String endpointKey) {
		String endpoint = prop.getProperty(endpointKey);

		return RestAssured.given().contentType(ContentType.JSON).get(endpoint + id);
	}

	public Response updateEmployee(String endpointKey, int id, Object payload) {
		String endpoint = prop.getProperty(endpointKey);

		return RestAssured.given().header("Content-Type", "application/json").body(payload).put(endpoint + id);
	}

	public Response deleteEmployee(String Endpoint, int id) {
		return RestAssured.given().header("Content-Type", "application/json").delete(Endpoint + id);
	}

	public Response deleteEmployee(String Endpoint, String id) {
		return RestAssured.given().header("Content-Type", "application/json").delete(Endpoint + id);
	}

	public Response listEmployees(String Endpoint, int page) {
		return RestAssured.given().header("Content-Type", "application/json").get(Endpoint + "/users?page=" + page);
	}
}
