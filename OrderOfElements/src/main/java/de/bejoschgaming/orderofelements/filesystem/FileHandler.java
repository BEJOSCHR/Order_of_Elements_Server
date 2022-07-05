package de.bejoschgaming.orderofelements.filesystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.bejoschgaming.orderofelements.debug.ConsoleHandler;

public class FileHandler {
	
	private static File ordner = new File("OrderOfElements/rsc");
	public static File file_Settings = new File(ordner+"/Settings.xml");

	
//==========================================================================================================
	/**
	 * Fill first configs if empty
	 */
	public static void firstWrite() {
		
		if(!ordner.exists()) {
			try {
				boolean success = ordner.mkdirs();
				ConsoleHandler.printMessageInConsole("Created folder '"+ordner.getAbsolutePath()+"' on first write ("+success+")", true);
			}catch(SecurityException error) {
				ConsoleHandler.printMessageInConsole("No permissions to create folder for setting files, pls update via chmod!", true);
			}
		}
		if(readOutData(file_Settings, "Successfully") == null) {
			createNewXmlFile(file_Settings);
			saveDataInFile(file_Settings, "CONNECTION_Port", "6776");
			saveDataInFile(file_Settings, "CONNECTION_Idletime", "10");
			saveDataInFile(file_Settings, "CONNECTION_Packetdivider", "_:_");
			saveDataInFile(file_Settings, "DB_Name", "OrderOfElements");
			saveDataInFile(file_Settings, "DB_Url", "jdbc:mysql://localhost:3306/");
			saveDataInFile(file_Settings, "DB_User", "OrderOfElements");
			saveDataInFile(file_Settings, "DB_Password", "1991");
			ConsoleHandler.printMessageInConsole("Created file '"+file_Settings.getAbsolutePath()+"' on first write", true);
		}
		
		ConsoleHandler.printMessageInConsole("Finished file system check!", true);
		
	}
	
//==========================================================================================================
	/**
	 * Save data in a given XML File witch could later be read out by the key
	 * @param file - file - The file there the data should be saved
	 * @param key - String - The key later this data can be read out
	 * @param value - String - The data that should be saved !!!If null the current data will be deleted!!!
	 * @return boolean - true if save worked, false if something goes wrong
	 */
	public static boolean saveDataInFile(File file, String key, String value) {
		
		Node testLoadedNote = readOutNote(file, key);
		
		try{ 
			
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			
			Document document = documentBuilder.parse(file);
			
			if(value == null) {
				//DELETE MODE - REMOVE IT
				document.getDocumentElement().removeChild(document.getDocumentElement().getElementsByTagName(key).item(0));
			}else if(testLoadedNote != null) {
				//ALREADY SAVED - SO DELETE FOR THE NEW ONE
				document.getDocumentElement().removeChild(document.getDocumentElement().getElementsByTagName(key).item(0));
				//THEN SAVE THE NEW ONE
				Element data = document.createElement(key);
				data.appendChild(document.createTextNode(value));
				document.getDocumentElement().appendChild(data);
			}else {
				//JUST SAVE
				Element data = document.createElement(key);
				data.appendChild(document.createTextNode(value));
				document.getDocumentElement().appendChild(data);
			}
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			
			StreamResult streamResult = new StreamResult(file);
			
			transformer.transform(source, streamResult);
			
			return true;
			
		}catch(ParserConfigurationException | SAXException | IOException | TransformerException e) {
			e.printStackTrace();
		}
			
		return false;
		
	}
	
//==========================================================================================================
	/**
	 * Load if possible a data witch is connected to the key
	 * @param file - file - The target file
	 * @param key - String - The key should be searched for
	 * @return String - The data found from the key or null if not
	 */
	public static String readOutData(File file, String key) {
		
		try {
			
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(file);
			
			NodeList nodeList = document.getElementsByTagName("Container");
			
			for(int i = 0 ; i < nodeList.getLength() ; i++) {
				
				Node node = nodeList.item(i);
				
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					
					Element element = (Element) node;
					String data = element.getElementsByTagName(key).item(0).getTextContent();
					
					return data;
					
				}
				
			}
		
		}catch(FileNotFoundException e) {
			//IGNORE - FILE WILL BE CREATED
			return null;
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
//==========================================================================================================
	/**
	 * Load if possible a Node witch has the same file and key values
	 * @param file - file - The target file
	 * @param key - String - The key should be searched for
	 * @return Node - The node found from the key or null if not
	 */
	private static Node readOutNote(File file, String key) {
		
		try {
			
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(file);
			
			NodeList nodeList = document.getElementsByTagName("Container");
			
			for(int i = 0 ; i < nodeList.getLength() ; i++) {
				
				Node node = nodeList.item(i);
				
				if(node.getNodeType() == Node.ELEMENT_NODE && node != null) {
					
					Element element = (Element) node;
					
					if(element.getElementsByTagName(key).item(0) != null) {
						if(element.getElementsByTagName(key).item(0).getNodeName().equalsIgnoreCase(key)) {
							return element.getElementsByTagName(key).item(0);
						}
					}
					
				}
				
			}
		
		}catch(FileNotFoundException e) {
			//IGNORE - FILE WILL BE CREATED
			return null;
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
//==========================================================================================================
	/**
	 * Create a XML File from a given File
	 * @param file - file - The file witch should be created
	 * @return boolean - true if create worked, false if something goes wrong
	 */
	public static boolean createNewXmlFile(File file) {
		
		try{ 
			
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			
			Document document = documentBuilder.newDocument();
			
			Element element = document.createElement("Container");
			document.appendChild(element);
			
			Element data = document.createElement("Successfully");
			data.appendChild(document.createTextNode("Created"));
			element.appendChild(data);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			
			StreamResult streamResult = new StreamResult(file);
			
			transformer.transform(source, streamResult);
			
		}catch(ParserConfigurationException | DOMException | TransformerException e) {
			e.printStackTrace();
		}
			
		return false;
		
	}
	
}
