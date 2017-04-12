package apps;

import structures.*;

import java.io.*;
import java.util.*;

public class IntervalTreeDriver {
	
	static BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
	
	public static void main(String[] args) throws IOException {
		System.out.print("Enter intervals file name => ");
		String infile = keyboard.readLine();
		BufferedReader br = new BufferedReader(new FileReader(infile));
		ArrayList<Interval> intervals = readIntervals(br);
		System.out.println("Read the following intervals:");
		for (Interval interval: intervals) {
			System.out.println(interval);
		}
		IntervalTree tree = new IntervalTree(intervals);
		performQueries(tree);
	}
	
	static ArrayList<Interval> readIntervals(BufferedReader br) throws IOException  {
		String line;
		ArrayList<Interval> ret = new ArrayList<Interval>();
		
		while ((line = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line);
			Interval intrvl = new Interval(
					Integer.parseInt(st.nextToken()), 
					Integer.parseInt(st.nextToken()), st.nextToken());
			ret.add(intrvl);
		}
		return ret;
	}
	
	static void performQueries(IntervalTree tree) throws IOException {
		System.out.print("\nEnter an interval (e.g. 3 5) to intersect, quit to stop => ");
		String schedule = keyboard.readLine();
		while (!schedule.equals("quit")) {
			StringTokenizer st = new StringTokenizer(schedule);
			
			Interval intrvl = new Interval(Integer.parseInt(st.nextToken()),
					Integer.parseInt(st.nextToken()), "");
					
			ArrayList<Interval> intersects = tree.findIntersectingIntervals(intrvl);
			for (Interval interval: intersects) {
				System.out.println(interval);
			}
			
			System.out.print("\nEnter an interval (e.g. 3 5) to intersect, quit to stop => ");	    
			schedule = keyboard.readLine();
		}
	}

	
	/*public static void main(String[] args) throws IOException {
		ArrayList<Interval> list = new ArrayList<Interval>();
		
		Interval node1 = new Interval(1, 42, "test");
		Interval node2 = new Interval(6, 54, "test");
		Interval node3 = new Interval(3, 6, "test");
		Interval node4 = new Interval(3, 9, "test");
		Interval node5 = new Interval(2, 13, "test");
		Interval node6 = new Interval(14, 33, "test");
		Interval node7 = new Interval(18, 30, "test");
		Interval node8 = new Interval(20, 3, "test");
		Interval node9 = new Interval(13, 3, "test");
		
		list.add(node1);
		list.add(node2);
		list.add(node3);
		list.add(node4);
		list.add(node5);
		list.add(node6);
		list.add(node7);
		list.add(node8);
		list.add(node9);
		
		ArrayList<Interval> RSort = IntervalTree.sortIntervals(list, 'r');
		ArrayList<Interval> LSort = IntervalTree.sortIntervals(list, 'l');
		
		System.out.println("Displaying LSort:");
		System.out.println(LSort);

		System.out.println("Displaying RSort:");
		System.out.println(RSort);
		
		System.out.println("LSort Size: " + LSort.size());
		System.out.println("RSort Size: " + RSort.size());
		
		ArrayList<Integer> results = IntervalTree.getSortedEndPoints(LSort, RSort);
		
		System.out.println("Result: " + results.toString());
	}*/
}
