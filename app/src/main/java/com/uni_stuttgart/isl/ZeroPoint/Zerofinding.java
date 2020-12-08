package com.uni_stuttgart.isl.ZeroPoint;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import androidx.annotation.MainThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.uni_stuttgart.isl.CoolStuff.NumLinAlg;
import com.uni_stuttgart.isl.Function.Function;
import com.uni_stuttgart.isl.Function.MonomialPolynom;
import com.uni_stuttgart.isl.Function.NewtonDivergentFunction;
import com.uni_stuttgart.isl.Function.NewtonEndlessCircleFunction;
import com.uni_stuttgart.isl.Function.NewtonSometimesFunction;
import com.uni_stuttgart.isl.Intros.Intro_Zerofinding;
import com.uni_stuttgart.isl.Intros.PrefManager;
import com.uni_stuttgart.isl.MainActivity;
import com.uni_stuttgart.isl.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Zerofinding extends AppCompatActivity {
    private final int maxiter = 18;
    private ListView iterListView;
    private IterAdapter iterAdapter;
    private List iterList = new ArrayList<String>();
    private List xiList = new ArrayList<String>();
    private List fxiList = new ArrayList<String>();
    private List colorList = new ArrayList<Color>();
    private List colorListRes = new ArrayList<Resources>();
    private ImageButton pauseButton;// = (ImageButton) findViewById(R.id.pauseButton);
    private ImageButton stopButton;// = (ImageButton) findViewById(R.id.stopButton);
    private ImageButton forwardButton;
    private ImageButton backwardButton;
    private TextView colorTextView;
    double[][] xCoords = new double[1000][2];
    double[][] yCoords = new double[1000][2];
    // Darstellungsfläche
    private MyCanvas myCanvas;
    // Objekt zum erzeugen eines Koordinaten Systems
    private CoordSystem coordSystem;
    // Button zum erzeugen von neuen Integrationspunkten
    private double start = -3;
//    private start = Math.PI;
    private double end = -start;
    // Welches Koordiantensystem soll gewählt werden (welche Quadranten)
    private int coordArt;
    // Funktion die geplottet werden soll
   private Function mainFunction = new NewtonEndlessCircleFunction();
//    private Function mainFunction = new TrigonomialFunction(1, 1, 0, 0);
 //   private Function mainFunction = new MonomialPolynom(3, new double[]{1, 0, 0, -1});
  // private Function mainFunction = new NewtonSometimesFunction();
    // Toolbar
    private Toolbar toolbar;
    private int final_size;
    private boolean isRunning = false;
    private boolean pause = false;
    private boolean stop = true;
    private int iterCounter;
    private int stepCounter;
    private long sleeptime = 500;
    private boolean isMarksAllowed = false;
    private boolean isDrawItersAllowed = false;
    private boolean isBroadlyAnimation = false;
    private boolean isFineAnimation = false;
    private boolean waitforClick_fineAnimation = false;
    private boolean backwardFlag = false;
    private boolean backwardstepzeroflag = false;
    private PrefManager prefManager;
    private double accuracy = 1e-4;

    private Spinner functionSelecter;
    private CharSequence[] functions = {"ENDLESS CIRCLE", "Endless Circle", "Polynom 3. Grades", "Polynom 4. Grades", "Divergent", "Sometimes"};
    private double antiAccuracy = 1e5;

    private RadioGroup animationSpeedGroup;
    private SeekBar AccuracySeekBar;
    private TextView AccuracyTextView;
    private Spinner methodeSelecter;
    private CharSequence[] methods = {"NEWTON", "Newton", "Quasi Newton"};
    private int methodeflag = 1;


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
            prefManager.setReopenZerofindingIntro(false);
//            saveActivityToLog();
            Intent myIntent = new Intent(Zerofinding.this, Intro_Zerofinding.class);
            Zerofinding.this.startActivity(myIntent);
            freeMemory();
            finish();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.zerofinding);

        prefManager = new PrefManager(this);

        // Erzeugen der Toolbar
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        // Zurückbutton, was soll beim "Click" passieren
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start der Main
                Intent myIntent = new Intent(Zerofinding.this, MainActivity.class);
                Zerofinding.this.startActivity(myIntent);
                freeMemory();
                finish();
            }
        });

        animationSpeedGroup = (RadioGroup) findViewById(R.id.animationRadioGroup);

        AccuracySeekBar = (SeekBar) findViewById(R.id.SeekBarAccuracy);
        AccuracyTextView = (TextView) findViewById(R.id.TextViewAccuracy);

        pauseButton = (ImageButton) findViewById(R.id.pauseButton);
        stopButton = (ImageButton) findViewById(R.id.stopButton);
        forwardButton = (ImageButton) findViewById(R.id.forwardButton);
        backwardButton = (ImageButton) findViewById(R.id.backwardButton);

        colorTextView = (TextView) findViewById(R.id.colorTextView);
        colorTextView.setVisibility(View.INVISIBLE);

        pauseButton.setVisibility(View.GONE);
        forwardButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.INVISIBLE);

        RadioButton radioButtonFastAnimation = (RadioButton) findViewById(R.id.radioButtonFastAnimation);
        radioButtonFastAnimation.setChecked(true);

        // Erzeugen der Darstellungsfläche
        myCanvas = (MyCanvas) findViewById(R.id.mycanvas);
        myCanvas.setMaxiter(maxiter);
        myCanvas.setxCoords(xCoords);
        myCanvas.setMarksAllowed(isMarksAllowed);
        myCanvas.setColorList(colorList);


        // Farben definieren
        colorList.add(Color.rgb(210, 210, 210));
        colorList.add(Color.rgb(255, 255, 0));
        colorList.add(Color.rgb(240, 163, 255));
        colorList.add(Color.rgb(94, 241, 242));
        colorList.add(Color.rgb(0, 117, 220));
        colorList.add(Color.rgb(25, 25, 25));
        colorList.add(Color.rgb(255, 164, 5));
        colorList.add(Color.rgb(0, 153, 143));
        colorList.add(Color.rgb(43, 206, 72));
        colorList.add(Color.rgb(255, 0, 16));
        // nochmal die gleichen Farben für die Vierecke
        colorList.add(Color.rgb(210, 210, 210));
        colorList.add(Color.rgb(255, 255, 0));
        colorList.add(Color.rgb(240, 163, 255));
        colorList.add(Color.rgb(94, 241, 242));
        colorList.add(Color.rgb(0, 117, 220));
        colorList.add(Color.rgb(25, 25, 25));
        colorList.add(Color.rgb(255, 164, 5));
        colorList.add(Color.rgb(0, 153, 143));
        colorList.add(Color.rgb(43, 206, 72));
        colorList.add(Color.rgb(255, 0, 16));

        // Farbsymbol definieren - Kreise
        colorListRes.add(R.mipmap.ic_point_grey);
        colorListRes.add(R.mipmap.ic_point_yellow);
        colorListRes.add(R.mipmap.ic_point_amethyst);
        colorListRes.add(R.mipmap.ic_point_sky);
        colorListRes.add(R.mipmap.ic_point_blue);
        colorListRes.add(R.mipmap.ic_point_ebony);
        colorListRes.add(R.mipmap.ic_point_orpiment);
        colorListRes.add(R.mipmap.ic_point_tuerkis);
        colorListRes.add(R.mipmap.ic_point_green);
        colorListRes.add(R.mipmap.ic_point_red);
        // Vierecke/Raute
        colorListRes.add(R.mipmap.ic_square_grey);
        colorListRes.add(R.mipmap.ic_square_yellow);
        colorListRes.add(R.mipmap.ic_square_amethylst);
        colorListRes.add(R.mipmap.ic_square_sky);
        colorListRes.add(R.mipmap.ic_square_blue);
        colorListRes.add(R.mipmap.ic_square_ebony);
        colorListRes.add(R.mipmap.ic_square_orpiment);
        colorListRes.add(R.mipmap.ic_square_tuerkis);
        colorListRes.add(R.mipmap.ic_square_green);
        colorListRes.add(R.mipmap.ic_square_red);

        // Volles Koordinatensystem
        setCoordArt(4);

        // Funtkion über einen Spinner wählen
        functionSelecter = (Spinner) findViewById(R.id.FunctionSelecter);

        ArrayAdapter<CharSequence> adapter_SpinnerDimension = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, functions);
        // Specify the layout to use when the list of choices appears
        adapter_SpinnerDimension.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter_SpinnerDimension
        //to the spinner
        functionSelecter.setAdapter(adapter_SpinnerDimension);

        // Methode über einen Spinner wählen
        methodeSelecter = (Spinner) findViewById(R.id.MethodeSelecter);

        ArrayAdapter<CharSequence> adapter_SpinnerDimension2 = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, methods);
        // Specify the layout to use when the list of choices appears
        adapter_SpinnerDimension2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter_SpinnerDimension
        //to the spinner
        methodeSelecter.setAdapter(adapter_SpinnerDimension2);
        final LinearLayout iterviewlay =  findViewById(R.id.iterViewLayout);
        final LinearLayout opt = findViewById(R.id.optcol);
        myCanvas.post(new Runnable() {
            @Override
            public void run() {


                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;

                ViewGroup.LayoutParams ParamsCanvas = myCanvas.getLayoutParams();
                ViewGroup.LayoutParams ParamsOC = opt.getLayoutParams();
                ViewGroup.LayoutParams ParamsIL = iterviewlay.getLayoutParams();
                int rest_height = height- toolbar.getHeight();
                int opt_width = opt.getWidth();
                int opt_height = opt.getHeight();

                ParamsIL.height = rest_height -opt_height+100;

                if(width-opt_width-50<rest_height)
                    final_size=width-opt_width-50;
                else
                    final_size = rest_height;

                ParamsCanvas.height=final_size;
                ParamsCanvas.width=final_size;

                iterviewlay.setLayoutParams(ParamsIL);
                myCanvas.setLayoutParams(ParamsCanvas);

                final_size = myCanvas.getLayoutParams().width;

                // Anlegen des Koordinatensystems
                coordSystem = new CoordSystem((int) final_size / 2, (int) final_size / 2, (int) final_size / 2 - 50, (int) final_size / 2 - 50, coordArt, myCanvas);


                // Koordinatensystem+Integrationspunkte der Darstellungsfläche übergen
                myCanvas.setCoordSystem(coordSystem);





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
                                myCanvas.getCsPath().reset();

                                myCanvas.setFunctionChanged(true);
                                functions[0] = "ENDLESS CIRCLE";
                                mainFunction = new NewtonEndlessCircleFunction();
                                // Volles Koordinatensystem
                                setCoordArt(4);

                                // Anlegen des Koordinatensystems
                                coordSystem = new CoordSystem((int) final_size / 2, (int) final_size / 2, (int) final_size / 2 - 50, (int) final_size / 2 - 50, coordArt, myCanvas);

                                // Koordinatensystem+Integrationspunkte der Darstellungsfläche übergen
                                myCanvas.setCoordSystem(coordSystem);
                                myCanvas.clearFunc();
                                myCanvas.drawpoint((float) -10, (float) -10);

                                start = -3;
                                end = -start;

                                myCanvas.setEnd(end);
                                myCanvas.setStart(start);
                                // Testweise setzen der x-Werte
                                coordSystem.setxFunc(start, end);
                                coordSystem.setxGrad(start, end);
                                coordSystem.setyFunc(mainFunction);
                                coordSystem.drawFunc();
                                coordSystem.drawAxis();
                                stopfindingroot(view);
                                break;
                            case 2:
                                myCanvas.getCsPath().reset();

                                myCanvas.setFunctionChanged(true);
                                functions[0] = "POLYNOM 3. GRADES";
                                mainFunction = new MonomialPolynom(3, new double[]{0, 5, 0, 1});
                                // Volles Koordinatensystem
                                setCoordArt(4);

                                // Anlegen des Koordinatensystems
                                coordSystem = new CoordSystem((int) final_size / 2, (int) final_size / 2, (int) final_size / 2 - 50, (int) final_size / 2 - 50, coordArt, myCanvas);

                                // Koordinatensystem+Integrationspunkte der Darstellungsfläche übergen
                                myCanvas.setCoordSystem(coordSystem);
                                myCanvas.clearFunc();
                                myCanvas.drawpoint((float) -10, (float) -10);

                                start = -3;
                                end = -start;

                                myCanvas.setEnd(end);
                                myCanvas.setStart(start);
                                // Testweise setzen der x-Werte
                                coordSystem.setxFunc(start, end);
                                coordSystem.setxGrad(start, end);
                                coordSystem.setyFunc(mainFunction);
                                coordSystem.drawFunc();
                                coordSystem.drawAxis();
                                stopfindingroot(view);
                                break;
                            case 3:
                                myCanvas.getCsPath().reset();

                                myCanvas.setFunctionChanged(true);
                                functions[0] = "POLYNOM 4. GRADES";
                                mainFunction = new MonomialPolynom(4, new double[]{0, 50, 2, 0, 1});

                                // Volles Koordinatensystem
                                setCoordArt(4);

                                // Anlegen des Koordinatensystems
                                coordSystem = new CoordSystem((int) final_size / 2, (int) final_size / 2, (int) final_size / 2 - 50, (int) final_size / 2 - 50, coordArt, myCanvas);


                                // Koordinatensystem+Integrationspunkte der Darstellungsfläche übergen
                                myCanvas.setCoordSystem(coordSystem);
                                myCanvas.clearFunc();
                                myCanvas.drawpoint((float) -10, (float) -10);

                                start = -3;
                                end = -start;

                                myCanvas.setEnd(end);
                                myCanvas.setStart(start);
                                // Testweise setzen der x-Werte
                                coordSystem.setxFunc(start, end);
                                coordSystem.setxGrad(start, end);
                                coordSystem.setyFunc(mainFunction);
                                coordSystem.drawFunc();
                                coordSystem.drawAxis();
                                stopfindingroot(view);
                                break;
                            case 4:
                                myCanvas.getCsPath().reset();

                                myCanvas.setFunctionChanged(true);
                                functions[0] = "DIVERGENT";
                                mainFunction = new NewtonDivergentFunction();
                                // Volles Koordinatensystem
                                setCoordArt(4);

                                // Anlegen des Koordinatensystems
                                coordSystem = new CoordSystem((int) final_size / 2, (int) final_size / 2, (int) final_size / 2 - 50, (int) final_size / 2 - 50, coordArt, myCanvas);


                                // Koordinatensystem+Integrationspunkte der Darstellungsfläche übergen
                                myCanvas.setCoordSystem(coordSystem);
                                myCanvas.clearFunc();
                                myCanvas.drawpoint((float) -10, (float) -10);

                                start = -3;
                                end = -start;

                                myCanvas.setEnd(end);
                                myCanvas.setStart(start);
                                // Testweise setzen der x-Werte
                                coordSystem.setxFunc(start, end);
                                coordSystem.setxGrad(start, end);
                                coordSystem.setyFunc(mainFunction);
                                coordSystem.drawFunc();
                                coordSystem.drawAxis();
                                stopfindingroot(view);
                                break;
                            case 5:
                                myCanvas.getCsPath().reset();
                                myCanvas.setMaxiter(maxiter);
                                myCanvas.setFunctionChanged(true);
                                functions[0] = "SOMETIMES";
                                mainFunction = new NewtonSometimesFunction();
                                // Volles Koordinatensystem
                                setCoordArt(4);

                                // Anlegen des Koordinatensystem
                                coordSystem = new CoordSystem((int) final_size / 2, (int) final_size / 2, (int) final_size / 2 - 50, (int) final_size / 2 - 50, coordArt, myCanvas);

                                // Koordinatensystem+Integrationspunkte der Darstellungsfläche übergen
                                myCanvas.setCoordSystem(coordSystem);
                                myCanvas.clearFunc();
                                myCanvas.drawpoint((float) -10, (float) -10);
                                //xCoords[0][1] = coordSystem.getX0();
                                start = -2.8;
                                end = -start;

                                myCanvas.setEnd(end);
                                myCanvas.setStart(start);
                                // Testweise setzen der x-Werte
                                coordSystem.setxFunc(start, end);
                                coordSystem.setxGrad(start, end);
                                coordSystem.setyFunc(mainFunction);
                                coordSystem.drawFunc();
                                coordSystem.drawAxis();
                                stopfindingroot(view);
                                break;
                        }
                        functionSelecter.setSelection(0);
                        freeMemory();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });




                methodeSelecter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                                methods[0] = "NEWTON";
                                myCanvas.drawpoint((float) -10, (float) -10);
                                stopfindingroot(view);
                                methodeflag = 1;
                                break;

                            case 2:
                                methods[0] = "QUASI NEWTON";
                                myCanvas.drawpoint((float) -10, (float) -10);
                                stopfindingroot(view);
                                methodeflag = 2;
                                break;
                        }
                        methodeSelecter.setSelection(0);
                        freeMemory();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });


                myCanvas.setIterCounter(iterCounter);
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

    public void loadActivity() {
        AccuracySeekBar.setMax(8);
        AccuracySeekBar.setProgress(4);
        AccuracySeekBar.setEnabled(true);
        AccuracyTextView.setText("1e-" + AccuracySeekBar.getProgress());

        // Einlesen und Einstellen der Seekbar
        AccuracySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                AccuracyTextView.setText("1e-" + AccuracySeekBar.getProgress());
                accuracy = Math.pow(10,-1.0*AccuracySeekBar.getProgress());
                antiAccuracy = Math.pow(10,1.0*AccuracySeekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                AccuracyTextView.setText("1e-" + AccuracySeekBar.getProgress());
                accuracy = Math.pow(10,-1.0*AccuracySeekBar.getProgress());
                antiAccuracy = Math.pow(10,1.0*AccuracySeekBar.getProgress());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                AccuracyTextView.setText("1e-" + AccuracySeekBar.getProgress());
                accuracy = Math.pow(10,-1.0*AccuracySeekBar.getProgress());
                antiAccuracy = Math.pow(10,1.0*AccuracySeekBar.getProgress());
            }
        });


        myCanvas.clearFunc();
        myCanvas.drawpoint((float) -10, (float) -10);

        myCanvas.setEnd(end);
        myCanvas.setStart(start);
        // Testweise setzen der x-Werte
        coordSystem.setxFunc(start, end);
        coordSystem.setxGrad(start, end);
        coordSystem.setyFunc(mainFunction);
        coordSystem.drawFunc();
        coordSystem.drawAxis();
        myCanvas.setCoordSystem(coordSystem);
        freeMemory();

        iterAdapter = new IterAdapter(getApplicationContext(), iterList, xiList, fxiList, colorListRes);

        iterListView = (ListView) findViewById(R.id.iterView);
        iterListView.setAdapter(iterAdapter);

        // ViewList mtiteilen, dass sich Daten geändert haben
        iterAdapter.notifyDataSetChanged();
    }

    public double solve_newton(final int iterationStart) {
        final double root = 0;
        myCanvas.drawpoint((float) -10, (float) -10);
        isRunning = true;
        myCanvas.setTouchPermission(false);
        AccuracySeekBar.setEnabled(false);
        stop = false;

        new Thread(new Runnable() {
            boolean flag1 = false;
            boolean flag2 = false;
            boolean flag3 = false;
            int step = stepCounter;
            double rootGradient;
            double rootGradient_old;
            double dfx;
            double yAchsenAbschnittGradient;

            @Override
            public void run() {
                while (iterCounter < maxiter && !pause && !stop) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Do all the stuff you need here
                            if (!pause && iterCounter < maxiter) {
                                // Gradienten am Punkt x_i bestimmen und zeichnen
                                if (step == 0 && !waitforClick_fineAnimation) {
                                    if (backwardFlag) {
                                        myCanvas.drawpoint((float) xCoords[iterCounter][1], yCoords[iterCounter][1]);
                                        backwardFlag = false;
                                        flag2 = true;
                                        step = 1;
                                        stepCounter = step;
                                        waitforClick_fineAnimation = true;
                                        myCanvas.clearExtraLine();
                                    } else {
                                        // Alles vorherige löschen
                                        myCanvas.drawpoint((float) -10, (float) -10);
                                        myCanvas.clearGrad();
                                        myCanvas.clearExtraLine();

                                        //Gradient berechnen und zeichnen
                                        xCoords[iterCounter][1] = coordSystem.getXTransFromReel(xCoords[iterCounter][0]);
                                        dfx = coordSystem.getFunction().derivation(xCoords[iterCounter][0]);
                                        yAchsenAbschnittGradient = coordSystem.getFunction().evaluate(xCoords[iterCounter][0]) - dfx * xCoords[iterCounter][0];
                                        rootGradient = -yAchsenAbschnittGradient / dfx;
                                        myCanvas.setRootGradientReal(rootGradient);
                                        myCanvas.setRootGradientTrans(coordSystem.getXTransFromReel(rootGradient));
                                        MonomialPolynom gradient = new MonomialPolynom(1, new double[]{yAchsenAbschnittGradient, dfx});
                                        coordSystem.setyGrad(gradient);
                                        coordSystem.drawGrad();
                                        //Punkt zeichnen, an dem der Gradient anliegt (aktueller IterPunkt)
                                        myCanvas.drawpoint((float) xCoords[iterCounter][1], coordSystem.getYTransFromReel(mainFunction.evaluate(xCoords[iterCounter][0])));

                                        myCanvas.invalidate();
                                    }

                                    // Nächster Schritt im Newtonverfahren, zumindest was das Zeichnen angeht
                                    if (!isBroadlyAnimation && !isFineAnimation) {
                                        step = 1;
                                        stepCounter = step;
                                    } else if (isBroadlyAnimation) {
                                        step = 1;
                                        stepCounter = step;
                                    } else if (isFineAnimation) {
                                        step = 1;
                                        stepCounter = step;
                                        waitforClick_fineAnimation = true;
                                        flag2 = true;
                                        pauseButton.setVisibility(View.GONE);
                                        forwardButton.setVisibility(View.VISIBLE);
                                    }

                                    Log.e("TAG", "EndStep0: " + "step. = " + stepCounter + ", iterCounter = " + iterCounter + ", backward = " + backwardFlag + ", bwszf = " + backwardstepzeroflag);
                                }
                                // Nullstelle des Gradienten zeichnen
                                else if (step == 1 && !waitforClick_fineAnimation) {
                                    if (backwardFlag) {
                                        myCanvas.clearExtraLine();
                                        backwardFlag = false;
                                    }
                                    if (backwardstepzeroflag){
                                        coordSystem.drawGrad();
//                                        myCanvas.drawpoint((float) xCoords[0][1], yCoords[0][1]);

                                        // Zur Sicherheit nochmal den Gradienten Zeichnen
                                        dfx = coordSystem.getFunction().derivation(xCoords[iterCounter][0]);
                                        yAchsenAbschnittGradient = coordSystem.getFunction().evaluate(xCoords[iterCounter][0]) - dfx * xCoords[iterCounter][0];
                                        // Nullstelle
                                        rootGradient = -yAchsenAbschnittGradient / dfx;
                                        Log.e("TAG", "rootGradient = " + rootGradient + "; Max = " + coordSystem.getReelXMax());
                                        if (Math.abs(rootGradient)>= Math.abs(coordSystem.getReelXMax())){
                                            Zerofinding.this.runOnUiThread(new Runnable() {
                                                public void run() {
                                                    Toast.makeText(Zerofinding.this, "Neue Iterierte außerhalb des betrachteten Intervalls!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                        myCanvas.setRootGradientReal(rootGradient);
                                        myCanvas.setRootGradientTrans(coordSystem.getXTransFromReel(rootGradient));
                                        MonomialPolynom gradient = new MonomialPolynom(1, new double[]{yAchsenAbschnittGradient, dfx});
                                        myCanvas.clearGrad();
                                        myCanvas.clearExtraLine();
                                        coordSystem.setyGrad(gradient);
                                        coordSystem.drawGrad();
                                        // Nullstelle des Gradienten einzeichnen
                                        myCanvas.drawpoint((float) myCanvas.getRootGradientTrans(), (float) coordSystem.getY0());
                                        myCanvas.setDrawCoords(rootGradient, 0);


                                        backwardstepzeroflag = false;
                                        waitforClick_fineAnimation = true;
                                        flag2 = true;
                                    }
                                    else {
                                        // Zur Sicherheit nochmal den Gradienten Zeichnen
                                        dfx = coordSystem.getFunction().derivation(xCoords[iterCounter][0]);
                                        yAchsenAbschnittGradient = coordSystem.getFunction().evaluate(xCoords[iterCounter][0]) - dfx * xCoords[iterCounter][0];
                                        rootGradient = -yAchsenAbschnittGradient / dfx;
                                        if (Math.abs(rootGradient)>= Math.abs(coordSystem.getReelXMax())){
                                            if (coordSystem.getFunction() instanceof NewtonSometimesFunction) {
                                                Zerofinding.this.runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Toast.makeText(Zerofinding.this, "Neue Iterierte außerhalb des Defintionsbereichs! Bitte neuen Startpunkt wählen!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                flag2 = true;
                                            }
                                            else {
                                                Zerofinding.this.runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Toast.makeText(Zerofinding.this, "Neue Iterierte außerhalb des betrachteten Intervalls!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                        myCanvas.setRootGradientReal(rootGradient);
                                        myCanvas.setRootGradientTrans(coordSystem.getXTransFromReel(rootGradient));
                                        MonomialPolynom gradient = new MonomialPolynom(1, new double[]{yAchsenAbschnittGradient, dfx});
                                        myCanvas.clearGrad();
                                        myCanvas.clearExtraLine();
                                        coordSystem.setyGrad(gradient);
                                        coordSystem.drawGrad();
                                        // Nullstelle des Gradienten einzeichnen
                                        myCanvas.drawpoint((float) myCanvas.getRootGradientTrans(), (float) coordSystem.getY0());
                                        myCanvas.setDrawCoords(rootGradient, 0);
                                    }
                                    if (!isBroadlyAnimation && !isFineAnimation) {
                                        step = 2;
                                        stepCounter = step;
                                    } else if (isBroadlyAnimation) {
                                        step = 2;
                                        stepCounter = step;
                                    } else if (isFineAnimation) {
                                        step = 2;
                                        stepCounter = step;
                                        waitforClick_fineAnimation = true;
                                        flag2 = true;
                                        pauseButton.setVisibility(View.GONE);
                                        forwardButton.setVisibility(View.VISIBLE);
                                    }

                                    Log.e("TAG", "EndStep1: " + "step. = " + stepCounter + ", iterCounter = " + iterCounter + ", backward = " + backwardFlag + ", bwszf = " + backwardstepzeroflag);
                                    //Extraline zeichnen
                                } else if (step == 2 && !waitforClick_fineAnimation) {
                                    if (backwardFlag) {
                                        myCanvas.drawpoint((float) -10, (float) -10);
                                        myCanvas.clearGrad();
                                        myCanvas.clearExtraLine();

                                        dfx = coordSystem.getFunction().derivation(xCoords[iterationStart][0]);
                                        yAchsenAbschnittGradient = coordSystem.getFunction().evaluate(xCoords[iterationStart][0]) - dfx * xCoords[iterationStart][0];
                                        rootGradient = -yAchsenAbschnittGradient / dfx;

                                        myCanvas.setRootGradientReal(rootGradient);
                                        myCanvas.setRootGradientTrans(coordSystem.getXTransFromReel(rootGradient));

                                        MonomialPolynom gradient = new MonomialPolynom(1, new double[]{yAchsenAbschnittGradient, dfx});
                                        coordSystem.setyGrad(gradient);
                                        coordSystem.drawGrad();
                                        myCanvas.drawXtoY_extraLine((float) myCanvas.getRootGradientTrans(), (float) coordSystem.getY0(), (float) myCanvas.getRootGradientTrans(), (float) coordSystem.getYTransFromReel(coordSystem.getFunction().evaluate(myCanvas.getRootGradientReal())));
                                        myCanvas.invalidate();

                                        // Versuch
                                        iterList.clear();
                                        xiList.clear();
                                        fxiList.clear();
                                        iterListView = null;
                                        iterListView = (ListView) findViewById(R.id.iterView);
                                        iterListView.setAdapter(iterAdapter);
                                        for (int i = 0; i <= iterCounter; i++){
                                            iterList.add(i);
                                            xiList.add(Math.round(xCoords[i][0] * antiAccuracy) / antiAccuracy);
                                            fxiList.add(Math.round(yCoords[i][0] * antiAccuracy) / antiAccuracy);
                                            // ViewList mitteilen, dass sich Daten geändert haben
                                            iterAdapter.notifyDataSetChanged();
                                            // In der Liste soll auf die aktuelle Iteration gesprungen werden
                                            iterListView.setSelection(i);
                                        }

                                        backwardFlag = false;
                                    }
                                    else {
                                        myCanvas.drawpoint((float) -10, (float) -10);
                                        coordSystem.drawGrad();
                                        myCanvas.drawXtoY_extraLine((float) myCanvas.getRootGradientTrans(), (float) coordSystem.getY0(),
                                                (float) myCanvas.getRootGradientTrans(), (float) coordSystem.getYTransFromReel(coordSystem.getFunction().evaluate(myCanvas.getRootGradientReal())));

                                    }
                                    if (!isBroadlyAnimation && !isFineAnimation) {
                                        step = 3;
                                        stepCounter = step;
                                    } else if (isBroadlyAnimation) {
                                        step = 3;
                                        stepCounter = step;
                                    } else if (isFineAnimation) {
                                        step = 3;
                                        stepCounter = step;
                                        waitforClick_fineAnimation = true;
                                        flag2 = true;
                                        pauseButton.setVisibility(View.GONE);
                                        forwardButton.setVisibility(View.VISIBLE);
                                    }

                                    Log.e("TAG", "EndStep2: " + "step. = " + stepCounter + ", iterCounter = " + iterCounter + ", backward = " + backwardFlag + ", bwszf = " + backwardstepzeroflag);
                                    // Neuer Punkt zeichnen
                                } else if (step == 3 && !waitforClick_fineAnimation) {
                                    if (backwardFlag) {
                                        myCanvas.clearGrad();
                                        myCanvas.drawpoint(xCoords[iterCounter+1][1], yCoords[iterCounter + 1][1]);

                                        backwardFlag = false;
                                        waitforClick_fineAnimation = true;
                                        iterCounter = iterCounter + 1;
                                        myCanvas.setIterCounter(iterCounter);
                                        myCanvas.clearExtraLine();
                                    }
                                    else {
                                        coordSystem.drawGrad();
                                        myCanvas.drawXtoY_extraLine((float) myCanvas.getRootGradientTrans(), (float) coordSystem.getY0(),
                                                (float) myCanvas.getRootGradientTrans(), (float) coordSystem.getYTransFromReel(coordSystem.getFunction().evaluate(myCanvas.getRootGradientReal())));
                                        // Neue x-Koordiante mit Newton ausrechen
                                        xCoords[iterCounter + 1][0] = NumLinAlg.Newton(xCoords[iterCounter][0], mainFunction);
                                        xCoords[iterCounter + 1][1] = coordSystem.getXTransFromReel(xCoords[iterCounter + 1][0]);
                                        // Funktion an der neuen x-Koordinate auswerten
                                        yCoords[iterCounter + 1][0] = mainFunction.evaluate(xCoords[iterCounter + 1][0]);
                                        yCoords[iterCounter + 1][1] = coordSystem.getYTransFromReel(yCoords[iterCounter + 1][0]);
                                        // Mit den neuen (x,f(x)) Werten den neuen punkt zeichnen
                                        myCanvas.drawpoint(xCoords[iterCounter + 1][1], yCoords[iterCounter + 1][1]);

                                        myCanvas.setDrawCoords(xCoords[iterCounter + 1][0], yCoords[iterCounter + 1][0]);
                                        // Iterationsanzahl erhöhen
                                        iterCounter = iterCounter + 1;
                                        myCanvas.setIterCounter(iterCounter);
//                                        // Neue Werte an die ViewList übergeben
//                                        iterList.add(iterCounter);
//                                        xiList.add(Math.round(xCoords[iterCounter][0] * antiAccuracy) / antiAccuracy);
//                                        fxiList.add(Math.round(yCoords[iterCounter][0] * antiAccuracy) / antiAccuracy);
//                                        // ViewList mtiteilen, dass sich Daten geändert haben
//                                        iterAdapter.notifyDataSetChanged();
//                                        // In der Liste soll auf die aktuelle Iteration gesprungen werden
//                                        iterListView.setSelection(iterCounter);

                                        // Versuch
                                        iterList.clear();
                                        xiList.clear();
                                        fxiList.clear();
                                        iterListView = null;
                                        iterListView = (ListView) findViewById(R.id.iterView);
                                        iterListView.setAdapter(iterAdapter);
                                        for (int i = 0; i <= iterCounter; i++){
                                            iterList.add(i);
                                            xiList.add(Math.round(xCoords[i][0] * antiAccuracy) / antiAccuracy);
                                            fxiList.add(Math.round(yCoords[i][0] * antiAccuracy) / antiAccuracy);
                                            // ViewList mtiteilen, dass sich Daten geändert haben
                                            iterAdapter.notifyDataSetChanged();
                                            // In der Liste soll auf die aktuelle Iteration gesprungen werden
                                            iterListView.setSelection(i);
                                        }
                                    }
                                        if (!isBroadlyAnimation && !isFineAnimation) {
                                            step = 0;
                                            stepCounter = step;
                                        } else if (isBroadlyAnimation) {
                                            step = 0;
                                            stepCounter = step;
                                            flag2 = true;
                                            pauseButton.setVisibility(View.GONE);
                                            forwardButton.setVisibility(View.VISIBLE);
                                        } else if (isFineAnimation) {
                                            step = 0;
                                            stepCounter = step;
                                            waitforClick_fineAnimation = true;
                                            flag2 = true;
                                            pauseButton.setVisibility(View.GONE);
                                            forwardButton.setVisibility(View.VISIBLE);
                                        }

                                    Log.e("TAG", "EndStep3: " + "step. = " + stepCounter + ", iterCounter = " + iterCounter + ", backward = " + backwardFlag + ", bwszf = " + backwardstepzeroflag);
                                }

                                // Abbruch, wenn wir nahe genug an der Null sind
                                if (Math.abs(yCoords[iterCounter][0]) < accuracy) {
                                    forwardButton.setVisibility(View.GONE);
                                    pauseButton.setVisibility(View.GONE);
                                    stopButton.setVisibility(View.VISIBLE);
                                    myCanvas.clearGrad();
                                    if (isDrawItersAllowed) {
                                        myCanvas.drawpoint(-10, -10);
                                    }
                                    flag1 = true;
                                    Zerofinding.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(Zerofinding.this, "Nullstelle erfolgreich gefunden!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                            if (iterCounter >= maxiter) {
                                Zerofinding.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(Zerofinding.this, "Maximale Anzahl an Iterierten erreicht!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                forwardButton.setVisibility(View.GONE);
                                pauseButton.setVisibility(View.GONE);
                                stopButton.setVisibility(View.VISIBLE);
                                flag1 = true;
                            }
                        }
                    });
                    try {
                        if (flag1) {
                            pause = true;
                            isRunning = false;
                            stop = true;
                            flag1 = false;
                        } else if (flag2) {
                            pause = true;
                            isRunning = false;
                            stop = false;
                            flag2 = false;
                        } else {
                            Thread.sleep(sleeptime);
                        }
                    } catch (InterruptedException e) {
                        Thread.interrupted();
                        e.printStackTrace();
                    }
                }
                isRunning = false;
                stop = true;

                freeMemory();
            }
        }).start();


        return root;
    }

    public double solve_quasinewton(final int iterationStart) {
        final double root = 0;
        myCanvas.drawpoint((float) -10, (float) -10);
        isRunning = true;
        myCanvas.setTouchPermission(false);
        AccuracySeekBar.setEnabled(false);
        stop = false;

        new Thread(new Runnable() {
            boolean flag1 = false;
            boolean flag2 = false;
            boolean flag3 = false;
            int step = stepCounter;
            double rootGradient;
            double rootGradient_old;
            double dfx;
            double yAchsenAbschnittGradient;

            @Override
            public void run() {
                while (iterCounter < maxiter && !pause && !stop) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Do all the stuff you need here
                            if (!pause && iterCounter < maxiter) {
                                // Gradienten am Punkt x_i bestimmen und zeichnen
                                if (step == 0 && !waitforClick_fineAnimation) {
                                    if (backwardFlag) {
                                        myCanvas.drawpoint((float) xCoords[iterCounter][1], yCoords[iterCounter][1]);
                                        backwardFlag = false;
                                        flag2 = true;
                                        step = 1;
                                        stepCounter = step;
                                        waitforClick_fineAnimation = true;
                                        myCanvas.clearExtraLine();
                                    } else {
                                        // Alles vorherige löschen
                                        myCanvas.drawpoint((float) -10, (float) -10);
                                        myCanvas.clearGrad();
                                        myCanvas.clearExtraLine();

                                        xCoords[iterCounter][1] = coordSystem.getXTransFromReel(xCoords[iterCounter][0]);
                                        //Tangente berechnen und zeichnen
                                        dfx = coordSystem.getFunction().derivation(xCoords[0][0]);
                                        yAchsenAbschnittGradient = coordSystem.getFunction().evaluate(xCoords[iterCounter][0]) - dfx * xCoords[iterCounter][0];
                                        rootGradient = -yAchsenAbschnittGradient / dfx;
                                        myCanvas.setRootGradientReal(rootGradient);
                                        myCanvas.setRootGradientTrans(coordSystem.getXTransFromReel(rootGradient));
                                        MonomialPolynom gradient = new MonomialPolynom(1, new double[]{yAchsenAbschnittGradient, dfx});
                                        coordSystem.setyGrad(gradient);
                                        coordSystem.drawGrad();
                                        //Punkt zeichnen, an dem der Tangente anliegt (aktueller IterPunkt)
                                        myCanvas.drawpoint((float) xCoords[iterCounter][1], coordSystem.getYTransFromReel(mainFunction.evaluate(xCoords[iterCounter][0])));

                                        myCanvas.invalidate();
                                    }

                                    // Nächster Schritt im Newtonverfahren, zumindest was das Zeichnen angeht
                                    if (!isBroadlyAnimation && !isFineAnimation) {
                                        step = 1;
                                        stepCounter = step;
                                    } else if (isBroadlyAnimation) {
                                        step = 1;
                                        stepCounter = step;
                                    } else if (isFineAnimation) {
                                        step = 1;
                                        stepCounter = step;
                                        waitforClick_fineAnimation = true;
                                        flag2 = true;
                                        pauseButton.setVisibility(View.GONE);
                                        forwardButton.setVisibility(View.VISIBLE);
                                    }

                                    Log.e("TAG", "EndStep0: " + "step. = " + stepCounter + ", iterCounter = " + iterCounter + ", backward = " + backwardFlag + ", bwszf = " + backwardstepzeroflag);
                                }
                                // Nullstelle des Gradienten zeichnen
                                else if (step == 1 && !waitforClick_fineAnimation) {
                                    if (backwardFlag) {
                                        myCanvas.clearExtraLine();
                                        backwardFlag = false;
                                    }
                                    if (backwardstepzeroflag){
                                        coordSystem.drawGrad();
//                                        myCanvas.drawpoint((float) xCoords[0][1], yCoords[0][1]);

                                        // Zur Sicherheit nochmal den Gradienten Zeichnen
                                        dfx = coordSystem.getFunction().derivation(xCoords[0][0]);
                                        yAchsenAbschnittGradient = coordSystem.getFunction().evaluate(xCoords[iterCounter][0]) - dfx * xCoords[iterCounter][0];
                                        // Nullstelle
                                        rootGradient = -yAchsenAbschnittGradient / dfx;
                                        Log.e("TAG", "rootGradient = " + rootGradient + "; Max = " + coordSystem.getReelXMax());
                                        if (Math.abs(rootGradient)>= Math.abs(coordSystem.getReelXMax())){
                                            Zerofinding.this.runOnUiThread(new Runnable() {
                                                public void run() {
                                                    Toast.makeText(Zerofinding.this, "Neue Iterierte außerhalb des betrachteten Intervalls!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                        myCanvas.setRootGradientReal(rootGradient);
                                        myCanvas.setRootGradientTrans(coordSystem.getXTransFromReel(rootGradient));
                                        MonomialPolynom gradient = new MonomialPolynom(1, new double[]{yAchsenAbschnittGradient, dfx});
                                        myCanvas.clearGrad();
                                        myCanvas.clearExtraLine();
                                        coordSystem.setyGrad(gradient);
                                        coordSystem.drawGrad();
                                        // Nullstelle des Gradienten einzeichnen
                                        myCanvas.drawpoint((float) myCanvas.getRootGradientTrans(), (float) coordSystem.getY0());
                                        myCanvas.setDrawCoords(rootGradient, 0);


                                        backwardstepzeroflag = false;
                                        waitforClick_fineAnimation = true;
                                        flag2 = true;
                                    }
                                    else {
                                        // Zur Sicherheit nochmal den Gradienten Zeichnen
                                        dfx = coordSystem.getFunction().derivation(xCoords[0][0]);
                                        yAchsenAbschnittGradient = coordSystem.getFunction().evaluate(xCoords[iterCounter][0]) - dfx * xCoords[iterCounter][0];
                                        rootGradient = -yAchsenAbschnittGradient / dfx;
                                        if (Math.abs(rootGradient)>= Math.abs(coordSystem.getReelXMax())){
                                            if (coordSystem.getFunction() instanceof NewtonSometimesFunction) {
                                                Zerofinding.this.runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Toast.makeText(Zerofinding.this, "Neue Iterierte außerhalb des Defintionsbereichs! Bitte neuen Startpunkt wählen!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                flag2 = true;
                                            }
                                            else {
                                                Zerofinding.this.runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Toast.makeText(Zerofinding.this, "Neue Iterierte außerhalb des betrachteten Intervalls!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                        myCanvas.setRootGradientReal(rootGradient);
                                        myCanvas.setRootGradientTrans(coordSystem.getXTransFromReel(rootGradient));
                                        MonomialPolynom gradient = new MonomialPolynom(1, new double[]{yAchsenAbschnittGradient, dfx});
                                        myCanvas.clearGrad();
                                        myCanvas.clearExtraLine();
                                        coordSystem.setyGrad(gradient);
                                        coordSystem.drawGrad();
                                        // Nullstelle des Gradienten einzeichnen
                                        myCanvas.drawpoint((float) myCanvas.getRootGradientTrans(), (float) coordSystem.getY0());
                                        myCanvas.setDrawCoords(rootGradient, 0);
                                    }
                                    if (!isBroadlyAnimation && !isFineAnimation) {
                                        step = 2;
                                        stepCounter = step;
                                    } else if (isBroadlyAnimation) {
                                        step = 2;
                                        stepCounter = step;
                                    } else if (isFineAnimation) {
                                        step = 2;
                                        stepCounter = step;
                                        waitforClick_fineAnimation = true;
                                        flag2 = true;
                                        pauseButton.setVisibility(View.GONE);
                                        forwardButton.setVisibility(View.VISIBLE);
                                    }

                                    Log.e("TAG", "EndStep1: " + "step. = " + stepCounter + ", iterCounter = " + iterCounter + ", backward = " + backwardFlag + ", bwszf = " + backwardstepzeroflag);
                                    //Extraline zeichnen
                                } else if (step == 2 && !waitforClick_fineAnimation) {
                                    if (backwardFlag) {
                                        myCanvas.drawpoint((float) -10, (float) -10);
                                        myCanvas.clearGrad();
                                        myCanvas.clearExtraLine();

                                        dfx = coordSystem.getFunction().derivation(xCoords[0][0]);
                                        yAchsenAbschnittGradient = coordSystem.getFunction().evaluate(xCoords[iterCounter][0]) - dfx * xCoords[iterCounter][0];
                                        rootGradient = -yAchsenAbschnittGradient / dfx;

                                        myCanvas.setRootGradientReal(rootGradient);
                                        myCanvas.setRootGradientTrans(coordSystem.getXTransFromReel(rootGradient));

                                        MonomialPolynom gradient = new MonomialPolynom(1, new double[]{yAchsenAbschnittGradient, dfx});
                                        coordSystem.setyGrad(gradient);
                                        coordSystem.drawGrad();
                                        myCanvas.drawXtoY_extraLine((float) myCanvas.getRootGradientTrans(), (float) coordSystem.getY0(), (float) myCanvas.getRootGradientTrans(), (float) coordSystem.getYTransFromReel(coordSystem.getFunction().evaluate(myCanvas.getRootGradientReal())));
                                        myCanvas.invalidate();

                                        // Versuch
                                        iterList.clear();
                                        xiList.clear();
                                        fxiList.clear();
                                        iterListView = null;
                                        iterListView = (ListView) findViewById(R.id.iterView);
                                        iterListView.setAdapter(iterAdapter);
                                        for (int i = 0; i <= iterCounter; i++){
                                            iterList.add(i);
                                            xiList.add(Math.round(xCoords[i][0] * antiAccuracy) / antiAccuracy);
                                            fxiList.add(Math.round(yCoords[i][0] * antiAccuracy) / antiAccuracy);
                                            // ViewList mitteilen, dass sich Daten geändert haben
                                            iterAdapter.notifyDataSetChanged();
                                            // In der Liste soll auf die aktuelle Iteration gesprungen werden
                                            iterListView.setSelection(i);
                                        }

                                        backwardFlag = false;
                                    }
                                    else {
                                        myCanvas.drawpoint((float) -10, (float) -10);
                                        coordSystem.drawGrad();
                                        myCanvas.drawXtoY_extraLine((float) myCanvas.getRootGradientTrans(), (float) coordSystem.getY0(),
                                                (float) myCanvas.getRootGradientTrans(), (float) coordSystem.getYTransFromReel(coordSystem.getFunction().evaluate(myCanvas.getRootGradientReal())));

                                    }
                                    if (!isBroadlyAnimation && !isFineAnimation) {
                                        step = 3;
                                        stepCounter = step;
                                    } else if (isBroadlyAnimation) {
                                        step = 3;
                                        stepCounter = step;
                                    } else if (isFineAnimation) {
                                        step = 3;
                                        stepCounter = step;
                                        waitforClick_fineAnimation = true;
                                        flag2 = true;
                                        pauseButton.setVisibility(View.GONE);
                                        forwardButton.setVisibility(View.VISIBLE);
                                    }

                                    Log.e("TAG", "EndStep2: " + "step. = " + stepCounter + ", iterCounter = " + iterCounter + ", backward = " + backwardFlag + ", bwszf = " + backwardstepzeroflag);
                                    // Neuer Punkt zeichnen
                                } else if (step == 3 && !waitforClick_fineAnimation) {
                                    if (backwardFlag) {
                                        myCanvas.clearGrad();
                                        myCanvas.drawpoint(xCoords[iterCounter+1][1], yCoords[iterCounter + 1][1]);

                                        backwardFlag = false;
                                        waitforClick_fineAnimation = true;
                                        iterCounter = iterCounter + 1;
                                        myCanvas.setIterCounter(iterCounter);
                                        myCanvas.clearExtraLine();
                                    }
                                    else {
                                        coordSystem.drawGrad();
                                        myCanvas.drawXtoY_extraLine((float) myCanvas.getRootGradientTrans(), (float) coordSystem.getY0(),
                                                (float) myCanvas.getRootGradientTrans(), (float) coordSystem.getYTransFromReel(coordSystem.getFunction().evaluate(myCanvas.getRootGradientReal())));
                                        // Neue x-Koordiante mit Newton ausrechen
                                        xCoords[iterCounter + 1][0] = NumLinAlg.QuasiNewton(xCoords[iterCounter][0], xCoords[0][0], mainFunction);
                                        xCoords[iterCounter + 1][1] = coordSystem.getXTransFromReel(xCoords[iterCounter + 1][0]);
                                        // Funktion an der neuen x-Koordinate auswerten
                                        yCoords[iterCounter + 1][0] = mainFunction.evaluate(xCoords[iterCounter + 1][0]);
                                        yCoords[iterCounter + 1][1] = coordSystem.getYTransFromReel(yCoords[iterCounter + 1][0]);
                                        // Mit den neuen (x,f(x)) Werten den neuen punkt zeichnen
                                        myCanvas.drawpoint(xCoords[iterCounter + 1][1], yCoords[iterCounter + 1][1]);

                                        myCanvas.setDrawCoords(xCoords[iterCounter + 1][0], yCoords[iterCounter + 1][0]);
                                        // Iterationsanzahl erhöhen
                                        iterCounter = iterCounter + 1;
                                        myCanvas.setIterCounter(iterCounter);
//                                        // Neue Werte an die ViewList übergeben
//                                        iterList.add(iterCounter);
//                                        xiList.add(Math.round(xCoords[iterCounter][0] * antiAccuracy) / antiAccuracy);
//                                        fxiList.add(Math.round(yCoords[iterCounter][0] * antiAccuracy) / antiAccuracy);
//                                        // ViewList mtiteilen, dass sich Daten geändert haben
//                                        iterAdapter.notifyDataSetChanged();
//                                        // In der Liste soll auf die aktuelle Iteration gesprungen werden
//                                        iterListView.setSelection(iterCounter);

                                        // Versuch
                                        iterList.clear();
                                        xiList.clear();
                                        fxiList.clear();
                                        iterListView = null;
                                        iterListView = (ListView) findViewById(R.id.iterView);
                                        iterListView.setAdapter(iterAdapter);
                                        for (int i = 0; i <= iterCounter; i++){
                                            iterList.add(i);
                                            xiList.add(Math.round(xCoords[i][0] * antiAccuracy) / antiAccuracy);
                                            fxiList.add(Math.round(yCoords[i][0] * antiAccuracy) / antiAccuracy);
                                            // ViewList mtiteilen, dass sich Daten geändert haben
                                            iterAdapter.notifyDataSetChanged();
                                            // In der Liste soll auf die aktuelle Iteration gesprungen werden
                                            iterListView.setSelection(i);
                                        }
                                    }
                                    if (!isBroadlyAnimation && !isFineAnimation) {
                                        step = 0;
                                        stepCounter = step;
                                    } else if (isBroadlyAnimation) {
                                        step = 0;
                                        stepCounter = step;
                                        flag2 = true;
                                        pauseButton.setVisibility(View.GONE);
                                        forwardButton.setVisibility(View.VISIBLE);
                                    } else if (isFineAnimation) {
                                        step = 0;
                                        stepCounter = step;
                                        waitforClick_fineAnimation = true;
                                        flag2 = true;
                                        pauseButton.setVisibility(View.GONE);
                                        forwardButton.setVisibility(View.VISIBLE);
                                    }

                                    Log.e("TAG", "EndStep3: " + "step. = " + stepCounter + ", iterCounter = " + iterCounter + ", backward = " + backwardFlag + ", bwszf = " + backwardstepzeroflag);
                                }

                                // Abbruch, wenn wir nahe genug an der Null sind
                                if (Math.abs(yCoords[iterCounter][0]) < accuracy) {
                                    forwardButton.setVisibility(View.GONE);
                                    pauseButton.setVisibility(View.GONE);
                                    stopButton.setVisibility(View.VISIBLE);
                                    myCanvas.clearGrad();
                                    if (isDrawItersAllowed) {
                                        myCanvas.drawpoint(-10, -10);
                                    }
                                    flag1 = true;
                                    Zerofinding.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(Zerofinding.this, "Nullstelle erfolgreich gefunden!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                            if (iterCounter >= maxiter) {
                                Zerofinding.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(Zerofinding.this, "Maximale Anzahl an Iterierten erreicht!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                forwardButton.setVisibility(View.GONE);
                                pauseButton.setVisibility(View.GONE);
                                stopButton.setVisibility(View.VISIBLE);
                                flag1 = true;
                            }
                        }
                    });
                    try {
                        if (flag1) {
                            pause = true;
                            isRunning = false;
                            stop = true;
                            flag1 = false;
                        } else if (flag2) {
                            pause = true;
                            isRunning = false;
                            stop = false;
                            flag2 = false;
                        } else {
                            Thread.sleep(sleeptime);
                        }
                    } catch (InterruptedException e) {
                        Thread.interrupted();
                        e.printStackTrace();
                    }
                }
                isRunning = false;
                stop = true;

                freeMemory();
            }
        }).start();


        return root;
    }

    public void findRoot(View view) {
        if (!isRunning) {
            if (!pause && stop) {
                myCanvas.setRunning(true);
                xCoords[0][0] = myCanvas.getStartPoint().getReelX();
                yCoords[0][0] = myCanvas.getStartPoint().getReelY();

                myCanvas.getStartPointPaint().setAlpha(255);

                myCanvas.clearFunc();
                myCanvas.drawpoint((float) -10, (float) -10);

                myCanvas.setEnd(end);
                myCanvas.setStart(start);

                coordSystem.setxFunc(start, end);
                coordSystem.setxGrad(start, end);
                coordSystem.setyFunc(mainFunction);
                coordSystem.drawFunc();
                coordSystem.drawAxis();
                myCanvas.setCoordSystem(coordSystem);
                freeMemory();

                forwardButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.VISIBLE);
                iterCounter = 0;
                myCanvas.setIterCounter(iterCounter);
                myCanvas.setIterCounter(iterCounter);
                xCoords[0][0] = myCanvas.getStartPoint().getReelX();
                xCoords[0][1] = coordSystem.getXTransFromReel(xCoords[0][0]);
                yCoords[0][0] = myCanvas.getStartPoint().getReelY();
                yCoords[0][1] = coordSystem.getYTransFromReel(yCoords[0][0]);
                iterList.clear();
                xiList.clear();
                fxiList.clear();
                iterListView = null;
                iterListView = (ListView) findViewById(R.id.iterView);
                iterListView.setAdapter(iterAdapter);

                // Startpunkt in die Liste eintragen
                iterList.add(iterCounter);
                xiList.add(Math.round(xCoords[0][0] * antiAccuracy) / antiAccuracy);
                fxiList.add(Math.round(yCoords[0][0] * antiAccuracy) / antiAccuracy);
                // ViewList mtiteilen, dass sich Daten geändert haben
                iterAdapter.notifyDataSetChanged();
                // In der Liste soll auf die aktuelle Iteration gesprungen werden
                iterListView.setSelection(iterCounter);
                Log.e("TAG", "SolverNew: " + "step = " + stepCounter + ", iterCounter = " + iterCounter + ", bwszf = " + backwardstepzeroflag);
                if (methodeflag == 1){
                    solve_newton(iterCounter);
                    methodeSelecter.setEnabled(false);
                    functionSelecter.setEnabled(false);
                }
                else{
                    solve_quasinewton(iterCounter);
                    methodeSelecter.setEnabled(false);
                    functionSelecter.setEnabled(false);
                }
                freeMemory();
            } else {
                Log.e("TAG", "Solver: " + "step = " + stepCounter + ", iterCounter = " + iterCounter + ", bwszf = " + backwardstepzeroflag);
                forwardButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.VISIBLE);
                pause = false;
                waitforClick_fineAnimation = false;
                if (methodeflag == 1){
                    solve_newton(iterCounter);
                    functionSelecter.setEnabled(false);
                    methodeSelecter.setEnabled(false);
                }
                else{
                    solve_quasinewton(iterCounter);
                    methodeSelecter.setEnabled(false);
                    functionSelecter.setEnabled(false);
                }
            }
        }
    }

    public void pausefindingroot(View view) {
        pause = true;
        stop = false;
        isRunning = false;
        if (stepCounter > 0) {
            stepCounter = stepCounter - 1;
        }
        pauseButton.setVisibility(View.GONE);
        forwardButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Pause Calculation");
        builder.setMessage("Calculation is paused!");
        builder.setCancelable(false);

        final AlertDialog closedialog = builder.create();

        closedialog.show();

        final Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            public void run() {
                closedialog.dismiss();
                timer2.cancel(); //this will cancel the timer of the system
            }
        }, sleeptime/2);
    }

    public void stopfindingroot(View view) {
        if (isRunning) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Stop Calculation");
            builder.setMessage("Calculation is stopped!");
            builder.setCancelable(false);

            final AlertDialog closedialog = builder.create();

            closedialog.show();

            final Timer timer2 = new Timer();
            timer2.schedule(new TimerTask() {
                public void run() {
                    closedialog.dismiss();
                    timer2.cancel(); //this will cancel the timer of the system
                }
            }, sleeptime/2);
        }

        xCoords = new double[100][2];
        myCanvas.setxCoords(xCoords);

        myCanvas.getStartPointPaint().setAlpha(255);

        myCanvas.clearFunc();

        myCanvas.setEnd(end);
        myCanvas.setStart(start);

        coordSystem.setxFunc(start, end);
        coordSystem.setxGrad(start, end);
        coordSystem.setyFunc(mainFunction);
        coordSystem.drawFunc();
        coordSystem.drawAxis();
        myCanvas.setCoordSystem(coordSystem);

        xCoords[0][0] = myCanvas.getStartPoint().getReelX();
        yCoords[0][0] = myCanvas.getStartPoint().getReelY();
        pauseButton.setVisibility(View.GONE);
        forwardButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
        stepCounter = 0;
        iterCounter = 0;
        myCanvas.setIterCounter(iterCounter);
        isRunning = false;
        stop = true;
        pause = false;
        waitforClick_fineAnimation = false;
        myCanvas.clearGrad();
        myCanvas.clearExtraLine();
        iterList.clear();
        xiList.clear();
        fxiList.clear();
        iterListView = (ListView) findViewById(R.id.iterView);
        iterListView.setAdapter(iterAdapter);
        myCanvas.setTouchPermission(true);
        AccuracySeekBar.setEnabled(true);
        myCanvas.setRunning(false);
        myCanvas.getStartPoint().setReelY(mainFunction.evaluate(myCanvas.getStartPoint().getReelX()));
        myCanvas.getStartPoint().setY(coordSystem.getYTransFromReel(myCanvas.getStartPoint().getReelY()));
        myCanvas.getStartPoint().setRectCoords();
        myCanvas.drawpoint(-10, -10);
        for (int i = 0; i < 10; i++) {
            xCoords[i][1] = -10;
        }
        methodeSelecter.setEnabled(true);
        functionSelecter.setEnabled(true);
        freeMemory();
    }

    public void onRadioButtonAnamationSpeedClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioButtonWithoutAnimation:
                if (checked)
                    sleeptime = 0;
                    break;
            case R.id.radioButtonSlowAnimation:
                if (checked)
                    sleeptime = 2000;
                break;
            case R.id.radioButtonFastAnimation:
                if (checked)
                    sleeptime = 500;
                break;
        }
    }

    public void onCheckboxClickedMarks(View view) {

        boolean checked = ((CheckBox) view).isChecked();

        if (checked) {
            isMarksAllowed = true;
        } else {
            isMarksAllowed = false;
        }

        myCanvas.setMarksAllowed(isMarksAllowed);
        myCanvas.invalidate();

    }

    public void onCheckboxClickedDrawIters(View view) {

        boolean checked = ((CheckBox) view).isChecked();

        if (checked) {
            isDrawItersAllowed = true;
            iterAdapter.setVisibility(true);
            colorTextView.setVisibility(View.VISIBLE);

        } else {
            isDrawItersAllowed = false;
            iterAdapter.setVisibility(false);
            colorTextView.setVisibility(View.INVISIBLE);
        }

        iterAdapter.notifyDataSetChanged();
        myCanvas.setDrawItersAllowed(isDrawItersAllowed);
        myCanvas.invalidate();
    }

    public void onRadioButtonAnamationStepClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioButtonAutoAnimation:
                if (checked) {
                    isBroadlyAnimation = false;
                    isFineAnimation = false;

                    int radioButtonID = animationSpeedGroup.getCheckedRadioButtonId();
                    View radioButton = animationSpeedGroup.findViewById(radioButtonID);
                    int idx = animationSpeedGroup.indexOfChild(radioButton);

                    if (idx == 0){
                        sleeptime = 10;
                    }
                    else if(idx == 1){
                        sleeptime = 2000;
                    }
                    else if (idx == 2){
                        sleeptime = 500;
                    }
                }
                break;
            case R.id.radioButtonManuBroadlyAnimation:
                if (checked) {
                    isBroadlyAnimation = true;
                    isFineAnimation = false;

                    int radioButtonID = animationSpeedGroup.getCheckedRadioButtonId();
                    View radioButton = animationSpeedGroup.findViewById(radioButtonID);
                    int idx = animationSpeedGroup.indexOfChild(radioButton);

                    if (idx == 0){
                        sleeptime = 10;
                    }
                    else if(idx == 1){
                        sleeptime = 2000;
                    }
                    else if (idx == 2){
                        sleeptime = 500;
                    }
                }
                break;
            case R.id.radioButtonManuFineAnimation:
                if (checked) {
                    isFineAnimation = true;
                    isBroadlyAnimation = false;
                    sleeptime = 10;
                }
                break;
        }

    }

//   /* public void onRadioButtonAccuracyClecked(View view) {
//
//        boolean checked = ((RadioButton) view).isChecked();
//
//        // Check which radio button was clicked
//        switch (view.getId()) {
//            case R.id.radioButtonAccuracy1:
//                if (checked) {
//                    accuracy = 1e-2;
//                    antiAccuracy = 1e3;
//                }
//                break;
//            case R.id.radioButtonAccuracy2:
//                if (checked) {
//                    accuracy = 1e-3;
//                    antiAccuracy = 1e4;
//                }
//                break;
//            case R.id.radioButtonAccuracy3:
//                if (checked) {
//                    accuracy = 1e-4;
//                    antiAccuracy = 1e5;
//                }
//                break;
//        }
//
//    }*/

    public void findRoot_stepforward(View view) {
//        if (isFineAnimation) {
//            Log.e("TAG", "StepForwardStart: " + "step. = " + stepCounter + ", iterCounter = " + iterCounter + ", backward = " + backwardFlag + ", bwszf = " + backwardstepzeroflag);
            findRoot(view);
//        } else if (isBroadlyAnimation) {
//            findRoot(view);
//        } else {
//            Toast.makeText(this, "Nur bei 'Manuell (grob)' und 'Manuell (fein)' verfügbar", Toast.LENGTH_LONG).show();
//        }

    }

    public void findRoot_stepbackward(View view) {
        Log.e("TAG", "StepbackwardStart: " + "step = " + stepCounter + ", iterCounter = " + iterCounter + ", backward = " + backwardFlag + ", bwszf = " + backwardstepzeroflag);
        if (isFineAnimation) {
            if (!isRunning) {
                Log.e("TAG","A");
                if (!pause && stop && maxiter != iterCounter) {
                    Log.e("TAG","B");
                } else {
                    Log.e("TAG","C");
                    backwardFlag = true;
                    forwardButton.setVisibility(View.GONE);
                    pauseButton.setVisibility(View.VISIBLE);
                    stopButton.setVisibility(View.VISIBLE);
                    pause = false;
                    waitforClick_fineAnimation = false;

                    if (iterCounter == 0 && stepCounter == 1){
                        Log.e("TAG","D");
                        myCanvas.clearGrad();
                        backwardstepzeroflag = true;
                        stepCounter = 0;
                        backwardFlag = false;
                    }
                    /*else if (iterCounter == 0 && stepCounter == 0){
                    }*/
                    else {
                        Log.e("TAG","E");
                        if (stepCounter == 0) {
                            if (iterCounter > 0) {
                                Log.e("TAG","F");
                                stepCounter = 2;
                                iterCounter = iterCounter - 1;
                            } else {
                                stepCounter = 2;
                                Log.e("TAG","G");
                            }
                        } else if (stepCounter == 1 && iterCounter > 0) {
                            stepCounter = 3;
                            Log.e("TAG","H");
                            if (iterCounter >= 1) {
                                Log.e("TAG","I");
                                iterCounter = iterCounter - 1;
                            }
                        } else if (stepCounter == 2) {
                            Log.e("TAG","J");
                            stepCounter = 0;
                        } else if (stepCounter == 3) {
                            Log.e("TAG","K");
                            stepCounter = 1;
                        }
                        Log.e("TAG", "StepbackwardBeforSolve: " + "step = " + stepCounter + ", iterCounter = " + iterCounter + ", backward = " + backwardFlag + ", bwszf = " + backwardstepzeroflag);
                        if (methodeflag == 1){
                            solve_newton(iterCounter);
                            methodeSelecter.setEnabled(false);
                            functionSelecter.setEnabled(false);
                        }
                        else{
                            solve_quasinewton(iterCounter);
                            methodeSelecter.setEnabled(false);
                            functionSelecter.setEnabled(false);
                        }
                    }
                }
            }
        } else if (isBroadlyAnimation) {
            Toast.makeText(this, "Nur bei 'Manuell (fein)' verfügbar", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Nur bei 'Manuell (fein)' verfügbar", Toast.LENGTH_LONG).show();
        }
    }
}
