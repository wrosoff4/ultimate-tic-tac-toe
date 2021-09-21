/*
 * @author: Will Rosoff
 * @Title: Ultimate Tic Tac toe
 * @Purpose: Fun game
 * @Date: 11-20-2020
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
//import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.Border;


public class UltimateTCT{
	
	private JFrame jfrm = new JFrame("Ultimate Tic Tac Toe");
	
	private JPanel jpnl_topL = new JPanel();
	private JPanel jpnl_topR = new JPanel();
	private JPanel jpnl_top = new JPanel();
	private JPanel boardPanel = new JPanel(new GridLayout(3,3));
	private JPanel jpnl_bottom = new JPanel();
	
	
	final Border border = BorderFactory.createLineBorder(Color.red);
	
	private JButton playerO = new JButton("Player O");
	private JButton playerX = new JButton("Player X");
	private JButton restart = new JButton("Restart");
	private JButton undo = new JButton("Undo");
	private JButton stats = new JButton("Playing Stats");
	private JButton lastClicked = null;
	
	private JTextField wins = new JTextField(15);
	private JTextField totalGames = new JTextField(5);
	private JTextField avgMoves = new JTextField(5);

	private Player X = new Player("X", Color.WHITE);
	private Player O = new Player("O", Color.BLACK);
	private Player currentPlayer;
	private Player winningPlayer = null;

	private int lastPosition = -1;
	private int undoPost = -1;
	private int closedBoards = 0;
	
	private boolean gameOver = false;
	
	private ArrayList<Board> boards = new ArrayList<>();
//	private Stack<Integer> undoStack = new Stack<Integer>();
	
	final int[][] winningCombos = new int[][] { {0,1,2}, {3,4,5}, {6,7,8},
												{0,3,6}, {1,4,7}, {2,5,8},
												{0,4,8}, {2,4,6} };
	
	//construct main game interface
	public UltimateTCT(){
		Random rand = new Random();
		int coin = rand.nextInt(2);
		
		this.jfrm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		this.jfrm.setSize(850, 500);
		this.jfrm.setTitle("Ultimate Tic Tac Toe");
//		contentPane.setLayout(new BorderLayout());
		this.wins.setEditable(false);
		this.totalGames.setEditable(false);
		this.avgMoves.setEditable(false);
		
		// jpnl_top construction
		this.jpnl_topL.add(new JLabel("Now Playing:"));
		this.jpnl_topL.add(this.playerO);
		this.jpnl_topL.add(this.playerX);
		this.playerX.setEnabled(false);
		this.playerO.setEnabled(false);
		this.undo.setEnabled(false);
		
		// coin toss
		if(coin == 0) {
			currentPlayer = O;
			this.playerO.setBackground(O.color);
			this.playerX.setBackground(Color.LIGHT_GRAY);
		}
		else{
			currentPlayer = X;
			this.playerX.setBackground(X.color);
			this.playerO.setBackground(Color.LIGHT_GRAY);
		}
		
		 // set top panel
		this.jpnl_topR.add(new JLabel("Manage Game:"));
		
		this.restart.addActionListener(new RestartButtonListener());
		this.jpnl_topR.add(this.restart);
		
		this.undo.addActionListener(new UndoButtonListener());
		this.jpnl_topR.add(this.undo);
		
		this.jpnl_top.setBorder(border);
		this.jpnl_top.add(this.jpnl_topL);
		this.jpnl_top.add(this.jpnl_topR);
		
		//set bottom panel
		this.stats.addActionListener(new StatButtonListener());
		this.jpnl_bottom.add(this.stats);
		this.jpnl_bottom.add(new JLabel("Win %:"));
		this.jpnl_bottom.add(this.wins);
		this.jpnl_bottom.add(new JLabel("Total # of games:"));
		this.jpnl_bottom.add(this.totalGames);
		this.jpnl_bottom.add(new JLabel("Average # of moves per win:"));
		this.jpnl_bottom.add(this.avgMoves);
		
		this.jfrm.add(this.jpnl_top,BorderLayout.NORTH);
		this.jfrm.add(this.jpnl_bottom, BorderLayout.SOUTH);
		
		//construct game board
		populateBoards();
		this.jfrm.add(boardPanel);
		this.jfrm.setVisible(true);
	}
	
	// create grid of game boards for ultimate board
	public void populateBoards() {
		for(int i=0; i<9; i++) {
			Board board = new Board(i);
			boards.add(board);
			this.boardPanel.add(board);
		}
	}
	
	// marks and disables a button when appropriate
	public void markButton(JButton b, Board board, int pos) {
			if(b.isEnabled()) {
				 undo.setEnabled(true);
				 currentPlayer.moves++;
				 board.moves++;
				 if(this.currentPlayer == O) {
					 b.putClientProperty("OWNED", 0);
					 b.setText(O.label);
					 b.setBackground(O.color);
					 this.currentPlayer = X;
					 this.playerO.setBackground(Color.LIGHT_GRAY);
					 this.playerX.setBackground(X.color);
					 b.setForeground(X.color);
				 }
				 else {
					 b.putClientProperty("OWNED", 1);
					 b.setText(X.label);
					 b.setForeground(O.color);
					 b.setBackground(X.color);
					 this.currentPlayer = O;
					 this.playerO.setBackground(O.color);
					 this.playerX.setBackground(Color.LIGHT_GRAY);
				 }
				 undoPost = lastPosition;
				 lastPosition = (int) b.getClientProperty("POS");
//				 undoStack.push(pos);

				 lastClicked = b;
				 b.setEnabled(false);
				 if(board.moves>2) {
					 board.checkPanel();
				 }
			}
		}//end mark button
	
	// check entire board for a victory
	public void checkBoard(Player p) {
		if(p != null) {
			int count;
			for(int i=0; i<winningCombos.length; i++) {
				count = 0;
				for(int j=0; j<3; j++) {
					if(p.boardsWon.contains(winningCombos[i][j])) {
						count++;
					}
				}
				if(count>2) {
					gameOver = true;
					undo.setEnabled(false);
					winningPlayer = p;
					JOptionPane.showMessageDialog(null, "Game Won by Player "+p.label+"!\n"+
													"Click \"Playing Stats\" to see statistics for winning player."+
													"\nClick \"Restart\" for new game.");
					System.out.println("Game Won by "+p.label);
					break;
				}
			}
		}
		if(!gameOver && closedBoards==9) {
			gameOver = true;
			undo.setEnabled(false);
			JOptionPane.showMessageDialog(null, "Game is Draw.\n"
										+"Click \"Playing Stats\" to see statistics for winning player."
										+"\nClick \"Restart\" for new game.");
		}
	}
	// creates new gameboard
	public void newBoard() {
		this.winningPlayer = null;
		this.gameOver = false;
		this.X.boardsWon.clear();
		this.O.boardsWon.clear();
		this.closedBoards = 0;
		this.undo.setEnabled(false);
		this.lastClicked = null;
		this.lastPosition = -1;

		this.X.moves = 0;
		this.O.moves = 0;
		if(this.currentPlayer == O) {
			this.currentPlayer = X;
			this.playerX.setBackground(X.color);
			this.playerO.setBackground(Color.LIGHT_GRAY);
		}
		else {
			this.currentPlayer = O;
			this.playerO.setBackground(O.color);
			this.playerX.setBackground(Color.LIGHT_GRAY);
		}
		for(Board b : this.boards) {
			b.closed = false;
			b.moves = 0;
			for(JButton btn : b.button) {
				btn.setEnabled(true);
				btn.setText(null);
				btn.setBackground(null);
				btn.setForeground(null);
				btn.putClientProperty("OWNED", -1);
			}
		}
	}

// tic tac toe board class
@SuppressWarnings("serial")
class Board extends JPanel{
	private ArrayList<JButton> button = new ArrayList<JButton>();
	private int boardNum;
	private int moves = 0;
	private boolean closed = false;
	
	// single game goard construction
	public Board(int num){
		this.setLayout(new GridLayout(3,3));
		int pos = 0;
		for(int i=0; i<9; i++) {
				JButton b = new JButton();
				b.setOpaque(true);
				b.putClientProperty("POS", pos);
				pos++;
				b.putClientProperty("OWNED", -1);
				b.addActionListener(new GameButtonListener());
				b.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
				button.add(b);
				this.add(b);
			
		}
		this.boardNum = num;
		this.setBorder(border);
	}
	// checks condition of a game panel, closes tied boards
	public void checkPanel() {
		boolean won = false;
		ArrayList<Integer> xButtons = new ArrayList<>();
		ArrayList<Integer> oButtons = new ArrayList<>();
		for(JButton b : this.button) {
			int owner = (int) b.getClientProperty("OWNED");
			int pos = (int) b.getClientProperty("POS");
			if(owner == 0) {
				oButtons.add(pos);
			}
			if(owner==1) {
				xButtons.add(pos);
			}
		}
		int xCount;
		int oCount;
		Player winner = null;
		for(int i=0; i<winningCombos.length;i++) {
			xCount = 0;
			oCount = 0;
			for(int j=0; j<3; j++) {
				if(xButtons.contains(winningCombos[i][j])) {
					xCount++;
				}
				if(oButtons.contains(winningCombos[i][j])) {
					oCount++;
				}	
			}
			if(xCount == 3) {
				System.out.println("Board "+this.boardNum+" won by X");
				won = true;
				X.boardsWon.add(this.boardNum);
				winner = X;
				break;

			}
			if(oCount == 3) {
				System.out.println("Board "+this.boardNum+" won by O");
				won = true;
				winner = O;
				O.boardsWon.add(this.boardNum);
				break;
			}
		}
		// panel has been won
		if(won) {
			this.closed = true;
			closePanel(winner);
			checkBoard(winner);
			}
		// panel hasn't been won, checks for panel tie
		else {
			if(oButtons.size() + xButtons.size() == 9) {
				this.closed = true;
				for(JButton b : this.button) {
					b.setBackground(Color.LIGHT_GRAY);
				}
				checkBoard(winner);
			}
		}
	}//end check panel
	//closes won boards
	public void closePanel(Player p) {
		for(JButton b : this.button) {
			b.setEnabled(false);
			b.setText(p.label);
			b.setBackground(p.color);
		}
		closedBoards++;
	}
	//reopens previously closed boards
	public void openPanel() {
		this.closed = false;
		if(O.boardsWon.contains(this.boardNum)) {
			O.boardsWon.remove(O.boardsWon.indexOf(this.boardNum));
		}
		if(X.boardsWon.contains(this.boardNum)) {
			X.boardsWon.remove(X.boardsWon.indexOf(this.boardNum));
		}
		for(JButton b : this.button) {
			int owned = (int) b.getClientProperty("OWNED");
			Color orig = (Color) b.getClientProperty("DEFAULT");
//			b.setBackground(Color.RED);
			if(owned<0) {
				b.setEnabled(true);
				b.setBackground(orig);
				b.setText(null);
			}
			if(owned == 0) {
				b.setBackground(O.color);
				b.setText("O");
			}
			if(owned == 1) {
				b.setBackground(X.color);
				b.setText("X");
			}
		}
		closedBoards--;
	}
}//end Board class

// player class
class Player{
	private ArrayList<Integer> boardsWon = new ArrayList<>();
	private String label;
	private Color color;
	private int moves;
	
	public Player(String label, Color c) {
		this.label = label;
		this.color = c;
		this.moves = 0;
	}
}//end Player class

// listener for Game Buttons
class GameButtonListener implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		if(!gameOver) {
			JButton b = (JButton)e.getSource();
			int pos = (int)b.getClientProperty("POS");
			Board picked = (Board) b.getParent();
			if(lastPosition<0) {
//				undoStack.push(lastPosition);
				markButton(b, picked, pos);
			}
			else {
				if(boards.get(lastPosition).closed) {
					markButton(b, picked, pos);
			}
			else {
				if(lastPosition == picked.boardNum) {
					markButton(b, picked, pos);
				}
			}
			}
		}//end not gameOver
		else {
			JOptionPane.showMessageDialog(null, "Click \"Restart\" to start New Game.");
		}
	}
}//end gameButtonListener

// listener for undo button
class UndoButtonListener implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		if(lastClicked != null) {
			Color orig = (Color) lastClicked.getClientProperty("DEFAULT");
			Board last = (Board) lastClicked.getParent();
			last.moves--;
			lastPosition = undoPost;
//			lastPosition = undoStack.pop();
			lastClicked.setEnabled(true);
			lastClicked.setBackground(orig);
			lastClicked.setText(null);
			lastClicked.putClientProperty("OWNED", -1);
			if(last.closed) {
				last.openPanel();
			}
//			last.checkPanel();
			if(currentPlayer == O) {
				currentPlayer = X;
				playerX.setBackground(X.color);
				playerO.setBackground(Color.LIGHT_GRAY);
			}
			else {
				currentPlayer = O;
				playerO.setBackground(O.color);
				playerX.setBackground(Color.LIGHT_GRAY);
			}
			currentPlayer.moves--;
			undo.setEnabled(false);

		}
		
	}	
}//end UndoButtonListener

// listener for restart game
class RestartButtonListener implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		newBoard();	
	}
	
}// end restartButtonListener

// listener for displaly stats
class StatButtonListener implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		if(gameOver) {
			if(winningPlayer == null) {
				JOptionPane.showMessageDialog(null, "Statistics only available after a win.");
			}
			else {
				float playerWon =  winningPlayer.boardsWon.size();
				float closed = closedBoards;
				float winPer = (playerWon / closed) * 100;
				float avgPerWin = winningPlayer.moves / winningPlayer.boardsWon.size();
				totalGames.setText(""+closedBoards);
				avgMoves.setText(""+avgPerWin);
				wins.setText(winPer+"%");
			}
		}
		else {
			JOptionPane.showMessageDialog(null, "Statistics only available after a win.");
		}
		
	}
	
}// end Stats Button Listener

}//end UltimateTCT