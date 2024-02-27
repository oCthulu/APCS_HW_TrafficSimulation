import graphs.Graph;
import graphs.Node;
import graphs.NodeLoader;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * A subtype of {@link Node} which stores various information important to simulating traffic.
 * For APCSA project due 2/27/24.
 * @author Andrew Denton
 * @author Kieran Chalk
 */
public class Intersection extends Node<Intersection> {
    public static final NodeLoader<Intersection> loader = (node, graph) -> {
        return new Intersection(
                graph,
                node.get("x").getAsDouble(),
                node.get("y").getAsDouble()
        );
    };

    public double xPosition;
    public double yPosition;

    /**The connections this intersection has in its base state (i.e. the state with no obstacles or baked logic)*/
    private Intersection[] baseConnections = new Intersection[0];
    public Intersection(Graph<Intersection> parent, double xPosition, double yPosition) {
        super(parent);
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }

    /**
     * Set the base state (i.e. the state with no obstacles or baked logic) to the current state
     */
    public void setBaseState(){
        baseConnections = new Intersection[connectedNodes.size()];
        connectedNodes.toArray(baseConnections);
    }

    /**
     * Set the current state to the base state (i.e. the state with no obstacles or baked logic)
     */
    public void restoreBaseState(){
        connectedNodes = new LinkedList<>(Arrays.asList(baseConnections));
    }

    public double squareDistanceTo(Intersection other){
        return ((other.xPosition - xPosition)*(other.xPosition - xPosition)) +
                ((other.yPosition - yPosition)*(other.yPosition - yPosition));
    }
}
