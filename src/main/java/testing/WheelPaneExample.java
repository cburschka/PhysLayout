package testing;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import layout.PhysLayout;
import layout.panes.PhysicalVBox;
import layout.panes.WheelPane;
import physics.Box2DSpringSimulation;

/**
 * An abstract template for example applications. This class creates the basic
 * prerequisites (layout and simulation) along with a GUI to start, stop and
 * reset the simulation.
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class WheelPaneExample extends Application {

    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;
    public static final int NODE_COUNT = 10;
    public static final int NODE_SIZE = 10;

    private final Circle[] circles;
    Box2DSpringSimulation simulation;
    PhysLayout layout;
    Pane canvas;
    BorderPane root;
    ToolBar menu;
    Stage primaryStage;
    private final Button reset;

    /**
     * Super-constructor for all examples.
     */
    public WheelPaneExample() {
        root = new BorderPane();
        menu = new ToolBar();
        Pane center = new Pane();
        canvas = new WheelPane();
        layout = new PhysLayout(canvas);
        simulation = ((WheelPane) canvas).simulation;
        root.setCenter(center);
        center.getChildren().add(canvas);
        root.setTop(menu);
        ((WheelPane) canvas).setRadius(100);
        System.err.println(root.getChildren().size());
        reset = new Button("Reset");
        Button startStop = new Button("Start"), step = new Button("Step"), exit = new Button("Exit");
        menu.getItems().addAll(startStop, step, reset, exit);
        simulation.getRunning().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            startStop.setText(newValue ? "Stop" : "Start");
        });
        startStop.setMinWidth(startStop.getWidth() + 50);
        startStop.setOnAction((ActionEvent event) -> {
            if (simulation.isRunning()) {
                simulation.stopSimulation();
            } else {
                simulation.startSimulation();
            }
        });
        step.setOnAction((ActionEvent event) -> {
            if (!simulation.isRunning()) {
                simulation.updateModel();
                simulation.step();
                simulation.updateView();
            }
        });
        step.disableProperty().bind(simulation.getRunning());

        exit.setOnAction((event) -> {
            primaryStage.close();
        });

        Circle anchor = new Circle(NODE_SIZE, Color.BLACK);
        circles = new Circle[NODE_COUNT];
        for (int i = 0; i < NODE_COUNT; i++) {
            circles[i] = new Circle();
            circles[i].setFill(Color.hsb(360.0 * i / NODE_COUNT, 1.0, 0.5));
            circles[i].setRadius(NODE_SIZE);
        }
        canvas.getChildren().addAll(circles);
        ((WheelPane) canvas).setCenter(anchor);

        //MouseControlUtil.makeDraggable(circles[1]);
        for (int i = 0; i < NODE_COUNT; i++) {
            Line line = new Line();
            line.setFill(Color.BLACK);
            line.setStroke(Color.BLACK);
            line.startXProperty().bind(circles[i].layoutXProperty().add(circles[i].translateXProperty()));
            line.startYProperty().bind(circles[i].layoutYProperty().add(circles[i].translateYProperty()));
            line.endXProperty().bind(circles[(i + 1) % NODE_COUNT].layoutXProperty().add(circles[(i + 1) % NODE_COUNT].translateXProperty()));
            line.endYProperty().bind(circles[(i + 1) % NODE_COUNT].layoutYProperty().add(circles[(i + 1) % NODE_COUNT].translateYProperty()));
            canvas.getChildren().add(line);
            line.setManaged(false);
            line.toBack();

            line = new Line();
            line.setFill(Color.BLACK);
            line.setStroke(Color.BLACK);
            line.startXProperty().bind(circles[i].layoutXProperty().add(circles[i].translateXProperty()));
            line.startYProperty().bind(circles[i].layoutYProperty().add(circles[i].translateYProperty()));
            line.endXProperty().bind(anchor.layoutXProperty().add(anchor.translateXProperty()));
            line.endYProperty().bind(anchor.layoutYProperty().add(anchor.translateYProperty()));
            canvas.getChildren().add(line);
            line.setManaged(false);
            line.toBack();
        }
        for (Circle circle : circles) {
            circle.setTranslateX((Math.random() - 0.5) * WIDTH);
            circle.setTranslateY((Math.random() - 0.5) * HEIGHT);
        }
        canvas.setLayoutX((WIDTH / 2));
        canvas.setLayoutY((2 * HEIGHT / 3));
        root.setCenter(canvas);

        root.setTop(menu);
        canvas.toBack();
    }

    /**
     * Sets up the application. This is called internally; the proper method to
     * start the application is launch().
     *
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        reset.setOnAction((ActionEvent event) -> {
            simulation.stopSimulation();
            reset();
        });

        reset();
        this.primaryStage = primaryStage;
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle(getTitle());
        primaryStage.show();
    }

    /**
     * Reset the simulation. This is called once during the setup, and whenever
     * the reset button is pressed.
     */
    public void reset() {
        for (Circle circle : circles) {
            circle.setTranslateX((Math.random() - 0.5) * WIDTH);
            circle.setTranslateY((Math.random() - 0.5) * HEIGHT);
        }
    }

    /**
     * Get the window title.
     *
     * @return the string that the title will be set to.
     */
    public String getTitle() {
        return "VBox";
    }
}
