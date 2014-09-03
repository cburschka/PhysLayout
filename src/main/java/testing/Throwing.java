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
public class Throwing extends Example {

    private final Circle ball;

    public Throwing() {
        ball = new Circle(20, Color.RED);
        Circle anchor = new Circle(5, Color.BLACK);
        Line line = new Line();
        line.setStroke(Color.BLACK);
        line.startXProperty().bind(ball.layoutXProperty());
        line.startYProperty().bind(ball.layoutYProperty());
        line.endXProperty().bind(anchor.layoutXProperty());
        line.endYProperty().bind(anchor.layoutYProperty());
        anchor.setLayoutX(WIDTH / 2);
        anchor.setLayoutY(HEIGHT / 2);

        canvas.getChildren().add(line);
        canvas.getChildren().add(anchor);
        canvas.getChildren().add(ball);
        layout.addNode(ball);
        layout.setMass(anchor, Double.POSITIVE_INFINITY);
        layout.addConnection(ball, anchor, new Spring(0, 10));
        MouseControlUtil.makeDraggable(ball);
        simulation.setFriction(0);
    }

    @Override
    public void reset() {
        ball.setLayoutX(WIDTH / 2);
        ball.setLayoutY(HEIGHT / 2);
    }

    @Override
    public String getTitle() {
        return "Throwing";
    }
}
