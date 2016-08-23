package scheduler;//####[1]####
//####[1]####
import java.util.ArrayList;//####[3]####
import org.graphstream.graph.Graph;//####[5]####
import org.graphstream.graph.Node;//####[6]####
import scheduler.Greedy.QueueItem;//####[8]####
import scheduler.Schedule;//####[9]####
import org.graphstream.graph.Edge;//####[11]####
import org.graphstream.graph.implementations.Graphs;//####[12]####
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
 * This class provides methods that the Schedule data structure can use
 *
 *///####[16]####
public class ScheduleHelper {//####[17]####
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
    public static int[][] dependencyMatrix;//####[19]####
//####[20]####
    public static Schedule currentBestSchedule;//####[20]####
//####[24]####
    public static Graph bestGraph;//####[24]####
//####[31]####
    /**
	 * This method should find all node dependencies and map them to an adjacency matrix.
	 * @param g the graph of nodes and edges
	 * @return a 2d int array of all edges between nodes
	 *///####[31]####
    public static void makeDependencyMatrix(Graph g) {//####[31]####
        dependencyMatrix = new int[g.getNodeCount()][g.getNodeCount()];//####[33]####
        for (Edge e : g.getEachEdge()) //####[35]####
        {//####[35]####
            int i = e.getNode0().getIndex();//####[36]####
            int j = e.getNode1().getIndex();//####[37]####
            dependencyMatrix[i][j] = 1;//####[38]####
        }//####[39]####
    }//####[40]####
//####[47]####
    /**
	 * Finds all the root nodes of the input graph
	 * @param g : graph
	 * @returns all the root nodes
	 *///####[47]####
    public static ArrayList<Integer> findRootNodes(Graph g) {//####[47]####
        ArrayList<Integer> rootNodes = new ArrayList<Integer>();//####[48]####
        int i = 0;//####[50]####
        for (Node n : g) //####[51]####
        {//####[51]####
            if (n.getInDegree() == 0) //####[52]####
            {//####[52]####
                rootNodes.add(i);//####[53]####
            }//####[54]####
            i++;//####[55]####
        }//####[56]####
        return rootNodes;//####[57]####
    }//####[58]####
//####[66]####
    /**
	 * Get's the weight of the input node
	 * @param g : graph
	 * @param nodeIndex : of node you want the weight of
	 * @return : weight of nodeIndex
	 *///####[66]####
    public static int getNodeWeight(Graph g, int nodeIndex) {//####[66]####
        return (int) Double.parseDouble(g.getNode(nodeIndex).getAttribute("Weight").toString());//####[67]####
    }//####[68]####
//####[78]####
    /**
	 * After a node has been processed, this method is used to return all new nodes that can be processed
	 * This is used for the Greedy algorithm. For the branch and bound algorithm use CheckChildNodes
	 * @param g
	 * @param nodeIndex
	 * @return
	 *///####[78]####
    public static ArrayList<Integer> processableNodes(Graph g, int nodeIndex) {//####[78]####
        ArrayList<Integer> processableNodes = new ArrayList<Integer>();//####[80]####
        boolean nodeProcessable;//####[81]####
        Iterable<Edge> ite = g.getNode(nodeIndex).getEachLeavingEdge();//####[83]####
        for (Edge e : ite) //####[86]####
        {//####[86]####
            Node n = e.getNode1();//####[87]####
            nodeProcessable = true;//####[88]####
            Iterable<Edge> childIte = g.getNode(n.getId()).getEachEnteringEdge();//####[91]####
            for (Edge childEdge : childIte) //####[92]####
            {//####[92]####
                Node parentNode = childEdge.getNode0();//####[93]####
                if ((int) Double.parseDouble(parentNode.getAttribute("Processor").toString()) == -1) //####[95]####
                {//####[95]####
                    nodeProcessable = false;//####[96]####
                    break;//####[97]####
                }//####[98]####
            }//####[99]####
            if (nodeProcessable == true) //####[101]####
            {//####[101]####
                processableNodes.add(n.getIndex());//####[102]####
            }//####[103]####
        }//####[104]####
        return processableNodes;//####[105]####
    }//####[106]####
//####[115]####
    /**
	 * Returns the cost of putting the queue item into the processor
	 * @param schedule
	 * @param q : item in the queue
	 * @param g : graph
	 * @return
	 *///####[115]####
    public static int[] scheduleNode(Schedule schedule, QueueItem q, Graph g) {//####[115]####
        int minimumProcLength;//####[117]####
        int procWaitTime = 0;//####[118]####
        int nodeWeight = getNodeWeight(g, q.nodeIndex);//####[119]####
        ArrayList<Integer> parentNodeCosts = new ArrayList<Integer>();//####[121]####
        ArrayList<Node> parentNodes = new ArrayList<Node>();//####[122]####
        if (g.getNode(q.nodeIndex).getInDegree() != 0) //####[125]####
        {//####[125]####
            int parentNodeFinishedProcessing = 0;//####[126]####
            for (Edge e : g.getNode(q.nodeIndex).getEachEnteringEdge()) //####[128]####
            {//####[128]####
                Node parentNode = e.getNode0();//####[129]####
                int tempValue = (int) Double.parseDouble(parentNode.getAttribute("Start").toString()) + getNodeWeight(g, parentNode.getIndex());//####[130]####
                if (tempValue > parentNodeFinishedProcessing) //####[131]####
                {//####[131]####
                    parentNodeFinishedProcessing = tempValue;//####[132]####
                }//####[133]####
            }//####[134]####
            for (Edge e : g.getNode(q.nodeIndex).getEachEnteringEdge()) //####[136]####
            {//####[136]####
                Node parentNode = e.getNode0();//####[137]####
                int parentProcessor = (int) Double.parseDouble(parentNode.getAttribute("Processor").toString());//####[138]####
                int edgeWeight = (int) Double.parseDouble(e.getAttribute("Weight").toString());//####[139]####
                if (q.Processor == parentProcessor) //####[142]####
                {//####[142]####
                    edgeWeight = 0;//####[143]####
                }//####[144]####
                int currentParentNodeFinish = (int) Double.parseDouble(parentNode.getAttribute("Start").toString()) + getNodeWeight(g, parentNode.getIndex());//####[147]####
                if (parentNodeFinishedProcessing > currentParentNodeFinish + edgeWeight) //####[148]####
                {//####[148]####
                    edgeWeight = 0;//####[149]####
                }//####[150]####
                if (schedule.procLengths[q.Processor] - parentNodeFinishedProcessing >= edgeWeight) //####[153]####
                {//####[153]####
                    parentNodeCosts.add(schedule.procLengths[q.Processor] + nodeWeight);//####[155]####
                    parentNodes.add(parentNode);//####[156]####
                } else {//####[158]####
                    int timeToWait = edgeWeight - (schedule.procLengths[q.Processor] - currentParentNodeFinish);//####[161]####
                    if (timeToWait < 0) //####[163]####
                    {//####[163]####
                        timeToWait = 0;//####[164]####
                    }//####[165]####
                    parentNodeCosts.add(schedule.procLengths[q.Processor] + nodeWeight + timeToWait);//####[166]####
                    parentNodes.add(parentNode);//####[167]####
                }//####[168]####
            }//####[170]####
            minimumProcLength = parentNodeCosts.get(0);//####[172]####
            for (int i = 0; i < parentNodeCosts.size(); i++) //####[175]####
            {//####[175]####
                int pNodeCost = parentNodeCosts.get(i);//####[176]####
                if (pNodeCost > minimumProcLength) //####[177]####
                {//####[177]####
                    minimumProcLength = pNodeCost;//####[178]####
                }//####[180]####
            }//####[181]####
            procWaitTime = minimumProcLength - nodeWeight - schedule.procLengths[q.Processor];//####[185]####
        } else {//####[187]####
            minimumProcLength = getNodeWeight(g, q.nodeIndex) + schedule.procLengths[q.Processor];//####[188]####
        }//####[189]####
        int[] newProcLengthAndTimeToWait = { minimumProcLength, procWaitTime };//####[191]####
        return newProcLengthAndTimeToWait;//####[192]####
    }//####[194]####
//####[204]####
    /**
	 * Check whether a node is processable (all of it's parents exist in the currentSchedule) check dependency from dependency matrix (from ScheduleHelper)
	 * nodeToCheck.getIndex() gives you the index. dependencyMatrix[i][j] i is parent j is child (nodeToCheck is the child)
	 * @param nodeToBeChecked
	 * @param currentSchedule
	 * @return true if nodeToBeChecked is processable (all of it's parents exist in the schedule), false otherwise
	 * 
	 *///####[204]####
    public static boolean isProcessable(Node nodeToBeChecked, Schedule currentSchedule) {//####[204]####
        int indexOfCheckNode = nodeToBeChecked.getIndex();//####[205]####
        boolean nodeProcessable = true;//####[207]####
        for (int i = 0; i < dependencyMatrix[0].length; i++) //####[209]####
        {//####[209]####
            if (dependencyMatrix[i][indexOfCheckNode] == 1) //####[210]####
            {//####[210]####
                boolean parentInSchedule = false;//####[212]####
                for (Node parent : currentSchedule.schedule) //####[214]####
                {//####[214]####
                    if (parent.getIndex() == i) //####[215]####
                    {//####[215]####
                        parentInSchedule = true;//####[216]####
                        break;//####[217]####
                    }//####[218]####
                }//####[219]####
                if (!parentInSchedule) //####[221]####
                {//####[221]####
                    nodeProcessable = false;//####[222]####
                    break;//####[223]####
                }//####[224]####
            }//####[225]####
        }//####[226]####
        return nodeProcessable;//####[228]####
    }//####[229]####
//####[238]####
    /**
	 * Replace the current best schedule with the new best schedule and replace the current best bound with new best bound (bound = schedule length)
	 * @param newBestSchedule
	 * @return return a copy of the newBestSchedule
	 * 
	 * could just call this one line from Branch instead of having to call this function
	 *///####[238]####
    public static void foundNewBestSolution(Schedule newBestSchedule, Graph g) {//####[238]####
        currentBestSchedule = new Schedule(newBestSchedule.schedule, newBestSchedule.procLengths, newBestSchedule.scheduleLength);//####[239]####
        for (Node n : g) //####[240]####
        {//####[240]####
            for (Node bestN : bestGraph) //####[241]####
            {//####[241]####
                if (n.getIndex() == bestN.getIndex()) //####[242]####
                {//####[242]####
                    Graphs.copyAttributes(n, bestN);//####[243]####
                }//####[244]####
            }//####[245]####
        }//####[246]####
        return;//####[247]####
    }//####[248]####
//####[256]####
    /**
	 * Check the cost of adding the child node into the schedule 
	 * @param node
	 * @param schedule (current best schedule)
	 * @return true if schedule time after adding the node is less than current best total schedule time
	 *///####[256]####
    public static int checkChildNode(Node node, Schedule schedule, int processorID) {//####[256]####
        ArrayList<Node> parentNodes = new ArrayList<Node>();//####[259]####
        for (Edge e : node.getEachEnteringEdge()) //####[260]####
        {//####[260]####
            Node parentNode = e.getNode0();//####[261]####
            parentNodes.add(parentNode);//####[262]####
        }//####[263]####
        int canStartat = -1;//####[265]####
        int tempValue;//####[266]####
        int edgeWeight;//####[267]####
        int timeLeftToWait = 0;//####[268]####
        int tempTimeToWait;//####[269]####
        for (Node parent : parentNodes) //####[271]####
        {//####[271]####
            int parentProcessor = (int) Double.parseDouble(parent.getAttribute("Processor").toString());//####[273]####
            if (parentProcessor == processorID) //####[275]####
            {//####[275]####
                tempValue = schedule.procLengths[processorID];//####[276]####
                tempTimeToWait = 0;//####[277]####
            } else {//####[279]####
                Edge parentToChild = parent.getEdgeToward(node);//####[281]####
                edgeWeight = (int) Double.parseDouble(parentToChild.getAttribute("Weight").toString());//####[282]####
                int lengthCurrentProcessor = schedule.procLengths[processorID];//####[283]####
                int endTime = (int) Double.parseDouble(parent.getAttribute("Start").toString()) + (int) Double.parseDouble(parent.getAttribute("Weight").toString());//####[284]####
                int timeWaited = lengthCurrentProcessor - endTime;//####[285]####
                tempTimeToWait = edgeWeight - timeWaited;//####[286]####
                if (tempTimeToWait < 0) //####[289]####
                {//####[289]####
                    tempTimeToWait = 0;//####[290]####
                }//####[291]####
                tempValue = lengthCurrentProcessor + tempTimeToWait;//####[292]####
            }//####[293]####
            if (tempValue > canStartat) //####[295]####
            {//####[295]####
                canStartat = tempValue;//####[296]####
            }//####[297]####
            if (tempTimeToWait > timeLeftToWait) //####[299]####
            {//####[299]####
                timeLeftToWait = tempTimeToWait;//####[300]####
            }//####[301]####
        }//####[302]####
        int procLength = canStartat + (int) Double.parseDouble(node.getAttribute("Weight").toString());//####[304]####
        for (int i : schedule.procLengths) //####[305]####
        {//####[305]####
            if (i > procLength) //####[306]####
            {//####[306]####
                procLength = i;//####[307]####
            }//####[308]####
        }//####[309]####
        if (procLength >= currentBestSchedule.scheduleLength) //####[311]####
        {//####[311]####
            return -1;//####[312]####
        } else {//####[313]####
            return timeLeftToWait;//####[314]####
        }//####[315]####
    }//####[316]####
//####[325]####
    /**
	 * Insert the node into schedule 
	 * @param nodeToInsert
	 * @param currentSchedule
	 * 
	 * could just call this one line from Branch instead of having to call this function
	 *///####[325]####
    public static void insertNodeToSchedule(Node nodeToInsert, Schedule currentSchedule, int Processor, int procWaitTime) {//####[325]####
        currentSchedule.addNode(nodeToInsert, Processor, procWaitTime);//####[326]####
        currentSchedule.updateProcessorLength(Processor, (int) Double.parseDouble(nodeToInsert.getAttribute("Weight").toString()) + procWaitTime);//####[327]####
    }//####[328]####
}//####[328]####
