import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class MenuPanel extends VBox {

    private SpaceGraph3D spaceGraph;
    private Timeline timeline;

    public MenuPanel(SpaceGraph3D spaceGraph) {
        this.spaceGraph = spaceGraph;
        this.timeline = Preparer.preparePlanetRotationAnimation(spaceGraph.getPlanetGroup());
        preparePanel();
    }

    private void preparePanel() {
        setSpacing(20);
        setMinWidth(160);
        setStyle("-fx-background-color: black; -fx-border-color: white");
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER);
        setSpacing(10);

        setButtonActions();
        panelUpdate();
    }

    private void panelUpdate() {
        getChildren().clear();
        getChildren().addAll(
                Images.LOGO,
                new Separator(),
                getGraphInfo(),
                new Separator(),
                getButtonsGrid(),
                new Separator(),
                getLegendSnippet(),
                Buttons.LEGEND
        );
    }

    private GridPane getButtonsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(5, 10, 5, 10));
        grid.add(Buttons.LOADER, 0, 0);
        grid.add(Buttons.TRANSPORT, 0, 1);
        grid.add(Buttons.ROTATION, 0, 2);
        grid.add(Buttons.PATHFINDER, 0, 3);
        grid.add(Buttons.COMPONENTFINDER, 0, 4);
        grid.add(Buttons.ALLCOMPONENTS, 0, 5);
        grid.add(Buttons.RELOAD, 0, 6);
        return grid;

    }

    private void makeDefaultState() {
        spaceGraph.reloadGraph();
        Buttons.TRANSPORT.setText("Turn Transport ON");
    }

    private void actionTransportButton() {
        if (!spaceGraph.packagesVisible()) {
            spaceGraph.setTransport(true);
            Buttons.TRANSPORT.setText("Turn Transport OFF");
        } else {
            spaceGraph.setTransport(false);
            Buttons.TRANSPORT.setText("Turn Transport ON");
        }
    }

    private void actionRotationButton() {
        if (!spaceGraph.planetsRotating()) {
            timeline.play();
            spaceGraph.setRotation(true);
            Buttons.ROTATION.setText("Turn Rotation OFF");
        } else {
            timeline.stop();
            spaceGraph.setRotation(false);
            Buttons.ROTATION.setText("Turn Rotation ON");
        }
    }

    private void actionLoaderButton() {
        final FileChooser fileChooser = new FileChooser();
        File defaultDir = new File("/src/sources/graphs/");
        if (defaultDir.isDirectory()) {
            fileChooser.setInitialDirectory(defaultDir);
        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showOpenDialog(spaceGraph.getMainGroup().getScene().getWindow());

        if (file != null) {
            if (file.getName().matches("file(10|25|50|75|118|4946)(|Redsi|Hustejsi).txt|sun.txt")) {
                spaceGraph.clearGraph();
                if (spaceGraph.packagesVisible()) {
                    spaceGraph.setTransport(false);
                }
                spaceGraph.loadGraph(file);
                panelUpdate();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("File loading error");
                alert.setContentText("You tried to load incorrect *.txt file.");
                alert.showAndWait();
            }
        }
    }

    private void actionReloadButton() {
        makeDefaultState();
    }

    private void actionAllComponentsButton() {
        makeDefaultState();
        for (List<Planet> component: Algorithms.allComponents(spaceGraph.getGraph())) {
            Color color = Utils.getRandomColor();
            for (Node n: spaceGraph.getPlanetGroup().getChildren()) {
                if (n instanceof Sphere) {
                    Planet planet = (Planet) n.getUserData();
                    if (component.contains(planet)) {
                        ((Sphere) n).materialProperty().setValue(new PhongMaterial(color));
                    }
                } else if (n instanceof Cylinder) {
                    Pair info = (Pair) n.getUserData();
                    Planet planet1 = (Planet) info.getKey();
                    Planet planet2 = (Planet) info.getValue();
                    if (component.contains(planet1) && component.contains(planet2)) {
                        ((Cylinder) n).materialProperty().setValue(new PhongMaterial(color));
                        ((Cylinder) n).radiusProperty().setValue(3);
                    }
                }
            }
        }
    }

    private void setButtonActions() {
        Buttons.TRANSPORT.setOnAction(event -> actionTransportButton());
        Buttons.ROTATION.setOnAction(event -> actionRotationButton());
        Buttons.LOADER.setOnAction(event -> actionLoaderButton());
        Buttons.PATHFINDER.setOnAction(event -> showPathFinderDialog());
        Buttons.COMPONENTFINDER.setOnAction(event -> showComponentFinderDialog());
        Buttons.ALLCOMPONENTS.setOnAction(event -> actionAllComponentsButton());
        Buttons.RELOAD.setOnAction(event -> actionReloadButton());
        Buttons.LEGEND.setOnAction(event -> showLegendDialog());
    }

    private GridPane getGraphInfo() {
        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(5, 10, 5, 10));
        String graphNameInfo = "File: " + spaceGraph.getGraph().getLoadedFile().getName();
        String graphNodesInfo = "Nodes: " + spaceGraph.getGraph().getPlanets().size();
        String graphEdgesInfo = "Edges: " + spaceGraph.getGraph().getLinks().size();
        String graphProductsInfo = "Products: " + spaceGraph.getGraph().getProductsMap().size();
        String connected = "Connected: False" + " (" + Algorithms.numberOfComponents(spaceGraph.getGraph()) + ")";
        if (Algorithms.isConnected(spaceGraph.getGraph())) {
            connected = "Connected: True" + " (" + Algorithms.numberOfComponents(spaceGraph.getGraph()) + ")";
        }
        Text t1 = new Text(graphNameInfo);
        t1.setFill(Color.WHITE);
        Text t2 = new Text(graphNodesInfo);
        t2.setFill(Color.WHITE);
        Text t3 = new Text(graphEdgesInfo);
        t3.setFill(Color.WHITE);
        Text t4 = new Text(graphProductsInfo);
        t4.setFill(Color.WHITE);
        Text t5 = new Text(connected);
        t5.setFill(Color.WHITE);
        pane.setVgap(10);
        pane.add(t1, 0, 0);
        pane.add(t2, 0, 1);
        pane.add(t3, 0, 2);
        pane.add(t4, 0, 3);
        pane.add(t5, 0, 4);
        return pane;
    }

    private GridPane getLegendSnippet() {

        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(10, 20, 10, 20));

        Map<String, Color> products = spaceGraph.getGraph().getProductsMap();
        List<String> productNames = new ArrayList<>(products.keySet());
        int n;
        if (productNames.size() < 3) {
            n = productNames.size();
        } else {
            n = 3;
        }
        for (int i = 0; i < n; i++) {
            Text t = new Text(productNames.get(i));
            t.setFill(Color.WHITE);
            Sphere sphere = new Sphere(5);
            PhongMaterial mat = new PhongMaterial(products.get(productNames.get(i)));
            sphere.setMaterial(mat);
            pane.add(sphere, 0, i);
            pane.add(t, 1, i);
        }
        return pane;
    }

    private void showPathFinderDialog() {

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Pathfinder Feauture");

        ButtonType confirmButton = new ButtonType("Draw Path", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 60, 20, 20));

        List<String> choices = spaceGraph.getGraph().getPlanets()
                .stream()
                .map(Planet::getData)
                .sorted()
                .collect(Collectors.toList());

        ComboBox<String> planetChoiceA = new ComboBox<>();
        planetChoiceA.getItems().addAll(choices);
        planetChoiceA.setPromptText("Choose");
        ComboBox<String> planetChoiceB = new ComboBox<>();
        planetChoiceB.getItems().addAll(choices);
        planetChoiceB.setPromptText("Choose");

        grid.add(new Label("Result:"), 0, 3);

        Text resultText = new Text("Not enough information");
        grid.add(resultText, 1, 3, 2, 1);
        planetChoiceA.setOnAction(event -> {
            grid.getChildren().remove(resultText);
            resultText.setText(getBFSInfo(planetChoiceA.getValue(), planetChoiceB.getValue()));
            grid.add(resultText, 1, 3, 2, 1);
        });

        planetChoiceB.setOnAction(event -> {
            grid.getChildren().remove(resultText);
            resultText.setText(getBFSInfo(planetChoiceA.getValue(), planetChoiceB.getValue()));
            grid.add(resultText, 1, 3, 2, 1);
        });

        Label placeHolder1 = new Label("");
        Label placeHolder2 = new Label("");

        grid.add(placeHolder1, 0, 2);
        grid.add(placeHolder2, 1, 2);

        grid.add(new Label("Start Node:"), 0, 0);
        grid.add(planetChoiceA, 1, 0);
        grid.add(new Label("End Node:"), 0, 1);
        grid.add(planetChoiceB, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButton) {
                return new Pair<>(planetChoiceA.getValue(), planetChoiceB.getValue());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            makeDefaultState();
            runShortestPathModule(pair.getKey(), pair.getValue());
        });
    }

    private String getBFSInfo(String choice1, String choice2) {
        List<Planet> result = Algorithms.shortestPath(spaceGraph.getGraph(), choice1, choice2);
        if (result.isEmpty()) {
            return "No path found!";
        }
        return "Path of length " + result.size() + " was found.";
    }

    private String getDFSInfo(String choice) {
        List<Planet> result = Algorithms.findComponent(spaceGraph.getGraph(), choice);
        if (result.size() == 1) {
            return result.size() + " (alone)";
        } else {
            return "" + result.size();
        }
    }

    private void runShortestPathModule(String choice1, String choice2) {
        List<Planet> result = Algorithms.shortestPath(spaceGraph.getGraph(), choice1, choice2);
        if (!result.isEmpty()) {
            recolorGraph(result, Color.ORANGERED);
        }
    }

    private void runComponentModule(String choice) {
        List<Planet> result = Algorithms.findComponent(spaceGraph.getGraph(), choice);
        if (!result.isEmpty()) {
            recolorGraph(result, Color.ORANGERED);
        }
    }

    private void recolorGraph(List<Planet> except, Color color) {

        for (Node n: spaceGraph.getPlanetGroup().getChildren()) {
            if (n instanceof Sphere) {
                Planet planet = (Planet) n.getUserData();
                if (except.contains(planet)) {
                    ((Sphere) n).materialProperty().setValue(new PhongMaterial(color));
                } else {
                    ((Sphere) n).materialProperty().setValue(new PhongMaterial(Color.rgb(35, 35, 35, 0.2)));

                }
            } else if (n instanceof Cylinder) {
                Pair info = (Pair) n.getUserData();
                Planet planet1 = (Planet) info.getKey();
                Planet planet2 = (Planet) info.getValue();
                if (except.contains(planet1) && except.contains(planet2)) {
                    ((Cylinder) n).materialProperty().setValue(new PhongMaterial(color));
                    ((Cylinder) n).radiusProperty().setValue(3);
                }
            }
        }
    }

    private void showComponentFinderDialog() {

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Component Finder");

        ButtonType confirmButton = new ButtonType("Draw Component", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 60, 20, 20));

        List<String> choices = spaceGraph.getGraph().getPlanets()
                .stream()
                .map(Planet::getData)
                .sorted()
                .collect(Collectors.toList());

        ComboBox<String> planetChoice = new ComboBox<>();
        planetChoice.getItems().addAll(choices);
        planetChoice.setPromptText("Choose");

        grid.add(new Label("Node:"), 0, 0);
        grid.add(planetChoice, 1, 0);

        grid.add(new Label("Component size:"), 0, 2);

        Text resultText = new Text("Not enough information");
        grid.add(resultText, 1, 2, 2, 1);
        planetChoice.setOnAction(event -> {
            grid.getChildren().remove(resultText);
            resultText.setText(getDFSInfo(planetChoice.getValue()));
            grid.add(resultText, 1, 2, 2, 1);
        });

        Label placeHolder1 = new Label("");
        Label placeHolder2 = new Label("");

        grid.add(placeHolder1, 0, 1);
        grid.add(placeHolder2, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButton) {
                return planetChoice.getValue();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(res -> {
            makeDefaultState();
            runComponentModule(res);
        });
    }

    private void showLegendDialog() {

        GridPane pane = new GridPane();
        pane.setStyle("-fx-background-color: black; -fx-border-color: white");
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(20, 20, 20, 20));

        Map<String, Color> products = spaceGraph.getGraph().getProductsMap();
        List<String> productNames = new ArrayList<>(products.keySet());

        int cols = (int) Math.ceil(Math.sqrt(productNames.size()));
        int rows = productNames.size() / cols;
        int rem = productNames.size() % cols;

        GridPane field;

        int idx = 0;
        int r = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Text t = new Text(productNames.get(idx));
                t.setFill(Color.WHITE);
                Sphere sphere = new Sphere(5);
                PhongMaterial mat = new PhongMaterial(products.get(productNames.get(idx)));
                sphere.setMaterial(mat);
                field = new GridPane();
                field.setHgap(10);
                field.add(sphere, 0, 0);
                field.add(t, 1, 0);
                pane.add(field, i, j);
                idx += 1;
                r = i;
            }
        }

        if (rem > 0) {
            r += 1;
            for (int j = 0; j < rem; j++) {
                Text t = new Text(productNames.get(idx));
                t.setFill(Color.WHITE);
                Sphere sphere = new Sphere(5);
                PhongMaterial mat = new PhongMaterial(products.get(productNames.get(idx)));
                sphere.setMaterial(mat);
                field = new GridPane();
                field.setHgap(10);
                field.add(sphere, 0, 0);
                field.add(t, 1, 0);
                pane.add(field, r, j);
                idx += 1;
            }
        }
        Scene secondScene = new Scene(pane);
        Stage secondStage = new Stage();
        secondStage.setTitle("Legend");
        secondStage.setResizable(false);
        secondStage.setScene(secondScene);

        secondStage.show();
    }
}

/**
 * Udrziava referencie na vsetky obrazky v GUI paneli aplikacie
 */
class Images {

    static ImageView LOGO = getlogo();

    private static ImageView getlogo() {
        Image image = new Image(MenuPanel.class.getResourceAsStream("sources/images/logo.png"));
        ImageView view = new ImageView(image);
        view.setFitHeight(80);
        view.setFitWidth(80);
        view.setOnMouseClicked(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About project");
            alert.setHeaderText(null);
            String text = "Project for class Programovanie (4) at FMFI UK.\n\n" +
                    "Author: Juraj Vetrák\n" +
                    "Idea by: Lukáš Gajdošech\n" +
                    "Date: May 2019";
            alert.setContentText(text);
            alert.showAndWait();
        });
        return view;
    }
}

/**
 * Udrziava referencie na vsetky tlacidla v GUI paneli aplikacie
 */
class Buttons {

    static Button TRANSPORT = getTransportButton();
    static Button ROTATION = getPlanetRotationButton();
    static Button LOADER = getFileChooserButton();
    static Button PATHFINDER = getPathFinderButton();
    static Button COMPONENTFINDER = getComponentFinderButton();
    static Button ALLCOMPONENTS = getAllComponentsButton();
    static Button RELOAD = getReloadButton();
    static Button LEGEND = getShowLegendButton();

    private static final String style = "-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; -fx-font-size: 12";

    private static Button getTransportButton() {
        Button transportButton = new Button("Turn Transport ON");
        transportButton.setPrefWidth(160);
        transportButton.setStyle(style);
        return transportButton;
    }

    private static Button getPlanetRotationButton() {

        Button planetRotationButton = new Button("Turn Rotation ON");
        planetRotationButton.setPrefWidth(160);
        planetRotationButton.setStyle(style);
        return planetRotationButton;
    }

    private static Button getFileChooserButton() {
        Button loadGraphButton = new Button("Load File");
        loadGraphButton.setPrefWidth(160);
        loadGraphButton.setStyle(style);
        return loadGraphButton;
    }

    private static Button getPathFinderButton() {
        Button buttonPathFinder = new Button("Find Path");
        buttonPathFinder.setPrefWidth(160);
        buttonPathFinder.setStyle(style);
        return buttonPathFinder;
    }

    private static Button getComponentFinderButton() {
        Button buttonComponentFinder = new Button("Find Component");
        buttonComponentFinder.setPrefWidth(160);
        buttonComponentFinder.setStyle(style);
        return buttonComponentFinder;
    }

    private static Button getAllComponentsButton() {
        Button buttonAllComponents = new Button("All Components");
        buttonAllComponents.setPrefWidth(160);
        buttonAllComponents.setStyle(style);
        return buttonAllComponents ;
    }

    private static Button getReloadButton() {
        Button reloadButton = new Button("Reload Graph");
        reloadButton.setPrefWidth(160);
        reloadButton.setStyle(style);
        return reloadButton;
    }

    private static Button getShowLegendButton() {
        Button buttonLegend = new Button("Show All");
        buttonLegend.setPrefWidth(160);
        buttonLegend.setStyle(style);
        return buttonLegend;
    }
}