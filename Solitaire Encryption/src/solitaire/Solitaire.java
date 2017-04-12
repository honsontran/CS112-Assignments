package solitaire;

import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

/**
 * This class implements a simplified version of Bruce Schneier's Solitaire Encryption algorithm.
 * 
 * @author RU NB CS112
 */
public class Solitaire {
	
	/**
	 * Circular linked list that is the deck of cards for encryption
	 */
	CardNode deckRear;
	
	/**
	 * Makes a shuffled deck of cards for encryption. The deck is stored in a circular
	 * linked list, whose last node is pointed to by the field deckRear
	 */
	public void makeDeck() {
		// start with an array of 1..28 for easy shuffling
		int[] cardValues = new int[28];
		// assign values from 1 to 28
		for (int i=0; i < cardValues.length; i++) {
			cardValues[i] = i+1;
		}
		
		// shuffle the cards
		Random randgen = new Random();
 	        for (int i = 0; i < cardValues.length; i++) {
	            int other = randgen.nextInt(28);
	            int temp = cardValues[i];
	            cardValues[i] = cardValues[other];
	            cardValues[other] = temp;
	        }
	     
	    // create a circular linked list from this deck and make deckRear point to its last node
	    CardNode cn = new CardNode();
	    cn.cardValue = cardValues[0];
	    cn.next = cn;
	    deckRear = cn;
	    for (int i=1; i < cardValues.length; i++) {
	    	cn = new CardNode();
	    	cn.cardValue = cardValues[i];
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
	    }
	}
	
	/**
	 * Makes a circular linked list deck out of values read from scanner.
	 */
	public void makeDeck(Scanner scanner) 
	throws IOException {
		CardNode cn = null;
		if (scanner.hasNextInt()) {
			cn = new CardNode();
		    cn.cardValue = scanner.nextInt();
		    cn.next = cn;
		    deckRear = cn;
		}
		while (scanner.hasNextInt()) {
			cn = new CardNode();
	    	cn.cardValue = scanner.nextInt();
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
		}
	}
	
	/**
	 * Implements Step 1 - Joker A - on the deck.
	 */
	void jokerA() {
		//System.out.println("Before: ");
		//printList(deckRear);
		
		boolean firstCard = false;
		// Case 1: Check the very first card and see if it is 27. Swap if it is.
		if (deckRear.next.cardValue == 27) {
			int temp = deckRear.next.next.cardValue;
			deckRear.next.cardValue = temp;
			deckRear.next.next.cardValue = 27;
			firstCard = true;
		}
		
		if (!firstCard) {
			// Start a for loop to search through the circular linked list.
			// Case 2: This takes care of normal cases and the 27 being the last card.
			for (CardNode jokerA = deckRear.next; jokerA != deckRear; jokerA = jokerA.next) {
				//Find the 27, and then all you have to do is switch the values with the 
				//one in front.
				if (jokerA.next.cardValue == 27) {
					int temp = jokerA.next.next.cardValue;
					//System.out.println("the temp is: " + temp);
					jokerA.next.cardValue = temp;
					jokerA.next.next.cardValue = 27;
					break;
				}
			}
		}		
		
		//System.out.println("After: ");
		//printList(deckRear);
	}
	
	/**
	 * Implements Step 2 - Joker B - on the deck.
	 */
	void jokerB() {
		//System.out.println("Before: ");
		//printList(deckRear);
		
		boolean firstCard = false;
		// Case 1: Check the very first card and see if it is 28. Swap if it is.
		if (deckRear.next.cardValue == 28) {
			int temp1 = deckRear.next.next.cardValue;
			int temp2 = deckRear.next.next.next.cardValue;
			//System.out.println("temp1: " + temp1 + " and " + "temp2: " + temp2);
			deckRear.next.cardValue = temp1;
			deckRear.next.next.cardValue = temp2;
			deckRear.next.next.next.cardValue = 28;
			firstCard = true;
		}
		
		if (!firstCard) {
			// Start a for loop to search through the circular linked list.
			// Case 2: This takes care of normal cases and the 27 being the last card.
			for (CardNode jokerB = deckRear.next; jokerB != deckRear; jokerB = jokerB.next) {
				//Find the 28, and then all you have to do is switch the values with the 
				//one in front.
				if (jokerB.next.cardValue == 28) {
					int temp1 = jokerB.next.next.cardValue;
					int temp2 = jokerB.next.next.next.cardValue;
					//System.out.println("temp1: " + temp1 + " and " + "temp2: " + temp2);
					jokerB.next.cardValue = temp1;
					jokerB.next.next.cardValue = temp2;
					jokerB.next.next.next.cardValue = 28;
					break;
				}
			}
		}
		
		//System.out.println("After: ");
		//printList(deckRear);
	}
	
	/**
	 * Implements Step 3 - Triple Cut - on the deck.
	 */
	void tripleCut() {
		//We need to create partitions of the deck for what needs to be split.
		CardNode beforePtr = deckRear.next, beforePtrRear = null, afterPtr = null;
		CardNode jokerA = deckRear.next, jokerB = null;
		boolean beforeValues = true, afterValues = true;
		
		//Check and see if there are any cards before 27 || 28 at all
		if (beforePtr.cardValue == 27 || beforePtr.cardValue == 28) {
			beforeValues = false;
		}
		
		if (beforeValues) {
			//Create a circular linked list for values before 27.
			for (CardNode ptr = deckRear.next; ptr != deckRear; ptr = ptr.next) {
				//System.out.println(ptr.cardValue);
				// Have this pointer go through to find last card before 27.
				if (ptr.next.cardValue == 27 || ptr.next.cardValue == 28) { //If the next card is 27
					jokerA = ptr.next;			//keep track of the rest of the linked list.
					beforePtrRear = ptr;
					beforePtrRear.next = beforePtr;	// keep this create the new sub-linked list (circular).
					break;
				}
			}
		}
		
		//System.out.println("before values is working");
		
		/*
		 * Note: we can use ptr = jokerA for the following for loop because jokerA is set
		 * to deckRear.next initially. It will change accordingly if there are values
		 * before it in the above if statement.
		 */
		//Starting from the ptr JokerA, we can now see how many cards are in between 27 and 28.
		for (CardNode ptr = jokerA; ptr != deckRear; ptr = ptr.next) {
			//System.out.println(ptr.cardValue);
			if (ptr.next.cardValue == 27 || ptr.next.cardValue == 28) {
				jokerB = ptr.next;					
				//printList(jokerA);
			}
		}		
		
		//System.out.println("dearRear Value: " + deckRear.cardValue);	
		//System.out.println("jokerB Value: " + jokerB.cardValue);
		
		//Before assigning the afterPtr, check if /2728 is the last card in the deck.
		if (jokerB != deckRear) {
			afterPtr = jokerB.next;
			jokerB.next = jokerA;
			afterValues = true;
			//printList(jokerB);
			//System.out.println(jokerB.next.cardValue);
		}
				
		//If 28 is the last card in the deck.
		else {
			jokerB.next = jokerA;
			afterValues = false;
			//printList(jokerB);
		}
		
		//Now we have to start creating the last sublist (afterPtr), but only if there are values after 27/28.
		if (afterValues) {
			for (CardNode ptr = afterPtr; ptr != deckRear; ptr = ptr.next) {
				if (ptr.next == deckRear) {
					deckRear.next = afterPtr;
				}
			}
		}
		
		//Now that we have our sublists, we can commence cutting.
		
		/* Case 1: Normal case where the cut is in the middle of the deck.
		 * 		Before: 1, 2, 3, 27--28, 4, 5, 6
		 * 		After:  4, 5, 6, 27--28, 1, 2, 3
		 */
		if (afterValues && beforeValues) {
			deckRear.next = jokerA;
			jokerB.next = beforePtr;
			beforePtrRear.next = afterPtr;
			deckRear = beforePtrRear;
		}
		
		/* Case 2: If there are no beforeValues
		 * 		Before: 27--28, 1, 2, 3
		 * 		After:  1, 2, 3, 27--28
		 */
		else if (!beforeValues) {
			deckRear.next = jokerA;
			jokerB.next = afterPtr;
			deckRear = jokerB;				//simply repointing deckRear to the actual rear after cut.
		}
		
		/* Case 3: If there are no afterValues
		 * 		Before: 1, 2, 3, 27--28
		 * 		After:  27--28, 1, 2, 3
		 */
		else if (!afterValues) {
			deckRear.next = beforePtr;
			beforePtrRear.next = jokerA; 		
			deckRear = beforePtrRear;		//simply repointing deckRear to the actual rear after cut.
		}
		
		//System.out.println("Result: ");
		//printList(deckRear);

	}
	
	/**
	 * Implements Step 4 - Count Cut - on the deck.
	 */
	void countCut() {		
		//Similar to the Triple Cut, we'll need to set pointers and just rearrange the linked list.
		CardNode frontCutPtr = deckRear.next, rearCutPtr, frontPtr, backPtr = null;	//Ptrs for the cut.
		CardNode ptr = deckRear.next;		//Generic pointer to access the circular linked list.
		int count = deckRear.cardValue;
		
		//1) determine the count cut. If the value is 28, make it 27 instead.
		if (count == 28) {
			count = 27;
		}
		
		//System.out.println("countCut: Step 1 pass.");
		
		//2) Loop into the deck and make the cut.
		for (int i = 1; i < count; i++) {
			ptr = ptr.next;
		}
		//System.out.println("countCut: Step 2 pass.");
		
		//3) After finding the cut, pointers can now be set.
		rearCutPtr = ptr;
		// rearCutPtr.next = frontCutPtr;
		frontPtr = ptr.next;
		
		//System.out.println("countCut: Step 3 pass.");
		//4) Now, begin finding the new front of the deck.
		while (ptr!= deckRear) {
			if (ptr.next == deckRear) {
				backPtr = ptr;
				break;
			}
			ptr = ptr.next;
		}
		
		//System.out.println("countCut: Step 4 pass.");
		
		//5) Commence cutting.
		
		backPtr.next = frontCutPtr;
		rearCutPtr.next = deckRear;
		deckRear.next = frontPtr;
		
		//printList(deckRear);
	}
	
	/**
	 * Gets a key. Calls the four steps - Joker A, Joker B, Triple Cut, Count Cut, then
	 * counts down based on the value of the first card and extracts the next card value 
	 * as key. But if that value is 27 or 28, repeats the whole process (Joker A through Count Cut)
	 * on the latest (current) deck, until a value less than or equal to 26 is found, which is then returned.
	 * 
	 * @return Key between 1 and 26
	 */
	int getKey() {

		//Shuffle the deck
		jokerA();
		jokerB();
		tripleCut();
		countCut();		

		//System.out.println("Shuffled deck: ");
		//printList(deckRear);
		
		int key;
		CardNode ptr = deckRear;
		//1) Look at the value of the first card.
		int counter = deckRear.next.cardValue;
		
		if (counter == 28) {
			counter = 27;
		}
		
		//System.out.println("The value of the counter is: " + counter);
		//System.out.println("Our current ptr is: " + ptr.cardValue);
		
		//2)Loop through the deck 
		for (int i = 0; i < counter; i++) {
			ptr = ptr.next;
		}
		
		//3) Key Cases
		key = ptr.next.cardValue;

		//3a) Look at the next card's value, if it's 27 or 28, then you have to reshuffle.
		if (key == 27 || key == 28) {
			getKey();
		}
		
		//System.out.println("After: " );
		//printList(deckRear);
		//System.out.println("After deckRear: " + deckRear.cardValue);
		
		//3b) If the card value is not 27 or 28, then that is your key.
		return key;

	}
	
	/**
	 * Utility method that prints a circular linked list, given its rear pointer
	 * 
	 * @param rear Rear pointer
	 */
	private static void printList(CardNode rear) {
		if (rear == null) { 
			return;
		}
		System.out.print(rear.next.cardValue);
		CardNode ptr = rear.next;
		do {
			ptr = ptr.next;
			System.out.print("," + ptr.cardValue);
		} while (ptr != rear);
		System.out.println("\n");
	}

	/**
	 * Encrypts a message, ignores all characters except upper case letters
	 * 
	 * @param message Message to be encrypted
	 * @return Encrypted message, a sequence of upper case letters only
	 */
	public String encrypt(String message) {	
		int keyNum = 0;
		int keyStream;
		String modMsg = "";
		String encryptMsg = "";
		
		//1) Take out all punctuation and convert all letters to capitals.
		for (int i = 0; i < message.length(); i++) {
			if (Character.isLetter( message.charAt(i) )) {
				modMsg += Character.toUpperCase( message.charAt(i) );	//convert to uppercase
				//System.out.println(modMsg); //working
			}
		}
		
		//2) Now, encrypt it.
		for (int i = 0; i < modMsg.length(); i++) {
			//System.out.println("getKey Value: " + getKey());
			//System.out.println("Letter Value: " + (modMsg.charAt(i)-64) );
			
			keyStream = getKey();
			//System.out.println("keyStream Value: " + keyStream );
			
			keyNum = modMsg.charAt(i) - 64 + keyStream;
			
			if (keyNum > 26) {
				keyNum -= 26;
			}
			
			//System.out.println("keyNum: " + keyNum);
			
			keyNum += 64;
			char temp = (char) keyNum;
			//System.out.println("Encrypted Letter: " + temp);
			//System.out.println();
			encryptMsg += (char) keyNum;
		}
		
		//System.out.println("encrypt for loop pass from line 327-328");
		

		
		//modMsg += (char)keyNum;
		
		return encryptMsg;
	}
	
	/**
	 * Decrypts a message, which consists of upper case letters only
	 * 
	 * @param message Message to be decrypted
	 * @return Decrypted message, a sequence of upper case letters only
	 */
	public String decrypt(String message) {	
		String msg = "";
		
		//1) Go through each char, and then convert it to a number, apply the decryption, and spit out a char.
		for (int i = 0; i < message.length(); i++) {
			
			//System.out.println("Our target letter is #: " + target);
			
			int decryptValue = (int) message.charAt(i) - 64 - getKey();
			
			// If a code value is equal or smaller than the corresponding decryption key, it will always
			// be less than or equal to 0.
			if (decryptValue <= 0) {
				decryptValue += 26;
			}
						
			//System.out.println("Decrypt Value: " + decryptValue);
			//System.out.println();
			
			msg += (char)(decryptValue + 64);
		}
		
		return msg;
	}
}
