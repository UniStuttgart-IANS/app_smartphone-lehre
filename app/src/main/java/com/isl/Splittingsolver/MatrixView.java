package com.uni_stuttgart.isl.Splittingsolver;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
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

/**
 * Created by nborg on 07.03.16.
 */
public class MatrixView extends View {
    Paint backArrowPaint = new Paint();
    private int dimension;
    private int[] BlockCornerCoords = new int[18];
    private Paint blockEdgePaint = new Paint();
    private Paint zoomSelecterPaint = new Paint();
    private Paint zoomUnselecterPaint = new Paint();
    private Paint textPaint = new Paint();
    private GestureDetector gestureDetector;
    private MyMatrix myMatrix;
    private boolean[][] activityStructure = new boolean[dimension][dimension];
    private Button computeButton;
    private int blockSize;
    private int distanceMatrixEdge = 20;
    private int distanceZoomBlockEdge = 100;
    private int sizeOfCanvas;
    private int zoomSelecter = 0; // 0: deaktiv, 1: select, 2: unselect
    private Boolean isSolving = false;
    private Boolean hasChanced = false;

    private DiagSelecter diagselecter;
    private AntiDiagSelecter antidiagselecter;
    private LowerDiagSelecter lowerdiagselecter;
    private UpperDiagSelecter upperdiagselecter;
    private LowerAntiDiagSelecter lowerantidiagselecter;
    private UpperAntiDiagSelecter upperantidiagselecter;
    private LowerTriangleSelecter lowertriangleselecter;
    private UpperTriangleSelecter uppertriangleselecter;
    private LowerAntiTriangleSelecter lowerantitriangleselecter;
    private UpperAntiTriangleSelecter upperantitriangleselecter;

    private LineChart mChart;

    public MatrixView(Context context, AttributeSet attrs) {
        super(context, attrs);

        gestureDetector = new GestureDetector(context, new GestureListener());

        blockEdgePaint.setAntiAlias(true);
        blockEdgePaint.setStyle(Paint.Style.STROKE);
        blockEdgePaint.setStrokeJoin(Paint.Join.ROUND);
        blockEdgePaint.setStrokeWidth(1F);
        blockEdgePaint.setColor(Color.WHITE);

        backArrowPaint.setAntiAlias(true);
        backArrowPaint.setStyle(Paint.Style.STROKE);
        backArrowPaint.setStrokeJoin(Paint.Join.ROUND);
        backArrowPaint.setStrokeWidth(5F);
        backArrowPaint.setColor(Color.WHITE);

        zoomSelecterPaint.setAntiAlias(true);
        zoomSelecterPaint.setStyle(Paint.Style.STROKE);
        zoomSelecterPaint.setStrokeJoin(Paint.Join.ROUND);
        zoomSelecterPaint.setStrokeWidth(30F);
        zoomSelecterPaint.setColor(Color.BLACK);

        zoomUnselecterPaint.setAntiAlias(true);
        zoomUnselecterPaint.setStyle(Paint.Style.STROKE);
        zoomUnselecterPaint.setStrokeJoin(Paint.Join.ROUND);
        zoomUnselecterPaint.setStrokeWidth(30F);
        zoomUnselecterPaint.setColor(Color.WHITE);

        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setStrokeJoin(Paint.Join.ROUND);
        textPaint.setStrokeWidth(2F);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30F);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mChart.clearValues();
        mChart.invalidate();


        if (myMatrix.getZoom() == false) {
            canvas.drawARGB(0, 192, 192, 192);

            canvas.drawRect(BlockCornerCoords[0], BlockCornerCoords[1], BlockCornerCoords[0] + blockSize, BlockCornerCoords[1] + blockSize, blockEdgePaint);
            canvas.drawRect(BlockCornerCoords[2], BlockCornerCoords[3], BlockCornerCoords[2] + blockSize, BlockCornerCoords[3] + blockSize, blockEdgePaint);
            canvas.drawRect(BlockCornerCoords[4], BlockCornerCoords[5], BlockCornerCoords[4] + blockSize, BlockCornerCoords[5] + blockSize, blockEdgePaint);
            canvas.drawRect(BlockCornerCoords[6], BlockCornerCoords[7], BlockCornerCoords[6] + blockSize, BlockCornerCoords[7] + blockSize, blockEdgePaint);
            canvas.drawRect(BlockCornerCoords[8], BlockCornerCoords[9], BlockCornerCoords[8] + blockSize, BlockCornerCoords[9] + blockSize, blockEdgePaint);
            canvas.drawRect(BlockCornerCoords[10], BlockCornerCoords[11], BlockCornerCoords[10] + blockSize, BlockCornerCoords[11] + blockSize, blockEdgePaint);
            canvas.drawRect(BlockCornerCoords[12], BlockCornerCoords[13], BlockCornerCoords[12] + blockSize, BlockCornerCoords[13] + blockSize, blockEdgePaint);
            canvas.drawRect(BlockCornerCoords[14], BlockCornerCoords[14], BlockCornerCoords[14] + blockSize, BlockCornerCoords[15] + blockSize, blockEdgePaint);
            canvas.drawRect(BlockCornerCoords[16], BlockCornerCoords[15], BlockCornerCoords[16] + blockSize, BlockCornerCoords[17] + blockSize, blockEdgePaint);

            for (int k = 0; k < 9; k++) {
                for (int i = 0; i < dimension / 3; i++) {
                    for (int j = 0; j < dimension / 3; j++) {
                        if (myMatrix.getBlock(k)[i][j].getValue() != 0) {
                            myMatrix.getBlock(k)[i][j].getPointPaint().setStrokeWidth(700 / dimension);
                            canvas.drawPoint(BlockCornerCoords[2 * k] + j * blockSize / (dimension / 3) + (blockSize / (dimension / 3)) / 2,
                                    BlockCornerCoords[2 * k + 1] + i * blockSize / (dimension / 3) + (blockSize / (dimension / 3)) / 2, myMatrix.getBlock(k)[i][j].getPointPaint());
                        }
                    }
                }
            }
        } else if (myMatrix.getZoom() == true) {
            canvas.drawARGB(0, 192, 192, 192);
            canvas.drawLine(distanceMatrixEdge, 50, 80, 50, backArrowPaint);
            canvas.drawLine(distanceMatrixEdge, 50, 40, 70, backArrowPaint);
            canvas.drawLine(distanceMatrixEdge, 50, 40, 30, backArrowPaint);
            canvas.drawText("<-Choose->", sizeOfCanvas / 2 - 73, 60, textPaint);
            if (zoomSelecter == 0) {
                zoomSelecterPaint.setStrokeWidth(30);
                zoomUnselecterPaint.setStrokeWidth(30);
            } else if (zoomSelecter == 1) {
                zoomSelecterPaint.setStrokeWidth(50);
                zoomUnselecterPaint.setStrokeWidth(20);
            } else if (zoomSelecter == 2) {
                zoomSelecterPaint.setStrokeWidth(20);
                zoomUnselecterPaint.setStrokeWidth(50);
            }
            canvas.drawPoint(sizeOfCanvas / 2 - 120, 50, zoomSelecterPaint);
            canvas.drawPoint(sizeOfCanvas / 2 + 120, 50, zoomUnselecterPaint);

            for (int k = 0; k < 9; k++) {
                for (int i = 0; i < dimension / 3; i++) {
                    for (int j = 0; j < dimension / 3; j++) {
                        if (myMatrix.getSelectedBlockNumber() == k && myMatrix.getBlock(k)[i][j].getValue() != 0) {
                            myMatrix.getBlock(k)[i][j].getTextPaint().setTextSize(2000 / dimension);
                            canvas.drawText("" + (int) (myMatrix.getBlock(k)[i][j].getValue()), distanceZoomBlockEdge + j * (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) + (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) / 2 - 1000 / dimension,
                                    distanceZoomBlockEdge + i * (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) + (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) / 2 + 1000 / dimension, myMatrix.getBlock(k)[i][j].getTextPaint());
                        }
                    }
                }
            }
        }
        freeMemory();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        double[] xy = new double[2];
        // Koordinaten des "Fingers"
        xy[0] = event.getX();
        xy[1] = event.getY();

        if (myMatrix.getZoom() == true) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Going Back
                    if (xy[0] > 0 && xy[0] < 100 && xy[1] > 0 && xy[1] < 100) {
                        myMatrix.setZoom(false);
                        diagselecter.invalidate();
                        antidiagselecter.invalidate();
                        lowerdiagselecter.invalidate();
                        upperdiagselecter.invalidate();
                        lowerantidiagselecter.invalidate();
                        upperantidiagselecter.invalidate();
                        lowertriangleselecter.invalidate();
                        uppertriangleselecter.invalidate();
                        lowerantitriangleselecter.invalidate();
                        upperantitriangleselecter.invalidate();
                        invalidate();
                        // Choose if select or unselect
                    } else if (xy[0] > sizeOfCanvas / 2 - 170 && xy[0] < sizeOfCanvas / 2 - 110 && xy[1] > 0 && xy[1] < 100 && (zoomSelecter == 0 || zoomSelecter == 2)) {
                        zoomSelecter = 1;
                        invalidate();
                    } else if (xy[0] > sizeOfCanvas / 2 + 110 && xy[0] < sizeOfCanvas / 2 + 170 && xy[1] > 0 && xy[1] < 100 && (zoomSelecter == 0 || zoomSelecter == 1)) {
                        zoomSelecter = 2;
                        invalidate();
                    } else {
                        for (int k = 0; k < 9; k++) {
                            for (int i = 0; i < dimension / 3; i++) {
                                for (int j = 0; j < dimension / 3; j++) {
                                    if (myMatrix.getSelectedBlockNumber() == k) {
                                        if (xy[0] < distanceZoomBlockEdge + j * (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) + (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) / 2 + (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) / 2
                                                && xy[0] > distanceZoomBlockEdge + j * (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) + (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) / 2 - (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) / 2
                                                && xy[1] < distanceZoomBlockEdge + i * (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) + (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) / 2 + (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) / 2
                                                && xy[1] > distanceZoomBlockEdge + i * (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) + (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) / 2 - (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) / 2) {
                                            if (myMatrix.getBlock(k)[i][j].getStatus() == true && zoomSelecter == 2 && myMatrix.getBlock(k)[i][j].getValue() != 0) {
                                                myMatrix.getBlock(k)[i][j].setUnselected();
                                                computeButton.setVisibility(INVISIBLE);
                                                invalidate();
                                                return true;
                                            } else if (myMatrix.getBlock(k)[i][j].getStatus() == false && zoomSelecter == 1 && myMatrix.getBlock(k)[i][j].getValue() != 0) {
                                                myMatrix.getBlock(k)[i][j].setSelected();
                                                computeButton.setVisibility(INVISIBLE);
                                                invalidate();
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                case MotionEvent.ACTION_MOVE:
                    for (int k = 0; k < 9; k++) {
                        for (int i = 0; i < dimension / 3; i++) {
                            for (int j = 0; j < dimension / 3; j++) {
                                if (myMatrix.getSelectedBlockNumber() == k) {
                                    if (xy[0] < distanceZoomBlockEdge + j * (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) + (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) / 2 + (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) / 3
                                            && xy[0] > distanceZoomBlockEdge + j * (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) + (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) / 2 - (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) / 3
                                            && xy[1] < distanceZoomBlockEdge + i * (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) + (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) / 2 + (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) / 3
                                            && xy[1] > distanceZoomBlockEdge + i * (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) + (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) / 2 - (sizeOfCanvas - 2 * distanceZoomBlockEdge) / (dimension / 3) / 3) {
                                        if (myMatrix.getBlock(k)[i][j].getStatus() == true && zoomSelecter == 2 && myMatrix.getBlock(k)[i][j].getValue() != 0) {
                                            myMatrix.getBlock(k)[i][j].setUnselected();
                                            computeButton.setVisibility(INVISIBLE);
                                            invalidate();
                                            return true;
                                        } else if (myMatrix.getBlock(k)[i][j].getStatus() == false && zoomSelecter == 1 && myMatrix.getBlock(k)[i][j].getValue() != 0) {
                                            myMatrix.getBlock(k)[i][j].setSelected();
                                            computeButton.setVisibility(INVISIBLE);
                                            invalidate();
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                case MotionEvent.ACTION_UP:
                    freeMemory();
            }
        } else {
            return gestureDetector.onTouchEvent(event);
        }

        return true;
    }

    public MyMatrix getMyMatrix() {
        return myMatrix;
    }

    public void setMyMatrix(MyMatrix myMatrix) {
        this.myMatrix = new MyMatrix(dimension);
        this.myMatrix = myMatrix;
    }

    public void setComputeButton(Button computeButton) {
        this.computeButton = computeButton;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public void setBlockCornerCoords(int sizeOfCanvas) {
        this.sizeOfCanvas = sizeOfCanvas;
        this.blockSize = (int) (1.0 / 3.0 * (sizeOfCanvas - 2 * distanceMatrixEdge));

        BlockCornerCoords[0] = distanceMatrixEdge;
        BlockCornerCoords[1] = distanceMatrixEdge;

        BlockCornerCoords[2] = distanceMatrixEdge + 1 * blockSize;
        BlockCornerCoords[3] = distanceMatrixEdge;

        BlockCornerCoords[4] = distanceMatrixEdge + 2 * blockSize;
        BlockCornerCoords[5] = distanceMatrixEdge;

        BlockCornerCoords[6] = distanceMatrixEdge;
        BlockCornerCoords[7] = distanceMatrixEdge + 1 * blockSize;

        BlockCornerCoords[8] = distanceMatrixEdge + 1 * blockSize;
        BlockCornerCoords[9] = distanceMatrixEdge + 1 * blockSize;

        BlockCornerCoords[10] = distanceMatrixEdge + 2 * blockSize;
        BlockCornerCoords[11] = distanceMatrixEdge + 1 * blockSize;

        BlockCornerCoords[12] = distanceMatrixEdge;
        BlockCornerCoords[13] = distanceMatrixEdge + 2 * blockSize;

        BlockCornerCoords[14] = distanceMatrixEdge + 1 * blockSize;
        BlockCornerCoords[15] = distanceMatrixEdge + 2 * blockSize;

        BlockCornerCoords[16] = distanceMatrixEdge + 2 * blockSize;
        BlockCornerCoords[17] = distanceMatrixEdge + 2 * blockSize;
    }

    public void setDiagselecter(DiagSelecter diagselecter) {
        this.diagselecter = diagselecter;
    }

    public void setAntidiagselecter(AntiDiagSelecter antidiagselecter) {
        this.antidiagselecter = antidiagselecter;
    }

    public void setLowerdiagselecter(LowerDiagSelecter lowerdiagselecter) {
        this.lowerdiagselecter = lowerdiagselecter;
    }

    public void setUpperdiagselecter(UpperDiagSelecter upperdiagselecter) {
        this.upperdiagselecter = upperdiagselecter;
    }

    public void setLowerantidiagselecter(LowerAntiDiagSelecter lowerantidiagselecter) {
        this.lowerantidiagselecter = lowerantidiagselecter;
    }

    public void setUpperantidiagselecter(UpperAntiDiagSelecter upperantidiagselecter) {
        this.upperantidiagselecter = upperantidiagselecter;
    }

    public void setUppertriangleselecter(UpperTriangleSelecter uppertriangleselecter) {
        this.uppertriangleselecter = uppertriangleselecter;
    }

    public void setLowertriangleselecter(LowerTriangleSelecter lowertriangleselecter) {
        this.lowertriangleselecter = lowertriangleselecter;
    }

    public void setLowerantitriangleselecter(LowerAntiTriangleSelecter lowerantitriangleselecter) {
        this.lowerantitriangleselecter = lowerantitriangleselecter;
    }

    public void setUpperantitriangleselecter(UpperAntiTriangleSelecter upperantitriangleselecter) {
        this.upperantitriangleselecter = upperantitriangleselecter;
    }

    public void setmChart(LineChart mChart) {
        this.mChart = mChart;
    }

    public Boolean getHasChanced() {
        return hasChanced;
    }

    public void setHasChanced(Boolean hasChanced) {
        this.hasChanced = hasChanced;
    }

    public Boolean getIsSolving() {
        return isSolving;
    }

    public void setIsSolving(Boolean isSolving) {
        this.isSolving = isSolving;
    }

    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            if (myMatrix.getZoom() == false) {
                for (int j = 0; j < 9; j++) {
                    if (x > BlockCornerCoords[2 * j] && x < BlockCornerCoords[2 * j] + blockSize && y > BlockCornerCoords[2 * j + 1] && y < BlockCornerCoords[2 * j + 1] + blockSize) {
                        myMatrix.setZoom(true);
                        myMatrix.setActivityMatrix(activityStructure);
                        myMatrix.zoomBlock(j);
                        zoomSelecter = 0;
                        diagselecter.invalidate();
                        antidiagselecter.invalidate();
                        lowerdiagselecter.invalidate();
                        upperdiagselecter.invalidate();
                        lowerantidiagselecter.invalidate();
                        upperantidiagselecter.invalidate();
                        lowertriangleselecter.invalidate();
                        uppertriangleselecter.invalidate();
                        lowerantitriangleselecter.invalidate();
                        upperantitriangleselecter.invalidate();
                        invalidate();
                    }
                }
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            if (myMatrix.getZoom() == false) {
                for (int j = 0; j < 9; j++) {
                    if (x > BlockCornerCoords[2 * j] && x < BlockCornerCoords[2 * j] + blockSize && y > BlockCornerCoords[2 * j + 1] && y < BlockCornerCoords[2 * j + 1] + blockSize) {
                        hasChanced = true;
                        if (myMatrix.getBlockActive()[j] == false) {
                            activityStructure = myMatrix.saveStructure();
                            myMatrix.selectBlock(j);
                            hasChanced = true;
                            if (!isSolving) {
                                computeButton.setVisibility(INVISIBLE);
                            }
                            invalidate();
                        } else if (myMatrix.getBlockActive()[j] == true) {
                            activityStructure = myMatrix.saveStructure();
                            myMatrix.unselectBlock(j);
                            if (!isSolving) {
                                computeButton.setVisibility(INVISIBLE);
                            }
                            hasChanced = true;
                            invalidate();
                        }
                    }
                }
            }
            return true;
        }

    }
}
