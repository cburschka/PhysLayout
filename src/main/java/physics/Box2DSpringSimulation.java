package physics;

import java.util.HashMap;
import java.util.Map;
import javafx.animation.AnimationTimer;
import javafx.collections.SetChangeListener;
import javafx.scene.Node;
import layout.PhysLayout;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
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
    private double friction = 0.5;
    private long timeStamp;

    public Box2DSpringSimulation(PhysLayout layout) {
        this.layout = layout;
        bodies = new HashMap<>();

        // New zero-gravity world:
        world = new World(new Vec2(0, 0));

        layout.getNodes().stream().forEach((node) -> {
            createBody(node);
        });

        layout.addNodeListener((SetChangeListener.Change<? extends Node> change) -> {
            if (change.wasAdded()) {
                createBody(change.getElementAdded());
            }
            if (change.wasRemoved()) {
                world.destroyBody(bodies.get(change.getElementRemoved()));
                bodies.remove(change.getElementRemoved());
            }
        });
    }

    private void createBody(Node node) {
        BodyDef def = new BodyDef();
        def.position.set((float) node.getLayoutX(), (float) node.getLayoutY());
        // Infinite-mass bodies are immovable.
        def.type = layout.getMass(node) == Double.POSITIVE_INFINITY ? BodyType.STATIC : BodyType.DYNAMIC;
        Body body = world.createBody(def);
        // Attach an infinitely dense point mass to the body:
        body.createFixture(new ShapelessShape((layout.getMass(node))), Float.POSITIVE_INFINITY);
        bodies.put(node, body);
    }

    public double getFriction(double friction) {
        return this.friction;
    }

    public void setFriction(double friction) {
        this.friction = friction;
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
                    final long nt = nextTimeStamp;
                    bodies.entrySet().stream().forEach((e) -> {
                        Vec2 relative = new Vec2((float) e.getKey().getLayoutX(), (float) e.getKey().getLayoutY());
                        relative.subLocal(e.getValue().getPosition());

                        // If the node has been moved externally or pressed, update.
                        if (relative.length() > 0 || e.getKey().isPressed()) {
                            Vec2 p = e.getValue().getTransform().p;
                            e.getValue().setTransform(p.add(relative), e.getValue().getAngle());
                            // Reset its momentum, since the user is "holding" it.
                            e.getValue().setLinearVelocity(new Vec2());
                        }
                    });

                    step(dt);

                    bodies.entrySet().stream().forEach((e) -> {
                        Vec2 p = e.getValue().getWorldCenter();
                        e.getKey().setLayoutX(p.x);
                        e.getKey().setLayoutY(p.y);
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
            layout.fields.stream().forEach((field) -> {
                a.applyForceToCenter(field.force(a.getWorldCenter()));
            });
        });
    }
}
