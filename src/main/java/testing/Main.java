package testing;


import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import layout.PhysLayoutPane;
import physics.Spring;
import physics.SpringSimulation;

/**
 *
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 */
public class Main extends Application {
    private static final int NODE_COUNT = 10;
    private static final int NODE_SIZE = 10;
    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;
    
    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        PhysLayoutPane canvas = new PhysLayoutPane();
        //Pane canvas = new Pane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        primaryStage.setTitle("Springs");
        
        Circle[] nodes = new Circle[NODE_COUNT];
        
        Circle anchor = new Circle();
        anchor.setFill(Color.BLACK);
        anchor.relocate(WIDTH*0.5, HEIGHT*0.5);
        anchor.setRadius(NODE_SIZE*2);
        canvas.getChildren().add(anchor);        
       
        for (int i = 0; i < NODE_COUNT; i++) {
            nodes[i] = new Circle();
            nodes[i].setFill(Color.hsb(360.0*i/NODE_COUNT, 1.0, 0.5));
            //nodes[i].relocate(WIDTH/2, HEIGHT/2);
            nodes[i].relocate(Math.random()*WIDTH, Math.random()*HEIGHT);
            nodes[i].setRadius(NODE_SIZE);
            canvas.getChildren().add(nodes[i]);
        }
        
        double radius = Math.min(WIDTH, HEIGHT) / 3;
        Spring radial = new Spring(radius, 100);
        Spring segment = new Spring(Math.PI * radius * 2 / NODE_COUNT, 100);
        
        for (int i = 0; i < NODE_COUNT; i++) {
            canvas.addConnection(nodes[i], nodes[(i+1)%NODE_COUNT], segment);
            canvas.addConnection(nodes[i], nodes[(NODE_COUNT+i-1)%NODE_COUNT], segment);
            canvas.addConnection(nodes[i], anchor, radial);
        }
        
        // This part stays put.
        canvas.setMass(anchor, Double.POSITIVE_INFINITY);        
        SpringSimulation simulation = new SpringSimulation(canvas);
        
        primaryStage.setScene(scene);
        primaryStage.show();
        simulation.createODE();
        simulation.runSimulation();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);    
    }
        
}
