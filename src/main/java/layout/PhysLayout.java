package layout;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.collections.FXCollections;
import javafx.collections.SetChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import physics.Spring;
import util.UnorderedPair;

public class PhysLayout {

    private final Pane root;
    private final ReadOnlySetWrapper<Node> nodes;

    private final Map<UnorderedPair<Node>, Spring> connections;
    private final Map<Node, Map<Node, Spring>> connectionsTo;
    private final Map<Node, Double> mass;

    public PhysLayout(Pane root) {
        this.root = root;
        nodes = new ReadOnlySetWrapper<>(FXCollections.observableSet());
        connections = new HashMap<>();
        connectionsTo = new HashMap<>();
        mass = new HashMap<>();
    }

    public Spring getConnection(Node a, Node b) {
        return connections.get(new UnorderedPair(a, b));
    }

    /**
     * Set the mass of a node. If set to infinity, the node will be fixed in
     * place.
     *
     * @param a
     * @param m
     */
    public void setMass(Node a, double m) {
        mass.put(a, m);
    }

    public double getMass(Node a) {
        return mass.getOrDefault(a, 1.0);
    }

    public void addConnection(Node a, Node b, Spring s) {
        nodes.add(a);
        nodes.add(b);
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
        if (this.connections.remove(new UnorderedPair(a, b)) != null) {
            Map m = this.connectionsTo.get(a);
            Map n = this.connectionsTo.get(b);
            m.remove(b);
            n.remove(a);
            if (m.isEmpty()) {
                nodes.remove(a);
            }
            if (n.isEmpty()) {
                nodes.remove(b);
            }
        }
    }

    public Set<Node> getNeighbors(Node a) {
        Map<Node, Spring> m = this.connectionsTo.get(a);
        if (m == null) {
            return null;
        }
        return m.keySet();
    }

    public Set<Node> getNodes() {
        return nodes.getReadOnlyProperty();
    }

    public Set<Entry<UnorderedPair<Node>, Spring>> getAllConnections() {
        return connections.entrySet();
    }

    public void addNodeListener(SetChangeListener<? super Node> listener) {
        nodes.addListener(listener);
    }
}
