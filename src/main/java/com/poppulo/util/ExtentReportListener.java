package com.poppulo.util;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExtentReportListener implements ITestListener {
	private ExtentReports extent;

	private ThreadLocal<ExtentTest> extentTestThread = new ThreadLocal<>();

	Logger log = LogManager.getLogger(ExtentReportListener.class);

	@Override
	public void onStart(ITestContext context) {
		log.info("onstart started");

		String path = System.getProperty("user.dir") + "\\test-output\\extentReportAPITesting.html";

		ExtentSparkReporter spark = new ExtentSparkReporter(path);
		extent = new ExtentReports();
		extent.attachReporter(spark);
		System.out.println("Extent Report: Test Suite Execution Started");
		log.info("onstart completed");

	}

	@Override
	public void onTestStart(ITestResult result) {
		log.info("Extent Report: Test Case Execution Started - " + result.getMethod().getMethodName());

		ExtentTest test = extent.createTest(result.getMethod().getMethodName());
		extentTestThread.set(test);
		System.out.println("Extent Report: Test Case Execution Started - " + result.getMethod().getMethodName());

	}

	@Override
	public void onTestSuccess(ITestResult result) {

		extentTestThread.get().log(Status.PASS, "Test Passed");
		log.info("Extent Report: Test Case Passed - " + result.getMethod().getMethodName());

	}

	@Override
	public void onTestFailure(ITestResult result) {

		extentTestThread.get().log(Status.FAIL, "Test Failed");
		extentTestThread.get().fail(result.getThrowable());
		log.error("Extent Report: Test Case Failed - " + result.getMethod().getMethodName(), result.getThrowable());

	}

	@Override
	public void onTestSkipped(ITestResult result) {

		ExtentTest test = extentTestThread.get();
        if (test == null) {
            test = extent.createTest(result.getMethod().getMethodName());
            extentTestThread.set(test);
        }

        test.log(Status.SKIP, "Test Skipped");
		extentTestThread.get().log(Status.SKIP, "Test Skipped");
		log.warn("Extent Report: Test Case Skipped - " + result.getMethod().getMethodName());


	}

	@Override
	public void onFinish(ITestContext context) {

		extent.flush();
		log.info("Extent Report: Test Suite Execution Finished");

	}
}
