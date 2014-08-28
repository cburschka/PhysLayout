package testing;

import javafx.application.Application;
import static javafx.application.Application.launch;
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
public class Oscillator extends Application {

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        ToolBar menu = new ToolBar();
        Pane canvas = new Pane();
        root.setCenter(canvas);
        root.setTop(menu);

        PhysLayout layout = new PhysLayout(canvas);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setTitle("Oscillator");

        Circle a = new Circle(30, Color.RED);
        a.setLayoutX(WIDTH / 4);
        a.setLayoutY(HEIGHT / 2);

        Circle b = new Circle(30, Color.BLUE);
        b.setLayoutX(3 * WIDTH / 4);
        b.setLayoutY(HEIGHT / 2);

        canvas.getChildren().add(a);
        canvas.getChildren().add(b);
        MouseControlUtil.makeDraggable(a);
        MouseControlUtil.makeDraggable(b);

        Line line = new Line();
        line.setStroke(Color.BLACK);
        line.startXProperty().bind(a.layoutXProperty());
        line.startYProperty().bind(a.layoutYProperty());
        line.endXProperty().bind(b.layoutXProperty());
        line.endYProperty().bind(b.layoutYProperty());
        canvas.getChildren().add(line);
        line.toBack();

        layout.addConnection(a, b, new Spring(WIDTH / 5, 100));

        Box2DSpringSimulation boxSimulation = new Box2DSpringSimulation(layout);
        boxSimulation.setFriction(0);

        Button startStop = new Button("Start"), step = new Button("Step");
        menu.getItems().addAll(startStop, step);
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

        boxSimulation.getRunning().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            startStop.setText(newValue ? "Stop" : "Start");
        });
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
