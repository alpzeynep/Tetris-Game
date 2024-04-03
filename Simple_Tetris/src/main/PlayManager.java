package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Random;

import mino.Block;
import mino.Mino;
import mino.Mino_Bar;
import mino.Mino_L1;
import mino.Mino_L2;
import mino.Mino_Square;
import mino.Mino_T;
import mino.Mino_Z1;
import mino.Mino_Z2;

public class PlayManager {
	
	//Main Play Area
	final int WIDTH = 360;
	final int HEIGHT = 600;
	public static int left_x;
	public static int right_x;
	public static int top_y;
	public static int bottom_y;
	
	//Mino
	Mino currentMino;
	final int MINO_START_X;
	final int MINO_START_Y;
	Mino nextMino;
	final int NEXTMINO_X ;
	final int NEXTMINO_Y;
	public static ArrayList<Block> staticBlocks = new ArrayList<> ();
	
	//Others
	public static int dropInvertal = 60; // mino drop in every 60 frames
	
	//Effects
	boolean effectCounterOn;
	int effectCounter;
	ArrayList<Integer> effectY = new ArrayList<>();
	
	public PlayManager() {
		left_x = (GamePanel.WIDTH/2) - (WIDTH/2); //1280/2 -360/2 = 460
		right_x = left_x + WIDTH;
		top_y = 50; 
		bottom_y = top_y + HEIGHT;
		
		//Mıno's starting place 
		MINO_START_X = left_x + (WIDTH/2) - Block.SIZE;
		MINO_START_Y = top_y + Block.SIZE;
		
		NEXTMINO_X = right_x + 175;
		NEXTMINO_Y = top_y + 500;
		//Set the starting Mino 
		currentMino = pickMino();
		currentMino.setXY(MINO_START_X, MINO_START_Y);
		nextMino = pickMino();
		nextMino.setXY(MINO_START_X, MINO_START_Y);
	}
	private Mino pickMino() {
		// Pick a random mino
		Mino mino = null; 
		int i = new Random().nextInt(7);
		
		switch(i)  {
		case 0: mino = new Mino_L1();break;
		case 1: mino = new Mino_L2();break;
		case 2: mino = new Mino_Square();break;
		case 3: mino = new Mino_Bar();break;
		case 4: mino = new Mino_T();break;
		case 5: mino = new Mino_Z1();break;
		case 6: mino = new Mino_Z2();break;	
		}
		return mino;
	}
	
	public void update() {
		
		//Check if the currentMino is active
		if(currentMino.activate == false) {
			
			//if the mino is not active, put it into the staticBlocks
			staticBlocks.add(currentMino.b[0]);
			staticBlocks.add(currentMino.b[1]);
			staticBlocks.add(currentMino.b[2]);
			staticBlocks.add(currentMino.b[3]);
			
			currentMino.deactivating = false;
			
			// replace the currentMino with the nextMino
			currentMino = nextMino;
			currentMino.setXY(MINO_START_X, MINO_START_Y);
			nextMino = pickMino();
			nextMino.setXY(NEXTMINO_X, NEXTMINO_Y); 
			
			// when a mino becomes inactive, check if line(s) can be deleted
			checkDelete();
		}
		else {
			currentMino.update();
		}   
	}
	private void checkDelete() {
		
		int x = left_x;
		int y = top_y;
		int blockCount = 0;
		
		while(x < right_x && y < bottom_y) {
			
			for(int i = 0; i < staticBlocks.size(); i++) {
				if(staticBlocks.get(i).x == x && staticBlocks.get(i).y == y) {
					// increase the count if there is a static blocks
					blockCount++;
				}
			}
			
			x += Block.SIZE;
			
			if(x == right_x) {
				
				//if the blockCount hits 12, that means the current y line is all filled with blocks
				// so we can delete them
				if(blockCount == 12) {
					
					effectCounterOn = true;
					effectY.add(y);
					
					for(int i = staticBlocks.size()-1; i > -1; i--) {
					// remove all the blocks in the current y line
					if(staticBlocks.get(i).y == y) {
						staticBlocks.remove(i);
				    	}
			    	}
					
					// a line has been deleted so need to slide down blocks that are above it
					for(int i = 0; i < staticBlocks.size(); i++) {
						// if a block is above the current y, move it down by the block size
						if(staticBlocks.get(i).y < y) {
							staticBlocks.get(i).y += Block.SIZE;
						}
					}
				}
				
				blockCount = 0;
				x = left_x;
				y += Block.SIZE;
			}
		}
	}
	public void draw (Graphics2D g2) {
		
		//Draw Play Area Frame
		g2.setColor(Color.white);
		g2.setStroke(new BasicStroke(4f));
		g2.drawRect(left_x - 4, bottom_y -605, WIDTH+8, HEIGHT+8);
		
		//Draw Next Mino Frame 
		int x = right_x + 100;
		int y = bottom_y -200;
		g2.drawRect(x,y, 200, 200);
		g2.setFont(new Font("Arial", Font.PLAIN, 30));
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.drawString("NEXT", x+60, y+60);
		
		//Draw current Mino
		if(currentMino != null) {
			currentMino.draw(g2);
		}
		//Draw the next mino 
		nextMino.draw(g2);	
			
		//Draw static Blocks
		for(int i = 0; i < staticBlocks.size(); i++) {
			staticBlocks.get(i).draw(g2);
		}
		
		// Draw Effect
		if(effectCounterOn) {
			effectCounter++;
			
			g2.setColor(Color.red);
			for(int i =0; i < effectY.size(); i++) {
				g2.fillRect(left_x, effectY.get(i), WIDTH, Block.SIZE);
			}
			
			if(effectCounter== 10) {
				effectCounterOn = false;
				effectCounter = 0;
				effectY.clear();	
			}
		}
		
		// Draw Pause
		g2.setColor(Color.yellow);
		g2.setFont(g2.getFont().deriveFont(50f));
		if(KeyHandler.pausePressed) {
			x = left_x + 70;
			y = top_y + 320;
			g2.drawString("PAUSED", x, y);
		
		}
	}

}
