package structures;

import java.util.ArrayList;

/**
 * This class implements a compressed trie. Each node of the tree is a CompressedTrieNode, with fields for
 * indexes, first child and sibling.
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	/**
	 * Words indexed by this trie.
	 */
	ArrayList<String> words; 
	
	/**
	 * Root node of this trie.
	 */
	TrieNode root;
	
	/**
	 * Initializes a compressed trie with words to be indexed, and root node set to
	 * null fields.
	 * 
	 * @param words
	 */
	public Trie() {
		root = new TrieNode(null, null, null);
		words = new ArrayList<String>();
	}
	
	/**
	 * Inserts a word into this trie. Converts to lower case before adding.
	 * The word is first added to the words array list, then inserted into the trie.
	 * 
	 * @param word Word to be inserted.
	 */
	 
	//Round 1
	public void insertWord(String word) {
		/** COMPLETE THIS METHOD **/
		TrieNode ptr = root.firstChild;
		TrieNode ptr2 = ptr;
		String prefix = "";
	
		words.add(word);
		
		//If the tree is empty.
		if (ptr == null) {
			TrieNode insert = new TrieNode(null, null, null);
			insert.substr = new Indexes(0, (short)0, (short)word.length() );
			root.firstChild = insert;
			ptr = insert;
			return;
		}
		
		System.out.println(words.toString());
		
		while (ptr != null){
			//Check if this is a tree or a leaf.
			if (ptr.firstChild == null) {			//Leaf
				prefix = words.get(ptr.substr.wordIndex);
				
				if ( getAmtShared( word, prefix ) > 0 ) {		//If there's any shared chars in the leaf
					//Leaf turns into a tree, and put the word under the new tree.
					TrieNode insert = new TrieNode(null, null, null);
					ptr.firstChild = insert;					//Point ptr to the new word.
					System.out.println();
					ptr.substr.endIndex = (short)(getAmtShared( word, prefix ) - 1);
					insert.substr = new Indexes( ptr.substr.wordIndex , (short) ptr.substr.startIndex , (short) (words.get( ptr.substr.wordIndex ).length() - 1) );
					insert.sibling = new TrieNode( null , null , null);
					insert.sibling.substr = new Indexes( words.size() - 1 , (short)ptr.substr.startIndex , (short)words.get(words.size() - 1).length() );
					return;
				}
				
			}else{                      			 //Tree
				prefix = words.get(ptr.substr.wordIndex);
				
				//Condition 1: When we have more than 1 shared char, but not more than our prefix
				if ( getAmtShared( word, prefix ) > 0 && getAmtShared( word, prefix ) < prefix.length() ) {
					TrieNode temp = ptr.firstChild;
					TrieNode insert = new TrieNode(null, temp, null);
					insert.substr = new Indexes( ptr.substr.wordIndex , ptr.substr.startIndex , ptr.substr.endIndex );
					ptr.substr.endIndex = (short)(getAmtShared( word, prefix ) - 1);
					ptr.firstChild = insert;
					insert.sibling = new TrieNode( null , null , null );
					insert.sibling.substr = new Indexes( words.size() - 1 , ptr.substr.endIndex , (short) ((short) words.get( words.size() - 1 ).length()-1)  );
					return;
				}
				
				//Condition 2: When our insertWord has the same amount of chars as the prefix, it should be a child.
				else if ( getAmtShared( word, prefix ) == prefix.length() ) {
					theNewInsertWord( word , ptr );
					return;
				}
			}
			
			ptr = ptr.sibling;
			if (ptr != null) {
				ptr2 = ptr;
			}
		}
		
		//if you get this far, then word needs to be inserted as a child of the root (put as sibling of the last pointer)
		TrieNode insert = new TrieNode( null, null, null );
		System.out.println(words.size());
		insert.substr = new Indexes(   words.size() - 1  , (short)0, (short)words.get( words.size()-1).length());
		System.out.println( insert.substr.wordIndex );
		//insert.substr = new Indexes( ptr2.substr.wordIndex , ptr2.substr.startIndex , ptr2.substr.endIndex );
		ptr2.sibling = insert;
		
	}

	/*
	 * Recursive version of the original insert word.
	*/
	private void theNewInsertWord (String word, TrieNode newRoot) {
		TrieNode ptr = newRoot.firstChild;
		TrieNode ptr2 = ptr;
		String prefix = "";
		String original = "";
		String shared = "";
		
		words.add(word);
		
		if( word.indexOf( '+' ) != -1){
			original = original.substring( 0 , original.indexOf( '+' ) );
			shared = original.substring( original.indexOf( '+') + 1 );
		}else{
			original = word;
			shared = "";
		}
		
		while (ptr != null){
			//Check if this is a tree or a leaf.
			if (ptr.firstChild == null) {			//Leaf
				prefix = words.get(ptr.substr.wordIndex);
				
				if ( getAmtShared( original, prefix ) > shared.length() ) {		//If there's any shared chars in the leaf
					TrieNode insert = new TrieNode(null, null, null);
					ptr.firstChild = insert;					//Point ptr to the new word.
					
					ptr.substr.endIndex = (short)(getAmtShared( original, prefix ) - 1);
					insert.substr = new Indexes( ptr.substr.wordIndex , (short) ptr.substr.startIndex , (short) (words.get( ptr.substr.wordIndex ).length() - 1) );
					insert.sibling = new TrieNode( null , null , null);
					insert.sibling.substr = new Indexes( words.size() - 1 , (short)ptr.substr.startIndex , (short)words.get(words.size() - 1).length() );
					return;
				}
				
			}else{                      			 //Tree
				prefix = words.get(ptr.substr.wordIndex);
				
				//Condition 1: When we have more than 1 shared char, but not more than our prefix
				if ( getAmtShared( original, prefix ) > shared.length() && getAmtShared( original, prefix ) < prefix.length() ) {
					TrieNode temp = ptr.firstChild;
					TrieNode insert = new TrieNode(null, temp, null);
					insert.substr = new Indexes( ptr.substr.wordIndex , ptr.substr.startIndex , ptr.substr.endIndex );
					ptr.substr.endIndex = (short)(getAmtShared( original, prefix ) - 1);
					ptr.firstChild = insert;
					insert.sibling = new TrieNode( null , null , null );
					insert.sibling.substr = new Indexes( words.size() - 1 , ptr.substr.endIndex , (short) ((short) words.get( words.size() - 1 ).length()-1)  );
					return;
				}
				
				//Condition 2: When our insertWord has the same amount of chars as the prefix, it should be a child.
				else if ( getAmtShared( original, prefix ) == prefix.length() ) {
					theNewInsertWord( original , ptr );
					return;
				}
			}
			
			ptr = ptr.sibling;
			if (ptr != null) {
				ptr2 = ptr;
			}
		}
		
		//if you get this far, then word needs to be inserted as a child of the root (put as sibling of the last pointer)
		TrieNode insert = new TrieNode( null, null, null );
		insert.substr = new Indexes(   words.size() - 1  , (short)shared.length(), (short)words.get( words.size()-1).length());
		ptr2.sibling = insert;
		
	}

		
	

	//Compare and see if the the prefix has more shared chars than the current tokenString
	private boolean checkPrefix (String prefix, String sharedChars, String word) {
		int counter = sharedChars.length();					//keep track of how many matched chars there are.
		boolean moreInPrefix = false;
		
		for (int i = 0; i < word.length(); i++) {
			if (prefix.charAt(i) == word.charAt(i)) {
				counter--;
			}
		}
		
		//Time to check the counter.
		if (counter < 0) {
			moreInPrefix = true;
		}
		
		else {
			moreInPrefix = false;
		}
		
		return moreInPrefix;
	}
	
	//Counts how many chars we have that are similar.
	private int getAmtShared (String word, String compareWord) {
		int counter = 0;
		
		for( int i = 0; i < word.length() && i < compareWord.length(); i++ ){
			if (word.charAt(i) == compareWord.charAt(i)) {
				counter++;
			}
			
			else {
				break;
			}
		}
		
		//return the amount of shared chars.
		return counter;
	}
	
	private String getIndexesString (Indexes indexes, ArrayList<String> words){
		//Take the wordIndex from indexes and find the word.
		String word = words.get( indexes.wordIndex );
		
		//Now, we need to take the substring of this (if any)
		word = word.substring(indexes.startIndex, indexes.endIndex);
		
		return word;
	}

	private String getOriginalWord (String word) {
		for (int i = 0; i < word.length(); i++) {
			if (word.charAt(i) == '+') {
				word = word.substring(0, i);
			}
		}
		
		
		return word;
	}
	/**
	 * Given a string prefix, returns its "completion list", i.e. all the words in the trie
	 * that start with this prefix. For instance, if the tree had the words bear, bull, stock, and bell,
	 * the completion list for prefix "b" would be bear, bull, and bell; for prefix "be" would be
	 * bear and bell; and for prefix "bell" would be bell. (The last example shows that a prefix can be
	 * an entire word.) The order of returned words DOES NOT MATTER. So, if the list contains bear and
	 * bell, the returned list can be either [bear,bell] or [bell,bear]
	 * 
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all words in tree that start with the prefix, order of words in list does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public ArrayList<String> completionList(String prefix) {
		/** COMPLETE THIS METHOD **/
		//First, create the list in which we will be storing the names of the completed lists.
		ArrayList<String> list = new ArrayList<String>();
		
		//Create a pointer to be able to compare the nodes.
		TrieNode ptr = root.firstChild;
		String word = "";
		
		
		//Check to see if there are any nodes in the loop at all.
		if (ptr == null) {
			return list;
		} else {
			//Go through the loop. comparing to see if any prefixes are a match.
			while (ptr != null){
				if (ptr.firstChild == null) {
					if( getAmtShared( prefix , words.get( ptr.substr.wordIndex )) ==  words.get( ptr.substr.wordIndex ).length()){
						list.add( words.get( ptr.substr.wordIndex ) );
					}
					
				}else{
					list.addAll( theNewCompletionList( prefix , ptr ) );
					
					
				}
				
				ptr = ptr.sibling;
			}
		}
		
		return list;
	}
	
	private ArrayList<String> theNewCompletionList (String prefix, TrieNode newRoot) {
		ArrayList<String> list = new ArrayList<String>();
		TrieNode ptr = newRoot.firstChild;
		
		while (ptr != null){
				if (ptr.firstChild == null) {
					if( getAmtShared( prefix , words.get( ptr.substr.wordIndex )) ==  words.get( ptr.substr.wordIndex ).length()){
						list.add( words.get( ptr.substr.wordIndex ) );
					}
					
				}else{
					list.addAll( theNewCompletionList( prefix , ptr ) );
					
					
				}
				
				ptr = ptr.sibling;
		}
	
		return list;
	}
	/*
	 * This method inputs an index and the master list of words, and then outputs a string
	 * with that index.
	 */
	private String getString(Indexes indexes, ArrayList<String> words){
		String word = words.get( indexes.wordIndex );					//This is our target word.
		
		//Create the substring (if any)
		word = word.substring( indexes.startIndex, indexes.endIndex );
		
		return word;
	}
	
	public void print() {
		print(root, 1, words);
	}
	
	private static void print(TrieNode root, int indent, ArrayList<String> words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			System.out.println("      " + words.get(root.substr.wordIndex));
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		System.out.println("(" + root.substr + ")");
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }
