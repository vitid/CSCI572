package invertindex.submit;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class InvertIndex {
	public static void main(String []args) throws
	IOException, ClassNotFoundException, InterruptedException{
		if(args.length != 2){
			System.err.println("Usage: InvertIndex <input_path> <output_path>");
			System.exit(-1);
		}
		
		Job job = new Job();
		job.setInputFormatClass(WholeFileInputFormat.class);
		job.setJarByClass(InvertIndex.class);
		job.setJobName("InvertIndex");
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		job.setMapperClass(InvertIndexMapper.class);
		job.setReducerClass(InvertIndexReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.waitForCompletion(true);
	}
}
