package com.roblox.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import org.openqa.selenium.By;

public class GenericUtilities {
	String xlfile[][];
	public static String getTimeStamp() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MMddyyhmmss");
		String formattedDate = sdf.format(date);
		return (formattedDate);
	}
	
	
	
	public Hashtable< String, String> convertStringArrayToHastable(String excelSheetName,String sheetName) throws IOException
	{
		//xlfile=ReadSheet.readSheet(System.getProperty("user.dir") + "\\locators.xls","Sheet2");
		xlfile=ReadSheet.readSheet(System.getProperty("user.dir") +"\\"+ excelSheetName,sheetName);
		Hashtable< String, String> table=new Hashtable<String,String>();
		int count=xlfile.length;
		for(int rowNum=1;rowNum<count;rowNum++)
		{
			String key=xlfile[rowNum][0];
			String value=xlfile[rowNum][1];
		//	System.out.println("key is "+key);
		//	System.out.println("Value is:" +value);
			table.put(key, value);
			
		//	System.out.println("jhndfks");
		//	System.out.println(table.get(key));
		}
		return table;
	}
	public static By locatortype(String type,String value)
	{
		By locName=null;
		if(type.equalsIgnoreCase("linkText"))
		{
			locName=By.linkText(value);
		}
		else if(type.equalsIgnoreCase("xpath"))
		{
			locName=By.xpath(value);
		}
		else if(type.equalsIgnoreCase("classname"))
		{
			locName=By.className(value);
		}
		else if(type.equalsIgnoreCase("id"))
		{
			locName=By.id(value);
		}
		else
			locName=By.partialLinkText(value);
		return locName;
	}
}