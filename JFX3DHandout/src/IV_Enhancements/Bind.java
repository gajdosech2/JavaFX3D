package IV_Enhancements;

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

public class Bind extends Application {
    Group group = new Group();
    Camera camera = new PerspectiveCamera(true);
    Scene scene = new Scene(group, 1024, 768, true);
    PhongMaterial boxMaterial = new PhongMaterial(Color.LIGHTGREEN);
    PhongMaterial playerMaterial = new PhongMaterial(Color.BLUE);
    List<Box> obstacles = new ArrayList<>();

    private char[][] map = {
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#'},
            {'#', '.', '#', '#', '#', '.', '#', '#', '.', '.', '#'},
            {'#', '.', '.', '.', '#', '.', '#', '.', '#', '.', '#'},
            {'#', '.', '#', '#', '#', '.', '#', '.', '#', '.', '#'},
            {'#', '.', '.', '.', '#', '.', '#', '.', '#', '.', '#'},
            {'#', '.', '#', '#', '#', '.', '#', '#', '.', '.', '#'},
            {'#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'}
    };

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        scene.setFill(Color.LIGHTBLUE);
        scene.setCamera(camera);
        camera.setTranslateX(0);
        camera.setTranslateY(500);
        camera.setTranslateZ(250);
        camera.setFarClip(2000);
        camera.setRotationAxis(Rotate.X_AXIS);
        camera.setRotate(115);

        generatePlayer();
        createFloor();
        loadMap();

        primaryStage.setTitle("Camera Bind");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadMap() {
        int Z = -250;
        for (int i = map.length - 1; i >= 0; i--) {
            int X = -250;
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

        obstacles.add(box);
        group.getChildren().add(box);
    }

    private void createFloor() {
        Box box = new Box(600,1,600);
        box.setMaterial(new PhongMaterial(Color.WHEAT));
        group.getChildren().add(box);
    }

    public boolean closeToWall(Sphere player)
    {
        for (Box box : obstacles)
        {
            if (player.getBoundsInParent().intersects(box.getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }

    private void generatePlayer() {
        Sphere player = new Sphere(5);
        player.setMaterial(playerMaterial);
        group.getChildren().add(player);

        camera.translateXProperty().bind(player.translateXProperty());
        camera.translateZProperty().bind(player.translateZProperty().add(250));

        scene.setOnKeyPressed(event -> {
            var oldZ = player.getTranslateZ();
            var oldX = player.getTranslateX();
            switch (event.getCode()) {
                case W:
                    player.setTranslateZ(player.getTranslateZ() - 10);
                    break;
                case S:
                    player.setTranslateZ(player.getTranslateZ() + 10);
                    break;
                case A:
                    player.setTranslateX(player.getTranslateX() - 10);
                    break;
                case D:
                    player.setTranslateX(player.getTranslateX() + 10);
                    break;
            }
            if (closeToWall(player)) {
                player.setTranslateZ(oldZ);
                player.setTranslateX(oldX);
            }
        });
    }
}