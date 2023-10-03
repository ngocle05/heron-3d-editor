package heron.gameboardeditor.tools;

import java.util.ArrayList; 
import java.util.Set;

import heron.gameboardeditor.CellUI;
import heron.gameboardeditor.GridBoardUI;
import heron.gameboardeditor.UndoRedoHandler;
import heron.gameboardeditor.datamodel.Block;
import heron.gameboardeditor.datamodel.Grid;
import heron.gameboardeditor.tools.TerrainTool.TerrainObject;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;

/**
 * Allows the user to add large scale terrain objects or create custom ones to use
 */
public class TerrainTool extends Tool {
	private GridBoardUI gridBoard;
	private TerrainObject terrainObject;
	private Grid gridData;
	
	private TerrainObject defaultMountain;
	private TerrainObject defaultVolcano;
	
	private ArrayList<TerrainObject> terrainObjects; //list of available terrain objects
	
	private String name; //the name of the next created terrain object
	
	public TerrainTool(GridBoardUI gridBoard, UndoRedoHandler handler) {
		super(handler);
		this.gridBoard = gridBoard;
		this.gridData = gridBoard.getGridData();
		
		defaultMountain = createMountain();
		defaultVolcano = createVolcano();
		
		terrainObjects = new ArrayList<TerrainObject>();
		terrainObjects.add(defaultMountain);
		terrainObjects.add(defaultVolcano);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		CellUI cellClicked = gridBoard.getCell((int) e.getX() / gridBoard.getTileSize(), (int) e.getY() /  gridBoard.getTileSize());
		Block initialBlock = cellClicked.getBlock();
		drawTerrainObject(terrainObject, initialBlock);
		gridBoard.updateVisual();
    	undoRedoHandler.saveState();
	}
	
	/**
	 * Sets the name of the terrainObject
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the name of the terrainObject
	 * @return - string the name of the terrainObject
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the current terrain object
	 * @param terrainObjectString - string the name of the terrainObject
	 */
	public void setCurrentTerrainObject(String terrainObjectString) {
		for (TerrainObject terrainObject : terrainObjects) {
			if (terrainObject.name.equals(terrainObjectString)) {
				this.terrainObject = terrainObject;
			}
		}
	}
	
	/**
	 * Returns whether or not the name is valid for the terrainObject
	 * @param name - the name to check
	 * @param terrainObjects - ArrayList of the terrainObjects which the user has available
	 * @return whether or not the name is valid for the terrainObject
	 */
	public boolean isValidName(String name, ArrayList<TerrainObject> terrainObjects) {
		if (name == null || name.isBlank()) {
    		return false;
    	}
		
		boolean isValidName = true;
    	for (TerrainObject terrainObject : terrainObjects) {
    		if (terrainObject.getName().equalsIgnoreCase(name)) {
    			isValidName = false;
    		}
    	}
    	if (!isValidName) {
        	Alert errorAlert = new Alert(AlertType.ERROR);
        	errorAlert.setHeaderText("Error");
        	errorAlert.setContentText("You already have a custom Terrain Object with the same name!");
        	errorAlert.showAndWait();
    	}
    	return isValidName;
	}
	
	/**
	 * Creates a custom terrain object
	 * @param name - String the name of the terrainObject
	 */
	public void createCustomTerrainObject(String name) {
		Set<CellUI> selectedCells = gridBoard.selectionTool.getSelectedCells(); //the blocks which are part of the custom terrain object
		Set<Block> terrainBlocks = gridData.getSelectedBlocks(selectedCells);
		Block initialBlock = new Block(Integer.MAX_VALUE, Integer.MAX_VALUE, 0); //initial block represents the first block of the object
		for (Block block : terrainBlocks) { //finds a block on the upper left of the grid, with more importance on being further left
			int x = block.getX();
			int y = block.getY();
			if (x < initialBlock.getX() || (x == initialBlock.getX() && y < initialBlock.getY())) {
				initialBlock = block;
			}
		}
		
		ArrayList<TerrainData> customTerrainData = new ArrayList<TerrainData>();
		terrainBlocks.remove(initialBlock);
		for (Block block : terrainBlocks) {
			int distX = block.getX() - initialBlock.getX();
			int distY = block.getY() - initialBlock.getY();
			customTerrainData.add(new TerrainData(distX, distY, block.getZ()));
		}
		
		TerrainObject customTerrainObject = new TerrainObject(name, customTerrainData, initialBlock.getZ());
		terrainObjects.add(customTerrainObject);
	}
	
	/**
	 * Sets the terrainObjects
	 * @param terrainObjects - ArrayList of terrainobjects
	 */
	public void setTerrainObjects(ArrayList<TerrainObject> terrainObjects) {
		this.terrainObjects = terrainObjects;
	}
	
	/**
	 * Returns the terrainObjects
	 * @return ArrayList of terrainObjects
	 */
	public ArrayList<TerrainObject> getTerrainObjects() {
		return this.terrainObjects;
	}
	
	private void drawTerrainObject(TerrainObject terrainObject, Block initialBlock) {
		
		initialBlock.setZ(setZ(terrainObject.initialTerrainData));
		for (TerrainData terrainData : terrainObject.terrainList) {
			int x = initialBlock.getX() + terrainData.distanceX;
			 int y = initialBlock.getY() + terrainData.distanceY;
			 int z = setZ(terrainData);
			 if (gridData.isCoordinateInGrid(x, y)) {
				 Block block = gridData.getBlockAt(x, y);
				 block.setZ(z);
			 }
		}
	}
	
	private int setZ(TerrainData terrainData) {
		int z = terrainData.level;
		if (terrainData.level > gridData.getMaxZ()) {
			 z = gridData.getMaxLevel();
		 }
		return z;
	}
	
	private TerrainObject createMountain() {
		ArrayList<TerrainData> mountainList = new ArrayList<TerrainData>();
		TerrainData initialTerrainData = new TerrainData(0, 0, 5); //top of the mountian
		mountainList = buildMountain();
		
		TerrainObject mountain = new TerrainObject("Mountain", mountainList, initialTerrainData);
		return mountain;
	}
	
	private ArrayList<TerrainData> buildMountain() {
		ArrayList<TerrainData> mountain = new ArrayList<TerrainData>();
		//first part of mountain
		mountain.add(new TerrainData(0, 1, 4));
		mountain.add(new TerrainData(1, 1, 4));
		mountain.add(new TerrainData(1, 0, 4));
		mountain.add(new TerrainData(1, -1, 4));
		mountain.add(new TerrainData(0, -1, 4));
		mountain.add(new TerrainData(-1, -1, 4));
		mountain.add(new TerrainData(-1, 0, 4));
		mountain.add(new TerrainData(-1, 1, 4));
		
		//second part of mountain
		mountain.add(new TerrainData(0, 2, 3));
		mountain.add(new TerrainData(1, 2, 3));
		mountain.add(new TerrainData(2, 2, 3));
		mountain.add(new TerrainData(2, 1, 3));
		mountain.add(new TerrainData(2, 0, 3));
		mountain.add(new TerrainData(2, -1, 3));
		mountain.add(new TerrainData(2, -2, 3));
		mountain.add(new TerrainData(1, -2, 3));
		mountain.add(new TerrainData(0, -2, 3));
		mountain.add(new TerrainData(-1, -2, 3));
		mountain.add(new TerrainData(-2, -2, 3));
		mountain.add(new TerrainData(-2, -1, 3));
		mountain.add(new TerrainData(-2, 0, 3));
		mountain.add(new TerrainData(-2, 1, 3));
		mountain.add(new TerrainData(-2, 2, 3));
		mountain.add(new TerrainData(-1, 2, 3));
		
		return mountain;
	}
	
	private TerrainObject createVolcano() {
		ArrayList<TerrainData> volcanoList = new ArrayList<TerrainData>();
		TerrainData initialTerrainData = new TerrainData(0, 0, 1); //top of the volcano
		volcanoList = buildMountain();
		TerrainObject volcano = new TerrainObject("Volcano", volcanoList, initialTerrainData);
		return volcano;
	}
	
	/**
	 * Represents a TerrainObject. This is data which decides how the terrain should be drawn
	 *
	 */
	public class TerrainObject {
		private ArrayList<TerrainData> terrainList;
		private TerrainData initialTerrainData;
		private String name;
		
		private TerrainObject(String name, ArrayList<TerrainData> terrainList, TerrainData initialTerrainData) {
			this.name = name;
			this.terrainList = terrainList;
			this.initialTerrainData = initialTerrainData;
		}
		
		private TerrainObject(String name, ArrayList<TerrainData> terrainList, int initialTerrainDataLevel) {
			this.name = name;
			this.terrainList = terrainList;
			this.initialTerrainData = new TerrainData(0, 0, initialTerrainDataLevel);
		}
		
		public String getName() {
			return this.name;
		}
	}
	
	/**
	 * Represents TerrainData. TerrainData makes up a TerrainObject
	 */
	private class TerrainData {
		int distanceX;
		int distanceY;
		int level;
		
		private TerrainData(int distanceX, int distanceY, int level) {
			this.distanceX = distanceX;
			this.distanceY = distanceY;
			this.level = level;
		}
	}
}
