import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GenericGraph<NodeT extends GenericGraph<NodeT>.GenericNode> {
    public abstract class GenericNode {
        private final LinkedList<NodeT> connectedNodes = new LinkedList<>();
        public GenericNode() {
            try {
                @SuppressWarnings("unchecked")
                NodeT self = (NodeT) this;
                nodes.add(self);
            } catch (ClassCastException e) {
                throw new InvalidParameterException(
                        "Cannot add a node of type " +
                                this.getClass().getName() +
                                " to a graph of a differing node type."
                );
            }
        }

        public List<NodeT> getConnectedNodes() {
            return Collections.unmodifiableList(connectedNodes);
        }
    }

    private final LinkedList<NodeT> nodes = new LinkedList<>();

    public List<NodeT> getNodes(){
        return Collections.unmodifiableList(nodes);
    }
}