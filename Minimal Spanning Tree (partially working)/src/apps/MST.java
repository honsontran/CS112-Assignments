package apps;

import structures.*;

import java.io.IOException;
import java.util.ArrayList;

public class MST {

	/**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static void main (String[] args) throws IOException {
		Graph file = new Graph("graph1.txt");
		PartialTreeList L = initialize(file);
		ArrayList<PartialTree.Arc> result = execute(L);
		
		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i).toString());
		}
	}
	public static PartialTreeList initialize(Graph graph) {
		//Create the Empty List
		PartialTreeList L = new PartialTreeList();
		
		//Go through each index in the graph
		for (int i = 0; i < graph.vertices.length; i++) {
			//Associate each vertex and neighbor with a tree
			Vertex v = graph.vertices[i];
			PartialTree T = new PartialTree(v);
			Vertex.Neighbor N = graph.vertices[i].neighbors;
			
			//Create a queue and put the arcs in. 
			MinHeap<PartialTree.Arc> P = T.getArcs();
			
			//Go through the neighbors and insert them into queue.
			for ( ; N != null; N = N.next) {
				PartialTree.Arc temp = new PartialTree.Arc(graph.vertices[i], N.vertex, N.weight);
				P.insert(temp);
				
				//Case: If the next vertex is the same as our root vertex, skip it.
				if (N.vertex == graph.vertices[i]) {
					N = N.next;
				}
			}
			
			//Add tree to the list.
			L.append(T);
			
		}
		
		return L;
	}

	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<PartialTree.Arc> execute(PartialTreeList ptlist) {
		/* COMPLETE THIS METHOD */
		
		//Create a list that will store the final tree.
		ArrayList<PartialTree.Arc> ans = new ArrayList<PartialTree.Arc>();
		
		int LSize = ptlist.size();			//Keeps track of how many trees we have to go through.
		
		//Go through each partial tree.
		for (int i = 0; i < LSize-1; i++) {
			
			//Pop out the first tree in the list for comparison.
			PartialTree PTX = ptlist.remove();
			
			//There's two possibilities.
			//Case 1: There are no more trees to combine in the list.
			if (PTX == null) {
				break;
			}
			
			//Case 2: There is a tree, and we must combine it.
			else {
				//Pop one out of the queue and then link them.
				MinHeap<PartialTree.Arc> PQX = PTX.getArcs();
				PartialTree.Arc priority = PQX.deleteMin();
				Vertex v1 = PTX.getRoot();
				Vertex v2 = priority.v2;
				
				if (v1 == v2 || v1 == v2.parent) {
					priority = PQX.deleteMin();
					v2 = priority.v2.parent;
				}

				//Updating everything.
				ans.add(priority);				//Add this to your resulting list.
				PartialTree PTY = ptlist.removeTreeContaining(v2); //remove after use.
				
				PTY.getRoot().parent = PTX.getRoot();
				PTY.merge(PTX);
				ptlist.append(PTY);
			}
		}
		
		return ans;
	}
}
