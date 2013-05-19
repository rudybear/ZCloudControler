package com.example.zwavecontroler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.os.Environment;
import android.util.Log;

public class XMLparser {

	public static ArrayList<Actioneur> getActioneurs() {
		ArrayList<Actioneur> actioneurs = new ArrayList<Actioneur>();
		
		try {
			String xmlString = FileIO.readFromFile(Environment.getExternalStorageDirectory() + "/Zwave/Rules.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xmlString));
			Document doc = dBuilder.parse(is);
			
			NodeList devicesNodes = doc.getElementsByTagName("device");
			String name;
			int id;
			Actioneur act;
			for(int i=0; i< devicesNodes.getLength(); i++)
			{
				Element deviceElt = (Element) devicesNodes.item(i);
				name = deviceElt.getAttribute("description");
				Log.v("Parser", name);
				id = Integer.parseInt(deviceElt.getAttribute("device"));
				
				act = new Actioneur(id, name);
				actioneurs.add(act);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return actioneurs;
	}
	
	public static ArrayList <Scene> getScenes() {
		ArrayList<Scene> scenes = new ArrayList <Scene>();
		
		String xmlString;
		try {
			xmlString = FileIO.readFromFile(Environment.getExternalStorageDirectory() + "/Zwave/Rules.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xmlString));
			Document doc = dBuilder.parse(is);
			
			NodeList nodes = doc.getElementsByTagName("scene");
			for (int i = 0; i < nodes.getLength(); i++) {
				Element elt = (Element) nodes.item(i);
				String idString = elt.getAttribute("id");
				String name = new String(nodes.item(i).getFirstChild().getFirstChild().getNodeValue().getBytes(), "UTF-8");
				Log.v("Parse", name + " " + nodes.item(i).getFirstChild().getFirstChild().getNodeValue());
				int id = Integer.parseInt(idString);
				Scene scene = new Scene(id, name);
				scenes.add(scene);
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return scenes;
	}
}
