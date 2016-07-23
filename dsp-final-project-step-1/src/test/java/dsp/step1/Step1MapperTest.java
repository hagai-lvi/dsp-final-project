package dsp.step1;

import dsp.Node;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class Step1MapperTest {
	private MapDriver<Object, Text, Text, LongWritable> mapDriver;
	private Step1Mapper mapper;

	@Before
	public void setUp() {
		mapper = new Step1Mapper();
		mapDriver = MapDriver.newMapDriver(mapper);
	}

	@Test
	public void mapWithPath() throws Exception {
		mapDriver.withInput(new LongWritable(1), new Text("abounded\tabounded/VBD/ccomp/0 in/IN/prep/1 gold/NN/pobj/2 and/CC/cc/3 silver/NN/conj/3\t23\t1831,4\t1850,1\t1854,1\t1866,1\t1898,5\t1899,2\t1902,1\t1953,2\t1967,1\t1980,3\t1982,1\t2000,1\n"));
		mapDriver.withOutput(new Text("silver/NN/conj/3 gold/NN/pobj/2\tsilver/NN/conj/3 => gold/NN/pobj/2"), new LongWritable(23));
		mapDriver.runTest();
	}

	@Test
	public void mapWithoutNouns() throws Exception {
		mapDriver.withInput(new LongWritable(1), new Text("abounded\tabounded/VBD/ccomp/0 in/IN/prep/1 ideas/NNS/pobj/2 on/IN/prep/1 mysteries/NNS/pobj/4\t10\t1881,7\t1908,2\t1911,1\n"));
		mapDriver.runTest();
	}

	@Test
	public void mapWithMultipleMatches() throws Exception {
		mapDriver.withInput(new LongWritable(1), new Text("abounded\tabounded/NN/ccomp/0 in/IN/prep/1 gold/NN/pobj/2 and/CC/cc/3 silver/NN/conj/3\t23\t1831,4\t1850,1\t1854,1\t1866,1\t1898,5\t1899,2\t1902,1\t1953,2\t1967,1\t1980,3\t1982,1\t2000,1\n"));
		mapDriver.withOutput(new Text("gold/NN/pobj/2 in/IN/prep/1 abounded/NN/ccomp/0\tgold/NN/pobj/2 => abounded/NN/ccomp/0"), new LongWritable(23));
		mapDriver.withOutput(new Text("silver/NN/conj/3 gold/NN/pobj/2 in/IN/prep/1 abounded/NN/ccomp/0\tsilver/NN/conj/3 => abounded/NN/ccomp/0"), new LongWritable(23));
		mapDriver.withOutput(new Text("silver/NN/conj/3 gold/NN/pobj/2\tsilver/NN/conj/3 => gold/NN/pobj/2"), new LongWritable(23));
		mapDriver.runTest();
	}

	@Test
	public void findDependencyPath() {
		Node n0 = new Node("abounded/VBD/ccomp/0");
		Node n1 = new Node("in/IN/prep/1");
		Node n2 = new Node("large/JJ/amod/4");
		Node n3 = new Node("numbers/NNS/pobj/2");
		Node[] arr = new Node[]{n0,n1,n2,n3};
		List<Node> dependencyPath = Step1Mapper.findDependencyPath(arr, 2, 0);

		Assert.assertNotNull(dependencyPath);

		Assert.assertEquals(4, dependencyPath.size());
		Assert.assertEquals(dependencyPath.get(0), n2);
		Assert.assertEquals(dependencyPath.get(1), n3);
		Assert.assertEquals(dependencyPath.get(2), n1);
		Assert.assertEquals(dependencyPath.get(3), n0);
	}

	@Test
	public void findDependencyPathTwoNodes() {
		Node n0 = new Node("abounded/VBD/ccomp/0");
		Node n1 = new Node("in/IN/prep/1");
		Node[] arr = new Node[]{n0,n1};
		List<Node> dependencyPath = Step1Mapper.findDependencyPath(arr, 1, 0);
		Assert.assertEquals(2, dependencyPath.size());
		Assert.assertEquals(dependencyPath.get(0), n1);
		Assert.assertEquals(dependencyPath.get(1), n0);
	}

	@Test
	public void findDependencyNoPath() {
		Node n1 = new Node("abounded/VBD/ccomp/0");
		Node n2 = new Node("in/IN/prep/1");
		Node n3 = new Node("large/JJ/amod/4");
		Node n4 = new Node("numbers/NNS/pobj/2");
		Node[] arr = new Node[]{n1,n2,n3,n4};
		List<Node> dependencyPath = Step1Mapper.findDependencyPath(arr, 3, 2);
		Assert.assertEquals(null, dependencyPath);
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