import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Hashtable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ExtractLinks {
	static final String BASE_DIR = "/home/vitidn/mydata/repo_git/CSCI572/Assignment_4/data/NYTimesData/NYTimesDownloadData/";
	
	public static void main(String[] args){
		ClassLoader cl = ExtractLinks.class.getClassLoader();
		File mappingFile = new File(cl.getResource("mapNYTimesDataFile.csv").getFile());
		
		HashMap<String, String> mapUrlId = new HashMap<>();
		try(
			BufferedReader br = new BufferedReader(new FileReader(mappingFile));	
		){
			String line;
			while((line = br.readLine()) != null){
				line = line.trim();
				if(line.length() == 0){
					continue;
				}
				mapUrlId.put(line.split(",")[1], line.split(",")[0]);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		StringBuffer buffer = new StringBuffer();
		File folder = new File(BASE_DIR);
		for(File file: folder.listFiles()){
			buffer.append(extractLinks(mapUrlId, file.getName()));
			buffer.append("\n");
		}
		
		try(
				Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/tmp/edgeList.txt"),"utf-8"));		
		){
			writer.write(buffer.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static StringBuffer extractLinks(HashMap<String, String>mapUrlId,String fileName){
		StringBuffer buffer = new StringBuffer();
		
		try{
			File htmlFile = new File(BASE_DIR + fileName);
			Document doc = Jsoup.parse(htmlFile,"UTF-8");
			
			Elements links = doc.select("a[href]");
			Elements medias = doc.select("[src]");
			Elements imports = doc.select("link[href]");
			Elements elements[] = new Elements[]{links,medias,imports};
			
			for(Elements es: elements){
				for(Element e: es){
					String urlLink = e.attr("abs:href");
					if(mapUrlId.containsKey(urlLink)){
						if(buffer.length() > 0){
							buffer.append("\n");
						}
						buffer.append(BASE_DIR + fileName + " " + BASE_DIR + mapUrlId.get(urlLink)); 
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return buffer;
	}
}
