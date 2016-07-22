package dsp.step1;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import java.io.IOException;


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

		Node[] nodes = getNodes(tree);
		for (int i = 0; i < nodes.length ; i++){
			for (int j = i+1 ; j < nodes.length ; j++){
				findDependencyPath(nodes, i, j);
			}
		}

	}

	/**
	 * Find the dependency path between nodes[i] to nodes[j]
	 * @return null if no path is found
	 */
	static String findDependencyPath(Node[] nodes, int i, int j) {
		// TODO
		throw new UnsupportedOperationException();
	}

	private Node[] getNodes(String tree) {
		//TODO
		return new Node[0];
	}
}
