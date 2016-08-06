package scheduler;

import java.util.ArrayList;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class ScheduleHelper {

	//Find the root nodes 
	public ArrayList<Integer> findRootNodes(Graph g) {

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
	
	

}
