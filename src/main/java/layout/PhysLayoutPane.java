/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package layout;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import physics.Spring;
import util.UnorderedPair;


/**
 *
 * @author christoph
 */
public class PhysLayoutPane extends Pane {
    private Map<UnorderedPair<Node>,Spring> connections;
}
