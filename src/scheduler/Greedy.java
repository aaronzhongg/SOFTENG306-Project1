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
		schedule.procLengths[smallest.processorID] += ScheduleHelper.getNodeWeight(g, smallest.nodeIndex);
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
			
			int[] weightIncreases = zongFunction(queue.get(0));		
			
			int smallestWeight = weightIncreases[0]; //first one to compare with
			int processorWeightInc = weightIncreases[1];
			for(int i = 0; i < queue.size(); i++){
				QueueItem q = queue.get(i);
				weightIncreases = zongFunction(q);
				if(weightIncreases[0] <= smallestWeight){
					smallest = q;
					smallestWeight = weightIncreases[0];
					processorWeightInc = weightIncreases[1];
					popIndex = i;
				}
			}
			schedule.addNode(g.getNode(smallest.nodeIndex), smallest.processorID);
			schedule.procLengths[smallest.processorID] += processorWeightInc;
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
