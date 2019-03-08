module pathfinder.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires pathfinder.utility;
    requires pathfinder.world;
    requires opencv;

    opens net.alexhyisen.pathfinder.gui to javafx.fxml;
    exports net.alexhyisen.pathfinder.gui;
}
