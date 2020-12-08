package com.uni_stuttgart.isl.Function;

import android.util.Log;

import com.uni_stuttgart.isl.CoolStuff.NumLinAlg;
import com.uni_stuttgart.isl.Solver.Solver;

/**
 * Created by nborg on 07.08.15.
 */

public class MonomialPolynom implements PolynomialFunction {

    int degree;
    double[] coeff;

    //Default-Konstruktor mit Grad 0
    public MonomialPolynom() {
        this.degree = 0;
        this.coeff = new double[1];
    }

    //Konstruktor mit spezifischem Grad
    public MonomialPolynom(int degree) {
        this.degree = degree;
        this.coeff = new double[degree + 1];
    }

    //Konstruktor mit spezifischem Grad und Koeffizienten
    public MonomialPolynom(int degree, double[] coeff) {
        this.degree = degree;
        this.coeff = coeff.clone();
    }

    public void setDegree(int degree) {
        this.degree = degree;
        this.coeff = new double[degree + 1];
    }

    public double antiderivation(double x){
        return 0;
    }

    public double evaluate(double x) {
        double value = coeff[0];

        for (int i = 0; i < degree; i++) {
            value = value + coeff[i + 1] * Math.pow(x,i+1);
        }

        return value;
    }

    //Auswertung der Ableitung des Polynoms an Stelle x
    public double derivation(double x) {

        double value = coeff[degree];
        double deriv = coeff[degree];

        for (int i = degree; i > 1; i--) {
            value = value * x + coeff[i - 1];
            deriv = deriv * x + value;
        }

        return deriv;
    }

    //Bestimme das Interpolationspolynom (Monombasis) zu den vorgegebenen Werten mit Polynomgrad degree
    public void interpolate(double[] X, double[] Y) {

        if (X.length != Y.length) {
            Log.d("Debug", "Number of X- and Y-values does not match!");
            return;
        } else if (X.length > degree + 1 || Y.length > degree + 1) {
            Log.d("Debug", "Dimension of X- or Y-Values does not match the polynomial degree!");
            return;
        }

        //Bei zu wenig Stützstellen wird mit niedrigerem Grad interpoliert
        double[][] mat = new double[X.length][X.length];
        degree = X.length - 1;

        for (int i = 0; i < X.length; i++) {
            mat[i][0] = 1;
            for (int j = 1; j < X.length; j++) {
                mat[i][j] = mat[i][j - 1] * X[i];
            }
        }

        //Löst das LGS
        Solver solver = new Solver();
        solver.setDim(X.length);
        solver.setA(mat);
        solver.setB(Y);
        solver.gauss();
        coeff = solver.getX().clone();
    }

    public void lsApproximation(double[] xCoord, double[] yCoord) {

        //Bei zu wenig Stützstellen wird mit niedrigerem Grad interpoliert
        double[][] mat = new double[xCoord.length][degree + 1];
        double[][] lsmat = new double[degree + 1][degree + 1];
        double[] lsRS = new double[degree + 1];

        for (int i = 0; i < xCoord.length; i++) {
            mat[i][0] = 1;
            //Log.d("Debug", "Mat("+i+","+0+") = " + mat[i][0]);
            for (int j = 1; j < degree + 1; j++) {
                mat[i][j] = mat[i][j - 1] * xCoord[i];
                //Log.d("Debug", "Mat("+i+","+j+") = " + mat[i][j]);
            }
        }

        lsmat = NumLinAlg.MatTMat(mat, mat);
        lsRS = NumLinAlg.MatTVec(mat, yCoord);

        Solver solver = new Solver();
        solver.setDim(degree + 1);
        solver.setA(lsmat);
        solver.setB(lsRS);
        solver.gauss();
        coeff = solver.getX().clone();
    }

    public double[] getCoeff() {
        return coeff;
    }

    //Setzt Koeffizienten des Polynoms in Monomdarstellung
    public void setCoeff(double[] coeff) {
        this.coeff = coeff.clone();
    }

    public double integralSimpson(double start, double end) {
        double antideriv = 0;

        antideriv = 1.0/3.0* coeff[0] *(Math.pow(end,3) - Math.pow(start,3)) + 1.0/2.0* coeff[1] *(Math.pow(end,2) - Math.pow(start,2)) + coeff[2] *(Math.pow(end,1) - Math.pow(start,1));

        return antideriv;
    }
}