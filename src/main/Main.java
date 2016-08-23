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
        int nThreads = 0;//####[30]####
        if (args.length < 2) //####[33]####
        {//####[33]####
            System.out.println("Please ensure that all input parameters have been entered - Input file name and number of processors. ");//####[34]####
            System.exit(1);//####[35]####
        }//####[36]####
        String inputFile = args[0];//####[37]####
        String[] split = inputFile.split(".dot");//####[38]####
        String inputWithoutExtension = split[0];//####[39]####
        File input = new File(inputWithoutExtension);//####[40]####
        String inputFileName = input.getName();//####[41]####
        String outputFile = inputFileName + "-output.dot";//####[42]####
        processorInput = Integer.parseInt(args[1]);//####[43]####
        if (args.length > 2) //####[44]####
        {//####[44]####
            int i = 2;//####[45]####
            while (i < args.length) //####[46]####
            {//####[46]####
                if (args[i] == "-p") //####[47]####
                {//####[47]####
                    i++;//####[48]####
                    nThreads = Integer.parseInt(args[i]);//####[49]####
                } else if (args[i] == "-v") //####[50]####
                {//####[50]####
                    vis = true;//####[51]####
                } else if (args[i] == "-o") //####[52]####
                {//####[52]####
                    i++;//####[53]####
                    outputFile = args[i];//####[54]####
                }//####[55]####
                i++;//####[56]####
            }//####[57]####
        }//####[58]####
        if (vis) //####[60]####
        {//####[60]####
        }//####[62]####
        File input_file = new File(inputFile);//####[68]####
        io IOProcessor = new io();//####[69]####
        Schedule schedule;//####[70]####
        Graph g = IOProcessor.DOTParser(input_file, inputFile);//####[73]####
        ArrayList<Integer> rootnodes = ScheduleHelper.findRootNodes(g);//####[74]####
        ScheduleHelper.currentBestSchedule = new Schedule(processorInput);//####[79]####
        ScheduleHelper.bestGraph = Graphs.clone(g);//####[80]####
        ScheduleHelper.currentBestSchedule.scheduleLength = 2147483647;//####[82]####
        Greedy greedy = new Greedy();//####[83]####
        ScheduleHelper.makeDependencyMatrix(g);//####[84]####
        for (int rootNode : rootnodes) //####[88]####
        {//####[88]####
            Graph tempNewGraph = Graphs.clone(g);//####[90]####
            Schedule tempNewSchedule = new Schedule(processorInput);//####[91]####
            tempNewSchedule.addNode(tempNewGraph.getNode(rootNode), 0, 0);//####[92]####
            tempNewGraph.getNode(rootNode).setAttribute("Processor", 0);//####[93]####
            ArrayList<Integer> processableNodes = new ArrayList<Integer>();//####[95]####
            for (Node n : tempNewGraph) //####[97]####
            {//####[97]####
                if (!tempNewSchedule.schedule.contains(n)) //####[98]####
                {//####[98]####
                    boolean isProcessable = ScheduleHelper.isProcessable(n, tempNewSchedule);//####[100]####
                    if (isProcessable) //####[101]####
                    {//####[101]####
                        processableNodes.add(n.getIndex());//####[102]####
                    }//####[103]####
                }//####[104]####
            }//####[105]####
            for (int processableNodeIndex : processableNodes) //####[107]####
            {//####[107]####
                if (true) //####[110]####
                {//####[110]####
                    int tempProcessorCount = 0;//####[112]####
                    TaskIDGroup<TaskID<Void>> taskGroup = new TaskIDGroup<TaskID<Void>>(processorInput);//####[113]####
                    while (tempProcessorCount < processorInput) //####[114]####
                    {//####[114]####
                        Graph newGraph = Graphs.clone(tempNewGraph);//####[116]####
                        Schedule newSchedule = new Schedule(processorInput);//####[117]####
                        newSchedule.addNode(newGraph.getNode(rootNode), 0, 0);//####[118]####
                        newSchedule.updateProcessorLength(0, (int) Double.parseDouble(newGraph.getNode(rootNode).getAttribute("Weight").toString()));//####[119]####
                        int procWaitTime = ScheduleHelper.checkChildNode(newGraph.getNode(processableNodeIndex), newSchedule, tempProcessorCount);//####[122]####
                        if (procWaitTime > -1) //####[123]####
                        {//####[123]####
                            newSchedule.addNode(newGraph.getNode(processableNodeIndex), tempProcessorCount, procWaitTime);//####[125]####
                            newSchedule.updateProcessorLength(tempProcessorCount, procWaitTime + (int) Double.parseDouble(newGraph.getNode(processableNodeIndex).getAttribute("Weight").toString()));//####[126]####
                            TaskID<Void> id = CreateScheduleTask(newSchedule, processorInput, newGraph);//####[127]####
                            taskGroup.add(id);//####[128]####
                        }//####[129]####
                        tempProcessorCount++;//####[130]####
                    }//####[131]####
                    try {//####[132]####
                        taskGroup.waitTillFinished();//####[133]####
                    } catch (Exception e) {//####[135]####
                    }//####[135]####
                } else {//####[138]####
                    int tempProcessorCount = 0;//####[141]####
                    while (tempProcessorCount < processorInput) //####[142]####
                    {//####[142]####
                        Graph newGraph = Graphs.clone(tempNewGraph);//####[144]####
                        Schedule newSchedule = new Schedule(processorInput);//####[145]####
                        newSchedule.addNode(newGraph.getNode(rootNode), 0, 0);//####[146]####
                        newSchedule.updateProcessorLength(0, (int) Double.parseDouble(newGraph.getNode(rootNode).getAttribute("Weight").toString()));//####[147]####
                        int procWaitTime = ScheduleHelper.checkChildNode(newGraph.getNode(processableNodeIndex), newSchedule, tempProcessorCount);//####[150]####
                        if (procWaitTime > -1) //####[151]####
                        {//####[151]####
                            newSchedule.addNode(newGraph.getNode(processableNodeIndex), tempProcessorCount, procWaitTime);//####[153]####
                            newSchedule.updateProcessorLength(tempProcessorCount, procWaitTime + (int) Double.parseDouble(newGraph.getNode(processableNodeIndex).getAttribute("Weight").toString()));//####[154]####
                            CreateSchedule(newSchedule, processorInput, newGraph);//####[155]####
                        }//####[156]####
                        tempProcessorCount++;//####[157]####
                    }//####[158]####
                }//####[160]####
            }//####[161]####
            for (Node n : ScheduleHelper.bestGraph) //####[163]####
            {//####[163]####
                System.out.println("Node id: " + n.getId() + " ProcID: " + n.getAttribute("Processor") + " Starts at: " + n.getAttribute("Start") + " Node Weight: " + n.getAttribute("Weight"));//####[164]####
            }//####[165]####
            System.out.println("Total Schedule Length: " + ScheduleHelper.currentBestSchedule.scheduleLength);//####[166]####
            IOProcessor.outputFile(ScheduleHelper.currentBestSchedule, ScheduleHelper.bestGraph, outputFile);//####[167]####
        }//####[171]####
    }//####[172]####
//####[174]####
    private static volatile Method __pt__CreateScheduleTask_Schedule_int_Graph_method = null;//####[174]####
    private synchronized static void __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet() {//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            try {//####[174]####
                __pt__CreateScheduleTask_Schedule_int_Graph_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__CreateScheduleTask", new Class[] {//####[174]####
                    Schedule.class, int.class, Graph.class//####[174]####
                });//####[174]####
            } catch (Exception e) {//####[174]####
                e.printStackTrace();//####[174]####
            }//####[174]####
        }//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, Graph graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, Graph graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setTaskIdArgIndexes(0);//####[174]####
        taskinfo.addDependsOn(schedule);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, Graph graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setQueueArgIndexes(0);//####[174]####
        taskinfo.setIsPipeline(true);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, Graph graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setTaskIdArgIndexes(1);//####[174]####
        taskinfo.addDependsOn(processorCount);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, Graph graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[174]####
        taskinfo.addDependsOn(schedule);//####[174]####
        taskinfo.addDependsOn(processorCount);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, Graph graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setQueueArgIndexes(0);//####[174]####
        taskinfo.setIsPipeline(true);//####[174]####
        taskinfo.setTaskIdArgIndexes(1);//####[174]####
        taskinfo.addDependsOn(processorCount);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setQueueArgIndexes(1);//####[174]####
        taskinfo.setIsPipeline(true);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setQueueArgIndexes(1);//####[174]####
        taskinfo.setIsPipeline(true);//####[174]####
        taskinfo.setTaskIdArgIndexes(0);//####[174]####
        taskinfo.addDependsOn(schedule);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setQueueArgIndexes(0, 1);//####[174]####
        taskinfo.setIsPipeline(true);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, TaskID<Graph> graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setTaskIdArgIndexes(2);//####[174]####
        taskinfo.addDependsOn(graph);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, TaskID<Graph> graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[174]####
        taskinfo.addDependsOn(schedule);//####[174]####
        taskinfo.addDependsOn(graph);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, TaskID<Graph> graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setQueueArgIndexes(0);//####[174]####
        taskinfo.setIsPipeline(true);//####[174]####
        taskinfo.setTaskIdArgIndexes(2);//####[174]####
        taskinfo.addDependsOn(graph);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[174]####
        taskinfo.addDependsOn(processorCount);//####[174]####
        taskinfo.addDependsOn(graph);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setTaskIdArgIndexes(0, 1, 2);//####[174]####
        taskinfo.addDependsOn(schedule);//####[174]####
        taskinfo.addDependsOn(processorCount);//####[174]####
        taskinfo.addDependsOn(graph);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setQueueArgIndexes(0);//####[174]####
        taskinfo.setIsPipeline(true);//####[174]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[174]####
        taskinfo.addDependsOn(processorCount);//####[174]####
        taskinfo.addDependsOn(graph);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setQueueArgIndexes(1);//####[174]####
        taskinfo.setIsPipeline(true);//####[174]####
        taskinfo.setTaskIdArgIndexes(2);//####[174]####
        taskinfo.addDependsOn(graph);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setQueueArgIndexes(1);//####[174]####
        taskinfo.setIsPipeline(true);//####[174]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[174]####
        taskinfo.addDependsOn(schedule);//####[174]####
        taskinfo.addDependsOn(graph);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setQueueArgIndexes(0, 1);//####[174]####
        taskinfo.setIsPipeline(true);//####[174]####
        taskinfo.setTaskIdArgIndexes(2);//####[174]####
        taskinfo.addDependsOn(graph);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, BlockingQueue<Graph> graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setQueueArgIndexes(2);//####[174]####
        taskinfo.setIsPipeline(true);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setQueueArgIndexes(2);//####[174]####
        taskinfo.setIsPipeline(true);//####[174]####
        taskinfo.setTaskIdArgIndexes(0);//####[174]####
        taskinfo.addDependsOn(schedule);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setQueueArgIndexes(0, 2);//####[174]####
        taskinfo.setIsPipeline(true);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setQueueArgIndexes(2);//####[174]####
        taskinfo.setIsPipeline(true);//####[174]####
        taskinfo.setTaskIdArgIndexes(1);//####[174]####
        taskinfo.addDependsOn(processorCount);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setQueueArgIndexes(2);//####[174]####
        taskinfo.setIsPipeline(true);//####[174]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[174]####
        taskinfo.addDependsOn(schedule);//####[174]####
        taskinfo.addDependsOn(processorCount);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setQueueArgIndexes(0, 2);//####[174]####
        taskinfo.setIsPipeline(true);//####[174]####
        taskinfo.setTaskIdArgIndexes(1);//####[174]####
        taskinfo.addDependsOn(processorCount);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setQueueArgIndexes(1, 2);//####[174]####
        taskinfo.setIsPipeline(true);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setQueueArgIndexes(1, 2);//####[174]####
        taskinfo.setIsPipeline(true);//####[174]####
        taskinfo.setTaskIdArgIndexes(0);//####[174]####
        taskinfo.addDependsOn(schedule);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[174]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[174]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[174]####
    }//####[174]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[174]####
        // ensure Method variable is set//####[174]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[174]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[174]####
        }//####[174]####
        taskinfo.setQueueArgIndexes(0, 1, 2);//####[174]####
        taskinfo.setIsPipeline(true);//####[174]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[174]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[174]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[174]####
    }//####[174]####
    public static void __pt__CreateScheduleTask(Schedule schedule, int processorCount, Graph graph) {//####[174]####
        CreateSchedule(schedule, processorCount, graph);//####[175]####
    }//####[176]####
//####[176]####
//####[179]####
    public static void CreateSchedule(Schedule schedule, int processorCount, Graph g) {//####[179]####
        Greedy greedy = new Greedy();//####[180]####
        ScheduleGraphPair sgPair = greedy.greedySearch(g, processorCount, schedule);//####[181]####
        if (sgPair.schedule.scheduleLength < ScheduleHelper.currentBestSchedule.scheduleLength) //####[185]####
        {//####[185]####
            ScheduleHelper.currentBestSchedule.scheduleLength = sgPair.schedule.scheduleLength;//####[186]####
            for (Node n : sgPair.g) //####[188]####
            {//####[188]####
                for (Node bestN : ScheduleHelper.bestGraph) //####[189]####
                {//####[189]####
                    if (n.getIndex() == bestN.getIndex()) //####[190]####
                    {//####[190]####
                        Graphs.copyAttributes(n, bestN);//####[191]####
                    }//####[192]####
                }//####[193]####
            }//####[194]####
        }//####[196]####
        BranchAndBound bnb = new BranchAndBound(sgPair.schedule, sgPair.g);//####[203]####
        bnb.branchAndBoundAlgorithm();//####[204]####
    }//####[206]####
}//####[206]####
