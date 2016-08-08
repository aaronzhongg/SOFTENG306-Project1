package main;

import java.io.File;
import java.util.ArrayList;

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
		ArrayList<Node> rootNodes = scheduleHelper.findRootNodes(g);

		for (Node r: rootNodes) {
			System.out.println(r);
		}
	}


}
