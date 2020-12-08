package com.uni_stuttgart.isl.GaussianSolver;

import android.graphics.Paint;
/*

 2 x 2 Matrizen

 5  1  10
 7 -1  2

 -4  3   6
  3 -6   3

  1 -1   7
  3  2   11

  5 -3   24
 -2  1   9

  2 -2   14
  3  2   11


 3 x 3 Matrizen
7   3   -5    -12
-1  -2  4       5
-4  1   -3      1

2   -3  4       8
3   4   -5      -4
4   -6  3       1

//mit Pivotierung (die folgende)
3   2   -4      -2
4   -5  3       9
8   7   -9      13

6   -1  2       1
5   -3  3       4
3   -2  1       14

4   3   1       13
2   -5  3       1
7   -1  -2      -1
 */

/* 4 x 4 Matrizen
    0    10     0    12     -6
    4    -5    -2    -8    -17
    0    -5     0    -4    -23
    4   -10     0   -16      2

    1   1   1   1   4
    1   2   4   8   2
    1   4   16  64  4
    1   5   25  125 20

    1   1   1   1      -5.5
    1  -1   1  -1      4.5
    1  -2   4  -8      8
    1  -3   9  -27     2.5

    1   -1  1   -1      -16
    1    2  4    8       11
    1    4  16   64     -11
    1    6  36   216    -9

    1   -1  1   -1      7
    1   -2  4   -8      6
    1    3  9    27     1
    1   -3  9   -27    -2

    5 x 5 Matrizen
    1   2   4   3   5   5
    3   5   3   1   2   6
    1   4   4   2   1   7
    4   1   2   5   3   8
    5   2   1   4   1   9

    4   1    2  -3   5
   -3   3   -1   4  -2
   -1   2    5   1   3
    5   4    3  -1   2
    1  -2    3  -4   5
   -16  20  -4  -10  3


 */

public class Entry {

    // Jeder Eintrag in der Matrix hat einen Wert, eine Position und eine Radius für die Größe des Kreises um die Zahl
    private double value;
    private double xCoord;
    private double yCoord;
    private double rad;

    // Evtl. ist es noch sinnvoll einen eigenen Paint zu definieren, noch unklar, ob nötig. Vllt kann man bei einer Null im LGS die Zahl dann grün färben oder so, mal sehen...
    private Paint paint;

    public Entry(double value) {
        this.value = value;
    }

    public Entry(double value, double xCoord, double yCoord) {
        this.value = value;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    public Entry(double value, double xCoord, double yCoord, double rad) {
        this.value = value;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.rad = rad;
    }

    public Entry(double value, double xCoord, double yCoord, double rad, Paint paint) {
        this.value = value;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.rad = rad;
        this.paint = paint;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getxCoord() {
        return xCoord;
    }

    public void setxCoord(double xCoord) {
        this.xCoord = xCoord;
    }

    public double getyCoord() {
        return yCoord;
    }

    public void setyCoord(double yCoord) {
        this.yCoord = yCoord;
    }

    public double getRad() {
        return rad;
    }

    public void setRad(double rad) {
        this.rad = rad;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }
}
