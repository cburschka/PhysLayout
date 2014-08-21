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
    private Map<UnorderedPair<Node>,Spring> connections;
}
