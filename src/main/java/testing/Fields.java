package testing;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import jfxtras.labs.util.event.MouseControlUtil;
import org.jbox2d.common.Vec2;
import physics.PointForceField;

/**
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class Fields extends Example {

    Circle ball;

    public Fields() {
        ball = new Circle(50, Color.RED);
        layout.addField(
                new PointForceField(new Vec2(WIDTH / 4, HEIGHT / 4), -5e7),
                new PointForceField(new Vec2(3 * WIDTH / 4, HEIGHT / 4), -5e7),
                new PointForceField(new Vec2(3 * WIDTH / 4, 3 * HEIGHT / 4), -5e7),
                new PointForceField(new Vec2(WIDTH / 4, 3 * HEIGHT / 4), -5e7)
        );
        canvas.getChildren().add(ball);
        canvas.getChildren().addAll(
                new Circle(WIDTH / 4, HEIGHT / 4, 10, Color.BLUE),
                new Circle(3 * WIDTH / 4, HEIGHT / 4, 10, Color.BLUE),
                new Circle(3 * WIDTH / 4, 3 * HEIGHT / 4, 10, Color.BLUE),
                new Circle(WIDTH / 4, 3 * HEIGHT / 4, 10, Color.BLUE)
        );
        layout.addNode(ball);
        simulation.setFriction(1);
        MouseControlUtil.makeDraggable(ball);
    }

    @Override
    public void reset() {
        ball.setLayoutX(WIDTH / 2);
        ball.setLayoutY(HEIGHT / 2);
    }

    @Override
    public String getTitle() {
        return "Force fields";
    }
}
