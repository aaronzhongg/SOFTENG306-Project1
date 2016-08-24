package ui;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 * This panel displays all the items for the key
 * Prints out the different colours of the nodes and their corresponding meanings
 */
public class KeyPanel extends JPanel{

	//suppress warnings
	private static final long serialVersionUID = 1L;
	
	// intantiates an empty KeyPanel
	public KeyPanel(){} 

	/**
	 * creates the key for the node colours to be visible on the GUI
	 */
	protected void paintComponent(Graphics g){
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
		g.drawString("The resulting graph colors various nodes according to their different processors",w/10,62);

	}
}
