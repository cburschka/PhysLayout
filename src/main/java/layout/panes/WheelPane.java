package layout.panes;

import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import layout.PhysLayout;
import physics.Box2DSpringSimulation;
import physics.Spring;

/**
 * Use one node as the center and orient the other nodes around it in clockwise
 * order. This layout currently ignores its children's sizes.
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class WheelPane extends Pane {

    public final ObjectProperty<Node> center;
    private final PhysLayout layout;
    public final Box2DSpringSimulation simulation;
    private double radius;
    private double strength = 10;

    public WheelPane() {
        center = new CenterProperty();
        layout = new PhysLayout(this);
        simulation = new Box2DSpringSimulation(layout);
        radius = Math.min(this.getWidth() * 0.5, this.getHeight() * 0.5);
    }

    @Override
    protected void layoutChildren() {
        simulation.stopSimulation();

        super.layoutChildren();
        final Node c = center.get();

        layout.clearAllMasses();
        layout.setMass(c, Double.POSITIVE_INFINITY);

        // TODO: Handle sizes.
        final List<Node> mc = getManagedChildren();

        // TODO: Try not to recreate this on each layout pass.
        final Node[] children = mc.stream().filter(e -> {
            return e != c;
        }).toArray(size -> {
            return new Node[size];
        });

        for (int _i = 0; _i < children.length; _i++) {
            for (int j = 1; j < children.length; j++) {
                // chord length on the unit circle is twice the sine of half the angle:
                final double chordLength = 2 * radius * Math.sin(j * Math.PI / children.length);
                layout.addConnection(children[_i], children[(_i + j) % children.length], new Spring(chordLength, strength));
            }
            layout.addConnection(c, children[_i], new Spring(radius, strength));
        }

        simulation.startSimulation();
    }

    public final void setCenter(Node value) {
        center.set(value);
    }

    public final Node getCenter() {
        return center.get();
    }

    /**
     * Inner class tracking the pane's central node (see
     * javafx.layout.scene.layout.BorderPane).
     */
    private final class CenterProperty extends ObjectPropertyBase<Node> {

        private boolean isBeingInvalidated;
        private Node oldValue = null;

        @Override
        public Object getBean() {
            return WheelPane.this;
        }

        @Override
        public String getName() {
            return "center";
        }

        CenterProperty() {
            // Unset the center property if the central node is removed from children.
            getChildren().addListener((ListChangeListener.Change<? extends Node> c) -> {
                if (oldValue != null && !isBeingInvalidated) {
                    while (c.next()) {
                        if (c.wasRemoved() && c.getRemoved().contains(oldValue)) {
                            oldValue = null;
                            set(null);
                        }
                    }
                }
            });
        }

        @Override
        protected void invalidated() {
            // Replace the node in the list of children.
            final List<Node> children = getChildren();

            isBeingInvalidated = true;
            try {
                if (oldValue != null) {
                    children.remove(oldValue);
                }

                final Node value = get();
                this.oldValue = value;

                if (value != null) {
                    children.add(value);
                }
            } finally {
                isBeingInvalidated = false;
            }
        }
    }

    public void setRadius(double radius) {
        this.radius = radius;
        requestLayout();
    }
}
