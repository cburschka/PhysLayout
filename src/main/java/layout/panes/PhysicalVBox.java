package layout.panes;

import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import layout.PhysLayout;
import physics.Box2DSpringSimulation;
import physics.Spring;
import physics.Tether;

/**
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class PhysicalVBox extends VBox {

    private final PhysLayout layout;
    private final Box2DSpringSimulation simulation;

    public PhysicalVBox() {
        layout = new PhysLayout(this);
        simulation = new Box2DSpringSimulation(layout);
    }

    public PhysicalVBox(Node... children) {
        super(children);
        layout = new PhysLayout(this);
        simulation = new Box2DSpringSimulation(layout);
    }

    public PhysicalVBox(double spacing) {
        super(spacing);
        layout = new PhysLayout(this);
        simulation = new Box2DSpringSimulation(layout);
    }

    public PhysicalVBox(double spacing, Node... children) {
        super(spacing, children);
        layout = new PhysLayout(this);
        simulation = new Box2DSpringSimulation(layout);
    }

    @Override
    protected void layoutChildren() {
        simulation.stopSimulation();

        List<Node> managedChildren = getManagedChildren();
        int n = managedChildren.size();

        // Store the old positions.
        Point2D[] positions = new Point2D[n];
        for (int i = 0; i < n; i++) {
            Node child = managedChildren.get(i);
            positions[i] = child.localToParent(Point2D.ZERO);
        }

        // Perform the layout.
        super.layoutChildren();

        // Determine the new positions, and translate the nodes to their old positions.
        for (int i = 0; i < n; i++) {
            Node child = managedChildren.get(i);
            Point2D newPosition = new Point2D(child.getLayoutX(), child.getLayoutY());
            child.setTranslateX(newPosition.getX() - positions[i].getX());
            child.setTranslateY(newPosition.getY() - positions[i].getY());
            positions[i] = newPosition;
        }

        // Reconnect the nodes.
        layout.clearAllConnections();
        layout.clearAllTethers();

        for (int i = 0; i < n; i++) {
            layout.addTether(managedChildren.get(i), new Tether(0, 20, positions[i]));
            for (int j = 0; j < i; j++) {
                double distance = positions[i].distance(positions[j]);
                layout.addConnection(managedChildren.get(i), managedChildren.get(j), new Spring(distance, 10));
            }
        }

        simulation.startSimulation();
    }
}