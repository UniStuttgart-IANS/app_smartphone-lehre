package com.uni_stuttgart.isl.Splittingsolver;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by nborg on 04.03.16.
 */
public class Entry {
    private double value;
    private int[] position = new int[2];
    private boolean status;
    private int blockNumber;
    private Paint pointPaint;
    private Paint textPaint;
    private int[] pointPosition = new int[2];

    public Entry(double value) {
        this.value = value;
        this.pointPaint = new Paint();
        this.pointPaint.setAntiAlias(true);
        this.pointPaint.setStyle(Paint.Style.FILL);
        this.pointPaint.setStrokeJoin(Paint.Join.ROUND);
        this.pointPaint.setStrokeWidth(10F);
        this.pointPaint.setColor(Color.WHITE);

        this.textPaint = new Paint();
        this.textPaint.setAntiAlias(true);
        this.textPaint.setStyle(Paint.Style.FILL);
        this.textPaint.setStrokeJoin(Paint.Join.ROUND);
        this.textPaint.setStrokeWidth(1F);
        this.textPaint.setColor(Color.WHITE);
        this.textPaint.setTextSize(12);
    }

    public Entry(double value, boolean status) {
        this.value = value;
        this.status = status;
        this.pointPaint = new Paint();
        this.pointPaint.setAntiAlias(true);
        this.pointPaint.setStyle(Paint.Style.STROKE);
        this.pointPaint.setStrokeJoin(Paint.Join.ROUND);
        this.pointPaint.setStrokeWidth(5F);
        this.pointPaint.setColor(Color.WHITE);

        this.textPaint = new Paint();
        this.textPaint.setAntiAlias(true);
        this.textPaint.setStyle(Paint.Style.FILL);
        this.textPaint.setStrokeJoin(Paint.Join.ROUND);
        this.textPaint.setStrokeWidth(1F);
        this.textPaint.setColor(Color.WHITE);
        this.textPaint.setTextSize(12);
    }

    public Entry(double value, boolean status, int[] position) {
        this.value = value;
        this.status = status;
        this.position = position.clone();
        this.pointPaint = new Paint();
        this.pointPaint.setAntiAlias(true);
        this.pointPaint.setStyle(Paint.Style.STROKE);
        this.pointPaint.setStrokeJoin(Paint.Join.ROUND);
        this.pointPaint.setStrokeWidth(5F);
        this.pointPaint.setColor(Color.WHITE);

        this.textPaint = new Paint();
        this.textPaint.setAntiAlias(true);
        this.textPaint.setStyle(Paint.Style.FILL);
        this.textPaint.setStrokeJoin(Paint.Join.ROUND);
        this.textPaint.setStrokeWidth(1F);
        this.textPaint.setColor(Color.WHITE);
        this.textPaint.setTextSize(12);
    }

    public void setSelected() {
        this.pointPaint.setColor(Color.BLACK);
        this.textPaint.setColor(Color.BLACK);
        this.status = true;
    }

    public void setUnselected() {
        this.pointPaint.setColor(Color.WHITE);
        this.textPaint.setColor(Color.WHITE);
        this.status = false;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int[] getPosition() {
        return position;
    }

    public void setPosition(int[] position) {
        this.position = position;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Paint getPointPaint() {
        return pointPaint;
    }

    public void setPointPaint(Paint pointPaint) {
        this.pointPaint = pointPaint;
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(int blockNumber) {
        this.blockNumber = blockNumber;
    }

    public int[] getPointPosition() {
        return pointPosition;
    }

    public void setPointPosition(int[] pointPosition) {
        this.pointPosition = pointPosition;
    }

    public Paint getTextPaint() {
        return textPaint;
    }
}
