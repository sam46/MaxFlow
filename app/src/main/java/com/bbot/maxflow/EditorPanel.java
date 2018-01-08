package com.bbot.maxflow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.text.InputType;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class EditorPanel extends BasePanel {
    public EditText etext;
    Bitmap clearBmp, deleteBmp, addEdgeBmp, saveBmp, setSinkBmp, setSrcBmp, changeCapBmp;
    RectF clearRect, deleteRect, addEdgeRect, saveRect, setSinkRect, setSrcRect, changeCapRect;
    boolean enableClearBtn = false, enableDeleteBtn = false, enableAddEdgeBtn = false, enableSaveBtn = false,
            enableSetSinkBtn = false, enableSetSrcBtn = false, enableChangeCapBtn = false,
            edgeMode = false;
    Clickable selected = null;
    InputMethodManager im;

    public EditorPanel(Context context) {
        super(context);
        System.out.println("Create!!");

        graph = new FlowGraph(false);
        etext = new EditText(context);
        setupEditText();
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();

//        this.setOnFocusChangeListener(new OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(hasFocus) {
//                    EditorPanel.this.toggleEditText(false);
//                }
//            }
//        });
        fit = false;
        clearBmp = BitmapFactory.decodeResource(getResources(), R.drawable.garbage);
        deleteBmp = BitmapFactory.decodeResource(getResources(), R.drawable.clear);
        addEdgeBmp = BitmapFactory.decodeResource(getResources(), R.drawable.vector_diagonal_line_with_box_edges);
        saveBmp = BitmapFactory.decodeResource(getResources(), R.drawable.check_symbol);
        setSrcBmp = BitmapFactory.decodeResource(getResources(), R.drawable.convergence);
        setSinkBmp = BitmapFactory.decodeResource(getResources(), R.drawable.convergence);
        changeCapBmp = BitmapFactory.decodeResource(getResources(), R.drawable.heart_meter);

        float btnOffset = 35 * 6, sep = 15, scl = 5, bmpW = clearBmp.getWidth(), bmpH = clearBmp.getHeight();
        clearRect = new RectF(btnOffset + 1 * sep + 0 * bmpW / scl, btnOffset, btnOffset + 1 * sep + 1 * bmpW / scl, btnOffset + bmpH / scl);
        deleteRect = new RectF(btnOffset + 3 * sep + 1 * bmpW / scl, btnOffset, btnOffset + 3 * sep + 2 * bmpW / scl, btnOffset + bmpH / scl);
        addEdgeRect = new RectF(btnOffset + 5 * sep + 2 * bmpW / scl, btnOffset, btnOffset + 5 * sep + 3 * bmpW / scl, btnOffset + bmpH / scl);
        saveRect = new RectF(btnOffset + 7 * sep + 3 * bmpW / scl, btnOffset, btnOffset + 7 * sep + 4 * bmpW / scl, btnOffset + bmpH / scl);
        setSrcRect = new RectF(btnOffset + 9 * sep + 4 * bmpW / scl, btnOffset, btnOffset + 9 * sep + 5 * bmpW / scl, btnOffset + bmpH / scl);
        setSinkRect = new RectF(btnOffset + 11 * sep + 5 * bmpW / scl, btnOffset, btnOffset + 11 * sep + 6 * bmpW / scl, btnOffset + bmpH / scl);
        changeCapRect = new RectF(btnOffset + 13 * sep + 6 * bmpW / scl, btnOffset, btnOffset + 13 * sep + 7 * bmpW / scl, btnOffset + bmpH / scl);

    /*
        whitespace:
            long press : add vertex, select newly added vertex
            tap: deselect, hide all buttons except clear, save

        Vertex:
            tap: select
            selected: delete, set src/sink  buttons show up

        Edge:
            selected:
                 change cap button shows up
            tap: select

        add edge button (shows up if >= 2 vertics exist in graph)
            clicked: enter add edge mode:
                deselect selected stuff -->
                select u ---> select v ---> add edge ---> select newly added edge


        clear button:
            tap: deselect, hide all buttons except clear, save
        save button
     */
    }

    void setupEditText() {
        etext.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        etext.setGravity(Gravity.TOP);
        etext.setBackgroundColor(Color.argb((int) (0.4 * 255), 200, 200, 255));
        etext.setInputType(InputType.TYPE_CLASS_NUMBER);
        etext.setFocusable(true);
        etext.setFocusableInTouchMode(true);

        toggleEditText(false);
//        etext.setClickable(false);
//        etext.setMovementMethod(null);
//        etext.setKeyListener(null);

//        etext.setOnFocusChangeListener(new OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                System.out.println("f changed");
//                if(!hasFocus) {
//                    EditorPanel.this.toggleEditText(false);
//                    EditorPanel.this.requestFocus();
//                }
//            }
//        });

//        etext.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
////                if(v instanceof EditorPanel) System.out.println("bam!!");
//                System.out.println("etext touch event");
//                return etext.isEnabled();
//            }
//        });
    }

    public void toggleEditText(boolean show) {
//        showEtext = show;
        etext.setAlpha(show ? 1 : 0);
        etext.setEnabled(show);
        if (show) {
            etext.requestFocus();
//            getWinsetSoftInputMode(SOFT_INPUT_ADJUST_PAN)‌​;
            InputMethodManager imm = (InputMethodManager) etext.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            imm.showSoftInput(etext, InputMethodManager.SHOW_FORCED);
            setFocusable(false);
            setFocusableInTouchMode(false);
//            imm.hideSoftInputFromWindow(etext.getWindowToken(), 0);
        } else {

            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etext.getWindowToken(), 0);
        }

//        if(show) etext.requestFocus();

//        final  boolean SHOW = show;
//            etext.setOnTouchListener(new OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    return SHOW;
//                }
//            });

    }

    @Override
    protected void onLongPress(MotionEvent e) {
        super.onLongPress(e);
        deselect();
        float[] wrldPos = toWorldCoords(e.getRawX(), e.getRawY());
        selected = graph.checkClick(wrldPos[0], wrldPos[1]);

        System.out.println("long press");
        if (selected != null) {
            if (selected instanceof FlowEdge) selectEdge((FlowEdge) selected);
            else selectVertex((FlowVertex) selected);
        } else {
            synchronized (graph) {
                selectVertex(graph.addVertex(wrldPos[0], wrldPos[1]));
            }
        }
    }

    void deselect() {
        System.out.println("deselect()");
        selected = null;
        enableClearBtn = !graph.isEmpty();
        enableAddEdgeBtn = graph.getVertCount() > 1;
//        enableSaveBtn = ;
        enableDeleteBtn = false;
        enableSetSrcBtn = false;
        enableSetSinkBtn = false;
        enableChangeCapBtn = false;
        graph.unHighlight();
        edgeMode = false;
    }

    void selectVertex(FlowVertex fv) {
        graph.unHighlight();
        enableClearBtn = !graph.isEmpty();
        enableAddEdgeBtn = graph.getVertCount() > 1;
        enableDeleteBtn = true;
        enableSetSrcBtn = true;
        enableSetSinkBtn = true;
        graph.highlight(fv);
        selected = fv;
    }

    void selectEdge(FlowEdge fe) {
        graph.unHighlight();
        enableClearBtn = !graph.isEmpty();
        enableAddEdgeBtn = graph.getVertCount() > 1;
        enableDeleteBtn = true;
        enableChangeCapBtn = true;
        graph.highlight(fe);
        selected = fe;
        edgeMode = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("Editor touch event");
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                float x = event.getRawX(), y = event.getRawY();
                synchronized (graph) {
                    if (enableClearBtn && clearRect.contains(x, y)) { // Non
                        deselect();
                        graph = new FlowGraph(false);
                        deselect();
                        break;
                    } else if (enableSaveBtn && saveRect.contains(x, y)) {   // Non

                        break;
                    } else if (enableAddEdgeBtn && addEdgeRect.contains(x, y)) {    // Non
                        edgeMode = true;
                        break;
                    } else if (enableDeleteBtn && deleteRect.contains(x, y)) { // Any
                        if (selected != null) {
                            if (selected instanceof FlowVertex)
                                graph.deleteVertex((FlowVertex) selected);
                            else graph.deleteEdge((FlowEdge) selected);
                        }
                        deselect();
                        break;
                    } else if (enableChangeCapBtn && changeCapRect.contains(x, y)) {  // Edge
//                        int cap = 0;
//                        ((FlowEdge) (selected)).setCapacity(cap);
                        edgeMode = false;
                        toggleEditText(true);

                        break;
                    } else if (enableSetSinkBtn && setSinkRect.contains(x, y)) {    // Vertex
                        graph.setSink((FlowVertex) (selected));
                        break;
                    } else if (enableSetSrcBtn && setSrcRect.contains(x, y)) {     // Vertex
                        graph.setSrc((FlowVertex) (selected));
                        break;
                    } else {
                        System.out.println("branch to else:");
                        float[] wrldPos = toWorldCoords(x, y);
                        Clickable temp = graph.checkClick(wrldPos[0], wrldPos[1]);
                        if (temp == null) {
                            deselect();
                            System.out.println("  click nowhere");
                        } else if (temp == selected) edgeMode = false;
                        else if (temp instanceof FlowVertex) {
                            if (edgeMode && selected != null && selected instanceof FlowVertex) {
                                edgeMode = false;
                                toggleEditText(true);
                                selectEdge(graph.addEdge((FlowVertex) selected, (FlowVertex) temp));
//                                System.out.println("  adding edge  " + (selected == null) + "  " + (temp == null));
                            } else {
//                                System.out.println(" selecting vertex");
                                if (edgeMode) {
                                    deselect();
                                    edgeMode = true;
                                } else deselect();

                                selectVertex((FlowVertex) temp);
                            }
                        } else {
                            deselect();
                            selectEdge((FlowEdge) temp);
                        }
                    }
                }
        }

        return true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        super.surfaceChanged(holder, format, width, height);
//         = new RectF(btnsOffset, btnsOffset, btnsOffset + bmpW / scl, btnsOffset + bmpH / scl);
//         = new RectF(w - btnsOffset - bmpW / scl, btnsOffset, w - btnsOffset, btnsOffset + bmpH / scl);
    }

    @Override
    public void draw(Canvas canvas) {

        super.draw(canvas);
//        System.out.println("Editor focused? "+hasFocus());
//        System.out.println("etext focused? "+etext.hasFocus());
//        canvas.setMatrix(cam.getMat());
//        RectF rect = new RectF(xx,yy,xx+30,yy+30);
//        canvas.drawArc(rect, 0, 360, true, paint);
//        canvas.restore();
        if (etext.getAlpha() > 0) {
            canvas.drawARGB((int) (0.4 * 255), 150, 150, 150);
        } else {
            if (enableClearBtn) canvas.drawBitmap(clearBmp, null, clearRect, paint);
            if (enableDeleteBtn) canvas.drawBitmap(deleteBmp, null, deleteRect, paint);
            if (enableAddEdgeBtn) canvas.drawBitmap(addEdgeBmp, null, addEdgeRect, paint);
            if (enableSaveBtn) canvas.drawBitmap(saveBmp, null, saveRect, paint);
            if (enableSetSrcBtn) canvas.drawBitmap(setSrcBmp, null, setSrcRect, paint);
            if (enableSetSinkBtn) canvas.drawBitmap(setSinkBmp, null, setSinkRect, paint);
            if (enableChangeCapBtn) canvas.drawBitmap(changeCapBmp, null, changeCapRect, paint);
        }
    }


}
