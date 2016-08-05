package dsp.step3;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


public class Step3Main {

	public static final String PATHS_OUTPUT_FILE = "paths";
	public static final String WORD_PAIRS_OUTPUT_FILE = "wordpairs";

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "step 3");
		job.setJarByClass(Step3Main.class);
		job.setMapperClass(Step3Mapper.class);
		job.setReducerClass(Step3Reducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setPartitionerClass(RemoveAsteriskOrPlusSignPartitioner.class);

		// Use MultipleOutputs to seperate word pairs and paths
		MultipleOutputs.addNamedOutput(job, WORD_PAIRS_OUTPUT_FILE, TextOutputFormat.class, Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, PATHS_OUTPUT_FILE, TextOutputFormat.class, Text.class, Text.class);


		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	//TODO
}
