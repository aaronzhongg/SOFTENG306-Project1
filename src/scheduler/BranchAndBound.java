package scheduler;//####[1]####
//####[1]####
import org.graphstream.graph.Graph;//####[2]####
import org.graphstream.graph.Node;//####[3]####
import java.util.ArrayList;//####[5]####
//####[5]####
//-- ParaTask related imports//####[5]####
import pt.runtime.*;//####[5]####
import java.util.concurrent.ExecutionException;//####[5]####
import java.util.concurrent.locks.*;//####[5]####
import java.lang.reflect.*;//####[5]####
import pt.runtime.GuiThread;//####[5]####
import java.util.concurrent.BlockingQueue;//####[5]####
import java.util.ArrayList;//####[5]####
import java.util.List;//####[5]####
//####[5]####
public class BranchAndBound {//####[7]####
    static{ParaTask.init();}//####[7]####
    /*  ParaTask helper method to access private/protected slots *///####[7]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[7]####
        if (m.getParameterTypes().length == 0)//####[7]####
            m.invoke(instance);//####[7]####
        else if ((m.getParameterTypes().length == 1))//####[7]####
            m.invoke(instance, arg);//####[7]####
        else //####[7]####
            m.invoke(instance, arg, interResult);//####[7]####
    }//####[7]####
//####[9]####
    private Schedule currentSchedule;//####[9]####
//####[10]####
    private Graph g;//####[10]####
//####[11]####
    private Node nodeToBeRemoved = null;//####[11]####
//####[18]####
    /**
	 * Constructor to instantiate a new Schedule
	 * @param s
	 * @param g
	 *///####[18]####
    public BranchAndBound(Schedule s, Graph g) {//####[18]####
        this.currentSchedule = new Schedule(s.schedule, s.procLengths, s.scheduleLength);//####[19]####
        this.g = g;//####[20]####
    }//####[21]####
//####[31]####
    /**
	 * The function that should initialise the branch and bound algorithm, While branch is giving a false (ie no better solution), loop the following:
	 * Take away last node from schedule, if this is the first node of the schedule, then check if any other root nodes that have not been processed as the first node in the schedule.
 	 * If all root nodes have being processed, then we have the optimal solution hopefully.
	 * @param schedule
	 * @param g
	 * @return
	 *///####[31]####
    public void branchAndBoundAlgorithm() {//####[31]####
        ArrayList<Node> rootNodes = new ArrayList<Node>();//####[33]####
        ArrayList<Integer> rootNodeIDs = ScheduleHelper.findRootNodes(g);//####[34]####
        for (int i : rootNodeIDs) //####[36]####
        {//####[36]####
            rootNodes.add(g.getNode(i));//####[37]####
        }//####[38]####
        while (Branch(new Schedule(currentSchedule.schedule, currentSchedule.procLengths, currentSchedule.scheduleLength)) == false) //####[41]####
        {//####[41]####
            nodeToBeRemoved = currentSchedule.schedule.get(currentSchedule.schedule.size() - 1);//####[43]####
            currentSchedule.removeNode(currentSchedule.schedule.size() - 1);//####[44]####
            updateRemoveLengthChanges(currentSchedule, nodeToBeRemoved);//####[45]####
            if (currentSchedule.schedule.isEmpty()) //####[47]####
            {//####[47]####
                rootNodes.remove(nodeToBeRemoved);//####[50]####
                if (rootNodes.isEmpty()) //####[51]####
                {//####[51]####
                    return;//####[53]####
                }//####[54]####
                currentSchedule.addNode(rootNodes.get(0), 0, 0);//####[56]####
                currentSchedule.updateProcessorLength(0, (int) Double.parseDouble(rootNodes.get(0).getAttribute("Weight").toString()));//####[57]####
            }//####[58]####
        }//####[59]####
    }//####[60]####
//####[70]####
    /**
	 * Recursive function, finds all processable nodes, for each processable children nodes, check how much the schedule increases when trying to add them to each of the processors,
	 * if the schedule time after adding to that processor is less than the current best schedule time (currentBestSchedule.scheduleLength) then insert node into the current schedule
	 * and recursively call Branch with the current schedule (this should be done for each of the nodes/processors that produce a lower schedule length)
	 * if the path is larger then return false, if no more nodes then return true.
	 * 
	 * @return false
	 *///####[70]####
    public boolean Branch(Schedule branchingSchedule) {//####[70]####
        boolean hasInserted = false;//####[72]####
        for (Node n : g) //####[74]####
        {//####[74]####
            if (!branchingSchedule.schedule.contains(n)) //####[75]####
            {//####[75]####
                boolean isProcessable = ScheduleHelper.isProcessable(n, branchingSchedule);//####[77]####
                if (isProcessable) //####[78]####
                {//####[78]####
                    for (int i = 0; i < branchingSchedule.procLengths.length; i++) //####[80]####
                    {//####[80]####
                        int timeToWait = ScheduleHelper.checkChildNode(n, branchingSchedule, i);//####[82]####
                        if (timeToWait > -1) //####[84]####
                        {//####[84]####
                            hasInserted = true;//####[86]####
                            ScheduleHelper.insertNodeToSchedule(n, branchingSchedule, i, timeToWait);//####[87]####
                            Branch(new Schedule(branchingSchedule.schedule, branchingSchedule.procLengths, branchingSchedule.scheduleLength));//####[90]####
                            branchingSchedule.removeNode(branchingSchedule.schedule.size() - 1);//####[91]####
                            updateRemoveLengthChanges(branchingSchedule, n);//####[93]####
                        }//####[94]####
                    }//####[95]####
                }//####[96]####
            }//####[97]####
        }//####[98]####
        if (branchingSchedule.schedule.size() == g.getNodeCount()) //####[100]####
        {//####[100]####
            ScheduleHelper.foundNewBestSolution(branchingSchedule, g);//####[101]####
        }//####[102]####
        if (!hasInserted) //####[104]####
        {//####[104]####
            return false;//####[105]####
        }//####[106]####
        return false;//####[108]####
    }//####[109]####
//####[116]####
    /**
	 * 
	 * @param s
	 * @param removeNode
	 *///####[116]####
    public void updateRemoveLengthChanges(Schedule s, Node removeNode) {//####[116]####
        int updatedScheduleLength = 0;//####[117]####
        for (int i = s.schedule.size() - 1; i > -1; i--) //####[119]####
        {//####[119]####
            Node n = s.schedule.get(i);//####[120]####
            int processedOn = (int) Double.parseDouble(n.getAttribute("Processor").toString());//####[123]####
            if (processedOn == (int) Double.parseDouble(removeNode.getAttribute("Processor").toString())) //####[124]####
            {//####[124]####
                s.procLengths[processedOn] = ScheduleHelper.getNodeWeight(g, n.getIndex()) + (int) Double.parseDouble(n.getAttribute("Start").toString());//####[127]####
                s.scheduleLength = s.findScheduleLength();//####[128]####
                updatedScheduleLength = 1;//####[129]####
                break;//####[130]####
            }//####[131]####
        }//####[132]####
        if (updatedScheduleLength == 0) //####[134]####
        {//####[134]####
            s.procLengths[(int) Double.parseDouble(removeNode.getAttribute("Processor").toString())] = 0;//####[135]####
            s.scheduleLength = s.findScheduleLength();//####[136]####
        }//####[137]####
    }//####[138]####
}//####[138]####
