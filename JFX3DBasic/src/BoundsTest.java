import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.naming.TimeLimitExceededException;

@SuppressWarnings("Duplicates")
public class BoundsTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        Rectangle rect1 = new Rectangle(50, 50, 100, 100);
        Rectangle rect2 = new Rectangle(150, 150, 100, 100);
        rect2.setRotate(40);

        Circle b = new Circle(5);
        b.setFill(Color.RED);
        double x = rect2.getBoundsInParent().getMinX();
        double y = rect2.getBoundsInParent().getMinY();
        b.setCenterX(x);
        b.setCenterY(y);

        Pane pane = new Pane();
        pane.getChildren().addAll(rect1, rect2, b);

        System.out.println(rect1.getBoundsInParent().intersects(rect2.getBoundsInParent()));

        Scene scene = new Scene(pane, 350, 350);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
