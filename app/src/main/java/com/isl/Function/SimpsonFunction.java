package com.uni_stuttgart.isl.Function;

import com.uni_stuttgart.isl.Integration.CoordSystem;
import com.uni_stuttgart.isl.Points.Points;
import com.uni_stuttgart.isl.Solver.Solver;

/**
 * Created by nborg on 10.02.16.
 */
public class SimpsonFunction implements AnalyticFunction {

    private int intervallNumber = 0;
    private Points points;
    private MonomialPolynom[] interpolate;
    private CoordSystem coordSystem;

    //Default-Konstruktor mit Grad 0
    public SimpsonFunction() {
    }

    public double antiderivation(double x){
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
        setIntervallNumber(1.0/2.0*(start + end));

        return interpolate[intervallNumber].integralSimpson(start, end);
    }


    public MonomialPolynom[] simpsonInterpolate() {
        interpolate = new MonomialPolynom[points.getNOPP() - 1];

        for (int i = 0; i < points.getNOPP() - 1; i++) {
            interpolate[i] = new MonomialPolynom(2);
        }

        double[][] A = new double[3][3];
        double[] b = new double[3];
        double start_x, end_x, middel_x;
        int current = points.getFirstPointNumber();

        for (int i = 0; i < points.getNOPP() - 1; i++) {

            start_x = points.getPoint(current).getReelX();
            end_x = points.getPoint(points.getNextPointNumber(current)).getReelX();
            middel_x = 1.0/2.0 * (start_x+end_x);

            A[0][0] = Math.pow(start_x, 0);
            A[0][1] = Math.pow(start_x, 1);
            A[0][2] = Math.pow(start_x, 2);

            A[1][0] = Math.pow(middel_x, 0);
            A[1][1] = Math.pow(middel_x, 1);
            A[1][2] = Math.pow(middel_x, 2);

            A[2][0] = Math.pow(end_x, 0);
            A[2][1] = Math.pow(end_x, 1);
            A[2][2] = Math.pow(end_x, 2);


            b[0] = coordSystem.getFunction().evaluate(start_x);
            b[1] = coordSystem.getFunction().evaluate(middel_x);
            b[2] = coordSystem.getFunction().evaluate(end_x);

            Solver solver = new Solver();
            solver.setDim(3);
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
        int current;
        int next;

        if (x <= points.getPoint(points.getFirstPointNumber()).getReelX()) {
            intervallNumber = 0;
            return;
        } else if (x >= points.getPoint(points.getLastPointNumber()).getReelX()) {
            intervallNumber = points.getNOPP() - 2;
            return;
        }

        for (int i = 0; i < points.getNOPP()-1; i++) {
            current = points.getFirstPointNumber();
            next = points.getNextPointNumber(points.getFirstPointNumber());

            for (int j = 0; j < points.getNOPP()-1; j++) {
                if (x >= points.getPoint(current).getReelX() && x < points.getPoint(next).getReelX()){
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
}
