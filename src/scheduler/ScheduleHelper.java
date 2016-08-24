package scheduler;

import java.util.ArrayList;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import scheduler.Greedy.QueueItem;
import scheduler.Schedule;

import org.graphstream.graph.Edge;
import org.graphstream.graph.implementations.Graphs;
/**
 * This class provides methods that the Schedule data structure can use
 *
 */
public class ScheduleHelper {
	
	public static int[][] dependencyMatrix;
	public static Schedule currentBestSchedule;
	public static Graph bestGraph; //This is a cloned graph just for the currentBestSchedule

	/**
	 * This method should find all node dependencies and map them to an adjacency matrix.
	 * @param g the graph of nodes and edges
	 * @return a 2d int array of all edges between nodes
	 */
	public static void makeDependencyMatrix(Graph g){
		
		dependencyMatrix = new int[g.getNodeCount()][g.getNodeCount()];
		
		for(Edge e:g.getEachEdge()){
			int i = e.getNode0().getIndex();
			int j = e.getNode1().getIndex();
			dependencyMatrix[i][j] = 1;
		}
	}
	
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
	 * This is used for the Greedy algorithm. For the branch and bound algorithm use CheckChildNodes
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

				if ((int)Double.parseDouble(parentNode.getAttribute("Processor").toString()) == -1) { //checks if parent processed
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
				int parentProcessor = (int)Double.parseDouble(parentNode.getAttribute("Processor").toString());
				int edgeWeight = (int)Double.parseDouble(e.getAttribute("Weight").toString());	
				
				//if parent node was processed on the same processor the edge weight is 0
				if (q.Processor == parentProcessor) {	
					edgeWeight = 0;
				}
				
				//if the parent node end time plus the edge is smaller than the longest parent node end time, dont add edge weight on
				int currentParentNodeFinish = (int)Double.parseDouble(parentNode.getAttribute("Start").toString()) + getNodeWeight(g, parentNode.getIndex());
				if (parentNodeFinishedProcessing > currentParentNodeFinish + edgeWeight){
					edgeWeight = 0;
				}					

				//if the parent node finished processing longer than the weight of the edge to the child then can add automatically to the processor
				if (schedule.procLengths[q.Processor] - parentNodeFinishedProcessing >= edgeWeight){
					
					parentNodeCosts.add(schedule.procLengths[q.Processor] + nodeWeight);
					parentNodes.add(parentNode);
					
				} else {	//find out how long need to wait before can add to processor
					
					//time left to wait
					int timeToWait = edgeWeight - (schedule.procLengths[q.Processor] - currentParentNodeFinish);

					if (timeToWait < 0) {
						timeToWait = 0;
					}
					parentNodeCosts.add(schedule.procLengths[q.Processor] + nodeWeight + timeToWait);
					parentNodes.add(parentNode);
				}
			}

			minimumProcLength = parentNodeCosts.get(0);
			
			for(int i = 0; i < parentNodeCosts.size(); i++) {
				int pNodeCost = parentNodeCosts.get(i);
				if (pNodeCost > minimumProcLength) {
					minimumProcLength = pNodeCost;
				}
			}

			procWaitTime = minimumProcLength - nodeWeight - schedule.procLengths[q.Processor];
			
		} else { // if it's a root node, length is the node weight plus the processor length of the processor
			minimumProcLength = getNodeWeight(g, q.nodeIndex) + schedule.procLengths[q.Processor];
		}
		
		int[] newProcLengthAndTimeToWait = {minimumProcLength, procWaitTime};
		return newProcLengthAndTimeToWait;

	}

	/**
	 * Check whether a node is processable (all of it's parents exist in the currentSchedule) check dependency from dependency matrix (from ScheduleHelper)
	 * nodeToCheck.getIndex() gives you the index. dependencyMatrix[i][j] i is parent j is child (nodeToCheck is the child)
	 * @param nodeToBeChecked
	 * @param currentSchedule
	 * @return true if nodeToBeChecked is processable (all of it's parents exist in the schedule), false otherwise
	 * 
	 */
	public static boolean isProcessable(Node nodeToBeChecked, Schedule currentSchedule) {
		int indexOfCheckNode = nodeToBeChecked.getIndex();// gets the index of the node to be checked
		
		boolean nodeProcessable = true;
		
		for (int i = 0; i<dependencyMatrix[0].length; i++){ //loops through all the parents, check if the nodeToBeChecked is the child
			if (dependencyMatrix[i][indexOfCheckNode] == 1){
				
				boolean parentInSchedule = false;
				// check i is in the schedule
				for (Node parent : currentSchedule.schedule){ //loops through whole schedule
					if (parent.getIndex() == i){ // if parent in schedule return true
						parentInSchedule = true;
						break;
					}
				}
				
				if (!parentInSchedule){ // if parent is not in the schedule return false
					nodeProcessable = false;
					break;
				}	
			}
		}
		
		return nodeProcessable;
	}
	
	/**
	 * Replace the current best schedule with the new best schedule and replace the current best bound with new best bound (bound = schedule length)
	 * @param newBestSchedule
	 * @return return a copy of the newBestSchedule
	 * 
	 * could just call this one line from Branch instead of having to call this function
	 */
	public static void foundNewBestSolution(Schedule newBestSchedule, Graph g) {
		currentBestSchedule = new Schedule(newBestSchedule.schedule, newBestSchedule.procLengths, newBestSchedule.scheduleLength);
		for(Node n : g){
			for(Node bestN : bestGraph){
				if(n.getIndex() == bestN.getIndex()){
					Graphs.copyAttributes(n, bestN);
				}
			}
		}
		return;
	}
	
	/**
	 * Check the cost of adding the child node into the schedule 
	 * @param node
	 * @param schedule (current best schedule)
	 * @return true if schedule time after adding the node is less than current best total schedule time
	 */
    public static int checkChildNode(Node node, Schedule schedule, int processorID){
        //scheduleCopy = new Schedule(schedule.schedule, schedule.procLengths, schedule.scheduleLength);
 
        ArrayList<Node> parentNodes = new ArrayList<Node>();
        for (Edge e : node.getEachEnteringEdge()) {
            Node parentNode = e.getNode0();
            parentNodes.add(parentNode);
        }

        int canStartat = -1;
        int tempValue;
        int edgeWeight;
        int timeLeftToWait = 0;
        int tempTimeToWait;
        
        for (Node parent: parentNodes){
                    
            int parentProcessor = (int)Double.parseDouble(parent.getAttribute("Processor").toString());
            
            if (parentProcessor == processorID){ //node is being processed on same processor as parent currently being checked
                tempValue = schedule.procLengths[processorID];
                tempTimeToWait = 0;
            }
            else { //node being processed on different processor
               
                Edge parentToChild = parent.getEdgeToward(node);
                edgeWeight = (int)Double.parseDouble(parentToChild.getAttribute("Weight").toString());
                int lengthCurrentProcessor = schedule.procLengths[processorID];
                int endTime = (int)Double.parseDouble(parent.getAttribute("Start").toString()) + (int)Double.parseDouble(parent.getAttribute("Weight").toString());
                int timeWaited = lengthCurrentProcessor - endTime;
                tempTimeToWait = edgeWeight - timeWaited;
               
                // timeWaited longer than edgeWeight
                if (tempTimeToWait < 0) {
                    tempTimeToWait = 0;
                }
                tempValue = lengthCurrentProcessor + tempTimeToWait;
            }
           
            if (tempValue > canStartat){
                canStartat = tempValue;
            }
           
            if (tempTimeToWait > timeLeftToWait) {
                timeLeftToWait = tempTimeToWait;
            }
        }
       
        int procLength = canStartat + (int)Double.parseDouble(node.getAttribute("Weight").toString());
        for(int i : schedule.procLengths){
            if (i > procLength){
                procLength = i;
            }
        }

        if (procLength >= currentBestSchedule.scheduleLength) {
            return -1;
        } else {
            return timeLeftToWait;
        }
    }
	
	/**
	 * Insert the node into schedule 
	 * @param nodeToInsert
	 * @param currentSchedule
	 * 
	 * could just call this one line from Branch instead of having to call this function
	 */ 
	public static void insertNodeToSchedule(Node nodeToInsert, Schedule currentSchedule, int Processor, int procWaitTime) {
		currentSchedule.addNode(nodeToInsert, Processor, procWaitTime);
		currentSchedule.updateProcessorLength(Processor, (int)Double.parseDouble(nodeToInsert.getAttribute("Weight").toString()) + procWaitTime);
	}
	

}
