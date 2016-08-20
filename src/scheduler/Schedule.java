package scheduler;

import java.util.ArrayList;
import org.graphstream.graph.*;

/**
 * This is the schedule data structure.
 * This stores the nodes of the graph with their assigned processor ids
 * The order of the schedule should be the order the tasks are scheduled
 * @author Alex
 */
public class Schedule {

	public ArrayList<Node> schedule;
	public int[] procLengths;			//this keeps track of the current lengths of each processor
	public int scheduleLength;			//this is the current total schedule length (of the largest processor length)

	/**
	 * CONSTRUCTORS: makes empty schedules
	 */
	public Schedule(){
		schedule = new ArrayList<Node>();
		procLengths = new int[1];		//default of 1 processor at the very least
		scheduleLength = 0;
	}

	public Schedule(int procCount){
		schedule = new ArrayList<Node>();
		procLengths = new int[procCount];		//makes amount of input processors
		scheduleLength = 0;
	}
	
	/**
	 * Clone a schedule for calculation purposes
	 * @param s
	 * @param procl
	 * @param scheduleL
	 */
	public Schedule(ArrayList<Node> s, int[]procl ,int scheduleL){
		schedule = s;
		procLengths = procl;		//makes amount of input processors
		scheduleLength = scheduleL;
	}

	/**
	 * adds an input node to the scheduler
	 * @param n : Node wanting to add
	 * @param Processor : the processor the node will get added to
	 * @param procWaitTime
	 */
	public void addNode(Node n, int Processor, int procWaitTime){
		n.setAttribute("Processor", Processor);
		n.setAttribute("Start", procLengths[Processor] + procWaitTime);
		this.schedule.add(n);
	}

	/**
	 * Changes the processor id for a node. This assumes the node has the Processor attribute.
	 * @param nodePosition
	 * @param Processor
	 */
	public void changeNodeProcessor(int nodePosition, int Processor){
		schedule.get(nodePosition).setAttribute("Processor", Processor);
		schedule.get(nodePosition).setAttribute("Start", procLengths[Processor]);
	}

	/**
	 * removes a node in a given position in the schedule
	 * @param nodePosition
	 */
	public void removeNode(int nodePosition){
		schedule.remove(nodePosition);
	}

	/**
	 * update processor lengths 
	 * @param Processor
	 * @param procIncrease
	 */
	public void updateProcessorLength(int Processor, int procIncrease) {
		procLengths[Processor] += procIncrease;
		scheduleLength = findScheduleLength();	//updates the schedule length to biggest of all processors
	}

	/**
	 * Find the current length of the schedule. This assumes the input schedule is valid.
	 * @returns the length of the schedule
	 */
	public int findScheduleLength() {
		int largestProcLength = procLengths[0];

		for (int proc: procLengths) {
			if (proc > largestProcLength) {
				largestProcLength = proc;
			}
		}

		return largestProcLength;
	}
}
