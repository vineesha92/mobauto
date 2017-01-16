package com.roblox.test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import com.roblox.test.BaseTest;
import com.roblox.pages.App;





/**
 * Parent Test Class with application specific features
 */

public class RobloxTest extends BaseTest
{

	
	protected String testplatform, clientType, baseUrl;
	App app;	

	
	@BeforeClass
	public void setupClass()
	{
		
		//SimpleLayout layout = new SimpleLayout();
		String PATTERN = "mTest %-5p - %d{yyyy-MM-dd HH:mm:ss}  %C{1}:%L - %m%n";
		PatternLayout layout= new PatternLayout(PATTERN);
		
		FileAppender appender=null;
		try {
			appender = new FileAppender(layout,"./target/logs/"+this.getClass().getSimpleName()+".log",false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
	      logger.addAppender(appender);
	      logger.setLevel(Level.INFO);
	}
	
	@BeforeMethod
	public void setUp(Method testMethod)
	{
		
		logger.info("*******************************************************************************************************");
		logger.info("Starting the execution of Testcase: "+testMethod.getName());
		logger.info("*******************************************************************************************************");

		testplatform=(String) getTestEnvironmentDetails().get("testplatform");
		baseUrl=(String) getTestEnvironmentDetails().get("baseurl");

		System.out.println("Test client given from maven is "+System.getProperty("testclient"));
		if (System.getProperty("testclient") == null)
		{
			clientType=(String) getTestEnvironmentDetails().get("testclient");
			System.out.println("Test client given in testconfig is "+clientType);

		}
		else
		{
			clientType=System.getProperty("testclient");
			
		}
		DesiredCapabilities capabilities = (DesiredCapabilities) getTestEnvironmentDetails().get("capabilities");
        
        openApp(clientType, capabilities);
        
        if(clientType.equalsIgnoreCase("firefox"))
		{
			driver.get(baseUrl);
		}

		app = new App(driver, clientType, logger);

	}
	
@AfterMethod
public void tearDown()
{
   app.closeDriver();

}
	


}
