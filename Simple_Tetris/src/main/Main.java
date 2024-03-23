package main;

import javax.swing.JFrame;

public class Main {
	public static void main (String[] args) {
		
		JFrame window = new JFrame("Simple Tetris");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		
		GamePanel gp = new GamePanel();
		window.add(gp);
		window.pack();
		
		
		window.setLocation(150, 50);
		window.setVisible(true);
		
		gp.launchGame();
		
	}

}
