package testing;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import layout.PhysLayoutManager;
import layout.PhysVBox;

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
    public static final int NODE_COUNT = 5;
    public static final int NODE_SIZE = 5;

    PhysLayoutManager mgr;
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
        canvas = new VBox(60, circles);
        root = new Pane(canvas);
        canvas.setLayoutX((WIDTH / 2));
        canvas.setLayoutY((HEIGHT / 2));
        mgr = new PhysVBox(canvas);
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
        mgr.start();
    }

    public static void main(String[] args) {
        System.err.println("TEST");
        launch(args);
    }
}
