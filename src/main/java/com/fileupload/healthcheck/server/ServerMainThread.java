package com.fileupload.healthcheck.server;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.log4j.Logger;

import com.fileupload.constants.Constants;

public class ServerMainThread implements Runnable{

	private static final Logger LOG = Logger.getLogger(ServerMainThread.class.getName());
	
	public void run() {
		LOG.info("Server Main Thread Started");
		ServerSocket listener = null;
		try {
			listener = new ServerSocket(Constants.PORT);
		} catch (IOException e) {
			LOG.error(e);
		}
		try {
			while (true) {
				new ServerThread(listener.accept()).start();
			}
		} catch (IOException e) {
			LOG.error(e);
		} finally {
			try {
				listener.close();
			} catch (IOException e) {
				LOG.error(e);
			}
			LOG.info("Server Main Thread ");
		}
	}
}
