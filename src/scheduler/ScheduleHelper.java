package scheduler;

import java.util.ArrayList;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class ScheduleHelper {

	//Find the root nodes 
	public ArrayList<Node> findRootNodes(Graph g) {

		ArrayList<Node> rootNodes = new ArrayList<>();
		for (Node n:g) {
			if (n.getInDegree() == 0) {
				rootNodes.add(n);
			}
		}

		return rootNodes;
	}

	//Find the disjoint nodes
    public ArrayList<Node> findDisjointNodes(Graph g){

        ArrayList<Node> disjointNodes = new ArrayList<>();

        for(Node n:g){
            if (n.getOutDegree() == 0){
                disjointNodes.add(n);
            }
        }
        return disjointNodes;
    }
	
	

}
