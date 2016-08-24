package scheduler;
import main.Main;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class BranchAndBound {

	private Schedule currentSchedule;
	private Graph g;
	private Node nodeToBeRemoved = null;
	private Node original=null;

	/**
	 * Constructor to instantiate a new Schedule
	 * @param s : Schedule
	 * @param g : Graph
	 */
	public BranchAndBound(Schedule s, Graph g){
		this.currentSchedule = new Schedule(s.schedule, s.procLengths, s.scheduleLength);
		this.g = g;
	}

	/**
	 * The function that should initialise the branch and bound algorithm, and calls Branch() to do a call down the children
	 * While branch is giving a false (ie no better solution), loop the following:
	 * - Take away last node from schedule, 
	 * --- if this is the first node of the schedule, then check if any other root nodes that have not been processed as the first node in the schedule.
	 * - If all root nodes have being processed, then we have the optimal solution hopefully.
	 */
	public void branchAndBoundAlgorithm() {	
		// removes the bottom node from the schedule
		original=nodeToBeRemoved ;
		nodeToBeRemoved = currentSchedule.schedule.get(currentSchedule.schedule.size() - 1);
		currentSchedule.removeNode(currentSchedule.schedule.size() - 1);
		updateRemoveLengthChanges(currentSchedule, nodeToBeRemoved);

		//Starts the branch and bound
		while(Branch(new Schedule(currentSchedule.schedule, currentSchedule.procLengths, currentSchedule.scheduleLength)) == false){ // checks branch is always false
			// gets current bottom node of the schedule
			original=nodeToBeRemoved ;
			nodeToBeRemoved = currentSchedule.schedule.get(currentSchedule.schedule.size() - 1);
			
			if (Main.vis){ // changes the colour to yellow briefly to show it was looked at
				Main.update.updateColor(nodeToBeRemoved.getId(),"yellow");
				Main.update.updateColor(original.getId(),"gray");
			}

			currentSchedule.removeNode(currentSchedule.schedule.size() - 1); // removes the node
			updateRemoveLengthChanges(currentSchedule, nodeToBeRemoved);

			if(currentSchedule.schedule.size() == 1){// checks if it's empty - then returns (back to branch and bound algorithm)
				if (Main.vis) {Main.update.updateColor(nodeToBeRemoved.getId(),"gray");} // changes the color to grey
				return;
			}
		}
	}

	/**
	 * Recursive function Branch, calleed by BranchandboundAlgorithm
	 * Loops through all the nodes of the graph and checks if the path is better (optomised) then the current best schedule
	 * finds all processable nodes and for each processable children nodes, check how much the schedule increases when trying to add them to each of the processors,
	 * if the schedule time after adding to that processor is less than the current best schedule time (currentBestSchedule.scheduleLength) then insert node into the current schedule
	 * and recursively call Branch with the current schedule (this should be done for each of the nodes/processors that produce a lower schedule length)
	 * if the path is larger then return false, if no more nodes then return true.
	 * 
	 * @return false
	 */
	public boolean Branch(Schedule branchingSchedule) {

		boolean hasInserted = false;

		for (Node n : g) {// loops through all the nodes
			if (!branchingSchedule.schedule.contains(n)) {// checks new schedule doesn't contain it
				
				boolean isProcessable = ScheduleHelper.isProcessable(n,branchingSchedule);
				if (isProcessable) { // checks if the nodes dependencies have been processed

					for (int i = 0; i < branchingSchedule.procLengths.length; i++) { // check all the available processors

						int timeToWait = ScheduleHelper.checkChildNode(n, branchingSchedule, i); //calculates the time of the node added to the schedule

						if (timeToWait > -1) { // if it's better, goes down child again
							hasInserted = true;
							ScheduleHelper.insertNodeToSchedule(n, branchingSchedule, i, timeToWait);

							// Recursive
							Branch(branchingSchedule);
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
	 * This method update the length of the schedule after a node is removed
	 * @param s : schedule
	 * @param removeNode : node that's been removed
	 */
	public void updateRemoveLengthChanges(Schedule s, Node removeNode){
		int updatedScheduleLength = 0;

		for (int i = s.schedule.size() -1 ; i > -1; i--) { //loop through whole schedule from back till you find the last one on the same processor
			Node n = s.schedule.get(i);

			//if the node you want to remove is on the same processor as the node in the schedule
			int processedOn = (int)(n.getAttribute("Processor"));
			if (processedOn == (int)(removeNode.getAttribute("Processor"))) { 

				//update the processor lengths
				s.procLengths[processedOn] = ScheduleHelper.getNodeWeight(g, n.getIndex()) + (int)(n.getAttribute("Start"));
				s.scheduleLength = s.findScheduleLength(); //make new schedule length
				updatedScheduleLength = 1;
				break;
			}
		}

		if (updatedScheduleLength == 0) {
			s.procLengths[(int)removeNode.getAttribute("Processor")] = 0;
			s.scheduleLength = s.findScheduleLength();
		}
	}

}
