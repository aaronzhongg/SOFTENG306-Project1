package scheduler;

import java.util.ArrayList;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Edge;

public class ScheduleHelper {


	//Find the root nodes 
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
	
	public static int getNodeWeight(Graph g, int nodeIndex){
		return Integer.parseInt(g.getNode(nodeIndex).getAttribute("Weight").toString());
	}
	

	// After a node has been processed, call this function to return all new nodes that can be processed
	public ArrayList<Integer> processableNodes(Graph g, int nodeIndex) {

		ArrayList<Integer> processableNodes = new ArrayList<Integer>();

		// Get all the leaving edges of the node just processed
		Iterable<Edge> ite = g.getNode(nodeIndex).getEachLeavingEdge();

		boolean nodeProcessable;
		// Get all the child nodes of the node that was just processed, for each child node, check whether all it's parent nodes have been processed
		for (Edge e: ite) {
			Node n = e.getNode1();
			nodeProcessable = true;

			// Check all of child node's parent nodes have been processed
			Iterable<Edge> childIte = g.getNode(n.getId()).getEachEnteringEdge();
			for (Edge childEdge: childIte) {
				Node parentNode = childEdge.getNode0();
				
				if (Integer.parseInt(parentNode.getAttribute("processorID").toString()) == -1) {
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

}
