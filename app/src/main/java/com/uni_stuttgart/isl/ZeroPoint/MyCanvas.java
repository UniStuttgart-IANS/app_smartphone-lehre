package com.uni_stuttgart.isl.ZeroPoint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.uni_stuttgart.isl.Function.NewtonSometimesFunction;
import com.uni_stuttgart.isl.Points.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nborg on 13.08.15.
 */

public class MyCanvas extends View {
    private final Paint pointPaint;
    private final Paint gradPaint;
    private Paint startPointPaint;
    Point startPoint = new Point(0);
    private float[] xy = new float[2];
    // Intervall auf dem der Fehler (L2, Max) berechnet werden soll
    private double start;
    private double end;
    // Paint/Path für das Koordinaten-System
    private Paint csPaint;
    private Path csPath;
    // Paint/Path für die Funktion die Interpoliert werden soll
    private Paint funcPaint;
    private Path funcPath;
    // Anlegen eines Objekts der Klasse Coordsystem
    private CoordSystem coordSystem;
    private float y;
    private float x;
    private boolean isTouchAllowed = true;
    private Path gradPath;
    private double rootGradientReal;
    private double rootGradientTrans;
    private Paint extraLinePaint;
    private Path extraLinePath;
    private Paint textPaint;

    private Paint clearPaint;

    private String drawCoords = "";

    private boolean isMarksAllowed = false;

    private double[][] xCoords = new double[100][2];

    private int iterationCounter;
    private boolean isDrawItersAllowed;
    private boolean isRunning;
    private boolean isFunctionChanged;

    private int iterCounter;
    private boolean backward = false;

    private List colorList = new ArrayList<Color>();
    private Paint tempPaint = new Paint();

    private int maxiter;


    public MyCanvas(Context c, AttributeSet attrs) {
        super(c, attrs);

        // Setzen der Parameter für das Koordinaten-System
        csPath = new Path();
        csPaint = new Paint();
        csPaint.setAntiAlias(true);
        csPaint.setColor(Color.BLACK);
        csPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        csPaint.setStrokeJoin(Paint.Join.ROUND);
        csPaint.setStrokeWidth(3F);

        startPointPaint = new Paint();
        startPointPaint.setAntiAlias(true);
        startPointPaint.setColor(Color.WHITE);
        startPointPaint.setStyle(Paint.Style.STROKE);
        startPointPaint.setStrokeJoin(Paint.Join.ROUND);
        startPointPaint.setStrokeWidth(20F);

        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setColor(Color.WHITE);
        pointPaint.setStyle(Paint.Style.STROKE);
        pointPaint.setStrokeJoin(Paint.Join.ROUND);
        pointPaint.setStrokeWidth(20F);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.GREEN);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setStrokeJoin(Paint.Join.BEVEL);
        textPaint.setStrokeWidth(1F);
        textPaint.setTextSize(20);

        // Setzen der Parameter für die Funktion
        funcPath = new Path();
        funcPaint = new Paint();
        funcPaint.setAntiAlias(true);
        funcPaint.setColor(Color.BLACK);
        funcPaint.setStyle(Paint.Style.STROKE);
        funcPaint.setStrokeJoin(Paint.Join.ROUND);
        funcPaint.setStrokeWidth(8F);

        // Setzen der Parameter für den Gradienten
        gradPath = new Path();
        gradPaint = new Paint();
        gradPaint.setAntiAlias(true);
        gradPaint.setColor(Color.WHITE);
        gradPaint.setStyle(Paint.Style.STROKE);
        gradPaint.setStrokeJoin(Paint.Join.ROUND);
        gradPaint.setStrokeWidth(4F);

        extraLinePath = new Path();
        extraLinePaint = new Paint();
        extraLinePaint.setAntiAlias(true);
        extraLinePaint.setColor(Color.WHITE);
        extraLinePaint.setStyle(Paint.Style.STROKE);
        extraLinePaint.setStrokeJoin(Paint.Join.ROUND);
        extraLinePaint.setStrokeWidth(4F);

        tempPaint = new Paint();
        tempPaint.setAntiAlias(true);
        tempPaint.setStyle(Paint.Style.STROKE);
        tempPaint.setStrokeJoin(Paint.Join.BEVEL);
        tempPaint.setStrokeWidth(20F);

        clearPaint = new Paint();
        clearPaint.setAntiAlias(true);
        clearPaint.setStyle(Paint.Style.FILL);
        clearPaint.setStrokeJoin(Paint.Join.ROUND);
        clearPaint.setStrokeWidth(1F);
        clearPaint.setARGB(0, 192,192,192);

        startPoint.setPaint(startPointPaint);
    }

    // Zeichen Routine
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0,0,getLayoutParams().width,getLayoutParams().width, clearPaint);

        // Zeichnen des Koordinaten Systems
        canvas.drawPath(csPath, csPaint);

        // Zeichnen der Funktion
        canvas.drawPath(funcPath, funcPaint);

        // Zeichnen des Gradienten
        canvas.drawPath(gradPath, gradPaint);

        // Zeichnen der Geraden von der Nullstelle des Gradienten zur Funktion
        canvas.drawPath(extraLinePath, extraLinePaint);

        if (!backward) {
            if (isDrawItersAllowed) {
                for (int i = 0; i <= iterCounter; i++) {
                    tempPaint.setColor((int) colorList.get(i));
                    if (xCoords[i][0] != 0) {
                        if (i<(int) maxiter/2) {
                            canvas.drawCircle((float) xCoords[i][1], (float) coordSystem.getYTransFromReel(coordSystem.getFunction().evaluate(xCoords[i][0])), 5, tempPaint);
                        }
                        else{
                            canvas.drawRect((float) xCoords[i][1]-3, (float) coordSystem.getYTransFromReel(coordSystem.getFunction().evaluate(xCoords[i][0]))-3,
                                    (float) xCoords[i][1]+3, (float) coordSystem.getYTransFromReel(coordSystem.getFunction().evaluate(xCoords[i][0]))+3, tempPaint);
                        }
                    }
                }
            }
        }
        else{
            if (isDrawItersAllowed) {
                // Beim Rückwärstgehen muss nur bis "<" iterCounter gegangen werden, nicht bis "<="! Sonst identisch zu dem if Term
                for (int i = 0; i < iterCounter; i++) {
                    tempPaint.setColor((int) colorList.get(i));
                    canvas.drawCircle((float) xCoords[i][1], (float) coordSystem.getYTransFromReel(coordSystem.getFunction().evaluate(xCoords[i][0])), 5, tempPaint);
                }
            }
        }

        //Aktueller Punkt
        canvas.drawCircle(x, y, 5, pointPaint);

        if (isTouchAllowed && !isFunctionChanged) {
            if (isMarksAllowed) {
                canvas.drawText(drawCoords, (float) startPoint.getX() - 65, (float) startPoint.getY() - 50, textPaint);
            }
        } else {
            if (isMarksAllowed) {
                canvas.drawText(drawCoords, (float) x - 65, (float) y - 50, textPaint);
            }
        }
        Log.e("TAG", "IsFunctionChanged6 = " + isFunctionChanged());
        if (!isRunning && !isFunctionChanged) {
            // Startpunkt zeichnen
            if (!startPoint.getActive()) {
                canvas.drawRect((float) startPoint.getRectCoords()[0], (float) startPoint.getRectCoords()[1], (float) startPoint.getRectCoords()[2], (float) startPoint.getRectCoords()[3], startPoint.getPaint());
            }
            if (startPoint.getActive()) {
                canvas.drawCircle((float) startPoint.getX(), (float) startPoint.getY(), 15, startPoint.getPaint());
            }
        }
        setFunctionChanged(false);
    }

    // Path festlegen für das Koordinaten-System
    public void drawXtoY_cs(float x1, float y1, float x2, float y2) {
        csPath.moveTo(x1, y1);
        csPath.lineTo(x2, y2);
    }

    // Path festlegen für die Funktion die Interpoliert werden soll
    public void drawXtoY_func(float x1, float y1, float x2, float y2, int i) {
        if (coordSystem.getFunction() instanceof NewtonSometimesFunction) {
            if (i == 5) {
                funcPath.moveTo(x1, y1);
                funcPath.lineTo(x2, y2);
            } else if (i > 5) {
                funcPath.lineTo(x1, y1);
                funcPath.lineTo(x2, y2);
            }
        }
        else{
            if (i == 1) {
                funcPath.moveTo(x1, y1);
                funcPath.lineTo(x2, y2);
            } else if (i > 1) {
                funcPath.lineTo(x1, y1);
                funcPath.lineTo(x2, y2);
            }
        }
    }

    public void drawXtoY_grad(float x1, float y1, float x2, float y2, int i) {
        if (i == 1) {
            gradPath.moveTo(x1, Math.min(Math.max(0, y1), 1450));
            gradPath.lineTo(x2, Math.min(Math.max(0, y2), 1450));
        } else {
            gradPath.lineTo(x1, Math.min(Math.max(0, y1), 1450));
            gradPath.lineTo(x2, Math.min(Math.max(0, y2), 1450));
        }
    }

    public void drawXtoY_extraLine(float x1, float y1, float x2, float y2) {
        extraLinePath.moveTo(x1, y1);
        extraLinePath.lineTo(x2, y2);
        invalidate();
    }

    // Routine für Aktionen bei TouchEvents
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        xy[0] = event.getX();
        xy[1] = event.getY();
        float x0 = coordSystem.getX0();

        switch (event.getAction()) {
            // Einfacher Touch, keine Bewegung
            case MotionEvent.ACTION_DOWN:
                if (xy[0] < x0 + coordSystem.getxLen() + 1 && xy[0] > x0 - (coordSystem.getCoordArtFlag() - 1) * coordSystem.getxLen() - 1) {
                    if (isTouchAllowed) {
                        startPoint.setActive(true);
                        this.setDrawCoords(startPoint.getReelX(), startPoint.getReelY());
                        drawABCD(xy[0]);
                    } else {
                        Toast.makeText(getContext(), "Bitte zuerst aktuellen Durchlauf beenden", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            //Bewegter Touch
            case MotionEvent.ACTION_MOVE:
                if (xy[0] < x0 + coordSystem.getxLen() + 1 && xy[0] > x0 - (coordSystem.getCoordArtFlag() - 1) * coordSystem.getxLen() - 1) {
                    if (isTouchAllowed) {
                        startPoint.setActive(true);
                        this.setDrawCoords(startPoint.getReelX(), startPoint.getReelY());
                        drawABCD(xy[0]);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                startPoint.setActive(false);
                break;
        }

        invalidate();
        return true;
    }

    public void drawpoint(double x, double y) {
        this.x = (float) x;
        this.y = (float) y;
        invalidate();
    }

    public void setCoordSystem(CoordSystem coordSystem) {
        this.coordSystem = coordSystem;
    }

    public void setEnd(double end) {
        this.end = end;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    public void clearFunc() {
        funcPath.reset();
        invalidate();
    }

    public void clearGrad() {
        gradPath.reset();
        gradPath = new Path();
        invalidate();
    }

    public void clearExtraLine() {
        extraLinePath.reset();
        extraLinePath = new Path();
        invalidate();
    }

    // Erstellen der Rechtecke um die Interpolationspunkte
    public void drawABCD(float x) {
        startPoint.setX(x);
        // Setzen der transformierten y-Werte, abhängig vom Koordinatensystem
        if (coordSystem.getCoordArt() == 1 || coordSystem.getCoordArt() == 2) {
            startPoint.setY(coordSystem.getyFuncTrans()[(int) (startPoint.getX() - coordSystem.getX0())]);
            startPoint.setRectCoords();
            startPoint.setReelX(coordSystem.getxFunc()[(int) (-coordSystem.getX0() + startPoint.getX())]);
            startPoint.setReelY(coordSystem.getFunction().evaluate(coordSystem.getxFunc()[(int) (-coordSystem.getX0() + startPoint.getX())]));
        }
        if (coordSystem.getCoordArt() == 3 || coordSystem.getCoordArt() == 4) {
            startPoint.setY(coordSystem.getyFuncTrans()[(int) (startPoint.getX() - coordSystem.getX0() + coordSystem.getxLen())]);
            startPoint.setRectCoords();
            startPoint.setReelX(coordSystem.getxFunc()[(int) (-coordSystem.getX0() + coordSystem.getxLen() + startPoint.getX())]);
            startPoint.setReelY(coordSystem.getFunction().evaluate(coordSystem.getxFunc()[(int) (-coordSystem.getX0() + coordSystem.getxLen() + startPoint.getX())]));
        }
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public void setTouchPermission(boolean touchPermission) {
        isTouchAllowed = touchPermission;
    }

    public double getRootGradientReal() {
        return rootGradientReal;
    }

    public void setRootGradientReal(double rootGradientReal) {
        this.rootGradientReal = rootGradientReal;
    }

    public double getRootGradientTrans() {
        return rootGradientTrans;
    }

    public void setRootGradientTrans(double rootGradientTrans) {
        this.rootGradientTrans = rootGradientTrans;
    }

    public Paint getStartPointPaint() {
        return startPointPaint;
    }

    public void setStartPointPaint(Paint startPointPaint) {
        this.startPointPaint = startPointPaint;
    }

    public String getDrawCoords() {
        return drawCoords;
    }

    public void setDrawCoords(double x, double y) {
        this.drawCoords = "" + "(" + Math.round(x * 1e3) / 1e3 + ", " + Math.round(y * 1e3) / 1e3 + ")";
    }

    public boolean isMarksAllowed() {
        return isMarksAllowed;
    }

    public void setMarksAllowed(boolean marksAllowed) {
        this.isMarksAllowed = marksAllowed;
    }

    public double[][] getxCoords() {
        return xCoords;
    }

    public void setxCoords(double[][] xCoords) {
        this.xCoords = xCoords;
    }

    public int getIterationCounter() {
        return iterationCounter;
    }

    public void setIterationCounter(int iterationCounter) {
        this.iterationCounter = iterationCounter;
    }

    public void setDrawItersAllowed(boolean drawItersAllowed) {
        isDrawItersAllowed = drawItersAllowed;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public boolean isFunctionChanged() {
        return isFunctionChanged;
    }

    public void setFunctionChanged(boolean functionChanged) {
        isFunctionChanged = functionChanged;
    }

    public void setIterCounter(int iterCounter) {
        this.iterCounter = iterCounter;
    }

    public void setBackward(boolean backward) {
        this.backward = backward;
    }

    public void setColorList(List colorList) {
        this.colorList = colorList;
    }

    public Path getCsPath() {
        return csPath;
    }

    public void setMaxiter(int maxiter) {
        this.maxiter = maxiter;
    }
}
