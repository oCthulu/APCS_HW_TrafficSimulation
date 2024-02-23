import java.util.Random;
public class GraphUtil {
    private GraphUtil() throws Exception{
        throw new Exception("Cannot create instance of GraphUtil class.");
    }

    static record SquareGraphReturnResult(Graph graph, Graph.Node startingNode, Graph.Node endNode){
    }

    public static int runTraverseSimulation(Graph currentGraph, Graph.Node startNode, Graph.Node endNode, Random nodeChoiceRandom){
        Graph.Node currentNode = startNode;
        int maximumAmountOfTurns = 100;

        for (int i = 0; i< maximumAmountOfTurns; i++){
            if (currentNode.getConnectedNodes().isEmpty()){
                return maximumAmountOfTurns;
            }
            if (currentNode == endNode){
                return i;
            }
            int randomNode = nodeChoiceRandom.nextInt(currentNode.getConnectedNodes().size());
            currentNode = currentNode.getConnectedNodes().get(randomNode);
        }
        return maximumAmountOfTurns;
    }


}
