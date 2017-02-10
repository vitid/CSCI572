package crawler;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import model.DiscoverURLData;
import model.FetchData;
import model.SuccessfulDownloadData;

public class MyCrawler extends WebCrawler {
	private static Logger logger = LoggerFactory.getLogger(MyCrawler.class);
			
	private final List<FetchData> fetchDataList = new ArrayList<>();
	private final List<SuccessfulDownloadData> successfulDownloadDataList = new ArrayList<>();
	private final List<DiscoverURLData> discoverURLDataList = new ArrayList<>();
	
	private final static Pattern PATTERN_ENDED_EMPTY = Pattern.compile("(^$|.*\\/[^(\\/\\.)]*$)");
	private final static Pattern PATTERN_ALLOWED_SIGNATURE = Pattern.compile(".*(\\.(html|doc|pdf|gif|jpg"
			+ "|png|bmp))$");
	
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {		
		String href = url.getURL().toLowerCase();
		URL checkUrl;
		
		try{
			checkUrl = new URL(href);
		}catch(Exception e){
			logger.error("Can't process URL:" + href,e);
			return false;
		}
		
		if(!Controller.CRAWL_SITE.equals(checkUrl.getHost())){
			return false;
		}
		
		String checkPath = url.getPath();
		
		if(PATTERN_ENDED_EMPTY.matcher(checkPath).matches()){
			return true;
		}
		return PATTERN_ALLOWED_SIGNATURE.matcher(checkPath).matches();	
	}
	
	@Override
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
		fetchDataList.add(new FetchData(webUrl.getURL(),statusCode));
	}
	
	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		
		int numBytes = page.getContentData().length;
		int numOutlinks = page.getParseData().getOutgoingUrls().size();
		String contentType = page.getContentType();
		contentType = contentType.toLowerCase().indexOf("text/html") > -1 ? "text/html":contentType;
		
		successfulDownloadDataList.add(new SuccessfulDownloadData(url, numBytes, numOutlinks, contentType));
		
		for(WebURL webUrl: page.getParseData().getOutgoingUrls()){
			String encounterUrl = webUrl.getURL().toLowerCase();
			
			boolean isOk = true;
			try{
				URL checkUrl = new URL(encounterUrl);
				isOk = Controller.CRAWL_SITE.equals(checkUrl.getHost());
			}catch(Exception e){
				logger.error("Can't process discovered URL:" + encounterUrl,e);
				isOk = encounterUrl.startsWith("http://" + Controller.CRAWL_SITE) 
						|| encounterUrl.startsWith("https://" + Controller.CRAWL_SITE);
			}
			
			discoverURLDataList.add(new DiscoverURLData(encounterUrl, isOk?"OK":"N_OK"));
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
