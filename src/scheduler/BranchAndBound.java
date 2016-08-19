package scheduler;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.ArrayList;

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

	public static int checkChildNode(Node node, Schedule schedule, int processorID){

        ArrayList<Node> parentNodes = new ArrayList();
        for (Edge e : node.getEachEnteringEdge()) {
            Node parentNode = e.getNode0();
            parentNodes.add(parentNode);
        }

        int startTime;
        int endTime;
        int canStartat = 2147483647;
        int tempValue;
        int communication_cost = 0;
        for (Node parent: parentNodes){
            startTime = (int)Double.parseDouble(parent.getAttribute("Start").toString());
            endTime = startTime + (int)Double.parseDouble(parent.getAttribute("Weight").toString());

            if ((int)Double.parseDouble(parent.getAttribute("Processor").toString()) == processorID){
                tempValue = endTime;
            }
            else {
                Edge parentToChild = parent.getEdgeToward(node);
                communication_cost = (int)Double.parseDouble(parentToChild.getAttribute("Weight").toString());
                tempValue = endTime + communication_cost;
            }
            if (tempValue < canStartat){
                canStartat = tempValue;
            }

        }
        // canStartat represents the earliest start time that the input node can be scheduled on the input processor.

	    return -1;
    }

	
}
