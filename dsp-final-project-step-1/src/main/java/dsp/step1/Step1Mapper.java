package dsp.step1;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;


public class Step1Mapper extends Mapper<Object, Text, Text, LongWritable> {

	final static Logger logger = Logger.getLogger(Step1Mapper.class);

	@Override
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		String[] split = value.toString().split("\t");
		if (split.length < 3){
			logger.warn("Got unexpected input: " + value.toString());
			return;
		}

		String tree = split[1];
		int count;
		try {
			count = Integer.parseInt(split[2]);
		}
		catch (NumberFormatException e){
			logger.warn("Got unexpected input: " + value.toString(),e);
			return;
		}
		logger.info("tree is: " + tree);
		logger.info("count is: " + count);

		Vector<String> res = new Vector<>();

		Node[] nodes = getNodes(tree);
		for (int i = 0; i < nodes.length ; i++){
			for (int j = i+1 ; j < nodes.length ; j++){
				addIfNotNull(res, findDependencyPath(nodes, i, j));
				addIfNotNull(res, findDependencyPath(nodes, j, i));
			}
		}


	}

	private void addIfNotNull(Vector<String> vec, String dependencyPath) {
		if (dependencyPath != null){
			vec.add(dependencyPath);
		}
	}

	/**
	 * Find the dependency path between nodes[i] to nodes[j]
	 * @return null if no path is found
	 */
	static String findDependencyPath(Node[] nodes, int i, int j) {
		ArrayList<String> indexes = new ArrayList();
		String res = "";
		while (i != -1) { // When i==-1 we have reached the root
			indexes.add(Integer.toString(i));
			i = nodes[i].getHeadIndex();
			if (i == j) {
				indexes.add(Integer.toString(j));
				return String.join(", ", indexes);
			}
		}
		return null;

	}

	static Node[] getNodes(String tree) {
		int size = StringUtils.countMatches(tree, " ") + 1;

		Node[] res = new Node[size];

		for (int i = 0 ; i < size ; i++) {
			res[i] = new Node(tree.split(" ")[i]);
		}
		return res;
	}
}
