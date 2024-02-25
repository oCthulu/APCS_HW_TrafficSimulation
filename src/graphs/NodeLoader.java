package graphs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@FunctionalInterface
public interface NodeLoader<NodeT extends Node<NodeT>> {
    NodeT load(JsonObject node, Graph<NodeT> graph);
}
