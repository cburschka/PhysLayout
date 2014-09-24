package testing;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import jfxtras.labs.util.event.MouseControlUtil;
import layout.panes.PhysicalVBox;

/**
 * An abstract template for example applications. This class creates the basic
 * prerequisites (layout and simulation) along with a GUI to start, stop and
 * reset the simulation.
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class VBoxTest extends Application {

    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;
    public static final int NODE_COUNT = 10;
    public static final int NODE_SIZE = 10;

    VBox canvas;
    Pane root;
    Stage primaryStage;
    private final Circle[] circles;

    /**
     * Super-constructor for all examples.
     */
    public VBoxTest() {
        circles = new Circle[NODE_COUNT];
        for (int i = 0; i < NODE_COUNT; i++) {
            circles[i] = new Circle();
            circles[i].setFill(Color.hsb(360.0 * i / NODE_COUNT, 1.0, 0.5));
            circles[i].setRadius(NODE_SIZE);
        }
        //MouseControlUtil.makeDraggable(circles[1]);
        canvas = new PhysicalVBox(10, circles);
        for (int i = 0; i < NODE_COUNT - 1; i++) {
            Line line = new Line();
            line.setFill(Color.BLACK);
            line.setStroke(Color.BLACK);
            line.startXProperty().bind(circles[i].layoutXProperty().add(circles[i].translateXProperty()));
            line.startYProperty().bind(circles[i].layoutYProperty().add(circles[i].translateYProperty()));
            line.endXProperty().bind(circles[(i + 1) % NODE_COUNT].layoutXProperty().add(circles[(i + 1) % NODE_COUNT].translateXProperty()));
            line.endYProperty().bind(circles[(i + 1) % NODE_COUNT].layoutYProperty().add(circles[(i + 1) % NODE_COUNT].translateYProperty()));
            canvas.getChildren().add(line);
            line.setManaged(false);

        }
        for (Circle circle : circles) {
            circle.setTranslateX((Math.random()-0.5)*WIDTH);
            circle.setTranslateY((Math.random()-0.5)*HEIGHT);
        }
        root = canvas;
        canvas.setLayoutX((WIDTH / 2));
        canvas.setLayoutY((HEIGHT / 2));

    }

    /**
     * Sets up the application. This is called internally; the proper method to
     * start the application is launch().
     *
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("VBox Test");
        primaryStage.show();
    }

    public static void main(String[] args) {
        System.err.println("TEST");
        launch(args);
    }
}
