package com.fileupload.healthcheck.client;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fileupload.propertyreader.PropertyMap;

public class HealthCheckService implements Job{
	
	private static final Logger LOG = Logger.getLogger(HealthCheckService.class.getName());
	
	private Map<String,Integer> seedNodeByPort = new HashMap<String, Integer>();
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Map<String,Integer> seedNodeByPortNew = PropertyMap.getInstance().getSeedNodesByPort();
		for(Map.Entry<String, Integer> entry : seedNodeByPortNew.entrySet()){
			if(!seedNodeByPort.containsKey(entry.getKey())){
				seedNodeByPort.put(entry.getKey(), entry.getValue());
				LOG.info("Got new seed node creating health check connection with server."+entry.getKey());
				Thread thread = new HealthCheckClientMainThread(entry.getKey(),entry.getValue());
				thread.setName("client:"+entry.getKey());
				thread.start();
			}
		}
	}
	
}



