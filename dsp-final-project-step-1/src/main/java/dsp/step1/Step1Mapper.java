package dsp.step1;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Step1Mapper extends Mapper<Object, Text, Text, Text> {

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
				if (nodes[i].isNoun() && nodes[j].isNoun()) {
					AddDependencyPathIfNotNull(context, findDependencyPath(nodes, i, j));
					AddDependencyPathIfNotNull(context, findDependencyPath(nodes, j, i));
				}
			}
		}


	}

	private void AddDependencyPathIfNotNull(Context context, List<Node> dependencyPath) throws IOException, InterruptedException {
		if (dependencyPath != null) {
			Stream<String> stream = dependencyPath.stream().map(node -> node.getStr());
			List<String> collect = stream.collect(Collectors.toList());
			context.write(new Text(""), new Text(String.join(" ", collect)));
		}
	}

	/**
	 * Find the dependency path between nodes[i] to nodes[j]
	 * @return null if no path is found
	 */
	static List<Node> findDependencyPath(Node[] nodes, int i, int j) {
		ArrayList<Node> res = new ArrayList();
		while (i != -1) { // When i==-1 we have reached the root
			res.add(nodes[i]);
			i = nodes[i].getHeadIndex();
			if (i == j) {
				res.add(nodes[j]);
				return res;
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
