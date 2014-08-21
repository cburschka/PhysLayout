package physics;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import layout.PhysLayoutPane;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;

/**
 * Simulating multiple objects connected by springs.
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */


public class SpringSimulation {
    private final FirstOrderIntegrator integrator;
    private SpringODE ode;
    private final PhysLayoutPane pane;
    private Pane root;
    private double timeStep;
    private long lastTimeStamp;
    
    //AnimationTimer frameListener = new AnimationTimer() {
    
    public SpringSimulation(PhysLayoutPane pane) {
        this.pane = pane;
        integrator = new DormandPrince853Integrator(1e-6, 1.0, 1e-4, 1e-4);
        timeStep = 1e-2;
    }
    
    public void createODE() {
        int N = pane.getChildren().size();
        System.out.println(N);
        double[] mass = new double[N];
        Spring[][] connections = new Spring[N][N];
        int i = 0;
        for (Node node : pane.getChildren()) {
            mass[i] = pane.getMass(node);
            int j = 0;
            for (Node node2 : pane.getChildren()) {
                System.out.println(j);
                connections[i][j] = pane.getConnection(node, node2);
                j++;
            }
            i++;
        }
        this.ode = new SpringODE(connections, mass, 0.5);
    }
    
    public void runSimulation() {
        double time = 0;
        double dt = 0.01;
        /*AnimationTimer frameListener = new AnimationTimer() {
            @Override
            public void handle(long now) {
                
            }*/
        int N = pane.getChildren().size();
        double[] y = new double[4*N];
        double[] yPrev = new double[4*N];
        double[] dy = new double[4*N];
        int i = 0;
        for (Node node : pane.getChildren()) {
            y[4*i] = node.getLayoutX();
            y[4*i+1] = node.getLayoutY();
            y[4*i+2] = 0;
            y[4*i+3] = 0;
            i++;
        }
        System.arraycopy(y, 0, yPrev, 0, yPrev.length);
        AnimationTimer frameListener = new AnimationTimer() {

            @Override
            public void handle(long now) {
                System.out.println(now - lastTimeStamp);
                lastTimeStamp = now;
                integrator.integrate(ode, time, y, time+dt, y);
                for (int j = 0; j < y.length; j++) {
                    y[j] += timeStep * dy[j];
                }
                int i = 0;
                for (Node node2 : pane.getChildren()) {
                    node2.relocate(y[4*i], y[4*i+1]);
                    System.out.println(y[4*i]);
                    i++;
                }
                
                System.arraycopy(y, 0, yPrev, 0, yPrev.length);
                
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SpringSimulation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        // finally, start the framle listener
        frameListener.start();

    }
}