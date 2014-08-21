package layout;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import physics.Spring;
import physics.SpringSimulation;
import physics.Vector;
import util.UnorderedPair;

/**
 * A layout pane that positions elements according to elastic connections.
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class PhysLayoutPane extends Pane {
    //private double timeStep = 1E-3;
    private final Map<UnorderedPair<Node>, Spring> connections;
    private final Map<Node, Map<Node, Spring>> connectionsTo;
    private final Map<Node, Double> mass;

    public PhysLayoutPane() {
        connections = new HashMap<>();
        connectionsTo = new HashMap<>();
        mass = new HashMap<>();
        //ObservableList<Node> children = getChildren();
    }

    public Spring getConnection(Node a, Node b) {
        return connections.get(new UnorderedPair(a, b));
    }
    
    /**
     * Set the mass of a node.
     * If set to infinity, the node will be fixed in place.
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
    
    public Vector velocity(Node a) {
        Vector result = combinedForce(a);
        result.scale(1.0 / getMass(a));
        return result;
    }
}
