package testing;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import jfxtras.labs.util.event.MouseControlUtil;
import physics.Spring;

/**
 * This example has a central anchor and a spring pendulum that can freely move
 * around it.
 * Using click-and-drag movement, the pendulum can be thrown into an elliptical orbit
 * around the anchor.
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
        line.startXProperty().bind(ball.layoutXProperty().add(ball.translateXProperty()));
        line.startYProperty().bind(ball.layoutYProperty().add(ball.translateYProperty()));
        line.endXProperty().bind(anchor.layoutXProperty().add(anchor.translateXProperty()));
        line.endYProperty().bind(anchor.layoutYProperty().add(anchor.translateYProperty()));
        anchor.setLayoutX(WIDTH / 2);
        anchor.setLayoutY(HEIGHT / 2);

        canvas.getChildren().add(line);
        canvas.getChildren().add(anchor);
        canvas.getChildren().add(ball);
        layout.addConnection(ball, anchor, new Spring(0, 10));
        layout.setMass(anchor, Double.POSITIVE_INFINITY);
        MouseControlUtil.makeDraggable(ball);
        getSimulation().setFriction(0);
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
