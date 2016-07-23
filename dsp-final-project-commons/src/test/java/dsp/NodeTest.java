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

	@Before
	public void setUp() {
		n1String = "abounded/VBD/ROOT/0";
		n1 = new Node(n1String);

		n2String = "crew/NN/nsubj/4";
		n2 = new Node(n2String);

		// word can be equals to "/"
		n3String = "//NN/nsubj/4";
		n3 = new Node(n3String);
	}

	@Test
	public void getWord() throws Exception {
		Assert.assertEquals("abounded", n1.getWord());
		Assert.assertEquals("crew", n2.getWord());
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
		Assert.assertEquals(n1String, n1.getStr());
		Assert.assertEquals(n2String, n2.getStr());
		Assert.assertEquals(n3String, n3.getStr());
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