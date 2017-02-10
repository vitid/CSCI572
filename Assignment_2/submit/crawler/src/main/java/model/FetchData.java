package model;

public class FetchData {
	private final String url;
	private final int httpStatusCode;
	
	public FetchData(String url, int httpStatusCode) {
		super();
		this.url = url;
		this.httpStatusCode = httpStatusCode;
	}
	
	public String getUrl() {
		return url;
	}
	public int getHttpStatusCode() {
		return httpStatusCode;
	}
	
}
