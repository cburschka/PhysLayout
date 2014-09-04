package physics.shapes;

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
public class UnknownNodeShape extends Shape {

    private final Node node;

    public UnknownNodeShape(ShapeType type, Node node) {
        super(type);
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
    public boolean raycast(RayCastOutput output, RayCastInput input, Transform transform, int childIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void computeAABB(AABB aabb, Transform xf, int childIndex) {
        Bounds b = node.getBoundsInLocal();
        final Vec2 lower = aabb.lowerBound, upper = aabb.upperBound;
        final float x1, y1, x2, y2, x3, y3, x4, y4;

        // Box it,
        x1 = (float) b.getMinX();
        y1 = (float) b.getMinY();
        x2 = (float) b.getMaxX();
        y2 = (float) b.getMaxY();

        // Spin it,
        x3 = x1 * xf.q.c + y1 * xf.q.s;
        y3 = y1 * xf.q.c + x1 * xf.q.s;
        x4 = x2 * xf.q.c + y2 * xf.q.s;
        y4 = y2 * xf.q.c + x2 * xf.q.s;

        // Rebox it,
        lower.x = Math.min(x3, x4);
        lower.y = Math.min(y3, y4);
        upper.x = Math.max(x3, x4);
        upper.y = Math.max(y3, y4);

        // Move it.
        lower.x += xf.p.x;
        lower.y += xf.p.y;
        upper.x += xf.p.x;
        upper.y += xf.p.y;
    }

    @Override
    public void computeMass(MassData massData, float density) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Shape clone() {
        return new UnknownNodeShape(m_type, node);
    }
}
