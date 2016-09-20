// Unit04vST.java by Leon Schram 07-19-10
//
// This is the student starting file of the Unit04 lab assignment.
// The starting file is actually the Puzzle09.java stage, which is the
// finished puzzle game for a 4 X 4 matrix.


import java.awt.*;
import java.applet.*;
import java.util.Random;
import javax.swing.*;



public class Unit04vSTall extends Applet
{
	int num;
	int d;
	int moves;
	Rectangle[][] r;
	boolean scramble[];
	String matrix[][];
	Random rnd;
	int blankR;
	int blankC;
	
	
	public void init()
	{	
		while(num == 0){
			try{
				num = Integer.valueOf((String)JOptionPane.showInputDialog(new JFrame("Difficulty?"), "What __ × __ matrix would you like to play?", "Difficulty?", JOptionPane.PLAIN_MESSAGE));
				if(num <= 1){
					num = 0;
					JOptionPane.showConfirmDialog(new JFrame("Zero Input"), "Sorry, you cannot play a negative, 0 × 0, or 1 × 1 matrix.", "Incorrect Input", JOptionPane.DEFAULT_OPTION);
				}
			}catch(NumberFormatException e){
				JOptionPane.showConfirmDialog(new JFrame("Wrong Input"), "Sorry, that's the incorrect input. Please try again.", "Incorrect Input", JOptionPane.DEFAULT_OPTION);
			}
		}
		d = 600/num;
		r = new Rectangle[num+2][num+2];
		for(int i = 1; i <= num; i++){
			for(int j = 1; j <= num; j++){
				r[i][j] = new Rectangle(100+d*(j-1), 100+d*(i-1), d, d);
			}
		}
		
		matrix = new String[num+2][num+2];
		scramble = new boolean[num*num+1];
		for (int k = 1; k <=num*num; k++)
			scramble[k] = false;
		rnd = new Random();
		
		for (int r = 0; r <= num+1; r++)
			for (int c = 0; c <= num+1; c++)
				matrix[r][c] = "#";	
		
		for (int r = 1; r <= num; r++)
			for (int c = 1; c <= num; c++)
			{
				matrix[r][c] = getLetter();
				if (matrix[r][c].equals(String.valueOf((char)(num*num+64))))
				{
					blankR = r;
					blankC = c;
				}
			}
	}
	
	
	public String getLetter()
	{
		String letter = "";
		boolean Done = false;
		while(!Done)
		{
			int rndNum = rnd.nextInt(num*num) + 1;
			if (scramble[rndNum] == false)
			{
				letter = String.valueOf((char) (rndNum+64));
				scramble[rndNum] = true;
				Done = true;
			}
		}
		return letter;
	}
	
		
	public void paint(Graphics g)
	{
		drawGrid(g);
		for(int i = 0; i < matrix.length-2; i++){
			for(int j = 0; j < matrix[i].length-2; j++){
				drawLetter(g, matrix[i+1][j+1], 100+j*d, 100+i*d);
			}
		}
	}
	
	
	public void drawGrid(Graphics g)
	{
		g.drawRect(100,100,600,600);
		for(int i = 1; i < num; i++){
			g.drawLine(100+i*d, 100, 100+i*d, 700);
			g.drawLine(100, 100+i*d, 700, 100+i*d);
		}
	}
	
	
	public void drawLetter(Graphics g, String letter, int x, int y)
	{
		int offSetX = x + d*3/20;
		int offSetY = y + d*65/80;
		g.setFont(new Font("Arial",Font.BOLD,d-20));
		if (letter.equals(String.valueOf((char)(num*num+64))))
		{
			g.setColor(Color.white);
			g.fillRect(x+1,y+1,d-2,d-2);
		}
		else
		{
			g.setColor(Color.black);
			g.drawString(letter,offSetX,offSetY);			
		}
		g.setColor(Color.WHITE);
		g.fillRect(100,45,600,30);
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial",Font.BOLD,30));
		g.drawString("Moves: "+ moves, 100, 75);
	}
	
	
	public boolean mouseDown(Event e, int x, int y)
	{
		for(int i = 1; i <= num; i++){
			for(int j = 1; j <= num; j++){
				if(r[j][i].inside(x,y) && okSquare(j,i)){
					swap(j,i);
					moves++;
				}
			}
		}
		return true;
	}
	
	
	public boolean okSquare(int r, int c)
	{
		boolean temp = false;
		if (matrix[r-1][c].equals(String.valueOf((char)(num*num+64))))
			temp = true;
		else if (matrix[r+1][c].equals(String.valueOf((char)(num*num+64))))
			temp = true;
		else if (matrix[r][c-1].equals(String.valueOf((char)(num*num+64))))
			temp = true;
		else if (matrix[r][c+1].equals(String.valueOf((char)(num*num+64))))
			temp = true;
		return temp;
	}
	
	
	public void swap(int r, int c)
	{
		matrix[blankR][blankC] = matrix[r][c];
		matrix[r][c] = String.valueOf((char)(num*num+64));
		blankR = r;
		blankC = c;
		repaint();
	}
	
			
	public void update(Graphics g)
	{
		paint(g);
	}
}




	
	
