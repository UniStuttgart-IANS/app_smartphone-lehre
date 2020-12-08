package com.uni_stuttgart.isl.Function;

/**
 * Created by nborg on 07.08.15.
 */
public interface PolynomialFunction extends Function {

    //Bestimme das Interpolationspolynom zu den vorgegebenen Werten mit Polynomgrad des Objektes
    public void interpolate(double[] X, double[] Y);
}
