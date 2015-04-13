import java.util.*;


/**
 * Class performing all operations of tfidf and tficf matrix.
 * 
 */

public class docmatrix {

	public Map<String, List<Double>> m = new HashMap<String, List<Double>>();

	// no of words in each documnet
	List<Double> termsInDoc = new ArrayList<Double>();

	ArrayList<HashMap<String, Double>> impWords = new ArrayList<HashMap<String, Double>>();

	ArrayList<Integer> maleTopics = new ArrayList<Integer>();

	ArrayList<Integer> femaleTopics = new ArrayList<Integer>();

	/**
	 * constructor for docmatrix
	 */
	public docmatrix() {
	}

	public int getNumWords() {
		return m.keySet().size();
	}

	void initTermsInDoc() {
		for (int i = 0; i < globals.num_docs + 2; i++) {
			Double termObj = 0.0;
			termsInDoc.add(termObj);
		}

	}

	/**
	 * Update the matrix to word frequencies for each document matrix has the
	 * structure : tf values, total how many times does this word occur, total
	 * in how many documents is this word
	 * 
	 * @param key
	 * @param docindex
	 */
	void update_tf(String key, int docindex) {
		List<Double> l = m.get(key);
		if (l == null) {
			m.put(key, l = new ArrayList<Double>(globals.num_docs + 2));
			for (int i = 0; i < globals.num_docs + 2; i++) {
				Double fobj = 0.0;
				l.add(fobj);
			}
			Double tempobj = 0.0;
			termsInDoc.add(tempobj);
		}
		// total in how many documents is this word
		if (l.get(docindex) == 0) {
			l.set(globals.num_docs + 1, l.get(globals.num_docs + 1) + 1);
		}

		l.set(docindex, l.get(docindex) + 1);
		// total how many times does this word occur
		l.set(globals.num_docs, l.get(globals.num_docs) + 1);
		termsInDoc.set(docindex, termsInDoc.get(docindex) + 1);
	}

	/**
	 * Apply the formula TF(t) = (Number of times term t appears in a document)
	 * / (Total number of terms in the document).
	 */
	void applytf() {
		Set<String> keys = m.keySet();
		Iterator<String> keyIter = keys.iterator();

		while (keyIter.hasNext()) {
			List<Double> l = m.get(keyIter.next());
			for (int j = 0; j < globals.num_docs; j++) {

				// IDF(t) = log_e(Total number of documents / Number of
				// documents with term t in it).
				l.set(j, l.get(j) / termsInDoc.get(j));
			}
		}

	}

	/**
	 * apply idf on raw matrix
	 */
	void applyidf() {
		Set<String> keys = m.keySet();
		Iterator<String> keyIter = keys.iterator();

		while (keyIter.hasNext()) {
			List<Double> l = m.get(keyIter.next());
			for (int j = 0; j < globals.num_docs; j++) {

				// IDF(t) = log_e(Total number of documents / Number of
				// documents with term t in it).
				l.set(j,
						l.get(j)
								* Math.log(globals.num_docs
										/ l.get(globals.num_docs + 1)));
			}
		}

	}

	void printMatrix() {
		for (String s : m.keySet()) {
			System.out.print(s + ": ");
			for (int i = 0; i < globals.num_docs; i++) {
				System.out.print(m.get(s).get(i) + " ");
			}
			System.out.println();
		}

	}

	/**
	 * apply idf on raw matrix
	 */
	void fillWordmap() {

		for (int i = 0; i < globals.num_docs; i++) {
			HashMap<String, Double> h = new HashMap<String, Double>();
			impWords.add(h);
		}

		Set<String> keys = m.keySet();
		for (String key : keys) {
			List<Double> l = m.get(key);
			for (int index = 0; index < globals.num_docs; index++) {
				Double val = l.get(index);
				if (val > 0) {
					impWords.get(index).put(key, val);
				}
			}
		}
	}

	/**
	 * apply idf on raw matrix
	 */
	void printImportantWords(String[] children) {
		int i = 0;
		for (i = 0; i < globals.num_docs; i++)

		{
			HashMap<String, Double> map = impWords.get(i);
			// System.out.println("results: " + map);

			ValueComparator bvc = new ValueComparator(map);
			TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(
					bvc);
			sorted_map.putAll(map);
			System.out.println(children[i] + ": " + sorted_map);

		}

	}

	/**
	 * apply idf on raw matrix
	 */
	void printTopics(ArrayList<HashMap<String, Integer>> topics,
			String[] children) {
		for (int i = 0; i < globals.num_docs; i++)

		{
			HashMap<String, Double> map = impWords.get(i);
			double maxScore = 0.0;
			int maxTopic = 0;
			String bestWordForTopic = null;
			String curBestWord = null;
			for (int j = 0; j < topics.size(); j++) {
				HashMap<String, Integer> curTopic = topics.get(j);
				double curScore = 0;
				
				double bestWordScore = 0;
				for (String wordinDoc : map.keySet()) {
					if (curTopic.get(wordinDoc) != null) {
						curScore += map.get(wordinDoc);
						if(map.get(wordinDoc) > bestWordScore)
						{
							bestWordScore = curScore;
							curBestWord = wordinDoc;
						}
					}
				}
				if (curScore > maxScore) {
					maxScore = curScore;
					maxTopic = j;
					bestWordForTopic = curBestWord;
				}
			}

			System.out.println("Topic for " + children[i] + " : " + maxTopic
					+ "		Score:" + maxScore+ "			Best word:" + preprocess.getFullWord(bestWordForTopic));
			String gender = mainfile.document_gender_map.get(children[i]
					.subSequence(0, children[i].indexOf(".")));
			if (gender.equalsIgnoreCase("Male")) {
				maleTopics.add(maxTopic);
			} else if (gender.equalsIgnoreCase("Female")) {
				femaleTopics.add(maxTopic);
			}
		}

	}

	void printGenderTopics() {
		Collections.sort(maleTopics);
		Collections.sort(femaleTopics);
		Set<Integer> maleSet = new HashSet<Integer>();
		maleSet.addAll(maleTopics);
		Set<Integer> femaleSet = new HashSet<Integer>();
		femaleSet.addAll(femaleTopics);
		
		
		System.out.println("==================================MALE TOPICS=======  Total males: "+ maleTopics.size() + " Total topics males:" + maleSet.size() );
		System.out.println(maleTopics);
		System.out
				.println("==================================FEMALE TOPICS======= Total females: "+ femaleTopics.size() + " Total topics female:" + femaleSet.size());
		System.out.println(femaleTopics);
		List<Integer> unionList = union(maleTopics, femaleTopics);
		System.out
				.println("==================================UNION========================================: "
						+ unionList.size());
		System.out.println(unionList);
		
		List<Integer> interSectionList = intersection(maleTopics, femaleTopics);
		Set<Integer> set = new HashSet<Integer>();

		set.addAll(interSectionList);
		interSectionList = new ArrayList<Integer>(set);
		System.out
				.println("==================================INTERSECTION========================================: "
						+ interSectionList.size());
		System.out.println(interSectionList);
		int m = 0, f = 0;
		int firstTopic = maleTopics.get(0);
		int count = 1, maxTopic = firstTopic, maxCount = 1;
		for (m = 1; m < maleTopics.size(); m++) {
			if (maleTopics.get(m) == firstTopic) {
				count++;

			} else {

				if (count > maxCount) {
					maxCount = count;
					maxTopic = firstTopic;

				}
				firstTopic = maleTopics.get(m);
				count = 1;
			}
		}
		if (count > maxCount) {
			maxTopic = maleTopics.get(m - 1);
		}
		System.out
				.println("==================================COMMON MALE TOPIC========================================: "
						+ maxTopic + " : " + maxCount);
		
		
		firstTopic = femaleTopics.get(0);
		count = 1; maxTopic = firstTopic; maxCount = 1;
		for (m = 1; m < femaleTopics.size(); m++) {
			if (femaleTopics.get(m) == firstTopic) {
				count++;

			} else {

				if (count > maxCount) {
					maxCount = count;
					maxTopic = firstTopic;

				}
				firstTopic = femaleTopics.get(m);
				count = 1;
			}
		}
		if (count > maxCount) {
			maxTopic = femaleTopics.get(m - 1);
		}
		System.out
				.println("==================================COMMON FEMALE TOPIC========================================: "
						+ maxTopic + " : " + maxCount);

	}

	/**
	 * print the matrix
	 * 
	 */
	void printmatrix() {
		System.out.println(m.entrySet());
	}

	public <T> List<T> union(List<T> list1, List<T> list2) {
		Set<T> set = new HashSet<T>();

		set.addAll(list1);
		set.addAll(list2);

		return new ArrayList<T>(set);
	}

	public <T> List<T> intersection(List<T> list1, List<T> list2) {
		List<T> list = new ArrayList<T>();

		for (T t : list1) {
			if (list2.contains(t)) {
				list.add(t);
			}
		}

		return list;
	}
}