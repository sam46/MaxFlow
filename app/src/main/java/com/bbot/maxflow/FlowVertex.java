package com.bbot.maxflow;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.HashMap;
import java.util.Map;

import AutoLayout.Diagram;
import AutoLayout.Node;

/**
 * Actual on-screen interactive node
 */
public class FlowVertex implements Clickable {
    public float fX = 3.1415f, fY = 3.1415f;
    public float x, y, r = 40;
    public int flow = 0;
    public Map<String, FlowVertex> in = new HashMap<>(), out = new HashMap<>();
    public Map<String, FlowEdge> edges = new HashMap<>();
    public int Cin, Cout; // in and out total capacities
    private String id = "";  // can't be "" or contain '-'
    private AutoLayout.Node layoutNode;

    /**
     * Actual on-screen interactive node
     */
    public FlowVertex() {
    }

    /**
     * Actual on-screen interactive node
     *
     * @param _id can't be "" or contain '-'
     */
    public FlowVertex(String _id) {
        setID(_id);
    }

    /**
     * Actual on-screen interactive node.
     * By using this constructor, Auto layout will take effect!!
     *
     * @param dg Auto layout's graph object
     */
    public FlowVertex(Diagram dg) {
        layoutNode = new Node();
        dg.AddNode(layoutNode);
    }

    /**
     * Actual on-screen interactive node.
     * By using this constructor, Auto layout will take effect!!
     *
     * @param _id can't be "" or contain '-'
     * @param dg  Auto layout's graph object
     */
    public FlowVertex(String _id, Diagram dg) {
        setID(_id);
        layoutNode = new Node();
        dg.AddNode(layoutNode);
    }

    public String getID() {
        return id;
    }

    /**
     * Initialize this vertex's ID. Once set, id becomes read-only.
     *
     * @param _id can't be "" or contain '-'
     * @return false if id has already been set. false if id has successfully been set.
     */
    public boolean setID(String _id) {
        try {
            if (_id == null)
                throw new NullPointerException("null vertex id");
            else if (_id.isEmpty() || _id.contains("-"))
                throw new IllegalArgumentException("invalid vertex id");
        } catch (Exception e) {
            // Todo
            // assign default id to vertex??
            return false;
        }

        if (id == null || id.isEmpty()) { // ensures id is never changed
            id = _id;
            return true;
        }
        return false;
    }

    public void removeEdge(FlowVertex fe) {
        if(in.containsKey(fe.getID())) in.remove(fe.getID());
        if(out.containsKey(fe.getID())) out.remove(fe.getID());
    }

    /**
     * Commit coords computed by auto layout (if enabled) to this vertex actual coords
     */
    public void refreshCoords() {
        if (layoutNode == null) {
            System.err.print("\n Can't set vertex coordinates. layoutNode is null.");
            return;
        }
//        System.out.print("vertex "+id+": "+x+", "+y+"  -->  ");
        x = (float) layoutNode.getX();
        y = (float) layoutNode.getY();
//        System.out.print(x+", "+y);
    }

    public int computeFlow() {
        int _flow = 0;
        for (String fv : in.keySet())
            _flow += edges.get(fv + "-" + id).getFlow();
        for (String fv : out.keySet())
            _flow -= edges.get(id + "-" + fv).getFlow();
        this.flow = _flow;
        return _flow;
    }

    public void computeCapacities() {
        int _in = 0, _out = 0;
        for (String fv : in.keySet())
            _in += edges.get(fv + "-" + id).getFlow();
        for (String fv : out.keySet())
            _out += edges.get(id + "-" + fv).getFlow();

        Cin = _in;
        Cout = _out;
    }

    /**
     * Quantify how much flow of in or out capacity is utilized
     * Sign depends on flow direction
     *
     * @return a signed normalized value. Zero if this vertex is in equilibrium
     */
    public double getBalanceRatio() {
        computeFlow();
        if (flow < 0) return flow * 1.0 / Cout;
        return flow * 1.0 / Cin;
    }


    public void draw(Canvas canvas, Clickable focusElem) {
        boolean focus = focusElem == this;

        Paint tPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tPaint.setColor(Color.WHITE);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int red = 255, green = 255, blue = 255;
        computeCapacities();
        double ratio = getBalanceRatio();
        if (ratio < 0) {
            red = 255;
            green = 255 - (int) (-ratio * 255);
            blue = green;
        } else if (ratio > 0) {
            blue = 255;
            green = 255 - (int) (ratio * 255);
            red = green;
        }
        if (Math.abs(ratio) < 0.3)
            tPaint.setColor(Color.BLACK);
        if (ratio == 0 || Double.isNaN(ratio)) {
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            tPaint.setColor(Color.BLACK);
        } else {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(red, green, blue));
        }

        /* Todo
        if(isSrcOrSink) {
            ...
        }
        */
        RectF rect = new RectF(x - r, y - r, x + r, y + r);
        canvas.drawArc(rect, 0, 360, true, paint);
        if (focus) {
            Paint tempPaint = new Paint();
            tempPaint.setStyle(Paint.Style.STROKE);
            tempPaint.setStrokeWidth(5);
            canvas.drawArc(rect, 0, 360, true, tempPaint);
        }
        tPaint.setTextSize(32);
        tPaint.setStyle(Paint.Style.FILL);
        tPaint.setTextAlign(Paint.Align.CENTER);
//        canvas.drawText((flow > 0) ? "+" + flow : "" + flow, x, y, tPaint);
        canvas.drawText("" + flow, x, y + 6, tPaint);

        fX = x;
        fY = y;
    }

    public void drawFit(Canvas canvas, Clickable focusElem, float xrange, float yrange, float cx, float cy) {
        boolean focus = focusElem == this;

        Paint tPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tPaint.setColor(Color.WHITE);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int red = 255, green = 255, blue = 255;
        computeCapacities();
        double ratio = getBalanceRatio();
        if (ratio < 0) {
            red = 255;
            green = 255 - (int) (-ratio * 255);
            blue = green;
        } else if (ratio > 0) {
            blue = 255;
            green = 255 - (int) (ratio * 255);
            red = green;
        }
        if (Math.abs(ratio) < 0.3)
            tPaint.setColor(Color.BLACK);
        if (ratio == 0 || Double.isNaN(ratio)) {
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            tPaint.setColor(Color.BLACK);
        } else {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(red, green, blue));
        }



        /* fit graph into screen: ONLY use when no more verts will be added */
        float shrink = 0.75f;
        //float x = ver.x, y = ver.y;     // Original System: original vertex coords
        float x_c = shrink * (MainPanel.w / xrange) * (x - cx), x_y = shrink * (MainPanel.h / yrange) * (y - cy);    // C-System: scaled vertex coords with C as origin,
        float shiftX = MainPanel.w / 2.0f - cx, shiftY = MainPanel.h / 2.0f - cy;  // shift vector to bring C-System into view
        float finalX = x_c + shiftX, finalY = x_y + shiftY;
        /*********************************************************************/
        fX = finalX;
        fY = finalY;


        /* Todo
        if(isSrcOrSink) {
            ...
        }
        */
        RectF rect = new RectF(finalX - r, finalY - r, finalX + r, finalY + r);
        canvas.drawArc(rect, 0, 360, true, paint);
//        if(focus) {
//            System.out.println("Focus");
//            Paint tempPaint  =new Paint();
//            tempPaint.setStyle(Paint.Style.STROKE);
//            tempPaint.setStrokeWidth(5);
//            canvas.drawArc(rect, 0, 360, true, tempPaint);
//        }
        tPaint.setTextSize(32);
        tPaint.setStyle(Paint.Style.FILL);
        tPaint.setTextAlign(Paint.Align.CENTER);
//        canvas.drawText((flow > 0) ? "+" + flow : "" + flow, finalX, finalY, tPaint);
        canvas.drawText("" + flow, finalX, finalY + 6, tPaint);
    }

    @Override
    public boolean collide(float x, float y) {

        return ((x - this.x) * (x - this.x) + (y - this.y) * (y - this.y) < (this.r + 25) * (this.r + 25));
    }

    @Override
    public String toString() {
        return "Vertex " + getID() + " (" + x + ", " + y + ")";
    }
}
