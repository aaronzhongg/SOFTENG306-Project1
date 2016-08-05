package data;

import java.util.ArrayList;

public class Node {
	//Using public instead of getter and setters might be faster
	public String nodeName;
	public int nodeWeight;
	public ArrayList<Edge> edges;
	public ArrayList<String> prevNodes; 	//This is a list of nodes that have to complete before this can start
	public int processorNo; 				//This will be the processor number assigned to the node in the algorithm
	
	public Node(String name, int weight){
		nodeName = name;
		nodeWeight = weight;
	}
	
	public void addEdge(Edge edge){
		this.edges.add(edge);
	}
}

