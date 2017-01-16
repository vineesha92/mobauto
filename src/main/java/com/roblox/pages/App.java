package com.roblox.pages;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import com.roblox.pages.OmniPage;
import com.roblox.pages.LandingPage;

/**
 * This works like a Page Factory class. Tests don't create page object and they create App object and use the getters for pages
 */

public class App {

	protected WebDriver driver;
	protected String clientType;
	protected Logger logger;

	public App (WebDriver webDriver, String clientType, Logger logger){
		this.driver = webDriver;
		this.clientType= clientType;
		this.logger= logger;
	} // End of App constructor 

	public void closeDriver() {
		driver.close();
	}

	/**
	 * getter method for LandingPage
	 * @return LandingPage
	 */
	public LandingPage LandingPage() {
		LandingPage landingpage  = new LandingPage(driver, clientType,  logger);
		return landingpage;
	}


}//End of class 