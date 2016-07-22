package dsp.step1;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Assert;
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
		Assert.assertEquals("2, 3, 1, 0", Step1Mapper.findDependencyPath(arr, 2, 0));
	}

	@Test
	public void findDependencyPathTwoNodes() {
		Node n1 = new Node("abounded/VBD/ccomp/0");
		Node n2 = new Node("in/IN/prep/1");
		Node[] arr = new Node[]{n1,n2};
		Assert.assertEquals("1, 0", Step1Mapper.findDependencyPath(arr, 1, 0));
	}

	@Test
	public void findDependencyNoPath() {
		Node n1 = new Node("abounded/VBD/ccomp/0");
		Node n2 = new Node("in/IN/prep/1");
		Node n3 = new Node("large/JJ/amod/4");
		Node n4 = new Node("numbers/NNS/pobj/2");
		Node[] arr = new Node[]{n1,n2,n3,n4};
		Assert.assertEquals(null, Step1Mapper.findDependencyPath(arr, 3, 2));
	}

	@Test
	public void getNodes() {
		String[] tree = new String[]{"abounded/VBD/ccomp/0","in/IN/prep/1","large/JJ/amod/4","numbers/NNS/pobj/2"};
		Node[] nodes = Step1Mapper.getNodes(String.join(" ", tree));
		Assert.assertEquals(4, nodes.length);
		for (int i = 0 ; i < tree.length ; i++) {
			Assert.assertEquals(new Node(tree[i]), nodes[i]);
		}
	}

}