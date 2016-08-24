package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;



import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

import org.graphstream.graph.*;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import scheduler.ScheduleHelper;

import main.Main;
public class MainView extends JFrame implements ActionListener{
	private Graph g;
	private int speed=300;
	private Thread replayThread;
	private JLabel time,timeValue;
	private int length=0;
	private JLabel current;
	public static final JButton start=new JButton("Replay Branch-and-bound");
	

	//private InfoPanel infoPanel;

public MainView(Graph g){
	this.g=g;
	String location="file://"+System.getProperty("user.dir")+"/StyleSheet.css";
	g.addAttribute("ui.stylesheet", "url('"+location+"')");
	setLayout(new BorderLayout());
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	this.setBounds(200, 200, 800, 500);
	Viewer viewer=new Viewer(g,  Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
	viewer.enableAutoLayout();
	View view = viewer.addDefaultView(false); 

	add((Component)view,BorderLayout.CENTER);
	JPanel buttonP=new JPanel();
	final JButton stop=new JButton("Stop Replay");
	stop.setEnabled(false);
	JLabel delay=new JLabel("Time Delay: ");
	JLabel milli=new JLabel(" Millisec");
	String[] speedStrings = { "20", "50", "100", "300","500" };
	final JComboBox speedCmbo = new JComboBox(speedStrings);
	speedCmbo.setSelectedIndex(3);
	ItemListener itemListener = new ItemListener() {
	      public void itemStateChanged(ItemEvent itemEvent) {
	        int state = itemEvent.getStateChange();
	       // System.out.println((state == ItemEvent.SELECTED) ? "Selected" : "Deselected");
	        System.out.println("Item: " + speedCmbo.getSelectedItem());
	       // ItemSelectable is = itemEvent.getItemSelectable();
	       // System.out.println(", Selected: " + selectedString(is));
	        speed=  Integer.parseInt((String) speedCmbo.getSelectedItem()) ;
	      }
	    };
	    speedCmbo.addItemListener(itemListener);
	    
	
	
	start.setEnabled(false);
	 
	initialThread(start,stop);
	start.addActionListener(new ActionListener()
	{     
		  public void actionPerformed(ActionEvent e)
		  {   
			  start.setEnabled(false);
		      stop.setEnabled(true);
		  
			  System.out.println("speed: "+speed);
		 
			  
			  replayThread.start();
		  }
		});
	
	stop.addActionListener(new ActionListener()
	{     
		
		@SuppressWarnings("deprecation")
		public void actionPerformed(ActionEvent e)
		  {  
			  replayThread.stop();
			  initialThread(start,stop);
			    	//  executor.shutdown();
			          Main.update.updateProcessorColor( ScheduleHelper.bestGraph);
					start.setEnabled(true);
					stop.setEnabled(false);
			     
		  }
	});
	
	
	
	time=new JLabel("Total Time : ");
	timeValue=new JLabel("0 MilliSec");
	JPanel timeP=new JPanel();
	
	timeP.add(time);
	timeP.add(timeValue);
	Timer timer = new Timer(10, this);
    timer.setInitialDelay(0);
    timer.start();
    
	 current=new JLabel("     Schedule Length: 0 ");
	JPanel scheduleP=new JPanel();
	timeP.add(current);
	

	buttonP.add(stop);
	buttonP.add(start);
	buttonP.add(delay);
	buttonP.add(speedCmbo);
	buttonP.add(milli);
	JPanel text=new JPanel();
	//JTextArea result=new JTextArea();
	
	//JScrollPane sp = new JScrollPane(result);
	//sp.setSize(450,200);
	//text.add(result);
	
	JPanel key=new JPanel();
	
	
	
	
	JPanel info=new JPanel();
	info.setLayout(new BorderLayout());
	info.add(buttonP,BorderLayout.NORTH);
	info.add(timeP,BorderLayout.CENTER);
	
	
	//info.add(sp,BorderLayout.SOUTH);
	
	add(info,BorderLayout.SOUTH);
	
	setVisible(true);
}



private void initialThread(final JButton start,final JButton stop){
	
	this.replayThread = new Thread(){
		public void run() {
	        try {
	      	  
				Main.update.replayChange(speed);
				start.setEnabled(true);
				stop.setEnabled(false);
				initialThread(start,stop);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	};
}

@Override
public void actionPerformed(ActionEvent e) {
	long currentTime = System.currentTimeMillis();
	long difference = currentTime - Main.initialTime;
	if(Main.endTime!=0){
	difference=	Main.endTime-Main.initialTime;
	}
	 timeValue.setText(difference+" MilliSec     ");
	
}

public void updateSchedule(int length){
	current.setText("     Schedule Length: "+length+" ");
}

}
