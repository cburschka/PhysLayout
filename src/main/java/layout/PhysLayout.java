package layout;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.collections.FXCollections;
import javafx.collections.SetChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import physics.ForceField;
import physics.Spring;
import util.UnorderedPair;

public class PhysLayout {

    private final Pane root;
    private final ReadOnlySetWrapper<Node> nodes;
    public final Set<ForceField> fields;

    private final Map<UnorderedPair<Node>, Spring> connections;
    private final Map<Node, Set<Node>> neighbors;
    private final Map<Node, Double> mass;

    public PhysLayout(Pane root) {
        this.root = root;
        nodes = new ReadOnlySetWrapper<>(FXCollections.observableSet());
        connections = new HashMap<>();
        neighbors = new HashMap<>();
        mass = new HashMap<>();
        fields = new HashSet<>();
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

        Set<Node> nA = neighbors.get(a);
        Set<Node> nB = neighbors.get(b);
        if (nA == null) {
            nA = new HashSet<>();
            neighbors.put(a, nA);
        }
        if (nB == null) {
            nB = new HashSet<>();
            neighbors.put(b, nB);
        }
        nA.add(b);
        nB.add(a);
    }

    public void removeConnection(Node a, Node b) {
        if (this.connections.remove(new UnorderedPair(a, b)) != null) {
            Set<Node> nA = neighbors.get(a);
            Set<Node> nB = neighbors.get(b);
            nA.remove(b);
            nB.remove(a);
            if (nA.isEmpty()) {
                nodes.remove(a);
            }
            if (nB.isEmpty()) {
                nodes.remove(b);
            }
        }
    }

    public Set<Node> getNeighbors(Node a) {
        return neighbors.get(a);
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
