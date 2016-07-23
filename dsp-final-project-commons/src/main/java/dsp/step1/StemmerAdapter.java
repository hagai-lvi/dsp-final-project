package dsp.step1;

/**
 * Created by thinkPAD on 7/18/2016.
 */
public class StemmerAdapter {

    private static Stemmer stemmer = new Stemmer();

    public static String stem(String s) {
        char[] chars = s.toCharArray();
        stemmer.add(chars,chars.length);
        stemmer.stem();
        return stemmer.toString();
    }
}
