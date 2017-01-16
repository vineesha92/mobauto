
package com.roblox.util;


/**

 *

 */

public class ReadPropertyFile {



	/*

	 * 	This method returns the value of the configuration parameter from the specified configuration file.

	 */

	public static String getConfigurationParameter(String propertyFileName, String key){

		String parameterValue = null;

		

		try {
			System.out.println(propertyFileName);

			ReadTriggerFile parameterFile = new ReadTriggerFile (propertyFileName);

			parameterValue = parameterFile.getParameter(key);
			System.out.println(parameterValue);

		} catch (Exception ex){

			ex.printStackTrace();

		}

		

		return parameterValue;

	}

}

