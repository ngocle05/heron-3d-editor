package heron.gameboardeditor;

import java.io.File;    
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import heron.gameboardeditor.datamodel.Block;
import heron.gameboardeditor.datamodel.Grid;
import heron.gameboardeditor.datamodel.ProjectIO;
import heron.gameboardeditor.tools.TerrainTool.TerrainObject;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class EditingScreenController {
	
	@FXML 
    private AnchorPane mapDisplay;
    @FXML
    private TextField numRow;
    @FXML
    private TextField numColumn;
    @FXML
    private Slider levelSlider;
    @FXML
    private MenuButton terrainMenuButton;
	@FXML
	private CheckBox checkBoxDisplayLevel;
	@FXML
	private Button zoomInButton;
	@FXML
	private Button zoomOutButton;
	@FXML
	private Button undoButton;
	@FXML
	private Button redoButton;
	
    private BorderPane gridMapPane;
    private VBox boardParentVBox;
    private GridBoardUI gridBoard;
    private UndoRedoHandler undoRedoHandler;
    
    private ArrayList<TerrainObject> terrainObjects = new ArrayList<TerrainObject>();
    
    @FXML
    private void initialize() {
    	this.undoRedoHandler = new UndoRedoHandler(this);
    	refreshUIFromGrid();
    	this.terrainObjects = gridBoard.terrainTool.getTerrainObjects();
    }
    
    //------------------------ State Restored to make or re-make --------------------
    private void refreshUIFromGrid() {
    	//initially create a BorderPane to contain all the components of a grid board and set the preference size to fir the computer window 
        gridMapPane = new BorderPane();
        gridMapPane.setPrefSize(600, 800);
        
    	int tileSize = CellUI.DEFAULT_TILE_SIZE;
        if (gridBoard != null) {
        	tileSize = gridBoard.getTileSize();
        }
        
        //creates a viewer GridBoardUI, which is the grid the user can see
        gridBoard = new GridBoardUI(App.getGrid(), undoRedoHandler, tileSize); 
        
        boardParentVBox = new VBox(50, gridBoard); //creates a vbox with myBoard for children
        boardParentVBox.setAlignment(Pos.TOP_RIGHT);
        gridMapPane.setCenter(boardParentVBox);

        mapDisplay.getChildren().clear();
    	mapDisplay.getChildren().addAll(gridMapPane);

    }
    
    private void refreshSlider() {
    	levelSlider.setMax(gridBoard.getGridData().getMaxZ());
    }
    
    private void refreshTerrainMenu() {
    	terrainMenuButton.getItems().clear();
    	for (TerrainObject terrainObject : terrainObjects) {
        	MenuItem menuItem = new MenuItem(terrainObject.getName()); //https://www.geeksforgeeks.org/javafx-menubutton/
        	menuItem.setOnAction(e -> terrainTool(e));
    		terrainMenuButton.getItems().add(menuItem);
    	}
    	MenuItem customMenuItem = new MenuItem("Custom...");
    	customMenuItem.setOnAction(e -> terrainToolCustom(e));
    	terrainMenuButton.getItems().add(customMenuItem);
    }

    private ArrayList<TerrainObject> getTerrainObjects() {
    	return this.terrainObjects;
    }

    private void setTerrainObjects(ArrayList<TerrainObject> terrainObjects) {
    	this.terrainObjects = terrainObjects;
    	gridBoard.terrainTool.setTerrainObjects(this.terrainObjects);
    }
    
    @FXML
    /**
     * When the user clicks on Set Size button, this method get the number of rows and columns from the user's input 
     * and change the size of the board based them.
     * 
     */
    void setSizeAction(ActionEvent event) {
        int rows = -1;
        int columns = -1;
    	if (!numRow.getText().isBlank()) {
    		rows = Integer.parseInt(numRow.getText());
    	} 
    	
    	if (!numColumn.getText().isBlank()) {
    		columns = Integer.parseInt(numColumn.getText());
    		
    	} 
    	if (!numRow.getText().isBlank() && !numColumn.getText().isBlank()) {
        	App.resizeGrid(columns, rows);
        	gridBoard.updateVisualBasedOnGrid();
        	undoRedoHandler.saveState();
    	}

    }
    
    //--------------- Menu Event Handling ----------------------
    
    @FXML
    private void undoAction() {
    	undoRedoHandler.undo();
    }
   
    @FXML
    private void redoAction() {
    	undoRedoHandler.redo();
    }

    @FXML
    /**
     * When the user clicks on the zoom in button, the grid will be zoomed in.
     */
    void zoomIn() {
    	gridBoard.setTileSize(gridBoard.getTileSize() + 10);
    }
    
    @FXML
    /**
     * When the user clicks on the zoom in button, the grid will be zoomed out.
     */
    void zoomOut() {
    	if (gridBoard.getTileSize() > 10) {
    		gridBoard.setTileSize(gridBoard.getTileSize() - 10);
    	}
    }

    @FXML
    /**
     * When the user ticks on the show level box, the level of each block will be shown
     */
    void displayLevel() {
    	if (checkBoxDisplayLevel.isSelected()) {
    		gridBoard.updateVisualDisplayLevel();
    	} else {
    		gridBoard.updateVisualRemoveLevel();
    	}
    }
    
    @FXML
    void changeLevel(MouseEvent event) {
    	gridBoard.setLevel((int)levelSlider.getValue());

    	undoRedoHandler.saveState();

    	gridBoard.setAllSelectedCellsToLevel((int) levelSlider.getValue());
    }
    
    @FXML
    void pencilButtonOn(ActionEvent event) {
    	gridBoard.gridEditor.setCurrentTool(gridBoard.pencilTool);
    	undoRedoHandler.saveState();
    }
    
    @FXML
    void eraserButtonOn(ActionEvent event) {
    	gridBoard.gridEditor.setCurrentTool(gridBoard.eraserTool);
    	undoRedoHandler.saveState();
    }
    
    @FXML
    void digButtonOn(ActionEvent event) {
    	gridBoard.gridEditor.setCurrentTool(gridBoard.digTool);
    	undoRedoHandler.saveState();
    }
    
    @FXML
    void levelPickerOn(ActionEvent event) {
    	gridBoard.levelPickerTool.addSlider(levelSlider);
    	gridBoard.gridEditor.setCurrentTool(gridBoard.levelPickerTool);
    	levelSlider.setValue(gridBoard.getLevel());
    	undoRedoHandler.saveState();
    }
    
    @FXML
    void fillToolOn(ActionEvent event) {
    	gridBoard.gridEditor.setCurrentTool(gridBoard.fillTool);
    	undoRedoHandler.saveState();
    }
    
    @FXML
    void selectToolOn(ActionEvent event) {
    	gridBoard.gridEditor.setCurrentTool(gridBoard.selectionTool);
    	undoRedoHandler.saveState();
    }
    
    @FXML
    void terrainToolOn(ActionEvent event) {
    	gridBoard.terrainTool.setCurrentTerrainObject(null);
    	gridBoard.gridEditor.setCurrentTool(gridBoard.terrainTool);
    	undoRedoHandler.saveState();
    }
    
    @FXML
    void terrainTool(ActionEvent event) {
    	MenuItem item = (MenuItem) event.getSource();
    	gridBoard.terrainTool.setCurrentTerrainObject(item.getText());
    	gridBoard.gridEditor.setCurrentTool(gridBoard.terrainTool);
    	undoRedoHandler.saveState();
    }
    
    @FXML
    void terrainToolCustom(ActionEvent event) {
    	if (gridBoard.selectionTool.getSelectedCells().size() == 0) {
        	Alert errorAlert = new Alert(AlertType.ERROR);
        	errorAlert.setHeaderText("Error");
        	errorAlert.setContentText("Select the tiles you want in your custom Terrain Object!");
        	errorAlert.showAndWait();
        	return;
    	}
    	
    	TextInputDialog textInputDialog = new TextInputDialog(); //https://www.geeksforgeeks.org/javafx-textinputdialog/
    	textInputDialog.setHeaderText("Enter name of custom object: ");
    	textInputDialog.showAndWait();
    	String name = textInputDialog.getResult();
    	
    	if (!gridBoard.terrainTool.isValidName(textInputDialog.getResult(), terrainObjects)) {
    		return;
    	}
    	
    	MenuItem customItem = new MenuItem(name); //https://www.geeksforgeeks.org/javafx-menubutton/
    	customItem.setOnAction(e -> terrainTool(e));
    	terrainMenuButton.getItems().add(terrainMenuButton.getItems().size() - 1, customItem); //adds the custom item to the second to last of the list
    	gridBoard.terrainTool.createCustomTerrainObject(name);
    	terrainObjects = gridBoard.terrainTool.getTerrainObjects();
    	undoRedoHandler.saveState(); //save the custom Terrain objects
    }
    
    
    @FXML
    void pointyToolOn(ActionEvent event) {
    	gridBoard.gridEditor.setCurrentTool(gridBoard.pointyTool);
    	undoRedoHandler.saveState();
    }
    
    @FXML
    void selectLevel(ActionEvent event) {
    	gridBoard.selectLevel(true);
    }
    
    @FXML
    void deselectLevel(ActionEvent event) {
    	gridBoard.selectLevel(false);
    }
    
    @FXML
    void setMaxLevel(ActionEvent event) {
    	TextInputDialog textInputDialog = new TextInputDialog(); //may need to refactor and combine with terrain tool's text box
    	textInputDialog.setHeaderText("Enter number of possible levels to work on: ");
    	textInputDialog.showAndWait();
    	int newMaxLevel = Integer.parseInt(textInputDialog.getResult());
    	gridBoard.getGridData().setMaxZ(newMaxLevel);
    	
    	levelSlider.setMax(newMaxLevel);
    	if (newMaxLevel < gridBoard.getGridData().getMaxLevel()) {
    		gridBoard.getGridData().lowerBlocksHigherThan(newMaxLevel);
    	}
    	CellUI.generateColors();
    	gridBoard.updateVisual();
    }
    
    @FXML
    void generateMaze(ActionEvent event) {
    	gridBoard.generateMaze();
    	undoRedoHandler.saveState();
    }
    
    //--------------------------------Templates----------------------
    @FXML
    void templateOne(ActionEvent event) throws JsonSyntaxException, JsonIOException, IOException {
    	templetLoaderHelper("src/main/resources/heron/gameboardeditor/Templates/AugieLetter.heron");
    }
    
    @FXML
    void templateTwo(ActionEvent event) throws JsonSyntaxException, JsonIOException, IOException {
    	templetLoaderHelper("src/main/resources/heron/gameboardeditor/Templates/Heart.heron");
    }

    @FXML
    void templateThree(ActionEvent event) throws JsonSyntaxException, JsonIOException, IOException {
    	templetLoaderHelper("src/main/resources/heron/gameboardeditor/Templates/TalkTree.heron");
    }
    
    @FXML
    void templateFour(ActionEvent event) throws JsonSyntaxException, JsonIOException, IOException {
    	templetLoaderHelper("src/main/resources/heron/gameboardeditor/Templates/Duck.heron");
    }
    
    private void templetLoaderHelper(String path) throws JsonSyntaxException, JsonIOException, IOException {
    	clear();
    	File file = new File(path);
    	if (file != null) {
	    		Grid grid = ProjectIO.load(file);
	        	App.setGrid(grid);
	        	undoRedoHandler = new UndoRedoHandler(this);
	    		gridBoard = new GridBoardUI(grid, undoRedoHandler, CellUI.DEFAULT_TILE_SIZE);
		    	this.terrainObjects = gridBoard.terrainTool.getTerrainObjects();
		    	refreshTerrainMenu();
	    		boardParentVBox.getChildren().clear();
	    		boardParentVBox.getChildren().addAll(gridBoard);
    	}
    }
    
    //------------------------- File menu bar ----------------------
    @FXML
    void loadProject(ActionEvent event) {
    	File file = saveLoadHelper("open", "heron");
		if (file != null) {
			try {
				Grid grid = ProjectIO.load(file);
				App.setGrid(grid);
				undoRedoHandler = new UndoRedoHandler(this);
				gridBoard = new GridBoardUI(grid, undoRedoHandler, CellUI.DEFAULT_TILE_SIZE);
				boardParentVBox.getChildren().clear();
				boardParentVBox.getChildren().addAll(gridBoard);
		    	this.terrainObjects = gridBoard.terrainTool.getTerrainObjects();
		    	refreshTerrainMenu();
			} catch (FileNotFoundException ex) {
				new Alert(AlertType.ERROR, "The file you tried to open could not be found.").showAndWait();
			} catch (IOException ex) {
				new Alert(AlertType.ERROR, "Error opening file.  Did you choose a valid .heron file (which uses JSON format?)").show();
			}
		}
    }
    
    @FXML
    void saveProject() {
    	File file = saveLoadHelper("save", "heron");
    	if(file != null) {
    		Grid grid = App.getGrid();
    		try {
				ProjectIO.save(grid, file);
			} catch (IOException ex) {
	    		new Alert(AlertType.ERROR, "An I/O error occurred while trying to save this file.").showAndWait();			
			}
    	}
    }
    
    private File saveLoadHelper(String dialog, String fileType) {
    	File file;
    	FileChooser chooser = new FileChooser();
    	FileChooser.ExtensionFilter extention;
    	if(fileType == "heron" ) {
    		extention = new FileChooser.ExtensionFilter("Heron game (*.heron)", "*.heron");
    	} else {
    		extention = new FileChooser.ExtensionFilter("OBJ File (*.OBJ)", "*.OBJ");
    	}
    	chooser.getExtensionFilters().add(extention);
    	if(dialog == "open") {
    		file = chooser.showOpenDialog(App.getMainWindow());
    	} else {
    		file = chooser.showSaveDialog(App.getMainWindow());
    	}
    	return file;
    }
    
    @FXML
    void clear() {
    	if(!gridBoard.isEmpty()) {
        	Alert alert = new Alert(AlertType.WARNING, "Do you want to save your work?", ButtonType.YES, ButtonType.NO);
        	Optional<ButtonType> result = alert.showAndWait();
        	if (result.get() == ButtonType.YES) {
        		saveProject();
        		gridBoard.clearAll();
        	} else {
        		gridBoard.clearAll();
        	}
    	}
    	undoRedoHandler.saveState();
    }
    
    @FXML
    void exitTheSceen(ActionEvent event) {
    	Alert alert = new Alert(AlertType.WARNING, "Are you sure you want to quit?", ButtonType.YES, ButtonType.NO);
    	
    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.YES) {
    		Platform.exit();
    	}
    }
    
    //------------------------- 3D menu bar ------------------------- 
    @FXML
    void show3DPreview(ActionEvent event) {
    	Board3DViewController preview3D = new Board3DViewController(gridBoard.getGridData());
    	preview3D.show();
    }
    

    @FXML
    private void switchToTemplateScreen(ActionEvent event) throws IOException {
    	App.setRoot("templateScreen");
    }
    
    @FXML
    void showHelpScreen(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("helpScreen.fxml"));
        VBox root = fxmlLoader.load();    
        Scene helpScene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
        Stage stage = new Stage();
        helpScene.setRoot(root);
        stage.setScene(helpScene);
        stage.show();
    }
    
    @FXML
    void exportToObj() throws IOException {
    	File file = saveLoadHelper("save", "OBJ");
    	if(file != null) {
    		Grid grid = App.getGrid();
    		FileWriter writer = new FileWriter(file);
    		for (int x = 0; x < grid.getWidth(); x++) {
    			for (int y = 0; y < grid.getHeight(); y++) {
    				Block blocks = grid.getBlockAt(x, y);
    				int r = blocks.getY();
    				int c = blocks.getX();
    				int e = blocks.getZ();
    				writer.write("v " + c + " " + r + " " + e + "\n");
    				writer.write("v " + c + " " + r + " " + 0 + "\n");
    				writer.write("v " + c + " " + (r + 1) + " " + 0 + "\n");
    				writer.write("v " + c + " " + (r + 1) + " " + e + "\n");
    				writer.write("v " + (c + 1) + " " + r + " " + e + "\n");
    				writer.write("v " + (c + 1) + " " + r + " " + 0 + "\n");
    				writer.write("v " + (c + 1) + " " + (r + 1) + " " + 0 + "\n");
    				writer.write("v " + (c + 1) + " " + (r + 1) + " " + e + "\n");
    				if(grid.getBlockAt(x, y).isPointy()) {
        				writer.write("v " + (c + 0.5) + " " + (r + 0.5) + " " + (e + 1) + "\n");
    				}

    			}
    		}
    		
    		int i = 0;
    		for (int x = 0; x < grid.getWidth(); x++) {
    			for (int y = 0; y < grid.getHeight(); y++) {
    				writer.write("f " + (i + 4) + " " + (i + 3) + " " + (i + 2) + " " + (i + 1) + "\n");
        			writer.write("f " + (i + 2) + " " + (i + 6) + " " + (i + 5) + " " + (i + 1) + "\n");
        			writer.write("f " + (i + 3) + " " + (i + 7) + " " + (i + 6) + " " + (i + 2) + "\n");
        			writer.write("f " + (i + 8) + " " + (i + 7) + " " + (i + 3) + " " + (i + 4) + "\n");
        			writer.write("f " + (i + 5) + " " + (i + 8) + " " + (i + 4) + " " + (i + 1) + "\n");
        			writer.write("f " + (i + 6) + " " + (i + 7) + " " + (i + 8) + " " + (i + 5) + "\n");
        			if(grid.getBlockAt(x, y).isPointy()) {
            			writer.write("f " + (i + 9) + " " + (i + 1) + " " + (i + 4) + "\n");
            			writer.write("f " + (i + 9) + " " + (i + 4) + " " + (i + 8) + "\n");
            			writer.write("f " + (i + 9) + " " + (i + 8) + " " + (i + 5) + "\n");
            			writer.write("f " + (i + 9) + " " + (i + 5) + " " + (i + 1) + "\n");
            			i = i + 9;
        			} else {
        				i += 8;
        			}
    			}
    		}
    		writer.close();
    	}
    }
    
    
    public class State {
    	private Grid grid;
    	private ArrayList<TerrainObject> terrainObjects;
    	
    	public State() {
    		grid = (Grid) App.getGrid().clone();
    		terrainObjects = getTerrainObjects();
    	}
    	
    	public void restore() {
    		App.setGrid(grid.clone());
    		refreshUIFromGrid();
    		refreshSlider();
    		setTerrainObjects(terrainObjects); //set the terrainObjects to what was saved
    		refreshTerrainMenu(); //updates the menu for the terrainTool
    	}
    }

	public State createMemento() {
		return new State();
	}

	public void restoreState(State gridBoardState) {
		gridBoardState.restore();
		
	}
}
