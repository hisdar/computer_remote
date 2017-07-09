package cn.hisdar.lib.configuration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class HConfig {
	
	private static ArrayList<HConfig> configList = null;
	private static final String DESCRIPTION_ATTR_NAME = "description";
	
	private String configFilePath;
	private ArrayList<ConfigItem> configItems;
	
	private HConfig() {
		this.configItems = new ArrayList<ConfigItem>();
	}
	
	private HConfig(String configFilePath) {
		// load configuration item from configuration file
		this.configItems = new ArrayList<ConfigItem>();
		loadConfigItemsFromConfigFile(configFilePath);
	}
	
	private String getAttributeValueByName(NamedNodeMap attributeMap, String attributeName) {
		Node attributeNode = attributeMap.getNamedItem(attributeName);
		if (attributeNode != null) {
			return attributeNode.getTextContent();
		}
		
		return null;
	}
	
	private boolean loadConfigItemsFromConfigFile(String configFilePath) {
		this.configFilePath = configFilePath;

		// remove all configuration items
		while (configItems.size() > 0) {
			configItems.remove(0);
		}
		
		if (configFilePath == null) {
			configItems = null;
			return false;
		}
		
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			File xmlFile = new File(configFilePath);
			if (!xmlFile.exists()) {
				System.err.println("HConfig.loadConfigItemsFromConfigFile(): config file not exist: " + configFilePath);
				return false;
			}
			
			Document document = db.parse(xmlFile);

			NodeList configNodeList = document.getChildNodes();
			if (configNodeList.getLength() != 1) {
				System.err.println("HConfig.loadConfigItemsFromConfigFile(): Config file is not a HConfig file:" + configFilePath);
				return false;
			}
			
			Node mainConfig = configNodeList.item(0);
			NodeList configItemList = mainConfig.getChildNodes();
			Node configItemNode = null;
			for (int i = 0; i < configItemList.getLength(); i++) {
				configItemNode = configItemList.item(i);
				if (configItemNode.getNodeName().equals("#text")) {
					continue;
				}
				
				String configName = configItemNode.getNodeName();
				String configValue = configItemNode.getTextContent();
				String configDescription = null;
				if (configItemNode.hasAttributes()) {
					configDescription = getAttributeValueByName(configItemNode.getAttributes(), DESCRIPTION_ATTR_NAME);
				}
				
				ConfigItem configItem = new ConfigItem(configName, configValue, configDescription);
				
				configItems.add(configItem);
			}
		} catch (ParserConfigurationException
				| SAXException
				| IOException e) {
			System.err.println("HConfig.loadConfigItemsFromConfigFile: " + e.getMessage());
			System.err.println(e);
			return false;
		}

		return true;
	}

	private boolean saveConfigItemsToFile() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			System.err.println("HConfig.saveConfigItemsToFile: " + e1.getMessage());
			System.err.println(e1);
			return false;
		}
		
		File configFile = new File(configFilePath);
		if (!configFile.exists()) {
			File configFileParent = new File(configFile.getParent());
			configFileParent.mkdirs();
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (!configFile.exists()) {
			System.err.println("HConfig.saveConfigItemsToFile: create config file fail");
			return false;
		}
		
		Document document = builder.newDocument();

		Element rootElement = document.createElement("Config"); 
        ConfigItem configItem = null;
        for (int i = 0; i < configItems.size(); i++) {
        	configItem = configItems.get(i);
        	Element configItemElement = document.createElement(configItem.name);
        	
        	configItemElement.appendChild(document.createTextNode(configItem.value));
        	if (configItem.description != null) {
        		configItemElement.setAttribute(DESCRIPTION_ATTR_NAME, configItem.description);
        	}
        	
        	rootElement.appendChild(configItemElement);
		}
        
        document.appendChild(rootElement); 
        TransformerFactory tf = TransformerFactory.newInstance();
        try {
            Transformer transformer = tf.newTransformer();
            document.setXmlStandalone(true);
            
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");//ÉèÖÃËõ½øÎª4
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            
            FileOutputStream xmlOutputStream = new FileOutputStream(configFilePath);
            OutputStreamWriter xmlOutputStreamWriter = new OutputStreamWriter(xmlOutputStream, "UTF-8");
            BufferedWriter xmlBufferedWriter = new BufferedWriter(xmlOutputStreamWriter);
            PrintWriter pw = new PrintWriter(xmlBufferedWriter);
            StreamResult result = new StreamResult(pw);
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
            
            pw.flush();
            xmlBufferedWriter.flush();
            xmlOutputStreamWriter.flush();
            xmlOutputStream.flush();
            
            pw.close();
           // xmlBufferedWriter.close();
            xmlOutputStreamWriter.close();
            xmlOutputStream.close();
        } catch (IllegalArgumentException
        		| TransformerException | IOException e) {
        	System.err.println(e);
        	System.err.println("HConfig.saveConfigItemsToFile: Fail to save config item:" + configItem.toString());
        }
        
		return true;
	}
	
	private static ArrayList<HConfig> getInstance() {
		if (configList == null) {
			synchronized (HConfig.class) {
				if (configList == null) {
					configList = new ArrayList<HConfig>();
				}
			}
		}
		
		return configList;
	}
	
	public static HConfig getInstance(String configPath) {
		return getInstance(configPath, false);
	}
	
	public static HConfig getInstance(String configPath, boolean isCreate) {	
		
		ArrayList<HConfig> configList = getInstance();
		
		HConfig currentConfig = null;
		for (int i = 0; i < configList.size(); i++) {
			if (configList.get(i).configFilePath.equals(configPath)) {
				currentConfig = configList.get(i);
				break;
			}
		}
		
		if (currentConfig == null) {
			currentConfig = new HConfig();
			if (currentConfig.loadConfigItemsFromConfigFile(configPath)) {
				System.err.println("HConfig.getInstance(): Load config file success:" + configPath);
				configList.add(currentConfig);
			} else {
				if (!isCreate) {
					System.err.println("HConfig.getInstance(): Load config file fail:" + configPath);
				}
			}
		}
		
		return currentConfig;
	}

	public String getConfigValue(String configName) {
		// search configuration value form configuration items
		String configValueString = null;
		for (int i = 0; i < configItems.size(); i++) {
			if (configItems.get(i).name.equals(configName)) {
				configValueString = configItems.get(i).value;
				break;
			}
		}
		
		return configValueString;
	}
	
	public int getConfigValue(String configName, int defaultValue) {
		// search configuration value form configuration items
		String configValueString = getConfigValue(configName);
		
		int configValue = defaultValue;
		if (configValueString != null) {
			try {
				configValue = Integer.parseInt(configValueString.trim());
			} catch (NumberFormatException e) {
				System.err.println("HConfig.getConfigValue() fail");
				System.err.println(e);
			}
		}
		
		return configValue;
	}

	public double getConfigValue(String configName, double defaultValue) {
		String configValueString = getConfigValue(configName);
		
		double configValue = defaultValue;
		if (configValueString != null) {
			try {
				configValue = Double.parseDouble(configValueString.trim());
			} catch (NumberFormatException e) {
				System.err.println("HConfig.getConfigValue() fail");
				System.err.println(e);
			}
		}
		
		return configValue;
	}
	
	public boolean getConfigValue(String configName, boolean defaultValue) {
		String configValueString = getConfigValue(configName);
		
		boolean configValue = defaultValue;
		if (configValueString != null) {
			try {
				configValue = Boolean.parseBoolean(configValueString.trim());
			} catch (NumberFormatException e) {
				System.err.println("HConfig.getConfigValue() fail");
				System.err.println(e);
			}
		}
		
		return configValue;
	}
	
	public String getConfigValue(String configName, String defaultValue) {
		String configValueString = getConfigValue(configName);
		
		String configValue = defaultValue;
		if (configValueString != null) {
			configValue = configValueString;
		}
		
		return configValue;
	}
	
	public void setConfigItem(ConfigItem configItem) {
		boolean foundFlag = false;
		for (int i = 0; i < configItems.size(); i++) {
			if (configItems.get(i).name.equals(configItem.name)) {
				configItems.get(i).value = configItem.value;
				configItems.get(i).description = configItem.description;
				foundFlag = true;
			}
		}
		
		if (!foundFlag) {
			configItems.add(new ConfigItem(configItem));
		}
		
		saveConfigItemsToFile();
	}

	public void addConfigItem(ConfigItem configItem) {
		for (int i = 0; i < configItems.size(); i++) {
			if (configItems.get(i).equals(configItem)) {
				return ;
			}
		}
		configItems.add(new ConfigItem(configItem));
		saveConfigItemsToFile();
	}
	
	public void removeConfigItem(String configName) {
		int configItemSize = configItems.size();
		for (int i = configItemSize - 1; i >= 0; i--) {
			if (configItems.get(i).name.equals(configName)) {
				configItems.remove(i);
			}
		}
		
		saveConfigItemsToFile();
	}

	public void clear() {
		int configItemSize = configItems.size();
		for (int i = configItemSize - 1; i >= 0; i--) {
			configItems.remove(i);
		}
		
		saveConfigItemsToFile();
	}

	public ArrayList<ConfigItem> getConfigItemList() {
		
		return configItems;
	}

	public int getConfigCount() {
		return configItems.size();
	}
}
