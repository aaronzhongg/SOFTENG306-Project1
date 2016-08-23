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
        boolean vis = false;//####[29]####
        String outputFile = "INPUT-output";//####[30]####
        int nThreads = 0;//####[31]####
        if (args.length < 2) //####[34]####
        {//####[34]####
            System.out.println("Please ensure that all input parameters have been entered - Input file name and number of processors. ");//####[35]####
            System.exit(1);//####[36]####
        }//####[37]####
        String inputFile = args[0];//####[38]####
        processorInput = Integer.parseInt(args[1]);//####[39]####
        if (args.length > 2) //####[40]####
        {//####[40]####
            int i = 2;//####[41]####
            while (i < args.length) //####[42]####
            {//####[42]####
                if (args[i] == "-p") //####[43]####
                {//####[43]####
                    i++;//####[44]####
                    nThreads = Integer.parseInt(args[i]);//####[45]####
                } else if (args[i] == "-v") //####[46]####
                {//####[46]####
                    vis = true;//####[47]####
                } else if (args[i] == "-o") //####[48]####
                {//####[48]####
                    i++;//####[49]####
                    outputFile = args[i];//####[50]####
                }//####[51]####
                i++;//####[52]####
            }//####[53]####
        }//####[54]####
        if (vis) //####[56]####
        {//####[56]####
        }//####[58]####
        File input_file = new File(inputFile);//####[64]####
        io IOProcessor = new io();//####[65]####
        Schedule schedule;//####[66]####
        Graph g = IOProcessor.DOTParser(input_file, inputFile);//####[69]####
        ArrayList<Integer> rootnodes = ScheduleHelper.findRootNodes(g);//####[70]####
        ScheduleHelper.currentBestSchedule = new Schedule(processorInput);//####[75]####
        ScheduleHelper.bestGraph = Graphs.clone(g);//####[76]####
        ScheduleHelper.currentBestSchedule.scheduleLength = 2147483647;//####[78]####
        Greedy greedy = new Greedy();//####[79]####
        ScheduleHelper.makeDependencyMatrix(g);//####[80]####
        for (int rootNode : rootnodes) //####[84]####
        {//####[84]####
            Graph tempNewGraph = Graphs.clone(g);//####[86]####
            Schedule tempNewSchedule = new Schedule(processorInput);//####[87]####
            tempNewSchedule.addNode(tempNewGraph.getNode(rootNode), 0, 0);//####[88]####
            tempNewGraph.getNode(rootNode).setAttribute("Processor", 0);//####[89]####
            ArrayList<Integer> processableNodes = new ArrayList<Integer>();//####[91]####
            for (Node n : tempNewGraph) //####[93]####
            {//####[93]####
                if (!tempNewSchedule.schedule.contains(n)) //####[94]####
                {//####[94]####
                    boolean isProcessable = ScheduleHelper.isProcessable(n, tempNewSchedule);//####[96]####
                    if (isProcessable) //####[97]####
                    {//####[97]####
                        processableNodes.add(n.getIndex());//####[98]####
                    }//####[99]####
                }//####[100]####
            }//####[101]####
            for (int processableNodeIndex : processableNodes) //####[103]####
            {//####[103]####
                if (true) //####[106]####
                {//####[106]####
                    int tempProcessorCount = 0;//####[108]####
                    TaskIDGroup<TaskID<Void>> taskGroup = new TaskIDGroup<TaskID<Void>>(processorInput);//####[109]####
                    while (tempProcessorCount < processorInput) //####[110]####
                    {//####[110]####
                        Graph newGraph = Graphs.clone(tempNewGraph);//####[112]####
                        Schedule newSchedule = new Schedule(processorInput);//####[113]####
                        newSchedule.addNode(newGraph.getNode(rootNode), 0, 0);//####[114]####
                        newSchedule.updateProcessorLength(0, (int) Double.parseDouble(newGraph.getNode(rootNode).getAttribute("Weight").toString()));//####[115]####
                        int procWaitTime = ScheduleHelper.checkChildNode(newGraph.getNode(processableNodeIndex), newSchedule, tempProcessorCount);//####[118]####
                        if (procWaitTime > -1) //####[119]####
                        {//####[119]####
                            newSchedule.addNode(newGraph.getNode(processableNodeIndex), tempProcessorCount, procWaitTime);//####[121]####
                            newSchedule.updateProcessorLength(tempProcessorCount, procWaitTime + (int) Double.parseDouble(newGraph.getNode(processableNodeIndex).getAttribute("Weight").toString()));//####[122]####
                            TaskID<Void> id = CreateScheduleTask(newSchedule, processorInput, newGraph);//####[123]####
                            taskGroup.add(id);//####[124]####
                        }//####[125]####
                        tempProcessorCount++;//####[126]####
                    }//####[127]####
                    try {//####[128]####
                        taskGroup.waitTillFinished();//####[129]####
                    } catch (Exception e) {//####[131]####
                    }//####[131]####
                } else {//####[134]####
                    int tempProcessorCount = 0;//####[137]####
                    while (tempProcessorCount < processorInput) //####[138]####
                    {//####[138]####
                        Graph newGraph = Graphs.clone(tempNewGraph);//####[140]####
                        Schedule newSchedule = new Schedule(processorInput);//####[141]####
                        newSchedule.addNode(newGraph.getNode(rootNode), 0, 0);//####[142]####
                        newSchedule.updateProcessorLength(0, (int) Double.parseDouble(newGraph.getNode(rootNode).getAttribute("Weight").toString()));//####[143]####
                        int procWaitTime = ScheduleHelper.checkChildNode(newGraph.getNode(processableNodeIndex), newSchedule, tempProcessorCount);//####[146]####
                        if (procWaitTime > -1) //####[147]####
                        {//####[147]####
                            newSchedule.addNode(newGraph.getNode(processableNodeIndex), tempProcessorCount, procWaitTime);//####[149]####
                            newSchedule.updateProcessorLength(tempProcessorCount, procWaitTime + (int) Double.parseDouble(newGraph.getNode(processableNodeIndex).getAttribute("Weight").toString()));//####[150]####
                            CreateSchedule(newSchedule, processorInput, newGraph);//####[151]####
                        }//####[152]####
                        tempProcessorCount++;//####[153]####
                    }//####[154]####
                }//####[156]####
            }//####[159]####
            for (Node n : ScheduleHelper.bestGraph) //####[161]####
            {//####[161]####
                System.out.println("Node id: " + n.getId() + " ProcID: " + n.getAttribute("Processor") + " Starts at: " + n.getAttribute("Start") + " Node Weight: " + n.getAttribute("Weight"));//####[162]####
            }//####[163]####
            System.out.println("Total Schedule Length: " + ScheduleHelper.currentBestSchedule.scheduleLength);//####[164]####
            IOProcessor.outputFile(ScheduleHelper.currentBestSchedule, ScheduleHelper.bestGraph, inputFile, outputFile);//####[165]####
        }//####[169]####
    }//####[170]####
//####[172]####
    private static volatile Method __pt__CreateScheduleTask_Schedule_int_Graph_method = null;//####[172]####
    private synchronized static void __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet() {//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            try {//####[172]####
                __pt__CreateScheduleTask_Schedule_int_Graph_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__CreateScheduleTask", new Class[] {//####[172]####
                    Schedule.class, int.class, Graph.class//####[172]####
                });//####[172]####
            } catch (Exception e) {//####[172]####
                e.printStackTrace();//####[172]####
            }//####[172]####
        }//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, Graph graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, Graph graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setTaskIdArgIndexes(0);//####[172]####
        taskinfo.addDependsOn(schedule);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, Graph graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setQueueArgIndexes(0);//####[172]####
        taskinfo.setIsPipeline(true);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, Graph graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setTaskIdArgIndexes(1);//####[172]####
        taskinfo.addDependsOn(processorCount);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, Graph graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[172]####
        taskinfo.addDependsOn(schedule);//####[172]####
        taskinfo.addDependsOn(processorCount);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, Graph graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setQueueArgIndexes(0);//####[172]####
        taskinfo.setIsPipeline(true);//####[172]####
        taskinfo.setTaskIdArgIndexes(1);//####[172]####
        taskinfo.addDependsOn(processorCount);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setQueueArgIndexes(1);//####[172]####
        taskinfo.setIsPipeline(true);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setQueueArgIndexes(1);//####[172]####
        taskinfo.setIsPipeline(true);//####[172]####
        taskinfo.setTaskIdArgIndexes(0);//####[172]####
        taskinfo.addDependsOn(schedule);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setQueueArgIndexes(0, 1);//####[172]####
        taskinfo.setIsPipeline(true);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, TaskID<Graph> graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setTaskIdArgIndexes(2);//####[172]####
        taskinfo.addDependsOn(graph);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, TaskID<Graph> graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[172]####
        taskinfo.addDependsOn(schedule);//####[172]####
        taskinfo.addDependsOn(graph);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, TaskID<Graph> graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setQueueArgIndexes(0);//####[172]####
        taskinfo.setIsPipeline(true);//####[172]####
        taskinfo.setTaskIdArgIndexes(2);//####[172]####
        taskinfo.addDependsOn(graph);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[172]####
        taskinfo.addDependsOn(processorCount);//####[172]####
        taskinfo.addDependsOn(graph);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setTaskIdArgIndexes(0, 1, 2);//####[172]####
        taskinfo.addDependsOn(schedule);//####[172]####
        taskinfo.addDependsOn(processorCount);//####[172]####
        taskinfo.addDependsOn(graph);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setQueueArgIndexes(0);//####[172]####
        taskinfo.setIsPipeline(true);//####[172]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[172]####
        taskinfo.addDependsOn(processorCount);//####[172]####
        taskinfo.addDependsOn(graph);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setQueueArgIndexes(1);//####[172]####
        taskinfo.setIsPipeline(true);//####[172]####
        taskinfo.setTaskIdArgIndexes(2);//####[172]####
        taskinfo.addDependsOn(graph);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setQueueArgIndexes(1);//####[172]####
        taskinfo.setIsPipeline(true);//####[172]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[172]####
        taskinfo.addDependsOn(schedule);//####[172]####
        taskinfo.addDependsOn(graph);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setQueueArgIndexes(0, 1);//####[172]####
        taskinfo.setIsPipeline(true);//####[172]####
        taskinfo.setTaskIdArgIndexes(2);//####[172]####
        taskinfo.addDependsOn(graph);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, BlockingQueue<Graph> graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setQueueArgIndexes(2);//####[172]####
        taskinfo.setIsPipeline(true);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setQueueArgIndexes(2);//####[172]####
        taskinfo.setIsPipeline(true);//####[172]####
        taskinfo.setTaskIdArgIndexes(0);//####[172]####
        taskinfo.addDependsOn(schedule);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setQueueArgIndexes(0, 2);//####[172]####
        taskinfo.setIsPipeline(true);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setQueueArgIndexes(2);//####[172]####
        taskinfo.setIsPipeline(true);//####[172]####
        taskinfo.setTaskIdArgIndexes(1);//####[172]####
        taskinfo.addDependsOn(processorCount);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setQueueArgIndexes(2);//####[172]####
        taskinfo.setIsPipeline(true);//####[172]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[172]####
        taskinfo.addDependsOn(schedule);//####[172]####
        taskinfo.addDependsOn(processorCount);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setQueueArgIndexes(0, 2);//####[172]####
        taskinfo.setIsPipeline(true);//####[172]####
        taskinfo.setTaskIdArgIndexes(1);//####[172]####
        taskinfo.addDependsOn(processorCount);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setQueueArgIndexes(1, 2);//####[172]####
        taskinfo.setIsPipeline(true);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setQueueArgIndexes(1, 2);//####[172]####
        taskinfo.setIsPipeline(true);//####[172]####
        taskinfo.setTaskIdArgIndexes(0);//####[172]####
        taskinfo.addDependsOn(schedule);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[172]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[172]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[172]####
    }//####[172]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[172]####
        // ensure Method variable is set//####[172]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[172]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[172]####
        }//####[172]####
        taskinfo.setQueueArgIndexes(0, 1, 2);//####[172]####
        taskinfo.setIsPipeline(true);//####[172]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[172]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[172]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[172]####
    }//####[172]####
    public static void __pt__CreateScheduleTask(Schedule schedule, int processorCount, Graph graph) {//####[172]####
        CreateSchedule(schedule, processorCount, graph);//####[173]####
    }//####[174]####
//####[174]####
//####[177]####
    public static void CreateSchedule(Schedule schedule, int processorCount, Graph g) {//####[177]####
        Greedy greedy = new Greedy();//####[178]####
        ScheduleGraphPair sgPair = greedy.greedySearch(g, processorCount, schedule);//####[179]####
        if (sgPair.schedule.scheduleLength < ScheduleHelper.currentBestSchedule.scheduleLength) //####[183]####
        {//####[183]####
            ScheduleHelper.currentBestSchedule.scheduleLength = sgPair.schedule.scheduleLength;//####[184]####
            for (Node n : sgPair.g) //####[186]####
            {//####[186]####
                for (Node bestN : ScheduleHelper.bestGraph) //####[187]####
                {//####[187]####
                    if (n.getIndex() == bestN.getIndex()) //####[188]####
                    {//####[188]####
                        Graphs.copyAttributes(n, bestN);//####[189]####
                    }//####[190]####
                }//####[191]####
            }//####[192]####
        }//####[194]####
        BranchAndBound bnb = new BranchAndBound(sgPair.schedule, sgPair.g);//####[201]####
        bnb.branchAndBoundAlgorithm();//####[202]####
    }//####[204]####
}//####[204]####
