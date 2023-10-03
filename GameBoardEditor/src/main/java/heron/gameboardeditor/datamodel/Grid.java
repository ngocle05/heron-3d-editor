package heron.gameboardeditor.datamodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import heron.gameboardeditor.CellUI;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * This class represents the data of the GridBoardUI class
 */
public class Grid implements Cloneable {

	private Block[][] blockGrid;
	private int width;
	private int height;
	private int maxZ; //the max level a block can be
	
	/**
	 * Constructs a grid 
	 * 
	 * @param width - the number of columns
	 * @param height - the number of rows
	 * 
	 */
	public Grid(int width, int height) {
		this.width = width;
		this.height = height;
		blockGrid = new Block[width][height];
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				blockGrid[x][y] = new Block(x,y,0);
			}			
		}
		
		this.maxZ = 5; //default max level
	}

	//------------- getters and setters -------------
	
	public int getMaxZ() {
		return this.maxZ;
	}
	
	public void setMaxZ(int maxZ) {
		this.maxZ = maxZ;
	}

	public Block getBlockAt(int x, int y) {
		return blockGrid[x][y];
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public Block[][] getBlockGrid() {
		return blockGrid;
	}
	
	/**
	 * This method return the current highest level of the grid
	 * 
	 * @return max - the highest level of the grid
	 */
	public int getMaxLevel() {
		int max = -1;
		for (int y = 0; y < width; y++) {
			for (int x = 0; x < height; x++) {
				Block block = getBlockAt(x, y);
				int level = block.getZ();
				if (level > max) {
					max = level;
				}
			}			
		}
		return max;
	}
	
	/**
	 * This method checks if a coordinate is inside the grid
	 * 
	 * @param x - the x coordinate
	 * @param y - the y coordinate
	 * 
	 * @return true if the coordinate is inside the grid, otherwise, it's false
	 */
	public boolean isCoordinateInGrid(int x, int y) {
		if ((x < 0) || (x > width - 1)) {
			return false;
		} else if ((y < 0) || (y > height - 1)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * This method allows us to resize the grid by creating a new gird (with a new width and a new height) 
	 * and set the data field blockGrid to this new grid. 
	 * The new grid is created based on the old grid, hence, the information of the cells (from the old grid)
	 * which fit in the new grid are kept and moved to the new grid.
	 * 
	 * @param newWidth - the number of columns of the new grid
	 * @param newHeight - the number of rows of the new grid
	 *           
	 */
	public void resize(int newWidth, int newHeight) {
		Block[][] newBlockGrid = new Block[newWidth][newHeight];
		for (int y = 0; y < newHeight; y++) {
			for (int x = 0; x < newWidth; x++) {
				if (x >= width || y >= height) {  // if the old grid does not contain the cell, create a new cell
					newBlockGrid[x][y] = new Block(x,y,0);
				} else { // if the old grid contains the cell, copy the cell to the new grid
					newBlockGrid[x][y] = blockGrid[x][y];
				}
			}			
		}
		this.blockGrid = newBlockGrid;
		this.width = newWidth;
		this.height = newHeight;
	}
	
	/**
	 * Sets if the block is pointy
	 * @param pointy - whether or not a block is pointy
	 */
	public void setPointy(boolean pointy) {
		for (int y = 0; y < this.getHeight(); y++) {
            for (int x = 0; x < this.getWidth(); x++) {
            	blockGrid[x][y].setPointy(pointy);
            }
    	}
	}
	
	/**
	 * Set the visibility of the block based on its level 
	 * Level 0 is auto set to invisible, and other levels greater than 0 are visible 
	 * @param level - pass in the level to check
	 * @return boolean if the level should be visible
	 */
	public boolean isVisibleLevel(int level) {
		boolean isVisible;
		if (level == 0) {
    		isVisible = false;
    	} else {
    		isVisible = true;
    	}
		return isVisible;
	}
	
	//---------------- Maze Generation -----------------------------
	/**
	 * Sets all the blocks to z
	 * @param level - the level which the blocks should be set to
	 */
	public void allBlocksSetZ(int level) {
		boolean isVisible = isVisibleLevel(level);
		
		for (int y = 0; y < this.getHeight(); y++) {
            for (int x = 0; x < this.getWidth(); x++) {
            	blockGrid[x][y].setZ(level);
            }
    	}
	}
	
	/**
	 * Returns the block to the right of the parameter
	 * @param block - Block the block to check
	 * @return the block to the right of the parameter
	 */
	public Block getBlockRight(Block block) {
		return this.getBlockAt(block.getX() + 1, block.getY());
	}
	
	/**
	 * Returns the block to the left of the parameter
	 * @param block - Block the block to check
	 * @return the block to the left of the parameter
	 */
	public Block getBlockLeft(Block block) {
		return this.getBlockAt(block.getX() - 1, block.getY());
	}
	
	/**
	 * Returns the block to the above block of the parameter
	 * @param block - Block the block to check
	 * @return the block above the parameter
	 */
	public Block getBlockAbove(Block block) {
		return this.getBlockAt(block.getX(), block.getY() - 1);
	}
	
	/**
	 * Returns the block below the parameter
	 * @param block - Block the block to check
	 * @return the block to the right of the parameter
	 */
	public Block getBlockBelow(Block block) {
		return this.getBlockAt(block.getX(), block.getY() + 1);
	}
	
	/**
	 * Returns if a block is on the edge of the grid
	 * @param block - Block block to check
	 * @return if a block is on the edge of the grid
	 */
    public boolean isEdgeBlock(Block block) {
    	return (block.getX() == this.width - 1 || block.getX() == 0 || block.getY() == this.height - 1 || block.getY() == 0);
    }
    
    /**
     * Returns if a block is on the corner of the grid
     * @param block - Block block to check
     * @return if a block is on the corner of the grid
     */
    public boolean isCornerBlock(Block block) {
    	return ((block.getX() == 0 && block.getY() == 0) || (block.getX() == 0 && block.getY() == this.height - 1) || (block.getX() == this.width - 1 && block.getY() == 0) || (block.getX() == this.width - 1 && block.getY() == this.height - 1));
    }
    
    /**
     * Gets all the blocks on the edge of the gridboard
     * @return - ArrayList of blocks on the edge of the gridboard
     */
    public ArrayList<Block> getEdgeBlocks() {
    	ArrayList<Block> edgeBlocks = new ArrayList<>();
    	
    	int edgeBlockCount = 0;
    	
    	for (int y = 0; y < this.getHeight(); y++) {
            for (int x = 0; x < this.getWidth(); x++) {
            	if (isEdgeBlock(blockGrid[x][y])) {
            		edgeBlocks.add(blockGrid[x][y]); //adds all edge blocks to an array
            		edgeBlockCount = edgeBlockCount + 1;
            	}
            }
    	}
    	return edgeBlocks;
    }
    
    /**
     * Returns a random block on the edge of the grid
     * @param edgeBlocks - the array list of blocks on the edge of the grid
     * @return - Block a block on the edge of the grid
     */
    public Block getRandomEdgeBlock(ArrayList<Block> edgeBlocks) {
    	Random rand = new Random();
    	Block block = edgeBlocks.get(rand.nextInt(edgeBlocks.size())); //randomly chooses an edge block for the start of the maze
    	
    	while (this.isCornerBlock(block)) { //the starting block should not be on a corner
    		block = edgeBlocks.get(rand.nextInt(edgeBlocks.size()));
    	}
    	
    	edgeBlocks.clear();
    	
    	return block;
    }
	
	//----------------- Set Max Level for the Level Slider ---------
	/**
	 * Lowers all blocks higher than a specified level
	 * @param level - the level which should be the highest in the grid
	 */
	public void lowerBlocksHigherThan(int level) {
		for (int y = 0; y < this.getHeight(); y++) {
            for (int x = 0; x < this.getWidth(); x++) {
            	if (blockGrid[x][y].getZ() > level) {
            		blockGrid[x][y].setZ(level);
            	}
            }
    	}
	}
	
	//----------------- Related Terrain Tool ----------------------
	/**
	 * Returns the blocks associated with the selectedCells
	 * @param selectedCells - Set of CellUI which are selected
	 * @return a set of blocks which are associated with the selected Cells
	 */
	public Set<Block> getSelectedBlocks(Set<CellUI> selectedCells) { //gets the blocks associated with the selected cells
		Set<Block> selectedBlocks = new HashSet<Block>();
		for (CellUI cell : selectedCells) {
			selectedBlocks.add(cell.getBlock());
		}
		
		return selectedBlocks;
	}
	
	//------------------ Related Selection Tool ----------------
	public void cutAndPaste(Set<Block> selectedBlocks, int changeInXIndex, int changeInYIndex) throws ArrayIndexOutOfBoundsException {
		Grid originalData = this.clone();
    	
		for (Block block : selectedBlocks) {
    		int srcX = block.getX();
    		int srcY = block.getY();
    		blockGrid[srcX][srcY].setZ(0);
    	}

    	for (Block block : selectedBlocks) {
    		int srcX = block.getX();
    		int srcY = block.getY();
	    	int destX = srcX + changeInXIndex;
	    	int destY = srcY + changeInYIndex;
    		if (destX < width && destX >= 0 && destY < height && destY >= 0) { //if block is in the grid
	    		blockGrid[destX][destY].setZ(originalData.blockGrid[srcX][srcY].getZ());
    		}
    	}
	}
	
	/**
	 * Prints the grid
	 */
	public void printGrid() {
		System.out.println(this.toString());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Grid " + width + "x" +height+"\n");
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				sb.append(" ");
				sb.append(blockGrid[x][y].getZ()) ;
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * creates and returns a clone of this Grid and the list of Block contained in this Grid and Block themselves
	 * 
	 * @return a deep clone of this Grid
	 */
	public Grid clone() {
		try {
			Grid clone = (Grid) super.clone();
			clone.blockGrid = new Block[width][height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					clone.blockGrid[x][y] = this.blockGrid[x][y].clone();
				}			
			} 
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
