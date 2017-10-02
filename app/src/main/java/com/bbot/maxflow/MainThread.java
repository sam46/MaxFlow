package com.bbot.maxflow;
import android.view.SurfaceHolder;
import android.graphics.Canvas;


public class MainThread extends Thread {
    public  static int MAX_FPS = 30;
    private double avgFPS;
    private SurfaceHolder surfaceHolder;
    private MainPanel mainPanel;
    private boolean running;
    public static Canvas canvas;


    public MainThread(SurfaceHolder holder, MainPanel mainPanel){
        super();
        this.surfaceHolder = holder;
        this.mainPanel = mainPanel;
    }

    public void setRunning(boolean arg){
        this.running = arg;
    }

    @Override
    public void run() {

        long startTime, waitTime, timeMillis = 1000/MAX_FPS;
        long totalTime = 0, targetTime = timeMillis;
        int frameCount = 0;
        while(running) {
            startTime = System.nanoTime();
            canvas = null;

            try {
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    this.mainPanel.update();
                    this.mainPanel.draw(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();;
            } finally {
                if(canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {e.printStackTrace();}

                }
            }

            timeMillis = (System.nanoTime() - startTime) / 1000000;
            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if(frameCount == MAX_FPS) {
                avgFPS = 1000/((totalTime/frameCount)/1000000);
                frameCount = 0;
                totalTime = 0;
                //System.out.println(avgFPS);
            }


        }
    }


}
















































