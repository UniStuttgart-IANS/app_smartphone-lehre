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
public class UpperTriangleSelecter extends View {

    private Paint diagPaint = new Paint();
    private Paint deactivePaint = new Paint();
    private Paint activePaint = new Paint();

    private int linePitch;

    private Button computeButton;

    private MyMatrix myMatrix;

    private MatrixView matrixView;

    private UpperDiagSelecter upperDiagSelecter;

    public UpperTriangleSelecter(Context context, AttributeSet attrs) {
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
            if (myMatrix.getBlockSelecterMatrix()[7][myMatrix.getSelectedBlockNumber()]) {
                canvas.drawLine(2 * linePitch, linePitch, width - linePitch, height - 2 * linePitch, activePaint);
                canvas.drawLine(2 * linePitch, linePitch, width - linePitch, linePitch, activePaint);
                canvas.drawLine(width - linePitch, linePitch, width - linePitch, height - 2 * linePitch, activePaint);
            }
            //No Active
            else {
                canvas.drawLine(2 * linePitch, linePitch, width - linePitch, height - 2 * linePitch, deactivePaint);
                canvas.drawLine(2 * linePitch, linePitch, width - linePitch, linePitch, deactivePaint);
                canvas.drawLine(width - linePitch, linePitch, width - linePitch, height - 2 * linePitch, deactivePaint);
            }
        } else {
            // Activ
            if (myMatrix.getWholeSelecterVector()[7]) {
                canvas.drawLine(2 * linePitch, linePitch, width - linePitch, height - 2 * linePitch, activePaint);
                canvas.drawLine(2 * linePitch, linePitch, width - linePitch, linePitch, activePaint);
                canvas.drawLine(width - linePitch, linePitch, width - linePitch, height - 2 * linePitch, activePaint);
            }
            //No Active
            else {
                canvas.drawLine(2 * linePitch, linePitch, width - linePitch, height - 2 * linePitch, deactivePaint);
                canvas.drawLine(2 * linePitch, linePitch, width - linePitch, linePitch, deactivePaint);
                canvas.drawLine(width - linePitch, linePitch, width - linePitch, height - 2 * linePitch, deactivePaint);
            }
        }

        canvas.drawLine(linePitch, linePitch, width - linePitch, height - linePitch, diagPaint);

        upperDiagSelecter.invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                matrixView.setHasChanced(true);
                if (myMatrix.getZoom() == true && myMatrix.getBlockSelecterMatrix()[7][myMatrix.getSelectedBlockNumber()] == false) {
                    myMatrix.selectUpperTriangle(myMatrix.getSelectedBlockNumber());
                    if (!matrixView.getIsSolving()) {
                        computeButton.setVisibility(View.INVISIBLE);
                    }
                } else if (myMatrix.getZoom() == true && myMatrix.getBlockSelecterMatrix()[7][myMatrix.getSelectedBlockNumber()] == true) {
                    myMatrix.unselectUpperTriangle(myMatrix.getSelectedBlockNumber());
                    if (!matrixView.getIsSolving()) {
                        computeButton.setVisibility(View.INVISIBLE);
                    }
                } else if (myMatrix.getZoom() == false && !myMatrix.getWholeSelecterVector()[7]) {
                    myMatrix.selectUpperTriangle(-1);
                    if (!matrixView.getIsSolving()) {
                        computeButton.setVisibility(View.INVISIBLE);
                    }
                } else if (myMatrix.getZoom() == false && myMatrix.getWholeSelecterVector()[7]) {
                    myMatrix.unselectUpperTriangle(-1);
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

    public void setUpperDiagSelecter(UpperDiagSelecter upperDiagSelecter) {
        this.upperDiagSelecter = upperDiagSelecter;
    }

    public void setComputeButton(Button computeButton) {
        this.computeButton = computeButton;
    }
}


