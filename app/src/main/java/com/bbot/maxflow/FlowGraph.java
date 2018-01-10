package com.bbot.maxflow;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import AutoLayout.Diagram;
import algos.Digraph;
import algos.DigraphGenerator;
import algos.FFEdge;
import algos.FFFlowNetwork;
import algos.FordFulkerson;

/**
 * Actual on-screen interactive graph
 */
public class FlowGraph {
    private FlowGraphEntity serialized;

    private Clickable focusElem;
    private double minX, minY, maxX, maxY;
    private Map<String, FlowVertex> verts;
    private Map<String, FlowEdge> edges;
    private FlowVertex src, sink;
    private int n = 0;
    private float xrange, yrange;
    private float cx, cy;
    /*  Ford-Fulkerson Stuff  */
    private FFFlowNetwork fordNet;
    private Map<String, Integer> idToInt = new HashMap<>();
    private Map<Integer, String> intToId = new HashMap<>();
    private int curV = 0;
    private int stateNum = 0, maxStateNum = 0;

    FlowGraph(String serialized) {
        this(serialized, "unnamed");
    }

    FlowGraph(FlowGraphEntity serialized) {
        this(serialized.getSerialized(), serialized.getName());
        this.serialized.setId(serialized.getId());
    }

    FlowGraph(String serialized, String name) {
        maxY = Double.MIN_VALUE;
        minY = Double.MAX_VALUE;
        minX = minY;
        maxX = maxY;
        verts = new HashMap<>();
        edges = new HashMap<>();
        this.serialized = new FlowGraphEntity(name, serialized);

        try {
            StringTokenizer reader = new StringTokenizer(serialized, "\n", false);
            StringTokenizer st = new StringTokenizer(reader.nextToken());
            final int N = Integer.parseInt(st.nextToken());
            final int N_EDGES = Integer.parseInt(st.nextToken());
            for (int i = 0; i < N; ++i) {
                st = new StringTokenizer(reader.nextToken());
                addNode(st.nextToken(), Float.parseFloat(st.nextToken()), Float.parseFloat(st.nextToken()));
            }
            fordNet = new FFFlowNetwork(n);
            for (int i = 0; i < N_EDGES; ++i) {
                st = new StringTokenizer(reader.nextToken());
                String u = st.nextToken(), v = st.nextToken();
//                if(!verts.containsKey(u) || !verts.containsKey(v)) throw new Exception();
                addEdge(u, v, Integer.parseInt(st.nextToken()));
            }
            st = new StringTokenizer(reader.nextToken());
            src = verts.get(st.nextToken());
            sink = verts.get(st.nextToken());

        } catch (Exception e) {
            // Todo
            System.out.println("Error in deserialization !!");
        }

        /* compute and store flow steps/states */
        FordFulkerson ford;
        try {
            ford = new FordFulkerson(fordNet, idToInt.get(src.getID()), idToInt.get(sink.getID()));
            while (ford.step(fordNet, idToInt.get(src.getID()), idToInt.get(sink.getID()))) {
                maxStateNum++;
                for (FFEdge fffedge : fordNet.edges()) {
                    FlowEdge fe = edges.get(intToId.get(fffedge.from()) + "-" + intToId.get(fffedge.to()));
                    fe.addState(fe.getFlow(), (int) fffedge.flow());
                    fe.applyState(1);
                }
            }
            for (int i = 0; i < maxStateNum; ++i) // move back to state 0
                for (String key : edges.keySet())
                    edges.get(key).applyState(-1);

            System.out.println("Maxflow = " + (int) ford.value());
        } catch (Exception e) {
        }
        n = verts.size();
        recompute();
    }


    FlowGraph(boolean auto) {
        this(auto, "unnamed");
    }

    /**
     * Actual on-screen interactive graph
     *
     * @param auto Generate a random auto-layedout graph or use default sample graph
     */
    FlowGraph(boolean auto, String name) {
        maxY = Double.MIN_VALUE;
        minY = Double.MAX_VALUE;
        minX = minY;
        maxX = maxY;
        verts = new HashMap<>();
        edges = new HashMap<>();
        fordNet = new FFFlowNetwork(n);

        if (auto) setupAuto();

        /* compute and store flow steps/states */
        FordFulkerson ford;
        try {
            ford = new FordFulkerson(fordNet, idToInt.get(src.getID()), idToInt.get(sink.getID()));
            while (ford.step(fordNet, idToInt.get(src.getID()), idToInt.get(sink.getID()))) {
                maxStateNum++;
                for (FFEdge fffedge : fordNet.edges()) {
                    FlowEdge fe = edges.get(intToId.get(fffedge.from()) + "-" + intToId.get(fffedge.to()));
                    fe.addState(fe.getFlow(), (int) fffedge.flow());
                    fe.applyState(1);
                }
            }
            for (int i = 0; i < maxStateNum; ++i) // move back to state 0
                for (String key : edges.keySet())
                    edges.get(key).applyState(-1);

            System.out.println("Maxflow = " + (int) ford.value());
        } catch (Exception e) {
        }

        /* ************************ */
        if (auto) {
            for (String str : verts.keySet())
                verts.get(str).refreshCoords();
        }
        n = verts.size();
        recompute();

        this.serialized = new FlowGraphEntity(name, serialize());
    }

    public boolean isEmpty() {
        return verts.isEmpty();
    }

    public int getVertCount() {
        return verts.size();
    }

    private void recompute() {
        if (verts.isEmpty()) return;
        for (String str : verts.keySet()) {
            FlowVertex fv = verts.get(str);
            minX = Math.min(minX, fv.x);
            maxX = Math.max(maxX, fv.x);
            minY = Math.min(minY, fv.y);
            maxY = Math.max(maxY, fv.y);
        }

        xrange = (float) (maxX - minX);
        yrange = (float) (maxY - minY);
        cx = (float) minX + (xrange / 2.0f);
        cy = (float) minY + (yrange / 2.0f);
    }

    public void setSrc(FlowVertex fv) {
        src = verts.get(fv.getID());
    }

    public void setSink(FlowVertex fv) {
        sink = verts.get(fv.getID());
    }

    private void setupAuto() {
        int virtualW = 1000, virtualH = 1000;
        Digraph digraph = DigraphGenerator.dag(n, 15);
        Diagram autoLayout = new Diagram();
        for (int u = 0; u < digraph.V(); u++) {
            String uID = "" + (int) u;
            if (!verts.containsKey(uID))
                addNode(uID, autoLayout);
            for (Integer v : digraph.adj(u)) {
                String vID = "" + (int) v;
//                System.out.println(u+" --> "+v);
                if (!verts.containsKey(vID))
                    addNode(vID, autoLayout);
                addEdge(uID, vID);
            }
        }
        sink = verts.get("0");
        src = verts.get("1");
        autoLayout.Arrange();
    }

    /**
     * add Node at location (0, 0)
     *
     * @param id node's ID
     */
    private void addNode(String id) {
        addNode(id, 0, 0);
    }

    /**
     * add Node at location (x, y)
     *
     * @param id node's ID
     * @param x  x-coord
     * @param y  y-coord
     */
    private void addNode(String id, float x, float y) {
        FlowVertex fv = new FlowVertex(id);
        fv.x = x;
        fv.y = y;
        verts.put(id, fv);
        idToInt.put(id, curV);
        intToId.put(curV++, id);
        n = verts.size();
    }

    /**
     * Add node to graph as well as autoLayout graph
     *
     * @param id node's ID
     * @param dg the AutoLayout Diagram this node belongs to
     */
    private void addNode(String id, Diagram dg) {
        FlowVertex fv = new FlowVertex(id, dg);
        verts.put(id, fv);
        idToInt.put(id, curV);
        intToId.put(curV++, id);
        n = verts.size();
    }

    public FlowVertex addVertex(float x, float y) {
        FlowVertex fv = new FlowVertex();
        fv.setID(fv.hashCode() + "");
        fv.x = x;
        fv.y = y;
        verts.put(fv.getID(), fv);
        n = verts.size();
        recompute();
//        idToInt.put(id, curV);
//        intToId.put(curV++, id);
        return fv;
    }

    public FlowEdge addEdge(FlowVertex u, FlowVertex v) {
        // Todo: check u, v are actually part of this graph?

        // Note: DON'T call any of the private addEdge() methods, they're intended for use with final graphs only

        String id = u.getID() + "-" + v.getID();

        // don't add/create an edge if it already exists
        if (edges.containsKey(id)) return edges.get(id);

        // if reverse-edge already exists, reverse it
        int preCapacity = 0;
        if (edges.containsKey(v.getID() + "-" + u.getID())) {
            preCapacity = edges.get(v.getID() + "-" + u.getID()).getCapacity();
            deleteEdge(edges.get(v.getID() + "-" + u.getID()));
        }
        FlowEdge fe = new FlowEdge(Math.max(preCapacity, 0));
        u.out.put(v.getID(), v);
        v.in.put(u.getID(), u);
        fe.u = u;
        fe.v = v;
        edges.put(id, fe);
        u.edges.put(id, fe);
        v.edges.put(id, fe);
        return fe;
    }

    private void addEdge(FlowVertex u, FlowVertex v, int cap) {
        FlowEdge fe = new FlowEdge(cap);
        u.out.put(v.getID(), v);
        v.in.put(u.getID(), u);
        fe.u = u;
        fe.v = v;
        String id = u.getID() + "-" + v.getID();
        edges.put(id, fe);
        u.edges.put(id, fe);
        v.edges.put(id, fe);

        /* add Edge to Fulkerson's Flow Network Object */
        FFEdge ffedge = new FFEdge(idToInt.get(u.getID()), idToInt.get(v.getID()), cap);
        fordNet.addEdge(ffedge);
    }

    private void addEdge(String uID, String vID) {
        FlowVertex u = verts.get(uID), v = verts.get(vID);
        addEdge(u, v);
    }


    private void addEdge(String uID, String vID, int cap) {
        FlowVertex u = verts.get(uID), v = verts.get(vID);
        addEdge(u, v, cap);
    }

//    void updateEdges() {
//        for (FFEdge fffedge : fordNet.edges()) {
//
////            System.out.println(intToId.get(ffedge.from()) + "->" + intToId.get(ffedge.to()) + " : " + ffedge.flow());
//            FlowEdge fe = edges.get(intToId.get(fffedge.from()) + "-" + intToId.get(fffedge.to()));
////            fe.setFlow((int) fffedge.flow());
////            fe.highlight = fe.flowChanged();
//            fe.addState((int) fffedge.flow());
//        }
//    }

    public void draw(Canvas canvas, boolean fit) {
        // Todo: optimize all draw/update methods

        for (String key : verts.keySet()) {
            if (fit) verts.get(key).drawFit(canvas, focusElem, xrange, yrange, cx, cy);
            else verts.get(key).draw(canvas, focusElem);
        }

        for (String key : edges.keySet()) {
            edges.get(key).draw(canvas, focusElem);
//            edges.get(key).draw(canvas);

        }
    }

    /**
     * step forward towards optimal flow. Augmenting path's edges will be highlighted
     */
    public void stepForward() {
        if (stateNum < maxStateNum) {
            for (String key : edges.keySet())
                edges.get(key).applyState(1);
            stateNum++;
        }
    }

    public void stepBackward() {
        if (stateNum > 0) {
            for (String key : edges.keySet())
                edges.get(key).applyState(-1);
            stateNum--;
        }
    }

    /**
     * Hit test + corresponding action for interacting with graph's components
     *
     * @param mapX world-space x-coord
     * @param mapY world-space y-coord
     */
    public Clickable checkClick(float mapX, float mapY) {
        for (String key : edges.keySet())
            if (edges.get(key).collide(mapX, mapY)) {
//                this.focusElem = edges.get(key);
                return edges.get(key);
            }
        for (String key : verts.keySet())
            if (verts.get(key).collide(mapX, mapY)) {
//                this.focusElem = verts.get(key);
                return verts.get(key);
            }
//        this.focusElem = null;
        return null;
    }

    public void unHighlight() {
        // Todo
        focusElem = null;
//        for (String key : edges.keySet()) {
//            edges.get(key);
//        }
//        for (String key : verts.keySet()) {
//            verts.get(key);
//        }
    }

    public void highlight(Clickable obj) {
        // Todo: check if callable actually belongs to this graph
        focusElem = obj;
    }

    public void deleteEdge(FlowEdge selected) {
        // Todo check that those objects belong to the graph
        selected.u.removeEdge(selected.v);
        selected.v.removeEdge(selected.u);
        edges.remove(selected.getID());
    }

    public void deleteVertex(FlowVertex selected) {
        // Todo check that those objects belong to the graph
        List<String> deleteList = new ArrayList<>();
        for (String key : edges.keySet()) {
            FlowEdge fe = edges.get(key);
            if (fe.u.getID() == selected.getID() || fe.v.getID() == selected.getID())
                deleteList.add(key);
        }
        for (String key : deleteList)
            deleteEdge(edges.get(key));
        verts.remove(selected.getID());
        n = verts.size();
    }

    /**
     * Get a compact String representation of this graph.<br/><br/>
     * Format:<br/>lines containing space-separated tokens:<br/>
     * <p><b>
     *  (# of nodes) (# of edges)<br/>
     *  (node id) (x) (y)<br/>
     *  ...<br/>
     *  (node id) (x) (y)<br/>
     *  (node id) (node id) (capacity)<br/>
     *  ...<br/>
     *  (node id) (node id) (capacity)<br/>
     * </b></p>
     * <br/>
     * node id's don't contain '-'<br/>
     *
     * @return Serialized FlowGraph
     */
    public String serialize() {
        String tmp = "";
        for (String k : verts.keySet()) {
            FlowVertex fv = verts.get(k);
            tmp += (fv.getID() + " " + (int) Math.round(fv.x) + " " + (int) Math.round(fv.y)) + "\n";
        }

        for (String k : edges.keySet()) {
            FlowEdge fe = edges.get(k);
            tmp += (fe.u.getID() + " " + fe.v.getID() + " " + fe.getCapacity()) + "\n";
        }

        return tmp;
    }

}