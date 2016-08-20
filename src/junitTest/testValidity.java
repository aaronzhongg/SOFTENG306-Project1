package junitTest;

import static org.junit.Assert.*;

import java.io.File;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.junit.Test;

import scheduler.Greedy;
import scheduler.Greedy.QueueItem;
import scheduler.Schedule;
import util.io;

/**
 * check parents of each node are in the schedule
 * for each parent look that parent is in the schedule
 * look that the distance is more then the minimum
 * @author idaknow
 *
 */

public class testValidity {

	@Test
	public void testMain() {

		for (int f = 7; f < 12; f++){ // loops through all files
			for (int p = 1; p < 5; p++){ // loops through processors 1 to 4
				Graph g = create_the_graph(f); // creates the graph
				Greedy greedy = new Greedy();
				Schedule s = greedy.greedySearch(g, p);

				assertTrue(checkAllNodesInGraph(s,g));
				assertTrue(checkPrecedence(s,g));
				// if (p == 2) || (p == 4) {
				// assert (s.scheduleLength, checkOptimal(s,p,f);
				//}
	}
			}
		}
	//}
	
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
	
	public boolean checkPrecedence(Schedule s, Graph g){
		
		for (Node n : g){
			Iterable<Edge> childIte = g.getNode(n.getId()).getEachEnteringEdge(); // loop through all the parents
			for (Edge childe: childIte){
				Node parentn = childe.getNode0();

				int edgeWeight = (int)Double.parseDouble(childe.getAttribute("Weight").toString());
				int nodeWeight = (int)Double.parseDouble(parentn.getAttribute("Weight").toString());
				int pstart = parentn.getAttribute("Start");
				int cstart = n.getAttribute("Start");
				
				int childProcessor = (int)Double.parseDouble(n.getAttribute("Processor").toString());
				int parentProcessor = (int)Double.parseDouble(parentn.getAttribute("Processor").toString());
				
				if (parentProcessor == childProcessor){ // if they're on the same processor
					if ((cstart - pstart) < nodeWeight){// check child and parent have at least edge weight between them	
						return false;
					}
				} else { // if they're on different processors
					if ((cstart - pstart) < (edgeWeight + nodeWeight)){// check child and parent have at least edge weight between them	
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public int checkOptimal(Schedule s, int p, int f){
		int optimaltime = 0;
		if (f == 7){
			if (p == 2){
				optimaltime = 28;
			} else if (p == 4){
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
			} else if (p == 4){
				optimaltime = 227;
			}
		}
		return optimaltime;
	}
	
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
	
	public Graph create_the_graph(int fileNumber) { //change name
		//initialise everything
		String file_name = create_the_filename(fileNumber);
		File input_file = new File(file_name);
		io IOProcessor = new io();

		return  IOProcessor.DOTParser(input_file,file_name);
	}

}
