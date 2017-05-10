package com.fileupload.healthcheck.message;

import java.io.Serializable;
import java.util.Date;

public class HealthCheckMessage implements Serializable{
	private static final long serialVersionUID = -6257788976673999403L;
	private final String serverIp;
	private final Long timestamp  =  new Date().getTime();
	private final String mySeedNodes;
	
	public HealthCheckMessage(String mySeedNodes,String serverIp) {
		this.mySeedNodes = mySeedNodes;
		this.serverIp = serverIp;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public String getMySeedNodes() {
		return mySeedNodes;
	}

	public String getServerIp() {
		return serverIp;
	}

	@Override
	public String toString() {
		return "HealthCheckMessage [serverIp=" + serverIp + ", timestamp=" + timestamp + ", mySeedNodes=" + mySeedNodes
				+ "]";
	}

}
