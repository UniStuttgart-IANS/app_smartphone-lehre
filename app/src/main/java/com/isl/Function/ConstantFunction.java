package com.uni_stuttgart.isl.Function;

/**
 * Created by nborg on 07.08.15.
 */
public class ConstantFunction implements AnalyticFunction {

    double val;

    public ConstantFunction(double C) {
        val = C;
    }

    public double evaluate(double x) {
        return val;
    }

    public double derivation(double x) {
        return 0;
    }

    public double antiderivation(double x){
        return x;
    }
}
