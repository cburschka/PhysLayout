package testing;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import jfxtras.labs.util.event.MouseControlUtil;
import layout.PhysLayout;
import physics.Spring;
import physics.SpringSimulation;

/**
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class Main extends Application {
    private static final int NODE_COUNT = 10;
    private static final int NODE_SIZE = 10;
    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        PhysLayout layout = new PhysLayout(root);
        Scene scene = new Scene(root, WIDTH, HEIGHT);
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
        Spring segment = new Spring(Math.PI * radius * 2 / NODE_COUNT, 10);

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
            layout.addConnection(nodes[i], nodes[(NODE_COUNT + i - 1) % NODE_COUNT], segment);
            layout.addConnection(nodes[i], anchor, radial);
            layout.setMass(nodes[i], 1);
        }

        // This part stays put.
        layout.setMass(anchor, Double.POSITIVE_INFINITY);
        SpringSimulation simulation = new SpringSimulation(layout);

        primaryStage.setScene(scene);
        primaryStage.show();
        simulation.runSimulation(1e-2);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
