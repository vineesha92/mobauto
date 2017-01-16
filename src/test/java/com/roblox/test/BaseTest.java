package com.roblox.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;


import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;

import com.roblox.util.TestEnvironments;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.*;



public abstract class BaseTest {
	public WebDriver driver;
	public Logger logger = Logger.getLogger(BaseTest.class);
	public TestEnvironments te= new TestEnvironments();
	
	public HashMap <String, Object> getTestEnvironmentDetails()
	{
		HashMap <String, Object> envinfo = null;
		try {
			 envinfo=te.getEnvironmentsList().iterator().next();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return envinfo;
	}
	
	public void openApp(String clientType, DesiredCapabilities capabilities) {
		

		if (clientType!=null) {
			logger.info("Starting the application with "+clientType+" as client");
			
			if(clientType.equalsIgnoreCase("ios") ||clientType.equalsIgnoreCase("iphone")||clientType.equalsIgnoreCase("android"))
			{
						try {
							driver = new RemoteWebDriver(new URL("http://localhost:4723/wd/hub"), capabilities);
						} catch (MalformedURLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						if (clientType.equalsIgnoreCase("ios") ||clientType.equalsIgnoreCase("iphone"))
						{
							try
							{
							Thread.sleep(10000);
							(driver).switchTo().alert().accept();
							}
							catch (NoAlertPresentException e)
							{
								System.out.println("There are no alerts present");

							}
							catch (InterruptedException e)
							{
								System.out.println("There are no alerts present");
							}
						}
			}
			else if (clientType.equalsIgnoreCase("chrome") ||clientType.equalsIgnoreCase("firefox")||clientType.equalsIgnoreCase("safari"))
			{
				if (clientType.equalsIgnoreCase("firefox"))
					{
					//driver = new FirefoxDriver(capabilities);
					driver = new FirefoxDriver();
					}
				else if (clientType.equalsIgnoreCase("chrome"))
					{
					System.setProperty("webdriver.chrome.driver", "/lib/chromedriver");
					driver = new ChromeDriver();
					}
				else if (clientType.equalsIgnoreCase("safari"))
					{
					driver = new SafariDriver();
					}
				else if (clientType.equalsIgnoreCase("ie"))
				{
				driver = new InternetExplorerDriver();
				}
					
			}
		}
		else 
		{
			
		}
	driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}
	
	@AfterMethod()
	public void tearDownForScreenshot(ITestResult testResult) throws IOException
	{

		if (testResult.getStatus() == ITestResult.FAILURE) { 
			System.out.println(testResult.getStatus()); 
			File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE); 
	    	String methodName=testResult.getName().toString().trim();
	    	String className=testResult.getTestClass().getRealClass().getSimpleName();
			FileUtils.copyFile(scrFile, new File("./target/screenshots/"+className+"-"+methodName+".jpg")); 
			} 

	}
  
}
