package dsp.step1;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by hagai_lvi on 20/07/2016.
 */
public class Step1MapperTest {
	private MapDriver<Object, Text, Text, LongWritable> mapDriver;
	private Step1Mapper mapper;

	@Before
	public void setUp() {
		mapper = new Step1Mapper();
		mapDriver = MapDriver.newMapDriver(mapper);
	}

	@Test
	public void map() throws Exception {
		mapDriver.withInput(new LongWritable(1), new Text("abounded\tabounded/VBD/ccomp/0 in/IN/prep/1 large/JJ/amod/4 numbers/NNS/pobj/2\t12\t1865,2\t1975,3\t2001,1\t2004,1\t2008,5\n"));
		mapDriver.runTest();
	}

	@Test
	public void findDependencyPath() {
		Node n1 = new Node("abounded/VBD/ccomp/0");
		Node n2 = new Node("in/IN/prep/1");
		Node n3 = new Node("large/JJ/amod/4");
		Node n4 = new Node("numbers/NNS/pobj/2");
		Node[] arr = new Node[]{n1,n2,n3,n4};

		System.out.println(Step1Mapper.findDependencyPath(arr, 3, 0));
	}

}