package physics;


import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

/**
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class SpringODE implements FirstOrderDifferentialEquations {
    private final Spring[][] connections;
    private final double[] mass;
    private final double b;
    
    public SpringODE(Spring[][] connections, double[] mass, double b) {
        this.connections = connections;
        this.mass = mass;
        this.b = b;
    }
    
    @Override
    public void computeDerivatives(double t, double[] y, double[] yDot) throws MaxCountExceededException, DimensionMismatchException {
        int N = mass.length;
        if (4*N != y.length) throw new DimensionMismatchException(y.length, 4*N);
        for (int i = 0; i < N; i++) {
            // d Position/dt = Velocity
            yDot[4*i] = y[4*i+2];
            yDot[4*i+1] = y[4*i+3];
            // d Velocity/dt = Acceleration
            yDot[4*i+2] = -b / mass[i] * y[4*i+2];
            yDot[4*i+3] = -b / mass[i] * y[4*i+3];
            for (int j = 0; j < N; j++) {
                if (connections[i][j] == null) continue;
                double dx = y[4*j] - y[4*i];
                double dy = y[4*j+1] - y[4*i+1];
                double distance = Math.pow(dx*dx+dy*dy, 0.5);
                double force = connections[i][j].getForce(distance);
                yDot[4*i+2] += dx / distance * force / mass[i];
                yDot[4*i+3] += dy / distance * force / mass[i];
            }
        }
    }

    @Override
    public int getDimension() {
        return 4*mass.length;
    }
}
