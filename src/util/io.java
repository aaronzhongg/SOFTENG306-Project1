package util;

import java.io.*;
import java.util.Iterator;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDOT;

/**
 * Created by jay on 1/08/16.
 * This is just a skeleton class that I've created to test some input and output of the DOT format file. All utility classes
 * should be included in the "util" package.
 */
public class io {


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

    public Graph DOTParser(File input_file){
        Graph g = new DefaultGraph("g");
        FileSource fs = new FileSourceDOT();

        fs.addSink(g);
        
        try{
            fs.readAll("digraph_example.dot");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fs.removeSink(g);
        }
        
        //Add processorID attribute to every node
        for (int i = 0; i < g.getNodeCount(); i++) {
        	g.getNode(i).addAttribute("processorID", -1);
        }
       
        g.display();
        return g;
    }
}