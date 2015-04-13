import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * main file doing all the stuff , calling other functions
 */

public class mainfile {
	public static Map<String, String> document_gender_map = new HashMap<String, String>();
	
	public static void main(String args[]) {

		File dir = new File(globals.directory);
		String[] children = dir.list();

		// assign globals
		globals.num_docs = children.length;

		// preprocess and create stop words map
		preprocess preobj = new preprocess();
		docmatrix matrixobj = new docmatrix();

		// for each doctokenize and update tfidf
		for (int i = 0; i < children.length; i++) {
			preobj.tokenize(globals.directory + "/" + children[i], matrixobj, i);
		}
		// if(true) return;
	//	globals.num_words = matrixobj.getNumWords();

		matrixobj.applytf();
		matrixobj.applyidf();
		// matrixobj.printmatrix();
		matrixobj.fillWordmap();
	//	matrixobj.printImportantWords(children);
		CreateDocumentGenderMap();
		matrixobj.printTopics(preobj.topicMap, children);
		
		
		matrixobj.printGenderTopics();
	}

	/**
	 * create map of stopwords for lookup
	 */

	public static void CreateDocumentGenderMap() {
		try {
			FileInputStream fstream = new FileInputStream(globals.genderfile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			 int curindex = -1;
			while ((strLine = br.readLine()) != null) {
				String s = strLine;
	//			System.out.println(s);
				document_gender_map.put(s.split(" ")[0], s.split(" ")[1]);
			}
			// Close the input stream
			in.close();

		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());

		}
	}
	
	/**
	 * prints the list of all files on which algo is applied
	 * 
	 * @param files
	 */
	static void printfiles(String[] files) {
		for (int i = 0; i < files.length; i++)
			System.out.println(files[i]);
	}
}