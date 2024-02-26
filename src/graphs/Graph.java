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

    public static <NodeT extends Node<NodeT>> void loadFromJson(Graph<NodeT> graph, JsonObject json, NodeLoader<NodeT> loader){
        if(graph.nodes.size() != 0){
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