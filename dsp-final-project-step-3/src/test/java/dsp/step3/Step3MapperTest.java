package dsp.step3;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class Step3MapperTest {

	private MapDriver<Object, Text, Text, Text> mapDriver;
	private Step3Mapper mapper;

	@Before
	public void setUp() {
		mapper = new Step3Mapper();
		mapDriver = MapDriver.newMapDriver(mapper);
	}

	@Test
	public void map() throws Exception {

		mapDriver.withInput(new Text(), new Text("tree youth/NN pobj of/IN prep %/NN\t133"));
		mapDriver.withInput(new Text(), new Text("path pobj of/IN prep"));
		mapDriver.withOutput(new Text(Step3Mapper.PAIR_PREFIX + "youth %"), new Text());
		mapDriver.withOutput(new Text("pobj of/IN prep +"), new Text("youth %"));
		mapDriver.withOutput(new Text("pobj of/IN prep *"), new Text("---"));
		mapDriver.runTest();
	}

	@Test
	public void mapWithSlashInWord() throws Exception {

		mapDriver.withInput(new Text(), new Text("tree youth/hello/NN pobj of/IN prep %/NN\t133"));
		mapDriver.withInput(new Text(), new Text("path pobj of/IN prep"));
		mapDriver.withOutput(new Text(Step3Mapper.PAIR_PREFIX + "youth/hello %"), new Text());
		mapDriver.withOutput(new Text("pobj of/IN prep +"), new Text("youth/hello %"));
		mapDriver.withOutput(new Text("pobj of/IN prep *"), new Text("---"));
		mapDriver.runTest();
	}

	@Test
	public void extractNounsFromTree() throws Exception {
		Assert.assertEquals("youth %",mapper.extractNounsFromTree("youth/NN pobj of/IN prep %/NN"));

		Assert.assertEquals("youth %",mapper.extractNounsFromTree("youth/NN pobj of/IN prep youth/NN pobj of/IN prep %/NN"));

		Assert.assertEquals("youth/hello %",mapper.extractNounsFromTree("youth/hello/NN pobj of/hello/IN prep %/NN"));

	}

	@Test
	public void extractWordFromNode() throws Exception {
		Assert.assertEquals(
				"abc",
				mapper.extractWordFromNode("abc/NN")
		);

		Assert.assertEquals(
				"abc/def",
				mapper.extractWordFromNode("abc/def/NN")
		);
	}

	@Test
	public void extractPathFromTree() throws Exception {
		Assert.assertEquals("pobj of/IN prep",mapper.extractPathFromTree("youth/NN pobj of/IN prep %/NN"));

		Assert.assertEquals("pobj of/IN prep youth/NN pobj of/IN prep",mapper.extractPathFromTree("youth/NN pobj of/IN prep youth/NN pobj of/IN prep %/NN"));

		Assert.assertEquals("pobj of/hello/IN prep",mapper.extractPathFromTree("youth/hello/NN pobj of/hello/IN prep %/NN"));
	}

}