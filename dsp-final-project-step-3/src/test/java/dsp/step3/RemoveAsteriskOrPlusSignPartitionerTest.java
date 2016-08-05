package dsp.step3;

import org.apache.hadoop.io.Text;
import org.junit.Assert;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * Created by hagai_lvi on 29/07/2016.
 */
public class RemoveAsteriskOrPlusSignPartitionerTest {

	@Test
	public void partitionAsterisk() {
		RemoveAsteriskOrPlusSignPartitioner partitioner = new RemoveAsteriskOrPlusSignPartitioner();

		Assert.assertEquals(
				partitioner.getPartition(
						new Text("abc"),
						new Text("abc"),
						1000
				),
				partitioner.getPartition(
						new Text("abc *"),
						new Text("abc"),
						1000
				));


		Assert.assertThat(
				partitioner.getPartition(
						new Text("abc"),
						new Text("abc"),
						1000
				),
				is(not(equalTo(
						partitioner.getPartition(
								new Text("abc $"),
								new Text("abc"),
								1000
						)
				)))
		);
	}


	@Test
	public void partitionPlusSign() {
		RemoveAsteriskOrPlusSignPartitioner partitioner = new RemoveAsteriskOrPlusSignPartitioner();

		Assert.assertEquals(
				partitioner.getPartition(
						new Text("abc"),
						new Text("abc"),
						1000
				),
				partitioner.getPartition(
						new Text("abc +"),
						new Text("abc"),
						1000
				));


		Assert.assertThat(
				partitioner.getPartition(
						new Text("abc"),
						new Text("abc"),
						1000
				),
				is(not(equalTo(
						partitioner.getPartition(
								new Text("abc $"),
								new Text("abc"),
								1000
						)
				)))
		);
	}

}
