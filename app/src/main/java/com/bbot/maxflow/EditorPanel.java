package com.bbot.maxflow;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.widget.Toast;

public class EditorPanel extends BasePanel {
    public EditText etext;
    boolean saved = false;
    Bitmap clearBmp, deleteBmp, addEdgeBmp, saveBmp, setSinkBmp, setSrcBmp, changeCapBmp;
    RectF clearRect, deleteRect, addEdgeRect, saveRect, setSinkRect, setSrcRect, changeCapRect;
    boolean enableClearBtn = false, enableDeleteBtn = false, enableAddEdgeBtn = false, enableSaveBtn = false,
            enableSetSinkBtn = false, enableSetSrcBtn = false, enableChangeCapBtn = false,
            edgeMode = false;
    Clickable selected = null;
    Paint paintDim;

    public EditorPanel(Context context) {
        super(context);
        System.out.println("Create!!");
        paintDim = new Paint();
        paintDim.setAlpha(120);
//        paintDim.setColorFilter(new LightingColorFilter(0xFF7F7F7F, 0x00000000));
        //ColorFilter filter = new LightingColorFilter(0xFFFFFFFF , 0x00222222); // lighten
//        paintDim.setColorFilter(PorterDuff.Mode.LIGHTEN);
        graph = new FlowGraph(false);
        etext = new EditText(context);
        setupEditText();
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();

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
        etext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                } else if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || event == null
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    System.out.println("ENTER PRESSED");

                    // todo: validate input
                    Integer cap = null;
                    try {
                         cap = Integer.parseInt(""+textView.getText());
                    } catch (Exception e) {
                        cap = 0;
                        Toast.makeText(getContext(), "Invalid Edge Capacity!", Toast.LENGTH_SHORT).show();
                    }
                    ((FlowEdge) (selected)).setCapacity(cap);
                    toggleEditText(false);
                    return true;
                }
                return false;
            }
        });

        toggleEditText(false);
    }

    public void toggleEditText(boolean show) {
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
        selected = null;
        enableClearBtn = !graph.isEmpty();
        enableAddEdgeBtn = graph.getVertCount() > 1;
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

    private void updateSaveEnabled() {
        enableSaveBtn = graph.isSerializeReady();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getRawX(), y = event.getRawY();
                synchronized (graph) {
                    if (enableClearBtn && clearRect.contains(x, y)) { // Non
                        deselect();
                        graph.clear();
                        deselect();
                        updateSaveEnabled(); break;
                    } else if (saveRect.contains(x, y)) {   // Save
                        if (enableSaveBtn) {
                            if (saved) save(null); // update
                            else  // first save
                                new SaveDialog().show();
                        } else {
                            Toast.makeText(getContext(), "Source and Sink nodes are not set", Toast.LENGTH_LONG).show();
                        }
                        updateSaveEnabled(); break;
                    } else if (enableAddEdgeBtn && addEdgeRect.contains(x, y)) {    // Non
                        edgeMode = true;
                        updateSaveEnabled(); break;
                    } else if (enableDeleteBtn && deleteRect.contains(x, y)) { // Any
                        if (selected != null) {
                            if (selected instanceof FlowVertex)
                                graph.deleteVertex((FlowVertex) selected);
                            else graph.deleteEdge((FlowEdge) selected);
                        }
                        deselect();
                        updateSaveEnabled(); break;
                    } else if (enableChangeCapBtn && changeCapRect.contains(x, y)) {  // Edge
                        edgeMode = false;
                        toggleEditText(true);
                        // the rest will be handled by edittext key listener
                        updateSaveEnabled(); break;
                    } else if (enableSetSinkBtn && setSinkRect.contains(x, y)) {    // Vertex
                        graph.setSink((FlowVertex) (selected));
                        updateSaveEnabled(); break;
                    } else if (enableSetSrcBtn && setSrcRect.contains(x, y)) {     // Vertex
                        graph.setSrc((FlowVertex) (selected));
                        updateSaveEnabled(); break;
                    } else {
                        float[] wrldPos = toWorldCoords(x, y);
                        Clickable temp = graph.checkClick(wrldPos[0], wrldPos[1]);
                        if (temp == null) {
                            deselect();
                        } else if (temp == selected) {
                            edgeMode = false;
                        } else if (temp instanceof FlowVertex) {
                            if (edgeMode && selected != null && selected instanceof FlowVertex) {
                                edgeMode = false;
                                selectEdge(graph.addEdge((FlowVertex) selected, (FlowVertex) temp));
                                toggleEditText(true);
                                // the rest will be handled by edittext key listener
                            } else {
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
                        updateSaveEnabled(); break;
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
            if (enableSetSrcBtn) canvas.drawBitmap(setSrcBmp, null, setSrcRect, paint);
            if (enableSetSinkBtn) canvas.drawBitmap(setSinkBmp, null, setSinkRect, paint);
            if (enableChangeCapBtn) canvas.drawBitmap(changeCapBmp, null, changeCapRect, paint);
            canvas.drawBitmap(saveBmp, null, saveRect, enableSaveBtn? paint:paintDim);
        }
    }

    private void save(String name) {
        if (name != null)
            graph.setName(name);
        new AsyncTask<Context, Void, Void>() {
            @Override
            protected Void doInBackground(Context... contexts) {
                try {
                    FlowGraphEntity fge = graph.serialize();
                    FlowGraphEntityDao dao = AppDatabase.getInstance(EditorPanel.this.getContext())
                            .flowGraphEntityDao();
                    if (dao.findById(fge.getId()) != null) {
                        dao.updateAll(fge);
                    }
                    else {
                        Long[] ids = dao.insertAll(fge);
                        fge.setId((int)((long) ids[0]));
                    }
                    saved = true;
                } catch (FlowGraph.NotSerializeReadyException exception) {
                    Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    System.out.println(exception.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getContext(), "Graph Saved!", Toast.LENGTH_SHORT).show();
            }
        }.execute(getContext()); // todo: thread pool?
    }

    private class SaveDialog extends Dialog implements View.OnClickListener {
        public SaveDialog() {
            super(EditorPanel.this.getContext());
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_save);
            Button yes = (Button) findViewById(R.id.btn_yes);
            Button no = (Button) findViewById(R.id.btn_no);
            yes.setOnClickListener(this);
            no.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            EditText filename = (EditText) findViewById(R.id.et_filename);
            switch (v.getId()) {
                case R.id.btn_yes:
                    if (filename.getText().toString().isEmpty())
                        Toast.makeText(getContext(), "Can't be empty!", Toast.LENGTH_LONG).show();
                    else {
                        save(filename.getText().toString());
                        dismiss();
                    }
                    break;
                case R.id.btn_no:
                    dismiss();
                    break;
                default:
                    break;
            }
        }
    }

}
