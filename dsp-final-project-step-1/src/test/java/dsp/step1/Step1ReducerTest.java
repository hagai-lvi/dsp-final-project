package dsp.step1;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by hagai_lvi on 23/07/2016.
 */
public class Step1ReducerTest {
	private ReduceDriver<Text, Text, Text, Text> reduceDriver;

	@Before
	public void setUp() {
		Reducer<Text, Text, Text, Text> reducer = new Step1Reducer();
		reduceDriver = ReduceDriver.newReduceDriver(reducer);
	}

	@Test
	public void reduce() {
		int ll1 = 10;
		Text l1 = new Text(Long.toString(ll1));
		int ll2 = 12;
		Text l2 = new Text(Long.toString(ll2));
		int ll3 = 941;
		Text l3 = new Text(Long.toString(ll3));

		long sum = ll1+ll2+ll3;

		List<Text> values = Arrays.asList(l1, l2, l3);
		Text key = new Text("silver/NN/conj/3 gold/NN/pobj/2");
		reduceDriver.withInput(key, values);

		reduceDriver.withOutput(key, new Text(Long.toString(sum)));
	}


	@Test
	public void reduceFilterPaths() throws Exception {

		// texts2 is the only list of abstract paths that have more than 2 wordpairs.
		// so it is the only one we expect to see.
		reduceDriver.getConfiguration().setInt(Step1Main.MIN_OCCURENCES, 2);

		List<Text> texts1 = Collections.singletonList(new Text("23"));
		List<Text> texts2 = Arrays.asList(new Text("fff ddd"), new Text("xxx yyy"));
		List<Text> texts3 = Collections.singletonList(new Text("23"));
		List<Text> texts4 = Collections.singletonList(new Text("23"));
		List<Text> texts5 = Arrays.asList(new Text("aaa bbb"), new Text("aaa bbb"));

		reduceDriver.withInput(new Text(Step1Mapper.STEP_1_PREFIX + "ccc/NN pobj bbb/IN prep aaa/NN"), texts1);
		reduceDriver.withInput(new Text(Step1Mapper.ABSTRACT_PATH_PREFIX + "pobj bbb/IN prep"), texts2);
		reduceDriver.withInput(new Text(Step1Mapper.STEP_1_PREFIX + "fff/NN pobj bbb/IN prep ddd/NN"), texts3);
		reduceDriver.withInput(new Text(Step1Mapper.STEP_1_PREFIX + "ccc/NN pobj ddd/IN prep aaa/NN"),texts4);
		reduceDriver.withInput(new Text(Step1Mapper.ABSTRACT_PATH_PREFIX + "pobj ddd/IN prep"), texts5);

		reduceDriver.withOutput(new Text("tree ccc/NN pobj bbb/IN prep aaa/NN"), new Text("23"));
		reduceDriver.withOutput(new Text("abstract-path pobj bbb/IN prep"), new Text("0"));
		reduceDriver.withOutput(new Text("tree fff/NN pobj bbb/IN prep ddd/NN"), new Text("23"));
		reduceDriver.withOutput(new Text("tree ccc/NN pobj ddd/IN prep aaa/NN"), new Text("23"));

		reduceDriver.runTest();

	}

}