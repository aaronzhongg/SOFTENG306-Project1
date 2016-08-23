package src.util;

import java.io.*;

import org.graphstream.stream.file.FileSinkDOT;

import src.scheduler.Schedule;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDOT;

/**
 * Created by jay on 1/08/16.
 * This is just a skeleton class that I've created to test some input and output of the DOT format file. All utility classes
 * should be included in the "util" package.
 */
public class io {
private Graph gVis;

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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Graph DOTParser(File input_file, String file_name){
        Graph g = new DefaultGraph("g");
        this.gVis = new DefaultGraph("g");
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
        
        //Add Processor attribute to every node
        for (int i = 0; i < g.getNodeCount(); i++) {
        	
        	g.getNode(i).addAttribute("Processor", -1);
        	g.getNode(i).addAttribute("Start" , -1);
        	gVis.getNode(i).addAttribute("ui.label", " "+g.getNode(i).getId());
        	gVis.getNode(i).addAttribute("ui.class",g.getNode(i).getAttribute("Processor")+"");
        	gVis.getNode(i).addAttribute("Processor", -1);
        	gVis.getNode(i).addAttribute("Start" , -1);
        
        }
       
        //g.display();
        return g;
    }

    public void outputFile(Schedule schedule, Graph inputGraph, String inputFileName){
//        for (Node n: schedule.schedule){
//            for (Node graphnode: inputGraph.getNodeSet()){
//                if (n.getId() == graphnode.getId()){
//                    //graphnode.addAttribute("Start", n.getAttribute("Start").toString());
//                    //graphnode.addAttribute("Processor", n.getAttribute("Processor").toString());
//                }
//
//            }
//        }
        String[] split = inputFileName.split(".dot");
        String nameWithoutSuffix = split[0];
        File outputFileWithoutSuffix = new File(nameWithoutSuffix);
        String outputFileName = "/Users/Jing/Desktop/newworkspace/SOFTENG306-Project1-feature-branch-and-bound/tmp/" + outputFileWithoutSuffix.getName() + "-output.dot";
        FileSinkDOT fs = new FileSinkDOT(true);
        File outputFile = new File(outputFileName);
        FileOutputStream fos = null;
        try {
            outputFile.createNewFile();
            fos = new FileOutputStream(outputFile);
            fs.writeAll(inputGraph, fos);
            System.out.println("Output file saved to: " + outputFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

	public Graph getVisGraph() {
		
		return this.gVis;
	}
}