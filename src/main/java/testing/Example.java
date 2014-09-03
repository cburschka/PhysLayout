package testing;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import layout.PhysLayout;
import physics.Box2DSpringSimulation;

/**
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public abstract class Example extends Application {

    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;

    Box2DSpringSimulation simulation;
    PhysLayout layout;
    Pane canvas;
    BorderPane root;
    ToolBar menu;
    Stage primaryStage;
    private Button reset;

    public Example() {
        root = new BorderPane();
        menu = new ToolBar();
        canvas = new Pane();
        layout = new PhysLayout(canvas);
        simulation = new Box2DSpringSimulation(layout);
        root.setCenter(canvas);
        root.setTop(menu);

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
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
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

    public abstract void reset();

    public abstract String getTitle();
}
