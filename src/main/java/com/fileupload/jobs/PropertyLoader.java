package com.fileupload.jobs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fileupload.constants.PropertyEnum;
import com.fileupload.propertyreader.PropertyMap;

public class PropertyLoader implements Job{
	
	private static final Logger LOG = Logger.getLogger(PropertyLoader.class.getName());
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("config.properties");
			// load a properties file
			prop.load(input);
			for(PropertyEnum propertyEnum : PropertyEnum.values()){
				String value = prop.getProperty(propertyEnum.getName());
				
				PropertyMap.getInstance().getConfiguration().put(propertyEnum, value);
				
				if(PropertyMap.getInstance().getSeedNodesByPort() == null){
					PropertyMap.getInstance().setSeedNodesByPort(PropertyMap.getInstance().getConfiguration().get(PropertyEnum.SEED_NODE));
				}
				LOG.info(propertyEnum.getName()+":"+value);
			}
		} catch (IOException ex) {
			LOG.error(ex);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					LOG.error(e);
				}
			}
		}
		
	}

}
