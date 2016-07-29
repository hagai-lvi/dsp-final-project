package dsp.step3;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by hagai_lvi on 29/07/2016.
 */
public class Step3MapReduceTest {

	private MapReduceDriver<Object, Text, Text, Text, Text, Text> mapReduceDriver;

	@Before
	public void setUp() {
		Step3Mapper mapper = new Step3Mapper();

		Reducer<Text, Text, Text, Text> reducer = new Step3Reducer();

		mapReduceDriver = MapReduceDriver.newMapReduceDriver(mapper, reducer);
	}

	@Test
	public void test() throws IOException {
		mapReduceDriver.withInput(new Text(), new Text("tree youth/NN pobj of/IN prep %/NN\t133"));

		// A tree with a path that wasn't seen anywhere, expect it not to be in the output
		mapReduceDriver.withInput(new Text(), new Text("tree youth/NN pobj xxxxxxxxxxxxxx/IN prep %/NN\t133"));

		mapReduceDriver.withInput(new Text(), new Text("path pobj of/IN prep"));
		mapReduceDriver.withOutput(new Text("youth %"), new Text("pobj of/IN prep"));
		mapReduceDriver.runTest();
	}
}
