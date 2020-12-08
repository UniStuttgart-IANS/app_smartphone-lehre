package com.uni_stuttgart.isl.Splittingsolver.Selecter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.uni_stuttgart.isl.Splittingsolver.MatrixView;
import com.uni_stuttgart.isl.Splittingsolver.MyMatrix;

/**
 * Created by nborg on 07.03.16.
 */
public class AntiDiagSelecter extends View {

    private Paint activePaint = new Paint();
    private Paint deactivePaint = new Paint();

    private int linePitch;

    private Button computeButton;

    private MyMatrix myMatrix;

    private MatrixView matrixView;

    public AntiDiagSelecter(Context context, AttributeSet attrs) {
        super(context, attrs);

        activePaint.setAntiAlias(true);
        activePaint.setStyle(Paint.Style.FILL);
        activePaint.setStrokeWidth(5F);
        activePaint.setColor(Color.BLACK);
        activePaint.setTextSize(70);

        deactivePaint.setAntiAlias(true);
        deactivePaint.setStyle(Paint.Style.FILL);
        deactivePaint.setStrokeWidth(5F);
        deactivePaint.setColor(Color.WHITE);
        deactivePaint.setTextSize(70);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = this.getWidth();
        int height = this.getHeight();

        if (myMatrix.getZoom()) {
            // Activ
            if (myMatrix.getBlockSelecterMatrix()[1][myMatrix.getSelectedBlockNumber()]) {
                canvas.drawLine(linePitch, height - linePitch, width - linePitch, linePitch, activePaint);
            }
            //No Active
            else {
                canvas.drawLine(linePitch, height - linePitch, width - linePitch, linePitch, deactivePaint);
            }
        } else {
            // Activ
            if (myMatrix.getWholeSelecterVector()[1]) {
                canvas.drawLine(linePitch, height - linePitch, width - linePitch, linePitch, activePaint);
            }
            //No Active
            else {
                canvas.drawLine(linePitch, height - linePitch, width - linePitch, linePitch, deactivePaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                matrixView.setHasChanced(true);
                if (myMatrix.getZoom() == true && myMatrix.getBlockSelecterMatrix()[1][myMatrix.getSelectedBlockNumber()] == false) {
                    myMatrix.selectAntiDiag(myMatrix.getSelectedBlockNumber());
                    if (!matrixView.getIsSolving()) {
                        computeButton.setVisibility(View.INVISIBLE);
                    }
                } else if (myMatrix.getZoom() == true && myMatrix.getBlockSelecterMatrix()[1][myMatrix.getSelectedBlockNumber()] == true) {
                    myMatrix.unselectAntiDiag(myMatrix.getSelectedBlockNumber());
                    if (!matrixView.getIsSolving()) {
                        computeButton.setVisibility(View.INVISIBLE);
                    }
                } else if (myMatrix.getZoom() == false && !myMatrix.getWholeSelecterVector()[1]) {
                    myMatrix.selectAntiDiag(-1);
                    if (!matrixView.getIsSolving()) {
                        computeButton.setVisibility(View.INVISIBLE);
                    }
                } else if (myMatrix.getZoom() == false && myMatrix.getWholeSelecterVector()[1]) {
                    myMatrix.unselectAntiDiag(-1);
                    if (!matrixView.getIsSolving()) {
                        computeButton.setVisibility(View.INVISIBLE);
                    }
                }
        }
        invalidate();
        matrixView.invalidate();
        return true;
    }

    public void setMyMatrix(MyMatrix myMatrix) {
        this.myMatrix = myMatrix;
    }

    public void setMatrixView(MatrixView matrixView) {
        this.matrixView = matrixView;
    }

    public void setLinePitch(int linePitch) {
        this.linePitch = linePitch;
    }

    public void setComputeButton(Button computeButton) {
        this.computeButton = computeButton;
    }
}

