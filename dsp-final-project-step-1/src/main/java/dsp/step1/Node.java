package dsp.step1;

/**
 * Represents a node in the google syntactic ngram tree
 */
public class Node {
	private final String word;
	private final int headIndex;
	private final boolean isNoun;

	public String getWord() {
		return word;
	}

	public int getHeadIndex() {
		return headIndex;
	}

	public boolean isNoun() {
		return isNoun;
	}

	public Node(String str) {
		this.word = str.split("/")[0];
		this.headIndex = Integer.parseInt(str.split("/")[3]);
		this.isNoun = str.split("/")[1].equals("NN");
	}
}
