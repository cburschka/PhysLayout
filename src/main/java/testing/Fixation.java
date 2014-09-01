package testing;

import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import jfxtras.labs.util.event.MouseControlUtil;
import org.jbox2d.common.Vec2;
import physics.Spring;

/**
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class Fixation extends Example {

    Circle anchor, pendulum;
    List<Line> lines;
    public static final int CONN = 50;

    public Fixation() {
        super();
        anchor = new Circle(50, Color.RED);
        pendulum = new Circle(50, Color.GREEN);
        canvas.getChildren().addAll(anchor, pendulum);
        lines = new ArrayList<>();
        layout.setMass(anchor, Double.POSITIVE_INFINITY);
        MouseControlUtil.makeDraggable(anchor);
        MouseControlUtil.makeDraggable(pendulum);
        Button reconnect = new Button("Reconnect");
        menu.getItems().add(reconnect);
        reconnect.setOnAction((ActionEvent event) -> {
            reconnect();
        });
        simulation.setFriction(5);
    }

    private void reconnect() {
        layout.clearConnections(anchor, pendulum);
        lines.stream().forEach((Line l) -> {
            canvas.getChildren().remove(l);
        });
        lines = new ArrayList<>();
        Spring[] s = new Spring[CONN];
        for (int i = 0; i < CONN; i++) {
            Vec2 a = new Vec2((float) Math.random() * 100 - 50, (float) Math.random() * 100 - 50);
            Vec2 b = new Vec2((float) Math.random() * 100 - 50, (float) Math.random() * 100 - 50);
            double length = a
                    .add(new Vec2((float) anchor.getLayoutX(), (float) anchor.getLayoutY()))
                    .sub(b)
                    .sub(new Vec2((float) pendulum.getLayoutX(), (float) pendulum.getLayoutY()))
                    .length();
            s[i] = new Spring(length, 100, a, b);
            Line line = new Line();
            line.startXProperty().bind(anchor.layoutXProperty().add(a.x));
            line.startYProperty().bind(anchor.layoutYProperty().add(a.y));
            line.endXProperty().bind(pendulum.layoutXProperty().add(b.x));
            line.endYProperty().bind(pendulum.layoutYProperty().add(b.y));
            lines.add(line);
            canvas.getChildren().add(line);
            line.toBack();
        }
        layout.addConnection(anchor, pendulum, s);
    }

    @Override
    public void reset() {
        anchor.setLayoutX(WIDTH / 3);
        anchor.setLayoutY(HEIGHT / 2);
        pendulum.setLayoutX(2 * WIDTH / 3);
        pendulum.setLayoutY(HEIGHT / 2);
        reconnect();
    }

    @Override
    public String getTitle() {
        return "Relative Fixation";
    }
}
