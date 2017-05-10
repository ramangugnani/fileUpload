package com.fileupload.model;

public class FileMetadata {
/*
 * url: web url from which data will be downloaded
 * retryCount: Initialized as 0. will be incremented for every failure.
 * protocol: Protocol used.
 * downloadLocation: directory in which file will be saved.
 */
	private String url;
	private int retryCount;
	private DownloadProtocol protocol;
	private String downloadLocation;
	
	public FileMetadata(String url, int retryCount,DownloadProtocol protocol,String downloadLocation) {
		this.url = url;
		this.retryCount = retryCount;
		this.protocol = protocol;
		this.downloadLocation=downloadLocation;
	}
	
	public String getUrl(){
		return url;
	}
	
	public int getRetryCount(){
		return retryCount;
	}
	
	public void incrementRetryCount(){
			retryCount++;
	}
	
	public DownloadProtocol getProtocol(){
		return protocol;
	}
	
	public String getDownloadLocation(){
		return downloadLocation;
	}

	@Override
	public String toString() {
		return "FileMetadata [url=" + url + ", retryCount=" + retryCount + ", protocol=" + protocol
				+ ", downloadLocation=" + downloadLocation + "]";
	}
	
	
}
