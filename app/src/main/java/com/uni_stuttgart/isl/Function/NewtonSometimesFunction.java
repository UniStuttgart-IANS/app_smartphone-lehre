package com.uni_stuttgart.isl.Function;

/**
 * Created by nborg on 12.12.17.
 */

public class NewtonSometimesFunction implements AnalyticFunction  {
    @Override
    public double evaluate(double x) {
        double temp = 0;
        if (Math.abs(x+3) < 1e-3) {
            temp = 0;
        }
        else {
            temp = Math.log(Math.abs(x+3));
        }
        return temp;
    }

    @Override
    public double derivation(double x) {
        double temp = 0;
        if (Math.abs(x+3) < 1e-3) {
            temp = 100.0;
        }
        else {
            temp = Math.signum(x+3) / (x+3);
        }
        return temp;
    }

    @Override
    public double antiderivation(double x) {
        return 0;
    }
}
