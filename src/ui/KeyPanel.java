package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class KeyPanel extends JPanel{
	public KeyPanel(){
	
	
	}
protected void paintComponent(Graphics g){
	int h=getHeight();
	int w = getWidth();
	
	super.paintComponent(g);
	g.setColor(Color.black);
	g.fillOval(w/10, 20, 15, 15);
	g.drawString("Root Node", (w/10)+30, 32);
	g.setColor(Color.green);
	g.fillOval((3*w/10), 20, 15, 15);
	g.setColor(Color.black);
	g.drawString("Better Result Children Nodes",(3*w/10)+30 , 32);
	g.setColor(Color.yellow);
	g.fillOval((6*w/10), 20, 15, 15);
	g.setColor(Color.black);
	g.drawString("Node processing Branch And Bound", (6*w/10)+30, 32);
	g.drawString("Result graph shows different colors of node according to the different processors.",w/10,62);
	
}
}
