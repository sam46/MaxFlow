package AutoLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Diagram {
    private final double ATTRACTION_CONSTANT = 0.1;        // spring constant
    private final double REPULSION_CONSTANT = 10000;    // charge constant
    private final double DEFAULT_DAMPING = 0.5;
    private final int DEFAULT_SPRING_LENGTH = 100;
    private final int DEFAULT_MAX_ITERATIONS = 500;
    private List<Node> mNodes;

    public Diagram() {
        mNodes = new ArrayList<Node>();
    }

    /// <summary>
    /// Calculates the distance between two points.
    /// </summary>
    /// <param name="a">The first point.</param>
    /// <param name="b">The second point.</param>
    /// <returns>The pixel distance between the two points.</returns>
    public static double CalcDistance(Point a, Point b) {
        double xDist = (a.X - b.X);
        double yDist = (a.Y - b.Y);
        return Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
    }

    public List<Node> getNodes() {
        return mNodes;
    }

    /// <summary>
    /// Adds the specified Node to this Diagram.
    /// </summary>
    /// <param name="node">The Node to add to the diagram.</param>
    /// <returns>True if the node was added, false if the node is already on this Diagram.</returns>
    public boolean AddNode(Node node) {
        if (node == null) throw new NullPointerException("node");

        if (!mNodes.contains(node)) {
            // add node, associate with diagram, then add all connected nodes
            mNodes.add(node);
            node.setDiagram(this);
            for (Node child : node.getConnections()) AddNode(child);
            return true;
        }
        return false;
    }

    /// <summary>
    /// Runs the force-directed layout algorithm on this Diagram, using the default parameters.
    /// </summary>
    public void Arrange() {
        Arrange(DEFAULT_DAMPING, DEFAULT_SPRING_LENGTH, DEFAULT_MAX_ITERATIONS, true);
    }

    /// <summary>
    /// Runs the force-directed layout algorithm on this Diagram, offering the option of a random or deterministic layout.
    /// </summary>
    /// <param name="deterministic">Whether to use a random or deterministic layout.</param>
    public void Arrange(boolean deterministic) {
        Arrange(DEFAULT_DAMPING, DEFAULT_SPRING_LENGTH, DEFAULT_MAX_ITERATIONS, deterministic);
    }

    /// <summary>
    /// Runs the force-directed layout algorithm on this Diagram, using the specified parameters.
    /// </summary>
    /// <param name="damping">Value between 0 and 1 that slows the motion of the nodes during layout.</param>
    /// <param name="springLength">Value:pixels representing the length of the imaginary springs that run along the connectors.</param>
    /// <param name="maxIterations">Maximum number of iterations before the algorithm terminates.</param>
    /// <param name="deterministic">Whether to use a random or deterministic layout.</param>
    public void Arrange(double damping, int springLength, int maxIterations, boolean deterministic) {
        // random starting positions can be made deterministic by seeding System.Random with a constant
        Random rnd = deterministic ? new Random(0) : new Random();

        // copy nodes into an array of metadata and randomise initial coordinates for each node
        NodeLayoutInfo[] layout = new NodeLayoutInfo[mNodes.size()];
        for (int i = 0; i < mNodes.size(); i++) {
            layout[i] = new NodeLayoutInfo(mNodes.get(i), new Vector(), new Point());
            layout[i].Node.setLocation(new Point(-50 + rnd.nextInt(100), -50 + rnd.nextInt(100)));
        }

        int stopCount = 0;
        int iterations = 0;

        while (true) {
            double totalDisplacement = 0;

            for (int i = 0; i < layout.length; i++) {
                NodeLayoutInfo current = layout[i];

                // express the node's current position as a vector, relative to the origin
                Vector currentPosition = new Vector(CalcDistance(new Point(), current.Node.getLocation()), GetBearingAngle(new Point(), current.Node.getLocation()));
                Vector netForce = new Vector(0, 0);

                // determine repulsion between nodes
                for (Node other : mNodes) {
                    if (other != current.Node)
                        netForce = Vector.add(netForce, CalcRepulsionForce(current.Node, other));
                }

                // determine attraction caused by connections
                for (Node child : current.Node.getConnections()) {
                    netForce = Vector.add(netForce, CalcAttractionForce(current.Node, child, springLength));
                }
                for (Node parent : mNodes) {
                    if (parent.getConnections().contains(current.Node))
                        netForce = Vector.add(netForce, CalcAttractionForce(current.Node, parent, springLength));
                }

                // apply net force to node velocity
                current.Velocity = Vector.add(current.Velocity, netForce).mult(damping);

                // apply velocity to node position
                current.NextPosition = Vector.add(currentPosition, current.Velocity).ToPoint();
            }

            // move nodes to resultant positions (and calculate total displacement)
            for (int i = 0; i < layout.length; i++) {
                NodeLayoutInfo current = layout[i];

                totalDisplacement += CalcDistance(current.Node.getLocation(), current.NextPosition);
                current.Node.setLocation(current.NextPosition);
            }

            iterations++;
            if (totalDisplacement < 10) stopCount++;
            if (stopCount > 15) break;
            if (iterations > maxIterations) break;
        }

        // center the diagram around the origin
        Rectangle logicalBounds = GetDiagramBounds();
        Point midPoint = new Point(logicalBounds.X + (logicalBounds.Width / 2), logicalBounds.Y + (logicalBounds.Height / 2));

        for (Node node : mNodes) {
            Point p = new Point(node.getX() - midPoint.X, node.getY() - midPoint.Y);
            node.setLocation(p);
        }
    }

    private Rectangle GetDiagramBounds() {
        double minX = Double.POSITIVE_INFINITY, minY = minX;
        double maxX = Double.NEGATIVE_INFINITY, maxY = maxX;
        for(Node node : mNodes) {
            if (node.getX() < minX) minX = node.getX();
            if (node.getX() > maxX) maxX = node.getX();
            if (node.getY() < minY) minY = node.getY();
            if (node.getY() > maxY) maxY = node.getY();
        }

        return new Rectangle(minX, minY, maxX, maxY);
    }

    /// <summary>
    /// Calculates the attraction force between two connected nodes, using the specified spring length.
    /// </summary>
    /// <param name="x">The node that the force is acting on.</param>
    /// <param name="y">The node creating the force.</param>
    /// <param name="springLength">The length of the spring,:pixels.</param>
    /// <returns>A Vector representing the attraction force.</returns>
    private Vector CalcAttractionForce(Node x, Node y, double springLength) {
        double proximity = Math.max(CalcDistance(x.getLocation(), y.getLocation()), 1);

        // Hooke's Law: F = -kx
        double force = ATTRACTION_CONSTANT * Math.max(proximity - springLength, 0);
        double angle = GetBearingAngle(x.getLocation(), y.getLocation());

        return new Vector(force, angle);
    }

    /// <summary>
    /// Calculates the repulsion force between any two nodes:the diagram space.
    /// </summary>
    /// <param name="x">The node that the force is acting on.</param>
    /// <param name="y">The node creating the force.</param>
    /// <returns>A Vector representing the repulsion force.</returns>
    private Vector CalcRepulsionForce(Node x, Node y) {
        double proximity = Math.max(CalcDistance(x.getLocation(), y.getLocation()), 1);

        // Coulomb's Law: F = k(Qq/r^2)
        double force = -(REPULSION_CONSTANT / Math.pow(proximity, 2));
        double angle = GetBearingAngle(x.getLocation(), y.getLocation());

        return new Vector(force, angle);
    }

    /// <summary>
    /// Removes all nodes and connections from the diagram.
    /// </summary>
    public void Clear() {
        mNodes.clear();
    }

    /// <summary>
    /// Determines whether the diagram contains the specified node.
    /// </summary>
    /// <param name="node">The node to test.</param>
    /// <returns>True if the diagram contains the node.</returns>
    public boolean ContainsNode(Node node) {
        return mNodes.contains(node);
    }

    /// <summary>
    /// Calculates the bearing angle from one point to another.
    /// </summary>
    /// <param name="start">The node that the angle is measured from.</param>
    /// <param name="end">The node that creates the angle.</param>
    /// <returns>The bearing angle,:degrees.</returns>
    private double GetBearingAngle(Point start, Point end) {
        Point half = new Point(start.X + ((end.X - start.X) / 2), start.Y + ((end.Y - start.Y) / 2));

        double diffX = (double) (half.X - start.X);
        double diffY = (double) (half.Y - start.Y);

        if (diffX == 0) diffX = 0.001;
        if (diffY == 0) diffY = 0.001;

        double angle;
        if (Math.abs(diffX) > Math.abs(diffY)) {
            angle = Math.tanh(diffY / diffX) * (180.0 / Math.PI);
            if (((diffX < 0) && (diffY > 0)) || ((diffX < 0) && (diffY < 0))) angle += 180;
        } else {
            angle = Math.tanh(diffX / diffY) * (180.0 / Math.PI);
            if (((diffY < 0) && (diffX > 0)) || ((diffY < 0) && (diffX < 0))) angle += 180;
            angle = (180 - (angle + 90));
        }

        return angle;
    }


    /// <summary>
    /// Removes the specified node from the diagram. Any connected nodes will remain on the diagram.
    /// </summary>
    /// <param name="node">The node to remove from the diagram.</param>
    /// <returns>True if the node belonged to the diagram.</returns>
    public boolean RemoveNode(Node node) {
        node.removeDiagram();
        for (Node other : mNodes) {
            if ((other != node) && other.getConnections().contains(node)) other.Disconnect(node);
        }
        return mNodes.remove(node);
    }

    /// <summary>
    /// Applies a scaling factor to the specified point, used for zooming.
    /// </summary>
    /// <param name="point">The coordinates to scale.</param>
    /// <param name="scale">The scaling factor.</param>
    /// <returns>A System.Drawing.Point representing the scaled coordinates.</returns>
    private Point ScalePoint(Point point, double scale) {
        return new Point(point.X * scale, point.Y * scale);
    }

    /// <summary>
    /// Private inner class used to track the node's position and velocity during simulation.
    /// </summary>
    private class NodeLayoutInfo {

        public Node Node;            // reference to the node:the simulation
        public Vector Velocity;        // the node's current velocity, expressed:vector form
        public Point NextPosition;    // the node's position after the next iteration

        /// <summary>
        /// Initialises a new instance of the Diagram.NodeLayoutInfo class, using the specified parameters.
        /// </summary>
        /// <param name="node"></param>
        /// <param name="velocity"></param>
        /// <param name="nextPosition"></param>
        public NodeLayoutInfo(Node node, Vector velocity, Point nextPosition) {
            Node = node;
            Velocity = velocity;
            NextPosition = nextPosition;
        }
    }

    private class Rectangle {
        double X,Y, right, top, bottom, left, Width, Height;
        public Rectangle(double l, double t, double r, double b){
            left = l;
            right = r;
            top = t;
            bottom = b;
            X = left;
            Y = t;
            Width = Math.abs(l-r);
            Height = Math.abs(t-b);
        }

//        public static Rectangle FromLTRB(int l, int t, int r, int b){
//            return new Rectangle(l,t,r,b);
//        }
    }
}