package com.bbot.maxflow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.OverScroller;

public class BasePanel extends SurfaceView implements SurfaceHolder.Callback {
    protected static int w, h;
    protected static FlowGraph graph;
    protected static int bgColor = Color.WHITE;
    //    protected float zoom = 1.0f;
    private OverScroller mScroller;
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetectorCompat mGestureDetector;
    private RotationGestureDetector mRotationDetector;
    protected Paint paint;
    protected Camera cam;
    protected boolean fit = true;

    protected float[] toWorldCoords(float x, float y) {
        float[] mapPos = {0, 0};
        float[] pos = {x,y};
        Matrix inv = cam.getInverse();
        inv.mapPoints(mapPos, pos);
        return mapPos;
    }

    private RotationGestureDetector.OnRotationGestureListener mRotationListener = new RotationGestureDetector.OnRotationGestureListener() {
        @Override
        public boolean OnRotation(RotationGestureDetector rotationDetector) {
            final float step = 1.25f;
            float[] inv = cam.applyInverse(rotationDetector.getFocalX(), rotationDetector.getFocalY());
            cam.rotate(-step * rotationDetector.getAngle() * (float) Math.PI / 180, inv[0], inv[1]);
            invalidate();
            return true;
        }
    };

    private final GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            return super.onScroll(e1, e2, distanceX, distanceY);
            cam.translate(-distanceX, -distanceY);
            System.out.println("scroll");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            BasePanel.this.onLongPress(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
//            float d = (float) Math.sqrt(velocityX * velocityX + velocityY * velocityY);
//            velocityX /= d;
//            velocityY /= d;
//            mScroller.fling(0, 0, (int) velocityX / 10, (int) velocityY / 10, -10000, 10000, -10000, 10000);
//
//            float dist = 200;
//            while (dist>0) {
//                dx += vx*
//            }
////
//            dx += velocityX*100;
//            dy += velocityY*100;
//            System.out.println(velocityX + ", " + velocityY);
//            return true;
        }

//        @Override
//        public boolean onDoubleTapEvent(MotionEvent e) {
////            return super.onDoubleTapEvent(e);
//
//            zoom *= mScaleGestureDetector.getScaleFactor();
//            // Don't let the object get too small or too large.
//            zoom = Math.max(0.1f, Math.min(zoom, 5.0f));
//            float[] inv = cam.applyInverse(e.getRawX(), e.getRawY());
//            cam.scale(zoom, inv[0], inv[1]);
//            invalidate();
//            return true;
//        }

    };
    //    float px = 0, py = 0;
    private final ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
//            zoom *= detector.getScaleFactor();
//             Don't let the object get too small or too large.
//            zoom = Math.max(0.1f, Math.min(zoom, 5.0f));

//            final float step = 1.01f;
            final float z = detector.getScaleFactor();
            float[] inv = cam.applyInverse(mScaleGestureDetector.getFocusX(), mScaleGestureDetector.getFocusY());
            cam.scale(z, inv[0], inv[1]);

//            px = mScaleGestureDetector.getFocusX();
//            py = mScaleGestureDetector.getFocusY();
            invalidate();
            return true;
        }

    };
    //    Bitmap bmp;
    private PanelThread thread;

    protected BasePanel(Context context) {
        super(context);
        getHolder().addCallback(this);
        graph = new FlowGraph(false);
        thread = new PanelThread(getHolder(), this);
//        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        paint = new Paint();
        mScaleGestureDetector = new ScaleGestureDetector(context, mScaleGestureListener);
        mGestureDetector = new GestureDetectorCompat(context, mGestureListener);
        mScroller = new OverScroller(context);
        mRotationDetector = new RotationGestureDetector(mRotationListener);
        cam = new Camera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        w = this.getWidth();
        h = this.getHeight();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new PanelThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);


            mRotationDetector.onTouchEvent(event);

//        return super.onTouchEvent(event);
        return true;
    }

    public void update() {
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

//        canvas.drawColor(Color.WHITE);
        canvas.drawColor(BasePanel.bgColor);
//        System.out.println(mat);
        canvas.save();
        canvas.setMatrix(cam.getMat());
//        Paint paint = new Paint();
//        paint.setColor(Color.RED);
//        float r = 25;
//        float[] p = cam.applyInverse(px, py);
//        System.out.println(px+", "+py);
        synchronized (graph) {
            graph.draw(canvas, fit);
        }
//        canvas.drawBitmap(bmp, 0, 0, paint);
//        RectF rect = new RectF(p[0] - r, p[1] - r, p[0] + r, p[1] + r);
//        canvas.drawArc(rect, 0, 360, true, paint);
        canvas.restore();
//        canvas.drawArc(rect, 0, 360, true, paint);

    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        final float Step = 1.5f;
        if (mScroller.computeScrollOffset()) {
            cam.translate(Step * (mScroller.getCurrX() - mScroller.getStartX()), Step * (mScroller.getCurrY() - mScroller.getStartY()));
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    protected void onLongPress(MotionEvent e) {

    }

}