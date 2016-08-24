package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.graphstream.graph.*;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import scheduler.ScheduleHelper;

import main.Main;

/**
 * Instantiates the main JFrame for visualisation
 * Contains the graph, and all the personalisable options
 */
public class MainView extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = 1L; // suppress warning
	//initialises variables
	private Graph g;
	private int speed=20;
	private Thread replayThread;
	private JLabel time,timeValue;
	private JLabel current;
	public static final JButton start=new JButton("Replay Branch-and-bound");

	/**
	 * The MainView is the JFrame visible when visualisation is required
	 * All objects visible are instantiated here
	 * @param g : The input graph
	 */
	public MainView(Graph g){
		
		this.g = g; // sets the graph
		
		//adds css style to attributes
		g.addAttribute("ui.stylesheet", "node {"+
				"size: 10px, 15px;"+
				"shape: box;"+
				"fill-color: gray;"+
				"stroke-mode: plain;"+
				"stroke-color: yellow;"+
				"text-style:bold;"+
				"text-alignment:at-right;"+
				"text-offset:40;"+
				"text-size:15;"+
				"}");
		
		setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.setBounds(200, 200, 800, 500); // size
		
		Viewer viewer=new Viewer(g,  Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		viewer.enableAutoLayout();
		View view = viewer.addDefaultView(false); 

		add((Component)view,BorderLayout.CENTER); // adds component to the center
		
		JPanel buttonP=new JPanel();// adds buttons
		final JButton stop=new JButton("Stop Replay");
		stop.setEnabled(false);
		
		//adds delay combo box
		JLabel delay=new JLabel("Time Delay: ");
		JLabel milli=new JLabel(" Millisec");
		String[] speedStrings = { "20", "50", "100", "300","500" };
		final JComboBox<String> speedCmbo = new JComboBox<String>(speedStrings);
		speedCmbo.setSelectedIndex(0);
		ItemListener itemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				speed=  Integer.parseInt((String) speedCmbo.getSelectedItem()) ;
			}
		};
		speedCmbo.addItemListener(itemListener);

		start.setEnabled(false); // disables button
		initialThread(start,stop); // starts thread
		start.addActionListener(new ActionListener() // waits for action at start button
		{     
			public void actionPerformed(ActionEvent e) // if an action is performed
			{   
				start.setEnabled(false);
				stop.setEnabled(true);
				replayThread.start();
			}
		});

		stop.addActionListener(new ActionListener() // waits for action at stop button
		{     
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e)
			{  
				replayThread.stop();
				initialThread(start,stop);
				Main.update.updateProcessorColor( ScheduleHelper.bestGraph);
				start.setEnabled(true);
				stop.setEnabled(false);
			}
		});
		
		//creates time label
		time=new JLabel("Total Time : ");
		timeValue=new JLabel("0 MilliSec");
		JPanel timeP=new JPanel();
		timeP.add(time);
		timeP.add(timeValue);
		Timer timer = new Timer(10, this); // adds timer to it
		timer.setInitialDelay(0); // sets  0 delay
		timer.start(); // starts the timer

		current=new JLabel("     Schedule Length: 0 ");

		timeP.add(current);
		
		//adds all butons to button panels
		buttonP.add(stop);
		buttonP.add(start);
		buttonP.add(delay);
		buttonP.add(speedCmbo);
		buttonP.add(milli);

		KeyPanel key=new KeyPanel(); // creates panel for the key
		key.setPreferredSize(new Dimension(500,80));

		JPanel info=new JPanel(); // creates panel and add button, timer and key accordingly
		info.setLayout(new BorderLayout());
		info.add(buttonP,BorderLayout.NORTH);
		info.add(timeP,BorderLayout.CENTER);
		info.add(key,BorderLayout.SOUTH);

		add(info,BorderLayout.SOUTH); // adds info to main frame

		setVisible(true); // makes main view visible
	}
	
	/**
	 * initialises the main thread that the buttons run on
	 * @param start
	 * @param stop
	 */
	private void initialThread(final JButton start,final JButton stop){

		this.replayThread = new Thread(){
			public void run() {
				try {
					Main.update.replayChange(speed);
					start.setEnabled(true);
					stop.setEnabled(false);
					initialThread(start,stop);
				} catch (InterruptedException e) { e.printStackTrace();} // catches exception
			}
		};
	}

	/**
	 * Overrides action performed to set the time value shownn on the GUI
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		long currentTime = System.currentTimeMillis();
		long difference = currentTime - Main.initialTime;
		if(Main.endTime!=0){
			difference=	Main.endTime-Main.initialTime;
		}
		timeValue.setText(difference+" MilliSec     ");
	}

	/**
	 * Updates the schedule length on the GUI
	 * @param length
	 */
	public void updateSchedule(int length){
		current.setText("     Schedule Length: "+length+" ");
	}

}
