package physics;

import javafx.scene.Node;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

/**
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class SpringODE implements FirstOrderDifferentialEquations {

    private final Node[] nodes;
    private final Spring[][] connections;
    private final double[] mass;
    private final double b;
    private final double[] y;

    public SpringODE(Node[] nodes, Spring[][] connections, double[] mass, double[] start, double b) {
        this.nodes = nodes;
        this.connections = connections;
        this.mass = mass;
        this.y = start;
        this.b = b;
    }

    @Override
    public void computeDerivatives(double t, double[] y0, double[] yDot) throws MaxCountExceededException, DimensionMismatchException {
        int N = nodes.length;
        if (4 * N != y.length) {
            throw new DimensionMismatchException(y.length, 4 * N);
        }
        for (int i = 0; i < N; i++) {
            // d Position/dt = Velocity
            yDot[4 * i] = y0[4 * i + 2];
            yDot[4 * i + 1] = y0[4 * i + 3];
            // d Velocity/dt = Acceleration
            yDot[4 * i + 2] = -b / mass[i] * y0[4 * i + 2];
            yDot[4 * i + 3] = -b / mass[i] * y0[4 * i + 3];
            for (int j = 0; j < N; j++) {
                if (connections[i][j] == null) {
                    continue;
                }
                double dx = y0[4 * j] - y0[4 * i];
                double dy = y0[4 * j + 1] - y0[4 * i + 1];
                double distance = Math.pow(dx * dx + dy * dy, 0.5);
                if (distance == 0) {
                    continue;
                }
                double force = connections[i][j].getForce(distance);
                yDot[4 * i + 2] += dx / distance * force / mass[i];
                yDot[4 * i + 3] += dy / distance * force / mass[i];
            }
        }
    }

    @Override
    public int getDimension() {
        return y.length;
    }

    public double[] getY() {
        return y;
    }

    public Node[] getNodes() {
        return nodes;
    }
}
