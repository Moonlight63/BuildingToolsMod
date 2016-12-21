package com.moonlight.buildingtools;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class UpdateHandler {
	
	private static String currentversion = Reference.VERSION;
	private static String newestVersion;
	
	public static String updateStatus = "NULL";

	
	public static void init(){
		getNewestVersion();
		
		if(newestVersion != null){
			if(newestVersion.equalsIgnoreCase(currentversion)){
				updateStatus = "§a[Building Tools] is up to date!";
			}
			else{
				updateStatus = "§c[Building Tools] is out of date! Current version: " + currentversion + "	Newest version: " + newestVersion;
			}
		}
		else{
			updateStatus = "Failed to check for update to Building Tools! Please make sure you are up to date for any bug fixes!";
		}
	}
	
	
	public static void getNewestVersion(){
		try{
			URL url = new URL("https://docs.google.com/uc?authuser=0&id=0B8HOvFad2ncDNjloV0xnZFpSdnM&export=download");
			Scanner s = new Scanner(url.openStream());
			newestVersion = s.next();
			s.close();
		}
		catch(IOException ex){
			ex.printStackTrace();
			System.out.println("Could not connect to check version");
		}
	}
}
