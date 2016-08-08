package scheduler;

import java.util.ArrayList;

import org.graphstream.graph.Graph;

public class Greedy {
	public Schedule greedySearch(Graph g, int procCount){
		Schedule schedule = new Schedule();		//make a new, empty schedule
		ArrayList<QueueItem> queue = new ArrayList<QueueItem>();
		ArrayList<Integer> root = ScheduleHelper.findRootNodes(g);
		for(Integer i:root){
			//Add root nodes to queue.
			for(int j = 0; j < procCount; j++ ){
				queue.add(new QueueItem(i, j));
			}
		}
		QueueItem smallest = queue.get(0);
		int popIndex = 0;
		for(int i = 0; i < queue.size(); i++){
			QueueItem q = queue.get(i);
			if(ScheduleHelper.getNodeWeight(g, q.nodeIndex) <= ScheduleHelper.getNodeWeight(g, smallest.nodeIndex)){
				smallest = q;
				popIndex = i;
			}
		}
		
		schedule.addNode(g.getNode(smallest.nodeIndex), smallest.processorID);
		schedule.updateProcessorLength(smallest.processorID, ScheduleHelper.getNodeWeight(g, smallest.nodeIndex));
		queue.remove(popIndex);
		
		while(!queue.isEmpty()){
			ArrayList<Integer> childrenNodes = ScheduleHelper.processableNodes(g, smallest.nodeIndex);
			for(int i:childrenNodes){
				for(int j = 0; j < procCount; j++ ){
					queue.add(new QueueItem(i, j));
				}
			}
			/*
			 * NOTE: Use zong's function to find 'best' node to insert
			 * this is an array with 2 values, the first is the increase in the schedule length if the node is inserted
			 * the second value is the increase in processor length if the node is inserted.
			 */
			
			int newProcLength;	
			int scheduleLength;
			int smallestWeightChange = 2147483647; //first one to compare with, give the initial weight change as the maximum possible
			int processorWeightInc = 0;
			
			for(int i = 0; i < queue.size(); i++){
				QueueItem q = queue.get(i);
				newProcLength = ScheduleHelper.scheduleNode(schedule, q, g);	//new length of the processor after adding this particular node
				scheduleLength = schedule.findScheduleLength();					//currrent length of schedule
				if(scheduleLength - newProcLength <= smallestWeightChange){ 	
					smallest = q;
					smallestWeightChange = scheduleLength - newProcLength;
					processorWeightInc = newProcLength - schedule.procLengths[q.processorID];
					popIndex = i;
				}
			}
			schedule.addNode(g.getNode(smallest.nodeIndex), smallest.processorID);
			schedule.updateProcessorLength(smallest.processorID, processorWeightInc);
			queue.remove(popIndex);
		}
			
		/*
		 * Possible outcomes:
		 * node is root = just add weight
		 * node is child, only dependent on 1 parent
		 * node is child, depends on more than 1 parent
		 */
		
		return schedule;
	}
	
//	public void addNodeToSchedule(Schedule s, QueueItem q, Graph g, int popIndex, int processorWeightInc){
//		s.addNode(g.getNode(q.nodeIndex), q.processorID);
//		s.procLengths[q.processorID] += processorWeightInc;
//		queue.remove(popIndex);
//	}
	public class QueueItem{
		public int nodeIndex;
		public int processorID;
		
		public QueueItem(int n){
			nodeIndex = n;
			processorID = -1;	//default value, -1 means it has not been processed yet.
		}
		public QueueItem(int n, int p){
			nodeIndex = n;
			processorID = p;
		}
	}
}
