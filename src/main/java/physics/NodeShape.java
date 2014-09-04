package physics;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.RayCastInput;
import org.jbox2d.collision.RayCastOutput;
import org.jbox2d.collision.shapes.MassData;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Rot;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;

/**
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class NodeShape extends Shape {

    private final Node node;
    private float mass;

    public NodeShape(Node node, double mass) {
        super(ShapeType.POLYGON);
        this.node = node;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public boolean testPoint(Transform xf, Vec2 p) {
        float tempx, tempy;
        final Rot xfq = xf.q;

        // Use the JBox2D transform rather than Node.parentToLocal,
        // in order to ensure a consistent result throughout the time step.
        tempx = p.x - xf.p.x;
        tempy = p.y - xf.p.y;
        final float pLocalx = xfq.c * tempx + xfq.s * tempy;
        final float pLocaly = -xfq.s * tempx + xfq.c * tempy;
        return node.contains(pLocalx, pLocaly);
    }

    @Override
    // @TODO: How?
    public boolean raycast(RayCastOutput output, RayCastInput input, Transform transform, int childIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void computeAABB(AABB aabb, Transform xf, int childIndex) {
        final Vec2 lower = aabb.lowerBound;
        final Vec2 upper = aabb.upperBound;
        float x1, x2, y1, y2;

        // @TODO: Figure out if we can somehow box *after* applying our
        // transform, instead of transforming the box.
        // The node seems to only allow applying its own transformation.
        // This box will almost certainly be too big.
        Bounds b = node.getBoundsInLocal();

        // Box it,
        x1 = (float) b.getMinX();
        y1 = (float) b.getMinY();
        x2 = (float) b.getMaxX();
        y2 = (float) b.getMaxY();

        // Spin it,
        lower.x = x1 * xf.q.c + y1 * xf.q.s;
        lower.y = y1 * xf.q.c + x1 * xf.q.s;
        upper.x = x2 * xf.q.c + y2 * xf.q.s;
        lower.y = y2 * xf.q.c + x2 * xf.q.s;

        // Move it.
        lower.x += xf.p.x;
        lower.y += xf.p.y;
        upper.x += xf.p.x;
        upper.y += xf.p.y;
    }

    @Override
    public void computeMass(MassData massData, float density) {
        // @TODO: How?!
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Shape clone() {
        return new NodeShape(node, mass);
    }

}
