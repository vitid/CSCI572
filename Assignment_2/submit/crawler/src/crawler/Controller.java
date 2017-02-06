package crawler;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import model.DiscoverURLData;
import model.FetchData;
import model.SuccessfulDownloadData;

public class Controller {
	
	private static Logger logger = LoggerFactory.getLogger(Controller.class);
	public static final String CRAWL_SITE = "https://www.nytimes.com/";
	
	public static void main(String[] args) throws Exception{
		String crawlStorageFolder = "/tmp/";
		String fetchDataFile = "fetch_NY_Times.csv";
		String successfulDownloadDataFile = "visit_NY_Times.csv";
		String discoverURLDataFile = "urls_NY_Times.csv";
		
		int numberOfCrawlers = 7;
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);

		List<Header> headerList = new ArrayList<>();
		//BasicHeader h0 = new BasicHeader("cookie", "");
		//BasicHeader h1 = new BasicHeader("path", "/2017/02/06/opinion/im-afraid-it-will-make-terrorism-worse.html");
		//BasicHeader h2 = new BasicHeader("scheme", "https");
		//headerList.add(h0);
		//headerList.add(h1);
		//headerList.add(h2);
		config.setDefaultHeaders(headerList);
		config.setMaxDepthOfCrawling(2);
		config.setMaxPagesToFetch(10);
		
		/*
		* Instantiate the controller for this crawl.
		*/
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		/*
		* For each crawl, you need to add some seed urls. These are the first
		* URLs that are fetched and then the crawler starts following links
		* which are found in these pages
		*/
		controller.addSeed(Controller.CRAWL_SITE);
		/*
		* Start the crawl. This is a blocking operation, meaning that your code
		* will reach the line after this only when crawling is finished.
		*/
		logger.info("Start crawling process...");
		controller.start(MyCrawler.class, numberOfCrawlers);
		
		List<FetchData> fetchDataList = new ArrayList<>();
		List<SuccessfulDownloadData> successfulDownloadDataList = new ArrayList<>();
		List<DiscoverURLData> discoverURLDataList = new ArrayList<>();
		
		List<Object> crawlers = controller.getCrawlersLocalData();
		for(Object crawler:crawlers){
			MyCrawler myCrawler = (MyCrawler)crawler;
			fetchDataList.addAll(myCrawler.getFetchDataList());
			successfulDownloadDataList.addAll(myCrawler.getSuccessfulDownloadDataList());
			discoverURLDataList.addAll(myCrawler.getDiscoverURLDataList());
		}
		
		CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("url","httpStatusCode");
		try(
				FileWriter fw = new FileWriter(crawlStorageFolder + fetchDataFile);
				CSVPrinter csvPrinter = new CSVPrinter(fw,csvFormat);
		){
			for(FetchData fetchData: fetchDataList){
				List<String> record = new ArrayList<>();
				record.add(fetchData.getUrl());
				record.add(fetchData.getHttpStatusCode() + "");
				csvPrinter.printRecord(record);
			}
		}catch(Exception e){
			logger.error("Can't store data to " + crawlStorageFolder + fetchDataFile, e);
		}
		
		csvFormat = CSVFormat.DEFAULT.withHeader("url","size","numOutlinks","content-type");
		try(
				FileWriter fw = new FileWriter(crawlStorageFolder + successfulDownloadDataFile);
				CSVPrinter csvPrinter = new CSVPrinter(fw,csvFormat);
		){
			for(SuccessfulDownloadData successfulDownloadData: successfulDownloadDataList){
				List<String> record = new ArrayList<>();
				record.add(successfulDownloadData.getUrl());
				record.add(successfulDownloadData.getSize() + "");
				record.add(successfulDownloadData.getNumOutlinks() + "");
				record.add(successfulDownloadData.getContentType());
				csvPrinter.printRecord(record);
			}
		}catch(Exception e){
			logger.error("Can't store data to " + crawlStorageFolder + successfulDownloadDataFile, e);
		}
		
		csvFormat = CSVFormat.DEFAULT.withHeader("url","isOk");
		try(
				FileWriter fw = new FileWriter(crawlStorageFolder + discoverURLDataFile);
				CSVPrinter csvPrinter = new CSVPrinter(fw,csvFormat);
		){
			for(DiscoverURLData discoverURLData: discoverURLDataList){
				List<String> record = new ArrayList<>();
				record.add(discoverURLData.getUrl());
				record.add(discoverURLData.getIsOK());
				csvPrinter.printRecord(record);
			}
		}catch(Exception e){
			logger.error("Can't store data to " + crawlStorageFolder + discoverURLDataFile, e);
		}
	}

}
