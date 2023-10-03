package heron.gameboardeditor.tools;

import heron.gameboardeditor.CellUI;
import heron.gameboardeditor.GridBoardUI;
import heron.gameboardeditor.UndoRedoHandler;
import javafx.scene.input.MouseEvent;

/**
 * Erases a tile the user clicks on
 */
public class EraserTool extends Tool {
	private GridBoardUI gridBoard;
	
	public EraserTool(GridBoardUI gridBoard, UndoRedoHandler handler) {
		super(handler);
		this.gridBoard = gridBoard;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		erase(e);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		erase(e);
	}
	
	/**
	 * Sets the CellUI to level 0
	 * @param e
	 */
	private void erase(MouseEvent e) {
		int x = (int) e.getX() / gridBoard.getTileSize();
		int y = (int) e.getY() / gridBoard.getTileSize();
		if (gridBoard.getGridData().isCoordinateInGrid(x, y)) {
			CellUI cellClicked = gridBoard.getCell(x, y);
			if (cellClicked.getLevel() > 0) {
				cellClicked.setLevel(0);
				cellClicked.setSelected(false);
				gridBoard.selectionTool.getSelectedCells().remove(cellClicked);
				undoRedoHandler.saveState();
			}
		}

	}
}
