package physics;

/**
 * An idealized mechanical spring.
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class Spring {
    private final double length;
    private final double strength;
    
    /**
     * Create a new spring.
     * @param length The equilibrium length.
     * @param strength The stiffness constant k.
     */
    public Spring(double length, double strength) {
        assert length > 0;
        assert strength > 0;
        this.length = length;
        this.strength = strength;
    }
    
    /**
     * Calculate the spring's currently exerted force.
     * @param distance The current (positive) length l of the spring.
     * @return The force (positive force is directed inward to the other point).
     */
    public double getForce(double distance) {
        return (distance - length)*strength;
    }
}
