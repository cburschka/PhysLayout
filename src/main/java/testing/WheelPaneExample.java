package testing;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import layout.panes.WheelPane;

/**
 * An abstract template for example applications. This class creates the basic
 * prerequisites (layout and simulation) along with a GUI to start, stop and
 * reset the simulation.
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class WheelPaneExample extends Example {

    public static final int NODE_COUNT = 20;
    public static final int NODE_SIZE = 10;

    private final Circle[] circles;

    /**
     * Super-constructor for all examples.
     */
    public WheelPaneExample() {
        canvas = new WheelPane();
        setSimulation(((WheelPane) canvas).simulation);
        ((WheelPane) canvas).setRadius(100);

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
        return "Wheel Pane";
    }

}
