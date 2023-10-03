package heron.gameboardeditor.generators;

import java.util.ArrayList; 
import java.util.Random;

import heron.gameboardeditor.datamodel.Block;
import heron.gameboardeditor.datamodel.Grid;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Generates a randomized maze on the grid
 */
public class Maze {
	private Grid grid;
	
	private ArrayList<Block> edgeBlocks = new ArrayList<Block>(); //stores all blocks on the edge of the gridBoard. Used for generating the maze
	private ArrayList<Block>mazeBranchBlocks = new ArrayList<Block>();
	private Block failedMovementMazeBlock = new Block (0, 0, 0); //when creating the maze, this represents a block which cannot move in a certain direction
	private Block possibleEndBlock;
	
	private int failedDirectionCount; //count of failed directions. If a failedMovementMazeBlock has 3 failed directions, it cannot move
	private int numBranches = 4; //the number of times branches should be made off of each other
	private int mazeBorderLevel = 2; //the level of the borders of the maze
	private int mazePathLevel = 1; //the level of the path of the maze
	
	public Maze(Grid grid) {
		this.grid = grid;
	}
	
	/**
     * The generate maze methods creates a randomized maze. It works by first setting
     * all of the blocks in the grid to a certain level. The method then starts at the edge
     * of the grid and carves out a path. This method then creates more paths which branch off of that one.
     */
    public void generateMaze() {
    	if (grid.getHeight() < 3 || grid.getWidth() < 3) {
    		Alert errorAlert = new Alert(AlertType.ERROR); //taken help from James_D on Stack Overflow https://stackoverflow.com/questions/39149242/how-can-i-do-an-error-messages-in-javafx
    		errorAlert.setHeaderText("Error");
    		errorAlert.setContentText("The grid is too small!");
    		errorAlert.showAndWait();
    		return;
    	}
    	
    	grid.allBlocksSetZ(mazeBorderLevel);
    	
    	edgeBlocks = grid.getEdgeBlocks();
    	Block block = grid.getRandomEdgeBlock(edgeBlocks); //gets the starting point for the maze
    	block.setZ(mazePathLevel);
    	
    	//direction is an integer which represents the direction the maze path is going in. 1 is up, 2 is right, 3 is down, 4 is left
    	int direction = getInitialDirection(block); //finds the initial direction the path of the maze should go in
    	
    	attemptMazeMovement(block, direction); //creates the first path of the maze
    	
    	createMazeBranches(mazeBranchBlocks); //for building off of the initial path of the maze with more paths
    	int count = 0;
    	while (count < numBranches) {
    		createMazeBranches(mazeBranchBlocks); //for branching off of branches and making more paths
    		mazeBranchBlocks.clear();
    		count = count + 1;
    	}
    	
    	//the possibleEndBlock is a block on the edge of the grid which is next to the end of a random path
    	possibleEndBlock.setZ(mazePathLevel); //places the last block to complete the maze
    }
    
    private int getInitialDirection(Block block) {
    	int direction;
    	
    	if (block.getX() == 0) { //block is on left edge of grid
    		direction = 2;
    	} else if (block.getX() == grid.getWidth() - 1) { //block is on right edge of grid
    		direction = 4;
    	} else if (block.getY() == grid.getHeight() - 1) { //block is on bottom edge of grid
    		direction = 1;
    	} else { //block is on top of grid
    		direction = 3;
    	}
    	
    	return direction;
    }
    
    private int generateNewDirection(int previousDireciton) {
    	Random rand = new Random();
    	int newDirection = rand.nextInt(4) + 1;
    	while (isOppositeDirection(newDirection, previousDireciton)) { //the path should not go backwards
    		newDirection = rand.nextInt(4) + 1;
    	}
    	return newDirection;
    }
    
    private boolean isOppositeDirection(int direction, int previousDirection) {
    	if ((direction == 1 && previousDirection == 3) || (direction == 3 && previousDirection == 1)) {
    		return true;
    	}
    	if ((direction == 2 && previousDirection == 4) || (direction == 4 && previousDirection == 2)) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
	
    /**
     * Randomly branches off of a path to create new paths
     * 
     * @param path- the path of blocks which will have branches
     */
	private void createMazeBranches(ArrayList<Block> path) {
		Random rand = new Random();
		int branchNum = (path.size() - 1); //the number of branches which should be made
		
		for (int i = 0; i < branchNum; i++) { //creating every branch
			Block initialBlock = path.get(rand.nextInt(path.size() - 1)); //finds a random block in the path to start branching off of
			
			while (grid.isEdgeBlock(initialBlock)) { //the starting point of the branch can't be an edge block
				initialBlock = path.get(rand.nextInt(mazeBranchBlocks.size() - 1));
			}
    		int initialDirection = 1;
			Block possiblePathBlock = grid.getBlockGrid()[initialBlock.getX()][initialBlock.getY() - 1];
			createMazePath(possiblePathBlock, initialDirection, initialBlock);
		}
	}
	
	/**
	 * Creates a path in the maze
	 * 
	 * @param possibleBlock - a block in the grid which may or may not become part of the path
	 * @param direction - the direction from the previousBlock in the path to the possibleBlock
	 * @param previousBlock - the previousBlock in the path
	 */
	private void createMazePath(Block possibleBlock, int direction, Block previousBlock) {
		if (grid.isEdgeBlock(possibleBlock)) {
			possibleEndBlock = possibleBlock;
			handleInvalidMovement(previousBlock, direction);
		} else {
			if (isValidPath(possibleBlock, mazeBorderLevel, direction)) {
				Block newBlock = possibleBlock;
				newBlock.setZ(mazePathLevel);
				mazeBranchBlocks.add(newBlock);
				int newDirection = generateNewDirection(direction);
				attemptMazeMovement(newBlock, newDirection);
			} else {
				handleInvalidMovement(previousBlock, direction);
			}
		}
	}
	
	/**
	 * Attempts to add a new block to a path in the maze
	 * 
	 * @param block - the previous block in the path
	 * @param newDirection - the direction the path will go in
	 */
    private void attemptMazeMovement(Block block, int newDirection) {
		if (newDirection == 1) { //up
    		createMazePath(grid.getBlockGrid()[block.getX()][block.getY() - 1], newDirection, block);
    	}
		else if (newDirection == 2) { //right
    		createMazePath(grid.getBlockGrid()[block.getX() + 1][block.getY()], newDirection, block);
    	} else if (newDirection == 3) { //down
    		createMazePath(grid.getBlockGrid()[block.getX()][block.getY() + 1], newDirection, block);
    	} else if (newDirection == 4) { //left
    		createMazePath(grid.getBlockGrid()[block.getX() - 1][block.getY()], newDirection, block);
    	}
    }
    
    /**
     * Handles if the path is unable to add new block in a certain direction
     * 
     * @param block - the block which failed to move (add a new block to the path) in a certain direction
     * @param failedDirection - the failed direction of the block
     */
	private void handleInvalidMovement(Block block, int failedDirection) {
		if (failedMovementMazeBlock.equals(block)) { //if the block has already failed to move (add a new block) in one direction
			failedDirectionCount = failedDirectionCount + 1;
		} else {
			failedMovementMazeBlock = block;
			failedDirectionCount = 1;
		}
		
		if (failedDirectionCount == 4) { //failed to move in every direction
			failedDirectionCount = 0;
			failedMovementMazeBlock = new Block(0, 0, 0);
			return; //if cannot move, the branch should end
		} else { //try moving in a new direction
			int newDirection;
			if (failedDirection == 4) {
				newDirection = 1;
			} else {
				newDirection = failedDirection + 1;
			}
	    	attemptMazeMovement(block, newDirection);
		}
	}
	
	/**
	 * Returns whether or not there are 3 adjacent blocks to the block with the given level
	 * 
	 * @param block - the block to be tested
	 * @param level - the level to be tested
	 * @return whether or not there are 3 adjacent blocks to the block with the given level
	 */
    public boolean isThreeAdjacentBlocksSameLevel(Block block, int level) {
    	int count = countBlocksInFourDirections(block, level);
    	return (count == 3); //returns true if there are 3 adjacent blocks with the level
    }
     
    /**
     * Returns whether or not the block given is a valid path for the maze
     * 
     * @param block - the block to be tested
     * @param level - the level to be tested
     * @param direction - direction the block went in
     * @return whether or not the block given is a valid path for the maze
     */
    public boolean isValidPath(Block block, int level, int direction) {
    	return (isThreeAdjacentBlocksSameLevel(block, level) && (isValidPathCorners(block, level, direction)));
    }
    
    /**
     * Counts the blocks above, below, right, and left of a certain block if they have the specified level
     * @param block
     * @param level
     * @return
     */
    private int countBlocksInFourDirections(Block block, int level) {
    	int count = 0;
    	if (grid.getBlockRight(block).getZ() == level) {
    		count = count + 1;
    	}
    	if (grid.getBlockLeft(block).getZ() == level) {
    		count = count + 1;
    	}
    	if (grid.getBlockAbove(block).getZ() == level) {
    		count = count + 1;
    	}
    	if (grid.getBlockBelow(block).getZ() == level) {
    		count = count + 1;
    	}
    	return count;
    }
    
    private boolean isValidPathCorners(Block block, int level, int direction) {
    	if (direction == 1) { //up
    		if (grid.getBlockAbove(grid.getBlockRight(block)).getZ() == level) { //top right is blank
    	    	if (grid.getBlockAbove(grid.getBlockLeft(block)).getZ() == level) { //top left is blank
    	    		return true;
    	    	}
    		}
    	}
    	
    	if (direction == 2) { //right
    		if ((grid.getBlockAbove(grid.getBlockRight(block)).getZ() == level)) { //top right is blank
    	    	if (grid.getBlockBelow(grid.getBlockRight(block)).getZ() == level) { //bottom right is blank
    	    		return true;
    	    	}
    		}
    	}
    	
    	if (direction == 3) { //down
        	if (grid.getBlockBelow(grid.getBlockRight(block)).getZ() == level) { //bottom right is blank
            	if (grid.getBlockBelow(grid.getBlockLeft(block)).getZ() == level) { //bottom left is blank
            		return true;
            	}
        	}
    	}
    	
    	if (direction == 4) { //left
        	if (grid.getBlockAbove(grid.getBlockLeft(block)).getZ() == level) { //top left is blank
            	if (grid.getBlockBelow(grid.getBlockLeft(block)).getZ() == level) { //bottom left is blank
            		return true;
            	}
        	}
    	}
    	
    	return false;
    }
}
