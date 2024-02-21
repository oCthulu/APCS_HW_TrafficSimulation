public class Main {
    public static void main(String[] args) {
        Graph graph = new Graph();
        Graph.Node nodeA = graph.new Node();
        Graph.Node nodeB = graph.new Node();

        nodeA.connectTo(nodeB);


    }
}