import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape3D;

import java.io.File;
import java.util.*;

public class Graph {

    private List<Planet> planets;
    private List<Link> links;
    private List<Product> products;
    private File loadedFile;
    private Map<String, Color> productNamesAndColors = new TreeMap<>();

    public Graph(File file) {
        planets = new ArrayList<>();
        links = new ArrayList<>();
        Utils.loadGraphFromFile(file, this);
        loadProductNamesAndColors();
        products = loadProducts();
        loadedFile = file;

    }

    private void loadProductNamesAndColors() {
        for (Link link: getLinks()) {
            productNamesAndColors.put(link.product, Utils.getRandomColor());
        }
    }

    private Color getProductColor(String productName)  {
        return productNamesAndColors.get(productName);
    }

    private List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        for (Link link: getLinks()) {
            products.add(new Product(link.getProduct(), getProductColor(link.getProduct()), link));
        }
        return products;
    }

    /**
     * Vlozi novy vrchol (Planet) do grafu
     * @param planet instancia vrcholu (Planet)
     */
    public void insertPlanet(Planet planet) {
        planets.add(planet);
    }

    /**
     * Do grafu vlozi novy spoj (Link) medzi dvoma vrcholmi
     * @param link instancia spoju (Link)
     */
    public void insertLink(Link link) {
        links.add(link);
    }

    /**
     * Vrati zoznam vsetkych instancii tovarov, ktore sa presuvaju medzi vrcholmi (Planet)
     * @return zoznam instancii tovarov
     */
    public List<Product> getProducts() {
        return products;
    }

    /**
     *
     * @return mapa nazvov tovarov a ich prislusnych farieb
     */
    public Map<String, Color> getProductsMap() {
        return productNamesAndColors;
    }

    /**
     * Vrati nacitany subor, z ktoreho bol graf vytvoreny
     * @return subor obsahujuci strukturu daneho grafu
     */
    public File getLoadedFile() {
        return loadedFile;
    }

    /**
     * Nastavi prislusny vstupny subor s informaciami pre dany graf
     * @param file textovy subor
     */
    public void setLoadedFile(File file) {
        loadedFile = file;
    }

    /**
     * Poskytne zoznam vrcholov grafu
     * @return zoznam vrcholov
     */
    public List<Planet> getPlanets() {
        return planets;
    }

    /**
     * Poskytne zoznam hran (spojov) grafu
     * @return zoznam hran (spojov)
     */
    public List<Link> getLinks() {
        return links;
    }
}

class Planet {

    private String data;
    private Point3D coords;
    private Set<Planet> neighbours;
    private double radius;
    private Image texture;
    private Color color;
    private Shape3D shape;

    public Planet(String data, Point3D coords, double radius, Image texture, Color color) {
        this.data = data;
        this.coords = coords;
        this.radius = radius;
        neighbours = new HashSet<>();
        this.texture = texture;
        this.color = color;
        shape = Preparer.preparePlanetSphere(this);
    }

    /**
     *
     * @return prislusny trojrozmerny graficky objekt
     */
    public Shape3D getShape() {
        return shape;
    }

    /**
     *
     * @return nazov vrcholu
     */
    public String getData() {
        return data;
    }

    /**
     *
     * @return mnozina susednych vrcholov daneho vrcholu
     */
    public Set<Planet> getNeighbours() {
        return neighbours;
    }

    /**
     * Prida dany vrchol medzi susedov vrcholu
     * @param neighbour instancia vrcholu (Planet)
     */
    public void addNeigbour(Planet neighbour) {
        neighbours.add(neighbour);
    }

    /**
     *
     * @return x-ova suradnica
     */
    public double getX() {
        return coords.getX();
    }

    /**
     *
     * @return y-ova suradnica
     */
    public double getY() {
        return coords.getY();
    }

    /**
     *
     * @return z-ova suradnica
     */
    public double getZ() {
        return coords.getZ();
    }

    /**
     *
     * @return polomer planety (vrcholu)
     */
    public double getRadius() {
        return radius;
    }

    /**
     *
     * @return textura planety
     */
    public Image getTexture() {
        return texture;
    }

    /**
     *
     * @return farba textury, resp. 3D grafickeho objektu
     */
    public Color getColor() {
        return color;
    }

    /**
     *
     * @return koordinaty vrcholu v 3D priestore ako instancia Point3D
     */
    public Point3D getCoords() {
        return coords;
    }

    @Override
    public String toString() {
        return "Planet(" + data + ", " + getX() + ", " + getY() + ", " + getZ() + ")";
    }
}

class Link {

    Planet start;
    Planet end;
    String product;

    public Link(Planet start, Planet end, String product) {
        this.start = start;
        this.end = end;
        this.product = product;
    }

    /**
     *
     * @return instancia pociatocneho vrcholu (Planet)
     */
    public Planet getFrom() {
        return start;
    }

    /**
     *
     * @return instancia konecneho vrcholu (Planet)
     */
    public Planet getTo() {
        return end;
    }

    /**
     *
     * @return nazov presuvaneho tovaru na hrane (spoji)
     */
    public String getProduct() {
        return product;
    }

    @Override
    public String toString() {
        return "Link(" + getFrom() + ", " + getTo() + " : " + getProduct() + ")";
    }
}
