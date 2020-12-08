package com.uni_stuttgart.isl.GaussianSolver;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.appcompat.app.AlertDialog;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by nborg on 13.08.15.
 */

public class MyCanvas extends View {

    private Paint testPaint3;
    private Paint testPaint2;
    private Paint testPaint1;
    private float testMidPointX1 = 100;
    private float testMidPointY1 = 100;
    private float testMidPointX2 = 300;
    private float testMidPointY2 = 800;
    private float testRad1 = 100;
    private float testRad2 = 100;
    private boolean active = false;
    private int dimension;

    private AlertDialog inputWindowDialog;
    private String addValueString;

    public MyCanvas(Context c, AttributeSet attrs) {
        super(c, attrs);

        testPaint1 = new Paint();
        testPaint1.setAntiAlias(true);
        testPaint1.setColor(Color.argb(255, 0, 255, 0));
        testPaint1.setStyle(Paint.Style.FILL);
        testPaint1.setStrokeJoin(Paint.Join.ROUND);
        testPaint1.setStrokeWidth(10F);

        testPaint2 = new Paint();
        testPaint2.setAntiAlias(true);
        testPaint2.setColor(Color.argb(100, 255, 0, 0));
        testPaint2.setStyle(Paint.Style.FILL);
        testPaint2.setStrokeJoin(Paint.Join.ROUND);
        testPaint2.setStrokeWidth(4F);

        testPaint3 = new Paint();
        testPaint3.setAntiAlias(true);
        testPaint3.setColor(Color.argb(100, 0, 0, 255));
        testPaint3.setStyle(Paint.Style.STROKE);
        testPaint3.setStrokeJoin(Paint.Join.ROUND);
        testPaint3.setStrokeWidth(4F);



    }

    // Zeichen Routine
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        canvas.drawCircle(testMidPointX1, testMidPointY1, testRad1, testPaint1);

//        canvas.drawCircle(testMidPointX2, testMidPointY2, testRad2, testPaint2);

        if (active){
            canvas.drawRect(testMidPointX2-testRad2-10, testMidPointY2-testRad2-10, testMidPointX2+testRad2+10, testMidPointY2+testRad2+10, testPaint3);
        }
    }



    // Routine für Aktionen bei TouchEvents
    @Override
    public boolean onTouchEvent(MotionEvent event) {

            switch (event.getAction()) {
                // Einfacher Touch, keine Bewegung
                case MotionEvent.ACTION_DOWN:
                    testMidPointX1 = event.getX();
                    testMidPointY1 = event.getY();
                    break;
                // Bewegter Touch
                case MotionEvent.ACTION_MOVE:
                    testMidPointX1 = event.getX();
                    testMidPointY1 = event.getY();

                    if (testMidPointY1 >= testMidPointY2-testRad2 && testMidPointY1 <= testMidPointY2+testRad2){
                        active = true;
                        testRad1 = 75;
                    }
                    else{
                        active = false;
                        testRad1 = 100;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (testMidPointY1 >= testMidPointY2-testRad2 && testMidPointY1 <= testMidPointY2+testRad2){
                        inputWindowDialog.show();
                    }
                    else{
                    }
                    freeMemory();
            }

        invalidate();

        return true;
    }



    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }


    public AlertDialog getInputWindowDialog() {
        return inputWindowDialog;
    }

    public void setInputWindowDialog(AlertDialog inputWindowDialog) {
        this.inputWindowDialog = inputWindowDialog;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }
}
