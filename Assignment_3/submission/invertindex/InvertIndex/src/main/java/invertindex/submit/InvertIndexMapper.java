package invertindex.submit;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class InvertIndexMapper extends Mapper<Text, Text, Text, Text>{
	
	private Text word = new Text();

	@Override
	public void map(Text key, Text value,Context context) throws IOException, InterruptedException{
		FileSplit fileSplit = (FileSplit)context.getInputSplit();
		String filename = fileSplit.getPath().getName();
		try{
			String line = value.toString();
			
			StringTokenizer tokenizer = new StringTokenizer(line);
			
			Text docId = new Text();
			docId.set(tokenizer.nextToken());
			
			while(tokenizer.hasMoreTokens()){
				
				word.set(tokenizer.nextToken());
				
				context.write(word, docId);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new InterruptedException("Can't process file: " + filename);
		}
	}

}
