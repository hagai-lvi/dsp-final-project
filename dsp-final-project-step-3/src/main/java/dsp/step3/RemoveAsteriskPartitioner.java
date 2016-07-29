package dsp.step3;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

/**
 * This partitioner allows us to first remove the asterisk from a string and only then choose a partition
 *
 * Created by hagai_lvi on 29/07/2016.
 */
public class RemoveAsteriskPartitioner extends Partitioner<Text,Text>{

	@Override
	public int getPartition(Text key, Text value, int numOfPartitions) {
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> e.printStackTrace(System.err));

		String s = key.toString();
		if (s.endsWith(" *")) {
			s = s.substring(0, s.length() - 2);
		}
		return Math.abs(s.hashCode() % numOfPartitions);
	}


}
