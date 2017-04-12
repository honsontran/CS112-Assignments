package search;

import java.io.*;
import java.util.*;

public class Driver 
{
	static Scanner sc = new Scanner(System.in);
	
	static String getOption() 
	{
		System.out.print("getKeyWord(): ");
		String response = sc.next();
		return response;
	}
	
	public static void main(String args[]) throws IOException
	{

		LittleSearchEngine engine=new LittleSearchEngine();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		//System.out.print("Enter document file name => ");
		String docsFile = "docs.txt";
		//System.out.print("Enter noise words file name => ");
		String noiseWordsFile= "noisewords.txt";
		engine.makeIndex(docsFile, noiseWordsFile);
		System.out.println(engine.keywordsIndex.toString());
		System.out.println(engine.top5search("deep", "world"));
		
		
		/*
		LittleSearchEngine engine = new LittleSearchEngine();
		String docsFile = "docs.txt";
		String noiseWordsFile = "noisewords.txt";
		engine.makeIndex(docsFile, noiseWordsFile);
		
		String test = "'because!!!!!";
		System.out.println(engine.newgetKeyword(test));
		*/
		
		//HashMap<String, Occurrence> result = engine.loadKeyWords(docsFile);
		//System.out.println(result.toString());
		
		/*//Integer ArrayList test.
		Occurrence test1 = new Occurrence ("A", 12);
		Occurrence test2 = new Occurrence ("A", 8);
		Occurrence test3 = new Occurrence ("A", 7);
		Occurrence test4 = new Occurrence ("A", 5);
		Occurrence test5 = new Occurrence ("A", 3);
		Occurrence test6 = new Occurrence ("A", 2);
		Occurrence test7 = new Occurrence ("A", 6);
		
		ArrayList<Occurrence> occs = new ArrayList<Occurrence>();
		
		occs.add(test1);
		occs.add(test2);
		occs.add(test3);
		occs.add(test4);
		occs.add(test5);
		occs.add(test6);
		occs.add(test7);
		
		ArrayList<Integer> result = engine.insertLastOccurrence(occs);
		
		System.out.println(result.toString());
		*/
	}
}