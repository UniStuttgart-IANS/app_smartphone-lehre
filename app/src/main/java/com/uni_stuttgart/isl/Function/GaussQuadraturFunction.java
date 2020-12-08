package com.uni_stuttgart.isl.Function;

import com.uni_stuttgart.isl.Integration.CoordSystem;
import com.uni_stuttgart.isl.Points.Points;

/**
 * Created by nborg on 10.02.16.
 */
public class GaussQuadraturFunction implements AnalyticFunction {

    private int intervallNumber = 0;
    private Points points;
    private MonomialPolynom[] interpolate;
    private CoordSystem coordSystem;
    private double[] weights;

    //Default-Konstruktor mit Grad 0
    public GaussQuadraturFunction() {
    }

    public double antiderivation(double x) {
        return 0;
    }

    public double evaluate(double x) {
        double value = 0;

        setIntervallNumber(x);

//        Log.e("Test", "Intervallnummer: " + this.getIntervallNumber());

        if (intervallNumber == -1) {
            intervallNumber = points.getPoint(points.getCount() - 1).getPosition();
        }

        value = interpolate[intervallNumber].evaluate(x);

        return value;
    }

    public double derivation(double x) {
        double value = 0;

        setIntervallNumber(x);

        value = interpolate[intervallNumber].derivation(x);

        return value;
    }

    public double integral(double start, double end) {
        setIntervallNumber(1.0 / 2.0 * (start + end));

        return interpolate[intervallNumber].integralSimpson(start, end);
    }

    public double gaussQuadratur(int n){
        double result = 0, a, b;
        int current, next;

        current = points.getFirstPointNumber();
        next = points.getNextPointNumber(points.getFirstPointNumber());

        for (int j = 0; j < points.getNOPP()-1; j++) {
            a = points.getPoint(current).getReelX();
            b = points.getPoint(next).getReelX();
            getWeights(n, b-a);
            for (int i = 0; i < n; i++) {
                result = result + weights[i]*coordSystem.getFunction().evaluate(PolynomToolbox.getLegendreCoords(a,b,n)[i]);
            }
            current = next;
            next = points.getNextPointNumber(current);
        }

        return result;
    }

//    public MonomialPolynom[] gaussQuadraturInterpolate(int degree) {
//        int dim = degree + 1;
//        double xCoord[];
//        double[][] A = new double[dim][dim];
//        double[] b = new double[dim];
//        int current = points.getFirstPointNumber();
//        int next = points.getNextPointNumber(current);
//
//        interpolate = new MonomialPolynom[points.getNOPP() - 1];
//
//        for (int i = 0; i < points.getNOPP() - 1; i++) {
//            interpolate[i] = new MonomialPolynom(degree);
//        }
//
//        for (int k = 0; k < points.getNOPP() - 1; k++) {
//
//            xCoord = PolynomToolbox.getTschebyX(points.getPoint(current).getReelX(), points.getPoint(next).getReelX(), dim);
//
//            for (int i = 0; i < dim; i++) {
//                A[i][0] = 1;
//                for (int j = 1; j < dim; j++) {
//                    A[i][j] = A[i][j - 1] * xCoord[i];
//                }
//                b[i] = coordSystem.getFunction().evaluate(xCoord[i]);
//            }
//
//            Solver solver = new Solver();
//            solver.setDim(dim);
//            solver.setA(A);
//            solver.setB(b);
//            solver.gauss();
//
////            double[] temp = new double[dim];
////            for (int z = 0; z < dim; z++) {
////                temp[z] = solver.getX()[dim-z-1];
////            }
//
//            interpolate[k].setCoeff(solver.getX());
//
//            current = next;
//            next = points.getNextPointNumber(current);
//        }
//
//        return interpolate;
//    }

    public void setPoints(Points points) {
        this.points = points;
    }

    public int getIntervallNumber() {
        return this.intervallNumber;
    }

    public void setIntervallNumber(double x) {
        int current;
        int next;

        if (x <= points.getPoint(points.getFirstPointNumber()).getReelX()) {
            intervallNumber = 0;
            return;
        } else if (x >= points.getPoint(points.getLastPointNumber()).getReelX()) {
            intervallNumber = points.getNOPP() - 2;
            return;
        }

        for (int i = 0; i < points.getNOPP() - 1; i++) {
            current = points.getFirstPointNumber();
            next = points.getNextPointNumber(points.getFirstPointNumber());

            for (int j = 0; j < points.getNOPP() - 1; j++) {
                if (x >= points.getPoint(current).getReelX() && x < points.getPoint(next).getReelX()) {
                    intervallNumber = points.getPoint(current).getPosition();
                    return;
                }
                current = next;
                next = points.getNextPointNumber(next);
            }
        }
    }

    public CoordSystem getCoordSystem() {
        return coordSystem;
    }

    public void setCoordSystem(CoordSystem coordSystem) {
        this.coordSystem = coordSystem;
    }

    public MonomialPolynom[] getInterpolate() {
        return interpolate;
    }

    public double[] getWeights(int n, double distance) {
        weights = new double[n];

        switch (n) {
            case 1:
                weights[0] = 2;
                break;
            case 2:
                weights[0] = 1;
                weights[1] = 1;
                break;
            case 3:
                weights[0] = 5.0/9.0;
                weights[1] = 8.0/9.0;
                weights[2] = weights[0];
                break;
            case 4:
                weights[0] = (18.0-Math.sqrt(30))/36.0;
                weights[1] = (18.0+Math.sqrt(30))/36.0;
                weights[2] = weights[1];
                weights[3] = weights[0];
                break;
            case 5:
                weights[0] = (322.0-13.0*Math.sqrt(70))/900.0;
                weights[1] = (322.0+13.0*Math.sqrt(70))/900.0;
                weights[2] = 128.0/225.0;
                weights[3] = weights[1];
                weights[4] = weights[0];
                break;
            case 6:
//                1 -0.932469	0.171324
//                2	-0.661209	0.360762
//                3	-0.238619	0.467914
//                4	 0.238619	0.467914
//                5	 0.661209	0.360762
//                6	 0.932469	0.171324
                break;
            case 7:
//                1	-0.949108	0.129485
//                2	-0.741531	0.279705
//                3	-0.405845	0.38183
//                4	 0.0    	0.417959
//                5	 0.405845	0.38183
//                6	 0.741531	0.279705
//                7	 0.949108	0.129485
                break;
            case 8:
//                1	-0.96029	0.101229
//                2	-0.796667	0.222381
//                3	-0.525532	0.313707
//                4	-0.183435	0.362684
//                5	 0.183435	0.362684
//                6	 0.525532	0.313707
//                7	 0.796667	0.222381
//                8	 0.96029	0.101229
                break;
            case 9:
//                1	-0.96816	0.0812744
//                2	-0.836031	0.180648
//                3	-0.613371	0.260611
//                4	-0.324253	0.312347
//                5	 0.0	    0.330239
//                6	 0.324253	0.312347
//                7	 0.613371	0.260611
//                8	 0.836031	0.180648
//                9	 0.96816	0.0812744
                break;
            case 10:
//                1	-0.973907	0.0666713
//                2	-0.865063	0.149451
//                3	-0.67941	0.219086
//                4	-0.433395	0.269267
//                5	-0.148874	0.295524
//                6	0.148874	0.295524
//                7	0.433395	0.269267
//                8	0.67941 	0.219086
//                9	0.865063	0.149451
//                10	0.973907	0.0666713
                break;

        }

        for (int i = 0; i < n; i++) {
            weights[i] = weights[i] * distance / 2.0;
        }

        return weights;
    }
}
