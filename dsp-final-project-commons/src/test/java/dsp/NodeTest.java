package dsp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by hagai_lvi on 23/07/2016.
 */
public class NodeTest {

	private Node n1, n2, n3;
	private String n1String, n2String, n3String;
	private String n1CleanString;
	private String n2CleanString;
	private String n3CleanString;

	@Before
	public void setUp() {
		n1CleanString = "abounded/VBD/ROOT";
		n1String = n1CleanString + "/0";
		n1 = new Node(n1String);

		// Some words contain more than one word with a separating slash
		n2CleanString = "hello/world/NN/nsubj";
		n2String = n2CleanString + "/4";
		n2 = new Node(n2String);

		// word can be equals to "/"
		n3CleanString = "//NN/nsubj";
		n3String = n3CleanString + "/4";
		n3 = new Node(n3String);
	}

	@Test
	public void getWord() throws Exception {
		Assert.assertEquals("abounded", n1.getWord());
		Assert.assertEquals("hello/world", n2.getWord());
		Assert.assertEquals("/", n3.getWord());
	}

	@Test
	public void getHeadIndex() throws Exception {
		Assert.assertEquals(-1, n1.getHeadIndex());
		Assert.assertEquals(3, n2.getHeadIndex());
		Assert.assertEquals(3, n3.getHeadIndex());
	}

	@Test
	public void isNoun() throws Exception {
		Assert.assertEquals(n1.isNoun(), false);
		Assert.assertEquals(n2.isNoun(), true);
		Assert.assertEquals(n3.isNoun(), true);
	}

	@Test
	public void getStr() throws Exception {
		Assert.assertEquals(n1CleanString, n1.toString());
		Assert.assertEquals(n2CleanString, n2.toString());
		Assert.assertEquals(n3CleanString, n3.toString());
	}

	@Test
	public void getCategory() throws Exception {
		Assert.assertEquals("VBD", n1.getCategory());
		Assert.assertEquals("NN", n2.getCategory());
		Assert.assertEquals("NN", n3.getCategory());
	}

	@Test
	public void getDependency() throws Exception {
		Assert.assertEquals("ROOT", n1.getDependency());
		Assert.assertEquals("nsubj", n2.getDependency());
		Assert.assertEquals("nsubj", n3.getDependency());
	}

}