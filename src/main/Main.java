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
        if (true) //####[60]####
        {//####[60]####
            inParallel = true;//####[61]####
            vis = false;//####[62]####
        }//####[63]####
        String inputFile = "TestDotFiles/Nodes_11_OutTree.dot";//####[66]####
        int processorInput = 4;//####[67]####
        File input_file = new File(inputFile);//####[69]####
        io IOProcessor = new io();//####[70]####
        Schedule schedule;//####[71]####
        Graph g = IOProcessor.DOTParser(input_file, inputFile);//####[73]####
        if (vis) //####[75]####
        {//####[75]####
            gVis = IOProcessor.getVisGraph();//####[77]####
            update = new Update(processorInput);//####[78]####
            mainView = new MainView(gVis);//####[79]####
            mainView.setVisible(true);//####[80]####
        }//####[83]####
        initialTime = System.currentTimeMillis();//####[85]####
        endTime = 0;//####[86]####
        ArrayList<Integer> rootnodes = ScheduleHelper.findRootNodes(g);//####[90]####
        ScheduleHelper.currentBestSchedule = new Schedule(processorInput);//####[95]####
        ScheduleHelper.bestGraph = Graphs.clone(g);//####[96]####
        ScheduleHelper.currentBestSchedule.scheduleLength = 2147483647;//####[98]####
        Greedy greedy = new Greedy();//####[99]####
        ScheduleHelper.makeDependencyMatrix(g);//####[100]####
        for (int rootNode : rootnodes) //####[104]####
        {//####[104]####
            Graph tempNewGraph = Graphs.clone(g);//####[106]####
            Schedule tempNewSchedule = new Schedule(processorInput);//####[107]####
            tempNewSchedule.addNode(tempNewGraph.getNode(rootNode), 0, 0);//####[108]####
            tempNewGraph.getNode(rootNode).setAttribute("Processor", 0);//####[109]####
            if (vis) //####[110]####
            {//####[110]####
                update.updateColor(rootNode + "", "black");//####[110]####
            }//####[110]####
            ArrayList<Integer> processableNodes = new ArrayList<Integer>();//####[114]####
            for (Node n : tempNewGraph) //####[116]####
            {//####[116]####
                if (!tempNewSchedule.schedule.contains(n)) //####[117]####
                {//####[117]####
                    boolean isProcessable = ScheduleHelper.isProcessable(n, tempNewSchedule);//####[119]####
                    if (isProcessable) //####[120]####
                    {//####[120]####
                        processableNodes.add(n.getIndex());//####[121]####
                    }//####[122]####
                }//####[123]####
            }//####[124]####
            for (int processableNodeIndex : processableNodes) //####[126]####
            {//####[126]####
                if (inParallel) //####[129]####
                {//####[129]####
                    int tempProcessorCount = 0;//####[131]####
                    TaskIDGroup<TaskID<Void>> taskGroup = new TaskIDGroup<TaskID<Void>>(processorInput);//####[132]####
                    while (tempProcessorCount < processorInput) //####[133]####
                    {//####[133]####
                        Graph newGraph = Graphs.clone(tempNewGraph);//####[136]####
                        Schedule newSchedule = new Schedule(processorInput);//####[137]####
                        newSchedule.addNode(newGraph.getNode(rootNode), 0, 0);//####[138]####
                        newSchedule.updateProcessorLength(0, (int) Double.parseDouble(newGraph.getNode(rootNode).getAttribute("Weight").toString()));//####[139]####
                        int procWaitTime = ScheduleHelper.checkChildNode(newGraph.getNode(processableNodeIndex), newSchedule, tempProcessorCount);//####[142]####
                        if (procWaitTime > -1) //####[143]####
                        {//####[143]####
                            if (vis) //####[144]####
                            {//####[144]####
                                update.updateColor(processableNodeIndex + "", "green");//####[145]####
                                update.updateProcessor(processableNodeIndex + "", tempProcessorCount);//####[146]####
                            }//####[147]####
                            newSchedule.addNode(newGraph.getNode(processableNodeIndex), tempProcessorCount, procWaitTime);//####[149]####
                            newSchedule.updateProcessorLength(tempProcessorCount, procWaitTime + (int) Double.parseDouble(newGraph.getNode(processableNodeIndex).getAttribute("Weight").toString()));//####[150]####
                            TaskID<Void> id = CreateScheduleTask(newSchedule, processorInput, newGraph);//####[151]####
                            taskGroup.add(id);//####[152]####
                        }//####[153]####
                        tempProcessorCount++;//####[154]####
                    }//####[155]####
                    try {//####[156]####
                        taskGroup.waitTillFinished();//####[157]####
                    } catch (Exception e) {//####[159]####
                    }//####[159]####
                } else {//####[162]####
                    int tempProcessorCount = 0;//####[166]####
                    while (tempProcessorCount < processorInput) //####[167]####
                    {//####[167]####
                        Graph newGraph = Graphs.clone(tempNewGraph);//####[169]####
                        Schedule newSchedule = new Schedule(processorInput);//####[170]####
                        newSchedule.addNode(newGraph.getNode(rootNode), 0, 0);//####[171]####
                        newSchedule.updateProcessorLength(0, (int) Double.parseDouble(newGraph.getNode(rootNode).getAttribute("Weight").toString()));//####[172]####
                        int procWaitTime = ScheduleHelper.checkChildNode(newGraph.getNode(processableNodeIndex), newSchedule, tempProcessorCount);//####[175]####
                        if (procWaitTime > -1) //####[176]####
                        {//####[176]####
                            if (vis) //####[177]####
                            {//####[177]####
                                update.updateColor(processableNodeIndex + "", "green");//####[177]####
                                update.updateProcessor(processableNodeIndex + "", tempProcessorCount);//####[178]####
                            }//####[178]####
                            newSchedule.addNode(newGraph.getNode(processableNodeIndex), tempProcessorCount, procWaitTime);//####[180]####
                            newSchedule.updateProcessorLength(tempProcessorCount, procWaitTime + (int) Double.parseDouble(newGraph.getNode(processableNodeIndex).getAttribute("Weight").toString()));//####[181]####
                            CreateSchedule(newSchedule, processorInput, newGraph);//####[182]####
                        }//####[183]####
                        tempProcessorCount++;//####[184]####
                    }//####[185]####
                }//####[187]####
            }//####[192]####
            if (vis) //####[195]####
            {//####[195]####
                update.updateProcessorColor(ScheduleHelper.bestGraph);//####[198]####
                mainView.updateSchedule(ScheduleHelper.currentBestSchedule.scheduleLength);//####[200]####
                mainView.start.setEnabled(true);//####[201]####
            }//####[202]####
            endTime = System.currentTimeMillis();//####[203]####
            System.out.println(endTime - initialTime);//####[204]####
            for (Node n : ScheduleHelper.bestGraph) //####[207]####
            {//####[207]####
                System.out.println("Node id: " + n.getId() + " ProcID: " + n.getAttribute("Processor") + " Starts at: " + n.getAttribute("Start") + " Node Weight: " + n.getAttribute("Weight"));//####[208]####
            }//####[209]####
            System.out.println("Total Schedule Length: " + ScheduleHelper.currentBestSchedule.scheduleLength);//####[210]####
            IOProcessor.outputFile(ScheduleHelper.currentBestSchedule, ScheduleHelper.bestGraph, "input-OUTPUT");//####[211]####
        }//####[212]####
    }//####[213]####
//####[218]####
    private static volatile Method __pt__CreateScheduleTask_Schedule_int_Graph_method = null;//####[218]####
    private synchronized static void __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet() {//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            try {//####[218]####
                __pt__CreateScheduleTask_Schedule_int_Graph_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__CreateScheduleTask", new Class[] {//####[218]####
                    Schedule.class, int.class, Graph.class//####[218]####
                });//####[218]####
            } catch (Exception e) {//####[218]####
                e.printStackTrace();//####[218]####
            }//####[218]####
        }//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, Graph graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, Graph graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setTaskIdArgIndexes(0);//####[218]####
        taskinfo.addDependsOn(schedule);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, Graph graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, Graph graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setQueueArgIndexes(0);//####[218]####
        taskinfo.setIsPipeline(true);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, Graph graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setTaskIdArgIndexes(1);//####[218]####
        taskinfo.addDependsOn(processorCount);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, Graph graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[218]####
        taskinfo.addDependsOn(schedule);//####[218]####
        taskinfo.addDependsOn(processorCount);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, Graph graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setQueueArgIndexes(0);//####[218]####
        taskinfo.setIsPipeline(true);//####[218]####
        taskinfo.setTaskIdArgIndexes(1);//####[218]####
        taskinfo.addDependsOn(processorCount);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setQueueArgIndexes(1);//####[218]####
        taskinfo.setIsPipeline(true);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setQueueArgIndexes(1);//####[218]####
        taskinfo.setIsPipeline(true);//####[218]####
        taskinfo.setTaskIdArgIndexes(0);//####[218]####
        taskinfo.addDependsOn(schedule);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, Graph graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setQueueArgIndexes(0, 1);//####[218]####
        taskinfo.setIsPipeline(true);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, TaskID<Graph> graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setTaskIdArgIndexes(2);//####[218]####
        taskinfo.addDependsOn(graph);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, TaskID<Graph> graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[218]####
        taskinfo.addDependsOn(schedule);//####[218]####
        taskinfo.addDependsOn(graph);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, TaskID<Graph> graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setQueueArgIndexes(0);//####[218]####
        taskinfo.setIsPipeline(true);//####[218]####
        taskinfo.setTaskIdArgIndexes(2);//####[218]####
        taskinfo.addDependsOn(graph);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[218]####
        taskinfo.addDependsOn(processorCount);//####[218]####
        taskinfo.addDependsOn(graph);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setTaskIdArgIndexes(0, 1, 2);//####[218]####
        taskinfo.addDependsOn(schedule);//####[218]####
        taskinfo.addDependsOn(processorCount);//####[218]####
        taskinfo.addDependsOn(graph);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setQueueArgIndexes(0);//####[218]####
        taskinfo.setIsPipeline(true);//####[218]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[218]####
        taskinfo.addDependsOn(processorCount);//####[218]####
        taskinfo.addDependsOn(graph);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setQueueArgIndexes(1);//####[218]####
        taskinfo.setIsPipeline(true);//####[218]####
        taskinfo.setTaskIdArgIndexes(2);//####[218]####
        taskinfo.addDependsOn(graph);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setQueueArgIndexes(1);//####[218]####
        taskinfo.setIsPipeline(true);//####[218]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[218]####
        taskinfo.addDependsOn(schedule);//####[218]####
        taskinfo.addDependsOn(graph);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, TaskID<Graph> graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setQueueArgIndexes(0, 1);//####[218]####
        taskinfo.setIsPipeline(true);//####[218]####
        taskinfo.setTaskIdArgIndexes(2);//####[218]####
        taskinfo.addDependsOn(graph);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, BlockingQueue<Graph> graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setQueueArgIndexes(2);//####[218]####
        taskinfo.setIsPipeline(true);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setQueueArgIndexes(2);//####[218]####
        taskinfo.setIsPipeline(true);//####[218]####
        taskinfo.setTaskIdArgIndexes(0);//####[218]####
        taskinfo.addDependsOn(schedule);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, int processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setQueueArgIndexes(0, 2);//####[218]####
        taskinfo.setIsPipeline(true);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setQueueArgIndexes(2);//####[218]####
        taskinfo.setIsPipeline(true);//####[218]####
        taskinfo.setTaskIdArgIndexes(1);//####[218]####
        taskinfo.addDependsOn(processorCount);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setQueueArgIndexes(2);//####[218]####
        taskinfo.setIsPipeline(true);//####[218]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[218]####
        taskinfo.addDependsOn(schedule);//####[218]####
        taskinfo.addDependsOn(processorCount);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, TaskID<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setQueueArgIndexes(0, 2);//####[218]####
        taskinfo.setIsPipeline(true);//####[218]####
        taskinfo.setTaskIdArgIndexes(1);//####[218]####
        taskinfo.addDependsOn(processorCount);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(Schedule schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setQueueArgIndexes(1, 2);//####[218]####
        taskinfo.setIsPipeline(true);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(TaskID<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setQueueArgIndexes(1, 2);//####[218]####
        taskinfo.setIsPipeline(true);//####[218]####
        taskinfo.setTaskIdArgIndexes(0);//####[218]####
        taskinfo.addDependsOn(schedule);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph) {//####[218]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[218]####
        return CreateScheduleTask(schedule, processorCount, graph, new TaskInfo());//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static TaskID<Void> CreateScheduleTask(BlockingQueue<Schedule> schedule, BlockingQueue<Integer> processorCount, BlockingQueue<Graph> graph, TaskInfo taskinfo) {//####[218]####
        // ensure Method variable is set//####[218]####
        if (__pt__CreateScheduleTask_Schedule_int_Graph_method == null) {//####[218]####
            __pt__CreateScheduleTask_Schedule_int_Graph_ensureMethodVarSet();//####[218]####
        }//####[218]####
        taskinfo.setQueueArgIndexes(0, 1, 2);//####[218]####
        taskinfo.setIsPipeline(true);//####[218]####
        taskinfo.setParameters(schedule, processorCount, graph);//####[218]####
        taskinfo.setMethod(__pt__CreateScheduleTask_Schedule_int_Graph_method);//####[218]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[218]####
    }//####[218]####
    /**
	 * Called when making tasks
	 *///####[218]####
    public static void __pt__CreateScheduleTask(Schedule schedule, int processorCount, Graph graph) {//####[218]####
        CreateSchedule(schedule, processorCount, graph);//####[220]####
    }//####[222]####
//####[222]####
//####[227]####
    /**
	 * Called when sequential
	 *///####[227]####
    public static void CreateSchedule(Schedule schedule, int processorCount, Graph g) {//####[227]####
        Greedy greedy = new Greedy();//####[228]####
        ScheduleGraphPair sgPair = greedy.greedySearch(g, processorCount, schedule);//####[229]####
        if (sgPair.schedule.scheduleLength < ScheduleHelper.currentBestSchedule.scheduleLength) //####[232]####
        {//####[232]####
            ScheduleHelper.currentBestSchedule.scheduleLength = sgPair.schedule.scheduleLength;//####[233]####
            for (Node n : sgPair.g) //####[235]####
            {//####[235]####
                for (Node bestN : ScheduleHelper.bestGraph) //####[236]####
                {//####[236]####
                    if (n.getIndex() == bestN.getIndex()) //####[237]####
                    {//####[237]####
                        Graphs.copyAttributes(n, bestN);//####[238]####
                    }//####[239]####
                }//####[240]####
            }//####[241]####
        }//####[242]####
        BranchAndBound bnb = new BranchAndBound(sgPair.schedule, sgPair.g);//####[245]####
        bnb.branchAndBoundAlgorithm();//####[246]####
    }//####[248]####
}//####[248]####
