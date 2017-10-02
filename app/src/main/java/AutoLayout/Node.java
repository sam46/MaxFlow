package AutoLayout;

import java.util.ArrayList;
import java.util.List;

public class Node {

    Diagram mDiagram;            // the parent diagram
    Point mLocation;            // node position, relative to the origin
    List<Node> mConnections;    // list of references to connected nodes (children)


    public Node() {
        mLocation = new Point();
        mDiagram = null;
        mConnections = new ArrayList<Node>();
    }

    public Point getLocation() {
        return mLocation;
    }

    public void setLocation(Point value) {
        mLocation = value;
    }

    public List<Node> getConnections() {
        return mConnections;
    }

    public Diagram getDiagram() {
        return mDiagram;
    }

    public void setDiagram(Diagram value) {
        if (mDiagram == value) return;
        if (mDiagram != null) mDiagram.RemoveNode(this);
        mDiagram = value;
        if (mDiagram != null) mDiagram.AddNode(this);
    }

    public void removeDiagram() {
        mDiagram = null;
    }

    public double getX() {
        return mLocation.X;
    }

    public void setX(double value) {
        mLocation.X = value;
    }

    public double getY() {
        return mLocation.Y;
    }

    public void setY(double value) {
        mLocation.Y = value;
    }

    /// <summary>
    /// Connects the specified child node to this node.
    /// </summary>
    /// <param name="child">The child node to add.</param>
    /// <returns>True if the node was connected to this node.</returns>
    public boolean AddChild(Node child) {
        if (child == null) throw new NullPointerException("child");
        if ((child != this) && !this.mConnections.contains(child)) {
            child.setDiagram(this.getDiagram());
            this.mConnections.add(child);
            return true;
        }
        return false;
    }

    public boolean AddParent(Node parent) {
        if (parent == null) throw new NullPointerException("parent");
        return parent.AddChild(this);
    }

    /// <summary>
    /// Removes any connection between this node and the specified node.
    /// </summary>
    /// <param name="other">The other node whose connection is to be removed.</param>
    /// <returns>True if a connection existed.</returns>
    public boolean Disconnect(Node other) {
        boolean c = this.mConnections.remove(other);
        boolean p = other.mConnections.remove(this);
        return c || p;
    }
}