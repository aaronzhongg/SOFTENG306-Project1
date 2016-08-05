package main;

import java.io.File;

import org.graphstream.graph.Graph;
import util.*;

/**
 * Created by jay on 5/08/16.
 */
public class Main {
    public static void main(String[] args){

        File input_file = new File("digraph_example.dot");
        io IOProcessor = new io();
        Graph g = IOProcessor.DOTParser(input_file);
    }
}
