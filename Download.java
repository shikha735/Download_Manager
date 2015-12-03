import java.io.*;
import java.net.*;
import java.util.*;

// To download from a URL
class Download extends Observable implements Runnable {
	// Specifying the maximum size of the download buffer
	private static final int MAX_BUF_SIZE = 1024;
	
	// Download manager status names
	public static final String STATUS_NAMES[] = {"Downloading", "Paused", "Complete", "Cancelled", "Error"};
	
	// Status codes
	public static final int DOWNLOADING = 0;
	public static final int PAUSED = 1;
	public static final int COMPLETE = 2;
	public static final int CANCELLED = 3;
	public static final int ERROR = 4;
	
	private URL url; // download url
	private int size; // size of download 
	private int downloaded; // number of bytes downloaded 
	private int status; // current download status code
	
	// Download constructor
	public Download(/*URL url*/) {
		// Initialization of variables
		this.url = myurl;
		size = -1;
		downloaded = 0;
		status = DOWNLOADING;
		
		// start downloading 
		download();
	}
	
	// Get download URL
	public String getUrl() {
		return url.toString();
	}
	
	// Get download size
	public int getSize() {
		return size;
	}
	
	// Get download progress
	public float getProgress() {
		return ((float) downloaded / size) * 100; // Type casted to return the percentage as real no.
	}
	
	// Get download status
	public int getStatus() {
		return status;
	}
	
	// Pause the download
	public void pause() {
		status = PAUSED;
		stateChanged(); // changing the state to PAUSED; Interface ChangeListener; javax.swing.event;
	}
	
	// Resume the download
	public void resume() {
		status = DOWNLOADING;
		stateChanged();
		download();
	}
	
	// Cancel the download 
	public void cancel() {
		status = CANCELLED;
		stateChanged();
	}
	
	// Mark download as having an error
	private void error() {
		status = ERROR;
		stateChanged();
	}
	
	// Start or resume downloading
	private void download() {
		Thread thread = new Thread(this);
		thread.start();
	}
	
	// Get file name portion of URL
	private String getFileName(URL url) {
		String fileName = url.getFile();
		return fileName.substring(fileName.lastIndexOf('/') + 1); // lastIndexOf return the index of last occurence of the character
		// substring(int beginIndex, int endIndex) or (int beginIndex) returns substring 
	}
	
	// Download the file
	public void run() {
		RandomAccessFile file = null;
		InputStream stream = null;
		
		try {
			// Open URL connection so that client can communicate with the server
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			// Specify portion of the file to be downloaded
			connection.setRequestProperty("Range", "bytes" + downloaded + "-");
			
			// Establish the actual connection 
			connection.connect();
			
			// Response code should be in the range of 200 indicating that the request has succeeded
			if (connection.getResponseCode() / 100 != 2) {
				error();
			}
			
			// Check for valid content length; getContentLength returns -1 if it cannot find the length;
			int contentLength = connection.getContentLength();
			if (contentLength < 1) {
				error();
			}
			
			// Set size for the download if it has not been already set
			if (size == -1) {
				size = contentLength;
				stateChanged();
			}
			
			// Open file
			file = new RandomAccessFile(getFileName(url), "rw");
			
			// seek to end of file; seek method sets the offset of the file to index where the next read or write occurs
			file.seek(downloaded);
			
			stream = connection.getInputStream();
			while (status == DOWNLOADING) {
				// buffer declaration
				byte buffer[];
				// buffer size according to how much of the file is left to download
				if (size - downloaded > MAX_BUF_SIZE) {
					buffer = new byte[MAX_BUF_SIZE];
				} else {
					buffer = new byte[size - downloaded];
				}
				
				// Reading from the server
				// reads MAX_BUF_SIZE bytes from the input stream to buffer
				int read = stream.read(buffer);
				// read returns number of bytes actually read into buffer or -1 if it encounters EOF
				if (read == -1)
					break;
				
				// Write buffer to file
				// write method writes 'read' number of bytes to 'buffer', 0 is start offset of buffer
				file.write(buffer, 0, read);
				downloaded += read;
				stateChanged();
			}
			// Change the status if download has finished
			if (status == DOWNLOADING) {
				status = COMPLETE;
				stateChanged();
			}
		} catch (Exception e){
			error();
		} finally {
			// Close the file
			try {
				if (file != null) {
					file.close();
				}
			} catch (Exception e) {}
		}
		
		// Close connection
		if (stream != null) {
			try {
				stream.close();
			} catch (Exception e) {}
		}
	}
	
	// Notify observers of the status change
	private void stateChanged() {
		setChanged();
		// Observable class method 
		notifyObservers();
	}
}