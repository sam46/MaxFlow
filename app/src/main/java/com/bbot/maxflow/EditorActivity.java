package com.bbot.maxflow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;


public class EditorActivity extends Activity {

    EditorPanel editorPanel;
    FrameLayout lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        editorPanel = new EditorPanel(this);
        lay = new FrameLayout(this);
        FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        editorPanel.etext.setLayoutParams(lparams);


        lay.addView(editorPanel);
        lay.addView(editorPanel.etext);

        setContentView(lay);







//        EditorPanel ep = new EditorPanel(this);
//        EditText et = (EditText) findViewById(R.id.editText);
//        setContentView(ep);
//        addContentView(et, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(false)
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                System.out.println("ACTION_DOWN");
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("ACTION_UP");
                break;
            case MotionEvent.ACTION_CANCEL:
                System.out.println("ACTION_CANCEL");
                break;
            case MotionEvent.ACTION_MOVE:
                System.out.println("ACTION_MOVE");
                break;
            case MotionEvent.ACTION_POINTER_UP:
                System.out.println("ACTION_POINTER_UP");
                break;
            case MotionEvent.ACTION_OUTSIDE:
                System.out.println("ACTION_OUTSIDE");
                break;
            default:
                System.out.println("OTHER ACTION");
                break;
        }
        View v = getCurrentFocus();
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
        if (v instanceof EditText) {
            System.out.println("T has focus");
            if (v.isEnabled() && event.getAction() == MotionEvent.ACTION_DOWN) {

                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {

                    editorPanel.toggleEditText(false);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    System.out.println("   turned off T");
                    return true;
                }
            }
        }
// else if (v instanceof EditorPanel) {
////                System.out.println("dispatch editor");
//            return v.dispatchTouchEvent(event);
////                v.onTouchEvent(event);
////                return  true;
////                return  false;
//        }

        return super.dispatchTouchEvent(event);
    }
}