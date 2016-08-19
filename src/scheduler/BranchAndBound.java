package scheduler;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.ArrayList;

public class BranchAndBound {
	Schedule currentSchedule;
	Graph g;
	
	public BranchAndBound(Schedule s, Graph g){
		this.currentSchedule = s;
		this.g = g;
	}
	/**
	 * The function that should initialise the branch and bound algorithm, While branch is giving a false (ie no better solution), loop the following:
	 * Take away last node from schedule, if this is the first node of the schedule, then check if any other root nodes that have not been processed as the first node in the schedule.
 	 * If all root nodes have being processed, then we have the optimal solution hopefully.
	 * @param schedule
	 * @param g
	 * @return
	 */
	public void branchAndBoundAlgorithm() {
		//make a list of root nodes
		ArrayList<Node> rootNodes = new ArrayList<Node>();
		ArrayList<Integer> rootNodeIDs = ScheduleHelper.findRootNodes(g);
		for(int i : rootNodeIDs){
			if(g.getNode(i).getInDegree() == 0){
				rootNodes.add(g.getNode(i));
			}
		}
		//Start the branch and bound
		while(Branch() == false){
			Node nodeToBeRemoved = currentSchedule.schedule.get(currentSchedule.schedule.size() - 1);
			currentSchedule.removeNode(currentSchedule.schedule.size() - 1);
			if(currentSchedule.schedule.isEmpty()){
				//If schedule is empty, then what we removed is a root node
				//Remove it from the list of root nodes
				rootNodes.remove(nodeToBeRemoved);
				if(rootNodes.isEmpty()){
					//No more root nodes to process, the search is complete. the current best will be stored in ScheduleHelper
					return;
				}
				//Otherwise, put a new root node in and start branch and bound again. (This part will be different for the parallel version)
				currentSchedule.addNode(rootNodes.get(0), 0, 0);
			}
		}
	}
	
	/**
	 * Recursive function, find all processable nodes, for each processable children nodes, check how much the schedule increases when trying to add them to each of the processors,
	 * if the schedule time after adding to that processor is less than the current best schedule time (currentBestSchedule.scheduleLength) then insert node into the current schedule
	 * and recursively call Branch with the current schedule (this should be done for each of the nodes/processors that produce a lower schedule length)
	 * if the path is larger then return false, if no more nodes then return true.
	 * 
	 * @return
	 */
	public static boolean Branch() {
		
		
		return false;
	}

	
}
