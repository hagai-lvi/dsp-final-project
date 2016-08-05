package dsp.step3;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by hagai_lvi on 29/07/2016.
 */
public class Step3Reducer extends Reducer<Text, Text, Text, Text> {
	final static Logger logger = Logger.getLogger(Step3Reducer.class);

	private String currentPath;

	private MultipleOutputs<Text, Text> mos;

	/**
	 * cleanup the MultipleOutputs
	 */
	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		if (mos != null) {
			mos.close();
		}
		super.cleanup(context);
	}

	/**
	 * Setup the MultipleOutputs
	 */
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		mos = new MultipleOutputs<Text, Text>(context);
		super.setup(context);
	}

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

		// Got a pair of words, write it to the output so that the model will be aware of it
		if (key.toString().startsWith(Step3Mapper.PAIR_PREFIX)) {
			mos.write(
					Step3Main.WORD_PAIRS_OUTPUT_FILE,
					new Text(key.toString().replaceFirst(Step3Mapper.PAIR_PREFIX, "")),
					new Text());
			return;
		}


		if (key.toString().endsWith(Step3Mapper.PATH_POSTFIX)) {
			this.currentPath = key.toString().substring(0, key.toString().length() - 2);
			return;
		}
		else if (key.toString().endsWith(Step3Mapper.TREE_POSTFIX)){
			String path = key.toString().substring(0, key.toString().length() - 2);
			if (! path.equals(this.currentPath)) {
				// We got paths that didn't pass the filtering of step 2, so we won't do nothing with them
				return;
			}

			for (Text value : values) {
				mos.write(Step3Main.PATHS_OUTPUT_FILE, value, new Text(currentPath));
			}
		}
		else {
			logger.error("Got unexpected key: \"" + key.toString() + "\".");
		}
	}

}
