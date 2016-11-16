package poly;

import java.io.*;
import java.util.StringTokenizer;

/**
 * This class implements a term of a polynomial.
 * 
 * @author runb-cs112
 *
 */
class Term {
	/**
	 * Coefficient of term.
	 */
	public float coeff;
	
	/**
	 * Degree of term.
	 */
	public int degree;
	
	/**
	 * Initializes an instance with given coefficient and degree.
	 * 
	 * @param coeff Coefficient
	 * @param degree Degree
	 */
	public Term(float coeff, int degree) {
		this.coeff = coeff;
		this.degree = degree;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		return other != null &&
		other instanceof Term &&
		coeff == ((Term)other).coeff &&
		degree == ((Term)other).degree;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (degree == 0) {
			return coeff + "";
		} else if (degree == 1) {
			return coeff + "x";
		} else {
			return coeff + "x^" + degree;
		}
	}
}

/**
 * This class implements a linked list node that contains a Term instance.
 * 
 * @author runb-cs112
 *
 */
class Node {
	
	/**
	 * Term instance. 
	 */
	Term term;
	
	/**
	 * Next node in linked list. 
	 */
	Node next;
	
	/**
	 * Initializes this node with a term with given coefficient and degree,
	 * pointing to the given next node.
	 * 
	 * @param coeff Coefficient of term
	 * @param degree Degree of term
	 * @param next Next node
	 */
	public Node(float coeff, int degree, Node next) {
		term = new Term(coeff, degree);
		this.next = next;
	}
}

/**
 * This class implements a polynomial.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Pointer to the front of the linked list that stores the polynomial. 
	 */ 
	Node poly;
	
	/** 
	 * Initializes this polynomial to empty, i.e. there are no terms.
	 *
	 */
	public Polynomial() {
		poly = null;
	}
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param br BufferedReader from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 */
	public Polynomial(BufferedReader br) throws IOException {
		String line;
		StringTokenizer tokenizer;
		float coeff;
		int degree;
		
		poly = null;
		
		while ((line = br.readLine()) != null) {
			tokenizer = new StringTokenizer(line);
			coeff = Float.parseFloat(tokenizer.nextToken());
			degree = Integer.parseInt(tokenizer.nextToken());
			poly = new Node(coeff, degree, poly);
		}
	}
	
	
	/**
	 * Returns the polynomial obtained by adding the given polynomial p
	 * to this polynomial - DOES NOT change this polynomial
	 * 
	 * @param p Polynomial to be added
	 * @return A new polynomial which is the sum of this polynomial and p.
	 */
	public Polynomial add(Polynomial p) {
		
		// Create a new linked list w/ blank node to start off.
		Polynomial ans = new Polynomial();
		ans.poly = new Node (0, 0, null);
		
		// Renaming nodes, then create a new node to input the result to.
		Node curr = this.poly;
		Node addPoly = p.poly;
		Node term;
		
		//Put stuff into the first node, and add it to the new linked list.
		while (curr != null){
			term = new Node(curr.term.coeff, curr.term.degree, null);
			ans.addNode(term);
			curr = curr.next;
		}
		
		// Do the same for the other poly list we're trying to add.
		while (addPoly != null){
			term = new Node(addPoly.term.coeff, addPoly.term.degree, null);
			ans.addNode(term);
			addPoly = addPoly.next;
		}
		
		return ans;
	}
	
	/** This method was created to take in nodes from other methods. This method was created to just add nodes together
	 * 	of the same degree or sort them in order when they are received.  **/
	public void addNode(Node n){
	
		Node ptr = this.poly;	//Use a pointer to see the linked list.
		
		// Exception: If the given node has a higher degree than the very first node of the linked list
		if (n.term.degree < ptr.term.degree){
			n.next = ptr;
			this.poly = n;
			return;
		}
		
		//Go through the list in attempt to find a matching node with the same degree.
		while (ptr != null){

			//If you have two of the same degree, add them.
			if (ptr.term.degree == n.term.degree){
				ptr.term.coeff += n.term.coeff;
				return;
			}

			//If none are found, put it last.
			else if (ptr.next == null){
				ptr.next = n;
				return;
			}
			
			//If the node given has a higher degree than the answer poly
			else if (ptr.next.term.degree > n.term.degree) {
				n.next = ptr.next;
				ptr.next = n;
				return;
			}
			
			ptr = ptr.next;
		}
		
	}
	
	/**
	 * Returns the polynomial obtained by multiplying the given polynomial p
	 * with this polynomial - DOES NOT change this polynomial
	 * 
	 * @param p Polynomial with which this polynomial is to be multiplied
	 * @return A new polynomial which is the product of this polynomial and p.
	 */
	public Polynomial multiply(Polynomial p) {

		// Create a new linked list
		Polynomial ans = new Polynomial();
		ans.poly = new Node (0, 0, null);
		
		
		// Renaming for clarity.
		Node curr = this.poly;
		
		// Declaring Nodes for clarity
		Node multPoly;
		Node term;
		
		while (curr != null){
			multPoly = p.poly; //Resets the pointer to the beginning to run through the second while loop again.
			
			//This while loop does FOIL for the polynomials, put this into a new node and call the addNode func
			while (multPoly != null) {
				term = new Node (curr.term.coeff * multPoly.term.coeff, curr.term.degree + multPoly.term.degree, null);
				ans.addNode(term);
				
				multPoly = multPoly.next; //Do this for every single term.
			}
			
			curr = curr.next; //Repeat again for second term in the original polynomial.
		}
		
		return ans;
	}
	
	/**
	 * Evaluates this polynomial at the given value of x
	 * 
	 * @param x Value at which this polynomial is to be evaluated
	 * @return Value of this polynomial at x
	 */
	public float evaluate(float x) {
		
		//Create a float to keep track of the current evaluation.
		float ans = 0;
		
		//We're going to need the node.
		Node func = this.poly;
		
		//Go through the whole linked list until the last pointer.
		//find your float^degree and then multiply by coefficient.
		while (func != null){
			float constant = func.term.coeff;	//Grab the coefficient of the node
			float exponent = func.term.degree;	//Grab the exponent
			
			//Computations
			float total = constant * (float)(Math.pow(x, exponent));
			ans += total;
			
			func = func.next;					//Go to next node.
		}
		
		return ans;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String retval;
		
		if (poly == null) {
			return "0";
		} else {
			retval = poly.term.toString();
			for (Node current = poly.next ;
			current != null ;
			current = current.next) {
				retval = current.term.toString() + " + " + retval;
			}
			return retval;
		}
	}
}
