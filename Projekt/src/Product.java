import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.scene.CacheHint;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.util.Duration;

public class Product {

    private String name; //
    private Color color;
    private TranslateTransition animation;
    private Sphere productShape;
    private Link link;

    public Product(String name, Color color, Link link) {
        this.name = name;
        this.color = color;
        this.link = link;
    }

    /**
     * Pripravi a nastavi 3D objekt sfery, reprezentujuci tovar presuvany medzi planetami (vrcholmi)
     * a zaroven nastavi jeho animaciu, t.j. posun po spoji medzi planerami (vrcholmi)
     */
    public void prepareProductShape() {
        productShape = new Sphere(5);
        productShape.setMaterial(new PhongMaterial(color));
        animation = new TranslateTransition();
        animation.setFromX(link.getFrom().getX());
        animation.setFromY(link.getFrom().getY());
        animation.setFromZ(link.getFrom().getZ());
        animation.setToX(link.getTo().getX());
        animation.setToY(link.getTo().getY());
        animation.setToZ(link.getTo().getZ());
        animation.setDuration(Duration.seconds(Utils.getRandomDouble(20, 100)));
        animation.setCycleCount(Animation.INDEFINITE);
        productShape.setCache(true);
        productShape.setCacheHint(CacheHint.SPEED);
        animation.setNode(productShape);
    }

    /**
     * Zapne animaciu presunu tovaru medzi planetami
     */
    public void startSending() {
        animation.play();
    }

    /**
     * Vypne animaciu presunu tovaru medzi planetami
     */
    public void stopSending() {
        animation.stop();
    }

    /**
     *
     * @return instancia 3D objektu sfery, reprezentujuca presuvany tovar medzi planetami
     */
    public Sphere getProductShape() {
        return productShape;
    }
}