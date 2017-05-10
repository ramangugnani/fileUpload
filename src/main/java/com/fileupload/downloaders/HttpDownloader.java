package com.fileupload.downloaders;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpDownloader implements IGenericDownloader {


	static HttpDownloader instance = new HttpDownloader();
	
	static final int BUFFER_SIZE_SMALL = 4096;
	static final int BUFFER_SIZE_MEDIUM = 1048576;
	static final int BUFFER_SIZE_LARGE = 5242880;

	private  HttpDownloader() {
		
	}
	
	public static HttpDownloader getInstance(){
		return instance;
	}
/*
 * (non-Javadoc)
 * @see com.downloaders.IGenericDownloader#downloadFile()
 * 
 * fileUrl: Url from where file is downloaded
 * saveFilePath: Absolute path where file will be saved.
 * 
 * If file is failed false is returned else true.
 * If file greater than 10 MB. Large buffer array is used i.e BUFFER_SIZE_LARGE
 * If file less than 10 MB and greater than 1 Mb. Medium buffer array is used i.e BUFFER_SIZE_MEDIUM
 * If file less than 1 MB. Small buffer array is used i.e BUFFER_SIZE_SMALL
 * 
 * For larger files, we can improve it to make multiple http connections and fetch the file in parts.
 */
	public boolean downloadFile(String fileUrl, String saveFilePath) {
		InputStream inputStream = null ;
		FileOutputStream outputStream = null;
		HttpURLConnection httpConn = null;
		try {
			URL url = new URL(fileUrl);
			httpConn = (HttpURLConnection) url.openConnection();

			System.out.println("Connecting to server.... "+fileUrl);
			int responseCode = httpConn.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				System.out.println("Connected to "+fileUrl+", starting download");

				long contentLength = httpConn.getContentLengthLong();
//				System.out.println("file of Size: " + contentLength / 1024 + " KB, i.e+ " + contentLength + " bytes will be saved to "+saveFilePath);

				byte[] buffer;
				int BUFFER_SIZE = 0;
				if (contentLength > 10485760 || contentLength == -1) {
					BUFFER_SIZE = BUFFER_SIZE_LARGE;
//					System.out.println("Using buffer large");
				} else if (contentLength >= 1048576 && contentLength <= 10485760) {
					BUFFER_SIZE = BUFFER_SIZE_MEDIUM;
//					System.out.println("Using buffer medium");
				} else {
					BUFFER_SIZE = BUFFER_SIZE_SMALL;
//					System.out.println("Using buffer small");
				}
				
				// opens input stream from the HTTP connection
				inputStream = httpConn.getInputStream();

				// opens an output stream to save into file
				outputStream = new FileOutputStream(saveFilePath);

				long bytesReadSoFar = 0;
				int bytesRead = -1;
				
				buffer = new byte[BUFFER_SIZE];
				long pre = 0;
				long curr = -1;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
					bytesReadSoFar += bytesRead;
					curr = 100l * (bytesReadSoFar) / contentLength;
					if (curr != pre && contentLength!=-1) {
						System.out.print(saveFilePath+" Download Status: ["+curr+"%]\r");
					}
					pre = curr;
				}
				outputStream.flush();
				System.out.println();
				return true;

			} else {
				System.out.println("No file to download. Server replied HTTP code: " + responseCode);
				return false;
			}
		} catch (Exception ex) {
			System.out.println("Failed for url: "+fileUrl + ": " + ex.getMessage());
			return false;
		} finally {
			try {
				if(outputStream!=null){
					outputStream.close();
				}
				if(inputStream!=null){
					inputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage() + ": " + fileUrl);
			}
			if(httpConn!=null){
				httpConn.disconnect();
			}
		}
	}
}
