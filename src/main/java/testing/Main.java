package testing;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.value.ChangeListener;
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
import jfxtras.labs.util.event.MouseControlUtil;
import layout.PhysLayout;
import physics.Box2DSpringSimulation;
import physics.Spring;

/**
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class Main extends Application {

    private static final int NODE_COUNT = 25;
    private static final int NODE_SIZE = 10;
    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

    @Override
    public void start(Stage primaryStage) {
        BorderPane window = new BorderPane();
        ToolBar menu = new ToolBar();
        Pane root = new Pane();
        window.setCenter(root);
        window.setTop(menu);

        PhysLayout layout = new PhysLayout(root);
        Scene scene = new Scene(window, WIDTH, HEIGHT);
        primaryStage.setTitle("Springs");

        Circle[] nodes = new Circle[NODE_COUNT];

        Circle anchor = new Circle();
        anchor.setFill(Color.BLACK);

        anchor.relocate(WIDTH * 0.5, HEIGHT * 0.5);
        anchor.setRadius(NODE_SIZE);
        root.getChildren().add(anchor);

        for (int i = 0; i < NODE_COUNT; i++) {
            nodes[i] = new Circle();
            nodes[i].setFill(Color.hsb(360.0 * i / NODE_COUNT, 1.0, 0.5));
            nodes[i].relocate(Math.random() * WIDTH, Math.random() * HEIGHT);
            nodes[i].setRadius(NODE_SIZE);
            root.getChildren().add(nodes[i]);
            MouseControlUtil.makeDraggable(nodes[i]);
        }
        MouseControlUtil.makeDraggable(anchor);

        double radius = Math.min(WIDTH, HEIGHT) / 3;
        Spring radial = new Spring(radius, 10);
        Spring segment = new Spring(2 * radius * Math.sin(Math.PI / NODE_COUNT), 10);

        for (int i = 0; i < NODE_COUNT; i++) {

            Line line = new Line();
            line.setFill(Color.BLACK);
            line.setStroke(Color.BLACK);
            line.startXProperty().bind(nodes[i].layoutXProperty());
            line.startYProperty().bind(nodes[i].layoutYProperty());
            line.endXProperty().bind(nodes[(i + 1) % NODE_COUNT].layoutXProperty());
            line.endYProperty().bind(nodes[(i + 1) % NODE_COUNT].layoutYProperty());
            root.getChildren().add(line);
            line.toBack();

            line = new Line();
            line.setFill(Color.BLACK);
            line.setStroke(Color.BLACK);
            line.startXProperty().bind(nodes[i].layoutXProperty());
            line.startYProperty().bind(nodes[i].layoutYProperty());
            line.endXProperty().bind(anchor.layoutXProperty());
            line.endYProperty().bind(anchor.layoutYProperty());
            root.getChildren().add(line);
            line.toBack();

            layout.addConnection(nodes[i], nodes[(i + 1) % NODE_COUNT], segment);
            layout.addConnection(nodes[i], anchor, radial);
            layout.setMass(nodes[i], 0.5);
        }
        for (int i = 0; i < NODE_COUNT; i++) {
            for (int j = 2; j < NODE_COUNT; j++) {
                layout.addConnection(nodes[i], nodes[(i + j) % NODE_COUNT], new Spring(2 * radius * Math.sin(j * Math.PI / NODE_COUNT), 10));
            }
        }

        // This part stays put.
        layout.setMass(anchor, Double.POSITIVE_INFINITY);

        Box2DSpringSimulation boxSimulation = new Box2DSpringSimulation(layout);

        Button startStop = new Button("Start"), step = new Button("Step"), reset = new Button("Randomize");
        menu.getItems().addAll(startStop, step, reset);
        startStop.setOnAction((ActionEvent event) -> {
            if (boxSimulation.isRunning()) {
                boxSimulation.stopSimulation();
            } else {
                boxSimulation.startSimulation();
            }
        });
        step.setOnAction((ActionEvent event) -> {
            if (!boxSimulation.isRunning()) {
                boxSimulation.step();
                boxSimulation.updateView();
            }
        });
        step.disableProperty().bind(boxSimulation.getRunning());
        reset.setOnAction((ActionEvent event) -> {
            for (Circle node : nodes) {
                node.setLayoutX(Math.random() * WIDTH);
                node.setLayoutY(Math.random() * HEIGHT);
            }
        });

        boxSimulation.getRunning().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            startStop.setText(newValue ? "Stop" : "Start");
        });
        startStop.setMinWidth(startStop.getWidth() + 50);
        primaryStage.setScene(scene);
        primaryStage.show();

        boxSimulation.startSimulation();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
