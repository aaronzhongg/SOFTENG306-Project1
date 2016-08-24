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
        MainView mainView = null;//####[30]####
        int nThreads = 1;//####[31]####
        if (args.length < 2) //####[36]####
        {//####[36]####
            System.out.println("Please ensure that all input parameters have been entered - Input file name and number of processors. ");//####[37]####
            System.exit(1);//####[38]####
        }//####[39]####
        String inputFile = args[0];//####[40]####
        String[] split = inputFile.split(".dot");//####[41]####
        String inputWithoutExtension = split[0];//####[42]####
        File input = new File(inputWithoutExtension);//####[43]####
        String inputFileName = input.getName();//####[44]####
        String outputFile = inputFileName + "-output.dot";//####[45]####
        processorInput = Integer.parseInt(args[1]);//####[46]####
        if (args.length > 2) //####[47]####
        {//####[47]####
            int i = 2;//####[48]####
            while (i < args.length) //####[49]####
            {//####[49]####
                if (args[i].equals("-p")) //####[50]####
                {//####[50]####
                    i++;//####[51]####
                    nThreads = Integer.parseInt(args[i]);//####[52]####
                    if (nThreads > 0) //####[53]####
                    {//####[53]####
                        inParallel = true;//####[54]####
                    }//####[55]####
                } else if (args[i].equals("-v")) //####[56]####
                {//####[56]####
                    vis = true;//####[57]####
                } else if (args[i].equals("-o")) //####[58]####
                {//####[58]####
                    i++;//####[59]####
                    outputFile = args[i];//####[60]####
                }//####[61]####
                i++;//####[62]####
            }//####[63]####
        }//####[65]####
        File input_file = new File(inputFile);//####[73]####
        io IOProcessor = new io();//####[74]####
        Schedule schedule;//####[75]####
        Graph g = IOProcessor.DOTParser(input_file, inputFile);//####[76]####
        if (vis) //####[78]####
        {//####[78]####
            gVis = IOProcessor.getVisGraph();//####[80]####
            update = new Update(processorInput);//####[81]####
            mainView = new MainView(gVis);//####[82]####
            mainView.setVisible(true);//####[83]####
        }//####[84]####
        initialTime = System.currentTimeMillis();//####[87]####
        endTime = 0;//####[88]####
        ArrayList<Integer> rootnodes = ScheduleHelper.findRootNodes(g);//####[90]####
        ScheduleHelper.currentBestSchedule = new Schedule(processorInput);//####[92]####
        ScheduleHelper.bestGraph = Graphs.clone(g);//####[93]####
        ScheduleHelper.currentBestSchedule.scheduleLength = 2147483647;//####[94]####
        Greedy greedy = new Greedy();//####[95]####
        ScheduleHelper.makeDependencyMatrix(g);//####[96]####
        for (int rootNode : rootnodes) //####[99]####
        {//####[99]####
            Graph tempNewGraph = Graphs.clone(g);//####[101]####
            Schedule tempNewSchedule = new Schedule(processorInput);//####[102]####
            tempNewSchedule.addNode(tempNewGraph.getNode(rootNode), 0, 0);//####[103]####
            tempNewGraph.getNode(rootNode).setAttribute("Processor", 0);//####[104]####
            if (vis) //####[105]####
            {//####[105]####
                update.updateColor(rootNode + "", "black");//####[105]####
            }//####[105]####
            ArrayList<Integer> processableNodes = new ArrayList<Integer>();//####[107]####
            for (Node n : tempNewGraph) //####[109]####
            {//####[109]####
                if (!tempNewSchedule.schedule.contains(n)) //####[110]####
                {//####[110]####
                    boolean isProcessable = ScheduleHelper.isProcessable(n, tempNewSchedule);//####[112]####
                    if (isProcessable) //####[113]####
                    {//####[113]####
                        processableNodes.add(n.getIndex());//####[114]####
                    }//####[115]####
                }//####[116]####
            }//####[117]####
            for (int processableNodeIndex : processableNodes) //####[119]####
            {//####[119]####
                if (inParallel) //####[122]####
                {//####[122]####
                    if (nThreads > processorInput) //####[123]####
                    {//####[123]####
                        nThreads = processorInput;//####[124]####
                    }//####[125]####
                    int tempProcessorCount = 0;//####[126]####
                    while (tempProcessorCount < processorInput) //####[128]####
                    {//####[128]####
                        TaskIDGroup<TaskID<Void>> taskGroup = new TaskIDGroup<TaskID<Void>>(nThreads);//####[129]####
                        for (int i = 0; ((i < nThreads) && (tempProcessorCount < processorInput)); i++) //####[130]####
                        {//####[130]####
                            Graph newGraph = Graphs.clone(tempNewGraph);//####[131]####
                            Schedule newSchedule = new Schedule(processorInput);//####[132]####
                            newSchedule.addNode(newGraph.getNode(rootNode), 0, 0);//####[133]####
                            newSchedule.updateProcessorLength(0, (int) Double.parseDouble(newGraph.getNode(rootNode).getAttribute("Weight").toString()));//####[134]####
                            int procWaitTime = ScheduleHelper.checkChildNode(newGraph.getNode(processableNodeIndex), newSchedule, tempProcessorCount);//####[137]####
                            if (procWaitTime > -1) //####[138]####
                            {//####[138]####
                                if (vis) //####[139]####
                                {//####[139]####
                                    update.updateColor(processableNodeIndex + "", "green");//####[140]####
                                    update.updateProcessor(processableNodeIndex + "", tempProcessorCount);//####[141]####
                                }//####[142]####
                                newSchedule.addNode(newGraph.getNode(processableNodeIndex), tempProcessorCount, procWaitTime);//####[144]####
                                newSchedule.updateProcessorLength(tempProcessorCount, procWaitTime + (int) Double.parseDouble(newGraph.getNode(processableNodeIndex).getAttribute("Weight").toString()));//####[145]####
                                TaskID<Void> id = CreateScheduleTask(newSchedule, processorInput, newGraph);//####[146]####
                                taskGroup.add(id);//####[147]####
                            }//####[148]####
                            tempProcessorCount++;//####[149]####
                        }//####[150]####
                        try {//####[151]####
                            taskGroup.waitTillFinished();//####[152]####
                        } catch (Exception e) {//####[154]####
                        }//####[154]####
                    }//####[155]####
                } else {//####[158]####
                    int tempProcessorCount = 0;//####[160]####
                    while (tempProcessorCount < processorInput) //####[161]####
                    {//####[161]####
                        Graph newGraph = Graphs.clone(tempNewGraph);//####[163]####
                        Schedule newSchedule = new Schedule(processorInput);//####[164]####
                        newSchedule.addNode(newGraph.getNode(rootNode), 0, 0);//####[165]####
                        newSchedule.updateProcessorLength(0, (int) Double.parseDouble(newGraph.getNode(rootNode).getAttribute("Weight").toString()));//####[166]####
                        int procWaitTime = ScheduleHelper.checkChildNode(newGraph.getNode(processableNodeIndex), newSchedule, tempProcessorCount);//####[169]####
                        if (procWaitTime > -1) //####[170]####
                        {//####[170]####
                            if (vis) //####[171]####
                            {//####[171]####
                                update.updateColor(processableNodeIndex + "", "green");//####[171]####
                                update.updateProcessor(processableNodeIndex + "", tempProcessorCount);//####[172]####
                            }//####[172]####
                            newSchedule.addNode(newGraph.getNode(processableNodeIndex), tempProcessorCount, procWaitTime);//####[174]####
                            newSchedule.updateProcessorLength(tempProcessorCount, procWaitTime + (int) Double.parseDouble(newGraph.getNode(processableNodeIndex).getAttribute("Weight").toString()));//####[175]####
                            CreateSchedule(newSchedule, processorInput, newGraph);//####[176]####
                        }//####[177]####
                        tempProcessorCount++;//####[178]####
                    }//####[179]####
                }//####[181]####
            }//####[182]####
            if (vis) //####[185]####
            {//####[185]####
                update.updateProcessorColor(ScheduleHelper.bestGraph);//####[186]####
                mainView.updateSchedule(ScheduleHelper.currentBestSchedule.scheduleLength);//####[187]####
                mainView.start.setEnabled(true);//####[188]####
            }//####[189]####
            endTime = System.currentTimeMillis();//####[190]####
        }//####[202]####
    }//####[203]####
//####[208]####
    private static volatile Method __pt__CreateScheduleTask_Schedule_int_Graph_method = null;//####[208]####
    private synchronized static void __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet() {//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            try {//####[208]####
                __pt__CreateScheduleTask_Schedule_int_Graph_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__CreateScheduleTask", new Class[] {//####[208]####
                    Schedule.class, int.class, Graph.class//####[208]####
                });//####[208]####
            } catch (Exception e) {//####[208]####
                e.printStackTrace();//####[208]####
            }//####[208]####
        }//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, Graph graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, Graph graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setTaskIdArgIndexes(0);//####[208]####
        taskinfo.addDependsOn(schedule);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, Graph graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setQueueArgIndexes(0);//####[208]####
        taskinfo.setIsPipeline(true);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, Graph graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setTaskIdArgIndexes(1);//####[208]####
        taskinfo.addDependsOn(processorCount);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, Graph graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[208]####
        taskinfo.addDependsOn(schedule);//####[208]####
        taskinfo.addDependsOn(processorCount);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, Graph graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setQueueArgIndexes(0);//####[208]####
        taskinfo.setIsPipeline(true);//####[208]####
        taskinfo.setTaskIdArgIndexes(1);//####[208]####
        taskinfo.addDependsOn(processorCount);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setQueueArgIndexes(1);//####[208]####
        taskinfo.setIsPipeline(true);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setQueueArgIndexes(1);//####[208]####
        taskinfo.setIsPipeline(true);//####[208]####
        taskinfo.setTaskIdArgIndexes(0);//####[208]####
        taskinfo.addDependsOn(schedule);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setQueueArgIndexes(0, 1);//####[208]####
        taskinfo.setIsPipeline(true);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, TaskID<Graph> graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setTaskIdArgIndexes(2);//####[208]####
        taskinfo.addDependsOn(graph);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, TaskID<Graph> graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[208]####
        taskinfo.addDependsOn(schedule);//####[208]####
        taskinfo.addDependsOn(graph);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, TaskID<Graph> graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setQueueArgIndexes(0);//####[208]####
        taskinfo.setIsPipeline(true);//####[208]####
        taskinfo.setTaskIdArgIndexes(2);//####[208]####
        taskinfo.addDependsOn(graph);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[208]####
        taskinfo.addDependsOn(processorCount);//####[208]####
        taskinfo.addDependsOn(graph);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setTaskIdArgIndexes(0, 1, 2);//####[208]####
        taskinfo.addDependsOn(schedule);//####[208]####
        taskinfo.addDependsOn(processorCount);//####[208]####
        taskinfo.addDependsOn(graph);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setQueueArgIndexes(0);//####[208]####
        taskinfo.setIsPipeline(true);//####[208]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[208]####
        taskinfo.addDependsOn(processorCount);//####[208]####
        taskinfo.addDependsOn(graph);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setQueueArgIndexes(1);//####[208]####
        taskinfo.setIsPipeline(true);//####[208]####
        taskinfo.setTaskIdArgIndexes(2);//####[208]####
        taskinfo.addDependsOn(graph);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setQueueArgIndexes(1);//####[208]####
        taskinfo.setIsPipeline(true);//####[208]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[208]####
        taskinfo.addDependsOn(schedule);//####[208]####
        taskinfo.addDependsOn(graph);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setQueueArgIndexes(0, 1);//####[208]####
        taskinfo.setIsPipeline(true);//####[208]####
        taskinfo.setTaskIdArgIndexes(2);//####[208]####
        taskinfo.addDependsOn(graph);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, BlockingQueue<Graph> graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setQueueArgIndexes(2);//####[208]####
        taskinfo.setIsPipeline(true);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setQueueArgIndexes(2);//####[208]####
        taskinfo.setIsPipeline(true);//####[208]####
        taskinfo.setTaskIdArgIndexes(0);//####[208]####
        taskinfo.addDependsOn(schedule);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setQueueArgIndexes(0, 2);//####[208]####
        taskinfo.setIsPipeline(true);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setQueueArgIndexes(2);//####[208]####
        taskinfo.setIsPipeline(true);//####[208]####
        taskinfo.setTaskIdArgIndexes(1);//####[208]####
        taskinfo.addDependsOn(processorCount);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setQueueArgIndexes(2);//####[208]####
        taskinfo.setIsPipeline(true);//####[208]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[208]####
        taskinfo.addDependsOn(schedule);//####[208]####
        taskinfo.addDependsOn(processorCount);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setQueueArgIndexes(0, 2);//####[208]####
        taskinfo.setIsPipeline(true);//####[208]####
        taskinfo.setTaskIdArgIndexes(1);//####[208]####
        taskinfo.addDependsOn(processorCount);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setQueueArgIndexes(1, 2);//####[208]####
        taskinfo.setIsPipeline(true);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setQueueArgIndexes(1, 2);//####[208]####
        taskinfo.setIsPipeline(true);//####[208]####
        taskinfo.setTaskIdArgIndexes(0);//####[208]####
        taskinfo.addDependsOn(schedule);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[208]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[208]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[208]####
        // ensure Method variable is set//####[208]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[208]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[208]####
        }//####[208]####
        taskinfo.setQueueArgIndexes(0, 1, 2);//####[208]####
        taskinfo.setIsPipeline(true);//####[208]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[208]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[208]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[208]####
    }//####[208]####
    /**
	 * Called when making tasks
	 *///####[208]####
    public static void __pt__CreateScheduleTask(Schedule schedule, int processorCount, Graph graph) {//####[208]####
        CreateSchedule(schedule, processorCount, graph);//####[209]####
    }//####[210]####
//####[210]####
//####[215]####
    /**
	 * Called when sequential
	 *///####[215]####
    public static void CreateSchedule(Schedule schedule, int processorCount, Graph g) {//####[215]####
        Greedy greedy = new Greedy();//####[216]####
        ScheduleGraphPair sgPair = greedy.greedySearch(g, processorCount, schedule);//####[217]####
        if (sgPair.schedule.scheduleLength < ScheduleHelper.currentBestSchedule.scheduleLength) //####[220]####
        {//####[220]####
            ScheduleHelper.currentBestSchedule.scheduleLength = sgPair.schedule.scheduleLength;//####[221]####
            for (Node n : sgPair.g) //####[223]####
            {//####[223]####
                for (Node bestN : ScheduleHelper.bestGraph) //####[224]####
                {//####[224]####
                    if (n.getIndex() == bestN.getIndex()) //####[225]####
                    {//####[225]####
                        Graphs.copyAttributes(n, bestN);//####[226]####
                    }//####[227]####
                }//####[228]####
            }//####[229]####
        }//####[230]####
        BranchAndBound bnb = new BranchAndBound(sgPair.schedule, sgPair.g);//####[233]####
        bnb.branchAndBoundAlgorithm();//####[234]####
    }//####[236]####
}//####[236]####
