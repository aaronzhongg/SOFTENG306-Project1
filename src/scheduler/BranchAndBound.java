package scheduler;
import org.graphstream.graph.Graph;

public class BranchAndBound {
	
	/**
	 * The function that should initialise the branch and bound algorithm, While branch is giving a false (ie no better solution), loop the following:
	 * Take away last node from schedule, if this is the first node of the schedule, then check if any other root nodes that have not been processed as the first node in the schedule.
 	 * If all root nodes have being processed, then we have the optimal solution hopefully.
	 * @param schedule
	 * @param g
	 * @return
	 */
	public void branchAndBoundAlgorithm(Schedule schedule, Graph g) {
		
	}
	
	public static boolean Branch(Schedule schedule, Graph g) {
		
		return true;
	}

	
}
