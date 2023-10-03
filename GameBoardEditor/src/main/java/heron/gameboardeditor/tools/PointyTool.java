package heron.gameboardeditor.tools;

import heron.gameboardeditor.CellUI;
import heron.gameboardeditor.GridBoardUI;
import heron.gameboardeditor.UndoRedoHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Sets a tile to "pointy" which means it has a pyramid top in 3D
 */
public class PointyTool extends Tool {
	private GridBoardUI gridBoard;
	private CellUI cellLastClicked;
	
	public PointyTool(GridBoardUI gridBoard, UndoRedoHandler handler) {
		super(handler);
		this.gridBoard = gridBoard;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		CellUI cellClicked = gridBoard.getCell((int) e.getX() / gridBoard.getTileSize(), (int) e.getY() / gridBoard.getTileSize());
		
		handlePointy(cellClicked, e);
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		CellUI cellClicked = gridBoard.getCell((int) e.getX() / gridBoard.getTileSize(), (int) e.getY() / gridBoard.getTileSize());
		
		handlePointy(cellClicked, e);
		
		if (!cellClicked.equals(cellLastClicked)) { 
			handlePointy(cellClicked, e);
		} else { //if the mouse is still on the same cell, nothing should happen
			return;
		}
	}

	/**
	 * Handle if the user left or right clicked
	 * @param e - the MouseEvent
	 */
	public void handlePointy(CellUI cellClciked, MouseEvent e) {
		CellUI cellClicked = gridBoard.getCell((int) e.getX() / gridBoard.getTileSize(), (int) e.getY() / gridBoard.getTileSize());
		if (e.getButton().equals(MouseButton.SECONDARY)) { //if the user right clicks
			if (cellClicked.getBlock().isPointy()) {
				setNotPointy(e);
			}
			
		} else {
			if (!cellClicked.getBlock().isPointy()) {
				setPointy(e);
			}
			
		}
		cellLastClicked = cellClicked;
	}
	
	private void setPointy(MouseEvent e) {
		int x = (int) e.getX() / gridBoard.getTileSize();
		int y = (int) e.getY() / gridBoard.getTileSize();
		if (gridBoard.getGridData().isCoordinateInGrid(x, y)) {
			CellUI cellClicked = gridBoard.getCell(x, y);
			cellClicked.setPointy(true);
			cellClicked.updateVisualBasedOnBlock();
			undoRedoHandler.saveState();
		}
	}
	
	private void setNotPointy(MouseEvent e) {
		int x = (int) e.getX() / gridBoard.getTileSize();
		int y = (int) e.getY() / gridBoard.getTileSize();
		
		if (gridBoard.getGridData().isCoordinateInGrid(x, y)) {
			CellUI cellClicked = gridBoard.getCell(x, y);
			cellClicked.setPointy(false);
			cellClicked.updateVisualBasedOnBlock();
			undoRedoHandler.saveState();
		}
	}
}
