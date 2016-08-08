package scheduler;

import java.util.ArrayList;

import org.graphstream.graph.Node;
import org.graphstream.graph.Graph;

public class BranchAndBound {
	
	private Schedule s;
	private Graph g;
	
	/**
	 * Class takes the valid schedule as input and uses other functions to compute it - called by other classes
	 * @param g
	 * @param bottom
	 */
	public void branchAndBound(Graph g, Schedule schedule){
		s = schedule;
		traverseSchedule(g);
	}
	
	/**
	 * This class traverses the graph from the bottom of the valid schedule
	 * @param g
	 * @param currentNode
	 */
	private void traverseSchedule(Graph g){
		for (int i = s.size(); i > -1; i--) {
			comparison(s.getNode(i));
		}
	}
	
	
	/**
	 * compare children of parent, to see which if worth going down
	 */
	private void comparison(Node currentNode){
		
		
		
		// if currentNode has more then one child (i.e. a child not in valid schedule)
		if (currentNode.getOutDegree() > 1) {

			// for (each child){
				// compare children with currentNode
			
				// if child < currentNode
					// go down path
					Node child = null;
					int childPath = checkChildPath(currentNode, child);
					if (childPath != -1) {
						smallerChildPath();
					}
				
				// else IGNORE
			//}
			changeSchedulerPath();
		}
		else return;
		
	}
	
	/**
	 * Checks if this path is more optimal then the one chosen in valid schedule
	 */
	private int checkChildPath(Node currentNode, Node child){
		// traverse down child USING SEARCHING TREE?? and see if path is better to take
		// calculate all weights
		return -1; // return -1 if bad, actual value if it's better then currentNode
	}
	
	/**
	 * 
	 */
	private void smallerChildPath(){
		// add children and their weights to this as a potential path
	}
	
	
	/**
	 * 
	 */
	private void changeSchedulerPath(){
		
	}
	
}
