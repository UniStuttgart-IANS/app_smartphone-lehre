package com.uni_stuttgart.isl.GaussianSolver;

import android.graphics.Paint;

public class Row {

    // Jeder Zeile hat eine y-Koordinate, die Anzahl der Objekte (Einträge der Matrix + Rechte Seite), ein Paint, eine Nummer ...
    private double yCoord;
    private int dimAB;
    private int dimA;
    private Paint paint;
    private int number;


    public Row(double yCoord) {
        this.yCoord = yCoord;
    }
    public Row(double yCoord, int dimA) {
        this.yCoord = yCoord;
        this.dimA = dimA;
    }
    public Row(double yCoord, int dimA, int dimAB) {
        this.yCoord = yCoord;
        this.dimA = dimA;
        this.dimAB = dimAB;
    }

    public Row(double yCoord, int dimA, int dimAB, Paint paint) {
        this.yCoord = yCoord;
        this.dimA = dimA;
        this.dimAB = dimAB;
        this.paint = paint;
    }

    public Row(double yCoord, int dimA, int dimAB, int number, Paint paint) {
        this.yCoord = yCoord;
        this.dimA = dimA;
        this.dimAB = dimAB;
        this.paint = paint;
        this.number = number;
    }

    public double getyCoord() {
        return yCoord;
    }

    public void setyCoord(double yCoord) {
        this.yCoord = yCoord;
    }

    public int getDimAB() {
        return dimAB;
    }

    public void setDimAB(int dimAB) {
        this.dimAB = dimAB;
    }

    public int getDimA() {
        return dimA;
    }

    public void setDimA(int dimA) {
        this.dimA = dimA;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
