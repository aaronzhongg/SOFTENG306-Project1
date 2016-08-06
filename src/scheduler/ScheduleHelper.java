package scheduler;

import java.util.ArrayList;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

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
	

}
