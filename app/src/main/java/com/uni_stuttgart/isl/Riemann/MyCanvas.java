package com.uni_stuttgart.isl.Riemann;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Switch;

import com.uni_stuttgart.isl.Function.Function;
import com.uni_stuttgart.isl.Function.RungeFunction;
import com.uni_stuttgart.isl.Function.findExtremum;
import com.uni_stuttgart.isl.Points.PurePoints;



/**
 * Created by ms
 */

public class MyCanvas extends View {
    private final Paint integrationPaint;
    private final Paint seperaterLinePaint;
    int current;
    int next;
    double middelXCoord;
    private float[] xy = new float[2];
    private RungeFunction rungeFunction = new RungeFunction();
    // Intervall auf dem der Fehler (L2, Max) berechnet werden soll
    private double start;
    private double end;
    // Paint/Path für das Koordinaten-System
    private Paint csPaint;
    private Path csPath;
    // Paint/Path für die Funktion die Interpoliert werden soll
    private Paint FuncPaint;
    private Path FuncPath;
    // Anlegen eines Objekts der Klasse Coordsystem
    private CoordSystem coordSystem;
    // Switch zum EIN/AUS-Schalten der ...
    private Switch supSumSwitch;
    private Switch infSumSwitch;
    private Function mainFunction;
    private int fid;
    //Anzahl Extremwerte fuer Runge, Sinus und Betragsfunktion
    private double[][] xValMaxima = { {0.0}, {-0.75,-0.25,0.25,0.75}, {0.0} ,{-1,0,2}, {-1,1}, {0,5, 1}};
    private double[] exactInt = {2.74680153389003, 0, 1, 4.0451774445, 4.0/3.0, 53./32.};
    // Interpolationspunkte
    private PurePoints points;


    private boolean already_init = false;


    public MyCanvas(Context c, AttributeSet attrs) {
        super(c, attrs);

        // Setzen der Parameter für die Integration
        integrationPaint = new Paint();
        integrationPaint.setAntiAlias(true);
        integrationPaint.setColor(Color.argb(85, 255, 255, 255));
        integrationPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        integrationPaint.setStrokeJoin(Paint.Join.ROUND);
        integrationPaint.setStrokeWidth(0F);

        seperaterLinePaint = new Paint();
        seperaterLinePaint.setAntiAlias(true);
        seperaterLinePaint.setColor(Color.WHITE);
        seperaterLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        seperaterLinePaint.setStrokeJoin(Paint.Join.ROUND);
        seperaterLinePaint.setStrokeWidth(3F);

        // Setzen der Parameter für das Koordinaten-System
        csPath = new Path();
        csPaint = new Paint();
        csPaint.setAntiAlias(true);
        csPaint.setColor(Color.BLACK);
        csPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        csPaint.setStrokeJoin(Paint.Join.ROUND);
        csPaint.setStrokeWidth(3F);

        // Setzen der Parameter für die Funktion
        FuncPath = new Path();
        FuncPaint = new Paint();
        FuncPaint.setAntiAlias(true);
        FuncPaint.setColor(Color.BLACK);
        FuncPaint.setStyle(Paint.Style.STROKE);
        FuncPaint.setStrokeJoin(Paint.Join.ROUND);
        FuncPaint.setStrokeWidth(8F);

    }

    // Zeichen Routine
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Zeichnen des Koordinaten Systems
        canvas.drawPath(csPath, csPaint);

        // Zeichnen der Funktion
        canvas.drawPath(FuncPath, FuncPaint);

        if(already_init) {
            points.sortPoints();
            current = points.getFirstPointNumber();
            next = points.getNextPointNumber(points.getFirstPointNumber());
            middelXCoord = 0;

            if (supSumSwitch.isChecked()) {
                if (points.getNOPP() > 1) {
                    for (int i = 0; i < points.getNOPP() - 1; i++) {
                        if (i % 2 == 0) {
                            integrationPaint.setColor(Color.argb(85, 255, 0, 0));
                        } else {
                            integrationPaint.setColor(Color.argb(85, 255, 0, 0));
                        }
                        middelXCoord = find_max((float) points.getPoint(next).getReelX(), (float) points.getPoint(current).getReelX());

                        canvas.drawPath(drawFourangle((float) points.getPoint(current).getX(), (float) coordSystem.getYTransFromReel((float) mainFunction.evaluate(middelXCoord)),
                                (float) points.getPoint(current).getX(), coordSystem.getY0(),
                                (float) points.getPoint(next).getX(), coordSystem.getY0(),
                                (float) points.getPoint(next).getX(), (float) coordSystem.getYTransFromReel((float) mainFunction.evaluate(middelXCoord))), integrationPaint);
                        current = next;
                        next = points.getNextPointNumber(next);
                    }
                }
            }

            current = points.getFirstPointNumber();
            next = points.getNextPointNumber(points.getFirstPointNumber());
            middelXCoord = 0;

            if (infSumSwitch.isChecked()) {
                if (points.getNOPP() > 1) {
                    for (int i = 0; i < points.getNOPP() - 1; i++) {
                        if (i % 2 == 0) {
                            integrationPaint.setColor(Color.argb(85, 0, 255, 0));
                        } else {
                            integrationPaint.setColor(Color.argb(85, 0, 255, 0));
                        }
                        middelXCoord = find_min((float) points.getPoint(next).getReelX(), (float) points.getPoint(current).getReelX());

                        canvas.drawPath(drawFourangle((float) points.getPoint(current).getX(), (float) coordSystem.getYTransFromReel((float) mainFunction.evaluate(middelXCoord)),
                                (float) points.getPoint(current).getX(), coordSystem.getY0(),
                                (float) points.getPoint(next).getX(), coordSystem.getY0(),
                                (float) points.getPoint(next).getX(), (float) coordSystem.getYTransFromReel((float) mainFunction.evaluate(middelXCoord))), integrationPaint);

                        current = next;
                        next = points.getNextPointNumber(next);
                    }
                }
            }

            // Seprationslinie zeichnen
            current = points.getFirstPointNumber();
            next = points.getNextPointNumber(points.getFirstPointNumber());
            for (int i = 0; i < points.getNOPP(); i++) {
                canvas.drawLine((float) points.getPoint(current).getX(), (float) coordSystem.getY0(), (float) points.getPoint(current).getX(), (float) points.getPoint(current).getY(), seperaterLinePaint);
                current = points.getNextPointNumber(current);
            }

            // Zeichnen der Interpolationspunkte
            for (int i = 0; i < points.getCount(); i++) {
                // Nicht-Aktive Punkte sollen als Rechteck gezeichnet werden und Punkt muss "placed" sein
                if (points.getPoint(i).getIsPlaced() && !points.getPoint(i).getActive()) {
                    canvas.drawRect((float) points.getPoint(i).getRectCoords()[0], (float) points.getPoint(i).getRectCoords()[1], (float) points.getPoint(i).getRectCoords()[2], (float) points.getPoint(i).getRectCoords()[3], points.getPoint(i).getPaint());
                }
                // Aktiver Punkt soll als Kreis gezeichnet werden und muss "placed" sein
                if (points.getPoint(i).getIsPlaced() && points.getPoint(i).getActive()) {
                    canvas.drawCircle((float) points.getPoint(i).getX(), (float) points.getPoint(i).getY(), 15, points.getPoint(i).getPaint());
                }
            }
        }
    }

    private Path drawFourangle(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        Path fourangle = new Path();
        fourangle.setFillType(Path.FillType.EVEN_ODD);
        fourangle.moveTo(x1, y1);
        fourangle.lineTo(x2, y2);
        fourangle.lineTo(x3, y3);
        fourangle.lineTo(x4, y4);
        fourangle.close();

        return fourangle;
    }

    // Path festlegen für das Koordinaten-System
    public void drawXtoY_cs(float x1, float y1, float x2, float y2) {
        csPath.moveTo(x1, y1);
        csPath.lineTo(x2, y2);
    }

    // Path festlegen für die Funktion die Interpoliert werden soll
    public void drawXtoY_func(float x1, float y1, float x2, float y2, int i) {
        if (i == 1) {
            FuncPath.moveTo(x1, y1);
            FuncPath.lineTo(x2, y2);
        } else {
            FuncPath.lineTo(x1, y1);
            FuncPath.lineTo(x2, y2);
        }
    }

    // Erstellen der Rechtecke um die Interpolationspunkte
    public void drawIntegratePoint(float x, int pointnumber) {
        points.getPoint(pointnumber).setX(x);
        // Setzen der transformierten y-Werte, abhängig vom Koordinatensystem
        if (coordSystem.getCoordArt() == 1 || coordSystem.getCoordArt() == 2) {
            points.getPoint(pointnumber).setY(coordSystem.getyFuncTrans()[(int) (points.getPoint(pointnumber).getX() - coordSystem.getX0())]);
            points.getPoint(pointnumber).setRectCoords();
            points.getPoint(pointnumber).setReelX(coordSystem.getxFunc()[(int) (-coordSystem.getX0() + points.getPoint(pointnumber).getX())]);
            points.getPoint(pointnumber).setReelY(coordSystem.getFunction().evaluate(coordSystem.getxFunc()[(int) (-coordSystem.getX0() + points.getPoint(pointnumber).getX())]));
        }
        if (coordSystem.getCoordArt() == 3 || coordSystem.getCoordArt() == 4) {

            points.getPoint(pointnumber).setY(coordSystem.getyFuncTrans()[(int) (points.getPoint(pointnumber).getX() - coordSystem.getX0() + coordSystem.getxLen())]);
            points.getPoint(pointnumber).setRectCoords();
            points.getPoint(pointnumber).setReelX(coordSystem.getxFunc()[(int) (-coordSystem.getX0() + coordSystem.getxLen() + points.getPoint(pointnumber).getX())]);
            points.getPoint(pointnumber).setReelY(coordSystem.getFunction().evaluate(coordSystem.getxFunc()[(int) (-coordSystem.getX0() + coordSystem.getxLen() + points.getPoint(pointnumber).getX())]));
        }
        invalidate();
    }

    // Routine für Aktionen bei TouchEvents
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        xy[0] = event.getX();
        xy[1] = event.getY();
        float x0 = coordSystem.getX0();
        double[] xCoord;
        double[] yCoord;
            switch (event.getAction()) {
                // Einfacher Touch, keine Bewegung
                case MotionEvent.ACTION_DOWN:
                    if (xy[0] < x0 + coordSystem.getxLen() + 1 && xy[0] > x0 - (coordSystem.getCoordArtFlag() - 1) * coordSystem.getxLen() - 1) {

                        // Aktivieren eine Punktes bei Touch in der Nähe
                        if (points.getPoint(points.getNearestPoint(xy[0], xy[1])).calcDiff(xy[0], xy[1]) < 2000 && points.getPoint(points.getNearestPoint(xy[0], xy[1])).getIsPlaced() && points.getNOPP() < 10) {
                            points.getPoint(points.getNearestPoint(xy[0],xy[1])).setActive(true);
                        }


                        for (int i = 0; i < points.getCount(); i++) {
                            if (points.getPoint(i).getActive()) {
                                points.getPoint(i).setIsPlaced(true);

                                drawIntegratePoint(xy[0], i);

                            }
                        }


                        points.sortPoints();

                        double supSumIntegralVaulue = 0;
                        double infSumIntegralVaulue = 0;
                        if (getsupSumSwitch().isChecked() && points.getNOPP() >= 2) {

                            current = points.getFirstPointNumber();
                            next = points.getNextPointNumber(points.getFirstPointNumber());

                            for (int i = 0; i < points.getNOPP() - 1; i++) {

                                double middelXCoord = find_max((float)points.getPoint(next).getReelX(), (float)points.getPoint(current).getReelX());

                                supSumIntegralVaulue = supSumIntegralVaulue + coordSystem.getFunction().evaluate(middelXCoord) * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX());
                                current = next;
                                next = points.getNextPointNumber(next);
                            }

                            coordSystem.getsupSumErrorView().setText("" + String.format("%.5f", Math.abs(supSumIntegralVaulue -exactInt[fid])));
                            coordSystem.getsupSumSolView().setText("" +  String.format("%.5f", supSumIntegralVaulue));
                        }
                        if (getinfSumSwitch().isChecked() && points.getNOPP() >= 2) {
                            points.sortPoints();


                            current = points.getFirstPointNumber();
                            next = points.getNextPointNumber(points.getFirstPointNumber());

                            for (int i = 0; i < points.getNOPP() - 1; i++) {
                                double middelXCoord = find_min((float)points.getPoint(next).getReelX(), (float)points.getPoint(current).getReelX());

                                infSumIntegralVaulue = infSumIntegralVaulue + coordSystem.getFunction().evaluate(middelXCoord) * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX());
                                current = next;
                                next = points.getNextPointNumber(next);
                            }

                            coordSystem.getinfSumErrorView().setText("" + String.format("%.5f", (Math.abs(infSumIntegralVaulue -exactInt[fid]))));
                            coordSystem.getinfSumSolView().setText("" +  String.format("%.5f", infSumIntegralVaulue));

                        }
                        if (getinfSumSwitch().isChecked() && getsupSumSwitch().isChecked() &&points.getNOPP() >= 2) {
                            coordSystem.getdiffInfSumView().setText("" + String.format("%.5f", (Math.abs(infSumIntegralVaulue -supSumIntegralVaulue))));
                        }

                    }
                    break;
                //Bewegter Touch
                case MotionEvent.ACTION_MOVE:
                    if (xy[0] < x0 + coordSystem.getxLen() + 1 && xy[0] > x0 - (coordSystem.getCoordArtFlag() - 1) * coordSystem.getxLen() - 1) {

                        // Schleife über alle Punkte
                        for (int j = 0; j < points.getCount(); j++) {
                            // Abfrage, ob es sich um den aktiven Punkt handelt.
                            if (points.getPoint(j).getActive()) {
                                points.sortPoints();

                                // Punkt neu zeichnen (Koordinaten ändern sich ja ständig)
                                drawIntegratePoint(xy[0], j);


                                double supSumIntegralVaulue = 0;
                                if (getsupSumSwitch().isChecked() && points.getNOPP() >= 2) {

                                    current = points.getFirstPointNumber();
                                    next = points.getNextPointNumber(points.getFirstPointNumber());

                                    for (int i = 0; i < points.getNOPP() - 1; i++) {

                                        double middelXCoord = find_max((float)points.getPoint(next).getReelX(), (float)points.getPoint(current).getReelX());

                                        supSumIntegralVaulue = supSumIntegralVaulue + coordSystem.getFunction().evaluate(middelXCoord) * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX());
                                        current = next;
                                        next = points.getNextPointNumber(next);
                                    }

                                    coordSystem.getsupSumErrorView().setText("" + String.format("%.5f", (Math.abs(supSumIntegralVaulue -exactInt[fid]))));
                                    coordSystem.getsupSumSolView().setText("" +  String.format("%.5f", supSumIntegralVaulue));
                                }
                                double infSumIntegralVaulue = 0;
                                if (getinfSumSwitch().isChecked() && points.getNOPP() >= 2) {
                                    points.sortPoints();


                                    current = points.getFirstPointNumber();
                                    next = points.getNextPointNumber(points.getFirstPointNumber());

                                    for (int i = 0; i < points.getNOPP() - 1; i++) {
                                        double middelXCoord = find_min((float)points.getPoint(next).getReelX(), (float)points.getPoint(current).getReelX());
                                        infSumIntegralVaulue = infSumIntegralVaulue + coordSystem.getFunction().evaluate(middelXCoord) * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX());
                                        current = next;
                                        next = points.getNextPointNumber(next);
                                    }

                                    coordSystem.getinfSumErrorView().setText("" + String.format("%.5f", (Math.abs(infSumIntegralVaulue -exactInt[fid]))));
                                    coordSystem.getinfSumSolView().setText("" + String.format("%.5f", infSumIntegralVaulue));


                                }
                                if (getinfSumSwitch().isChecked() && getsupSumSwitch().isChecked() &&points.getNOPP() >= 2) {
                                    coordSystem.getdiffInfSumView().setText("" + String.format("%.5f", (Math.abs(infSumIntegralVaulue -supSumIntegralVaulue))));
                                }
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    for (int i = 0; i < points.getCount(); i++) {
                        points.getPoint(i).setActive(false);
                    }
                    freeMemory();
            }
//        }

        invalidate();

        return true;
    }

    public void setMainFunction(Function function, int id) { this.mainFunction = function; this.fid = id;}

    public void setCoordSystem(CoordSystem coordSystem) {
        this.coordSystem = coordSystem;
    }

    public void setPoints(PurePoints points) {
        this.points = points;
    }

    public void deletePoint() {
        invalidate();
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
        FuncPath.reset();
        invalidate();
    }

    public void clearPoints() {
        for (int i = 0; i < points.getCount(); i++) {
            points.deletePoint(i);
        }
        invalidate();
    }

    public Switch getsupSumSwitch() {
        return supSumSwitch;
    }

    public void setsupSumSwitch(Switch supSumSwitch) {
        this.supSumSwitch = supSumSwitch;
    }

    public Switch getinfSumSwitch() {
        return infSumSwitch;
    }

    public void setinfSumSwitch(Switch infSumSwitch) {
        this.infSumSwitch = infSumSwitch;
    }

    public Path drawTriangle(float x1, float y1, float x2, float y2, float x3, float y3) {
        Path triangle = new Path();
        triangle.setFillType(Path.FillType.EVEN_ODD);
        triangle.moveTo(x1, y1);
        triangle.lineTo(x2, y2);
        triangle.lineTo(x3, y3);
        triangle.close();

        return triangle;
    }

    public float find_max(float a, float b) {
        float res;
        if(a>b) {
            float tmp = a;
            a = b;
            b = tmp;
        }
        if (mainFunction.evaluate(a) > mainFunction.evaluate(b))
            res = a;
        else
            res = b;
        for(int j = 0; j < xValMaxima[fid].length; j++) {
            if(xValMaxima[fid][j] > a && xValMaxima[fid][j] < b)
                if (mainFunction.evaluate(xValMaxima[fid][j]) > mainFunction.evaluate(res))
                    res = (float)xValMaxima[fid][j];
        }

        return res;
    }

    public float find_min(float a, float b) {
        float res;
        if(a>b) {
            float tmp = a;
            a = b;
            b = tmp;
        }
        if (mainFunction.evaluate(a) < mainFunction.evaluate(b))
            res = a;
        else
            res = b;
        for(int j = 0; j < xValMaxima[fid].length; j++) {
            if(xValMaxima[fid][j] > a && xValMaxima[fid][j] < b)
                if (mainFunction.evaluate(xValMaxima[fid][j]) < mainFunction.evaluate(res))
                    res = (float)xValMaxima[fid][j];
        }

        return res;
    }

    public void init_finished() { this.already_init = true;}
}
