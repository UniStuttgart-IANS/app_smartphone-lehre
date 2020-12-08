package com.uni_stuttgart.isl.Function;

/**
 * Created by nborg on 07.08.15.
 */
public class TrigonomialFunction implements AnalyticFunction {

    double a;
    double b;
    double alpha;
    double beta;

    public TrigonomialFunction(double a, double alpha, double b, double beta) {
        this.a = a;
        this.b = b;
        this.alpha = alpha;
        this.beta = beta;
    }

    public TrigonomialFunction(double a, double b) {
        this.a = a;
        this.b = b;
        this.alpha = 1;
        this.beta = 1;
    }

    public double evaluate(double x) {

        return a * Math.sin(alpha * x) + b * Math.cos(beta * x);
    }

    public double derivation(double x) {
        return alpha * a * Math.cos(alpha * x) - beta * b * Math.sin(beta * x);
    }

    public double antiderivation(double x) {
        if (beta == 0 && alpha != 0){
            return -a * 1.0 / alpha * Math.cos(alpha * x) + b * x;
        }
        if (alpha == 0 && beta != 0){
            return b * 1.0 / beta * Math.sin(beta * x);
        }
        if (alpha == 0 && beta == 0){
            return b * x;
        }
        else {
            return -a * 1.0 / alpha * Math.cos(alpha * x) + b * 1.0 / beta * Math.sin(beta * x);
        }
    }

}