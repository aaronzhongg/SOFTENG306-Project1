package util;

import java.io.*;

/**
 * Created by jay on 1/08/16.
 * This is just a skeleton class that I've created to test some input and output of the DOT format file. All utility classes
 * should be included in the "util" package.
 */
public class io {

    public static void main(String[] args){

        File input_file = new File("D:\\Uni Work\\SE306\\input.dot");
        processInput(input_file);
    }
    public static void processInput(File input_file) {
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(input_file));
            br.readLine();
            while ((line = br.readLine()) != null){
                String[] edge = line.split("\\[");
                System.out.println("Edge/Node: " + edge[0]);
                if(edge.length > 1) {
                    int weight = Integer.parseInt(edge[1].replaceAll("[^0-9]", ""));
                    System.out.println(weight);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}