package com.uni_stuttgart.isl.Integration;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.uni_stuttgart.isl.Function.AbsFunction;
import com.uni_stuttgart.isl.Function.Function;
import com.uni_stuttgart.isl.Function.GaussQuadraturFunction;
import com.uni_stuttgart.isl.Function.PolynomToolbox;
import com.uni_stuttgart.isl.Function.RungeFunction;
import com.uni_stuttgart.isl.Function.SimpsonFunction;
import com.uni_stuttgart.isl.Function.TrigonomialFunction;
import com.uni_stuttgart.isl.Intros.Intro_Integration;
import com.uni_stuttgart.isl.Intros.PrefManager;
import com.uni_stuttgart.isl.MainActivity;
import com.uni_stuttgart.isl.Points.Points;
import com.uni_stuttgart.isl.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Integration extends AppCompatActivity {

    // String zum Speichern aller relevanten Informationen, um bei erneutem öffnen der App fortfahren zu können
    String logSave;
    // logSave in einen Vektor umgewandelt um besser damit umgehen zu können
    String[] logSaveVec;
    // Integrationspunkte
    private Points points;
    // Darstellungsfläche
    private MyCanvas myCanvas;
    // Objekt zum erzeugen eines Koordinaten Systems
    private CoordSystem coordSystem;
    // Button zum erzeugen von neuen Integrationspunkten
    private ImageButton addButton;
    // Intervall auf dem die Funktion geplottete werden soll
    private double start;
    private double end;
    // Welches Koordiantensystem soll gewählt werden (welche Quadranten)
    private int coordArt;
    // Spinner zur Wahl der Funktion
    private Spinner functionSelecter;
    // Darstellungsfelder für die berechneten Integrationsfehler
    private TextView startPointErrorView;
    private TextView middlePointErrorView;
    private TextView trapezErrorView;
    private TextView simpsonErrorView;
    private TextView gaussErrorView;
    private TextView gaussDegreeView;
    // Switchs/Switche um verschiedene Integrationsmethoden zu de-aktivieren
    private Switch startPointSwitch;
    private Switch middelPointSwitch;
    private Switch trapezSwitch;
    private Switch simpsonSwitch;
    private Switch gaussSwitch;

    private SeekBar gaussSeekbar;
    // Hauptfunktion (Funktion über die integriert werden soll)
    private Function mainFunction;
    // Auflistung der verschiedenen Funktionen über die integriert werden kann
    private CharSequence[] functions = {"Funktionen", "Runge Funktion", "Sinus Funktion", "Betrags Funktion"};
    // Funktionen über die integriert werden kann
    private TrigonomialFunction trigonomialFunction = new TrigonomialFunction(1, Math.PI * 2, 0, 1);
    private RungeFunction rungeFunction = new RungeFunction();
    private AbsFunction absFunction = new AbsFunction();
    // Toolbar
    private Toolbar toolbar;
    // Prefmanager für weiterführende Funktionen (Restart, Intros etc...)
    private PrefManager prefManager;
    private int n= 10;
    private int final_size;

    private Paint[] pointPaint = new Paint[n];
    private int startNOOP;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_integ, menu);
        return true;
    }

    // Funktion zum erzeugen den Menüpunktes "oben-rechts"
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        // Bei Auswahl von "Introduction" start von Intro und speichern der aktuellen Konfiguration
        if (item.getItemId() == R.id.introduction) {
            // Beim erneuten
            prefManager.setReopenIntegrationIntro(false);
            saveActivityToLog();
            Intent myIntent = new Intent(Integration.this, Intro_Integration.class);
            Integration.this.startActivity(myIntent);
            freeMemory();
            finish();
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.integration);
        // Erzeugen des Premanagers
        prefManager = new PrefManager(this);
        // Erzeugen der Toolbar
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        // Zurückbutton, was soll beim "Click" passieren
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
                Intent myIntent = new Intent(Integration.this, MainActivity.class);
                Integration.this.startActivity(myIntent);
                freeMemory();
                finish();
            }
        });

        logSave = Read();
        logSaveVec = StringToStringVev(logSave);

        // Festlegen der Farbe, Dicke,... der ...-Punkte
        for (int i = 0; i < n; i++) {
            pointPaint[i] = new Paint();
            pointPaint[i].setAntiAlias(true);
            pointPaint[i].setStyle(Paint.Style.FILL_AND_STROKE);
            pointPaint[i].setStrokeJoin(Paint.Join.ROUND);
            pointPaint[i].setStrokeWidth(5.5F);
        }

        // Farben der Punkte definieren
        pointPaint[0].setColor(Color.rgb(210, 210, 210));       //Grey
        pointPaint[1].setColor(Color.rgb(255, 0, 16));          //Red
        pointPaint[2].setColor(Color.rgb(255, 255, 0));         //Yellow
        pointPaint[3].setColor(Color.rgb(240, 163, 255));       //Amethyst
        pointPaint[4].setColor(Color.rgb(94, 241, 242));        //Sky (Cyan)
        pointPaint[5].setColor(Color.rgb(0, 117, 220));         //Blue
        pointPaint[6].setColor(Color.rgb(25, 25, 25));          //Ebony
        pointPaint[7].setColor(Color.rgb(255, 164, 5));         //Orpiment (Orange)
        pointPaint[8].setColor(Color.rgb(0, 153, 143));         //Türkis
        pointPaint[9].setColor(Color.rgb(43, 206, 72));         //Green

        // Erzeugen der Darstellungsfläche
        myCanvas = (MyCanvas) findViewById(R.id.mycanvas);
        final Button eqbutton = findViewById(R.id.equi);
        final LinearLayout left = findViewById(R.id.left_col);
        final LinearLayout mid = findViewById(R.id.pointcol);
        final LinearLayout opt = findViewById(R.id.optcol);
        // Volles Koordinatensystem
        setCoordArt(4);
        myCanvas.post(new Runnable() {
            @Override
            public void run() {

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;

                ViewGroup.LayoutParams ParamsCanvas = myCanvas.getLayoutParams();
                ViewGroup.LayoutParams Button = eqbutton.getLayoutParams();
                int canvas_size = height-toolbar.getHeight()-eqbutton.getHeight()-20;
                int size_other = mid.getWidth() + opt.getWidth() +20;

                if(canvas_size>(width-size_other-20)) canvas_size=width-size_other-20;

                ParamsCanvas.width = canvas_size;
                ParamsCanvas.height = canvas_size;
                Button.width = canvas_size;
                myCanvas.setLayoutParams(ParamsCanvas);
                eqbutton.setLayoutParams(Button);

                final_size = myCanvas.getLayoutParams().width;

                // Button erzeugen zum hinzufügen von Integrationspunkten
                addButton = (ImageButton) findViewById(R.id.addButton);


                // Anlegen der Punkte
                points = new Points(n);
                // Anlegen des Koordinatensystems
                coordSystem = new CoordSystem((int) final_size / 2, (int) final_size / 2, (int) final_size / 2 - 50, (int) final_size / 2 - 50, coordArt, myCanvas);

                // Koordinatensystem+Integrationspunkte der Darstellungsfläche übergen
                myCanvas.setCoordSystem(coordSystem);
                myCanvas.setPoints(points);

                // Fehleranzeige für das "StartPoint-Verfahren"
                startPointErrorView = (TextView) findViewById(R.id.errorAnfangspunkt);
                coordSystem.setStartPointErrorView(startPointErrorView);

                // Fehleranzeige für das "MiddelPoint-Verfahren"
                middlePointErrorView = (TextView) findViewById(R.id.errorMittelpunkt);
                coordSystem.setMiddlePointErrorView(middlePointErrorView);

                // Fehleranzeige für das "Trapez-Verfahren"
                trapezErrorView = (TextView) findViewById(R.id.errorTrapezregel);
                coordSystem.setTrapezErrorView(trapezErrorView);

                // Fehleranzeige für das "Simpson-Verfahren"
                simpsonErrorView = (TextView) findViewById(R.id.errorSimpsonregel);
                coordSystem.setSimpsonErrorView(simpsonErrorView);

                // Fehleranzeige für die Gauß-Quadratur
                gaussErrorView = (TextView) findViewById(R.id.errorGaussQuadratur);
                coordSystem.setGaussErrorView(gaussErrorView);

                // Switche zum de-aktiveren der verschiedenen Integrationsverfahren
                startPointSwitch = (Switch) findViewById(R.id.startPointSwitch);
                myCanvas.setStartPointSwitch(startPointSwitch);
                middelPointSwitch = (Switch) findViewById(R.id.middelPointSwitch);
                myCanvas.setMiddelPointSwitch(middelPointSwitch);
                trapezSwitch = (Switch) findViewById(R.id.trapezRegel);
                myCanvas.setTrapezSwitch(trapezSwitch);
                simpsonSwitch = (Switch) findViewById(R.id.simpsonRegel);
                myCanvas.setSimpsonSwitch(simpsonSwitch);
                gaussSwitch = (Switch) findViewById(R.id.gaussQuadratur);
                myCanvas.setGaussSwitch(gaussSwitch);

                gaussSeekbar = (SeekBar) findViewById(R.id.gaussSeekbar);
                myCanvas.setGaussSeekbar(gaussSeekbar);

                gaussDegreeView = (TextView) findViewById(R.id.gaussDegree);
                myCanvas.setGaussDegreeView(gaussDegreeView);

                myCanvas.setIs_init();
                loadActivity();
            }
        });
    }

    public void Deleter(View view) {
        String ID = "" + view;
        int current = Integer.parseInt(ID.substring(ID.indexOf("deleter") + 7, ID.length() - 1));

        if (points.getNOAP() != 1) {
            points.removePoint(current);
            points.sortPoints();

            if (points.getNOPP() > 0) {
                double[] xCoord;
                double[] yCoord;
                int k = 0;
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

                int current1;
                int next;

                if (myCanvas.getStartPointSwitch().isChecked() && points.getNOPP() >= 2) {
                    double startPointIntegralValue = 0;

                    current1 = points.getFirstPointNumber();
                    next = points.getNextPointNumber(points.getFirstPointNumber());

                    for (int i = 0; i < points.getNOPP() - 1; i++) {

                        startPointIntegralValue = startPointIntegralValue + mainFunction.evaluate(points.getPoint(current1).getReelX()) * (points.getPoint(next).getReelX() - points.getPoint(current1).getReelX());
                        current1 = next;
                        next = points.getNextPointNumber(next);
                    }

                    coordSystem.getStartPointErrorView().setText("" + Math.round((Math.abs(startPointIntegralValue -
                            mainFunction.antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                            mainFunction.antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);
                }
                if (myCanvas.getMiddelPointSwitch().isChecked() && points.getNOPP() >= 2) {
                    double middelPointIntegralVaulue = 0;

                    current1 = points.getFirstPointNumber();
                    next = points.getNextPointNumber(points.getFirstPointNumber());

                    for (int i = 0; i < points.getNOPP() - 1; i++) {

                        double middelXCoord = 1.0 / 2.0 * (points.getPoint(next).getReelX() + points.getPoint(current1).getReelX());
                        ;
                        middelPointIntegralVaulue = middelPointIntegralVaulue + mainFunction.evaluate(middelXCoord) * (points.getPoint(next).getReelX() - points.getPoint(current1).getReelX());
                        current1 = next;
                        next = points.getNextPointNumber(next);
                    }

                    coordSystem.getMiddelPointErrorView().setText("" + Math.round((Math.abs(middelPointIntegralVaulue -
                            mainFunction.antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                            mainFunction.antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);
                }
                if (myCanvas.getTrapezSwitch().isChecked() && points.getNOPP() >= 2) {
                    points.sortPoints();

                    double trapezIntegralVaulue = 0;

                    current = points.getFirstPointNumber();
                    next = points.getNextPointNumber(points.getFirstPointNumber());

                    for (int i = 0; i < points.getNOPP() - 1; i++) {
                        trapezIntegralVaulue = trapezIntegralVaulue + (points.getPoint(next).getReelX() - points.getPoint(current).getReelX()) * 0.5 * (mainFunction.evaluate(points.getPoint(next).getReelX())
                                + mainFunction.evaluate(points.getPoint(current).getReelX()));
                        current = next;
                        next = points.getNextPointNumber(next);
                    }

                    coordSystem.getTrapezErrorView().setText("" + Math.round((Math.abs(trapezIntegralVaulue -
                            mainFunction.antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                            mainFunction.antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);

                }
                if (simpsonSwitch.isChecked() && points.getNOPP() >= 2) {
                    myCanvas.setSimpsonFunction(new SimpsonFunction());
                    myCanvas.getSimpsonFunction().setPoints(points);
                    myCanvas.getSimpsonFunction().setCoordSystem(coordSystem);
                    myCanvas.getSimpsonFunction().simpsonInterpolate();
                    coordSystem.setySimpson(myCanvas.getSimpsonFunction());
                    myCanvas.clearSimpsonFunc();
                    coordSystem.drawSimpsonFunc();

                    double simpsonIntegralValue = 0;
                    current1 = points.getFirstPointNumber();
                    next = points.getNextPointNumber(points.getFirstPointNumber());
                    for (int i = 0; i < points.getNOPP() - 1; i++) {
                        simpsonIntegralValue = simpsonIntegralValue + myCanvas.getSimpsonFunction().integral(points.getPoint(current1).getReelX(), points.getPoint(next).getReelX());
                        current1 = next;
                        next = points.getNextPointNumber(next);
                    }
                    coordSystem.getSimpsonErrorView().setText("" + Math.round((Math.abs(simpsonIntegralValue -
                            mainFunction.antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                            mainFunction.antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);
                }
                if (gaussSwitch.isChecked() && points.getNOPP() > 1){
                    gaussSeekbar.setEnabled(true);
                    gaussDegreeView.setText("" + gaussSeekbar.getProgress());

                    myCanvas.setGaussQuadraturFunction(new GaussQuadraturFunction());
                    myCanvas.getGaussQuadraturFunction().setPoints(points);
                    myCanvas.getGaussQuadraturFunction().setCoordSystem(coordSystem);

                    coordSystem.getGaussErrorView().setText("" + Math.round((Math.abs(myCanvas.getGaussQuadraturFunction().gaussQuadratur(gaussSeekbar.getProgress()+1)-
                            coordSystem.getFunction().antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                            coordSystem.getFunction().antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);
//                    myCanvas.getGaussQuadraturFunction().gaussQuadraturInterpolate(gaussSeekbar.getProgress());
//                    coordSystem.setyGauss(myCanvas.getGaussQuadraturFunction());
//                    myCanvas.clearGaussQuadraturFunc();
//                    coordSystem.drawGaussQuadraturFunc();
                }
            } else {
                coordSystem.getSimpsonErrorView().setText("");
                points.getPoint(current).getxView().setText("");
                points.getPoint(current).getyView().setText("");
            }
        } else {
            myCanvas.deletePoint();
            points.deletePoint(current);
            simpsonErrorView.setText("");
            points.getPoint(current).getxView().setText("");
            points.getPoint(current).getyView().setText("");

            int k = -1;
            do {
                k++;
            } while (!points.getPoint(k).getIsAvailable() && k + 1 < points.getCount());
            points.setSwitchOn(k);
        }

        if (points.getNOAP() < points.getCount()) {
            addButton.setVisibility(View.VISIBLE);
        }

        if (points.getNOPP() < 2){
            startPointErrorView.setText("");
            middlePointErrorView.setText("");
            trapezErrorView.setText("");
            simpsonErrorView.setText("");
            gaussErrorView.setText("");

        }

        if (gaussSwitch.isChecked() && points.getNOPP() < 2){
            gaussSeekbar.setEnabled(false);
        }

        freeMemory();

        myCanvas.invalidate();

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

        points.sortPoints();

        int current;
        int next;

        if (myCanvas.getStartPointSwitch().isChecked() && points.getNOPP() >= 2) {
            double startPointIntegralValue = 0;

            current = points.getFirstPointNumber();
            next = points.getNextPointNumber(points.getFirstPointNumber());

            for (int i = 0; i < points.getNOPP() - 1; i++) {
                startPointIntegralValue = startPointIntegralValue + mainFunction.evaluate(points.getPoint(current).getReelX()) * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX());
                current = next;
                next = points.getNextPointNumber(next);
            }

            coordSystem.getStartPointErrorView().setText("" + Math.round((Math.abs(startPointIntegralValue -
                    mainFunction.antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                    mainFunction.antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);
        }
        if (myCanvas.getMiddelPointSwitch().isChecked() && points.getNOPP() >= 2) {
            double middelPointIntegralVaulue = 0;

            current = points.getFirstPointNumber();
            next = points.getNextPointNumber(points.getFirstPointNumber());

            for (int i = 0; i < points.getNOPP() - 1; i++) {

                double middelXCoord = 1.0 / 2.0 * (points.getPoint(next).getReelX() + points.getPoint(current).getReelX());
                ;
                middelPointIntegralVaulue = middelPointIntegralVaulue + mainFunction.evaluate(middelXCoord) * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX());
                current = next;
                next = points.getNextPointNumber(next);
            }

            coordSystem.getMiddelPointErrorView().setText("" + Math.round((Math.abs(middelPointIntegralVaulue -
                    mainFunction.antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                    mainFunction.antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);
        }
        if (myCanvas.getTrapezSwitch().isChecked() && points.getNOPP() >= 2) {
            points.sortPoints();

            double trapezIntegralVaulue = 0;

            current = points.getFirstPointNumber();
            next = points.getNextPointNumber(points.getFirstPointNumber());

            for (int i = 0; i < points.getNOPP() - 1; i++) {
                trapezIntegralVaulue = trapezIntegralVaulue + (points.getPoint(next).getReelX() - points.getPoint(current).getReelX()) * 0.5 * (mainFunction.evaluate(points.getPoint(next).getReelX())
                        + mainFunction.evaluate(points.getPoint(current).getReelX()));
                current = next;
                next = points.getNextPointNumber(next);
            }

            coordSystem.getTrapezErrorView().setText("" + Math.round((Math.abs(trapezIntegralVaulue -
                    mainFunction.antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                    mainFunction.antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);

        }
        if (simpsonSwitch.isChecked() && points.getNOPP() >= 2) {
            myCanvas.setSimpsonFunction(new SimpsonFunction());
            myCanvas.getSimpsonFunction().setPoints(points);
            myCanvas.getSimpsonFunction().setCoordSystem(coordSystem);
            myCanvas.getSimpsonFunction().simpsonInterpolate();
            coordSystem.setySimpson(myCanvas.getSimpsonFunction());
            myCanvas.clearSimpsonFunc();
            coordSystem.drawSimpsonFunc();

            double simpsonIntegralValue = 0;
            current = points.getFirstPointNumber();
            next = points.getNextPointNumber(points.getFirstPointNumber());
            for (int i = 0; i < points.getNOPP() - 1; i++) {
                simpsonIntegralValue = simpsonIntegralValue + myCanvas.getSimpsonFunction().integral(points.getPoint(current).getReelX(), points.getPoint(next).getReelX());
                current = next;
                next = points.getNextPointNumber(next);
            }
            coordSystem.getSimpsonErrorView().setText("" + Math.round((Math.abs(simpsonIntegralValue -
                    mainFunction.antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                    mainFunction.antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);
        }

        if (gaussSwitch.isChecked() && points.getNOPP() > 1){
            gaussSeekbar.setEnabled(true);
            gaussDegreeView.setText("" + gaussSeekbar.getProgress());

            myCanvas.setGaussQuadraturFunction(new GaussQuadraturFunction());
            myCanvas.getGaussQuadraturFunction().setPoints(points);
            myCanvas.getGaussQuadraturFunction().setCoordSystem(coordSystem);

            coordSystem.getGaussErrorView().setText("" + Math.round((Math.abs(myCanvas.getGaussQuadraturFunction().gaussQuadratur(gaussSeekbar.getProgress()+1)-
                    coordSystem.getFunction().antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                    coordSystem.getFunction().antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);
//            myCanvas.getGaussQuadraturFunction().gaussQuadraturInterpolate(gaussSeekbar.getProgress());
//            coordSystem.setyGauss(myCanvas.getGaussQuadraturFunction());
//            myCanvas.clearGaussQuadraturFunc();
//            coordSystem.drawGaussQuadraturFunc();
        }

        myCanvas.invalidate();

        for (int i = 0; i < points.getCount(); i++) {
            points.getPoint(i).setActive(false);
            points.getPoint(i).getSwitch().setChecked(false);
        }

        freeMemory();
    }


    public void resetAll(View view) {
        startPointSwitch.setChecked(false);
        middelPointSwitch.setChecked(false);
        trapezSwitch.setChecked(false);
        simpsonSwitch.setChecked(false);
        gaussSwitch.setChecked(false);
        gaussSeekbar.setEnabled(false);
        gaussSeekbar.setProgress(0);

        myCanvas.clearPoints();
        myCanvas.clearFunc();

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

        if (Integer.parseInt(logSaveVec[36]) == 0 || Integer.parseInt(logSaveVec[36]) == -1) {
            start = -5;
            end = 5;
            myCanvas.setEnd(end);
            myCanvas.setStart(start);
            // Testweise setzen der x-Werte
            coordSystem.setxFunc(start, end);
            coordSystem.setyFunc(rungeFunction);
        } else if (Integer.parseInt(logSaveVec[36]) == 1) {
            start = -1;
            end = 1;
            myCanvas.setEnd(end);
            myCanvas.setStart(start);
            // Testweise setzen der x-Werte
            coordSystem.setxFunc(start, end);
            coordSystem.setyFunc(trigonomialFunction);
        } else if (Integer.parseInt(logSaveVec[36]) == 2) {
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

    public int getCoordArt() {
        return coordArt;
    }

    public void setCoordArt(int coordArt) {
        this.coordArt = coordArt;
    }

    public CoordSystem getCoordSystem() {
        return coordSystem;
    }

    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    public final String Read() {

        String logString = "";
        try {
            InputStream inputStream = this.openFileInput("LogIntegration.txt");

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

    public void setPointLog(int pointnumber, int active, double x, double y) {
        logSaveVec[pointnumber * 3] = Integer.toString(active);
        logSaveVec[pointnumber * 3 + 1] = Double.toString(x);
        logSaveVec[pointnumber * 3 + 2] = Double.toString(y);
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

    private void saveActivityToLog() {
        logSaveVec = new String[37];

        for (int i = 0; i < 10; i++) {
            logSaveVec[3 * i] = Integer.toString(getIntFromBoolean(points.getPoint(i).getIsPlaced()));
            logSaveVec[3 * i + 1] = "" + points.getPoint(i).getX();
            logSaveVec[3 * i + 2] = "" + points.getPoint(i).getY();
        }

        logSaveVec[30] = Integer.toString(getIntFromBoolean(startPointSwitch.isChecked()));
        logSaveVec[31] = Integer.toString(getIntFromBoolean(middelPointSwitch.isChecked()));
        logSaveVec[32] = Integer.toString(getIntFromBoolean(trapezSwitch.isChecked()));
        logSaveVec[33] = Integer.toString(getIntFromBoolean(simpsonSwitch.isChecked()));
        logSaveVec[34] = Integer.toString(getIntFromBoolean(gaussSwitch.isChecked()));
        logSaveVec[35] = "" + gaussSeekbar.getProgress();


        if (coordSystem.getFunction() instanceof RungeFunction) {
            logSaveVec[36] = ""+0;
        }
        else if (coordSystem.getFunction() instanceof TrigonomialFunction){
            logSaveVec[36] = ""+1;
        }
        else if(coordSystem.getFunction() instanceof AbsFunction){
            logSaveVec[36] = ""+2;
        }
        Save(StringVecToString(logSaveVec));
    }

    public void Save(String log) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("LogIntegration.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(log);
            outputStreamWriter.close();

        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
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
        prefManager.setIsInterLastActivity(false);
        prefManager.setIsSplitLastActivity(false);
        prefManager.setIsIntegLastActivity(true);
        saveActivityToLog();
        freeMemory();
    }

    public void saveActivityToLogButton(View view) {
        prefManager.setIsIntegrationSaved(true);

        String[] logSaveVecButton = new String[37];
        for (int i = 0; i < 10; i++) {
            logSaveVecButton[3 * i] = Integer.toString(getIntFromBoolean(points.getPoint(i).getIsPlaced()));
            logSaveVecButton[3 * i + 1] = "" + points.getPoint(i).getX();
            logSaveVecButton[3 * i + 2] = "" + points.getPoint(i).getY();
        }

        logSaveVecButton[30] = Integer.toString(getIntFromBoolean(startPointSwitch.isChecked()));
        logSaveVecButton[31] = Integer.toString(getIntFromBoolean(middelPointSwitch.isChecked()));
        logSaveVecButton[32] = Integer.toString(getIntFromBoolean(trapezSwitch.isChecked()));
        logSaveVecButton[33] = Integer.toString(getIntFromBoolean(simpsonSwitch.isChecked()));
        logSaveVecButton[34] = Integer.toString(getIntFromBoolean(gaussSwitch.isChecked()));
        logSaveVecButton[35] = "" + gaussSeekbar.getProgress();

        if (coordSystem.getFunction() instanceof RungeFunction) {
            logSaveVecButton[36] = ""+0;
        }
        else if (coordSystem.getFunction() instanceof TrigonomialFunction){
            logSaveVecButton[36] = ""+1;
        }
        else if(coordSystem.getFunction() instanceof AbsFunction){
            logSaveVecButton[36] = ""+2;
        }

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("LogIntegrationSave.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(StringVecToString(logSaveVecButton));
            outputStreamWriter.close();

        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        Toast.makeText(this, "Aktuelle Konfiguration wurde gespeichert!", Toast.LENGTH_SHORT).show();
    }

    public void loadLogButton(View view) {
        if (prefManager.isIntegrationSaved()) {
            String Temp = ReadSave();
            Save(Temp);
            loadActivity();
            freeMemory();
        }
        else{
            Toast.makeText(this, "Bitte zuerst eine Konfiguration abspeichern!", Toast.LENGTH_SHORT).show();
        }
    }

    public final String ReadSave() {

        String logString = "";
        try {
            InputStream inputStream = this.openFileInput("LogIntegrationSave.txt");

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

    public void StringVecViewer(String[] splitString) {
        Log.e("TAG", "-------------------------------------------------------------------");
        for (int i = 0; i < 10; i++) {
            Log.e("TAG", "Integrationspunkt " + i);
            Log.e("TAG", "I/O: " + splitString[3 * i] + "; " + "X = " + splitString[3 * i + 1] + "; " + "Y = " + splitString[3 * i + 2]);
            Log.e("TAG", "--------------------");
        }
        Log.e("TAG", "--------------------");
        Log.e("TAG", "Startpunkt I/O: " + splitString[30]);
        Log.e("TAG", "Mittelpunkt I/O: " + splitString[31]);
        Log.e("TAG", "Trapez-Regel I/O: " + splitString[32]);
        Log.e("TAG", "Simpson-Regel I/O: " + splitString[33]);
        Log.e("TAG", "Gauß-Quadratur I/O: " + splitString[34]);
        Log.e("TAG", "Gauß-Quadratur Ordnung: " + splitString[35]);
        Log.e("TAG", "Funktion: " + splitString[36]);

    }

    public void loadActivity(){
        // Einlesen der letzten Konfiguration und umwandeln in ein Vektor
        logSave = Read();
        logSaveVec = StringToStringVev(logSave);

//        StringVecViewer(logSaveVec);

        // Welche Funktion soll geplottete werden, ausgehend von der letzten Konfiguration
        if (Integer.parseInt(logSaveVec[36]) == 0 || Integer.parseInt(logSaveVec[36]) == -1) {
            functions[0] = "RUNGE FUNKTION";
        } else if (Integer.parseInt(logSaveVec[36]) == 1) {
            functions[0] = "SINUS FUNKTION";
        } else if (Integer.parseInt(logSaveVec[36]) == 2) {
            functions[0] = "BETRAGS FUNKTION";
        }


        // Entsprechend der Hauptfunktion verschiedene Parameter setzen
        if (Integer.parseInt(logSaveVec[36]) == 0 || Integer.parseInt(logSaveVec[36]) == -1) {
            start = -5;
            end = 5;
            myCanvas.setEnd(end);
            myCanvas.setStart(start);
            // Testweise setzen der x-Werte
            coordSystem.setxFunc(start, end);
            mainFunction = rungeFunction;
            coordSystem.setyFunc(mainFunction);
        } else if (Integer.parseInt(logSaveVec[36]) == 1) {
            start = -1;
            end = 1;
            myCanvas.setEnd(end);
            myCanvas.setStart(start);
            // Testweise setzen der x-Werte
            coordSystem.setxFunc(start, end);
            mainFunction = trigonomialFunction;
            coordSystem.setyFunc(trigonomialFunction);
        } else if (Integer.parseInt(logSaveVec[36]) == 2) {
            start = -1;
            end = 1;
            myCanvas.setEnd(end);
            myCanvas.setStart(start);
            // Testweise setzen der x-Werte
            coordSystem.setxFunc(start, end);
            mainFunction = absFunction;
            coordSystem.setyFunc(absFunction);
        }

        myCanvas.clearFunc();
        // Koordinatensystem und Funktion zeichnen
        coordSystem.drawAxis();
        coordSystem.drawFunc();

        for (int i = 0; i < points.getCount(); i++) {
            if (getPointPlacedLog(i) == 1){
                startNOOP = startNOOP + 1;
            }
        }

        // Zuweisen der Switche zum EINS/AUS schalten, des Löschen Buttons und der Koordinatenanzeige zu dem jeweiligen Punkt
        for (int i = 0; i < n; i++) {
            int ID = getResources().getIdentifier("switch" + i, "id", getPackageName());
            points.getPoint(i).setSwitcher((Switch) findViewById(ID));

            ID = getResources().getIdentifier("deleter" + i, "id", getPackageName());
            points.getPoint(i).setDeleteButton((ImageButton) findViewById(ID));

            ID = getResources().getIdentifier("xcoord" + i, "id", getPackageName());
            points.getPoint(i).setxView((TextView) findViewById(ID));

            ID = getResources().getIdentifier("ycoord" + i, "id", getPackageName());
            points.getPoint(i).setyView((TextView) findViewById(ID));

            points.getPoint(i).setPaint(pointPaint[i]);

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
                    myCanvas.drawIntegratePoint((float) points.getPoint(i).getX(), i);
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
        };


        // Zuweisen der Aktion, die bei de-aktiveren des Switches für das "StartPoint-Verfahren" ausgeführt werden soll
        startPointSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Falls es weniger als 2 Punkte ging kann nicht integiert werden->siehe "else"
                if (isChecked && points.getNOPP() >= 2) {
                    // Sortieren der Punkte um die Intervalle herauszufinden auf denen jeweils integriert wird
                    points.sortPoints();

                    // Nummern für den aktuellen Punkt und den folgenden (Intevall, auf dem Integriert werden soll)
                    int current;
                    int next;

                    double startPointIntegralValue = 0;

                    // Zuweisen des entsrpechenden Punktes
                    current = points.getFirstPointNumber();
                    next = points.getNextPointNumber(points.getFirstPointNumber());

                    // Berechnen des numerischen Integralwerts mit dem "AnfangsPunkt-Verfahren"
                    for (int i = 0; i < points.getNOPP() - 1; i++) {
                        startPointIntegralValue = startPointIntegralValue + mainFunction.evaluate(points.getPoint(current).getReelX()) * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX());
                        current = next;
                        next = points.getNextPointNumber(next);
                    }
                    // Berechnen des Fehlers und darstellen im entsprechenden Textview
                    coordSystem.getStartPointErrorView().setText("" + Math.round((Math.abs(startPointIntegralValue -
                            mainFunction.antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                            mainFunction.antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);
                } else {
                    coordSystem.getStartPointErrorView().setText("");
                }
                // Canvas muss neu gezeichnet werden, da dieses Verfahren de-aktiviert wurde
                myCanvas.invalidate();
                freeMemory();
            }
        });

        // Zuweisen der Aktion, die bei de-aktiveren des Switches für das "MiddlePoint-Verfahren" ausgeführt werden soll
        // Für Details siehe "startPointSwitch.setOnCheckedChangeListener"
        middelPointSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && points.getNOPP() >= 2) {

                    points.sortPoints();

                    int current;
                    int next;

                    double middelPointIntegralVaulue = 0;

                    current = points.getFirstPointNumber();
                    next = points.getNextPointNumber(points.getFirstPointNumber());

                    for (int i = 0; i < points.getNOPP() - 1; i++) {
                        // Berechnen des Mittelpunktes
                        double middelXCoord = 1.0 / 2.0 * (points.getPoint(next).getReelX() + points.getPoint(current).getReelX());

                        middelPointIntegralVaulue = middelPointIntegralVaulue + mainFunction.evaluate(middelXCoord) * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX());
                        current = next;
                        next = points.getNextPointNumber(next);
                    }

                    coordSystem.getMiddelPointErrorView().setText("" + Math.round((Math.abs(middelPointIntegralVaulue -
                            mainFunction.antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                            mainFunction.antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);


                } else {
                    coordSystem.getMiddelPointErrorView().setText("");
                }
                myCanvas.invalidate();
                freeMemory();
            }
        });
        // Zuweisen der Aktion, die bei de-aktiveren des Switches für das "Trapez-Verfahren" ausgeführt werden soll
        // Für Details siehe "startPointSwitch.setOnCheckedChangeListener"
        trapezSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && points.getNOPP() >= 2) {
                    points.sortPoints();

                    int current;
                    int next;

                    double trapezIntegralVaulue = 0;

                    current = points.getFirstPointNumber();
                    next = points.getNextPointNumber(points.getFirstPointNumber());

                    for (int i = 0; i < points.getNOPP() - 1; i++) {
                        trapezIntegralVaulue = trapezIntegralVaulue + (points.getPoint(next).getReelX() - points.getPoint(current).getReelX()) * 0.5 * (mainFunction.evaluate(points.getPoint(next).getReelX())
                                + mainFunction.evaluate(points.getPoint(current).getReelX()));
                        current = next;
                        next = points.getNextPointNumber(next);
                    }

                    coordSystem.getTrapezErrorView().setText("" + Math.round((Math.abs(trapezIntegralVaulue -
                            mainFunction.antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                            mainFunction.antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);

                } else {
                    coordSystem.getTrapezErrorView().setText("");
                }
                myCanvas.invalidate();
                freeMemory();
            }
        });
        // Zuweisen der Aktion, die bei de-aktiveren des Switches für das "Simpson-Verfahren" ausgeführt werden soll
        // Für Details siehe "startPointSwitch.setOnCheckedChangeListener"
        simpsonSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (points.getNOPP() >= 2) {
                        points.sortPoints();
                        myCanvas.setSimpsonFunction(new SimpsonFunction());
                        myCanvas.getSimpsonFunction().setPoints(points);
                        myCanvas.getSimpsonFunction().setCoordSystem(coordSystem);
                        myCanvas.getSimpsonFunction().simpsonInterpolate();
                        coordSystem.setySimpson(myCanvas.getSimpsonFunction());
                        myCanvas.clearSimpsonFunc();
                        coordSystem.drawSimpsonFunc();
                        myCanvas.invalidate();

                        double simpsonIntegralValue = 0;
                        int current = points.getFirstPointNumber();
                        int next = points.getNextPointNumber(points.getFirstPointNumber());
                        for (int i = 0; i < points.getNOPP() - 1; i++) {
                            simpsonIntegralValue = simpsonIntegralValue + myCanvas.getSimpsonFunction().integral(points.getPoint(current).getReelX(), points.getPoint(next).getReelX());
                            current = next;
                            next = points.getNextPointNumber(next);
                        }
                        coordSystem.getSimpsonErrorView().setText("" + Math.round((Math.abs(simpsonIntegralValue -
                                mainFunction.antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                                mainFunction.antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);
                    }
                } else {
                    coordSystem.getSimpsonErrorView().setText("");
                }
                myCanvas.invalidate();
                freeMemory();
            }
        });

        // Zuweisen der Aktion, die bei de-aktiveren des Switches für das "Simpson-Verfahren" ausgeführt werden soll
        // Für Details siehe "startPointSwitch.setOnCheckedChangeListener"
        gaussSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (points.getNOPP() >= 2) {
                        gaussSeekbar.setEnabled(true);
                        gaussDegreeView.setText("" + gaussSeekbar.getProgress());

                        points.sortPoints();

                        myCanvas.setGaussQuadraturFunction(new GaussQuadraturFunction());
                        myCanvas.getGaussQuadraturFunction().setPoints(points);
                        myCanvas.getGaussQuadraturFunction().setCoordSystem(coordSystem);

                        coordSystem.getGaussErrorView().setText("" + Math.round((Math.abs(myCanvas.getGaussQuadraturFunction().gaussQuadratur(gaussSeekbar.getProgress()+1)-
                                coordSystem.getFunction().antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                                coordSystem.getFunction().antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);
//                        myCanvas.getGaussQuadraturFunction().gaussQuadraturInterpolate(gaussSeekbar.getProgress());
//                        coordSystem.setyGauss(myCanvas.getGaussQuadraturFunction());
//                        myCanvas.clearGaussQuadraturFunc();
//                        coordSystem.drawGaussQuadraturFunc();
                    }
                } else {
                    gaussSeekbar.setEnabled(false);
                    coordSystem.getGaussErrorView().setText("");
                    gaussDegreeView.setText("");
                }
                myCanvas.invalidate();
                freeMemory();
            }
        });

        gaussSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                if (myCanvas.getGaussSwitch().isChecked()) {
                    gaussDegreeView.setText("" + seekBar.getProgress());

                    myCanvas.setGaussQuadraturFunction(new GaussQuadraturFunction());
                    myCanvas.getGaussQuadraturFunction().setPoints(points);
                    myCanvas.getGaussQuadraturFunction().setCoordSystem(coordSystem);
//                    myCanvas.getGaussQuadraturFunction().gaussQuadraturInterpolate(gaussSeekbar.getProgress());
//                    coordSystem.setyGauss(myCanvas.getGaussQuadraturFunction());
//                    myCanvas.clearGaussQuadraturFunc();
//                    coordSystem.drawGaussQuadraturFunc();

                    coordSystem.getGaussErrorView().setText("" + Math.round((Math.abs(myCanvas.getGaussQuadraturFunction().gaussQuadratur(gaussSeekbar.getProgress()+1)-
                            coordSystem.getFunction().antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                            coordSystem.getFunction().antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);

                    freeMemory();
                }
                myCanvas.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if ( myCanvas.getGaussSwitch().isChecked()) {
                    gaussDegreeView.setText("" + seekBar.getProgress());

                    myCanvas.setGaussQuadraturFunction(new GaussQuadraturFunction());
                    myCanvas.getGaussQuadraturFunction().setPoints(points);
                    myCanvas.getGaussQuadraturFunction().setCoordSystem(coordSystem);
//                    myCanvas.getGaussQuadraturFunction().gaussQuadraturInterpolate(gaussSeekbar.getProgress());
//                    coordSystem.setyGauss(myCanvas.getGaussQuadraturFunction());
//                    myCanvas.clearGaussQuadraturFunc();
//                    coordSystem.drawGaussQuadraturFunc();

                    freeMemory();
                }
                myCanvas.invalidate();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (myCanvas.getGaussSwitch().isChecked()) {
                    gaussDegreeView.setText("" + seekBar.getProgress());

                    coordSystem.getGaussErrorView().setText("" + Math.round((Math.abs(myCanvas.getGaussQuadraturFunction().gaussQuadratur(gaussSeekbar.getProgress()+1)-
                            coordSystem.getFunction().antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                            coordSystem.getFunction().antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);

                    freeMemory();
                }
                myCanvas.invalidate();
            }
        });


        // Anfängliches (bei onCreate) setzen der Switche auf EIN/AUS um vorherige Kofiguration auszuführe,
        // hierbei werden automatischdie OnCheckChangeListener ausgeführt
        startPointSwitch.setChecked(getBooleanfromInt(Integer.parseInt(logSaveVec[30])));
        middelPointSwitch.setChecked(getBooleanfromInt(Integer.parseInt(logSaveVec[31])));
        trapezSwitch.setChecked(getBooleanfromInt(Integer.parseInt(logSaveVec[32])));
        simpsonSwitch.setChecked(getBooleanfromInt(Integer.parseInt(logSaveVec[33])));
        gaussSwitch.setChecked(getBooleanfromInt(Integer.parseInt(logSaveVec[34])));

        gaussSeekbar.setProgress(Integer.parseInt(logSaveVec[35]));
        if (gaussSwitch.isChecked()) {
            gaussSeekbar.setEnabled(true);
            coordSystem.getGaussErrorView().setText("" + Math.round((Math.abs(myCanvas.getGaussQuadraturFunction().gaussQuadratur(gaussSeekbar.getProgress()+1)-
                    coordSystem.getFunction().antiderivation(points.getPoint(points.getLastPointNumber()).getReelX()) +
                    coordSystem.getFunction().antiderivation(points.getPoint(points.getFirstPointNumber()).getReelX()))) * 1e6) / 1e6);
        }
        else{
            gaussSeekbar.setEnabled(false);
        }

        // Zuordnen der Switche zu den entsprechenden Punkten und setzen der setOnCheckedChangeListener
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
        // Apply the adapter_SpinnerDimension to the spinner
        functionSelecter.setAdapter(adapter_SpinnerDimension);

        functionSelecter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        break;

                    case 1:
                        functions[0] = "RUNGE FUNKTION";
                        logSaveVec[36] = "" + 0;
                        startPointSwitch.setChecked(false);
                        middelPointSwitch.setChecked(false);
                        trapezSwitch.setChecked(false);
                        simpsonSwitch.setChecked(false);
                        gaussSwitch.setChecked(false);
                        gaussSeekbar.setProgress(0);
                        startPointErrorView.setText("");
                        middlePointErrorView.setText("");
                        trapezErrorView.setText("");
                        simpsonErrorView.setText("");
                        myCanvas.clearPoints();
                        myCanvas.clearFunc();

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
                        mainFunction = new RungeFunction();
                        coordSystem.setyFunc(mainFunction);
                        coordSystem.drawAxis();
                        coordSystem.drawFunc();
                        myCanvas.setCoordSystem(coordSystem);
                        break;
                    case 2:
                        functions[0] = "SINUS FUNKTION";
                        logSaveVec[36] = "" + 1;
                        startPointSwitch.setChecked(false);
                        middelPointSwitch.setChecked(false);
                        trapezSwitch.setChecked(false);
                        simpsonSwitch.setChecked(false);
                        gaussSwitch.setChecked(false);
                        gaussSeekbar.setProgress(0);
                        startPointErrorView.setText("");
                        middlePointErrorView.setText("");
                        trapezErrorView.setText("");
                        simpsonErrorView.setText("");
                        myCanvas.clearPoints();
                        myCanvas.clearFunc();

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
                        mainFunction = new TrigonomialFunction(1, Math.PI * 2, 0, 1);
                        coordSystem.setyFunc(mainFunction);
                        coordSystem.drawAxis();
                        coordSystem.drawFunc();
                        myCanvas.setCoordSystem(coordSystem);
                        break;
                    case 3:
                        functions[0] = "BETRAGS FUNKTION";
                        logSaveVec[36] = "" + 2;
                        startPointSwitch.setChecked(false);
                        middelPointSwitch.setChecked(false);
                        trapezSwitch.setChecked(false);
                        simpsonSwitch.setChecked(false);
                        gaussSwitch.setChecked(false);
                        gaussSeekbar.setProgress(0);
                        startPointErrorView.setText("");
                        middlePointErrorView.setText("");
                        trapezErrorView.setText("");
                        simpsonErrorView.setText("");
                        myCanvas.clearPoints();
                        myCanvas.clearFunc();

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
                        mainFunction = new AbsFunction();
                        coordSystem.setyFunc(mainFunction);
                        coordSystem.drawAxis();
                        coordSystem.drawFunc();
                        myCanvas.setCoordSystem(coordSystem);
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

        freeMemory();

        if (points.getNOAP() == points.getCount()) {
            this.addButton.setVisibility(View.GONE);
        }
    }
}
