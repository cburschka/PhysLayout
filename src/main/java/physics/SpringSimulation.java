package physics;

import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import layout.PhysLayoutPane;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;

/**
 * Simulating multiple objects connected by springs.
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class SpringSimulation {

    private final FirstOrderIntegrator integrator;
    private SpringODE ode;
    private final PhysLayoutPane pane;

    // Simulation time (starting at 0).
    private double time = 0.0;

    // System time.
    private long timeStamp;

    public SpringSimulation(PhysLayoutPane pane) {
        this.pane = pane;
        integrator = new DormandPrince853Integrator(1e-6, 1.0, 1e-4, 1e-4);
    }

    public void createODE() {
        int N = pane.getChildren().size();
        double[] mass = new double[N];
        Spring[][] connections = new Spring[N][N];
        int i = 0;
        for (Node node : pane.getChildren()) {
            mass[i] = pane.getMass(node);
            int j = 0;
            for (Node node2 : pane.getChildren()) {
                connections[i][j] = pane.getConnection(node, node2);
                j++;
            }
            i++;
        }
        this.ode = new SpringODE(connections, mass, 0.5);
    }

    /**
     * Run the physics simulation with a specified time step.
     *
     * @param dt physics time step (not framerate) in seconds.
     */
    public void runSimulation(double dt) {
        final long nanoTimeStep = (long) (dt * 1e9);
        timeStamp = System.nanoTime();

        // Initialize the physics model.
        int N = pane.getChildren().size();
        double[] y = new double[4 * N];
        int i = 0;
        for (Node node : pane.getChildren()) {
            y[4 * i] = node.getLayoutX();
            y[4 * i + 1] = node.getLayoutY();
            y[4 * i + 2] = 0;
            y[4 * i + 3] = 0;
            i++;
        }

        AnimationTimer frameListener = new AnimationTimer() {

            @Override
            public void handle(long now) {
                long nextTimeStamp = timeStamp + nanoTimeStep;
                double tPlusDt = time + dt;

                // Simulate in dt-sized steps until caught up.
                while (nextTimeStamp < now) {
                    integrator.integrate(ode, time, y, tPlusDt, y);
                    int i = 0;
                    for (Node node2 : pane.getChildren()) {
                        node2.relocate(y[4 * i], y[4 * i + 1]);
                        i++;
                    }

                    timeStamp = nextTimeStamp;
                    time = tPlusDt;
                    nextTimeStamp = timeStamp + nanoTimeStep;
                    tPlusDt = time + dt;
                }
            }
        };

        frameListener.start();
    }
}
