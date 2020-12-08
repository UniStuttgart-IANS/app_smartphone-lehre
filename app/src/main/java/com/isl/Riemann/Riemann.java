package com.uni_stuttgart.isl.Riemann;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.uni_stuttgart.isl.Function.AbsFunction;
import com.uni_stuttgart.isl.Function.ConstantFunction;
import com.uni_stuttgart.isl.Function.Function;
import com.uni_stuttgart.isl.Function.MonomialPolynom;
import com.uni_stuttgart.isl.Function.PiecewiseFunction;
import com.uni_stuttgart.isl.Function.PolynomToolbox;
import com.uni_stuttgart.isl.Function.RationalFunction;
import com.uni_stuttgart.isl.Function.RungeFunction;
import com.uni_stuttgart.isl.Function.TrigonomialFunction;
import com.uni_stuttgart.isl.Intros.Intro_Riemann;
import com.uni_stuttgart.isl.Intros.PrefManager;
import com.uni_stuttgart.isl.MainActivity;
import com.uni_stuttgart.isl.Points.PurePoints;
import com.uni_stuttgart.isl.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static java.lang.Float.max;


public class Riemann extends AppCompatActivity {

    // String zum Speichern aller relevanten Informationen, um bei erneutem öffnen der App fortfahren zu können
    String logSave;
    // logSave in einen Vektor umgewandelt um besser damit umgehen zu können
    String[] logSaveVec;
    // Integrationspunkte
    private PurePoints points;
    // Darstellungsfläche
    private MyCanvas myCanvas;
    // Objekt zum erzeugen eines Koordinaten Systems
    private CoordSystem coordSystem;

    // Intervall auf dem die Funktion geplottete werden soll
    private double start;
    private double end;
    // Welches Koordiantensystem soll gewählt werden (welche Quadranten)
    private int coordArt;
    // Spinner zur Wahl der Funktion
    private Spinner functionSelecter;
    // Darstellungsfelder für die berechneten Integrationsfehler
    private TextView supSumErrorView;
    private TextView infSumErrorView;
    private TextView supSumSolView;
    private TextView infSumSolView;
    private TextView diffInfSumView;
    private TextView exactSolView;

    private EditText MatNr;

    private TextView numBins;
    // Switchs/Switche um verschiedene Integrationsmethoden zu de-aktivieren
    private Switch supSumSwitch;
    private Switch infSumSwitch;
    private SeekBar barNumBins;

    private boolean load_in_progress = false;

    public AlertDialog alertDialog;


    // Hauptfunktion (Funktion über die integriert werden soll)
    private Function mainFunction;
    // Auflistung der verschiedenen Funktionen über die integriert werden kann
    private CharSequence[] functions = {"Funktionen", "Runge Funktion", "Sinus Funktion", "Betrags Funktion","HM Funk. 1","HM Funk. 2","HM Funk. 3"};
    private int fid; //Function id
    //Anzahl Extremwerte fuer Runge, Sinus und Betragsfunktion
    private double[][] xValMaxima = { {0.0}, {-0.75,-0.25,0.25,0.75}, {0.0} ,{-1,0,2}, {-1,1}, {0,5, 1}};
    private double[] exactInt = {2.74680153389003, 0, 1, 4.0451774445, 4.0/3.0, 53./32.};

    // Funktionen über die integriert werden kann
    private TrigonomialFunction trigonomialFunction = new TrigonomialFunction(1, Math.PI * 2, 0, 1);
    private RungeFunction rungeFunction = new RungeFunction();
    private AbsFunction absFunction = new AbsFunction();
    double f1_coeff[] = {2.,1.,1.,2.,1.,0.};
    private RationalFunction HMFunction1 = new RationalFunction(2, f1_coeff);
    double f2_coeff[] = {1./3.,2./3.};
    private MonomialPolynom HMFunction2 = new MonomialPolynom(1, f2_coeff);
    private int selected_func = 2;

    double ranges[] = {-1,0.5,1.1};
    int owner[] = {1,-1,1};
    double coeffs_f32[]={-5./8.,5./4.};
    private Function funcs_f3[] = {new ConstantFunction(1), new MonomialPolynom(1,coeffs_f32)};
    private PiecewiseFunction HMFunction3 = new PiecewiseFunction(2, ranges, owner,funcs_f3);
    // Toolbar
    private Toolbar toolbar;
    // Prefmanager für weiterführende Funktionen (Restart, Intros etc...)
    private PrefManager prefManager;
    private int n= 101;
    private int num_apoints=0;
    private int final_size;

    private Paint[] pointPaint = new Paint[n];
    private int startNOOP;

    public double supSumIntegralVaulue = 0;
    public double infSumIntegralVaulue = 0;

    // Funktion zum erzeugen den Menüpunktes "oben-rechts"
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_riem, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        // Bei Auswahl von "Introduction" start von Intro und speichern der aktuellen Konfiguration
        if (item.getItemId() == R.id.introduction) {
            // Beim erneuten
            if(item.getTitle().equals("Prüfungsmodus")) {
                item.setTitle("Standardmodus");
                TextClock tc = findViewById(R.id.header_clock);
                TextView tw = findViewById(R.id.ToolbarText);
                TextView tw_name = findViewById(R.id.ToolbarTextName);
                EditText matnr = findViewById(R.id.ToolbarMatNr);
                EditText txt_name = findViewById(R.id.ToolbarName);
                tc.setVisibility(View.INVISIBLE);
                tw.setVisibility(View.INVISIBLE);
                tw_name.setVisibility(View.INVISIBLE);
                txt_name.setVisibility(View.INVISIBLE);
                matnr.setVisibility(View.INVISIBLE);

            }
            else {
                item.setTitle("Prüfungsmodus");
                TextClock tc = findViewById(R.id.header_clock);
                TextView tw = findViewById(R.id.ToolbarText);
                TextView tw_name = findViewById(R.id.ToolbarTextName);
                EditText matnr = findViewById(R.id.ToolbarMatNr);
                EditText txt_name = findViewById(R.id.ToolbarName);
                tc.setVisibility(View.VISIBLE);
                tw.setVisibility(View.VISIBLE);
                tw_name.setVisibility(View.VISIBLE);
                txt_name.setVisibility(View.VISIBLE);
                matnr.setVisibility(View.VISIBLE);

            }
            //prefManager.setReopenRiemannIntro(false);
            //saveActivityToLog();
            //Intent myIntent = new Intent(Riemann.this, Intro_Riemann.class);
            //Riemann.this.startActivity(myIntent);

            freeMemory();
            //finish();
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riemann);
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
//                saveActivityToLog();
                // Bei Wiederaufruf der App soll (da ja zurückgegangen wurde zu Main) die Main geöffnet werden
                prefManager.setIsMainLastActivity(true);
                prefManager.setIsInterLastActivity(false);
                prefManager.setIsSplitLastActivity(false);
                prefManager.setIsRiemLastActivity(false);
                // Es wurde auf "Back" geklickt und nicht die App geschlossen
                prefManager.setIsGoingBack(true);
                // Start der Main
                Intent myIntent = new Intent(Riemann.this, MainActivity.class);
                Riemann.this.startActivity(myIntent);
                freeMemory();
                finish();
            }
        });

        MatNr = (EditText) findViewById(R.id.ToolbarMatNr);
        alertDialog = new AlertDialog.Builder(Riemann.this).create();
//        logSave = Read();
//        logSaveVec = StringToStringVev(logSave);

        // Festlegen der Farbe, Dicke,... der ...-Punkte
        for (int i = 0; i < n; i++) {
            pointPaint[i] = new Paint();
            pointPaint[i].setAntiAlias(true);
            pointPaint[i].setStyle(Paint.Style.FILL_AND_STROKE);
            pointPaint[i].setStrokeJoin(Paint.Join.ROUND);
            pointPaint[i].setStrokeWidth(5.5F);

            // Farben der Punkte definieren
            pointPaint[i].setColor(Color.rgb(210, 210, 210));       //Grey
        }

        // Anlegen der Punkte
        points = new PurePoints(n);
        // Erzeugen der Darstellungsfläche
        myCanvas = (MyCanvas) findViewById(R.id.mycanvasriemann);


        numBins = (TextView) findViewById(R.id.NumBins);
        barNumBins = (SeekBar) findViewById(R.id.seekBarNumBins);

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
                ParamsCanvas.width = height-toolbar.getHeight()-8;
                myCanvas.setLayoutParams(ParamsCanvas);

                final_size = myCanvas.getHeight();


                // Anlegen des Koordinatensystems
                coordSystem = new CoordSystem((int) final_size / 2, (int) final_size / 2, (int) final_size / 2 - 50, (int) final_size / 2 - 50, coordArt, myCanvas);

                // Koordinatensystem+Integrationspunkte der DarstellungsflÃ¤che Ã¼bergen
                myCanvas.setCoordSystem(coordSystem);
                myCanvas.setPoints(points);


                // Fehleranzeige fuer das "Obersumme-Verfahren"
                supSumErrorView = (TextView) findViewById(R.id.errorSupSum);
                coordSystem.setsupSumErrorView(supSumErrorView);

                // Fehleranzeige fuer das "Untersumme-Verfahren"
                infSumErrorView = (TextView) findViewById(R.id.errorinfSum);
                coordSystem.setinfSumErrorView(infSumErrorView);

                infSumSolView = (TextView) findViewById(R.id.solinfSum);
                coordSystem.setinfSumSolView(infSumSolView);

                supSumSolView = (TextView) findViewById(R.id.solSupSum);
                coordSystem.setsupSumSolView(supSumSolView);

                exactSolView = (TextView) findViewById(R.id.exSol);

                diffInfSumView = (TextView) findViewById(R.id.diffOSUS);
                coordSystem.setdiffInfSumView(diffInfSumView);

                // Switche zum de-aktiveren der verschiedenen Integrationsverfahren
                supSumSwitch = (Switch) findViewById(R.id.supSumSwitch);
                myCanvas.setsupSumSwitch(supSumSwitch);
                infSumSwitch = (Switch) findViewById(R.id.infSumRegel);
                myCanvas.setinfSumSwitch(infSumSwitch);

                myCanvas.init_finished();

                loadActivity();
            }

        });


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
            InputStream inputStream = this.openFileInput("LogRiemann.txt");

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
/*
    public void setPointLog(int pointnumber, int active, double x, double y) {
        logSaveVec[pointnumber * 3] = Integer.toString(active);
        logSaveVec[pointnumber * 3 + 1] = Double.toString(x);
        logSaveVec[pointnumber * 3 + 2] = Double.toString(y);
    }

    public int getPointPlacedLog(int pointnumber) {
        return 0;//return Integer.parseInt(logSaveVec[pointnumber * 3]);
    }
*/
    public String StringVecToString(String[] logStringVec) {
        String logString = "";

        for (int i = 0; i < logStringVec.length; i++) {
            logString = logString + logStringVec[i] + " ";
        }

        return logString;
    }

    private void saveActivityToLog() {
        logSaveVec = new String[3*n+4];

        for (int i = 0; i < n; i++) {
            logSaveVec[3 * i] = Integer.toString(getIntFromBoolean(points.getPoint(i).getIsPlaced()));
            logSaveVec[3 * i + 1] = "" + points.getPoint(i).getX();
            logSaveVec[3 * i + 2] = "" + points.getPoint(i).getY();
        }

        logSaveVec[3*n] = Integer.toString(getIntFromBoolean(supSumSwitch.isChecked()));
        logSaveVec[3*n+1] = Integer.toString(getIntFromBoolean(infSumSwitch.isChecked()));


        logSaveVec[3*n+2] = ""+selected_func;
        logSaveVec[3*n+3] = ""+num_apoints;

        Save(StringVecToString(logSaveVec));
    }

    public void Save(String log) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("LogRiemann.txt", Context.MODE_PRIVATE));
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
        prefManager.setIsRiemLastActivity(true);
//        saveActivityToLog();
        freeMemory();
    }

    public void saveActivityToLogButton(View view) {
        prefManager.setIsRiemannSaved(true);

        String[] logSaveVecButton = new String[3*n+4];
        for (int i = 0; i < n; i++) {
            logSaveVecButton[3 * i] = Integer.toString(getIntFromBoolean(points.getPoint(i).getIsPlaced()));
            logSaveVecButton[3 * i + 1] = "" + points.getPoint(i).getX();
            logSaveVecButton[3 * i + 2] = "" + points.getPoint(i).getY();
        }

        logSaveVecButton[3*n] = Integer.toString(getIntFromBoolean(supSumSwitch.isChecked()));
        logSaveVecButton[3*n+1] = Integer.toString(getIntFromBoolean(infSumSwitch.isChecked()));

        logSaveVecButton[3*n+2] = ""+ selected_func;
        logSaveVecButton[3*n+3] = ""+ num_apoints;

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("LogRiemannSave.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(StringVecToString(logSaveVecButton));
            outputStreamWriter.close();

        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        Toast.makeText(this, "Aktuelle Konfiguration wurde gespeichert!", Toast.LENGTH_SHORT).show();
    }

    public void loadLogButton(View view) {
        if (prefManager.isRiemannSaved()) {
            load_in_progress = true;
            String Temp = ReadSave();
            Save(Temp);
            loadFromLogFile();
//            loadActivity();
            Toast.makeText(this, "Konfiguration wurde geladen!", Toast.LENGTH_SHORT).show();

            freeMemory();
        }
        else{
            Toast.makeText(this, "Bitte zuerst eine Konfiguration abspeichern!", Toast.LENGTH_SHORT).show();
        }
    }

    public final String ReadSave() {

        String logString = "";
        try {
            InputStream inputStream = this.openFileInput("LogRiemannSave.txt");

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
/*
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
        Log.e("TAG", "infSum-Regel I/O: " + splitString[32]);
        Log.e("TAG", "Funktion: " + splitString[33]);

    }
*/

    public void loadFromLogFile() {

        logSave = Read();
        logSaveVec = StringToStringVev(logSave);
        if (Integer.parseInt(logSaveVec[3*n+2]) == 0 || Integer.parseInt(logSaveVec[3*n+2]) == -1) {
            functions[0] = "RUNGE FUNKTION";
            selected_func=0;
        } else if (Integer.parseInt(logSaveVec[3*n+2]) == 1) {
            functions[0] = "SINUS FUNKTION";
            selected_func=1;
        } else if (Integer.parseInt(logSaveVec[3*n+2]) == 2) {
            functions[0] = "BETRAGS FUNKTION";
            selected_func=2;
        } else if (Integer.parseInt(logSaveVec[3*n+2]) == 3) {
            selected_func=3;
            functions[0] = "HM FUNK. 1";
        } else if (Integer.parseInt(logSaveVec[3*n+2]) == 4) {
            selected_func=4;
            functions[0] = "HM FUNK. 2";
        } else if (Integer.parseInt(logSaveVec[3*n+2]) == 5) {
            selected_func=5;
            functions[0] = "HM FUNK. 3";
        }

        functionSelecter.setSelection(selected_func+1);

        switch (selected_func+1) {
            case 0:
                break;

            case 1:
                supSumSwitch.setChecked(false);
                infSumSwitch.setChecked(false);
                supSumErrorView.setText("");
                infSumErrorView.setText("");
                myCanvas.clearPoints();
                myCanvas.clearFunc();

                for (int i = 1; i < points.getCount(); i++) {
                    points.removePoint(i);
                }

                points.getPoint(0).setActive(true);


                start = -5;
                end = 5;

                myCanvas.setEnd(end);
                myCanvas.setStart(start);
                // Testweise setzen der x-Werte
                coordSystem.setxFunc(start, end);
                mainFunction = rungeFunction;
                fid = 0;
                exactSolView.setText(String.format("%.5f", exactInt[fid]));
                myCanvas.setMainFunction(mainFunction,fid);
                coordSystem.setyFunc(mainFunction);
                coordSystem.drawAxis();
                coordSystem.drawFunc();
                myCanvas.setCoordSystem(coordSystem);
                break;
            case 2:
                supSumSwitch.setChecked(false);
                infSumSwitch.setChecked(false);
                supSumErrorView.setText("");
                infSumErrorView.setText("");
                myCanvas.clearPoints();
                myCanvas.clearFunc();

                for (int i = 1; i < points.getCount(); i++) {
                    points.removePoint(i);
                }

                points.getPoint(0).setActive(true);

                start = -1;
                end = 1;

                myCanvas.setEnd(end);
                myCanvas.setStart(start);
                // Testweise setzen der x-Werte
                coordSystem.setxFunc(start, end);
                mainFunction = trigonomialFunction;
                fid = 1;
                exactSolView.setText(String.format("%.5f", exactInt[fid]));
                myCanvas.setMainFunction(mainFunction,fid);
                coordSystem.setyFunc(mainFunction);
                coordSystem.drawAxis();
                coordSystem.drawFunc();
                myCanvas.setCoordSystem(coordSystem);
                break;

            case 3:
                supSumSwitch.setChecked(false);
                infSumSwitch.setChecked(false);
                supSumErrorView.setText("");
                infSumErrorView.setText("");
                myCanvas.clearPoints();
                myCanvas.clearFunc();

                for (int i = 1; i < points.getCount(); i++) {
                    points.removePoint(i);
                }

                points.getPoint(0).setActive(true);


                start = -1;
                end = 1;

                myCanvas.setEnd(end);
                myCanvas.setStart(start);
                // Testweise setzen der x-Werte
                coordSystem.setxFunc(start, end);
                mainFunction = absFunction;
                fid = 2;
                exactSolView.setText(String.format("%.5f", exactInt[fid]));
                myCanvas.setMainFunction(mainFunction,fid);
                coordSystem.setyFunc(mainFunction);
                coordSystem.drawAxis();
                coordSystem.drawFunc();
                myCanvas.setCoordSystem(coordSystem);
                break;

            case 4:
                supSumSwitch.setChecked(false);
                infSumSwitch.setChecked(false);
                supSumErrorView.setText("");
                infSumErrorView.setText("");
                myCanvas.clearPoints();
                myCanvas.clearFunc();

                for (int i = 1; i < points.getCount(); i++) {
                    points.removePoint(i);
                }

                points.getPoint(0).setActive(true);

                start = -1;
                end = 2;

                myCanvas.setEnd(end);
                myCanvas.setStart(start);
                // Testweise setzen der x-Werte
                coordSystem.setxFunc(start, end);
                mainFunction = HMFunction1;
                fid = 3;
                exactSolView.setText(String.format("%.5f", exactInt[fid]));
                myCanvas.setMainFunction(mainFunction,fid);
                coordSystem.setyFunc(mainFunction);
                coordSystem.drawAxis();
                coordSystem.drawFunc();
                myCanvas.setCoordSystem(coordSystem);
                break;

            case 5:
                supSumSwitch.setChecked(false);
                infSumSwitch.setChecked(false);
                supSumErrorView.setText("");
                infSumErrorView.setText("");
                myCanvas.clearPoints();
                myCanvas.clearFunc();

                for (int i = 1; i < points.getCount(); i++) {
                    points.removePoint(i);
                }

                points.getPoint(0).setActive(true);

                start = -1;
                end = 1;

                myCanvas.setEnd(end);
                myCanvas.setStart(start);
                // Testweise setzen der x-Werte
                coordSystem.setxFunc(start, end);
                mainFunction = HMFunction2;
                fid = 4;
                exactSolView.setText(String.format("%.5f", exactInt[fid]));
                myCanvas.setMainFunction(mainFunction,fid);
                coordSystem.setyFunc(mainFunction);
                coordSystem.drawAxis();
                coordSystem.drawFunc();
                myCanvas.setCoordSystem(coordSystem);
                break;
            case 6:
                supSumSwitch.setChecked(false);
                infSumSwitch.setChecked(false);
                supSumErrorView.setText("");
                infSumErrorView.setText("");
                myCanvas.clearPoints();
                myCanvas.clearFunc();

                for (int i = 1; i < points.getCount(); i++) {
                    points.removePoint(i);
                }

                points.getPoint(0).setActive(true);

                start = -1;
                end = 1;

                myCanvas.setEnd(end);
                myCanvas.setStart(start);
                // Testweise setzen der x-Werte
                coordSystem.setxFunc(start, end);
                mainFunction = HMFunction3;
                fid = 5;
                exactSolView.setText(String.format("%.5f", exactInt[fid]));
                myCanvas.setMainFunction(mainFunction,fid);
                coordSystem.setyFunc(mainFunction);
                coordSystem.drawAxis();
                coordSystem.drawFunc();
                myCanvas.setCoordSystem(coordSystem);
                break;
        }


        myCanvas.invalidate();

        if(Integer.parseInt(logSaveVec[3*n])==1)
            myCanvas.getsupSumSwitch().setChecked(true);
        else
            myCanvas.getsupSumSwitch().setChecked(false);

        if(Integer.parseInt(logSaveVec[3*n+1])==1)
            myCanvas.getinfSumSwitch().setChecked(true);
        else
            myCanvas.getinfSumSwitch().setChecked(false);

        // clean up
        myCanvas.clearPoints();

        // set num points
        int numbins = Integer.parseInt(logSaveVec[3*n+3])-1;
        num_apoints = Integer.parseInt(logSaveVec[3*n+3]);
        int numPoints = numbins + 1;

        numBins.setText("" + numbins);
        barNumBins.setProgress(numbins);

        for(int i = 0; i < numPoints; i++) {
            points.getPoint(i).setActive(true);
            points.getPoint(i).setIsAvailable(true);
        }

        if(infSumSwitch.isChecked()||supSumSwitch.isChecked())
        {
            // clean up
            myCanvas.clearPoints();
            // set num points

            //equi
            if(points.getCount()> numPoints)
            {
                for (int i = numPoints; i < points.getCount(); i++) {
                    points.removePoint(i);
                }
            }
            else
            {
                for (int i =  points.getCount(); i <numPoints; i++) {
                    points.addPoint();
                }
            }


            int k = 0;

            for (int i = 0; i < points.getCount(); i++) {
                if (points.getPoint(i).getIsAvailable()) {
                    points.getPoint(i).setX(Float.parseFloat(logSaveVec[3 * i + 1]));
                    points.getPoint(i).setY(Float.parseFloat(logSaveVec[3 * i + 2]));
                    myCanvas.drawIntegratePoint((float) points.getPoint(i).getX(), i);
                    points.getPoint(i).setIsPlaced(true);
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

            supSumIntegralVaulue = 0;
            if (myCanvas.getsupSumSwitch().isChecked() && points.getNOPP() >= 2) {

                current = points.getFirstPointNumber();
                next = points.getNextPointNumber(points.getFirstPointNumber());

                for (int i = 0; i < points.getNOPP() - 1; i++) {

                    double middelXCoord = find_max((float)points.getPoint(next).getReelX(), (float)points.getPoint(current).getReelX());

                    supSumIntegralVaulue = supSumIntegralVaulue + mainFunction.evaluate(middelXCoord) * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX());
                    current = next;
                    next = points.getNextPointNumber(next);
                }

                supSumErrorView.setText("" + String.format("%.5f", supSumIntegralVaulue - exactInt[fid]));
                supSumSolView.setText("" + String.format("%.5f", supSumIntegralVaulue));
            }
            infSumIntegralVaulue = 0;
            if (myCanvas.getinfSumSwitch().isChecked() && points.getNOPP() >= 2) {
                points.sortPoints();


                current = points.getFirstPointNumber();
                next = points.getNextPointNumber(points.getFirstPointNumber());

                for (int i = 0; i < points.getNOPP() - 1; i++) {
                    double middelXCoord = find_min((float)points.getPoint(next).getReelX(), (float)points.getPoint(current).getReelX());
                    infSumIntegralVaulue = infSumIntegralVaulue + mainFunction.evaluate(middelXCoord) * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX());
                    current = next;
                    next = points.getNextPointNumber(next);
                }

                infSumErrorView.setText("" + String.format("%.5f", infSumIntegralVaulue - exactInt[fid]));
                infSumSolView.setText("" + String.format("%.5f", infSumIntegralVaulue));

            }

            if(myCanvas.getinfSumSwitch().isChecked() && myCanvas.getinfSumSwitch().isChecked() && points.getNOPP() >= 2)
                diffInfSumView.setText("" + String.format("%.5f", supSumIntegralVaulue-infSumIntegralVaulue));
            else
                diffInfSumView.setText("");

            myCanvas.invalidate();

            for (int i = 0; i < points.getCount(); i++) {
                points.getPoint(i).setActive(false);
                //points.getPoint(i).getSwitch().setChecked(false);
            }

        }

        myCanvas.invalidate();
    }

        public void loadActivity(){
        // Einlesen der letzten Konfiguration und umwandeln in ein Vektor

//        logSave = Read();
//        logSaveVec = StringToStringVev(logSave);

//        StringVecViewer(logSaveVec);

        // Welche Funktion soll geplottete werden, ausgehend von der letzten Konfiguration
//        if (Integer.parseInt(logSaveVec[3*n+3]) == 0 || Integer.parseInt(logSaveVec[3*n+3]) == -1) {
            functions[0] = "RUNGE FUNKTION";
            selected_func=0;/*
        } else if (Integer.parseInt(logSaveVec[3*n+2]) == 1) {
            functions[0] = "SINUS FUNKTION";
            selected_func=1;
        } else if (Integer.parseInt(logSaveVec[3*n+2]) == 2) {
            functions[0] = "BETRAGS FUNKTION";
            selected_func=2;
        } else if (Integer.parseInt(logSaveVec[3*n+2]) == 3) {
            selected_func=3;
            functions[0] = "HM FUNK. 1";
        } else if (Integer.parseInt(logSaveVec[3*n+2]) == 4) {
            selected_func=4;
            functions[0] = "HM FUNK. 2";
        } else if (Integer.parseInt(logSaveVec[3*n+2]) == 5) {
            selected_func=5;
            functions[0] = "HM FUNK. 3";
        }




        // Entsprechend der Hauptfunktion verschiedene Parameter setzen
        if (Integer.parseInt(logSaveVec[3*n+3]) == 0 || Integer.parseInt(logSaveVec[3*n+3]) == -1) {*/
            start = -5;
            end = 5;
            myCanvas.setEnd(end);
            myCanvas.setStart(start);
            // Testweise setzen der x-Werte
            coordSystem.setxFunc(start, end);
            mainFunction = rungeFunction;
            fid = 0;
            exactSolView.setText(String.format("%.5f", exactInt[fid]));
            myCanvas.setMainFunction(mainFunction,fid);
            coordSystem.setyFunc(mainFunction);/*
        } else if (Integer.parseInt(logSaveVec[3*n+3]) == 1) {
            start = -1;
            end = 1;
            myCanvas.setEnd(end);
            myCanvas.setStart(start);
            // Testweise setzen der x-Werte
            coordSystem.setxFunc(start, end);
            mainFunction = trigonomialFunction;
            fid = 1;
            myCanvas.setMainFunction(mainFunction,fid);
            coordSystem.setyFunc(trigonomialFunction);
        } else if (Integer.parseInt(logSaveVec[3*n+3]) == 2) {
            start = -1;
            end = 1;
            myCanvas.setEnd(end);
            myCanvas.setStart(start);
            // Testweise setzen der x-Werte
            coordSystem.setxFunc(start, end);
            mainFunction = absFunction;
            fid = 2;
            myCanvas.setMainFunction(mainFunction,fid);
            coordSystem.setyFunc(absFunction);
        }*/

        myCanvas.clearFunc();
        // Koordinatensystem und Funktion zeichnen
        coordSystem.drawAxis();
        coordSystem.drawFunc();
/*
        for (int i = 0; i < points.getCount(); i++) {
            if (getPointPlacedLog(i) == 1){
                startNOOP = startNOOP + 1;
            }
        }
*/
        // Zuweisen der Switche zum EINS/AUS schalten, des Löschen Buttons und der Koordinatenanzeige zu dem jeweiligen Punkt
        for (int i = 0; i < n; i++) {

            points.getPoint(i).setPaint(pointPaint[i]);

            if (startNOOP == 0){

                if (i!=0) {

                    points.getPoint(i).setIsAvailable(false);
                    points.getPoint(i).setIsPlaced(false);
                }
            }
/*            else{
                if (getPointPlacedLog(i) == 0) {

                    points.getPoint(i).setIsAvailable(false);
                    points.getPoint(i).setIsPlaced(false);
                }
                else if (getPointPlacedLog(i) == 1) {

                    points.getPoint(i).setX(Float.parseFloat(logSaveVec[3 * i + 1]));
                    points.getPoint(i).setIsPlaced(true);
                    points.getPoint(i).setIsAvailable(true);
                    myCanvas.drawIntegratePoint((float) points.getPoint(i).getX(), i);
                }
            }*/
        }
/*
        if (startNOOP > 0) {
            for (int j = 0; j < points.getCount(); j++) {
                if (getPointPlacedLog(j) == 1) {
                    points.getPoint(j).setIsAvailable(true);
                    break;
                }
            }
        }else {
            points.getPoint(0).setIsAvailable(true);
        };
*/

        MatNr.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
                if(MatNr.getText().length()!= 7){
                    alertDialog.setMessage("Ihre Matrikelnummer sollte genau 7-stellig sein!");
                    alertDialog.show();
                }
            }
        });
/*
        MatNr.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // TODO Auto-generated method stub
                if(MatNr.getText().length()!= 7){
                    alertDialog.setMessage("Ihre Matrikelnummer sollte genau 7-stellig sein!");
                    alertDialog.show();
                }
            }
        });*/


        // Zuweisen der Aktion, die bei de-aktiveren des Switches für das "supSumPoint-Verfahren" ausgeführt werden soll
        // Für Details siehe "startPointSwitch.setOnCheckedChangeListener"
        supSumSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && points.getNOPP() >= 2) {

                    points.sortPoints();

                    int current;
                    int next;

                    supSumIntegralVaulue = 0;

                    current = points.getFirstPointNumber();
                    next = points.getNextPointNumber(points.getFirstPointNumber());

                    for (int i = 0; i < points.getNOPP() - 1; i++) {
                        // Berechnen des Mittelpunktes
                        double middelXCoord = find_max((float)points.getPoint(next).getReelX(), (float)points.getPoint(current).getReelX());

                        supSumIntegralVaulue = supSumIntegralVaulue + mainFunction.evaluate(middelXCoord) * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX());
                        current = next;
                        next = points.getNextPointNumber(next);
                    }

                    supSumErrorView.setText("" + String.format("%.5f", supSumIntegralVaulue - exactInt[fid]));
                    supSumSolView.setText(""+ String.format("%.5f", supSumIntegralVaulue));


                } else {
                    supSumErrorView.setText("");
                }
                if(myCanvas.getinfSumSwitch().isChecked() && myCanvas.getinfSumSwitch().isChecked() && points.getNOPP() >= 2)
                    diffInfSumView.setText("" + String.format("%.5f", supSumIntegralVaulue-infSumIntegralVaulue));
                else
                    diffInfSumView.setText("");

                myCanvas.invalidate();
                freeMemory();
            }
        });
        // Zuweisen der Aktion, die bei de-aktiveren des Switches für das "infSum-Verfahren" ausgeführt werden soll
        // Für Details siehe "startPointSwitch.setOnCheckedChangeListener"
        infSumSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && points.getNOPP() >= 2) {
                    points.sortPoints();

                    int current;
                    int next;

                    infSumIntegralVaulue = 0;

                    current = points.getFirstPointNumber();
                    next = points.getNextPointNumber(points.getFirstPointNumber());

                    for (int i = 0; i < points.getNOPP() - 1; i++) {
                        double middelXCoord = find_min((float)points.getPoint(next).getReelX(), (float)points.getPoint(current).getReelX());
                        infSumIntegralVaulue = infSumIntegralVaulue + mainFunction.evaluate(middelXCoord) * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX());
                        current = next;
                        next = points.getNextPointNumber(next);
                    }

                    infSumErrorView.setText("" + String.format("%.5f", infSumIntegralVaulue -exactInt[fid]));
                    infSumSolView.setText(""+ String.format("%.5f", infSumIntegralVaulue));

                } else {
                    infSumErrorView.setText("");
                }
                if(myCanvas.getinfSumSwitch().isChecked() && myCanvas.getinfSumSwitch().isChecked() && points.getNOPP() >= 2)
                    diffInfSumView.setText("" + String.format("%.5f", supSumIntegralVaulue-infSumIntegralVaulue));
                else
                    diffInfSumView.setText("");
                myCanvas.invalidate();
                freeMemory();
            }
        });



        // Anfängliches (bei onCreate) setzen der Switche auf EIN/AUS um vorherige Kofiguration auszuführe,
        // hierbei werden automatischdie OnCheckChangeListener ausgeführt
//        supSumSwitch.setChecked(getBooleanfromInt(Integer.parseInt(logSaveVec[31])));
//        infSumSwitch.setChecked(getBooleanfromInt(Integer.parseInt(logSaveVec[32])));


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

                if(!load_in_progress)
                    switch (position) {
                    case 0:
                        break;

                    case 1:
                        functions[0] = "RUNGE FUNKTION";
                        selected_func = 0;
//                        logSaveVec[33] = "" + 0;
                        supSumSwitch.setChecked(false);
                        infSumSwitch.setChecked(false);
                        supSumErrorView.setText("");
                        infSumErrorView.setText("");
                        myCanvas.clearPoints();
                        myCanvas.clearFunc();

                        for (int i = 1; i < points.getCount(); i++) {
                            points.removePoint(i);
                        }

                        points.getPoint(0).setActive(true);


                        start = -5;
                        end = 5;

                        myCanvas.setEnd(end);
                        myCanvas.setStart(start);
                        // Testweise setzen der x-Werte
                        coordSystem.setxFunc(start, end);
                        mainFunction = rungeFunction;
                        fid = 0;
                        exactSolView.setText(String.format("%.5f", exactInt[fid]));
                        myCanvas.setMainFunction(mainFunction,fid);
                        coordSystem.setyFunc(mainFunction);
                        coordSystem.drawAxis();
                        coordSystem.drawFunc();
                        myCanvas.setCoordSystem(coordSystem);
                        break;
                    case 2:
                        functions[0] = "SINUS FUNKTION";
//                        logSaveVec[33] = "" + 1;
                        selected_func = 1;
                        supSumSwitch.setChecked(false);
                        infSumSwitch.setChecked(false);
                        supSumErrorView.setText("");
                        infSumErrorView.setText("");
                        myCanvas.clearPoints();
                        myCanvas.clearFunc();

                        for (int i = 1; i < points.getCount(); i++) {
                            points.removePoint(i);
                        }

                        points.getPoint(0).setActive(true);

                        start = -1;
                        end = 1;

                        myCanvas.setEnd(end);
                        myCanvas.setStart(start);
                        // Testweise setzen der x-Werte
                        coordSystem.setxFunc(start, end);
                        mainFunction = trigonomialFunction;
                        fid = 1;
                        exactSolView.setText(String.format("%.5f", exactInt[fid]));
                        myCanvas.setMainFunction(mainFunction,fid);
                        coordSystem.setyFunc(mainFunction);
                        coordSystem.drawAxis();
                        coordSystem.drawFunc();
                        myCanvas.setCoordSystem(coordSystem);
                        break;

                    case 3:
                        functions[0] = "BETRAGS FUNKTION";
//                        logSaveVec[33] = "" + 2;
                        selected_func = 2;
                        supSumSwitch.setChecked(false);
                        infSumSwitch.setChecked(false);
                        supSumErrorView.setText("");
                        infSumErrorView.setText("");
                        myCanvas.clearPoints();
                        myCanvas.clearFunc();

                        for (int i = 1; i < points.getCount(); i++) {
                            points.removePoint(i);
                        }

                        points.getPoint(0).setActive(true);


                        start = -1;
                        end = 1;

                        myCanvas.setEnd(end);
                        myCanvas.setStart(start);
                        // Testweise setzen der x-Werte
                        coordSystem.setxFunc(start, end);
                        mainFunction = absFunction;
                        fid = 2;
                        exactSolView.setText(String.format("%.5f", exactInt[fid]));
                        myCanvas.setMainFunction(mainFunction,fid);
                        coordSystem.setyFunc(mainFunction);
                        coordSystem.drawAxis();
                        coordSystem.drawFunc();
                        myCanvas.setCoordSystem(coordSystem);
                        break;

                    case 4:
                        functions[0] = "HM FUNK. 1";
//                        logSaveVec[33] = "" + 3;
                        selected_func = 3;
                        supSumSwitch.setChecked(false);
                        infSumSwitch.setChecked(false);
                        supSumErrorView.setText("");
                        infSumErrorView.setText("");
                        myCanvas.clearPoints();
                        myCanvas.clearFunc();

                        for (int i = 1; i < points.getCount(); i++) {
                            points.removePoint(i);
                        }

                        points.getPoint(0).setActive(true);

                        start = -1;
                        end = 2;

                        myCanvas.setEnd(end);
                        myCanvas.setStart(start);
                        // Testweise setzen der x-Werte
                        coordSystem.setxFunc(start, end);
                        mainFunction = HMFunction1;
                        fid = 3;
                        exactSolView.setText(String.format("%.5f", exactInt[fid]));
                        myCanvas.setMainFunction(mainFunction,fid);
                        coordSystem.setyFunc(mainFunction);
                        coordSystem.drawAxis();
                        coordSystem.drawFunc();
                        myCanvas.setCoordSystem(coordSystem);
                        break;

                    case 5:
                        functions[0] = "HM FUNK. 2";
//                        logSaveVec[33] = "" + 4;
                        selected_func = 4;
                        supSumSwitch.setChecked(false);
                        infSumSwitch.setChecked(false);
                        supSumErrorView.setText("");
                        infSumErrorView.setText("");
                        myCanvas.clearPoints();
                        myCanvas.clearFunc();

                        for (int i = 1; i < points.getCount(); i++) {
                            points.removePoint(i);
                        }

                        points.getPoint(0).setActive(true);

                        start = -1;
                        end = 1;

                        myCanvas.setEnd(end);
                        myCanvas.setStart(start);
                        // Testweise setzen der x-Werte
                        coordSystem.setxFunc(start, end);
                        mainFunction = HMFunction2;
                        fid = 4;
                        exactSolView.setText(String.format("%.5f", exactInt[fid]));
                        myCanvas.setMainFunction(mainFunction,fid);
                        coordSystem.setyFunc(mainFunction);
                        coordSystem.drawAxis();
                        coordSystem.drawFunc();
                        myCanvas.setCoordSystem(coordSystem);
                        break;
                    case 6:
                        functions[0] = "HM FUNK. 3";
//                        logSaveVec[33] = "" + 5;
                        selected_func = 5;
                        supSumSwitch.setChecked(false);
                        infSumSwitch.setChecked(false);
                        supSumErrorView.setText("");
                        infSumErrorView.setText("");
                        myCanvas.clearPoints();
                        myCanvas.clearFunc();

                        for (int i = 1; i < points.getCount(); i++) {
                            points.removePoint(i);
                        }

                        points.getPoint(0).setActive(true);


                        start = -1;
                        end = 1;

                        myCanvas.setEnd(end);
                        myCanvas.setStart(start);
                        // Testweise setzen der x-Werte
                        coordSystem.setxFunc(start, end);
                        mainFunction = HMFunction3;
                        fid = 5;
                        exactSolView.setText(String.format("%.5f", exactInt[fid]));
                        myCanvas.setMainFunction(mainFunction,fid);
                        coordSystem.setyFunc(mainFunction);
                        coordSystem.drawAxis();
                        coordSystem.drawFunc();
                        myCanvas.setCoordSystem(coordSystem);
                        break;
                }
                else {
                    //myCanvas.clearFunc();
                    switch (position) {
                        case 0:
                            break;

                        case 1:
                            functions[0] = "RUNGE FUNKTION";
                            selected_func = 0;/*
                            start = -5;
                            end = 5;

                            myCanvas.setEnd(end);
                            myCanvas.setStart(start);
                            // Testweise setzen der x-Werte
                            coordSystem.setxFunc(start, end);
                            mainFunction = rungeFunction;
                            fid = 0;
                            exactSolView.setText(String.format("%.5f", exactInt[fid]));
                            myCanvas.setMainFunction(mainFunction,fid);
                            coordSystem.setyFunc(mainFunction);
                            coordSystem.drawAxis();
                            coordSystem.drawFunc();*/
                            break;
                        case 2:
                            functions[0] = "SINUS FUNKTION";
                            selected_func = 1;/*
                            start = -1;
                            end = 1;

                            myCanvas.setEnd(end);
                            myCanvas.setStart(start);
                            // Testweise setzen der x-Werte
                            coordSystem.setxFunc(start, end);
                            mainFunction = trigonomialFunction;
                            fid = 1;
                            exactSolView.setText(String.format("%.5f", exactInt[fid]));
                            myCanvas.setMainFunction(mainFunction,fid);
                            coordSystem.setyFunc(mainFunction);
                            coordSystem.drawAxis();
                            coordSystem.drawFunc();
                            myCanvas.setCoordSystem(coordSystem);/*
                            break;

                        case 3:
                            functions[0] = "BETRAGS FUNKTION";
                            selected_func = 2;/*
                            start = -1;
                            end = 1;

                            myCanvas.setEnd(end);
                            myCanvas.setStart(start);
                            // Testweise setzen der x-Werte
                            coordSystem.setxFunc(start, end);
                            mainFunction = absFunction;
                            fid = 2;
                            exactSolView.setText(String.format("%.5f", exactInt[fid]));
                            myCanvas.setMainFunction(mainFunction,fid);
                            coordSystem.setyFunc(mainFunction);
                            coordSystem.drawAxis();
                            coordSystem.drawFunc();
                            myCanvas.setCoordSystem(coordSystem);*/
                            break;

                        case 4:
                            functions[0] = "HM FUNK. 1";
                            selected_func = 3;/*
                            start = -1;
                            end = 2;

                            myCanvas.setEnd(end);
                            myCanvas.setStart(start);
                            // Testweise setzen der x-Werte
                            coordSystem.setxFunc(start, end);
                            mainFunction = HMFunction1;
                            fid = 3;
                            exactSolView.setText(String.format("%.5f", exactInt[fid]));
                            myCanvas.setMainFunction(mainFunction,fid);
                            coordSystem.setyFunc(mainFunction);
                            coordSystem.drawAxis();
                            coordSystem.drawFunc();
                            myCanvas.setCoordSystem(coordSystem);*/
                            break;

                        case 5:
                            functions[0] = "HM FUNK. 2";
                            selected_func = 4;/*
                            start = -1;
                            end = 1;
                            myCanvas.setEnd(end);
                            myCanvas.setStart(start);
                            // Testweise setzen der x-Werte
                            coordSystem.setxFunc(start, end);
                            mainFunction = HMFunction2;
                            fid = 4;
                            exactSolView.setText(String.format("%.5f", exactInt[fid]));
                            myCanvas.setMainFunction(mainFunction,fid);
                            coordSystem.setyFunc(mainFunction);
                            coordSystem.drawAxis();
                            coordSystem.drawFunc();
                            myCanvas.setCoordSystem(coordSystem);*/
                            break;
                        case 6:
                            functions[0] = "HM FUNK. 3";
                            selected_func = 5;/*
                            start = -1;
                            end = 1;

                            myCanvas.setEnd(end);
                            myCanvas.setStart(start);
                            // Testweise setzen der x-Werte
                            coordSystem.setxFunc(start, end);
                            mainFunction = HMFunction3;
                            fid = 5;
                            exactSolView.setText(String.format("%.5f", exactInt[fid]));
                            myCanvas.setMainFunction(mainFunction,fid);
                            coordSystem.setyFunc(mainFunction);
                            coordSystem.drawAxis();
                            coordSystem.drawFunc();
                            myCanvas.setCoordSystem(coordSystem);*/
                            break;
                    }
                    load_in_progress = false;
                }
                functionSelecter.setSelection(0);
                myCanvas.invalidate();
                freeMemory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        // Einlesen und Einstellen der Seekbar
        barNumBins.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                //if(infSumSwitch.isChecked()||supSumSwitch.isChecked())
                {
                    // clean up
                    myCanvas.clearPoints();
                    // set num points
                    int numbins = seekBar.getProgress();
                    numBins.setText("" + numbins);
                    num_apoints = numbins + 1;
                    int numPoints = numbins + 1;
                    //equi
                    for(int i = 0; i < numPoints; i++) {
                        points.getPoint(i).setActive(true);
//                        points.getPoint(i).setIsPlaced(true);
                        points.getPoint(i).setIsAvailable(true);
                    }
                    if(points.getCount()> numPoints)
                    {
                        for (int i = numPoints; i < points.getCount(); i++) {
                            points.removePoint(i);
                        }
                    }
                    else
                    {
                        for (int i =  points.getCount(); i <numPoints; i++) {
                            points.addPoint();
                        }
                    }


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

                    supSumIntegralVaulue = 0;
                    if (myCanvas.getsupSumSwitch().isChecked() && points.getNOPP() >= 2) {

                        current = points.getFirstPointNumber();
                        next = points.getNextPointNumber(points.getFirstPointNumber());

                        for (int i = 0; i < points.getNOPP() - 1; i++) {

                            double middelXCoord = find_max((float)points.getPoint(next).getReelX(), (float)points.getPoint(current).getReelX());

                            supSumIntegralVaulue = supSumIntegralVaulue + mainFunction.evaluate(middelXCoord) * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX());
                            current = next;
                            next = points.getNextPointNumber(next);
                        }

                        supSumErrorView.setText("" + String.format("%.5f", supSumIntegralVaulue - exactInt[fid]));
                        supSumSolView.setText(""+ String.format("%.5f", supSumIntegralVaulue));
                    }

                    infSumIntegralVaulue = 0;
                    if (myCanvas.getinfSumSwitch().isChecked() && points.getNOPP() >= 2) {
                        points.sortPoints();


                        current = points.getFirstPointNumber();
                        next = points.getNextPointNumber(points.getFirstPointNumber());

                        for (int i = 0; i < points.getNOPP() - 1; i++) {
                            double middelXCoord = find_min((float)points.getPoint(next).getReelX(), (float)points.getPoint(current).getReelX());
                            infSumIntegralVaulue = infSumIntegralVaulue + mainFunction.evaluate(middelXCoord) * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX());
                            current = next;
                            next = points.getNextPointNumber(next);
                        }

                        infSumErrorView.setText("" +String.format("%.5f", infSumIntegralVaulue - exactInt[fid]));
                        infSumSolView.setText(""+ String.format("%.5f", infSumIntegralVaulue));

                    }

                    if(myCanvas.getinfSumSwitch().isChecked() && myCanvas.getinfSumSwitch().isChecked() && points.getNOPP() >= 2)
                        diffInfSumView.setText("" + String.format("%.5f", supSumIntegralVaulue-infSumIntegralVaulue));
                    else
                        diffInfSumView.setText("");

                    myCanvas.invalidate();

                    for (int i = 0; i < points.getCount(); i++) {
                        points.getPoint(i).setActive(false);
                    }

                }

                freeMemory();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(infSumSwitch.isChecked()||supSumSwitch.isChecked())
                {
                    // clean up
                    myCanvas.clearPoints();
                    // set num points
                    int numbins = seekBar.getProgress();
                    int numPoints = numbins + 1;
                    num_apoints = numbins + 1;
                    //equi
                    if(points.getCount()> numPoints)
                    {
                        for (int i = numPoints; i < points.getCount(); i++) {
                            points.removePoint(i);
                        }
                    }
                    else
                    {
                        for (int i =  points.getCount(); i <numPoints; i++) {
                            points.addPoint();
                        }
                    }


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
//                            points.getPoint(i).getxView().setText("" + Math.round(points.getPoint(i).getReelX() * 1e3) / 1e3);
//                            points.getPoint(i).getyView().setText("" + Math.round(points.getPoint(i).getReelY() * 1e3) / 1e3);
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

                    supSumIntegralVaulue = 0;
                    if (myCanvas.getsupSumSwitch().isChecked() && points.getNOPP() >= 2) {

                        current = points.getFirstPointNumber();
                        next = points.getNextPointNumber(points.getFirstPointNumber());

                        for (int i = 0; i < points.getNOPP() - 1; i++) {

                            double middelXCoord = find_max((float)points.getPoint(next).getReelX(), (float)points.getPoint(current).getReelX());

                            supSumIntegralVaulue = supSumIntegralVaulue + mainFunction.evaluate(middelXCoord) * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX());
                            current = next;
                            next = points.getNextPointNumber(next);
                        }

                        supSumErrorView.setText("" + String.format("%.5f", supSumIntegralVaulue - exactInt[fid]));
                        supSumSolView.setText("" + String.format("%.5f", supSumIntegralVaulue));
                    }
                    infSumIntegralVaulue = 0;
                    if (myCanvas.getinfSumSwitch().isChecked() && points.getNOPP() >= 2) {
                        points.sortPoints();


                        current = points.getFirstPointNumber();
                        next = points.getNextPointNumber(points.getFirstPointNumber());

                        for (int i = 0; i < points.getNOPP() - 1; i++) {
                            double middelXCoord = find_min((float)points.getPoint(next).getReelX(), (float)points.getPoint(current).getReelX());
                            infSumIntegralVaulue = infSumIntegralVaulue + mainFunction.evaluate(middelXCoord) * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX());
                            current = next;
                            next = points.getNextPointNumber(next);
                        }

                        infSumErrorView.setText("" + String.format("%.5f", infSumIntegralVaulue - exactInt[fid]));
                        infSumSolView.setText("" + String.format("%.5f", infSumIntegralVaulue));

                    }

                    if(myCanvas.getinfSumSwitch().isChecked() && myCanvas.getinfSumSwitch().isChecked() && points.getNOPP() >= 2)
                        diffInfSumView.setText("" + String.format("%.5f", supSumIntegralVaulue-infSumIntegralVaulue));
                    else
                        diffInfSumView.setText("");

                    myCanvas.invalidate();

                    for (int i = 0; i < points.getCount(); i++) {
                        points.getPoint(i).setActive(false);
                        //points.getPoint(i).getSwitch().setChecked(false);
                    }

                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(infSumSwitch.isChecked()||supSumSwitch.isChecked())
                {
                    // clean up
                    myCanvas.clearPoints();
                    // set num points
                    int numbins = seekBar.getProgress();
                    int numPoints = numbins + 1;
                    num_apoints = numbins + 1;
                    //equi
                    if(points.getCount()> numPoints)
                    {
                        for (int i = numPoints; i < points.getCount(); i++) {
                            points.removePoint(i);
                        }
                    }
                    else
                    {
                        for (int i =  points.getCount(); i <numPoints; i++) {
                            points.addPoint();
                        }
                    }


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
                            //points.getPoint(i).getxView().setText("" + Math.round(points.getPoint(i).getReelX() * 1e3) / 1e3);
                            //points.getPoint(i).getyView().setText("" + Math.round(points.getPoint(i).getReelY() * 1e3) / 1e3);
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

                    supSumIntegralVaulue = 0;
                    if (myCanvas.getsupSumSwitch().isChecked() && points.getNOPP() >= 2) {

                        current = points.getFirstPointNumber();
                        next = points.getNextPointNumber(points.getFirstPointNumber());

                        for (int i = 0; i < points.getNOPP() - 1; i++) {

                            double middelXCoord = find_max((float)points.getPoint(next).getReelX(), (float)points.getPoint(current).getReelX());

                            supSumIntegralVaulue = supSumIntegralVaulue + mainFunction.evaluate(middelXCoord) * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX());
                            current = next;
                            next = points.getNextPointNumber(next);
                        }

                        supSumErrorView.setText("" + String.format("%.5f", supSumIntegralVaulue -exactInt[fid]));
                        supSumSolView.setText("" + String.format("%.5f", supSumIntegralVaulue));
                    }
                    infSumIntegralVaulue = 0;
                    if (myCanvas.getinfSumSwitch().isChecked() && points.getNOPP() >= 2) {
                        points.sortPoints();


                        current = points.getFirstPointNumber();
                        next = points.getNextPointNumber(points.getFirstPointNumber());

                        for (int i = 0; i < points.getNOPP() - 1; i++) {
                            double middelXCoord = find_min((float)points.getPoint(next).getReelX(), (float)points.getPoint(current).getReelX());
                            infSumIntegralVaulue = infSumIntegralVaulue + mainFunction.evaluate(middelXCoord) * (points.getPoint(next).getReelX() - points.getPoint(current).getReelX());
                            current = next;
                            next = points.getNextPointNumber(next);
                        }

                        infSumErrorView.setText("" +String.format("%.5f", infSumIntegralVaulue - exactInt[fid]));
                        infSumSolView.setText("" + String.format("%.5f", infSumIntegralVaulue));
                    }
                    if(myCanvas.getinfSumSwitch().isChecked() && myCanvas.getinfSumSwitch().isChecked() && points.getNOPP() >= 2)
                        diffInfSumView.setText("" + String.format("%.5f", supSumIntegralVaulue-infSumIntegralVaulue));
                    else
                        diffInfSumView.setText("");

                    myCanvas.invalidate();

                    for (int i = 0; i < points.getCount(); i++) {
                        points.getPoint(i).setActive(false);
                        //points.getPoint(i).getSwitch().setChecked(false);
                    }

                }

                freeMemory();
            }
        });



        freeMemory();

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


    public void resetAll(View view) {
        supSumSwitch.setChecked(false);
        infSumSwitch.setChecked(false);
        barNumBins.setProgress(1);
        num_apoints = 2;

        myCanvas.clearPoints();
        myCanvas.clearFunc();

        for (int i = 1; i < points.getCount(); i++) {
            points.removePoint(i);
        }

        functions[0] = "RUNGE FUNKION";
        functionSelecter.setSelection(1);
        infSumSolView.setText("");
        supSumSolView.setText("");
        infSumErrorView.setText("");
        supSumErrorView.setText("");
        exactSolView.setText("");
        diffInfSumView.setText("");

        coordSystem.drawAxis();
        coordSystem.drawFunc();
        myCanvas.setCoordSystem(coordSystem);
        myCanvas.invalidate();

        freeMemory();

    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        if(MatNr.getText().length()!= 7){
            alertDialog.setMessage("Ihre Matrikelnummer sollte genau 7-stellig sein!");
            alertDialog.show();
        }
    }

}
