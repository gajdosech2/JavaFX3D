import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReinforcementLearning extends Application {
    Group group = new Group();
    Camera camera = new PerspectiveCamera(true);
    Scene scene = new Scene(group, 1024, 768, true);
    PhongMaterial boxMaterial = new PhongMaterial(Color.LIGHTGREEN);
    PhongMaterial sphereMaterial = new PhongMaterial(Color.DARKRED);
    List<Box> obstacles = new ArrayList<>();

    private char[][] map = {
            {'#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '.', '.', '.', '.', '.', '.', '.', '#'},
            {'#', '.', '.', '.', '.', '.', '.', '.', '#'},
            {'#', '.', '.', '.', '.', '.', '.', '.', '#'},
            {'#', '.', '.', '.', '.', '.', '.', '.', '#'},
            {'#', '.', '.', '.', '.', '.', '.', '.', '#'},
            {'#', '.', '.', '.', '.', '.', '.', '.', '#'},
            {'#', '.', '.', '.', '.', '.', '.', '.', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#'}
    };

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        scene.setFill(Color.LIGHTBLUE);
        scene.setCamera(camera);
        camera.setTranslateX(0);
        camera.setTranslateY(1000);
        camera.setTranslateZ(500);
        camera.setFarClip(2000);

        camera.setRotationAxis(Rotate.X_AXIS);
        camera.setRotate(115);

        createFloor();
        loadMap();

        primaryStage.setTitle("Reinforcement Learning");
        primaryStage.setScene(scene);
        primaryStage.show();

        Agent agent = new Agent(this);
    }

    private void loadMap() {
        int Z = -200;
        for (int i = map.length - 1; i >= 0; i--) {
            int X = -200;
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == '#') {
                    createBox(X, Z);
                }
                X += 50;
            }
            Z += 50;
        }
    }

    private void createBox(int X, int Z) {
        Box box = new Box(45, 45,45);
        box.setTranslateX(X);
        box.setTranslateZ(Z);
        box.setMaterial(boxMaterial);

        Sphere sphere = new Sphere(5);
        sphere.setTranslateX(X);
        sphere.setTranslateZ(Z);
        sphere.setTranslateY(25);
        sphere.setMaterial(sphereMaterial);

        obstacles.add(box);
        group.getChildren().add(box);
        group.getChildren().add(sphere);
    }

    private void createFloor() {
        Box box = new Box(600,1,600);
        box.setMaterial(new PhongMaterial(Color.WHEAT));
        group.getChildren().add(box);
    }


    public boolean closeToWall(double agent_x, double agent_z)
    {
        for (Box box : obstacles)
        {
            double box_x = box.getTranslateX();
            double box_z = box.getTranslateZ();
            double distance = (agent_z - box_z) * (agent_z - box_z) + (agent_x - box_x) * (agent_x - box_x);
            if (distance < 1500)
            {
                return true;
            }
        }
        return false;
    }
}

class Agent extends Thread {
    PhongMaterial mat = new PhongMaterial(Color.BLUE);
    ReinforcementLearning map;
    Sphere sphere;
    boolean alive = true;
    int angle = 0;
    Random rnd = new Random();

    public Agent(ReinforcementLearning map)
    {
        this.map = map;
        sphere = new Sphere(10);
        sphere.setMaterial(mat);
        map.group.getChildren().add(sphere);
        this.start();
    }

    @Override
    public void run() {
        while (alive) {
            try {
                if (rnd.nextInt(15) == 0)
                {
                    angle = (angle + (rnd.nextInt(30) + 5) * ((rnd.nextInt(2) == 0) ? 1 : -1)) % 360;
                }
                if (map.closeToWall(sphere.getTranslateX(), sphere.getTranslateZ()))
                {
                    angle = (angle - 180) % 360;
                }
                double dx = Math.sin(Math.toRadians(angle));
                double dz = Math.cos(Math.toRadians(angle));
                sphere.setTranslateX(sphere.getTranslateX() + dx);
                sphere.setTranslateZ(sphere.getTranslateZ() + dz);
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}