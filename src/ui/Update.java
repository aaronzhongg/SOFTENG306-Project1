package ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import main.Main;

import scheduler.ScheduleHelper;

/**
 * Contains all methods called when something from the GUI needs to update
 */
public class Update {

	private int processorNum;
	private List<String> change;
	private ArrayList<Node> schedule;

	/**
	 * initailises the processor number to be correct for following calls
	 * @param processorNum
	 */
	public Update(int processorNum){
		this.processorNum=processorNum;
		change=Collections.synchronizedList(new ArrayList<String>());
	}

	/**
	 * sets the local schedule to be the one passed in
	 * @param schedule
	 */
	public void setSchedule( ArrayList<Node> schedule ){
		this.schedule=schedule;
	}

	/**
	 * updates the processor color of the the graph
	 * @param best
	 */
	public void updateProcessorColor(Graph best){
		for(Node n:best){
			int processor=n.getAttribute("Processor");
			Main.gVis.getNode(n.getId()).addAttribute("ui.label", n.getId()+" Processor: "+processor);
			switch (processor){
			case 0: 
				Main.gVis.getNode(n.getId()).addAttribute("ui.style", "fill-color: red;");
				break;
			case 1:
				Main.gVis.getNode(n.getId()).addAttribute("ui.style", "fill-color: blue;");
				break;
			case 2:
				Main.gVis.getNode(n.getId()).addAttribute("ui.style", "fill-color: orange;");
				break;
			case 3:
				Main.gVis.getNode(n.getId()).addAttribute("ui.style", "fill-color: brown;");
				break;
			case 4:
				Main.gVis.getNode(n.getId()).addAttribute("ui.style", "fill-color: magenta;");
				break;	
			}
		}	
	}

	/**
	 * updates the color of the node
	 * @param id : The node index
	 * @param color : the new node color
	 */
	public void updateColor(String id,String color){
		change.add(id+","+color+";");

		Main.gVis.getNode(id).addAttribute("ui.style",  "fill-color:"+color+";");
		// If the node has been assigned a processor
		if(!(Main.gVis.getNode(id).getAttribute("Processor").equals(-1))){
			Main.gVis.getNode(id).addAttribute("ui.label",  id+" Processor: "+Main.gVis.getNode(id).getAttribute("Processor"));
			change.add("label,"+id+","+Main.gVis.getNode(id).getAttribute("Processor"));
		}
	}	

	/**
	 * 
	 * @param delay
	 * @throws InterruptedException
	 */
	public void replayChange(int delay) throws InterruptedException{
		for (Node n : Main.gVis) {
			n.addAttribute("ui.style","fill-color:gray;");
			n.addAttribute("ui.label", " "+n.getId());
		}

		for(int i=0; i<change.size();i++){
			long startTime=System.currentTimeMillis();
			while(System.currentTimeMillis()-startTime<delay){

			}
			String[] temp=((String) change.get(i)).split(",");
			if(temp.length>2){
				Main.gVis.getNode(temp[1]).addAttribute("ui.label", temp[1]+" Processor: "+temp[2]);

			}
			else{
				Main.gVis.getNode(temp[0]).addAttribute("ui.style", "fill-color:"+temp[1]);
			}
		}
		updateProcessorColor(ScheduleHelper.bestGraph);
	}
	
	/**
	 * 
	 * @param g
	 */
	public void updateSchedule(Graph g) {
		for(Node n: g ){
			Main.gVis.getNode(n.getId()).addAttribute("ui.style", "fill-color:cyan;");
			Main.gVis.getNode(n.getId()).addAttribute("ui.label", n.getId()+" Processor: "+n.getAttribute("Processor"));

		}
	}
	
	/**
	 * This method updates the processor of a particular node during the visualisation of the search.
	 * @param id
	 * @param i
	 */
	public void updateProcessor(String id, int i) {
		Main.gVis.getNode(id).addAttribute("ui.label", id+" Processor: "+i);
		change.add("label,"+id+","+i);

	}

}




