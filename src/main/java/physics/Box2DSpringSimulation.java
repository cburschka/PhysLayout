package physics;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javafx.animation.AnimationTimer;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.collections.MapChangeListener;
import javafx.collections.SetChangeListener;
import javafx.geometry.Point2D;
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
    private AnimationTimer animation;
    private long timeStep = (long) 5e5, timeStamp = 0;
    private static final int ITER_VELOCITY = 6, ITER_POS = 3;
    private final ReadOnlyBooleanWrapper running = new ReadOnlyBooleanWrapper(false);

    public Box2DSpringSimulation(PhysLayout layout) {
        this.layout = layout;
        bodies = new HashMap<>();

        // New zero-gravity world:
        world = new World(new Vec2(0, 0));

        layout.getNodes().stream().forEach((node) -> {
            createBody(node);
        });

        layout.getNodes().addListener((SetChangeListener.Change<? extends Node> change) -> {
            if (change.wasAdded()) {
                createBody(change.getElementAdded());
            }
            if (change.wasRemoved()) {
                world.destroyBody(bodies.get(change.getElementRemoved()));
                bodies.remove(change.getElementRemoved());
            }
        });

        layout.getMasses().addListener((MapChangeListener.Change<? extends Node, ? extends Double> change) -> {
            Body body = bodies.get(change.getKey());
            if (body != null) {
                if (change.wasAdded() && change.getValueAdded().isInfinite()) {
                    body.setType(BodyType.STATIC);
                } else if (change.wasRemoved() && change.getValueRemoved().isInfinite()) {
                    body.setType(BodyType.DYNAMIC);
                }
            }
        });

        this.createAnimation();
    }

    public Box2DSpringSimulation(PhysLayout layout, double dt, double friction) {
        this(layout);
        setTimeStep(dt);
        setFriction(friction);
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

    public final void setFriction(double friction) {
        this.friction = friction;
    }

    public void step() {
        // Box2D physics work by applying a fixed force on every timestep.
        applyAllForces();
        // 6 iterations of u' and 3 iterations of u (recommended value).
        world.step((float) (timeStep * 1e-9), ITER_VELOCITY, ITER_POS);
    }

    /**
     * Update object positions based on their JavaFX nodes.
     *
     * For elements that have been moved and released by the mouse, their
     * movement during the last simulated timestep becomes their new momentum,
     * if the simulation is running.
     *
     * This allows "throwing" an element with the mouse.
     *
     * @param timeInterval nanoseconds since the last timestep. if set to 0,
     * all displaced elements will lose their momentum.
     */
    public void updateModel(long timeInterval) {
        bodies.entrySet().stream().forEach((e) -> {
            Node node = e.getKey();
            Body body = e.getValue();
            Vec2 relative = new Vec2((float) node.getLayoutX(), (float) node.getLayoutY());
            relative.subLocal(body.getPosition());

            // If the node has been moved externally or pressed, update.
            if (relative.length() > 0) {
                Vec2 p = body.getTransform().p;
                body.setTransform(p.add(relative), body.getAngle());
                // Use last timestep to set momentum.
                if (isRunning() && timeInterval > 0 && !node.isPressed()) {
                    body.setLinearVelocity(relative.mul((float) (1e9 / timeInterval)));
                } else {
                    body.setLinearVelocity(new Vec2());
                }
            } // Elements must not move while they are held with the mouse.
            else if (node.isPressed()) {
                body.setLinearVelocity(new Vec2());
            }
        });
    }

    /**
     * Update positions without setting velocities.
     */
    public void updateModel() {
        updateModel(0);
    }

    public void updateView() {
        bodies.entrySet().stream().forEach((e) -> {
            Vec2 p = e.getValue().getPosition();
            e.getKey().setLayoutX(p.x);
            e.getKey().setLayoutY(p.y);
        });
    }

    private void createAnimation() {
        animation = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long nextTimeStamp = timeStamp + timeStep;

                // Simulate in dt-sized steps until caught up.
                while (nextTimeStamp < now) {
                    updateModel(now - timeStamp);
                    step();
                    updateView();

                    timeStamp = nextTimeStamp;
                    nextTimeStamp = timeStamp + timeStep;
                }
            }
        };
    }

    public void startSimulation() {
        updateModel();
        running.set(true);
        timeStamp = System.nanoTime();
        animation.start();
    }

    public void stopSimulation() {
        running.set(false);
        animation.stop();
    }

    public boolean isRunning() {
        return running.get();
    }

    public ReadOnlyBooleanProperty getRunning() {
        return running.getReadOnlyProperty();
    }

    /**
     * Set the simulated time step.
     *
     * This is distinct from the frame rate, and will always be fixed.
     *
     * @param dt time step in seconds.
     */
    public final void setTimeStep(double dt) {
        timeStep = (long) (dt * 1e9);
    }

    /**
     * Get the simulated time step.
     *
     * @return the current timestep in seconds.
     */
    public double getTimeStep() {
        return timeStep * 1e-9;
    }

    /**
     * Applies spring force between a and b.
     *
     * @param a
     * @param b
     * @param spring
     */
    private void applySprings(Body a, Body b, Set<Spring> springs) {
        Point2D pA = point(a.getPosition());
        Point2D pB = point(b.getPosition());

        Point2D force = springs.stream().map((s) -> {
            return s.getForce(pA, pB);
        }).reduce(new Point2D(0, 0), (x, y) -> {
            return x.add(y);
        });

        a.applyForceToCenter(vec(force));
    }

    private void applyFriction(Body a) {
        Vec2 v = a.getLinearVelocity();
        a.applyForceToCenter(v.mul((float) -friction));
    }

    private void applyAllForces() {
        layout.getAllConnections().stream().forEach((e) -> {
            Node a = e.getKey().getKey();
            Node b = e.getKey().getValue();
            Set<Spring> s = e.getValue();
            applySprings(bodies.get(a), bodies.get(b), s);
        });
        bodies.values().stream().forEach((a) -> {
            applyFriction(a);
            layout.getFields().stream().forEach((field) -> {
                a.applyForceToCenter(vec(field.force(point(a.getPosition()))));
            });
        });
    }

    private static Point2D point(Vec2 v) {
        return new Point2D(v.x, v.y);
    }

    private static Vec2 vec(Point2D v) {
        return new Vec2((float) v.getX(), (float) v.getY());
    }
}
