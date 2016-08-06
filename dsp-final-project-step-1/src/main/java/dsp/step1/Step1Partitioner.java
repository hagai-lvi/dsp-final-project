package dsp.step1;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

/**
 * Created by hagai_lvi on 05/08/2016.
 */
public class Step1Partitioner extends Partitioner<Text,Text> {

	@Override
	public int getPartition(Text key, Text value, int numOfPartitions) {

		if (key.toString().startsWith(Step1Mapper.ABSTRACT_PATH_PREFIX)) {
			// All abstract paths must get to the same partition, so that we can give them
			// serial numbers
			return 0;
		}
		return Math.abs(key.hashCode() % numOfPartitions);
	}
}
