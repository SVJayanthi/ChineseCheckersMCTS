package MCTSCCGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import MCTSCCGame.GUI.Seed;

public class Node {
    private Node parent;
    private List<Node> childArray;
    private Seed[][] board;
    private Seed player;
	private Seed piece;
    private int wins;
    private int plays;
    private int x0, y0;
    private int x, y;
    private int level;

    public Node(Node parent, Seed[][] board, Seed player, Seed piece, int wins, int plays, int x0, int y0, int x, int y) {
        this.parent = parent;
    	Simulation figure = new Simulation(board);
        this.board = figure.copyBoard(board);
        this.player = player;
        this.piece = piece;
        this.wins = wins;
        this.plays = plays;
        this.x0 = x0;
        this.y0 = y0;
        this.x = x;
        this.y = y;
        this.level = parent.level++;
        childArray = new ArrayList<>();
    }

    public Node(Seed[][] board, Seed player, int wins, int plays, int x0, int y0, int x, int y, int level) {
    	Simulation figure = new Simulation(board);
        this.board = figure.copyBoard(board);
        this.player = player;
        this.wins = wins;
        this.plays = plays;
        this.x0 = x0;
        this.y0 = y0;
        this.x = x;
        this.y = y;
        this.level = level;
        childArray = new ArrayList<>();
        
    }

    public Node(Node node) {
        if (node.getParent() != null) {
            this.parent = node.getParent();
        }
        this.childArray = new ArrayList<>();
        ArrayList<Node> childArray = new ArrayList<Node>(node.getChildArray().size());
        for (int i = 0; i < node.getChildArray().size(); i++) {
        	childArray.add(node.getChildArray().get(i));
        }
        
        this.board = node.getBoard();
    	Simulation figure = new Simulation(board);
        this.board = figure.copyBoard(node.getBoard());
        this.player = node.getPlayer();
        this.piece = node.getPiece();
        this.wins = node.getWins();
        this.plays = node.getPlays();
        this.x0 = node.getX0();
        this.y0 = node.getY0();
        this.x = node.getX();
        this.y = node.getY();
        this.level = node.getLevel();
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node> getChildArray() {
        return childArray;
    }

    public void setChildArray(List<Node> childArray) {
        this.childArray = childArray;
    }

    Seed[][] getBoard() {
        return board;
    }

    void setBoard(Seed[][] board) {
        this.board = board;
    }

    Seed getPlayer() {
        return player;
    }

    void setPlayer(Seed player) {
        this.player = player;
    }

    Seed getPiece() {
        return piece;
    }

    void setPiece(Seed piece) {
        this.piece = piece;
    }

    Seed getOpponent() {
        return ((player == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS);
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getPlays() {
        return plays;
    }

    public void setPlays(int plays) {
        this.plays = plays;
    }
    
    public int getX0() {
        return x0;
    }
    
    public int getY0() {
        return y0;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }

    public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Node getRandomChildNode() {
        int noOfPossibleMoves = this.childArray.size();
        int selectRandom = (int) (Math.random() * ((noOfPossibleMoves - 1) + 1));
        return this.childArray.get(selectRandom);
    }

    public Node getChildWithMaxScore() {
        return Collections.max(this.childArray, Comparator.comparing(c -> {
            return c.getPlays();
        }));
    }


    public void addChild(Node parent, Node child) {
        parent.getChildArray().add(child);
    }
    
    public void createChildren(Node parenterNode) {
        List<Node> childrenNodes = new ArrayList<Node>();
        
    	Simulation moveFigure = new Simulation(board);        
        int[] positions = moveFigure.searchPieces(board, getOpponent());
        
        for (int i = 0; i < positions.length/2; i++) {
        	List<Integer> tempPositions = new ArrayList<Integer>();
        	tempPositions.addAll(moveFigure.legalMoves(board, getOpponent(), positions[(i*2)], positions[(i*2)+1]));
            int count = tempPositions.size()/2;
            for (int j = 0; j < count; j++) {
            	Seed[][] tempBoard = moveFigure.copyBoard(board);
            	Seed tempSeed = tempBoard[positions[i*2]][positions[i*2+1]];
            	tempBoard[positions[i*2]][positions[i*2+1]] = Seed.EMPTY;
            	tempBoard[tempPositions.get(j*2)][tempPositions.get(j*2+1)] = tempSeed;
                childrenNodes.add(new Node(parenterNode, tempBoard, getOpponent(), tempSeed, 0, 0, positions[i*2], positions[i*2+1], 
                		tempPositions.get(j*2), tempPositions.get(j*2+1)));
            }
        }
        childArray = childrenNodes;
    }

    void incrementWins() {
    	this.wins++;
    }
    
    void incrementPlays() {
        this.plays++;
    }
}