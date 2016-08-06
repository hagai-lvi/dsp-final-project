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

		// The concrete tree
		mapDriver.withInput(new Text(),new Text(
				Step3Mapper.STEP_1_PREFIX + " stage/NN amod diseas/NN pobj in/IN prep %/NN\t11"));

		// The abstract path
		mapDriver.withInput(new Text(), new Text(
				Step3Mapper.STEP_2_PREFIX + " amod diseas/NN pobj in/IN prep\t20"));

		// The pair of words
		mapDriver.withOutput(new Text(Step3Mapper.PAIR_PREFIX + "stage %"), new Text());

		// The concrete tree
		mapDriver.withOutput(new Text("amod diseas/NN pobj in/IN prep +"), new Text("stage %"));

		// The abstract path with its ID
		mapDriver.withOutput(new Text("amod diseas/NN pobj in/IN prep *"), new Text("20"));
		mapDriver.runTest();
	}

	@Test
	public void mapWithSlashInWord() throws Exception {

		mapDriver.withInput(new Text(), new Text(
				Step3Mapper.STEP_1_PREFIX + " standpoint/hello/NN pobj from/IN prep make/VB infmod abil/NN\t11"));
		mapDriver.withInput(new Text(),
				new Text(Step3Mapper.STEP_2_PREFIX + " pobj from/IN prep make/VB infmod\t1901"));

		// The pair of words
		mapDriver.withOutput(new Text(Step3Mapper.PAIR_PREFIX + "standpoint/hello abil"), new Text());

		// The concrete tree
		mapDriver.withOutput(new Text("pobj from/IN prep make/VB infmod +"), new Text("standpoint/hello abil"));

		// The abstract path with its ID
		mapDriver.withOutput(new Text("pobj from/IN prep make/VB infmod *"), new Text("1901"));

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