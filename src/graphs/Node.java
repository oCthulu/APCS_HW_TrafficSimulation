package graphs;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Node<SELF extends Node<SELF>> {
    public final Graph<SELF> parent;
    protected LinkedList<SELF> connectedNodes = new LinkedList<>();
    public Node(Graph<SELF> parent) {
        this.parent = parent;

        //noinspection unchecked
        parent.addNode((SELF) this);
    }

    public List<SELF> getConnectedNodes() {
        return Collections.unmodifiableList(connectedNodes);
    }

    public void connectToOneWay(SELF other){
        if(other.parent != parent) {
            throw new InvalidParameterException("Cannot connect to a node of another graph.");
        }
        connectedNodes.add(other);
    }

    public void connectTo(SELF other){
        if(other.parent != parent) {
            throw new InvalidParameterException("Cannot connect to a node of another graph.");
        }
        //noinspection unchecked
        other.connectToOneWay((SELF) this);
        connectToOneWay(other);
    }

    public void severConnectionOneWay(SELF other){
        connectedNodes.remove(other);
    }

    public void severConnection(SELF other){
        severConnectionOneWay(other);
        //noinspection unchecked
        other.severConnectionOneWay((SELF) this);
    }
}