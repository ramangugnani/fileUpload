package com.fileupload.downloaders;

import java.io.IOException;

public interface IGenericDownloader {
	
	    boolean downloadFile(String fileUrl, String saveDir);
}
