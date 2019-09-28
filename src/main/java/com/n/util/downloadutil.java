package com.n.util;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class downloadutil extends  Observable implements Runnable {
	private static Logger logger = LoggerFactory.getLogger("服务器文件下载器");

	// Max size of download buffer.
	private static final int MAX_BUFFER_SIZE = 16184;

	// These are the status names.
	public static final String STATUSES[] = { "Downloading", "Paused", "Complete", "Cancelled", "Error" };

	// These are the status codes.
	public static final int DOWNLOADING = 0;
	public static final int PAUSED = 1;
	public static final int COMPLETE = 2;
	public static final int CANCELLED = 3;
	public static final int ERROR = 4;

	private URL url; // download URL
	private int size; // size of download in bytes
	private int downloaded; // number of bytes downloaded
	private int status; // current status of download
	private String sessionid;
	private String savepath;
	private String File_Id_onServer;
	private String filemodifydate_self;

	// Constructor for Download.
	public downloadutil(String _url,String _savepath, String _sessionid,String _File_Id_onServer,String _filemodifydate_self) throws MalformedURLException {
		this.url = new URL(_url);
		size = -1;
		downloaded = 0;
		status = DOWNLOADING;
		sessionid = _sessionid;
		savepath=_savepath;
		File_Id_onServer=_File_Id_onServer;
		filemodifydate_self=_filemodifydate_self;
	 
	}

	// Get this download's URL.
	public String getUrl() {
		return url.toString();
	}

	// Get this download's size.
	public int getSize() {
		return size;
	}

	// Get this download's progress.
	public float getProgress() {
		return ((float) downloaded / size) * 100;
	}

	// Get this download's status.
	public int getStatus() {
		return status;
	}

	// Pause this download.
	public void pause() {
		status = PAUSED;
		stateChanged();
	}

	// Resume this download.
	public void resume() {
		status = DOWNLOADING;
		stateChanged();
		download();
	}

	// Cancel this download.
	public void cancel() {
		status = CANCELLED;
		stateChanged();
	}

	// Mark this download as having an error.
	private void error() {
		status = ERROR;
		stateChanged();
	}

	// Start or resume downloading.
	public void download() {
		Thread thread = new Thread(this);
		thread.start();
	}



	// Download file.
	public void run() {
		RandomAccessFile file = null;
		InputStream stream = null;

		try {
			// Open connection to URL.
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// Specify what portion of file to download.
			connection.setRequestProperty("Range", "bytes=" + downloaded + "-");
			connection.setConnectTimeout(50*1000);
			connection.setReadTimeout(50*1000);
			 
			if(!cString.isnovalue(sessionid)){
				connection.setRequestProperty("Cookie", sessionid);
			}
			// Connect to server.
			connection.connect();

			// Make sure response code is in the 200 range.
			if (connection.getResponseCode() / 100 != 2) {
				error();
			}

			// Check for valid content length.
			int contentLength = connection.getContentLength();
			if (contentLength < 1) {
				error();
			}

			// Set the size for this download if it hasn't been already set.
			if (size == -1) {
				size = contentLength;
				stateChanged();
			}

			//删除以前的临时文件
			String tmpfilename=savepath+".tmp";
			File filetmp=new File(tmpfilename);
			if(filetmp.exists()) filetmp.delete();
			
			//开始写新文件.
			file = new RandomAccessFile(tmpfilename, "rw");
			file.seek(downloaded);
			System.out.println("大小共:"+size/1000+"K");
			stream = connection.getInputStream();
			while (status == DOWNLOADING) {
				// Size buffer according to how much of the file is left to download.
				byte buffer[];
				if (size - downloaded > MAX_BUFFER_SIZE) {
					buffer = new byte[MAX_BUFFER_SIZE];
				} else {
					buffer = new byte[size - downloaded];
				}

				// Read from server into buffer.
				int read = stream.read(buffer);
				if (read == -1)
					break;

				// Write buffer to file.
			
				file.write(buffer, 0, read);
				downloaded += read;
				System.out.print("\r已下载:"+downloaded/1000+"K");
			}
			System.out.println(" ");

			// Change status to complete if this point was reached because downloading has
			// finished.
			if (status == DOWNLOADING) {
				status = COMPLETE;
				if (file != null) {
					try {
						file.close();
						//将临时文件改名 
						File ftmp=new File(tmpfilename);
						File f=new File(savepath);					
						boolean brename=ftmp.renameTo(f);
						if(!brename) 
							logger.info("下载文件-更改文件名时出错!"+tmpfilename+"ID:"+File_Id_onServer);
						
						//写入文件最后修改时间属性						 
						SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
						//设置要读取的时间字符串格式
						Date dfilemodifydate_self = format.parse(filemodifydate_self);
					 	f.setLastModified(dfilemodifydate_self.getTime());
					 	 
					} catch (Exception e) {
						logger.info("下载文件-写入文件时出错!"+savepath+"ID:"+File_Id_onServer+e.getStackTrace().toString());
						e.printStackTrace();
					}
				}
				stateChanged();
			}
		} catch (Exception e) {
			logger.info("下载文件-写入文件时出错!"+savepath+"ID:"+File_Id_onServer);
			e.printStackTrace();
			error();
		} finally {
			// Close file.
			if (file != null) {
				try {
					file.close();
				} catch (Exception e) {
				}
			}

			// Close connection to server.
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {
				}
			}
		}
	}

	// Notify observers that this download's status has changed.
	private void stateChanged() {
		setChanged();
		notifyObservers();
	}



	public String getFile_Id_onServer() {
		return File_Id_onServer;
	}

	public void setFile_Id_onServer(String File_Id_onServer) {
		this.File_Id_onServer = File_Id_onServer;
	}
}
