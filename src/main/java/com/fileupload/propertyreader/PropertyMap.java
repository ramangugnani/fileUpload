package com.fileupload.propertyreader;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fileupload.constants.PropertyEnum;

public class PropertyMap {
	
	private static final Logger LOG = Logger.getLogger(PropertyMap.class.getName());

	private static PropertyMap propertyMap = null;

	private Map<PropertyEnum,String> configuration = new HashMap<PropertyEnum, String>();	

	private  Map<String,Integer> seedNodesByPort = null;

	synchronized public static PropertyMap  getInstance(){
		if(null == propertyMap){
			propertyMap = new PropertyMap();
		}
		return propertyMap;
	}

	private PropertyMap(){

	}

	public Map<PropertyEnum, String> getConfiguration() {
		return configuration;
	}

	public Map<String, Integer> getSeedNodesByPort() {
		return seedNodesByPort;
	}
	
	public String getSeedNodes() {
		StringBuilder builder = new StringBuilder();
		for(Map.Entry<String, Integer> entry : seedNodesByPort.entrySet()){
			builder.append(entry.getKey());
			builder.append(":");
			builder.append(entry.getValue());
			builder.append(",");
		}
		builder.deleteCharAt(builder.length()-1);
		return builder.toString();
	}

	public void setSeedNodesByPort(String seedNodes) {
		String[] seedNodeArray = seedNodes.split(",");
		if(null == seedNodesByPort){
			seedNodesByPort = new HashMap<String, Integer>();
		}
		
		for(String seedNode : seedNodeArray){
			String[] ipPort = seedNode.split(":");
			seedNodesByPort.put(ipPort[0], Integer.valueOf(ipPort[1]));			
		}
		LOG.info(seedNodesByPort);
	}
}
