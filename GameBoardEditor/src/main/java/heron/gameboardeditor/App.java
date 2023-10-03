package heron.gameboardeditor;

import javafx.application.Application; 
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import heron.gameboardeditor.datamodel.Grid;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Stage mainWindow;
    private static Grid gridData;//creates the data for the grid
    
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("welcomeScreen"), 1280, 720);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
        mainWindow = stage;
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }
    
    static Scene getScene() throws IOException {
    	return scene;
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    
    public static void main(String[] args) {
        launch();
    }
    
    /**
     * This method will create a new grid based on the number of rows and columns
     * 
	 * @param newWidth - the number of columns of the new grid
	 * @param neHeight - the number of rows of the new grid
     */
    public static void useNewGrid(int columns, int rows) {
    	gridData = new Grid(columns, rows);
    }
    
    /**
     * This method will expand or crop the gridData based on the number of rows and columns the user inputs
     * 
	 * @param newWidth - the number of columns of the new grid
	 * @param newHeight - the number of rows of the new grid
     */
    public static void resizeGrid(int newWidth, int newHeight) {
    	gridData.resize(newWidth, newHeight);
    }
    
    
    /**
     * This method returns the gridData
     * 
	 * @return gridData - the grid data
     */
    public static Grid getGrid() {
    	return gridData;
    }
        
    public static Stage getMainWindow() {
    	return mainWindow;
    }
    
    public static void setGrid(Grid newGrid) {
    	gridData = newGrid;
    }

}
