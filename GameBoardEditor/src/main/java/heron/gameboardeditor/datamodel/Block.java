package heron.gameboardeditor.datamodel;

import javafx.fxml.FXML;

public class Block {
    private int x;
    private int y;
    private int z;  // If the level is zero, it should not be visible
    private boolean isPointy = false;
    
	/**
	 * Constructs a block
	 * 
	 * @param x - the x coordinate
	 * @param y - the y coordinate
	 * @param z - the height or level 
	 * 
	 */
    public Block(int x, int y, int z) {
    	this.x = x;
    	this.y = y;
    	this.z = z;
    	
    }
    
    /**
     * This method checks if a block is visible.
     * 
     * @return true - if the height is bigger than 0, the block itself is visible 
     * @return false - if the height is less than or equal to 0, the block itself is inVisible 
     */
	public boolean isVisible() {
		return z > 0;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}

	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
    
	/**
	 * This method sets the height/level of the block.
	 * A z value of zero makes the block "invisible",
	 * otherwise it is visible.
	 * 
	 * @param z - the value that the height of the block will be set to
	 */
	public void setZ(int z) {
		this.z = z;
	}

	
	/**
	 * This method sets the isPointy data field of the block
	 * 
	 * @param pointy - true if the block is pointy and false if the block is not pointy
	 */
	public void setPointy(boolean pointy) {
		this.isPointy = pointy;
	}
	
	/**
	 * This method checks whether the block is pointy
	 * 
	 * @return true if the block is pointy
	 * @return false if the block is not pointy
	 */
	public boolean isPointy() {
		return isPointy;
	}

	
	public Block clone() {
		return new Block(x,y,z);
	}

}
