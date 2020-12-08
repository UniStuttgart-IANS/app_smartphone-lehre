package com.uni_stuttgart.isl.Function;

/**
 * Created by nborg on 11.08.15.
 */
public class RungeFunction implements AnalyticFunction {

    public double evaluate(double x) {
        return 1.0 / (1 + Math.pow(x, 2));
    }

    public double derivation(double x) {
        return -2.0 * x / Math.pow(1 + Math.pow(x, 2), 2);
    }

    public double antiderivation(double x){
        return Math.atan(x);
    }

}
