import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Graph {
    public class Node {
        private final LinkedList<Node> connectedNodes = new LinkedList<>();
        public Node() {
            nodes.add(this);
        }

        public List<Node> getConnectedNodes() {
            return Collections.unmodifiableList(connectedNodes);
        }

        public Graph getParentGraph(){
            return Graph.this;
        }

        public void connectToOneWay(Node other){
            if(other.getParentGraph() != getParentGraph()) {
                throw new InvalidParameterException("Cannot connect to a node of another graph.");
            }
            connectedNodes.add(other);
        }

        public void connectTo(Node other){
            if(other.getParentGraph() != getParentGraph()) {
                throw new InvalidParameterException("Cannot connect to a node of another graph.");
            }
            other.connectToOneWay(this);
            connectToOneWay(other);
        }

        public void severConnectionOneWay(Node other){
            connectedNodes.remove(other);
        }

        public void severConnection(Node other){
            severConnectionOneWay(other);
            other.severConnectionOneWay(this);
        }
    }

    private final LinkedList<Node> nodes = new LinkedList<>();

    public List<Node> getNodes(){
        return Collections.unmodifiableList(nodes);
    }
}