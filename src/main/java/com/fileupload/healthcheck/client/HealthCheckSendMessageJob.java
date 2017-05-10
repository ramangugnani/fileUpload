package com.fileupload.healthcheck.client;

import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fileupload.healthcheck.message.HealthCheckMessage;
import com.fileupload.propertyreader.PropertyMap;
import com.google.gson.Gson;

public class HealthCheckSendMessageJob implements Job{

	private static final Logger LOG = Logger.getLogger(HealthCheckSendMessageJob.class.getName());
	
	private Gson gson = new Gson();
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap data = context.getJobDetail().getJobDataMap();
		PrintWriter printWriter = (PrintWriter) data.get("out");
		HealthCheckMessage message = new HealthCheckMessage(PropertyMap.getInstance().getSeedNodes(), "127.0.0.1");
		printWriter.println(gson.toJson(message));
		LOG.info("Message Sent:"+message);
	}

}
