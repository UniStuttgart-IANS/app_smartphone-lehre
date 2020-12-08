package com.uni_stuttgart.isl.Function;

import com.uni_stuttgart.isl.CoolStuff.NumLinAlg;
import com.uni_stuttgart.isl.Solver.Solver;

/**
 * Created by nborg on 07.08.15.
 */

public class NewtonPolynom implements PolynomialFunction {

    int degree;
    double[] coeff;
    double[] xCoord;

    //Default-Konstruktor mit Grad 0
    public NewtonPolynom() {
        this.degree = 0;
        this.coeff = new double[1];
    }

    //Konstruktor mit spezifischem Grad
    public NewtonPolynom(int degree) {
        this.degree = degree;
        this.coeff = new double[degree + 1];
    }

    public double antiderivation(double x){
        return 0;
    }

    //Auswertung des Polynoms an Stelle x
    public double evaluate(double x) {

        double value = coeff[degree];

        for (int i = degree - 1; i >= 0; i--) {
            value = value * (x - xCoord[i]) + coeff[i];
        }

        return value;
    }

    //Auswertung der Ableitung des Polynoms mit dem einseitigen Differenzenquotioenten (vorwärts)
    public double derivation(double x) {

        double tmp1 = evaluate(x);
        double tmp2 = evaluate(x + 1e-8);

        return (tmp2 - tmp1) / 1e-8;
    }

    //Bestimme das Interpolationspolynom (Monombasis) zu den vorgegebenen Werten mit Polynomgrad degree
    public void interpolate(double[] xCoord, double[] yCoord) {

        if (xCoord.length != yCoord.length) {
            throw new IllegalArgumentException("Number of X- and Y-values does not match!");
        } else if (xCoord.length > degree + 1 || yCoord.length > degree + 1) {
            throw new IllegalArgumentException("Dimension of X- or Y-Values does not match the polynomial degree!");
        }

        //Bei zu wenig Stützstellen wird mit niedrigerem Grad interpoliert
        double[][] mat = new double[xCoord.length][xCoord.length];
        this.xCoord = xCoord.clone();

        for (int i = 0; i < xCoord.length; i++) {
            mat[i][0] = 1;
            for (int j = 1; j < i + 1; j++) {
                mat[i][j] = mat[i][j - 1] * (xCoord[i] - xCoord[j - 1]);
            }
        }

        //Löst das LGS
        Solver solver = new Solver();
        solver.setDim(yCoord.length);
        solver.setA(mat);
        solver.setB(yCoord);
        solver.gauss();
        coeff = solver.getX().clone();
    }


    public void lsApproximation(double[] xcoord, double[] ycoord) {

        //Bei zu wenig Stützstellen wird mit niedrigerem Grad interpoliert
        double[][] mat = new double[xcoord.length][degree + 1];
        double[][] lsmat = new double[degree + 1][degree + 1];
        double[] lsRS = new double[degree + 1];

        this.xCoord = new double[degree];

        for (int i = 0; i < degree; i++) {
            this.xCoord[i] = xcoord[i];
        }

        for (int i = 0; i < xcoord.length; i++) {
            mat[i][0] = 1;
            for (int j = 1; j < Math.min(i + 1, degree + 1); j++) {
                mat[i][j] = mat[i][j - 1] * (xcoord[i] - xcoord[j - 1]);
            }
        }

        lsmat = NumLinAlg.MatTMat(mat, mat);
        lsRS = NumLinAlg.MatTVec(mat, ycoord);

        Solver solver = new Solver();
        solver.setDim(degree + 1);
        solver.setA(lsmat);
        solver.setB(lsRS);
        solver.gauss();
        coeff = solver.getX().clone();
    }
}
