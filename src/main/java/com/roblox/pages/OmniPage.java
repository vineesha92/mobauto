package com.roblox.pages;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.Clock;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.SystemClock;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.remote.DesiredCapabilities;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.roblox.util.ReadPropertyFile;
import com.thoughtworks.selenium.SeleniumException;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;



/**
 * @author Vineesha, Kevin
 *
 */
public abstract class OmniPage  {
	//public static WebDriver driver ;

 //MobileDriver mdriver;
 protected WebDriver driver;
 public Logger logger= Logger.getLogger(OmniPage.class);
 public String baseDir=System.getProperty("user.dir");
 public String resourceHomeDir=baseDir+"/src/main";
 
 	public DesiredCapabilities capabilities =new DesiredCapabilities(); 
	protected HashMap<String,AppUIElement> pageElements = new HashMap<String,AppUIElement>();
	public static final int WAIT_TIME = 100000;
	public static final int WAIT_INCR = 500;
	private static Clock clock = new SystemClock();	
	/*
	public WebDriverWait shortwait = new WebDriverWait(driver, 5);
	public WebDriverWait wait = new WebDriverWait(driver, 10);
	public WebDriverWait longwait = new WebDriverWait(driver, 60);
	public WebDriverWait extralongwait = new WebDriverWait(driver,120);
	*/
	
// RESOURCE MANAGEMENT METHODS
	
	/**
	 * You would need to override this method in anypage which extends OmniPage() 
	 * to point to your custom XXXXIndex.xml file. This helps in decoupling Application from Framework
	 * @return Nothing
	 */
		protected String getIndexFileName() {
	 		return "/resources/appElementsIndex.xml";
		}


	/**
	   * This method add all the elements of your page from the resource XML.
	   * @param String applicationAreaName the key of the application area i.e. page to identify the respective resource file.
	   * @return Nothing
	   */

	protected void addElementsToPageElements(String applicationAreaName) {
		// Read the index file to get path of elements xml resource.
		String indexFileName = resourceHomeDir+getIndexFileName();
		String elementfile = resourceHomeDir+ReadPropertyFile.getConfigurationParameter(indexFileName, applicationAreaName);

		// read the element resource xml file for the given area and load the page elements object.
		try {
			//getResourceAsStream() method is used for using the files from other projects
			InputStream is = OmniPage.class.getResourceAsStream(elementfile);
			if (is == null)
			{
				is = new FileInputStream(elementfile);
			}
			pageElements.putAll(getWebElementsFromXML(is, applicationAreaName));
			logger.info("Retrieving elements from : " + elementfile + " For area : " + applicationAreaName);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Unable to load the xml for area: " + applicationAreaName, ex);
		}
	}
	
//LOCATOR INFO METHODS	
	/**
	   * This method gets you WebElement inside the AppUIElement by using meta information of AppUIElement.
	   * @param AppUIElement appUIElement of the element.
	   * @return WebElement WebElement of the AppUIElement passed..
	   * @see AppUIElement
	   */
	
	protected WebElement getWebElementFromAppUIElement (AppUIElement appUIElement){
		logger.info("\t"+appUIElement);

		WebElement elementOfInterest = null;
		try{
			if (appUIElement.getFindBy().equalsIgnoreCase("id") )
				elementOfInterest = driver.findElement(By.id(appUIElement.getElementID()));
			else if (appUIElement.getFindBy().equalsIgnoreCase("XPATH")){
				if(driver instanceof InternetExplorerDriver && appUIElement.getElementIExPath()!=null &&
						!appUIElement.getElementIExPath().equals(""))
					elementOfInterest = driver.findElement(By.xpath(appUIElement.getElementIExPath()));
				else
				{
					elementOfInterest = driver.findElement(By.xpath(appUIElement.getElementXPath()));
				}
			}
			else if (appUIElement.getFindBy().equalsIgnoreCase("name"))
				elementOfInterest = driver.findElement(By.name(appUIElement.getElementName()));
			else if (appUIElement.getFindBy().equalsIgnoreCase("tag"))
				elementOfInterest = driver.findElement(By.tagName(appUIElement.getElementTag()));
			else if (appUIElement.getFindBy().equalsIgnoreCase("class")) {
				elementOfInterest = driver.findElement(By.className(appUIElement.getElementClass()));
			}
			else if (appUIElement.getFindBy().equalsIgnoreCase("text"))
				elementOfInterest = driver.findElement(By.linkText(appUIElement.getElementText()));

		} 
		catch (Exception ex){
			throw new NoSuchElementException("Unable to find element: " + appUIElement, ex);
		}

		//If element can't be found, fail fast by throwing an exception.
		if (elementOfInterest == null) {
			throw new NoSuchElementException("Unable to find element: " + appUIElement);
		}

		return elementOfInterest;
	}
	
	/**
	   * This method gets you WebElement inside the AppUIElement by elementkey.
	   * @param String elementKey of the element.
	   * @param int[] lineNumbers of elements to be replaced.
	   * @return WebElement WebElement of the elementkey passed.
	   */
	protected WebElement getWebElementFromElementKey(String elementKey, int... lineNumbers) {	

		AppUIElement element = new AppUIElement(pageElements.get(elementKey));
		logger.debug("getting WebElement of element "+element);
	
		if ( element.equals(null))
		{
			return null;	// check if element is not found in the hash map then return FALSE.
		}
		
		if (lineNumbers.length > 0){
			addLineNumberToElement(element, "#", lineNumbers);
		}

		return getWebElementFromAppUIElement(element);
	}
	
	/**
	   * This method gets you WebElement inside the AppUIElement by elementkey.
	   * @param String elementKey of the element.
	   * @return WebElement WebElement of the elementkey passed.
	   */
	protected WebElement getWebElementFromElementKey(String elementKey) {	
		return (getWebElementFromElementKey(elementKey,-1));
		
	}

	/**
	   * This method gets you By reference of WebElement inside the AppUIElement by using meta information of AppUIElement.
	   * @param AppUIElement appUIElement of the element.
	   * @return By By reference of the AppUIElement passed..
	   * @see AppUIElement
	   */

	protected By getByFromAppUIElement (AppUIElement appUIElement){
		By elementOfInterest = null;
		try{
			if (appUIElement.getFindBy().equalsIgnoreCase("id") )
				elementOfInterest = By.id(appUIElement.getElementID());
			else if (appUIElement.getFindBy().equalsIgnoreCase("XPATH")){
				if(driver instanceof InternetExplorerDriver && appUIElement.getElementIExPath()!=null &&
						!appUIElement.getElementIExPath().equals(""))
					elementOfInterest = By.xpath(appUIElement.getElementIExPath());
				else					
					elementOfInterest = By.xpath(appUIElement.getElementXPath());
			}
			else if (appUIElement.getFindBy().equalsIgnoreCase("name"))
				elementOfInterest = By.name(appUIElement.getElementName());
			else if (appUIElement.getFindBy().equalsIgnoreCase("tag"))
				elementOfInterest = By.tagName(appUIElement.getElementTag());
			else if (appUIElement.getFindBy().equalsIgnoreCase("class"))
				elementOfInterest = By.className(appUIElement.getElementClass());
			else if (appUIElement.getFindBy().equalsIgnoreCase("text"))
				elementOfInterest = By.linkText(appUIElement.getElementText());
		} 
		catch (Exception ex){
			throw new NoSuchElementException("Unable to find element: " + appUIElement, ex);
		}

		//If we can't find the element, attempt to fail fast by throwing an exception.
		if (elementOfInterest == null) {
			throw new NoSuchElementException("Unable to find element: " + appUIElement);
		}
		
		return elementOfInterest;
	}

	
	/**
	   * This method gets you By reference of WebElement inside the AppUIElement by using meta information of AppUIElement.
	   * @param String elementKey of the element.
	   * @param int[] lineNumbers of elements to be replaced.
	   * @return By By reference of the AppUIElement passed.
	   */
	
	protected By getByFromAppUIElement(String elementKey, int... lineNumbers) {	
		AppUIElement element = new AppUIElement(pageElements.get(elementKey));
		logger.debug("getting By of element "+element);
		if (lineNumbers.length > 0){
			addLineNumberToElement(element, "#", lineNumbers);
		}
		return getByFromAppUIElement(element);
	}

	/**
	 * The method waitForElementPresent is used to wait for an element to be present in DOM for a max of 15 secs wait time.
	 * @param elementKey
	 * @param lineNumbers
	 */
	public void waitForElementPresent(String elementKey, int... lineNumbers)
	{
		WebDriverWait wait = new WebDriverWait(driver, 15);
		wait.until(ExpectedConditions.presenceOfElementLocated(getByFromAppUIElement(elementKey, lineNumbers)));
	}
	
	/**
	 * The method waitForElementPresent is used to wait for an element to be present in DOM for a max of 15 secs wait time.
	 * @param elementKey
	 */
	public void waitForElementPresent(String elementKey)
	{
		waitForElementPresent(elementKey, -1);
	}
	
	
	/**
	 * The method waitForElementVisible is used to wait for an element to be visible in the page for a max of 15 secs wait time.
	 * @param elementKey
	 * @param lineNumbers
	 */
	public void waitForElementVisible(String elementKey, int... lineNumbers)
	{
		WebDriverWait wait = new WebDriverWait(driver, 15);
		waitForElementPresent( elementKey,  lineNumbers);
		wait.until(ExpectedConditions.visibilityOfElementLocated(getByFromAppUIElement(elementKey, lineNumbers)));
	}
	
	/**
	 * The method waitForElementVisible is used to wait for an element to be visible in the page for a max of 15 secs wait time.
	 * @param elementKey
	 */
	public void waitForElementVisible(String elementKey)
	{
		waitForElementVisible(elementKey, -1);
	}
	
	
	//TODO Delete below commented methods
	
	@Deprecated
	public void waitForElementPresent(By element)
	{
		int time=0;
		while(driver.findElements(element).size()==0&& time<=30)
		{
			time++;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}

	}


	@Deprecated
	public void waitForElementPresent(By element, int secs)
	{
		int time=0;
		while(driver.findElements(element).size()==0&& time<=secs)
		{
			time++;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}

	}
	
	@Deprecated
	public void waitForElementDisplayed(By element)
	{
		waitForElementDisplayed(element,30);

	}


	public void waitForElementDisplayed(By element, int secs)
	{
		int time=0;
		while(driver.findElements(element).size()==0&& time<=secs)
		{
			time++;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try
		{
			while((!driver.findElement(element).isDisplayed())&& time<=secs)
			{
				time++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		catch(NoSuchElementException e)
		{
			logger.info("Required element "+element+" is not displayed");
		}

	}


	@Deprecated
	public boolean waitForElementDisplayed(String elementKey)
	{
		return waitForElementDisplayed(elementKey,30);

	}


	@Deprecated
	public boolean waitForElementDisplayed(String elementKey, int secs)
	{
		logger.info(elementKey);
		System.out.print("Waiting for ....");
		By element= getByFromAppUIElement(elementKey);

		int time=0;
		while(driver.findElements(element).size()==0&& time<=secs)
		{
			time++;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try
		{
			while(!(driver.findElement(element).isDisplayed())&& time<=secs)
			{
				time++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		catch(NoSuchElementException e)
		{
			logger.info("Required element "+element+" is not displayed");
		}
		if(time>secs)
			return false;
		else
			return true;

	}

	@Deprecated
	public boolean verifyElementDisplayed(By element)
	{
		if(!driver.findElement(element).isDisplayed())
		{
			logger.info("Element "+element+" is not displayed");
			return false;
		}
		else
		{
			logger.debug("Element "+ element+" is displayed");
			return true;
		}
	}
	
	/**
	 * 
	 * @param elementkey
	 * @param expectedtext
	 * @return true if given text matches with application text.
	 */
	public boolean verifyElementText(String elementkey, String expectedtext)
	{
		String actualtext=readTextFromElement(elementkey);
		if(actualtext.equals(expectedtext))
		{
			logger.debug("Element "+elementkey+" text is matcing");
			return true;
		}
		else
		{
			logger.info("Element "+ elementkey+" text is "+actualtext+" but expected is "+expectedtext);
			return false;
		}
	}



	public void clickAndWait(By element, int time)
	{
		driver.findElement(element).click();
		int count=0;
		while(driver.findElements(element).size()==0&& count<=time)
		{
			count++;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	public void captureScreenShot()
	{
		try {
			File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			//FileUtils.copyFile(scrFile, new File("screenshot.png"));
			FileUtils.copyFile(scrFile, new File(this.getClass().getSimpleName()+".png")); // Code to name screenshots with test case name
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void forcedWait(int secs)
	{
		try {
			Thread.sleep(secs*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	public String getTimeStamp()
	{
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MMddyyhmmss");
		String formattedDate = sdf.format(date);
		return(formattedDate);
	}

	/**
	 * Takes a screenshot of the browser window. With the Firefox Driver, the
	 * firefox window does not need to be visible or on screen, the IE driver
	 * however does not implement this feature yet. Follow the webdriver issue
	 * http://code.google.com/p/webdriver/issues/detail?id=12
	 * 
	 * @param fileName
	 * @param destinationDir
	 */
	public void takeBrowserScreenshot(String fileName, String destinationDir) {
		if (!(new File(destinationDir).exists())) {
			throw new RuntimeException("Directory does not exist: " + destinationDir);
		}

		// save captured image to PNG file
		try {
			String imageFileName = destinationDir + fileName;

			int count = 0;
			String currentImageFilePath = null;
			File f;
			do {
				currentImageFilePath = imageFileName + ((count==0) ? "" : count) + ".png";
				f = new File(currentImageFilePath);
				count++;
			} while (f.exists());
			if (driver instanceof FirefoxDriver) { 
				//((FirefoxDriver) driver).saveScreenshot(new File(currentImageFilePath));
				File ss = ((FirefoxDriver) driver).getScreenshotAs(OutputType.FILE);
				ss.renameTo(new File(currentImageFilePath));
			} else { // as soon as the IE driver has it's own native saveScreen shot method, we have to use Robot
				Robot robot = null;
				try {
					robot = new Robot();
				} catch (AWTException e1) {
					e1.printStackTrace();
				}	
				// create screen shot
				BufferedImage image = robot.createScreenCapture(getBrowserRect());
				ImageIO.write(image, "png", new File(currentImageFilePath));
			}
			logger.info("Saved screenshot titled " + fileName + " to: " + imageFileName);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	/**
	 * A temporary method used to identify Safari in it's current dumb
	 * implementation. SafariDriver is not as mature as the other drivers and
	 * lacks some functionality key to some of the methods written
	 * 
	 * @return
	 */
	protected boolean isSafariDummyPlug() {
		return false;//(driver instanceof SafariDriver && true);
	}

	/**
	 * Returns the coordinates of the top lefthand corner of the browser window
	 */
	private Point getWindowLocation() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		Point windowLocation = new Point(0, 0);

		// TODO ensure this works in safari on mac
		if (driver instanceof InternetExplorerDriver) {
			windowLocation.x = ((Long)js.executeScript("return parent.window.screenLeft;")).intValue();	    		
			windowLocation.y = ((Long)js.executeScript("return parent.window.screenTop;")).intValue();	    		
		}
		else {
			windowLocation.x = ((Long)js.executeScript("return window.screenX;")).intValue();
			windowLocation.y = ((Long)js.executeScript("return window.screenY;")).intValue();
		}
		return windowLocation;
	}

	public void switchToNewWindow() {
		String parentWindow = driver.getWindowHandle();
		Set<String> handles =  driver.getWindowHandles();
		if (handles.size()>2)
		{
			logger.error("More than one new window error: Test doesn't know which new window to switch to");
			throw new SeleniumException("More than one new window error: Test doesn't know which new window to switch to");
		}
		for(String windowHandle  : handles)
		    {
		     if(!windowHandle.equals(parentWindow))
		       {
		       driver.switchTo().window(windowHandle);
		       }
		    }	
		}
	/**
	 * @return
	 */
	private Point getBrowserSize() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		Point browserSize = new Point(0, 0);


		// TODO ensure this works in IE 7 and 8
		if (driver instanceof InternetExplorerDriver) {
			// IE 4 compatible, but seems to work for IE7
			browserSize.x = ((Long)js.executeScript("return parent.document.body.clientWidth;")).intValue();	    		
			browserSize.y = ((Long)js.executeScript("return parent.document.body.clientHeight;")).intValue();	 	    		


			// for IE 6+ in 'standards compliant mode'
			// supposedly uses (unverified):           
			//   wWidth = document.documentElement.clientWidth;
			//   wHeight = document.documentElement.clientHeight;
		}
		else if (driver instanceof FirefoxDriver) {
			browserSize.x = ((Long)js.executeScript("return window.outerWidth;")).intValue();
			browserSize.y = ((Long)js.executeScript("return window.outerHeight;")).intValue();
		} else {
			throw new UnsupportedOperationException();
		}
		return browserSize;	
	}

	private Rectangle getBrowserRect() {
		Point location = getWindowLocation();
		Point size = getBrowserSize();
		return new Rectangle(location.x, location.y, size.x, size.y); 
	}

	/**
	 * Overrides the javascript alert so that we can capture its output
	 */
	public void overrideAlert() {
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript("window.alertDone = false; window.alert = function(msg) {window.alertDone = true; window.alertMsg = msg}");
	}

	/**
	 * Convenience method that will check for a Javascript alert window and throw
	 *  a JsAlertPresentException if one is open.
	 */


	public void runJSFunction() {
		JavascriptExecutor js = (JavascriptExecutor)driver;
		logger.info("JS RETURNED "+js.executeScript("return roots()"));
	}

	/**
	 * This method reads an xml files with Web Element Meta data and returns a HashMap containing information stored in all the elements. 
	 * @param XML_FileName
	 * @param elementHashMap
	 * @return
	 * @throws ParserConfigurationException s
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public  HashMap<String, AppUIElement> getWebElementsFromXML (String filename, String areaSeeked) 
			throws ParserConfigurationException, SAXException, IOException{

		FileInputStream fis = new FileInputStream(filename);
		return getWebElementsFromXML(fis, areaSeeked);
	}





	public  HashMap<String, AppUIElement> getWebElementsFromXML (InputStream is, String areaSeeked) 
			throws ParserConfigurationException, SAXException, IOException{

		HashMap<String, AppUIElement> elementHashMap = new HashMap<String,AppUIElement>();

		// 	1.	Open the XML File.
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance(); 
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder(); 
		Document doc = docBuilder.parse(is);
		//logger.info(doc.getNodeValue());
		//	2.	Read contents from the XML File One Element at a time and Populate the HashMap.
		Node rootNode = doc.getDocumentElement();
		NodeList listOfElements = rootNode.getChildNodes();		// Get all the Child nodes of the root node.	
		logger.info("Node Length"+listOfElements.getLength());
		if (rootNode.hasAttributes()){	//	The root node contains the area name of UT as attribute value.
			NamedNodeMap attributesOfRoot = rootNode.getAttributes();

			//	If the elements values seeked by the calling method are not of the same area as in the XML file return null. 
			if (!attributesOfRoot.getNamedItem("area").getNodeValue().equalsIgnoreCase(areaSeeked))
				return null;
		}

		/* Getting values out of each element from the XML file.*/
		for (int i=0; i<listOfElements.getLength();i++){
			Node currentNode = listOfElements.item(i);
			boolean elementDataPopulated = false;
			//	data values to extract from the XML File.
			String id = "";
			String key = "";
			String findBy = "";
			String tag = "";
			String name = "";
			String text = "";
			String classValue = "";
			String xPath = "";
			String ieXPath = "";

			/* Getting values out of Tags */
			if (currentNode.hasChildNodes()){
				NodeList elementFields = currentNode.getChildNodes();

				for (int k=0; k<elementFields.getLength(); k++){
					String nodeName = elementFields.item(k).getNodeName();
					String nodeValue = "";

					if (elementFields.item(k).hasChildNodes()){
						if (elementFields.item(k).getFirstChild().getNodeValue() != null){
							nodeValue = elementFields.item(k).getFirstChild().getNodeValue();
						}
					}else if (!elementFields.item(k).getNodeName().equalsIgnoreCase("#text")){
						nodeValue = "";
					}

					if (nodeName.equalsIgnoreCase("key"))
						key = nodeValue;
					else if (nodeName.equalsIgnoreCase("findby"))
						findBy = nodeValue;
					else if (nodeName.equalsIgnoreCase("tag"))
						tag = nodeValue;
					else if (nodeName.equalsIgnoreCase("name"))
						name = nodeValue;
					else if (nodeName.equalsIgnoreCase("text"))
						text = nodeValue;
					else if (nodeName.equalsIgnoreCase("class"))
						classValue = nodeValue;
					else if (nodeName.equalsIgnoreCase("xpath"))
						xPath = nodeValue;
					else if (nodeName.equalsIgnoreCase("id"))
						id = nodeValue;
					else if(nodeName.equalsIgnoreCase("iexpath"))
						ieXPath = nodeValue;
					else if (!nodeName.equalsIgnoreCase("#text")){	// The node is not a valid / known node in the framework. print the message.
						logger.info("\n Not a valid NODE in the XML File : " + nodeName+ " : " + nodeValue);
					}
				}

				elementDataPopulated = true;
			}

			/* Getting values out of attributes */
			if (currentNode.hasAttributes()){
				NamedNodeMap elementAttributesMap = currentNode.getAttributes();

				if (elementAttributesMap.getNamedItem("key") != null)
					key = elementAttributesMap.getNamedItem("key").getNodeValue();

				if (elementAttributesMap.getNamedItem("findBy")!= null)
					findBy = elementAttributesMap.getNamedItem("findBy").getNodeValue();

				if (elementAttributesMap.getNamedItem("tag") != null)
					tag = elementAttributesMap.getNamedItem("tag").getNodeValue();

				if (elementAttributesMap.getNamedItem("name") != null)
					name = elementAttributesMap.getNamedItem("name").getNodeValue();

				if (elementAttributesMap.getNamedItem("text") != null)
					text = elementAttributesMap.getNamedItem("text").getNodeValue();

				if (elementAttributesMap.getNamedItem("id") != null)
					id = elementAttributesMap.getNamedItem("id").getNodeValue();

				if (elementAttributesMap.getNamedItem("class") != null)
					classValue = elementAttributesMap.getNamedItem("class").getNodeValue();

				if (elementAttributesMap.getNamedItem("xPath") != null)
					xPath = elementAttributesMap.getNamedItem("xPath").getNodeValue();

				if (elementAttributesMap.getNamedItem("iexpath") != null)
					ieXPath = elementAttributesMap.getNamedItem("iexpath").getNodeValue();

				elementDataPopulated = true;
			}			

			// 	we create a new AppUI element only when we find an element that has attributes or nodes or combination of both. 
			if (elementDataPopulated){
				// We have all the data values to feed to AppUIElement while creating it
				AppUIElement appUIElement = new AppUIElement(key, id, name, classValue, text, xPath, findBy, tag, ieXPath);
				elementDataPopulated = false;		
				//	Put the appUIElement Object in the hashmap.
				elementHashMap.put(appUIElement.getElementKey(), appUIElement);
			}
		}

		//	3.	Close the file

		//	4.	Return the HashMap.
		return elementHashMap;		
	}

	/*
	 * 	This method reads text from text elements like text box, text area.
	 */
	protected String readTextFromElement (String elementKey){
		return readTextFromElement (elementKey, -1);
	}

	/**
	 * 	This method does the following:
	 * 	1. 	Retrieve the Element Information from the hash map using the elementKey passed as parameter to the method.
	 * 	2.	Finds the Element on the web page. 
	 * 	@return If Element is found it reads text from the element and returns it. else reutnrs null.
	 */
	protected String readTextFromElement (String elementKey, int... lineNumbers ){
		
		WebElement elementOfInterest= getWebElementFromElementKey(elementKey,lineNumbers);
		waitForElementVisible(elementKey, lineNumbers);
		logger.debug("Reading text from element...");
		String returnText = elementOfInterest.getText();
		return returnText;
	}

	//TODO - Praveen - review if following methods work different from above.
	/*
	 * 	This method reads text from text elements like text box, text area.
	 */
	protected String readValueFromElement (String elementKey){
		return readValueFromElement (elementKey, -1);
	}

	/**
	 * 	This method does the following:
	 * 	1. 	Retrieve the Element Information from the hash map using the elementKey passed as parameter to the method.
	 * 	2.	Finds the Element on the web page. 
	 * 	@return If Element is found it reads text from the element and returns it. else returns null.
	 */
	protected String readValueFromElement (String elementKey, int... lineNumbers ){
		//	We need to create a clone of the element retrieved from the hashmap as we cant make the hash map dirty. 
		AppUIElement anElement = new AppUIElement(pageElements.get(elementKey));

		//	if the element extracted from the HashMap is a Line Item then we need to replace the "#" character with the LINE NUMBER.
		if (lineNumbers.length > 0){
			addLineNumberToElement(anElement, "#", lineNumbers);
		}

		waitOnElement(elementKey, 7500);
		if (!isElementVisible(elementKey, lineNumbers)) {
			throw new WebDriverException("Unable to read from non-visible WebElement: " + anElement.toString());
		}

		if ( anElement == null)
			return null;	// check if element is not found in the hash map then return FALSE.

		System.out.print("Reading value from input element...");
		WebElement elementOfInterest = getWebElementFromAppUIElement(anElement);
		String returnText = null;
		try {
			//Due to an issue with the way that webdriver used to report multiple "&nbsp;" as a single space
			// we have multiple tests that are designed around assuming there are only one space in certain
			// messages.  Therefore, we'll replace any instance of multiple spaces with a single space
			// instead.
			returnText = elementOfInterest.getAttribute("value").replaceAll(" +", " ");
		} catch (NullPointerException ex){
			ex.printStackTrace();
			return "";
		}
		return returnText;
	}

	
	
	/*
	 * 	This method reads target url from image or link elements.
	 */
	protected String readTargetURLFromElement (String elementKey){
		return readValueFromElement (elementKey, -1);
	}

	/**
	 * 	This method does the following:
	 * 	1. 	Retrieve the Element Information from the hash map using the elementKey passed as parameter to the method.
	 * 	2.	Finds the Element on the web page. 
	 * 	@return If Element is found it reads href from the element and returns it. else returns null.
	 */
	protected String readTargetURLFromElement (String elementKey, int... lineNumbers ){
		//	We need to create a clone of the element retrieved from the hashmap as we cant make the hash map dirty. 
		AppUIElement anElement = new AppUIElement(pageElements.get(elementKey));

		//	if the element extracted from the HashMap is a Line Item then we need to replace the "#" character with the LINE NUMBER.
		if (lineNumbers.length > 0){
			addLineNumberToElement(anElement, "#", lineNumbers);
		}

		waitOnElement(elementKey, 7500);
		if (!isElementVisible(elementKey, lineNumbers)) {
			throw new WebDriverException("Unable to read from non-visible WebElement: " + anElement.toString());
		}

		if ( anElement == null)
			return null;	// check if element is not found in the hash map then return FALSE.

		System.out.print("Reading href from the element...");
		WebElement elementOfInterest = getWebElementFromAppUIElement(anElement);
		String returnText = null;
		try {

			returnText = elementOfInterest.getAttribute("value");
		} catch (NullPointerException ex){
			ex.printStackTrace();
			return "";
		}
		return returnText;
	}

	
	
	/*
	 * 	This method returns the count of matching elements.
	 */
	protected int getElementsCount (String elementKey){
		return getElementsCount (elementKey, -1);
	}

	/**
	 * 	This method does the following:
	 * 	1. 	Retrieve the Elements Information from the hash map using the elementKey passed as parameter to the method.
	 * 	2.	Finds the matching Elements on the web page. 
	 * 	@return count of the matching elements
	 */
	protected int getElementsCount (String elementKey, int... lineNumbers ){
		//	We need to create a clone of the element retrieved from the hashmap as we cant make the hash map dirty. 
		AppUIElement anElement = new AppUIElement(pageElements.get(elementKey));

		int count=0;

		//	if the element extracted from the HashMap is a Line Item then we need to replace the "#" character with the LINE NUMBER.
		if (lineNumbers.length > 0){
			addLineNumberToElement(anElement, "#", lineNumbers);
		}

		waitOnElement(elementKey, 7500);
		if (!isElementVisible(elementKey, lineNumbers)) {
			throw new WebDriverException("Unable to read from non-visible WebElement: " + anElement.toString());
		}

		System.out.print("Reading text from element...");
		WebElement elementOfInterest = getWebElementFromAppUIElement(anElement);
		try 
		{
			count=elementOfInterest.findElements(By.tagName("li")).size();
			logger.info("Count of matching elements is "+count);
		} 
		catch (NullPointerException ex){
			ex.printStackTrace();
			return count;
		}
		return count;
	}


	/*
	 * 	This method corrects the elements meta data for the line item elements.
	 */
	protected void addLineNumberToElement (AppUIElement anElement, String toReplace, String replaceWith){
		if (anElement.getElementID() != null)
			anElement.setElementID(anElement.getElementID().replace(toReplace, replaceWith));
		if (anElement.getElementName() != null)
			anElement.setElementName(anElement.getElementName().replace(toReplace, replaceWith));
		if (anElement.getElementXPath() != null)
			anElement.setElementXPath(anElement.getElementXPath().replace(toReplace, replaceWith));
	}

	protected void addLineNumberToElement(AppUIElement anElement, String toReplace, int[] replacements) {
		if (anElement.getElementID() != null)
			anElement.setElementID(replaceStringWithIndexes(anElement.getElementID(), toReplace, replacements));
		if (anElement.getElementName() != null)
			anElement.setElementName(replaceStringWithIndexes(anElement.getElementName(), toReplace, replacements));
		if (anElement.getElementXPath() != null)
			anElement.setElementXPath(replaceStringWithIndexes(anElement.getElementXPath(), toReplace, replacements));
	}

	protected String replaceStringWithIndexes(String original, String toReplace, int[] replacements) {
		String result = new String(original);
		for(int i=0; i < replacements.length; i++) {
			if (replacements[i] == -1) {
				continue;
			}

			result = result.replaceFirst(toReplace, replacements[i] + "");
		}

		return result;
	}

	public boolean isElementVisible(String elementKey) {
		return isElementVisible(elementKey, -1);
	}

	public boolean isElementVisible(String elementKey, int... lineNumbers) {		
		// WebElement may need to be changed to RenderedWebElement.
		waitForElementDisplayed(elementKey);
		System.out.print("Verify visible...");
		try {
			WebElement elementOfInterest = (getWebElementFromElementKey(elementKey, lineNumbers));
			return isElementVisible(elementOfInterest);
		} catch(NoSuchElementException ex) {
			logger.info("Not running");
			return false;
		}
	}

	public boolean isElementVisible(WebElement element) {
		return element.isDisplayed();
	}

	public boolean isElementEnabled(String elementKey) {
		return isElementEnabled(elementKey, -1);
	}

	public boolean isElementEnabled(String elementKey, int... lineNumbers) {
		System.out.print("Verify enabled...");
		WebElement elementOfInterest = getWebElementFromElementKey(elementKey, lineNumbers);
		return elementOfInterest.isEnabled();
	}

	/**
	 * Function that should be called to determine whether or not an element is
	 *  selected or not.  No validation is performed to verify that the element
	 *  specified is selectable.
	 *
	 * @param elementKey The element key representing the particular element to
	 *  verify if it is selected.
	 * @param lineNumber The particular line number that this component is part
	 *  of.  This distinguishes elements in a list.
	 * @return True if the element is selected.  False otherwise.  Execution is
	 *  undefined for elements that can not be selected.
	 */
	protected boolean isElementSelected(String elementKey, int... lineNumbers) {
		AppUIElement checkBox = new AppUIElement(pageElements.get(elementKey));
		if (lineNumbers.length > 0){
			addLineNumberToElement(checkBox, "#", lineNumbers);
		}

		WebElement selectableElement = getWebElementFromAppUIElement(checkBox);
		return selectableElement.isSelected();
	}

	protected boolean isElementSelected(String elementKey) {
		return isElementSelected(elementKey, -1);
	}

	/**
	 * Function that should be called on radio buttons or dropdowns in order to
	 *  select a particular element from a list of elements.  The element should
	 *  be an "option" or "input" radio button in order to work successfully.
	 *
	 * @param elementKey The element key representing the particular element to
	 *  be set as the selected option.
	 * @param lineNumber The particular line number that this component is part
	 *  of.  This distinguishes elements in a list.
	 */
	protected void setSelected(String elementKey, int... lineNumbers) {
		String action = "Selecting element...";
		AppUIElement element = new AppUIElement(pageElements.get(elementKey));
		if (lineNumbers.length > 0){
			addLineNumberToElement(element, "#", lineNumbers);
		}

		//Log the action performed
		System.out.print(action);

		WebElement selectableElement = getWebElementFromAppUIElement(element);
		//selectableElement.setSelected();
		selectableElement.click();


	}


	protected void setSelected(String elementKey) {
		setSelected(elementKey, -1);
	}


	protected boolean isSelected(String elementKey) {
		return isSelected(elementKey, -1);
	}

	protected boolean isSelected(String elementKey, int... lineNumbers) {
		logger.info("Is Element Selected...");
		WebElement webElement = getWebElementFromElementKey(elementKey, lineNumbers);
		return webElement.isSelected();
	}

	
	protected void selectElementInDropDownByVisibleText(String dropdownelementKey, String optiontoselect, int... lineNumbers) {
		String action = "Selecting element...";
		AppUIElement element = new AppUIElement(pageElements.get(dropdownelementKey));
		if (lineNumbers.length > 0){
			addLineNumberToElement(element, "#", lineNumbers);
		}

		//Log the action performed
		System.out.print(action);
		WebElement selectableElement = getWebElementFromAppUIElement(element);
		Select dropdownelement= new Select(selectableElement);
		dropdownelement.selectByVisibleText(optiontoselect);

		//selectableElement.setSelected();
		selectableElement.click();

		
	}


	protected void selectElementInDropDownByVisibleText(String dropdownelementKey, String optiontoselect) {
		selectElementInDropDownByVisibleText(dropdownelementKey,  optiontoselect, -1);
	}
	
	protected void selectElementInDropDownByValue(String dropdownelementKey, String optiontoselect, int... lineNumbers) {
		String action = "Selecting element...";
		AppUIElement element = new AppUIElement(pageElements.get(dropdownelementKey));
		if (lineNumbers.length > 0){
			addLineNumberToElement(element, "#", lineNumbers);
		}

		//Log the action performed
		System.out.print(action);
		WebElement selectableElement = getWebElementFromAppUIElement(element);
		Select dropdownelement= new Select(selectableElement);
		dropdownelement.selectByValue(optiontoselect);

		//selectableElement.setSelected();
		selectableElement.click();

		
	}


	protected void selectElementInDropDownByValue(String dropdownelementKey, String optiontoselect) {
		selectElementInDropDownByValue(dropdownelementKey,  optiontoselect, -1);
	}
	
	protected void selectElementInDropDownByIndex(String dropdownelementKey, int optiontoselect, int... lineNumbers) {
		String action = "Selecting element...";
		AppUIElement element = new AppUIElement(pageElements.get(dropdownelementKey));
		if (lineNumbers.length > 0){
			addLineNumberToElement(element, "#", lineNumbers);
		}

		//Log the action performed
		System.out.print(action);
		WebElement selectableElement = getWebElementFromAppUIElement(element);
		Select dropdownelement= new Select(selectableElement);
		dropdownelement.selectByIndex(optiontoselect);

		//selectableElement.setSelected();
		selectableElement.click();

		
	}


	protected void selectElementInDropDownByIndex(String dropdownelementKey, int optiontoselect) {
		selectElementInDropDownByIndex(dropdownelementKey,  optiontoselect, -1);
	}
	
	protected List<WebElement> getSelectDropDownOptions (String elementKey){
		AppUIElement selectDropDown = new AppUIElement(pageElements.get(elementKey));
		WebElement wSelectDropDown = getWebElementFromAppUIElement(selectDropDown);


		Select sSelectDropDown  = new Select(wSelectDropDown );
		return sSelectDropDown.getOptions();
	}
	
	
	
	
	protected List<WebElement> getSelectDropDownOptions (String elementKey, int... lineNumbers) {
		AppUIElement selectDropDown = new AppUIElement(pageElements.get(elementKey));
		logger.info(elementKey);
		if (lineNumbers.length > 0){
			addLineNumberToElement(selectDropDown, "#", lineNumbers);
		}

		WebElement selectElement = getWebElementFromAppUIElement(selectDropDown);
		Select sSelectDropDown = new Select(selectElement);
		return sSelectDropDown.getOptions();

	}

	public boolean waitOnElement(String elementKey, int timeout, boolean ignoreRendering, int... lineNumbers) {
		AppUIElement element = new AppUIElement(pageElements.get(elementKey));
		if (lineNumbers.length > 0) {
			addLineNumberToElement(element, "#", lineNumbers);
		}

		if (element.getFindBy().equalsIgnoreCase("ID")) {
			return waitOnId(element.getElementID(), timeout, null,  ignoreRendering);
		} else if (element.getFindBy().equalsIgnoreCase("NAME")) {
			return waitOnName(element.getElementName(), timeout, null, ignoreRendering);
		} else if (element.getFindBy().equalsIgnoreCase("XPATH")) {
			return waitOnXPath(elementKey, timeout, null, ignoreRendering);
		} else {
			return true;
		}
	}

	public boolean waitOnElement(String elementKey) {
		return waitOnElement(elementKey, -1, WAIT_TIME, false);
	}

	public boolean waitOnElement(String elementKey, int timeout) {
		return waitOnElement(elementKey, -1, timeout, false);
	}

	/**
	 * Wait for a particular AppUI element to appear.
	 * @param elementyKey
	 * @param lineNumber
	 * @param timeout
	 */
	public boolean waitOnElement(String elementyKey, int lineNumber, int timeout, boolean ignoreRendering) {
		AppUIElement element = new AppUIElement(pageElements.get(elementyKey));
		if (lineNumber > -1)
			addLineNumberToElement(element, "#", "" + lineNumber);

		if (element.getFindBy().equalsIgnoreCase("ID")) {
			return waitOnId(element.getElementID(), timeout, ignoreRendering);
		} else if (element.getFindBy().equalsIgnoreCase("NAME")) {
			return waitOnName(element.getElementName(), timeout, null, ignoreRendering);
		} else if (element.getFindBy().equalsIgnoreCase("XPATH")) {
			logger.info("xpath");
			return waitOnXPath(element.getElementXPath(), timeout, null, ignoreRendering);
		} else if (element.getFindBy().equalsIgnoreCase("class")) {
			return true;
		}

		return false;
	}



	/**
	 * 
	 * @param elementKey
	 * @return
	 */

	public boolean clickIfElementDisplayed (String elementKey){
		return(clickIfElementDisplayed(elementKey, 10));
	}

	/**
	 * 
	 * @param elementKey
	 * @param sec
	 * @return
	 */
	public boolean clickIfElementDisplayed (String elementKey, int sec){
		if(waitForElementDisplayed(elementKey,sec)){
			logger.info(elementKey+" is now displayed");
			clickOnIndependentElement(elementKey);
			return(true);
		}
		else
			logger.info(elementKey);
		
			logger.info("Wrong");
		return false;
	}

	/**
	 * 	This method clicks on an element -whose KEY is passed- on the Web Page.  
	 */
	protected boolean clickOnIndependentElement (String elementKey){
		return clickOnIndependentElement(elementKey, -1);
	}

	/**
	 * 	This method clicks on an element -Whose Generic KEY is passed containing # along with the line number where the element is present- on the web page.
	 */
	protected boolean clickOnIndependentElement (String elementKey, int... lineNumbers){
		String action = "Clicking on...";
		//	We need to create a clone of the element retrieved from the hashmap as we cant make the hash map dirty. 
		AppUIElement anElement = new AppUIElement(pageElements.get(elementKey));

		logger.info("\n\nELEMENT TO CLICK : " + anElement);
		//	if the element extracted from the HashMap is a Line Item then we need to replace the "#" character with the LINE NUMBER.
		if (lineNumbers.length > 0){
			addLineNumberToElement(anElement, "#", lineNumbers);
		}

		if ( anElement == null)
			return false;	// check if element is not found in the hash map then return FALSE.

		//Log the action
		logger.info(action);
		try {
			WebElement elementOfInterest = getWebElementFromAppUIElement(anElement);
			if (driver instanceof InternetExplorerDriver) {
				if(elementOfInterest.getAttribute("type")!=null){
					if (elementOfInterest.getTagName().equalsIgnoreCase("div")
							||  elementOfInterest.getTagName().equalsIgnoreCase("a")							
							//	|| (elementOfInterest.getTagName().equalsIgnoreCase("img") && elementOfInterest.getText().equals(""))
							|| elementOfInterest.getAttribute("type").equalsIgnoreCase("radio")) {
						logger.info("elementOfInterest for IE click ---> "+ elementOfInterest.getText());
						elementOfInterest.click();
					} else {
						logger.info("elementOfInterest for IE sendKeys ---> "+ elementOfInterest.getText());
						elementOfInterest.sendKeys("\n");
					}
				}
				else {
					elementOfInterest.click();
				}
				wait(10000); // TODO: to check whether we need this wait for both click & sendKeys
			} else {
				elementOfInterest.click();
			}
		} catch (TimeoutException ex){
			// do nothing this behaviour is seen on IE. Exact reason is not known as on 7 jan 2009
			logger.error("Exception while clicking an element : ");
			ex.printStackTrace();
		}
		catch (NullPointerException ex){
			ex.printStackTrace();
			return false;
		}

		return true;		
	}
	
	public void mouseHover(String action)
	{
		Actions actions = new Actions(driver);
		actions.moveToElement(getWebElementFromElementKey(action));
		actions.click().build().perform();
	
	}
	
	public void mouseHover(String action,String element)
	{
		Actions actions = new Actions(driver);
		actions.moveToElement(getWebElementFromElementKey(action)).moveToElement(getWebElementFromElementKey(element));
		actions.click().build().perform();
	
	}
	
	protected Select getSelectDropDownOptionsForIndex(String elementKey,int index){
		AppUIElement selectDropDown = new AppUIElement(pageElements.get(elementKey));
		WebElement wSelectDropDown = getWebElementFromAppUIElement(selectDropDown);


		Select sSelectDropDown  = new Select(wSelectDropDown );
		 sSelectDropDown.selectByIndex(index);
		 return sSelectDropDown;
	}
	
	/*
	 * 	This method Writes text in text elements like text box, text area.
	 */
	protected boolean writeTextIntoTextBox (String elementKey, String textToWrite){
		return writeTextIntoTextBox (elementKey, textToWrite, -1);
	}

	/*
	 * 	This method does the following:
	 * 	1. 	Retrieve the Element Information from the hash map using the elementKey passed as parameter to the method.
	 * 	2.	Finds the Element on the web page. If Element found it sends text to the element and returns TRUE.
	 * 		If element is not found or there is some error in sending text it returns False.
	 */
	protected boolean writeTextIntoTextBox (String elementKey, String textToWrite, int... lineNumbers ){
		String action = "";
		if (textToWrite.length() > 0) {
			action = "Writing '" + textToWrite + "' to...";
		} else {
			action = "Focusing on...";
		}
		AppUIElement anElement;

		try{
			//	We need to create a clone of the element retrieved from the hashmap as we can't make the hash map dirty.
			anElement = new AppUIElement(pageElements.get(elementKey));
		}
		catch (NullPointerException ex) {
			throw new NullPointerException("The element "+elementKey+" key-value pair is not present or is wrong in the xml file!");
		}
		//	if the element extracted from the HashMap is a Line Item then we need to replace the "#" character with the LINE NUMBER.
		if (lineNumbers.length > 0){
			addLineNumberToElement(anElement, "#", lineNumbers);
		}

		waitOnElement(elementKey, 7500);

		if (!isElementVisible(elementKey, lineNumbers)) {
			throw new WebDriverException("Unable to read from non-visible WebElement: " + anElement.toString());
		}

		if ( anElement == null)
			return false;	// check if element is not found in the hash map then return FALSE.

		//Log the action
		System.out.print(action);

		WebElement elementOfInterest = getWebElementFromAppUIElement(anElement);

		if (textToWrite == null)
			textToWrite = "";

		try {
			//This is needed for Chrome instance
			//The Firefox and InternetExplorer drivers are also RemoteWebDriver
			// so we need to explicitly exclude these drivers from the following
			// click call.
			//Chrome also doesn't need this click call now.
			//if (!(driver instanceof FirefoxDriver || driver instanceof InternetExplorerDriver))
			//elementOfInterest.click();
			elementOfInterest.sendKeys("");
			elementOfInterest.clear();	// clears the text in the element before sending keys.
			wait(300);
			elementOfInterest.sendKeys(textToWrite);
		} catch (NullPointerException ex){
			ex.printStackTrace();
			return false;
		}

		//Now attempt to read the value from the field to make sure that it was written
		// correctly.  This should fix issues we were seeing where the first part of the
		// text is cut off.
		try {
			if (textToWrite.length() > 0 && !elementOfInterest.getAttribute("value").equals(textToWrite)) {
				elementOfInterest.clear();
				//The Firefox and InternetExplorer drivers are also RemoteWebDriver
				// so we need to explicitly exclude these drivers from the following
				// click call.
				if (!(driver instanceof FirefoxDriver || driver instanceof InternetExplorerDriver))
					elementOfInterest.click();				
				elementOfInterest.sendKeys(textToWrite);
			}
		} catch(Exception ex) {
			//Do nothing if this fails since it's just a fallback anyway.
		}

		//Hack prevent failures while running test cases in remote machine
		//type.tab();

		//Check for JS alerts


		return true;
	}

	/*
	 * 	mouseOverAnElement method is used to mouse over a menu item to see the submenu and similar actions
	 *  @param elementkey elementkey to read the applicationelement from resource file
	 */
	public void mouseOverAnElement(String elementkey)
	{
	Actions builder = new Actions(driver);
	builder.moveToElement(getWebElementFromElementKey(elementkey)).build().perform();
	}
	
	/*
	 * 	openMenu method is used to mouse over a menu item to see the submenu and similar actions. This is same as mouseOverAnElement
	 *  @param elementkey elementkey to read the applicationelement from resource file
	 */
	public void openMenu(String elementkey)
	{
		mouseOverAnElement(elementkey);
	}
	
	
	/*
	 * 	openMenuAndSelectMenuItem method is used to mouse over a menu item and click on a submenu.
	 *  @param submenuitemelementkey elementkey to read the menu's application element from resource file
	 */
	public void openSubMenuItem(String submenuitemelementkey)
	{
	clickIfElementDisplayed(submenuitemelementkey);
	}
	
	
	public boolean isModalWindowPresent (){


		int numberOfOpenWindows = driver.getWindowHandles().size();
		if ( numberOfOpenWindows > 1)
			return true;
		else 
			return false;
	}

	/**
	 * 	This method is used to set focus on a particular Web Element eg: button. 
	 * 	This is a workaround as no existing API supports this in GWD.
	 * 	@param elementKey : key of the element to be interacted with on the Page.
	 *  @return TRUE if Set Focus operation is done successfully. Otherwise returns FALSE. 
	 */
	protected boolean setFocusOnElement (String elementKey){
		String action = "Focussing on Element...";
		//	We need to create a clone of the element retrieved from the hashmap as we can't make the hash map dirty. 
		AppUIElement anElement = new AppUIElement(pageElements.get(elementKey));
		if (anElement == null)
		{
			return false;	// check if element is not found in the hash map then return FALSE.
		}
		//Log the action    
		System.out.print(action);
		WebElement elementOfInterest = getWebElementFromAppUIElement(anElement);

		try {
			elementOfInterest.sendKeys("");	
		} catch (NullPointerException ex){
			ex.printStackTrace();
			return false;
		}
		//Check for JS alerts

		return true;		
	}

	


	/* WAIT FUNCTIONS*/
	/**
	 * Sleeps the specified amount of milliseconds
	 * @param milliseconds
	 * @deprecated
	 * The reason I am deprecating this is that we should really avoid using waits. 
	 * We should rely more on ait for page and only use this when no other solution works.
	 */
	@Deprecated	
	public void wait(int milliseconds) {

		long endTime = clock.laterBy(milliseconds);
		while (clock.isNowBefore(endTime)) {
			try {
				Thread.sleep(WAIT_INCR);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method waits the specified time, and also prints a message out.
	 * Useful for when you want to debug what you're waiting on and see in real
	 * time when waits occur
	 * 
	 * @param milliseconds
	 * @param message
	 */
	public void wait(int milliseconds, String message) {
		logger.info(message);
		wait(milliseconds);
	}


	/**
	 * Waits until webdriver can find an element with the specified name attribute.
	 * 
	 * @param name the 'name' attribute value
	 * @param maxMilliseconds how long we should wait
	 * @param frame the frame the element is located on
	 * @param ignoreRendering do we care if the element is visible or not
	 * @return true if WebDriver is able to find a web element with the given name attribute value
	 */
	public boolean waitOnName(String name, int maxMilliseconds, String frame, boolean ignoreRendering) {

		WebElement identifier = null;
		int secondsPassed = 0;

		while (secondsPassed < maxMilliseconds) {
			try {
				if (frame == null)
					identifier = driver.findElement(By.name(name));
				else {
					driver.switchTo().defaultContent();
					identifier = driver.switchTo().frame(frame).findElement(By.name(name));
				}
			} catch (Exception e) {			
				identifier = null;
			}

			if (identifier != null){ // && !(driver instanceof SafariDriver)) { // can't cast SafariWebElement to RenderedWebElement
				if (identifier.isDisplayed())
					System.out.print("   Object name Found: "+ name);
				else {
					logger.info("   ...looking for object name ("+ name +")");
					if (driver instanceof InternetExplorerDriver) {
						wait(500); // prevent too much checking if IE (it seems to crash on these alot)
					}
				}
			}
			logger.info("identifier currently:" + identifier + " and driver: " + driver);
			if (identifier != null && (/*driver instanceof SafariDriver || */ ignoreRendering || (identifier.isDisplayed()) )) {
				logger.info("   Found name in: " + secondsPassed/1000 + " sec");
				return true;
			}
			secondsPassed += WAIT_INCR;
			wait(WAIT_INCR);
		}
		logger.info("Waiting for object name: " + name + " timed out after " + maxMilliseconds/1000 + " sec");
		return false;
	}

	/**
	 * Waits until webdriver can find an element with the specified name attribute.
	 * @param name
	 * @param frame
	 * @return
	 */
	public boolean waitOnName(String name, String frame ) {
		return waitOnName(name, WAIT_TIME, frame, false);
	}

	/**
	 * Waits until webdriver can find an element with the specified name attribute.
	 * @param name
	 * @return
	 */
	public boolean waitOnName(String name) {
		return waitOnName(name, WAIT_TIME, null, false);
	}

	/**
	 * Waits until webdriver can find an element with the specified name attribute.
	 * @param name
	 * @param ignoreRendering
	 * @return
	 */
	public boolean waitOnName(String name, boolean ignoreRendering) {
		return waitOnName(name, WAIT_TIME, null, ignoreRendering);
	}

	/**
	 * Waits until webdriver can find the specified header text. The header
	 * title is the common ui header text that spans all pages (part of the new
	 * application ui refresh)
	 * 
	 * @param id
	 * @param maxMilliseconds
	 * @param frame
	 * @param ignoreRendering
	 * @return true if WebDriver is able to find an element with the given id
	 */
	public boolean waitOnId(String id, int maxMilliseconds, String frame, boolean ignoreRendering) {

		WebElement identifier = null;
		int secondsPassed = 0;

		while (secondsPassed < maxMilliseconds) {
			try {
				if (frame == null)
					identifier = driver.findElement(By.id(id));
				else {
					driver.switchTo().defaultContent();
					identifier = driver.switchTo().frame(frame).findElement(By.id(id));
				}
			} catch (Exception e) {	
				if (isSafariDummyPlug()) {frame = null; e.printStackTrace();}
				identifier = null;
			}

			try {
				if (identifier != null && !isSafariDummyPlug()) {
					if (identifier.isDisplayed())
						System.out.print("   Id Found: "+ id);
					else {
						logger.info("   ...looking for id ("+ id +")");
						if (driver instanceof InternetExplorerDriver) {
							wait(500);
						}
					}
				}
			} catch(RuntimeException e) {
				wait(WAIT_INCR);
				identifier = null;

				if (isSafariDummyPlug()){e.printStackTrace(); frame = null;}
			}

			if (identifier != null && (isSafariDummyPlug() || ignoreRendering || (identifier.isDisplayed())) ) {
				logger.info("   Found id in: " + secondsPassed/1000 + " sec");
				return true;
			}
			secondsPassed += WAIT_INCR;
			wait(WAIT_INCR);
			//logger.info("Waiting on identifier(" + id + ")...");
		}
		logger.info("Waiting for id: " + id + " timed out after " + maxMilliseconds/1000 + " sec");
		return false;
	}


	public boolean waitOnId(String id, String frame ) {
		return waitOnId(id, WAIT_TIME, frame, false);
	}

	public boolean waitOnId(String id) {
		return waitOnId(id, WAIT_TIME, null, false);
	}
	public boolean waitOnId(String id, boolean ignoreRendering) {
		return waitOnId(id, WAIT_TIME, null, ignoreRendering);
	}	
	public boolean waitOnId(String id, int maxTime, boolean ignoreRendering) {
		return waitOnId(id, maxTime, null, ignoreRendering);
	}	
	public boolean waitOnId(String id, int maxTime) {
		return waitOnId(id, maxTime, null, false);
	}

	public boolean waitOnId(String id, String frame, boolean ignoreRendering) {
		return waitOnId(id, WAIT_TIME, frame, ignoreRendering);
	}
	
	

	/**
	 * Waits until webdriver can find the element specified by xpath.
	 *
	 * @param path
	 * @param maxMilliseconds
	 * @param frame
	 * @param ignoreRendering
	 * @return true if WebDriver is able to find an element with the given id
	 */
	public boolean waitOnXPath(String path, int maxMilliseconds, String frame, boolean ignoreRendering) {

		WebElement identifier = null;
		int secondsPassed = 0;

		while (secondsPassed < maxMilliseconds) {
			try {
				if (frame == null)
					identifier = driver.findElement(By.xpath(path));
				else {
					driver.switchTo().defaultContent();
					identifier = driver.switchTo().frame(frame).findElement(By.xpath(path));
				}
			} catch (Exception e) {
				if (isSafariDummyPlug()) {frame = null; e.printStackTrace();}
				identifier = null;
			}

			try {
				if (identifier != null && !isSafariDummyPlug()) {
					if (identifier.isDisplayed())
						System.out.print("   XPath Found: "+ path);
					else {
						logger.info("   ...looking for xpath ("+ path +")");
						if (driver instanceof InternetExplorerDriver) {
							wait(500); 
						}
					}
				}
			} catch(RuntimeException e) {
				logger.info("Xpath not found");
				wait(WAIT_INCR);
				identifier = null;

				if (isSafariDummyPlug()){e.printStackTrace(); frame = null;}
			}

			if (identifier != null && (isSafariDummyPlug() || ignoreRendering || identifier.isDisplayed()) ) {
				logger.info("   Found id in: " + secondsPassed/1000 + " sec");
				return true;
			}
			secondsPassed += WAIT_INCR;
			wait(WAIT_INCR);
			//logger.info("Waiting on identifier(" + id + ")...");
		}
		logger.info("Waiting for xpath: " + path + " timed out after " + maxMilliseconds/1000 + " sec");
		return false;
	}
	
	
	public boolean waitOnXPath1(String path, int maxMilliseconds, int frame, boolean ignoreRendering) {

		WebElement identifier = null;
		int secondsPassed = 0;

		while (secondsPassed < maxMilliseconds) {
			
				
				
					driver.switchTo().defaultContent();
					identifier = driver.switchTo().frame(frame).findElement(By.xpath(path));
				}
		
			

			
		logger.info("Waiting for xpath: " + path + " timed out after " + maxMilliseconds/1000 + " sec");
		return false;
	}
	
	public boolean waitOnXPath1(String path, int frame ) {
		return waitOnXPath1(path, WAIT_TIME, frame, false);
	}

	public boolean waitOnXPath(String path, String frame ) {
		return waitOnXPath(path, WAIT_TIME, frame, false);
	}
	public boolean waitOnXPath(String path) {
		return waitOnXPath(path, WAIT_TIME, null, false);
	}
	public boolean waitOnXPath(String path, boolean ignoreRendering) {
		return waitOnXPath(path, WAIT_TIME, null, ignoreRendering);
	}
	public boolean waitOnXPath(String path, int maxTime, boolean ignoreRendering) {
		return waitOnXPath(path, maxTime, null, ignoreRendering);
	}
	public boolean waitOnXPath(String path, int maxTime) {
		return waitOnXPath(path, maxTime, null, false);
	}
	public boolean waitOnXPath(String path, String frame, boolean ignoreRendering) {
		return waitOnXPath(path, WAIT_TIME, frame, ignoreRendering);
	}

	
	//********************************************
	/*
	 * Suspend test for a specified number of milliseconds.
	 */

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
	/*
	public boolean waitForElementPresent(By by)
	{
		return (false);
	}
	*/

}// End of OmniPage Class
