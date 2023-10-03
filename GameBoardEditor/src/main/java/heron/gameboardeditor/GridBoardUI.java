
package heron.gameboardeditor;


import java.util.Set;
import heron.gameboardeditor.datamodel.Grid;
import heron.gameboardeditor.generators.Maze;
import heron.gameboardeditor.tools.DigTool;
import heron.gameboardeditor.tools.EraserTool;
import heron.gameboardeditor.tools.FillTool;
import heron.gameboardeditor.tools.LevelPickerTool;
import heron.gameboardeditor.tools.PencilTool;
import heron.gameboardeditor.tools.PointyTool;
import heron.gameboardeditor.tools.SelectionTool;
import heron.gameboardeditor.tools.TerrainTool;
import javafx.scene.layout.AnchorPane;

/**
 * This class represents the grid of cells
 */
public class GridBoardUI extends AnchorPane {

	
	private Grid gridData;
	private CellUI[][] cellArray;
	public GridEditor gridEditor; //controls which tool the user is currently using
	private UndoRedoHandler undoRedoHandler;
	
    private int width; //number of cells of the width
    private int height; //number of cells of the height
	private int level = 1; //the level of the depth map the user is currently working on. The user starts on level 1
    private boolean isPointy = false;
    private int tileSize;
    
    public final PencilTool pencilTool;
    public final EraserTool eraserTool;
    public final DigTool digTool;
    public final LevelPickerTool levelPickerTool;
    public final FillTool fillTool;
    public final SelectionTool selectionTool;
    public final TerrainTool terrainTool;
    public final PointyTool pointyTool;
  
	public GridBoardUI(Grid grid, UndoRedoHandler undoRedoHandler, int tileSize) {
        this.gridData = grid;
        cellArray = new CellUI[grid.getWidth()][grid.getHeight()];
        this.tileSize = tileSize;
        for (int x = 0; x < grid.getWidth(); x++) {
        	for (int y = 0; y < grid.getHeight(); y++) { //creates the grid
                cellArray[x][y] = new CellUI(this, x, y, tileSize);
                cellArray[x][y].setLayoutX(x * tileSize); //spaces out the tiles based on TILE_SIZE
                cellArray[x][y].setLayoutY(y * tileSize);
                this.getChildren().add(cellArray[x][y]);
            }
        }
        
        //create the tools associated with the buttons and the editor
        this.pencilTool = new PencilTool(this, undoRedoHandler);
        this.eraserTool = new EraserTool(this, undoRedoHandler);
        this.digTool = new DigTool(this, undoRedoHandler);
        this.levelPickerTool = new LevelPickerTool(this, undoRedoHandler);
        this.fillTool = new FillTool(this, this.gridData, undoRedoHandler);
        this.selectionTool = new SelectionTool(this, undoRedoHandler);
        this.terrainTool = new TerrainTool(this, undoRedoHandler);
        this.pointyTool = new PointyTool(this, undoRedoHandler);
        
        //pencilTool is set to be the default tool
        this.gridEditor = new GridEditor(pencilTool); 
        
		this.setOnMousePressed(e -> gridEditor.mousePressed(e));
		this.setOnMouseReleased(e -> gridEditor.mouseReleased(e));
		this.setOnMouseDragged(e -> gridEditor.mouseDragged(e));
		
		this.width = gridData.getWidth();
		this.height = gridData.getHeight();
    }
    
	// ----------------- update visual based on each feature ----------------
	
	/**
	 * Overview updates the GridBoardUI to reflect the grid
	 */
    public void updateVisualBasedOnGrid() {
    	CellUI[][] newCellArray = new CellUI[gridData.getWidth()][gridData.getHeight()];
    	for (int x = 0; x < gridData.getWidth(); x++) {
        	for (int y = 0; y < gridData.getHeight(); y++) {
        		if (x >= width || y >= height) { //if grid board must add new cells
        			newCellArray[x][y] = new CellUI(this, x, y, tileSize);
                    newCellArray[x][y].setLayoutX(x * tileSize);
                    newCellArray[x][y].setLayoutY(y * tileSize);
                    this.getChildren().add(newCellArray[x][y]);
        		} else { //if contains cell, copy the cell to the new array
        			newCellArray[x][y] = cellArray[x][y];
        		}
        	}
    	}
    	
    	for (int x = 0; x < width; x++) {
        	for (int y = 0; y < height; y++) {
        		if (x >= gridData.getWidth() || y >= gridData.getHeight()) { //remove the cells if they are not in the new gridBoard size
        			this.getChildren().remove(cellArray[x][y]);
        		}
        	}
    	}
    	
    	this.cellArray = newCellArray;
    	this.width = gridData.getWidth();
    	this.height = gridData.getHeight();
    }
    
    public void updateVisual() {
    	for (int y = 0; y < gridData.getHeight(); y++) {
    		for (int x = 0; x < gridData.getWidth(); x++) {
            	cellArray[x][y].updateVisualBasedOnBlock();
            }
    	}
    }
    
    public void updateVisualDisplayLevel() {
    	for (int y = 0; y < gridData.getHeight(); y++) {
    		for (int x = 0; x < gridData.getWidth(); x++) {
            	cellArray[x][y].updateVisualDisplayLevel();
            }
    	}
    }
    
    public void updateVisualRemoveLevel() {
    	for (int y = 0; y < gridData.getHeight(); y++) {
    		for (int x = 0; x < gridData.getWidth(); x++) {
            	cellArray[x][y].updateVisualRemoveLevel();
            }
    	}
    }
    
    //---------------- Access the local references such as getters and setters -------------
    public int getTileSize() { 
		return tileSize;
	}
    
    public void setTileSize(int size) throws ArithmeticException { 
    	this.tileSize = size;
    	for (int y = 0; y < gridData.getHeight(); y++) { 
    		for (int x = 0; x < gridData.getWidth(); x++) {
               	cellArray[x][y].getColorRect().setWidth(size - 1);
               	cellArray[x][y].getColorRect().setHeight(size - 1);
            	cellArray[x][y].setLayoutX(x*size);
            	cellArray[x][y].setLayoutY(y*size);
            	updateVisual();
            }
    	}
	}
    
    public Grid getGridData() { //grid data represents the data of the GridUI
		return gridData;
	}
        
	/**
	 * Get the cell at a particular index position on grid
	 * 
	 * @param xIndex - the x index
	 * @param yIndex - the y index
	 * 
	 * @return the cell at that index position
	 */
    public CellUI getCell(int xIndex, int yIndex) throws IndexOutOfBoundsException {
        return cellArray[xIndex][yIndex];
    }
    
    
	/**
	 * This method returns cell at a particular pixel position on grid
	 * 
	 * @param x - the x coordinate
	 * @param y - the y coordinate
	 * 
	 * @return the cell at coordinate position
	 */
    public CellUI getCellAtPixelCoordinates(double x, double y) throws IndexOutOfBoundsException {
    	int xIndex = (int) (x / getTileSize());
    	int yIndex = (int) (y / getTileSize());
    	return cellArray[xIndex][yIndex];
    }
    
    public boolean isPointy() {
    	return this.isPointy;
    }
    
    public void setPointy(boolean pointy) {
    	this.isPointy = pointy;
    }
    
    public int getLevel() {
    	return this.level;
    }

    public void setLevel(int level) {
    	this.level = level;
    }
 
    public Boolean isEmpty() {
    	for (int y = 0; y < gridData.getHeight(); y++) {
    		for (int x = 0; x < gridData.getWidth(); x++) {
    			if(gridData.getBlockAt(x, y).getZ() != 0) {
    				return false;
				}
            }
    	}
    	return true;
    }
    
    //------------------ Some Features --------------------
    /**
     * Changes the level of all the selected cells to another level based on user's choices
     * @param level - pass in the level that the selected cells should turn to
     */
    public void setAllSelectedCellsToLevel(int level) {
    	Set<CellUI> selectedCells = selectionTool.getSelectedCells();
    	for(CellUI cell : selectedCells){
    		cell.setLevel(level);
    	}
    }
    
    /**
     * Selects the cells of a chosen level
     * @param isSelected - if the cells should be selected
     */
    public void selectLevel(boolean isSelected) { //Different from getLevel()
    	for (int y = 0; y < gridData.getHeight(); y++) {
    		for (int x = 0; x < gridData.getWidth(); x++) {
            	if ((cellArray[x][y]).getBlock().getZ() == level) {
            		if (isSelected) {
            			selectionTool.addSelectedCell(cellArray[x][y]);
            		} else {
            			selectionTool.removeSelectedCell(cellArray[x][y]);
            		}
            	}
            }
    	}
    }
    
    public void resize(int newWidth, int newHeight) {
    	gridData.resize(newWidth, newHeight);
    }
    
    public void clearAll() {
    	for (int y = 0; y < gridData.getHeight(); y++) {
    		for (int x = 0; x < gridData.getWidth(); x++) {
    			if(gridData.getBlockAt(x, y).getZ() != 0) {
    				gridData.getBlockAt(x, y).setZ(0);
    				cellArray[x][y].updateVisualBasedOnBlock();
				}
            }
    	}
    }

    public void generateMaze() {
    	Maze maze = new Maze(gridData);
    	maze.generateMaze();
    	updateVisual();
    }
}