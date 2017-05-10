package com.fileupload.main;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.fileupload.healthcheck.client.HealthCheckService;
import com.fileupload.healthcheck.server.ServerMainThread;
import com.fileupload.jobs.PropertyLoader;
import com.fileupload.model.DownloadProtocol;
import com.fileupload.model.FileMetadata;
import com.fileupload.service.DownloadThreads;

public class MainService {

	
	final static int NumberOfThreads = 3;
	
	private static final Logger LOG = Logger.getLogger(MainService.class.getName());
	
	public static void main(String[] args) throws IOException {

		LOG.info("File Upload Application started");
		
		JobDetail jobPropertyLoader = JobBuilder.newJob(PropertyLoader.class).build();
		Trigger triggerPropertyLoader = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?")).build();

		Scheduler schedulerPropertyLoader;
		try {
			schedulerPropertyLoader = new StdSchedulerFactory().getScheduler();
			schedulerPropertyLoader.start();
			schedulerPropertyLoader.scheduleJob(jobPropertyLoader, triggerPropertyLoader);
		} catch (SchedulerException e) {
			LOG.error(e);
		}

		
		/**
		 * The thread (Server) which just listens on a port and spawns handler threads.
		 */
		ServerMainThread task = new ServerMainThread();
		Thread serverThread = new Thread(task);
		serverThread.setName("serverThread");
		serverThread.start();
		

		JobDetail jobHealthCheck = JobBuilder.newJob(HealthCheckService.class).build();
		Trigger triggerHealthCheck = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?")).build();

		Scheduler schedulerHealthCheck;
		try {
			schedulerHealthCheck = new StdSchedulerFactory().getScheduler();
			schedulerHealthCheck.start();
			schedulerHealthCheck.scheduleJob(jobHealthCheck, triggerHealthCheck);
		} catch (SchedulerException e) {
			LOG.error(e);
		}
		
		LOG.info("File Upload Application Ended");
		
		//		BufferedReader br;
		//		try {
		//			br = new BufferedReader(new FileReader("configuration.txt"));
		//		} catch (FileNotFoundException e1) {
		//			System.out.println("Configuration file not found, Please read the readme.txt file for details " + e1.getMessage());
		//			// TODO Auto-generated catch block
		//			return;
		//		}
		//
		//		String directory = br.readLine();
		//		File dirFile = new File(directory);
		//		if (!dirFile.exists() || !dirFile.isDirectory()) {
		//			System.out.println("Invalid directory given. Exiting from the program");
		//			br.close();
		//			return;
		//
		//		}
		//		Set<String> urls = new HashSet<String>();
		//		String line;
		//		try {
		//			while ((line = br.readLine()) != null) {
		//				urls.add(line.trim());
		//			}
		//		} catch (IOException e1) {
		//			// TODO Auto-generated catch block
		//			System.out.println("Error while reading data from file configuration.txt: " + e1.getMessage());
		//		}
		//		br.close();
		//		if (urls.isEmpty()) {
		//			System.out.print("No Url given. Exiting");
		//			return;
		//		}
		//		
		//		downloadStart(urls, directory);
		//		
		//		System.out.println("Thanks for using our downloader.");

	}

	/*
	 * A LinkedBlockingQueue is used which will have all the urls to be downloaded.
	 * 
	 * NumberOfThreads property defines the number of threads which are started. Each thread will have this queue.
	 * Each thread will poll a file from queue.
	 * 
	 * If it fails it is pushed again in downloadQueue after incrementing the retryCount.
	 * The failed file will be picked again after all remaining files are tried once. (FIFO)
	 * Hence a corrupt url will not block other downloads.
	 * In every retry partial corrupted file saved in directory is deleted.
	 * 
	 */
	public static void downloadStart(Set<String> allUrls,String directory){

		LinkedBlockingQueue<FileMetadata> downloadQueue = new LinkedBlockingQueue<FileMetadata>();

		addUrlsToQueue(downloadQueue,allUrls,directory);

		submitTasks(downloadQueue);

	}

	/*
	 * All urls are added in LinkedBlockingQueue. 
	 * Set is used to avoid duplicate urls.
	 * Urls having protocol other than specified in DownloadProtocol enum are discarded.
	 * 
	 */
	private static void addUrlsToQueue(LinkedBlockingQueue<FileMetadata> downloadQueue,Set<String> allUrls,String directory){
		Iterator<String> urlIterator = allUrls.iterator();

		while(urlIterator.hasNext()){
			String url = urlIterator.next();
			int protocolIndex = url.indexOf(":");
			try {
				String protocolType = url.substring(0, protocolIndex);
				DownloadProtocol protocol = DownloadProtocol.valueOf(protocolType);
				downloadQueue.put(new FileMetadata(url,0,protocol,directory));
			}
			catch(IllegalArgumentException iae){
				System.err.println(url+" : protocol not supported yet.");
			}
			catch(IndexOutOfBoundsException npe){
				System.err.println("Invalid url. Protocol not present");
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}

	private static void submitTasks(LinkedBlockingQueue<FileMetadata> downloadQueue){
		Thread[] threadPool = new Thread[NumberOfThreads];
		for(int i=0;i<NumberOfThreads;i++){
			threadPool[i] = new Thread(new DownloadThreads(downloadQueue));
		}
		for(int i=0;i<NumberOfThreads;i++){
			threadPool[i].start();
		}
		for(int i=0;i<NumberOfThreads;i++){
			try {
				threadPool[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
