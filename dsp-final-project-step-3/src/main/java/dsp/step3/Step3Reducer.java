package dsp.step3;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by hagai_lvi on 29/07/2016.
 */
public class Step3Reducer extends Reducer<Text, Text, Text, Text> {

	private String currentPath;
	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		if (! key.toString().endsWith(" *")) {
			this.currentPath = key.toString();
			return;
		}
		else {
			String path = key.toString().substring(0, key.toString().length() - 2);
			if (! path.equals(this.currentPath)) {
				// We got paths that didn't pass the filtering of step 2, so we won't do nothing with them
				return;
			}

			for (Text value : values) {
				context.write(value, new Text(currentPath));
			}
		}
	}

}
