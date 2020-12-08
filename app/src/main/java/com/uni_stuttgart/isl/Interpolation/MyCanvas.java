package com.uni_stuttgart.isl.Interpolation;

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

import com.uni_stuttgart.isl.CoolStuff.NumLinAlg;
import com.uni_stuttgart.isl.Function.AbsFunction;
import com.uni_stuttgart.isl.Function.HermitFunction;
import com.uni_stuttgart.isl.Function.NewtonPolynom;
import com.uni_stuttgart.isl.Function.RungeFunction;
import com.uni_stuttgart.isl.Function.TrigonomialFunction;
import com.uni_stuttgart.isl.Points.Points;
import com.uni_stuttgart.isl.Quadrature.NewtonCotes;

/**
 * Created by nborg on 13.08.15.
 */

public class MyCanvas extends View {
    private float[] xy = new float[2];

    // Intervall auf dem der Fehler (L2, Max) berechnet werden soll
    private double start;
    private double end;

    // Paint/Path für das Koordinaten-System
    private Paint csPaint;
    private Path csPath;

    // Paint/Path für die Funktion die Interpoliert werden soll
    private Paint FuncPaint;
    private Path FuncPath;

    // Paint/Path für die Interpolierende
    private Paint interPaint;
    private Path interPath;

    // Paint/Path für die Approximierende
    private Paint approxPaint;
    private Path approxPath;

    // Paint/Path für die Hermitinterpolierende
    private Paint hermitPaint;
    private Path hermitPath;

    // Anlegen eines Objekts der Klasse Coordsystem
    private CoordSystem coordSystem;

    // Regler für die Ordnung der Approximation
    private SeekBar approxDegreeBar;

    // Switch zum EIN/AUS-Schalten der HermitInterpolation
    private Switch InterSwitch;

    // Switch zum EIN/AUS-Schalten der Approximation
    private Switch ApproxSwitch;

    // Switch zum EIN/AUS-Schalten der HermitInterpolation
    private Switch HermitSwitch;

    // Interpolationspunkte
    private Points points;

    // Anlegen eines Polynoms in Newton-Basis für die Interpolation
    private NewtonPolynom interpolant;
    // Anlegen eines Polynoms in Newton-Basis für die Approximation
    private NewtonPolynom approximant;
    // Anlegen eines stückweisen kubischen Hermitpolynoms
    private HermitFunction hermitFunction;

    // Textview für die Ordnung der Interpolation
    private TextView interView;
    // Textview für die Ordnung der Approximation
    private TextView approxView;

    private boolean is_init = false;


    public MyCanvas(Context c, AttributeSet attrs) {
        super(c, attrs);

        // Setzen der Parameter für das Koordinaten-System
        csPath = new Path();
        csPaint = new Paint();
        csPaint.setAntiAlias(true);
        csPaint.setColor(Color.BLACK);
        csPaint.setStyle(Paint.Style.STROKE);
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

        // Setzen der Parameter für die Interpolierende
        interPath = new Path();
        interPaint = new Paint();
        interPaint.setAntiAlias(true);
        interPaint.setColor(Color.BLUE);
        interPaint.setStyle(Paint.Style.STROKE);
        interPaint.setStrokeJoin(Paint.Join.ROUND);
        interPaint.setStrokeWidth(6F);

        // Setzen der Parameter für die Approximierende
        approxPath = new Path();
        approxPaint = new Paint();
        approxPaint.setAntiAlias(true);
        approxPaint.setColor(Color.RED);
        approxPaint.setStyle(Paint.Style.STROKE);
        approxPaint.setStrokeJoin(Paint.Join.ROUND);
        approxPaint.setStrokeWidth(3F);


        // Setzen der Parameter für die Hermit
        hermitPath = new Path();
        hermitPaint = new Paint();
        hermitPaint.setAntiAlias(true);
        hermitPaint.setColor(Color.rgb(0, 153, 143));
        hermitPaint.setStyle(Paint.Style.STROKE);
        hermitPaint.setStrokeJoin(Paint.Join.ROUND);
        hermitPaint.setStrokeWidth(3F);
    }

    // Zeichen Rutine
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        Resources res = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.ic_help);
//        canvas.drawBitmap(bitmap, 0, 0, approxPaint);

        if(is_init) {
            // Zeichnen des Koordinaten Systems
            canvas.drawPath(csPath, csPaint);

            // Zeichnen der Funktion
            canvas.drawPath(FuncPath, FuncPaint);

            // Zeichnen der Interpolierden
            canvas.drawPath(interPath, interPaint);

            // Abfrage, ob approximiert werden soll
            if (ApproxSwitch.isChecked()) {
                // Zeichnen der Approximierenden
                canvas.drawPath(approxPath, approxPaint);
            }

            // Abfrage, ob Hermit interpoliert werden soll
            if (HermitSwitch.isChecked()) {
                // Zeichnen der Hermit Interpolierenden
                canvas.drawPath(hermitPath, hermitPaint);
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

    // Path festlegen für das Koordinaten-System
    public void drawXtoY_cs(float x1, float y1, float x2, float y2) {
        csPath.moveTo(x1, y1);
        csPath.lineTo(x2, y2);
        invalidate();
    }

    // Path festlegen für die Funktion die interpoliert werden soll
    public void drawXtoY_func(float x1, float y1, float x2, float y2, boolean flag) {
        if (flag){
            FuncPath.moveTo(x1, y1);
            FuncPath.lineTo(x2, y2);
        }
        else{
            FuncPath.lineTo(x2, y2);
        }
    }

    // Path festlegen für die Interpolierende
    public void drawInterFunc(float x1, float y1, float x2, float y2, boolean flag) {
        // Die "min" "max" Funktion ist nötig, um nicht außerhalb des berechs den Path noch weiter zu zeichnen (Pathlänge beschränkt)
        if (flag){
            interPath.moveTo(x1, Math.min(Math.max(0, y1), 1200));
            interPath.lineTo(x2, Math.min(Math.max(0, y2), 1200));
        }
        else{
            interPath.lineTo(x2, Math.min(Math.max(0, y2), 1200));
        }
    }

    // Path festlegen für die Approximierende
    public void drawApproxFunc(float x1, float y1, float x2, float y2) {
        // Die "min" "max" Funktion ist nötig, um nicht außerhalb des berechs den Path noch weiter zu zeichnen (Pathlänge beschränkt)
        approxPath.moveTo(x1, Math.min(Math.max(0, y1), 1200));
        approxPath.lineTo(x2, Math.min(Math.max(0, y2), 1200));
    }

    // Path festlegen für die Approximierende
    public void drawHermitFunc(float x1, float y1, float x2, float y2) {
        // Die "min" "max" Funktion ist nötig, um nicht außerhalb des berechs den Path noch weiter zu zeichnen (Pathlänge beschränkt)
        hermitPath.moveTo(x1, Math.min(Math.max(0, y1), 1200));
        hermitPath.lineTo(x2, Math.min(Math.max(0, y2), 1200));
    }

    // Löschen der Interpolierenden
    public void clearInterFunc() {
        interPath.reset();
        invalidate();
    }

    // Löschen der Approximierenden
    public void clearApproxFunc() {
        approxPath.reset();
        invalidate();
    }

    // Löschen der Hermitinterpolierenden
    public void clearHermitFunc() {
        hermitPath.reset();
        invalidate();
    }

    // Erstellen der Rechtecke um die Interpolationspunkte
    public void drawABCD(float x, int pointnumber) {
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
    }

    // Rutine für Aktionen bei TouchEvents
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        xy[0] = event.getX();
        xy[1] = event.getY();
        boolean flag = false;
        float x0 = coordSystem.getX0();
        double[] xCoord;
        double[] yCoord;

            switch (event.getAction()) {
                // Einfacher Touch, keine Bewegung
                case MotionEvent.ACTION_DOWN:
                    // Aktivieren eine Punktes bei Touch in der Nähe
                    if (xy[0] < x0 + coordSystem.getxLen() + 1 && xy[0] > x0 - (coordSystem.getCoordArtFlag() - 1) * coordSystem.getxLen() - 1) {
                        for (int i = 0; i < points.getCount(); i++) {
                            if (points.getPoint(i).getSwitch().isChecked()) {
                                flag = true;
                                points.getPoint(i).setActive(true);
                                points.getPoint(i).setIsPlaced(true);
                                xCoord = new double[points.getNOPP()];
                                yCoord = new double[points.getNOPP()];
                                drawABCD(xy[0], i);
                                points.getPoint(i).getxView().setText("" + Math.round(points.getPoint(i).getReelX() * 1e3) / 1e3);
                                points.getPoint(i).getyView().setText("" + Math.round(points.getPoint(i).getReelY() * 1e3) / 1e3);

                                if (getInterSwitch().isChecked()) {
                                    // Berechnen der Interpolierenden
                                    // Anlegen eines Polynomes mit Grad entsprechend der gesetzten Punkte
                                    interpolant = new NewtonPolynom(points.getNOPP() - 1);
                                    interView.setText("" + (points.getNOPP() - 1));

                                    int k = 0;
                                    // Füllen der oben genannten Vektoren
                                    for (int l = 0; l < points.getCount(); l++) {
                                        if (points.getPoint(l).getIsPlaced()) {
                                            xCoord[k] = points.getPoint(l).getReelX();
                                            yCoord[k] = points.getPoint(l).getReelY();
                                            k++;
                                        }
                                    }
                                    // Interpolieren, bzw. Kooeffizienten berechnen
                                    interpolant.interpolate(xCoord, yCoord);

                                    // Alte Interpolierende löschen
                                    clearInterFunc();

                                    // Setzen der y-Koordinate der Interpolationspunkte
                                    coordSystem.setyInter(interpolant);

                                    // Zeichnen der neuen Interpolierenden
                                    coordSystem.drawInter();

                                    //L2, Max Fehler berechnen
                                    NewtonCotes newtonCotes = new NewtonCotes(3);

                                    double temp = 0;
                                    if (coordSystem.getFunction() instanceof RungeFunction) {
                                        temp = Math.round(newtonCotes.integrate_LP(this.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                        if (temp > 1) {
                                            coordSystem.getL2InterView().setTextColor(Color.RED);
                                        } else if (temp <= 1 && temp > 0.5) {
                                            coordSystem.getL2InterView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getL2InterView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getL2InterView().setText("" + temp);

                                        temp = Math.round(newtonCotes.integrate_infty(this.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                        if (temp > 1) {
                                            coordSystem.getMAXInterView().setTextColor(Color.RED);
                                        } else if (temp <= 1 && temp > 0.5) {
                                            coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getMAXInterView().setText("" + temp);
                                    } else if (coordSystem.getFunction() instanceof AbsFunction) {
                                        temp = Math.round(newtonCotes.integrate_LP(this.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                        if (temp > 0.3) {
                                            coordSystem.getL2InterView().setTextColor(Color.RED);
                                        } else if (temp <= 0.3 && temp > 0.07) {
                                            coordSystem.getL2InterView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getL2InterView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getL2InterView().setText("" + temp);

                                        temp = Math.round(newtonCotes.integrate_infty(this.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                        if (temp > 0.5) {
                                            coordSystem.getMAXInterView().setTextColor(Color.RED);
                                        } else if (temp <= 0.5 && temp > 0.1) {
                                            coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getMAXInterView().setText("" + temp);
                                    } else if (coordSystem.getFunction() instanceof TrigonomialFunction) {
                                        temp = Math.round(newtonCotes.integrate_LP(this.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                        if (temp > 1) {
                                            coordSystem.getL2InterView().setTextColor(Color.RED);
                                        } else if (temp <= 1 && temp > 0.09) {
                                            coordSystem.getL2InterView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getL2InterView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getL2InterView().setText("" + temp);

                                        temp = Math.round(newtonCotes.integrate_infty(this.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                        if (temp > 1) {
                                            coordSystem.getMAXInterView().setTextColor(Color.RED);
                                        } else if (temp <= 1 && temp > 0.09) {
                                            coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getMAXInterView().setText("" + temp);
                                    }
                                }
                                // Soll approximiert werden?
                                if (getApproxSwitch().isChecked()) {
                                    // Berechnen der Approximierenden
                                    // Anlegen eines Polynomes mit Grad entsprechend der gesetzten Punkte
                                    if (points.getNOPP() > 0) {
                                        approxDegreeBar.setEnabled(true);
                                        approxView.setText("" + approxDegreeBar.getProgress());
                                    }
                                    approximant = new NewtonPolynom(approxDegreeBar.getProgress());

                                    int k = 0;
                                    // Füllen der oben genannten Vektoren
                                    for (int l = 0; l < points.getCount(); l++) {
                                        if (points.getPoint(l).getIsPlaced()) {
                                            xCoord[k] = points.getPoint(l).getReelX();
                                            yCoord[k] = points.getPoint(l).getReelY();
                                            k++;
                                        }
                                    }

                                    // Approximieren, bzw. Kooeffizienten berechnen
                                    approximant.lsApproximation(xCoord, yCoord);

                                    // Alte Approximierende löschen
                                    clearApproxFunc();

                                    // Setzen der y-Koordinate der Approximationspunkte
                                    coordSystem.setyApprox(approximant);

                                    // Zeichnen der neuen Approximierende
                                    coordSystem.drawApprox();

                                    // L2,Max Fehler berechnen
                                    NewtonCotes newtonCotes = new NewtonCotes(3);
                                    double temp = 0;
                                    if (coordSystem.getFunction() instanceof RungeFunction) {
                                        temp = Math.round(newtonCotes.integrate_LP(this.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                        if (temp > 1) {
                                            coordSystem.getL2ApproxView().setTextColor(Color.RED);
                                        } else if (temp <= 1 && temp > 0.5) {
                                            coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getL2ApproxView().setText("" + temp);

                                        temp = Math.round(newtonCotes.integrate_infty(this.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                        if (temp > 1) {
                                            coordSystem.getMAXApproxView().setTextColor(Color.RED);
                                        } else if (temp <= 1 && temp > 0.5) {
                                            coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getMAXApproxView().setText("" + temp);
                                    } else if (coordSystem.getFunction() instanceof AbsFunction) {
                                        temp = Math.round(newtonCotes.integrate_LP(this.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                        if (temp > 0.3) {
                                            coordSystem.getL2ApproxView().setTextColor(Color.RED);
                                        } else if (temp <= 0.3 && temp > 0.07) {
                                            coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getL2ApproxView().setText("" + temp);

                                        temp = Math.round(newtonCotes.integrate_infty(this.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                        if (temp > 0.5) {
                                            coordSystem.getMAXApproxView().setTextColor(Color.RED);
                                        } else if (temp <= 0.5 && temp > 0.1) {
                                            coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getMAXApproxView().setText("" + temp);
                                    } else if (coordSystem.getFunction() instanceof TrigonomialFunction) {
                                        temp = Math.round(newtonCotes.integrate_LP(this.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                        if (temp > 1) {
                                            coordSystem.getL2ApproxView().setTextColor(Color.RED);
                                        } else if (temp <= 1 && temp > 0.09) {
                                            coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getL2ApproxView().setText("" + temp);

                                        temp = Math.round(newtonCotes.integrate_infty(this.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                        if (temp > 1) {
                                            coordSystem.getMAXApproxView().setTextColor(Color.RED);
                                        } else if (temp <= 1 && temp > 0.09) {
                                            coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getMAXApproxView().setText("" + temp);
                                    }
                                }

                                // Soll Hermit-Interpoliert werden?
                                if (getHermitSwitch().isChecked()) {
                                    // Sortieren der Punkte
                                    points.sortPoints();

                                    // Berechnen der Approximierenden
                                    // Anlegen eines Hermit Interpolationspolynom
                                    hermitFunction = new HermitFunction();
                                    hermitFunction.setPoints(points);
                                    hermitFunction.setCoordSystem(coordSystem);

                                    if (points.getNOPP() >= 2) {
                                        // Hermit Interpolierenden berechnen
                                        hermitFunction.hermitinterpolate();

                                        // Alte HermitIntepolierende löschen
                                        clearHermitFunc();

                                        // Setzen der y-Koordinate der HermitIntepolierenden
                                        coordSystem.setyHermit(hermitFunction);

                                        // Zeichnen der neuen HermitIntepolierenden
                                        coordSystem.drawHermit();

                                        //L2, Max Fehler berechnen
                                        int current = points.getFirstPointNumber();
                                        int next = points.getNextPointNumber(points.getFirstPointNumber());
                                        double MaxErrorHermit = 1.0;
                                        double[] MaxErrorsHermit = new double[points.getNOPP()];
                                        double L2ErrorHermit = 0.0;
                                        NewtonCotes newtonCotes = new NewtonCotes(3);
                                        //L2
                                        for (i = 0; i < points.getNOPP() - 1; i++) {
                                            if (i == 0) {
                                                if (points.getNOPP() == 2) {
                                                    L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                                } else {
                                                    L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, points.getPoint(next).getReelX(), 2, 100) * 1e6) / 1e6;
                                                    current = next;
                                                    next = points.getNextPointNumber(next);
                                                }
                                            }
                                            if (i > 0 && i < points.getNOPP() - 2) {
                                                L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), points.getPoint(next).getReelX(), 2, 100) * 1e6) / 1e6;
                                                current = next;
                                                next = points.getNextPointNumber(next);
                                            }
                                            if (i == points.getNOPP() - 2 && i > 0) {
                                                L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), end, 2, 100) * 1e6) / 1e6;
                                            }
                                        }

                                        // MAX
                                        current = points.getFirstPointNumber();
                                        next = points.getNextPointNumber(points.getFirstPointNumber());

                                        for (i = 0; i < points.getNOPP() - 1; i++) {
                                            if (i == 0) {
                                                if (points.getNOPP() == 2) {
                                                    MaxErrorHermit = Math.round(newtonCotes.integrate_infty(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                                } else {
                                                    MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, points.getPoint(next).getReelX(), 100) * 1e6) / 1e6;
                                                    current = next;
                                                    next = points.getNextPointNumber(next);
                                                }
                                            }
                                            if (i > 0 && i < points.getNOPP() - 2) {
                                                MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), points.getPoint(next).getReelX(), 100) * 1e6) / 1e6;
                                                current = next;
                                                next = points.getNextPointNumber(next);
                                            }
                                            if (i == points.getNOPP() - 2 && i > 0) {
                                                MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), end, 100) * 1e6) / 1e6;
                                            }
                                        }
                                        if (points.getNOPP() != 2) {
                                            MaxErrorHermit = NumLinAlg.getMax(MaxErrorsHermit);
                                        }
                                        coordSystem.getMAXHermitView().setText("" + Math.round(MaxErrorHermit * 1e6) / 1e6);
                                        coordSystem.getL2HermitView().setText("" + Math.round(L2ErrorHermit * 1e6) / 1e6);
                                    }
                                }
                            }
                        }
                        if (!flag) {
                            for (int t = 0; t < points.getCount(); t++) {
                                if (points.getPoint(points.getNearestPoint(xy[0], xy[1])).calcDiff(xy[0], xy[1]) < 2000 && points.getPoint(points.getNearestPoint(xy[0], xy[1])).getIsPlaced()) {
                                    points.setSwitchOn(points.getNearestPoint(xy[0], xy[1]));
                                } else {
                                    // Schleife über alle Punkte
                                    for (int j = 0; j < points.getCount(); j++) {
                                        // Nur der aktive Punkt soll behandelt werden
                                        if (points.getPoint(j).getActive()) {
                                            // Der aktive Punkt wird nach dem Touch als "placed" markiert
                                            points.getPoint(j).setIsPlaced(true);

                                            // Button zum Löschen des Punktes wird sichtbar
                                            points.getPoint(j).getDeleteButton().setVisibility(VISIBLE);

                                            // Anlegen der x-, und y-Vektoren mit den Koordinaten zum Interpolieren
                                            xCoord = new double[points.getNOPP()];
                                            yCoord = new double[points.getNOPP()];


                                            // Zeichnen des Punktes
                                            drawABCD(xy[0], j);

                                            // in x-,y-View werden die Koordinaten des Interpoationspunktes angezeigt
                                            points.getPoint(j).getxView().setText("" + Math.round(points.getPoint(j).getReelX() * 1e3) / 1e3);
                                            points.getPoint(j).getyView().setText("" + Math.round(points.getPoint(j).getReelY() * 1e3) / 1e3);

                                            if (getInterSwitch().isChecked()) {
                                                // Berechnen der Interpolierenden
                                                // Anlegen eines Polynomes mit Grad entsprechend der gesetzten Punkte
                                                interpolant = new NewtonPolynom(points.getNOPP() - 1);

                                                int k = 0;
                                                // Füllen der oben genannten Vektoren
                                                for (int l = 0; l < points.getCount(); l++) {
                                                    if (points.getPoint(l).getIsPlaced()) {
                                                        xCoord[k] = points.getPoint(l).getReelX();
                                                        yCoord[k] = points.getPoint(l).getReelY();
                                                        k++;
                                                    }
                                                }
                                                // Interpolieren, bzw. Kooeffizienten berechnen
                                                interpolant.interpolate(xCoord, yCoord);

                                                // Alte Interpolierende löschen
                                                clearInterFunc();

                                                // Setzen der y-Koordinate der Interpolationspunkte
                                                coordSystem.setyInter(interpolant);

                                                // Zeichnen der neuen Interpolierenden
                                                coordSystem.drawInter();

                                                //L2, Max Fehler berechnen
                                                NewtonCotes newtonCotes = new NewtonCotes(3);

                                                double temp = 0;
                                                if (coordSystem.getFunction() instanceof RungeFunction) {
                                                    temp = Math.round(newtonCotes.integrate_LP(this.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                                    if (temp > 1) {
                                                        coordSystem.getL2InterView().setTextColor(Color.RED);
                                                    } else if (temp <= 1 && temp > 0.5) {
                                                        coordSystem.getL2InterView().setTextColor(Color.BLACK);
                                                    } else {
                                                        coordSystem.getL2InterView().setTextColor(Color.GREEN);
                                                    }
                                                    coordSystem.getL2InterView().setText("" + temp);

                                                    temp = Math.round(newtonCotes.integrate_infty(this.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                                    if (temp > 1) {
                                                        coordSystem.getMAXInterView().setTextColor(Color.RED);
                                                    } else if (temp <= 1 && temp > 0.5) {
                                                        coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                                                    } else {
                                                        coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                                                    }
                                                    coordSystem.getMAXInterView().setText("" + temp);
                                                } else if (coordSystem.getFunction() instanceof AbsFunction) {
                                                    temp = Math.round(newtonCotes.integrate_LP(this.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                                    if (temp > 0.3) {
                                                        coordSystem.getL2InterView().setTextColor(Color.RED);
                                                    } else if (temp <= 0.3 && temp > 0.07) {
                                                        coordSystem.getL2InterView().setTextColor(Color.BLACK);
                                                    } else {
                                                        coordSystem.getL2InterView().setTextColor(Color.GREEN);
                                                    }
                                                    coordSystem.getL2InterView().setText("" + temp);

                                                    temp = Math.round(newtonCotes.integrate_infty(this.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                                    if (temp > 0.5) {
                                                        coordSystem.getMAXInterView().setTextColor(Color.RED);
                                                    } else if (temp <= 0.5 && temp > 0.1) {
                                                        coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                                                    } else {
                                                        coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                                                    }
                                                    coordSystem.getMAXInterView().setText("" + temp);
                                                } else if (coordSystem.getFunction() instanceof TrigonomialFunction) {
                                                    temp = Math.round(newtonCotes.integrate_LP(this.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                                    if (temp > 1) {
                                                        coordSystem.getL2InterView().setTextColor(Color.RED);
                                                    } else if (temp <= 1 && temp > 0.09) {
                                                        coordSystem.getL2InterView().setTextColor(Color.BLACK);
                                                    } else {
                                                        coordSystem.getL2InterView().setTextColor(Color.GREEN);
                                                    }
                                                    coordSystem.getL2InterView().setText("" + temp);

                                                    temp = Math.round(newtonCotes.integrate_infty(this.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                                    if (temp > 1) {
                                                        coordSystem.getMAXInterView().setTextColor(Color.RED);
                                                    } else if (temp <= 1 && temp > 0.09) {
                                                        coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                                                    } else {
                                                        coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                                                    }
                                                    coordSystem.getMAXInterView().setText("" + temp);
                                                }
                                            }
                                            // Soll approximiert werden?
                                            if (getApproxSwitch().isChecked()) {
                                                // Berechnen der Approximierenden
                                                // Anlegen eines Polynomes mit Grad entsprechend der gesetzten Punkte
                                                approximant = new NewtonPolynom(approxDegreeBar.getProgress());

                                                int k = 0;
                                                // Füllen der oben genannten Vektoren
                                                for (int l = 0; l < points.getCount(); l++) {
                                                    if (points.getPoint(l).getIsPlaced()) {
                                                        xCoord[k] = points.getPoint(l).getReelX();
                                                        yCoord[k] = points.getPoint(l).getReelY();
                                                        k++;
                                                    }
                                                }

                                                // Approximieren, bzw. Kooeffizienten berechnen
                                                approximant.lsApproximation(xCoord, yCoord);

                                                // Alte Approximierende löschen
                                                clearApproxFunc();

                                                // Setzen der y-Koordinate der Approximationspunkte
                                                coordSystem.setyApprox(approximant);

                                                // Zeichnen der neuen Approximierende
                                                coordSystem.drawApprox();

                                                // L2,Max Fehler berechnen
                                                NewtonCotes newtonCotes = new NewtonCotes(3);
                                                double temp = 0;
                                                if (coordSystem.getFunction() instanceof RungeFunction) {
                                                    temp = Math.round(newtonCotes.integrate_LP(this.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                                    if (temp > 1) {
                                                        coordSystem.getL2ApproxView().setTextColor(Color.RED);
                                                    } else if (temp <= 1 && temp > 0.5) {
                                                        coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                                                    } else {
                                                        coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                                                    }
                                                    coordSystem.getL2ApproxView().setText("" + temp);

                                                    temp = Math.round(newtonCotes.integrate_infty(this.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                                    if (temp > 1) {
                                                        coordSystem.getMAXApproxView().setTextColor(Color.RED);
                                                    } else if (temp <= 1 && temp > 0.5) {
                                                        coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                                                    } else {
                                                        coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                                                    }
                                                    coordSystem.getMAXApproxView().setText("" + temp);
                                                } else if (coordSystem.getFunction() instanceof AbsFunction) {
                                                    temp = Math.round(newtonCotes.integrate_LP(this.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                                    if (temp > 0.3) {
                                                        coordSystem.getL2ApproxView().setTextColor(Color.RED);
                                                    } else if (temp <= 0.3 && temp > 0.07) {
                                                        coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                                                    } else {
                                                        coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                                                    }
                                                    coordSystem.getL2ApproxView().setText("" + temp);

                                                    temp = Math.round(newtonCotes.integrate_infty(this.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                                    if (temp > 0.5) {
                                                        coordSystem.getMAXApproxView().setTextColor(Color.RED);
                                                    } else if (temp <= 0.5 && temp > 0.1) {
                                                        coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                                                    } else {
                                                        coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                                                    }
                                                    coordSystem.getMAXApproxView().setText("" + temp);
                                                } else if (coordSystem.getFunction() instanceof TrigonomialFunction) {
                                                    temp = Math.round(newtonCotes.integrate_LP(this.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                                    if (temp > 1) {
                                                        coordSystem.getL2ApproxView().setTextColor(Color.RED);
                                                    } else if (temp <= 1 && temp > 0.09) {
                                                        coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                                                    } else {
                                                        coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                                                    }
                                                    coordSystem.getL2ApproxView().setText("" + temp);

                                                    temp = Math.round(newtonCotes.integrate_infty(this.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                                    if (temp > 1) {
                                                        coordSystem.getMAXApproxView().setTextColor(Color.RED);
                                                    } else if (temp <= 1 && temp > 0.09) {
                                                        coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                                                    } else {
                                                        coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                                                    }
                                                    coordSystem.getMAXApproxView().setText("" + temp);
                                                }
                                            }

                                            // Soll Hermit-Interpoliert werden?
                                            if (getHermitSwitch().isChecked()) {
                                                // Sortieren der Punkte
                                                points.sortPoints();

                                                // Berechnen der Approximierenden
                                                // Anlegen eines Hermit Interpolationspolynom
                                                hermitFunction = new HermitFunction();
                                                hermitFunction.setPoints(points);
                                                hermitFunction.setCoordSystem(coordSystem);

                                                if (points.getNOPP() >= 2) {
                                                    // Hermit Interpolierenden berechnen
                                                    hermitFunction.hermitinterpolate();

                                                    // Alte HermitIntepolierende löschen
                                                    clearHermitFunc();

                                                    // Setzen der y-Koordinate der HermitIntepolierenden
                                                    coordSystem.setyHermit(hermitFunction);

                                                    // Zeichnen der neuen HermitIntepolierenden
                                                    coordSystem.drawHermit();

                                                    //L2, Max Fehler berechnen
                                                    int current = points.getFirstPointNumber();
                                                    int next = points.getNextPointNumber(points.getFirstPointNumber());
                                                    double MaxErrorHermit = 1.0;
                                                    double[] MaxErrorsHermit = new double[points.getNOPP()];
                                                    double L2ErrorHermit = 0.0;
                                                    NewtonCotes newtonCotes = new NewtonCotes(3);
                                                    //L2
                                                    for (int i = 0; i < points.getNOPP() - 1; i++) {
                                                        if (i == 0) {
                                                            if (points.getNOPP() == 2) {
                                                                L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                                            } else {
                                                                L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, points.getPoint(next).getReelX(), 2, 100) * 1e6) / 1e6;
                                                                current = next;
                                                                next = points.getNextPointNumber(next);
                                                            }
                                                        }
                                                        if (i > 0 && i < points.getNOPP() - 2) {
                                                            L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), points.getPoint(next).getReelX(), 2, 100) * 1e6) / 1e6;
                                                            current = next;
                                                            next = points.getNextPointNumber(next);
                                                        }
                                                        if (i == points.getNOPP() - 2 && i > 0) {
                                                            L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), end, 2, 100) * 1e6) / 1e6;
                                                        }
                                                    }

                                                    // MAX
                                                    current = points.getFirstPointNumber();
                                                    next = points.getNextPointNumber(points.getFirstPointNumber());

                                                    for (int i = 0; i < points.getNOPP() - 1; i++) {
                                                        if (i == 0) {
                                                            if (points.getNOPP() == 2) {
                                                                MaxErrorHermit = Math.round(newtonCotes.integrate_infty(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                                            } else {
                                                                MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, points.getPoint(next).getReelX(), 100) * 1e6) / 1e6;
                                                                current = next;
                                                                next = points.getNextPointNumber(next);
                                                            }
                                                        }
                                                        if (i > 0 && i < points.getNOPP() - 2) {
                                                            MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), points.getPoint(next).getReelX(), 100) * 1e6) / 1e6;
                                                            current = next;
                                                            next = points.getNextPointNumber(next);
                                                        }
                                                        if (i == points.getNOPP() - 2 && i > 0) {
                                                            MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), end, 100) * 1e6) / 1e6;
                                                        }
                                                    }
                                                    if (points.getNOPP() != 2) {
                                                        MaxErrorHermit = NumLinAlg.getMax(MaxErrorsHermit);
                                                    }
                                                    coordSystem.getMAXHermitView().setText("" + Math.round(MaxErrorHermit * 1e6) / 1e6);
                                                    coordSystem.getL2HermitView().setText("" + Math.round(L2ErrorHermit * 1e6) / 1e6);
                                                }
                                            }

                                        }
                                    }
                                } // Klammer für Touch-Aktivierung
                            } // Klammer für Touch-Aktivierung
                        }
                        // Anpassen des Schiebereglers für die Ordnung der Approximation an die Anzahl der "placed" (Number of placed points, NOPP) Points.
                        approxDegreeBar.setMax(points.getNOPP() - 1);
                        invalidate();
                    }
                    break;
                //Bewegter Touch
                case MotionEvent.ACTION_MOVE:
                    if (xy[0] < x0 + coordSystem.getxLen() + 1 && xy[0] > x0 - (coordSystem.getCoordArtFlag() - 1) * coordSystem.getxLen() - 1) {
                        // Schleife über alle Punktee
                        for (int j = 0; j < points.getCount(); j++) {
                            // Abfrage, ob es sich um den aktiven Punkt handelt.
                            if (points.getPoint(j).getActive()) {
                                // Punkt neu zeichnen (Koordinaten ändern sich ja ständig)
                                drawABCD(xy[0], j);

                                // in x-,y-View werden die Koordinaten des Interpoationspunktes angezeigt
                                points.getPoint(j).getxView().setText("" + Math.round(points.getPoint(j).getReelX() * 1e3) / 1e3);
                                points.getPoint(j).getyView().setText("" + Math.round(points.getPoint(j).getReelY() * 1e3) / 1e3);

                                // Anlegen der x-, und y-Vektoren mit den Koordinaten zum Interpolieren
                                xCoord = new double[points.getNOPP()];
                                yCoord = new double[points.getNOPP()];

                                if (getInterSwitch().isChecked()) {
                                    // Berechnen der Interpolierenden
                                    // Anlegen eines Polynomes mit Grad entsprechend der gesetzten Punkte
                                    interpolant = new NewtonPolynom(points.getNOPP() - 1);

                                    // Füllen der oben genannten Vektoren
                                    int k = 0;
                                    for (int i = 0; i < points.getCount(); i++) {
                                        if (points.getPoint(i).getIsPlaced()) {
                                            xCoord[k] = points.getPoint(i).getReelX();
                                            yCoord[k] = points.getPoint(i).getReelY();
                                            k++;
                                        }
                                    }

                                    // Interpolieren, bzw. Kooeffizienten berechnen
                                    interpolant.interpolate(xCoord, yCoord);

                                    // Alte Interpolierende löschen
                                    clearInterFunc();

                                    // Setzen der y-Koordinate der Interpolationspunkte
                                    coordSystem.setyInter(interpolant);

                                    // Zeichnen der neuen Interpolierenden
                                    coordSystem.drawInter();

                                    //L2, Max Fehler berechnen
                                    NewtonCotes newtonCotes = new NewtonCotes(3);
                                    double temp = 0;
                                    if (coordSystem.getFunction() instanceof RungeFunction) {
                                        temp = Math.round(newtonCotes.integrate_LP(this.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                        if (temp > 1) {
                                            coordSystem.getL2InterView().setTextColor(Color.RED);
                                        } else if (temp <= 1 && temp > 0.5) {
                                            coordSystem.getL2InterView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getL2InterView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getL2InterView().setText("" + temp);

                                        temp = Math.round(newtonCotes.integrate_infty(this.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                        if (temp > 1) {
                                            coordSystem.getMAXInterView().setTextColor(Color.RED);
                                        } else if (temp <= 1 && temp > 0.5) {
                                            coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getMAXInterView().setText("" + temp);
                                    } else if (coordSystem.getFunction() instanceof AbsFunction) {
                                        temp = Math.round(newtonCotes.integrate_LP(this.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                        if (temp > 0.3) {
                                            coordSystem.getL2InterView().setTextColor(Color.RED);
                                        } else if (temp <= 0.3 && temp > 0.07) {
                                            coordSystem.getL2InterView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getL2InterView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getL2InterView().setText("" + temp);

                                        temp = Math.round(newtonCotes.integrate_infty(this.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                        if (temp > 0.5) {
                                            coordSystem.getMAXInterView().setTextColor(Color.RED);
                                        } else if (temp <= 0.5 && temp > 0.1) {
                                            coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getMAXInterView().setText("" + temp);
                                    } else if (coordSystem.getFunction() instanceof TrigonomialFunction) {
                                        temp = Math.round(newtonCotes.integrate_LP(this.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                        if (temp > 1) {
                                            coordSystem.getL2InterView().setTextColor(Color.RED);
                                        } else if (temp <= 1 && temp > 0.09) {
                                            coordSystem.getL2InterView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getL2InterView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getL2InterView().setText("" + temp);

                                        temp = Math.round(newtonCotes.integrate_infty(this.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                        if (temp > 1) {
                                            coordSystem.getMAXInterView().setTextColor(Color.RED);
                                        } else if (temp <= 1 && temp > 0.09) {
                                            coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getMAXInterView().setText("" + temp);
                                    }
                                }
                                // Soll approximiert werden?
                                if (getApproxSwitch().isChecked()) {
                                    // Berechnen der Approximierenden
                                    // Anlegen eines Polynomes mit Grad entsprechend der gesetzten Punkte
                                    approximant = new NewtonPolynom(approxDegreeBar.getProgress());

                                    int k = 0;
                                    // Füllen der oben genannten Vektoren
                                    for (int l = 0; l < points.getCount(); l++) {
                                        if (points.getPoint(l).getIsPlaced()) {
                                            xCoord[k] = points.getPoint(l).getReelX();
                                            yCoord[k] = points.getPoint(l).getReelY();
                                            k++;
                                        }
                                    }

                                    // Interpolieren, bzw. Kooeffizienten berechnen
                                    approximant.lsApproximation(xCoord, yCoord);

                                    // Alte Approximierende löschen
                                    clearApproxFunc();

                                    // Setzen der y-Koordinate der Approximationspunkte
                                    coordSystem.setyApprox(approximant);

                                    // Zeichnen der neuen Approximierende
                                    coordSystem.drawApprox();

                                    // L2,Max Fehler berechnen
                                    NewtonCotes newtonCotes = new NewtonCotes(3);
                                    double temp = 0;
                                    if (coordSystem.getFunction() instanceof RungeFunction) {
                                        temp = Math.round(newtonCotes.integrate_LP(this.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                        if (temp > 1) {
                                            coordSystem.getL2ApproxView().setTextColor(Color.RED);
                                        } else if (temp <= 1 && temp > 0.5) {
                                            coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getL2ApproxView().setText("" + temp);

                                        temp = Math.round(newtonCotes.integrate_infty(this.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                        if (temp > 1) {
                                            coordSystem.getMAXApproxView().setTextColor(Color.RED);
                                        } else if (temp <= 1 && temp > 0.5) {
                                            coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getMAXApproxView().setText("" + temp);
                                    } else if (coordSystem.getFunction() instanceof AbsFunction) {
                                        temp = Math.round(newtonCotes.integrate_LP(this.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                        if (temp > 0.3) {
                                            coordSystem.getL2ApproxView().setTextColor(Color.RED);
                                        } else if (temp <= 0.3 && temp > 0.07) {
                                            coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getL2ApproxView().setText("" + temp);

                                        temp = Math.round(newtonCotes.integrate_infty(this.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                        if (temp > 0.5) {
                                            coordSystem.getMAXApproxView().setTextColor(Color.RED);
                                        } else if (temp <= 0.5 && temp > 0.1) {
                                            coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getMAXApproxView().setText("" + temp);
                                    } else if (coordSystem.getFunction() instanceof TrigonomialFunction) {
                                        temp = Math.round(newtonCotes.integrate_LP(this.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                        if (temp > 1) {
                                            coordSystem.getL2ApproxView().setTextColor(Color.RED);
                                        } else if (temp <= 1 && temp > 0.09) {
                                            coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getL2ApproxView().setText("" + temp);

                                        temp = Math.round(newtonCotes.integrate_infty(this.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                        if (temp > 1) {
                                            coordSystem.getMAXApproxView().setTextColor(Color.RED);
                                        } else if (temp <= 1 && temp > 0.09) {
                                            coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                                        } else {
                                            coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                                        }
                                        coordSystem.getMAXApproxView().setText("" + temp);
                                    }
                                }

                                // Soll Hermit-Interpoliert werden?
                                if (getHermitSwitch().isChecked()) {
                                    // Sortieren der Punkte
                                    points.sortPoints();

                                    if (points.getNOPP() >= 2) {

                                        // Berechnen der Approximierenden
                                        // Anlegen eines Hermit Interpolationspolynom
                                        hermitFunction = new HermitFunction();
                                        hermitFunction.setPoints(points);
                                        hermitFunction.setCoordSystem(coordSystem);

                                        // Hermit Interpolierenden berechnen
                                        hermitFunction.hermitinterpolate();

                                        // Alte HermitIntepolierende löschen
                                        clearHermitFunc();

                                        // Setzen der y-Koordinate der HermitIntepolierenden
                                        coordSystem.setyHermit(hermitFunction);

                                        // Zeichnen der neuen HermitIntepolierenden
                                        coordSystem.drawHermit();

                                        //L2, Max Fehler berechnen
                                        int current = points.getFirstPointNumber();
                                        int next = points.getNextPointNumber(points.getFirstPointNumber());
                                        double MaxErrorHermit = 1.0;
                                        double[] MaxErrorsHermit = new double[points.getNOPP()];
                                        double L2ErrorHermit = 0.0;
                                        NewtonCotes newtonCotes = new NewtonCotes(3);
                                        //L2
                                        for (int i = 0; i < points.getNOPP() - 1; i++) {
                                            if (i == 0) {
                                                if (points.getNOPP() == 2) {
                                                    L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                                } else {
                                                    L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, points.getPoint(next).getReelX(), 2, 100) * 1e6) / 1e6;
                                                    current = next;
                                                    next = points.getNextPointNumber(next);
                                                }
                                            }
                                            if (i > 0 && i < points.getNOPP() - 2) {
                                                L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), points.getPoint(next).getReelX(), 2, 100) * 1e6) / 1e6;
                                                current = next;
                                                next = points.getNextPointNumber(next);
                                            }
                                            if (i == points.getNOPP() - 2 && i > 0) {
                                                L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), end, 2, 100) * 1e6) / 1e6;
                                            }
                                        }

                                        // MAX
                                        current = points.getFirstPointNumber();
                                        next = points.getNextPointNumber(points.getFirstPointNumber());

                                        for (int i = 0; i < points.getNOPP() - 1; i++) {
                                            if (i == 0) {
                                                if (points.getNOPP() == 2) {
                                                    MaxErrorHermit = Math.round(newtonCotes.integrate_infty(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                                } else {
                                                    MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, points.getPoint(next).getReelX(), 100) * 1e6) / 1e6;
                                                    current = next;
                                                    next = points.getNextPointNumber(next);
                                                }
                                            }
                                            if (i > 0 && i < points.getNOPP() - 2) {
                                                MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), points.getPoint(next).getReelX(), 100) * 1e6) / 1e6;
                                                current = next;
                                                next = points.getNextPointNumber(next);
                                            }
                                            if (i == points.getNOPP() - 2 && i > 0) {
                                                MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), end, 100) * 1e6) / 1e6;
                                            }
                                        }
                                        if (points.getNOPP() != 2) {
                                            MaxErrorHermit = NumLinAlg.getMax(MaxErrorsHermit);
                                        }
                                        coordSystem.getMAXHermitView().setText("" + Math.round(MaxErrorHermit * 1e6) / 1e6);
                                        coordSystem.getL2HermitView().setText("" + Math.round(L2ErrorHermit * 1e6) / 1e6);
                                    }
                                }
                            }
                        }
                        // Anpassen des Schiebereglers für die Ordnung der Approximation an die Anzahl der "placed" (Number of placed points, NOPP) Points.
                        approxDegreeBar.setMax(points.getNOPP() - 1);
                        invalidate();

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

    public NewtonPolynom getInterpolant() {
        return interpolant;
    }

    public void setInterpolant(NewtonPolynom interpolant) {
        this.interpolant = interpolant;
    }

    public void setEnd(double end) {
        this.end = end;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public void setApproxDegreeBar(SeekBar approxDegreeBar) {
        this.approxDegreeBar = approxDegreeBar;
    }

    public NewtonPolynom getApproximant() {
        return approximant;
    }

    public void setApproximant(NewtonPolynom approximant) {
        this.approximant = approximant;
    }

    public Switch getApproxSwitch() {
        return ApproxSwitch;
    }

    public void setApproxSwitch(Switch approxSwitch) {
        ApproxSwitch = approxSwitch;
    }

    public TextView getApproxView() {
        return approxView;
    }

    public void setApproxView(TextView approxView) {
        this.approxView = approxView;
    }

    public TextView getInterView() {
        return interView;
    }

    public void setInterView(TextView interView) {
        this.interView = interView;
    }

    public Switch getHermitSwitch() {
        return HermitSwitch;
    }

    public void setHermitSwitch(Switch hermitSwitch) {
        HermitSwitch = hermitSwitch;
    }

    public HermitFunction getHermitFunction() {
        return hermitFunction;
    }

    public void setHermitFunction(HermitFunction hermitFunction) {
        this.hermitFunction = hermitFunction;
    }

    public Switch getInterSwitch() {
        return InterSwitch;
    }

    public void setInterSwitch(Switch interSwitch) {
        InterSwitch = interSwitch;
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
    public void setIs_init() {this.is_init=true;}
}
