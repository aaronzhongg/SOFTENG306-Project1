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

	    //uncomment below before submitting - just some additional robustness and error-checking
//	    if (args.length < 2){
//            System.out.println("Please ensure that all input parameters have been entered - Input file name and number of processors. ");
//            System.exit(1);
//        }
//	    String inputFile = args[0];
//        int processorInput = Integer.parseInt(args[1]);

        // comment out code below before submitting.
        String inputFile = "digraph_example3.dot";
        int processorInput = 2;

		//int processorInput = 2;
		//String file_name = "digraph_example.dot";
		File input_file = new File(inputFile);
		io IOProcessor = new io();
		Schedule schedule;
		//ScheduleHelper scheduleHelper = new ScheduleHelper();

		Graph g = IOProcessor.DOTParser(input_file, inputFile);

		// Find root nodes from the input graph
		//ArrayList<Integer> rootNodes = ScheduleHelper.findRootNodes(g);
		
		Greedy greedy = new Greedy();
		schedule = greedy.greedySearch(g, processorInput);
		
		// prints answer
		for(Node n:schedule.schedule){
			System.out.println("Node id: " + n.getId() + " ProcID: " + n.getAttribute("processorID") + " Starts at: " + n.getAttribute("Start"));
		}
		System.out.println("Total Schedule Length: " + schedule.scheduleLength);

        IOProcessor.outputFile(schedule, g, inputFile); // creates the output file

	}
}
