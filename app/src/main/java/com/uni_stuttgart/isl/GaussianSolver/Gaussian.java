package com.uni_stuttgart.isl.GaussianSolver;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Gravity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.uni_stuttgart.isl.Intros.Intro_Gaussian;
import com.uni_stuttgart.isl.Intros.PrefManager;
import com.uni_stuttgart.isl.MainActivity;
import com.uni_stuttgart.isl.R;

import java.util.ArrayList;

public class Gaussian extends AppCompatActivity {
    private PrefManager prefManager;
    private Toolbar toolbar;

    private LinearLayout[] row = new LinearLayout[5];
    private LinearLayout[] popUpRow = new LinearLayout[5];
    private LinearLayout showLGS;
    private LinearLayout leftscreen;

    private EditText[] edit_a = new EditText[25];
    private EditText[] edit_b = new EditText[25];
    private EditText[] PopUpedit_a = new EditText[25];
    private EditText[] PopUpedit_b = new EditText[25];

    private EditText ed_scale;
    private TextView manual;

    private Button eliminateButton;
    private Button fillLGSButton;
    private Button InputButton;
    private Button swapRowsButton;
    private Button closePopupBtn;
    private Button PUscaleButton;
    private Button PUelimButton1;
    private Button PUelimButton2;
    private Button PUswapButton;

    private Spinner dimensionSelecter;
    private Spinner algorithmSelecter;
    private Spinner eRow1Selecter;
    private Spinner eRow2Selecter;
    private Spinner sRow1Selecter;
    private Spinner sRow2Selecter;

    private Spinner scaleSelecter;

    private  EditText eFactor1;
    private  EditText[] PopUpEdit = new EditText[2];
    private  TextView[] editPopUp = new TextView[8];
    private  TextView eRowText;


    private CharSequence[] algorithms = {"Gauß-Elimination", "Gauß-Jordan Verfahren", "LR-Zerlegung"};
    private CharSequence[] dimensions = {"3x3", "4x4", "5x5"};
    ArrayList<String> num_lines = new ArrayList<String>();

    private double[][] finalMatrix;
    private double[] Matrix;
    private double[] RHS;
    private boolean flag = true;
    private int dim = 3;
    private int algo = 0;

    private int eliminate1=0;
    private int eliminate2=0;
    private int swap1=0;
    private int swap2=0;
    private int max_dim = 5;
    private double scale=0;
    private int srow=0;

    private int selected_row1 = -1;
    private int selected_row2 = -1;

    private boolean phase1_finished = false;

    private LayoutInflater layoutInflater;
    private PopupWindow popupWindow;
    private RelativeLayout relativeLayout;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gauss, menu);
        return true;
    }

    // Funktion zum erzeugen den Menüpunktes "oben-rechts"
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        // Bei Auswahl von "Introduction" start von Intro und speichern der aktuellen Konfiguration
        if (item.getItemId() == R.id.introduction) {
            // Beim erneuten
            prefManager.setReopenGaussianIntro(false);
//            saveActivityToLog();
            Intent myIntent = new Intent(Gaussian.this, Intro_Gaussian.class);
            Gaussian.this.startActivity(myIntent);
            freeMemory();
            finish();
        }
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaussian_solver);

        prefManager = new PrefManager(this);

        // Erzeugen der Toolbar
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        // Zurückbutton, was soll beim "Click" passieren
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start der Main
                Intent myIntent = new Intent(Gaussian.this, MainActivity.class);
                Gaussian.this.startActivity(myIntent);
                freeMemory();
                finish();
            }
        });

        num_lines.add("-");
        num_lines.add("I");
        num_lines.add("II");
        num_lines.add("III");


        manual = (TextView) findViewById(R.id.manual);

        eFactor1 = (EditText) findViewById(R.id.e_factor1);
        ed_scale = (EditText) findViewById(R.id.edit_sfactor);
        eRowText = (TextView) findViewById(R.id.eliminateRowText);

        row[0] = (LinearLayout) findViewById(R.id.row1);
        row[1] = (LinearLayout) findViewById(R.id.row2);
        row[2] = (LinearLayout) findViewById(R.id.row3);
        row[3] = (LinearLayout) findViewById(R.id.row4);
        row[4] = (LinearLayout) findViewById(R.id.row5);
        showLGS = (LinearLayout) findViewById(R.id.showLGS);
        leftscreen = (LinearLayout) findViewById(R.id.leftScreen);

        edit_a[0] = (EditText) findViewById(R.id.edit_a11);
        edit_a[1] = (EditText) findViewById(R.id.edit_a12);
        edit_a[2] = (EditText) findViewById(R.id.edit_a13);
        edit_a[3] = (EditText) findViewById(R.id.edit_a14);
        edit_a[4] = (EditText) findViewById(R.id.edit_a15);
        edit_a[5] = (EditText) findViewById(R.id.edit_a21);
        edit_a[6] = (EditText) findViewById(R.id.edit_a22);
        edit_a[7] = (EditText) findViewById(R.id.edit_a23);
        edit_a[8] = (EditText) findViewById(R.id.edit_a24);
        edit_a[9] = (EditText) findViewById(R.id.edit_a25);
        edit_a[10] = (EditText) findViewById(R.id.edit_a31);
        edit_a[11] = (EditText) findViewById(R.id.edit_a32);
        edit_a[12] = (EditText) findViewById(R.id.edit_a33);
        edit_a[13] = (EditText) findViewById(R.id.edit_a34);
        edit_a[14] = (EditText) findViewById(R.id.edit_a35);
        edit_a[15] = (EditText) findViewById(R.id.edit_a41);
        edit_a[16] = (EditText) findViewById(R.id.edit_a42);
        edit_a[17] = (EditText) findViewById(R.id.edit_a43);
        edit_a[18] = (EditText) findViewById(R.id.edit_a44);
        edit_a[19] = (EditText) findViewById(R.id.edit_a45);
        edit_a[20] = (EditText) findViewById(R.id.edit_a51);
        edit_a[21] = (EditText) findViewById(R.id.edit_a52);
        edit_a[22] = (EditText) findViewById(R.id.edit_a53);
        edit_a[23] = (EditText) findViewById(R.id.edit_a54);
        edit_a[24] = (EditText) findViewById(R.id.edit_a55);

        edit_b[0] = (EditText) findViewById(R.id.edit_b11);
        edit_b[1] = (EditText) findViewById(R.id.edit_b12);
        edit_b[2] = (EditText) findViewById(R.id.edit_b13);
        edit_b[3] = (EditText) findViewById(R.id.edit_b14);
        edit_b[4] = (EditText) findViewById(R.id.edit_b15);
        edit_b[5] = (EditText) findViewById(R.id.edit_b21);
        edit_b[6] = (EditText) findViewById(R.id.edit_b22);
        edit_b[7] = (EditText) findViewById(R.id.edit_b23);
        edit_b[8] = (EditText) findViewById(R.id.edit_b24);
        edit_b[9] = (EditText) findViewById(R.id.edit_b25);
        edit_b[10] = (EditText) findViewById(R.id.edit_b31);
        edit_b[11] = (EditText) findViewById(R.id.edit_b32);
        edit_b[12] = (EditText) findViewById(R.id.edit_b33);
        edit_b[13] = (EditText) findViewById(R.id.edit_b34);
        edit_b[14] = (EditText) findViewById(R.id.edit_b35);
        edit_b[15] = (EditText) findViewById(R.id.edit_b41);
        edit_b[16] = (EditText) findViewById(R.id.edit_b42);
        edit_b[17] = (EditText) findViewById(R.id.edit_b43);
        edit_b[18] = (EditText) findViewById(R.id.edit_b44);
        edit_b[19] = (EditText) findViewById(R.id.edit_b45);
        edit_b[20] = (EditText) findViewById(R.id.edit_b51);
        edit_b[21] = (EditText) findViewById(R.id.edit_b52);
        edit_b[22] = (EditText) findViewById(R.id.edit_b53);
        edit_b[23] = (EditText) findViewById(R.id.edit_b54);
        edit_b[24] = (EditText) findViewById(R.id.edit_b55);

        eliminateButton = (Button) findViewById(R.id.eliminateButton);
        swapRowsButton = (Button) findViewById(R.id.swapButton);
        fillLGSButton = (Button) findViewById(R.id.FillLGSButton);
        InputButton = (Button) findViewById(R.id.InputButton);

        // Dimension über einen Spinner wählen
        dimensionSelecter = (Spinner) findViewById(R.id.DimensionSelecter);

        ArrayAdapter<CharSequence> adapter_SpinnerDimension = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, dimensions);
        // Specify the layout to use when the list of choices appears
        adapter_SpinnerDimension.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter_SpinnerDimension
        //to the spinner
        dimensionSelecter.setAdapter(adapter_SpinnerDimension);

        dimensionSelecter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        dim = 3;
                        num_lines.remove("IV");
                        num_lines.remove("V");
                        break;
                    case 1:
                        dim = 4;
                        if(!num_lines.contains("IV"))
                            num_lines.add("IV");
                        num_lines.remove("V");
                        break;
                    case 2:
                        dim = 5;
                        if(!num_lines.contains("IV"))
                            num_lines.add("IV");
                        if(!num_lines.contains("V"))
                            num_lines.add("V");
                        break;
                }
                freeMemory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        // Algorithmus über einen Spinner wählen
        algorithmSelecter = (Spinner) findViewById(R.id.AlgorithmSelecter);

        ArrayAdapter<CharSequence> adapter_SpinnerAlgorithm = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, algorithms);
        // Specify the layout to use when the list of choices appears
        adapter_SpinnerAlgorithm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter_SpinnerDimension
        //to the spinner
        algorithmSelecter.setAdapter(adapter_SpinnerAlgorithm);

        algorithmSelecter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                algo = position;
                freeMemory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        //  über einen Spinner wählen
        eRow1Selecter = (Spinner) findViewById(R.id.Row1Selecter);

        ArrayAdapter<String> adapter_SpinnerRow1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, num_lines);
        // Specify the layout to use when the list of choices appears
        adapter_SpinnerRow1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter_SpinnerDimension
        //to the spinner
        eRow1Selecter.setAdapter(adapter_SpinnerRow1);
        adapter_SpinnerRow1.setNotifyOnChange(true);

        eRow1Selecter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                eliminate1 = position;
                freeMemory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //  über einen Spinner wählen
        eRow2Selecter = (Spinner) findViewById(R.id.Row2Selecter);

        ArrayAdapter<String> adapter_SpinnerRow2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, num_lines);
        // Specify the layout to use when the list of choices appears
        adapter_SpinnerRow2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter_SpinnerDimension
        //to the spinner
        eRow2Selecter.setAdapter(adapter_SpinnerRow2);

        eRow2Selecter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        eliminate2=0;
                        eRowText.setText("Ersetze ");
                        break;
                    case 1:
                        eliminate2=1;
                        eRowText.setText("Ersetze I durch:");
                        break;
                    case 2:
                        eliminate2=2;
                        eRowText.setText("Ersetze II durch:");
                        break;
                    case 3:
                        eliminate2=3;
                        eRowText.setText("Ersetze III durch:");
                        break;
                    case 4:
                        eliminate2=4;
                        eRowText.setText("Ersetze IV durch:");
                        break;
                    case 5:
                        eliminate2=5;
                        eRowText.setText("Ersetze V durch:");
                        break;
                }
                freeMemory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Dimension über einen Spinner wählen
        sRow1Selecter = (Spinner) findViewById(R.id.swapRow1Selecter);

        ArrayAdapter<String> adapter_SpinnerSwapRow1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, num_lines);
        // Specify the layout to use when the list of choices appears
        adapter_SpinnerSwapRow1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter_SpinnerDimension
        //to the spinner
        sRow1Selecter.setAdapter(adapter_SpinnerSwapRow1);

        sRow1Selecter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                swap1 = position;
                freeMemory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //  über einen Spinner wählen
        sRow2Selecter = (Spinner) findViewById(R.id.swapRow2Selecter);

        ArrayAdapter<String> adapter_SpinnerSwapRow2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, num_lines);
        // Specify the layout to use when the list of choices appears
        adapter_SpinnerSwapRow2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter_SpinnerDimension
        //to the spinner
        sRow2Selecter.setAdapter(adapter_SpinnerSwapRow2);

        sRow2Selecter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                swap2 = position;
                freeMemory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        //  über einen Spinner wählen
        scaleSelecter = (Spinner) findViewById(R.id.ScaleSelecter);

        ArrayAdapter<String> adapter_SpinnerScale = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, num_lines);
        // Specify the layout to use when the list of choices appears
        adapter_SpinnerScale.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter_SpinnerDimension
        //to the spinner
        scaleSelecter.setAdapter(adapter_SpinnerScale);

        scaleSelecter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                srow = position;
                freeMemory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final Context context = this;
        for (int i = 0; i < max_dim * max_dim; i++) {
            final int fi = i;
            edit_a[fi].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!phase1_finished || algo==1) {
                        if (selected_row1 == -1) {
                            ChooseFirstRow(fi);
                            selected_row1 = fi / max_dim;
                        } else {
                            ChooseSecondRow(fi);
                            selected_row2 = fi / max_dim;

                            if (selected_row1 == selected_row2) {
                                if (algo == 2) {
                                    UnselectRow(selected_row1);
                                    UnselectRow(selected_row2);
                                    selected_row2 = -1;
                                    selected_row1 = -1;
                                } else {
                                    //Scale
                                    // inflate the layout of the popup window
                                    LayoutInflater inflater = (LayoutInflater)
                                            getSystemService(LAYOUT_INFLATER_SERVICE);
                                    View popupView = inflater.inflate(R.layout.popup_gaussian_same_row, null);

                                    // create the popup window
                                    int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                                    int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                    boolean focusable = true; // lets taps outside the popup also dismiss it
                                    final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);


                                    // show the popup window
                                    // which view you pass in doesn't matter, it is only used for the window tolken
                                    popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                                    editPopUp[0] = (TextView) popupView.findViewById(R.id.scaleRowID);
                                    editPopUp[0].setText(num_lines.get(selected_row1 + 1));

                                    closePopupBtn = (Button) popupView.findViewById(R.id.closePopUp);
                                    //close the popup window on button click
                                    closePopupBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            UnselectRow(selected_row1);
                                            UnselectRow(selected_row2);
                                            popupWindow.dismiss();
                                            selected_row2 = selected_row1 = -1;
                                        }
                                    });

                                    PopUpEdit[0] = (EditText) popupView.findViewById(R.id.edit_sfactor);
                                    PUscaleButton = (Button) popupView.findViewById(R.id.scaleButton);
                                    PUscaleButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            int dividend = 1, divisor = 1;
                                            AlertDialog alertDialog = new AlertDialog.Builder(Gaussian.this).create();

                                            boolean goodInput = true;
                                            boolean fraction = false;
                                            String string1 = PopUpEdit[0].getText().toString();
                                            String pieces[] = string1.split("/");

                                            if (TextUtils.isEmpty(string1))
                                                goodInput = false;
                                            if (string1.contains("/")) {
                                                //possible division
                                                if (pieces.length != 2) {
                                                    goodInput = false;
                                                } else {
                                                    try {
                                                        fraction = true;
                                                        dividend = Integer.parseInt(pieces[0]);
                                                        divisor = Integer.parseInt(pieces[1]);
                                                        scale = Double.parseDouble(pieces[0]) / Double.parseDouble(pieces[1]);
                                                    } catch (Exception e) {
                                                        goodInput = false;
                                                    }
                                                }
                                            } else if (string1.contains(".")) {
                                                try {
                                                    scale = Double.parseDouble(string1);
                                                } catch (Exception e) {
                                                    goodInput = false;
                                                }
                                            } else {
                                                try {
                                                    scale = Double.parseDouble(string1);
                                                } catch (Exception e) {
                                                    goodInput = false;
                                                }
                                            }

                                            if (TextUtils.isEmpty(string1) || !goodInput) {
                                                alertDialog.setMessage("Forbidden input data");
                                                alertDialog.show();
                                                return;
                                            }
                                            if (scale == 0 || dividend == 0 || divisor == 0) {
                                                alertDialog.setMessage("Forbidden input data!");
                                                alertDialog.show();
                                                return;
                                            }

                                            int start1 = dim * (selected_row1);
                                            if(!fraction) {
                                                for (int i = 0; i < dim; i++) {
                                                    Matrix[start1 + i] *= scale;
                                                    if (algo == 1)
                                                        RHS[start1 + i] *= scale;
                                                }
                                                if (algo == 0) {
                                                    RHS[selected_row1 * max_dim] *= scale;
                                                }
                                            }
                                            else {
                                                for (int i = 0; i < dim; i++) {
                                                    Matrix[start1 + i] *= new Double(dividend) / divisor;
                                                    if (algo == 1)
                                                        RHS[start1 + i] *= new Double(dividend) / divisor;
                                                }
                                                if (algo == 0) {
                                                    RHS[selected_row1 * max_dim] *= new Double(dividend) / divisor;
                                                }
                                            }
                                            updateLGSView();
                                            popupWindow.dismiss();
                                            UnselectRow(selected_row1);
                                            selected_row2 = selected_row1 = -1;
                                        }
                                    });
                                }
                            }
                            else {

                                int tmp;
                                int orig_sel1, orig_sel2;
                                orig_sel1 = selected_row1;
                                orig_sel2 = selected_row2;
                                if (selected_row2 < selected_row1 && !phase1_finished) {
                                    tmp = selected_row1;
                                    selected_row1 = selected_row2;
                                    selected_row2 = tmp;
                                }
                                if (selected_row2 > selected_row1 && phase1_finished) {
                                    tmp = selected_row1;
                                    selected_row1 = selected_row2;
                                    selected_row2 = tmp;
                                }
                                // inflate the layout of the popup window
                                LayoutInflater inflater = (LayoutInflater)
                                        getSystemService(LAYOUT_INFLATER_SERVICE);
                                View popupView = inflater.inflate(R.layout.popup_gaussian_different_rows, null);

                                popUpRow[0] = (LinearLayout) popupView.findViewById(R.id.row1);
                                popUpRow[1] = (LinearLayout) popupView.findViewById(R.id.row2);
                                popUpRow[2] = (LinearLayout) popupView.findViewById(R.id.row3);
                                popUpRow[3] = (LinearLayout) popupView.findViewById(R.id.row4);
                                popUpRow[4] = (LinearLayout) popupView.findViewById(R.id.row5);


                                PopUpedit_a[0] = (EditText) popupView.findViewById(R.id.edit_a11);
                                PopUpedit_a[1] = (EditText) popupView.findViewById(R.id.edit_a12);
                                PopUpedit_a[2] = (EditText) popupView.findViewById(R.id.edit_a13);
                                PopUpedit_a[3] = (EditText) popupView.findViewById(R.id.edit_a14);
                                PopUpedit_a[4] = (EditText) popupView.findViewById(R.id.edit_a15);
                                PopUpedit_a[5] = (EditText) popupView.findViewById(R.id.edit_a21);
                                PopUpedit_a[6] = (EditText) popupView.findViewById(R.id.edit_a22);
                                PopUpedit_a[7] = (EditText) popupView.findViewById(R.id.edit_a23);
                                PopUpedit_a[8] = (EditText) popupView.findViewById(R.id.edit_a24);
                                PopUpedit_a[9] = (EditText) popupView.findViewById(R.id.edit_a25);
                                PopUpedit_a[10] = (EditText) popupView.findViewById(R.id.edit_a31);
                                PopUpedit_a[11] = (EditText) popupView.findViewById(R.id.edit_a32);
                                PopUpedit_a[12] = (EditText) popupView.findViewById(R.id.edit_a33);
                                PopUpedit_a[13] = (EditText) popupView.findViewById(R.id.edit_a34);
                                PopUpedit_a[14] = (EditText) popupView.findViewById(R.id.edit_a35);
                                PopUpedit_a[15] = (EditText) popupView.findViewById(R.id.edit_a41);
                                PopUpedit_a[16] = (EditText) popupView.findViewById(R.id.edit_a42);
                                PopUpedit_a[17] = (EditText) popupView.findViewById(R.id.edit_a43);
                                PopUpedit_a[18] = (EditText) popupView.findViewById(R.id.edit_a44);
                                PopUpedit_a[19] = (EditText) popupView.findViewById(R.id.edit_a45);
                                PopUpedit_a[20] = (EditText) popupView.findViewById(R.id.edit_a51);
                                PopUpedit_a[21] = (EditText) popupView.findViewById(R.id.edit_a52);
                                PopUpedit_a[22] = (EditText) popupView.findViewById(R.id.edit_a53);
                                PopUpedit_a[23] = (EditText) popupView.findViewById(R.id.edit_a54);
                                PopUpedit_a[24] = (EditText) popupView.findViewById(R.id.edit_a55);

                                PopUpedit_b[0] = (EditText) popupView.findViewById(R.id.edit_b11);
                                PopUpedit_b[1] = (EditText) popupView.findViewById(R.id.edit_b12);
                                PopUpedit_b[2] = (EditText) popupView.findViewById(R.id.edit_b13);
                                PopUpedit_b[3] = (EditText) popupView.findViewById(R.id.edit_b14);
                                PopUpedit_b[4] = (EditText) popupView.findViewById(R.id.edit_b15);
                                PopUpedit_b[5] = (EditText) popupView.findViewById(R.id.edit_b21);
                                PopUpedit_b[6] = (EditText) popupView.findViewById(R.id.edit_b22);
                                PopUpedit_b[7] = (EditText) popupView.findViewById(R.id.edit_b23);
                                PopUpedit_b[8] = (EditText) popupView.findViewById(R.id.edit_b24);
                                PopUpedit_b[9] = (EditText) popupView.findViewById(R.id.edit_b25);
                                PopUpedit_b[10] = (EditText) popupView.findViewById(R.id.edit_b31);
                                PopUpedit_b[11] = (EditText) popupView.findViewById(R.id.edit_b32);
                                PopUpedit_b[12] = (EditText) popupView.findViewById(R.id.edit_b33);
                                PopUpedit_b[13] = (EditText) popupView.findViewById(R.id.edit_b34);
                                PopUpedit_b[14] = (EditText) popupView.findViewById(R.id.edit_b35);
                                PopUpedit_b[15] = (EditText) popupView.findViewById(R.id.edit_b41);
                                PopUpedit_b[16] = (EditText) popupView.findViewById(R.id.edit_b42);
                                PopUpedit_b[17] = (EditText) popupView.findViewById(R.id.edit_b43);
                                PopUpedit_b[18] = (EditText) popupView.findViewById(R.id.edit_b44);
                                PopUpedit_b[19] = (EditText) popupView.findViewById(R.id.edit_b45);
                                PopUpedit_b[20] = (EditText) popupView.findViewById(R.id.edit_b51);
                                PopUpedit_b[21] = (EditText) popupView.findViewById(R.id.edit_b52);
                                PopUpedit_b[22] = (EditText) popupView.findViewById(R.id.edit_b53);
                                PopUpedit_b[23] = (EditText) popupView.findViewById(R.id.edit_b54);
                                PopUpedit_b[24] = (EditText) popupView.findViewById(R.id.edit_b55);

                                int help = 0;
                                for (int i = 0; i < dim * dim; i++) {
                                    PopUpedit_a[i + help].setText(edit_a[i + help].getText());
                                    if (algo == 1)
                                        PopUpedit_b[i + help].setText(edit_b[i + help].getText());
                                    if ((i + 1) % dim == 0) help += (max_dim - dim);
                                }
                                if (algo == 0) {
                                    for (int i = 0; i < dim; i++) {
                                        PopUpedit_b[i * max_dim].setText(edit_b[i * max_dim].getText());
                                    }
                                }

                                for(int i = 0; i<max_dim;i++) {
                                    popUpRow[i].setVisibility(View.GONE);
                                }

                                popUpRow[orig_sel1].setVisibility(View.VISIBLE);
                                popUpRow[orig_sel1].setBackgroundColor(getResources().getColor((R.color.green)));
                                popUpRow[orig_sel2].setVisibility(View.VISIBLE);
                                popUpRow[orig_sel2].setBackgroundColor(getResources().getColor((R.color.blue)));

                                editPopUp[0] = (TextView) popupView.findViewById(R.id.elRowID0);
                                editPopUp[1] = (TextView) popupView.findViewById(R.id.elRowID1);
                                editPopUp[2] = (TextView) popupView.findViewById(R.id.elRowID2);
                                //                            editPopUp[3] = (TextView) popupView.findViewById(R.id.elRowID0b);
                                //                            editPopUp[4] = (TextView) popupView.findViewById(R.id.elRowID1b);
                                //                            editPopUp[5] = (TextView) popupView.findViewById(R.id.elRowID2b);
                                editPopUp[6] = (TextView) popupView.findViewById(R.id.swapRowID1);
                                editPopUp[7] = (TextView) popupView.findViewById(R.id.swapRowID2);

                                editPopUp[0].setText(num_lines.get(selected_row2 + 1));
                                editPopUp[1].setText(num_lines.get(selected_row1 + 1));
                                editPopUp[2].setText(num_lines.get(selected_row2 + 1));
                                if (orig_sel1 > orig_sel2) {
                                    editPopUp[0].setBackgroundColor(getResources().getColor((R.color.green)));
                                    editPopUp[1].setBackgroundColor(getResources().getColor((R.color.blue)));
                                    editPopUp[2].setBackgroundColor(getResources().getColor((R.color.green)));
                                } else {
                                    editPopUp[0].setBackgroundColor(getResources().getColor((R.color.blue)));
                                    editPopUp[1].setBackgroundColor(getResources().getColor((R.color.green)));
                                    editPopUp[2].setBackgroundColor(getResources().getColor((R.color.blue)));
                                }

                                //                            editPopUp[3].setText(num_lines.get(selected_row1+1));
                                //                            editPopUp[4].setText(num_lines.get(selected_row2+1));
                                //                            editPopUp[5].setText(num_lines.get(selected_row1+1));
                                editPopUp[6].setText(num_lines.get(selected_row1 + 1));
                                editPopUp[7].setText(num_lines.get(selected_row2 + 1));

                                // create the popup window
                                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                boolean focusable = true; // lets taps outside the popup also dismiss it
                                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                                // show the popup window
                                // which view you pass in doesn't matter, it is only used for the window tolken
                                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


                                PopUpEdit[0] = (EditText) popupView.findViewById(R.id.e_factor1);
                                //                            PopUpEdit[1] = (EditText) popupView.findViewById(R.id.e_factor1b);
                                PUelimButton1 = (Button) popupView.findViewById(R.id.eliminateButton);
                                PUelimButton1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        double fac1 = 0;
                                        int dividend = 1, divisor = 1;

                                        AlertDialog alertDialog = new AlertDialog.Builder(Gaussian.this).create();

                                        boolean goodInput = true;
                                        boolean fraction = false;
                                        String string1 = PopUpEdit[0].getText().toString();
                                        String pieces[] = string1.split("/");


                                        if (TextUtils.isEmpty(string1))
                                            goodInput = false;
                                        if (string1.contains("/")) {
                                            //possible division
                                            if (pieces.length != 2) {
                                                goodInput = false;
                                            } else {
                                                try {
                                                    fraction = true;
                                                    dividend = Integer.parseInt(pieces[0]);
                                                    divisor = Integer.parseInt(pieces[1]);
                                                    fac1 = Double.parseDouble(pieces[0]) / Double.parseDouble(pieces[1]);
                                                } catch (Exception e) {
                                                    goodInput = false;
                                                }
                                            }
                                        } else if (string1.contains(".")) {
                                            try {
                                                fac1 = Double.parseDouble(string1);
                                            } catch (Exception e) {
                                                goodInput = false;
                                            }
                                        } else {
                                            try {
                                                fac1 = Double.parseDouble(string1);
                                            } catch (Exception e) {
                                                goodInput = false;
                                            }
                                        }

                                        if (TextUtils.isEmpty(string1) || !goodInput) {
                                            alertDialog.setMessage("Forbidden input data");
                                            alertDialog.show();
                                            return;
                                        }
                                        if (fac1 == 0 || dividend == 0 || divisor == 0) {
                                            alertDialog.setMessage("Forbidden input data!");
                                            alertDialog.show();
                                            return;
                                        }

                                        int start1 = dim * (selected_row1);
                                        int start2 = dim * (selected_row2);

                                        if (fraction) {
                                            for (int i = 0; i < dim; i++) {
                                                Matrix[i + start2] = (Matrix[i + start1] * dividend) / divisor - Matrix[i + start2];
                                                if (algo == 1)
                                                    RHS[i + start2] = (RHS[i + start1] * dividend) / divisor - RHS[i + start2];
                                                else if (algo == 2)
                                                    if (i >= selected_row2)
                                                        RHS[i + start2] = Matrix[i + start2];
                                            }
                                            if (algo == 0) {
                                                RHS[selected_row2] = (RHS[selected_row1] * dividend) / divisor - RHS[selected_row2];
                                            } else if (algo == 2) {
                                                for (int i = 0; i < dim; i++) {
                                                    RHS[i] = Matrix[i];
                                                }
                                                int index = selected_row2 * dim + selected_row1;
                                                if (RHS[index] == 0)
                                                    RHS[index] = new Double(dividend) / divisor;
                                                else
                                                    RHS[index] *= new Double(dividend) / divisor;
                                            }
                                        } else {
                                            for (int i = 0; i < dim; i++) {
                                                Matrix[i + start2] = Matrix[i + start1] * fac1 - Matrix[i + start2];
                                                if (algo == 1)
                                                    RHS[i + start2] = RHS[i + start1] * fac1 - RHS[i + start2];
                                                else if (algo == 2)
                                                    if (i >= selected_row2)
                                                        RHS[i + start2] = Matrix[i + start2];
                                            }
                                            if (algo == 0) {
                                                RHS[selected_row2] = RHS[selected_row1] * fac1 - RHS[selected_row2];
                                            } else if (algo == 2) {
                                                for (int i = 0; i < dim; i++) {
                                                    RHS[i] = Matrix[i];
                                                }
                                                int index = selected_row2 * dim + selected_row1;
                                                if (RHS[index] == 0)
                                                    RHS[index] = fac1;
                                                else
                                                    RHS[index] *= fac1;
                                            }
                                        }

                                        updateLGSView();
                                        popupWindow.dismiss();
                                        UnselectRow(selected_row1);
                                        UnselectRow(selected_row2);
                                        popUpRow[selected_row1].setVisibility(View.GONE);
                                        popUpRow[selected_row1].setVisibility(View.GONE);
                                        selected_row2 = selected_row1 = -1;
                                    }
                                });/*
                                PUelimButton2 = (Button) popupView.findViewById(R.id.eliminateButtonb);
                                PUelimButton2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        double fac1 = 0;
                                        int dividend=1, divisor=1;

                                        AlertDialog alertDialog = new AlertDialog.Builder(Gaussian.this).create();

                                        boolean goodInput = true;
                                        boolean fraction = false;
                                        String string1 = PopUpEdit[1].getText().toString();
                                        String pieces[] = string1.split("/");

                                        if(TextUtils.isEmpty(string1))
                                            goodInput =false;
                                        if (string1.contains("/")) {
                                            //possible division
                                            if (pieces.length != 2) {
                                                goodInput = false;
                                            } else {
                                                try {
                                                    fraction =true;
                                                    dividend = Integer.parseInt(pieces[0]);
                                                    divisor = Integer.parseInt(pieces[1]);
                                                    fac1 = Double.parseDouble(pieces[0])/Double.parseDouble(pieces[1]);
                                                } catch (Exception e) {
                                                    goodInput = false;
                                                }
                                            }
                                        }  else if (string1.contains(".")) {
                                            try {
                                                fac1 = Double.parseDouble(string1);
                                            } catch (Exception e) {
                                                goodInput = false;
                                            }
                                        }
                                        else  {
                                            try {
                                                fac1 = Double.parseDouble(string1);
                                            } catch (Exception e) {
                                                goodInput = false;
                                            }
                                        }

                                        if(TextUtils.isEmpty(string1) || !goodInput) {
                                            alertDialog.setMessage("Forbidden input data");
                                            alertDialog.show();
                                            return;
                                        }
                                        if( fac1==0 || dividend == 0 || divisor ==0) {
                                            alertDialog.setMessage("Forbidden input data!");
                                            alertDialog.show();
                                            return;
                                        }

                                        int start1 = dim*(selected_row2);
                                        int start2 = dim*(selected_row1);

                                        if(fraction) {
                                            for (int i = 0; i < dim; i++) {
                                                Matrix[i + start2] = (Matrix[i + start1] * dividend) / divisor - Matrix[i + start2];
                                            }
                                            if(algo == 0) {
                                                RHS[selected_row1] = (RHS[selected_row2] * dividend) / divisor - RHS[selected_row1];
                                            }
                                        }
                                        else {
                                            for (int i = 0; i < dim; i++) {
                                                Matrix[i + start2] = Matrix[i + start1] * fac1 - Matrix[i + start2];
                                            }

                                            if(algo == 0) {
                                                RHS[selected_row1] = RHS[selected_row2] * fac1 - RHS[selected_row1];
                                            }
                                        }
                                        updateLGSView();
                                        popupWindow.dismiss();
                                        UnselectRow(selected_row1);
                                        UnselectRow(selected_row2);
                                        popUpRow[selected_row1].setVisibility(View.GONE);
                                        popUpRow[selected_row1].setVisibility(View.GONE);
                                        selected_row2 = selected_row1 = -1;
                                    }
                                });
    */
                                PUswapButton = (Button) popupView.findViewById(R.id.swapButton);
                                PUswapButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int start1 = dim * (selected_row1);
                                        int start2 = dim * (selected_row2);
                                        double[] tmp = new double[dim];
                                        for (int i = 0; i < dim; i++) {
                                            tmp[i] = Matrix[start1 + i];
                                        }
                                        for (int i = 0; i < dim; i++) {
                                            Matrix[i + start1] = Matrix[i + start2];
                                            Matrix[i + start2] = tmp[i];
                                            if (algo == 1) {
                                                RHS[i + start1] = RHS[i + start2];
                                                RHS[i + start2] = tmp[i];
                                            }
                                        }
                                        if (algo == 0) {
                                            double dtmp = RHS[selected_row1];
                                            RHS[selected_row1] = RHS[selected_row2];
                                            RHS[selected_row2] = dtmp;
                                        }

                                        updateLGSView();

                                        UnselectRow(selected_row1);
                                        UnselectRow(selected_row2);
                                        popUpRow[selected_row1].setVisibility(View.GONE);
                                        popUpRow[selected_row1].setVisibility(View.GONE);
                                        selected_row2 = selected_row1 = -1;
                                        popupWindow.dismiss();
                                    }
                                });

                                closePopupBtn = (Button) popupView.findViewById(R.id.closePopUp);
                                //close the popup window on button click
                                closePopupBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        UnselectRow(selected_row1);
                                        UnselectRow(selected_row2);
                                        popUpRow[selected_row1].setVisibility(View.GONE);
                                        popUpRow[selected_row1].setVisibility(View.GONE);
                                        selected_row2 = selected_row1 = -1;
                                        popupWindow.dismiss();
                                    }
                                });

                            }
                        }
                    }
                    else {
                        AlertDialog alertDialog = new AlertDialog.Builder(Gaussian.this).create();
                        alertDialog.setMessage("Phase 1 ist geschafft! Nun geht es mit Phase 2 weiter! Beachte die Anleitung.");
                        alertDialog.show();
                    }
                }
            });
        }
        for (int i = 0; i < max_dim * max_dim; i++) {
            final int fi = i;
            edit_b[fi].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!phase1_finished || algo == 1) {
                        if (selected_row1 == -1) {
                            ChooseFirstRow(fi);
                            selected_row1 = fi / max_dim;
                        } else {
                            ChooseSecondRow(fi);
                            selected_row2 = fi / max_dim;

                            if (selected_row1 == selected_row2) {
                                if (algo == 2) {
                                    UnselectRow(selected_row1);
                                    UnselectRow(selected_row2);
                                    selected_row2 = -1;
                                    selected_row1 = -1;
                                } else {
                                    //Scale
                                    // inflate the layout of the popup window
                                    LayoutInflater inflater = (LayoutInflater)
                                            getSystemService(LAYOUT_INFLATER_SERVICE);
                                    View popupView = inflater.inflate(R.layout.popup_gaussian_same_row, null);

                                    // create the popup window
                                    int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                                    int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                    boolean focusable = true; // lets taps outside the popup also dismiss it
                                    final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);


                                    // show the popup window
                                    // which view you pass in doesn't matter, it is only used for the window tolken
                                    popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                                    editPopUp[0] = (TextView) popupView.findViewById(R.id.scaleRowID);
                                    editPopUp[0].setText(num_lines.get(selected_row1 + 1));

                                    closePopupBtn = (Button) popupView.findViewById(R.id.closePopUp);
                                    //close the popup window on button click
                                    closePopupBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            UnselectRow(selected_row1);
                                            UnselectRow(selected_row2);
                                            popupWindow.dismiss();
                                            selected_row2 = selected_row1 = -1;
                                        }
                                    });

                                    PopUpEdit[0] = (EditText) popupView.findViewById(R.id.edit_sfactor);
                                    PUscaleButton = (Button) popupView.findViewById(R.id.scaleButton);
                                    PUscaleButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            int dividend = 1, divisor = 1;
                                            AlertDialog alertDialog = new AlertDialog.Builder(Gaussian.this).create();

                                            boolean goodInput = true;
                                            boolean fraction = false;
                                            String string1 = PopUpEdit[0].getText().toString();
                                            String pieces[] = string1.split("/");

                                            if (TextUtils.isEmpty(string1))
                                                goodInput = false;
                                            if (string1.contains("/")) {
                                                //possible division
                                                if (pieces.length != 2) {
                                                    goodInput = false;
                                                } else {
                                                    try {
                                                        fraction = true;
                                                        dividend = Integer.parseInt(pieces[0]);
                                                        divisor = Integer.parseInt(pieces[1]);
                                                        scale = Double.parseDouble(pieces[0]) / Double.parseDouble(pieces[1]);
                                                    } catch (Exception e) {
                                                        goodInput = false;
                                                    }
                                                }
                                            } else if (string1.contains(".")) {
                                                try {
                                                    scale = Double.parseDouble(string1);
                                                } catch (Exception e) {
                                                    goodInput = false;
                                                }
                                            } else {
                                                try {
                                                    scale = Double.parseDouble(string1);
                                                } catch (Exception e) {
                                                    goodInput = false;
                                                }
                                            }

                                            if (TextUtils.isEmpty(string1) || !goodInput) {
                                                alertDialog.setMessage("Forbidden input data");
                                                alertDialog.show();
                                                return;
                                            }
                                            if (scale == 0 || dividend == 0 || divisor == 0) {
                                                alertDialog.setMessage("Forbidden input data!");
                                                alertDialog.show();
                                                return;
                                            }

                                            int start1 = dim * (selected_row1);
                                            if (!fraction) {
                                                for (int i = 0; i < dim; i++) {
                                                    Matrix[start1 + i] *= scale;
                                                    if (algo == 1)
                                                        RHS[start1 + i] *= scale;
                                                }
                                                if (algo == 0) {
                                                    RHS[selected_row1 * max_dim] *= scale;
                                                }
                                            } else {
                                                for (int i = 0; i < dim; i++) {
                                                    Matrix[start1 + i] *= new Double(dividend) / divisor;
                                                    if (algo == 1)
                                                        RHS[start1 + i] *= new Double(dividend) / divisor;
                                                }
                                                if (algo == 0) {
                                                    RHS[selected_row1 * max_dim] *= new Double(dividend) / divisor;
                                                }
                                            }
                                            updateLGSView();
                                            popupWindow.dismiss();
                                            UnselectRow(selected_row1);
                                            selected_row2 = selected_row1 = -1;
                                        }
                                    });
                                }
                            } else {

                                int tmp;
                                int orig_sel1, orig_sel2;
                                orig_sel1 = selected_row1;
                                orig_sel2 = selected_row2;
                                if (selected_row2 < selected_row1 && !phase1_finished) {
                                    tmp = selected_row1;
                                    selected_row1 = selected_row2;
                                    selected_row2 = tmp;
                                }
                                if (selected_row2 > selected_row1 && phase1_finished) {
                                    tmp = selected_row1;
                                    selected_row1 = selected_row2;
                                    selected_row2 = tmp;
                                }
                                // inflate the layout of the popup window
                                LayoutInflater inflater = (LayoutInflater)
                                        getSystemService(LAYOUT_INFLATER_SERVICE);
                                View popupView = inflater.inflate(R.layout.popup_gaussian_different_rows, null);

                                popUpRow[0] = (LinearLayout) popupView.findViewById(R.id.row1);
                                popUpRow[1] = (LinearLayout) popupView.findViewById(R.id.row2);
                                popUpRow[2] = (LinearLayout) popupView.findViewById(R.id.row3);
                                popUpRow[3] = (LinearLayout) popupView.findViewById(R.id.row4);
                                popUpRow[4] = (LinearLayout) popupView.findViewById(R.id.row5);


                                PopUpedit_a[0] = (EditText) popupView.findViewById(R.id.edit_a11);
                                PopUpedit_a[1] = (EditText) popupView.findViewById(R.id.edit_a12);
                                PopUpedit_a[2] = (EditText) popupView.findViewById(R.id.edit_a13);
                                PopUpedit_a[3] = (EditText) popupView.findViewById(R.id.edit_a14);
                                PopUpedit_a[4] = (EditText) popupView.findViewById(R.id.edit_a15);
                                PopUpedit_a[5] = (EditText) popupView.findViewById(R.id.edit_a21);
                                PopUpedit_a[6] = (EditText) popupView.findViewById(R.id.edit_a22);
                                PopUpedit_a[7] = (EditText) popupView.findViewById(R.id.edit_a23);
                                PopUpedit_a[8] = (EditText) popupView.findViewById(R.id.edit_a24);
                                PopUpedit_a[9] = (EditText) popupView.findViewById(R.id.edit_a25);
                                PopUpedit_a[10] = (EditText) popupView.findViewById(R.id.edit_a31);
                                PopUpedit_a[11] = (EditText) popupView.findViewById(R.id.edit_a32);
                                PopUpedit_a[12] = (EditText) popupView.findViewById(R.id.edit_a33);
                                PopUpedit_a[13] = (EditText) popupView.findViewById(R.id.edit_a34);
                                PopUpedit_a[14] = (EditText) popupView.findViewById(R.id.edit_a35);
                                PopUpedit_a[15] = (EditText) popupView.findViewById(R.id.edit_a41);
                                PopUpedit_a[16] = (EditText) popupView.findViewById(R.id.edit_a42);
                                PopUpedit_a[17] = (EditText) popupView.findViewById(R.id.edit_a43);
                                PopUpedit_a[18] = (EditText) popupView.findViewById(R.id.edit_a44);
                                PopUpedit_a[19] = (EditText) popupView.findViewById(R.id.edit_a45);
                                PopUpedit_a[20] = (EditText) popupView.findViewById(R.id.edit_a51);
                                PopUpedit_a[21] = (EditText) popupView.findViewById(R.id.edit_a52);
                                PopUpedit_a[22] = (EditText) popupView.findViewById(R.id.edit_a53);
                                PopUpedit_a[23] = (EditText) popupView.findViewById(R.id.edit_a54);
                                PopUpedit_a[24] = (EditText) popupView.findViewById(R.id.edit_a55);

                                PopUpedit_b[0] = (EditText) popupView.findViewById(R.id.edit_b11);
                                PopUpedit_b[1] = (EditText) popupView.findViewById(R.id.edit_b12);
                                PopUpedit_b[2] = (EditText) popupView.findViewById(R.id.edit_b13);
                                PopUpedit_b[3] = (EditText) popupView.findViewById(R.id.edit_b14);
                                PopUpedit_b[4] = (EditText) popupView.findViewById(R.id.edit_b15);
                                PopUpedit_b[5] = (EditText) popupView.findViewById(R.id.edit_b21);
                                PopUpedit_b[6] = (EditText) popupView.findViewById(R.id.edit_b22);
                                PopUpedit_b[7] = (EditText) popupView.findViewById(R.id.edit_b23);
                                PopUpedit_b[8] = (EditText) popupView.findViewById(R.id.edit_b24);
                                PopUpedit_b[9] = (EditText) popupView.findViewById(R.id.edit_b25);
                                PopUpedit_b[10] = (EditText) popupView.findViewById(R.id.edit_b31);
                                PopUpedit_b[11] = (EditText) popupView.findViewById(R.id.edit_b32);
                                PopUpedit_b[12] = (EditText) popupView.findViewById(R.id.edit_b33);
                                PopUpedit_b[13] = (EditText) popupView.findViewById(R.id.edit_b34);
                                PopUpedit_b[14] = (EditText) popupView.findViewById(R.id.edit_b35);
                                PopUpedit_b[15] = (EditText) popupView.findViewById(R.id.edit_b41);
                                PopUpedit_b[16] = (EditText) popupView.findViewById(R.id.edit_b42);
                                PopUpedit_b[17] = (EditText) popupView.findViewById(R.id.edit_b43);
                                PopUpedit_b[18] = (EditText) popupView.findViewById(R.id.edit_b44);
                                PopUpedit_b[19] = (EditText) popupView.findViewById(R.id.edit_b45);
                                PopUpedit_b[20] = (EditText) popupView.findViewById(R.id.edit_b51);
                                PopUpedit_b[21] = (EditText) popupView.findViewById(R.id.edit_b52);
                                PopUpedit_b[22] = (EditText) popupView.findViewById(R.id.edit_b53);
                                PopUpedit_b[23] = (EditText) popupView.findViewById(R.id.edit_b54);
                                PopUpedit_b[24] = (EditText) popupView.findViewById(R.id.edit_b55);

                                int help = 0;
                                for (int i = 0; i < dim * dim; i++) {
                                    PopUpedit_a[i + help].setText(edit_a[i + help].getText());
                                    if (algo == 1)
                                        PopUpedit_b[i + help].setText(edit_b[i + help].getText());
                                    if ((i + 1) % dim == 0) help += (max_dim - dim);
                                }
                                if (algo == 0) {
                                    for (int i = 0; i < dim; i++) {
                                        PopUpedit_b[i * max_dim].setText(edit_b[i * max_dim].getText());
                                    }
                                }

                                popUpRow[orig_sel1].setVisibility(View.VISIBLE);
                                popUpRow[orig_sel1].setBackgroundColor(getResources().getColor((R.color.green)));
                                popUpRow[orig_sel2].setVisibility(View.VISIBLE);
                                popUpRow[orig_sel2].setBackgroundColor(getResources().getColor((R.color.blue)));

                                editPopUp[0] = (TextView) popupView.findViewById(R.id.elRowID0);
                                editPopUp[1] = (TextView) popupView.findViewById(R.id.elRowID1);
                                editPopUp[2] = (TextView) popupView.findViewById(R.id.elRowID2);
                                //                            editPopUp[3] = (TextView) popupView.findViewById(R.id.elRowID0b);
                                //                            editPopUp[4] = (TextView) popupView.findViewById(R.id.elRowID1b);
                                //                            editPopUp[5] = (TextView) popupView.findViewById(R.id.elRowID2b);
                                editPopUp[6] = (TextView) popupView.findViewById(R.id.swapRowID1);
                                editPopUp[7] = (TextView) popupView.findViewById(R.id.swapRowID2);

                                editPopUp[0].setText(num_lines.get(selected_row2 + 1));
                                editPopUp[1].setText(num_lines.get(selected_row1 + 1));
                                editPopUp[2].setText(num_lines.get(selected_row2 + 1));
                                if (orig_sel1 > orig_sel2) {
                                    editPopUp[0].setBackgroundColor(getResources().getColor((R.color.green)));
                                    editPopUp[1].setBackgroundColor(getResources().getColor((R.color.blue)));
                                    editPopUp[2].setBackgroundColor(getResources().getColor((R.color.green)));
                                } else {
                                    editPopUp[0].setBackgroundColor(getResources().getColor((R.color.blue)));
                                    editPopUp[1].setBackgroundColor(getResources().getColor((R.color.green)));
                                    editPopUp[2].setBackgroundColor(getResources().getColor((R.color.blue)));
                                }

                                //                            editPopUp[3].setText(num_lines.get(selected_row1+1));
                                //                            editPopUp[4].setText(num_lines.get(selected_row2+1));
                                //                            editPopUp[5].setText(num_lines.get(selected_row1+1));
                                editPopUp[6].setText(num_lines.get(selected_row1 + 1));
                                editPopUp[7].setText(num_lines.get(selected_row2 + 1));

                                // create the popup window
                                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                boolean focusable = true; // lets taps outside the popup also dismiss it
                                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                                // show the popup window
                                // which view you pass in doesn't matter, it is only used for the window tolken
                                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


                                PopUpEdit[0] = (EditText) popupView.findViewById(R.id.e_factor1);
                                //                            PopUpEdit[1] = (EditText) popupView.findViewById(R.id.e_factor1b);
                                PUelimButton1 = (Button) popupView.findViewById(R.id.eliminateButton);
                                PUelimButton1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        double fac1 = 0;
                                        int dividend = 1, divisor = 1;

                                        AlertDialog alertDialog = new AlertDialog.Builder(Gaussian.this).create();

                                        boolean goodInput = true;
                                        boolean fraction = false;
                                        String string1 = PopUpEdit[0].getText().toString();
                                        String pieces[] = string1.split("/");


                                        if (TextUtils.isEmpty(string1))
                                            goodInput = false;
                                        if (string1.contains("/")) {
                                            //possible division
                                            if (pieces.length != 2) {
                                                goodInput = false;
                                            } else {
                                                try {
                                                    fraction = true;
                                                    dividend = Integer.parseInt(pieces[0]);
                                                    divisor = Integer.parseInt(pieces[1]);
                                                    fac1 = Double.parseDouble(pieces[0]) / Double.parseDouble(pieces[1]);
                                                } catch (Exception e) {
                                                    goodInput = false;
                                                }
                                            }
                                        } else if (string1.contains(".")) {
                                            try {
                                                fac1 = Double.parseDouble(string1);
                                            } catch (Exception e) {
                                                goodInput = false;
                                            }
                                        } else {
                                            try {
                                                fac1 = Double.parseDouble(string1);
                                            } catch (Exception e) {
                                                goodInput = false;
                                            }
                                        }

                                        if (TextUtils.isEmpty(string1) || !goodInput) {
                                            alertDialog.setMessage("Forbidden input data");
                                            alertDialog.show();
                                            return;
                                        }
                                        if (fac1 == 0 || dividend == 0 || divisor == 0) {
                                            alertDialog.setMessage("Forbidden input data!");
                                            alertDialog.show();
                                            return;
                                        }

                                        int start1 = dim * (selected_row1);
                                        int start2 = dim * (selected_row2);

                                        if (fraction) {
                                            for (int i = 0; i < dim; i++) {
                                                Matrix[i + start2] = (Matrix[i + start1] * dividend) / divisor - Matrix[i + start2];
                                                if (algo == 1)
                                                    RHS[i + start2] = (RHS[i + start1] * dividend) / divisor - RHS[i + start2];
                                                else if (algo == 2)
                                                    if (i >= selected_row2)
                                                        RHS[i + start2] = Matrix[i + start2];
                                            }
                                            if (algo == 0) {
                                                RHS[selected_row2] = (RHS[selected_row1] * dividend) / divisor - RHS[selected_row2];
                                            } else if (algo == 2) {
                                                for (int i = 0; i < dim; i++) {
                                                    RHS[i] = Matrix[i];
                                                }
                                                int index = selected_row2 * dim + selected_row1;
                                                if (RHS[index] == 0)
                                                    RHS[index] = new Double(dividend) / divisor;
                                                else
                                                    RHS[index] *= new Double(dividend) / divisor;
                                            }
                                        } else {
                                            for (int i = 0; i < dim; i++) {
                                                Matrix[i + start2] = Matrix[i + start1] * fac1 - Matrix[i + start2];
                                                if (algo == 1)
                                                    RHS[i + start2] = RHS[i + start1] * fac1 - RHS[i + start2];
                                                else if (algo == 2)
                                                    if (i >= selected_row2)
                                                        RHS[i + start2] = Matrix[i + start2];
                                            }
                                            if (algo == 0) {
                                                RHS[selected_row2] = RHS[selected_row1] * fac1 - RHS[selected_row2];
                                            } else if (algo == 2) {
                                                for (int i = 0; i < dim; i++) {
                                                    RHS[i] = Matrix[i];
                                                }
                                                int index = selected_row2 * dim + selected_row1;
                                                if (RHS[index] == 0)
                                                    RHS[index] = fac1;
                                                else
                                                    RHS[index] *= fac1;
                                            }
                                        }

                                        updateLGSView();
                                        popupWindow.dismiss();
                                        UnselectRow(selected_row1);
                                        UnselectRow(selected_row2);
                                        popUpRow[selected_row1].setVisibility(View.GONE);
                                        popUpRow[selected_row1].setVisibility(View.GONE);
                                        selected_row2 = selected_row1 = -1;
                                    }
                                });/*
                                PUelimButton2 = (Button) popupView.findViewById(R.id.eliminateButtonb);
                                PUelimButton2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        double fac1 = 0;
                                        int dividend=1, divisor=1;

                                        AlertDialog alertDialog = new AlertDialog.Builder(Gaussian.this).create();

                                        boolean goodInput = true;
                                        boolean fraction = false;
                                        String string1 = PopUpEdit[1].getText().toString();
                                        String pieces[] = string1.split("/");

                                        if(TextUtils.isEmpty(string1))
                                            goodInput =false;
                                        if (string1.contains("/")) {
                                            //possible division
                                            if (pieces.length != 2) {
                                                goodInput = false;
                                            } else {
                                                try {
                                                    fraction =true;
                                                    dividend = Integer.parseInt(pieces[0]);
                                                    divisor = Integer.parseInt(pieces[1]);
                                                    fac1 = Double.parseDouble(pieces[0])/Double.parseDouble(pieces[1]);
                                                } catch (Exception e) {
                                                    goodInput = false;
                                                }
                                            }
                                        }  else if (string1.contains(".")) {
                                            try {
                                                fac1 = Double.parseDouble(string1);
                                            } catch (Exception e) {
                                                goodInput = false;
                                            }
                                        }
                                        else  {
                                            try {
                                                fac1 = Double.parseDouble(string1);
                                            } catch (Exception e) {
                                                goodInput = false;
                                            }
                                        }

                                        if(TextUtils.isEmpty(string1) || !goodInput) {
                                            alertDialog.setMessage("Forbidden input data");
                                            alertDialog.show();
                                            return;
                                        }
                                        if( fac1==0 || dividend == 0 || divisor ==0) {
                                            alertDialog.setMessage("Forbidden input data!");
                                            alertDialog.show();
                                            return;
                                        }

                                        int start1 = dim*(selected_row2);
                                        int start2 = dim*(selected_row1);

                                        if(fraction) {
                                            for (int i = 0; i < dim; i++) {
                                                Matrix[i + start2] = (Matrix[i + start1] * dividend) / divisor - Matrix[i + start2];
                                            }
                                            if(algo == 0) {
                                                RHS[selected_row1] = (RHS[selected_row2] * dividend) / divisor - RHS[selected_row1];
                                            }
                                        }
                                        else {
                                            for (int i = 0; i < dim; i++) {
                                                Matrix[i + start2] = Matrix[i + start1] * fac1 - Matrix[i + start2];
                                            }

                                            if(algo == 0) {
                                                RHS[selected_row1] = RHS[selected_row2] * fac1 - RHS[selected_row1];
                                            }
                                        }
                                        updateLGSView();
                                        popupWindow.dismiss();
                                        UnselectRow(selected_row1);
                                        UnselectRow(selected_row2);
                                        popUpRow[selected_row1].setVisibility(View.GONE);
                                        popUpRow[selected_row1].setVisibility(View.GONE);
                                        selected_row2 = selected_row1 = -1;
                                    }
                                });
    */
                                PUswapButton = (Button) popupView.findViewById(R.id.swapButton);
                                PUswapButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int start1 = dim * (selected_row1);
                                        int start2 = dim * (selected_row2);
                                        double[] tmp = new double[dim];
                                        for (int i = 0; i < dim; i++) {
                                            tmp[i] = Matrix[start1 + i];
                                        }
                                        for (int i = 0; i < dim; i++) {
                                            Matrix[i + start1] = Matrix[i + start2];
                                            Matrix[i + start2] = tmp[i];
                                            if (algo == 1) {
                                                RHS[i + start1] = RHS[i + start2];
                                                RHS[i + start2] = tmp[i];
                                            }
                                        }
                                        if (algo == 0) {
                                            double dtmp = RHS[selected_row1];
                                            RHS[selected_row1] = RHS[selected_row2];
                                            RHS[selected_row2] = dtmp;
                                        }

                                        updateLGSView();

                                        UnselectRow(selected_row1);
                                        UnselectRow(selected_row2);
                                        popUpRow[selected_row1].setVisibility(View.GONE);
                                        popUpRow[selected_row1].setVisibility(View.GONE);
                                        selected_row2 = selected_row1 = -1;
                                        popupWindow.dismiss();
                                    }
                                });

                                closePopupBtn = (Button) popupView.findViewById(R.id.closePopUp);
                                //close the popup window on button click
                                closePopupBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        UnselectRow(selected_row1);
                                        UnselectRow(selected_row2);
                                        popUpRow[selected_row1].setVisibility(View.GONE);
                                        popUpRow[selected_row1].setVisibility(View.GONE);
                                        selected_row2 = selected_row1 = -1;
                                        popupWindow.dismiss();
                                    }
                                });

                            }
                        }
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(Gaussian.this).create();
                        alertDialog.setMessage("Phase 1 ist geschafft! Nun geht es mit Phase 2 weiter! Beachte die Anleitung.");
                        alertDialog.show();
                    }
                }
            });
        }

    }


    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    public void ShowInputLines(View v){

        flag = true;
        fillLGSButton.setEnabled(true);
        InputButton.setEnabled(false);

        manual.setText("Füllen Sie das LGS mit belibiegen Zahlen (geht nocht nicht) und bestätigen Sie mit 'Fülle LGS'. \nOder drücken Sie direkt 'Fülle LGS' um eine Beispiel Matrix zu generieren.");

        switch (this.dim){
            case 3:
                showLGS.setVisibility(View.VISIBLE);
                row[3].setVisibility(View.GONE);
                row[4].setVisibility(View.GONE);
                edit_a[3].setVisibility(View.GONE);
                edit_a[8].setVisibility(View.GONE);
                edit_a[13].setVisibility(View.GONE);
                edit_a[4].setVisibility(View.GONE);
                edit_a[9].setVisibility(View.GONE);
                edit_a[14].setVisibility(View.GONE);
                edit_a[19].setVisibility(View.GONE);
                if( algo != 0) {
                    edit_b[1].setVisibility(View.VISIBLE);
                    edit_b[2].setVisibility(View.VISIBLE);
                    edit_b[6].setVisibility(View.VISIBLE);
                    edit_b[7].setVisibility(View.VISIBLE);
                    edit_b[11].setVisibility(View.VISIBLE);
                    edit_b[12].setVisibility(View.VISIBLE);
                }

                break;
            case 4:
                showLGS.setVisibility(View.VISIBLE);
                row[3].setVisibility(View.VISIBLE);
                row[4].setVisibility(View.GONE);
                edit_a[3].setVisibility(View.VISIBLE);
                edit_a[8].setVisibility(View.VISIBLE);
                edit_a[13].setVisibility(View.VISIBLE);
                edit_a[4].setVisibility(View.GONE);
                edit_a[9].setVisibility(View.GONE);
                edit_a[14].setVisibility(View.GONE);
                edit_a[19].setVisibility(View.GONE);
                if( algo != 0) {

                }
                break;
            case 5:
                showLGS.setVisibility(View.VISIBLE);
                row[3].setVisibility(View.VISIBLE);
                row[4].setVisibility(View.VISIBLE);
                edit_a[3].setVisibility(View.VISIBLE);
                edit_a[8].setVisibility(View.VISIBLE);
                edit_a[13].setVisibility(View.VISIBLE);
                edit_a[4].setVisibility(View.VISIBLE);
                edit_a[9].setVisibility(View.VISIBLE);
                edit_a[14].setVisibility(View.VISIBLE);
                edit_a[19].setVisibility(View.VISIBLE);
                if( algo != 0) {

                }
                break;
        }
    }


    public void FillLGS(View v){

        fillLGSButton.setEnabled(false);
        manual.setText("Phase 1: Bringen Sie die Matrix obere Dreicksgestallt (Vorwärtselimination).\n" +
                "Berühren Sie zwei Zeilen um diese von einander abzuziehen oder um sie zu tauschen.\n");
        if(algo!=2) manual.append("Berühren Sie zweimal die gleiche Zeile um diese zu skalieren.\n");
        manual.append("Faktoren können sowohl als Dezimalzahlen (z.B. -1.5), oder als Bruch (z.B. -3/2) eingegeben werden.");
        TextView wwts = (TextView) findViewById(R.id.WhatToSolve);
        Matrix = new double[max_dim*max_dim];
        RHS = new double[max_dim*max_dim];

        if(algo ==2) {
            for(int i = 0; i < max_dim*max_dim; i++) {
                RHS[i]=0;
            }
        }
        if(dim==3) {
            Matrix[0] = 7;
            Matrix[1] = 3;
            Matrix[2] = -5;
            Matrix[3] = -1;
            Matrix[4] = -2;
            Matrix[5] = 4;
            Matrix[6] = -4;
            Matrix[7] = 1;
            Matrix[8] = -3;
            if(algo == 0) {
                RHS[0] = -12;
                RHS[1] = 5;
                RHS[2] = 1;
            }
            else if(algo==1) {
                RHS[0] = 1;
                RHS[1] = 0;
                RHS[2] = 0;
                RHS[3] = 0;
                RHS[4] = 1;
                RHS[5] = 0;
                RHS[6] = 0;
                RHS[7] = 0;
                RHS[8] = 1;
            }
        }

        String matrix_entries;
        if(dim==3) {
            int help = 0;
            for(int i =0; i < dim*dim; i++) {
                edit_a[i+help].setFocusable(false);
                matrix_entries = new Double(Matrix[i]).toString();
                edit_a[i+help].setText(matrix_entries);
                if((i+1)%dim==0) help+=(max_dim-dim);
            }
            if(algo == 0) {
                for (int i = 0; i < dim; i++) {
                    edit_b[i * max_dim].setFocusable(false);
                    matrix_entries = new Double(RHS[i]).toString();
                    edit_b[i * max_dim].setText(matrix_entries);
                }
            }
            else if(algo == 1) {
                help = 0;

                for (int i = 0; i < dim*dim; i++) {
                    edit_b[i+help].setFocusable(false);
                    matrix_entries = new Double(RHS[i]).toString();
                    edit_b[i+help].setText(matrix_entries);
                    if((i+1)%dim==0) help+=(max_dim-dim);
                }
            }
        }
        else
        {
            for(int i =0; i < max_dim*max_dim; i++) {
                edit_a[i].setFocusable(false);
                matrix_entries = new Double(i).toString();
                edit_a[i].setText(matrix_entries);
            }
            if( algo == 0) {
                for (int i = 0; i < max_dim; i++) {
                    edit_b[i * max_dim].setFocusable(false);
                    matrix_entries = new Double(i).toString();
                    edit_b[i * max_dim].setText(matrix_entries);
                }
            }
            else if( algo == 1) {
                for (int i = 0; i < max_dim; i++) {
                    edit_b[i].setFocusable(false);
                    matrix_entries = new Double(i).toString();
                    edit_b[i].setText(matrix_entries);
                }
            }
         }
        if(algo == 2) {
            for (int i = 0; i < max_dim * max_dim; i++) {
                edit_b[i].setBackgroundColor(getResources().getColor((R.color.dot_light_screen3)));
            }
            edit_b[5].setBackgroundColor(getResources().getColor((R.color.dot_light_screen4)));
            edit_b[10].setBackgroundColor(getResources().getColor((R.color.dot_light_screen4)));
            edit_b[11].setBackgroundColor(getResources().getColor((R.color.dot_light_screen4)));
            edit_b[15].setBackgroundColor(getResources().getColor((R.color.dot_light_screen4)));
            edit_b[16].setBackgroundColor(getResources().getColor((R.color.dot_light_screen4)));
            edit_b[17].setBackgroundColor(getResources().getColor((R.color.dot_light_screen4)));
            edit_b[20].setBackgroundColor(getResources().getColor((R.color.dot_light_screen4)));
            edit_b[21].setBackgroundColor(getResources().getColor((R.color.dot_light_screen4)));
            edit_b[22].setBackgroundColor(getResources().getColor((R.color.dot_light_screen4)));
            edit_b[23].setBackgroundColor(getResources().getColor((R.color.dot_light_screen4)));
        }

        if(algo==0) {
            String text;
            text = " Das zu lösende LGS lautet <br>";
            for(int i=0; i<dim; i++){
                for(int j=0; j<dim; j++) {
                    if(Matrix[i*dim+j]>0 && j>0)
                        text+=" + ";
                    text += new Double(Matrix[i*dim+j]).toString() +"x<sub>"+new Integer(j)+"</sub>";
                }
                text += " ="+ new Double(RHS[i]).toString() + "<br>";
            }
            wwts.setText(Html.fromHtml(text));
        }
        else if(algo==1) {
            String text;
            text = " Wir suchen die Inverse folgender Matrix: <br>";
            for(int i=0; i<dim; i++){
                for(int j=0; j<dim; j++) {
                    if(Matrix[i*dim+j]>0)
                        text+=" ";
                    text += new Double(Matrix[i*dim+j]).toString()+" " ;
                }
                text += "<br>";
            }
            wwts.setText(Html.fromHtml(text));

        }
        else {
            String text;
            text = " Die LR-Zerlegung folgender Matrix ist gesucht: <br>";
            for(int i=0; i<dim; i++){
                for(int j=0; j<dim; j++) {
                    if(Matrix[i*dim+j]>0)
                        text+=" ";
                    text += new Double(Matrix[i*dim+j]).toString()+" " ;
                }
                text += "<br>";
            }
            wwts.setText(Html.fromHtml(text));

        }
    }

    public void ChooseFirstRow(int i) {
        if(i<max_dim)
            row[0].setBackgroundColor(getResources().getColor((R.color.green)));
        else if(i<2*max_dim)
            row[1].setBackgroundColor(getResources().getColor((R.color.green)));
        else if(i<3*max_dim)
            row[2].setBackgroundColor(getResources().getColor((R.color.green)));
        else if(i<4*max_dim)
            row[3].setBackgroundColor(getResources().getColor((R.color.green)));
        else if(i<2*max_dim)
            row[4].setBackgroundColor(getResources().getColor((R.color.green)));

    }
    public void ChooseSecondRow(int i) {
        if(i<max_dim)
            row[0].setBackgroundColor(getResources().getColor((R.color.blue)));
        else if(i<2*max_dim)
            row[1].setBackgroundColor(getResources().getColor((R.color.blue)));
        else if(i<3*max_dim)
            row[2].setBackgroundColor(getResources().getColor((R.color.blue)));
        else if(i<4*max_dim)
            row[3].setBackgroundColor(getResources().getColor((R.color.blue)));
        else if(i<2*max_dim)
            row[4].setBackgroundColor(getResources().getColor((R.color.blue)));

    }

    public void UnselectRow(int i) {
            row[i].setBackgroundColor(getResources().getColor((R.color.white)));
    }


    public void elimination(View v) {
        double fac1 = 0;
        int dividend=1, divisor=1;

        AlertDialog alertDialog = new AlertDialog.Builder(Gaussian.this).create();
        if(eliminate1==0 || eliminate2==0 || eliminate1==eliminate2) {
            alertDialog.setMessage("Forbidden input data");
            alertDialog.show();
            return;
        }

        boolean goodInput = true;
        boolean fraction = false;
        String string1 = eFactor1.getText().toString();
        String pieces[] = string1.split("/");

        if(TextUtils.isEmpty(string1))
            goodInput =false;
        if (string1.contains("/")) {
            //possible division
            if (pieces.length != 2) {
                goodInput = false;
            } else {
                try {
                    fraction =true;
                    dividend = Integer.parseInt(pieces[0]);
                    divisor = Integer.parseInt(pieces[1]);
                    fac1 = Double.parseDouble(pieces[0])/Double.parseDouble(pieces[1]);
                } catch (Exception e) {
                    goodInput = false;
                }
            }
        }  else if (string1.contains(".")) {
            try {
                fac1 = Double.parseDouble(string1);
            } catch (Exception e) {
                goodInput = false;
            }
        }
            else  {
            try {
                fac1 = Double.parseDouble(string1);
            } catch (Exception e) {
                goodInput = false;
            }
        }

        if(TextUtils.isEmpty(string1) || !goodInput) {
            alertDialog.setMessage("Forbidden input data");
            alertDialog.show();
            return;
        }
        if( fac1==0 || dividend == 0 || divisor ==0) {
            alertDialog.setMessage("Forbidden input data!");
            alertDialog.show();
            return;
        }

        int start1 = dim*(eliminate1-1);
        int start2 = dim*(eliminate2-1);

        if(fraction) {
            for (int i = 0; i < dim; i++) {
                Matrix[i + start2] = (Matrix[i + start1] * dividend) / divisor - Matrix[i + start2];

                if( algo == 1) {
                    RHS[i + start2] = (RHS[i + start1] * dividend) / divisor - RHS[i + start2];
                }
            }
            if( algo == 0) {
                RHS[eliminate2 - 1] = (RHS[eliminate1 - 1] * dividend) / divisor - RHS[eliminate2 - 1];
            }
        }
        else {
            for (int i = 0; i < dim; i++) {
                Matrix[i + start2] = Matrix[i + start1] * fac1 - Matrix[i + start2];
                if(algo==1)
                    RHS[i + start2] = RHS[i + start1] * fac1 - RHS[i + start2];
            }
            if( algo == 0) {
                RHS[eliminate2 - 1] = RHS[eliminate1 - 1] * fac1 - RHS[eliminate2 - 1];
            }
        }
        updateLGSView();
    }

    public void swaprows(View v) {
        if(swap1==0 || swap2==0) {
            AlertDialog alertDialog = new AlertDialog.Builder(Gaussian.this).create();
            alertDialog.setMessage("Forbidden input data");
            alertDialog.show();
            return;
        }

        int start1 = dim*(swap1-1);
        int start2 = dim*(swap2-1);
        double[] tmp = new double[dim];
        for(int i=0; i < dim; i++) {
            tmp[i] = Matrix[start1+i];
        }
        for(int i=0; i < dim; i++) {
            Matrix[i+start1] = Matrix[i+start2];
            Matrix[i+start2] = tmp[i];
        }
        if (algo==1) {
            for(int i=0; i < dim; i++) {
                tmp[i] = RHS[start1+i];
            }
            for(int i=0; i < dim; i++) {
                RHS[i+start1] = RHS[i+start2];
                RHS[i+start2] = tmp[i];
            }
        }
        if( algo == 0 ) {
            double dtmp = RHS[swap1 - 1];
            RHS[swap1 - 1] = RHS[swap2 - 1];
            RHS[swap2 - 1] = dtmp;
        }
        updateLGSView();
    }

    public void scalerow(View v) {

        AlertDialog alertDialog = new AlertDialog.Builder(Gaussian.this).create();
        String string1 = ed_scale.getText().toString();
        if(TextUtils.isEmpty(string1)) {
            alertDialog.setMessage("No input data");
            alertDialog.show();
            return;
        }
        try {
            scale = Double.parseDouble(string1);
        } catch (Exception e){
            scale = 0;
        }
        if(scale==0 || srow==0 ) {
            alertDialog.setMessage("Forbidden input data");
            alertDialog.show();
            return;
        }

        int start1 = dim*(srow-1);
        for(int i=0; i < dim; i++) {
            Matrix[start1+i]*=scale;
            if(algo==1)
                RHS[start1+i]*=scale;
        }
        if( algo == 0 ) {
            RHS[srow - 1] *= scale;
        }
        updateLGSView();
    }

    public void resetAll(View v) {

        Intent intent = new Intent(this, Gaussian.class);
        this.startActivity(intent);
        this.finishAffinity();
    }
    public void secondStep(View v) {
        TextView showSol = (TextView)  findViewById(R.id.ShowSolution);
        double[] x = new double[dim];
        if(algo==0) {
            x[dim-1] = RHS[dim-1]/Matrix[dim*dim-1];
            for (int i = dim - 2; i >= 0; i--) {
                double sum = 0;
                for (int j = i+1; j < dim; j++) {
                    sum += Matrix[i*dim+j] *x[j];
                }
                sum = RHS[i] - sum;
                x[i] = sum / Matrix[i*dim+i];
            }
            showSol.setText("Die Lösung lautet:\n");
            String text="";
            for(int i=0; i < dim; i++) {
                x[i] = Math.round(x[i]*1000.0)/1000.0;
                text +="x<sub>"+new Integer(i).toString();
                text+="</sub>";
                text+=" = ";
                text+=new Double(x[i]).toString();
                text+="<br>";
            }
            showSol.append(Html.fromHtml(text));
        }

    }
    public void updateLGSView() {

        Button secondButton = (Button) findViewById(R.id.secStepButton);
        String matrix_entries;
        int help = 0;
        int row=0;
        int col=0;
        boolean p1 = true;
        for(int i =0; i < dim*dim; i++) {
            row = i/dim;
            col = i%dim;
            if(col<row) {
                p1 = p1 && (Math.abs(Matrix[i]) < 1E-7);
            }
            matrix_entries = new Double(Matrix[i]).toString();
            edit_a[i+help].setText(matrix_entries);
            if((i+1)%dim==0) help+=(max_dim-dim);
        }
        help = 0;
        for(int i =0; i < dim*dim; i++) {
            matrix_entries = new Double(RHS[i]).toString();
            edit_b[i+help].setText(matrix_entries);
            if((i+1)%dim==0 && algo!=0) help+=(max_dim-dim);
        }
        phase1_finished = p1;
        if(p1) {
            if (algo == 0) {
                secondButton.setVisibility(View.VISIBLE);
                secondButton.setText("Rückwärtseinsetzen");
                manual.setText("Phase 1: Erledigt!\n");
                manual.setText("Phase 2: Das Rückwärtseinsetzen wird für Sie automatisch ausgeführt. Drücken Sie dafür rechts den Button 'Rückwärtseinsetzen.' \n");
            }
            else if (algo == 2) {
                manual.setText("Phase 1: Erledigt!\n");
                manual.append("Phase 2: Bringen Sie die Matrix nun auf Diagonalgestallt bzw. überführen sie die Matrix zur Einheitsmatrix.\n" +
                        "Berühren Sie zwei Zeilen um diese von einander abzuziehen oder um sie zu tauschen.\n");
                manual.append("Berühren Sie zweimal die gleiche Zeile um diese zu skalieren.\n");
                manual.append("Faktoren können sowohl als Dezimalzahlen (z.B. -1.5), oder als Bruch (z.B. -3/2) eingegeben werden.");
            }
            else {
                secondButton.setVisibility(View.VISIBLE);
                secondButton.setText("Lösen durch Anwenden");
                manual.setText("Phase 1: Erledigt!\nSoll hier auch ne rechte Seite geben oder reicht LR-Zerlegung ohne Anwendung auf RHS?");
            }
        }

    }

/*    public void TransformToCanvas(View v){
        valuesRightSideString =  rightSideEdit.getText().toString();
        valuesRightSideDoubleVec = StringVecToDoubleVec(StringToStringVev(valuesRightSideString));
    }*/

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

}
