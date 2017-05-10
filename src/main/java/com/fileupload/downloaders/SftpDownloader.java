package com.fileupload.downloaders;

import java.io.*;
/*
 * Created by divya on 22/4/17.
 */
public class SftpDownloader implements IGenericDownloader {

	static SftpDownloader instance = new SftpDownloader();
	
    private  SftpDownloader(){
    	
    }
    public static SftpDownloader getInstance(){
		return instance;
	}
    
/*
 * (non-Javadoc)
 * @see com.downloaders.IGenericDownloader#downloadFile()
 * 
 * Here downloadFile can be implemented for sftp protocol.
 * It must return false if file is failed.
 */
    
    public boolean downloadFile(String fileUrl, String saveFilePath) {
    	System.out.println("SFTP protocol is not supported yet. Url: "+fileUrl);
    	return false;
    }

}
