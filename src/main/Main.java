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
		File input_file = new File("digraph_example.dot");
		io IOProcessor = new io();
		Schedule schedule = new Schedule();
		ScheduleHelper scheduleHelper = new ScheduleHelper();

		Graph g = IOProcessor.DOTParser(input_file);

		// Find root nodes from the input graph
		ArrayList<Integer> rootNodes = scheduleHelper.findRootNodes(g);
		
		
		//Test if processableNodes function is working
		g.getNode(0).setAttribute("processorID", 0);
		g.getNode(1).setAttribute("processorID", 0);
		ArrayList<Integer> processableNodes = scheduleHelper.processableNodes(g, 1);
		for (int r: processableNodes) {
			System.out.println(r + " " + g.getNode(r));
		}
	}


}
