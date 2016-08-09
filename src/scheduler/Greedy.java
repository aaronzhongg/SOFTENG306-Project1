package scheduler;

import java.util.ArrayList;
import ui.Update;
import org.graphstream.graph.Graph;

public class Greedy {
	
	/**
	 * 
	 * @param g
	 * @param procCount
	 * @return
	 */
	public Schedule greedySearch(Graph g, int procCount){
		
		Schedule schedule = new Schedule(procCount);		//make a new, empty schedule
		ArrayList<QueueItem> queue = new ArrayList<QueueItem>();
		ArrayList<Integer> root = ScheduleHelper.findRootNodes(g);
		Update update=new Update(procCount);
		for(Integer i:root){
			for(int j = 0; j < procCount; j++ ){
				queue.add(new QueueItem(i, j));
				
			}
		}
		QueueItem smallest = queue.get(0);
		
		// picking smallest root node 
		for(int i = 0; i < queue.size(); i++){
			QueueItem q = queue.get(i);
			if(ScheduleHelper.getNodeWeight(g, q.nodeIndex) <= ScheduleHelper.getNodeWeight(g, smallest.nodeIndex)){
				smallest = q;
			}
		}
		
		for (int popIndex = queue.size() -1 ; popIndex > -1; popIndex--) {
			if (queue.get(popIndex).nodeIndex == smallest.nodeIndex) {
				queue.remove(popIndex);
			}
		}
		schedule.addNode(g.getNode(smallest.nodeIndex), smallest.processorID, 0);
		schedule.updateProcessorLength(smallest.processorID, ScheduleHelper.getNodeWeight(g, smallest.nodeIndex));
		update.updateColor(smallest.nodeIndex, smallest.processorID, g);
		
		g.getNode(smallest.nodeIndex).addAttribute("ui.style", "text-style:bold-italic; text-size:18;");
		
		//need to pop all queueitems with same nodeindex that was just processed
		
		ArrayList<Integer> childrenNodes = ScheduleHelper.processableNodes(g, smallest.nodeIndex);
		for(int i:childrenNodes){
			for(int j = 0; j < procCount; j++ ){
				queue.add(new QueueItem(i, j));
				
			}
		}
		int t=0;
		while(!queue.isEmpty()){
			
			int[] procInfo;
			int procWaitTime = 0;
			int newProcLength;	
			int scheduleLength;
			int smallestWeightChange = 2147483647; //first one to compare with, give the initial weight change as the maximum possible
			int processorWeightInc = 0;
			
			for(int i = 0; i < queue.size(); i++){
				QueueItem q = queue.get(i);
				procInfo = ScheduleHelper.scheduleNode(schedule, q, g);
				newProcLength = procInfo[0];	//new length of the processor after adding this particular node
				scheduleLength = schedule.findScheduleLength();					//currrent length of schedule
				if(newProcLength - scheduleLength <= smallestWeightChange){ 
					procWaitTime = procInfo[1];
					smallest = q;
					smallestWeightChange = newProcLength - scheduleLength;
					processorWeightInc = newProcLength - schedule.procLengths[q.processorID];
					
					//g.getNode(q.nodeIndex).addAttribute("ui.style", "fill-color: rgb(0,100,255);");
					
				}
			}
			
			for (int popIndex = queue.size() -1 ; popIndex > -1; popIndex--) {
				if (queue.get(popIndex).nodeIndex == smallest.nodeIndex) {
					queue.remove(popIndex);
				}
			}

			
			schedule.addNode(g.getNode(smallest.nodeIndex), smallest.processorID, procWaitTime);
			schedule.updateProcessorLength(smallest.processorID, processorWeightInc);
			
			//System.out.println("TEST i="+t+" :  Node id: " + smallest.nodeIndex + " ProcID: " + smallest.processorID );
			childrenNodes = ScheduleHelper.processableNodes(g, smallest.nodeIndex);
			for(int i:childrenNodes){
				for(int j = 0; j < procCount; j++ ){
					queue.add(new QueueItem(i, j));
				}
			}
			update.updateColor(smallest.nodeIndex,smallest.processorID,g);
		}
			
		/*
		 * Possible outcomes:
		 * node is root = just add weight
		 * node is child, only dependent on 1 parent
		 * node is child, depends on more than 1 parent
		 */
		
		return schedule;
	}
	
	/**
	 * This class is used by processable nodes as a queue items
	 */
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
