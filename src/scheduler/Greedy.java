package scheduler;

import java.util.ArrayList;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 * This class contains all the functions used for the greedy algorithm
 * This class works to form a valid schedule output
 * contains basic milestone deliverable code
 */
public class Greedy {

	/**
	 * Greedy Search searches through the graph, according to the queue, adding nodes to the schedule that will increment each schedule time by the least amount
	 * Then adds the process-able nodes to the queue and goes through the queue again
	 * @param g : input graph
	 * @param procCount : amount of processors
	 * @param schedule : a schedule with 2 nodes already inserted. This will be unique for each instance of greedy
	 * @returns : when gone through whole queue (i.e. all nodes in graph)
	 */
	public ScheduleGraphPair greedySearch(Graph g, int procCount, Schedule schedule){

		//Schedule schedule = new Schedule(procCount);		//make a new, empty schedule
		ArrayList<QueueItem> queue = new ArrayList<QueueItem>();	//make an empty queue
		
		for (Node n : g) {// loops through all the nodes
			if (!schedule.schedule.contains(n)) {// new schedule doesn't contain it
				// check all the node that is not in the schedule
				boolean isProcessable = ScheduleHelper.isProcessable(n,schedule);
				if (isProcessable) { // if it is processable
					for (int i = 0; i < schedule.procLengths.length; i++) { // check all the available processor
						queue.add(new QueueItem(n.getIndex(),i));
					}
				}
			}
		}		
		
		QueueItem smallest = queue.get(0); // intialises the smallest queueitem as the first one

		// loops through the whole queue
		while(!queue.isEmpty()){

			int[] procInfo;
			int procWaitTime = 0;
			int newProcLength;	
			int scheduleLength;
			int smallestWeightChange = 2147483647; //first one to compare with, give the initial weight change as the maximum possible
			int processorWeightInc = 0;

			for(int i = 0; i < queue.size(); i++){ //loops through queue
				QueueItem q = queue.get(i);
				procInfo = ScheduleHelper.scheduleNode(schedule, q, g);
				newProcLength = procInfo[0];	//new length of the processor after adding this particular node
				scheduleLength = schedule.findScheduleLength();		// current length of schedule

				if(newProcLength - scheduleLength < smallestWeightChange){ //checks if this item to the schedule is the smallest weight change

					procWaitTime = procInfo[1];
					smallest = q;
					smallestWeightChange = newProcLength - scheduleLength;
					processorWeightInc = newProcLength - schedule.procLengths[q.Processor];	

				}
			}

			for (int popIndex = queue.size() -1 ; popIndex > -1; popIndex--) { //removes the node from the queue that makes the smallest difference

				if (queue.get(popIndex).nodeIndex == smallest.nodeIndex) {
					queue.remove(popIndex);
				}
			}

			//adds smallest node to schedule
			schedule.addNode(g.getNode(smallest.nodeIndex), smallest.Processor, procWaitTime);
			schedule.updateProcessorLength(smallest.Processor, processorWeightInc);


			ArrayList<Integer> childrenNodes = ScheduleHelper.processableNodes(g, smallest.nodeIndex); //adds processable children to queue
			for(int i:childrenNodes){
				for(int j = 0; j < procCount; j++ ){
					queue.add(new QueueItem(i, j));
				}
			}

		}
		
		ScheduleGraphPair sng = new ScheduleGraphPair(schedule, g);
		return sng;
	}

	/**
	 * This class is used by processable nodes method
	 * Adds items to the queue, adding node index and processor ID
	 */
	public class QueueItem{
		public int nodeIndex;
		public int Processor;

		public QueueItem(int n){
			nodeIndex = n;
			Processor = -1;	//default value, -1 means it has not been processed yet.
		}
		public QueueItem(int n, int p){
			nodeIndex = n;
			Processor = p;
		}
	}
	

	/**Simple schedule graph pair returned by greedy. Required for multi-threading since each thread needs its own graph
	 */
	public class ScheduleGraphPair{
		public Schedule schedule;
		public Graph g;
		
		public ScheduleGraphPair(Schedule s, Graph g){
			this.schedule = s;
			this.g = g;
		}
	}
}
