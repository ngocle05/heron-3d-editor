module heron.gameboardeditor {
    requires javafx.controls;
    requires javafx.fxml;
	requires javafx.graphics;
	requires com.google.gson;
	requires javafx.base;
	requires java.base;

    opens heron.gameboardeditor to javafx.fxml;
    opens heron.gameboardeditor.datamodel to com.google.gson;
    exports heron.gameboardeditor;
}
