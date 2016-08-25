package main;//####[1]####
//####[1]####
import java.io.File;//####[3]####
import java.util.ArrayList;//####[4]####
import org.graphstream.graph.*;//####[5]####
import org.graphstream.graph.implementations.Graphs;//####[6]####
import scheduler.*;//####[7]####
import scheduler.Greedy.ScheduleGraphPair;//####[8]####
import ui.*;//####[9]####
import util.io;//####[10]####
//####[10]####
//-- ParaTask related imports//####[10]####
import pt.runtime.*;//####[10]####
import java.util.concurrent.ExecutionException;//####[10]####
import java.util.concurrent.locks.*;//####[10]####
import java.lang.reflect.*;//####[10]####
import pt.runtime.GuiThread;//####[10]####
import java.util.concurrent.BlockingQueue;//####[10]####
import java.util.ArrayList;//####[10]####
import java.util.List;//####[10]####
//####[10]####
/**
 * Main class contains the main()
 * Initialises variables and graph used by other functions
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
//####[23]####
    public static boolean inParallel = false;//####[23]####
//####[24]####
    public static boolean vis = false;//####[24]####
//####[29]####
    /**
	 * Takes in the user input as a parameter, and calls the functions required to calculate the valid, optomised solution
	 *///####[29]####
    public static void main(String[] args) {//####[29]####
        MainView mainView = null;//####[31]####
        int nThreads = 1;//####[32]####
        if (args.length < 2) //####[34]####
        {//####[34]####
            System.out.println("Please ensure that all input parameters have been entered - Input file name and number of processors. ");//####[35]####
            System.exit(1);//####[36]####
        }//####[37]####
        String inputFile = args[0];//####[39]####
        String[] split = inputFile.split(".dot");//####[40]####
        String inputWithoutExtension = split[0];//####[41]####
        File input = new File(inputWithoutExtension);//####[42]####
        String inputFileName = input.getName();//####[43]####
        String outputFile = inputFileName + "-output.dot";//####[44]####
        processorInput = Integer.parseInt(args[1]);//####[45]####
        if (args.length > 2) //####[48]####
        {//####[48]####
            int i = 2;//####[49]####
            while (i < args.length) //####[50]####
            {//####[50]####
                if (args[i].equals("-p")) //####[51]####
                {//####[51]####
                    i++;//####[52]####
                    nThreads = Integer.parseInt(args[i]);//####[53]####
                    if (nThreads > 0) //####[54]####
                    {//####[54]####
                        inParallel = true;//####[55]####
                    }//####[56]####
                } else if (args[i].equals("-v")) //####[57]####
                {//####[57]####
                    vis = true;//####[58]####
                } else if (args[i].equals("-o")) //####[59]####
                {//####[59]####
                    i++;//####[60]####
                    outputFile = args[i];//####[61]####
                }//####[62]####
                i++;//####[63]####
            }//####[64]####
        }//####[65]####
        File input_file = new File(inputFile);//####[67]####
        io IOProcessor = new io();//####[68]####
        Schedule schedule;//####[69]####
        Graph g = IOProcessor.DOTParser(input_file, inputFile);//####[70]####
        if (vis) //####[72]####
        {//####[72]####
            gVis = IOProcessor.getVisGraph();//####[73]####
            update = new Update(processorInput);//####[74]####
            mainView = new MainView(gVis);//####[75]####
            mainView.setVisible(true);//####[76]####
        }//####[77]####
        initialTime = System.currentTimeMillis();//####[80]####
        endTime = 0;//####[81]####
        ArrayList<Integer> rootnodes = ScheduleHelper.findRootNodes(g);//####[84]####
        ScheduleHelper.currentBestSchedule = new Schedule(processorInput);//####[87]####
        ScheduleHelper.bestGraph = Graphs.clone(g);//####[88]####
        ScheduleHelper.currentBestSchedule.scheduleLength = 2147483647;//####[89]####
        Greedy greedy = new Greedy();//####[90]####
        ScheduleHelper.makeDependencyMatrix(g);//####[91]####
        for (int rootNode : rootnodes) //####[94]####
        {//####[94]####
            Graph tempNewGraph = Graphs.clone(g);//####[96]####
            Schedule tempNewSchedule = new Schedule(processorInput);//####[97]####
            tempNewSchedule.addNode(tempNewGraph.getNode(rootNode), 0, 0);//####[98]####
            tempNewGraph.getNode(rootNode).setAttribute("Processor", 0);//####[99]####
            if (vis) //####[101]####
            {//####[101]####
                update.updateColor(rootNode + "", "black");//####[101]####
            }//####[101]####
            ArrayList<Integer> processableNodes = new ArrayList<Integer>();//####[103]####
            for (Node n : tempNewGraph) //####[106]####
            {//####[106]####
                if (!tempNewSchedule.schedule.contains(n)) //####[107]####
                {//####[107]####
                    boolean isProcessable = ScheduleHelper.isProcessable(n, tempNewSchedule);//####[108]####
                    if (isProcessable) //####[109]####
                    {//####[109]####
                        processableNodes.add(n.getIndex());//####[110]####
                    }//####[111]####
                }//####[112]####
            }//####[113]####
            for (int processableNodeIndex : processableNodes) //####[115]####
            {//####[115]####
                if (inParallel) //####[118]####
                {//####[118]####
                    if (nThreads > processorInput) //####[120]####
                    {//####[120]####
                        nThreads = processorInput;//####[121]####
                    }//####[122]####
                    int tempProcessorCount = 0;//####[124]####
                    while (tempProcessorCount < processorInput) //####[126]####
                    {//####[126]####
                        TaskIDGroup<TaskID<Void>> taskGroup = new TaskIDGroup<TaskID<Void>>(nThreads);//####[128]####
                        for (int i = 0; ((i < nThreads) && (tempProcessorCount < processorInput)); i++) //####[130]####
                        {//####[130]####
                            Graph newGraph = Graphs.clone(tempNewGraph);//####[132]####
                            Schedule newSchedule = new Schedule(processorInput);//####[133]####
                            newSchedule.addNode(newGraph.getNode(rootNode), 0, 0);//####[134]####
                            newSchedule.updateProcessorLength(0, (int) Double.parseDouble(newGraph.getNode(rootNode).getAttribute("Weight").toString()));//####[135]####
                            int procWaitTime = ScheduleHelper.checkChildNode(newGraph.getNode(processableNodeIndex), newSchedule, tempProcessorCount);//####[138]####
                            if (procWaitTime > -1) //####[139]####
                            {//####[139]####
                                if (vis) //####[140]####
                                {//####[140]####
                                    update.updateColor(processableNodeIndex + "", "green");//####[141]####
                                    update.updateProcessor(processableNodeIndex + "", tempProcessorCount);//####[142]####
                                }//####[143]####
                                newSchedule.addNode(newGraph.getNode(processableNodeIndex), tempProcessorCount, procWaitTime);//####[145]####
                                newSchedule.updateProcessorLength(tempProcessorCount, procWaitTime + (int) Double.parseDouble(newGraph.getNode(processableNodeIndex).getAttribute("Weight").toString()));//####[146]####
                                TaskID<Void> id = CreateScheduleTask(newSchedule, processorInput, newGraph);//####[149]####
                                taskGroup.add(id);//####[150]####
                            }//####[151]####
                            tempProcessorCount++;//####[152]####
                        }//####[153]####
                        try {//####[154]####
                            taskGroup.waitTillFinished();//####[155]####
                        } catch (Exception e) {//####[155]####
                        }//####[155]####
                    }//####[155]####
                } else {//####[157]####
                    int tempProcessorCount = 0;//####[159]####
                    while (tempProcessorCount < processorInput) //####[160]####
                    {//####[160]####
                        Graph newGraph = Graphs.clone(tempNewGraph);//####[163]####
                        Schedule newSchedule = new Schedule(processorInput);//####[164]####
                        newSchedule.addNode(newGraph.getNode(rootNode), 0, 0);//####[165]####
                        newSchedule.updateProcessorLength(0, (int) Double.parseDouble(newGraph.getNode(rootNode).getAttribute("Weight").toString()));//####[166]####
                        int procWaitTime = ScheduleHelper.checkChildNode(newGraph.getNode(processableNodeIndex), newSchedule, tempProcessorCount);//####[169]####
                        if (procWaitTime > -1) //####[170]####
                        {//####[170]####
                            if (vis) //####[171]####
                            {//####[171]####
                                update.updateColor(processableNodeIndex + "", "green");//####[172]####
                                update.updateProcessor(processableNodeIndex + "", tempProcessorCount);//####[173]####
                            }//####[174]####
                            newSchedule.addNode(newGraph.getNode(processableNodeIndex), tempProcessorCount, procWaitTime);//####[176]####
                            newSchedule.updateProcessorLength(tempProcessorCount, procWaitTime + (int) Double.parseDouble(newGraph.getNode(processableNodeIndex).getAttribute("Weight").toString()));//####[177]####
                            CreateSchedule(newSchedule, processorInput, newGraph);//####[179]####
                        }//####[180]####
                        tempProcessorCount++;//####[181]####
                    }//####[182]####
                }//####[183]####
            }//####[184]####
            if (vis) //####[187]####
            {//####[187]####
                update.updateProcessorColor(ScheduleHelper.bestGraph);//####[188]####
                mainView.updateSchedule(ScheduleHelper.currentBestSchedule.scheduleLength);//####[189]####
                mainView.start.setEnabled(true);//####[190]####
            }//####[191]####
            endTime = System.currentTimeMillis();//####[193]####
            IOProcessor.outputFile(ScheduleHelper.currentBestSchedule, ScheduleHelper.bestGraph, outputFile);//####[195]####
        }//####[197]####
    }//####[198]####
//####[204]####
    private static volatile Method __pt__CreateScheduleTask_Schedule_int_Graph_method = null;//####[204]####
    private synchronized static void __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet() {//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            try {//####[204]####
                __pt__CreateScheduleTask_Schedule_int_Graph_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__CreateScheduleTask", new Class[] {//####[204]####
                    Schedule.class, int.class, Graph.class//####[204]####
                });//####[204]####
            } catch (Exception e) {//####[204]####
                e.printStackTrace();//####[204]####
            }//####[204]####
        }//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, Graph graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, Graph graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setTaskIdArgIndexes(0);//####[204]####
        taskinfo.addDependsOn(schedule);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, Graph graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setQueueArgIndexes(0);//####[204]####
        taskinfo.setIsPipeline(true);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, Graph graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setTaskIdArgIndexes(1);//####[204]####
        taskinfo.addDependsOn(processorCount);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, Graph graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[204]####
        taskinfo.addDependsOn(schedule);//####[204]####
        taskinfo.addDependsOn(processorCount);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, Graph graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setQueueArgIndexes(0);//####[204]####
        taskinfo.setIsPipeline(true);//####[204]####
        taskinfo.setTaskIdArgIndexes(1);//####[204]####
        taskinfo.addDependsOn(processorCount);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setQueueArgIndexes(1);//####[204]####
        taskinfo.setIsPipeline(true);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setQueueArgIndexes(1);//####[204]####
        taskinfo.setIsPipeline(true);//####[204]####
        taskinfo.setTaskIdArgIndexes(0);//####[204]####
        taskinfo.addDependsOn(schedule);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setQueueArgIndexes(0, 1);//####[204]####
        taskinfo.setIsPipeline(true);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, TaskID<Graph> graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setTaskIdArgIndexes(2);//####[204]####
        taskinfo.addDependsOn(graph);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, TaskID<Graph> graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[204]####
        taskinfo.addDependsOn(schedule);//####[204]####
        taskinfo.addDependsOn(graph);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, TaskID<Graph> graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setQueueArgIndexes(0);//####[204]####
        taskinfo.setIsPipeline(true);//####[204]####
        taskinfo.setTaskIdArgIndexes(2);//####[204]####
        taskinfo.addDependsOn(graph);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[204]####
        taskinfo.addDependsOn(processorCount);//####[204]####
        taskinfo.addDependsOn(graph);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setTaskIdArgIndexes(0, 1, 2);//####[204]####
        taskinfo.addDependsOn(schedule);//####[204]####
        taskinfo.addDependsOn(processorCount);//####[204]####
        taskinfo.addDependsOn(graph);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setQueueArgIndexes(0);//####[204]####
        taskinfo.setIsPipeline(true);//####[204]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[204]####
        taskinfo.addDependsOn(processorCount);//####[204]####
        taskinfo.addDependsOn(graph);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setQueueArgIndexes(1);//####[204]####
        taskinfo.setIsPipeline(true);//####[204]####
        taskinfo.setTaskIdArgIndexes(2);//####[204]####
        taskinfo.addDependsOn(graph);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setQueueArgIndexes(1);//####[204]####
        taskinfo.setIsPipeline(true);//####[204]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[204]####
        taskinfo.addDependsOn(schedule);//####[204]####
        taskinfo.addDependsOn(graph);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setQueueArgIndexes(0, 1);//####[204]####
        taskinfo.setIsPipeline(true);//####[204]####
        taskinfo.setTaskIdArgIndexes(2);//####[204]####
        taskinfo.addDependsOn(graph);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, BlockingQueue<Graph> graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setQueueArgIndexes(2);//####[204]####
        taskinfo.setIsPipeline(true);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setQueueArgIndexes(2);//####[204]####
        taskinfo.setIsPipeline(true);//####[204]####
        taskinfo.setTaskIdArgIndexes(0);//####[204]####
        taskinfo.addDependsOn(schedule);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setQueueArgIndexes(0, 2);//####[204]####
        taskinfo.setIsPipeline(true);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setQueueArgIndexes(2);//####[204]####
        taskinfo.setIsPipeline(true);//####[204]####
        taskinfo.setTaskIdArgIndexes(1);//####[204]####
        taskinfo.addDependsOn(processorCount);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setQueueArgIndexes(2);//####[204]####
        taskinfo.setIsPipeline(true);//####[204]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[204]####
        taskinfo.addDependsOn(schedule);//####[204]####
        taskinfo.addDependsOn(processorCount);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setQueueArgIndexes(0, 2);//####[204]####
        taskinfo.setIsPipeline(true);//####[204]####
        taskinfo.setTaskIdArgIndexes(1);//####[204]####
        taskinfo.addDependsOn(processorCount);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setQueueArgIndexes(1, 2);//####[204]####
        taskinfo.setIsPipeline(true);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setQueueArgIndexes(1, 2);//####[204]####
        taskinfo.setIsPipeline(true);//####[204]####
        taskinfo.setTaskIdArgIndexes(0);//####[204]####
        taskinfo.addDependsOn(schedule);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[204]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[204]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[204]####
        // ensure Method variable is set//####[204]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[204]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[204]####
        }//####[204]####
        taskinfo.setQueueArgIndexes(0, 1, 2);//####[204]####
        taskinfo.setIsPipeline(true);//####[204]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[204]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[204]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[204]####
    }//####[204]####
    /**
	 * Called when making multiple tasks to run on seperate cores
	 * Each task calls CreateSchedule with its own inputs
	 *///####[204]####
    public static void __pt__CreateScheduleTask(Schedule schedule, int processorCount, Graph graph) {//####[204]####
        CreateSchedule(schedule, processorCount, graph);//####[205]####
    }//####[206]####
//####[206]####
//####[212]####
    /**
	 * This initialises a greedy call and does the greedy searhc on the schedule
	 * updates the current best if a schedule is better
	 *///####[212]####
    public static void CreateSchedule(Schedule schedule, int processorCount, Graph g) {//####[212]####
        Greedy greedy = new Greedy();//####[213]####
        ScheduleGraphPair sgPair = greedy.greedySearch(g, processorCount, schedule);//####[214]####
        if (sgPair.schedule.scheduleLength < ScheduleHelper.currentBestSchedule.scheduleLength) //####[217]####
        {//####[217]####
            ScheduleHelper.currentBestSchedule.scheduleLength = sgPair.schedule.scheduleLength;//####[218]####
            for (Node n : sgPair.g) //####[220]####
            {//####[220]####
                for (Node bestN : ScheduleHelper.bestGraph) //####[221]####
                {//####[221]####
                    if (n.getIndex() == bestN.getIndex()) //####[222]####
                    {//####[222]####
                        Graphs.copyAttributes(n, bestN);//####[223]####
                    }//####[224]####
                }//####[225]####
            }//####[226]####
        }//####[227]####
        BranchAndBound bnb = new BranchAndBound(sgPair.schedule, sgPair.g);//####[229]####
        bnb.branchAndBoundAlgorithm();//####[230]####
    }//####[231]####
}//####[231]####
