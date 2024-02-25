import com.google.gson.JsonObject;
import graphs.Graph;

import java.util.Random;

public class TrafficGraph extends Graph<Intersection> {
    public record Connection(Intersection a, Intersection b){
    }

    public Connection[][] validObstaclePlacements;
    public Intersection endNode;
    public Intersection startNode;
    public static void loadTrafficGraphFromJson(JsonObject json){
        TrafficGraph graph = new TrafficGraph();
        Graph.loadFromJson(graph, json, Intersection.loader);

        graph.startNode = graph.nodes.get(json.get("startNodeIndex").getAsInt());
        graph.endNode = graph.nodes.get(json.get("endNodeIndex").getAsInt());


        graph.validObstaclePlacements =
                json.getAsJsonArray("obstaclePlacements")
                        .asList()
                        .stream()
                        .map(connectionSet ->
                                connectionSet
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
    }
    public void setBaseState(){
        for(Intersection intersection : nodes){
            intersection.setBaseState();
        }
    }

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
            if(node.getConnectedNodes().stream().noneMatch(n ->
                    n.squareDistanceTo(endNode) < node.squareDistanceTo(endNode)
            )){
                continue;
            }

            for(Intersection connected : node.getConnectedNodes()){
                if(connected.squareDistanceTo(endNode) < node.squareDistanceTo(endNode)){
                    node.severConnectionOneWay(connected);
                }
            }
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
