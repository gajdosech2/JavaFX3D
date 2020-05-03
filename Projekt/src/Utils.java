import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Utils {

    /**
     * Nacita obrazky textur planet
     * @param betterQuality ak true, tak vyberie kvalitnejsie obrazky
     * @return zoznam instancii Image obrazkov textur
     */
    public static List<Image> getPlanetTextures(boolean betterQuality) {
        List<Image> textures = new ArrayList<>();
        String dir;
        if (betterQuality) {
            dir = "/sources/textures50/";
        } else {
            dir = "/sources/textures/";
        }
        for (int i = 0; i < 15; i++) {
            String url = dir + i + ".jpg";
            textures.add(new Image(SpaceGraph3D.class.getResourceAsStream(url)));
        }
        return textures;
    }

    /**
     * Zo zoznamu obrazkov textur vrati tu na zadanom indexe
     * @param index index textury v zozname textur
     * @param textures zoznam textur
     * @return instancia Image obrazku textury
     */
    public static Image getTexture(int index, List<Image> textures) {
        return textures.get(index);
    }

    /**
     * Nacita a rozparsuje textovy subor, vytvori nove objekty Planet (vrcholov)
     * a Link (spojov) a vlozi ich do datovej struktury graf
     * v GUI je momentalne striktne obmedzeny vyber vstupneho suboru na tie, co su v zlozke src/sources/graphs
     * @param file vstupny textovy subor
     * @param g instancia grafu
     */
    public static void loadGraphFromFile(File file, Graph g) {
        List<Image> planetTextures = getPlanetTextures(false);
        try (Scanner scanner = new Scanner(file)) {
            String[] data;
            String name, from, to, product;
            Color color;
            double x, y, z, radius;
            int textureIndex;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equals("#####")) {
                    break;
                }
                data = line.split("\\|");
                name = data[0];
                x = Double.parseDouble(data[1]);
                y = Double.parseDouble(data[2]);
                z = Double.parseDouble(data[3]);
                radius = Double.parseDouble(data[4]);
                color = Color.web(data[5]);
                textureIndex = Integer.parseInt(data[6]);
                Planet newPlanet = new Planet(name, new Point3D(x, y, z), radius, getTexture(textureIndex, planetTextures), color);
                g.insertPlanet(newPlanet);
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                data = line.split("\\|");
                from = data[0];
                to = data[1];
                product = data[2];
                Planet planetFrom = Algorithms.findPlanet(g, from);
                Planet planetTo = Algorithms.findPlanet(g, to);
                if (planetFrom != null && planetTo != null) {
                    planetFrom.addNeigbour(planetTo);
                    planetTo.addNeigbour(planetFrom);
                    g.insertLink(new Link(planetFrom, planetTo, product));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return nahodna RGB farba
     */
    public static Color getRandomColor() {
        Random rnd = new Random();
        int r = rnd.nextInt(255);
        int g = rnd.nextInt(255);
        int b = rnd.nextInt(255);
        return Color.rgb(r, g, b);
    }

    /**
     *
     * @param from dolna hranica
     * @param to horna hranica (bez)
     * @return nahodne cele cislo z intervalu
     */
    public static int getRandomInt(int from, int to) {
        return new Random().nextInt(to - from) + from;
    }

    /**
     *
     * @param from dolna hranica
     * @param to horna hranica (bez)
     * @return nahodne desatinne cislo z intervalu
     */
    public static double getRandomDouble(double from, double to) {
        return new Random().nextDouble() * (to - from) + from;
    }
}