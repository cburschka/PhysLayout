package testing;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
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

    private final List<Node> circles;
    private final List<Node> lines;
    private final static int NODE_COUNT = 5;
    private final static int NODE_SIZE = 20;

    /**
     * Super-constructor for all examples.
     */
    public VBoxExample() {
        canvas = new PhysicalVBox(30);
        Button add = new Button("Add node");
        Button remove = new Button("Remove node");
        menu.getItems().add(add);
        menu.getItems().add(remove);
        add.setOnAction(e -> {
            addCircle();
        });
        remove.setOnAction(e -> {
            removeCircle();
        });

        setSimulation(((PhysicalVBox) canvas).simulation);

        circles = new ArrayList<>();

        lines = new ArrayList<>();
        for (int i = 0; i < NODE_COUNT; i++) {
            addCircle();
        }

        getSimulation().setFriction(2);
        ((PhysicalVBox) canvas).setStrength(20);
        ((VBox) canvas).setAlignment(Pos.CENTER);
        canvas.setLayoutX(WIDTH / 2);
        canvas.setLayoutY(HEIGHT / 2);
        root.setCenter(canvas);
        canvas.toBack();
    }

    /**
     * Reset the simulation. This is called once during the setup, and whenever
     * the reset button is pressed.
     */
    @Override
    public void reset() {
        for (Node circle : circles) {
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

    private void addCircle() {
        int i = circles.size();
        Circle circle = new Circle(NODE_SIZE);
        circle.setFill(Color.hsb(360.0 * (i % NODE_COUNT) / NODE_COUNT, 1.0, 0.5));
        circles.add(circle);
        canvas.getChildren().add(circle);

        if (i > 0) {
            Line line = new Line();
            line.setFill(Color.BLACK);
            line.setStroke(Color.BLACK);
            line.startXProperty().bind(circles.get(i - 1).layoutXProperty().add(circles.get(i - 1).translateXProperty()));
            line.startYProperty().bind(circles.get(i - 1).layoutYProperty().add(circles.get(i - 1).translateYProperty()));
            line.endXProperty().bind(circle.layoutXProperty().add(circle.translateXProperty()));
            line.endYProperty().bind(circle.layoutYProperty().add(circle.translateYProperty()));
            lines.add(line);
            line.setManaged(false);

            canvas.getChildren().add(line);
            line.toBack();
        }
    }

    private void removeCircle() {
        int i = circles.size() - 1;

        if (i > 0) {
            Node line = lines.get(i - 1);
            canvas.getChildren().remove(line);
            lines.remove(i - 1);
        }
        if (i >= 0) {
            Node circle = circles.get(i);
            canvas.getChildren().remove(circle);
            circles.remove(i);
        }
    }
}
