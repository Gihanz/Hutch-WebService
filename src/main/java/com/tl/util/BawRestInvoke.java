package com.tl.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:config.properties")
public class BawRestInvoke {
	
	static Logger log = LoggerFactory.getLogger(BawRestInvoke.class);
	
	@Value("${BAW_BASE_URL}")
	private String bawBaseUrl;

	@Value("${BAW_USERNAME}")
	private String bawUsername;

	@Value("${BAW_PASSWORD}")
	private String bawPassword;

	
	public String doPost(URL requestUrl) {
		
		String response = null;
		try {
				
			String authString = bawUsername + ":" + bawPassword;
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);

			HttpURLConnection conn = (HttpURLConnection) requestUrl.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "Basic " + authStringEnc);
			
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder stringBuilder = new StringBuilder();
			String output;
			while ((output = br.readLine()) != null) {
				log.info("BAW api response : "+output);
				stringBuilder.append(output);
			}
			conn.disconnect();
			response = stringBuilder.toString();
		
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.error(e.getMessage() + "" + e);
			response = e.getMessage() + " : " + e;
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage() + "" + e);
			response = e.getMessage() + " : " + e;
		}
		
		return response;
		
	}
	
	public String doGet(URL requestUrl) {
		
		String response = null;
		try {
				
			String authString = bawUsername + ":" + bawPassword;
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);

			HttpURLConnection conn = (HttpURLConnection) requestUrl.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "Basic " + authStringEnc);
			
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder stringBuilder = new StringBuilder();
			String output;
			while ((output = br.readLine()) != null) {
				log.info("BAW api response : "+output);
				stringBuilder.append(output);
			}
			conn.disconnect();
			response = stringBuilder.toString();
		
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.error(e.getMessage() + "" + e);
			response = e.getMessage() + " : " + e;
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage() + "" + e);
			response = e.getMessage() + " : " + e;
		}
		
		return response;
		
	}
	
	public String finishTask(String taskID, JSONObject taskData) {
		
		String response = null;
		try {

			String encodedTaskData = URLEncoder.encode(taskData.toString(), "UTF-8");  
			URL requestUrl = new URL(bawBaseUrl+"task/"+taskID+"?action=finish&parts=all&params="+encodedTaskData);
			response = doPost(requestUrl);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.error(e.getMessage() + "" + e);
			response = e.getMessage() + " : " + e;
		} catch (UnsupportedEncodingException e) {  
			e.printStackTrace();
			log.error(e.getMessage() + "" + e);
			response = e.getMessage() + " : " + e;  
        } 
			
		return response;
	}
	
	public String taskDetails(String taskID) {
		
		String response = null;
		try {
					
			URL requestUrl = new URL(bawBaseUrl+"task/"+taskID+"?parts=none");
			response = doGet(requestUrl);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.error(e.getMessage() + "" + e);
			response = e.getMessage() + " : " + e;
		}
			
		return response;
	}
	
}
