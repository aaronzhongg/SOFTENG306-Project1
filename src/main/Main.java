package main;//####[1]####
//####[1]####
import java.io.File;//####[3]####
import java.util.ArrayList;//####[4]####
import org.graphstream.graph.*;//####[6]####
import org.graphstream.graph.implementations.Graphs;//####[7]####
import scheduler.*;//####[9]####
import ui.MainView;//####[10]####
import util.io;//####[11]####
import ui.Update;//####[12]####
//####[12]####
//-- ParaTask related imports//####[12]####
import pt.runtime.*;//####[12]####
import java.util.concurrent.ExecutionException;//####[12]####
import java.util.concurrent.locks.*;//####[12]####
import java.lang.reflect.*;//####[12]####
import pt.runtime.GuiThread;//####[12]####
import java.util.concurrent.BlockingQueue;//####[12]####
import java.util.ArrayList;//####[12]####
import java.util.List;//####[12]####
//####[12]####
/**
 * Created by jay on 5/08/16.
 *///####[16]####
public class Main {//####[17]####
    static{ParaTask.init();}//####[17]####
    /*  ParaTask helper method to access private/protected slots *///####[17]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[17]####
        if (m.getParameterTypes().length == 0)//####[17]####
            m.invoke(instance);//####[17]####
        else if ((m.getParameterTypes().length == 1))//####[17]####
            m.invoke(instance, arg);//####[17]####
        else //####[17]####
            m.invoke(instance, arg, interResult);//####[17]####
    }//####[17]####
//####[19]####
    public static int processorInput;//####[19]####
//####[21]####
    public static void main(String[] args) {//####[21]####
        String inputFile = "TestDotFiles/Nodes_10_Random.dot";//####[33]####
        int processorInput = 2;//####[34]####
        File input_file = new File(inputFile);//####[36]####
        io IOProcessor = new io();//####[37]####
        Schedule schedule;//####[38]####
        Graph g = IOProcessor.DOTParser(input_file, inputFile);//####[41]####
        ArrayList<Integer> rootnodes = ScheduleHelper.findRootNodes(g);//####[42]####
        Greedy greedy = new Greedy();//####[50]####
        schedule = greedy.greedySearch(g, processorInput, rootnodes);//####[51]####
        ScheduleHelper.currentBestSchedule = new Schedule(schedule.schedule, schedule.procLengths, schedule.scheduleLength);//####[52]####
        ScheduleHelper.bestGraph = Graphs.clone(g);//####[53]####
        BranchAndBound bnb = new BranchAndBound(schedule, g);//####[62]####
        ScheduleHelper.makeDependencyMatrix(g);//####[63]####
        bnb.branchAndBoundAlgorithm();//####[64]####
        for (Node n : ScheduleHelper.bestGraph) //####[70]####
        {//####[70]####
            System.out.println("Node id: " + n.getId() + " ProcID: " + n.getAttribute("Processor") + " Starts at: " + n.getAttribute("Start") + " Node Weight: " + n.getAttribute("Weight"));//####[71]####
        }//####[72]####
        System.out.println("Total Schedule Length: " + ScheduleHelper.currentBestSchedule.scheduleLength);//####[73]####
        IOProcessor.outputFile(schedule, ScheduleHelper.bestGraph, inputFile);//####[74]####
    }//####[78]####
}//####[78]####
