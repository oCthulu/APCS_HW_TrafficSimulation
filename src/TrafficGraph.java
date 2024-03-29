import com.google.gson.JsonObject;
import graphs.Graph;
import graphs.Node;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * A subclass of {@link Graph} which has various helper methods and attributes for simulating traffic.
 * For APCSA project due 2/27/24.
 * @author Andrew Denton
 * @author Kieran Chalk
 */
public class TrafficGraph extends Graph<Intersection> {
    public record Connection(Intersection a, Intersection b){
    }

    /**
     * The valid connections to place obstacles on. This double array could potentially be jagged.
     * Each row represents a set of connections "packaged" together, i.e., all of said connections will be broken
     * together. This is useful for ensuring the forward and reverse connections are broken.
     * */
    public Connection[][] validObstaclePlacements;
    public Intersection endNode;
    public Intersection startNode;
    public static TrafficGraph loadTrafficGraphFromJson(JsonObject json){
        TrafficGraph graph = new TrafficGraph();
        Graph.loadFromJson(graph, json, Intersection.loader);

        graph.startNode = graph.nodes.get(json.get("startNodeIndex").getAsInt());
        graph.endNode = graph.nodes.get(json.get("endNodeIndex").getAsInt());


        graph.validObstaclePlacements =
                json.getAsJsonArray("obstaclePlacements")
                        .asList()
                        .stream()
                        .map(connectionSet -> connectionSet
                                .getAsJsonArray()
                                .asList()
                                .stream()
                                .map(connection -> new Connection(
                                        graph.nodes.get(connection.getAsJsonObject().get("a").getAsInt()),
                                        graph.nodes.get(connection.getAsJsonObject().get("b").getAsInt())
                                ))
                                .toArray(Connection[]::new)
                        )
                        .toArray(Connection[][]::new);

        graph.setBaseState();

        return graph;
    }

    /**
     * Set the base state (i.e. the state with no obstacles or baked logic) to the current state
     */
    public void setBaseState(){
        for(Intersection intersection : nodes){
            intersection.setBaseState();
        }
    }

    /**
     * Set the current state to the base state (i.e. the state with no obstacles or baked logic)
     */
    public void restoreBaseState(){
        for(Intersection intersection : nodes){
            intersection.restoreBaseState();
        }
    }

    public void placeObstacles(int obstacleCount, Random random) {
        int obstaclesPlaced = 0;
        for(int i = 0; i < validObstaclePlacements.length; i++) {
            double probability = ((float) (obstacleCount - obstaclesPlaced)) / (validObstaclePlacements.length - i);
            if (random.nextDouble() < probability) {
                obstaclesPlaced++;
                for (Connection connection : validObstaclePlacements[i]) {
                    connection.a.severConnection(connection.b);
                }
            }
        }
    }

    public void bakeDecisionLogic(){
        for(Intersection node : nodes){
            List<Intersection> newNodes =
                    node.getConnectedNodes()
                        .stream()
                        .filter(connected -> connected.squareDistanceTo(endNode) < node.squareDistanceTo(endNode))
                        .toList();

            if(newNodes.isEmpty()) continue;

            node.setConnectedNodes(newNodes);
        }
    }

    public int runTraverseSimulation(int maxTurns, Random random){
        Intersection currentNode = startNode;

        for (int i = 0; i< maxTurns; i++){
            if (currentNode.getConnectedNodes().isEmpty()){
                return maxTurns;
            }
            if (currentNode == endNode){
                return i;
            }
            int randomNode = random.nextInt(currentNode.getConnectedNodes().size());
            currentNode = currentNode.getConnectedNodes().get(randomNode);
        }
        return maxTurns;
    }
}
