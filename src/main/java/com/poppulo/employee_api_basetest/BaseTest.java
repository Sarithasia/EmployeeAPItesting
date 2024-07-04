package com.poppulo.employee_api_basetest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

import com.poppulo.util.ExcelReader;

import io.restassured.RestAssured;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class BaseTest {
	FileInputStream fis;
	public static ExcelReader excel;
	protected static Properties prop;

	private static ThreadLocal<String> filePath = new ThreadLocal<>();
	private static ThreadLocal<String> sheetName = new ThreadLocal<>();

	public BaseTest() throws IOException {
		try {

			fis = new FileInputStream("src/test/resources/Property/Configuration.properties");
			prop = new Properties();
			prop.load(fis);

			RestAssured.baseURI = prop.getProperty("baseURI");
			RestAssured.basePath = prop.getProperty("basePath");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new IOException("Configuration file not found", e);
		}
	}

	public static void setFilePath(String path) {
		filePath.set(path);
	}

	public static String getFilePath() {
		return filePath.get();
	}

	public static void setSheetName(String name) {
		sheetName.set(name);
	}

	public static String getSheetName() {
		return sheetName.get();
	}

	@AfterSuite
	public void tearDown() throws IOException {
		if (fis != null) {
			fis.close();
		}
	}
}