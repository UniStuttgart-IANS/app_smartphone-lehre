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
public class LowerTriangleSelecter extends View {

    private Paint diagPaint = new Paint();
    private Paint deactivePaint = new Paint();
    private Paint activePaint = new Paint();
    private int linePitch;

    private MyMatrix myMatrix;

    private Button computeButton;

    private MatrixView matrixView;

    private LowerDiagSelecter lowerDiagSelecter;

    public LowerTriangleSelecter(Context context, AttributeSet attrs) {
        super(context, attrs);

        diagPaint.setAntiAlias(true);
        diagPaint.setStrokeWidth(1F);
        diagPaint.setColor(Color.BLACK);
        diagPaint.setTextSize(70);

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
            if (myMatrix.getBlockSelecterMatrix()[6][myMatrix.getSelectedBlockNumber()]) {
                canvas.drawLine(linePitch, 2 * linePitch, width - 2 * linePitch, height - linePitch, activePaint);
                canvas.drawLine(linePitch, 2 * linePitch, linePitch, height - linePitch, activePaint);
                canvas.drawLine(linePitch, height - linePitch, width - 2 * linePitch, height - linePitch, activePaint);
            }
            //No Active
            else {
                canvas.drawLine(linePitch, 2 * linePitch, width - 2 * linePitch, height - linePitch, deactivePaint);
                canvas.drawLine(linePitch, 2 * linePitch, linePitch, height - linePitch, deactivePaint);
                canvas.drawLine(linePitch, height - linePitch, width - 2 * linePitch, height - linePitch, deactivePaint);
            }
        } else {
            // Activ
            if (myMatrix.getWholeSelecterVector()[6]) {
                canvas.drawLine(linePitch, 2 * linePitch, width - 2 * linePitch, height - linePitch, activePaint);
                canvas.drawLine(linePitch, 2 * linePitch, linePitch, height - linePitch, activePaint);
                canvas.drawLine(linePitch, height - linePitch, width - 2 * linePitch, height - linePitch, activePaint);
            }
            //No Active
            else {
                canvas.drawLine(linePitch, 2 * linePitch, width - 2 * linePitch, height - linePitch, deactivePaint);
                canvas.drawLine(linePitch, 2 * linePitch, linePitch, height - linePitch, deactivePaint);
                canvas.drawLine(linePitch, height - linePitch, width - 2 * linePitch, height - linePitch, deactivePaint);
            }
        }
        canvas.drawLine(linePitch, linePitch, width - linePitch, height - linePitch, diagPaint);

        lowerDiagSelecter.invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                matrixView.setHasChanced(true);
                if (myMatrix.getZoom() == true && myMatrix.getBlockSelecterMatrix()[6][myMatrix.getSelectedBlockNumber()] == false) {
                    myMatrix.selectLowerTriangle(myMatrix.getSelectedBlockNumber());
                    myMatrix.selectLowerDiag(myMatrix.getSelectedBlockNumber());
                    if (!matrixView.getIsSolving()) {
                        computeButton.setVisibility(View.INVISIBLE);
                    }
                } else if (myMatrix.getZoom() == true && myMatrix.getBlockSelecterMatrix()[6][myMatrix.getSelectedBlockNumber()] == true) {
                    myMatrix.unselectLowerTriangle(myMatrix.getSelectedBlockNumber());
                    myMatrix.unselectLowerDiag(myMatrix.getSelectedBlockNumber());
                    if (!matrixView.getIsSolving()) {
                        computeButton.setVisibility(View.INVISIBLE);
                    }
                } else if (myMatrix.getZoom() == false && !myMatrix.getWholeSelecterVector()[6]) {
                    myMatrix.selectLowerTriangle(-1);
                    myMatrix.selectLowerDiag(-1);
                    if (!matrixView.getIsSolving()) {
                        computeButton.setVisibility(View.INVISIBLE);
                    }
                } else if (myMatrix.getZoom() == false && myMatrix.getWholeSelecterVector()[6]) {
                    myMatrix.unselectLowerTriangle(-1);
                    myMatrix.unselectLowerDiag(-1);
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

    public void setLowerDiagSelecter(LowerDiagSelecter lowerDiagSelecter) {
        this.lowerDiagSelecter = lowerDiagSelecter;
    }

    public void setComputeButton(Button computeButton) {
        this.computeButton = computeButton;
    }
}



