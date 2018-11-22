module pathfinder.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires pathfinder.utility;
    requires opencv;

    opens net.alexhyisen.pathfinder.gui to javafx.fxml;
    exports net.alexhyisen.pathfinder.gui;
}
