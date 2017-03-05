package invertindex.submit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class WholeFileRecordReader extends RecordReader<Text, Text>{

	private Text key = new Text();
	private Text value = new Text();
	private boolean hasNext = true;
	
	public WholeFileRecordReader() {
		
	}
	
	@Override
	public void initialize(InputSplit genericSplit, TaskAttemptContext context) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		FileSplit split = (FileSplit)genericSplit;
		Configuration job = context.getConfiguration();
		final Path file = split.getPath();
		final FileSystem fs = file.getFileSystem(job);
		
		key.set(file.getName());
		
		StringBuffer content = new StringBuffer();
		try(
			FSDataInputStream fileIn = fs.open(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fileIn));
		)
		{
			String line = null;
			while((line = reader.readLine()) != null){
				content.append(line);
				content.append("\n");
			}
		}
		
		value.set(content.toString());
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		if(hasNext){
			hasNext = false;
			return true;
		}
		key = null;
		value = null;
		return false;
	}
	
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Text getCurrentKey() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return key;
	}

	@Override
	public Text getCurrentValue() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return 0;
	}


}
