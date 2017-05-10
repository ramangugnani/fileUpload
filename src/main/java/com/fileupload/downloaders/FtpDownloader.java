package com.fileupload.downloaders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by divya on 22/4/17.
 */
public class FtpDownloader implements IGenericDownloader {


	static FtpDownloader instance = new FtpDownloader();
	
    private  FtpDownloader(){
    	
    }
    public static FtpDownloader getInstance(){
		return instance;
	}
    
/*
 * (non-Javadoc)
 * @see com.downloaders.IGenericDownloader#downloadFile()
 * 
 * Here downloadFile can be implemented for ftp protocol.
 * It must return false if file is failed.
 */
    
    public boolean downloadFile(String fileUrl, String saveFilePath) {
    	System.out.println("FTP protocol is not supported yet. Url: "+fileUrl);
    	return false;
    }

}
