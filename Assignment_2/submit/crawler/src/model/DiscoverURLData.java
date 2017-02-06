package model;

public class DiscoverURLData {
	private final String url;
	private final String isOK;
	public DiscoverURLData(String url, String isOK) {
		super();
		this.url = url;
		this.isOK = isOK;
	}
	public String getUrl() {
		return url;
	}
	public String getIsOK() {
		return isOK;
	}
	
}
