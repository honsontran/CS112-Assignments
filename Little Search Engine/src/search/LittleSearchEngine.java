package search;

import java.io.*;
import java.util.*;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Sesh Venugopal
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;

	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.put(word,word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
		
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) 
	throws FileNotFoundException {
		//Create a Hashmap to return.
		HashMap<String, Occurrence> masterKey = new HashMap<String, Occurrence>();
		
		// Create a scanner that will go through the file.
		Scanner sc = new Scanner(new File(docFile));
		
		//We want to keep loading in words until the end of the document.
		while (sc.hasNext()) {
			
			//Target word to compare.
			String word = getKeyWord( sc.next() );
			
			/*
			 * Conditions: Call the getKeywords method. Determine what to do if you get a word or if you
			 * 			   get null back.
			 */
			
			//Condition 1: If the condition doesn't return null, either put into HashMap or increase the occurrence.
			if (word != null) {				
				//Condition 1a: If this word isn't placed into the HashMap yet.
				if (!masterKey.containsKey(word)) {
					//Make a new occurrence of this word and make the freq. 1
					Occurrence occur = new Occurrence(docFile, 1);
					
					//Place this word into the Hashmap.
					masterKey.put(word, occur);
				}
				
				else { // If there already is a word in the map present.
					//Simply add 1 to the frequency.
					masterKey.get(word).frequency++;
				}
			}
			
		}
		
		// System.out.println(masterKey.toString());
		
		//Condition 2: If it's null, don't do anything.
		return masterKey;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		
		// Go through the HashMap of the keywords.
		for (String key : kws.keySet()) {
			
			//System.out.println("-------------------------------------------------------------------------------");
			//System.out.println("Current key for target: " + key);
			
			Occurrence freq = kws.get(key);		// This is the occurrence of the specific key (word)
			
			//There are two cases to this algorithm.
			
			// Case 1: The key does not exist in master yet.
			if (!keywordsIndex.containsKey(key)) {
				
				// 1. Create an ArrayList to insert the occurrence into.
				ArrayList<Occurrence> list = new ArrayList<Occurrence>();
				
				// 2. add this occurrence into the Arraylist.
				list.add(freq);
				
				// 3. Now, we can put this into the designated index of master.
				keywordsIndex.put(key, list);
				
				// 4. Call insertLastOccurrence to sort the ArrayList.
				insertLastOccurrence(list);
				
			}
			
			// Case 2: The key exists in master, then there should also be an ArrayList present.
			else if(keywordsIndex.containsKey(key)) {
				
				// 1. Get the list of occurrences we are working with.
				ArrayList<Occurrence> currList = keywordsIndex.get(key);
				
				// 2. Add this occurrence into the ArrayList.
				currList.add(freq);
				
				// 3. Now, we have to update the ArrayList.
				insertLastOccurrence(currList);
				
				// 4. Put this sorted list back into master.
				keywordsIndex.put(key, currList);
				
			}
		}

	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) {
		//Turn everything to lower-case. Doing so will be able to compare words a lot easier.
		word = word.toLowerCase();
		
		// Noise words include those that are a letter or less. If this is a case,
		// it's automatically null.
		if ( word.length() <= 1 || word == null ){
			return null;
		}
		
		//Strip the beginning punctuation until it hits a letter.
		while (!Character.isLetter(word.charAt(0)) ) {
			
			if (word.length() == 1) {
				return null;
			}
			
			word = word.substring(1, word.length());
		}
		
		//Now strip the end until it hits a letter.
		while ( !Character.isLetter(word.charAt(word.length()-1)) ) {
			
			if (word.length() == 1) {
				return null;
			}
			
			word = word.substring(0, word.length()-1);			
		}
		
		//Run through the word and see if it's a letter at each char.
		for (int i = 0; i < word.length(); i++) {
			
			if (!Character.isLetter( word.charAt(i)) ) {
				return null;
			}
			
		}
		
		// After stripping the word, we have to check if it's a noise word.
		if (noiseWords.containsKey(word)) {
			return null;
		}
		
		return word;
	}

	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) { //checked
		ArrayList<Integer> freq = new ArrayList<Integer>();
		ArrayList<Integer> points = new ArrayList<Integer>();

		
		//Declare variables for binary search
		int low = 0;
		int hi = occs.size()-2;
		int mid;				//keep track of midpoint
		int target = occs.get( occs.size()-1 ).frequency;
		
		// System.out.println("Our target is: " + target);
		
		//Get all the frequencies for the occurrences.
		for (int i = 0; i < occs.size(); i++) {
			freq.add( occs.get(i).frequency );
		}
		
		//System.out.println("Our current freq list: " + freq.toString());
		
		// Now to do binary search.
		// The point of this method is to return the mid points that we visit.
		while (hi >= low) {
			mid = (hi + low)/2;		// Calculate our mid point.
			
			//System.out.println("Our new mid is: " + mid);
			
			points.add(mid);		// then add this point to our points list.

			if (freq.get(mid) < target) {
				hi = mid - 1;
			}
			
			else if (freq.get(mid) > target){
				low = mid + 1;
			}
			
			else {
				break;
			}

		}
		
		return points;
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		//Create an Arraylist to keep track of which documents have the more frequent words in order.
		ArrayList<String> results = new ArrayList<String>();
		
		//Create an ArrayList for the first and second keywords
		ArrayList<Occurrence> word1 = keywordsIndex.get(kw1);
		ArrayList<Occurrence> word2 = keywordsIndex.get(kw2);
		
		//Keep track of how many documents there currently are in the search results.
		int total = 0;
		int i = 0;
		int j = 0;
		
		/*
		 * Check if any of the lists are null, meaning that there is no such word in the documents.
		 * This search continues until 5 documents populate the
		 */
		
		//If both lists are null.
		if (word1 == null && word2 == null) {
			results = inverse(results);
			return results;
		}
		
		else if (word1 == null) {				//This means that the second keyword isn't null.
			while (i < word2.size() && total < 5) {
				results.add(word2.get(j).document);
				total++;
			}
			
			results = inverse(results);
			return results;
		}
		
		else if (word2 == null) { 				//This means that the first keyword isn't null.
			while (i < word1.size() && total < 5) {
				results.add(word1.get(j).document);
				total++;
			}
			
			results = inverse(results);
			return results;
		}
		
		else {									//If both lists are true.
			while(i < word1.size() && j < word2.size() && total < 5 ) {
				
				//Take advantage of the fact that the occurrences are in descending order.
				//Comparison 1: If word1 has more occurrences than word2. If word1 == word2, take word1 and include in results.
				if (word1.get(i).frequency >= word2.get(j).frequency && !results.contains(word1.get(i).document) ) {
					results.add(word1.get(i).document);
					total++;
					i++;
				}
				
				//Comparison 2: If word2 has a great occurrence than word2, and it doesn't exist in the results array.
				else if (word2.get(j).frequency <= word1.get(i).frequency && !results.contains(word2.get(j).document) ) {
					results.add(word2.get(j).document);
					total++;
					j++;
				}
				
				//Comparison 3: If word2 is null from running through the list, and there is only word1's array left.
				else if (word2.get(j) == null && !results.contains(word1.get(i).document) ) {
					results.add(word1.get(i).document);
					total++;
					i++;
				}
				
				//Comparison 4: If word1 is null from running through the list, and there is only word2's array left.
				else if (word1.get(i) == null && !results.contains(word2.get(j).document)) {
					results.add(word2.get(j).document);
					total++;
					j++;
				}
				
				else {
					results = inverse(results);
					return results;
				}
			}
		}
		results = inverse(results);
		return results;
	}
	
	private ArrayList<String> inverse(ArrayList<String> original) {
		ArrayList<String> results = new ArrayList<String>();
		
		for (int i = original.size(); i > 0 ; i--) {
			results.add(original.get(i-1));
		}
		return results;
	}
}
