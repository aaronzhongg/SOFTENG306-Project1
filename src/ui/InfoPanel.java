package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.Date;

import javafx.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import main.Main;

public class InfoPanel extends JPanel implements ActionListener{
	
	private JLabel time;
	
public 	InfoPanel(){
	this.setLayout(new BorderLayout(0,0));
	 time= new JLabel(" Total Time : 0 milisec ");
	 
     Timer timer = new Timer(10, this);
     timer.setInitialDelay(0);
     timer.start();
	
	
   // JLabel pathLength = new JLabel(" Total Path Length: 0  ");
    
    
	this.add(time,BorderLayout.NORTH);
	
	//this.add(pathLength,BorderLayout.CENTER);
	
	
	
	
}

public void actionPerformed(java.awt.event.ActionEvent e) {
	long currentTime = System.currentTimeMillis();
	long difference = currentTime - Main.initialTime;
	if(Main.endTime!=0){
	difference=	Main.endTime-Main.initialTime;
	}
	 time.setText( " Total Time : "+difference+"  milisec ");
	
}




	
	
}
