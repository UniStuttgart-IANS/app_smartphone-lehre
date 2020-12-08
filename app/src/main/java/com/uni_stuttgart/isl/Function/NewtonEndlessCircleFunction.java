package com.uni_stuttgart.isl.Function;

/**
 * Created by nborg on 10.07.17.
 */

public class NewtonEndlessCircleFunction implements AnalyticFunction {
    @Override
    public double evaluate(double x) {
        return Math.signum(x)*Math.sqrt(Math.abs(x));
    }

    @Override
    public double derivation(double x) {
        return x*Math.signum(x)/(2*Math.pow(Math.abs(x), 3.0/2.0));
    }

    @Override
    public double antiderivation(double x) {
        return 0;
    }
}
