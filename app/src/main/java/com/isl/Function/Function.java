package com.uni_stuttgart.isl.Function;

/**
 * Created by nborg on 07.08.15.
 */
public interface Function {

    double evaluate(double x);

    double derivation(double x);

    double antiderivation(double x);

//    double max(double x, double y);

//    double min(double x, double y);
}
