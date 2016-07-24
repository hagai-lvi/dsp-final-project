package dsp;

import dsp.step1.StemmerAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a node in the google syntactic ngram tree
 */
public class Node {
	private final String word;
	private final int headIndex;
	private final boolean isNoun;
	private final String category;
	private final String dependency;

	public String getWord() {
		return word;
	}

	public int getHeadIndex() {
		return headIndex;
	}

	public boolean isNoun() {
		return isNoun;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Node node = (Node) o;

		if (getHeadIndex() != node.getHeadIndex()) return false;
		if (isNoun() != node.isNoun()) return false;
		if (!getWord().equals(node.getWord())) return false;
		if (!category.equals(node.category)) return false;
		return dependency.equals(node.dependency);

	}

	@Override
	public int hashCode() {
		int result = getWord().hashCode();
		result = 31 * result + getHeadIndex();
		result = 31 * result + (isNoun() ? 1 : 0);
		result = 31 * result + category.hashCode();
		result = 31 * result + dependency.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return  String.join("/", this.word, this.category, this.dependency);
	}

	public String getCategory() {
		return category;
	}

	public String getDependency() {
		return dependency;
	}

	public Node(String str) {
		int lastIndexOf = str.lastIndexOf("/");
		this.headIndex = Integer.parseInt(str.substring(lastIndexOf + 1)) - 1;
		str = str.substring(0, lastIndexOf);

		lastIndexOf = str.lastIndexOf("/");
		this.dependency = str.substring(lastIndexOf + 1);
		str = str.substring(0, lastIndexOf);

		lastIndexOf = str.lastIndexOf("/");
		this.category = str.substring(lastIndexOf + 1);
		lastIndexOf = str.lastIndexOf("/");

		str = str.substring(0, lastIndexOf);
		this.word = StemmerAdapter.stem(str);

		this.isNoun = this.category.equals("NN");

	}

	/**
	 * @throws RuntimeException if path.size()<2
	 * @return a string representation of a path
	 */
	public static String getPathRepresentation(List<Node> path) {
		String res = "";
		if (path.size() < 2) {
			throw new RuntimeException("Path must be of size >= 2. Got " +
					Arrays.toString(path.toArray()));
		}

		for (int i = 0 ; i < path.size() ; i++) {
			res += path.get(i).getWord() + "/" + path.get(i).getCategory();
			if (i < path.size() - 1) {
				res += " " + path.get(i).getDependency() + " ";
			}
		}
		return res;
	}
}
