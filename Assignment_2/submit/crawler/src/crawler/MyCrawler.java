package crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import model.DiscoverURLData;
import model.FetchData;
import model.SuccessfulDownloadData;

public class MyCrawler extends WebCrawler {
	private final List<FetchData> fetchDataList = new ArrayList<>();
	private final List<SuccessfulDownloadData> successfulDownloadDataList = new ArrayList<>();
	private final List<DiscoverURLData> discoverURLDataList = new ArrayList<>();
	
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(html|doc|pdf|gif|jpg"
			+ "|png))$");
	
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		return FILTERS.matcher(href).matches()
				&& href.startsWith(Controller.CRAWL_SITE);
	}
	
	@Override
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
		System.out.println(statusDescription);
		fetchDataList.add(new FetchData(webUrl.getURL(),statusCode));
	}
	
	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		
		int numBytes = page.getContentData().length;
		int numOutlinks = page.getParseData().getOutgoingUrls().size();
		String contentType = page.getContentType();
		
		successfulDownloadDataList.add(new SuccessfulDownloadData(url, numBytes, numOutlinks, contentType));
		
		for(WebURL webUrl: page.getParseData().getOutgoingUrls()){
			String encounterUrl = webUrl.getURL();
			String isOk = webUrl.getURL().startsWith(Controller.CRAWL_SITE)? "OK":"N_OK";
			discoverURLDataList.add(new DiscoverURLData(encounterUrl, isOk));
		}
	}
	
	@Override
	public MyCrawler getMyLocalData() {
		return this;
	}

	public List<FetchData> getFetchDataList() {
		return fetchDataList;
	}

	public List<SuccessfulDownloadData> getSuccessfulDownloadDataList() {
		return successfulDownloadDataList;
	}

	public List<DiscoverURLData> getDiscoverURLDataList() {
		return discoverURLDataList;
	}
}
