package scheduler;

import org.graphstream.graph.Node;
import org.graphstream.graph.Graph;

public class BranchAndBound {

	/**
	 * Class takes the valid schedule as input and uses other functions to compute it - called by other classes
	 * @param g
	 * @param bottom
	 */
	public void branchAndBound(Graph g, Node bottom){
		
		traversegraph(g, bottom);
	}
	
	/**
	 * This class traverses the graph from the bottom of the valid schedule
	 * @param g
	 * @param currentNode
	 */
	private void traversegraph(Graph g, Node currentNode){
		int i = 0;
		while (i==0) {
			comparison(currentNode);
			i++;
		}
	}
	
	
	/**
	 * compare children of parent, to see which if worth going down
	 */
	private void comparison(Node currentNode){
		
		// if currentNode has more then one child (i.e. a child not in valid schedule)
		
			// Node a = currentNode.child1; (in valid schedule)
			// Node b = currentNode.child2; (not in valid schedule)
		
			// if child 2 < child 1
				// go down path
				checkChildPath();
			
			// else IGNORE
		
	}
	
	/**
	 * Checks if this path is more optimal then the one chosen in valid schedule
	 */
	private void checkChildPath(){
		// traverse down and see if path is better to take
		//if yes
			changeSchedulerPath();
		//if not do nothing, as will go back to traversegraph()
		
	}
	
	/**
	 * 
	 */
	private void changeSchedulerPath(){
		
	}
	
}
