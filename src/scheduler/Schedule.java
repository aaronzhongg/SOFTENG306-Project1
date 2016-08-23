package scheduler;//####[1]####
//####[1]####
import java.util.ArrayList;//####[3]####
import org.graphstream.graph.*;//####[4]####
//####[4]####
//-- ParaTask related imports//####[4]####
import pt.runtime.*;//####[4]####
import java.util.concurrent.ExecutionException;//####[4]####
import java.util.concurrent.locks.*;//####[4]####
import java.lang.reflect.*;//####[4]####
import pt.runtime.GuiThread;//####[4]####
import java.util.concurrent.BlockingQueue;//####[4]####
import java.util.ArrayList;//####[4]####
import java.util.List;//####[4]####
//####[4]####
/**
 * This is the schedule data structure.
 * This stores the nodes of the graph with their assigned processor ids
 * The order of the schedule should be the order the tasks are scheduled
 * @author Alex
 *///####[11]####
public class Schedule {//####[12]####
    static{ParaTask.init();}//####[12]####
    /*  ParaTask helper method to access private/protected slots *///####[12]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[12]####
        if (m.getParameterTypes().length == 0)//####[12]####
            m.invoke(instance);//####[12]####
        else if ((m.getParameterTypes().length == 1))//####[12]####
            m.invoke(instance, arg);//####[12]####
        else //####[12]####
            m.invoke(instance, arg, interResult);//####[12]####
    }//####[12]####
//####[14]####
    public ArrayList<Node> schedule;//####[14]####
//####[15]####
    public int[] procLengths;//####[15]####
//####[16]####
    public int scheduleLength;//####[16]####
//####[21]####
    /**
	 * CONSTRUCTORS: makes empty schedules
	 *///####[21]####
    public Schedule() {//####[21]####
        schedule = new ArrayList<Node>();//####[22]####
        procLengths = new int[1];//####[23]####
        scheduleLength = 0;//####[24]####
    }//####[25]####
//####[27]####
    public Schedule(int procCount) {//####[27]####
        schedule = new ArrayList<Node>();//####[28]####
        procLengths = new int[procCount];//####[29]####
        scheduleLength = 0;//####[30]####
    }//####[31]####
//####[39]####
    /**
	 * Clone a schedule for calculation purposes
	 * @param s
	 * @param procl
	 * @param scheduleL
	 *///####[39]####
    public Schedule(ArrayList<Node> s, int[] procl, int scheduleL) {//####[39]####
        schedule = new ArrayList<Node>();//####[40]####
        for (Node n : s) //####[41]####
        {//####[41]####
            schedule.add(n);//####[42]####
        }//####[43]####
        procLengths = procl;//####[44]####
        scheduleLength = scheduleL;//####[45]####
    }//####[46]####
//####[54]####
    /**
	 * adds an input node to the scheduler
	 * @param integer : Node wanting to add
	 * @param Processor : the processor the node will get added to
	 * @param procWaitTime
	 *///####[54]####
    public void addNode(Node node, int Processor, int procWaitTime) {//####[54]####
        node.setAttribute("Processor", Processor);//####[55]####
        node.setAttribute("Start", procLengths[Processor] + procWaitTime);//####[56]####
        this.schedule.add(node);//####[57]####
    }//####[58]####
//####[65]####
    /**
	 * Changes the processor id for a node. This assumes the node has the Processor attribute.
	 * @param nodePosition
	 * @param Processor
	 *///####[65]####
    public void changeNodeProcessor(int nodePosition, int Processor) {//####[65]####
        schedule.get(nodePosition).setAttribute("Processor", Processor);//####[66]####
        schedule.get(nodePosition).setAttribute("Start", procLengths[Processor]);//####[67]####
    }//####[68]####
//####[74]####
    /**
	 * removes a node in a given position in the schedule
	 * @param nodePosition
	 *///####[74]####
    public void removeNode(int nodePosition) {//####[74]####
        schedule.remove(nodePosition);//####[75]####
    }//####[76]####
//####[83]####
    /**
	 * update processor lengths 
	 * @param Processor
	 * @param procIncrease
	 *///####[83]####
    public void updateProcessorLength(int Processor, int procIncrease) {//####[83]####
        procLengths[Processor] += procIncrease;//####[84]####
        scheduleLength = findScheduleLength();//####[85]####
    }//####[86]####
//####[92]####
    /**
	 * Find the current length of the schedule. This assumes the input schedule is valid.
	 * @returns the length of the schedule
	 *///####[92]####
    public int findScheduleLength() {//####[92]####
        int largestProcLength = procLengths[0];//####[93]####
        for (int proc : procLengths) //####[95]####
        {//####[95]####
            if (proc > largestProcLength) //####[96]####
            {//####[96]####
                largestProcLength = proc;//####[97]####
            }//####[98]####
        }//####[99]####
        return largestProcLength;//####[101]####
    }//####[102]####
}//####[102]####
