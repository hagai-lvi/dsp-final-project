package dsp.step3;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import java.io.IOException;

// Example output for step 1:
// 	youth/NN pobj of/IN prep %/NN	133

public class Step3Mapper extends Mapper<Object, Text, Text, Text> {

	final static Logger logger = Logger.getLogger(Step3Mapper.class);

	public static final String STEP_2_PREFIX = "path";
	public static final String STEP_1_PREFIX = "tree";

	// appended to trees representation to make sure that the path created in step 2 will be read by the reducer
	// before the trees
	public static final String
			TREE_POSTFIX = " +",
			PATH_POSTFIX = " *";

	// Allows us to differentiate noun pairs from other data because we write the noun pairs to a different file
	public static final String PAIR_PREFIX = "-PAIR-";

	@Override
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {


		String extractedValue = value.toString().split("\t")[0].substring(value.toString().indexOf(" ") + 1);

		// This is a tree that represents path between 2 "concrete" words
		if (value.toString().startsWith(STEP_1_PREFIX)) {
			handleTree(extractedValue, context);
		}
		else if (value.toString().startsWith(STEP_2_PREFIX)) {
			handlePath(extractedValue, context);
		}
		else {
			logger.error("Got unexpected value prefix. expected \"" + STEP_1_PREFIX + "\"" +
					" or \"" + STEP_2_PREFIX + "\", but got value \"" + value.toString() + "\"");
		}
	}

	/**
	 * Handle an "abstract" path, i.e with no actual nouns in its external nodes
	 */
	void handlePath(String path, Context context) throws IOException, InterruptedException {
		context.write(new Text(path + PATH_POSTFIX), new Text("---"));
	}

	void handleTree(String tree, Context context) throws IOException, InterruptedException {
		String path = extractPathFromTree(tree);
		String nouns = extractNounsFromTree(tree);

		// Write the pair of words so that the regression model will be aware of all the words that
		// we have seen, even if it didn't match any "pattern"
		writeWordPair(nouns, context);

		// Use TREE_POSTFIX to make sure that trees are getting to the reducer after the corresponding path
		context.write(new Text(path + TREE_POSTFIX), new Text(nouns));
	}

	private void writeWordPair(String nouns, Context context) throws IOException, InterruptedException {
		context.write(new Text(PAIR_PREFIX + nouns),new Text());
	}

	/**
	 * Extract the 2 outermost nodes from a tree
	 */
	String extractNounsFromTree(String tree) {
		String n1Node = tree.substring(0, tree.indexOf(" "));
		String n1 = extractWordFromNode(n1Node);

		String n2Node = tree.substring(tree.lastIndexOf(" ") + 1);
		String n2 = extractWordFromNode(n2Node);

		return String.join(" ", n1, n2);
	}

	String extractWordFromNode(String n1Node) {
		return n1Node.substring(
				0,
				n1Node.lastIndexOf('/')
		);
	}

	String extractPathFromTree(String tree) {
		return tree.substring(
				tree.indexOf(" ") + 1,
				tree.lastIndexOf(" ")
		);
	}
}
