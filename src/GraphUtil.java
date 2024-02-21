public class GraphUtil {
    private GraphUtil() throws Exception{
        throw new Exception("Cannot create instance of GraphUtil class.");
    }

    static record SquareGraphReturnResult(Graph graph, Graph.Node startingNode, Graph.Node endNode){
    }


}
