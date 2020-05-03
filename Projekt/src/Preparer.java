import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.File;

public class Preparer {

    /**
     * Nastavi a pripravi kameru, ktorou sa pozera na trojrozmerny priestor
     * @return instancia PerspectiveCamera
     */
    public static Camera prepareCamera() {
        Camera camera = new PerspectiveCamera(true);
        camera.setNearClip(1);
        camera.setFarClip(10000);
        camera.translateZProperty().set(-4000);
        return camera;
    }

    /**
     * Nastavi datovu strukturu graf podla prislusneho vstupneho suboru
     * @param file vstupny textovy subor
     * @return instancia grafu
     */
    public static Graph prepareGraph(File file) {
        if (file == null) {
            file = new File("src/sources/graphs/file25Hustejsi.txt"); // default
        }
        Graph graph = new Graph(file);
        graph.setLoadedFile(file);
        return graph;
    }

    /**
     * Pripravi zoznam objektov, ktorych vykreslenim sa vytvori trojrozmerny graf
     * @param graph instancia grafu
     * @return zoznam objektov na nacitanie do sceny
     */
    public static ObservableList<Node> prepareChildren(Graph graph) {
        ObservableList<Node> children = FXCollections.observableArrayList();
        for (Planet planet: graph.getPlanets()) {
            children.addAll(planet.getShape(), Preparer.preparePlanetText(planet));

        }
        for (Link link: graph.getLinks()) {
            children.addAll(
                    Preparer.prepareLinkLine(link.getFrom(), link.getTo()));
        }
        return children;
    }

    /**
     * Pripravi a nastavi 3D objekt sfery, reprezentujuci planetu (vrchol grafu)
     * @param node instancia vrcholu
     * @return instancia 3D objektu sfery
     */
    public static Sphere preparePlanetSphere(Planet node) {
        PhongMaterial texture = new PhongMaterial();
        texture.setDiffuseMap(node.getTexture());
        texture.setDiffuseColor(node.getColor());
        Sphere sphere = new Sphere(node.getRadius());
        sphere.setMaterial(texture);
        sphere.setTranslateX(node.getX());
        sphere.setTranslateY(node.getY());
        sphere.setTranslateZ(node.getZ());
        sphere.setUserData(node);
        return sphere;
    }

    /**
     * Pripravi a nastavi text, reprezentujuci nazov planety (vrcholu)
     * @param node instancia vrcholu
     * @return instancia textu na vykreslenie do sceny
     */
    public static Text preparePlanetText(Planet node) {
        Text text = new Text(node.getData());
        text.setTranslateX(node.getX());
        text.setTranslateY(node.getY() - node.getRadius() - 5);
        text.setTranslateZ(node.getZ());
        text.setFill(Color.WHITE);
        text.setStyle("-fx-font-family: Verdana; -fx-font-size: 10");
        return text;
    }

    /**
     * Pripravi a nastavi animacny objekt, ktory simuluje rotaciu jednotlivych planet (vrcholov)
     * @param planetGroup instancia skupiny objektov, reprezentujuca 3D objekty vrcholov (planet)
     * @return instancia animacie
     */
    public static Timeline preparePlanetRotationAnimation(Group planetGroup) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(100),
                        event -> {
                    for (Node n: planetGroup.getChildren()) {
                        if (n instanceof Sphere) {
                            n.rotateProperty().set(n.getRotate() + Utils.getRandomDouble(0.05, 0.5));
                        }
                    }
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        return timeline;
    }

    /**
     * Pripravi a nastavi 3D objekt cylindru, reprezentujuci spojenie medzi dvoma vrcholmi
     * @param planet1 instancia pociatocnej planety
     * @param planet2 instancia konecnej planety
     * @return instancia 3D objektu cylindru (ciary)
     */
    public static Cylinder prepareLinkLine(Planet planet1, Planet planet2) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = planet2.getCoords().subtract(planet1.getCoords());
        double height = diff.magnitude();
        Point3D mid = planet2.getCoords().midpoint(planet1.getCoords());
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());
        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);
        Cylinder line = new Cylinder(0.2, height);
        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);
        line.setUserData(new Pair<>(planet1, planet2));
        return line;
    }

    /**
     * Pripravi a nastavi pozadie sceny, ktore simuluje vesmir
     * @return instancia ImageView
     */
    public static ImageView prepareBackground() {
        Image image = new Image("/sources/images/milky_way.jpg");
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.getTransforms().add(new Translate(-image.getWidth() / 2, -image.getHeight() / 2, 1400));
        return imageView;
    }
}