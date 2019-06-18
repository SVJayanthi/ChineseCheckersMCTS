package MCTSCCGame;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.*;

@SuppressWarnings("serial")
public class GUI extends JFrame {
   public static final int[] CCArray = {1, 2, 3, 4, 13, 12, 11, 10, 9, 10, 11, 12, 13, 4, 3, 2, 1};
 
   public static final int ROWS = 17;
   public static final int COLS = 13;
   public static final int CELL_SIZE = 40;
   public static final int CANVAS_WIDTH = CELL_SIZE * COLS;
   public static final int CANVAS_HEIGHT = CELL_SIZE * ROWS;
   public static final int GRID_WIDTH = 4;
   public static final int GRID_WIDHT_HALF = GRID_WIDTH / 2;
   public static final int CELL_PADDING = CELL_SIZE / 6;
   public static final int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2;
   public static final int SYMBOL_STROKE_WIDTH = 4;
   
   public static Seed pieceMoved;
   public static boolean firstClick = true;
   public static int[] firstMove = new int[2];
   public static Node pieceMove = null;

   public enum GameState {
      PLAYING, CROSS_WON, NOUGHT_WON
   }
   private GameState currentState;
 
   public enum Seed {
      EMPTY,  CROSS, CROSS0, CROSS1, CROSS2, CROSS3, CROSS4, CROSS5, CROSS6, CROSS7, CROSS8, CROSS9, 
      NOUGHT, NOUGHT0, NOUGHT1, NOUGHT2, NOUGHT3, NOUGHT4, NOUGHT5, NOUGHT6, NOUGHT7, NOUGHT8, NOUGHT9, CONSIDERED, ILLEGITIMATE
   }
   
   public static Seed[] crossId = {Seed.CROSS0, Seed.CROSS1, Seed.CROSS2, Seed.CROSS3, Seed.CROSS4, Seed.CROSS5, 
		   Seed.CROSS6, Seed.CROSS7, Seed.CROSS8, Seed.CROSS9};
   
   public static Seed[] noughtId = {Seed.NOUGHT0, Seed.NOUGHT1, Seed.NOUGHT2, Seed.NOUGHT3, Seed.NOUGHT4, Seed.NOUGHT5, 
		   Seed.NOUGHT6, Seed.NOUGHT7, Seed.NOUGHT8, Seed.NOUGHT9};
   
   private Seed currentPlayer;
 
   private Seed[][] board;
   private DrawCanvas canvas;
   private JLabel statusBar;
 
   /** Constructor to setup the game and the GUI components */
   public GUI() {
      canvas = new DrawCanvas();
      canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
      
      canvas.addMouseListener(new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
             int mouseX = e.getX();
             int mouseY = e.getY();
             
             int rowSelected = mouseY / CELL_SIZE;
             int colSelected = mouseX / CELL_SIZE;;
             if (rowSelected%2 == 1) {
                 double column = (mouseX - CELL_SIZE/2) / CELL_SIZE;
                 colSelected = (int) column;
             }

      		Simulation lawyer = new Simulation(board);
             if (currentState == GameState.PLAYING) {
             	if(firstClick) {
             		pieceMoved = checkPiece(board, currentPlayer, rowSelected, colSelected);
             		
             		if (checkPresent(crossId, pieceMoved) || checkPresent(noughtId, pieceMoved)) {
             			ArrayList<Integer> moves = lawyer.legalMoves(board, currentPlayer, rowSelected, colSelected);
             			considerMoves(moves);
             			
             			firstMove[0] = rowSelected;
             			firstMove[1] = colSelected;
                 		firstClick = false;
             		} else {
             			pieceMoved = Seed.ILLEGITIMATE;
             			firstClick = true;
             		}
             		
             	} else {
                    statusBar.setText("Computer's Turn");
         			deConsiderMoves();
         			ArrayList<Integer> moves = lawyer.legalMoves(board, currentPlayer, firstMove[0], firstMove[1]);
                 	if (moveLegal(moves, rowSelected, colSelected)) {
                 		removePiece(pieceMoved);
                 		board[rowSelected][colSelected] = pieceMoved;
                 		int[] finalMove = {rowSelected, colSelected};
        		        currentState = lawyer.updateGame(board, currentPlayer);
        		        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;            	

                        repaint();
                         
                         if (currentState == GameState.PLAYING) {
                         	pieceMove = AIMove(pieceMove, firstMove, finalMove);
                         }
                 	} 
                 	pieceMoved = Seed.ILLEGITIMATE;
                     firstClick = true;
             	}
             } else {
                initGame();
             }
             
             repaint();
          }
       });
      
      statusBar = new JLabel("  ");
      statusBar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
      statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));
 
      Container cp = getContentPane();
      cp.setLayout(new BorderLayout());
      cp.add(canvas, BorderLayout.CENTER);
      cp.add(statusBar, BorderLayout.PAGE_END);
 
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      pack();
      setTitle("Chinese Checkers");
      setVisible(true);
 
      board = new Seed[ROWS][COLS];
      initGame();
   }
   
   	public void initGame() {
       int col;
       for (int row = 0; row < ROWS; row++) {
      	 col = ((COLS-1)/2) - ((CCArray[row] - (CCArray[row]%2))/2);
      	 for (int i = 0; i < CCArray[row]; i++) {
      		 board[row][col] = Seed.EMPTY;
      		 col++;
      	 }
      	 col = 0;
       }

       for (int row = 0; row < ROWS; row++) {
    	   for (col = 0; col < COLS; col++) {
    		   if (board[row][col] != Seed.EMPTY) {
          		 board[row][col] = Seed.ILLEGITIMATE;
    		   }  
    	   }
       }

	   int count = 0;
       for (int row = 0; row < 4; row++) {
        	 col = ((COLS-1)/2) - ((CCArray[row] - (CCArray[row]%2))/2);
        	 for (int i = 0; i < CCArray[row]; i++) {
        		 board[row][col] = noughtId[count];
        		 col++;
            	 count++;
        	 }
          	 col = 0;
       }
       
       count = 0;
       for (int row = ROWS - 1; row > (ROWS - 5); row--) {
        	 col = ((COLS-1)/2) - ((CCArray[row] - (CCArray[row]%2))/2);
      	 	for (int i = 0; i < CCArray[row]; i++) {
      	 		board[row][col] = crossId[count];
      	 		col++;
          	 	count++;
      	 	}
         	 col = 0;
        }
      currentState = GameState.PLAYING;
      currentPlayer = Seed.CROSS;
   }


	public Seed checkPiece(Seed[][] boarding, Seed player, int rowSelected, int colSelected) {
		Seed selected = Seed.ILLEGITIMATE;
		if (player == Seed.CROSS) {
			   for (int i = 0; i < crossId.length; i++) {
				   if (boarding[rowSelected][colSelected] == crossId[i]) {
					   selected = crossId[i];
				   }
			   }
		} else if (player == Seed.NOUGHT) {
			   for (int i = 0; i < noughtId.length; i++) {
				   if (boarding[rowSelected][colSelected] == noughtId[i]) {
					   selected = noughtId[i];
				   }
			   }
		}
		return selected;
	}
	
	public void removePiece(Seed piece) {
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[0].length; col++) {
				if (piece == board[row][col]) {
					board[row][col] = Seed.EMPTY;
				}
			}
		}
	}
	
   
   public boolean moveLegal(ArrayList<Integer> moves, int rowSelected, int colSelected) {
		boolean possibility = false;
		int possible = moves.size()/2;
		for (int look = 0; look < possible; look++) {
			if ((rowSelected == moves.get(look*2)) && (colSelected == moves.get(look*2 + 1))) {
				possibility = true;
				break;
			}
		}
		return possibility;
	}

	   public void considerMoves(ArrayList<Integer> moves) {
		   ArrayList<Integer> movesC = moves;
		   int count = movesC.size()/2;
		   for (int i = 0; i < count; i++) {
			   board[movesC.get(i*2)][movesC.get((i*2)+1)] = Seed.CONSIDERED;
		   }
	   }
	   
	   public void deConsiderMoves() {
		   for (int row = 0; row < board.length; row ++) {
			   for (int col = 0; col < board[0].length; col++) {
				   if (board[row][col] == Seed.CONSIDERED) {
					   board[row][col] = Seed.EMPTY;
				   }
			   }
		   }
	   }

   public Node AIMove(Node moveNode, int[] moveFirst, int[] moveLast) {
       Simulation sim = new Simulation(board);
       int[] move = {moveFirst[0], moveFirst[1], moveLast[0], moveLast[1]};
       Node AIMove = sim.simulate(currentPlayer, moveNode, move);
       Seed piece = checkPiece(board, currentPlayer, AIMove.getX0(), AIMove.getY0());
       removePiece(piece);
       board[AIMove.getX()][AIMove.getY()] = piece;
       currentState = sim.updateGame(board, currentPlayer);         
       currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
       return AIMove;
   }
   
   public boolean checkPresent(Seed[] options, Seed match) {
	   boolean check = false;
	   for (int i = 0; i < options.length; i++) {
		   if (match == options[i]) {
			   check = true;
		   }
	   }
	   return check;
   }
 
   class DrawCanvas extends JPanel {
      @Override
      public void paintComponent(Graphics g) {
         super.paintComponent(g); 
         setBackground(Color.WHITE);
         g.setColor(Color.BLACK);
         
         int xVal;
         for (int yVal = 0; yVal < CCArray.length; yVal++) {
        	 xVal = 0;
        	 while (xVal < CCArray[yVal]) {
        		 g.drawOval(((CANVAS_WIDTH/2) - (((CCArray[yVal]) % 2) * CELL_SIZE/2)) - (CELL_SIZE * (((CCArray[yVal]) - (CCArray[yVal]%2))/2)) + 
        				 ((CELL_SIZE) * xVal), CELL_SIZE * yVal, CELL_SIZE, CELL_SIZE);
        		 xVal++;
        	 }
         }
         
         
         Graphics2D g2d = (Graphics2D)g;
         g2d.setStroke(new BasicStroke(SYMBOL_STROKE_WIDTH, BasicStroke.CAP_ROUND,
               BasicStroke.JOIN_ROUND));
         
         for (int yVal = 0; yVal < ROWS; yVal++) {
        	 for (int xFill = 0; xFill < COLS; xFill++) {
        		 int yPlot = yVal;
        		 int xPlot = (((CCArray[yPlot] - (CCArray[yPlot]%2))/2) - 6) + xFill;
        		 
        		 
        		 if (checkPresent(crossId, board[yVal][xFill])) {
        			 g2d.setColor(Color.RED);
            		 g.drawOval(((CANVAS_WIDTH/2) - (((CCArray[yPlot]) % 2) * CELL_SIZE/2)) - (CELL_SIZE * (((CCArray[yPlot]) - (CCArray[yPlot]%2))/2)) + 
            				 ((CELL_SIZE) * xPlot), CELL_SIZE * yPlot, CELL_SIZE, CELL_SIZE);
            		 g.fillOval(((CANVAS_WIDTH/2) - (((CCArray[yPlot]) % 2) * CELL_SIZE/2)) - (CELL_SIZE * (((CCArray[yPlot]) - (CCArray[yPlot]%2))/2)) + 
            				 ((CELL_SIZE) * xPlot), CELL_SIZE * yPlot, CELL_SIZE, CELL_SIZE);
        		 }
        		 
        		 if (checkPresent(noughtId, board[yVal][xFill])) {
        			 g2d.setColor(Color.BLUE);
            		 g.drawOval(((CANVAS_WIDTH/2) - (((CCArray[yPlot]) % 2) * CELL_SIZE/2)) - (CELL_SIZE * (((CCArray[yPlot]) - (CCArray[yPlot]%2))/2)) + 
            				 ((CELL_SIZE) * xPlot), CELL_SIZE * yPlot, CELL_SIZE, CELL_SIZE);
            		 g.fillOval(((CANVAS_WIDTH/2) - (((CCArray[yPlot]) % 2) * CELL_SIZE/2)) - (CELL_SIZE * (((CCArray[yPlot]) - (CCArray[yPlot]%2))/2)) + 
            				 ((CELL_SIZE) * xPlot), CELL_SIZE * yPlot, CELL_SIZE, CELL_SIZE);
        		 }

        		 if (board[yVal][xFill] == Seed.CONSIDERED) {
        			 g2d.setColor(Color.GREEN);
            		 g.drawOval(((CANVAS_WIDTH/2) - (((CCArray[yPlot]) % 2) * CELL_SIZE/2)) - (CELL_SIZE * (((CCArray[yPlot]) - (CCArray[yPlot]%2))/2)) + 
            				 ((CELL_SIZE) * xPlot), CELL_SIZE * yPlot, CELL_SIZE, CELL_SIZE);
            		 g.fillOval(((CANVAS_WIDTH/2) - (((CCArray[yPlot]) % 2) * CELL_SIZE/2)) - (CELL_SIZE * (((CCArray[yPlot]) - (CCArray[yPlot]%2))/2)) + 
            				 ((CELL_SIZE) * xPlot), CELL_SIZE * yPlot, CELL_SIZE, CELL_SIZE);
        		 }
        		 

        		 if (board[yVal][xFill] == Seed.EMPTY) {
        			 g2d.setColor(Color.WHITE);
            		 g.drawOval(((CANVAS_WIDTH/2) - (((CCArray[yPlot]) % 2) * CELL_SIZE/2)) - (CELL_SIZE * (((CCArray[yPlot]) - (CCArray[yPlot]%2))/2)) + 
            				 ((CELL_SIZE) * xPlot), CELL_SIZE * yPlot, CELL_SIZE, CELL_SIZE);
            		 g.fillOval(((CANVAS_WIDTH/2) - (((CCArray[yPlot]) % 2) * CELL_SIZE/2)) - (CELL_SIZE * (((CCArray[yPlot]) - (CCArray[yPlot]%2))/2)) + 
            				 ((CELL_SIZE) * xPlot), CELL_SIZE * yPlot, CELL_SIZE, CELL_SIZE);
        			 g2d.setColor(Color.BLACK);
            		 g.drawOval(((CANVAS_WIDTH/2) - (((CCArray[yPlot]) % 2) * CELL_SIZE/2)) - (CELL_SIZE * (((CCArray[yPlot]) - (CCArray[yPlot]%2))/2)) + 
            				 ((CELL_SIZE) * xPlot), CELL_SIZE * yPlot, CELL_SIZE, CELL_SIZE);
        		 }
        	 }
         }
 
         
         if (currentState == GameState.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            if (currentPlayer == Seed.CROSS) {
               statusBar.setText("Your Turn");
            } else {
               statusBar.setText("Computer's Turn");
            }
         } else if (currentState == GameState.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'Red' Won! Click to play again.");
         } else if (currentState == GameState.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'Blue' Won! Click to play again.");
         }
      }
   }
}
