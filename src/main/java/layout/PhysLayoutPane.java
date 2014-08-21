package layout;

import java.util.Map;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import physics.Spring;
import util.UnorderedPair;

/**
 * A layout pane that positions elements according to elastic connections.
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class PhysLayoutPane extends Pane {
    private Map<UnorderedPair<Node>, Spring> connections;

    public Spring getConnection(Node a, Node b) {
        return connections.get(new UnorderedPair(a, b));
    }

    public void addConnection(Node a, Node b, Spring s) {
        this.connections.put(new UnorderedPair(a, b), s);
    }

    public void removeConnection(Node a, Node b) {
        this.connections.remove(new UnorderedPair(a, b));
    }
}
