package com.uni_stuttgart.isl.Quadrature;


import com.uni_stuttgart.isl.Function.Function;

/**
 * Created by nborg on 07.08.15.
 */
public interface Quadrature {

    public double integrate(Function function, double a, double b);

    public double integrate(Function function, double a, double b, int n);

    public double integrate_LP(Function function, double a, double b, double p);

    public double integrate_LP(Function function, double a, double b, double p, int n);

    public double integrate_LP(Function function1, Function function2, double a, double b, double p);

    public double integrate_LP(Function function1, Function function2, double a, double b, double p, int n);

    public double integrate_infty(Function function1, Function function2, double a, double b, int n);
}
