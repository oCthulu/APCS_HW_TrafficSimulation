package graphs;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.*;

public class Graph<NodeT extends Node<NodeT>> {
    protected ArrayList<NodeT> nodes = new ArrayList<>();

    public void addNode(NodeT node){
        nodes.add(node);
    }

    public List<NodeT> getNodes(){
        return Collections.unmodifiableList(nodes);
    }

    public static <NodeT extends Node<NodeT>> void loadFromJson(Graph<NodeT> graph, JsonObject json, NodeLoader<NodeT> loader){
        JsonArray jsonNodes = json.getAsJsonArray("nodes");

        for(JsonElement element : jsonNodes) {
            graph.addNode(loader.load((JsonObject) element, graph));
        }

        for (int i = 0; i < graph.nodes.size(); i++) {
            graph.nodes.get(i).connectedNodes =
                    new LinkedList<>(
                    ((JsonObject)jsonNodes.get(i))
                            .getAsJsonArray("connectedTo")
                            .asList().stream().map(
                                    index -> graph.nodes.get(index.getAsInt())
                            ).toList()
                    );
        }
    }

    public static <NodeT extends Node<NodeT>> Graph<NodeT> loadFromJson(JsonObject json, NodeLoader<NodeT> loader){
        Graph<NodeT> graph = new Graph<>();
        loadFromJson(graph, json, loader);
        return graph;
    }
}