package src.main;

import java.io.File;
import java.util.ArrayList;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.Graphs;

import src.scheduler.*;
import src.ui.MainView;
import src.util.io;
import src.ui.*;

/**
 * Created by jay on 5/08/16.
 */
public class Main {
	
	public static int processorInput;
	public static Update update;
	public static Graph gVis;
	public static long initialTime,endTime;
	public static void main(String[] args) throws InterruptedException{

	    //uncomment below before submitting - just some additional robustness and error-checking
	    /*if (args.length < 2){
            System.out.println("Please ensure that all input parameters have been entered - Input file name and number of processors. ");
            System.exit(1);
        }*/
	    //String inputFile = args[0];
        //processorInput = Integer.parseInt(args[1]);

        // comment out code below before submitting.

        String inputFile = "TestDotFiles/Nodes_8_Random.dot";
         processorInput = 2;
        
		File input_file = new File(inputFile);
		io IOProcessor = new io();
		Schedule schedule=null;
		//ScheduleHelper scheduleHelper = new ScheduleHelper();

		Graph g = IOProcessor.DOTParser(input_file, inputFile);
		 gVis=IOProcessor.getVisGraph();
		ArrayList<Integer> rootnodes = ScheduleHelper.findRootNodes(g);
		
	
		MainView MainView=new MainView(gVis);
		MainView.setVisible(true);
		initialTime = System.currentTimeMillis();
		endTime=0;

		// Find root nodes from the input graph
		//ArrayList<Integer> rootNodes = ScheduleHelper.findRootNodes(g);
		
		Greedy greedy = new Greedy();
		schedule = greedy.greedySearch(g, processorInput, rootnodes,gVis);
		update=new Update(processorInput,schedule.schedule);
		ScheduleHelper.currentBestSchedule = new Schedule(schedule.schedule, schedule.procLengths, schedule.scheduleLength);
		ScheduleHelper.bestGraph = Graphs.clone(g);
		//temporary printing the greedy stuff
/*		for(Node n:schedule.schedule){
			System.out.println("Node id: " + n.getId() + " ProcID: " + n.getAttribute("Processor") + " Starts at: " + n.getAttribute("Start") + " Node Weight: " + n.getAttribute("Weight"));
		}
		System.out.println("Total Schedule Length: " + schedule.scheduleLength);*/
		
		
		
		BranchAndBound bnb = new BranchAndBound(schedule, g);
		ScheduleHelper.makeDependencyMatrix(g);
		bnb.branchAndBoundAlgorithm();
		endTime=System.currentTimeMillis();
		update.updateProcessorColor(gVis);
		MainView.updateSchedule(ScheduleHelper.currentBestSchedule.scheduleLength);
		
		for(Node n: ScheduleHelper.bestGraph){
			System.out.println("Node id: " + n.getId() + " ProcID: " + n.getAttribute("Processor") + " Starts at: " + n.getAttribute("Start") + " Node Weight: " + n.getAttribute("Weight"));
		}
		System.out.println("Total Schedule Length: " + ScheduleHelper.currentBestSchedule.scheduleLength);
       // IOProcessor.outputFile(schedule, ScheduleHelper.bestGraph, inputFile); // creates the output file
      
        

	}
}
