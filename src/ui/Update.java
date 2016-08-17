package ui;

import org.graphstream.graph.Graph;

import scheduler.Greedy.QueueItem;

public class Update {
	private int processorNum;
	public Update(int processorNum ){
		this.processorNum=processorNum;
	}
	public void updateColor(int id,int processor,Graph g){
	switch (processor){
			case 1: 
				g.getNode(id).addAttribute("ui.style", "fill-color: red;");
				break;
			case 2:
				g.getNode(id).addAttribute("ui.style", "fill-color: blue;");
				break;
			case 3:
				g.getNode(id).addAttribute("ui.style", "fill-color: yellow;");
				break;
			case 4:
				g.getNode(id).addAttribute("ui.style", "fill-color: green;");
				break;
			case 5:
				g.getNode(id).addAttribute("ui.style", "fill-color: magenta;");
				break;
			case 6:
				g.getNode(id).addAttribute("ui.style", "fill-color: cyan;");
				break;
			case 7:
				g.getNode(id).addAttribute("ui.style", "fill-color: brown;");
				break;
			}
		}
		
		
	}
	
	
	

