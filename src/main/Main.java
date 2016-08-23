package main;//####[1]####
//####[1]####
import java.io.File;//####[3]####
import java.util.ArrayList;//####[4]####
import java.util.*;//####[5]####
import org.graphstream.graph.*;//####[7]####
import org.graphstream.graph.implementations.Graphs;//####[8]####
import pt.runtime.TaskID;//####[10]####
import pt.runtime.TaskIDGroup;//####[11]####
import scheduler.*;//####[13]####
import scheduler.Greedy.QueueItem;//####[14]####
import scheduler.Greedy.ScheduleGraphPair;//####[15]####
import ui.MainView;//####[16]####
import util.io;//####[17]####
import ui.Update;//####[18]####
//####[18]####
//-- ParaTask related imports//####[18]####
import pt.runtime.*;//####[18]####
import java.util.concurrent.ExecutionException;//####[18]####
import java.util.concurrent.locks.*;//####[18]####
import java.lang.reflect.*;//####[18]####
import pt.runtime.GuiThread;//####[18]####
import java.util.concurrent.BlockingQueue;//####[18]####
import java.util.ArrayList;//####[18]####
import java.util.List;//####[18]####
//####[18]####
/**
 * Created by jay on 5/08/16.
 *///####[22]####
public class Main {//####[23]####
    static{ParaTask.init();}//####[23]####
    /*  ParaTask helper method to access private/protected slots *///####[23]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[23]####
        if (m.getParameterTypes().length == 0)//####[23]####
            m.invoke(instance);//####[23]####
        else if ((m.getParameterTypes().length == 1))//####[23]####
            m.invoke(instance, arg);//####[23]####
        else //####[23]####
            m.invoke(instance, arg, interResult);//####[23]####
    }//####[23]####
//####[25]####
    public static int processorInput;//####[25]####
//####[27]####
    public static void main(String[] args) {//####[27]####
        String inputFile = "TestDotFiles/Nodes_10_Random.dot";//####[38]####
        int processorInput = 4;//####[39]####
        File input_file = new File(inputFile);//####[41]####
        io IOProcessor = new io();//####[42]####
        Schedule schedule;//####[43]####
        Graph g = IOProcessor.DOTParser(input_file, inputFile);//####[46]####
        ArrayList<Integer> rootnodes = ScheduleHelper.findRootNodes(g);//####[47]####
        ScheduleHelper.currentBestSchedule = new Schedule(processorInput);//####[55]####
        ScheduleHelper.bestGraph = Graphs.clone(g);//####[56]####
        ScheduleHelper.currentBestSchedule.scheduleLength = 2147483647;//####[58]####
        Greedy greedy = new Greedy();//####[59]####
        ScheduleHelper.makeDependencyMatrix(g);//####[60]####
        for (int rootNode : rootnodes) //####[64]####
        {//####[64]####
            Graph tempNewGraph = Graphs.clone(g);//####[66]####
            Schedule tempNewSchedule = new Schedule(processorInput);//####[67]####
            tempNewSchedule.addNode(tempNewGraph.getNode(rootNode), 0, 0);//####[68]####
            tempNewGraph.getNode(rootNode).setAttribute("Processor", 0);//####[69]####
            ArrayList<Integer> processableNodes = new ArrayList<Integer>();//####[71]####
            for (Node n : tempNewGraph) //####[73]####
            {//####[73]####
                if (!tempNewSchedule.schedule.contains(n)) //####[74]####
                {//####[74]####
                    boolean isProcessable = ScheduleHelper.isProcessable(n, tempNewSchedule);//####[76]####
                    if (isProcessable) //####[77]####
                    {//####[77]####
                        processableNodes.add(n.getIndex());//####[78]####
                    }//####[79]####
                }//####[80]####
            }//####[81]####
            for (int processableNodeIndex : processableNodes) //####[83]####
            {//####[83]####
                int tempProcessorCount = 0;//####[88]####
                TaskIDGroup<TaskID<Void>> taskGroup = new TaskIDGroup<TaskID<Void>>(processorInput);//####[89]####
                while (tempProcessorCount < processorInput) //####[90]####
                {//####[90]####
                    Graph newGraph = Graphs.clone(tempNewGraph);//####[92]####
                    Schedule newSchedule = new Schedule(processorInput);//####[93]####
                    newSchedule.addNode(newGraph.getNode(rootNode), 0, 0);//####[94]####
                    newSchedule.updateProcessorLength(0, (int) Double.parseDouble(newGraph.getNode(rootNode).getAttribute("Weight").toString()));//####[95]####
                    int procWaitTime = ScheduleHelper.checkChildNode(newGraph.getNode(processableNodeIndex), newSchedule, tempProcessorCount);//####[98]####
                    if (procWaitTime > -1) //####[99]####
                    {//####[99]####
                        newSchedule.addNode(newGraph.getNode(processableNodeIndex), tempProcessorCount, procWaitTime);//####[101]####
                        newSchedule.updateProcessorLength(tempProcessorCount, procWaitTime + (int) Double.parseDouble(newGraph.getNode(processableNodeIndex).getAttribute("Weight").toString()));//####[102]####
                        TaskID<Void> id = CreateScheduleTask(newSchedule, processorInput, newGraph);//####[103]####
                        taskGroup.add(id);//####[104]####
                    }//####[105]####
                    tempProcessorCount++;//####[106]####
                }//####[108]####
                try {//####[109]####
                    taskGroup.waitTillFinished();//####[110]####
                } catch (Exception e) {//####[115]####
                }//####[116]####
            }//####[118]####
        }//####[121]####
        for (Node n : ScheduleHelper.bestGraph) //####[139]####
        {//####[139]####
            System.out.println("Node id: " + n.getId() + " ProcID: " + n.getAttribute("Processor") + " Starts at: " + n.getAttribute("Start") + " Node Weight: " + n.getAttribute("Weight"));//####[140]####
        }//####[141]####
        System.out.println("Total Schedule Length: " + ScheduleHelper.currentBestSchedule.scheduleLength);//####[142]####
    }//####[147]####
//####[149]####
    private static volatile Method __pt__CreateScheduleTask_Schedule_int_Graph_method = null;//####[149]####
    private synchronized static void __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet() {//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            try {//####[149]####
                __pt__CreateScheduleTask_Schedule_int_Graph_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__CreateScheduleTask", new Class[] {//####[149]####
                    Schedule.class, int.class, Graph.class//####[149]####
                });//####[149]####
            } catch (Exception e) {//####[149]####
                e.printStackTrace();//####[149]####
            }//####[149]####
        }//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, Graph graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, Graph graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setTaskIdArgIndexes(0);//####[149]####
        taskinfo.addDependsOn(schedule);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, Graph graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setQueueArgIndexes(0);//####[149]####
        taskinfo.setIsPipeline(true);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, Graph graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setTaskIdArgIndexes(1);//####[149]####
        taskinfo.addDependsOn(processorCount);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, Graph graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[149]####
        taskinfo.addDependsOn(schedule);//####[149]####
        taskinfo.addDependsOn(processorCount);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, Graph graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setQueueArgIndexes(0);//####[149]####
        taskinfo.setIsPipeline(true);//####[149]####
        taskinfo.setTaskIdArgIndexes(1);//####[149]####
        taskinfo.addDependsOn(processorCount);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setQueueArgIndexes(1);//####[149]####
        taskinfo.setIsPipeline(true);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setQueueArgIndexes(1);//####[149]####
        taskinfo.setIsPipeline(true);//####[149]####
        taskinfo.setTaskIdArgIndexes(0);//####[149]####
        taskinfo.addDependsOn(schedule);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setQueueArgIndexes(0, 1);//####[149]####
        taskinfo.setIsPipeline(true);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, TaskID<Graph> graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setTaskIdArgIndexes(2);//####[149]####
        taskinfo.addDependsOn(graph);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, TaskID<Graph> graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[149]####
        taskinfo.addDependsOn(schedule);//####[149]####
        taskinfo.addDependsOn(graph);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, TaskID<Graph> graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setQueueArgIndexes(0);//####[149]####
        taskinfo.setIsPipeline(true);//####[149]####
        taskinfo.setTaskIdArgIndexes(2);//####[149]####
        taskinfo.addDependsOn(graph);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[149]####
        taskinfo.addDependsOn(processorCount);//####[149]####
        taskinfo.addDependsOn(graph);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setTaskIdArgIndexes(0, 1, 2);//####[149]####
        taskinfo.addDependsOn(schedule);//####[149]####
        taskinfo.addDependsOn(processorCount);//####[149]####
        taskinfo.addDependsOn(graph);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setQueueArgIndexes(0);//####[149]####
        taskinfo.setIsPipeline(true);//####[149]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[149]####
        taskinfo.addDependsOn(processorCount);//####[149]####
        taskinfo.addDependsOn(graph);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setQueueArgIndexes(1);//####[149]####
        taskinfo.setIsPipeline(true);//####[149]####
        taskinfo.setTaskIdArgIndexes(2);//####[149]####
        taskinfo.addDependsOn(graph);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setQueueArgIndexes(1);//####[149]####
        taskinfo.setIsPipeline(true);//####[149]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[149]####
        taskinfo.addDependsOn(schedule);//####[149]####
        taskinfo.addDependsOn(graph);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setQueueArgIndexes(0, 1);//####[149]####
        taskinfo.setIsPipeline(true);//####[149]####
        taskinfo.setTaskIdArgIndexes(2);//####[149]####
        taskinfo.addDependsOn(graph);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, BlockingQueue<Graph> graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setQueueArgIndexes(2);//####[149]####
        taskinfo.setIsPipeline(true);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setQueueArgIndexes(2);//####[149]####
        taskinfo.setIsPipeline(true);//####[149]####
        taskinfo.setTaskIdArgIndexes(0);//####[149]####
        taskinfo.addDependsOn(schedule);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setQueueArgIndexes(0, 2);//####[149]####
        taskinfo.setIsPipeline(true);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setQueueArgIndexes(2);//####[149]####
        taskinfo.setIsPipeline(true);//####[149]####
        taskinfo.setTaskIdArgIndexes(1);//####[149]####
        taskinfo.addDependsOn(processorCount);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setQueueArgIndexes(2);//####[149]####
        taskinfo.setIsPipeline(true);//####[149]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[149]####
        taskinfo.addDependsOn(schedule);//####[149]####
        taskinfo.addDependsOn(processorCount);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setQueueArgIndexes(0, 2);//####[149]####
        taskinfo.setIsPipeline(true);//####[149]####
        taskinfo.setTaskIdArgIndexes(1);//####[149]####
        taskinfo.addDependsOn(processorCount);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setQueueArgIndexes(1, 2);//####[149]####
        taskinfo.setIsPipeline(true);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setQueueArgIndexes(1, 2);//####[149]####
        taskinfo.setIsPipeline(true);//####[149]####
        taskinfo.setTaskIdArgIndexes(0);//####[149]####
        taskinfo.addDependsOn(schedule);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[149]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[149]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[149]####
    }//####[149]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[149]####
        // ensure Method variable is set//####[149]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[149]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[149]####
        }//####[149]####
        taskinfo.setQueueArgIndexes(0, 1, 2);//####[149]####
        taskinfo.setIsPipeline(true);//####[149]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[149]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[149]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[149]####
    }//####[149]####
    public static void __pt__CreateScheduleTask(Schedule schedule, int processorCount, Graph graph) {//####[149]####
        CreateSchedule(schedule, processorCount, graph);//####[150]####
    }//####[151]####
//####[151]####
//####[154]####
    public static void CreateSchedule(Schedule schedule, int processorCount, Graph g) {//####[154]####
        Greedy greedy = new Greedy();//####[155]####
        ScheduleGraphPair sgPair = greedy.greedySearch(g, processorCount, schedule);//####[156]####
        if (sgPair.schedule.scheduleLength < ScheduleHelper.currentBestSchedule.scheduleLength) //####[160]####
        {//####[160]####
            ScheduleHelper.currentBestSchedule.scheduleLength = sgPair.schedule.scheduleLength;//####[161]####
            for (Node n : sgPair.g) //####[163]####
            {//####[163]####
                for (Node bestN : ScheduleHelper.bestGraph) //####[164]####
                {//####[164]####
                    if (n.getIndex() == bestN.getIndex()) //####[165]####
                    {//####[165]####
                        Graphs.copyAttributes(n, bestN);//####[166]####
                    }//####[167]####
                }//####[168]####
            }//####[169]####
        }//####[171]####
        BranchAndBound bnb = new BranchAndBound(sgPair.schedule, sgPair.g);//####[178]####
        bnb.branchAndBoundAlgorithm();//####[179]####
    }//####[181]####
}//####[181]####
