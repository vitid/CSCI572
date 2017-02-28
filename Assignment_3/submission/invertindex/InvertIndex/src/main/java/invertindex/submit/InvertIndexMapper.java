package invertindex.submit;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class InvertIndexMapper extends Mapper<LongWritable, Text, Text, Text>{
	
	private Text word = new Text();

	@Override
	public void map(LongWritable key, Text value,Context context) throws IOException, InterruptedException{
		String line = value.toString();
		StringTokenizer tokenizer = new StringTokenizer(line);
		
		Text docId = new Text();
		docId.set(tokenizer.nextToken());
		
		while(tokenizer.hasMoreTokens()){
			
			word.set(tokenizer.nextToken());
			
			context.write(word, docId);
		}
	}

}
