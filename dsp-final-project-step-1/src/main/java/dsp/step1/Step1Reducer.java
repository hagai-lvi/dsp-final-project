package dsp.step1;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class Step1Reducer extends Reducer<Text, Text, Text, Text> {

	final static Logger logger = Logger.getLogger(Step1Reducer.class);

	private AtomicLong abstractPathCounter = new AtomicLong(0L);
	private int MIN_OCCURENCES;

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		int errorValue = -1;
		MIN_OCCURENCES = context.getConfiguration().getInt(Step1Main.MIN_OCCURENCES, errorValue);
		if (MIN_OCCURENCES == errorValue) {
			String message = "Didn't get the MIN_OCCURENCES. Please set the MIN_OCCURENCES variable";
			logger.error(message);
			throw new RuntimeException(message);
		}
		super.setup(context);

	}

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		Set<String> s = new HashSet<>();

		if (key.toString().startsWith(Step1Mapper.ABSTRACT_PATH_PREFIX)) {
			for (Text value : values) {
				s.add(value.toString());
				if (s.size() >= MIN_OCCURENCES) {
					context.write(key, new Text(Long.toString(abstractPathCounter.getAndIncrement())));
					return;
				}
			}
			return;
		}

		long count = 0;
		for (Text value : values) {
			count += Long.parseLong(value.toString());
		}
		context.write(key, new Text(Long.toString(count)));
	}
}
