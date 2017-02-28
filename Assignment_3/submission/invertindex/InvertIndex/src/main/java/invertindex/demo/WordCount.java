package invertindex.demo;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount {
	public static void main(String []args) throws
	IOException, ClassNotFoundException, InterruptedException{
		if(args.length != 2){
			System.err.println("Usage: Word Count <input_path> <output_path>");
			System.exit(-1);
		}
		
		Job job = new Job();
		job.setJarByClass(WordCount.class);
		job.setJobName("Word Count");
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		job.setMapperClass(WordCountMapper.class);
		job.setReducerClass(WordCountReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.waitForCompletion(true);
	}
}
