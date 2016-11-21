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
		
		//Now, make a scanner to go through the document.
		Scanner currWord = new Scanner(new File(docFile) );
		
		//We want to keep loading in words until the end of the document.
		while (currWord.hasNext()) {
			
			//Target word to compare.
			String word = currWord.next();
			
			/*
			 * Conditions: Call the getKeywords method. Determine what to do if you get a word or if you
			 * 			   get null back.
			 */
			
			//Condition 1: If the condition doesn't return null, either put into HashMap or increase the occurrence.
			if (getKeyWord(word) != null) {
				//Condition 1a: If this word isn't placed into the HashMap yet.
				if (!masterKey.containsKey(word)) {
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
		ArrayList<Occurrence> existingOccList = new ArrayList<Occurrence>();

		// loop through each key within the set of the keywords in the doc
		for(String key: kws.keySet()) {	

			// store all occurances within the kws in 'occurrence'
			Occurrence occurrence = kws.get(key);
			
			// if the kewords Index does not contain the key we add it to a new occurrence list and place the key value into the keywords index
			// otherwise, insert the occurance of the keword into the appriopiate place using insertLastOccurance
			if(!keywordsIndex.containsKey(key))	{	
				ArrayList<Occurrence> newOccList = new ArrayList<Occurrence>();			
				newOccList.add(occurrence);
				keywordsIndex.put(key, newOccList);
			}
			else {
				existingOccList = keywordsIndex.get(key);
				existingOccList.add(occurrence);

				// implement this
				insertLastOccurrence(existingOccList);

				keywordsIndex.put(key, existingOccList);
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
		
		String curr = "";		//keeps track of our current keyword
		
		//Exception: When punctuation is in the beginning.
		if ( Character.isLetter( word.charAt(0) ) ) {
			return null;			
		}
		
		//Turn everything to lowercase.
		word.toLowerCase();
		
		//Run through the word and see if it's a letter at each char.
		for (int i = 0; i < word.length(); i++) {
			
			if ( Character.isLetter( word.charAt(i) ) ) {			//If letter, add to keyword string
				curr += word.charAt(i);
			}
			
			else {	//Anything other than a letter
				if ( Character.isDigit( word.charAt(i) ) ) {
					return null;
				}
				
				else if ( isPunctuation(word.charAt(i)) ) { //If it's punctuation
					//Case 1: If the punctuation is between 2 chars (i.e. we're), return null.
					if ( Character.isLetter( word.charAt(i-1) ) && Character.isLetter( word.charAt(i+1) ) ) {
						return null;
					}
				
					/* Case 2: If there's just a trailing end of punctuation (i.e. what??!!?!?)
					 * 		   keep checking the rest of the string in the next i++
					 */
					
					// Case 3: If you have punctuation and there's a letter or not a punctuation, return null.
					if ( !isPunctuation(word.charAt(i)) || Character.isDigit( word.charAt(i+1) ) ) {
						return null;
					}
					
				}
			}
		}
		
		return curr;
	
	}
	
	private boolean isPunctuation(char c) {
		if (c == '.' || c == ',' || c == '?' || c == ':' || c == ';' || c == '!') {
			return true;
		}
		
		else {
			return false;
		}
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
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		return null;
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
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		return null;
	}
}
