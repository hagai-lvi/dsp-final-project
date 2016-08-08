package dsp.step1;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by hagai_lvi on 08/08/2016.
 */
public class Step1Combiner extends Reducer<Text, Text, Text, Text> {

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

		if (key.toString().startsWith(Step1Mapper.ABSTRACT_PATH_PREFIX)) {
			// we cannot combine this part, write it as is
			for (Text value : values) {
				context.write(key, value);
			}
		}

		long count = 0;
		for (Text value : values) {
			count += Long.parseLong(value.toString());
		}
		context.write(key, new Text(Long.toString(count)));
	}
}
