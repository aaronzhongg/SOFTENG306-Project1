package main;//####[1]####
//####[1]####
import java.io.File;//####[3]####
import java.util.ArrayList;//####[4]####
import org.graphstream.graph.*;//####[6]####
import org.graphstream.graph.implementations.Graphs;//####[7]####
import scheduler.*;//####[8]####
import scheduler.Greedy.ScheduleGraphPair;//####[9]####
import ui.*;//####[10]####
import util.io;//####[11]####
//####[11]####
//-- ParaTask related imports//####[11]####
import pt.runtime.*;//####[11]####
import java.util.concurrent.ExecutionException;//####[11]####
import java.util.concurrent.locks.*;//####[11]####
import java.lang.reflect.*;//####[11]####
import pt.runtime.GuiThread;//####[11]####
import java.util.concurrent.BlockingQueue;//####[11]####
import java.util.ArrayList;//####[11]####
import java.util.List;//####[11]####
//####[11]####
/**
 * Created by jay on 5/08/16.
 *///####[15]####
public class Main {//####[16]####
    static{ParaTask.init();}//####[16]####
    /*  ParaTask helper method to access private/protected slots *///####[16]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[16]####
        if (m.getParameterTypes().length == 0)//####[16]####
            m.invoke(instance);//####[16]####
        else if ((m.getParameterTypes().length == 1))//####[16]####
            m.invoke(instance, arg);//####[16]####
        else //####[16]####
            m.invoke(instance, arg, interResult);//####[16]####
    }//####[16]####
//####[18]####
    public static int processorInput;//####[18]####
//####[19]####
    public static Graph gVis;//####[19]####
//####[20]####
    public static Update update;//####[20]####
//####[21]####
    public static long initialTime, endTime;//####[21]####
//####[22]####
    public static boolean inParallel = false;//####[22]####
//####[23]####
    public static boolean vis = false;//####[23]####
//####[28]####
    /**
	 * main
	 *///####[28]####
    public static void main(String[] args) {//####[28]####
        MainView mainView = null;//####[29]####
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
                if (args[i].equals("-p")) //####[47]####
                {//####[47]####
                    i++;//####[48]####
                    nThreads = Integer.parseInt(args[i]);//####[49]####
                    inParallel = true;//####[50]####
                } else if (args[i].equals("-v")) //####[51]####
                {//####[51]####
                    vis = true;//####[52]####
                } else if (args[i].equals("-o")) //####[53]####
                {//####[53]####
                    i++;//####[54]####
                    outputFile = args[i];//####[55]####
                }//####[56]####
                i++;//####[57]####
            }//####[58]####
        }//####[59]####
        File input_file = new File(inputFile);//####[65]####
        io IOProcessor = new io();//####[66]####
        Schedule schedule;//####[67]####
        Graph g = IOProcessor.DOTParser(input_file, inputFile);//####[68]####
        if (vis) //####[70]####
        {//####[70]####
            gVis = IOProcessor.getVisGraph();//####[72]####
            update = new Update(processorInput);//####[73]####
            mainView = new MainView(gVis);//####[74]####
            mainView.setVisible(true);//####[75]####
        }//####[76]####
        initialTime = System.currentTimeMillis();//####[79]####
        endTime = 0;//####[80]####
        ArrayList<Integer> rootnodes = ScheduleHelper.findRootNodes(g);//####[82]####
        ScheduleHelper.currentBestSchedule = new Schedule(processorInput);//####[84]####
        ScheduleHelper.bestGraph = Graphs.clone(g);//####[85]####
        ScheduleHelper.currentBestSchedule.scheduleLength = 2147483647;//####[86]####
        Greedy greedy = new Greedy();//####[87]####
        ScheduleHelper.makeDependencyMatrix(g);//####[88]####
        for (int rootNode : rootnodes) //####[91]####
        {//####[91]####
            Graph tempNewGraph = Graphs.clone(g);//####[93]####
            Schedule tempNewSchedule = new Schedule(processorInput);//####[94]####
            tempNewSchedule.addNode(tempNewGraph.getNode(rootNode), 0, 0);//####[95]####
            tempNewGraph.getNode(rootNode).setAttribute("Processor", 0);//####[96]####
            if (vis) //####[97]####
            {//####[97]####
                update.updateColor(rootNode + "", "black");//####[97]####
            }//####[97]####
            ArrayList<Integer> processableNodes = new ArrayList<Integer>();//####[99]####
            for (Node n : tempNewGraph) //####[101]####
            {//####[101]####
                if (!tempNewSchedule.schedule.contains(n)) //####[102]####
                {//####[102]####
                    boolean isProcessable = ScheduleHelper.isProcessable(n, tempNewSchedule);//####[104]####
                    if (isProcessable) //####[105]####
                    {//####[105]####
                        processableNodes.add(n.getIndex());//####[106]####
                    }//####[107]####
                }//####[108]####
            }//####[109]####
            for (int processableNodeIndex : processableNodes) //####[111]####
            {//####[111]####
                if (inParallel) //####[114]####
                {//####[114]####
                    int tempProcessorCount = 0;//####[115]####
                    TaskIDGroup<TaskID<Void>> taskGroup = new TaskIDGroup<TaskID<Void>>(processorInput);//####[116]####
                    while (tempProcessorCount < processorInput) //####[117]####
                    {//####[117]####
                        Graph newGraph = Graphs.clone(tempNewGraph);//####[120]####
                        Schedule newSchedule = new Schedule(processorInput);//####[121]####
                        newSchedule.addNode(newGraph.getNode(rootNode), 0, 0);//####[122]####
                        newSchedule.updateProcessorLength(0, (int) Double.parseDouble(newGraph.getNode(rootNode).getAttribute("Weight").toString()));//####[123]####
                        int procWaitTime = ScheduleHelper.checkChildNode(newGraph.getNode(processableNodeIndex), newSchedule, tempProcessorCount);//####[126]####
                        if (procWaitTime > -1) //####[127]####
                        {//####[127]####
                            if (vis) //####[128]####
                            {//####[128]####
                                update.updateColor(processableNodeIndex + "", "green");//####[129]####
                                update.updateProcessor(processableNodeIndex + "", tempProcessorCount);//####[130]####
                            }//####[131]####
                            newSchedule.addNode(newGraph.getNode(processableNodeIndex), tempProcessorCount, procWaitTime);//####[133]####
                            newSchedule.updateProcessorLength(tempProcessorCount, procWaitTime + (int) Double.parseDouble(newGraph.getNode(processableNodeIndex).getAttribute("Weight").toString()));//####[134]####
                            TaskID<Void> id = CreateScheduleTask(newSchedule, processorInput, newGraph);//####[135]####
                            taskGroup.add(id);//####[136]####
                        }//####[137]####
                        tempProcessorCount++;//####[138]####
                    }//####[139]####
                    try {//####[140]####
                        taskGroup.waitTillFinished();//####[141]####
                    } catch (Exception e) {//####[143]####
                    }//####[143]####
                } else {//####[147]####
                    int tempProcessorCount = 0;//####[149]####
                    while (tempProcessorCount < processorInput) //####[150]####
                    {//####[150]####
                        Graph newGraph = Graphs.clone(tempNewGraph);//####[152]####
                        Schedule newSchedule = new Schedule(processorInput);//####[153]####
                        newSchedule.addNode(newGraph.getNode(rootNode), 0, 0);//####[154]####
                        newSchedule.updateProcessorLength(0, (int) Double.parseDouble(newGraph.getNode(rootNode).getAttribute("Weight").toString()));//####[155]####
                        int procWaitTime = ScheduleHelper.checkChildNode(newGraph.getNode(processableNodeIndex), newSchedule, tempProcessorCount);//####[158]####
                        if (procWaitTime > -1) //####[159]####
                        {//####[159]####
                            if (vis) //####[160]####
                            {//####[160]####
                                update.updateColor(processableNodeIndex + "", "green");//####[160]####
                                update.updateProcessor(processableNodeIndex + "", tempProcessorCount);//####[161]####
                            }//####[161]####
                            newSchedule.addNode(newGraph.getNode(processableNodeIndex), tempProcessorCount, procWaitTime);//####[163]####
                            newSchedule.updateProcessorLength(tempProcessorCount, procWaitTime + (int) Double.parseDouble(newGraph.getNode(processableNodeIndex).getAttribute("Weight").toString()));//####[164]####
                            CreateSchedule(newSchedule, processorInput, newGraph);//####[165]####
                        }//####[166]####
                        tempProcessorCount++;//####[167]####
                    }//####[168]####
                }//####[170]####
            }//####[171]####
            if (vis) //####[174]####
            {//####[174]####
                update.updateProcessorColor(ScheduleHelper.bestGraph);//####[175]####
                mainView.updateSchedule(ScheduleHelper.currentBestSchedule.scheduleLength);//####[176]####
                mainView.start.setEnabled(true);//####[177]####
            }//####[178]####
            endTime = System.currentTimeMillis();//####[179]####
            for (Node n : ScheduleHelper.bestGraph) //####[181]####
            {//####[181]####
                System.out.println("Node id: " + n.getId() + " ProcID: " + n.getAttribute("Processor") + " Starts at: " + n.getAttribute("Start") + " Node Weight: " + n.getAttribute("Weight"));//####[182]####
            }//####[183]####
            System.out.println("Total Schedule Length: " + ScheduleHelper.currentBestSchedule.scheduleLength);//####[184]####
            IOProcessor.outputFile(ScheduleHelper.currentBestSchedule, ScheduleHelper.bestGraph, outputFile);//####[185]####
        }//####[186]####
    }//####[187]####
//####[192]####
    private static volatile Method __pt__CreateScheduleTask_Schedule_int_Graph_method = null;//####[192]####
    private synchronized static void __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet() {//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            try {//####[192]####
                __pt__CreateScheduleTask_Schedule_int_Graph_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__CreateScheduleTask", new Class[] {//####[192]####
                    Schedule.class, int.class, Graph.class//####[192]####
                });//####[192]####
            } catch (Exception e) {//####[192]####
                e.printStackTrace();//####[192]####
            }//####[192]####
        }//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, Graph graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, Graph graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setTaskIdArgIndexes(0);//####[192]####
        taskinfo.addDependsOn(schedule);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, Graph graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setQueueArgIndexes(0);//####[192]####
        taskinfo.setIsPipeline(true);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, Graph graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setTaskIdArgIndexes(1);//####[192]####
        taskinfo.addDependsOn(processorCount);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, Graph graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[192]####
        taskinfo.addDependsOn(schedule);//####[192]####
        taskinfo.addDependsOn(processorCount);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, Graph graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setQueueArgIndexes(0);//####[192]####
        taskinfo.setIsPipeline(true);//####[192]####
        taskinfo.setTaskIdArgIndexes(1);//####[192]####
        taskinfo.addDependsOn(processorCount);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setQueueArgIndexes(1);//####[192]####
        taskinfo.setIsPipeline(true);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setQueueArgIndexes(1);//####[192]####
        taskinfo.setIsPipeline(true);//####[192]####
        taskinfo.setTaskIdArgIndexes(0);//####[192]####
        taskinfo.addDependsOn(schedule);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setQueueArgIndexes(0, 1);//####[192]####
        taskinfo.setIsPipeline(true);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, TaskID<Graph> graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setTaskIdArgIndexes(2);//####[192]####
        taskinfo.addDependsOn(graph);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, TaskID<Graph> graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[192]####
        taskinfo.addDependsOn(schedule);//####[192]####
        taskinfo.addDependsOn(graph);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, TaskID<Graph> graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setQueueArgIndexes(0);//####[192]####
        taskinfo.setIsPipeline(true);//####[192]####
        taskinfo.setTaskIdArgIndexes(2);//####[192]####
        taskinfo.addDependsOn(graph);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[192]####
        taskinfo.addDependsOn(processorCount);//####[192]####
        taskinfo.addDependsOn(graph);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setTaskIdArgIndexes(0, 1, 2);//####[192]####
        taskinfo.addDependsOn(schedule);//####[192]####
        taskinfo.addDependsOn(processorCount);//####[192]####
        taskinfo.addDependsOn(graph);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setQueueArgIndexes(0);//####[192]####
        taskinfo.setIsPipeline(true);//####[192]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[192]####
        taskinfo.addDependsOn(processorCount);//####[192]####
        taskinfo.addDependsOn(graph);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setQueueArgIndexes(1);//####[192]####
        taskinfo.setIsPipeline(true);//####[192]####
        taskinfo.setTaskIdArgIndexes(2);//####[192]####
        taskinfo.addDependsOn(graph);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setQueueArgIndexes(1);//####[192]####
        taskinfo.setIsPipeline(true);//####[192]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[192]####
        taskinfo.addDependsOn(schedule);//####[192]####
        taskinfo.addDependsOn(graph);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setQueueArgIndexes(0, 1);//####[192]####
        taskinfo.setIsPipeline(true);//####[192]####
        taskinfo.setTaskIdArgIndexes(2);//####[192]####
        taskinfo.addDependsOn(graph);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, BlockingQueue<Graph> graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setQueueArgIndexes(2);//####[192]####
        taskinfo.setIsPipeline(true);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setQueueArgIndexes(2);//####[192]####
        taskinfo.setIsPipeline(true);//####[192]####
        taskinfo.setTaskIdArgIndexes(0);//####[192]####
        taskinfo.addDependsOn(schedule);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setQueueArgIndexes(0, 2);//####[192]####
        taskinfo.setIsPipeline(true);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setQueueArgIndexes(2);//####[192]####
        taskinfo.setIsPipeline(true);//####[192]####
        taskinfo.setTaskIdArgIndexes(1);//####[192]####
        taskinfo.addDependsOn(processorCount);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setQueueArgIndexes(2);//####[192]####
        taskinfo.setIsPipeline(true);//####[192]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[192]####
        taskinfo.addDependsOn(schedule);//####[192]####
        taskinfo.addDependsOn(processorCount);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setQueueArgIndexes(0, 2);//####[192]####
        taskinfo.setIsPipeline(true);//####[192]####
        taskinfo.setTaskIdArgIndexes(1);//####[192]####
        taskinfo.addDependsOn(processorCount);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setQueueArgIndexes(1, 2);//####[192]####
        taskinfo.setIsPipeline(true);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setQueueArgIndexes(1, 2);//####[192]####
        taskinfo.setIsPipeline(true);//####[192]####
        taskinfo.setTaskIdArgIndexes(0);//####[192]####
        taskinfo.addDependsOn(schedule);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[192]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[192]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[192]####
        // ensure Method variable is set//####[192]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[192]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[192]####
        }//####[192]####
        taskinfo.setQueueArgIndexes(0, 1, 2);//####[192]####
        taskinfo.setIsPipeline(true);//####[192]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[192]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[192]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[192]####
    }//####[192]####
    /**
	 * Called when making tasks
	 *///####[192]####
    public static void __pt__CreateScheduleTask(Schedule schedule, int processorCount, Graph graph) {//####[192]####
        CreateSchedule(schedule, processorCount, graph);//####[194]####
    }//####[196]####
//####[196]####
//####[201]####
    /**
	 * Called when sequential
	 *///####[201]####
    public static void CreateSchedule(Schedule schedule, int processorCount, Graph g) {//####[201]####
        Greedy greedy = new Greedy();//####[202]####
        ScheduleGraphPair sgPair = greedy.greedySearch(g, processorCount, schedule);//####[203]####
        if (sgPair.schedule.scheduleLength < ScheduleHelper.currentBestSchedule.scheduleLength) //####[206]####
        {//####[206]####
            ScheduleHelper.currentBestSchedule.scheduleLength = sgPair.schedule.scheduleLength;//####[207]####
            for (Node n : sgPair.g) //####[209]####
            {//####[209]####
                for (Node bestN : ScheduleHelper.bestGraph) //####[210]####
                {//####[210]####
                    if (n.getIndex() == bestN.getIndex()) //####[211]####
                    {//####[211]####
                        Graphs.copyAttributes(n, bestN);//####[212]####
                    }//####[213]####
                }//####[214]####
            }//####[215]####
        }//####[216]####
        BranchAndBound bnb = new BranchAndBound(sgPair.schedule, sgPair.g);//####[219]####
        bnb.branchAndBoundAlgorithm();//####[220]####
    }//####[222]####
}//####[222]####
