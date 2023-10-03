package heron.gameboardeditor.datamodel;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class GridTest {

	@Test
	void testCutAndPaste() {
		Grid grid = new Grid(5,4);
		for (int x = 0; x < grid.getWidth(); x++) {
			for (int y = 0; y < grid.getHeight(); y++) {
				grid.getBlockAt(x, y).setZ(x);
			}
		}
		grid.printGrid();

		Set<Block> selectedBlocks = new HashSet<>();
		selectedBlocks.add(grid.getBlockAt(0, 1));
		selectedBlocks.add(grid.getBlockAt(0, 2));
		grid.cutAndPaste(selectedBlocks, 1, 0);
//		grid.printGrid();
		assertEquals(grid.toString(), "Grid 5x4\n"
				+ " 0 1 2 3 4\n"
				+ " 0 0 2 3 4\n"
				+ " 0 0 2 3 4\n"
				+ " 0 1 2 3 4\n");
		
		Set<Block> selectedBlocks2 = new HashSet<>();
		selectedBlocks2.add(grid.getBlockAt(1, 1));
		selectedBlocks2.add(grid.getBlockAt(1, 2));
		grid.cutAndPaste(selectedBlocks2, 3, -1);
//		grid.printGrid();
		assertEquals(grid.toString(), "Grid 5x4\n"
				+ " 0 1 2 3 0\n"
				+ " 0 0 2 3 0\n"
				+ " 0 0 2 3 4\n"
				+ " 0 1 2 3 4\n");
		
		
	}
	
	@Test
	void testIsCoordinateInGrid() {
		Grid grid = new Grid(5, 5);
		assertEquals(grid.isCoordinateInGrid(1, 2), true);
		assertEquals(grid.isCoordinateInGrid(0, 0), true);
		assertEquals(grid.isCoordinateInGrid(0, 1), true);
		assertEquals(grid.isCoordinateInGrid(-1, 2), false);
		assertEquals(grid.isCoordinateInGrid(0, -1), false);
		assertEquals(grid.isCoordinateInGrid(5, 5), false);
		assertEquals(grid.isCoordinateInGrid(4, 4), true);
	}

}
