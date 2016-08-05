package scheduler;

import java.util.ArrayList;
import java.util.Iterator;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.DefaultGraph;

/**
 * This is the schedule data structure.
 * This stores the nodes of the graph with their assigned processor ids
 * The order of the schedule should be the order the tasks are scheduled
 * @author Alex
 *
 */
public class Schedule {
	
	public ArrayList<Node> schedule;

	//make empty schedule
	public Schedule(){
		schedule = new ArrayList<Node>();
	}
	
	//For initialising a list of all the nodes. Do not use this to make the schedule
	public Schedule(DefaultGraph g){
		schedule = new ArrayList<Node>();
		Iterator<Node> i = g.getNodeIterator();
		while(i.hasNext()){
			schedule.add(i.next());
		}
	}
	
	//adds a node to the scheduler
	public void addNode(Node n, int processorID){
		n.setAttribute("processorID", processorID);
		schedule.add(n);
	}
	//Changes the processor id for a node. This assumes the node has the processorID attribute.
	public void changeNodeProcessor(int nodePosition, int processorID){
		schedule.get(nodePosition).setAttribute("processorID", processorID);
	}
	
	//removes a node in a given position in the schedule
	public void removeNode(int nodePosition){
		schedule.remove(nodePosition);
	}
	
}
