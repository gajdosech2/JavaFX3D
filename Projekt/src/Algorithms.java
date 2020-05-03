import javafx.util.Pair;

import java.util.*;

public class Algorithms {


    /**
     * Pomocou algoritmu BFS najde najkratsiu cestu medzi dvoma vrcholmi
     * @param g instancia grafu
     * @param planetName1 nazov startoveho vrcholu
     * @param planetName2 nazov konecneho vrcholu
     * @return usporiadany zoznam vrcholov tvoriacich najkratsiu cestu
     */
    public static List<Planet> shortestPath(Graph g, String planetName1, String planetName2) {

        Planet planet1 = findPlanet(g, planetName1);
        Planet planet2 = findPlanet(g, planetName2);

        if (planet1 == null || planet2 == null) { // nenastane
            return new ArrayList<>();
        }

        Set<Planet> visited = new HashSet<>();
        HashMap<Planet, Planet> predecessors = new HashMap<>();
        Queue<Pair<Planet, Planet>> frontier = new LinkedList<>();
        frontier.add(new Pair<>(planet1, null));

        while (!frontier.isEmpty()) {
            Pair<Planet, Planet> pair = frontier.poll();
            Planet node1 = pair.getKey();
            Planet predecessor = pair.getValue();
            if (!visited.contains(node1)) {
                visited.add(node1);
                predecessors.put(node1, predecessor);
                if (node1.getData().equals(planet2.getData())) {
                    List<Planet> result = new ArrayList<>();
                    while (node1 != null) {
                        result.add(0, node1);
                        node1 = predecessors.get(node1);
                    }
                    return result;
                }
                for (Planet node2: node1.getNeighbours()) {
                    if (!visited.contains(node2)) {
                        frontier.add(new Pair<>(node2, node1));
                    }
                }

            }
        }
        return new ArrayList<>();
    }

    /**
     * Pomocou algoritmu DFS najde pre zadany vrchol jeho komponent (zoznam vrcholov)
     * @param g instancia grafu
     * @param planetName nazov vrcholu, pre ktory hlada komponent
     * @return zoznam vrcholov tvoriacich komponent pre zadany vrchol
     */
    public static List<Planet> findComponent(Graph g, String planetName) {

        Planet planet = findPlanet(g, planetName);

        if (planet == null) { // nenastane
            return new ArrayList<>();
        }

        List<Planet> result = new ArrayList<>();
        Set<Planet> visited = new HashSet<>();
        Stack<Planet> frontier = new Stack<>();

        frontier.add(planet);

        while (!frontier.isEmpty()) {
            Planet node1 = frontier.pop();
            if (!visited.contains(node1)) {
                visited.add(node1);
                result.add(node1);
                for (Planet node2: node1.getNeighbours()) {
                    if (!visited.contains(node2)) {
                        frontier.add(node2);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Funkcia vypocita celkovy pocet komponentov v grafe
     * @param g instancia grafu
     * @return celkovy pocet komponentov
     */
    public static int numberOfComponents(Graph g) {

        Set<Planet> visited = new HashSet<>();
        int result = 0;
        for (Planet planet: g.getPlanets()) {
            if (!visited.contains(planet)) {
                visited.add(planet);
                visited.addAll(findComponent(g, planet.getData()));
                result += 1;
            }
        }
        return result;
    }

    /**
     * Funkcia vyrobi zoznam vsetkych komponentov grafu, t.j. zoznam zoznamov vrcholov
     * @param g instancia grafu
     * @return zoznam vsetkych komponentov (zoznamov vrcholov) grafu
     */
    public static List<List<Planet>> allComponents(Graph g) {
        List<List<Planet>> result = new ArrayList<>();
        Set<Planet> visited = new HashSet<>();
        for (Planet planet: g.getPlanets()) {
            if (!visited.contains(planet)) {
                visited.add(planet);
                List<Planet> component = findComponent(g, planet.getData());
                visited.addAll(component);
                result.add(component);
            }
        }
        return result;
    }

    /**
     * Funkcia zisti, ci je graf spojity
     * @param g instancia grafu
     * @return true, ak vsetky vrcholy grafu tvoria jeden komponent, inak false
     */
    public static boolean isConnected(Graph g) {

        Planet planet = findPlanet(g, g.getPlanets().get(0).getData());

        if (planet == null) { // nenastane
            return false;
        }

        Set<Planet> visited = new HashSet<>();
        Stack<Planet> frontier = new Stack<>();

        frontier.add(planet);

        while (!frontier.isEmpty()) {
            Planet node1 = frontier.pop();
            if (!visited.contains(node1)) {
                visited.add(node1);
                for (Planet node2: node1.getNeighbours()) {
                    if (!visited.contains(node2)) {
                        frontier.add(node2);
                    }
                }
            }
        }
        return visited.size() == g.getPlanets().size();
    }

    /**
     * Podla zadaneho mena najde jeho prislusnu instanciu v grafe
     * @param g instancia grafu
     * @param name meno hladaneho vrcholu
     * @return instancia vrcholu (Planet)
     */
    public static Planet findPlanet(Graph g, String name) {
        for (Planet planet: g.getPlanets()) {
            if (planet.getData().equals(name)) {
                return planet;
            }
        }
        return null;
    }
}
