import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.io.File;


public class SpaceGraph3D extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static final float WIDTH = 960;
    private static final float HEIGHT = 720;

    private static Graph graph;

    private Group group = new Group();
    private Group planetGroup = new Group();
    private SubScene scene = new SubScene(group, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);
    private BorderPane layout = new BorderPane();
    private Scene root = new Scene(layout);
    private static MenuPanel panel;
    private File actualGraphFile;

    private boolean packagesVisible;
    private boolean planetRotation;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // default graph
        loadGraph(null); // vytvori strukturu grafu a nacita na obrazovku default

        panel = new MenuPanel(this);
        layout.setLeft(panel);
        layout.setCenter(scene);

        group.getChildren().add(Preparer.prepareBackground());
        group.getChildren().add(planetGroup);


        scene.setFill(Color.SILVER);
        scene.setCamera(Preparer.prepareCamera());

        MouseControl.initMouseControl(planetGroup, root); // otacaj len planety

        primaryStage.setScene(root);
        primaryStage.setResizable(false);
        primaryStage.setTitle("SpaceGraph3D");
        primaryStage.show();
    }

    /**
     *
     * @return instancia skupiny objektov, reprezentujuca zobrazovaciu cast aplikacie
     */
    public Group getMainGroup() {
        return group;
    }

    /**
     *
     * @return instancia skupiny objektov, reprezentujuca zobrazeny graf v priestore
     */
    public Group getPlanetGroup() {
        return planetGroup;
    }

    /**
     *
     * @return instancia datovej struktury graf
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     *
     * @return informacia, ci prebieha animacia presunu tovarov medzi planetami
     */
    public boolean packagesVisible() {
        return packagesVisible;
    }

    /**
     *
     * @return informacia, ci prebieha animacia rotacie planet
     */
    public boolean planetsRotating() {
        return planetRotation;
    }

    /**
     * Nacitanie textoveho suboru do datovej struktury graf, ulozenie informacie o aktualnom grafe,
     * vlozenie 3D objektov reprezenetujucich graf do sceny
     * @param file textovy subor
     */
    public void loadGraph(File file) {
        graph = Preparer.prepareGraph(file);
        actualGraphFile = graph.getLoadedFile();
        planetGroup.getChildren().addAll(Preparer.prepareChildren(graph));
    }

    /**
     * vymazanie vsetkych objektov grafu zo sceny
     */
    public void clearGraph() {
        planetGroup.getChildren().clear();
    }

    /**
     * opatovne nacitanie grafu do sceny
     */
    public void reloadGraph() {
        if (packagesVisible) {
            setTransport(false);
        }
        clearGraph();
        loadGraph(actualGraphFile);
    }

    /**
     * Zapnutie/vypnutie animacie presunu vsetkych tovarov medzi planetami
     * @param value true/falsie
     */
    public void setTransport(boolean value) {
        if (value) {
            for (Product product: graph.getProducts()) {
                product.prepareProductShape();
                planetGroup.getChildren().add(product.getProductShape());
                product.startSending();
            }
            packagesVisible = true;
        } else {
            for (Product product: graph.getProducts()) {
                product.stopSending();
                planetGroup.getChildren().remove(product.getProductShape());
            }
            packagesVisible = false;
        }
    }

    /**
     * Zapnutie/vypnutie animacie rotacie vsetkych planet
     * @param value true/false
     */
    public void setRotation(boolean value) {
        planetRotation = value;
    }
}

class MouseControl {

    private static double anchorX, anchorY, anchorAngleX, anchorAngleY;

    /**
     * Spracuje udalosti pohybu, drzania a skrolovania mysi tak, aby doslo k zelanemu efektu vizualizacie grafu
     * @param group instancia skupiny objektov v scene
     * @param scene instancia sceny
     */
    public static void initMouseControl(Group group, Scene scene) {
        Rotate xRotate = new Rotate(0, Rotate.X_AXIS);
        Rotate yRotate = new Rotate(0, Rotate.Y_AXIS);
        group.getTransforms().addAll(xRotate, yRotate);

        scene.setOnMousePressed(event -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = xRotate.getAngle();
            anchorAngleY = yRotate.getAngle();
        });

        scene.setOnMouseDragged(event -> {
            xRotate.setAngle(anchorAngleX - (anchorY - event.getSceneY()));
            yRotate.setAngle(anchorAngleY + (anchorX - event.getSceneX()));
        });

        scene.setOnScroll(event -> group.translateZProperty().set(group.getTranslateZ() + event.getDeltaY()));
    }
}