package ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import main.Main;
import scheduler.Greedy.QueueItem;
import scheduler.Schedule;
import scheduler.ScheduleHelper;

public class Update {
	//private File file;
	private int processorNum;
	private List<String> change;
	private ArrayList<Node> schedule;
	
	public Update(int processorNum){
		this.processorNum=processorNum;
		//file= new File("/Users/Jing/Desktop/newworkspace/SOFTENG306-Project1-feature-branch-and-bound/tmp/change.txt");
		change=Collections.synchronizedList( new ArrayList<String>());
		//this.schedule=schedule;
	}
	public void setSchedule( ArrayList<Node> schedule ){
		this.schedule=schedule;
	}
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
						Main.gVis.getNode(n.getId()).addAttribute("ui.style", "fill-color: yellow;");
					case 3:
						Main.gVis.getNode(n.getId()).addAttribute("ui.style", "fill-color: brown;");
						break;
					case 4:
						Main.gVis.getNode(n.getId()).addAttribute("ui.style", "fill-color: magenta;");
						break;
			
						
					}
			
		}
		

		}
	
	public void updateColor(String id,String color){
		change.add(id+","+color+";");
		
		Main.gVis.getNode(id).addAttribute("ui.style",  "fill-color:"+color+";");
//		if(thread!=-1){
//			
//		}
		if(!(Main.gVis.getNode(id).getAttribute("Processor").equals(-1))){
		Main.gVis.getNode(id).addAttribute("ui.label",  id+" Processor: "+Main.gVis.getNode(id).getAttribute("Processor"));
		change.add("label,"+id+","+Main.gVis.getNode(id).getAttribute("Processor"));
		}
	}
		
	
//	public void saveChange(String change){
		
//  try {
//		// if file doesnt exists, then create it
//		if (!file.exists()) {
//			file.createNewFile();
//		}
//		
//			FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
//		BufferedWriter bw = new BufferedWriter(fw);
//		bw.write(change+'\n');
//			bw.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//	}
	
	
	public void replayChange(int delay) throws InterruptedException{
		for (Node n : Main.gVis) {
			n.addAttribute("ui.style","fill-color:gray;");
			n.addAttribute("ui.label", " "+n.getId());
		}
		

	for(int i=0; i<change.size();i++){
		//System.out.println(change.size());
		long startTime=System.currentTimeMillis();
		while(System.currentTimeMillis()-startTime<delay){
			
		}
	   String[] temp=change.get(i).split(",");
	 //  System.out.println(temp[0]+temp[1]);
	   if(temp.length>2){
		   Main.gVis.getNode(temp[1]).addAttribute("ui.label", temp[1]+" Processor: "+temp[2]);
		   
	   }
	   else{
	   Main.gVis.getNode(temp[0]).addAttribute("ui.style", "fill-color:"+temp[1]);
	   }
	}
	
	updateProcessorColor(ScheduleHelper.bestGraph);
	}
	public void updateSchedule(Graph g) {
		for(Node n: g ){
			Main.gVis.getNode(n.getId()).addAttribute("ui.style", "fill-color:cyan;");
			Main.gVis.getNode(n.getId()).addAttribute("ui.label", n.getId()+" Processor: "+n.getAttribute("Processor"));
			
		}
	}
	public void updateProcessor(String id, int i) {
		Main.gVis.getNode(id).addAttribute("ui.label", id+" Processor: "+i);
		change.add("label,"+id+","+i);
		
	}
		
	}
	
	
	

