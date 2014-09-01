package physics;

import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import layout.PhysLayout;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;

/**
 * Simulating multiple objects connected by springs.
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class SpringSimulation {

    private final FirstOrderIntegrator integrator;
    private final PhysLayout pane;

    // Simulation time (starting at 0).
    private double time = 0.0;

    // System time.
    private long timeStamp;

    public SpringSimulation(PhysLayout pane) {
        this.pane = pane;
        integrator = new DormandPrince853Integrator(1e-6, 1.0, 1e-4, 1e-4);
    }

    private SpringODE createODE() {
        int N = pane.getNodes().size();

        Node[] nodes = new Node[N];
        Spring[][] connections = new Spring[N][N];
        double[] mass = new double[N];
        double[] start = new double[4 * N];

        int i = 0;
        for (Node node : pane.getNodes()) {
            nodes[i++] = node;
        }

        for (i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                //connections[i][j] = pane.getConnection(nodes[i], nodes[j]);
            }
            mass[i] = pane.getMass(nodes[i]);
            start[4 * i] = nodes[i].getLayoutX();
            start[4 * i + 1] = nodes[i].getLayoutY();
            start[4 * i + 2] = 0;
            start[4 * i + 3] = 0;
        }

        return new SpringODE(nodes, connections, mass, start, 0.5);
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
        SpringODE ode = this.createODE();
        double[] y0 = ode.getY();
        double[] y = new double[y0.length];
        System.arraycopy(y0, 0, y, 0, y.length);
        Node[] nodes = ode.getNodes();

        AnimationTimer frameListener = new AnimationTimer() {

            @Override
            public void handle(long now) {
                long nextTimeStamp = timeStamp + nanoTimeStep;
                double tPlusDt = time + dt;

                // Simulate in dt-sized steps until caught up.
                while (nextTimeStamp < now) {
                    for (int i = 0; i < nodes.length; i++) {
                        // If the node has been moved externally or pressed, update.
                        if (nodes[i].getLayoutX() != y[4*i] || nodes[i].getLayoutY() != y[4*i+1] || nodes[i].isPressed()) {
                            y[4*i] = nodes[i].getLayoutX();
                            y[4*i+1] = nodes[i].getLayoutY();
                            // Reset its momentum, since the user is "holding" it.
                            y[4*i+2] = 0;
                            y[4*i+3] = 0;
                        }
                    }
                    integrator.integrate(ode, time, y, tPlusDt, y);
                    for (int i = 0; i < nodes.length; i++) {
                        nodes[i].setLayoutX(y[4 * i]);
                        nodes[i].setLayoutY(y[4 * i + 1]);
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
