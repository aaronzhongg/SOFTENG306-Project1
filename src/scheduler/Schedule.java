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
	public int[] procLengths;			//this keeps track of the current lengths of each processor
	public int scheduleLength;

	//make empty schedule
	public Schedule(){
		schedule = new ArrayList<Node>();
		procLengths = new int[1];		//default of 1 processor at the very least
		scheduleLength = 0;
	}
	
	//make empty schedule
	public Schedule(int procCount){
		schedule = new ArrayList<Node>();
		procLengths = new int[procCount];		//default of 1 processor at the very least
		scheduleLength = 0;
	}
	
	//For initialising a list of all the nodes. Do not use this to make the schedule
	public Schedule(DefaultGraph g){
		schedule = new ArrayList<Node>();
		Iterator<Node> i = g.getNodeIterator();
		while(i.hasNext()){
			schedule.add(i.next());
		}
		scheduleLength = 0;
	}
	
	//adds a node to the scheduler
	public void addNode(Node n, int processorID){
		n.setAttribute("processorID", processorID);
		schedule.add(n);
		scheduleLength = findScheduleLength();
	}
	//Changes the processor id for a node. This assumes the node has the processorID attribute.
	public void changeNodeProcessor(int nodePosition, int processorID){
		schedule.get(nodePosition).setAttribute("processorID", processorID);
		scheduleLength = findScheduleLength();
	}
	
	//removes a node in a given position in the schedule
	public void removeNode(int nodePosition){
		schedule.remove(nodePosition);
		scheduleLength = findScheduleLength();
	}
	
	//Find the current length of the schedule. This assumes the input schedule is valid
	//MIGHT NOT BE NEEDED, leave it for now
	public int findScheduleLength() {
//		int cost = this.schedule.get(0).getAttribute("Weight");	//initial cost is the first node in the first processor
//		int currentProc = 0; 			//current processor, starts at the first one 0
//		int i = 1;
//		
//		do{
//			Node n = this.schedule.get(i);
//			if(Integer.parseInt(n.getAttribute("processorID").toString()) == currentProc){
//				
//			}
//		}while(i < this.schedule.size());
//		
//		return 0;
		
		// schedule length should be equal to the largest procLength - assuming procLengths is updated every time new node is added/removed
		int largestProcLength = procLengths[0];
		
		for (int proc: procLengths) {
			if (proc > largestProcLength) {
				largestProcLength = proc;
			}
		}
		
		return largestProcLength;
		
	}
}
