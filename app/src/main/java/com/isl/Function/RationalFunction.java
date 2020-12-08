package com.uni_stuttgart.isl.Function;

import android.util.Log;

import com.uni_stuttgart.isl.CoolStuff.NumLinAlg;
import com.uni_stuttgart.isl.Solver.Solver;

/**
 * Created by ms on 30.03.20.
 */

public class RationalFunction implements AnalyticFunction {

    int degree;
    double[] coeff;

    //Default-Konstruktor mit Grad 0
    public RationalFunction() {
        this.degree = 0;
        this.coeff = new double[1];
    }

    //Konstruktor mit spezifischem Grad
    public RationalFunction(int degree) {
        this.degree = degree;
        this.coeff = new double[2*degree + 2]; //first numerator;
    }

    //Konstruktor mit spezifischem Grad und Koeffizienten
    public RationalFunction(int degree, double[] coeff) {
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
        double numerator = coeff[0];
        double denominator = coeff[degree+1];
        for (int i = 0; i < degree; i++) {
            numerator = numerator + coeff[i + 1] * Math.pow(x,i+1);
            denominator = denominator + coeff[degree + i + 2] * Math.pow(x,i+1);
        }

        return numerator/denominator;
    }

    //Auswertung der Ableitung des Polynoms an Stelle x
    public double derivation(double x) {

        double value_n = coeff[degree];
        double deriv_n = coeff[degree];

        for (int i = degree; i > 1; i--) {
            value_n = value_n * x + coeff[i - 1];
            deriv_n = deriv_n * x + value_n;
        }

        double value_d = coeff[degree];
        double deriv_d = coeff[degree];

        for (int i = 2*degree+1; i > degree; i--) {
            value_d = value_d * x + coeff[i - 1];
            deriv_d = deriv_d * x + value_d;
        }

        value_n = coeff[0];
        value_d = coeff[degree];
        for (int i = 0; i < degree; i++) {
            value_n = value_n + coeff[i + 1] * Math.pow(x,i+1);
            value_d = value_d + coeff[degree + i + 1] * Math.pow(x,i+1);
        }

        return (deriv_n*value_d-value_n*deriv_d)/Math.pow(value_d,2);
    }



}