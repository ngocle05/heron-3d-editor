package heron.gameboardeditor.tools;

import java.util.HashSet; 
import java.util.Set;

import heron.gameboardeditor.CellUI;
import heron.gameboardeditor.GridBoardUI;
import heron.gameboardeditor.UndoRedoHandler;
import heron.gameboardeditor.datamodel.Block;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * The user can drag a selection rectangle or click to select tiles. They can then move these tiles by dragging
 */
public class SelectionTool extends Tool {
	private GridBoardUI gridBoard;
	Rectangle selectionRectangle;
	private double initialSelectX;
	private double initialSelectY;
	private Set<CellUI>selectedRegionOfCells = new HashSet<CellUI>();
	private boolean pressedInSelectedCell;

	public SelectionTool(GridBoardUI gridBoard, UndoRedoHandler handler) {
		super(handler);
		this.gridBoard = gridBoard;
		selectionRectangle = new Rectangle();
		selectionRectangle.setStroke(Color.BLACK);
		selectionRectangle.setFill(Color.TRANSPARENT);
		selectionRectangle.getStrokeDashArray().addAll(5.0, 5.0);
		pressedInSelectedCell = false;
		gridBoard.getChildren().add(selectionRectangle);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		
		initialSelectX = e.getX();
		initialSelectY = e.getY();
		
		selectionRectangle.setX(initialSelectX);
		selectionRectangle.setY(initialSelectY);
		selectionRectangle.setWidth(0);
		selectionRectangle.setHeight(0);
		selectionRectangle.setVisible(true);

		try {
			CellUI cellClicked = gridBoard.getCellAtPixelCoordinates(initialSelectX, initialSelectY);
			pressedInSelectedCell = (cellClicked.isSelected() == true);

			if (pressedInSelectedCell && !cellClicked.isSelected()) //if user clicks on the selected cells (the modified ones) but the cell doesn't get selected somehow (red)
				deselectAll();

			if (pressedInSelectedCell)
				cellClicked.setSelected(true);
		} catch (IndexOutOfBoundsException ex) {
			//ignore, because user just clicked outside of the grid
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!pressedInSelectedCell) { //&& gridBoard.getGridData().isCoordinateInGrid((int) e.getX() / CellUI.TILE_SIZE, (int) e.getY() / CellUI.TILE_SIZE)) {
			selectionRectangle.setX(Math.min(e.getX(), initialSelectX));
			selectionRectangle.setWidth(Math.abs(e.getX() - initialSelectX));
			selectionRectangle.setY(Math.min(e.getY(), initialSelectY));
			selectionRectangle.setHeight(Math.abs(e.getY() - initialSelectY));
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (pressedInSelectedCell) { //user releases after dragging the selected cells
			int xStartIndex = (int) (initialSelectX/ gridBoard.getTileSize()); //original x index when the cell initially gets pressed 
			int yStartIndex = (int) (initialSelectY/ gridBoard.getTileSize()); //original y index when the cell initially gets pressed 
			int xEndIndex = (int) (e.getX() / gridBoard.getTileSize());
			int yEndIndex = (int) (e.getY() / gridBoard.getTileSize());
			cutAndPaste(xStartIndex, yStartIndex, xEndIndex, yEndIndex); 
			deselectAll();
		} else {
			int xStartIndex = (int) selectionRectangle.getX() / gridBoard.getTileSize();
			int yStartIndex = (int) selectionRectangle.getY() / gridBoard.getTileSize();
			int xEndIndex = (int) (selectionRectangle.getX() + selectionRectangle.getWidth()) /  gridBoard.getTileSize();
			int yEndIndex = (int) (selectionRectangle.getY() + selectionRectangle.getHeight()) / gridBoard.getTileSize();
			for (int xIndex = xStartIndex; xIndex <= xEndIndex; xIndex++) {
				for (int yIndex = yStartIndex; yIndex <= yEndIndex; yIndex++) {
					if (gridBoard.getGridData().isCoordinateInGrid(xIndex, yIndex)) {
						if (gridBoard.getCell(xIndex, yIndex).getBlock().isVisible()) {
							selectedRegionOfCells.add(gridBoard.getCell(xIndex, yIndex));
							gridBoard.getCell(xIndex, yIndex).setSelected(true);
						}
					}
				}
			}
			selectionRectangle.setVisible(false);
			undoRedoHandler.saveState();
		}
	}
	
	/**
	 * Deselects all selected cells
	 */
	public void deselectAll() {
		for (CellUI cell: selectedRegionOfCells) {
			cell.setSelected(false);
    	}
    	selectedRegionOfCells.clear();
    }
	
	/**
	 * Returns the selected cells
	 * @return - ArrayList of selected cells
	 */
	public Set<CellUI> getSelectedCells() {
		return selectedRegionOfCells;
	}
	
	/**
	 * Removes the selected cells
	 * @param xIndex - the x index of the array which cell should be removed
	 * @param yIndex - the y index of the array which cell should be removed
	 */
	public void removeSelectedCell(int xIndex, int yIndex) {
		for (CellUI cell: selectedRegionOfCells) {
			if (cell.getBlock().getX() == xIndex && cell.getBlock().getY() == yIndex) {
				cell.setSelected(false);
				selectedRegionOfCells.remove(cell);
			}
		}
	}
	
	/**
	 * Adds a cell to the selected cells
	 * @param cell - CellUI cell to be selected
	 */
	public void addSelectedCell(CellUI cell) {
		cell.setSelected(true);
		selectedRegionOfCells.add(cell);
	}
	
	/**
	 * Removes a selected cell
	 * @param cell - CellUI selected cell to be removed
	 */
	public void removeSelectedCell(CellUI cell) {
		cell.setSelected(false);
		if (selectedRegionOfCells.contains(cell)) {
			selectedRegionOfCells.remove(cell);
		}
	}
	
	/**
	 * Moves the cells and cuts the cells which were there previously
	 * 
	 * @param startXIndex - int start x index
	 * @param startYIndex - int start y index
	 * @param endXIndex - int end x index
	 * @param endYIndex - int end y index
	 * @throws ArrayIndexOutOfBoundsException
	 */
    public void cutAndPaste(int startXIndex, int startYIndex, int endXIndex, int endYIndex) throws ArrayIndexOutOfBoundsException {
    	Set<Block> selectedBlocks = new HashSet<>();
    	
    	for (CellUI cell: selectedRegionOfCells) {
    		selectedBlocks.add(cell.getBlock());
    	} 
    	int changeInXIndex = endXIndex - startXIndex;
    	int changeInYIndex = endYIndex - startYIndex;
    	gridBoard.getGridData().cutAndPaste(selectedBlocks, changeInXIndex, changeInYIndex);
    	gridBoard.updateVisual();
    }
}
