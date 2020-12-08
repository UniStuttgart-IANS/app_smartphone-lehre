package com.uni_stuttgart.isl.EliminateLGS;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.nishant.math.MathView;
import com.uni_stuttgart.isl.EliminateLGS.MyCanvas;
import com.uni_stuttgart.isl.Intros.Intro_EliminateLGS;
import com.uni_stuttgart.isl.Intros.PrefManager;
import com.uni_stuttgart.isl.MainActivity;
import com.uni_stuttgart.isl.R;

import static java.lang.Math.max;
import static java.lang.Math.floor;

public class EliminateLGS extends AppCompatActivity {
    private PrefManager prefManager;
    private Toolbar toolbar;
    private int dimMat=5;
    private int method = 2;
    private MyCanvas myCanvas;
    private int sizeCanvasx;
    private int sizeCanvasy;
    private String m_Text;
    private SeekBar dimensionSeekbar;
    private TextView dimensionView;
    private Spinner MethodSpinner;

    private MathView textL;
    private MathView textR;
    private MathView textP;
    private WebView webView;

    private Switch change_or_elim;

    private int selcted_matrix=0;
    private int num_matrices=5;

    private int minCanvasWidth=400;
    private int maxCanvasWidth=1100;

    public AlertDialog alertDialog;

    private LinearLayout left_view;

    private int mat_depo[][] = {
// 2 x 2 Matrizen
            {5,1,7,-1},
            {-4,3,3,-6},
            {1,-1,3,2},
            {5,-3,-2,1},
            {2,-2,3,2},
// 3 x 3 Matrizen
            {7,3,-5,-1,-2,4,-4,1,-3},
            {2,-3,4,3,4,-5,4,-6,3},
            {3,2,-4,4,-5,3,8,7,-9},
            {6,-1,2,5,-3,3,3,-2,1},
            {4,3,1,2,-5,3,7,-1,-2},
// 4 x 4 Matrizen
            {0,10,0,12,4,-5,-2,-8,0,-5,0,-4,4,-10,0,-16},
            {1,1,1,1,1,2,4,8,1,4,16,64,0,1,5,25,125},
            {2,2,2,2,2,-2,2,-2,2,-4,8,-16,0,2,-6,18,-54},
            {1,-1,1,-1,1,2,4,8,1,4,16,64,1,6,36,216},
            {1,-1,1,-1,1,-2,4,-8,1,3,9,27,1,-3,9,-270},
// 5 x 5 Matrizen
            {1,2,4,3,5,3,5,3,1,2,1,4,4,2,1,4,1,2,5,3,5,2,1,4,1},
            {2,-1,4,1,-1,-1,3,-2,-1,2,5,1,3,-4,1,3,-2,-2,-2,3,-4,-1,-5,3,-4},
            {2,4,2,3,5,-6,4,4,-2,1,7,-3,6,1,2,9,-1,8,-4,2,1,6,10,7,9},
            {1,4,10,2,9,2,3,4,5,-5,6,-2,5,4,-3,8,-1,2,3,4,4,4,6,4,-4},
            {2,2,4,5,6,2,2,3,3,2,1,2,1,5,8,2,4,1,5,8,2,2,1,5,8}};

    int rhs_depo[][]={
// 2 x 2 RHS
        {10,0,2,0},
        {6,0,3,0},
        {7,0,11,0},
        {24,0,9,0},
        {14,0,11,0},
// 3 x 3 RHS
        {-12,0,0,5,0,0,1,0,0},
        {8,0,0,-4,0,0,1,0,0},
        {-2,0,0,9,0,0,13,0,0},
        {1,0,0,4,0,0,14,0,0},
        {13,0,0,1,0,0,-1,0,0},
// 4 x 4 RHS
        {-6,0,0,0,-17,0,0,0,-23,0,0,0,2,0,0,0},
        {4,0,0,0,2,0,0,0,4,0,0,0,20,0,0,0},
        {-11,0,0,0,9,0,0,0,16,0,0,0,5,0,0,0},
        {-16,0,0,0,11,0,0,0,-11,0,0,0,-9,0,0,0},
        {7,0,0,0,6,0,0,0,1,0,0,0,-2,0,0,0},
//5 x 5 RHS
        {5,0,0,0,0,6,0,0,0,0,7,0,0,0,0,8,0,0,0,0,9,0,0,0,0},
        {7,0,0,0,0,1,0,0,0,0,22,0,0,0,0,24,0,0,0,0,-49,0,0,0,0},
        {5,0,0,0,0,-5,0,0,0,0,-2,0,0,0,0,-4,0,0,0,0,7,0,0,0,0},
        {44,0,0,0,0,28,0,0,0,0,-9,0,0,0,0,15,0,0,0,0,24,0,0,0,0},
        {7,0,0,0,0,12,0,0,0,0,-5,0,0,0,0,-5,0,0,0,0,-5,0,0,0,0}};

    int gj_rhs [][] = {{1,0,0,1},{1,0,0,0,1,0,0,0,1},{1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1},{1,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,1}};
    private CharSequence[] methods = {"GAUß-JORDAN", "Gauß-Eimination", "LR-Zerlegung", "Gauß-Jordan"};

    private double[][] finalMatrix;
    private boolean flag = true;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_eliminate_lgs, menu);
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
            Intent myIntent = new Intent(EliminateLGS.this, Intro_EliminateLGS.class);
            EliminateLGS.this.startActivity(myIntent);
            freeMemory();
            finish();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminate_lgs);

        prefManager = new PrefManager(this);

        // Erzeugen der Toolbar
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        // Zurückbutton, was soll beim "Click" passieren
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start der Main
                Intent myIntent = new Intent(EliminateLGS.this, MainActivity.class);
                EliminateLGS.this.startActivity(myIntent);
                freeMemory();
                finish();
            }
        });


        // Größe der "Fläche" auslesen
        myCanvas = (MyCanvas) findViewById(R.id.mycanvaseliminatelags);
        left_view= (LinearLayout) findViewById(R.id.left_linear_layout);

        dimensionSeekbar = (SeekBar) findViewById(R.id.dimensionseekbar);
        dimensionView = (TextView) findViewById(R.id.dimensionview);
        MethodSpinner = (Spinner)  findViewById(R.id.MethodeSelecter);

        change_or_elim = (Switch) findViewById(R.id.change_or_elim);
        myCanvas.set_change_or_elim(change_or_elim);


        textL = (MathView) findViewById(R.id.textL);
        textR = (MathView) findViewById(R.id.textR);
        textP = (MathView) findViewById(R.id.textP);

        final Button undo = findViewById(R.id.ButtonUndo);
        final Button reset = findViewById(R.id.ButtonReset);
        final Button newmat = findViewById(R.id.ButtonNew);

        alertDialog = new AlertDialog.Builder(EliminateLGS.this).create();

        myCanvas.set_alertDialog(alertDialog);
        myCanvas.setTextViews(textL,textR,textP);

        myCanvas.post(new Runnable() {
            @Override
            public void run() {

                sizeCanvasx = myCanvas.getWidth();
                sizeCanvasy = myCanvas.getHeight();

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;

                int width_left_view =undo.getWidth()+reset.getWidth()+newmat.getWidth();

                ViewGroup.LayoutParams params = dimensionSeekbar.getLayoutParams();
                params.width = width_left_view;
                dimensionSeekbar.setLayoutParams(params);

                sizeCanvasx = max(minCanvasWidth,width-width_left_view);
                sizeCanvasy = (int) floor(sizeCanvasx*0.625);
                if ( height-toolbar.getHeight() < sizeCanvasy) {
                    sizeCanvasy = height-toolbar.getHeight();
                    sizeCanvasx = (int) floor(1.6*sizeCanvasy);
                }

                if(sizeCanvasx>maxCanvasWidth) {
                    sizeCanvasx = maxCanvasWidth;
                    sizeCanvasy = (int) floor(sizeCanvasx*0.625);
                }


                ViewGroup.LayoutParams ParamsCanvas = myCanvas.getLayoutParams();
                ParamsCanvas.width = sizeCanvasx;
                ParamsCanvas.height = sizeCanvasy;
                myCanvas.setLayoutParams(ParamsCanvas);

                myCanvas.set_screensize(sizeCanvasx,sizeCanvasy);
                myCanvas.set_init();
                myCanvas.invalidate();

                loadActivity();
            }

        });

    }

    private void loadActivity() {

        ArrayAdapter<CharSequence> adapter_SpinnerDimension = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, methods);
        // Specify the layout to use when the list of choices appears
        adapter_SpinnerDimension.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter_SpinnerDimension
        //to the spinner
        MethodSpinner.setAdapter(adapter_SpinnerDimension);

        MethodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:

                        break;

                    case 1:
                        methods[0] = "GAUß-ELIMINATION";
                        method = 0;
                        myCanvas.setModus(0);
                        myCanvas.set_numrhs(1);
                        break;
                    case 2:
                        methods[0] = "LR-Zerlegung";
                        method=1;
                        myCanvas.setModus(1);
                        myCanvas.set_numrhs(dimMat);
                        break;
                    case 3:
                        methods[0] = "GAUß-JORDAN";
                        method=2;
                        myCanvas.setModus(2);
                        myCanvas.set_numrhs(dimMat);
                        break;
                }
                change_matrix();
                myCanvas.set_init();
                myCanvas.invalidate();
                freeMemory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        dimensionSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {

                dimMat =seekBar.getProgress()+2;
                dimensionView.setText(""+ dimMat);
                dimMat =seekBar.getProgress()+2;
                myCanvas.set_numrhs(dimMat);
                myCanvas.set_matdim(dimMat);
                freeMemory();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                dimMat =seekBar.getProgress()+2;
                dimensionView.setText(""+ dimMat);
                myCanvas.set_numrhs(dimMat);
                myCanvas.set_matdim(dimMat);
                freeMemory();

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                dimMat =seekBar.getProgress()+2;
                dimensionView.setText(""+ dimMat);
                myCanvas.set_numrhs(dimMat);
                myCanvas.set_matdim(dimMat);
                change_matrix();
                myCanvas.set_init();
                myCanvas.invalidate();
                freeMemory();
            }
        });
    }

    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }


    public String[] StringToStringVev(String s) {
        String[] splitString;

        splitString = s.split(" ");

        return splitString;
    }

    public double[] StringVecToDoubleVec(String[] s){
        double[] d = new double[s.length];

        for (int i = 0; i < s.length; i++) {
            d[i] = Double.parseDouble(s[i]);
        }

        return d;
    }

    public void new_matrix(View view) {
        selcted_matrix++;
        selcted_matrix=selcted_matrix%num_matrices;
        change_matrix();
    }

    public void change_matrix() {
        myCanvas.set_matrix(mat_depo[num_matrices*(dimMat-2)+selcted_matrix]);
        if(method==0) {
          myCanvas.set_rhs(rhs_depo[num_matrices*(dimMat-2)+selcted_matrix]);}
        else {
          myCanvas.set_rhs(gj_rhs[dimMat-2]);
        }
        myCanvas.set_pivot(gj_rhs[dimMat-2]);
        myCanvas.set_init();
        myCanvas.invalidate();
    }

    public void reset(View view) {
        change_matrix();
        myCanvas.reset_params();

    }


    public void undo(View view) {
        myCanvas.undo();

    }
}