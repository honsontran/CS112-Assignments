package apps;

import java.io.*;
import java.util.*;

import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;                
    
	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   
	
	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;
    
    /**
     * String containing all delimiters (characters other than variables and constants), 
     * to be used with StringTokenizer
     */
    public static final String delims = " \t*+-/()[]";
    
    /**
     * Initializes this Expression object with an input expression. Sets all other
     * fields to null.
     * 
     * @param expr Expression
     */
    public Expression(String expr) {
        this.expr = expr;
    }

    /**
     * Populates the scalars and arrays lists with symbols for scalar and array
     * variables in the expression. For every variable, a SINGLE symbol is created and stored,
     * even if it appears more than once in the expression.
     * At this time, values for all variables are set to
     * zero - they will be loaded from a file in the loadSymbolValues method.
     */
    public void buildSymbols() {
		/** COMPLETE THIS METHOD **/
	    scalars = new ArrayList<ScalarSymbol>();
	    arrays = new ArrayList<ArraySymbol>();
		String var;
		int z = 0;
		
		//Check and see if the expression is valid first.
		if ( !validExpr(expr) ){
			System.out.println("There is an error in the formatting the expression!");
			return;
		}
		
		for (int i = 0; i < expr.length(); i++){
			//Condition for Scalar.
			if(Character.isLetter(expr.charAt(i))){ 		//if char at the index is a letter
				var = getVariable(expr.substring(i));       //return the var 
				
				//add these variables in the
				i += var.length();
				if (i >= expr.length()){
					for( int y = 0; y < scalars.size(); y++){
						if( var.equals( scalars.get(y).name )){
							z = 1;
							break;
						}
					}
					if( z == 1 ){
						z = 0;
						break;
					}
					scalars.add(new ScalarSymbol(var));
					break;
				}

			    //if the letter is part of the array
			    if(expr.charAt(i) == '['){
			    	for( int y = 0; y < arrays.size(); y++){
						if( var.equals( arrays.get(y).name )){
							z = 1;
							break;
						}
					}
			    	if( z == 1 ){
			    		z = 0;
						continue;
					}
	                arrays.add(new ArraySymbol(var));
		    	}
		    	
		    	else {
		    		for( int y = 0; y < scalars.size(); y++){
						if( var.equals( scalars.get(y).name )){
							z = 1;
							break;
						}
					}
		    		if( z == 1 ){
		    			z = 0;
						continue;
					}
		    		scalars.add(new ScalarSymbol(var));
		    	}
			}
			
			
		}
    
		//This method gets the variable and returns it as a string to put it in the array of variables.
    private String getVariable(String subStr) {
        String variable = "";
        int i = 0;
        while (Character.isLetter(subStr.charAt(i))) {
           variable += subStr.charAt(i);
           i++;
           
           if (i >= subStr.length()){
        	   break;
           }
        }
        return variable;
    }
    
    //This method checks if the char is a number, bracket/parenthesis, or an operator
    private boolean validChar (char c) {
    	String validChars = " \t*+-/()[]";				//String of chars that are allowed.
    	boolean valid = true;
    	
    	//Check if it's a digit. Return true if it is.
		if (Character.isDigit(c)) {
			 return true;
		}
		
		else if (Character.isLetter(c)) {
			return true;
		}
		
		else {
			for (int i = 0; i < validChars.length(); i++) {			//Check if the char is in the valid chars.
	    		if (c == validChars.charAt(i)) {
	    			valid = true;
	    			break;
	    		}
	    		
	    		else {
	    			valid = false;
	    		}
	    	}
		}
		return valid;
    }
    
    //This method checks if the expression itself is correctly formatted.
    private boolean validExpr (String expr) {
		int numParen = 0;
		int numBracket = 0;
		boolean valid = true;

		//Check if each char is valid. We can use the validChar method for this.
		for (int i = 0; i < expr.length(); i++) {
			if ( !validChar(expr.charAt(i)) ) {
				valid = false;
				return valid;
			}
		}
	
		//Check if all brackets/parenthesis are formatted correctly.
		for (int i = 0; i < expr.length(); i++){
			if ( expr.charAt(i) == '(' ){
				numParen++;
			}
			
			else if ( expr.charAt(i) == ')' ) {
				numParen--;
			}
			
			if ( expr.charAt(i) == '[' ) {
				numBracket++;
			}
			
			else if ( expr.charAt(i) == ']' ){
				numBracket--;
			}
			
			//Terminate the loop early if there are already more closing than opening brackets.
			if (numParen < 0 || numBracket < 0) {
				System.out.println("Error in format!");
				valid = false;
			}
		}
		
		if (numParen > 0 || numBracket > 0) {
			System.out.println("Error in format!");
			valid = false;
		}
		
		else {
			valid = true;
		}
		
		return valid;	
    }
    
    /**
     * Loads values for symbols in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     */
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbol
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
    
    
    /**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions.
     * 
     * @return Result of evaluation
     */
    public float evaluate() {
    		/** COMPLETE THIS METHOD **/
    	//Declare variables.
    	//We need to store the stack and have a stack to work on the current operation.
    	Stack<Character> opStack = new Stack<Character>();
    	Stack<Character> currentOp = new Stack<Character>();
    	
    	//We need to store our numbers in an array.
    	ArrayList<Float> numbers = new ArrayList<Float>();
    	
    	//Other variables for tokenizing.
    	int indexOpen = 0, indexClose = 0, numOpen = 0, numClose = 0;	//Used to create substring of content inside multiple ()
    	String newExpr;
    	String temp;
    	float ans = 0;
    	int temp2 = 0;
    	
    	//Math logic variables
    	char operation;
    	int first, second;
    	
    	//remove all spaces and tabs from expression. The more universally formatted, the better.
    	expr = expr.replaceAll(" ", "");
    	expr = expr.replaceAll("\t", "");
    	expr += " ";
    	
    	//Find parenthesis in the method to create the substring to recurse on.
    	for (int i = 0; i < expr.length(); i++) {
    		//if [ is found, find the corresponding ].
    		if ( expr.charAt(i) == '[') {
    			indexOpen = i;
    			i++;
    			
    			//Search through the string until )
    			for (; i < expr.length(); i++){
    	    		if ( expr.charAt(i) == ']'){
    	    			numClose++;
    	    		}
    	    		
    	    		if (expr.charAt(i) == '['){
    	    			numOpen++;
    	    		}
    				
    	    		if ( expr.charAt(i) == ']' && numOpen < numClose){
    	    			indexClose = i;
    	    			numOpen = 0;
    	    			numClose = 0;
    	    			break;
    				}
    	    	}
    			
            	newExpr = expr;
            	expr = expr.substring(indexOpen+1, indexClose);
            	temp = newExpr.substring(indexOpen, indexClose+1);
            		
            	//Create the substring. indexOpen+1 because first index is inclusive, end index is exclusive.
            	temp2 = temp.length();
            	temp = "" + (int)evaluate();
            	
            	/Concatenate the string.
            	expr = newExpr.substring(0, indexOpen+1) + temp
            		+ newExpr.substring(indexClose, newExpr.length());

            	//Update the new length of the string in the for loop.
            	i-=temp2 - temp.length();
    		}
    	}
    	
    	//Same thing as before applies.
    	for (int i = 0; i < expr.length(); i++) {
    		//if ( is found, find the corresponding ).
    		if ( expr.charAt(i) == '(') {
    			indexOpen = i;
    			i++;
    			
    			//Search through the string until )
    			for (; i < expr.length(); i++){
    	    		if ( expr.charAt(i) == ')'){
    	    			numClose++;
    	    		}
    	    		
    	    		if (expr.charAt(i) == '('){
    	    			numOpen++;
    	    		}
    				
    	    		if ( expr.charAt(i) == ')' && numOpen < numClose){
    	    			indexClose = i;
    	    			numOpen = 0;
    	    			numClose = 0;
    	    			break;
    				}
    	    	}
    			
            	newExpr = expr;
            	expr = expr.substring(indexOpen+1, indexClose);
            	temp = newExpr.substring(indexOpen, indexClose+1);
            	
            	//Temp is updating the new string
            	temp2 = temp.length();
            	temp = "" + evaluate();
            	
            	 //Concatenate the string.
            	expr = newExpr.substring(0, indexOpen) + temp
            		+ newExpr.substring(indexClose+1, newExpr.length());
            	
            	//Adjust i so that it updates in the for loop.
            	i -= temp2 - temp.length();
    		}
    	}

    	//Then, get your stack of operations.
    	opStack = getStackOps(expr);
    	
    	//Also, get the ArrayList of numbers.
    	numbers = getNumbers(expr);
    	
    	//Conditional if there is one integer, nothing else.
    	if ( opStack.isEmpty() ){
    		return numbers.get(0);
    	}
    	
    	//Once you get them, do math with this algorithm.
    	//For every number in the array, do this.
    	int test = opStack.size();
    	for (int x = 0; x < test; x++){
    		//pop the operation out of the stack.
    		operation = opStack.pop();
    		
    		//Put this operation into the currentOps stack in queue for math.
    		currentOp.push(operation);
    		
    		//If the operation has priority, proceed with math.
    		if ( hasPriority(operation, opStack ) ) {
    			first = currentOp.size()-1;
    			second = currentOp.size();
    			
    			ans = doMath(numbers.get(first) , numbers.get(second), operation);
    			
    			//Update the array with the new number.
    			//numbers = updateArray(numbers, ans, first);
    			numbers.set(first, ans);
    			numbers.remove(second);
    			
    			//Pop out the operator that we finished doing math with.
    			currentOp.pop();
    		}
    		
    		//If the operation doesn't have priority
    		else {
    			//pop the priority operator into the currentOp stack.

        		operation = opStack.pop();
            	currentOp.push(operation);

    			//Indicate where the first and second places in the array are.
    			first = currentOp.size()-1;
    			second = currentOp.size();
    			
    			//Do math.
    			ans = doMath(numbers.get(first) , numbers.get(second), operation);
    			
    			//Update array and pop out the used operator.
    			numbers.set(first, ans);
    			numbers.remove(second);
    			currentOp.pop();
    			opStack.push( currentOp.pop() );
    		}
    	return ans;
    }
    
    //This method finds out what the operator is, and then does the math between two numbers.
    private float doMath (float first, float second, char operator) {
    	float ans = 0;
    	
    	switch (operator) {
	    	case '+':
	    		ans = first + second;
	    		break;
	    	case '-':
	    		ans = first - second;
	    		break;
	    	case '*':
	    		ans = first * second;
	    		break;
	    	case '/':
	    		ans = first / second;
	    		break;
    	}
    	return ans;
     }
    
    /*This method tests to see if operations in the stack need to be done first before the current op.
     * In other words, this checks for PEMDAS for the algorithm.
    */
    private boolean hasPriority(char c, Stack<Character> opStack) {
    	boolean priority = true;
    	
    	/* The only priority of operations we need is when the next operation in the stack is a * or /
    	 * and the current operation is either a + or a -.
    	 */
    	
    	
    	//If the next operation in the stack is * or /, and our operation is + or -, priority is false.
    	if ( ( !opStack.isEmpty() ) && (c == '+' || c == '-') &&
    			(opStack.peek() == '*' || opStack.peek() == '/') ) {
    		priority = false;
    	}
    	
    	return priority;
    }
    
    //Go through the string and create the stack of operations to use to do math.
    //Algorithm requires the stack to be reversed.
    private Stack<Character> getStackOps (String expr){
    	Stack<Character> operations = new Stack<Character>();
    	Stack<Character> reverse = new Stack<Character>();
    	
    	//If the string has any of these operators, add them to the stack.
    	for (int i = 0; i < expr.length(); i++) {
    		if (expr.charAt(i) == '+' || expr.charAt(i) == '-'|| expr.charAt(i) == '*'|| expr.charAt(i) == '/'){
    			operations.push( expr.charAt(i) );
    		}
    	}
    	
    	//Now, reverse the stack.
    	while ( !operations.isEmpty() ) {
     		reverse.push( operations.pop() );
    	}
    	
    	return reverse;
    }
    
    //Get all the numbers and put them into an array
    public ArrayList<Float> getNumbers (String expr) {
    	ArrayList<Float> numbers = new ArrayList<Float>();
    	String num = "";
    	
    	//Search through the string and get numbers (if more than one digit place)
    	for (int i = 0; i < expr.length(); i++) {
    		if (Character.isDigit(expr.charAt(i)) ){
    			for (; i < expr.length(); i++) {
    				//In case it's a float with a decimal point.
    				if ( Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.' ){
    	    			num += expr.charAt(i);
    	    		}
    	    		
    	    		else{									// Once we found the number, parse & add to array.
    	    			numbers.add( Float.parseFloat(num) );
    	    			num = "";		
    	    			break; //Reset for the next number.
    	    		}
    			}
    		}
    		
    		//Same thing applies for characters, but a lot more complicated.
    		else if ( Character.isLetter(expr.charAt(i)) ){
    			for (; i < expr.length(); i++) {
    				if ( Character.isLetter(expr.charAt(i)) ) {
    	    			num += expr.charAt(i);
    	    		}
    	    		
    				//Conditionals for both array and a scalar.
    	    		else {							
    	    			if ( expr.charAt(i) == '[' ) {
    	    				i++;
    	    				for (int x = 0; x < arrays.size(); x++) {
    	    					if ( num.equals(arrays.get(x).name) ){
    	    						num = "";
    	    						for (; i < expr.length(); i++) {
    	    		    				if ( Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.' ){
    	    		    	    			num += expr.charAt(i);
    	    		    	    		}
    	    		    	    		
    	    		    	    		else {	    	    		    	    			
    	    		    	    			numbers.add( (float)arrays.get(x).values[ (int)Float.parseFloat(num) ] );
    	    		    	    			num = "";		
    	    		    	    			break; //Reset for the next number.
    	    		    	    		}
    	    		    			}
    	    						break;
    	    					}
    	    				}
    	    				break;
    	    			}
    	    			
    	    			else {
    	    				for (int x = 0; x < scalars.size(); x++) {
    	    					if ( num.equals(scalars.get(x).name) ){
    	    						numbers.add( (float)scalars.get(x).value );
    	    						num = "";
    	    						break;
    	    					}
    	    				}
    	    				break;
    	    			}
    	    		}
    			}
    		}
    	}
    	
    	return numbers;
    }
    
    //Tells you whether or not the specific char is a math operator.
    private boolean validMath (char c) {
    	String validOperators = "+-/*";
    	boolean valid = true;
    	
    	for (int i = 0; i < validOperators.length(); i++) {
    		if (c == validOperators.charAt(i)) {
    			valid = true;
    		}
    		
    		else {
    			valid = false;
    		}
    	}
    	
    	return valid;
    }

    /**
     * Utility method, prints the symbols in the scalars list
     */
    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    /**
     * Utility method, prints the symbols in the arrays list
     */
    public void printArrays() {
    		for (ArraySymbol as: arrays) {
    			System.out.println(as);
    		}
    }

}
