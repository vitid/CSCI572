package model;

public class SuccessfulDownloadData {
	private final String url;
	private final long size;
	private final int numOutlinks;
	private final String contentType;
	public SuccessfulDownloadData(String url, long size, int numOutlinks, String contentType) {
		super();
		this.url = url;
		this.size = size;
		this.numOutlinks = numOutlinks;
		this.contentType = contentType;
	}
	public String getUrl() {
		return url;
	}
	public long getSize() {
		return size;
	}
	public int getNumOutlinks() {
		return numOutlinks;
	}
	public String getContentType() {
		return contentType;
	}
	
}
