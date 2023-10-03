package heron.gameboardeditor;

import java.util.Stack;

public class UndoRedoHandler {
	private Stack<EditingScreenController.State> undoStack, redoStack;
	// invariant: The top state of the undoStack always is a copy of the
	// current state of the canvas.
	private EditingScreenController controller;

	/**
	 * constructor
	 * 
	 * @param controller -  the EditingScreenController whose changes are saved for later
	 *               restoration.
	 */
	public UndoRedoHandler(EditingScreenController controller) {
		undoStack = new Stack<EditingScreenController.State>();
		redoStack = new Stack<EditingScreenController.State>();
		this.controller = controller;
		// store the initial state of the canvas on the undo stack
		undoStack.push(controller.createMemento());
	}

	/**
	 * saves the current state of the editing screen controller for later restoration
	 */
	public void saveState() {
		EditingScreenController.State canvasState = controller.createMemento();
		undoStack.push(canvasState);
		redoStack.clear();
	}

	/**
	 * restores the state of the editing screen controller to what it was before the last
	 * change. Nothing happens if there is no previous state (for example, when the
	 * application first starts up or when you've already undone all actions since
	 * the startup state).
	 */
	public void undo() {
		if (undoStack.size() == 1) // only the current state is on the stack
			return;

		EditingScreenController.State canvasState = undoStack.pop();
		redoStack.push(canvasState);
		controller.restoreState(undoStack.peek());
	}

	/**
	 * restores the state of the editing screen controller to what it was before the last undo
	 * action was performed. If some change was made to the state of the controller
	 * since the last undo, then this method does nothing.
	 */
	public void redo() {
		if (redoStack.isEmpty())
			return;

		EditingScreenController.State canvasState = redoStack.pop();
		undoStack.push(canvasState);
		controller.restoreState(canvasState);
	}
}
