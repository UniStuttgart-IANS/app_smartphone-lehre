package com.uni_stuttgart.isl.Splittingsolver;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.uni_stuttgart.isl.CoolStuff.NumLinAlg;
import com.uni_stuttgart.isl.Intros.Intro_SplittingSolver;
import com.uni_stuttgart.isl.Intros.PrefManager;
import com.uni_stuttgart.isl.MainActivity;
import com.uni_stuttgart.isl.R;
import com.uni_stuttgart.isl.Splittingsolver.Selecter.AntiDiagSelecter;
import com.uni_stuttgart.isl.Splittingsolver.Selecter.DiagSelecter;
import com.uni_stuttgart.isl.Splittingsolver.Selecter.LowerAntiDiagSelecter;
import com.uni_stuttgart.isl.Splittingsolver.Selecter.LowerAntiTriangleSelecter;
import com.uni_stuttgart.isl.Splittingsolver.Selecter.LowerDiagSelecter;
import com.uni_stuttgart.isl.Splittingsolver.Selecter.LowerTriangleSelecter;
import com.uni_stuttgart.isl.Splittingsolver.Selecter.UpperAntiDiagSelecter;
import com.uni_stuttgart.isl.Splittingsolver.Selecter.UpperAntiTriangleSelecter;
import com.uni_stuttgart.isl.Splittingsolver.Selecter.UpperDiagSelecter;
import com.uni_stuttgart.isl.Splittingsolver.Selecter.UpperTriangleSelecter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static java.lang.Math.ceil;

public class Splittingsolver extends AppCompatActivity {
    String logSave;
    String[] logSaveVec;
    boolean isCancle = false;
    private Toolbar toolbar;
    private int dimension = 9;
    private int kindOfMatrix = 1;
    private double[] b = new double[dimension];
    private double[] x = new double[dimension];
    private MyMatrix myMatrix = new MyMatrix(dimension);
    private MatrixView matrixView;
    private DiagSelecter diagSelecter;
    private AntiDiagSelecter antiDiagSelecter;
    private LowerDiagSelecter lowerDiagSelecter;
    private UpperDiagSelecter upperDiagSelecter;
    private LowerAntiDiagSelecter lowerAntiDiagSelecter;
    private UpperAntiDiagSelecter upperAntiDiagSelecter;
    private LowerTriangleSelecter lowerTriangleSelecter;
    private UpperTriangleSelecter upperTriangleSelecter;
    private LowerAntiTriangleSelecter lowerAntiTriangleSelecter;
    private UpperAntiTriangleSelecter upperAntiTriangleSelecter;
    private Spinner dimensionSelecter;
    private Spinner matrixSelecter;
    private Button computeButton;
    private Button resetButton;
    private Button checkButton;
    private ProgressDialog progress;
    private TextView iterView;
    private TextView resView;
    private int linePitch = 10;
    private RelativeLayout mainLayout;
    private LineChart mChart;

    private PrefManager prefManager;

    private double[] restartX = new double[dimension];
    private int restartIter = 0;

    private Boolean boolDim = false;
    private Boolean boolMat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splittingsolver);

        prefManager = new PrefManager(this);

        logSave = null;
        logSaveVec = null;
        b = null;
        myMatrix = null;
        freeMemory();

        logSave = Read();
        logSaveVec = StringToStringVev(logSave);

        switch (Integer.parseInt(logSaveVec[0])) {
            case 0:
                dimension = 9;
                break;
            case 1:
                dimension = 18;
                break;
            case 2:
                dimension = 27;
                break;
            case 3:
                dimension = 54;
                break;
            case 4:
                dimension = 72;
                break;
        }

        kindOfMatrix = Integer.parseInt(logSaveVec[1]);

        b = new double[dimension];
        myMatrix = new MyMatrix(dimension);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matrixView.setIsSolving(false);
                saveActivityToLog();
                freeMemory();

                prefManager.setIsMainLastActivity(true);
                prefManager.setIsInterLastActivity(false);
                prefManager.setIsSplitLastActivity(false);
                prefManager.setIsGoingBack(true);

                Intent myIntent = new Intent(Splittingsolver.this, MainActivity.class);
                Splittingsolver.this.startActivity(myIntent);
                mChart.removeAllViews();
                mainLayout.removeAllViews();
                freeMemory();
                finish();
                freeMemory();
            }
        });

        matrixView = (MatrixView) findViewById(R.id.matrixview);

        mainLayout = (RelativeLayout) findViewById(R.id.Plotter);
        mChart = new LineChart(this);
        mainLayout.addView(mChart, new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));

        mChart.setNoDataText("No data for the moment!");

        mChart.setHighlightPerTapEnabled(true);
        mChart.setTouchEnabled(true);

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        mChart.setPinchZoom(true);

        mChart.setBackgroundColor(Color.rgb(192, 192, 192));

        LineData data = new LineData();
//        data.setValueTextColor(Color.BLACK);

        mChart.setData(data);

        Legend legend = mChart.getLegend();

        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(Color.BLACK);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setDrawLabels(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setAxisLineWidth(20f);
        xAxis.setAxisLineColor(Color.BLACK);

        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setTextColor(Color.BLACK);
        yAxis.setDrawGridLines(true);
        yAxis.setValueFormatter(new MyYAxisValueFormatter());

        YAxis yAxis1 = mChart.getAxisRight();
        yAxis1.setEnabled(true);
        yAxis1.setValueFormatter(new MyYAxisValueFormatter());

        matrixSelecter = (Spinner) findViewById(R.id.MatrixSelecter);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter_SpinnerMatrix = ArrayAdapter.createFromResource(this, R.array.matrixselection, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter_SpinnerMatrix.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter_SpinnerMatrix to the spinner
        matrixSelecter.setAdapter(adapter_SpinnerMatrix);

        matrixSelecter.setSelection(Integer.parseInt(logSaveVec[1]));

        matrixSelecter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (boolMat) {
                    switch (position) {
                        case 0:
                            kindOfMatrix = 0;
                            break;
                        case 1:
                            kindOfMatrix = 1;
                            break;
                        case 2:
                            kindOfMatrix = 2;
                            break;
                        case 3:
                            kindOfMatrix = 3;
                            break;
                        case 4:
                            break;
                        case 5:
                            break;
                    }

                    b = new double[dimension];
                    restartX = new double[dimension];

                    for (int i = 0; i < dimension; i++) {
                        b[i] = 1;
                    }

                    myMatrix = null;
                    myMatrix = new MyMatrix(dimension);

                    myMatrix.addMatrix(matrixChooser(kindOfMatrix));
                    myMatrix.relateBlock();
                    myMatrix.sortEntrys();

                    myMatrix.setInvA(NumLinAlg.invert(myMatrix.getA()));


                    matrixView.destroyDrawingCache();
                    matrixView = (MatrixView) findViewById(R.id.matrixview);

                    matrixView.setBlockCornerCoords(matrixView.getLayoutParams().width);
                    matrixView.setMyMatrix(myMatrix);
                    matrixView.setComputeButton(computeButton);
                    matrixView.setDimension(dimension);
                    matrixView.invalidate();


                    diagSelecter = null;
                    diagSelecter = (DiagSelecter) findViewById(R.id.diagselecter);
                    diagSelecter.setMatrixView(matrixView);
                    diagSelecter.setMyMatrix(myMatrix);
                    diagSelecter.setLinePitch(linePitch);
                    diagSelecter.setComputeButton(computeButton);
                    diagSelecter.invalidate();

                    antiDiagSelecter = null;
                    antiDiagSelecter = (AntiDiagSelecter) findViewById(R.id.antidiagselecter);
                    antiDiagSelecter.setMatrixView(matrixView);
                    antiDiagSelecter.setMyMatrix(myMatrix);
                    antiDiagSelecter.setLinePitch(linePitch);
                    antiDiagSelecter.setComputeButton(computeButton);
                    antiDiagSelecter.invalidate();

                    lowerDiagSelecter = null;
                    lowerDiagSelecter = (LowerDiagSelecter) findViewById(R.id.lowerdiagselecter);
                    lowerDiagSelecter.setMatrixView(matrixView);
                    lowerDiagSelecter.setMyMatrix(myMatrix);
                    lowerDiagSelecter.setLinePitch(linePitch);
                    lowerDiagSelecter.setComputeButton(computeButton);
                    lowerDiagSelecter.invalidate();

                    upperDiagSelecter = null;
                    upperDiagSelecter = (UpperDiagSelecter) findViewById(R.id.upperdiagselecter);
                    upperDiagSelecter.setMatrixView(matrixView);
                    upperDiagSelecter.setMyMatrix(myMatrix);
                    upperDiagSelecter.setLinePitch(linePitch);
                    upperDiagSelecter.setComputeButton(computeButton);
                    upperDiagSelecter.invalidate();

                    lowerAntiDiagSelecter = null;
                    lowerAntiDiagSelecter = (LowerAntiDiagSelecter) findViewById(R.id.lowerantidiagselecter);
                    lowerAntiDiagSelecter.setMatrixView(matrixView);
                    lowerAntiDiagSelecter.setMyMatrix(myMatrix);
                    lowerAntiDiagSelecter.setLinePitch(linePitch);
                    lowerAntiDiagSelecter.setComputeButton(computeButton);
                    lowerAntiDiagSelecter.invalidate();

                    upperAntiDiagSelecter = null;
                    upperAntiDiagSelecter = (UpperAntiDiagSelecter) findViewById(R.id.upperantidiagselecter);
                    upperAntiDiagSelecter.setMatrixView(matrixView);
                    upperAntiDiagSelecter.setMyMatrix(myMatrix);
                    upperAntiDiagSelecter.setLinePitch(linePitch);
                    upperAntiDiagSelecter.setComputeButton(computeButton);
                    upperAntiDiagSelecter.invalidate();

                    lowerTriangleSelecter = null;
                    lowerTriangleSelecter = (LowerTriangleSelecter) findViewById(R.id.lowertriangleselecter);
                    lowerTriangleSelecter.setMatrixView(matrixView);
                    lowerTriangleSelecter.setMyMatrix(myMatrix);
                    lowerTriangleSelecter.setLinePitch(linePitch);
                    lowerTriangleSelecter.setLowerDiagSelecter(lowerDiagSelecter);
                    lowerTriangleSelecter.setComputeButton(computeButton);
                    lowerTriangleSelecter.invalidate();

                    upperTriangleSelecter = null;
                    upperTriangleSelecter = (UpperTriangleSelecter) findViewById(R.id.uppertriangleselecter);
                    upperTriangleSelecter.setMatrixView(matrixView);
                    upperTriangleSelecter.setMyMatrix(myMatrix);
                    upperTriangleSelecter.setLinePitch(linePitch);
                    upperTriangleSelecter.setUpperDiagSelecter(upperDiagSelecter);
                    upperTriangleSelecter.setComputeButton(computeButton);
                    upperTriangleSelecter.invalidate();

                    lowerAntiTriangleSelecter = null;
                    lowerAntiTriangleSelecter = (LowerAntiTriangleSelecter) findViewById(R.id.lowerantitriangleselecter);
                    lowerAntiTriangleSelecter.setMatrixView(matrixView);
                    lowerAntiTriangleSelecter.setMyMatrix(myMatrix);
                    lowerAntiTriangleSelecter.setLinePitch(linePitch);
                    lowerAntiTriangleSelecter.setLowerAntiDiagSelecter(lowerAntiDiagSelecter);
                    lowerAntiTriangleSelecter.setComputeButton(computeButton);
                    lowerAntiTriangleSelecter.invalidate();

                    upperAntiTriangleSelecter = null;
                    upperAntiTriangleSelecter = (UpperAntiTriangleSelecter) findViewById(R.id.upperantitriangleselecter);
                    upperAntiTriangleSelecter.setMatrixView(matrixView);
                    upperAntiTriangleSelecter.setMyMatrix(myMatrix);
                    upperAntiTriangleSelecter.setLinePitch(linePitch);
                    upperAntiTriangleSelecter.setUpperAntiDiagSelecter(upperAntiDiagSelecter);
                    upperAntiTriangleSelecter.setComputeButton(computeButton);
                    upperAntiTriangleSelecter.invalidate();

                    computeButton.setVisibility(View.INVISIBLE);
                    resetButton.setVisibility(View.INVISIBLE);
                    resetPlotFunc();
                    freeMemory();
                }
                boolMat = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        computeButton = (Button) findViewById(R.id.compute_button);
        resetButton = (Button) findViewById(R.id.resetbutton);
        checkButton = (Button) findViewById(R.id.check_button);

        iterView = (TextView) findViewById(R.id.iterView);
        resView = (TextView) findViewById(R.id.resView);

        dimensionSelecter = (Spinner) findViewById(R.id.dimensionspinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter_SpinnerDimension = ArrayAdapter.createFromResource(this, R.array.dimensions, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter_SpinnerDimension.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter_SpinnerDimension
        //to the spinner
        dimensionSelecter.setAdapter(adapter_SpinnerDimension);

        dimensionSelecter.setSelection(Integer.parseInt(logSaveVec[0]));

        dimensionSelecter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (boolDim) {
                    switch (position) {
                        case 0:
                            dimension = 9;
                            break;
                        case 1:
                            dimension = 18;
                            break;
                        case 2:
                            dimension = 27;
                            break;
                        case 3:
                            dimension = 54;
                            break;
                        case 4:
                            dimension = 72;
                            break;
                    }


                    b = new double[dimension];
                    restartX = new double[dimension];

                    for (int i = 0; i < dimension; i++) {
                        b[i] = 1;
                    }

                    myMatrix = null;
                    myMatrix = new MyMatrix(dimension);

                    myMatrix.addMatrix(matrixChooser(kindOfMatrix));
                    myMatrix.relateBlock();
                    myMatrix.sortEntrys();

                    myMatrix.setInvA(NumLinAlg.invert(myMatrix.getA()));


                    matrixView = null;
                    matrixView = (MatrixView) findViewById(R.id.matrixview);


                    matrixView.setBlockCornerCoords(matrixView.getLayoutParams().width);
                    matrixView.setMyMatrix(myMatrix);
                    matrixView.setComputeButton(computeButton);
                    matrixView.setDimension(dimension);
                    matrixView.invalidate();


                    diagSelecter = null;
                    diagSelecter = (DiagSelecter) findViewById(R.id.diagselecter);
                    diagSelecter.setMatrixView(matrixView);
                    diagSelecter.setMyMatrix(myMatrix);
                    diagSelecter.setLinePitch(linePitch);
                    diagSelecter.setComputeButton(computeButton);
                    diagSelecter.invalidate();

                    antiDiagSelecter = null;
                    antiDiagSelecter = (AntiDiagSelecter) findViewById(R.id.antidiagselecter);
                    antiDiagSelecter.setMatrixView(matrixView);
                    antiDiagSelecter.setMyMatrix(myMatrix);
                    antiDiagSelecter.setLinePitch(linePitch);
                    antiDiagSelecter.setComputeButton(computeButton);
                    antiDiagSelecter.invalidate();

                    lowerDiagSelecter = null;
                    lowerDiagSelecter = (LowerDiagSelecter) findViewById(R.id.lowerdiagselecter);
                    lowerDiagSelecter.setMatrixView(matrixView);
                    lowerDiagSelecter.setMyMatrix(myMatrix);
                    lowerDiagSelecter.setLinePitch(linePitch);
                    lowerDiagSelecter.setComputeButton(computeButton);
                    lowerDiagSelecter.invalidate();

                    upperDiagSelecter = null;
                    upperDiagSelecter = (UpperDiagSelecter) findViewById(R.id.upperdiagselecter);
                    upperDiagSelecter.setMatrixView(matrixView);
                    upperDiagSelecter.setMyMatrix(myMatrix);
                    upperDiagSelecter.setLinePitch(linePitch);
                    upperDiagSelecter.setComputeButton(computeButton);
                    upperDiagSelecter.invalidate();

                    lowerAntiDiagSelecter = null;
                    lowerAntiDiagSelecter = (LowerAntiDiagSelecter) findViewById(R.id.lowerantidiagselecter);
                    lowerAntiDiagSelecter.setMatrixView(matrixView);
                    lowerAntiDiagSelecter.setMyMatrix(myMatrix);
                    lowerAntiDiagSelecter.setLinePitch(linePitch);
                    lowerAntiDiagSelecter.setComputeButton(computeButton);
                    lowerAntiDiagSelecter.invalidate();

                    upperAntiDiagSelecter = null;
                    upperAntiDiagSelecter = (UpperAntiDiagSelecter) findViewById(R.id.upperantidiagselecter);
                    upperAntiDiagSelecter.setMatrixView(matrixView);
                    upperAntiDiagSelecter.setMyMatrix(myMatrix);
                    upperAntiDiagSelecter.setLinePitch(linePitch);
                    upperAntiDiagSelecter.setComputeButton(computeButton);
                    upperAntiDiagSelecter.invalidate();

                    lowerTriangleSelecter = null;
                    lowerTriangleSelecter = (LowerTriangleSelecter) findViewById(R.id.lowertriangleselecter);
                    lowerTriangleSelecter.setMatrixView(matrixView);
                    lowerTriangleSelecter.setMyMatrix(myMatrix);
                    lowerTriangleSelecter.setLinePitch(linePitch);
                    lowerTriangleSelecter.setLowerDiagSelecter(lowerDiagSelecter);
                    lowerTriangleSelecter.setComputeButton(computeButton);
                    lowerTriangleSelecter.invalidate();

                    upperTriangleSelecter = null;
                    upperTriangleSelecter = (UpperTriangleSelecter) findViewById(R.id.uppertriangleselecter);
                    upperTriangleSelecter.setMatrixView(matrixView);
                    upperTriangleSelecter.setMyMatrix(myMatrix);
                    upperTriangleSelecter.setLinePitch(linePitch);
                    upperTriangleSelecter.setUpperDiagSelecter(upperDiagSelecter);
                    upperTriangleSelecter.setComputeButton(computeButton);
                    upperTriangleSelecter.invalidate();

                    lowerAntiTriangleSelecter = null;
                    lowerAntiTriangleSelecter = (LowerAntiTriangleSelecter) findViewById(R.id.lowerantitriangleselecter);
                    lowerAntiTriangleSelecter.setMatrixView(matrixView);
                    lowerAntiTriangleSelecter.setMyMatrix(myMatrix);
                    lowerAntiTriangleSelecter.setLinePitch(linePitch);
                    lowerAntiTriangleSelecter.setLowerAntiDiagSelecter(lowerAntiDiagSelecter);
                    lowerAntiTriangleSelecter.setComputeButton(computeButton);
                    lowerAntiTriangleSelecter.invalidate();

                    upperAntiTriangleSelecter = null;
                    upperAntiTriangleSelecter = (UpperAntiTriangleSelecter) findViewById(R.id.upperantitriangleselecter);
                    upperAntiTriangleSelecter.setMatrixView(matrixView);
                    upperAntiTriangleSelecter.setMyMatrix(myMatrix);
                    upperAntiTriangleSelecter.setLinePitch(linePitch);
                    upperAntiTriangleSelecter.setUpperAntiDiagSelecter(upperAntiDiagSelecter);
                    upperAntiTriangleSelecter.setComputeButton(computeButton);
                    upperAntiTriangleSelecter.invalidate();
//
                    computeButton.setVisibility(View.INVISIBLE);
                    resetButton.setVisibility(View.INVISIBLE);
                    resetPlotFunc();
                    freeMemory();
                }
                boolDim = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        for (int i = 0; i < dimension; i++) {
            b[i] = 1;
        }

        myMatrix.addMatrix(matrixChooser(Integer.parseInt(logSaveVec[1])));
        myMatrix.relateBlock();
        myMatrix.sortEntrys();
        myMatrix.setInvA(NumLinAlg.invert(myMatrix.getA()));

        String[] temp = new String[10];
        for (int i = 0; i < 10; i++) {
            temp[i] = logSaveVec[2 + i];
        }
        myMatrix.setWholeSelecterVectorFromStringVec(temp);

        temp = new String[9 * 10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 9; j++) {
                temp[i * 9 + j] = logSaveVec[12 + i * 9 + j];
            }
        }
        myMatrix.setBlockSelecterMatrixFromStringVec(temp);

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (Integer.parseInt(logSaveVec[12 + 90 + i * dimension + j]) == 1) {
                    myMatrix.getEntry(i, j).setSelected();
                } else {
                    myMatrix.getEntry(i, j).setUnselected();
                }
            }
        }

        matrixView = (MatrixView) findViewById(R.id.matrixview);

        matrixView.setBlockCornerCoords(matrixView.getLayoutParams().width);
        matrixView.setMyMatrix(myMatrix);
        matrixView.setComputeButton(computeButton);
        matrixView.setDimension(dimension);

        diagSelecter = (DiagSelecter) findViewById(R.id.diagselecter);
        diagSelecter.setMatrixView(matrixView);
        diagSelecter.setMyMatrix(myMatrix);
        diagSelecter.setLinePitch(linePitch);
        diagSelecter.setComputeButton(computeButton);
        diagSelecter.invalidate();

        antiDiagSelecter = (AntiDiagSelecter) findViewById(R.id.antidiagselecter);
        antiDiagSelecter.setMatrixView(matrixView);
        antiDiagSelecter.setMyMatrix(myMatrix);
        antiDiagSelecter.setLinePitch(linePitch);
        antiDiagSelecter.setComputeButton(computeButton);
        antiDiagSelecter.invalidate();

        lowerDiagSelecter = (LowerDiagSelecter) findViewById(R.id.lowerdiagselecter);
        lowerDiagSelecter.setMatrixView(matrixView);
        lowerDiagSelecter.setMyMatrix(myMatrix);
        lowerDiagSelecter.setLinePitch(linePitch);
        lowerDiagSelecter.setComputeButton(computeButton);
        lowerDiagSelecter.invalidate();

        upperDiagSelecter = (UpperDiagSelecter) findViewById(R.id.upperdiagselecter);
        upperDiagSelecter.setMatrixView(matrixView);
        upperDiagSelecter.setMyMatrix(myMatrix);
        upperDiagSelecter.setLinePitch(linePitch);
        upperDiagSelecter.setComputeButton(computeButton);
        upperDiagSelecter.invalidate();

        lowerAntiDiagSelecter = (LowerAntiDiagSelecter) findViewById(R.id.lowerantidiagselecter);
        lowerAntiDiagSelecter.setMatrixView(matrixView);
        lowerAntiDiagSelecter.setMyMatrix(myMatrix);
        lowerAntiDiagSelecter.setLinePitch(linePitch);
        lowerAntiDiagSelecter.setComputeButton(computeButton);
        lowerAntiDiagSelecter.invalidate();

        upperAntiDiagSelecter = (UpperAntiDiagSelecter) findViewById(R.id.upperantidiagselecter);
        upperAntiDiagSelecter.setMatrixView(matrixView);
        upperAntiDiagSelecter.setMyMatrix(myMatrix);
        upperAntiDiagSelecter.setLinePitch(linePitch);
        upperAntiDiagSelecter.setComputeButton(computeButton);
        upperAntiDiagSelecter.invalidate();

        lowerTriangleSelecter = (LowerTriangleSelecter) findViewById(R.id.lowertriangleselecter);
        lowerTriangleSelecter.setMatrixView(matrixView);
        lowerTriangleSelecter.setMyMatrix(myMatrix);
        lowerTriangleSelecter.setLinePitch(linePitch);
        lowerTriangleSelecter.setLowerDiagSelecter(lowerDiagSelecter);
        lowerTriangleSelecter.setComputeButton(computeButton);
        lowerTriangleSelecter.invalidate();

        upperTriangleSelecter = (UpperTriangleSelecter) findViewById(R.id.uppertriangleselecter);
        upperTriangleSelecter.setMatrixView(matrixView);
        upperTriangleSelecter.setMyMatrix(myMatrix);
        upperTriangleSelecter.setLinePitch(linePitch);
        upperTriangleSelecter.setUpperDiagSelecter(upperDiagSelecter);
        upperTriangleSelecter.setComputeButton(computeButton);
        upperTriangleSelecter.invalidate();

        lowerAntiTriangleSelecter = (LowerAntiTriangleSelecter) findViewById(R.id.lowerantitriangleselecter);
        lowerAntiTriangleSelecter.setMatrixView(matrixView);
        lowerAntiTriangleSelecter.setMyMatrix(myMatrix);
        lowerAntiTriangleSelecter.setLinePitch(linePitch);
        lowerAntiTriangleSelecter.setLowerAntiDiagSelecter(lowerAntiDiagSelecter);
        lowerAntiTriangleSelecter.setComputeButton(computeButton);
        lowerAntiTriangleSelecter.invalidate();

        upperAntiTriangleSelecter = (UpperAntiTriangleSelecter) findViewById(R.id.upperantitriangleselecter);
        upperAntiTriangleSelecter.setMatrixView(matrixView);
        upperAntiTriangleSelecter.setMyMatrix(myMatrix);
        upperAntiTriangleSelecter.setLinePitch(linePitch);
        upperAntiTriangleSelecter.setUpperAntiDiagSelecter(upperAntiDiagSelecter);
        upperAntiTriangleSelecter.setComputeButton(computeButton);
        upperAntiTriangleSelecter.invalidate();

        matrixView.setDiagselecter(diagSelecter);
        matrixView.setAntidiagselecter(antiDiagSelecter);
        matrixView.setLowerdiagselecter(lowerDiagSelecter);
        matrixView.setUpperdiagselecter(upperDiagSelecter);
        matrixView.setLowerantidiagselecter(lowerAntiDiagSelecter);
        matrixView.setUpperantidiagselecter(upperAntiDiagSelecter);
        matrixView.setLowertriangleselecter(lowerTriangleSelecter);
        matrixView.setUppertriangleselecter(upperTriangleSelecter);
        matrixView.setLowerantitriangleselecter(lowerAntiTriangleSelecter);
        matrixView.setUpperantitriangleselecter(upperAntiTriangleSelecter);

        matrixView.setmChart(mChart);

        matrixView.post(new Runnable() {
            @Override
            public void run() {

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int height = size.y;
                if((height - toolbar.getHeight())<370 * Resources.getSystem().getDisplayMetrics().density) {
                    ViewGroup.LayoutParams mview = matrixView.getLayoutParams();
                    mview.height = height - toolbar.getHeight() - (int) ceil(50 / Resources.getSystem().getDisplayMetrics().density);
                    mview.width = height - toolbar.getHeight() - (int) ceil(50 / Resources.getSystem().getDisplayMetrics().density);
                    matrixView.setLayoutParams(mview);
                    matrixView.invalidate();

                    matrixView.setBlockCornerCoords(matrixView.getLayoutParams().width);
                    matrixView.setMyMatrix(myMatrix);
                    matrixView.setComputeButton(computeButton);
                    matrixView.setDimension(dimension);
                }
            }
        });

        resetPlotFunc();
        freeMemory();
    }

    public void check(View view) {
        myMatrix.getM();
        resetPlotFunc();
//        CoolStuff.log("Eigenwert = " + NumLinAlg.computeEigenvalue(NumLinAlg.subMatMat(CoolStuff.identity(dimension), NumLinAlg.MatMat(myMatrix.getInvM(), myMatrix.getA()))));
//        CoolStuff.log("Eigenwert = " + NumLinAlg.computeEigenvalue(CoolStuff.identity(dimension)));

        if (NumLinAlg.checkForSingularity(myMatrix.getM())) {
            double[][] A = myMatrix.getA();
            x = new double[dimension];

            int n = b.length;
            double[][] invM = NumLinAlg.invert(myMatrix.getM());
            double[] x_old, temp_vec = new double[n];
            int iter = 0;
            double res_old = NumLinAlg.norm_res(x, A, b);
            double res = NumLinAlg.norm_res(x, A, b);

            for (int i = 0; i < 1000; i++) {
                x_old = NumLinAlg.vecCopy(x);
                res_old = res;

                temp_vec = NumLinAlg.aMatVecbVec(-1, A, x_old, 1, b);
                temp_vec = NumLinAlg.MatVec(invM, temp_vec);

                x = NumLinAlg.addVecVec(x_old, temp_vec);

                res = NumLinAlg.norm_res(x, A, b);
//                log("TestRes: " + res);
                iter = iter + 1;
            }

//            Log.e("Test", "Cond(AinvM) = " + NumLinAlg.matCondSpezi(myMatrix));

            if (res > res_old || Double.isNaN(res) || Double.isInfinite(res)) {
                Toast.makeText(Splittingsolver.this, "Verfahren konvergiert so nicht", Toast.LENGTH_SHORT).show();
                computeButton.setText("Try");
                computeButton.setVisibility(View.VISIBLE);
            } else {
                computeButton.setText("Solve");
                resetPlotFunc();
                computeButton.setVisibility(View.VISIBLE);
            }
        } else {
            Toast.makeText(Splittingsolver.this, "Iterations-Matrix ist singulär", Toast.LENGTH_SHORT).show();
        }

        freeMemory();
    }

    private void resetPlotFunc() {
        restartIter = 0;
        restartX = new double[dimension];
        resetButton.setVisibility(View.INVISIBLE);
        computeButton.setText("Solve");

        iterView.setText("#Iter = ");
        resView.setText("Res = ");

        mainLayout.removeAllViews();
        mainLayout = null;
        mChart = null;

        mainLayout = (RelativeLayout) findViewById(R.id.Plotter);
        mChart = new LineChart(this);
        mainLayout.addView(mChart, new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));

//        mChart.setDescription(" ");

        mChart.setNoDataText("No data for the moment!");

        mChart.setHighlightPerTapEnabled(true);
        mChart.setTouchEnabled(true);

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        mChart.setPinchZoom(true);

        mChart.setBackgroundColor(Color.rgb(192, 192, 192));

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        mChart.setData(data);

        Legend legend = mChart.getLegend();

        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(Color.BLACK);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setDrawLabels(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setAvoidFirstLastClipping(true);

        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setTextColor(Color.BLACK);
        yAxis.setDrawGridLines(true);
        yAxis.setValueFormatter(new MyYAxisValueFormatter());

        YAxis yAxis1 = mChart.getAxisRight();
        yAxis1.setEnabled(true);
        yAxis1.setValueFormatter(new MyYAxisValueFormatter());

        isCancle = true;
        freeMemory();
    }

    public void play(View view) {

        isCancle = false;
        dimensionSelecter.setEnabled(false);
        matrixSelecter.setEnabled(false);
        checkButton.setVisibility(View.INVISIBLE);

        if (!matrixView.getIsSolving()) {
            matrixView.setHasChanced(false);
            matrixView.setIsSolving(true);
            computeButton.setText("Pause");
            resetButton.setVisibility(View.INVISIBLE);
        } else if (matrixView.getIsSolving()) {
            matrixView.setIsSolving(false);

            if (matrixView.getHasChanced()) {
                computeButton.setVisibility(View.INVISIBLE);
                checkButton.setVisibility(View.VISIBLE);
                resetButton.setVisibility(View.VISIBLE);
                dimensionSelecter.setEnabled(true);
                matrixSelecter.setEnabled(true);
                isCancle = true;
                matrixView.setHasChanced(false);
            } else {
                computeButton.setText("Solve");
                resetButton.setVisibility(View.VISIBLE);
            }

        }

        new Thread(new Runnable() {
            double[][] A = myMatrix.getA();
            double[][] M = myMatrix.getM();
            double[][] invM = NumLinAlg.invert(M);
            double[] x = new double[dimension];
            double[] x_1, x_old, temp_vec = new double[dimension];
            double eps = 1e-8;
            int iter_max = 1000000;
            int iter = restartIter;
            double res = NumLinAlg.norm_res(x, A, b);
            boolean flag = true;

            @Override
            public void run() {
                x_1 = restartX.clone();
                while (iter < iter_max && res > eps && !Double.isNaN(res) && !Double.isInfinite(res) && !isCancle && matrixView.getIsSolving()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!matrixView.getIsSolving()) {
                                dimensionSelecter.setEnabled(true);
                                matrixSelecter.setEnabled(true);
                                checkButton.setVisibility(View.VISIBLE);
                            }
                            if (res < eps || iter >= iter_max) {
                                Log.e("TAG", "...");
                                computeButton.setText("Solve");
                                resetButton.setVisibility(View.VISIBLE);
                                x = new double[dimension];
                                restartX = new double[dimension];
                                restartIter = 0;
                                matrixView.setIsSolving(false);
                                flag = false;
                                dimensionSelecter.setEnabled(true);
                                matrixSelecter.setEnabled(true);
                                checkButton.setVisibility(View.VISIBLE);
                                freeMemory();
                            }
                            if (Double.isNaN(res) || Double.isInfinite(res)) {
                                computeButton.setVisibility(View.INVISIBLE);
                                resetButton.setVisibility(View.INVISIBLE);
                                dimensionSelecter.setEnabled(true);
                                matrixSelecter.setEnabled(true);
                                checkButton.setVisibility(View.VISIBLE);
                                resetPlotFunc();
                                flag = false;
                            }
                            if (flag) {
                                x_old = x_1.clone();

                                temp_vec = NumLinAlg.aMatVecbVec(-1, A, x_old, 1, b);
                                temp_vec = NumLinAlg.MatVec(invM, temp_vec);

                                x_1 = NumLinAlg.addVecVec(x_old, temp_vec);

                                restartX = x_1.clone();

                                res = NumLinAlg.norm_res(x_1, A, b);
                                iter = iter + 1;
                                restartIter = iter;
                                iterView.setText("#Iter = " + iter);
                                resView.setText("Res = " + res);
                                addEntry(Math.log10(res));
                            }

                            if (!matrixView.getIsSolving()) {
                                dimensionSelecter.setEnabled(true);
                                matrixSelecter.setEnabled(true);
                                checkButton.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    try {
                        Thread.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                freeMemory();
                matrixView.setIsSolving(false);
            }
        }).start();

        freeMemory();
    }


    public double[][] matrixChooser(int number) {
        double[][] A = new double[dimension][dimension];

        if (number == 0) {
            for (int i = 0; i < dimension; i++) {
                A[i][i] = 2;
                if (i != dimension - 1) {
                    A[i][i + 1] = -1;
                }
                if (i != 0) {
                    A[i][i - 1] = -1;
                }
            }
        } else if (number == 1) {
            for (int i = 0; i < dimension; i++) {
                A[i][i] = 4;
                if (i != dimension - 1) {
                    A[i][i + 1] = -1;
                }
                if (i != 0) {
                    A[i][i - 1] = -1;
                }
                if (i < dimension - Math.sqrt(dimension)) {
                    A[i][i + (int) Math.sqrt(dimension)] = -1;
                }
                if (i > Math.sqrt(dimension) - 1) {
                    A[i][i - (int) Math.sqrt(dimension)] = -1;
                }
            }
        } else if (number == 2) {
            for (int i = 0; i < dimension; i++) {
                A[i][0] = 1;
                for (int j = 1; j < dimension; j++) {
                    if ((j) % (i + 1) == 0 && i != 0) {
                        A[i][j] = 1;
                    } else if (i == 0) {
                        A[0][j] = 1;
                    }
                }
            }
            A = NumLinAlg.MatTMat(A, A);
        } else if (number == 3) {
            for (int i = 0; i < dimension; i++) {
                A[i][i] = 2;
                if (i != dimension - 1) {
                    A[i][i + 1] = -1;
                }
                if (i != 0) {
                    A[i][i - 1] = -1;
                }
            }
            A = NumLinAlg.MatTMat(A, A);
        }

        return A;
    }

    private void addEntry(double res) {
        LineData data = mChart.getLineData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

//            data.addXValue("");
            data.addEntry(new com.github.mikephil.charting.data.Entry((float) res, set.getEntryCount()), 0);


            mChart.notifyDataSetChanged();

            mChart.setVisibleXRangeMaximum(10000);

            mChart.moveViewToX(data.getEntryCount() - 10001);
        }
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Residuum");
//        set.setDrawCubic(false);
//        set.setCubicIntensity(0.2F);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
//        set.setValueTextColor(ColorTemplate.getHoloBlue());
//        set.setCircleColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(2F);
        set.setColor(Color.BLACK);
//        set.setCircleSize(4F);
        set.setFillAlpha(65);
//        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 0, 0));
        set.setValueTextSize(0);
        set.setDrawCircles(false);

        return set;
    }

    public void resetPlot(View view) {

        restartIter = 0;
        restartX = new double[dimension];
        resetButton.setVisibility(View.INVISIBLE);
        computeButton.setText("Solve");

        iterView.setText("#Iter = ");
        resView.setText("Res = ");

        mainLayout.removeAllViews();
        mainLayout = null;
        mChart = null;

        mainLayout = (RelativeLayout) findViewById(R.id.Plotter);
        mChart = new LineChart(this);

        mainLayout.addView(mChart, new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));

//        mChart.setDescription(" ");

        mChart.setNoDataText("No data for the moment!");

        mChart.setHighlightPerTapEnabled(true);
        mChart.setTouchEnabled(true);

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        mChart.setPinchZoom(true);

        mChart.setBackgroundColor(Color.rgb(192, 192, 192));

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        mChart.setData(data);

        Legend legend = mChart.getLegend();

        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(Color.BLACK);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setDrawLabels(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setAvoidFirstLastClipping(true);

        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setTextColor(Color.BLACK);
        yAxis.setDrawGridLines(true);
        yAxis.setValueFormatter(new MyYAxisValueFormatter());

        YAxis yAxis1 = mChart.getAxisRight();
        yAxis1.setEnabled(true);
        yAxis1.setValueFormatter(new MyYAxisValueFormatter());

        isCancle = true;

        freeMemory();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splitting, menu);
        return true;
    }

    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.introduction) {
            matrixView.setIsSolving(false);
            saveActivityToLog();

            mChart.removeAllViews();
            mainLayout.removeAllViews();

            prefManager.setReopenSplittingSolverIntro(false);
            Intent myIntent = new Intent(Splittingsolver.this, Intro_SplittingSolver.class);
            Splittingsolver.this.startActivity(myIntent);
            freeMemory();
            finish();
        }

        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();

        prefManager.setIsGoingBack(false);
        prefManager.setIsMainLastActivity(false);
        prefManager.setIsInterLastActivity(false);
        prefManager.setIsSplitLastActivity(true);

        if (matrixView.getIsSolving()) {
            matrixView.setIsSolving(false);
            computeButton.setVisibility(View.VISIBLE);
            computeButton.setText("Resume");
            resetButton.setVisibility(View.VISIBLE);
        }
        saveActivityToLog();
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

    public void StringVecViewer(String[] splitString) {
        Log.e("TAG", "-------------------------------------------------------------------");
    }

    public String StringVecToString(String[] logStringVec) {
        String logString = "";

        for (int i = 0; i < logStringVec.length; i++) {
            logString = logString + logStringVec[i] + " ";
        }

        return logString;
    }

    public String[] StringToStringVev(String s) {
        String[] splitS;

        splitS = s.split(" ");

        return splitS;
    }

    public String Read() {

        String logString = "";
        try {
            InputStream inputStream = this.openFileInput("LogSpSo.txt");

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

    public String SelecterBlockMatrixToString() {
        String s = "";

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 9; j++) {
                s = s + getIntFromBoolean(myMatrix.getBlockSelecterMatrix()[i][j]) + " ";
            }
        }

        return s;
    }

    public String SelecterMatrixToString() {
        String s = "";

        for (int i = 0; i < 10; i++) {
            s = s + getIntFromBoolean(myMatrix.getWholeSelecterVector()[i]) + " ";
        }

        return s;
    }

    public String SaveEntryStatus() {
        String s = "";

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                s = s + getIntFromBoolean(myMatrix.getEntry(i, j).getStatus()) + " ";
            }
        }

        return s;
    }

    public void Save(String log) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("LogSpSo.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(log);
            outputStreamWriter.close();

        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void saveActivityToLog() {
        logSave = "";
        logSave = Integer.toString(dimensionSelecter.getSelectedItemPosition()) + " ";
        logSave = logSave + Integer.toString(matrixSelecter.getSelectedItemPosition()) + " ";
        logSave = logSave + SelecterMatrixToString();
        logSave = logSave + SelecterBlockMatrixToString();
        logSave = logSave + SaveEntryStatus();

        Save(logSave);
    }

}
