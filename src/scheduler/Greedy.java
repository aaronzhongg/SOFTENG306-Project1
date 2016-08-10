package scheduler;

import java.util.ArrayList;
import ui.Update;
import org.graphstream.graph.Graph;

public class Greedy {
	
	/**
	 * Greedy Search searches through the graph, according to the queue, adding nodes to the schedule that will increment each schedule time by the least amount
	 * Then adds the process-able nodes to the queue and goes through the queue again
	 * @param g : input graph
	 * @param procCount : amount of processors
	 * @returns : when gone through whole queue (i.e. all nodes in graph)
	 */
	public Schedule greedySearch(Graph g, int procCount){
		
		Schedule schedule = new Schedule(procCount);		//make a new, empty schedule
		ArrayList<QueueItem> queue = new ArrayList<QueueItem>();	//make an empty queue
		ArrayList<Integer> root = ScheduleHelper.findRootNodes(g);	//finds all root nodes
		//Update update=new Update(procCount);
		
		for(Integer i:root){ //adds all root nodes to the queue, each with all processor ids
			for(int j = 0; j < procCount; j++ ){
				queue.add(new QueueItem(i, j));
			}
		}
		
		QueueItem smallest = queue.get(0);
		// picking smallest root node from the queue
		for(int i = 0; i < queue.size(); i++){
			QueueItem q = queue.get(i);
			if(ScheduleHelper.getNodeWeight(g, q.nodeIndex) <= ScheduleHelper.getNodeWeight(g, smallest.nodeIndex)){
				smallest = q;
			}
		}
		
		//removes the smallest root node found and removes it from the queue
		for (int popIndex = queue.size() -1 ; popIndex > -1; popIndex--) {
			if (queue.get(popIndex).nodeIndex == smallest.nodeIndex) {
				queue.remove(popIndex);
			}
		}
		
		schedule.addNode(g.getNode(smallest.nodeIndex), smallest.Processor, 0); //adds the smallest root node to the schedule of the smallest processor
		schedule.updateProcessorLength(smallest.Processor, ScheduleHelper.getNodeWeight(g, smallest.nodeIndex)); //changes processor length of added smallest root node
		
		//updates GUI
		//update.updateColor(smallest.nodeIndex, smallest.Processor, g);//updates the color
		//g.getNode(smallest.nodeIndex).addAttribute("ui.style", "text-style:bold-italic; text-size:18;");
		
		//goes through all children of the smallest root nodes and 
		ArrayList<Integer> childrenNodes = ScheduleHelper.processableNodes(g, smallest.nodeIndex);
		for(int i:childrenNodes){
			for(int j = 0; j < procCount; j++ ){
				queue.add(new QueueItem(i, j));	
			}
		}
		
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
				
				if(newProcLength - scheduleLength <= smallestWeightChange){ //checks if this item to the schedule is the smallest weight change
					procWaitTime = procInfo[1];
					smallest = q;
					smallestWeightChange = newProcLength - scheduleLength;
					processorWeightInc = newProcLength - schedule.procLengths[q.Processor];	
					
					//GUI //g.getNode(q.nodeIndex).addAttribute("ui.style", "fill-color: rgb(0,100,255);");
				}
			}
			
			for (int popIndex = queue.size() -1 ; popIndex > -1; popIndex--) { //removes the node from the queue that makes the smallest dif
				if (queue.get(popIndex).nodeIndex == smallest.nodeIndex) {
					queue.remove(popIndex);
				}
			}

			//adds smallest node to schedule
			schedule.addNode(g.getNode(smallest.nodeIndex), smallest.Processor, procWaitTime);
			schedule.updateProcessorLength(smallest.Processor, processorWeightInc);
			
			//TEST //System.out.println("TEST i="+t+" :  Node id: " + smallest.nodeIndex + " ProcID: " + smallest.Processor );
			
			childrenNodes = ScheduleHelper.processableNodes(g, smallest.nodeIndex); //adds processable children to queue
			for(int i:childrenNodes){
				for(int j = 0; j < procCount; j++ ){
					queue.add(new QueueItem(i, j));
				}
			}
			
			//update.updateColor(smallest.nodeIndex,smallest.Processor,g); //GUI
		}
			
		/* NOTE
		 * Possible outcomes:
		 * node is root = just add weight
		 * node is child, only dependent on 1 parent
		 * node is child, depends on more than 1 parent
		 */
		
		return schedule;
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
}
