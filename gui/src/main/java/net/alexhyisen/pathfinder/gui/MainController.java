package net.alexhyisen.pathfinder.gui;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.alexhyisen.pathfinder.world.World;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.function.IntConsumer;

public class MainController {
//    @FXML
//    private Button nextButton;
//    @FXML
//    private Canvas canvas;
//    @FXML
//    private ImageView imageView0;
//    @FXML
//    private ImageView imageView1;
//    @FXML
//    private Slider slider;
//    @FXML
//    private ToggleButton toggleButton;

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

//    private int count = 0;
//    private Image image0;
//    private Image image1;

    private ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture sf;

    private float[] oldCameraInfo;
    private DoubleProperty[] localCameraInfo = new DoubleProperty[6];

    private World world;

    private ExecutorService es = Executors.newCachedThreadPool();
    private Future wf;

    private Spinner<Integer> frameSpinner;

//    @FXML
//    protected void onButtonAction() {
//        System.out.println("BANG!");
//        var gc = canvas.getGraphicsContext2D();
//        gc.setFill(Color.BLACK);
//        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
//        gc.setFill(Color.CYAN);
//        gc.fillRect(100, 50, 50, 100);
//    }
//
//    @FXML
//    protected void onNextButtonAction() {
//        slider.setValue(slider.getValue() + 1);
//    }
//
//    private void showImage(int id) {
//        String path0 = "file:///C:\\code\\dataset\\sequences\\00\\image_0\\";
//        String path1 = "file:///C:\\code\\dataset\\sequences\\00\\image_1\\";
//        String file = String.format("%06d.png", id);
//        System.out.println("showImage " + id);
//        slider.setValue(id);
//        image0 = new Image(path0 + file);
//        image1 = new Image(path1 + file);
//        imageView0.setImage(image0);
//        imageView1.setImage(image1);
//    }

    private DummyProgress slamDP = new DummyProgress(1200, "SLAM", ses, this::updateSlamProgress);
    private DummyProgress meshDP = new DummyProgress(1000, "Mesh", ses, this::updateMeshProgress);

    private static DoubleProperty genParamLine(Pane root, double min, double max, String name) {
        var label = new Label(name);
        var slider = new Slider();
        var spinner = new Spinner<Double>();

        label.setPrefWidth(150);
        label.setAlignment(Pos.CENTER);

        SimpleObjectProperty<Double> property = new SimpleObjectProperty<>(min);
        DoubleProperty param = SimpleDoubleProperty.doubleProperty(property);
        slider.setMin(min);
        slider.setMax(max);
        slider.setBlockIncrement(0.01);
        slider.valueProperty().bindBidirectional(param);
        slider.valueProperty().addListener(((observable, oldValue, newValue) -> param.setValue(newValue)));
        slider.setPrefWidth(250);

        spinner.setEditable(true);
        final var spinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max);
        spinnerValueFactory.setAmountToStepBy(0.01);
        spinner.setValueFactory(spinnerValueFactory);
        spinner.getValueFactory().valueProperty().bindBidirectional(property);
        spinner.valueProperty().addListener((observable, oldValue, newValue) -> param.setValue(newValue));
        spinner.setPrefWidth(100);

        param.setValue((min + max) / 2.0);

        var line = new HBox();
        line.setSpacing(10);
        line.getChildren().addAll(label, slider, spinner);

        root.getChildren().add(line);
        return param;
    }

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
        localCameraInfo[0] = genParamLine(paramVBox, -10.0, 10.0, "Position x");
        localCameraInfo[1] = genParamLine(paramVBox, -10.0, 10.0, "Position y");
        localCameraInfo[2] = genParamLine(paramVBox, -10.0, 10.0, "Position z");
        paramVBox.getChildren().add(new Separator());
        localCameraInfo[3] = genParamLine(paramVBox, -1.0, 1.0, "Orientation i");
        localCameraInfo[4] = genParamLine(paramVBox, -1.0, 1.0, "Orientation j");
        localCameraInfo[5] = genParamLine(paramVBox, -1.0, 1.0, "Orientation k");
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
            sf = ses.scheduleAtFixedRate(
                    () -> frameSpinner.getValueFactory().increment(1),
                    100,
                    100,
                    TimeUnit.MILLISECONDS
            );
        } else {
            sf.cancel(false);
        }
    }

    @FXML
    public void handleViewToggleButtonAction() {
        if (viewToggleButton.isSelected()) {
            world = new World();
            wf = es.submit(() -> world.open(false));
            if (sf != null && !sf.isDone()) {
                sf.cancel(false);
            }
            sf = ses.scheduleWithFixedDelay(this::updateCameraInfo, 1, 1, TimeUnit.SECONDS);
        } else {
            sf.cancel(false);
            world.close();
            wf.cancel(false);
            System.out.println(wf.isDone() + " " + wf.isCancelled());
        }
    }

    void shutdown() {
        System.out.println("Diablo");
        es.shutdown();
    }

    private static boolean unequal(DoubleProperty[] lhs, float[] rhs) {
        for (int i = 0; i < 6; i++) {
            if (Math.abs(lhs[i].get() - rhs[i]) > 0.005) {
                System.out.println("inner!");
                return true;
            }
        }
        return false;
    }

    private void updateCameraInfo() {
        final float[] data = world.getCameraInfo();
        if (oldCameraInfo != null && unequal(localCameraInfo, oldCameraInfo)) {
            //has changed by GUI
            final float[] info = {
                    (float) localCameraInfo[0].get(),
                    (float) localCameraInfo[1].get(),
                    (float) localCameraInfo[2].get(),
                    (float) localCameraInfo[3].get(),
                    (float) localCameraInfo[4].get(),
                    (float) localCameraInfo[5].get(),
            };
            world.setCameraInfo(info);
            oldCameraInfo = info;
            System.out.println("set " + Arrays.toString(info));
        } else {
            //has changed by world
            oldCameraInfo = data;
            for (int i = 0; i != 6; ++i) {
                localCameraInfo[i].set((double) data[i]);
            }
            System.out.println("get " + Arrays.toString(data));
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

        DummyProgress(int max, String name, ScheduledExecutorService ses, IntConsumer handler) {
            this.max = max;
            this.name = name;
            this.ses = ses;
            this.handler = handler;
        }

        void action(boolean isSelected) {
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
