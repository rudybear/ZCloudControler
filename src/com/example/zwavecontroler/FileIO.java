package com.example.zwavecontroler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.util.Log;

public class FileIO {
	
	public static void writeToFile(String filename, String data)
	{
		File newFile = new File(filename);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(newFile);
			fos.write(data.getBytes());
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String readFromFile(String filename) throws FileNotFoundException
	{
		String fileReaded = "";
		try {
			File fileDir = new File(filename);
			BufferedReader in = new BufferedReader(
			   new InputStreamReader(
	                      new FileInputStream(fileDir), "UTF8"));
			String str;
			while ((str = in.readLine()) != null) {
			    fileReaded += str;
			}
	                in.close();
	    } 
	    catch (UnsupportedEncodingException e) 
	    {
			System.out.println(e.getMessage());
	    } 
	    catch (IOException e) 
	    {
			System.out.println(e.getMessage());
	    }
	    catch (Exception e)
	    {
			System.out.println(e.getMessage());
	    }
		return fileReaded;
		
		/*
		StringBuilder db = new StringBuilder();
		FileInputStream fis = null;
		byte[] buf;
		int n = 0;
		fis = new FileInputStream(new File(filename));
		buf = new byte[1];
		try {
			while ((n = fis.read(buf)) >= 0) {
				for (byte bit : buf) {
					db.append((char) bit);
				}
			}
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.v("FILEIO", db.toString());
		return db.toString();
		*/
	}
}
