package scheduler;//####[1]####
//####[1]####
import java.util.ArrayList;//####[3]####
import org.graphstream.graph.Graph;//####[4]####
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
public class Greedy {//####[6]####
    static{ParaTask.init();}//####[6]####
    /*  ParaTask helper method to access private/protected slots *///####[6]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[6]####
        if (m.getParameterTypes().length == 0)//####[6]####
            m.invoke(instance);//####[6]####
        else if ((m.getParameterTypes().length == 1))//####[6]####
            m.invoke(instance, arg);//####[6]####
        else //####[6]####
            m.invoke(instance, arg, interResult);//####[6]####
    }//####[6]####
//####[15]####
    /**
	 * Greedy Search searches through the graph, according to the queue, adding nodes to the schedule that will increment each schedule time by the least amount
	 * Then adds the process-able nodes to the queue and goes through the queue again
	 * @param g : input graph
	 * @param procCount : amount of processors
	 * @returns : when gone through whole queue (i.e. all nodes in graph)
	 *///####[15]####
    public Schedule greedySearch(Graph g, int procCount, ArrayList<Integer> root) {//####[15]####
        Schedule schedule = new Schedule(procCount);//####[17]####
        ArrayList<QueueItem> queue = new ArrayList<QueueItem>();//####[18]####
        for (Integer i : root) //####[20]####
        {//####[20]####
            for (int j = 0; j < procCount; j++) //####[21]####
            {//####[21]####
                queue.add(new QueueItem(i, j));//####[22]####
            }//####[23]####
        }//####[24]####
        QueueItem smallest = queue.get(0);//####[26]####
        for (int i = 0; i < queue.size(); i++) //####[28]####
        {//####[28]####
            QueueItem q = queue.get(i);//####[29]####
            if (ScheduleHelper.getNodeWeight(g, q.nodeIndex) <= ScheduleHelper.getNodeWeight(g, smallest.nodeIndex)) //####[30]####
            {//####[30]####
                smallest = q;//####[31]####
            }//####[32]####
        }//####[33]####
        for (int popIndex = queue.size() - 1; popIndex > -1; popIndex--) //####[36]####
        {//####[36]####
            if (queue.get(popIndex).nodeIndex == smallest.nodeIndex) //####[37]####
            {//####[37]####
                queue.remove(popIndex);//####[38]####
            }//####[39]####
        }//####[40]####
        schedule.addNode(g.getNode(smallest.nodeIndex), smallest.Processor, 0);//####[42]####
        schedule.updateProcessorLength(smallest.Processor, ScheduleHelper.getNodeWeight(g, smallest.nodeIndex));//####[43]####
        ArrayList<Integer> childrenNodes = ScheduleHelper.processableNodes(g, smallest.nodeIndex);//####[50]####
        for (int i : childrenNodes) //####[51]####
        {//####[51]####
            for (int j = 0; j < procCount; j++) //####[52]####
            {//####[52]####
                queue.add(new QueueItem(i, j));//####[53]####
            }//####[54]####
        }//####[55]####
        while (!queue.isEmpty()) //####[58]####
        {//####[58]####
            int[] procInfo;//####[60]####
            int procWaitTime = 0;//####[61]####
            int newProcLength;//####[62]####
            int scheduleLength;//####[63]####
            int smallestWeightChange = 2147483647;//####[64]####
            int processorWeightInc = 0;//####[65]####
            for (int i = 0; i < queue.size(); i++) //####[67]####
            {//####[67]####
                QueueItem q = queue.get(i);//####[68]####
                procInfo = ScheduleHelper.scheduleNode(schedule, q, g);//####[69]####
                newProcLength = procInfo[0];//####[70]####
                scheduleLength = schedule.findScheduleLength();//####[71]####
                if (newProcLength - scheduleLength <= smallestWeightChange) //####[73]####
                {//####[73]####
                    procWaitTime = procInfo[1];//####[74]####
                    smallest = q;//####[75]####
                    smallestWeightChange = newProcLength - scheduleLength;//####[76]####
                    processorWeightInc = newProcLength - schedule.procLengths[q.Processor];//####[77]####
                }//####[80]####
            }//####[81]####
            for (int popIndex = queue.size() - 1; popIndex > -1; popIndex--) //####[83]####
            {//####[83]####
                if (queue.get(popIndex).nodeIndex == smallest.nodeIndex) //####[84]####
                {//####[84]####
                    queue.remove(popIndex);//####[85]####
                }//####[86]####
            }//####[87]####
            schedule.addNode(g.getNode(smallest.nodeIndex), smallest.Processor, procWaitTime);//####[90]####
            schedule.updateProcessorLength(smallest.Processor, processorWeightInc);//####[91]####
            childrenNodes = ScheduleHelper.processableNodes(g, smallest.nodeIndex);//####[95]####
            for (int i : childrenNodes) //####[96]####
            {//####[96]####
                for (int j = 0; j < procCount; j++) //####[97]####
                {//####[97]####
                    queue.add(new QueueItem(i, j));//####[98]####
                }//####[99]####
            }//####[100]####
        }//####[103]####
        return schedule;//####[112]####
    }//####[113]####
//####[119]####
    /**
	 * This class is used by processable nodes method
	 * Adds items to the queue, adding node index and processor ID
	 *///####[119]####
    public class QueueItem {//####[119]####
//####[119]####
        /*  ParaTask helper method to access private/protected slots *///####[119]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[119]####
            if (m.getParameterTypes().length == 0)//####[119]####
                m.invoke(instance);//####[119]####
            else if ((m.getParameterTypes().length == 1))//####[119]####
                m.invoke(instance, arg);//####[119]####
            else //####[119]####
                m.invoke(instance, arg, interResult);//####[119]####
        }//####[119]####
//####[120]####
        public int nodeIndex;//####[120]####
//####[121]####
        public int Processor;//####[121]####
//####[123]####
        public QueueItem(int n) {//####[123]####
            nodeIndex = n;//####[124]####
            Processor = -1;//####[125]####
        }//####[126]####
//####[127]####
        public QueueItem(int n, int p) {//####[127]####
            nodeIndex = n;//####[128]####
            Processor = p;//####[129]####
        }//####[130]####
    }//####[130]####
}//####[130]####
