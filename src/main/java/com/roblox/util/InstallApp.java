package com.roblox.util;

public class InstallApp {

	// Installing  App
	//public IMobileDevice device;
	
			public void installApp( String appRepository)
			{
				//Set the application identifier and MobileCloud repository path
				
				String appRepositoryPath;

				//if(device.getProperty(MobileDeviceProperty.OS).equals("Android")){
				appRepositoryPath = appRepository;	
				//Define the nativeDriver
				//note: the same nativelDriver is used throughout the test
				//Install the demo app from the MobileCloud Public repository folder
				System.out.println("installation started");
				//MobilePage.device.installApplication(appRepositoryPath);
				System.out.println("installation finished");

				//Define the nativeDriver

			}

}
