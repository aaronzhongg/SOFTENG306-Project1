package junitTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import main.Main;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.Graphs;
import org.junit.Test;

import scheduler.BranchAndBound;
import scheduler.Greedy;
import scheduler.Schedule;
import scheduler.ScheduleHelper;
import util.io;
import java.util.ArrayList;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.Graphs;

import pt.runtime.TaskID;
import pt.runtime.TaskIDGroup;

import scheduler.*;
import scheduler.Greedy.ScheduleGraphPair;
import ui.MainView;
import util.io;

/**
 * TEST VALID and OPTOMISED
 * STEPS:
 * Check parents of each node are in the schedule
 * for each parent look that parent is in the schedule
 * look that the distance is more then the minimum
 * @author idaknow
 */
public class testValidity {

	boolean isParallel = true; //change this to test different ones
	
	/**
	 * Main test class, where each file 7012 is tested on processors 1 -7
	 * These f and p values can be edited to suit the tester's needs
	 **/
	@Test
	public void testMain() {
		
		for (int f = 7; f < 12; f++){ // loops through all files
			for (int p = 1; p < 7; p++){ // loops through processors 1 to 7
				
				Graph g = create_the_graph(f); // creates the graph

				//USES CODE FROM MAIN to do all our calculations
				createSchedule(g,p);

				// THIS TEST WAS USED FOR EARLIER TESTING
				//assertTrue(checkAllNodesInGraph(ScheduleHelper.currentBestSchedule,g));
				
				assertTrue("Precedence check failed for file "+f+ " with  " + p + "processors",checkPrecedence(ScheduleHelper.currentBestSchedule,ScheduleHelper.bestGraph));
			
				if ((p == 2) || (p == 4)) { // uses given optimal times to compare against
					assertEquals("The Schedule is not optimal for file "+ f + " with " + p + " processors",ScheduleHelper.currentBestSchedule.scheduleLength, checkOptimal(p,f));
				}
			}
		}
	}
	
	/**
	 * This contains the code from the MAIN method in the Main.main we are testing
	 * **/
	public void createSchedule(Graph g, int p){
		ArrayList<Integer> rootnodes = ScheduleHelper.findRootNodes(g);

		ScheduleHelper.currentBestSchedule = new Schedule(p);
		ScheduleHelper.bestGraph = Graphs.clone(g);
		ScheduleHelper.currentBestSchedule.scheduleLength = 2147483647;
		Greedy greedy = new Greedy();
		ScheduleHelper.makeDependencyMatrix(g);

		// Create a schedule that has every combination of root node + next processable node (in each processor)
		for(int rootNode: rootnodes) {
			Graph tempNewGraph = Graphs.clone(g); 
			Schedule tempNewSchedule = new Schedule(p);
			tempNewSchedule.addNode(tempNewGraph.getNode(rootNode), 0, 0);
			tempNewGraph.getNode(rootNode).setAttribute("Processor", 0);
			ArrayList<Integer> processableNodes = new ArrayList<Integer>();

			for (Node n : tempNewGraph) {// loops through all the nodes
				if (!tempNewSchedule.schedule.contains(n)) {// new schedule doesn't contain it
					// check all the node that is not in the schedule
					boolean isProcessable = ScheduleHelper.isProcessable(n,tempNewSchedule);
					if (isProcessable) { // if it is processable
						processableNodes.add(n.getIndex());
					}
				}
			}

			for(int processableNodeIndex: processableNodes) {

				// in PARALLEL
				if (isParallel){
					int tempProcessorCount = 0;
					TaskIDGroup<TaskID<Void>> taskGroup = new TaskIDGroup<TaskID<Void>>(p);
					while(tempProcessorCount < p) {
						//New graph for each processableNode
						Graph newGraph = Graphs.clone(tempNewGraph); 		//NEED to create a new graph because GraphStream nodes
						Schedule newSchedule = new Schedule(p);		//New schedule with nodes from newly created Graph
						newSchedule.addNode(newGraph.getNode(rootNode), 0, 0);
						newSchedule.updateProcessorLength(0, (int)Double.parseDouble(newGraph.getNode(rootNode).getAttribute("Weight").toString()));

						int procWaitTime = ScheduleHelper.checkChildNode(newGraph.getNode(processableNodeIndex), newSchedule, tempProcessorCount);
						if (procWaitTime > -1 ) {

							newSchedule.addNode(newGraph.getNode(processableNodeIndex), tempProcessorCount, procWaitTime);
							newSchedule.updateProcessorLength(tempProcessorCount, procWaitTime + (int)Double.parseDouble(newGraph.getNode(processableNodeIndex).getAttribute("Weight").toString()));			
							TaskID<Void> id = Main.CreateScheduleTask(newSchedule, p, newGraph); // This function should run on a new thread
							taskGroup.add(id);
						}
						tempProcessorCount++;
					}
					try {taskGroup.waitTillFinished();}catch (Exception e){}

				
				} else { // in SEQUENTIAL

					// Add processable node into each processor
					int tempProcessorCount = 0;
					while(tempProcessorCount < p) {
						//New graph for each processableNode
						Graph newGraph = Graphs.clone(tempNewGraph); 		//create a new graph because GraphStream nodes
						Schedule newSchedule = new Schedule(p);		//New schedule with nodes from newly created Graph
						newSchedule.addNode(newGraph.getNode(rootNode), 0, 0);
						newSchedule.updateProcessorLength(0, (int)Double.parseDouble(newGraph.getNode(rootNode).getAttribute("Weight").toString()));


						int procWaitTime = ScheduleHelper.checkChildNode(newGraph.getNode(processableNodeIndex), newSchedule, tempProcessorCount);
						if (procWaitTime > -1 ) {

							newSchedule.addNode(newGraph.getNode(processableNodeIndex), tempProcessorCount, procWaitTime);
							newSchedule.updateProcessorLength(tempProcessorCount, procWaitTime + (int)Double.parseDouble(newGraph.getNode(processableNodeIndex).getAttribute("Weight").toString()));			
							Main.CreateSchedule(newSchedule, p, newGraph);
						}
						tempProcessorCount++;
					}

				}
			}
		}
	}

	/**
	 * Tests that all the nodes in the graph are in the schedule
	 * returns true if correct
	 * **/
	public boolean checkAllNodesInGraph(Schedule s, Graph g){
		boolean allNodesInGraph = true;
		for(Node n : g){
			if (!s.schedule.contains(n)){
				allNodesInGraph = false;
				break;
			}
		}
		return allNodesInGraph;
	}

	/**
	 * Checks the precedence
	 * **/
	public boolean checkPrecedence(Schedule s, Graph g){

		for (Node n : g){
			Iterable<Edge> childIte = g.getNode(n.getId()).getEachEnteringEdge(); // loop through all the parents
			for (Edge childe: childIte){
				Node parentn = childe.getNode0();

				int edgeWeight = (int)Double.parseDouble(childe.getAttribute("Weight").toString());
				int nodeWeight = (int)Double.parseDouble(g.getNode(parentn.getId()).getAttribute("Weight").toString());
				int pstart = g.getNode(parentn.getId()).getAttribute("Start");
				int cstart = g.getNode(n.getId()).getAttribute("Start");

				int childProcessor = (int)Double.parseDouble(g.getNode(n.getId()).getAttribute("Processor").toString());
				int parentProcessor = (int)Double.parseDouble(parentn.getAttribute("Processor").toString());

				if (parentProcessor == childProcessor){ // if they're on the same processor
					if ((cstart - pstart) < nodeWeight){// check child and parent have at least edge weight between them	
						//System.out.println(parentn+" "+n+" " + "cstart: " +cstart +" pstart: "+ pstart + " nw " + nodeWeight);
						return false;
					}
				} else { // if they're on different processors
					if ((cstart - pstart) < (edgeWeight + nodeWeight)){// check child and parent have at least edge weight between them	
						//System.out.println("that");
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Compares optimal times provided with schedule output
	 * **/
	public int checkOptimal( int p, int f){
		int optimaltime = 0;
		if (f == 7){
			if (p == 2){
				optimaltime = 28;
			} else{
				optimaltime = 22;
			}
		} else if (f == 8){
			optimaltime = 581;
		} else if (f == 9){
			optimaltime = 55;
		} else if (f == 10){
			optimaltime = 50;
		} else if (f == 11){
			if (p == 2){
				optimaltime = 350;
			} else {
				optimaltime = 227;
			}
		}
		return optimaltime;
	}

	/**
	 * loops through all the different file names provided on canvas.
	 * @int a : could be edited to suit testers needs
	 * **/
	public String create_the_filename(int a){
		String filename = "";
		if (a == 7){
			filename = "TestDotFiles/Nodes_7_OutTree.dot";
		} else if (a == 8){
			filename = "TestDotFiles/Nodes_8_Random.dot";
		} else if (a == 9){
			filename = "TestDotFiles/Nodes_9_SeriesParallel.dot";
		} else if (a == 10){
			filename = "TestDotFiles/Nodes_10_Random.dot";
		} else if (a == 11){
			filename = "TestDotFiles/Nodes_11_OutTree.dot";
		}
		return filename;
	}

	/**
	 * creates the graph from the testing input file
	 * **/
	public Graph create_the_graph(int fileNumber) { //change name
		//initialise everything
		String file_name = create_the_filename(fileNumber);
		File input_file = new File(file_name);
		io IOProcessor = new io();

		return  IOProcessor.DOTParser(input_file,file_name);
	}

}
