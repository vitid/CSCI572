package invertindex.submit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class InvertIndexReducer extends Reducer<Text, Text, Text, Text>{
	@Override
	protected void reduce(Text word, Iterable<Text> docIds,
			Context context) throws IOException, InterruptedException {
		
		HashMap<String, Integer> hashDocIdCount = new HashMap<String, Integer>();
		for(Text docId: docIds){
			String docIdStr = docId.toString();
			int count = hashDocIdCount.containsKey(docIdStr)? (hashDocIdCount.get(docIdStr)+1) : 1;
			
			hashDocIdCount.put(docIdStr, count);
		}
		
		StringBuffer listStr = new StringBuffer();
		Iterator<Entry<String, Integer>> iterator = hashDocIdCount.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String,Integer> entry = iterator.next();
			if(listStr.length() > 0){
				listStr.append("\t");
			}
			
			listStr.append(entry.getKey() + ":" + entry.getValue());
		}
		
		Text documentList = new Text(listStr.toString());
		context.write(word, documentList);
	}
}
