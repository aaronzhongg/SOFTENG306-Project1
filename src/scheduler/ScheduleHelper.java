package scheduler;

import java.util.ArrayList;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import scheduler.Greedy.QueueItem;
import scheduler.Schedule;

import org.graphstream.graph.Edge;
/**
 * This class provides methods that the Schedule data structure can use
 *
 */
public class ScheduleHelper {

	/**
	 * Finds all the root nodes of the input graph
	 * @param g : graph
	 * @returns all the root nodes
	 */
	public static ArrayList<Integer> findRootNodes(Graph g) {
		ArrayList<Integer> rootNodes = new ArrayList<Integer>();
		
		int i = 0;
		for (Node n:g) {
			if (n.getInDegree() == 0) {
				rootNodes.add(i);
			}
			i++;
		}
		return rootNodes;
	}

	/**
	 * Get's the weight of the input node
	 * @param g : graph
	 * @param nodeIndex : of node you want the weight of
	 * @return : weight of nodeIndex
	 */
	public static int getNodeWeight(Graph g, int nodeIndex){
		return (int)Double.parseDouble(g.getNode(nodeIndex).getAttribute("Weight").toString());
	}


	/**
	 * After a node has been processed, this method is used to return all new nodes that can be processed
	 * @param g
	 * @param nodeIndex
	 * @return
	 */
	public static ArrayList<Integer> processableNodes(Graph g, int nodeIndex) {

		ArrayList<Integer> processableNodes = new ArrayList<Integer>();
		boolean nodeProcessable;
		
		Iterable<Edge> ite = g.getNode(nodeIndex).getEachLeavingEdge(); // Gets all the leaving edges of the node just processed

		// Gets all the child nodes of the node that was just processed, for each child node, check whether all it's parent nodes have been processed
		for (Edge e: ite) {
			Node n = e.getNode1();
			nodeProcessable = true;

			// Check all of child node's parent nodes have been processed
			Iterable<Edge> childIte = g.getNode(n.getId()).getEachEnteringEdge();
			for (Edge childEdge: childIte) {
				Node parentNode = childEdge.getNode0();

				if ((int)Double.parseDouble(parentNode.getAttribute("processorID").toString()) == -1) { //checks if parent processed
					nodeProcessable = false;
					break;
				}
			}

			if (nodeProcessable == true) {
				processableNodes.add(n.getIndex());
			}
		}
		return processableNodes;
	}

	/**
	 * Returns the cost of putting the queue item into the processor
	 * @param schedule
	 * @param q : item in the queue
	 * @param g : graph
	 * @return
	 */
	public static int[] scheduleNode(Schedule schedule, QueueItem q, Graph g) {

		int minimumProcLength;
		int procWaitTime = 0;
		int nodeWeight = getNodeWeight(g, q.nodeIndex);
		
		ArrayList<Integer> parentNodeCosts = new ArrayList<Integer>(); // This stores the cost of putting the queue item into the specified pid when coming from each parent node
		ArrayList<Node> parentNodes = new ArrayList<Node>(); // Stores the parent node queue item comes from
		

		if (g.getNode(q.nodeIndex).getInDegree() != 0) { //if it's not a root
			int parentNodeFinishedProcessing = 0;
			//need to find when the longest parent node finished processing
			for (Edge e : g.getNode(q.nodeIndex).getEachEnteringEdge()) {
				Node parentNode = e.getNode0();
				int tempValue = (int)Double.parseDouble(parentNode.getAttribute("Start").toString()) + getNodeWeight(g, parentNode.getIndex());
				if(tempValue > parentNodeFinishedProcessing){
					parentNodeFinishedProcessing = tempValue;
				}
			}
			//Get the post-processed processorLength of the queueitem from each of the parent nodes
			for (Edge e : g.getNode(q.nodeIndex).getEachEnteringEdge()) {
				Node parentNode = e.getNode0();
				int parentProcessor = (int)Double.parseDouble(parentNode.getAttribute("processorID").toString());
				int edgeWeight = (int)Double.parseDouble(e.getAttribute("Weight").toString());	
				
				//if parent node was processed on the same processor the edge weight is 0
				if (q.processorID == parentProcessor) {	
					edgeWeight = 0;
				}
				
				//if the parent node end time plus the edge is smaller than the longest parent node end time, dont add edge weight on
				int currentParentNodeFinish = (int)Double.parseDouble(parentNode.getAttribute("Start").toString()) + getNodeWeight(g, parentNode.getIndex());
				if (parentNodeFinishedProcessing > currentParentNodeFinish + edgeWeight){
					edgeWeight = 0;
				}					

				//if the parent node finished processing longer than the weight of the edge to the child then can add automatically to the processor
				if (schedule.procLengths[q.processorID] - parentNodeFinishedProcessing >= edgeWeight){
					
					parentNodeCosts.add(schedule.procLengths[q.processorID] + nodeWeight);
					parentNodes.add(parentNode);
					
				} else {	//find out how long need to wait before can add to processor
					
					//time left to wait
					int timeToWait = edgeWeight - (schedule.procLengths[q.processorID] - currentParentNodeFinish);

					if (timeToWait < 0) {
						timeToWait = 0;
					}
					parentNodeCosts.add(schedule.procLengths[q.processorID] + nodeWeight + timeToWait);
					parentNodes.add(parentNode);
				}
				
			}

			minimumProcLength = parentNodeCosts.get(0);
			
			int temp = 0;
			for(int i = 0; i < parentNodeCosts.size(); i++) {
				int pNodeCost = parentNodeCosts.get(i);
				if (pNodeCost > minimumProcLength) {
					minimumProcLength = pNodeCost;
					temp = i;
				}
			}
			
			Node p = parentNodes.get(temp);
			
			procWaitTime = minimumProcLength - nodeWeight - schedule.procLengths[q.processorID];
			
		} else { // if it's a root node, length is the node weight plus the processor length of the processor
			minimumProcLength = getNodeWeight(g, q.nodeIndex) + schedule.procLengths[q.processorID];
		}
		
		int[] newProcLengthAndTimeToWait = {minimumProcLength, procWaitTime};
		return newProcLengthAndTimeToWait;

	}
}
