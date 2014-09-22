package layout;

import javafx.collections.ListChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import jfxtras.labs.util.event.MouseControlUtil;
import physics.Box2DSpringSimulation;
import physics.Spring;

/**
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class PhysVBox extends VBox {

    private final PhysLayout layout;
    private final Box2DSpringSimulation simulation;
    private final double strength = 1.0f;

    public PhysVBox() {
        layout = new PhysLayout(this);
        simulation = new Box2DSpringSimulation(layout);
        initialize();
    }

    public PhysVBox(Node... children) {
        super(children);
        layout = new PhysLayout(this);
        simulation = new Box2DSpringSimulation(layout);
        initialize();
    }

    public PhysVBox(double spacing) {
        super(spacing);
        layout = new PhysLayout(this);
        simulation = new Box2DSpringSimulation(layout);
        initialize();
    }

    public PhysVBox(double spacing, Node... children) {
        super(spacing, children);
        layout = new PhysLayout(this);
        simulation = new Box2DSpringSimulation(layout);
        initialize();
    }

    private void initialize() {
        getChildren().stream().forEach((child) -> {
            //MouseControlUtil.makeDraggable(child);
        });

        getChildren().addListener((ListChangeListener.Change<? extends Node> e) -> {
            /*while (e.next()) {
                if (e.wasAdded()) {
                    e.getAddedSubList().stream().forEach((child) -> {
                        // TODO: This replaces other handlers?
                        MouseControlUtil.makeDraggable(child, null, null);
                    });
                }
                if (e.wasRemoved()) {
                    // TODO: Remove drag handlers.
                }
            }*/
        });

        //simulation.startSimulation();
    }

    @Override
    protected void layoutChildren() {
        layout.clearAllConnections();
        layout.clearAllMasses();

        // anchor the first node (TODO: More options)
        layout.setMass(getChildren().get(0), Double.POSITIVE_INFINITY);
        super.layoutChildren();
        getChildren().stream().forEach((a) -> {
            getChildren().stream().filter((b) -> (a != b)).forEach((b) -> {
                Point2D relative = new Point2D(b.getLayoutX() - a.getLayoutX(), b.getLayoutY() - a.getLayoutY());
                layout.addConnection(a, b, new Spring(relative.magnitude(), strength));
            });
        });
    }
}
