// Originally copyrighted to Leon Schram 07-19-10
// Completely reworked from depreciated Applet to using JLabel

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class LetterPuzzle{
	private static JFrame window;
	private static Puzzle p;
	public static void main(String[] args){
		String s = null;
		boolean fail = true;
		while(fail){
			try{
				fail = false;
				s = (String)JOptionPane.showInputDialog(new JFrame("Difficulty?"), "What __ × __ matrix would you like to play?", "Difficulty?", JOptionPane.PLAIN_MESSAGE);
				if(s != null && Integer.valueOf(s) <= 1){
					JOptionPane.showConfirmDialog(new JFrame("Zero Input"), "Sorry, you cannot play a negative, 0 × 0, or 1 × 1 matrix.", "Incorrect Input", JOptionPane.DEFAULT_OPTION);
					fail = true;
				}else if(s != null && Integer.valueOf(s) > 5){
					JOptionPane.showConfirmDialog(new JFrame("Big Input"), "Sorry, the limitations of the program do not allow matrices with dimensions greater than 5.", "Big Input", JOptionPane.DEFAULT_OPTION);
					fail = true;
				}
			}catch(NumberFormatException e){
				JOptionPane.showConfirmDialog(new JFrame("Wrong Input"), "Sorry, that's the incorrect input. Please try again with an integer between 2 and 5.", "Incorrect Input", JOptionPane.DEFAULT_OPTION);
				fail = true;
			}
		}
		if(s != null){
			p = new Puzzle(Integer.valueOf(s));
			window = new JFrame("Letter Puzzle");
			window.setSize(630,800);
			Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
			int cX = size.width/2, cY = size.height/2;
			window.setLocation(cX-630, cY-400);
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.setResizable(false);
			window.setVisible(true);
			window.add(p);
		}else
			System.exit(0);
	}
}

class Puzzle extends JLabel implements MouseListener{
	private int num, d, moves;
	private Letter[][] letters;
	private Score score;
	private Solvability sol;
	private Solver bfs, astar, idastar;
	private boolean scramble[];
	private Random rnd;
	private int blankR;
	private int blankC;
	private boolean done;
	public Puzzle(int n){
		num = n;
		d = 600/n;
		letters = new Letter[num+2][num+2];
		scramble = new boolean[num*num+1];
		rnd = new Random();
		String s;
		for(int i = 1; i <= num; i++){
			for(int j = 1; j <= num; j++){
				letters[i][j] = new Letter(s = getLetter(), d, num, 10+d*(j-1), 10+d*(i-1));
				if(s.equals(String.valueOf((char)(num*num+64)))){
					blankR = i;
					blankC = j;
				}
				add(letters[i][j]);
			}
		}
		setVisible(true);
		score = new Score();
		add(score);
		addMouseListener(this);
		sol = new Solvability();
		sol.check(num, letters);
		add(sol);
		JButton resetButton = new JButton("Request a new matrix");
		resetButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				reset();
			}
		});
		JPanel resetButtonPanel = new JPanel();
		resetButtonPanel.add(resetButton);
		resetButtonPanel.setSize(resetButtonPanel.getPreferredSize());
		resetButtonPanel.setLocation(20, 720);
		resetButton.setVisible(true);
		resetButtonPanel.setVisible(true);
		add(resetButtonPanel);
		if(num <= 3){
			JButton bfssol = new JButton("Breadth-First Search");
			bfs = new Solver(num, d, letters, 0);
			bfssol.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(sol.isSolvable())
						bfs.show();
					else
						JOptionPane.showMessageDialog(new JFrame("Unsolvable"), "Sorry, but the puzzle cannot be solved.");
				}
			});
			JPanel BFSButtonPanel = new JPanel();
			BFSButtonPanel.add(bfssol);
			BFSButtonPanel.setSize(BFSButtonPanel.getPreferredSize());
			BFSButtonPanel.setLocation(250, 645);
			bfssol.setVisible(true);
			BFSButtonPanel.setVisible(true);
			add(BFSButtonPanel);
			JButton astarsol = new JButton("A* Search");
			astar = new Solver(num, d, letters, 1);
			astarsol.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(sol.isSolvable())
						astar.show();
					else
						JOptionPane.showMessageDialog(new JFrame("Unsolvable"), "Sorry, but the puzzle cannot be solved.");
				}
			});
			JPanel AStarButtonPanel = new JPanel();
			AStarButtonPanel.add(astarsol);
			AStarButtonPanel.setSize(AStarButtonPanel.getPreferredSize());
			AStarButtonPanel.setLocation(440, 645);
			astarsol.setVisible(true);
			AStarButtonPanel.setVisible(true);
			add(AStarButtonPanel);
		}
		if(num <= 4){
			JButton idastarsol = new JButton("Iterative Deepening A* Search");
			idastar = new Solver(num, d, letters, 2);
			idastarsol.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(sol.isSolvable())
						idastar.show();
					else
						JOptionPane.showMessageDialog(new JFrame("Unsolvable"), "Sorry, but the puzzle cannot be solved.");
				}
			});
			JPanel IDAStarButtonPanel = new JPanel();
			IDAStarButtonPanel.add(idastarsol);
			IDAStarButtonPanel.setSize(IDAStarButtonPanel.getPreferredSize());
			IDAStarButtonPanel.setLocation(250, 685);
			idastarsol.setVisible(true);
			IDAStarButtonPanel.setVisible(true);
			add(IDAStarButtonPanel);
		}
		done = false;
	}
	public void reset(){
		for(int i = 1; i <= num*num; i++)
			scramble[i] = false;
		for(int i = 1; i <= num; i++){
			for(int j = 1; j <= num; j++){
				letters[i][j].setNum(getLetter());
				if(letters[i][j].getNum().equals(String.valueOf((char)(num*num+64)))){
					blankR = i;
					blankC = j;
				}
			}
		}
		score.reset();
		sol.check(num, letters);
		if(bfs != null){
			if(sol.isSolvable())
				bfs.reset(letters);
			else
				bfs.close();
		}
		if(astar != null){
			if(sol.isSolvable())
				astar.reset(letters);
			else
				astar.close();
		}
		if(idastar != null){
			if(sol.isSolvable())
				idastar.reset(letters);
			else
				idastar.close();
		}
		done = false;
		repaint();
	}
	public String getLetter(){
		String letter = "";
		int rndNum = rnd.nextInt(num*num) + 1;
		while(scramble[rndNum] != false)
			rndNum = rnd.nextInt(num*num) + 1;
		letter = String.valueOf((char) (rndNum+64));
		scramble[rndNum] = true;
		return letter;
	}
	public void paintComponent(Graphics g)
	{
		g.drawRect(10,10,600,600);
		for(int i = 1; i < num; i++){
			g.drawLine(10+i*d, 10, 10+i*d, 610);
			g.drawLine(10, 10+i*d, 610, 10+i*d);
		}
		g.setFont(new Font("Arial", Font.BOLD, 16));
		if(num <= 4)
			g.drawString("AI Solvers", 380, 640);
		if(done){
			g.setFont(new Font("Arial",Font.BOLD,13));
			g.drawString("Done! Finished in "+ score.getScore() + " moves.", 20, 700);
		}
	}
	public void mouseClicked(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mousePressed(MouseEvent e){
		if(!done){
			int r = ((int)e.getPoint().getY()-10)/d;
			int c = ((int)e.getPoint().getX()-10)/d;
			if(e.getPoint().getY() >= 10.0 && e.getPoint().getX() >= 10.0 && e.getPoint().getY() <= 600 && e.getPoint().getX() <= 600 && okSquare(r+1, c+1)){
				score.incre();
				swap(r+1, c+1);
			}
			done = check();
			repaint();
		}
	}
	private boolean check(){
		boolean done = true;
		String s = "";
		for(int i = 1; i <= num; i++)
			for(int j = 1; j <= num; j++)
				s += letters[i][j].getNum();
		for(int i = 1; i < s.length() && done; i++)
			if(s.charAt(i) < s.charAt(i-1))
				done = false;
		return done;
	}
	private final int[] rdir = {-1, 0, 1, 0};
	private final int[] cdir = {0, 1, 0, -1};
	public boolean okSquare(int r, int c)
	{
		boolean temp = false;
		for(int i = 0; i < 4 && !temp; i++)
			if(r+rdir[i] >= 1 && r+rdir[i] <= num && c+cdir[i] >= 1 && c+cdir[i] <= num)
				if(letters[r+rdir[i]][c+cdir[i]].getNum().equals(String.valueOf((char)(num*num+64))))
					temp = true;
		return temp;
	}
	public void swap(int r, int c)
	{
		letters[blankR][blankC].setNum(letters[r][c].getNum());
		letters[r][c].setNum(String.valueOf((char)(num*num+64)));
		blankR = r;
		blankC = c;
	}
}

class Letter extends JLabel{
	private String number;
	private int num, d, x, y;
	public Letter(String s, int dim, int n, int xi, int yi){
		number = s;
		d = dim;
		num = n;
		x = xi;
		y = yi;
		setSize(630, 800);
		setVisible(true);
	}
	public String getNum(){
		return number;
	}
	public void setNum(String s){
		number = s;
	}
	public void paintComponent(Graphics g){
		int offSetX = x + d*3/20;
		int offSetY = y + d*65/80;
		g.setFont(new Font("Arial",Font.BOLD,d-20));
		if (number.equals(String.valueOf((char)(num*num+64))))
		{
			g.setColor(Color.white);
			g.fillRect(x+1,y+1,d-2,d-2);
		}
		else
		{
			g.setColor(Color.black);
			g.drawString(number,offSetX,offSetY);			
		}
	}
}

class Score extends JLabel{
	private int score;
	public Score(){
		this.setLocation(10, 610);
		score = 0;
		setSize(630, 800);
		setVisible(true);
	}
	public int getScore(){
		return score;
	}
	public void incre(){
		score++;
	}
	public void reset(){
		score = 0;
	}
	public void paintComponent(Graphics g){
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial",Font.BOLD,16));
		g.drawString("Moves: "+ score, 10, 30);
	}
}

class Solvability extends JLabel{
	private boolean b;
	public Solvability(){
		this.setLocation(10, 640);
		b = true;
		setSize(630, 800);
		setVisible(true);
	}
	public void check(int n, Letter[][] mat){
		int[] arr = new int[n*n];
		int blankR = 0;
		for(int i = 1; i <= n; i++){
			for(int j = 1; j <= n; j++){
				arr[(i-1)*n+j-1] = mat[i][j].getNum().charAt(0)-64;
				if(arr[(i-1)*n+j-1] == n*n)
					blankR = n-i+1;
			}
		}
		int inversions = 0;
		for(int i = 0; i < n*n; i++)
			for(int j = i+1; j < n*n && arr[i] != n*n; j++)
				if(arr[j] != n*n && arr[i] > arr[j])
					inversions++;
		if(n%2 == 1)
			b = (inversions%2 == 0);
		else
			b = (blankR%2 == 0) ? (inversions%2 == 1) : (inversions%2 == 0);
	}
	public boolean isSolvable(){
		return b;
	}
	public void paintComponent(Graphics g){
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial",Font.BOLD,16));
		g.drawString("Solvability: " + (b ? "Solvable" : "Not solvable"), 10, 30);
	}
}

class Solver extends JLabel{
	private Letter[][] letters;
	private JFrame window;
	private int num, cX, cY, d, minSteps, curStep, fblankR, fblankC, blankR, blankC, htype, newtype, type;
	private ArrayList<Integer> steps;
	private String oriStr;
	public Solver(int n, int dim, Letter[][] mat, int t){
		num = n;
		d = dim;
		minSteps = -1;
		curStep = 1;
		type = t;
		letters = new Letter[num+2][num+2];
		String name = "";
		switch(type){
			case 0: name = "Breadth-First Search"; break;
			case 1: name = "A* Search"; break;
			case 2: name = "Iterative Deepening A* Search"; break;
		}
		window = new JFrame(name);
		window.setSize(630,800);
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		cX = size.width/2;
		cY = size.height/2;
		window.setLocation(cX, cY-400);
		window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		window.setResizable(false);
		oriStr = "";
		for(int i = 1; i <= num; i++){
			for(int j = 1; j <= num; j++){
				letters[i][j] = new Letter(mat[i][j].getNum(), d, num, 10+d*(j-1), 10+d*(i-1));
				if(letters[i][j].getNum().charAt(0) == num*num+64){
					blankR = fblankR = i;
					blankC = fblankC = j;
				}
				window.add(letters[i][j]);
				oriStr += letters[i][j].getNum();
			}
		}
		JButton solvebutton = new JButton("Solve it!");
		solvebutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				solveIt();
			}
		});
		JPanel SolveButtonPanel = new JPanel();
		SolveButtonPanel.add(solvebutton);
		SolveButtonPanel.setSize(SolveButtonPanel.getPreferredSize());
		SolveButtonPanel.setLocation(500, 620);
		solvebutton.setVisible(true);
		SolveButtonPanel.setVisible(true);
		add(SolveButtonPanel);
		JButton nextbutton = new JButton(">>");
		nextbutton.setFont(new Font("Arial",Font.BOLD,50));
		nextbutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(curStep <= minSteps){
					int r = steps.get(curStep-1)/num+1;
					int c = steps.get(curStep-1)%num+1;
					letters[blankR][blankC].setNum(letters[r][c].getNum());
					letters[r][c].setNum(String.valueOf((char)(num*num+64)));
					blankR = r;
					blankC = c;
					curStep++;
					repaint();
				}
			}
		});
		JPanel NextButtonPanel = new JPanel();
		NextButtonPanel.add(nextbutton);
		NextButtonPanel.setSize(NextButtonPanel.getPreferredSize());
		NextButtonPanel.setLocation(500, 670);
		nextbutton.setVisible(true);
		NextButtonPanel.setVisible(true);
		add(NextButtonPanel);
		JButton prevbutton = new JButton("<<");
		prevbutton.setFont(new Font("Arial",Font.BOLD,50));
		prevbutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(curStep > 2){
					int r = steps.get(curStep-3)/num+1;
					int c = steps.get(curStep-3)%num+1;
					letters[blankR][blankC].setNum(letters[r][c].getNum());
					letters[r][c].setNum(String.valueOf((char)(num*num+64)));
					blankR = r;
					blankC = c;
					curStep--;
					repaint();
				}else if(curStep == 2){
					letters[blankR][blankC].setNum(letters[fblankR][fblankC].getNum());
					letters[fblankR][fblankC].setNum(String.valueOf((char)(num*num+64)));
					blankR = fblankR;
					blankC = fblankC;
					curStep--;
					repaint();
				}
			}
		});
		JPanel PrevButtonPanel = new JPanel();
		PrevButtonPanel.add(prevbutton);
		PrevButtonPanel.setSize(PrevButtonPanel.getPreferredSize());
		PrevButtonPanel.setLocation(400, 670);
		prevbutton.setVisible(true);
		PrevButtonPanel.setVisible(true);
		add(PrevButtonPanel);
		if(type == 1 || type == 2){
			htype = 0;
			JButton hbutton = new JButton("Pick a heuristic function");
			hbutton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					String hfunc = (String)JOptionPane.showInputDialog(null, "Pick a heuristic function:", "Heuristic function", JOptionPane.QUESTION_MESSAGE, null, new Object[] {
							"Manhattan Distance",
							"Hamming Distance",
							"Linear Conflict"
						}, "Manhattan Distance");
					if(hfunc != null){
						if(hfunc.equals("Manhattan Distance"))
							newtype = 0;
						else if(hfunc.equals("Hamming Distance"))
							newtype = 1;
						else if(hfunc.equals("Linear Conflict"))
							newtype = 2;
						if(newtype != htype){
							curStep = 1;
							minSteps = -1;
							htype = newtype;
							for(int i = 0; i < num*num; i++)
								letters[i/num+1][i%num+1].setNum(""+oriStr.charAt(i));
							blankR = fblankR;
							blankC = fblankC;
						}
						repaint();
					}
				}
			});
			JPanel hbuttonPanel = new JPanel();
			hbuttonPanel.add(hbutton);
			hbuttonPanel.setSize(hbuttonPanel.getPreferredSize());
			hbuttonPanel.setLocation(20, 710);
			hbutton.setVisible(true);
			hbuttonPanel.setVisible(true);
			add(hbuttonPanel);
		}
		window.add(this);
	}
	public void reset(Letter[][] mat){
		minSteps = -1;
		curStep = 1;
		oriStr = "";
		for(int i = 1; i <= num; i++){
			for(int j = 1; j <= num; j++){
				letters[i][j].setNum(mat[i][j].getNum());
				if(letters[i][j].getNum().charAt(0) == num*num+64){
					blankR = fblankR = i;
					blankC = fblankC = j;
				}
				oriStr += letters[i][j].getNum();
			}
		}
		repaint();
	}
	public void show(){
		window.setLocation(cX, cY-400);
		window.setVisible(true);
	}
	public void close(){
		window.setVisible(false);
	}
	public void solveIt(){
		if(minSteps == -1){
			String s = "";
			for(int i = 1; i <= num; i++)
				for(int j = 1; j <= num; j++)
					s += letters[i][j].getNum();
			switch(type){
				case 0:
					BFS bfssolver = new BFS();
					steps = bfssolver.solve(s, num); break;
				case 1:
					AStar astarsolver = new AStar();
					steps = astarsolver.solve(s, num, htype); break;
				case 2:
					IDAStar idastarsolver = new IDAStar();
					steps = idastarsolver.solve(s, num, htype); break;
			}
			minSteps = steps.size();
			repaint();
		}
	}
	public void paintComponent(Graphics g){
		g.drawRect(10,10,600,600);
		for(int i = 1; i < num; i++){
			g.drawLine(10+i*d, 10, 10+i*d, 610);
			g.drawLine(10, 10+i*d, 610, 10+i*d);
		}
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial",Font.BOLD,16));
		g.drawString("Number of moves required: " + (minSteps == -1 ? "Has not been solved yet!" : minSteps), 20, 640);
		if(minSteps != -1 && curStep <= minSteps){
			g.setFont(new Font("Arial",Font.BOLD,50));
			g.drawString("Move #" + curStep, 20, 700);
			int r = steps.get(curStep-1)/num;
			int c = steps.get(curStep-1)%num;
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.RED);
			g2.setStroke(new BasicStroke(5));
			g2.drawRect(1+10+c*d,1+10+r*d,d-2,d-2);
		}
		if(curStep == minSteps+1){
			g.setFont(new Font("Arial",Font.BOLD,50));
			g.drawString("Done!", 20, 700);
		}
	}
}

class BFS{
	protected HashMap<String, String> prev;
	protected HashMap<String, Integer> prevmove;
	public BFS(){
		prev = new HashMap<String, String>();
		prevmove = new HashMap<String, Integer>();
	}
	static class pair{
		private String first;
		private int second;
		public pair(String a, int b){
			first = a;
			second = b;
		}
	}
	protected boolean check(String s){
		boolean done = true;
		for(int i = 1; i < s.length() && done; i++)
			if(s.charAt(i) < s.charAt(i-1))
				done = false;
		return done;
	}
	protected String swap(String str, int i, int j){
		if(i > j){
			int t = i;
			i = j;
			j = t;
		}
		String s = "";
		s += str.substring(0, i);
		s += str.charAt(j);
		s += str.substring(i+1, j);
		s += str.charAt(i);
		s += str.substring(j+1);
		return s;
	}
	public ArrayList<Integer> solve(String s, int n){
		ArrayList<Integer> answer = new ArrayList<Integer>();
		Stack<Integer> ansback = new Stack<Integer>();
		prevmove.put(s, -1);
		Queue<pair> q = new LinkedList<pair>();
		int pos = 0;
		for(int i = 0; i < n*n; i++)
			if(s.charAt(i) == n*n+64)
				pos = i;
		q.offer(new pair(s, pos));
		final int[] rdir = {-1, 0, 1, 0};
		final int[] cdir = {0, 1, 0, -1};
		int x, y, nextx, nexty;
		String done = "";
		while(!q.isEmpty()){
			pair cur = q.poll();
			if(check(cur.first)){
				done = cur.first;
				break;
			}
			y = cur.second/n;
			x = cur.second%n;
			for(int i = 0; i < 4; i++){
				nexty = y + rdir[i];
				nextx = x + cdir[i];
				if(nexty >= 0 && nexty < n && nextx >= 0 && nextx < n){
					String next = swap(cur.first, cur.second, nexty*n+nextx);
					if(prevmove.get(next) == null){
						prevmove.put(next, nexty*n+nextx);
						prev.put(next, cur.first);
						q.offer(new pair(next, nexty*n+nextx));
					}
				}
			}
		}
		while(prevmove.get(done) != -1){
			ansback.push(prevmove.get(done));
			done = prev.get(done);
		}
		while(!ansback.isEmpty())
			answer.add(ansback.pop());
		return answer;
	}
}

class AStar extends BFS{
	class tuple implements Comparable<tuple>{
		private String s;
		private int blank, g, h;
		public tuple(String str, int b, int gi, int hi){
			s = str;
			blank = b;
			g = gi;
			h = hi;
		}
		public int f(){
			return g+h;
		}
		public int compareTo(tuple t){
			return f()-t.f();
		}
	}
	protected int abs(int i){
		return (i < 0) ? -i : i;
	}
	protected int heuristic(String s, int n, int type){
		int h = 0;
		switch(type){
			case 0:
				for(int i = 0; i < n*n; i++){
					int c = s.charAt(i)-'A';
					h += abs(c/n - i/n) + abs(c%n - i%n);
				}break;
			case 1:
				for(int i = 0; i < n*n; i++){
					if(s.charAt(i)-'A' != i && s.charAt(i) != n*n+'A')
						h++;
				}break;
			case 2:
				int count = 0;
				for(int i = 0; i < n*n; i++)
					for(int j = i+1; j < i*n; j++)
						if((s.charAt(i)-'A')/n == i && (s.charAt(j)-'A')/n == i && s.charAt(j) < s.charAt(i))
							count++;
				for(int i = 0; i < n*n; i++)
					for(int j = (i/n+1)*n+i%n; j < n*n; j+=n)
						if((s.charAt(i)-'A')%n == i%n && (s.charAt(j)-'A')%n == i%n && s.charAt(j) < s.charAt(i))
							count++;
				h += count*2;
				for(int i = 0; i < n*n; i++){
					int c = s.charAt(i)-'A';
					h += abs(c/n - i/n) + abs(c%n - i%n);
				}break;
		}
		return h;
	}
	public ArrayList<Integer> solve(String s, int n, int type){
		ArrayList<Integer> answer = new ArrayList<Integer>();
		Stack<Integer> ansback = new Stack<Integer>();
		prev = new HashMap<String, String>();
		prevmove = new HashMap<String, Integer>();
		prevmove.put(s, -1);
		PriorityQueue<tuple> q = new PriorityQueue<tuple>();
		int pos = 0;
		for(int i = 0; i < n*n; i++)
			if(s.charAt(i) == n*n+64)
				pos = i;
		q.offer(new tuple(s, pos, 0, heuristic(s, n, type)));
		final int[] rdir = {-1, 0, 1, 0};
		final int[] cdir = {0, 1, 0, -1};
		int x, y, nextx, nexty;
		String done = "";
		while(!q.isEmpty()){
			tuple cur = q.poll();
			if(check(cur.s)){
				done = cur.s;
				break;
			}
			y = cur.blank/n;
			x = cur.blank%n;
			for(int i = 0; i < 4; i++){
				nexty = y + rdir[i];
				nextx = x + cdir[i];
				if(nexty >= 0 && nexty < n && nextx >= 0 && nextx < n){
					String next = swap(cur.s, cur.blank, nexty*n+nextx);
					if(prevmove.get(next) == null){
						prevmove.put(next, nexty*n+nextx);
						prev.put(next, cur.s);
						q.offer(new tuple(next, nexty*n+nextx, cur.g+1, heuristic(next, n, type)));
					}
				}
			}
		}
		while(prevmove.get(done) != -1){
			ansback.push(prevmove.get(done));
			done = prev.get(done);
		}
		while(!ansback.isEmpty())
			answer.add(ansback.pop());
		return answer;
	}
}

class IDAStar extends AStar{
	private boolean found;
	private Stack<Integer> ansback;
	final int[] rdir = {-1, 0, 1, 0};
	final int[] cdir = {0, 1, 0, -1};
	public IDAStar(){
		super();
		found = false;
		ansback = new Stack<Integer>();
	}
	public ArrayList<Integer> solve(String s, int n, int type){
		int threshold = heuristic(s, n, type);
		int pos = 0;
		for(int i = 0; i < n*n; i++)
			if(s.charAt(i) == n*n+64)
				pos = i;
		while(!found)
			threshold = dfs(s, pos, n, type, 0, threshold);
		ArrayList<Integer> answer = new ArrayList<Integer>();
		while(!ansback.isEmpty())
			answer.add(ansback.pop());
		return answer;
	}
	private int dfs(String s, int blank, int n, int type, int g, int threshold){
		int f = g+heuristic(s, n, type);
		if(f > threshold)
			return f;
		if(check(s)){
			found = true;
			return 0;
		}
		int min = threshold*4, nexty, nextx;
		for(int i = 0; i < 4 && !found; i++){
			nexty = blank/n + rdir[i];
			nextx = blank%n + cdir[i];
			if(nexty >= 0 && nexty < n && nextx >= 0 && nextx < n){
				String next = swap(s, blank, nexty*n+nextx);
				int newthreshold = dfs(next, nexty*n+nextx, n, type, g+1, threshold);
				if(found)
					ansback.push(nexty*n+nextx);
				if(min > newthreshold)
					min = newthreshold;
			}
		}
		return min;
	}
}