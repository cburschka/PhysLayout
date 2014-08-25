package physics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import layout.PhysLayout;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

/**
 * Manage a JBox2D simulation of multiple JavaFX nodes. Nodes have no collision,
 * and are moved by applying forces (from mechanical springs and forcefields).
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class Box2DSpringSimulation {

    private final PhysLayout layout;
    private final Map<Node, Body> bodies;
    private final World world;
    private final Set<ForceField> fields;
    private double friction;
    private long timeStamp;

    public Box2DSpringSimulation(PhysLayout layout) {
        this.layout = layout;
        bodies = new HashMap<>();
        fields = new HashSet<>();

        // New zero-gravity world:
        world = new World(new Vec2(0, 0));
        BodyDef def = new BodyDef();

        layout.getNodes().stream().forEach((node) -> {
            def.position.set((float) node.getLayoutX(), (float) node.getLayoutY());
            Body body = world.createBody(def);
            // Attach an infinitely dense point mass to the body:
            body.createFixture(new ShapelessShape((layout.getMass(node))), Float.POSITIVE_INFINITY);
            bodies.put(node, body);
        });
    }

    public void step(double dt) {
        // Box2D physics work by applying a fixed force on every timestep.
        applyAllForces();
        // 6 iterations of u' and 3 iterations of u (recommended value).
        world.step((float) dt, 6, 3);
    }

    public void runSimulation(double dt) {
        final long nanoTimeStep = (long) (dt * 1e9);
        timeStamp = System.nanoTime();

        AnimationTimer frameListener = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long nextTimeStamp = timeStamp + nanoTimeStep;

                // Simulate in dt-sized steps until caught up.
                while (nextTimeStamp < now) {
                    bodies.entrySet().stream().forEach((e) -> {
                        Vec2 relative = new Vec2((float) e.getKey().getLayoutX(), (float) e.getKey().getLayoutY());
                        relative.subLocal(e.getValue().getWorldCenter());
                        // If the node has been moved externally or pressed, update.
                        // Tolerance is 1e-6f to account for rounding between JBox2D float and JavaFX double.
                        if (relative.length() > 1e-6f || e.getKey().isPressed()) {
                            e.getValue().getTransform().p.addLocal(relative);
                            // Reset its momentum, since the user is "holding" it.
                            e.getValue().setLinearVelocity(new Vec2());
                        }
                    });

                    step(dt);

                    bodies.entrySet().stream().forEach((e) -> {
                        Vec2 p = e.getValue().getWorldCenter();
                        e.getKey().relocate(p.x, p.y);
                    });

                    timeStamp = nextTimeStamp;
                    nextTimeStamp = timeStamp + nanoTimeStep;
                }
            }
        };

        frameListener.start();
    }

    /**
     * Applies spring force between a and b.
     *
     * @param a
     * @param b
     * @param spring
     */
    private void applySpring(Body a, Body b, Spring spring) {
        Vec2 pA = a.getWorldCenter();
        Vec2 pB = b.getWorldCenter();
        Vec2 force = spring.getForce(pA, pB);
        a.applyForceToCenter(force);
        b.applyForceToCenter(force.negate());
    }

    private void applyFriction(Body a) {
        Vec2 v = a.getLinearVelocity();
        a.applyForceToCenter(v.mul((float) -friction));
    }

    private void applyAllForces() {
        layout.getAllConnections().stream().forEach((e) -> {
            applySpring(bodies.get(e.getKey().getA()), bodies.get(e.getKey().getB()), e.getValue());
        });
        bodies.values().stream().forEach((a) -> {
            applyFriction(a);
            fields.stream().forEach((field) -> {
                a.applyForceToCenter(field.force(a.getWorldCenter()));
            });
        });
    }
}
