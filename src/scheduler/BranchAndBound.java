package scheduler;
import java.util.ArrayList;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

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
		//make a list of root nodes
		ArrayList<Node> rootNodes = new ArrayList<Node>();
		ArrayList<Integer> rootNodeIDs = ScheduleHelper.findRootNodes(g);
		for(int i : rootNodeIDs){
			if(g.getNode(i).getInDegree() == 0){
				rootNodes.add(g.getNode(i));
			}
		}
		//Start the branch and bound
		while(Branch(schedule, g) == false){
			Node nodeToBeRemoved = schedule.schedule.get(schedule.scheduleLength - 1);
			schedule.removeNode(schedule.scheduleLength - 1);
			if(schedule.schedule.isEmpty()){
				//If schedule is empty, then what we removed is a root node
				//Remove it from the list of root nodes
				rootNodes.remove(nodeToBeRemoved);
				if(rootNodes.isEmpty()){
					//No more root nodes to process, the search is complete. the current best will be stored in ScheduleHelper
					return;
				}
				//Otherwise, put a new root node in and start branch and bound again. (This part will be different for the parallel version
				schedule.addNode(rootNodes.get(0), 0, 0);
			}
		}
	}
	
	/**
	 * Recursive function, find all processable nodes, for each processable children nodes, check how much the schedule increases when trying to add them to each of the processors,
	 * if the schedule time after adding to that processor is less than the current best schedule time (currentBestSchedule.scheduleLength) then insert node into the current schedule
	 * and recursively call Branch with the current schedule (this should be done for each of the nodes/processors that produce a lower schedule length)
	 * if the path is larger then return false, if no more nodes then return true.
	 * 
	 * @param schedule
	 * @param g
	 * @return
	 */
	public static boolean Branch(Schedule schedule, Graph g) {
		
		
		return true;
	}

	
}
