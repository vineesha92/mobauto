package com.roblox.pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

public class AppiumPage extends OmniPage {
	/*
	 * This class is used to test native applications Through appium 
	 */
	public DesiredCapabilities capabilities =new DesiredCapabilities();  

		public void deviceSelection(String deviceName,String platformVersion,String platformName)
		{
			capabilities.setCapability("deviceName",deviceName);// specifies the name of the device
			capabilities.setCapability("platformVersion", platformVersion);  // specifies the version of the device
			capabilities.setCapability("platformName", platformName);   // specifies the OS name of the device
		}

		public void appSelection(String appName,String appPackage,String appActivity,String url ) throws InterruptedException, MalformedURLException
		{
			File app=new File(appName);  //to give location of the app 
			capabilities.setCapability("app", app.getAbsolutePath());  // gets the absolute path of app
			capabilities.setCapability("appPackage", appPackage);   // gets the appPackage of application
			capabilities.setCapability("appActivity", appActivity);   // gets appActivity of application
			driver=new AppiumDriver((new URL(url)), capabilities)  // initializes the appium server
				//driver=new AppiumDriver((new URL("http://127.0.0.1:4723/wd/hub")), capabilities)  // initializes the appium server
			{
				public MobileElement scrollToExact(String arg0)
				{
					// TODO Auto-generated method stub
					return null;
				}

				public MobileElement scrollTo(String arg0)
				{
					// TODO Auto-generated method stub
					return null;
				}
			};

			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			Thread.sleep(5000);
		}

		@Override
		protected String getIndexFileName() {
			
	        return "/resources/appElementsIndex.xml";
		}

	}

