package util;

import java.io.*;
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

    public static void main(String[] args){

        File input_file = new File("digraph_example.dot");
        graphstream(input_file);
        //processInput(input_file);

    }
    public static void processInput(File input_file) {
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

    public static void graphstream(File input_file){
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
        g.display(true);
    }
}