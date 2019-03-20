package net.alexhyisen.pathfinder.gui;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.alexhyisen.pathfinder.utility.DummyTask;
import net.alexhyisen.pathfinder.utility.Loader;
import net.alexhyisen.pathfinder.utility.QuadFunction;
import net.alexhyisen.pathfinder.world.Mode;
import net.alexhyisen.pathfinder.world.World;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

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
    private ChoiceBox<Mode> modeChoiceBox;

    @SuppressWarnings("unused")
    @FXML
    private TextField execPathTextField;

    @FXML
    private Label execProgressLabel;

    @FXML
    private ProgressBar execProgressBar;

    @FXML
    private ToggleButton execToggleButton;

    @FXML
    private TextField loadPathTextField;

    @FXML
    private Label loadProgressLabel;

    @FXML
    private ProgressBar loadProgressBar;

    @FXML
    private ToggleButton loadToggleButton;

    @FXML
    private VBox paramVBox;

    @FXML
    private ToggleButton viewToggleButton;

    @FXML
    private ToggleButton playToggleButton;

    @FXML
    private GridPane matrixGrid;

    @SuppressWarnings("unchecked")
    private Supplier<Double>[] matrixValueGetter = (Supplier<Double>[]) new Supplier[9];

//    private int count = 0;
//    private Image image0;
//    private Image image1;

    private ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture sf;

    private float[] oldCameraInfo;
    private DoubleProperty[] localCameraInfo = new DoubleProperty[6];

    private World world;
    private Loader loader;

    private ExecutorService es = Executors.newCachedThreadPool();
    private Future wf;

    private IntegerProperty frameProperty;

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

    //I wish I have the real generic which enable partial template specialization, just like that in cpp.
    private static DoubleProperty manageDoubleParamLine(String name, Pane root, double range) {
        return manageParamLine(name, root, -range, range, 0.0, 0.01, 0.01,
                SimpleObjectProperty::new,
                SimpleDoubleProperty::doubleProperty,
                SpinnerValueFactory.DoubleSpinnerValueFactory::new);
    }

    //The DRY principle survived. But was it worth it? To extract the boilerplate code, I do harm to its readability.
    private static <ValueType extends Number, PropertyType extends Property<Number>> PropertyType manageParamLine(
            String name, Pane root, ValueType min, ValueType max, ValueType initValue,
            double sliderIncrement, ValueType spinnerStep,
            Function<ValueType, SimpleObjectProperty<ValueType>> propertyProvider,
            Function<SimpleObjectProperty<ValueType>, PropertyType> paramProvider,
            QuadFunction<ValueType, ValueType, ValueType, ValueType, SpinnerValueFactory<ValueType>> svfProvider
    ) {
        var label = new Label(name);
        var slider = new Slider();

        label.setPrefWidth(150);
        label.setAlignment(Pos.CENTER);

        var property = propertyProvider.apply(initValue);
        var param = paramProvider.apply(property);
        slider.setMin(min.doubleValue());
        slider.setMax(max.doubleValue());
        slider.setBlockIncrement(sliderIncrement);
        slider.valueProperty().bindBidirectional(param);
        slider.valueProperty().addListener(((observable, oldValue, newValue) -> param.setValue(newValue)));
        slider.setPrefWidth(250);

        var spinnerValueFactory = svfProvider.apply(min, max, initValue, spinnerStep);
        var spinner = genSpinner(
                spinnerValueFactory,
                (observable, oldValue, newValue) -> param.setValue(newValue)
        );
        spinner.getValueFactory().valueProperty().bindBidirectional(property);
        spinner.setPrefWidth(100);

        param.setValue(initValue);

        var line = new HBox();
        line.setSpacing(10);
        line.getChildren().addAll(label, slider, spinner);

        root.getChildren().add(line);
        return param;
    }

    private static <T> Spinner<T> genSpinner(SpinnerValueFactory<T> spinnerValueFactory, ChangeListener<T> listener) {
        var spinner = new Spinner<T>();
        spinner.setEditable(true);
        spinner.setValueFactory(spinnerValueFactory);
        spinner.valueProperty().addListener(listener);
        return spinner;
    }

    @FXML
    public void initialize() {
//        slider.setBlockIncrement(1);
//        slider.setMin(0);
//        slider.setMax(4540);
//        slider.valueProperty().addListener(v -> showImage((int) slider.getValue()));
//        slider.setValue(0);

        modeChoiceBox.setItems(FXCollections.observableArrayList(Mode.UNI, Mode.TRI));
        modeChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (world != null) {
                        world.setItemMode(newValue);
                    }
                });
        modeChoiceBox.setValue(Mode.UNI);

        paramVBox.setSpacing(10);
        localCameraInfo[0] = manageDoubleParamLine("Position x", paramVBox, 10.0);
        localCameraInfo[1] = manageDoubleParamLine("Position y", paramVBox, 10.0);
        localCameraInfo[2] = manageDoubleParamLine("Position z", paramVBox, 10.0);
        paramVBox.getChildren().add(new Separator());
        localCameraInfo[3] = manageDoubleParamLine("Orientation i", paramVBox, 1.0);
        localCameraInfo[4] = manageDoubleParamLine("Orientation j", paramVBox, 1.0);
        localCameraInfo[5] = manageDoubleParamLine("Orientation k", paramVBox, 1.0);
        paramVBox.getChildren().add(new Separator());
        frameProperty = manageParamLine("Frame Number", paramVBox,
                0, 1000, 0, 1.0, 1,
                SimpleObjectProperty::new,
                SimpleIntegerProperty::integerProperty,
                SpinnerValueFactory.IntegerSpinnerValueFactory::new);


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                //I would take the cost of more memory consumption much rather than naming space pollution,
                //moreover, hopefully the compiler would optimize it, therefore I don't declare them final.
                var initValue = (i == j ? 1 : 0);
                var spinner = genSpinner(
                        new SpinnerValueFactory.DoubleSpinnerValueFactory(
                                -10, 10, initValue, 0.2),
                        (observable, oldValue, newValue) -> updateMatrix()
                );
                spinner.setPrefWidth(80);
                matrixGrid.add(spinner, j + 1, i + 1);
                matrixValueGetter[i * 3 + j] = spinner::getValue;
            }
        }

        loader = new Loader();

        loadPathTextField.setText("manifold_final.off");

        //It's it better to use a closure rather than the constructor only class?
        new ProgressManager(execProgressLabel, execProgressBar, execToggleButton, ses, es, 600, new DummyTask());
        new ProgressManager(loadProgressLabel, loadProgressBar, loadToggleButton, ses, es, 120,
                () -> {
                    String text = loadPathTextField.getText();
                    System.out.println("load path = " + text);
                    try {
                        loader.load(Paths.get(text));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return true;
                    }
                    return false;
                });
    }

    private void updateMatrix() {
        loader.setMatrix(IntStream.range(0, 9).mapToDouble(v -> matrixValueGetter[v].get()).toArray());
    }

    @FXML
    public void handlePlayToggleButtonAction() {
        if (playToggleButton.isSelected()) {
            sf = ses.scheduleAtFixedRate(
                    () -> frameProperty.add(1),
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
            world = new World(loader);
            world.setItemMode(modeChoiceBox.getValue());
            updateMatrix();
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
        ses.shutdown();
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
}
