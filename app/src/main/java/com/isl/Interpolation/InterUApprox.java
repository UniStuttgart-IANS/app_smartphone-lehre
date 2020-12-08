package com.uni_stuttgart.isl.Interpolation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.uni_stuttgart.isl.CoolStuff.NumLinAlg;
import com.uni_stuttgart.isl.Function.AbsFunction;
import com.uni_stuttgart.isl.Function.HermitFunction;
import com.uni_stuttgart.isl.Function.NewtonPolynom;
import com.uni_stuttgart.isl.Function.PolynomToolbox;
import com.uni_stuttgart.isl.Function.RungeFunction;
import com.uni_stuttgart.isl.Function.TrigonomialFunction;
import com.uni_stuttgart.isl.Intros.Intro_Interpolation;
import com.uni_stuttgart.isl.Intros.PrefManager;
import com.uni_stuttgart.isl.MainActivity;
import com.uni_stuttgart.isl.Points.Points;
import com.uni_stuttgart.isl.Quadrature.NewtonCotes;
import com.uni_stuttgart.isl.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class InterUApprox extends AppCompatActivity {

    String logSave;
    String[] logSaveVec;
    private Toolbar toolbar;
    private Points points;
    private MyCanvas myCanvas;
    private CoordSystem coordSystem;
    private SeekBar ApproxDegreeBar;
    private ImageButton addButton;
    private double start;
    private double end;
    private int coordArt;
    private Spinner functionSelecter;
    private TextView DegreeApproxView;
    private TextView DegreeInterView;
    private TextView L2InterView;
    private TextView L2ApproxView;
    private TextView L2HermitView;
    private TextView MAXInterView;
    private TextView MAXApproxView;
    private TextView MAXHermitView;
    private Switch HermitSwtich;
    private Switch InterSwitch;
    private Switch ApproxSwitch;
    private PrefManager prefManager;
    private int n = 10;
    private Paint[] interPointPaint = new Paint[n];
    private int final_size;


    private CharSequence[] functions = {"Funktionen", "Runge Funktion", "Sinus Funktion", "Betrags Funktion"};

    private TrigonomialFunction trigonomialFunction = new TrigonomialFunction(1, Math.PI * 2, 0, 0);
    private RungeFunction rungeFunction = new RungeFunction();
    private AbsFunction absFunction = new AbsFunction();
    private int startNOOP = 0;
    private ImageButton interpolationHelperImageButton;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
//
        if (item.getItemId() == R.id.introduction) {
            prefManager.setReopenInterpolationIntro(false);
            saveActivityToLog();
            Intent myIntent = new Intent(InterUApprox.this, Intro_Interpolation.class);
            InterUApprox.this.startActivity(myIntent);
            freeMemory();
            finish();
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.interuapprox);

        prefManager = new PrefManager(this);

        logSave = Read();
        logSaveVec = StringToStringVev(logSave);

        // Erzeugen der Toolbar
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Speichern der aktuelle Konfiguration
                saveActivityToLog();
                // Bei Wiederaufruf der App soll (da ja zurückgegangen wurde zu Main) die Main geöffnet werden
                prefManager.setIsMainLastActivity(true);
                prefManager.setIsInterLastActivity(false);
                prefManager.setIsSplitLastActivity(false);
                prefManager.setIsIntegLastActivity(false);
                // Es wurde auf "Back" geklickt und nicht die App geschlossen
                prefManager.setIsGoingBack(true);
                // Start der Main
                Intent myIntent = new Intent(InterUApprox.this, MainActivity.class);
                InterUApprox.this.startActivity(myIntent);
                freeMemory();
                finish();
            }
        });

//
//        DisplayMetrics metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // Festlegen der Farbe, Dicke,... der ...-Punkte
        for (int i = 0; i < n; i++) {
            interPointPaint[i] = new Paint();
            interPointPaint[i].setAntiAlias(true);
            interPointPaint[i].setStyle(Paint.Style.FILL_AND_STROKE);
            interPointPaint[i].setStrokeJoin(Paint.Join.ROUND);
            interPointPaint[i].setStrokeWidth(5.5F);
        }

        // Farben der Punkte definieren
        interPointPaint[0].setColor(Color.rgb(210, 210, 210));       //Grey
        interPointPaint[1].setColor(Color.rgb(255, 0, 16));          //Red
        interPointPaint[2].setColor(Color.rgb(255, 255, 0));         //Yellow
        interPointPaint[3].setColor(Color.rgb(240, 163, 255));       //Amethyst
        interPointPaint[4].setColor(Color.rgb(94, 241, 242));        //Sky (Cyan)
        interPointPaint[5].setColor(Color.rgb(0, 117, 220));         //Blue
        interPointPaint[6].setColor(Color.rgb(25, 25, 25));          //Ebony
        interPointPaint[7].setColor(Color.rgb(255, 164, 5));         //Orpiment (Orange)
        interPointPaint[8].setColor(Color.rgb(0, 153, 143));         //Türkis
        interPointPaint[9].setColor(Color.rgb(43, 206, 72));         //Green

        myCanvas = (MyCanvas) findViewById(R.id.mycanvas);

        final Button eqbutton = findViewById(R.id.equi);
        final LinearLayout mid = findViewById(R.id.pointcol);
        final LinearLayout opt = findViewById(R.id.optcol);

        setCoordArt(4);

        myCanvas.post(new Runnable() {
            @Override
            public void run() {

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;

                System.out.println(height-toolbar.getHeight()+ " "+ eqbutton.getHeight()+ " "+size.y);
                ViewGroup.LayoutParams ParamsCanvas = myCanvas.getLayoutParams();
                int canvas_size = height-toolbar.getHeight()-eqbutton.getHeight()-20;
                int size_other = mid.getWidth() + opt.getWidth() +20;
                if(canvas_size>(width-size_other-20)) canvas_size=width-size_other-20;
                ParamsCanvas.width = canvas_size;
                ParamsCanvas.height = canvas_size;
                myCanvas.setLayoutParams(ParamsCanvas);

                final_size = myCanvas.getLayoutParams().width;

                // Button für äquidistante Stützstelen
                Button equi = (Button) findViewById(R.id.equi);
                equi.setWidth((int) final_size / 2);

                // Button für Tschebyscheff Stützstellen
                Button tscheby = (Button) findViewById(R.id.tscheby);
                tscheby.setWidth((int) final_size / 2);

                addButton = (ImageButton) findViewById(R.id.addButton);


                // Anlegen der Punkte
                points = new Points(n);
                coordSystem = new CoordSystem((int) final_size / 2, (int) final_size / 2, (int) final_size / 2 - 50, (int) final_size / 2 - 50, coordArt, myCanvas);

                myCanvas.setCoordSystem(coordSystem);
                myCanvas.setPoints(points);

                // Einlesen verschiedener Textviews (Namen erklären ihre Funktion)
                DegreeApproxView = (TextView) findViewById(R.id.DegreeApproxViewID);
                myCanvas.setApproxView(DegreeApproxView);

                DegreeInterView = (TextView) findViewById(R.id.DegreeInterViewID);
                myCanvas.setInterView(DegreeInterView);

                L2InterView = (TextView) findViewById(R.id.L2ErrorInter);
                coordSystem.setL2InterView(L2InterView);

                L2ApproxView = (TextView) findViewById(R.id.L2ErrorApprox);
                coordSystem.setL2ApproxView(L2ApproxView);

                L2HermitView = (TextView) findViewById(R.id.L2ErrorHermit);
                coordSystem.setL2HermitView(L2HermitView);

                MAXInterView = (TextView) findViewById(R.id.MAXErrorInter);
                coordSystem.setMAXInterView(MAXInterView);

                MAXApproxView = (TextView) findViewById(R.id.MAXErrorApprox);
                coordSystem.setMAXApproxView(MAXApproxView);

                MAXHermitView = (TextView) findViewById(R.id.MAXErrorHermit);
                coordSystem.setMAXHermitView(MAXHermitView);

                InterSwitch = (Switch) findViewById(R.id.interSwitch);
                myCanvas.setInterSwitch(InterSwitch);

                ApproxSwitch = (Switch) findViewById(R.id.approxSwitch);
                myCanvas.setApproxSwitch(ApproxSwitch);

                ApproxDegreeBar = (SeekBar) findViewById(R.id.seekBar0);
                myCanvas.setApproxDegreeBar(ApproxDegreeBar);

                HermitSwtich = (Switch) findViewById(R.id.hermitSwitch);
                myCanvas.setHermitSwitch(HermitSwtich);

                // Hilfe Button Listener erzeugen
                //addListenerOnInterHelpButton();
                myCanvas.setIs_init();
                loadActivity();
            }
        });
    }

    public void Adder(View view) {
        int k = -1;
        do {
            k++;
        } while (points.getPoint(k).getIsAvailable() && k + 1 < points.getCount());

//		Log.e("Debug", "k = " + k + ", NOOP = "+ points.getNOPP());

        if (k < points.getCount()) {
            points.getPoint(k).getSwitch().setVisibility(View.VISIBLE);
            points.getPoint(k).getDeleteButton().setVisibility(View.VISIBLE);
            points.getPoint(k).getxView().setVisibility(View.VISIBLE);
            points.getPoint(k).getyView().setVisibility(View.VISIBLE);
            points.getPoint(k).getxView().setText("");
            points.getPoint(k).getyView().setText("");

            points.getPoint(k).setIsAvailable(true);
            points.getPoint(k).setActive(true);
            points.getPoint(k).getSwitch().setChecked(true);
            if (k == points.getNOPP() || points.getNOPP() == points.getNOAP() - 1) {
                points.getPoint(k).setSwitchOnOff(true);
            }
        }

        if (points.getNOAP() == points.getCount()) {
            this.addButton.setVisibility(View.GONE);
        }

        freeMemory();

    }

    public void setEqui(View view) {
        int k = 0;
        double[] equiX = new double[points.getNOAP()];
        if (coordArt == 2 || coordArt == 1) {
            equiX = PolynomToolbox.getEquiDistX(coordSystem.getX0(), coordSystem.getX0() + coordSystem.getxLen(), points.getNOAP());
        }
        if (coordArt == 4 || coordArt == 3) {
            equiX = PolynomToolbox.getEquiDistX(coordSystem.getX0() - coordSystem.getxLen(), coordSystem.getX0() + coordSystem.getxLen(), points.getNOAP());
        }

        for (int i = 0; i < points.getCount(); i++) {
            if (points.getPoint(i).getIsAvailable()) {
                points.getPoint(i).setX(equiX[k]);
                if (coordArt == 4 || coordArt == 3) {
                    points.getPoint(i).setY(coordSystem.getyFuncTrans()[(int) (points.getPoint(i).getX() - coordSystem.getX0() + coordSystem.getxLen())]);
                }
                if (coordArt == 2 || coordArt == 1) {
                    points.getPoint(i).setY(coordSystem.getyFuncTrans()[(int) (points.getPoint(i).getX() - coordSystem.getX0())]);
                }
                points.getPoint(i).setRectCoords();
                if (coordArt == 4 || coordArt == 3) {
                    points.getPoint(i).setReelX(coordSystem.getxFunc()[(int) (-coordSystem.getX0() + coordSystem.getxLen() + points.getPoint(i).getX())]);
                }
                if (coordArt == 2 || coordArt == 1) {
                    points.getPoint(i).setReelX(coordSystem.getxFunc()[(int) (-coordSystem.getX0() + points.getPoint(i).getX())]);
                }
                if (coordArt == 4 || coordArt == 3) {
                    points.getPoint(i).setReelY(coordSystem.getFunction().evaluate(coordSystem.getxFunc()[(int) (-coordSystem.getX0() + coordSystem.getxLen() + points.getPoint(i).getX())]));
                }
                if (coordArt == 2 || coordArt == 1) {
                    points.getPoint(i).setReelY(coordSystem.getFunction().evaluate(coordSystem.getxFunc()[(int) (-coordSystem.getX0() + points.getPoint(i).getX())]));
                }
                points.getPoint(i).setIsPlaced(true);
                points.getPoint(i).getxView().setText("" + Math.round(points.getPoint(i).getReelX() * 1e3) / 1e3);
                points.getPoint(i).getyView().setText("" + Math.round(points.getPoint(i).getReelY() * 1e3) / 1e3);
                k++;
            }

        }
        double[] xCoord;
        double[] yCoord;

        if (myCanvas.getInterSwitch().isChecked()) {
            // Berechnen der Interpolierenden
            // Anlegen eines Polynomes mit Grad entsprechend der gesetzten Punkte
            myCanvas.setInterpolant(new NewtonPolynom(points.getNOPP() - 1));

            // Anlegen der x-, und y-Vektoren mit den Koordinaten zum Interpolieren
            xCoord = new double[points.getNOPP()];
            yCoord = new double[points.getNOPP()];
            k = 0;

            // Füllen der oben genannten Vektoren
            for (int i = 0; i < points.getCount(); i++) {
                if (points.getPoint(i).getIsPlaced()) {
                    xCoord[k] = points.getPoint(i).getReelX();
                    yCoord[k] = points.getPoint(i).getReelY();
                    k++;
                }
            }

            // Interpolieren, bzw. Kooeffizienten berechnen
            myCanvas.getInterpolant().interpolate(xCoord, yCoord);

            myCanvas.clearInterFunc();
            coordSystem.setyInter(myCanvas.getInterpolant());
            coordSystem.drawInter();

            NewtonCotes newtonCotes = new NewtonCotes(3);
            double temp = 0;
            if (coordSystem.getFunction() instanceof RungeFunction) {
                temp = Math.round(newtonCotes.integrate_LP(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                if (temp > 1) {
                    coordSystem.getL2InterView().setTextColor(Color.RED);
                } else if (temp <= 1 && temp > 0.5) {
                    coordSystem.getL2InterView().setTextColor(Color.BLACK);
                } else {
                    coordSystem.getL2InterView().setTextColor(Color.GREEN);
                }
                coordSystem.getL2InterView().setText("" + temp);

                temp = Math.round(newtonCotes.integrate_infty(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                if (temp > 1) {
                    coordSystem.getMAXInterView().setTextColor(Color.RED);
                } else if (temp <= 1 && temp > 0.5) {
                    coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                } else {
                    coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                }
                coordSystem.getMAXInterView().setText("" + temp);
            } else if (coordSystem.getFunction() instanceof AbsFunction) {
                temp = Math.round(newtonCotes.integrate_LP(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                if (temp > 0.3) {
                    coordSystem.getL2InterView().setTextColor(Color.RED);
                } else if (temp <= 0.3 && temp > 0.07) {
                    coordSystem.getL2InterView().setTextColor(Color.BLACK);
                } else {
                    coordSystem.getL2InterView().setTextColor(Color.GREEN);
                }
                coordSystem.getL2InterView().setText("" + temp);

                temp = Math.round(newtonCotes.integrate_infty(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                if (temp > 0.5) {
                    coordSystem.getMAXInterView().setTextColor(Color.RED);
                } else if (temp <= 0.5 && temp > 0.1) {
                    coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                } else {
                    coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                }
                coordSystem.getMAXInterView().setText("" + temp);
            } else if (coordSystem.getFunction() instanceof TrigonomialFunction) {
                temp = Math.round(newtonCotes.integrate_LP(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                if (temp > 1) {
                    coordSystem.getL2InterView().setTextColor(Color.RED);
                } else if (temp <= 1 && temp > 0.09) {
                    coordSystem.getL2InterView().setTextColor(Color.BLACK);
                } else {
                    coordSystem.getL2InterView().setTextColor(Color.GREEN);
                }
                coordSystem.getL2InterView().setText("" + temp);

                temp = Math.round(newtonCotes.integrate_infty(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                if (temp > 1) {
                    coordSystem.getMAXInterView().setTextColor(Color.RED);
                } else if (temp <= 1 && temp > 0.09) {
                    coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                } else {
                    coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                }
                coordSystem.getMAXInterView().setText("" + temp);
            }
            myCanvas.getInterView().setText("" + (points.getNOPP() - 1));
        }
        // Approximieren
        if (myCanvas.getApproxSwitch().isChecked()) {
            myCanvas.setApproximant(new NewtonPolynom(ApproxDegreeBar.getProgress()));

            // Anlegen der x-, und y-Vektoren mit den Koordinaten zum Approximieren
            xCoord = new double[points.getNOPP()];
            yCoord = new double[points.getNOPP()];
            k = 0;

            // Füllen der oben genannten Vektoren
            for (int i = 0; i < points.getCount(); i++) {
                if (points.getPoint(i).getIsPlaced()) {
                    xCoord[k] = points.getPoint(i).getReelX();
                    yCoord[k] = points.getPoint(i).getReelY();
                    k++;
                }
            }

            // Approximieren, bzw. Kooeffizienten berechnen
            myCanvas.getApproximant().lsApproximation(xCoord, yCoord);
            myCanvas.clearApproxFunc();
            coordSystem.setyApprox(myCanvas.getApproximant());
            coordSystem.drawApprox();

            NewtonCotes newtonCotes = new NewtonCotes(3);
            double temp = 0;
            if (coordSystem.getFunction() instanceof RungeFunction) {
                temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                if (temp > 1) {
                    coordSystem.getL2ApproxView().setTextColor(Color.RED);
                } else if (temp <= 1 && temp > 0.5) {
                    coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                } else {
                    coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                }
                coordSystem.getL2ApproxView().setText("" + temp);

                temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                if (temp > 1) {
                    coordSystem.getMAXApproxView().setTextColor(Color.RED);
                } else if (temp <= 1 && temp > 0.5) {
                    coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                } else {
                    coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                }
                coordSystem.getMAXApproxView().setText("" + temp);
            } else if (coordSystem.getFunction() instanceof AbsFunction) {
                temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                if (temp > 0.3) {
                    coordSystem.getL2ApproxView().setTextColor(Color.RED);
                } else if (temp <= 0.3 && temp > 0.07) {
                    coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                } else {
                    coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                }
                coordSystem.getL2ApproxView().setText("" + temp);

                temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                if (temp > 0.5) {
                    coordSystem.getMAXApproxView().setTextColor(Color.RED);
                } else if (temp <= 0.5 && temp > 0.1) {
                    coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                } else {
                    coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                }
                coordSystem.getMAXApproxView().setText("" + temp);
            } else if (coordSystem.getFunction() instanceof TrigonomialFunction) {
                temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                if (temp > 1) {
                    coordSystem.getL2ApproxView().setTextColor(Color.RED);
                } else if (temp <= 1 && temp > 0.09) {
                    coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                } else {
                    coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                }
                coordSystem.getL2ApproxView().setText("" + temp);

                temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
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
        if (myCanvas.getHermitSwitch().isChecked()) {
            if (points.getNOPP() >= 2) {
                // Sortieren der Punkte
                points.sortPoints();

                // Berechnen der Approximierenden
                // Anlegen eines Hermit Interpolationspolynom
                myCanvas.setHermitFunction(new HermitFunction());
                myCanvas.getHermitFunction().setPoints(points);
                myCanvas.getHermitFunction().setCoordSystem(coordSystem);

                if (points.getNOPP() >= 2) {
                    // Hermit Interpolierenden berechnen
                    myCanvas.getHermitFunction().hermitinterpolate();

                    // Alte HermitIntepolierende löschen
                    myCanvas.clearHermitFunc();

                    // Setzen der y-Koordinate der HermitIntepolierenden
                    coordSystem.setyHermit(myCanvas.getHermitFunction());

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
                                L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                            } else {
                                L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, points.getPoint(next).getReelX(), 2, 100) * 1e6) / 1e6;
                                current = next;
                                next = points.getNextPointNumber(next);
                            }
                        }
                        if (i > 0 && i < points.getNOPP() - 2) {
                            L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), points.getPoint(next).getReelX(), 2, 100) * 1e6) / 1e6;
                            current = next;
                            next = points.getNextPointNumber(next);
                        }
                        if (i == points.getNOPP() - 2 && i > 0) {
                            L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), end, 2, 100) * 1e6) / 1e6;
                        }
                    }

                    // MAX
                    current = points.getFirstPointNumber();
                    next = points.getNextPointNumber(points.getFirstPointNumber());

                    for (int i = 0; i < points.getNOPP() - 1; i++) {
                        if (i == 0) {
                            if (points.getNOPP() == 2) {
                                MaxErrorHermit = Math.round(newtonCotes.integrate_infty(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                            } else {
                                MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, points.getPoint(next).getReelX(), 100) * 1e6) / 1e6;
                                current = next;
                                next = points.getNextPointNumber(next);
                            }
                        }
                        if (i > 0 && i < points.getNOPP() - 2) {
                            MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), points.getPoint(next).getReelX(), 100) * 1e6) / 1e6;
                            current = next;
                            next = points.getNextPointNumber(next);
                        }
                        if (i == points.getNOPP() - 2 && i > 0) {
                            MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), end, 100) * 1e6) / 1e6;
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

        myCanvas.invalidate();

        for (int i = 0; i < points.getCount(); i++) {
            points.getPoint(i).setActive(false);
            points.getPoint(i).getSwitch().setChecked(false);
        }

            freeMemory();

        ApproxDegreeBar.setMax(points.getNOPP() - 1);
    }

    public void setTscheby(View view) {
        int k = 0;
        double[] tschebyX = new double[points.getNOAP()];
        if (coordArt == 2 || coordArt == 1) {
            tschebyX = PolynomToolbox.getTschebyX(coordSystem.getX0(), coordSystem.getX0() + coordSystem.getxLen(), points.getNOAP());
        }
        if (coordArt == 4 || coordArt == 3) {
            tschebyX = PolynomToolbox.getTschebyX(coordSystem.getX0() - coordSystem.getxLen(), coordSystem.getX0() + coordSystem.getxLen(), points.getNOAP());
        }

        for (int i = 0; i < points.getCount(); i++) {
            if (points.getPoint(i).getIsAvailable()) {
                points.getPoint(i).setX(tschebyX[k]);
                if (coordArt == 4 || coordArt == 3) {
                    points.getPoint(i).setY(coordSystem.getyFuncTrans()[(int) (points.getPoint(i).getX() - coordSystem.getX0() + coordSystem.getxLen())]);
                }
                if (coordArt == 2 || coordArt == 1) {
                    points.getPoint(i).setY(coordSystem.getyFuncTrans()[(int) (points.getPoint(i).getX() - coordSystem.getX0())]);
                }
                points.getPoint(i).setRectCoords();
                if (coordArt == 4 || coordArt == 3) {
                    points.getPoint(i).setReelX(coordSystem.getxFunc()[(int) (-coordSystem.getX0() + coordSystem.getxLen() + points.getPoint(i).getX())]);
                }
                if (coordArt == 2 || coordArt == 1) {
                    points.getPoint(i).setReelX(coordSystem.getxFunc()[(int) (-coordSystem.getX0() + points.getPoint(i).getX())]);
                }
                if (coordArt == 4 || coordArt == 3) {
                    points.getPoint(i).setReelY(coordSystem.getFunction().evaluate(coordSystem.getxFunc()[(int) (-coordSystem.getX0() + coordSystem.getxLen() + points.getPoint(i).getX())]));
                }
                if (coordArt == 2 || coordArt == 1) {
                    points.getPoint(i).setReelY(coordSystem.getFunction().evaluate(coordSystem.getxFunc()[(int) (-coordSystem.getX0() + points.getPoint(i).getX())]));
                }
                points.getPoint(i).setIsPlaced(true);

                points.getPoint(i).getxView().setText("" + Math.round(points.getPoint(i).getReelX() * 1e3) / 1e3);
                points.getPoint(i).getyView().setText("" + Math.round(points.getPoint(i).getReelY() * 1e3) / 1e3);
                k++;
            }
        }

        double[] xCoord;
        double[] yCoord;

        if (myCanvas.getInterSwitch().isChecked()) {
            // Berechnen der Interpolierenden
            // Anlegen eines Polynomes mit Grad entsprechend der gesetzten Punkte
            myCanvas.setInterpolant(new NewtonPolynom(points.getNOPP() - 1));

            // Anlegen der x-, und y-Vektoren mit den Koordinaten zum Interpolieren
            xCoord = new double[points.getNOPP()];
            yCoord = new double[points.getNOPP()];
            k = 0;

            // Füllen der oben genannten Vektoren
            for (int i = 0; i < points.getCount(); i++) {
                if (points.getPoint(i).getIsPlaced()) {
                    xCoord[k] = points.getPoint(i).getReelX();
                    yCoord[k] = points.getPoint(i).getReelY();
                    k++;
                }
            }

            // Interpolieren, bzw. Kooeffizienten berechnen
            myCanvas.getInterpolant().interpolate(xCoord, yCoord);

            myCanvas.clearInterFunc();
            coordSystem.setyInter(myCanvas.getInterpolant());
            coordSystem.drawInter();

            NewtonCotes newtonCotes = new NewtonCotes(3);
            double temp = 0;
            if (coordSystem.getFunction() instanceof RungeFunction) {
                temp = Math.round(newtonCotes.integrate_LP(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                if (temp > 1) {
                    coordSystem.getL2InterView().setTextColor(Color.RED);
                } else if (temp <= 1 && temp > 0.5) {
                    coordSystem.getL2InterView().setTextColor(Color.BLACK);
                } else {
                    coordSystem.getL2InterView().setTextColor(Color.GREEN);
                }
                coordSystem.getL2InterView().setText("" + temp);

                temp = Math.round(newtonCotes.integrate_infty(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                if (temp > 1) {
                    coordSystem.getMAXInterView().setTextColor(Color.RED);
                } else if (temp <= 1 && temp > 0.5) {
                    coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                } else {
                    coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                }
                coordSystem.getMAXInterView().setText("" + temp);
            } else if (coordSystem.getFunction() instanceof AbsFunction) {
                temp = Math.round(newtonCotes.integrate_LP(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                if (temp > 0.3) {
                    coordSystem.getL2InterView().setTextColor(Color.RED);
                } else if (temp <= 0.3 && temp > 0.07) {
                    coordSystem.getL2InterView().setTextColor(Color.BLACK);
                } else {
                    coordSystem.getL2InterView().setTextColor(Color.GREEN);
                }
                coordSystem.getL2InterView().setText("" + temp);

                temp = Math.round(newtonCotes.integrate_infty(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                if (temp > 0.5) {
                    coordSystem.getMAXInterView().setTextColor(Color.RED);
                } else if (temp <= 0.5 && temp > 0.1) {
                    coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                } else {
                    coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                }
                coordSystem.getMAXInterView().setText("" + temp);
            } else if (coordSystem.getFunction() instanceof TrigonomialFunction) {
                temp = Math.round(newtonCotes.integrate_LP(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                if (temp > 1) {
                    coordSystem.getL2InterView().setTextColor(Color.RED);
                } else if (temp <= 1 && temp > 0.09) {
                    coordSystem.getL2InterView().setTextColor(Color.BLACK);
                } else {
                    coordSystem.getL2InterView().setTextColor(Color.GREEN);
                }
                coordSystem.getL2InterView().setText("" + temp);

                temp = Math.round(newtonCotes.integrate_infty(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                if (temp > 1) {
                    coordSystem.getMAXInterView().setTextColor(Color.RED);
                } else if (temp <= 1 && temp > 0.09) {
                    coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                } else {
                    coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                }
                coordSystem.getMAXInterView().setText("" + temp);
            }
            myCanvas.getInterView().setText("" + (points.getNOPP() - 1));
        }
        if (myCanvas.getApproxSwitch().isChecked()) {
            myCanvas.setApproximant(new NewtonPolynom(ApproxDegreeBar.getProgress()));
            // Anlegen der x-, und y-Vektoren mit den Koordinaten zum Approximieren
            xCoord = new double[points.getNOPP()];
            yCoord = new double[points.getNOPP()];
            k = 0;

            // Füllen der oben genannten Vektoren
            for (int i = 0; i < points.getCount(); i++) {
                if (points.getPoint(i).getIsPlaced()) {
                    xCoord[k] = points.getPoint(i).getReelX();
                    yCoord[k] = points.getPoint(i).getReelY();
                    k++;
                }
            }

            // Approximieren, bzw. Kooeffizienten berechnen
            myCanvas.getApproximant().lsApproximation(xCoord, yCoord);
            myCanvas.clearApproxFunc();
            coordSystem.setyApprox(myCanvas.getApproximant());
            coordSystem.drawApprox();

            NewtonCotes newtonCotes = new NewtonCotes(3);
            double temp = 0;
            if (coordSystem.getFunction() instanceof RungeFunction) {
                temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                if (temp > 1) {
                    coordSystem.getL2ApproxView().setTextColor(Color.RED);
                } else if (temp <= 1 && temp > 0.5) {
                    coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                } else {
                    coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                }
                coordSystem.getL2ApproxView().setText("" + temp);

                temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                if (temp > 1) {
                    coordSystem.getMAXApproxView().setTextColor(Color.RED);
                } else if (temp <= 1 && temp > 0.5) {
                    coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                } else {
                    coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                }
                coordSystem.getMAXApproxView().setText("" + temp);
            } else if (coordSystem.getFunction() instanceof AbsFunction) {
                temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                if (temp > 0.3) {
                    coordSystem.getL2ApproxView().setTextColor(Color.RED);
                } else if (temp <= 0.3 && temp > 0.07) {
                    coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                } else {
                    coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                }
                coordSystem.getL2ApproxView().setText("" + temp);

                temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                if (temp > 0.5) {
                    coordSystem.getMAXApproxView().setTextColor(Color.RED);
                } else if (temp <= 0.5 && temp > 0.1) {
                    coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                } else {
                    coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                }
                coordSystem.getMAXApproxView().setText("" + temp);
            } else if (coordSystem.getFunction() instanceof TrigonomialFunction) {
                temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                if (temp > 1) {
                    coordSystem.getL2ApproxView().setTextColor(Color.RED);
                } else if (temp <= 1 && temp > 0.09) {
                    coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                } else {
                    coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                }
                coordSystem.getL2ApproxView().setText("" + temp);

                temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
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
        if (myCanvas.getHermitSwitch().isChecked()) {
            if (points.getNOPP() >= 2) {
                // Sortieren der Punkte
                points.sortPoints();

                // Berechnen der Approximierenden
                // Anlegen eines Hermit Interpolationspolynom
                myCanvas.setHermitFunction(new HermitFunction());
                myCanvas.getHermitFunction().setPoints(points);
                myCanvas.getHermitFunction().setCoordSystem(coordSystem);

                if (points.getNOPP() >= 2) {
                    // Hermit Interpolierenden berechnen
                    myCanvas.getHermitFunction().hermitinterpolate();

                    // Alte HermitIntepolierende löschen
                    myCanvas.clearHermitFunc();

                    // Setzen der y-Koordinate der HermitIntepolierenden
                    coordSystem.setyHermit(myCanvas.getHermitFunction());

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
                                L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                            } else {
                                L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, points.getPoint(next).getReelX(), 2, 100) * 1e6) / 1e6;
                                current = next;
                                next = points.getNextPointNumber(next);
                            }
                        }
                        if (i > 0 && i < points.getNOPP() - 2) {
                            L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), points.getPoint(next).getReelX(), 2, 100) * 1e6) / 1e6;
                            current = next;
                            next = points.getNextPointNumber(next);
                        }
                        if (i == points.getNOPP() - 2 && i > 0) {
                            L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), end, 2, 100) * 1e6) / 1e6;
                        }
                    }

                    // MAX
                    current = points.getFirstPointNumber();
                    next = points.getNextPointNumber(points.getFirstPointNumber());

                    for (int i = 0; i < points.getNOPP() - 1; i++) {
                        if (i == 0) {
                            if (points.getNOPP() == 2) {
                                MaxErrorHermit = Math.round(newtonCotes.integrate_infty(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                            } else {
                                MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, points.getPoint(next).getReelX(), 100) * 1e6) / 1e6;
                                current = next;
                                next = points.getNextPointNumber(next);
                            }
                        }
                        if (i > 0 && i < points.getNOPP() - 2) {
                            MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), points.getPoint(next).getReelX(), 100) * 1e6) / 1e6;
                            current = next;
                            next = points.getNextPointNumber(next);
                        }
                        if (i == points.getNOPP() - 2 && i > 0) {
                            MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), end, 100) * 1e6) / 1e6;
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

        myCanvas.invalidate();

        ApproxDegreeBar.setMax(points.getNOPP() - 1);

        for (int i = 0; i < points.getCount(); i++) {
            points.getPoint(i).setActive(false);
            points.getPoint(i).getSwitch().setChecked(false);
        }

        freeMemory();
    }

    public void setCoordArt(int coordArt) {
        this.coordArt = coordArt;
    }


    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    public final String Read() {

        String logString = "";
        try {
            InputStream inputStream = this.openFileInput("LogInter.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                logString = stringBuilder.toString();

//                Log.e("TAG", logString);
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return logString;
    }

    public String[] StringToStringVev(String LogString) {
        String[] splitLogString;

        splitLogString = LogString.split(" ");

        return splitLogString;
    }

    public void StringVecViewer(String[] splitString) {
        Log.e("TAG", "-------------------------------------------------------------------");
        for (int i = 0; i < 10; i++) {
            Log.e("TAG", "Interpolationpunkt " + i);
            Log.e("TAG", "I/O: " + splitString[3 * i] + "; " + "X = " + splitString[3 * i + 1] + "; " + "Y = " + splitString[3 * i + 2]);
            Log.e("TAG", "--------------------");
        }
        Log.e("TAG", "--------------------");
        Log.e("TAG", "Interpolation I/O: " + splitString[30]);
        Log.e("TAG", "Approximation I/O: " + splitString[31] + "; " + "Ordnung = " + splitString[32]);
        Log.e("TAG", "Hermitinterpolation I/O: " + splitString[33]);
        Log.e("TAG", "Funktion: " + splitString[34]);

    }

    public int getPointPlacedLog(int pointnumber) {
        return Integer.parseInt(logSaveVec[pointnumber * 3]);
    }

    public String StringVecToString(String[] logStringVec) {
        String logString = "";

        for (int i = 0; i < logStringVec.length; i++) {
            logString = logString + logStringVec[i] + " ";
        }

        return logString;
    }

    public void saveActivityToLog() {
        for (int i = 0; i < 10; i++) {
            logSaveVec[3 * i] = Integer.toString(getIntFromBoolean(points.getPoint(i).getIsPlaced()));
            logSaveVec[3 * i + 1] = "" + points.getPoint(i).getX();
            logSaveVec[3 * i + 2] = "" + points.getPoint(i).getY();
        }

        logSaveVec[30] = Integer.toString(getIntFromBoolean(InterSwitch.isChecked()));
        logSaveVec[31] = Integer.toString(getIntFromBoolean(myCanvas.getApproxSwitch().isChecked()));
        logSaveVec[32] = Integer.toString(ApproxDegreeBar.getProgress());
        logSaveVec[33] = Integer.toString(getIntFromBoolean(HermitSwtich.isChecked()));
        if (coordSystem.getFunction() instanceof RungeFunction) {
            logSaveVec[34] = ""+0;
        }
        else if (coordSystem.getFunction() instanceof TrigonomialFunction){
            logSaveVec[34] = ""+1;
        }
        else if(coordSystem.getFunction() instanceof AbsFunction){
            logSaveVec[34] = ""+2;
        }

        Save(StringVecToString(logSaveVec));
    }

    public void Save(String log) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("LogInter.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(log);
            outputStreamWriter.close();

        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public void resetAll(View view) {
        myCanvas.getInterSwitch().setChecked(false);
        myCanvas.getApproxSwitch().setChecked(false);
        myCanvas.getHermitSwitch().setChecked(false);
        coordSystem.getL2InterView().setText("");
        coordSystem.getMAXInterView().setText("");
        coordSystem.getL2ApproxView().setText("");
        coordSystem.getMAXApproxView().setText("");
        myCanvas.getInterView().setText("");
        myCanvas.clearPoints();
        myCanvas.clearFunc();
        myCanvas.clearApproxFunc();
        myCanvas.clearHermitFunc();
        myCanvas.clearInterFunc();
        myCanvas.setInterpolant(new NewtonPolynom());
        ApproxDegreeBar.setMax(0);
        ApproxDegreeBar.setProgress(0);

        for (int i = 1; i < points.getCount(); i++) {
            points.removePoint(i);
        }

        points.getPoint(0).getxView().setText("");
        points.getPoint(0).getyView().setText("");
        points.getPoint(0).setActive(true);
        points.getPoint(0).getSwitch().setChecked(true);
        points.getPoint(0).getDeleteButton().setVisibility(View.VISIBLE);
        points.getPoint(0).getSwitch().setVisibility(View.VISIBLE);
        points.getPoint(0).getxView().setVisibility(View.VISIBLE);
        points.getPoint(0).getyView().setVisibility(View.VISIBLE);

        addButton.setVisibility(View.VISIBLE);

        if (Integer.parseInt(logSaveVec[34]) == 0 || Integer.parseInt(logSaveVec[34]) == -1) {
            start = -5;
            end = 5;
            myCanvas.setEnd(end);
            myCanvas.setStart(start);
            // Testweise setzen der x-Werte
            coordSystem.setxFunc(start, end);
            coordSystem.setyFunc(rungeFunction);
        } else if (Integer.parseInt(logSaveVec[34]) == 1) {
            start = -1;
            end = 1;
            myCanvas.setEnd(end);
            myCanvas.setStart(start);
            // Testweise setzen der x-Werte
            coordSystem.setxFunc(start, end);
            coordSystem.setyFunc(trigonomialFunction);
        } else if (Integer.parseInt(logSaveVec[34]) == 2) {
            start = -1;
            end = 1;
            myCanvas.setEnd(end);
            myCanvas.setStart(start);
            // Testweise setzen der x-Werte
            coordSystem.setxFunc(start, end);
            coordSystem.setyFunc(absFunction);
        }
        coordSystem.drawAxis();
        coordSystem.drawFunc();
        myCanvas.setCoordSystem(coordSystem);
        myCanvas.invalidate();

        saveActivityToLog();

        freeMemory();
    }

    public int getIntFromBoolean(boolean b) {
        int i;

        if (b) {
            i = 1;
        } else {
            i = 0;
        }

        return i;
    }

    private boolean getBooleanfromInt(int i) {
        boolean b;

        if (i == 1) {
            b = true;
        } else {
            b = false;
        }

        return b;
    }

    @Override
    protected void onStop() {
        super.onStop();

        prefManager.setIsGoingBack(false);
        prefManager.setIsMainLastActivity(false);
        prefManager.setIsInterLastActivity(true);
        prefManager.setIsSplitLastActivity(false);
        prefManager.setIsIntegLastActivity(false);

        saveActivityToLog();
        freeMemory();
    }

    public void saveActivityToLogButton(View view) {
        prefManager.setIsInterpolationSaved(true);

        String[] logSaveVecButton = new String[35];
        for (int i = 0; i < 10; i++) {
            logSaveVecButton[3 * i] = Integer.toString(getIntFromBoolean(points.getPoint(i).getIsPlaced()));
            logSaveVecButton[3 * i + 1] = "" + points.getPoint(i).getX();
            logSaveVecButton[3 * i + 2] = "" + points.getPoint(i).getY();
        }

        logSaveVecButton[30] = Integer.toString(getIntFromBoolean(InterSwitch.isChecked()));
        logSaveVecButton[31] = Integer.toString(getIntFromBoolean(myCanvas.getApproxSwitch().isChecked()));
        logSaveVecButton[32] = Integer.toString(ApproxDegreeBar.getProgress());
        logSaveVecButton[33] = Integer.toString(getIntFromBoolean(HermitSwtich.isChecked()));
        if (coordSystem.getFunction() instanceof RungeFunction) {
            logSaveVecButton[34] = ""+0;
        }
        else if (coordSystem.getFunction() instanceof TrigonomialFunction){
            logSaveVecButton[34] = ""+1;
        }
        else if(coordSystem.getFunction() instanceof AbsFunction){
            logSaveVecButton[34] = ""+2;
        }

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("LogInterSave.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(StringVecToString(logSaveVecButton));
            outputStreamWriter.close();

        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        Toast.makeText(this, "Aktuelle Konfiguration wurde gespeichert!", Toast.LENGTH_SHORT).show();
    }

    public final String ReadSave() {

        String logString = "";
        try {
            InputStream inputStream = this.openFileInput("LogInterSave.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                logString = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return logString;
    }

    public void loadLogButton(View view) {
        if (prefManager.isInterpolationSaved()) {
            String Temp = ReadSave();
            Save(Temp);
            loadActivity();
            freeMemory();
        }
        else{
            Toast.makeText(this, "Bitte zuerst eine Konfiguration abspeichern!", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadActivity(){

        logSave = Read();
        logSaveVec = StringToStringVev(logSave);

        InterSwitch.setChecked(getBooleanfromInt(Integer.parseInt(logSaveVec[30])));
        ApproxSwitch.setChecked(getBooleanfromInt(Integer.parseInt(logSaveVec[31])));
        HermitSwtich.setChecked(getBooleanfromInt(Integer.parseInt(logSaveVec[33])));


//        StringVecViewer(logSaveVec);

        if (Integer.parseInt(logSaveVec[34]) == 0 || Integer.parseInt(logSaveVec[34]) == -1) {
            functions[0] = "RUNGE FUNKTION";
        } else if (Integer.parseInt(logSaveVec[34]) == 1) {
            functions[0] = "SINUS FUNKTION";
        } else if (Integer.parseInt(logSaveVec[34]) == 2) {
            functions[0] = "BETRAGS FUNKTION";
        }

        if (Integer.parseInt(logSaveVec[34]) == 0 || Integer.parseInt(logSaveVec[34]) == -1) {
            start = -5;
            end = 5;
            myCanvas.setEnd(end);
            myCanvas.setStart(start);
            // Testweise setzen der x-Werte
            coordSystem.setxFunc(start, end);
            coordSystem.setyFunc(rungeFunction);
        } else if (Integer.parseInt(logSaveVec[34]) == 1) {
            start = -1;
            end = 1;
            myCanvas.setEnd(end);
            myCanvas.setStart(start);
            // Testweise setzen der x-Werte
            coordSystem.setxFunc(start, end);
            coordSystem.setyFunc(trigonomialFunction);
        } else if (Integer.parseInt(logSaveVec[34]) == 2) {
            start = -1;
            end = 1;
            myCanvas.setEnd(end);
            myCanvas.setStart(start);
            // Testweise setzen der x-Werte
            coordSystem.setxFunc(start, end);
            coordSystem.setyFunc(absFunction);
        }

        // Koordinatensystem und Funktion zeichnen
        myCanvas.clearFunc();
        coordSystem.drawAxis();
        coordSystem.drawFunc();

        for (int i = 0; i < points.getCount(); i++) {
            if (getPointPlacedLog(i) == 1){
                startNOOP = startNOOP + 1;
            }
        }

        // Zuweisen des Switches zum EINS/AUS schalten, des Löschen Buttons und der Koordinatenanzeige zu dem jeweiligen Punkt
        // Hierbei werden alle Buttons,.. auf GONE gesetze, da bei Start des Programms nur ein Punkt alle Funktionen haben soll
        // Weiter werden alle Punkte als nicht Verfügbar deklariert und jeder Punkt erhält Farbe, Dicke,....
        for (int i = 0; i < n; i++) {
            int ID = getResources().getIdentifier("switch" + i, "id", getPackageName());
            points.getPoint(i).setSwitcher((Switch) findViewById(ID));

            ID = getResources().getIdentifier("deleter" + i, "id", getPackageName());
            points.getPoint(i).setDeleteButton((ImageButton) findViewById(ID));

            ID = getResources().getIdentifier("xcoord" + i, "id", getPackageName());
            points.getPoint(i).setxView((TextView) findViewById(ID));

            ID = getResources().getIdentifier("ycoord" + i, "id", getPackageName());
            points.getPoint(i).setyView((TextView) findViewById(ID));

            points.getPoint(i).setPaint(interPointPaint[i]);

            if (startNOOP == 0){
                points.getPoint(0).getSwitch().setVisibility(View.VISIBLE);
                points.getPoint(0).getDeleteButton().setVisibility(View.VISIBLE);
                points.getPoint(0).getxView().setVisibility(View.VISIBLE);
                points.getPoint(0).getyView().setVisibility(View.VISIBLE);
                points.getPoint(0).getxView().setText("");
                points.getPoint(0).getyView().setText("");
                if (i!=0) {
                    points.getPoint(i).getSwitch().setVisibility(View.GONE);
                    points.getPoint(i).getDeleteButton().setVisibility(View.GONE);
                    points.getPoint(i).getxView().setVisibility(View.GONE);
                    points.getPoint(i).getyView().setVisibility(View.GONE);
                    points.getPoint(i).setIsAvailable(false);
                    points.getPoint(i).setIsPlaced(false);
                }
            }
            else{
                if (getPointPlacedLog(i) == 0) {
                    points.getPoint(i).getSwitch().setVisibility(View.GONE);
                    points.getPoint(i).getDeleteButton().setVisibility(View.GONE);
                    points.getPoint(i).getxView().setVisibility(View.GONE);
                    points.getPoint(i).getyView().setVisibility(View.GONE);
                    points.getPoint(i).setIsAvailable(false);
                    points.getPoint(i).setIsPlaced(false);
                }
                else if (getPointPlacedLog(i) == 1) {
                    points.getPoint(i).getSwitch().setVisibility(View.VISIBLE);
                    points.getPoint(i).getDeleteButton().setVisibility(View.VISIBLE);
                    points.getPoint(i).getxView().setVisibility(View.VISIBLE);
                    points.getPoint(i).getyView().setVisibility(View.VISIBLE);
                    points.getPoint(i).setX(Float.parseFloat(logSaveVec[3 * i + 1]));
                    points.getPoint(i).setIsPlaced(true);
                    points.getPoint(i).setIsAvailable(true);
                    myCanvas.drawABCD((float) points.getPoint(i).getX(), i);
                    points.getPoint(i).getxView().setText("" + Math.round(points.getPoint(i).getReelX() * 1e3) / 1e3);
                    points.getPoint(i).getyView().setText("" + Math.round(points.getPoint(i).getReelY() * 1e3) / 1e3);
                }
            }
        }

        if (startNOOP > 0) {
            for (int j = 0; j < points.getCount(); j++) {
                if (getPointPlacedLog(j) == 1) {
                    points.getPoint(j).setIsAvailable(true);
                    points.getPoint(j).setSwitchOnOff(true);
                    break;
                }
            }
        }else {
            points.getPoint(0).setIsAvailable(true);
            points.getPoint(0).setSwitchOnOff(true);
        }

//        points.getPoint(0).setIsAvailable(true);
//        points.getPoint(0).setSwitchOnOff(true);

        ApproxDegreeBar.setMax(points.getNOPP() - 1);
        ApproxDegreeBar.setProgress(Integer.parseInt(logSaveVec[32]));
        if (ApproxSwitch.isChecked()){
            ApproxDegreeBar.setEnabled(true);
            DegreeApproxView.setText(logSaveVec[32]);
        }
        else{
            ApproxDegreeBar.setEnabled(false);
            DegreeApproxView.setText("");
        }


        // Einlesen und Einstellen der Seekbar
        ApproxDegreeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                if (seekBar.getMax() != 0 && myCanvas.getApproxSwitch().isChecked()) {
                    myCanvas.getApproxView().setText("" + seekBar.getProgress());

                    // Berechnen der Approximierenden
                    // Anlegen eines Polynomes mit entsprechend Grad
                    myCanvas.setApproximant(new NewtonPolynom(seekBar.getProgress()));

                    // Anlegen der x-, und y-Vektoren mit den Koordinaten zum Approximieren
                    double[] xCoord = new double[points.getNOPP()];
                    double[] yCoord = new double[points.getNOPP()];
                    int k = 0;

                    // Füllen der oben genannten Vektoren
                    for (int i = 0; i < points.getCount(); i++) {
                        if (points.getPoint(i).getIsPlaced()) {
                            xCoord[k] = points.getPoint(i).getReelX();
                            yCoord[k] = points.getPoint(i).getReelY();
                            k++;
                        }
                    }
                    // Approximieren, bzw. Kooeffizienten berechnen
                    myCanvas.getApproximant().lsApproximation(xCoord, yCoord);
                    myCanvas.clearApproxFunc();
                    coordSystem.setyApprox(myCanvas.getApproximant());
                    coordSystem.drawApprox();

                    // Fehler berechnen (L2, MAX)
                    NewtonCotes newtonCotes = new NewtonCotes(3);
                    double temp = 0;
                    if (coordSystem.getFunction() instanceof RungeFunction) {
                        temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                        if (temp > 1) {
                            coordSystem.getL2ApproxView().setTextColor(Color.RED);
                        } else if (temp <= 1 && temp > 0.5) {
                            coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getL2ApproxView().setText("" + temp);

                        temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                        if (temp > 1) {
                            coordSystem.getMAXApproxView().setTextColor(Color.RED);
                        } else if (temp <= 1 && temp > 0.5) {
                            coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getMAXApproxView().setText("" + temp);
                    } else if (coordSystem.getFunction() instanceof AbsFunction) {
                        temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                        if (temp > 0.3) {
                            coordSystem.getL2ApproxView().setTextColor(Color.RED);
                        } else if (temp <= 0.3 && temp > 0.07) {
                            coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getL2ApproxView().setText("" + temp);

                        temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                        if (temp > 0.5) {
                            coordSystem.getMAXApproxView().setTextColor(Color.RED);
                        } else if (temp <= 0.5 && temp > 0.1) {
                            coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getMAXApproxView().setText("" + temp);
                    } else if (coordSystem.getFunction() instanceof TrigonomialFunction) {
                        temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                        if (temp > 1) {
                            coordSystem.getL2ApproxView().setTextColor(Color.RED);
                        } else if (temp <= 1 && temp > 0.09) {
                            coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getL2ApproxView().setText("" + temp);

                        temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
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
                freeMemory();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (seekBar.getMax() != 0 && myCanvas.getApproxSwitch().isChecked()) {
                    myCanvas.getApproxView().setText("" + seekBar.getProgress());

                    // Berechnen der Approximierenden
                    // Anlegen eines Polynomes mit entsprechend Grad
                    myCanvas.setApproximant(new NewtonPolynom(seekBar.getProgress()));

                    // Anlegen der x-, und y-Vektoren mit den Koordinaten zum Approximieren
                    double[] xCoord = new double[points.getNOPP()];
                    double[] yCoord = new double[points.getNOPP()];
                    int k = 0;

                    // Füllen der oben genannten Vektoren
                    for (int i = 0; i < points.getCount(); i++) {
                        if (points.getPoint(i).getIsPlaced()) {
                            xCoord[k] = points.getPoint(i).getReelX();
                            yCoord[k] = points.getPoint(i).getReelY();
                            k++;
                        }
                    }
                    // Approximieren, bzw. Kooeffizienten berechnen
                    myCanvas.getApproximant().lsApproximation(xCoord, yCoord);
                    myCanvas.clearApproxFunc();
                    coordSystem.setyApprox(myCanvas.getApproximant());
                    coordSystem.drawApprox();

                    // Fehler berechnen (L2, MAX)
                    NewtonCotes newtonCotes = new NewtonCotes(3);
                    double temp = 0;
                    if (coordSystem.getFunction() instanceof RungeFunction) {
                        temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                        if (temp > 1) {
                            coordSystem.getL2ApproxView().setTextColor(Color.RED);
                        } else if (temp <= 1 && temp > 0.5) {
                            coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getL2ApproxView().setText("" + temp);

                        temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                        if (temp > 1) {
                            coordSystem.getMAXApproxView().setTextColor(Color.RED);
                        } else if (temp <= 1 && temp > 0.5) {
                            coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getMAXApproxView().setText("" + temp);
                    } else if (coordSystem.getFunction() instanceof AbsFunction) {
                        temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                        if (temp > 0.3) {
                            coordSystem.getL2ApproxView().setTextColor(Color.RED);
                        } else if (temp <= 0.3 && temp > 0.07) {
                            coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getL2ApproxView().setText("" + temp);

                        temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                        if (temp > 0.5) {
                            coordSystem.getMAXApproxView().setTextColor(Color.RED);
                        } else if (temp <= 0.5 && temp > 0.1) {
                            coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getMAXApproxView().setText("" + temp);
                    } else if (coordSystem.getFunction() instanceof TrigonomialFunction) {
                        temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                        if (temp > 1) {
                            coordSystem.getL2ApproxView().setTextColor(Color.RED);
                        } else if (temp <= 1 && temp > 0.09) {
                            coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getL2ApproxView().setText("" + temp);

                        temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                        if (temp > 1) {
                            coordSystem.getMAXApproxView().setTextColor(Color.RED);
                        } else if (temp <= 1 && temp > 0.09) {
                            coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getMAXApproxView().setText("" + temp);
                    }

                    freeMemory();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getMax() != 0 && myCanvas.getApproxSwitch().isChecked()) {
                    myCanvas.getApproxView().setText("" + seekBar.getProgress());

                    // Berechnen der Approximierenden
                    // Anlegen eines Polynomes mit entsprechend Grad
                    myCanvas.setApproximant(new NewtonPolynom(seekBar.getProgress()));

                    // Anlegen der x-, und y-Vektoren mit den Koordinaten zum Approximieren
                    double[] xCoord = new double[points.getNOPP()];
                    double[] yCoord = new double[points.getNOPP()];
                    int k = 0;

                    // Füllen der oben genannten Vektoren
                    for (int i = 0; i < points.getCount(); i++) {
                        if (points.getPoint(i).getIsPlaced()) {
                            xCoord[k] = points.getPoint(i).getReelX();
                            yCoord[k] = points.getPoint(i).getReelY();
                            k++;
                        }
                    }
                    // Approximieren, bzw. Kooeffizienten berechnen
                    myCanvas.getApproximant().lsApproximation(xCoord, yCoord);
                    myCanvas.clearApproxFunc();
                    coordSystem.setyApprox(myCanvas.getApproximant());
                    coordSystem.drawApprox();

                    // Fehler berechnen (L2, MAX)
                    NewtonCotes newtonCotes = new NewtonCotes(3);
                    double temp = 0;
                    if (coordSystem.getFunction() instanceof RungeFunction) {
                        temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                        if (temp > 1) {
                            coordSystem.getL2ApproxView().setTextColor(Color.RED);
                        } else if (temp <= 1 && temp > 0.5) {
                            coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getL2ApproxView().setText("" + temp);

                        temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                        if (temp > 1) {
                            coordSystem.getMAXApproxView().setTextColor(Color.RED);
                        } else if (temp <= 1 && temp > 0.5) {
                            coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getMAXApproxView().setText("" + temp);
                    } else if (coordSystem.getFunction() instanceof AbsFunction) {
                        temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                        if (temp > 0.3) {
                            coordSystem.getL2ApproxView().setTextColor(Color.RED);
                        } else if (temp <= 0.3 && temp > 0.07) {
                            coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getL2ApproxView().setText("" + temp);

                        temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                        if (temp > 0.5) {
                            coordSystem.getMAXApproxView().setTextColor(Color.RED);
                        } else if (temp <= 0.5 && temp > 0.1) {
                            coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getMAXApproxView().setText("" + temp);
                    } else if (coordSystem.getFunction() instanceof TrigonomialFunction) {
                        temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                        if (temp > 1) {
                            coordSystem.getL2ApproxView().setTextColor(Color.RED);
                        } else if (temp <= 1 && temp > 0.09) {
                            coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getL2ApproxView().setText("" + temp);

                        temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
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

                freeMemory();
            }
        });


        // EIN/AUS schalten der Approximation, wenn aktiviert wird, wird direkt gezeichnet und die Fehler berechnet, wenn deaktiviert, wird die Approximierenden, Fehler etc gelöscht
        InterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && points.getNOPP() != 0) {
                    double[] xCoord;
                    double[] yCoord;

                    // Berechnen der Interpolierenden
                    // Anlegen eines Polynomes mit Grad entsprechend der gesetzten Punkte
                    myCanvas.setInterpolant(new NewtonPolynom(points.getNOPP() - 1));

                    // Anlegen der x-, und y-Vektoren mit den Koordinaten zum Interpolieren
                    xCoord = new double[points.getNOPP()];
                    yCoord = new double[points.getNOPP()];
                    int k = 0;

                    // Füllen der oben genannten Vektoren
                    for (int i = 0; i < points.getCount(); i++) {
                        if (points.getPoint(i).getIsPlaced()) {
                            xCoord[k] = points.getPoint(i).getReelX();
                            yCoord[k] = points.getPoint(i).getReelY();
                            k++;
                        }
                    }

                    // Interpolieren, bzw. Kooeffizienten berechnen
                    myCanvas.getInterpolant().interpolate(xCoord, yCoord);
                    myCanvas.clearInterFunc();
                    coordSystem.setyInter(myCanvas.getInterpolant());
                    coordSystem.drawInter();


                    NewtonCotes newtonCotes = new NewtonCotes(3);
                    double temp = 0;
                    if (coordSystem.getFunction() instanceof RungeFunction) {
                        temp = Math.round(newtonCotes.integrate_LP(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                        if (temp > 1) {
                            coordSystem.getL2InterView().setTextColor(Color.RED);
                        } else if (temp <= 1 && temp > 0.5) {
                            coordSystem.getL2InterView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getL2InterView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getL2InterView().setText("" + temp);

                        temp = Math.round(newtonCotes.integrate_infty(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                        if (temp > 1) {
                            coordSystem.getMAXInterView().setTextColor(Color.RED);
                        } else if (temp <= 1 && temp > 0.5) {
                            coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getMAXInterView().setText("" + temp);
                    } else if (coordSystem.getFunction() instanceof AbsFunction) {
                        temp = Math.round(newtonCotes.integrate_LP(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                        if (temp > 0.3) {
                            coordSystem.getL2InterView().setTextColor(Color.RED);
                        } else if (temp <= 0.3 && temp > 0.07) {
                            coordSystem.getL2InterView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getL2InterView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getL2InterView().setText("" + temp);

                        temp = Math.round(newtonCotes.integrate_infty(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                        if (temp > 0.5) {
                            coordSystem.getMAXInterView().setTextColor(Color.RED);
                        } else if (temp <= 0.5 && temp > 0.1) {
                            coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getMAXInterView().setText("" + temp);
                    } else if (coordSystem.getFunction() instanceof TrigonomialFunction) {
                        temp = Math.round(newtonCotes.integrate_LP(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                        if (temp > 1) {
                            coordSystem.getL2InterView().setTextColor(Color.RED);
                        } else if (temp <= 1 && temp > 0.09) {
                            coordSystem.getL2InterView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getL2InterView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getL2InterView().setText("" + temp);

                        temp = Math.round(newtonCotes.integrate_infty(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                        if (temp > 1) {
                            coordSystem.getMAXInterView().setTextColor(Color.RED);
                        } else if (temp <= 1 && temp > 0.09) {
                            coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getMAXInterView().setText("" + temp);
                    }
                    myCanvas.getInterView().setText("" + (points.getNOPP() - 1));
                } else {
                    myCanvas.clearInterFunc();
                    coordSystem.getL2InterView().setText("");
                    coordSystem.getMAXInterView().setText("");
                    myCanvas.getInterView().setText("");
                }

                freeMemory();
            }
        });


        // EIN/AUS schalten der Approximation, wenn aktiviert wird, wird direkt gezeichnet und die Fehler berechnet, wenn deaktiviert, wird die Approximierenden, Fehler etc gelöscht
        ApproxSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (points.getNOPP() > 0 ) {
                        ApproxDegreeBar.setEnabled(true);
                    }
                    if (points.getNOPP() != 0) {
                        myCanvas.getApproxView().setText("" + ApproxDegreeBar.getProgress());

                        // Berechnen der Approximierenden
                        // Anlegen eines Polynomes mit entsprechend Grad
                        myCanvas.setApproximant(new NewtonPolynom(ApproxDegreeBar.getProgress()));

                        // Anlegen der x-, und y-Vektoren mit den Koordinaten zum Approximieren
                        double[] xCoord = new double[points.getNOPP()];
                        double[] yCoord = new double[points.getNOPP()];
                        int k = 0;

                        // Füllen der oben genannten Vektoren
                        for (int i = 0; i < points.getCount(); i++) {
                            if (points.getPoint(i).getIsPlaced()) {
                                xCoord[k] = points.getPoint(i).getReelX();
                                yCoord[k] = points.getPoint(i).getReelY();
                                k++;
                            }
                        }
                        // Approximieren, bzw. Kooeffizienten berechnen
                        myCanvas.getApproximant().lsApproximation(xCoord, yCoord);
                        myCanvas.clearApproxFunc();
                        coordSystem.setyApprox(myCanvas.getApproximant());
                        coordSystem.drawApprox();

                        // Fehler berechnen (L2, MAX)
                        NewtonCotes newtonCotes = new NewtonCotes(3);
                        double temp = 0;
                        if (coordSystem.getFunction() instanceof RungeFunction) {
                            temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                            if (temp > 1) {
                                coordSystem.getL2ApproxView().setTextColor(Color.RED);
                            } else if (temp <= 1 && temp > 0.5) {
                                coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                            } else {
                                coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                            }
                            coordSystem.getL2ApproxView().setText("" + temp);

                            temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                            if (temp > 1) {
                                coordSystem.getMAXApproxView().setTextColor(Color.RED);
                            } else if (temp <= 1 && temp > 0.5) {
                                coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                            } else {
                                coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                            }
                            coordSystem.getMAXApproxView().setText("" + temp);
                        } else if (coordSystem.getFunction() instanceof AbsFunction) {
                            temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                            if (temp > 0.3) {
                                coordSystem.getL2ApproxView().setTextColor(Color.RED);
                            } else if (temp <= 0.3 && temp > 0.07) {
                                coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                            } else {
                                coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                            }
                            coordSystem.getL2ApproxView().setText("" + temp);

                            temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                            if (temp > 0.5) {
                                coordSystem.getMAXApproxView().setTextColor(Color.RED);
                            } else if (temp <= 0.5 && temp > 0.1) {
                                coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                            } else {
                                coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                            }
                            coordSystem.getMAXApproxView().setText("" + temp);
                        } else if (coordSystem.getFunction() instanceof TrigonomialFunction) {
                            temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                            if (temp > 1) {
                                coordSystem.getL2ApproxView().setTextColor(Color.RED);
                            } else if (temp <= 1 && temp > 0.09) {
                                coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                            } else {
                                coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                            }
                            coordSystem.getL2ApproxView().setText("" + temp);

                            temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                            if (temp > 1) {
                                coordSystem.getMAXApproxView().setTextColor(Color.RED);
                            } else if (temp <= 1 && temp > 0.09) {
                                coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                            } else {
                                coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                            }
                            coordSystem.getMAXApproxView().setText("" + temp);
                        }
                        DegreeApproxView.setText("" + ApproxDegreeBar.getProgress());
                    }

                } else {
                    ApproxDegreeBar.setEnabled(false);
                    myCanvas.clearApproxFunc();
                    coordSystem.getL2ApproxView().setText("");
                    coordSystem.getMAXApproxView().setText("");
                    myCanvas.getApproxView().setText("");
                }

                freeMemory();
            }
        });


        // EIN/AUS schalten der Approximation, wenn aktiviert wird, wird direkt gezeichnet und die Fehler berechnet, wenn deaktiviert, wird die Approximierenden, Fehler etc gelöscht
        myCanvas.setHermitSwitch(HermitSwtich);
        HermitSwtich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (points.getNOPP() >= 2) {
                        // Sortieren der Punkte
                        points.sortPoints();

                        // Berechnen der Approximierenden
                        // Anlegen eines Hermit Interpolationspolynom
                        myCanvas.setHermitFunction(new HermitFunction());
                        myCanvas.getHermitFunction().setPoints(points);
                        myCanvas.getHermitFunction().setCoordSystem(coordSystem);

                        if (points.getNOPP() >= 2) {
                            // Hermit Interpolierenden berechnen
                            myCanvas.getHermitFunction().hermitinterpolate();

                            // Alte HermitIntepolierende löschen
                            myCanvas.clearHermitFunc();

                            // Setzen der y-Koordinate der HermitIntepolierenden
                            coordSystem.setyHermit(myCanvas.getHermitFunction());

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
                                        L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                    } else {
                                        L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, points.getPoint(next).getReelX(), 2, 100) * 1e6) / 1e6;
                                        current = next;
                                        next = points.getNextPointNumber(next);
                                    }
                                }
                                if (i > 0 && i < points.getNOPP() - 2) {
                                    L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), points.getPoint(next).getReelX(), 2, 100) * 1e6) / 1e6;
                                    current = next;
                                    next = points.getNextPointNumber(next);
                                }
                                if (i == points.getNOPP() - 2 && i > 0) {
                                    L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), end, 2, 100) * 1e6) / 1e6;
                                }
                            }

                            // MAX
                            current = points.getFirstPointNumber();
                            next = points.getNextPointNumber(points.getFirstPointNumber());

                            for (int i = 0; i < points.getNOPP() - 1; i++) {
                                if (i == 0) {
                                    if (points.getNOPP() == 2) {
                                        MaxErrorHermit = Math.round(newtonCotes.integrate_infty(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                    } else {
                                        MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, points.getPoint(next).getReelX(), 100) * 1e6) / 1e6;
                                        current = next;
                                        next = points.getNextPointNumber(next);
                                    }
                                }
                                if (i > 0 && i < points.getNOPP() - 2) {
                                    MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), points.getPoint(next).getReelX(), 100) * 1e6) / 1e6;
                                    current = next;
                                    next = points.getNextPointNumber(next);
                                }
                                if (i == points.getNOPP() - 2 && i > 0) {
                                    MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(current).getReelX(), end, 100) * 1e6) / 1e6;
                                }
                            }
                            if (points.getNOPP() != 2) {
                                MaxErrorHermit = NumLinAlg.getMax(MaxErrorsHermit);
                            }
                            coordSystem.getMAXHermitView().setText("" + Math.round(MaxErrorHermit * 1e6) / 1e6);
                            coordSystem.getL2HermitView().setText("" + Math.round(L2ErrorHermit * 1e6) / 1e6);
                        }
                    }
                } else {
                    myCanvas.clearHermitFunc();
                    coordSystem.getL2HermitView().setText("");
                    coordSystem.getMAXHermitView().setText("");
                }
            }
        });

        for (int j = 0; j < n; j++) {
            points.getPoint(j).getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String ID = "" + buttonView;
                    int current = Integer.parseInt(ID.substring(ID.indexOf("switch") + 6, ID.length() - 1));
                    if (isChecked) {
                        points.setSwitchOn(current);
                        points.getPoint(current).setActive(true);
                        myCanvas.invalidate();
                    } else {
                        for (int i = 0; i < points.getCount(); i++) {
                            points.getPoint(i).setActive(false);
                            myCanvas.invalidate();
                        }
                    }
                }
            });
        }

        // Funtkion über einen Spinner wählen
        functionSelecter = (Spinner) findViewById(R.id.FunctionSelecter);

        ArrayAdapter<CharSequence> adapter_SpinnerDimension = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, functions);
        // Specify the layout to use when the list of choices appears
        adapter_SpinnerDimension.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter_SpinnerDimension
        //to the spinner
        functionSelecter.setAdapter(adapter_SpinnerDimension);

        functionSelecter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
//                        points.getPoint(0).getDeleteButton().setVisibility(View.VISIBLE);
//                        points.getPoint(0).getSwitch().setVisibility(View.VISIBLE);
//                        points.getPoint(0).getxView().setVisibility(View.VISIBLE);
//                        points.getPoint(0).getyView().setVisibility(View.VISIBLE);
                        break;

                    case 1:
                        functions[0] = "RUNGE FUNKTION";
                        myCanvas.getInterSwitch().setChecked(false);
                        myCanvas.getApproxSwitch().setChecked(false);
                        myCanvas.getHermitSwitch().setChecked(false);
                        coordSystem.getL2InterView().setText("");
                        coordSystem.getMAXInterView().setText("");
                        coordSystem.getL2ApproxView().setText("");
                        coordSystem.getMAXApproxView().setText("");
                        myCanvas.getInterView().setText("");
                        myCanvas.clearPoints();
                        myCanvas.clearFunc();
                        myCanvas.clearApproxFunc();
                        myCanvas.clearHermitFunc();
                        myCanvas.clearInterFunc();
                        myCanvas.setInterpolant(new NewtonPolynom());
                        ApproxDegreeBar.setMax(0);
                        ApproxDegreeBar.setProgress(0);

                        for (int i = 1; i < points.getCount(); i++) {
                            points.removePoint(i);
                        }

                        points.getPoint(0).getxView().setText("");
                        points.getPoint(0).getyView().setText("");
                        points.getPoint(0).setActive(true);
                        points.getPoint(0).getSwitch().setChecked(true);
                        points.getPoint(0).getDeleteButton().setVisibility(View.VISIBLE);
                        points.getPoint(0).getSwitch().setVisibility(View.VISIBLE);
                        points.getPoint(0).getxView().setVisibility(View.VISIBLE);
                        points.getPoint(0).getyView().setVisibility(View.VISIBLE);

                        addButton.setVisibility(View.VISIBLE);

                        start = -5;
                        end = 5;

                        myCanvas.setEnd(end);
                        myCanvas.setStart(start);
                        // Testweise setzen der x-Werte
                        coordSystem.setxFunc(start, end);
                        coordSystem.setyFunc(rungeFunction);
                        coordSystem.drawAxis();
                        coordSystem.drawFunc();
                        myCanvas.setCoordSystem(coordSystem);
                        logSaveVec[34] = "0";
                        break;
                    case 2:
                        functions[0] = "SINUS FUNKTION";
                        myCanvas.getInterSwitch().setChecked(false);
                        myCanvas.getApproxSwitch().setChecked(false);
                        myCanvas.getHermitSwitch().setChecked(false);
                        coordSystem.getL2InterView().setText("");
                        coordSystem.getMAXInterView().setText("");
                        coordSystem.getL2ApproxView().setText("");
                        coordSystem.getMAXApproxView().setText("");
                        myCanvas.getInterView().setText("");
                        myCanvas.clearPoints();
                        myCanvas.clearFunc();
                        myCanvas.clearApproxFunc();
                        myCanvas.clearHermitFunc();
                        myCanvas.clearInterFunc();
                        myCanvas.setInterpolant(new NewtonPolynom());
                        ApproxDegreeBar.setMax(0);
                        ApproxDegreeBar.setProgress(0);

                        for (int i = 1; i < points.getCount(); i++) {
                            points.removePoint(i);
                        }

                        points.getPoint(0).getxView().setText("");
                        points.getPoint(0).getyView().setText("");
                        points.getPoint(0).setActive(true);
                        points.getPoint(0).getSwitch().setChecked(true);
                        points.getPoint(0).getDeleteButton().setVisibility(View.VISIBLE);
                        points.getPoint(0).getSwitch().setVisibility(View.VISIBLE);
                        points.getPoint(0).getxView().setVisibility(View.VISIBLE);
                        points.getPoint(0).getyView().setVisibility(View.VISIBLE);

                        addButton.setVisibility(View.VISIBLE);

                        start = -1;
                        end = 1;

                        myCanvas.setEnd(end);
                        myCanvas.setStart(start);
                        // Testweise setzen der x-Werte
                        coordSystem.setxFunc(start, end);
                        coordSystem.setyFunc(trigonomialFunction);
                        coordSystem.drawAxis();
                        coordSystem.drawFunc();
                        myCanvas.setCoordSystem(coordSystem);
                        logSaveVec[34] = "1";
                        break;
                    case 3:
                        functions[0] = "BETRAGS FUNKTION";
                        myCanvas.getInterSwitch().setChecked(false);
                        myCanvas.getApproxSwitch().setChecked(false);
                        myCanvas.getHermitSwitch().setChecked(false);
                        coordSystem.getL2InterView().setText("");
                        coordSystem.getMAXInterView().setText("");
                        coordSystem.getL2ApproxView().setText("");
                        coordSystem.getMAXApproxView().setText("");
                        myCanvas.getInterView().setText("");
                        myCanvas.clearPoints();
                        myCanvas.clearFunc();
                        myCanvas.clearApproxFunc();
                        myCanvas.clearHermitFunc();
                        myCanvas.clearInterFunc();
                        myCanvas.setInterpolant(new NewtonPolynom());
                        ApproxDegreeBar.setMax(0);
                        ApproxDegreeBar.setProgress(0);

                        for (int i = 1; i < points.getCount(); i++) {
                            points.removePoint(i);
                        }

                        points.getPoint(0).getxView().setText("");
                        points.getPoint(0).getyView().setText("");
                        points.getPoint(0).setActive(true);
                        points.getPoint(0).getSwitch().setChecked(true);
                        points.getPoint(0).getDeleteButton().setVisibility(View.VISIBLE);
                        points.getPoint(0).getSwitch().setVisibility(View.VISIBLE);
                        points.getPoint(0).getxView().setVisibility(View.VISIBLE);
                        points.getPoint(0).getyView().setVisibility(View.VISIBLE);

                        addButton.setVisibility(View.VISIBLE);

                        start = -1;
                        end = 1;

                        myCanvas.setEnd(end);
                        myCanvas.setStart(start);
                        // Testweise setzen der x-Werte
                        coordSystem.setxFunc(start, end);
                        coordSystem.setyFunc(absFunction);
                        coordSystem.drawAxis();
                        coordSystem.drawFunc();
                        myCanvas.setCoordSystem(coordSystem);
                        logSaveVec[34] = "2";
                        break;
                }
                functionSelecter.setSelection(0);
                myCanvas.invalidate();
                freeMemory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        if (points.getNOPP() > 0) {
            double[] xCoord;
            double[] yCoord;
            int k = 0;
            if (myCanvas.getInterSwitch().isChecked()) {
                // Berechnen der Interpolierenden
                // Anlegen eines Polynomes mit Grad entsprechend der gesetzten Punkte
                myCanvas.setInterpolant(new NewtonPolynom(points.getNOPP() - 1));

                // Anlegen der x-, und y-Vektoren mit den Koordinaten zum Interpolieren
                xCoord = new double[points.getNOPP()];
                yCoord = new double[points.getNOPP()];

                // Füllen der oben genannten Vektoren
                for (int i = 0; i < points.getCount(); i++) {
                    if (points.getPoint(i).getIsPlaced()) {
                        xCoord[k] = points.getPoint(i).getReelX();
                        yCoord[k] = points.getPoint(i).getReelY();
                        k++;
                    }
                }

                // Interpolieren, bzw. Kooeffizienten berechnen
                myCanvas.getInterpolant().interpolate(xCoord, yCoord);

                coordSystem.setyInter(myCanvas.getInterpolant());
                myCanvas.clearInterFunc();
                coordSystem.drawInter();

                NewtonCotes newtonCotes = new NewtonCotes(3);
                double temp = 0;
                if (coordSystem.getFunction() instanceof RungeFunction) {
                    temp = Math.round(newtonCotes.integrate_LP(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                    if (temp > 1) {
                        coordSystem.getL2InterView().setTextColor(Color.RED);
                    } else if (temp <= 1 && temp > 0.5) {
                        coordSystem.getL2InterView().setTextColor(Color.BLACK);
                    } else {
                        coordSystem.getL2InterView().setTextColor(Color.GREEN);
                    }
                    coordSystem.getL2InterView().setText("" + temp);

                    temp = Math.round(newtonCotes.integrate_infty(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                    if (temp > 1) {
                        coordSystem.getMAXInterView().setTextColor(Color.RED);
                    } else if (temp <= 1 && temp > 0.5) {
                        coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                    } else {
                        coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                    }
                    coordSystem.getMAXInterView().setText("" + temp);
                } else if (coordSystem.getFunction() instanceof AbsFunction) {
                    temp = Math.round(newtonCotes.integrate_LP(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                    if (temp > 0.3) {
                        coordSystem.getL2InterView().setTextColor(Color.RED);
                    } else if (temp <= 0.3 && temp > 0.07) {
                        coordSystem.getL2InterView().setTextColor(Color.BLACK);
                    } else {
                        coordSystem.getL2InterView().setTextColor(Color.GREEN);
                    }
                    coordSystem.getL2InterView().setText("" + temp);

                    temp = Math.round(newtonCotes.integrate_infty(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                    if (temp > 0.5) {
                        coordSystem.getMAXInterView().setTextColor(Color.RED);
                    } else if (temp <= 0.5 && temp > 0.1) {
                        coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                    } else {
                        coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                    }
                    coordSystem.getMAXInterView().setText("" + temp);
                } else if (coordSystem.getFunction() instanceof TrigonomialFunction) {
                    temp = Math.round(newtonCotes.integrate_LP(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                    if (temp > 1) {
                        coordSystem.getL2InterView().setTextColor(Color.RED);
                    } else if (temp <= 1 && temp > 0.09) {
                        coordSystem.getL2InterView().setTextColor(Color.BLACK);
                    } else {
                        coordSystem.getL2InterView().setTextColor(Color.GREEN);
                    }
                    coordSystem.getL2InterView().setText("" + temp);

                    temp = Math.round(newtonCotes.integrate_infty(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                    if (temp > 1) {
                        coordSystem.getMAXInterView().setTextColor(Color.RED);
                    } else if (temp <= 1 && temp > 0.09) {
                        coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                    } else {
                        coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                    }
                    coordSystem.getMAXInterView().setText("" + temp);
                }
                myCanvas.getInterView().setText("" + (points.getNOPP() - 1));
            }
            // Approximieren
            if (myCanvas.getApproxSwitch().isChecked()) {
                ApproxDegreeBar.setEnabled(true);
                myCanvas.setApproximant(new NewtonPolynom(ApproxDegreeBar.getProgress()));
                // Anlegen der x-, und y-Vektoren mit den Koordinaten zum Approximieren
                xCoord = new double[points.getNOPP()];
                yCoord = new double[points.getNOPP()];
                k = 0;

                // Füllen der oben genannten Vektoren
                for (int i = 0; i < points.getCount(); i++) {
                    if (points.getPoint(i).getIsPlaced()) {
                        xCoord[k] = points.getPoint(i).getReelX();
                        yCoord[k] = points.getPoint(i).getReelY();
                        k++;
                    }
                }

                // Approximieren, bzw. Kooeffizienten berechnen
                myCanvas.getApproximant().lsApproximation(xCoord, yCoord);
                myCanvas.clearApproxFunc();
                coordSystem.setyApprox(myCanvas.getApproximant());
                coordSystem.drawApprox();

                NewtonCotes newtonCotes = new NewtonCotes(3);
                double temp = 0;
                if (coordSystem.getFunction() instanceof RungeFunction) {
                    temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                    if (temp > 1) {
                        coordSystem.getL2ApproxView().setTextColor(Color.RED);
                    } else if (temp <= 1 && temp > 0.5) {
                        coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                    } else {
                        coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                    }
                    coordSystem.getL2ApproxView().setText("" + temp);

                    temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                    if (temp > 1) {
                        coordSystem.getMAXApproxView().setTextColor(Color.RED);
                    } else if (temp <= 1 && temp > 0.5) {
                        coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                    } else {
                        coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                    }
                    coordSystem.getMAXApproxView().setText("" + temp);
                } else if (coordSystem.getFunction() instanceof AbsFunction) {
                    temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                    if (temp > 0.3) {
                        coordSystem.getL2ApproxView().setTextColor(Color.RED);
                    } else if (temp <= 0.3 && temp > 0.07) {
                        coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                    } else {
                        coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                    }
                    coordSystem.getL2ApproxView().setText("" + temp);

                    temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                    if (temp > 0.5) {
                        coordSystem.getMAXApproxView().setTextColor(Color.RED);
                    } else if (temp <= 0.5 && temp > 0.1) {
                        coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                    } else {
                        coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                    }
                    coordSystem.getMAXApproxView().setText("" + temp);
                } else if (coordSystem.getFunction() instanceof TrigonomialFunction) {
                    temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                    if (temp > 1) {
                        coordSystem.getL2ApproxView().setTextColor(Color.RED);
                    } else if (temp <= 1 && temp > 0.09) {
                        coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                    } else {
                        coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                    }
                    coordSystem.getL2ApproxView().setText("" + temp);

                    temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                    if (temp > 1) {
                        coordSystem.getMAXApproxView().setTextColor(Color.RED);
                    } else if (temp <= 1 && temp > 0.09) {
                        coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                    } else {
                        coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                    }
                    coordSystem.getMAXApproxView().setText("" + temp);
                }
                DegreeApproxView.setText("" + ApproxDegreeBar.getProgress());
            }
            if (myCanvas.getHermitSwitch().isChecked()) {
                if (points.getNOPP() >= 2) {
                    // Sortieren der Punkte
                    points.sortPoints();

                    // Berechnen der Approximierenden
                    // Anlegen eines Hermit Interpolationspolynom
                    myCanvas.setHermitFunction(new HermitFunction());
                    myCanvas.getHermitFunction().setPoints(points);
                    myCanvas.getHermitFunction().setCoordSystem(coordSystem);

                    if (points.getNOPP() >= 2) {
                        // Hermit Interpolierenden berechnen
                        myCanvas.getHermitFunction().hermitinterpolate();

                        // Alte HermitIntepolierende löschen
                        myCanvas.clearHermitFunc();

                        // Setzen der y-Koordinate der HermitIntepolierenden
                        coordSystem.setyHermit(myCanvas.getHermitFunction());

                        // Zeichnen der neuen HermitIntepolierenden
                        coordSystem.drawHermit();

                        //L2, Max Fehler berechnen
                        int aktuell = points.getFirstPointNumber();
                        int next = points.getNextPointNumber(points.getFirstPointNumber());
                        double MaxErrorHermit = 1.0;
                        double[] MaxErrorsHermit = new double[points.getNOPP()];
                        double L2ErrorHermit = 0.0;
                        NewtonCotes newtonCotes = new NewtonCotes(3);
                        //L2
                        for (int i = 0; i < points.getNOPP() - 1; i++) {
                            if (i == 0) {
                                if (points.getNOPP() == 2) {
                                    L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                } else {
                                    L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, points.getPoint(next).getReelX(), 2, 100) * 1e6) / 1e6;
                                    aktuell = next;
                                    next = points.getNextPointNumber(next);
                                }
                            }
                            if (i > 0 && i < points.getNOPP() - 2) {
                                L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(aktuell).getReelX(), points.getPoint(next).getReelX(), 2, 100) * 1e6) / 1e6;
                                aktuell = next;
                                next = points.getNextPointNumber(next);
                            }
                            if (i == points.getNOPP() - 2 && i > 0) {
                                L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(aktuell).getReelX(), end, 2, 100) * 1e6) / 1e6;
                            }
                        }

                        // MAX
                        aktuell = points.getFirstPointNumber();
                        next = points.getNextPointNumber(points.getFirstPointNumber());

                        for (int i = 0; i < points.getNOPP() - 1; i++) {
                            if (i == 0) {
                                if (points.getNOPP() == 2) {
                                    MaxErrorHermit = Math.round(newtonCotes.integrate_infty(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                } else {
                                    MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, points.getPoint(next).getReelX(), 100) * 1e6) / 1e6;
                                    aktuell = next;
                                    next = points.getNextPointNumber(next);
                                }
                            }
                            if (i > 0 && i < points.getNOPP() - 2) {
                                MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(aktuell).getReelX(), points.getPoint(next).getReelX(), 100) * 1e6) / 1e6;
                                aktuell = next;
                                next = points.getNextPointNumber(next);
                            }
                            if (i == points.getNOPP() - 2 && i > 0) {
                                MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(aktuell).getReelX(), end, 100) * 1e6) / 1e6;
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
        } else {
            coordSystem.getL2InterView().setText("");
            coordSystem.getMAXInterView().setText("");
            coordSystem.getL2ApproxView().setText("");
            coordSystem.getMAXApproxView().setText("");
            myCanvas.getInterView().setText("");
        }
        if (points.getNOAP() == points.getCount()) {
            this.addButton.setVisibility(View.GONE);
        }

        freeMemory();
    }

    public void Deleter(View view) {
        String ID = "" + view;
        int current = Integer.parseInt(ID.substring(ID.indexOf("deleter") + 7, ID.length() - 1));

        if (points.getNOAP() != 1) {
            points.removePoint(current);
            myCanvas.clearInterFunc();
            ApproxDegreeBar.setMax(points.getNOPP() - 1);
            if (ApproxDegreeBar.getProgress() > ApproxDegreeBar.getMax()) {
                ApproxDegreeBar.setProgress(ApproxDegreeBar.getMax());
            }

            if (points.getNOPP() > 0) {
                double[] xCoord;
                double[] yCoord;
                int k = 0;
                if (myCanvas.getInterSwitch().isChecked()) {
                    // Berechnen der Interpolierenden
                    // Anlegen eines Polynomes mit Grad entsprechend der gesetzten Punkte
                    myCanvas.setInterpolant(new NewtonPolynom(points.getNOPP() - 1));

                    // Anlegen der x-, und y-Vektoren mit den Koordinaten zum Interpolieren
                    xCoord = new double[points.getNOPP()];
                    yCoord = new double[points.getNOPP()];

                    // Füllen der oben genannten Vektoren
                    for (int i = 0; i < points.getCount(); i++) {
                        if (points.getPoint(i).getIsPlaced()) {
                            xCoord[k] = points.getPoint(i).getReelX();
                            yCoord[k] = points.getPoint(i).getReelY();
                            k++;
                        }
                    }

                    // Interpolieren, bzw. Kooeffizienten berechnen
                    myCanvas.getInterpolant().interpolate(xCoord, yCoord);

                    coordSystem.setyInter(myCanvas.getInterpolant());
                    coordSystem.drawInter();

                    NewtonCotes newtonCotes = new NewtonCotes(3);
                    double temp = 0;
                    if (coordSystem.getFunction() instanceof RungeFunction) {
                        temp = Math.round(newtonCotes.integrate_LP(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                        if (temp > 1) {
                            coordSystem.getL2InterView().setTextColor(Color.RED);
                        } else if (temp <= 1 && temp > 0.5) {
                            coordSystem.getL2InterView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getL2InterView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getL2InterView().setText("" + temp);

                        temp = Math.round(newtonCotes.integrate_infty(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                        if (temp > 1) {
                            coordSystem.getMAXInterView().setTextColor(Color.RED);
                        } else if (temp <= 1 && temp > 0.5) {
                            coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getMAXInterView().setText("" + temp);
                    } else if (coordSystem.getFunction() instanceof AbsFunction) {
                        temp = Math.round(newtonCotes.integrate_LP(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                        if (temp > 0.3) {
                            coordSystem.getL2InterView().setTextColor(Color.RED);
                        } else if (temp <= 0.3 && temp > 0.07) {
                            coordSystem.getL2InterView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getL2InterView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getL2InterView().setText("" + temp);

                        temp = Math.round(newtonCotes.integrate_infty(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                        if (temp > 0.5) {
                            coordSystem.getMAXInterView().setTextColor(Color.RED);
                        } else if (temp <= 0.5 && temp > 0.1) {
                            coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getMAXInterView().setText("" + temp);
                    } else if (coordSystem.getFunction() instanceof TrigonomialFunction) {
                        temp = Math.round(newtonCotes.integrate_LP(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                        if (temp > 1) {
                            coordSystem.getL2InterView().setTextColor(Color.RED);
                        } else if (temp <= 1 && temp > 0.09) {
                            coordSystem.getL2InterView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getL2InterView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getL2InterView().setText("" + temp);

                        temp = Math.round(newtonCotes.integrate_infty(myCanvas.getInterpolant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                        if (temp > 1) {
                            coordSystem.getMAXInterView().setTextColor(Color.RED);
                        } else if (temp <= 1 && temp > 0.09) {
                            coordSystem.getMAXInterView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getMAXInterView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getMAXInterView().setText("" + temp);
                    }
                    myCanvas.getInterView().setText("" + (points.getNOPP() - 1));

                }
                // Approximieren
                if (myCanvas.getApproxSwitch().isChecked()) {
                    myCanvas.setApproximant(new NewtonPolynom(ApproxDegreeBar.getProgress()));
                    // Anlegen der x-, und y-Vektoren mit den Koordinaten zum Approximieren
                    xCoord = new double[points.getNOPP()];
                    yCoord = new double[points.getNOPP()];
                    k = 0;

                    // Füllen der oben genannten Vektoren
                    for (int i = 0; i < points.getCount(); i++) {
                        if (points.getPoint(i).getIsPlaced()) {
                            xCoord[k] = points.getPoint(i).getReelX();
                            yCoord[k] = points.getPoint(i).getReelY();
                            k++;
                        }
                    }

                    // Approximieren, bzw. Kooeffizienten berechnen
                    myCanvas.getApproximant().lsApproximation(xCoord, yCoord);
                    myCanvas.clearApproxFunc();
                    coordSystem.setyApprox(myCanvas.getApproximant());
                    coordSystem.drawApprox();

                    NewtonCotes newtonCotes = new NewtonCotes(3);
                    double temp = 0;
                    if (coordSystem.getFunction() instanceof RungeFunction) {
                        temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                        if (temp > 1) {
                            coordSystem.getL2ApproxView().setTextColor(Color.RED);
                        } else if (temp <= 1 && temp > 0.5) {
                            coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getL2ApproxView().setText("" + temp);

                        temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                        if (temp > 1) {
                            coordSystem.getMAXApproxView().setTextColor(Color.RED);
                        } else if (temp <= 1 && temp > 0.5) {
                            coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getMAXApproxView().setText("" + temp);
                    } else if (coordSystem.getFunction() instanceof AbsFunction) {
                        temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                        if (temp > 0.3) {
                            coordSystem.getL2ApproxView().setTextColor(Color.RED);
                        } else if (temp <= 0.3 && temp > 0.07) {
                            coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getL2ApproxView().setText("" + temp);

                        temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                        if (temp > 0.5) {
                            coordSystem.getMAXApproxView().setTextColor(Color.RED);
                        } else if (temp <= 0.5 && temp > 0.1) {
                            coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getMAXApproxView().setText("" + temp);
                    } else if (coordSystem.getFunction() instanceof TrigonomialFunction) {
                        temp = Math.round(newtonCotes.integrate_LP(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                        if (temp > 1) {
                            coordSystem.getL2ApproxView().setTextColor(Color.RED);
                        } else if (temp <= 1 && temp > 0.09) {
                            coordSystem.getL2ApproxView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getL2ApproxView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getL2ApproxView().setText("" + temp);

                        temp = Math.round(newtonCotes.integrate_infty(myCanvas.getApproximant(), coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                        if (temp > 1) {
                            coordSystem.getMAXApproxView().setTextColor(Color.RED);
                        } else if (temp <= 1 && temp > 0.09) {
                            coordSystem.getMAXApproxView().setTextColor(Color.BLACK);
                        } else {
                            coordSystem.getMAXApproxView().setTextColor(Color.GREEN);
                        }
                        coordSystem.getMAXApproxView().setText("" + temp);
                    }
                    DegreeApproxView.setText("" + ApproxDegreeBar.getProgress());

                }
                if (myCanvas.getHermitSwitch().isChecked()) {
                    if (points.getNOPP() >= 2) {
                        // Sortieren der Punkte
                        points.sortPoints();

                        // Berechnen der Approximierenden
                        // Anlegen eines Hermit Interpolationspolynom
                        myCanvas.setHermitFunction(new HermitFunction());
                        myCanvas.getHermitFunction().setPoints(points);
                        myCanvas.getHermitFunction().setCoordSystem(coordSystem);

                        if (points.getNOPP() >= 2) {
                            // Hermit Interpolierenden berechnen
                            myCanvas.getHermitFunction().hermitinterpolate();

                            // Alte HermitIntepolierende löschen
                            myCanvas.clearHermitFunc();

                            // Setzen der y-Koordinate der HermitIntepolierenden
                            coordSystem.setyHermit(myCanvas.getHermitFunction());

                            // Zeichnen der neuen HermitIntepolierenden
                            coordSystem.drawHermit();

                            //L2, Max Fehler berechnen
                            int aktuell = points.getFirstPointNumber();
                            int next = points.getNextPointNumber(points.getFirstPointNumber());
                            double MaxErrorHermit = 1.0;
                            double[] MaxErrorsHermit = new double[points.getNOPP()];
                            double L2ErrorHermit = 0.0;
                            NewtonCotes newtonCotes = new NewtonCotes(3);
                            //L2
                            for (int i = 0; i < points.getNOPP() - 1; i++) {
                                if (i == 0) {
                                    if (points.getNOPP() == 2) {
                                        L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, end, 2, 100) * 1e6) / 1e6;
                                    } else {
                                        L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, points.getPoint(next).getReelX(), 2, 100) * 1e6) / 1e6;
                                        aktuell = next;
                                        next = points.getNextPointNumber(next);
                                    }
                                }
                                if (i > 0 && i < points.getNOPP() - 2) {
                                    L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(aktuell).getReelX(), points.getPoint(next).getReelX(), 2, 100) * 1e6) / 1e6;
                                    aktuell = next;
                                    next = points.getNextPointNumber(next);
                                }
                                if (i == points.getNOPP() - 2 && i > 0) {
                                    L2ErrorHermit = L2ErrorHermit + Math.round(newtonCotes.integrate_LP(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(aktuell).getReelX(), end, 2, 100) * 1e6) / 1e6;
                                }
                            }

                            // MAX
                            aktuell = points.getFirstPointNumber();
                            next = points.getNextPointNumber(points.getFirstPointNumber());

                            for (int i = 0; i < points.getNOPP() - 1; i++) {
                                if (i == 0) {
                                    if (points.getNOPP() == 2) {
                                        MaxErrorHermit = Math.round(newtonCotes.integrate_infty(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, end, 100) * 1e6) / 1e6;
                                    } else {
                                        MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), start, points.getPoint(next).getReelX(), 100) * 1e6) / 1e6;
                                        aktuell = next;
                                        next = points.getNextPointNumber(next);
                                    }
                                }
                                if (i > 0 && i < points.getNOPP() - 2) {
                                    MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(aktuell).getReelX(), points.getPoint(next).getReelX(), 100) * 1e6) / 1e6;
                                    aktuell = next;
                                    next = points.getNextPointNumber(next);
                                }
                                if (i == points.getNOPP() - 2 && i > 0) {
                                    MaxErrorsHermit[i] = Math.round(newtonCotes.integrate_infty(myCanvas.getHermitFunction().getInterpolate()[i], coordSystem.getFunction(), points.getPoint(aktuell).getReelX(), end, 100) * 1e6) / 1e6;
                                }
                            }
                            if (points.getNOPP() != 2) {
                                MaxErrorHermit = NumLinAlg.getMax(MaxErrorsHermit);
                            }
                            coordSystem.getMAXHermitView().setText("" + Math.round(MaxErrorHermit * 1e6) / 1e6);
                            coordSystem.getL2HermitView().setText("" + Math.round(L2ErrorHermit * 1e6) / 1e6);

                        }
                    } else {
                        // Alte HermitIntepolierende löschen
                        myCanvas.clearHermitFunc();

                        myCanvas.getHermitSwitch().setChecked(false);
                    }
                }
            } else {
                coordSystem.getL2InterView().setText("");
                coordSystem.getMAXInterView().setText("");
                coordSystem.getL2ApproxView().setText("");
                coordSystem.getMAXApproxView().setText("");
                myCanvas.getInterView().setText("0");
                points.getPoint(current).getxView().setText("");
                points.getPoint(current).getyView().setText("");
            }
        } else {
            myCanvas.deletePoint();
            points.deletePoint(current);
            myCanvas.clearInterFunc();
            myCanvas.clearApproxFunc();
            if (points.getNOAP() != 1) {
                points.setSwitchOn(current);
            }
            coordSystem.getL2InterView().setText("");
            coordSystem.getMAXInterView().setText("");
            coordSystem.getL2ApproxView().setText("");
            coordSystem.getMAXApproxView().setText("");
            points.getPoint(current).getxView().setText("");
            points.getPoint(current).getyView().setText("");
            myCanvas.getInterView().setText("");
        }

        if (points.getNOAP() == 1) {
            int k = -1;
            do {
                k++;
            } while (!points.getPoint(k).getIsAvailable() && k + 1 < points.getCount());
            points.setSwitchOn(k);
        }

        if (points.getNOAP() < points.getCount()) {
            this.addButton.setVisibility(View.VISIBLE);
        }

        if (points.getNOPP() == 0){
            DegreeApproxView.setText("");
            ApproxDegreeBar.setEnabled(false);
        }

        freeMemory();

    }


    public void helperFunctionSelection(View view) {
        // String anlegen, der als Link interpretiert werden kann
        final SpannableString s = new SpannableString("Das hier ist ein Link: https://de.wikipedia.org/wiki/Interpolation_(Mathematik)");
        Linkify.addLinks(s, Linkify.ALL);

        // AlertDialog erzeugen (Popup)
        AlertDialog alertDialog = new AlertDialog.Builder(InterUApprox.this).create();
        alertDialog.setTitle("Interpolation");
        alertDialog.setMessage(s);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Schließen",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

        // Wichtig, um den Link "clickable" zu machen
        ((TextView)alertDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }

}