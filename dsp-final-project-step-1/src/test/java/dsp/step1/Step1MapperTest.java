package dsp.step1;

import dsp.Node;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class Step1MapperTest {
	private MapDriver<Object, Text, Text, Text> mapDriver;
	private Step1Mapper mapper;

	@Before
	public void setUp() {
		mapper = new Step1Mapper();
		mapDriver = MapDriver.newMapDriver(mapper);
		mapDriver.getConfiguration().setInt(Step1Main.MIN_OCCURENCES, 1);
	}

	@Test
	public void mapWithPath() throws Exception {
		String text = "abounded\tabounded/VBD/ccomp/0 in/IN/prep/1 gold/NN/pobj/2 and/CC/cc/3 silver/NN/conj/3\t23\t1831,4\t1850,1\t1854,1\t1866,1\t1898,5\t1899,2\t1902,1\t1953,2\t1967,1\t1980,3\t1982,1\t2000,1\n";
		mapDriver.withInput(new LongWritable(1), new Text(text));
		mapDriver.withOutput(new Text(Step1Mapper.STEP_1_PREFIX + "silver/NN conj gold/NN"), new Text(Long.toString(23)));
		mapDriver.withOutput(new Text(Step1Mapper.ABSTRACT_PATH_PREFIX + "conj"), new Text("silver gold"));
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
		mapDriver.withOutput(new Text(Step1Mapper.STEP_1_PREFIX + "gold/NN pobj in/IN prep abound/NN"), new Text(Long.toString(23)));
		mapDriver.withOutput(new Text(Step1Mapper.ABSTRACT_PATH_PREFIX + "pobj in/IN prep"), new Text("gold abound"));
		mapDriver.withOutput(new Text(Step1Mapper.STEP_1_PREFIX + "silver/NN conj gold/NN pobj in/IN prep abound/NN"), new Text(Long.toString(23)));
		mapDriver.withOutput(new Text(Step1Mapper.ABSTRACT_PATH_PREFIX + "conj gold/NN pobj in/IN prep"), new Text("silver abound"));
		mapDriver.withOutput(new Text(Step1Mapper.STEP_1_PREFIX + "silver/NN conj gold/NN"), new Text(Long.toString((23))));
		mapDriver.withOutput(new Text(Step1Mapper.ABSTRACT_PATH_PREFIX + "conj"), new Text("silver gold"));
		mapDriver.runTest();
	}


	@Test
	public void mapFilterPaths() throws Exception {
		// s1 and s2 have the same pattern, so expect to see this pattern in the output.
		// s3 has another patterm don't expect it in the output
		mapDriver.getConfiguration().setInt(Step1Main.MIN_OCCURENCES, 2);

		String s1 = "aaa\taaa/NN/ccomp/0 bbb/IN/prep/1 ccc/NN/pobj/2\t23\t1831,4\t1850,1\t1854,1\t1866,1\t1898,5\t1899,2\t1902,1\t1953,2\t1967,1\t1980,3\t1982,1\t2000,1\n";
		mapDriver.withInput(new LongWritable(1), new Text(s1));

		String s2 = "ddd\tddd/NN/ccomp/0 bbb/IN/prep/1 fff/NN/pobj/2\t23\t1831,4\t1850,1\t1854,1\t1866,1\t1898,5\t1899,2\t1902,1\t1953,2\t1967,1\t1980,3\t1982,1\t2000,1\n";
		mapDriver.withInput(new LongWritable(1), new Text(s2));

		String s3 = "aaa\taaa/NN/ccomp/0 ddd/IN/prep/1 ccc/NN/pobj/2\t23\t1831,4\t1850,1\t1854,1\t1866,1\t1898,5\t1899,2\t1902,1\t1953,2\t1967,1\t1980,3\t1982,1\t2000,1\n";
		mapDriver.withInput(new LongWritable(1), new Text(s3));


		mapDriver.withOutput(new Text(Step1Mapper.STEP_1_PREFIX + "ccc/NN pobj bbb/IN prep aaa/NN"), new Text("23"));
		mapDriver.withOutput(new Text(Step1Mapper.ABSTRACT_PATH_PREFIX + "pobj bbb/IN prep"), new Text("ccc aaa"));
		mapDriver.withOutput(new Text(Step1Mapper.STEP_1_PREFIX + "fff/NN pobj bbb/IN prep ddd/NN"), new Text("23"));
		mapDriver.withOutput(new Text(Step1Mapper.ABSTRACT_PATH_PREFIX + "pobj bbb/IN prep"), new Text("fff ddd"));
		mapDriver.withOutput(new Text(Step1Mapper.STEP_1_PREFIX + "ccc/NN pobj ddd/IN prep aaa/NN"), new Text("23"));
		mapDriver.withOutput(new Text(Step1Mapper.ABSTRACT_PATH_PREFIX + "pobj ddd/IN prep"), new Text("ccc aaa"));

		mapDriver.runTest();

	}

	@Test
	public void getOutermostNouns(){
		List<Node> path = new LinkedList<>();
		path.add(new Node("gold/NN/pobj/2"));
		path.add(new Node("in/IN/prep/1"));
		path.add(new Node("abounded/NN/ccomp/0"));
		Assert.assertEquals("gold abound", Step1Mapper.getOutermostNouns(path));
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