package com.roblox.util;

import java.io.FileInputStream;
import java.util.Properties;

import org.openqa.selenium.WebDriver;



public class ReadProperties
{

	public Properties OR=null;
	Properties ENV=null;



	public void readingPropertiesFile(String propertyFilePath )
	{
		try
		{
			OR=new Properties();
			FileInputStream fs=new FileInputStream(System.getProperty("user.dir") +propertyFilePath);    
			//FileInputStream fs=new FileInputStream(System.getProperty("user.dir")+ "//src//com//properties//OR.properties");
			OR.load(fs);//initialize the object
			ENV=new Properties();
			String fileName=OR.getProperty("environment") + ".properties";
			fs=new FileInputStream(System.getProperty("user.dir")+ "//src//com//properties//" +fileName);
			ENV.load(fs);
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}


}
