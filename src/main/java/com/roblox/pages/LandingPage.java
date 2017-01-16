package com.roblox.pages;

import org.apache.log4j.Logger;
import java.util.*;
import org.openqa.selenium.WebDriver;
import com.roblox.pages.OmniPage;
import com.roblox.pages.AppiumPage;

public class LandingPage extends OmniPage {

	public LandingPage(WebDriver driver, String clientType, Logger logger) {
		this.driver=driver;	
		this.logger=logger; 
		if (clientType.equalsIgnoreCase("ios"))
			addElementsToPageElements("ios_LandingPage");
		else if (clientType.equalsIgnoreCase("android"))
			addElementsToPageElements("android_LandingPage");
		else
			addElementsToPageElements("web_LandingPage");
	}

	/**
	* submitsignin method is used to submit the signin form 
	* @param email The elementname reference of edit email from element resource file
	* @param password The elementname reference of edit password from element resource file
	*/
	public void submitsignin(String email, String password) {
		writeTextIntoTextBox("email",email);
		writeTextIntoTextBox("password",password);
		clickIfElementDisplayed("loginme");
	}

	/**
	* getText from span formerrormessage
	*/
	public String getTextFromspan(String formerrormessage) {
		String text=readTextFromElement(formerrormessage);
		return text;

}

	/**
	* verifyText from text formerrormessage
	*/
	public boolean verifyTextFromElementformerrormessage(String expectedtext) {
		return verifyElementText("formerrormessage",expectedtext);
}

	/**
	* LinkClick method is used to click on a link in the page
	*/
	public void clickforgotpassword(){
		//Clicking on link forgotpassword
		clickIfElementDisplayed("forgotpassword");
	}

	/**
	* LinkClick method is used to click on a link in the page
	*/
	public void clicksignmeup(){
		//Clicking on link signmeup
		clickIfElementDisplayed("signmeup");
	}

}//End of class 