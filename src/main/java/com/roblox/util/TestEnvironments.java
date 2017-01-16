package com.roblox.util;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TestEnvironments 
{

	int temp[]={1,2,3,4};
	DesiredCapabilities capabilities= new DesiredCapabilities();
	HashSet<HashMap <String, Object>> environmentlist = new HashSet<HashMap <String, Object>>();
	HashMap <String, Object> environmentinfo= new HashMap <String, Object>();;
	
	public    HashSet<HashMap<String, Object>> getEnvironmentsList( ) throws Exception
	{
		return getEnvironmentsList("testenvironment.xml");
	}
	
	
	/**
	 * @param xmlfile 
	 * @return HashSet<HashMap<String, Object>>
	 * @throws Exception
	 * @comment Return HastSet of environmentinfo Objects which will have Map of prop and value
	 */
	
	public    HashSet<HashMap<String, Object>> getEnvironmentsList(String xmlfile) throws Exception
	{
		File file = new File(System.getProperty("user.dir") + ("//src//test//resources//"+xmlfile));
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);
		doc.getDocumentElement().normalize();
		
		NodeList nodeList = doc.getElementsByTagName("environment");
		
		//System.out.println(nodeLst.item(0).getTextContent());
		for (int s = 0; s < nodeList.getLength(); s++)
		{
			Node fstNode = nodeList.item(s);
			Element testElement = (Element) fstNode;
			loadEnvValueByProperty(testElement, "executionplatform");
			loadEnvValueByProperty(testElement, "testclient");
			loadEnvValueByProperty(testElement, "baseurl");
			environmentinfo.put("capabilities", loadCapabilities(testElement.getElementsByTagName("capabilities"),capabilities ));

			//TODO enhance the script to provide capabilities details if required.
			environmentlist.add(new HashMap <String, Object>(environmentinfo));
			environmentinfo.clear();
		
		}

		return environmentlist;
		}

	/**
	 * @param node XMLNode with environment info
	 * @param property property to be read and stored 
	 * @return void
	 * @comment Load the environment info to a list byt property and value
	 */
	
	public void loadEnvValueByProperty(Element node, String property)
	{
		String propvalue=null;
		
		try
		{
		Node childnode=node.getElementsByTagName(property).item(0);
		 propvalue=childnode.getChildNodes().item(0).getNodeValue();
		 environmentinfo.put(property, propvalue); 
		}
		catch (Exception e)
		{
			System.out.println("can't read value for property "+property+" in the environment file.");
		}	
	}
	
	
	/**
	 * @param nodelist XMLNodeList with capabilities info
	 * @param capabilities DesiredCapabilities Object that stored the environment info as capabilities object
	 * @return void
	 * @comment Load the environment info to a list byt property and value
	 */
	
	public Capabilities loadCapabilities(NodeList nodelist, DesiredCapabilities capabilities)
	{
		
			for (int i=0;i<nodelist.getLength();i++)
			{
				if(nodelist.item(i).getNodeType()==1)
				{
				Element element= (Element)nodelist.item(i);
				NodeList nl=element.getChildNodes();
				for (int j=0;j<nl.getLength();j++)
					{
					String nodename=null;
					String nodevalue = null;
					Node nValue = (Node) nl.item(j);
					if(nValue.getNodeType()==1)
						{
						nodename=nValue.getNodeName();
					    Node capabilitinode= nValue.getChildNodes().item(0);
					    if(capabilitinode!=null)
					    	nodevalue=capabilitinode.getNodeValue();
						// nodevalue=element.getElementsByTagName(nodename).item(0).getNodeValue();
							
						System.out.println("Capabilities are "+nodename+" : "+nodevalue);
						capabilities.setCapability(nodename, nodevalue);
						}
					}
				}
			}
			return capabilities;
	
	}
	
}
