package layout;

import java.util.List;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import jfxtras.labs.util.event.MouseControlUtil;
import physics.Box2DSpringSimulation;

/**
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public abstract class PhysLayoutManager {

    protected final PhysLayout layout;
    private final Box2DSpringSimulation simulation;

    public PhysLayoutManager(Pane root) {
        layout = new PhysLayout(root);
        simulation = new Box2DSpringSimulation(layout);
        childrenAdded(root.getChildren(), root.getChildren());
        root.getChildren().stream().forEach((child) -> {
            // TODO: This replaces other handlers?
            MouseControlUtil.makeDraggable(child);
        });
        root.getChildren().addListener((ListChangeListener.Change<? extends Node> e) -> {
            while (e.next()) {
                if (e.wasAdded()) {
                    e.getAddedSubList().stream().forEach((child) -> {
                        // TODO: This replaces other handlers?
                        MouseControlUtil.makeDraggable(child);
                    });
                    childrenAdded((List<Node>) e.getAddedSubList(), (List<Node>) e.getList());
                }
                if (e.wasRemoved()) {
                    childrenRemoved((List<Node>) e.getAddedSubList(), (List<Node>) e.getList());
                    // TODO: Remove drag-handlers.
                }
            }
        });
    }

    protected abstract void childrenAdded(List<Node> added, List<Node> children);

    protected abstract void childrenRemoved(List<Node> removed, List<Node> children);

    public final void start() {
        simulation.startSimulation();
    }

    public final void stop() {
        simulation.stopSimulation();
    }

    public final ReadOnlyBooleanProperty isRunning() {
        return simulation.getRunning();
    }
}
