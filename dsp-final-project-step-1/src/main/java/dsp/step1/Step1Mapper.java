package dsp.step1;

import dsp.Node;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Step1Mapper extends Mapper<Object, Text, Text, Text> {

	final static Logger logger = Logger.getLogger(Step1Mapper.class);
	public static final String STEP_1_PREFIX = "tree "; // Help step 3 separate between step 1 and step 2 inputs
	public static final String ABSTRACT_PATH_PREFIX = "abstract-path ";
	private int MIN_OCCURENCES;

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		int errorValue = -1;
		MIN_OCCURENCES = context.getConfiguration().getInt(Step1Main.MIN_OCCURENCES, errorValue);
		if (MIN_OCCURENCES == errorValue) {
			String message = "Didn't get the MIN_OCCURENCES. Please set the MIN_OCCURENCES variable";
			logger.error(message);
			throw new RuntimeException(message);
		}
		super.setup(context);
	}

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
		logger.debug("tree is: " + tree);
		logger.debug("count is: " + count);


		Node[] nodes;
		try {
			nodes = getNodes(tree);
		}
		catch (RuntimeException e) {
			logger.error(e);
			return;
		}
		try {
			for (int i = 0; i < nodes.length ; i++){
				for (int j = i+1 ; j < nodes.length ; j++){
					if (nodes[i].isNoun() && nodes[j].isNoun()) {
						AddDependencyPathIfNotNull(context, findDependencyPath(nodes, i, j), count);
						AddDependencyPathIfNotNull(context, findDependencyPath(nodes, j, i), count);
					}
				}
			}
		}
		catch (RuntimeException e) {
			logger.error("Failed to iterate tree. tree is " + tree, e);
			return;
		}


	}

	private void AddDependencyPathIfNotNull(Context context, List<Node> dependencyPath, int count) throws IOException, InterruptedException {

		if (dependencyPath != null) {
			String pathRepresentation = Node.getPathRepresentation(dependencyPath);
			context.write(new Text(STEP_1_PREFIX + pathRepresentation), new Text(Integer.toString(count)));

			String abstractPath = getAbstractPathRepresentation(pathRepresentation);
			String outermostNouns = getOutermostNouns(dependencyPath);
			context.write(new Text(ABSTRACT_PATH_PREFIX + abstractPath),new Text(outermostNouns));
		}
	}

	static String getOutermostNouns(List<Node> path) {
		String word1 = path.get(0).getWord();
		String word2 = path.get(path.size() - 1).getWord();
		return String.join(" ", word1, word2);
	}


	/**
	 * Remove the first and last words in the string, i.e remove the first and last nodes from a path
	 */
	private static String getAbstractPathRepresentation(String s) {
		return s.substring(s.indexOf(" ") + 1, s.lastIndexOf(" "));
	}

	/**
	 * Find the dependency path between nodes[i] to nodes[j]
	 * @return null if no path is found
	 */
	static List<Node> findDependencyPath(Node[] nodes, int i, int j) {
		ArrayList<Node> res = new ArrayList<>();
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

	/**
	 * @throws RuntimeException if fails to parse the tree
	 */
	static Node[] getNodes(String tree) {
		try {
			int size = StringUtils.countMatches(tree, " ") + 1;

			Node[] res = new Node[size];

			for (int i = 0; i < size; i++) {
				res[i] = new Node(tree.split(" ")[i]);
			}
			return res;
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to parse tree " + tree,e);
		}
	}
}
