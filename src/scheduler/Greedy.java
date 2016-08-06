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
			if(ScheduleHelper.getNodeWeight(g, q.nodeIndex) < ScheduleHelper.getNodeWeight(g, smallest.nodeIndex)){
				smallest = q;
				popIndex = i;
			}
		}
		
		schedule.addNode(g.getNode(smallest.nodeIndex), smallest.processorID);
		schedule.procLengths[smallest.processorID] += ScheduleHelper.getNodeWeight(g, smallest.nodeIndex);
		queue.remove(popIndex);
		
		return null;
	}
	
	private class QueueItem{
		private int nodeIndex;
		private int processorID;
		
		private QueueItem(int n){
			nodeIndex = n;
			processorID = -1;	//default value, -1 means it has not been processed yet.
		}
		private QueueItem(int n, int p){
			nodeIndex = n;
			processorID = p;
		}
	}
}
