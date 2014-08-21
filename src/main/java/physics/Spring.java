/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package physics;

/**
 *
 * @author christoph
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
