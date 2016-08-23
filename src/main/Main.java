package main;//####[1]####
//####[1]####
import java.io.File;//####[3]####
import java.util.ArrayList;//####[4]####
import org.graphstream.graph.*;//####[6]####
import org.graphstream.graph.implementations.Graphs;//####[7]####
import pt.runtime.TaskID;//####[9]####
import pt.runtime.TaskIDGroup;//####[10]####
import scheduler.*;//####[12]####
import scheduler.Greedy.ScheduleGraphPair;//####[13]####
import ui.MainView;//####[14]####
import util.io;//####[15]####
//####[15]####
//-- ParaTask related imports//####[15]####
import pt.runtime.*;//####[15]####
import java.util.concurrent.ExecutionException;//####[15]####
import java.util.concurrent.locks.*;//####[15]####
import java.lang.reflect.*;//####[15]####
import pt.runtime.GuiThread;//####[15]####
import java.util.concurrent.BlockingQueue;//####[15]####
//####[15]####
/**
 * Created by jay on 5/08/16.
 *///####[19]####
public class Main {//####[20]####
    static{ParaTask.init();}//####[20]####
    /*  ParaTask helper method to access private/protected slots *///####[20]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[20]####
        if (m.getParameterTypes().length == 0)//####[20]####
            m.invoke(instance);//####[20]####
        else if ((m.getParameterTypes().length == 1))//####[20]####
            m.invoke(instance, arg);//####[20]####
        else //####[20]####
            m.invoke(instance, arg, interResult);//####[20]####
    }//####[20]####
//####[22]####
    public static int processorInput;//####[22]####
//####[27]####
    /**
	 * main
	 *///####[27]####
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
            for (Node n : ScheduleHelper.bestGraph) //####[164]####
            {//####[164]####
                System.out.println("Node id: " + n.getId() + " ProcID: " + n.getAttribute("Processor") + " Starts at: " + n.getAttribute("Start") + " Node Weight: " + n.getAttribute("Weight"));//####[165]####
            }//####[166]####
            System.out.println("Total Schedule Length: " + ScheduleHelper.currentBestSchedule.scheduleLength);//####[167]####
            IOProcessor.outputFile(ScheduleHelper.currentBestSchedule, ScheduleHelper.bestGraph, outputFile);//####[168]####
        }//####[169]####
    }//####[170]####
//####[175]####
    private static volatile Method __pt__CreateScheduleTask_Schedule_int_Graph_method = null;//####[175]####
    private synchronized static void __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet() {//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            try {//####[175]####
                __pt__CreateScheduleTask_Schedule_int_Graph_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__CreateScheduleTask", new Class[] {//####[175]####
                    Schedule.class, int.class, Graph.class//####[175]####
                });//####[175]####
            } catch (Exception e) {//####[175]####
                e.printStackTrace();//####[175]####
            }//####[175]####
        }//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, Graph graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, Graph graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setTaskIdArgIndexes(0);//####[175]####
        taskinfo.addDependsOn(schedule);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, Graph graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setQueueArgIndexes(0);//####[175]####
        taskinfo.setIsPipeline(true);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, Graph graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setTaskIdArgIndexes(1);//####[175]####
        taskinfo.addDependsOn(processorCount);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, Graph graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[175]####
        taskinfo.addDependsOn(schedule);//####[175]####
        taskinfo.addDependsOn(processorCount);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, Graph graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setQueueArgIndexes(0);//####[175]####
        taskinfo.setIsPipeline(true);//####[175]####
        taskinfo.setTaskIdArgIndexes(1);//####[175]####
        taskinfo.addDependsOn(processorCount);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setQueueArgIndexes(1);//####[175]####
        taskinfo.setIsPipeline(true);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setQueueArgIndexes(1);//####[175]####
        taskinfo.setIsPipeline(true);//####[175]####
        taskinfo.setTaskIdArgIndexes(0);//####[175]####
        taskinfo.addDependsOn(schedule);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setQueueArgIndexes(0, 1);//####[175]####
        taskinfo.setIsPipeline(true);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, TaskID<Graph> graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setTaskIdArgIndexes(2);//####[175]####
        taskinfo.addDependsOn(graph);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, TaskID<Graph> graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[175]####
        taskinfo.addDependsOn(schedule);//####[175]####
        taskinfo.addDependsOn(graph);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, TaskID<Graph> graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setQueueArgIndexes(0);//####[175]####
        taskinfo.setIsPipeline(true);//####[175]####
        taskinfo.setTaskIdArgIndexes(2);//####[175]####
        taskinfo.addDependsOn(graph);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[175]####
        taskinfo.addDependsOn(processorCount);//####[175]####
        taskinfo.addDependsOn(graph);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setTaskIdArgIndexes(0, 1, 2);//####[175]####
        taskinfo.addDependsOn(schedule);//####[175]####
        taskinfo.addDependsOn(processorCount);//####[175]####
        taskinfo.addDependsOn(graph);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setQueueArgIndexes(0);//####[175]####
        taskinfo.setIsPipeline(true);//####[175]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[175]####
        taskinfo.addDependsOn(processorCount);//####[175]####
        taskinfo.addDependsOn(graph);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setQueueArgIndexes(1);//####[175]####
        taskinfo.setIsPipeline(true);//####[175]####
        taskinfo.setTaskIdArgIndexes(2);//####[175]####
        taskinfo.addDependsOn(graph);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setQueueArgIndexes(1);//####[175]####
        taskinfo.setIsPipeline(true);//####[175]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[175]####
        taskinfo.addDependsOn(schedule);//####[175]####
        taskinfo.addDependsOn(graph);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setQueueArgIndexes(0, 1);//####[175]####
        taskinfo.setIsPipeline(true);//####[175]####
        taskinfo.setTaskIdArgIndexes(2);//####[175]####
        taskinfo.addDependsOn(graph);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, BlockingQueue<Graph> graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setQueueArgIndexes(2);//####[175]####
        taskinfo.setIsPipeline(true);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setQueueArgIndexes(2);//####[175]####
        taskinfo.setIsPipeline(true);//####[175]####
        taskinfo.setTaskIdArgIndexes(0);//####[175]####
        taskinfo.addDependsOn(schedule);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setQueueArgIndexes(0, 2);//####[175]####
        taskinfo.setIsPipeline(true);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setQueueArgIndexes(2);//####[175]####
        taskinfo.setIsPipeline(true);//####[175]####
        taskinfo.setTaskIdArgIndexes(1);//####[175]####
        taskinfo.addDependsOn(processorCount);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setQueueArgIndexes(2);//####[175]####
        taskinfo.setIsPipeline(true);//####[175]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[175]####
        taskinfo.addDependsOn(schedule);//####[175]####
        taskinfo.addDependsOn(processorCount);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setQueueArgIndexes(0, 2);//####[175]####
        taskinfo.setIsPipeline(true);//####[175]####
        taskinfo.setTaskIdArgIndexes(1);//####[175]####
        taskinfo.addDependsOn(processorCount);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setQueueArgIndexes(1, 2);//####[175]####
        taskinfo.setIsPipeline(true);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setQueueArgIndexes(1, 2);//####[175]####
        taskinfo.setIsPipeline(true);//####[175]####
        taskinfo.setTaskIdArgIndexes(0);//####[175]####
        taskinfo.addDependsOn(schedule);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[175]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[175]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[175]####
        // ensure Method variable is set//####[175]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[175]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[175]####
        }//####[175]####
        taskinfo.setQueueArgIndexes(0, 1, 2);//####[175]####
        taskinfo.setIsPipeline(true);//####[175]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[175]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[175]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[175]####
    }//####[175]####
    /**
	 * Called when making tasks
	 *///####[175]####
    public static void __pt__CreateScheduleTask(Schedule schedule, int processorCount, Graph graph) {//####[175]####
        CreateSchedule(schedule, processorCount, graph);//####[176]####
    }//####[177]####
//####[177]####
//####[182]####
    /**
	 * Called when sequential
	 *///####[182]####
    public static void CreateSchedule(Schedule schedule, int processorCount, Graph g) {//####[182]####
        Greedy greedy = new Greedy();//####[183]####
        ScheduleGraphPair sgPair = greedy.greedySearch(g, processorCount, schedule);//####[184]####
        if (sgPair.schedule.scheduleLength < ScheduleHelper.currentBestSchedule.scheduleLength) //####[187]####
        {//####[187]####
            ScheduleHelper.currentBestSchedule.scheduleLength = sgPair.schedule.scheduleLength;//####[188]####
            for (Node n : sgPair.g) //####[190]####
            {//####[190]####
                for (Node bestN : ScheduleHelper.bestGraph) //####[191]####
                {//####[191]####
                    if (n.getIndex() == bestN.getIndex()) //####[192]####
                    {//####[192]####
                        Graphs.copyAttributes(n, bestN);//####[193]####
                    }//####[194]####
                }//####[195]####
            }//####[196]####
        }//####[197]####
        BranchAndBound bnb = new BranchAndBound(sgPair.schedule, sgPair.g);//####[199]####
        bnb.branchAndBoundAlgorithm();//####[200]####
    }//####[202]####
}//####[202]####
