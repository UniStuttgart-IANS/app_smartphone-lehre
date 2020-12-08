package com.uni_stuttgart.isl.EliminateLGS;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.nishant.math.MathView;


/**
 * Created by ms on 13.03.20.
 */


public class MyCanvas extends View {

    private boolean finished = false;

    private MathView textL;
    private MathView textR;
    private MathView textP;

    private Paint[] paints= new Paint[25];
    private Paint[] staticPaints= new Paint[25];
    private Paint[] paintsRHS= new Paint[25];
    private Paint[] staticPaintsRHS= new Paint[25];
    private Paint[] fracpaints= new Paint[25];
    private Paint[] fracstaticPaints= new Paint[25];
    private Paint[] fracpaintsRHS= new Paint[25];
    private Paint[] fracstaticPaintsRHS= new Paint[25];

    private int matDim = 5;
    private int num_rhs = 5;
    private float orientationPointsX[] = new float[2*matDim+1];
    private float orientationPointsY[] = new float[matDim+1];
    private float matPosX[] = new float[matDim*matDim];
    private float matPosY[] = new float[matDim*matDim];
    private float staticMatPosX[] = new float[matDim*matDim];
    private float staticMatPosY[] = new float[matDim*matDim];
    private float rhsPosX[] = new float[matDim*matDim];
    private float rhsPosY[] = new float[matDim*matDim];
    private float staticRhsPosX[] = new float[matDim*matDim];
    private float staticRhsPosY[] = new float[matDim*matDim];

    private int array_numerator[] = {1, 2, 4, 3, 5, 3, 5, 3, 1, 2, 1, 4, 4, 2, 1, 4, 1, 2, 5, 3, 5, 2, 1, 4, 1};
    private int array_denominator[] = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    private int rhs_numerator[] = {1, 0, 0, 0, 0,   0, 1, 0, 0, 0,   0, 0, 1, 0, 0,  0, 0, 0, 1, 0,   0, 0, 0, 0, 1};
    private int rhs_denominator[] = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

    private int piv_mat[] = {1, 0, 0, 0, 0,   0, 1, 0, 0, 0,   0, 0, 1, 0, 0,  0, 0, 0, 1, 0,   0, 0, 0, 0, 1};

    private int reminder_num_mat [][] = new int[100][matDim*matDim];
    private int reminder_den_mat [][] = new int[100][matDim*matDim];
    private int reminder_num_rhs [][] = new int[100][matDim*matDim];
    private int reminder_den_rhs [][] = new int[100][matDim*matDim];
    private int reminder_pivot [][] = new int[100][matDim*matDim];
    private int reminder_lu_pahse[] =new int[100];

    private int dimension;
    private int row = 0;
    private int selected_row = 0;

    private int modus = 2;
    private int screensizex;
    private int screensizey;
    private int size_bins;

    private int nstep=0;

    private int lu_phase=0;

    private int last_row=-1;
    private boolean no_action=false;

    boolean is_init=false;
    private boolean touched_rows[]={false,false,false,false,false};

    private Switch change_or_elim;

    private AlertDialog alert_dialog;
    private String addValueString;

    public MyCanvas(Context c, AttributeSet attrs) {
        super(c, attrs);


        for (int i = 0; i < 25; i++) {
            paints[i] = new Paint();
            paints[i].setColor(Color.BLACK);
            paints[i].setTextSize(50);
            staticPaints[i] = new Paint();
            staticPaints[i].setColor(Color.GRAY);
            staticPaints[i].setTextSize(50);
            paintsRHS[i] = new Paint();
            paintsRHS[i].setColor(Color.BLACK);
            paintsRHS[i].setTextSize(50);
            staticPaintsRHS[i] = new Paint();
            staticPaintsRHS[i].setColor(Color.GRAY);
            staticPaintsRHS[i].setTextSize(50);
            staticPaintsRHS[i].setStrokeWidth(3);


            fracpaints[i] = new Paint();
            fracpaints[i].setColor(Color.BLACK);
            fracstaticPaints[i] = new Paint();
            fracstaticPaints[i].setColor(Color.GRAY);
            fracpaintsRHS[i] = new Paint();
            fracpaintsRHS[i].setColor(Color.BLACK);
            fracstaticPaintsRHS[i] = new Paint();
            fracstaticPaintsRHS[i].setColor(Color.GRAY);
        }


    }
    // Zeichen Routine
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for(int j = 0; j < matDim; j++) {
            for(int i = 0; i < matDim; i++) {
                if(array_denominator[i + matDim * j]==1) {
                    canvas.drawText(String.format("%d", array_numerator[i + matDim * j]), staticMatPosX[i + matDim * j], staticMatPosY[i + matDim * j], staticPaints[i + matDim * j]);
                    canvas.drawText(String.format("%d", array_numerator[i + matDim * j]), matPosX[i + matDim * j], matPosY[i + matDim * j], paints[i + matDim * j]);
                }
                else {
                    canvas.drawText(String.format("%d", array_numerator[i + matDim * j]), staticMatPosX[i + matDim * j], staticMatPosY[i + matDim * j]-size_bins/5, fracstaticPaints[i + matDim * j]);
                    canvas.drawLine(staticMatPosX[i + matDim * j], (orientationPointsY[ j]+size_bins/2),staticMatPosX[i + matDim * j]+2*size_bins/3, orientationPointsY[ j]+size_bins/2, paints[i + matDim * j]);
                    canvas.drawText(String.format("%d", array_denominator[i + matDim * j]), staticMatPosX[i + matDim * j], staticMatPosY[i + matDim * j]+size_bins/10, fracstaticPaints[i + matDim * j]);
                    canvas.drawText(String.format("%d", array_numerator[i + matDim * j]), matPosX[i + matDim * j], matPosY[i + matDim * j]-size_bins/5, fracpaints[i + matDim * j]);
                    canvas.drawLine(matPosX[i + matDim * j], (orientationPointsY[ j]+size_bins/2),matPosX[i + matDim * j]+2*size_bins/3, orientationPointsY[ j]+size_bins/2, paints[i + matDim * j]);
                    canvas.drawText(String.format("%d", array_denominator[i + matDim * j]), matPosX[i + matDim * j], matPosY[i + matDim * j]+size_bins/10, fracpaints[i + matDim * j]);
                }
            }

            for(int i = 0; i < num_rhs; i++) {
                if(rhs_denominator[i + matDim * j]==1) {
                    canvas.drawText(String.format("%d", rhs_numerator[i + matDim * j]), staticRhsPosX[i + matDim * j], staticRhsPosY[i + matDim * j], staticPaintsRHS[i + matDim * j]);
                    canvas.drawText(String.format("%d", rhs_numerator[i + matDim * j]), rhsPosX[i + matDim * j], rhsPosY[i + matDim * j], paintsRHS[i + matDim * j]);
                }
                else {
                    canvas.drawText(String.format("%d", rhs_numerator[i + matDim * j]), staticRhsPosX[i + matDim * j], staticRhsPosY[i + matDim * j]-size_bins/5, fracstaticPaintsRHS[i + matDim * j]);
                    canvas.drawLine(staticRhsPosX[i + matDim * j], (orientationPointsY[ j]+size_bins/2),staticRhsPosX[i + matDim * j]+2*size_bins/3, orientationPointsY[ j]+size_bins/2, staticPaintsRHS[i + matDim * j]);
                    canvas.drawText(String.format("%d", rhs_denominator[i + matDim * j]), staticRhsPosX[i + matDim * j], staticRhsPosY[i + matDim * j]+size_bins/10, fracstaticPaintsRHS[i + matDim * j]);
                    canvas.drawText(String.format("%d", rhs_numerator[i + matDim * j]), rhsPosX[i + matDim * j], rhsPosY[i + matDim * j]-size_bins/5, fracpaintsRHS[i + matDim * j]);
                    canvas.drawLine(rhsPosX[i + matDim * j], (orientationPointsY[ j]+size_bins/2),rhsPosX[i + matDim * j]+2*size_bins/3, orientationPointsY[ j]+size_bins/2, paintsRHS[i + matDim * j]);
                    canvas.drawText(String.format("%d", rhs_denominator[i + matDim * j]), rhsPosX[i + matDim * j], rhsPosY[i + matDim * j]+size_bins/10, fracpaintsRHS[i + matDim * j]);
                }
            }
        }

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        for(int i = 0; i < matDim+num_rhs+1; i++) {
            canvas.drawLine(orientationPointsX[i], orientationPointsY[0], orientationPointsX[i], orientationPointsY[matDim], paint);
        }

        for(int i = 0; i < matDim+1; i++) {
            canvas.drawLine(orientationPointsX[0], orientationPointsY[i], orientationPointsX[matDim+ num_rhs], orientationPointsY[i], paint);
        }
        canvas.drawLine(orientationPointsX[matDim]-5, orientationPointsY[0], orientationPointsX[matDim]-5, orientationPointsY[matDim], paint);
        canvas.drawLine(orientationPointsX[matDim]+5, orientationPointsY[0], orientationPointsX[matDim]+5, orientationPointsY[matDim], paint);

        String sL="$$";
        String sR="$$";
        String sP="$$";
        if(matDim==4) {
            sL=sL+"\\\\small";
            sR=sR+"\\\\small";
            sP=sP+"\\\\small";
        }
        else if(matDim==5) {
            sL=sL+"\\\\tiny";
            sR=sR+"\\\\tiny";
            sP=sP+"\\\\tiny";
        }

        if(lu_phase==matDim-1) {
            sR=sR+" R= \\\\begin{pmatrix} ";
            sL=sL+" L= \\\\begin{pmatrix} ";
            sP=sP+"  P= \\\\begin{pmatrix} ";
            for(int j = 0; j < matDim; j++) {
                for (int i = 0; i < matDim; i++) {
                    if(array_denominator[i + matDim * j]==1)
                        sR = sR + String.format("%d ",array_numerator[i + matDim * j]);
                    else
                        sR = sR + String.format("\\\\frac{%d}{%d} ",array_numerator[i + matDim * j],array_denominator[i + matDim * j]);

                    if(rhs_denominator[i + matDim * j]==1)
                        sL = sL + String.format("%d ",rhs_numerator[i + matDim * j]);
                    else
                        sL = sL + String.format("\\\\frac{%d}{%d} ",rhs_numerator[i + matDim * j],rhs_denominator[i + matDim * j]);
                    sP = sP +  String.format("%d ",piv_mat[i + matDim * j]);
                    if(i<matDim-1) {
                        sL = sL + " & ";
                        sP = sP + " & ";
                        sR = sR + " & ";
                    }
                }

                if(j<matDim-1) {
                    sL = sL + "\\\\\\\\";
                    sP = sP + "\\\\\\\\";
                    sR = sR + "\\\\\\\\";
                }
            }

            sL = sL + "\\\\end{pmatrix} $$";
            sR = sR + "\\\\end{pmatrix} $$";
            sP = sP + "\\\\end{pmatrix} $$";
            textL.setText(sL);
            textR.setText(sR);
            textP.setText(sP);
        }
    }



    // Routine für Aktionen bei TouchEvents
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x_action= event.getX();
        float y_action = event.getY();
System.out.println(matDim+" "+lu_phase);
        if(!finished) {
            switch (event.getAction()) {
                // Einfacher Touch, keine Bewegung
                case MotionEvent.ACTION_DOWN:
                    for (int i = 0; i < matDim; i++)
                        if (x_action > orientationPointsX[i] && x_action < orientationPointsX[i + 1]) {
                            for (int j = 0; j < matDim; j++) {
                                if (y_action > orientationPointsY[j] && y_action < orientationPointsY[j + 1]) {
                                    selected_row = j;
                                    row = j;
                                    for (int k = 0; k < matDim; k++) {
                                        paints[k + j * matDim].setColor(Color.RED);
                                        //paints[i + k * matDim].setColor(Color.RED);
                                    }
                                    j = matDim;
                                    i = matDim;
                                }
                            }
                        }

                    break;
                // Bewegter Touch
                case MotionEvent.ACTION_MOVE:
                    for (int k = 0; k < matDim; k++) {
                        matPosY[k + selected_row * matDim] = event.getY();
                    }

                    //float delta_bins[] = {0, 0.3f * size_bins, 0.7f * size_bins, size_bins};

                    for (int i = 0; i < matDim; i++) {
                            if ((x_action > orientationPointsX[i] ) && (x_action < orientationPointsX[i] + size_bins)) {
                                for (int j = 0; j < matDim; j++) {
                                        if ((y_action > orientationPointsY[j]) && (y_action < orientationPointsY[j] + size_bins)) {
                                            row = j;

                                            j = matDim;
                                            i = matDim;

                                        }
                                }
                            }
                    }

                    if (row == selected_row) {
                        for (int i = 0; i < matDim; i++) {
                            for (int j = 0; j < matDim; j++) {
                                if (i != selected_row) {
                                    matPosX[j + i * matDim] = staticMatPosX[j + i * matDim];
                                    matPosY[j + i * matDim] = staticMatPosY[j + i * matDim];
                                    paints[j + i * matDim].setColor(Color.BLACK);
                                }
                            }
                        }
                    } else {
                        for (int i = 0; i < matDim; i++) {
                            for (int j = 0; j < matDim; j++) {
                                if (i != selected_row && i != row)
                                    paints[j + i * matDim].setColor(Color.BLACK);
                            }
                        }
                        if (selected_row < row) {
                            for (int i = 0; i < matDim; i++) {
                                for (int j = 0; j < matDim; j++) {
                                    //schwarz und wert setzen
                                    if (i != selected_row && i != row) {
                                        paints[j + i * matDim].setColor(Color.BLACK);
                                        matPosY[j + i * matDim] = staticMatPosY[j + i * matDim];
                                    }
                                }
                            }
                            if (!change_or_elim.isChecked())
                                for (int k = 0; k < matDim; k++) {
                                    paints[k + row * matDim].setColor(Color.BLUE);
                                }
                            else {
                                //selected row unverändert
                                //alle andern außer selcted row and row zurücksetzen
                                for (int i = 0; i < matDim; i++) {
                                    if (i != row && i != selected_row) {
                                        for (int j = 0; j < matDim; j++) {
                                            if (i != selected_row)
                                                matPosY[j + i * matDim] = staticMatPosY[j + i * matDim];
                                        }
                                    }
                                }
                                for (int k = 0; k < matDim; k++) {
                                    paints[k + row * matDim].setColor(Color.BLUE);
                                    matPosY[k + row * matDim] = staticMatPosY[k + selected_row * matDim];
                                }
                            }
                        } else if (selected_row > row) {
                            for (int i = 0; i < matDim; i++) {
                                for (int j = 0; j < matDim; j++) {
                                    //schwarz und wert setzen
                                    if (i != selected_row && i != row) {
                                        paints[j + i * matDim].setColor(Color.BLACK);
                                        matPosY[j + i * matDim] = staticMatPosY[j + i * matDim];
                                    }
                                }
                            }
                            if (!change_or_elim.isChecked())
                                for (int k = 0; k < matDim; k++) {
                                    paints[k + row * matDim].setColor(Color.BLUE);
                                }
                            else {
                                //selected row unverändert
                                //alle andern außer selcted row and row zurücksetzen
                                for (int i = 0; i < matDim; i++) {
                                    if (i != row && i != selected_row) {
                                        for (int j = 0; j < matDim; j++) {
                                            if (i != selected_row)
                                                matPosY[j + i * matDim] = staticMatPosY[j + i * matDim];
                                        }
                                    }
                                }
                                for (int k = 0; k < matDim; k++) {
                                    paints[k + row * matDim].setColor(Color.BLUE);
                                    matPosY[k + row * matDim] = staticMatPosY[k + selected_row * matDim];
                                }
                            }
                        }

                    }

                    break;
                case MotionEvent.ACTION_UP:

                    if (selected_row == row && last_row == row && modus != 1) {
                        double num = array_denominator[row + selected_row * matDim];
                        double den = array_numerator[row + selected_row * matDim];
                        boolean is_zero = true;

                        for (int k = 0; k < matDim; k++) {
                            if (Math.abs(((float)array_numerator[k + selected_row * matDim] / array_denominator[k + selected_row * matDim])) > 1E-1 && k != row)
                                is_zero = false;
                        }
                        if (is_zero) {
                            for (int k = 0; k < matDim; k++) {
                                array_numerator[k + selected_row * matDim] *= num;
                                array_denominator[k + selected_row * matDim] *= den;
                                kuerzen(array_numerator, array_denominator, k + selected_row * matDim);
                            }
                            for (int k = 0; k < matDim; k++) {
                                rhs_numerator[k + selected_row * matDim] *= num;
                                rhs_denominator[k + selected_row * matDim] *= den;
                                kuerzen(rhs_numerator, rhs_denominator, k + selected_row * matDim);
                            }
                        }
                    }

                    if (row < lu_phase || selected_row < lu_phase) {
                        reset_action();
                    }
                    if (modus == 1 && selected_row > row) {
                        int tmp = selected_row;
                        selected_row = row;
                        row = tmp;
                    }

                    //setze auf feste positionen wenn row!=selctedrow && col!=selected row
                    //tausche static array und move array (also physikalischer tausch)
                    //setze alles wieder auf standard werte
                    if (row == selected_row) {/*do nothing*/}
                    else if (selected_row > row) {
                        // tausche spalten oder verrechne spalten
                        if (change_or_elim.isChecked()) {
                            int[] tmp_nrow = new int[matDim];
                            int[] tmp_drow = new int[matDim];
                            for (int k = 0; k < matDim; k++) {
                                tmp_nrow[k] = array_numerator[k + selected_row * matDim];
                                tmp_drow[k] = array_denominator[k + selected_row * matDim];
                            }
                            for (int k = 0; k < matDim; k++) {
                                array_numerator[k + selected_row * matDim] = array_numerator[k + row * matDim];
                                array_denominator[k + selected_row * matDim] = array_denominator[k + row * matDim];
                                array_numerator[k + row * matDim] = tmp_nrow[k];
                                array_denominator[k + row * matDim] = tmp_drow[k];
                            }
                            for (int k = 0; k < matDim; k++) {
                                tmp_nrow[k] = rhs_numerator[k + selected_row * matDim];
                                tmp_drow[k] = rhs_denominator[k + selected_row * matDim];
                            }

                            if (modus != 1) {
                                for (int k = 0; k < matDim; k++) {
                                    rhs_numerator[k + selected_row * matDim] = rhs_numerator[k + row * matDim];
                                    rhs_denominator[k + selected_row * matDim] = rhs_denominator[k + row * matDim];
                                    rhs_numerator[k + row * matDim] = tmp_nrow[k];
                                    rhs_denominator[k + row * matDim] = tmp_drow[k];
                                }
                            }

                        } else  { //verrechne spalten
                            int el_entry = 0;
                            for (int k = matDim - 1; k >= 0; k--) {
                                if (Math.abs((float)array_numerator[k + row * matDim] / array_denominator[k + row * matDim]) > 1E-6) {
                                    el_entry = k;
                                    k = -1;
                                }
                            }
                            if (Math.abs((float)array_numerator[el_entry + selected_row * matDim]/array_denominator[el_entry + selected_row * matDim]) > 1E-6) {
                                int den = array_denominator[el_entry + row * matDim] * array_numerator[el_entry + selected_row * matDim];
                                int num = -array_numerator[el_entry + row * matDim] * array_denominator[el_entry + selected_row * matDim];
                                for (int k = 0; k < matDim; k++) {
                                    elim_row(array_numerator, array_denominator, k + row * matDim, k + selected_row * matDim, num, den);

                                    if (modus != 1) {
                                        elim_row(rhs_numerator, rhs_denominator, k + row * matDim, k + selected_row * matDim, num, den);
                                    }
                                }
                            } else warning_div_by_zero();
                        }

                        // wenn selected row > row , eliminiere links
                        // wenn seleted row < row eliminiere rechts
                    } else if (selected_row < row) {
                        // tausche spalten oder verrechne spalten
                        if (change_or_elim.isChecked()) {
                            if(modus!=1 || (!touched_rows[row]&&!touched_rows[selected_row])) {
                                int[] tmp_nrow = new int[matDim];
                                int[] tmp_drow = new int[matDim];
                                for (int k = 0; k < matDim; k++) {
                                    tmp_nrow[k] = array_numerator[k + selected_row * matDim];
                                    tmp_drow[k] = array_denominator[k + selected_row * matDim];
                                }
                                for (int k = 0; k < matDim; k++) {
                                    array_numerator[k + selected_row * matDim] = array_numerator[k + row * matDim];
                                    array_denominator[k + selected_row * matDim] = array_denominator[k + row * matDim];
                                    array_numerator[k + row * matDim] = tmp_nrow[k];
                                    array_denominator[k + row * matDim] = tmp_drow[k];
                                }
                                for (int k = 0; k < matDim; k++) {
                                    tmp_nrow[k] = rhs_numerator[k + selected_row * matDim];
                                    tmp_drow[k] = rhs_denominator[k + selected_row * matDim];
                                }

                                if (modus != 1) {// Gauß El & Gauß Jordan

                                    for (int k = 0; k < matDim; k++) {
                                        rhs_numerator[k + selected_row * matDim] = rhs_numerator[k + row * matDim];
                                        rhs_denominator[k + selected_row * matDim] = rhs_denominator[k + row * matDim];
                                        rhs_numerator[k + row * matDim] = tmp_nrow[k];
                                        rhs_denominator[k + row * matDim] = tmp_drow[k];
                                    }
                                } else {
                                        if(selected_row!=0)
                                            for (int k = 0; k < lu_phase + 1; k++) {
                                                rhs_numerator[k + selected_row * matDim] = rhs_numerator[k + row * matDim];
                                                rhs_denominator[k + selected_row * matDim] = rhs_denominator[k + row * matDim];
                                                rhs_numerator[k + row * matDim] = tmp_nrow[k];
                                                rhs_denominator[k + row * matDim] = tmp_drow[k];
                                        }
                                        for (int k = 0; k < matDim; k++) {
                                            tmp_nrow[k] = piv_mat[k + selected_row * matDim];
                                        }
                                        for (int k = 0; k < matDim; k++) {
                                            piv_mat[k + selected_row * matDim] = piv_mat[k + row * matDim];
                                            piv_mat[k + row * matDim] = tmp_nrow[k];
                                        }

                                }
                            }
                            else {
                                pivot_not_allowed();
                            }
                        } else { //verrechne spalten
                            if (modus == 1 && selected_row != lu_phase) {
                                choose_wrong_row();
                            }
                            int el_entry = 0;
                            for (int k = 0; k < matDim; k++) {
                                if (Math.abs((float)array_numerator[k + row * matDim] / array_denominator[k + row * matDim]) > 1E-6) {
                                    el_entry = k;
                                    k = matDim;
                                }
                            }

                            if (Math.abs((float)array_numerator[el_entry + selected_row * matDim]/array_denominator[el_entry + selected_row * matDim]) > 1E-6) {
                                int den = array_denominator[el_entry + row * matDim] * array_numerator[el_entry + selected_row * matDim];
                                int num = -array_numerator[el_entry + row * matDim] * array_denominator[el_entry + selected_row * matDim];
                                for (int k = 0; k < matDim; k++) {

                                    elim_row(array_numerator, array_denominator, k + row * matDim, k + selected_row * matDim, num, den);

                                    if (modus != 1)
                                        elim_row(rhs_numerator, rhs_denominator, k + row * matDim, k + selected_row * matDim, num, den);
                                }
                                if (modus == 1) {
                                    rhs_numerator[lu_phase + row * matDim] = -num;
                                    rhs_denominator[lu_phase + row * matDim] = den;
                                    kuerzen(rhs_numerator,rhs_denominator,lu_phase+row*matDim);
                                    touched_rows[row] = true;
                                }
                            } else warning_div_by_zero();
                        }
                    }
                    for (int i = 0; i < matDim; i++) {
                        for (int j = 0; j < matDim; j++) {
                            paints[i + j * matDim].setColor(Color.BLACK);
                            matPosX[j + i * matDim] = staticMatPosX[j + i * matDim];
                            matPosY[j + i * matDim] = staticMatPosY[j + i * matDim];
                            paintsRHS[i + j * matDim].setColor(Color.BLACK);
                            rhsPosX[j + i * matDim] = staticRhsPosX[j + i * matDim];
                            rhsPosY[j + i * matDim] = staticRhsPosY[j + i * matDim];
                        }
                    }
                    boolean test_end_phase = true;
                    for (int i = lu_phase + 1; i < matDim; i++) {
//                        test_end_phase = test_end_phase && touched_rows[i];
                        if(array_numerator[i*matDim+lu_phase]!=0)
                            test_end_phase=false;
                    }

                    if(modus==1){
                        if (test_end_phase) {
                            lu_phase++;
                            for (int i = lu_phase; i < matDim; i++)
                                touched_rows[i] = false;
                        }
                        finished =true;
                        for(int j=1;j<matDim;j++) {
                            for (int i=0;i<j;i++) {
                                if(Math.abs((float)array_numerator[i+j*matDim]/array_denominator[i+j*matDim])>1E-6)
                                {
                                    finished=false;
                                    i=matDim;
                                    j=matDim;
                                }
                            }
                        }
                        if(lu_phase==matDim-1)
                            finished=true;
                    }

                    last_row = selected_row;

                    selected_row = 0;
                    row = 0;

                    nstep++;
                    reminder_den_mat[nstep]=array_denominator.clone();
                    reminder_num_mat[nstep]=array_numerator.clone();
                    reminder_den_rhs[nstep]=rhs_denominator.clone();
                    reminder_num_rhs[nstep]=rhs_numerator.clone();
                    reminder_pivot[nstep]=piv_mat.clone();
                    reminder_lu_pahse[nstep]=lu_phase;

                    freeMemory();
            }
        }
        invalidate();

        return true;
    }



    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    public int getDimension() {
        return dimension;
    }


    public void setDimension(int dimension) {
        this.dimension = dimension;
    }


    public void setModus(int m) {this.modus = m;}
    public void set_init(){is_init=true;

        if (is_init) {

            int screen_width = screensizex - 100;
            size_bins = screen_width / (2 * matDim);

            setTextSize((int)Math.floor(size_bins/2.2));

            for (int i = 0; i < 2 * matDim+1; i++) {
                orientationPointsX[i] = 50 + i * size_bins;}
            for (int i = 0; i < matDim+1; i++)
                orientationPointsY[i] = 50 + i * size_bins;


            for (int i = 0; i < matDim; i++) {
                for (int j = 0; j < matDim; j++) {
                    matPosX[i + j * matDim] = orientationPointsX[i ] + size_bins/4;
                    matPosY[i + j * matDim] = orientationPointsY[j + 1] - size_bins/3;
                    staticMatPosX[i + j * matDim] = orientationPointsX[i ] + size_bins/4;
                    staticMatPosY[i + j * matDim] = orientationPointsY[j + 1] - size_bins/3;

                }
                    for (int j = 0; j < matDim; j++) {
                        rhsPosX[i + j * matDim] = orientationPointsX[i + matDim] + size_bins / 4;
                        rhsPosY[i + j * matDim] = orientationPointsY[j + 1] - size_bins / 3;
                        staticRhsPosX[i + j * matDim] = orientationPointsX[i + matDim] + size_bins / 4;
                        staticRhsPosY[i + j * matDim] = orientationPointsY[j + 1] - size_bins / 3;
                    }
            }

        }

        textL.setText("");
        textR.setText("");
        textP.setText("");
        reminder_den_mat[0]=array_denominator.clone();
        reminder_num_mat[0]=array_numerator.clone();
        reminder_den_rhs[0]=rhs_denominator.clone();
        reminder_num_rhs[0]=rhs_numerator.clone();
        reminder_pivot[0]=piv_mat.clone();
        reminder_lu_pahse[0]=0;

        last_row =-1;

        selected_row = 0;
        row = 0;

        lu_phase=0;
        finished = false;
    }

    public void set_screensize(int sizex, int sizey){this.screensizex=sizex;this.screensizey=sizey;}
    public void set_numrhs(int nc) {if(modus==0)this.num_rhs=1;else this.num_rhs=nc;}
    public void set_matdim(int nc) {this.matDim=nc;}

    public void set_matrix(int[] mat) { array_numerator = mat.clone();for(int i=0; i<matDim*matDim;i++){array_denominator[i]=1;kuerzen(array_numerator,array_denominator,i);}boolean tmp[]={false,false,false,false,false};touched_rows=tmp.clone();}
    public void set_rhs(int[] mat) { rhs_numerator = mat.clone();for(int i=0; i<matDim*matDim;i++){rhs_denominator[i]=1;kuerzen(rhs_numerator,rhs_denominator,i);}}
    public void set_pivot(int[] mat) { piv_mat = mat.clone();}

    public void reset_action() {
        alert_dialog.setMessage("Mindestens einer der beiden Zeilen darf nicht mehr angefasst werden, da Sie bereits in Schritt "+lu_phase+" sind, Deswegen dürfen Zeilen 1-"+lu_phase+" nicht mehr verändert werden.");
        alert_dialog.show();
        for(int i = 0; i < matDim; i++) {
            for (int j = 0; j < matDim; j++) {
                paints[i + j * matDim].setColor(Color.BLACK);
                matPosX[j+i*matDim] = staticMatPosX[j+i*matDim];
                matPosY[j+i*matDim] = staticMatPosY[j+i*matDim];
                paintsRHS[i + j * matDim].setColor(Color.BLACK);
                rhsPosX[j+i*matDim] = staticRhsPosX[j+i*matDim];
                rhsPosY[j+i*matDim] = staticRhsPosY[j+i*matDim];
            }
        }

        last_row =selected_row;

        selected_row = 0;
        row = 0;
    }

    public void pivot_not_allowed() {
        alert_dialog.setMessage("Sie können nur Zeilen pivotieren, die zu dieser Phasegehören und in dieser Phase noch nicht verändert wurden.");
        alert_dialog.show();
    }
    public void choose_wrong_row() {
        alert_dialog.setMessage("In Phase "+lu_phase+" muss die Elimination immer mit Zeile "+lu_phase+ " vorgenommen werden!");
        alert_dialog.show();
        for(int i = 0; i < matDim; i++) {
            for (int j = 0; j < matDim; j++) {
                paints[i + j * matDim].setColor(Color.BLACK);
                matPosX[j+i*matDim] = staticMatPosX[j+i*matDim];
                matPosY[j+i*matDim] = staticMatPosY[j+i*matDim];
                paintsRHS[i + j * matDim].setColor(Color.BLACK);
                rhsPosX[j+i*matDim] = staticRhsPosX[j+i*matDim];
                rhsPosY[j+i*matDim] = staticRhsPosY[j+i*matDim];
            }
        }

        last_row =selected_row;

        selected_row = 0;
        row = 0;
    }
    private void warning_div_by_zero() {
        alert_dialog.setMessage("Diese Aktion ist nicht möglich. Es wird immer das linkteste (bzw. rechteste) nicht Null Element eine Zeile eliminiert. Dies ist hier nicht möglich, da die zu verrechnende Zeile dort den Eintrag Null hat.");
        alert_dialog.show();
    }

    public void set_alertDialog(AlertDialog dia){this.alert_dialog=dia;}

    public void setTextViews(MathView L, MathView R, MathView P) {
        this.textL=L;
        this.textR=R;
        this.textP=P;
    }

    private int ggT(int aa, int bb){

        int a = Math.abs(aa);
        int b = Math.abs(bb);
        if(b == 0)

            return a;

        else return ggT(b, a % b);

    }

    private int kgV(int aa, int bb){

        int a = Math.abs(aa);
        int b = Math.abs(bb);
        return (a * b) / ggT(a, b);

    }

    private void kuerzen(int[] zaehler, int[] nenner,int idx) {
        int tmp = ggT(zaehler[idx], nenner[idx]);

        if (tmp != 0) {
            zaehler[idx] /= tmp;
            nenner[idx] /= tmp;
        }
        if(nenner[idx]<0){
            zaehler[idx]*=-1;
            nenner[idx]*=-1;
        }
    }

    private void elim_row(int[] num, int[] den, int idx1,int idx2, int facn, int facd) {

        kuerzen(num,den,idx1);


        int tmp_num[] = {facn*num[idx2]};
        int tmp_den[] = {facd*den[idx2]};
        kuerzen(tmp_num, tmp_den, 0);

        if(tmp_num[0]==0) {

        }
        else if( num[idx1]==0) {
            num[idx1] = tmp_num[0];
            den[idx1] = tmp_den[0];
        }
        else {
            int sol_den = kgV(den[idx1], tmp_den[0]);
            int sol_num = num[idx1] * (sol_den / den[idx1]) +
                    tmp_num[0] * (sol_den / tmp_den[0]);

            num[idx1] = sol_num;
            den[idx1] = sol_den;
        }
        kuerzen(num,den,idx1);

    }

    private void setTextSize(int size) {


        for (int i = 0; i < 25; i++) {
            paints[i].setTextSize(size);
            staticPaints[i].setTextSize(size);
            paintsRHS[i].setTextSize(size);
            staticPaintsRHS[i].setTextSize(size);
            fracpaints[i].setTextSize(size/1.5f);
            fracstaticPaints[i].setTextSize(size/1.5f);
            fracpaintsRHS[i].setTextSize(size/1.5f);
            fracstaticPaintsRHS[i].setTextSize(size/1.5f);
        }
    }


    public void set_change_or_elim(Switch sw) {
        this.change_or_elim = sw;
    }

    public void reset_params() {

        last_row =selected_row;

        selected_row = 0;
        row = 0;
        lu_phase=0;
        nstep=0;
        finished = false;
    }

    public void undo()
    {
        if(nstep>0) {
            nstep=nstep-1;

            lu_phase = reminder_lu_pahse[nstep];
            array_denominator=reminder_den_mat[nstep].clone();
            array_numerator=reminder_num_mat[nstep].clone();
            rhs_denominator=reminder_den_rhs[nstep].clone();
            rhs_numerator=reminder_num_rhs[nstep].clone();
            piv_mat=reminder_pivot[nstep].clone();
        }
        else {
            alert_dialog.setMessage("Das ist bereits das Ausgangssystem, weiter zurück geht es nicht!");
            alert_dialog.show();
        }
        invalidate();
    }
}

