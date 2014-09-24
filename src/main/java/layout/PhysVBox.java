package layout;

import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import physics.Spring;

/**
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class PhysVBox extends PhysLayoutManager {

    private final VBox root;
    private final double strength = 1.0;

    public PhysVBox(VBox root) {
        super(root);
        this.root = root;
    }

    private void reconnect(List<Node> children) {
        layout.clearAllConnections();
        layout.clearAllMasses();

        // anchor the first node (TODO: More options)
        //layout.setMass(children.get(0), Double.POSITIVE_INFINITY);
        children.stream().forEach((a) -> {
            children.stream().filter((b) -> (a != b)).forEach((b) -> {
                Point2D relative = new Point2D(b.getLayoutX() - a.getLayoutX(), b.getLayoutY() - a.getLayoutY());
                layout.addConnection(a, b, new Spring(relative.magnitude(), strength));
            });
        });
    }

    @Override
    protected void childrenAdded(List<Node> added, List<Node> children) {
        reconnect(children);
    }

    @Override
    protected void childrenRemoved(List<Node> removed, List<Node> children) {
        reconnect(children);
    }
}
