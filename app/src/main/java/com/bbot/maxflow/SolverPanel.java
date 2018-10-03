package com.bbot.maxflow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class SolverPanel extends BasePanel {
    Bitmap prevBmp, nextBmp;
    RectF prevBtnRect, nextBtnRect;
    float btnsOffset = 50, bmpW, bmpH, scl = 4;
    boolean firstStep, lastStep;

    public SolverPanel(Context context, String serialized) {
        super(context);
        graph = new FlowGraph(serialized);
        updateShowBtns();
        fit = false;
        HighlightAugPaths = true;
        prevBmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_keyboard_arrow_left_black_24dp);
        nextBmp = BitmapFactory.decodeResource(getResources(), R.drawable.keyboard_right_arrow_button);
        bmpW = prevBmp.getWidth();
        bmpH = prevBmp.getHeight();
        prevBtnRect = new RectF(btnsOffset, btnsOffset, btnsOffset + bmpW / scl, btnsOffset + bmpH / scl);
        nextBtnRect = new RectF(w - btnsOffset - bmpW / scl, btnsOffset, w - btnsOffset, btnsOffset + bmpH / scl);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        super.surfaceChanged(holder, format, width, height);
        prevBtnRect = new RectF(btnsOffset, btnsOffset, btnsOffset + bmpW / scl, btnsOffset + bmpH / scl);
        nextBtnRect = new RectF(w - btnsOffset - bmpW / scl, btnsOffset, w - btnsOffset, btnsOffset + bmpH / scl);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // buttons click test
                float x = event.getRawX(), y = event.getRawY();
                if (nextBtnRect.contains(x, y)) {
                    graph.stepForward();
                    updateShowBtns();
                    break;
                } else if (prevBtnRect.contains(x, y)) {
                    graph.stepBackward();
                    updateShowBtns();
                    break;
                }

                // graph interaction: happens only if next/prev buttons weren't hit
//                float[] wrldPos = toWorldCoords(x,y);
//                graph.checkClick(wrldPos[0], wrldPos[1]);
        }

//        return super.onTouchEvent(event);
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (! firstStep) canvas.drawBitmap(prevBmp, null, prevBtnRect, paint);
        if (! lastStep) canvas.drawBitmap(nextBmp, null, nextBtnRect, paint);
    }

    private void updateShowBtns() {
        firstStep = graph.getStateNum() == 0;
        lastStep = graph.getMaxStateNum() == graph.getStateNum();
    }
}