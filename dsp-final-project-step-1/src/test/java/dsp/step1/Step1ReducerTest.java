package dsp.step1;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hagai_lvi on 23/07/2016.
 */
public class Step1ReducerTest {
	private ReduceDriver<Text, LongWritable, Text, LongWritable> reduceDriver;

	@Before
	public void setUp() {
		Reducer<Text, LongWritable, Text, LongWritable> reducer = new Step1Reducer();
		reduceDriver = ReduceDriver.newReduceDriver(reducer);
	}

	@Test
	public void reduce() {
		LongWritable l1 = new LongWritable(10);
		LongWritable l2 = new LongWritable(12);
		LongWritable l3 = new LongWritable(941);

		long sum = l1.get() + l2.get() + l3.get();

		List<LongWritable> values = Arrays.asList(l1, l2, l3);
		Text key = new Text("silver/NN/conj/3 gold/NN/pobj/2");
		reduceDriver.withInput(key, values);

		reduceDriver.withOutput(key, new LongWritable(sum));
	}


}