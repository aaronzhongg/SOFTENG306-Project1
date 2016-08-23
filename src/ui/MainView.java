package ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;



import org.graphstream.graph.*;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
public class MainView extends JFrame{
	private Graph g;
	//private InfoPanel infoPanel;

public MainView(Graph g){
	this.g=g;
	String location="file://"+System.getProperty("user.dir")+"/StyleSheet.css";
	g.addAttribute("ui.stylesheet", "url('"+location+"')");
	setLayout(new BorderLayout());
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	this.setBounds(200, 200, 700, 500);
	Viewer viewer=new Viewer(g,  Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
	viewer.enableAutoLayout();
	View view = viewer.addDefaultView(false); 

	add((Component)view,BorderLayout.CENTER);

	setVisible(true);
}







}
