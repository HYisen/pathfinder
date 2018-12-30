package net.alexhyisen.pathfinder.gui;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;

public class MainController {
    @FXML
    private Button nextButton;
    @FXML
    private Canvas canvas;
    @FXML
    private ImageView imageView0;
    @FXML
    private ImageView imageView1;
    @FXML
    private Slider slider;
    @FXML
    private ToggleButton toggleButton;

    @FXML
    private ChoiceBox<String> modeChoiceBox;

    @FXML
    private Label slamProgressLabel;

    @FXML
    private ProgressBar slamProgressBar;

    @FXML
    private ToggleButton slamToggleButton;

    @FXML
    private Label meshProgressLabel;

    @FXML
    private ProgressBar meshProgressBar;

    @FXML
    private ToggleButton meshToggleButton;

    @FXML
    private VBox paramVBox;

    @FXML
    private ToggleButton viewToggleButton;

    @FXML
    private ToggleButton playToggleButton;

    private int count = 0;
    private Image image0;
    private Image image1;

    private ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture sf;

    private Spinner<Integer> frameSpinner;

    @FXML
    protected void onButtonAction() {
        System.out.println("BANG!");
        var gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.CYAN);
        gc.fillRect(100, 50, 50, 100);
    }

    @FXML
    protected void onNextButtonAction() {
        slider.setValue(slider.getValue() + 1);
    }

    private void showImage(int id) {
        String path0 = "file:///C:\\code\\dataset\\sequences\\00\\image_0\\";
        String path1 = "file:///C:\\code\\dataset\\sequences\\00\\image_1\\";
        String file = String.format("%06d.png", id);
        System.out.println("showImage " + id);
        slider.setValue(id);
        image0 = new Image(path0 + file);
        image1 = new Image(path1 + file);
        imageView0.setImage(image0);
        imageView1.setImage(image1);
    }

    private DummyProgress slamDP = new DummyProgress(1200, "SLAM", ses, this::updateSlamProgress);
    private DummyProgress meshDP = new DummyProgress(1000, "Mesh", ses, this::updateMeshProgress);

    private static Spinner<Integer> genParamLine(Pane root, int min, int max, String name) {
        var label = new Label(name);
        var slider = new Slider();
        var spinner = new Spinner<Integer>();

        label.setPrefWidth(150);
        label.setAlignment(Pos.CENTER);

        SimpleObjectProperty<Integer> property = new SimpleObjectProperty<>(min);
        IntegerProperty param = SimpleIntegerProperty.integerProperty(property);
        slider.setMin(min);
        slider.setMax(max);
        slider.setBlockIncrement(1);
        slider.valueProperty().bindBidirectional(param);
        slider.valueProperty().addListener(((observable, oldValue, newValue) -> param.setValue(newValue)));
        slider.setPrefWidth(250);

        spinner.setEditable(true);
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max));
        spinner.getValueFactory().valueProperty().bindBidirectional(property);
        spinner.valueProperty().addListener((observable, oldValue, newValue) -> param.setValue(newValue));
        spinner.setPrefWidth(100);

        param.setValue(min);

        var line = new HBox();
        line.setSpacing(10);
        line.getChildren().addAll(label, slider, spinner);

        root.getChildren().add(line);
        return spinner;
    }

    private static void updateProgress(int current, int total, Label label, ProgressBar progressBar) {
        label.setText(String.format("%5d/%5d", current, total));
        progressBar.setProgress((double) current / total);
    }

    @FXML
    public void initialize() {
//        slider.setBlockIncrement(1);
//        slider.setMin(0);
//        slider.setMax(4540);
//        slider.valueProperty().addListener(v -> showImage((int) slider.getValue()));
//        slider.setValue(0);

        modeChoiceBox.setItems(FXCollections.observableArrayList("Points", "Surfaces"));

        paramVBox.setSpacing(10);
        genParamLine(paramVBox, 0, 1000, "Position x");
        genParamLine(paramVBox, 0, 1000, "Position y");
        genParamLine(paramVBox, 0, 1000, "Position z");
        paramVBox.getChildren().add(new Separator());
        genParamLine(paramVBox, 1, 100, "Orientation i");
        genParamLine(paramVBox, 1, 100, "Orientation j");
        genParamLine(paramVBox, 1, 100, "Orientation k");
        paramVBox.getChildren().add(new Separator());
        frameSpinner = genParamLine(paramVBox, 0, 1000, "Frame Number");
    }

    private void updateSlamProgress(int current) {
        updateProgress(current, 1200, slamProgressLabel, slamProgressBar);
    }

    private void updateMeshProgress(int current) {
        updateProgress(current, 1000, meshProgressLabel, meshProgressBar);
    }

    @FXML
    public void handlePlayToggleButtonAction() {
        if (playToggleButton.isSelected()) {
            sf = ses.scheduleAtFixedRate(() -> {
                frameSpinner.getValueFactory().increment(1);
            }, 100, 100, TimeUnit.MILLISECONDS);
        } else {
            sf.cancel(false);
        }
    }

    @FXML
    public void handleSlamToggleButtonAction() {
        slamDP.action(slamToggleButton.isSelected());
    }

    @FXML
    public void handleMeshToggleButtonAction() {
        meshDP.action(meshToggleButton.isSelected());
    }

    private class DummyProgress {
        private int num = 0;
        private int max;
        private String name;
        private ScheduledExecutorService ses;
        private ScheduledFuture sf;
        private IntConsumer handler;

        public DummyProgress(int max, String name, ScheduledExecutorService ses, IntConsumer handler) {
            this.max = max;
            this.name = name;
            this.ses = ses;
            this.handler = handler;
        }

        public void action(boolean isSelected) {
            if (isSelected) {
                System.out.println(name + " play");
                sf = ses.scheduleAtFixedRate(() -> {
                    if (++num <= max) {
                        Platform.runLater(() -> handler.accept(num));
                        System.out.println(name + num);
                    }
                }, 100, 100, TimeUnit.MILLISECONDS);
            } else {
                System.out.println(name + " stop");
                sf.cancel(false);
            }
        }
    }

}
