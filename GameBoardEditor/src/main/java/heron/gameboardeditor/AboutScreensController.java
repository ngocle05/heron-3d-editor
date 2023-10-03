package heron.gameboardeditor;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class AboutScreensController {
	
	@FXML
    private TextArea credits;

	@FXML
    void switchToWelcomeScreen(ActionEvent event) throws IOException {
		App.setRoot("welcomeScreen");
    }
	
	@FXML
    void initialize() {
		credits.setEditable(false);
    }
	
}