package ui;

import org.graphstream.graph.Graph;

import scheduler.Greedy.QueueItem;

public class Update {
	private int processorNum;
	public Update(int processorNum ){
		this.processorNum=processorNum;
	}
	public void updateColor(int id,int processor,Graph g){
		for(int i=0;i<processorNum;i++){
			
			
			//System.out.println(processor+"??");
			if(processor==i){
			
			int colorR=255/(i+1);
			
			
			
			//System.out.println(color);
			g.getNode(id).addAttribute("ui.style", "fill-color: rgb("+colorR+",100,255);");
		}
		}
		
		
	}
	
	
	
}
