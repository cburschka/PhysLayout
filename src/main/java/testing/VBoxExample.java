package testing;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import layout.panes.PhysicalVBox;

/**
 * An abstract template for example applications. This class creates the basic
 * prerequisites (layout and simulation) along with a GUI to start, stop and
 * reset the simulation.
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class VBoxExample extends Example {

    private final Circle[] circles;
    private final static int NODE_COUNT = 5;
    private final static int NODE_SIZE = 20;

    /**
     * Super-constructor for all examples.
     */
    public VBoxExample() {
        canvas = new PhysicalVBox(30);

        setSimulation(((PhysicalVBox) canvas).simulation);

        circles = new Circle[NODE_COUNT];
        for (int i = 0; i < NODE_COUNT; i++) {
            circles[i] = new Circle();
            circles[i].setFill(Color.hsb(360.0 * i / NODE_COUNT, 1.0, 0.5));
            circles[i].setRadius(NODE_SIZE);
        }
        canvas.getChildren().addAll(circles);

        //MouseControlUtil.makeDraggable(circles[1]);
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
            circle.setTranslateX((Math.random() - 0.5) * WIDTH);
            circle.setTranslateY((Math.random() - 0.5) * HEIGHT);
        }
        getSimulation().setFriction(2);
        ((PhysicalVBox) canvas).setStrength(20);

        canvas.setLayoutX((WIDTH / 2));
        canvas.setLayoutY((2 * HEIGHT / 3));
        root.setCenter(canvas);

        canvas.toBack();
    }

    /**
     * Reset the simulation. This is called once during the setup, and whenever
     * the reset button is pressed.
     */
    @Override
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
    @Override
    public String getTitle() {
        return "VBox";
    }
}
