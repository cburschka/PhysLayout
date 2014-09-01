package testing;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import jfxtras.labs.util.event.MouseControlUtil;
import physics.Spring;

/**
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class Oscillator extends Example {

    Circle a, b;

    public Oscillator() {
        a = new Circle(30, Color.RED);
        a.setLayoutX(WIDTH / 4);
        a.setLayoutY(HEIGHT / 2);

        b = new Circle(30, Color.BLUE);
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
        simulation.setFriction(0);
    }

    @Override
    public void reset() {
        a.setLayoutX(WIDTH / 4);
        a.setLayoutY(HEIGHT / 2);
        b.setLayoutX(3 * WIDTH / 4);
        b.setLayoutY(HEIGHT / 2);
    }

    @Override
    public String getTitle() {
        return "Oscillator";
    }
}
