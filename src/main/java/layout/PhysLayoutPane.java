package layout;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import physics.Spring;
import physics.Vector;
import util.UnorderedPair;

/**
 * A layout pane that positions elements according to elastic connections.
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class PhysLayoutPane extends Pane {

    private Map<UnorderedPair<Node>, Spring> connections;
    private Map<Node, Map<Node, Spring>> connectionsTo;

    public PhysLayoutPane() {
        connections = new HashMap<>();
        connectionsTo = new HashMap<>();
    }

    public Spring getConnection(Node a, Node b) {
        return connections.get(new UnorderedPair(a, b));
    }

    public void addConnection(Node a, Node b, Spring s) {
        this.connections.put(new UnorderedPair(a, b), s);
        Map m = this.connectionsTo.get(a);
        Map n = this.connectionsTo.get(b);
        if (m == null) {
            m = new HashMap<>();
            this.connectionsTo.put(a, m);
        }
        if (n == null) {
            n = new HashMap<>();
            this.connectionsTo.put(b, n);
        }
        this.connectionsTo.get(a).put(b, s);
        this.connectionsTo.get(b).put(a, s);
    }

    public void removeConnection(Node a, Node b) {
        this.connections.remove(new UnorderedPair(a, b));
        Map m = this.connectionsTo.get(a);
        Map n = this.connectionsTo.get(b);
        if (m != null) {
            m.remove(b);
        }
        if (n != null) {
            n.remove(a);
        }
    }

    public Set<Node> getNeighbors(Node a) {
        Map<Node, Spring> m = this.connectionsTo.get(a);
        if (m == null) {
            return null;
        }
        return m.keySet();
    }

    public Vector combinedForce(Node a) {
        Vector result = new Vector(0, 0);
        Vector position = new Vector(a.getLayoutX(), a.getLayoutY());
        connectionsTo.get(a).entrySet().stream().map((e) -> {
            Node b = e.getKey();
            Spring s = e.getValue();
            Vector relative = new Vector(b.getLayoutX(), b.getLayoutY()).difference(position);
            double force = s.getForce(relative.norm());
            relative.unit();
            relative.scale(force);
            return relative;
        }).forEach((relative) -> {
            result.add(relative);
        });
        return result;
    }
}
