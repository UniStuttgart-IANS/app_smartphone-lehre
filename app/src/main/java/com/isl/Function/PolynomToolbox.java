package com.uni_stuttgart.isl.Function;

/**
 * Created by nborg on 10.08.15.
 */
public class PolynomToolbox {

    public static double[] getEquiDistX(double start, double end, int n) {
        double[] xCoord = new double[n];

        for (int i = 0; i < n; i++) {
            xCoord[i] = start + (i * 1.0) / (n - 1) * (end - start);
        }
        if (n == 1) {
            xCoord[0] = start + (end - start) / 2.0;
        }

        return xCoord;
    }

    public static double[] getY(Function function, double[] xCoord) {
        double[] yCoord = new double[xCoord.length];

        for (int i = 0; i < xCoord.length; i++) {
            yCoord[i] = function.evaluate(xCoord[i]);
        }

        return yCoord;
    }

    public static double[] getTschebyX(double start, double end, int n) {
        double[] xCoord = new double[n];

        for (int i = 0; i < n; i++) {
            xCoord[i] = ((end - start) / 2.0) * Math.cos(((2 * i + 1) * Math.PI) / (2.0 * n)) + (start + end) / 2;
        }

        if (n == 1) {
            xCoord[0] = start + (end - start) / 2.0;
        }

        return xCoord;
    }


    public static double[] getLegendreCoords(double a, double b, int n){
        double[] xCoord = new double[n];

        switch (n) {
            case 1:
                xCoord[0] = 0;
                break;
            case 2:
                xCoord[0] = -Math.sqrt(1.0/3.0);
                xCoord[1] = -xCoord[0];
                break;
            case 3:
                xCoord[0] = -Math.sqrt(3.0/5.0);
                xCoord[1] = 0;
                xCoord[2] = -xCoord[0];
                break;
            case 4:
                xCoord[0] = -Math.sqrt((3.0/7.0+2.0/7.0*Math.sqrt(6.0/5.0)));
                xCoord[1] = -Math.sqrt((3.0/7.0-2.0/7.0*Math.sqrt(6.0/5.0)));
                xCoord[2] = -xCoord[1];
                xCoord[3] = -xCoord[0];
                break;
            case 5:
                xCoord[0] = -1.0/3.0*Math.sqrt((5+2.0*Math.sqrt(10.0/7.0)));
                xCoord[1] = -1.0/3.0*Math.sqrt((5-2.0*Math.sqrt(10.0/7.0)));
                xCoord[2] = 0;
                xCoord[3] = -xCoord[1];
                xCoord[4] = -xCoord[0];
                break;
            case 6:
                
                break;
            case 7:

                break;
            case 8:

                break;

        }

        for (int i = 0; i < n; i++) {
            xCoord[i] = (b-a)/2.0*xCoord[i]+(b+a)/2;
        }

        return xCoord;
    }
}
