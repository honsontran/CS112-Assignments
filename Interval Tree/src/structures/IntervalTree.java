package structures;

import java.util.ArrayList;

/**
 * Encapsulates an interval tree.
 * 
 * @author runb-cs112
 */
public class IntervalTree {
	
	/**
	 * The root of the interval tree
	 */
	IntervalTreeNode root;
	
	/**
	 * Constructs entire interval tree from set of input intervals. Constructing the tree
	 * means building the interval tree structure and mapping the intervals to the nodes.
	 * 
	 * @param intervals Array list of intervals for which the tree is constructed
	 */
	public IntervalTree(ArrayList<Interval> intervals) {
		
		// make a copy of intervals to use for right sorting
		ArrayList<Interval> intervalsRight = new ArrayList<Interval>(intervals.size());
		for (Interval iv : intervals) {
			intervalsRight.add(iv);
		}
		
		// rename input intervals for left sorting
		ArrayList<Interval> intervalsLeft = intervals;
		
		// sort intervals on left and right end points
		sortIntervals(intervalsLeft, 'l');
		sortIntervals(intervalsRight,'r');
		
		// get sorted list of end points without duplicates
		ArrayList<Integer> sortedEndPoints = 
							getSortedEndPoints(intervalsLeft, intervalsRight);
		
		// build the tree nodes
		root = buildTreeNodes(sortedEndPoints);
		
		// map intervals to the tree nodes
		mapIntervalsToTree(intervalsLeft, intervalsRight);
	}
	
	/**
	 * Returns the root of this interval tree.
	 * 
	 * @return Root of interval tree.
	 */
	public IntervalTreeNode getRoot() {
		return root;
	}
	
	/**
	 * Sorts a set of intervals in place, according to left or right endpoints.  
	 * At the end of the method, the parameter array list is a sorted list. 
	 * 
	 * @param intervals Array list of intervals to be sorted.
	 * @param lr If 'l', then sort is on left endpoints; if 'r', sort is on right endpoints
	 */
	public static ArrayList<Interval> sortIntervals(ArrayList<Interval> intervals, char lr) {
		
		//Check if you are trying to sort using left or right.
		if (lr == 'l') {
			// We're going to need a left list.
			ArrayList<Interval> LSort = new ArrayList<Interval>( intervals.size() );
			
			// Go through the interval list and order the left intervals in order.
			for (Interval i : intervals) {
				LSort.add(i);
			}
			
			// Insertion sort used to sort the intervals
			for (int i = 1; i < LSort.size(); i++) {
				Interval target = LSort.get(i);
				int counter = i; 			// used to determine position into the arrayList
				
				// As long as the number before the current target is bigger, you have to switch.
				while ( counter > 0 && LSort.get(counter-1).leftEndPoint > target.leftEndPoint) {			
					LSort.set(counter, LSort.get(counter-1));
					counter--;
				}
				
				LSort.set(counter, target);
			}
			
			return LSort;
		}
		
		else if (lr == 'r') {
			// We're going to need a right list.
			ArrayList<Interval> RSort = new ArrayList<Interval>( intervals.size() );
			
			// Go through the interval list and order the right intervals in order.
			for (Interval i : intervals) {
				RSort.add(i);
			}
			
			// Insertion sort used to sort the intervals
			for (int i = 1; i < RSort.size(); i++) {
				Interval target = RSort.get(i);
				int counter = i; 			// used to determine position into the arrayList
				
				// As long as the number before the current target is bigger, you have to switch.
				while ( counter > 0 && RSort.get(counter-1).rightEndPoint > target.rightEndPoint) {			
					RSort.set(counter, RSort.get(counter-1));
					counter--;
				}
				RSort.set(counter, target);
			}
			
			return RSort;
		}
		
		return null;
	}
	
	/**
	 * Given a set of intervals (left sorted and right sorted), extracts the left and right end points,
	 * and returns a sorted list of the combined end points without duplicates.
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 * @return Sorted array list of all endpoints without duplicates
	 */
	public static ArrayList<Integer> getSortedEndPoints(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		ArrayList<Integer> results = new ArrayList<Integer>(10);		//capacity set to 10 to assume room for 10 intervals
		int leftDuplicate = leftSortedIntervals.get(0).leftEndPoint; //Set your duplicate first as a start of what to check first.
		int rightDuplicate = rightSortedIntervals.get(0).rightEndPoint;
		
		//System.out.println("leftSortedIntervals size: " + leftSortedIntervals.size());
		//System.out.println("Results size: " + results.size());
		
		leftSortedIntervals = sortIntervals(leftSortedIntervals, 'l');
		//System.out.println("LSort: ");
		//System.out.println(leftSortedIntervals.toString());
		
		rightSortedIntervals = sortIntervals(rightSortedIntervals, 'r');
		//System.out.println("RSort: ");
		//System.out.println(rightSortedIntervals.toString());
		
		//Add the first entry in.
		results.add( leftSortedIntervals.get(0).leftEndPoint );
		
		//Now, we will create for loops to run through both LSort taking advantage that it's sorted, checking each integer.
		for (int i = 0; i < leftSortedIntervals.size(); i++) {
			
			//System.out.println("Iteration " + i + ": ");
			if (leftSortedIntervals.get(i).leftEndPoint != leftDuplicate) {
				leftDuplicate = leftSortedIntervals.get(i).leftEndPoint;
				results.add( leftSortedIntervals.get(i).leftEndPoint );
			}
			
			//System.out.println( results.toString() );
			//System.out.println("-------------------------------------------------------------");
		}

		//Once they're all added, start to check duplicates in RSort.
		ArrayList<Integer> rightSortedIntegers = new ArrayList<Integer>();
		
		for (int j = 0; j < leftSortedIntervals.size(); j++) {
			
			//System.out.println("Iteration j: " + j);
			if (rightSortedIntervals.get(j).rightEndPoint != rightDuplicate) {
				rightDuplicate = rightSortedIntervals.get(j).rightEndPoint;
				rightSortedIntegers.add( rightSortedIntervals.get(j).rightEndPoint );
			}
			
			//System.out.println( rightSortedIntegers.toString() );
			//System.out.println("-------------------------------------------------------------");
		}
		
		// Now that we have duplicates taken out of rightSortedIntervals, we can use rightSortedIntegers
		// to add to results.
		int targetIndex;
		int target;
		for (int k = 0; k < rightSortedIntegers.size(); k++) {

			target = rightSortedIntegers.get(k);
			targetIndex = 0;
			
			//System.out.println("Looping through results with value: " + target);
			
			//Case 1: If the integer we are comparing is bigger than the biggest of results, just add it to the end.
			if (target > results.get( results.size()-1 )) {
				results.add(target);
			}
			
			// Case 2: If it's not bigger, then we have to find its placement.
			else {
				//Now take the first integer and check through results list for a duplicate or for correct index.
				for (int l = 0; l < results.size(); l++) {
	
					// Case 1: We found our duplicate.
					if (target == results.get(l)) {
						break;
					}
					
					// Case 2: If our target is smaller than the current integer, increment our target's index.
					else if (target > results.get(l)) {
						targetIndex++;
					}
					
					// Case 3: If our target is bigger than the current integer, we've found our index!
					else if (target < results.get(l)) {
						//System.out.println("Found Index: " + targetIndex);
						results.add(targetIndex, target);
						break;
					}
				}
			}
		}
		
		//System.out.println("Results: ");
		//System.out.println(results.toString());
		return results;
	}

	
	/**
	 * Builds the interval tree structure given a sorted array list of end points
	 * without duplicates.
	 * 
	 * @param endPoints Sorted array list of end points
	 * @return Root of the tree structure
	 */
	public static IntervalTreeNode buildTreeNodes(ArrayList<Integer> endPoints) {
		Queue<IntervalTreeNode> treeQueue = new Queue<IntervalTreeNode>();
		float splitValue;
		IntervalTreeNode root;
		//System.out.println("Here are the endPoints: " + endPoints.toString());
		
		// Step 5.
		//First start off by creating a node for each point in the ArrayList, and queuing it/
		for (int i = 0; i < endPoints.size(); i++) {
			IntervalTreeNode tree = new IntervalTreeNode(endPoints.get(i), endPoints.get(i),
					endPoints.get(i));
			
			//Place the node into a tree.
			tree.leftIntervals = new ArrayList<Interval>();
			tree.rightIntervals = new ArrayList<Interval>();
			
			//System.out.println("Node: " + tree.toString());
			//Queue the tree.
			treeQueue.enqueue(tree);
			
			//System.out.println("Current queue size: " + treeQueue.size());
			//System.out.println("----------------------------------------------------------------");
		}
		
		// Step 6.
		// Keep track of how many small trees are left (it's the same size as the queue)
		int treesLeft = treeQueue.size;
		int tempSize = treesLeft;
		
		while (treesLeft > 0) { 		// Repeat Step 6
			// If there is one tree left.
			if (treesLeft == 1) {
				root = treeQueue.dequeue();
				//System.out.println("There's only one node!" + root.toString());
				//System.out.println("---------------------------------------------------------------------");
				return root;
			}
			
			//Update the temp size to repeat again
			tempSize = treesLeft;
			
			while (tempSize > 1) {
				IntervalTreeNode T1 = treeQueue.dequeue();
				IntervalTreeNode T2 = treeQueue.dequeue();
				
				float v1 = T1.maxSplitValue;
				float v2 = T2.minSplitValue;
				
				//System.out.println("v1: " + v1);
				//System.out.println("v2: " + v2);
				
				// Calculate Split Value
				splitValue = (v1 + v2)/2;
				
				//System.out.println("SplitValue: " + splitValue);
				
				// Create a new node containing the split value, T1 as left child, and T2 as right.
				IntervalTreeNode newTree = new IntervalTreeNode(splitValue, 
						T1.minSplitValue, T2.maxSplitValue);
				
				// Create a new tree with this newTree node as the root, T1 as left child, and T2 as the right.
				newTree.leftIntervals = new ArrayList<Interval>();
				newTree.rightIntervals = new ArrayList<Interval>();
				
				newTree.leftChild = T1;
				newTree.rightChild = T2;
				
				// Enqueue newTree into the queue.
				treeQueue.enqueue(newTree);
				tempSize -= 2;
				
				//System.out.println("Creation of new node complete! " + newTree.toString());
				//System.out.println("------------------------------------------------------------------------");
			}
					
			// If the tempSize is 1, then dequeue and enqueue.
			if (tempSize == 1) {
				treeQueue.enqueue( treeQueue.dequeue() );
			}
			
			//Update the tree size.
			treesLeft = treeQueue.size();
			//System.out.println("Trees Left: " + treesLeft);
		}
		
		root = treeQueue.dequeue();
		//System.out.println(root.toString());
		return root;
	}
	
	/**
	 * Maps a set of intervals to the nodes of this interval tree. 
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 */
	public void mapIntervalsToTree(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		
		leftSortedIntervals = sortIntervals(leftSortedIntervals, 'l');
		rightSortedIntervals = sortIntervals(rightSortedIntervals, 'r');		
		
		for (int i = 0; i < leftSortedIntervals.size(); i++) {
			recursiveMap(leftSortedIntervals.get(i), root, 'l');
		}
		
		for (int i = 0; i < rightSortedIntervals.size(); i++) {
			recursiveMap(rightSortedIntervals.get(i), root, 'r');
		}
	}

	// Recursive search through the binary tree to find the correct mapping. Works in correlation with mapIntervalsToTree()
	private void recursiveMap (Interval currInt, IntervalTreeNode node, char lr) {
		
		// Base Case: The split value is inside the interval.
		if (currInt.contains(node.splitValue)) {

			// Add this to the left
			if (lr == 'l') {
				node.leftIntervals.add(currInt);
			}
			
			else if (lr == 'r') {
				node.rightIntervals.add(currInt);
			}
			
		}
		
		// Case 1: if the current integer is greater than the splitValue...
        else if (currInt.leftEndPoint > node.splitValue) {
			// Add all the intervals on the left branch (since we have a bigger interval,
			// so by default they're bigger than all the intervals on the left, and then
			// branch right in search of other intervals.
			recursiveMap(currInt, node.rightChild, 'l');
		}
		
		// Case 2: Vice versa.
		else if (currInt.leftEndPoint < node.splitValue) {
			recursiveMap(currInt, node.leftChild, 'r');
		}
		
	}
	
	/**
	 * Gets all intervals in this interval tree that intersect with a given interval.
	 * 
	 * @param q The query interval for which intersections are to be found
	 * @return Array list of all intersecting intervals; size is 0 if there are no intersections
	 */
	public ArrayList<Interval> findIntersectingIntervals(Interval q) {
		/*  Input: Interval tree T, query interval Iq
       	 *  Output: ResultList, a list of intervals from T that intersect Iq
         */
		return findIntersections( root, q );
	}
	
	private ArrayList<Interval> findIntersections(IntervalTreeNode root, Interval ptr){
		ArrayList<Interval> ResultList = new ArrayList<Interval>();		// Let ResultList be empty.
	
		// If R is a leaf, return empty list.
		if (root == null){
			return ResultList;
		}
		
		IntervalTreeNode R = root;						// Let R be the root node of T
		float splitValue = R.splitValue;				// Let SplitVal be the split value stored in R
		
		// Let Llist be the list of intervals stored in R that is sorted by left endpoint
		ArrayList<Interval> Llist = R.leftIntervals;
		// Let Rlist be the list of intervals stored in R that is sorted by right endpoint
		ArrayList<Interval> Rlist = R.rightIntervals;
		
		IntervalTreeNode Lsub = R.leftChild;			// Let Lsub be the left subtree of R
		IntervalTreeNode Rsub = R.rightChild;			// Let Rsub be the right subtree of R
		
		// If SplitVal falls within Iq then...
		if (ptr.contains(splitValue)) {						
			
			// Add all intervals in Llist to ResultList
			ResultList.addAll(Llist);	
			
			// Query Rsub and add the results to ResultList
			ResultList.addAll(findIntersections(Rsub, ptr));
			
			// Query Lsub and add the results to ResultList
			ResultList.addAll(findIntersections(Lsub, ptr));			
		}
		
		// else if SplitVal falls to the left of Iq then...
		else if (splitValue < ptr.leftEndPoint) {			
			int i = Rlist.size()-1;		// Let i be the size of Rlist
			
			// while (i >= 0 and the i-th interval in Rlist intersects Iq)
			while (i >= 0 && (Rlist.get(i).intersects(ptr))) {
				ResultList.add(Rlist.get(i));
				i--;
			}
			
			// Query Rsub and add the results to ResultList
			ResultList.addAll(findIntersections(Rsub, ptr));
		}
		
		// else if SplitVal falls to the right of Iq then...
		else if (splitValue > ptr.rightEndPoint) {			
			int i = 0;				// Let i be 0
			
			// while (i < the size of Llist and the i-th interval in Llist intersects Iq)
			while ( i < Llist.size() && Llist.get(i).intersects(ptr) ) {				
				
				// Add the ith interval to ResultList
				ResultList.add(Llist.get(i));						
				i++;
			}
			
			// Query Lsub and add the results to ResultList
			ResultList.addAll(findIntersections(Lsub, ptr));
		}
	
		return ResultList;
	}

}

