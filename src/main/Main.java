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
    public static boolean inParallel = true;//####[22]####
//####[23]####
    public static boolean vis = true;//####[23]####
//####[28]####
    /**
	 * main
	 *///####[28]####
    public static void main(String[] args) {//####[28]####
        MainView mainView = null;//####[29]####
        int nThreads = 4;//####[31]####
        String inputFile = "TestDotFiles/Nodes_10_Random.dot";//####[65]####
        String outputFile = inputFile + "-output.dot";//####[66]####
        int processorInput = 4;//####[67]####
        File input_file = new File(inputFile);//####[69]####
        io IOProcessor = new io();//####[70]####
        Schedule schedule;//####[71]####
        Graph g = IOProcessor.DOTParser(input_file, inputFile);//####[72]####
        if (vis) //####[74]####
        {//####[74]####
            gVis = IOProcessor.getVisGraph();//####[76]####
            update = new Update(processorInput);//####[77]####
            mainView = new MainView(gVis);//####[78]####
            mainView.setVisible(true);//####[79]####
        }//####[80]####
        initialTime = System.currentTimeMillis();//####[83]####
        endTime = 0;//####[84]####
        ArrayList<Integer> rootnodes = ScheduleHelper.findRootNodes(g);//####[86]####
        ScheduleHelper.currentBestSchedule = new Schedule(processorInput);//####[88]####
        ScheduleHelper.bestGraph = Graphs.clone(g);//####[89]####
        ScheduleHelper.currentBestSchedule.scheduleLength = 2147483647;//####[90]####
        Greedy greedy = new Greedy();//####[91]####
        ScheduleHelper.makeDependencyMatrix(g);//####[92]####
        for (int rootNode : rootnodes) //####[95]####
        {//####[95]####
            Graph tempNewGraph = Graphs.clone(g);//####[97]####
            Schedule tempNewSchedule = new Schedule(processorInput);//####[98]####
            tempNewSchedule.addNode(tempNewGraph.getNode(rootNode), 0, 0);//####[99]####
            tempNewGraph.getNode(rootNode).setAttribute("Processor", 0);//####[100]####
            if (vis) //####[101]####
            {//####[101]####
                update.updateColor(rootNode + "", "black");//####[101]####
            }//####[101]####
            ArrayList<Integer> processableNodes = new ArrayList<Integer>();//####[103]####
            for (Node n : tempNewGraph) //####[105]####
            {//####[105]####
                if (!tempNewSchedule.schedule.contains(n)) //####[106]####
                {//####[106]####
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
                    if (nThreads > processorInput) //####[119]####
                    {//####[119]####
                        nThreads = processorInput;//####[120]####
                    }//####[121]####
                    int tempProcessorCount = 0;//####[122]####
                    while (tempProcessorCount < processorInput) //####[124]####
                    {//####[124]####
                        TaskIDGroup<TaskID<Void>> taskGroup = new TaskIDGroup<TaskID<Void>>(nThreads);//####[125]####
                        for (int i = 0; ((i < nThreads) && (tempProcessorCount < processorInput)); i++) //####[126]####
                        {//####[126]####
                            Graph newGraph = Graphs.clone(tempNewGraph);//####[127]####
                            Schedule newSchedule = new Schedule(processorInput);//####[128]####
                            newSchedule.addNode(newGraph.getNode(rootNode), 0, 0);//####[129]####
                            newSchedule.updateProcessorLength(0, (int) Double.parseDouble(newGraph.getNode(rootNode).getAttribute("Weight").toString()));//####[130]####
                            int procWaitTime = ScheduleHelper.checkChildNode(newGraph.getNode(processableNodeIndex), newSchedule, tempProcessorCount);//####[133]####
                            if (procWaitTime > -1) //####[134]####
                            {//####[134]####
                                if (vis) //####[135]####
                                {//####[135]####
                                    update.updateColor(processableNodeIndex + "", "green");//####[136]####
                                    update.updateProcessor(processableNodeIndex + "", tempProcessorCount);//####[137]####
                                }//####[138]####
                                newSchedule.addNode(newGraph.getNode(processableNodeIndex), tempProcessorCount, procWaitTime);//####[140]####
                                newSchedule.updateProcessorLength(tempProcessorCount, procWaitTime + (int) Double.parseDouble(newGraph.getNode(processableNodeIndex).getAttribute("Weight").toString()));//####[141]####
                                TaskID<Void> id = CreateScheduleTask(newSchedule, processorInput, newGraph);//####[142]####
                                taskGroup.add(id);//####[143]####
                            }//####[144]####
                            tempProcessorCount++;//####[145]####
                        }//####[146]####
                        try {//####[147]####
                            taskGroup.waitTillFinished();//####[148]####
                        } catch (Exception e) {//####[150]####
                        }//####[150]####
                    }//####[151]####
                } else {//####[154]####
                    int tempProcessorCount = 0;//####[156]####
                    while (tempProcessorCount < processorInput) //####[157]####
                    {//####[157]####
                        Graph newGraph = Graphs.clone(tempNewGraph);//####[159]####
                        Schedule newSchedule = new Schedule(processorInput);//####[160]####
                        newSchedule.addNode(newGraph.getNode(rootNode), 0, 0);//####[161]####
                        newSchedule.updateProcessorLength(0, (int) Double.parseDouble(newGraph.getNode(rootNode).getAttribute("Weight").toString()));//####[162]####
                        int procWaitTime = ScheduleHelper.checkChildNode(newGraph.getNode(processableNodeIndex), newSchedule, tempProcessorCount);//####[165]####
                        if (procWaitTime > -1) //####[166]####
                        {//####[166]####
                            if (vis) //####[167]####
                            {//####[167]####
                                update.updateColor(processableNodeIndex + "", "green");//####[167]####
                                update.updateProcessor(processableNodeIndex + "", tempProcessorCount);//####[168]####
                            }//####[168]####
                            newSchedule.addNode(newGraph.getNode(processableNodeIndex), tempProcessorCount, procWaitTime);//####[170]####
                            newSchedule.updateProcessorLength(tempProcessorCount, procWaitTime + (int) Double.parseDouble(newGraph.getNode(processableNodeIndex).getAttribute("Weight").toString()));//####[171]####
                            CreateSchedule(newSchedule, processorInput, newGraph);//####[172]####
                        }//####[173]####
                        tempProcessorCount++;//####[174]####
                    }//####[175]####
                }//####[177]####
            }//####[178]####
            if (vis) //####[181]####
            {//####[181]####
                update.updateProcessorColor(ScheduleHelper.bestGraph);//####[182]####
                mainView.updateSchedule(ScheduleHelper.currentBestSchedule.scheduleLength);//####[183]####
                mainView.start.setEnabled(true);//####[184]####
            }//####[185]####
            endTime = System.currentTimeMillis();//####[186]####
            System.out.println(endTime - initialTime);//####[187]####
            for (Node n : ScheduleHelper.bestGraph) //####[189]####
            {//####[189]####
                System.out.println("Node id: " + n.getId() + " ProcID: " + n.getAttribute("Processor") + " Starts at: " + n.getAttribute("Start") + " Node Weight: " + n.getAttribute("Weight"));//####[190]####
            }//####[191]####
            System.out.println("Total Schedule Length: " + ScheduleHelper.currentBestSchedule.scheduleLength);//####[192]####
        }//####[194]####
    }//####[195]####
//####[200]####
    private static volatile Method __pt__CreateScheduleTask_Schedule_int_Graph_method = null;//####[200]####
    private synchronized static void __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet() {//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            try {//####[200]####
                __pt__CreateScheduleTask_Schedule_int_Graph_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__CreateScheduleTask", new Class[] {//####[200]####
                    Schedule.class, int.class, Graph.class//####[200]####
                });//####[200]####
            } catch (Exception e) {//####[200]####
                e.printStackTrace();//####[200]####
            }//####[200]####
        }//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, Graph graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, Graph graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setTaskIdArgIndexes(0);//####[200]####
        taskinfo.addDependsOn(schedule);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, Graph graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setQueueArgIndexes(0);//####[200]####
        taskinfo.setIsPipeline(true);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, Graph graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setTaskIdArgIndexes(1);//####[200]####
        taskinfo.addDependsOn(processorCount);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, Graph graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[200]####
        taskinfo.addDependsOn(schedule);//####[200]####
        taskinfo.addDependsOn(processorCount);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, Graph graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setQueueArgIndexes(0);//####[200]####
        taskinfo.setIsPipeline(true);//####[200]####
        taskinfo.setTaskIdArgIndexes(1);//####[200]####
        taskinfo.addDependsOn(processorCount);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setQueueArgIndexes(1);//####[200]####
        taskinfo.setIsPipeline(true);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setQueueArgIndexes(1);//####[200]####
        taskinfo.setIsPipeline(true);//####[200]####
        taskinfo.setTaskIdArgIndexes(0);//####[200]####
        taskinfo.addDependsOn(schedule);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setQueueArgIndexes(0, 1);//####[200]####
        taskinfo.setIsPipeline(true);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, TaskID<Graph> graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setTaskIdArgIndexes(2);//####[200]####
        taskinfo.addDependsOn(graph);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, TaskID<Graph> graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[200]####
        taskinfo.addDependsOn(schedule);//####[200]####
        taskinfo.addDependsOn(graph);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, TaskID<Graph> graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setQueueArgIndexes(0);//####[200]####
        taskinfo.setIsPipeline(true);//####[200]####
        taskinfo.setTaskIdArgIndexes(2);//####[200]####
        taskinfo.addDependsOn(graph);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[200]####
        taskinfo.addDependsOn(processorCount);//####[200]####
        taskinfo.addDependsOn(graph);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setTaskIdArgIndexes(0, 1, 2);//####[200]####
        taskinfo.addDependsOn(schedule);//####[200]####
        taskinfo.addDependsOn(processorCount);//####[200]####
        taskinfo.addDependsOn(graph);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setQueueArgIndexes(0);//####[200]####
        taskinfo.setIsPipeline(true);//####[200]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[200]####
        taskinfo.addDependsOn(processorCount);//####[200]####
        taskinfo.addDependsOn(graph);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setQueueArgIndexes(1);//####[200]####
        taskinfo.setIsPipeline(true);//####[200]####
        taskinfo.setTaskIdArgIndexes(2);//####[200]####
        taskinfo.addDependsOn(graph);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setQueueArgIndexes(1);//####[200]####
        taskinfo.setIsPipeline(true);//####[200]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[200]####
        taskinfo.addDependsOn(schedule);//####[200]####
        taskinfo.addDependsOn(graph);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setQueueArgIndexes(0, 1);//####[200]####
        taskinfo.setIsPipeline(true);//####[200]####
        taskinfo.setTaskIdArgIndexes(2);//####[200]####
        taskinfo.addDependsOn(graph);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, BlockingQueue<Graph> graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setQueueArgIndexes(2);//####[200]####
        taskinfo.setIsPipeline(true);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setQueueArgIndexes(2);//####[200]####
        taskinfo.setIsPipeline(true);//####[200]####
        taskinfo.setTaskIdArgIndexes(0);//####[200]####
        taskinfo.addDependsOn(schedule);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setQueueArgIndexes(0, 2);//####[200]####
        taskinfo.setIsPipeline(true);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setQueueArgIndexes(2);//####[200]####
        taskinfo.setIsPipeline(true);//####[200]####
        taskinfo.setTaskIdArgIndexes(1);//####[200]####
        taskinfo.addDependsOn(processorCount);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setQueueArgIndexes(2);//####[200]####
        taskinfo.setIsPipeline(true);//####[200]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[200]####
        taskinfo.addDependsOn(schedule);//####[200]####
        taskinfo.addDependsOn(processorCount);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setQueueArgIndexes(0, 2);//####[200]####
        taskinfo.setIsPipeline(true);//####[200]####
        taskinfo.setTaskIdArgIndexes(1);//####[200]####
        taskinfo.addDependsOn(processorCount);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setQueueArgIndexes(1, 2);//####[200]####
        taskinfo.setIsPipeline(true);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setQueueArgIndexes(1, 2);//####[200]####
        taskinfo.setIsPipeline(true);//####[200]####
        taskinfo.setTaskIdArgIndexes(0);//####[200]####
        taskinfo.addDependsOn(schedule);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[200]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setQueueArgIndexes(0, 1, 2);//####[200]####
        taskinfo.setIsPipeline(true);//####[200]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[200]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    /**
	 * Called when making tasks
	 *///####[200]####
    public static void __pt__CreateScheduleTask(Schedule schedule, int processorCount, Graph graph) {//####[200]####
        CreateSchedule(schedule, processorCount, graph);//####[202]####
    }//####[204]####
//####[204]####
//####[209]####
    /**
	 * Called when sequential
	 *///####[209]####
    public static void CreateSchedule(Schedule schedule, int processorCount, Graph g) {//####[209]####
        Greedy greedy = new Greedy();//####[210]####
        ScheduleGraphPair sgPair = greedy.greedySearch(g, processorCount, schedule);//####[211]####
        if (sgPair.schedule.scheduleLength < ScheduleHelper.currentBestSchedule.scheduleLength) //####[214]####
        {//####[214]####
            ScheduleHelper.currentBestSchedule.scheduleLength = sgPair.schedule.scheduleLength;//####[215]####
            for (Node n : sgPair.g) //####[217]####
            {//####[217]####
                for (Node bestN : ScheduleHelper.bestGraph) //####[218]####
                {//####[218]####
                    if (n.getIndex() == bestN.getIndex()) //####[219]####
                    {//####[219]####
                        Graphs.copyAttributes(n, bestN);//####[220]####
                    }//####[221]####
                }//####[222]####
            }//####[223]####
        }//####[224]####
        BranchAndBound bnb = new BranchAndBound(sgPair.schedule, sgPair.g);//####[227]####
        bnb.branchAndBoundAlgorithm();//####[228]####
    }//####[230]####
}//####[230]####
