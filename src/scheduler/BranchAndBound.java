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
			rootNodes.add(g.getNode(i));
		}
		//Start the branch and bound
		while(Branch(currentSchedule,g) == false){
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
	public static boolean Branch(Schedule currentSchedule, Graph g) {
	
		boolean hasProcessable = false;

		for (Node n : g) {
			if (!currentSchedule.schedule.contains(n)) {
				// check all the node that is not in the schedule
				boolean isProcessable = ScheduleHelper.isProcessable(n,
						currentSchedule);
				if (isProcessable) {
					// if it is processasble
					hasProcessable = true;
					for (int i = 0; i < Main.processorInput; i++) {
						// check all the available processor
						int isBetter = ScheduleHelper.checkChildNode(n,
								currentSchedule, i);
						if (isBetter == 1) {
							// if it is a better solution
							// calculate the waiting time for the node
							// get current last node from scheduler as parent
							Node parent = currentSchedule.schedule
									.get(currentSchedule.schedule.size() - 1);
							if (!parent.getAttribute("processer")
									.equals(i + "")) {
								// different processor
								Edge parentToChild = parent.getEdgeToward(n);
								int procWaitTime = (int) Double
										.parseDouble(parentToChild
												.getAttribute("Weight")
												.toString());
								ScheduleHelper.insertNodeToSchedule(n,
										currentSchedule, i, procWaitTime);
							} else {
								// same processor no waiting time
								ScheduleHelper.insertNodeToSchedule(n,
										currentSchedule, i, 0);
							}
							// Recursive
							Branch(currentSchedule, g);

						}
					}
				}
			}

		}

		if (!hasProcessable) {
			// no more children
			ScheduleHelper.foundNewBestSolution(currentSchedule);
			return false;
		} else {
			return true;
		}

	
	}

	
}
