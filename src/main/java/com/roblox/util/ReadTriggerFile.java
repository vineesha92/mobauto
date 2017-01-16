package com.roblox.util;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReadTriggerFile {
	private Properties parameters;

	/**
	 * Sets up the file to be used by class
	 * @param file path to the parameter file
	 */
	public ReadTriggerFile(String file) {
		System.out.println(file);
		parameters = new Properties();
        InputStream is;
		try {
			//is = new FileInputStream(file);
			is = ReadTriggerFile.class.getResourceAsStream(file);
			if (is ==null)
				is = new FileInputStream(file);

			if (file.endsWith(".xml")){
				System.out.println("XML found");
				parameters.loadFromXML(is);
			}else
				parameters.load(is);
		} catch (IOException e) {
			System.err.println("Error reading file from: " + file);
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieves the value for the specified parameter and throws an exception
	 * if it can't be found
	 * 
	 * @param param
	 * @return
	 */
	public String getParameter(String param) {
		return getParameter(param, true);
	}
	
	/**
	 * Retrieves the value for the specified parameter and errors out if the
	 * parameter is required but is not found in the file
	 * 
	 * @param param
	 * @param paramRequired
	 * @return value of the parameter
	 */
	public String getParameter(String param, boolean paramRequired) {
		String parameter = parameters.getProperty(param);
		if (parameter == null && paramRequired) 
			throw new NullPointerException("Parameter: " + param + " not found in the file");
		return parameter;		
	}
	
	public String getParameter(String param, String defaultValue) {
		String parameter = parameters.getProperty(param, defaultValue);
		return parameter;		
	}
	
	
	
}