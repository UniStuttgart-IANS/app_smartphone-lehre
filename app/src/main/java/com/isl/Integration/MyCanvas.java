package com.uni_stuttgart.isl.Integration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.uni_stuttgart.isl.Function.GaussQuadraturFunction;
import com.uni_stuttgart.isl.Function.PolynomToolbox;
import com.uni_stuttgart.isl.Function.RungeFunction;
import com.uni_stuttgart.isl.Function.SimpsonFunction;
import com.uni_stuttgart.isl.Points.Points;

/**
 * Created by nborg on 13.08.15.
 */

public class MyCanvas extends View {
    private static final String TAG = "Blah";
    private final Paint integrationPaint;
    private final Paint simpsonPaint;
    private final Paint seperaterLinePaint;
    private final Paint gaussPointsPaint;
    private final Paint simpsonMidPointPaint;
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
    private Path simpsonIntegratePath;
    // Anlegen eines Objekts der Klasse Coordsystem
    private CoordSystem coordSystem;
    // Switch zum EIN/AUS-Schalten der ...
    private Switch startPointSwitch;
    private Switch middelPointSwitch;
    private Switch trapezSwitch;
    private Switch simpsonSwitch;
    private Switch gaussSwitch;
    private SeekBar gaussSeekbar;
    private TextView gaussDegreeView;
    // Interpolationspunkte
    private Points points;
    private SimpsonFunction simpsonFunction;
    private GaussQuadraturFunction gaussQuadraturFunction = new GaussQuadraturFunction();
    private Path simpsonPath;
    private double[] legendrePoints;
    private Path gaussPath;
    private Path tschebychowIntegratePath;

    private boolean is_init = false;

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

        // Setzen der Parameter für die Hermit
        simpsonPath = new Path();
        simpsonMidPointPaint = new Paint();
        simpsonMidPointPaint.setAntiAlias(true);
        simpsonMidPointPaint.setColor(Color.argb(255, 238, 25, 245));
        simpsonMidPointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        simpsonMidPointPaint.setStrokeJoin(Paint.Join.ROUND);
        simpsonMidPointPaint.setStrokeWidth(1F);

        simpsonPaint = new Paint();
        simpsonPaint.setAntiAlias(true);
        simpsonPaint.setColor(Color.argb(85, 238, 25, 245));
        simpsonPaint.setStyle(Paint.Style.STROKE);
        simpsonPaint.setStrokeJoin(Paint.Join.ROUND);
        simpsonPaint.setStrokeWidth(0F);

        gaussPath = new Path();
        gaussPointsPaint = new Paint();
        gaussPointsPaint.setAntiAlias(true);
        gaussPointsPaint.setColor(Color.rgb(255, 255, 196));
        gaussPointsPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        gaussPointsPaint.setStrokeJoin(Paint.Join.ROUND);
        gaussPointsPaint.setStrokeWidth(1F);
    }

    // Zeichen Routine
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(is_init) {
            points.sortPoints();

            // Zeichnen des Koordinaten Systems
            canvas.drawPath(csPath, csPaint);

            // Zeichnen der Funktion
            canvas.drawPath(FuncPath, FuncPaint);

            middelXCoord = 0;

            current = points.getFirstPointNumber();
            next = points.getNextPointNumber(points.getFirstPointNumber());

            if (startPointSwitch.isChecked()) {
                if (points.getNOPP() > 1) {
                    for (int i = 0; i < points.getNOPP() - 1; i++) {
                        if (i % 2 == 0) {
                            integrationPaint.setColor(Color.argb(85, 0, 0, 255));
                        } else {
                            integrationPaint.setColor(Color.argb(85, 0, 0, 255));
                        }
                        canvas.drawPath(drawFourangle((float) points.getPoint(current).getX(), (float) points.getPoint(current).getY(),
                                (float) points.getPoint(current).getX(), coordSystem.getY0(),
                                (float) points.getPoint(next).getX(), coordSystem.getY0(),
                                (float) points.getPoint(next).getX(), (float) points.getPoint(current).getY()), integrationPaint);
                        current = next;
                        next = points.getNextPointNumber(next);
                    }
                }

            }
            current = points.getFirstPointNumber();
            next = points.getNextPointNumber(points.getFirstPointNumber());
            middelXCoord = 0;

            if (middelPointSwitch.isChecked()) {
                if (points.getNOPP() > 1) {
                    for (int i = 0; i < points.getNOPP() - 1; i++) {
                        if (i % 2 == 0) {
                            integrationPaint.setColor(Color.argb(85, 255, 0, 0));
                        } else {
                            integrationPaint.setColor(Color.argb(85, 255, 0, 0));
                        }
                        middelXCoord = 1.0 / 2.0 * (points.getPoint(next).getX() + points.getPoint(current).getX());
                        canvas.drawPath(drawFourangle((float) points.getPoint(current).getX(), (float) coordSystem.getyFuncTrans()[(int) (middelXCoord - coordSystem.getX0() + coordSystem.getxLen())],
                                (float) points.getPoint(next).getX(), (float) coordSystem.getyFuncTrans()[(int) (middelXCoord - coordSystem.getX0() + coordSystem.getxLen())],
                                (float) points.getPoint(next).getX(), coordSystem.getY0(),
                                (float) points.getPoint(current).getX(), coordSystem.getY0()), integrationPaint);
                        current = next;
                        next = points.getNextPointNumber(next);
                    }
                }
            }

            current = points.getFirstPointNumber();
            next = points.getNextPointNumber(points.getFirstPointNumber());
            middelXCoord = 0;

            if (trapezSwitch.isChecked()) {
                if (points.getNOPP() > 1) {
                    for (int i = 0; i < points.getNOPP() - 1; i++) {
                        if (i % 2 == 0) {
                            integrationPaint.setColor(Color.argb(85, 0, 255, 0));
                        } else {
                            integrationPaint.setColor(Color.argb(85, 0, 255, 0));
                        }
                        if (points.getPoint(current).getReelY() > 0 && points.getPoint(next).getReelY() < 0 || points.getPoint(current).getReelY() < 0 && points.getPoint(next).getReelY() > 0) {
                            canvas.drawPath(drawTriangle((float) points.getPoint(current).getX(), (float) points.getPoint(current).getY(),
                                    (float) points.getPoint(current).getX(), coordSystem.getY0(),
                                    (float) coordSystem.getXTransFromReel(-points.getPoint(current).getReelY() * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX()) / (points.getPoint(next).getReelY() - points.getPoint(current).getReelY()) + points.getPoint(current).getReelX()), (float) coordSystem.getY0()),
                                    integrationPaint);
                            canvas.drawPath(drawTriangle((float) points.getPoint(next).getX(),
                                    (float) points.getPoint(next).getY(), (float) points.getPoint(next).getX(),
                                    coordSystem.getY0(), (float) coordSystem.getXTransFromReel(-points.getPoint(current).getReelY() * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX()) / (points.getPoint(next).getReelY() - points.getPoint(current).getReelY()) + points.getPoint(current).getReelX()), (float) coordSystem.getY0()),
                                    integrationPaint);
                        } else {
                            canvas.drawPath(drawFourangle((float) points.getPoint(current).getX(), (float) points.getPoint(current).getY(),
                                    (float) points.getPoint(next).getX(), (float) points.getPoint(next).getY(),
                                    (float) points.getPoint(next).getX(), (float) coordSystem.getY0(),
                                    (float) points.getPoint(current).getX(), (float) coordSystem.getY0()), integrationPaint);
                        }

                        current = next;
                        next = points.getNextPointNumber(next);
                    }
                }
            }

            if (simpsonSwitch.isChecked()) {
                if (points.getNOPP() >= 2) {
                    points.sortPoints();
                    current = points.getFirstPointNumber();
                    next = points.getNextPointNumber(points.getFirstPointNumber());

                    for (int i = 0; i < points.getNOPP() - 1; i++) {
                        integrationPaint.setColor(Color.argb(85, 238, 25, 245));

                        canvas.drawPath(drawSimpsonIntegration(current, next), integrationPaint);

//                    canvas.drawPoint((float) (1.0/2.0 * (points.getPoint(current).getX()+points.getPoint(next).getX())), (float) coordSystem.getyFuncTrans()[(int) (1.0/2.0 * (points.getPoint(current).getX()+points.getPoint(next).getX()) - coordSystem.getX0() + coordSystem.getxLen())], simpsonMidPointPaint);
                        canvas.drawCircle((float) (1.0 / 2.0 * (points.getPoint(current).getX() + points.getPoint(next).getX())), (float) coordSystem.getyFuncTrans()[(int) (1.0 / 2.0 * (points.getPoint(current).getX() + points.getPoint(next).getX()) - coordSystem.getX0() + coordSystem.getxLen())], 8, simpsonMidPointPaint);

                        current = next;
                        next = points.getNextPointNumber(next);
                    }
                }
            }

            // Gauß-Kram
            if (gaussSwitch.isChecked()) {
                if (points.getNOPP() >= 2) {
                    points.sortPoints();

                    integrationPaint.setColor(Color.argb(85, 255, 255, 196));

                    canvas.drawPath(drawGaußStairs(), integrationPaint);

                    current = points.getFirstPointNumber();
                    next = points.getNextPointNumber(points.getFirstPointNumber());

                    for (int i = 0; i < points.getNOPP() - 1; i++) {
                        legendrePoints = PolynomToolbox.getLegendreCoords((double) points.getPoint(current).getX(), (double) points.getPoint(next).getX(), gaussSeekbar.getProgress() + 1);
                        for (int j = 0; j < gaussSeekbar.getProgress() + 1; j++) {
//                        canvas.drawPoint((float) legendrePoints[j], (float) coordSystem.getyFuncTrans()[(int) (legendrePoints[j] - coordSystem.getX0() + coordSystem.getxLen())], gaussPointsPaint);
                            canvas.drawCircle((float) legendrePoints[j], (float) coordSystem.getyFuncTrans()[(int) (legendrePoints[j] - coordSystem.getX0() + coordSystem.getxLen())], 8, gaussPointsPaint);

                        }
                        current = next;
                        next = points.getNextPointNumber(current);
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

    private Path drawGaußStairs() {
        Path gaußPath = new Path();
        float x;
        current = points.getFirstPointNumber();
        next = points.getNextPointNumber(points.getFirstPointNumber());

        for (int i = 0; i < points.getNOPP() - 1; i++) {
            legendrePoints = PolynomToolbox.getLegendreCoords((double) points.getPoint(current).getX(), (double) points.getPoint(next).getX(), gaussSeekbar.getProgress() + 1);
            x = (float) points.getPoint(current).getX();
            gaußPath.moveTo(x, coordSystem.getY0());
            gaußPath.lineTo(x, (float) coordSystem.getyFuncTrans()[(int) (legendrePoints[0] - coordSystem.getX0() + coordSystem.getxLen())]);
            x = (float) (x + gaussQuadraturFunction.getWeights(gaussSeekbar.getProgress() + 1, points.getPoint(next).getReelX() - points.getPoint(current).getReelX())[0] * coordSystem.getxLen() / coordSystem.getReelXMax());
            gaußPath.lineTo(x, (float) coordSystem.getyFuncTrans()[(int) (legendrePoints[0] - coordSystem.getX0() + coordSystem.getxLen())]);

            for (int j = 1; j < gaussSeekbar.getProgress() + 1; j++) {
                gaußPath.lineTo(x, (float) coordSystem.getyFuncTrans()[(int) (legendrePoints[j] - coordSystem.getX0() + coordSystem.getxLen())]);
                x = (float) (x + gaussQuadraturFunction.getWeights(gaussSeekbar.getProgress() + 1, points.getPoint(next).getReelX() - points.getPoint(current).getReelX())[j] * coordSystem.getxLen() / coordSystem.getReelXMax());
                gaußPath.lineTo(x, (float) coordSystem.getyFuncTrans()[(int) (legendrePoints[j] - coordSystem.getX0() + coordSystem.getxLen())]);

            }
            gaußPath.lineTo(x, coordSystem.getY0());

            current = next;
            next = points.getNextPointNumber(current);
        }
        gaußPath.close();

        return gaußPath;
    }


    public void clearSimpsonFunc() {
        simpsonPath.reset();
        invalidate();
    }

    // Path festlegen für die Approximierende
    public void drawSimpsonFunc(float x1, float y1, float x2, float y2, boolean flag) {
        // Die "min" "max" Funktion ist nötig, um nicht außerhalb des Bereichs den Path noch weiter zu zeichnen (Pathlänge beschränkt!)
        if (x1 >= points.getPoint(points.getFirstPointNumber()).getX() && x1 <= points.getPoint(points.getLastPointNumber()).getX()
                && x2 >= points.getPoint(points.getFirstPointNumber()).getX() && x2 <= points.getPoint(points.getLastPointNumber()).getX() && !flag) {
            simpsonPath.moveTo(x1, Math.min(Math.max(0, y1), 1200));
            simpsonPath.lineTo(x2, Math.min(Math.max(0, y2), 1200));
        }
        if (x1 >= points.getPoint(points.getFirstPointNumber()).getX() && x1 <= points.getPoint(points.getLastPointNumber()).getX()
                && x2 >= points.getPoint(points.getFirstPointNumber()).getX() && x2 <= points.getPoint(points.getLastPointNumber()).getX() && flag) {
            simpsonPath.moveTo(x1, Math.min(Math.max(0, y1), 1200));
            simpsonPath.lineTo(x2, Math.min(Math.max(0, y2), 1200));
        }

    }

    public void clearGaussQuadraturFunc() {
        gaussPath.reset();
        invalidate();
    }

    public void drawGaussQuadraturFunc(float x1, float y1, float x2, float y2, boolean flag) {
        // Die "min" "max" Funktion ist nötig, um nicht außerhalb des Bereichs den Path noch weiter zu zeichnen (Pathlänge beschränkt!)
        if (x1 >= points.getPoint(points.getFirstPointNumber()).getX() && x1 <= points.getPoint(points.getLastPointNumber()).getX()
                && x2 >= points.getPoint(points.getFirstPointNumber()).getX() && x2 <= points.getPoint(points.getLastPointNumber()).getX() && !flag) {
            gaussPath.moveTo(x1, Math.min(Math.max(0, y1), 1200));
            gaussPath.lineTo(x2, Math.min(Math.max(0, y2), 1200));
        }
        if (x1 >= points.getPoint(points.getFirstPointNumber()).getX() && x1 <= points.getPoint(points.getLastPointNumber()).getX()
                && x2 >= points.getPoint(points.getFirstPointNumber()).getX() && x2 <= points.getPoint(points.getLastPointNumber()).getX() && flag) {
            gaussPath.moveTo(x1, Math.min(Math.max(0, y1), 1200));
            gaussPath.lineTo(x2, Math.min(Math.max(0, y2), 1200));
        }
    }

    public Path drawSimpsonIntegration(int current, int next) {

        simpsonIntegratePath = new Path();
        simpsonIntegratePath.setFillType(Path.FillType.EVEN_ODD);

        simpsonIntegratePath.moveTo((float) points.getPoint(current).getX(), (float) coordSystem.getY0());
        simpsonIntegratePath.lineTo((float) points.getPoint(current).getX(), (float) points.getPoint(current).getY());

        for (int j = (int) points.getPoint(current).getX() + 1; j <= (int) points.getPoint(next).getX(); j = j + 4) {
            simpsonIntegratePath.lineTo(j, (float) coordSystem.getYTransFromReel(simpsonFunction.evaluate(coordSystem.getxFunc()[(int) (-coordSystem.getX0() + coordSystem.getxLen() + j)])));
        }
        simpsonIntegratePath.lineTo((float) points.getPoint(next).getX(), (float) points.getPoint(next).getY());
        simpsonIntegratePath.lineTo((float) points.getPoint(next).getX(), (float) coordSystem.getY0());
        simpsonIntegratePath.close();

        return simpsonIntegratePath;
    }

    public Path drawGaussIntegration(int current, int next) {

        tschebychowIntegratePath = new Path();
        tschebychowIntegratePath.setFillType(Path.FillType.EVEN_ODD);

        tschebychowIntegratePath.moveTo((float) points.getPoint(current).getX(), (float) coordSystem.getY0());
        tschebychowIntegratePath.lineTo((float) points.getPoint(current).getX(), (float) points.getPoint(current).getY());

        for (int j = (int) points.getPoint(current).getX() + 1; j <= (int) points.getPoint(next).getX(); j = j + 4) {
            tschebychowIntegratePath.lineTo(j, (float) coordSystem.getYTransFromReel(gaussQuadraturFunction.evaluate(coordSystem.getxFunc()[(int) (-coordSystem.getX0() + coordSystem.getxLen() + j)])));
        }
        tschebychowIntegratePath.lineTo((float) points.getPoint(next).getX(), (float) points.getPoint(next).getY());
        tschebychowIntegratePath.lineTo((float) points.getPoint(next).getX(), (float) coordSystem.getY0());
        tschebychowIntegratePath.close();

        return tschebychowIntegratePath;
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
                        if (points.getPoint(points.getNearestPoint(xy[0], xy[1])).calcDiff(xy[0], xy[1]) < 2000 && points.getPoint(points.getNearestPoint(xy[0], xy[1])).getIsPlaced()) {
                            points.setSwitchOn(points.getNearestPoint(xy[0], xy[1]));
                        }


                        for (int i = 0; i < points.getCount(); i++) {
                            if (points.getPoint(i).getSwitch().isChecked()) {
                                points.getPoint(i).setActive(true);
                                points.getPoint(i).setIsPlaced(true);

                                drawIntegratePoint(xy[0], i);

                                points.getPoint(i).getxView().setText("" + Math.round(points.getPoint(i).getReelX() * 1e3) / 1e3);
                                points.getPoint(i).getyView().setText("" + Math.round(points.getPoint(i).getReelY() * 1e3) / 1e3);
                            }
                        }


                        points.sortPoints();

                        if (getStartPointSwitch().isChecked() && points.getNOPP() >= 2) {
                            double startPointIntegralValue = 0;

                            current = points.getFirstPointNumber();
                            next = points.getNextPointNumber(points.getFirstPointNumber());

                            for (int i = 0; i < points.getNOPP() - 1; i++) {

                                startPointIntegralValue = startPointIntegralValue + coordSystem.getFunction().evaluate(points.getPoint(current).getReelX()) * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX());
                                current = next;
                                next = points.getNextPointNumber(next);
                            }

                            coordSystem.getStartPointErrorView().setText("" + Math.round((Math.abs(startPointIntegralValue -
                                    coordSystem.getFunction().antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                                    coordSystem.getFunction().antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);
                        }
                        if (getMiddelPointSwitch().isChecked() && points.getNOPP() >= 2) {
                            double middelPointIntegralVaulue = 0;

                            current = points.getFirstPointNumber();
                            next = points.getNextPointNumber(points.getFirstPointNumber());

                            for (int i = 0; i < points.getNOPP() - 1; i++) {

                                double middelXCoord = 1.0 / 2.0 * (points.getPoint(next).getReelX() + points.getPoint(current).getReelX());
                                ;
                                middelPointIntegralVaulue = middelPointIntegralVaulue + coordSystem.getFunction().evaluate(middelXCoord) * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX());
                                current = next;
                                next = points.getNextPointNumber(next);
                            }

                            coordSystem.getMiddelPointErrorView().setText("" + Math.round((Math.abs(middelPointIntegralVaulue -
                                    coordSystem.getFunction().antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                                    coordSystem.getFunction().antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);
                        }
                        if (getTrapezSwitch().isChecked() && points.getNOPP() >= 2) {
                            points.sortPoints();

                            double trapezIntegralVaulue = 0;

                            current = points.getFirstPointNumber();
                            next = points.getNextPointNumber(points.getFirstPointNumber());

                            for (int i = 0; i < points.getNOPP() - 1; i++) {
                                trapezIntegralVaulue = trapezIntegralVaulue + (points.getPoint(next).getReelX() - points.getPoint(current).getReelX()) * 0.5 * (coordSystem.getFunction().evaluate(points.getPoint(next).getReelX()) + coordSystem.getFunction().evaluate(points.getPoint(current).getReelX()));
                                current = next;
                                next = points.getNextPointNumber(next);
                            }

                            coordSystem.getTrapezErrorView().setText("" + Math.round((Math.abs(trapezIntegralVaulue -
                                    coordSystem.getFunction().antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                                    coordSystem.getFunction().antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);

                        }
                        if (simpsonSwitch.isChecked() && points.getNOPP() >= 2) {
                            simpsonFunction = new SimpsonFunction();
                            simpsonFunction.setPoints(points);
                            simpsonFunction.setCoordSystem(coordSystem);
                            simpsonFunction.simpsonInterpolate();
                            coordSystem.setySimpson(simpsonFunction);
                            clearSimpsonFunc();
                            coordSystem.drawSimpsonFunc();

                            double simpsonIntegralValue = 0;
                            current = points.getFirstPointNumber();
                            next = points.getNextPointNumber(points.getFirstPointNumber());
                            for (int i = 0; i < points.getNOPP() - 1; i++) {
                                simpsonIntegralValue = simpsonIntegralValue + simpsonFunction.integral(points.getPoint(current).getReelX(), points.getPoint(next).getReelX());
                                current = next;
                                next = points.getNextPointNumber(next);
                            }
                            coordSystem.getSimpsonErrorView().setText("" + Math.round((Math.abs(simpsonIntegralValue -
                                    coordSystem.getFunction().antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                                    coordSystem.getFunction().antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);
                        }
                        if (gaussSwitch.isChecked() && points.getNOPP() >= 2) {
                            gaussSeekbar.setEnabled(true);
                            gaussDegreeView.setText("" + gaussSeekbar.getProgress());
                            gaussQuadraturFunction = new GaussQuadraturFunction();
                            gaussQuadraturFunction.setPoints(points);
                            gaussQuadraturFunction.setCoordSystem(coordSystem);

                            coordSystem.getGaussErrorView().setText("" + Math.round((Math.abs(gaussQuadraturFunction.gaussQuadratur(gaussSeekbar.getProgress() + 1) -
                                    coordSystem.getFunction().antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                                    coordSystem.getFunction().antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);

                            // Tschebychow Polynom Integration
//                        gaussQuadraturFunction.gaussQuadraturInterpolate(gaussSeekbar.getProgress());
//                        coordSystem.setyGauss(gaussQuadraturFunction);
//                        clearGaussQuadraturFunc();
//                        coordSystem.drawGaussQuadraturFunc();

//                        double simpsonIntegralValue = 0;
//                        current = points.getFirstPointNumber();
//                        next = points.getNextPointNumber(points.getFirstPointNumber());
//                        for (int i = 0; i < points.getNOPP()-1; i++) {
//                            simpsonIntegralValue = simpsonIntegralValue + gaussQuadraturFunction.integralSimpson(points.getPoint(current).getReelX(), points.getPoint(next).getReelX());
//                            current = next;
//                            next = points.getNextPointNumber(next);
//                        }
//                        coordSystem.getSimpsonErrorView().setText("" + Math.round((Math.abs(simpsonIntegralValue-
//                                coordSystem.getFunction().antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
//                                coordSystem.getFunction().antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);
                        }

                    }
                    break;
                //Bewegter Touch
                case MotionEvent.ACTION_MOVE:
                    if (xy[0] < x0 + coordSystem.getxLen() + 1 && xy[0] > x0 - (coordSystem.getCoordArtFlag() - 1) * coordSystem.getxLen() - 1) {

                        // Schleife über alle Punktee
                        for (int j = 0; j < points.getCount(); j++) {
                            // Abfrage, ob es sich um den aktiven Punkt handelt.
                            if (points.getPoint(j).getActive()) {
                                points.sortPoints();

                                // Punkt neu zeichnen (Koordinaten ändern sich ja ständig)
                                drawIntegratePoint(xy[0], j);

                                // in x-,y-View werden die Koordinaten des Integrationspunktes angezeigt
                                points.getPoint(j).getxView().setText("" + Math.round(points.getPoint(j).getReelX() * 1e3) / 1e3);
                                points.getPoint(j).getyView().setText("" + Math.round(points.getPoint(j).getReelY() * 1e3) / 1e3);

                                if (getStartPointSwitch().isChecked() && points.getNOPP() >= 2) {
                                    double startPointIntegralValue = 0;

                                    current = points.getFirstPointNumber();
                                    next = points.getNextPointNumber(points.getFirstPointNumber());

                                    for (int i = 0; i < points.getNOPP() - 1; i++) {

                                        startPointIntegralValue = startPointIntegralValue + coordSystem.getFunction().evaluate(points.getPoint(current).getReelX()) * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX());
                                        current = next;
                                        next = points.getNextPointNumber(next);
                                    }

                                    coordSystem.getStartPointErrorView().setText("" + Math.round((Math.abs(startPointIntegralValue -
                                            coordSystem.getFunction().antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                                            coordSystem.getFunction().antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);
                                }
                                if (getMiddelPointSwitch().isChecked() && points.getNOPP() >= 2) {
                                    double middelPointIntegralVaulue = 0;

                                    current = points.getFirstPointNumber();
                                    next = points.getNextPointNumber(points.getFirstPointNumber());

                                    for (int i = 0; i < points.getNOPP() - 1; i++) {

                                        double middelXCoord = 1.0 / 2.0 * (points.getPoint(next).getReelX() + points.getPoint(current).getReelX());
                                        ;
                                        middelPointIntegralVaulue = middelPointIntegralVaulue + coordSystem.getFunction().evaluate(middelXCoord) * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX());
                                        current = next;
                                        next = points.getNextPointNumber(next);
                                    }

                                    coordSystem.getMiddelPointErrorView().setText("" + Math.round((Math.abs(middelPointIntegralVaulue -
                                            coordSystem.getFunction().antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                                            coordSystem.getFunction().antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);
                                }
                                if (getTrapezSwitch().isChecked() && points.getNOPP() >= 2) {
                                    points.sortPoints();

                                    double trapezIntegralVaulue = 0;

                                    current = points.getFirstPointNumber();
                                    next = points.getNextPointNumber(points.getFirstPointNumber());

                                    for (int i = 0; i < points.getNOPP() - 1; i++) {
                                        trapezIntegralVaulue = trapezIntegralVaulue + (points.getPoint(next).getReelX() - points.getPoint(current).getReelX()) * 0.5 * (coordSystem.getFunction().evaluate(points.getPoint(next).getReelX()) + coordSystem.getFunction().evaluate(points.getPoint(current).getReelX()));
                                        current = next;
                                        next = points.getNextPointNumber(next);
                                    }

                                    coordSystem.getTrapezErrorView().setText("" + Math.round((Math.abs(trapezIntegralVaulue -
                                            coordSystem.getFunction().antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                                            coordSystem.getFunction().antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);


                                }
                                if (simpsonSwitch.isChecked() && points.getNOPP() >= 2) {
                                    clearSimpsonFunc();
                                    simpsonFunction = new SimpsonFunction();
                                    simpsonFunction.setPoints(points);
                                    simpsonFunction.setCoordSystem(coordSystem);
                                    simpsonFunction.simpsonInterpolate();
                                    coordSystem.setySimpson(simpsonFunction);
                                    coordSystem.drawSimpsonFunc();

                                    double simpsonIntegralValue = 0;
                                    current = points.getFirstPointNumber();
                                    next = points.getNextPointNumber(points.getFirstPointNumber());
                                    for (int i = 0; i < points.getNOPP() - 1; i++) {
                                        simpsonIntegralValue = simpsonIntegralValue + simpsonFunction.integral(points.getPoint(current).getReelX(), points.getPoint(next).getReelX());
                                        current = next;
                                        next = points.getNextPointNumber(next);
                                    }
                                    coordSystem.getSimpsonErrorView().setText("" + Math.round((Math.abs(simpsonIntegralValue -
                                            coordSystem.getFunction().antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                                            coordSystem.getFunction().antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);
                                }

                                if (gaussSwitch.isChecked() && points.getNOPP() >= 2) {
                                    gaussQuadraturFunction = new GaussQuadraturFunction();
                                    gaussQuadraturFunction.setPoints(points);
                                    gaussQuadraturFunction.setCoordSystem(coordSystem);

                                    coordSystem.getGaussErrorView().setText("" + Math.round((Math.abs(gaussQuadraturFunction.gaussQuadratur(gaussSeekbar.getProgress() + 1) -
                                            coordSystem.getFunction().antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                                            coordSystem.getFunction().antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);


//                                gaussQuadraturFunction.gaussQuadraturInterpolate(gaussSeekbar.getProgress());
//                                coordSystem.setyGauss(gaussQuadraturFunction);
//                                clearGaussQuadraturFunc();
//                                coordSystem.drawGaussQuadraturFunc();

//                        double simpsonIntegralValue = 0;
//                        current = points.getFirstPointNumber();
//                        next = points.getNextPointNumber(points.getFirstPointNumber());
//                        for (int i = 0; i < points.getNOPP()-1; i++) {
//                            simpsonIntegralValue = simpsonIntegralValue + gaussQuadraturFunction.integralSimpson(points.getPoint(current).getReelX(), points.getPoint(next).getReelX());
//                            current = next;
//                            next = points.getNextPointNumber(next);
//                        }
//                        coordSystem.getSimpsonErrorView().setText("" + Math.round((Math.abs(simpsonIntegralValue-
//                                coordSystem.getFunction().antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
//                                coordSystem.getFunction().antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);
                                }
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    for (int i = 0; i < points.getCount(); i++) {
                        points.getPoint(i).setActive(false);
                        points.getPoint(i).getSwitch().setChecked(false);
                    }
                    freeMemory();
            }
//        }

        invalidate();

        return true;
    }

    public void setCoordSystem(CoordSystem coordSystem) {
        this.coordSystem = coordSystem;
    }

    public void setPoints(Points points) {
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


    public Switch getStartPointSwitch() {
        return startPointSwitch;
    }

    public void setStartPointSwitch(Switch startPointSwitch) {
        this.startPointSwitch = startPointSwitch;
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

    public Switch getMiddelPointSwitch() {
        return middelPointSwitch;
    }

    public void setMiddelPointSwitch(Switch middelPointSwitch) {
        this.middelPointSwitch = middelPointSwitch;
    }

    public Switch getTrapezSwitch() {
        return trapezSwitch;
    }

    public void setTrapezSwitch(Switch trapezSwitch) {
        this.trapezSwitch = trapezSwitch;
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

    public void setSimpsonSwitch(Switch simpsonSwitch) {
        this.simpsonSwitch = simpsonSwitch;
    }

    public SimpsonFunction getSimpsonFunction() {
        return simpsonFunction;
    }

    public void setSimpsonFunction(SimpsonFunction simpsonFunction) {
        this.simpsonFunction = simpsonFunction;
    }

    public Switch getGaussSwitch() {
        return gaussSwitch;
    }

    public void setGaussSwitch(Switch gaussSwitch) {
        this.gaussSwitch = gaussSwitch;
    }

    public SeekBar getGaussSeekbar() {
        return gaussSeekbar;
    }

    public void setGaussSeekbar(SeekBar gaussSeekbar) {
        this.gaussSeekbar = gaussSeekbar;
    }

    public TextView getGaussDegreeView() {
        return gaussDegreeView;
    }

    public void setGaussDegreeView(TextView gaussDegreeView) {
        this.gaussDegreeView = gaussDegreeView;
    }

    public GaussQuadraturFunction getGaussQuadraturFunction() {
        return gaussQuadraturFunction;
    }

    public void setGaussQuadraturFunction(GaussQuadraturFunction gaussQuadraturFunction) {
        this.gaussQuadraturFunction = gaussQuadraturFunction;
    }
    public void setIs_init() {this.is_init=true;}
}
