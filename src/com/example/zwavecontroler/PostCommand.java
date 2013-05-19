package com.example.zwavecontroler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class PostCommand {

	private String targetUrl;
	private String headerAuthentcation;
	private int port;
	private String resultString = null;

	public PostCommand(String url, Context context) {
		this.targetUrl = url;
		parseForLogin();
	}
	
	private void parseForLogin() {
		String xmlString;
		try {
			xmlString = FileIO.readFromFile(Environment
					.getExternalStorageDirectory() + "/Zwave/login.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xmlString));
			Document doc = dBuilder.parse(is);

			NodeList loginNodes = doc.getElementsByTagName("header");
			Element loginElt = (Element) loginNodes.item(0);
			headerAuthentcation = loginElt.getAttribute("value");

			NodeList portNodes = doc.getElementsByTagName("port");
			Element portElt = (Element) portNodes.item(0);
			port = Integer.parseInt(portElt.getAttribute("value"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void postData() throws URISyntaxException, KeyStoreException,
			NoSuchAlgorithmException, CertificateException, IOException,
			KeyManagementException, UnrecoverableKeyException {
		
        DefaultHttpClient client = (DefaultHttpClient) WebClientDevWrapper.getNewHttpClient(port);

        HttpPost post = new HttpPost(targetUrl);
        post.setHeader("Authorization", headerAuthentcation);
        HttpResponse result = client.execute(post);
        HttpEntity entity = result.getEntity();
		InputStream is = entity.getContent();
		resultString = convertStreamToString(is);
	}


	/*
	 * protected org.apache.http.conn.ssl.SSLSocketFactory
	 * createAdditionalCertsSSLSocketFactory() { try { final KeyStore ks =
	 * KeyStore.getInstance("BKS");
	 * 
	 * final InputStream in = context.getResources().openRawResource(
	 * R.raw.certs); try { ks.load(in, context.getString(R.string.store_pass)
	 * .toCharArray()); } finally { in.close(); }
	 * 
	 * return new AdditionalKeyStoresSSLSocketFactory(ks);
	 * 
	 * } catch (Exception e) { throw new RuntimeException(e); } }
	 */

	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append((line + "\n"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public String getAnswer() {
		return resultString;
	}
}
