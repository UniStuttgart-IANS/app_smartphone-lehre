package com.uni_stuttgart.isl.Function;

/**
 * Created by nborg on 12.12.17.
 */

public class NewtonDivergentFunction implements AnalyticFunction  {
    @Override
public double evaluate(double x) {
    return Math.signum(x)*Math.pow(Math.abs(x), 1.0/3.0);
}

    @Override
    public double derivation(double x) {
        return x*Math.signum(x)/(3*Math.pow(Math.abs(x), 5.0/3.0));
    }

    @Override
    public double antiderivation(double x) {
        return 0;
    }
}
