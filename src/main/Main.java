package main;

import java.io.File;
import java.util.ArrayList;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.Graphs;

import scheduler.*;
import scheduler.Greedy.ScheduleGraphPair;
import ui.MainView;
import util.io;
import ui.Update;

/**
 * Created by jay on 5/08/16.
 */
public class Main {
	
	public static int processorInput;
	
	public static void main(String[] args){

	    //uncomment below before submitting - just some additional robustness and error-checking
	    /*if (args.length < 2){
            System.out.println("Please ensure that all input parameters have been entered - Input file name and number of processors. ");
            System.exit(1);
        }*/
	    //String inputFile = args[0];
        //processorInput = Integer.parseInt(args[1]);

        // comment out code below before submitting.

        String inputFile = "TestDotFiles/Nodes_7_OutTree.dot";
        int processorInput = 2;
        
		File input_file = new File(inputFile);
		io IOProcessor = new io();
		Schedule schedule;
		//ScheduleHelper scheduleHelper = new ScheduleHelper();

		Graph g = IOProcessor.DOTParser(input_file, inputFile);
		ArrayList<Integer> rootnodes = ScheduleHelper.findRootNodes(g);
		//System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		//MainView MainView=new MainView(g);
		//MainView.setVisible(true);

		// Find root nodes from the input graph
		//ArrayList<Integer> rootNodes = ScheduleHelper.findRootNodes(g);
		
		ScheduleHelper.currentBestSchedule = new Schedule(processorInput);
		ScheduleHelper.bestGraph = Graphs.clone(g);
		ScheduleHelper.currentBestSchedule.scheduleLength = 2147483647;
		Greedy greedy = new Greedy();
		ScheduleHelper.makeDependencyMatrix(g);
		
	
		// Create a schedule that has every combination of root node + next processable node (in each processor)
		for(int rootNode: rootnodes) {
			// Make a clone to get the processableNodes after adding the root node to the schedule
			Graph tempNewGraph = Graphs.clone(g); 
//			Schedule tempNewSchedule = new Schedule(processorInput);
			tempNewGraph.getNode(rootNode).setAttribute("Processor", 0);
			ArrayList<Integer> processableNodes = ScheduleHelper.processableNodes(tempNewGraph, rootNode);
			
			for(int processableNodeIndex: processableNodes) {
				
				//New graph for each processableNode
				Graph newGraph = Graphs.clone(tempNewGraph); 		//NEED to create a new graph because GraphStream nodes
				Schedule newSchedule = new Schedule(processorInput);		//New schedule with nodes from newly created Graph
				newSchedule.addNode(newGraph.getNode(rootNode), 0, 0);
				newSchedule.updateProcessorLength(0, (int)Double.parseDouble(newGraph.getNode(rootNode).getAttribute("Weight").toString()));
				
				// Add processable node into each processor
				int tempProcessorCount = 0;
				while(tempProcessorCount < processorInput) {
					int procWaitTime = ScheduleHelper.checkChildNode(g.getNode(processableNodeIndex), newSchedule, tempProcessorCount);
					newSchedule.addNode(g.getNode(processableNodeIndex), tempProcessorCount, procWaitTime);
					newSchedule.updateProcessorLength(tempProcessorCount, procWaitTime + (int)Double.parseDouble(newGraph.getNode(processableNodeIndex).getAttribute("Weight").toString()));			
					CreateSchedule(newSchedule, processorInput, newGraph);		// This function should run on a new thread
				}
				
				
			}
			
//			schedule = greedy.greedySearch(g, processorInput, schedule);
		}

		//temporary printing the greedy stuff
/*		for(Node n:schedule.schedule){
			System.out.println("Node id: " + n.getId() + " ProcID: " + n.getAttribute("Processor") + " Starts at: " + n.getAttribute("Start") + " Node Weight: " + n.getAttribute("Weight"));
		}
		System.out.println("Total Schedule Length: " + schedule.scheduleLength);*/
		
		
		
//		BranchAndBound bnb = new BranchAndBound(schedule, g);
		
//		bnb.branchAndBoundAlgorithm();
		// prints answer
	/*  for(Node n:schedule.schedule){
			System.out.println("Node id: " + n.getId() + " ProcID: " + n.getAttribute("Processor") + " Starts at: " + n.getAttribute("Start") + " Node Weight: " + n.getAttribute("Weight"));
		}
		System.out.println("Total Schedule Length: " + schedule.scheduleLength);*/
		for(Node n: ScheduleHelper.bestGraph){
			System.out.println("Node id: " + n.getId() + " ProcID: " + n.getAttribute("Processor") + " Starts at: " + n.getAttribute("Start") + " Node Weight: " + n.getAttribute("Weight"));
		}
		System.out.println("Total Schedule Length: " + ScheduleHelper.currentBestSchedule.scheduleLength);
//        IOProcessor.outputFile(schedule, ScheduleHelper.bestGraph, inputFile); // creates the output file
       
        

	}
	
	//Need to start new thread 
	public static void CreateSchedule(Schedule schedule, int processorCount, Graph g) {
		Greedy greedy = new Greedy();
		ScheduleGraphPair sgPair = greedy.greedySearch(g, processorCount, schedule);
		
		// After greedy algorithm returns a schedule, if it is better than current best, update the bound and bestGraph
		if (sgPair.schedule.scheduleLength < ScheduleHelper.currentBestSchedule.scheduleLength) {
			ScheduleHelper.currentBestSchedule.scheduleLength = sgPair.schedule.scheduleLength;
			
			for(Node n : g){
				for(Node bestN : ScheduleHelper.bestGraph){
					if(n.getIndex() == bestN.getIndex()){
						Graphs.copyAttributes(n, bestN);
					}
				}
			}

		}
		
		BranchAndBound bnb = new BranchAndBound(sgPair.schedule, sgPair.g);
		bnb.branchAndBoundAlgorithm();
		
		
	}
}
