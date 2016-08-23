package src.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import src.main.Main;

public class InfoPanel extends JPanel implements ActionListener{
	private JLabel time,timeValue;
	public InfoPanel(){
		this.setSize(200, 500);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		time=new JLabel("Total Time : ");
		timeValue=new JLabel("0 MilliSec");
		JPanel timeP=new JPanel();
		timeP.setSize(200, 100);
		timeP.add(time);
		timeP.add(timeValue);
		
		Timer timer = new Timer(10, this);
	     timer.setInitialDelay(0);
	     timer.start();
		
		JLabel current=new JLabel(" Current Schedule Length: 0 ");
		JPanel scheduleP=new JPanel();
		scheduleP.setSize(200, 100);
		scheduleP.add(current);
		
		
		add(timeP);
		add(scheduleP);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		long currentTime = System.currentTimeMillis();
		long difference = currentTime - Main.initialTime;
		if(Main.endTime!=0){
		difference=	Main.endTime-Main.initialTime;
		}
		 timeValue.setText(difference+" MilliSec");
		
	}

}
