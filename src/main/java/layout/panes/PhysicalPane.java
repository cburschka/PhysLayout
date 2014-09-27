package layout.panes;

import physics.Box2DSpringSimulation;

/**
 *
 * @author christoph
 */


public interface PhysicalPane {
    public abstract Box2DSpringSimulation getSimulation();
    public abstract void setStrength(double strength);
    public abstract double getStrength();
}
