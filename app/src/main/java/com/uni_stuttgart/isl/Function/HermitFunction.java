package com.uni_stuttgart.isl.Function;

import com.uni_stuttgart.isl.Interpolation.CoordSystem;
import com.uni_stuttgart.isl.Points.Points;
import com.uni_stuttgart.isl.Solver.Solver;

/**
 * Created by nborg on 10.02.16.
 */
public class HermitFunction implements AnalyticFunction {

    private int intervallNumber = 0;
    private Points points;
    private MonomialPolynom[] interpolate;
    private CoordSystem coordSystem;

    //Default-Konstruktor mit Grad 0
    public HermitFunction() {
    }

    public double antiderivation(double x){
        return 0;
    }

    public double evaluate(double x) {
        double value = 0;

        setIntervallNumber(x);

//        Log.e("Test", "Intervallnummer: " + this.getIntervallNumber());

//        Log.e("Test", "Coeff_3: " + interpolate[0].getCoeff()[3]);
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

    public MonomialPolynom[] hermitinterpolate() {
        interpolate = new MonomialPolynom[points.getNOPP() - 1];

        for (int i = 0; i < points.getNOPP() - 1; i++) {
            interpolate[i] = new MonomialPolynom(3);
        }

        double[][] A = new double[4][4];
        double[] b = new double[4];
        double[] coeff = new double[4];
        double start_x, end_x;
        int current = points.getFirstPointNumber();

        for (int i = 0; i < points.getNOPP() - 1; i++) {
//            Log.e("Test", "Current: " + current);
//            Log.e("Test", "Position: " + points.getPoint(current).getPosition());
//            Log.e("Test", "Next: " + points.getNextPointNumber(current));
//
//            for (int j = 0; j < points.getCount(); j++) {
//                Log.e("Test", j + ": " + points.getPoint(j).getPosition());
//
//            }

            start_x = points.getPoint(current).getReelX();
            end_x = points.getPoint(points.getNextPointNumber(current)).getReelX();

            A[0][0] = Math.pow(start_x, 0);
            A[0][1] = Math.pow(start_x, 1);
            A[0][2] = Math.pow(start_x, 2);
            A[0][3] = Math.pow(start_x, 3);

            A[1][0] = Math.pow(end_x, 0);
            A[1][1] = Math.pow(end_x, 1);
            A[1][2] = Math.pow(end_x, 2);
            A[1][3] = Math.pow(end_x, 3);

            A[2][0] = 0;
            A[2][1] = 1;
            A[2][2] = 2 * Math.pow(start_x, 1);
            A[2][3] = 3 * Math.pow(start_x, 2);

            A[3][0] = 0;
            A[3][1] = 1;
            A[3][2] = 2 * Math.pow(end_x, 1);
            A[3][3] = 3 * Math.pow(end_x, 2);

            b[0] = coordSystem.getFunction().evaluate(start_x);
            b[1] = coordSystem.getFunction().evaluate(end_x);
            b[2] = coordSystem.getFunction().derivation(start_x);
            b[3] = coordSystem.getFunction().derivation(end_x);

            Solver solver = new Solver();
            solver.setDim(4);
            solver.setA(A);
            solver.setB(b);
            solver.gauss();

            interpolate[i].setCoeff(solver.getX());

            current = points.getNextPointNumber(current);
        }

        return interpolate;
    }

    public void setPoints(Points points) {
        this.points = points;
    }

    public int getIntervallNumber() {
        return this.intervallNumber;
    }

    public void setIntervallNumber(double x) {
        this.intervallNumber = -1;

        // Schleife über alle Punkte
        for (int i = 0; i < points.getCount() - 1; i++) {
            if (x <= points.getPoint(points.getFirstPointNumber()).getReelX()) {
                intervallNumber = 0;
            } else if (x >= points.getPoint(points.getLastPointNumber()).getReelX()) {
                intervallNumber = points.getNOPP() - 2;
            }
            // Berücksicht werden nur Punkte die "placed" sind
            else if (points.getPoint(i).getIsPlaced()) {
//                if (points.getNextPointNumber(i) != -2 ){
                if (x >= points.getPoint(i).getReelX() && x <= points.getPoint(points.getNextPointNumber(i)).getReelX()) {
                    intervallNumber = points.getPoint(i).getPosition();
                }
//                }
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
}
