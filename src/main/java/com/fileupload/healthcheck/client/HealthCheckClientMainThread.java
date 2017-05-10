package com.fileupload.healthcheck.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.fileupload.healthcheck.message.HealthCheckMessage;
import com.fileupload.healthcheck.server.ServerMainThread;
import com.google.gson.Gson;

public class HealthCheckClientMainThread extends Thread{

	private BufferedReader in;
	private PrintWriter out;
	private String serverAddress;
	private Integer port;
	private Gson gson;
	
	private static final Logger LOG = Logger.getLogger(HealthCheckClientMainThread.class.getName());
	
	public HealthCheckClientMainThread(String serverAdress,Integer port) {
		this.serverAddress = serverAdress;
		this.port = port;
	}

	public void run() {
		// Make connection and initialize streams
		Socket socket = null;
		
		ServerMainThread task = new ServerMainThread();
		Thread serverThread = new Thread(task);
		serverThread.setName("healthCheckClientMainThread");
		serverThread.start();
		

		JobDetail healthCheckSendMessageJob = JobBuilder.newJob(HealthCheckSendMessageJob.class).build();
		Trigger triggerHealthCheck = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?")).build();
		
		
		try{
			socket = new Socket(serverAddress, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			gson = new Gson();

			Scheduler schedulerHealthCheck;
			try {
				schedulerHealthCheck = new StdSchedulerFactory().getScheduler();
				schedulerHealthCheck.start();
				healthCheckSendMessageJob.getJobDataMap().put("out", out);
				schedulerHealthCheck.scheduleJob(healthCheckSendMessageJob, triggerHealthCheck);
			} catch (SchedulerException e) {
				LOG.error(e);
			}
			
			// Process all messages from server, according to the protocol.
			while (true) {
				String mssg = in.readLine();
				if (mssg == null) {
					continue;
				}
				HealthCheckMessage healthCheckMessage = gson.fromJson(mssg, HealthCheckMessage.class);
				LOG.info("Message Received "+ healthCheckMessage);
				out.println(getName());
			}
		}catch (IOException e) {
			System.out.println(e);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
}
