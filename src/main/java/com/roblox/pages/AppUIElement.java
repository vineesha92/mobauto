/**
 * 
 */
package com.roblox.pages;

/**
 * 	A simple bean class to represent Elements on a web Page.
 *
 */
public class  AppUIElement {

	private String XML_TEMPLATE = "<element key=\"%s\" findBy=\"%s\" id=\"%s\" tag=\"%s\" name=\"%s\" text=\"%s\" class=\"%s\" xPath=\"%s\" ieXPath=\"%s\" cssSelector=\"%s\"/>";

	/**
	 * 
	 * This method print the WebElement to a string for logs sake. 
	 */
	@Override
	public String toString() {
		
		String elementofinterest="WebElement [elementKey=" + elementKey+", findBy=" + findBy;
		if (findBy.equalsIgnoreCase("id"))
		{
			elementofinterest=elementofinterest+", elementID=" + elementID+ "]";
		}
		else if(findBy.equalsIgnoreCase("tag"))
		{
			elementofinterest=elementofinterest+", elementTag=" + elementTag+ "]";

		}
		else if(findBy.equalsIgnoreCase("name"))
		{
			elementofinterest=elementofinterest+", elementName=" + elementName+ "]";

		}
		else if(findBy.equalsIgnoreCase("text"))
		{
			elementofinterest=elementofinterest+", elementText=" + elementText+ "]";

		}
		else if(findBy.equalsIgnoreCase("xPath"))
		{
			elementofinterest=elementofinterest+", elementXPath=" + elementXPath+ "]";

		}
		else if(findBy.equalsIgnoreCase("class"))
		{
			elementofinterest=elementofinterest+", elementClass=" + elementClass+ "]";

		}
		else if(findBy.equalsIgnoreCase("iexpath"))
		{
			elementofinterest=elementofinterest+", elementIExPath=" + elementIExPath+ "]";

		}
		
		else if(findBy.equalsIgnoreCase("cssSelector"))
		{
			elementofinterest=elementofinterest+", cssSelector=" + cssSelector+ "]";

		}
		
		return elementofinterest;
	}

	public AppUIElement(String elementKey, String elementID,
			String elementName, String elementClass, String elementText,
			String elementXPath, String findBy, String elementTag, String elementIExPath) {
		
		this.elementKey = elementKey;
		this.elementID = elementID;
		this.elementName = elementName;
		this.elementClass = elementClass;
		this.elementText = elementText;
		this.elementXPath = elementXPath;
		this.findBy = findBy;
		this.elementTag = elementTag;
		this.elementIExPath = elementIExPath;
	}
	
	public AppUIElement(AppUIElement aUpThereWebElement){
		this.elementKey = aUpThereWebElement.elementKey;
		this.elementID = aUpThereWebElement.elementID;
		this.elementName = aUpThereWebElement.elementName;
		this.elementClass = aUpThereWebElement.elementClass;
		this.elementText = aUpThereWebElement.elementText;
		this.elementXPath = aUpThereWebElement.elementXPath;
		this.findBy = aUpThereWebElement.findBy;
		this.elementTag = aUpThereWebElement.elementTag;
		this.elementIExPath = aUpThereWebElement.elementIExPath;
	}
	

	public String getElementTag() {
		return elementTag;
	}

	public void setElementTag(String elementTag) {
		this.elementTag = elementTag;
	}

	public String getElementKey() {
		return elementKey;
	}

	public String getElementID() {
		return elementID;
	}

	public String getElementName() {
		return elementName;
	}

	public String getElementClass() {
		return elementClass;
	}

	public String getElementText() {
		return elementText;
	}

	public String getElementXPath() {
		return elementXPath;
	}

	public String getFindBy() {
		return findBy;
	}

	public String getElementIExPath(){
		return elementIExPath;
	}
	
	public String getElementcssSelector(){
		return cssSelector;
	}
	
	public void setElementID(String elementID) {
		this.elementID = elementID;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public void setElementClass(String elementClass) {
		this.elementClass = elementClass;
	}

	public void setElementText(String elementText) {
		this.elementText = elementText;
	}

	public void setElementXPath(String elementXPath) {
		this.elementXPath = elementXPath;
	}

	public void setFindBy(String findBy) {
		this.findBy = findBy;
	}
	
	public void setElementIExPath(String elementIExPath){
		this.elementIExPath = elementIExPath;
	}
	
	public void setElementcssSelector(String cssSelector){
		this.cssSelector = cssSelector;
	}

	public String toXML() {
		return String.format(XML_TEMPLATE, elementKey, findBy, elementID, elementTag, elementName, elementText, elementClass, elementXPath, elementIExPath,cssSelector);
	}
	
	private String elementKey = null;	//	The unique key associated with the element eg: Page_Body_Title
	
	//	Ways in which the element can be found.	
	private String elementID = null;	
	private String elementName = null;
	private String elementClass = null;
	private String elementText = null;
	private String elementXPath = null;
	private String elementTag = null;
	private String elementIExPath = null;
	private String cssSelector = null;

	
	// 	Preferred way of finding an element.
	private String findBy = null;	
}
