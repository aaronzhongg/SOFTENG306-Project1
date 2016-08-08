package main;

import java.io.File;
import java.util.ArrayList;

import org.graphstream.graph.Graph;
import org.graphstream.graph.*;
import scheduler.*;
import util.io;

/**
 * Created by jay on 5/08/16.
 */
public class Main {
	public static void main(String[] args){

		int processorInput = 2;
		String file_name = "digraph_example4.dot";
		File input_file = new File(file_name);
		io IOProcessor = new io();
		Schedule schedule = new Schedule();
		ScheduleHelper scheduleHelper = new ScheduleHelper();

		Graph g = IOProcessor.DOTParser(input_file, file_name);

		// Find root nodes from the input graph
		ArrayList<Integer> rootNodes = ScheduleHelper.findRootNodes(g);
		
		
		//Test if processableNodes function is working
//		g.getNode(0).setAttribute("processorID", 0);
////		g.getNode(1).setAttribute("processorID", 0);
//		ArrayList<Integer> processableNodes = ScheduleHelper.processableNodes(g, 0);
//		for (int r: processableNodes) {
//			System.out.println(r + " " + Double.parseDouble(g.getNode(r).getAttribute("Weight").toString()));
//			
//		}
		
		Greedy greedy = new Greedy();
		schedule = greedy.greedySearch(g, 2);
		for(Node n:schedule.schedule){
			System.out.println("Node id: " + n.getId() + " ProcID: " + n.getAttribute("processorID") + " Starts at: " + n.getAttribute("Start"));
		}
		System.out.println("Total Schedule Length: " + schedule.scheduleLength);
	}


}
