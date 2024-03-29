package graphs;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.*;

/**
 * Graph class. Stores an arbitrary arrangement of connected nodes extending from {@link NodeT}.
 * For APCSA project due 2/27/24.
 * @author Andrew Denton
 * @author Kieran Chalk
 * @param <NodeT> The type of node to store.
 */
public class Graph<NodeT extends Node<NodeT>> {
    protected ArrayList<NodeT> nodes = new ArrayList<>();

    public void addNode(NodeT node){
        nodes.add(node);
    }

    public List<NodeT> getNodes(){
        return Collections.unmodifiableList(nodes);
    }

    /**
     * Loads a JSON file into an existing graph
     * @param graph The graph to load the data into
     * @param json The json object to pull the data from
     * @param loader A delegate which turns a {@link JsonObject} representing a node into a node object
     * @param <NodeT> The node type of the graph
     * @throws InvalidParameterException When the graph has nodes in it already.
     */
    public static <NodeT extends Node<NodeT>> void loadFromJson(Graph<NodeT> graph, JsonObject json, NodeLoader<NodeT> loader){
        if(!graph.nodes.isEmpty()){
            throw new InvalidParameterException("\"graph\" parameter must have zero nodes.");
        }

        JsonArray jsonNodes = json.getAsJsonArray("nodes");

        for(JsonElement element : jsonNodes) {
            loader.load((JsonObject) element, graph);
        }

        for (int i = 0; i < graph.nodes.size(); i++) {
            graph.nodes.get(i).setConnectedNodes(
                    jsonNodes.get(i)
                            .getAsJsonObject()
                            .getAsJsonArray("connectedTo")
                            .asList()
                            .stream()
                            .map(
                                    index -> graph.nodes.get(index.getAsInt())
                            )
                            .toList()
                    );
        }
    }

    public static <NodeT extends Node<NodeT>> Graph<NodeT> loadFromJson(JsonObject json, NodeLoader<NodeT> loader){
        Graph<NodeT> graph = new Graph<>();
        loadFromJson(graph, json, loader);
        return graph;
    }
}