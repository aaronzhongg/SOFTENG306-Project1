package util;

import java.io.*;

import org.graphstream.stream.file.FileSinkDOT;

import scheduler.Schedule;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDOT;

/**
 * This class is used to handle the input and output of the Task graphs in .dot format. Initially, the processInput method
 * was written as a way to understand the input formatting. However, after deciding to use Graphstream, the IO handling is
 * heavily abstracted and simple to do - so the initial code was not used and is kept for reference.
 */
public class io {

    // Creating a separate graph which will be used for visualisation
	private Graph gVis;

	/**
	 * 
	 * @param input_file
	 */
	public void processInput(File input_file) {
		String line;
		try {
			BufferedReader br = new BufferedReader(new FileReader(input_file));
			br.readLine();
			while ((line = br.readLine()) != null && !line.equals("}")){
				String[] left = line.split("\\[");
				String[] edge = left[0].split("->");
				if (edge.length > 1){
					System.out.println("Edge from node " + edge[0] + " to node " + edge[1]); // can populate data structure using this
				}
				else{
					System.out.println("Single node: " + edge[0]);
				}
				if(left.length > 1) {
					int weight = Integer.parseInt(left[1].replaceAll("[^0-9]", ""));
					System.out.println(weight); // use weight to populate node weight or edge weight in data structure.
				}
			}
			br.close();
		//catch potential bufferedreader exceptions
		} catch (FileNotFoundException e) {e.printStackTrace();
		} catch (IOException e) {e.printStackTrace();}

	}

	/**
	 * 
	 * @param input_file
	 * @param file_name
	 * @return
	 */
	public Graph DOTParser(File input_file, String file_name){
		Graph g = new DefaultGraph("g");
		this.gVis=new DefaultGraph("g");
		FileSource fs = new FileSourceDOT();

		fs.addSink(g);
		fs.addSink(gVis);

		try{
			fs.readAll(file_name);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fs.removeSink(g);
		}

		//Add required attributes to every node - includes attributes used in visualisation and BnB.
		for (int i = 0; i < g.getNodeCount(); i++) {
			g.getNode(i).addAttribute("Processor", -1);
			g.getNode(i).addAttribute("Start" , -1);   
			gVis.getNode(i).addAttribute("ui.label", " "+g.getNode(i).getId());
			gVis.getNode(i).addAttribute("ui.class",g.getNode(i).getAttribute("Processor")+"");
			gVis.getNode(i).addAttribute("Processor", -1);
			gVis.getNode(i).addAttribute("Start" , -1);
		}

		return g;
	}

	/**
	 * 
	 * @param schedule
	 * @param inputGraph
	 * @param outputFileName
	 */
	public void outputFile(Schedule schedule, Graph inputGraph, String outputFileName){

	    // The output graph is saved in the /tmp/ folder by default. This was a design decision made to ensure that the output files
        // didn't occupy too much memory (tmp is cleared on restart).

		String output = "/tmp/" + outputFileName;
		FileSinkDOT fs = new FileSinkDOT(true);
		File outputFile = new File(output);
		FileOutputStream fos = null;
		try {
			outputFile.createNewFile();
			fos = new FileOutputStream(outputFile);
			fs.writeAll(inputGraph, fos);
			System.out.println("Output file saved to: " + output);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get method for the visualisation graph
	 * @return
	 */
	public Graph getVisGraph(){ return this.gVis; }

}