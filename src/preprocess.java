import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * class for preprocessing the data before clustering
 * 
 */

public class preprocess {

	// map used for stopwords
	private Map<String, Integer> stopWordsMap = new HashMap<String, Integer>();

	// map to maintain stemmed words with full words  appl -> apple
	private static Map<String, String> full_strmap = new HashMap<String, String>();

	public ArrayList<HashMap<String, Integer>> topicMap = new ArrayList<HashMap<String, Integer>>();
	
	
	
	public preprocess() {
		CreateStopWordsMap();
		CreateTopicsMap();
		
	}

	public static String getFullWord(String partialWord)
	{
		return full_strmap.get(partialWord);
	}
	
	/**
	 * check whether noise word or not eliminate numbers also
	 */
	public boolean isNoiseWord(String word) {
		try {
			Double.parseDouble(word);
		} catch (NumberFormatException nfe) {
			if (word.length() > 2)
				return false;
			else
				return true;
		}
		return true;
	}

	/**
	 * tokenize the file
	 * 
	 * @param filename
	 * @param matrixobj
	 * @param docid
	 * @return
	 */
	public int tokenize(String filename, docmatrix matrixobj, int docid) {

		String separator = "\t\n\r\f ,().\"-'<>/()@:";
		try {
			FileInputStream fstream = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// CreateMap();
			while ((strLine = br.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(strLine,
						separator, false);
				while (tokenizer.hasMoreTokens()) {

					String word = tokenizer.nextToken();
					if (!isNoiseWord(word) && !isStopWord(word)) {

						word = word.toLowerCase();

						// Stemmer.stemString(word);
						// word = word.toLowerCase();
						String word1 = Stemmer.stemString(word);
						if (full_strmap.get(word1) == null)
							full_strmap.put(word1, word);

						word = word1;
						matrixobj.update_tf(word, docid);
						// System.out.println(word);
					}
				}
			}
			// Close the input stream
			in.close();

		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
			return -1;
		}
		return 0;
	}

	/**
	 * create map of stopwords for lookup
	 */

	public void CreateStopWordsMap() {
		try {
			FileInputStream fstream = new FileInputStream(globals.stopwordfile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				stopWordsMap.put(strLine, 1);
			}
			// Close the input stream
			in.close();

		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());

		}
	}
	
	/**
	 * create map of stopwords for lookup
	 */

	public void CreateTopicsMap() {
		try {
			FileInputStream fstream = new FileInputStream(globals.topicsfile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			 int curindex = -1;
			while ((strLine = br.readLine()) != null) {
				if(strLine.startsWith("Topic"))
				{
					HashMap<String, Integer> h = new HashMap<String, Integer>();
					topicMap.add(h);
					curindex++;
				}
				else
				{
					strLine = strLine.toLowerCase().trim();;

					topicMap.get(curindex).put(Stemmer.stemString(strLine), 1);
				}
			}
			// Close the input stream
			in.close();

		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());

		}
	}

	
	
	/**
	 * check for stop word
	 * 
	 * @param word
	 * @return
	 */
	public boolean isStopWord(String word) {
		if (stopWordsMap.get(word) == null)
			return false;
		else
			return true;
	}

}