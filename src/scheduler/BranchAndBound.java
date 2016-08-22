package scheduler;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.ArrayList;

public class BranchAndBound {
	
	private Schedule currentSchedule;
	private Graph g;
	private Node nodeToBeRemoved = null;
	
	/**
	 * Constructor to instantiate a new Schedule
	 * @param s
	 * @param g
	 */
	public BranchAndBound(Schedule s, Graph g){
		this.currentSchedule = new Schedule(s.schedule, s.procLengths, s.scheduleLength);
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
		while(Branch(new Schedule(currentSchedule.schedule, currentSchedule.procLengths, currentSchedule.scheduleLength)) == false){
			
			nodeToBeRemoved = currentSchedule.schedule.get(currentSchedule.schedule.size() - 1);
			currentSchedule.removeNode(currentSchedule.schedule.size() - 1);
			updateRemoveLengthChanges(currentSchedule, nodeToBeRemoved);
			
			if(currentSchedule.schedule.isEmpty()){
				//If schedule is empty, then what we removed is a root node
				// delete the root node from the root nodes
				rootNodes.remove(nodeToBeRemoved);
				if(rootNodes.isEmpty()){
					//No more root nodes to process, the search is complete. the current best will be stored in ScheduleHelper
					return;
				}

				currentSchedule.addNode(rootNodes.get(0), 0, 0); //gets a new root node to start branch and bound with (dif to parallel)
				currentSchedule.updateProcessorLength(0, (int)Double.parseDouble(rootNodes.get(0).getAttribute("Weight").toString()));
			}
		}
	}
	
	/**
	 * Recursive function, finds all processable nodes, for each processable children nodes, check how much the schedule increases when trying to add them to each of the processors,
	 * if the schedule time after adding to that processor is less than the current best schedule time (currentBestSchedule.scheduleLength) then insert node into the current schedule
	 * and recursively call Branch with the current schedule (this should be done for each of the nodes/processors that produce a lower schedule length)
	 * if the path is larger then return false, if no more nodes then return true.
	 * 
	 * @return false
	 */
	public boolean Branch(Schedule branchingSchedule) {
		
		boolean hasInserted = false;

		for (Node n : g) {// loops through all the nodes
			if (!branchingSchedule.schedule.contains(n)) {// new schedule doesn't contain it
				// check all the node that is not in the schedule
				boolean isProcessable = ScheduleHelper.isProcessable(n,branchingSchedule);
				if (isProcessable) { // if it is processable
					
					for (int i = 0; i < branchingSchedule.procLengths.length; i++) { // check all the available processor
						
                        int timeToWait = ScheduleHelper.checkChildNode(n, branchingSchedule, i);

                        if (timeToWait > -1) {
							
                        	hasInserted = true;
							ScheduleHelper.insertNodeToSchedule(n, branchingSchedule, i, timeToWait);
							
							// Recursive
							Branch(new Schedule(branchingSchedule.schedule, branchingSchedule.procLengths, branchingSchedule.scheduleLength));
							branchingSchedule.removeNode(branchingSchedule.schedule.size() - 1);
							
							updateRemoveLengthChanges(branchingSchedule, n);
						}
					}
				}
			}
		}
		
		if (branchingSchedule.schedule.size() == g.getNodeCount()) { // no more children
			ScheduleHelper.foundNewBestSolution(branchingSchedule, g);
		}
		
		if (!hasInserted){ //If nothing was inserted, then go back up the tree.
			return false;
		}
		
		return false; //Return false to prevent while loop from exiting
	}
	
	/**
	 * 
	 * @param s
	 * @param removeNode
	 */
	public void updateRemoveLengthChanges(Schedule s, Node removeNode){
		int updatedScheduleLength = 0;
		
		for (int i = s.schedule.size() -1 ; i > -1; i--) { //loop through whole schedule from back till you find the last one on the same processor
			Node n = s.schedule.get(i);
			
			//if the node you want to remove is on the same processor as the node in the schedule
			int processedOn = (int)Double.parseDouble(n.getAttribute("Processor").toString());
			if (processedOn == (int)Double.parseDouble(removeNode.getAttribute("Processor").toString())) { 
				
				//update the processor lengths
				s.procLengths[processedOn] = ScheduleHelper.getNodeWeight(g, n.getIndex()) + (int)Double.parseDouble(n.getAttribute("Start").toString());
				s.scheduleLength = s.findScheduleLength(); //make new schedule length
				updatedScheduleLength = 1;
				break;
			}
		}
		
		if (updatedScheduleLength == 0) { 
			s.procLengths[(int)Double.parseDouble(removeNode.getAttribute("Processor").toString())] = 0;
			s.scheduleLength = s.findScheduleLength();
		}
	}
	
}
