package dsp.step1;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.Logger;

public class Step1Main {
	final static Logger logger = Logger.getLogger(Step1Main.class);

	public static final String MIN_OCCURENCES = "MIN_OCCURENCES";

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();

		if (args.length < 3) {
			String message = "Didn't get the MIN_OCCURRENCES variable";
			logger.error(message);
			throw new RuntimeException(message);
		}

		conf.setInt(MIN_OCCURENCES, Integer.parseInt(args[2]));

		Job job = Job.getInstance(conf, "step 1");
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.setJarByClass(Step1Main.class);
		job.setMapperClass(Step1Mapper.class);
//		job.setCombinerClass(Step1Reducer.class);
		job.setPartitionerClass(Step1Partitioner.class);
		job.setReducerClass(Step1Reducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
