# Code for Chinese Checkers AI
<p align="center">
  <img width="450" height="590" src="ChineseCheckersBoard.PNG">
</p>


## Author
Sravan Jayanthi

## Chinese Checkers AI
This is a Java program that is an Aritificial Intelligence player that utilizes machine learning and simulation methods to predict and execute moves in Chinese Checkers. This program allows for a user to play against the simulation iteration of the A.I.
It has both a graphical user interface.

### Chinese Checkers GUI
To run the program, open the GUI file from `ChineseCheckersMCTS\src\MCTSCCGame\GUI.java` in a Java IDE and run the interface to play the Chinese Checkers game.

## Method

### Background
The Chinese Checkers game was created utilizing the Java computer programming language in the Eclipse Integrated Development Environment. The premise of the application was to create an unbeatable Chinese Checkers player. The elements that would constitute the creation of the application would be the methods of Monte Carlo, Markov Chain Monte Carlo, Monte Carlo Tree Search, and Artificial Neural Networks that had been studied. The structure of play would be focused on a limited game between only two players. All rules and regulations for the play of Chinese Checkers would be maintained and the condition for winning would be when one player moves all their pieces to the opposite end of the board. 

### Design
 The game was created with a two dimensional Graphical User Interface that would represent the board. In addition, there was an object created to represent the board and the positions of each of the pieces at each time. Each player has ten pieces aligned in a triangular formation and could interact with any piece on the board and perform any moves that are legally sanctioned. In order to determine what moves were allowed for each piece, upon reaching a player’s turn, simulate the moves utilizing a random samples of possible moves. It would analyze the current state of the game board and see which spaces were opened and which spaces were occupied but the adjacent space was open so a piece could still jump over.

### Monte Carlo Tree Search
In application of the Monte Carlo Tree Search method, a simulation is implemented in the process that determines the possible moves. The parameters for which a piece can traverse are restricted by the size of the board, which is seventeen units in the horizontal direction (`x_space`) and thirteen in the vertical direction (`y_space`). Thus, any location a piece can move would be restricted to be inside the playable game board. Due to the fact that Chinese Checkers allows pieces to jump multiple times consecutively, it is unknown from an initial simulation all the possible locations where a piece could eventually land. Therefore, moves of a piece are simulated multiple different times and the final outcome of the pieces’ location is recorded after each simulation. 

### Code Example:  
Sample of simulating the potential outcomes of move.

	   public Node simulate(Seed seedSim, Node moveNode, int[] move) {
		   Seed[][] boardSimulation = copyBoard(gameBoard);
		   Seed currentPlayer = (seedSim == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
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

### Insights
The final product can effectively handle a two player simulation of Chinese Checkers and return moves with a strategy built on MCTS was engineered from scratch. Although strategies currently exist for human players, the strategy used by the computer within the virtual simulation is entirely based off of simulation methods of Monte Carlo Tree Search, deriving from the Original Monte Carlo method. The game is able to utilize a tree of nodes representing possible moves, and moves from those moves, and so on. This function continues for a fixed period to allow the computer to determine possible moves but still generate efficient gameplay. After the time has elapsed, the computer determines the most advantageous first move from the first layer of nodes and uses that selection. The play then returns to the human player, and this pattern repeats in a cyclical manner.

## License
[GNU](LICENSE)
