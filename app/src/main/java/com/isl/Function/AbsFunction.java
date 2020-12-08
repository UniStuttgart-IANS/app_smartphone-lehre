package com.uni_stuttgart.isl.Function;

/**
 * Created by nborg on 19.10.15.
 */
public class AbsFunction implements AnalyticFunction {

    public double evaluate(double x) {
        return Math.abs(x);
    }

    public double derivation(double x) {
        return Math.signum(x);
    }

    public double antiderivation(double x){
        return  Math.signum(x) * 1.0/2.0 * Math.pow(x, 2);
    }
}
