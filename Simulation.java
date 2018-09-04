package MCTSCCGame;
import java.util.*;

import MCTSCCGame.GUI.GameState;
import MCTSCCGame.GUI.Seed;

public class Simulation{
	public static Seed[] crossId = {Seed.CROSS0, Seed.CROSS1, Seed.CROSS2, Seed.CROSS3, Seed.CROSS4, Seed.CROSS5, 
			   Seed.CROSS6, Seed.CROSS7, Seed.CROSS8, Seed.CROSS9};
	   
	   public static Seed[] noughtId = {Seed.NOUGHT0, Seed.NOUGHT1, Seed.NOUGHT2, Seed.NOUGHT3, Seed.NOUGHT4, Seed.NOUGHT5, 
			   Seed.NOUGHT6, Seed.NOUGHT7, Seed.NOUGHT8, Seed.NOUGHT9};
	   private Seed[][] gameBoard;

	   public static final int[] CCArray = {1, 2, 3, 4, 13, 12, 11, 10, 9, 10, 11, 12, 13, 4, 3, 2, 1};

	   final long timeMaxSeconds = 10;
	   final int iterations = 100;
	   int iterationCount;
	   final static int alpha = 1;
	   final static int kappa = 4;
	   	   
	   public Simulation (Seed[][] boardSim) {
		   gameBoard = copyBoard(boardSim);
	   }
	   
	   
	   public Node simulate(Seed seedSim, Node moveNode, int[] move) {
		   Seed[][] boardSimulation = copyBoard(gameBoard);
		   Seed currentPlayer = (seedSim == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;;
		   
		   if (moveNode == null) {
			   moveNode = new Node(boardSimulation, currentPlayer, 0, 0, move[0], move[1], move[2], move[3], 0);
			   moveNode.createChildren(moveNode);
		   } else if (moveNode != null) {
			   moveNode = findChildren(moveNode, move[0], move[1], move[2], move[3]);
			   moveNode.createChildren(moveNode);
		   }
		   
		   moveNode.setParent(null);

		   Node bestMove = runSim(boardSimulation, moveNode);
		   
		   return bestMove;
	   }
	   

	   private Node runSim(Seed[][] gaming, Node rootNode) {	   
		   exploreChildren(rootNode);

		   long start = System.currentTimeMillis();
		   while (System.currentTimeMillis() - start < (timeMaxSeconds*1000)) {
			   //System.out.println("Time: " + (System.currentTimeMillis() - start)/1000 + " " + (timeMaxSeconds));
			   Node node = selectPromisingMove(rootNode);
			   /*System.out.println("Promising Node: " + node.getPlayer() + " " + 
					   node.getPiece() + " " + node.getWins() + " " + node.getPlays() + " " + 
					   node.getX0() + " " + node.getY0() + " " + node.getX() + " " + node.getY());*/

			   boolean win = false;
			   Seed winner = node.getPlayer();
			   GameState currentState = GameState.PLAYING;
			   
			   while (currentState == GameState.PLAYING) {
				   if (node.getChildArray().size() <= 0) {
					   node.createChildren(node);
				   }
				   
				   Node nextMove = selectPromisingMove(node);
				   
				   node = new Node(nextMove);
				   /*System.out.println("Simulation Node: " + node.getPlayer() + " " + 
						   node.getPiece() + " " + node.getWins() + " " + node.getPlays() + " " + 
						   node.getX0() + " " + node.getY0() + " " + node.getX() + " " + node.getY());*/
				   
				   currentState = updateGame(node.getBoard(), node.getPlayer());
				   //System.out.println("Level Time: " + node.getLevel() + " " + ((System.currentTimeMillis()-start)/1000) + " " + currentState);
				   if (winner == Seed.CROSS) {
					   if (currentState == GameState.CROSS_WON) {
						   win = true;
					   }
				   } else if (winner == Seed.NOUGHT) {
					   if (currentState == GameState.NOUGHT_WON) {
						   win = true;
					   }
				   }
			   }
			   
			   incrementStats(node, win);
		   }
		   
		   int best = 0;
		   ArrayList<Integer> tie = new ArrayList<Integer>();
		   for (int i = 0; i < rootNode.getChildArray().size(); i++) {
			   double wins = rootNode.getChildArray().get(i).getWins();
			   double plays = rootNode.getChildArray().get(i).getPlays();
			   double winRate = wins/plays;

			   wins = rootNode.getChildArray().get(best).getWins();
			   plays = rootNode.getChildArray().get(best).getPlays();
			   double topRate = wins/plays;
			   System.out.println("Final Nodes: " + rootNode.getChildArray().get(i).getPlayer() + " " + 
					   rootNode.getChildArray().get(i).getPiece() + " " + rootNode.getChildArray().get(i).getWins() + " " +
					   rootNode.getChildArray().get(i).getPlays() + " " + winRate + " " + rootNode.getChildArray().get(i).getX0() + " " + 
					   rootNode.getChildArray().get(i).getY0() + " " + rootNode.getChildArray().get(i).getX() + " " + 
					   rootNode.getChildArray().get(i).getY());
			   if (winRate > topRate) {
				   best = i;
				   tie.clear();
				   tie.add(best);
			   } else if (winRate == topRate) {
				   tie.add(i);
			   }
		   }

		   Random rand = new Random();
		   int select = rand.nextInt(tie.size());
		   best = tie.get(select);
		   
		   System.out.println("Best Node: " + rootNode.getChildArray().get(best).getPlayer() + " " + 
				   rootNode.getChildArray().get(best).getPiece() + " " + rootNode.getChildArray().get(best).getWins() + " " +
				   rootNode.getChildArray().get(best).getPlays() + " " + rootNode.getChildArray().get(best).getX0() + " " + 
				   rootNode.getChildArray().get(best).getY0() + " " + rootNode.getChildArray().get(best).getX() + " " + 
				   rootNode.getChildArray().get(best).getY());
		   System.out.println("Root Node: " + rootNode.getPlayer() + " " + rootNode.getWins() + " " + rootNode.getPlays());
		   return rootNode.getChildArray().get(best);
	   }

	   private Node selectPromisingMove(Node node) {
		   int best = 0;
		   ArrayList<Double> select = new ArrayList<Double>();
		   select.ensureCapacity(node.getChildArray().size());
		   for (int i = 0; i < node.getChildArray().size(); i++) {
			   if (node.getChildArray().get(i).getPlays() <= 0) {
				   select.add(Math.random());
			   } else if (node.getChildArray().get(i).getPlays() > 0) {
				   double parentPlays = node.getPlays();
				   double wins = node.getChildArray().get(i).getWins();
				   double plays = node.getChildArray().get(i).getPlays();
				   select.add((wins/plays) + (alpha*Math.sqrt((Math.log(parentPlays))/(kappa*plays))));
			   }
		   }
		   
		   ArrayList<Integer> tie = new ArrayList<Integer>();
		   for (int j = 0; j < select.size(); j++) {
			   if (select.get(j) > select.get(best)) {
				   best = j;
				   tie.clear();
				   tie.add(best);
			   } else if (select.get(j) == select.get(best)) {
				   tie.add(j);
			   }
		   }
		   
		   Random rand = new Random();
		   int stop = rand.nextInt(tie.size());
		   best = tie.get(stop);
		   
		   return node.getChildArray().get(best);
	   }
	   
	   private void incrementStats(Node tempNode, boolean win) {
		   while (tempNode != null) {
			   tempNode.incrementPlays();
			   if (win) {
				   tempNode.incrementWins();
			   }
			   tempNode = tempNode.getParent();
		   }
	   }
	   
	   private void exploreChildren(Node rootNode) {
		   int count = rootNode.getChildArray().size();
		   for (int i = 0; i < count; i++) {
			   Node node = rootNode.getChildArray().get(i);
			   /*System.out.println("Promising Node: " + node.getPlayer() + " " + 
					   node.getPiece() + " " + node.getWins() + " " + node.getPlays() + " " + 
					   node.getX0() + " " + node.getY0() + " " + node.getX() + " " + node.getY());*/

			   boolean win = false;
			   Seed winner = node.getPlayer();
			   GameState currentState = GameState.PLAYING;
			   
			   while (currentState == GameState.PLAYING) {
				   if (node.getChildArray().size() <= 0) {
					   node.createChildren(node);
				   }
				   
				   Node nextMove = selectPromisingMove(node);
				   
				   node = nextMove;
				   /*System.out.println("Simulation Node: " + node.getPlayer() + " " + 
						   node.getPiece() + " " + node.getWins() + " " + node.getPlays() + " " + 
						   node.getX0() + " " + node.getY0() + " " + node.getX() + " " + node.getY());*/
				   
				   currentState = updateGame(node.getBoard(), node.getPlayer());
				   //System.out.println("Level Time: " + node.getLevel() + " " + ((System.currentTimeMillis()-start)/1000) + " " + currentState);
				   if (winner == Seed.CROSS) {
					   if (currentState == GameState.CROSS_WON) {
						   win = true;
					   }
				   } else if (winner == Seed.NOUGHT) {
					   if (currentState == GameState.NOUGHT_WON) {
						   win = true;
					   }
				   }
			   }
			   
			   incrementStats(node, win);
		   }
		   
	   }
	   
	   public Node findChildren(Node ogNode, int x0, int y0, int x, int y) {
		   Node returnNode = null;
		   //System.out.println("Expected: " + x0 + " " + y0 + " " + x + " " + y);
		   for (int i = 0; i < ogNode.getChildArray().size(); i++) {
			   /*System.out.println("OgNode: " + ogNode.getChildArray().get(i).getX0() + " " + ogNode.getChildArray().get(i).getY0() + " " +
					   ogNode.getChildArray().get(i).getX() + " " + ogNode.getChildArray().get(i).getY() + " " + ogNode.getChildArray().get(i).getWins()
					   + " " + ogNode.getChildArray().get(i).getPlays());*/
			   if (ogNode.getChildArray().get(i).getX0() == x0 && ogNode.getChildArray().get(i).getY0() == y0 &&
					   ogNode.getChildArray().get(i).getX() == x && ogNode.getChildArray().get(i).getY() == y) {
				   returnNode = ogNode.getChildArray().get(i);
				   //System.out.println("Selected");
			   }
		   }
		   return returnNode;
	   }

	   public int[] searchPieces(Seed[][] boardSim, Seed simPiece) {
		   int[] pieces = new int[20];
		   if (simPiece == Seed.CROSS) {
			   for (int count = 0; count < 10; count++) {
				   for (int i = 0; i < boardSim.length; i++) {
					   for (int j = 0; j < boardSim[0].length; j++) {
						   if (boardSim[i][j] == crossId[count]) {
							   pieces[count*2] = i;
							   pieces[count*2+1] = j;
						   }
					   }
				   }
			   }
		   }
		   if (simPiece == Seed.NOUGHT) {
			   for (int count = 0; count < 10; count++) {
				   for (int i = 0; i < boardSim.length; i++) {
					   for (int j = 0; j < boardSim[0].length; j++) {
						   if (boardSim[i][j] == noughtId[count]) {
							   pieces[count*2] = i;
							   pieces[count*2+1] = j;
						   }
					   }
				   }
			   }
		   }
		   return pieces;
	   }


	   public Seed[][] copyBoard(Seed[][] gamingBoard) {
		   Seed[][] boardToCopy = new Seed[gamingBoard.length][gamingBoard[0].length];
		   for (int row = 0; row < gamingBoard.length; row++) {
			   for (int col = 0; col < gamingBoard[0].length; col++) {
				   boardToCopy[row][col] = gamingBoard[row][col];
			   }
		   }
		   return boardToCopy;
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
	   
	   
	   public GameState updateGame(Seed[][] game, Seed theSeed) {
		  GameState stateEnd = GameState.PLAYING;
		  if (hasWon(game, theSeed)) {  
	         stateEnd = (theSeed == Seed.CROSS) ? GameState.CROSS_WON : GameState.NOUGHT_WON;
	      }
		  return stateEnd;
	   }

	   public boolean hasWon(Seed[][] winBoard, Seed seedEnd) {
		   boolean winPotential = false;
		   int track = 0;
		   if (seedEnd == Seed.CROSS) {
			   for (int row = 0; row < 4; row++) {
				   for (int col = 0; col < winBoard[0].length; col++) {
					   if (checkPresent(crossId, winBoard[row][col])) {track++;}
				   }
			   }
		   } else if (seedEnd == Seed.NOUGHT) {
			   for (int row = winBoard.length-1; row > winBoard.length-5; row--) {
				   for (int col = 0; col < winBoard[0].length; col++) {
					   if (checkPresent(noughtId, winBoard[row][col])) {track++;}
				   }
			   }
		   }
		   
		   if (track == 10) {
			   winPotential = true;
		   }
		      
		   return winPotential;
	   }
	   
	   public int[] moveCoordinates(Seed[][] game, Seed player, int row, int col) {
		   int[] move = new int[8];
		   
		   if (player == Seed.NOUGHT) {
			   if (row%2 == 0) {
				   move[0] = row;
				   move[1] = col-1;
				   move[2] = row+1;
				   move[3] = col-1;
				   move[4] = row+1;
				   move[5] = col;
				   move[6] = row;
				   move[7] = col+1;
			   } else if (row%2 == 1) {
				   move[0] = row;
				   move[1] = col-1;
				   move[2] = row+1;
				   move[3] = col;
				   move[4] = row+1;
				   move[5] = col+1;
				   move[6] = row;
				   move[7] = col+1;
			   }
		   } else if (player == Seed.CROSS) {
			   if (row%2 == 0) {
				   move[0] = row;
				   move[1] = col-1;
				   move[2] = row-1;
				   move[3] = col-1;
				   move[4] = row-1;
				   move[5] = col;
				   move[6] = row;
				   move[7] = col+1;
			   } else if (row%2 == 1) {
				   move[0] = row;
				   move[1] = col-1;
				   move[2] = row-1;
				   move[3] = col;
				   move[4] = row-1;
				   move[5] = col+1;
				   move[6] = row;
				   move[7] = col+1;
			   }
		   }
		   return move;
	   }

		public ArrayList<Integer> jumpMove(int[] moveConsidered, Seed player, Seed[] stateSemiCircle) {
			ArrayList<Integer> considered = new ArrayList<Integer>();
			int[] considerOptions = {(moveConsidered[0]-1), (moveConsidered[1]), (moveConsidered[0]+1), (moveConsidered[1]), (moveConsidered[0]), (moveConsidered[1]-1),
					(moveConsidered[0]+1), (moveConsidered[1]-1)};
			
			for (int i = 0; i < stateSemiCircle.length; i++) {
				if (stateSemiCircle[i] == Seed.EMPTY) {
					considered.add(considerOptions[i*2]);
					considered.add(considerOptions[(i*2)+1]);
				}
			}
			
			return considered;
		}
		
		
	   
	   public ArrayList<Integer> movesSimulate(Seed[][] game, Seed player, int oldX, int oldY, int newX, int newY) {
		   Seed[][] simBoard = copyBoard(game);
		   Seed currentPlayer = player;
		   ArrayList<Integer> simMoves = new ArrayList<Integer>();
		   simMoves.ensureCapacity(40);
		   
		   if (Arrays.deepEquals(simBoard, simBoard)) {
			   simMoves.clear();
		   }
		   
		   int xDif, yDif, xM, yM;
		   
		   xDif = newX - oldX;
		   yDif = newY - oldY;
		   xM = newX + xDif;
		   yM = newY;
		   if ((yDif + 1)%2 == 1) {
			   if (newX%2 == 1) {
				   yM = newY + ((yDif + 1)%2);
			   } else if (newX%2 == 0) {
				   yM = newY - ((yDif + 1)%2);
			   }
		   }
		   if (xDif == 0) {
			   yM = newY + yDif;
		   }

		   if (xM < 17 && xM > -1 && yM > -1 && yM < 13) {
			   if (simBoard[xM][yM] == Seed.EMPTY) {
				   simBoard[xM][yM] = currentPlayer;
				   simMoves.add(xM);
				   simMoves.add(yM);

				   int[] movesC = moveCoordinates(simBoard, player, xM, yM);
				   int count = movesC.length/2;
				   for (int i = 0; i < count; i++) {
					   if (movesC[(i*2)] < 17 && movesC[(i*2)] > -1 && movesC[(i*2)+1] > -1 && movesC[(i*2)+1] < 13) {
						   if (checkPresent(crossId, simBoard[movesC[i*2]][movesC[(i*2)+1]]) || checkPresent(noughtId, simBoard[movesC[i*2]][movesC[(i*2)+1]])) {
							   ArrayList<Integer> setMoves = movesSimulate(simBoard, currentPlayer, xM, yM, movesC[(i*2)], movesC[(i*2)+1]);
							   for (int j = 0; j < setMoves.size(); j++) {
								   simMoves.add(setMoves.get(j));
							   }
						   }
					   }
				   }			   
			   }
		   }

		   return simMoves;
	   }
	   
	   public ArrayList<Integer> legalMoves(Seed[][] game, Seed player, int x, int y) {
		   ArrayList<Integer> moves = new ArrayList<Integer>();
		   moves.ensureCapacity(60);
		   
		   Seed[][] gameB = copyBoard(game);
		   int[] movesC = moveCoordinates(gameB, player, x, y);
		   
		   if (x < 17 && x > -1 && y > -1 && y < 13) {
			   int count = movesC.length/2;
			   for (int i = 0; i < count; i++) {
				   if (movesC[(i*2)] < 17 && movesC[(i*2)] > -1 && movesC[(i*2)+1] > -1 && movesC[(i*2)+1] < 13) {
					   if (gameB[movesC[(i*2)]][movesC[(i*2)+1]] == Seed.EMPTY) {
						   moves.add(movesC[(i*2)]);
						   moves.add(movesC[(i*2)+1]);
					   }
					   if (checkPresent(crossId, gameB[movesC[(i*2)]][movesC[(i*2)+1]]) || checkPresent(noughtId, gameB[movesC[(i*2)]][movesC[(i*2)+1]])) {
						   ArrayList<Integer> setMoves = movesSimulate(gameB, player, x, y, movesC[(i*2)], movesC[(i*2)+1]);
						   for (int j = 0; j < setMoves.size(); j++) {
							   moves.add(setMoves.get(j));
						   }
					   }
				   }
			   }
		   }
		   
			return moves;
		}
}