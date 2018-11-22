package net.alexhyisen.pathfinder.gui;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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


    private int count = 0;
    private Image image0;
    private Image image1;

    private ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture sf;

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

    @FXML
    public void initialize() {
        slider.setBlockIncrement(1);
        slider.setMin(0);
        slider.setMax(4540);
        slider.valueProperty().addListener(v -> showImage((int) slider.getValue()));
        slider.setValue(0);


    }

    @FXML
    public void onToggleButtonAction() {
        if (toggleButton.isSelected()) {
            System.out.println("play");
            sf = ses.scheduleAtFixedRate(this::onNextButtonAction, 100, 100, TimeUnit.MILLISECONDS);
        } else {
            System.out.println("stop");
            sf.cancel(false);
        }
    }
}
