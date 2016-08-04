package data;

import java.util.ArrayList;

public class Node {
	public String nodeName;
	public int nodeWeight;
	public ArrayList<Edge> edges;
	public ArrayList<String> prevNodes; //This is a list of nodes that have to complete before this can start
	
	public Node(String name, int weight){
		nodeName = name;
		nodeWeight = weight;
	}
	
	public void addEdge(Edge edge){
		this.edges.add(edge);
	}
}

