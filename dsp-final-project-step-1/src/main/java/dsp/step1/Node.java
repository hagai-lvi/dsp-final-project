package dsp.step1;

/**
 * Represents a node in the google syntactic ngram tree
 */
public class Node {
	private final String word;
	private final int headIndex;
	private final boolean isNoun;
	private final String category;
	private final String str;
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
		return getWord().equals(node.getWord());

	}

	@Override
	public int hashCode() {
		int result = getWord().hashCode();
		result = 31 * result + getHeadIndex();
		result = 31 * result + (isNoun() ? 1 : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Node{" +
				"word='" + word + '\'' +
				", headIndex=" + headIndex +
				", isNoun=" + isNoun +
				'}';
	}

	public String getStr() {
		return str;
	}

	public Node(String str) {
		this.word = str.split("/")[0];
		this.headIndex = Integer.parseInt(str.split("/")[3]) - 1;
		this.category = str.split("/")[1];
		this.isNoun = this.category.equals("NN");
		this.str = str;
	}
}
