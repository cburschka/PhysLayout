package physics;

/**
 * An idealized mechanical spring.
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class Spring {
    private final double length;
    private final double strength;
    
    public Spring(double length, double strength) {
        this.length = length;
        this.strength = strength;
        assert length > 0;
        assert strength > 0;
    }
    
    public double getForce(double distance) {
        return (length - distance)*strength;
    }
}
