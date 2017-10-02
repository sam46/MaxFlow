package com.bbot.maxflow;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * Actual on-screen interactive edge.
 */
public class FlowEdge implements Clickable {
    private static List<FlowEdge> states = new ArrayList<>();
    public FlowVertex u, v;     // convention: u --> v
    private int capacity = 0;
    boolean highlight = false;
    private int stateNum = 0;
    private int flow = 0, preFlow = 0;
    private double[][] boundbox = new double[5][2];
    private EdgeStateNode curState = new EdgeStateNode();

    /**
     * Actual on-screen interactive edge.
     * Capacity is set to a random integer
     */
    public FlowEdge() {
//        Random rand = new Random();
////        focus = rand.nextFloat() < 0.3;
//        capacity = rand.nextInt(10) + 5;
//        flow = rand.nextInt(capacity);
    }

    /**
     * Actual on-screen interactive edge.
     *
     * @param cap Edge Capacity
     */
    public FlowEdge(int cap) {
        setCapacity(cap);
//        Random rand = new Random();
//        flow = rand.nextInt(capacity);
//        Random rand = new Random();
//        focus = rand.nextFloat() < 0.3;
    }

    public void setCapacity(int cap) {
        if (cap < 0)
            throw new IllegalArgumentException("Invalid addEdge() Argument: capacity must be non-negative!");
        capacity = cap;
    }
    public int getCapacity() {return capacity;}

    /**
     * Refersh edge or step its state forward/backwards
     * @param dir 0 to refersh this edges properies, 1 to set properties to next state, -1 to set properties to prev state
     */
    public void applyState(int dir) {
        if (curState == null) return;

        if (dir < 0 && curState.n > 0)
            curState = curState.prev;
        else if (dir > 0 && curState.next != null) {
            curState = curState.next;

        }

        this.preFlow = curState.preFlow;
        this.flow = curState.flow;
        this.highlight = curState.highlight;
        this.stateNum = curState.n;
        System.out.println(getID()+":  "+this.preFlow+"   "+this.flow);
    }

    /**
     * @return "u-v", separator is "-". Edge goes from u to v
     */
    public String getID() {
        return u.getID() + "-" + v.getID();
    }

    /**
     * Get a bare-minimum copy of this FlowEdge.
     *
     * @return
     */
//    private FlowEdge copyState() {
//        FlowEdge fe = new FlowEdge(capacity);
//        fe.preFlow = preFlow;
//        fe.flow = flow;
//        fe.stateNum = stateNum + 1;
//        fe.highlight = highlight;
//        return fe;
//    }

//    public FlowEdge getState(int num) {
//        return states.get(num);
//    }

    public void addState(int pf, int f) {
        EdgeStateNode esn = new EdgeStateNode(stateNum+1, pf, f, f!=pf);
        esn.prev = curState;
        curState.next = esn;
    }

    public int getFlow() {
        return this.flow;
    }

    public void setFlow(int f) {
        preFlow = flow;
        flow = f;
    }

    public boolean flowChanged() {
        return flow != preFlow;
    }

    public void draw(Canvas canvas) {
        draw(canvas, highlight ? this : null);
    }

    /**
     * draw and force highlight/focus if focusElem is the same this object
     *
     * @param canvas
     * @param focusElem
     */
    public void draw(Canvas canvas, Clickable focusElem) {
        boolean focus = focusElem == this;
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        float ux = u.fX, uy = u.fY;
        float vx = v.fX, vy = v.fY;
//        System.out.println(ux+", "+uy + " --> "+vx+", "+vy+"   "+u.x+", "+u.y + " --> "+v.x+", "+v.y);
        float d = (float) Math.sqrt((ux - vx) * (ux - vx) + (uy - vy) * (uy - vy));
        float perpX = (uy - vy) / d, perpY = (vx - ux) / d;     // normalized perpendicular
        float backX = (ux - vx) / d, backY = (uy - vy) / d;     // normalized v--->u
        float slack = 1.2f; // shorted edge a bit so it doesn't touch the vertices
        float r1 = 10, r2 = 25;     // for stretching the triangle
        if (focus) {
            r1 = 20;
            r2 = 35;
        }
        ux += slack * -u.r * backX;
        uy += slack * -u.r * backY;
        vx += slack * v.r * backX;
        vy += slack * v.r * backY;
        float p1X = vx + r1 * perpX + r2 * backX;
        float p1Y = vy + r1 * perpY + r2 * backY;
        float p2X = vx - r1 * perpX + r2 * backX;
        float p2Y = vy - r1 * perpY + r2 * backY;
        Path path = new Path();  // For arrow (triangle)
        path.moveTo(vx, vy);
        path.lineTo(p1X, p1Y);
        path.lineTo(p2X, p2Y);
        canvas.drawPath(path, paint);

        /* compute and store bound box points */
        float prox = 40;
        boundbox[0][0] = ux + prox * perpX;
        boundbox[0][1] = uy + prox * perpY;
        boundbox[1][0] = ux - prox * perpX;
        boundbox[1][1] = uy - prox * perpY;
        boundbox[2][0] = vx - prox * perpX;
        boundbox[2][1] = vy - prox * perpY;
        boundbox[3][0] = vx + prox * perpX;
        boundbox[3][1] = vy + prox * perpY;
        boundbox[4][0] = 0;
        boundbox[4][1] = 0;
        for (int i = 0; i < 4; ++i) {
            boundbox[4][0] += boundbox[i][0];
            boundbox[4][1] += boundbox[i][1];
        }
        boundbox[4][0] /= 4;
        boundbox[4][1] /= 4;
//        Path bbox = new Path();
//        bbox.moveTo((float) boundbox[0][0], (float) boundbox[0][1]);
//        bbox.lineTo((float) boundbox[1][0], (float) boundbox[1][1]);
//        bbox.lineTo((float) boundbox[2][0], (float) boundbox[2][1]);
//        bbox.lineTo((float) boundbox[3][0], (float) boundbox[3][1]);
//        bbox.lineTo((float) boundbox[0][0], (float) boundbox[0][1]);
//        paint.setStyle(Paint.Style.STROKE);
//        canvas.drawPath(bbox, paint);
        /****************************************/

        if (focus) {
            paint.setStrokeWidth(8);
            // shorten the line so it doesn't pop out of the arrow
            vx += 0.2 * v.r * backX;
            vy += 0.2 * v.r * backY;
        }
        canvas.drawLine(ux, uy, vx, vy, paint);
//        System.out.println(ux+", "+uy + "  ----> "+vx+", "+vy);
//        RectF ctr = new RectF((float) boundbox[4][0] - 5, (float) boundbox[4][1] + 5, (float) boundbox[4][0] + 5, (float) boundbox[4][1] - 5);
//        Paint pa = new Paint();
//        pa.setStyle(Paint.Style.FILL_AND_STROKE);
//        canvas.drawArc(ctr, 0, 360, true, pa);

        final float bgH = 30, bgV = 8;
        Path txtBG = new Path();
        txtBG.moveTo((float) boundbox[4][0] - bgH * backX + bgV * perpX, (float) boundbox[4][1] - bgH * backY + bgV * perpY);
        txtBG.lineTo((float) boundbox[4][0] - bgH * backX - bgV * perpX, (float) boundbox[4][1] - bgH * backY - bgV * perpY);
        txtBG.lineTo((float) boundbox[4][0] + bgH * backX - bgV * perpX, (float) boundbox[4][1] + bgH * backY - bgV * perpY);
        txtBG.lineTo((float) boundbox[4][0] + bgH * backX + bgV * perpX, (float) boundbox[4][1] + bgH * backY + bgV * perpY);
        txtBG.moveTo((float) boundbox[4][0] - bgH * backX + bgV * perpX, (float) boundbox[4][1] - bgH * backY + bgV * perpY);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(MainPanel.bgColor);
        canvas.drawPath(txtBG, paint);

        Paint tPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tPaint.setColor(Color.BLUE);
        tPaint.setTextSize(30);
        tPaint.setStyle(Paint.Style.FILL);
        tPaint.setTextAlign(Paint.Align.CENTER);
        float shift = -10;
        if (vx <= ux) shift = 15;
        canvas.save();
        if (Math.round(Math.atan((uy - vy) / (ux - vx)) * 180 / Math.PI) == -90) {
            shift *= -1;
            canvas.rotate((float) 90, (float) boundbox[4][0] - shift * perpX, (float) boundbox[4][1] - shift * perpY);
        } else
            canvas.rotate((float) (Math.atan((uy - vy) / (ux - vx)) * 180 / Math.PI), (float) boundbox[4][0] - shift * perpX, (float) boundbox[4][1] - shift * perpY);
        canvas.drawText("" + flow + "/" + capacity, (float) boundbox[4][0] - shift * perpX, (float) boundbox[4][1] - shift * perpY, tPaint);
        canvas.restore();
    }

    private double dot(double x1, double y1, double x2, double y2) {
        return x1 * x2 + y1 * y2;
    }

    private double dot(double x1, double y1) {
        return dot(x1, y1, x1, y1);
    }

    @Override
    public boolean collide(float x, float y) {
//        final double clickRad = 1;
//
//        //prepare the vectors
//        double cur_box_cornerX, cur_box_cornerY;
//        double ctr_boxX = boundbox[4][0], ctr_boxY = boundbox[4][1];
//
//        double max = Double.NEGATIVE_INFINITY;
//        double box2circleX = x - ctr_boxX, box2circleY = y - ctr_boxY;
//        double mag = Math.sqrt(box2circleX * box2circleX + box2circleY * box2circleY);
//        double box2circle_normX = box2circleX / mag, box2circle_normY = box2circleY / mag;
//
//        //get the maximum
//        for (int i = 0; i < 4; i++) {
//            cur_box_cornerX = boundbox[i][0];
//            cur_box_cornerY = boundbox[i][1];
//            double vecX = cur_box_cornerX - ctr_boxX, vecY = cur_box_cornerY - ctr_boxY;
//            double cur_proj = vecX * box2circle_normX + vecY * box2circle_normY;
//            max = Math.max(cur_proj, max);
//        }
//
//        if (mag - max - clickRad > 0 && mag > 0) return false;
        // ab bc:  a is +ux, b is -ux, c is -vx
        double abam = dot(boundbox[1][0] - boundbox[0][0], boundbox[1][1] - boundbox[0][1], x - boundbox[0][0], y - boundbox[0][1]);
        double ab2 = dot(boundbox[1][0] - boundbox[0][0], boundbox[1][1] - boundbox[0][1]);
        double bcbm = dot(boundbox[2][0] - boundbox[1][0], boundbox[2][1] - boundbox[1][1], x - boundbox[1][0], y - boundbox[1][1]);
        double bc2 = dot(boundbox[2][0] - boundbox[1][0], boundbox[2][1] - boundbox[1][1]);


//        System.out.println(u.id + "->" + v.id);
//        System.out.println("M: " + x + ", " + y);
//        System.out.println("A: " + boundbox[0][0] + ", " + boundbox[0][1]);
//        System.out.println("B: " + boundbox[1][0] + ", " + boundbox[1][1]);
//        System.out.println("C: " + boundbox[2][0] + ", " + boundbox[2][1]);
//        System.out.println("D: "+ boundbox[3][0] +", "+boundbox[3][1]);
//        System.out.println("AB . AM = " + abam);
//        System.out.println("BC . BM = " + bcbm);
//        System.out.println("AB . AB = " + ab2);
//        System.out.println("BC . BC = " + bc2);

        if (ab2 >= abam && abam >= 0 && bc2 >= bcbm && bcbm >= 0) {
//            System.out.println("Collision!!");

            float ux = u.fX, uy = u.fY;
            float vx = v.fX, vy = v.fY;
//            System.out.println(u.getID() + "->" + v.getID() + ": " + (Math.atan((uy - vy) / (ux - vx)) * 180 / Math.PI));
            return true;
        }
        return false;
        //System.out.println("edge collision !!");
//        return true;
    }

    private class EdgeStateNode {
        EdgeStateNode prev = null, next = null;
        int n, preFlow, flow;
        boolean highlight;
        EdgeStateNode(){
            this(0,0,0,false);
        }
        EdgeStateNode(int num, int pf, int f, boolean hl){
            preFlow = pf;
            flow = f;
            highlight = hl;
            n = num;
        }
    }

    @Override
    public String toString() {
        return "Edge "+getID()+ "flow/cap = "+flow+"/"+capacity;
    }
}
