package com.uni_stuttgart.isl.Function;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class findExtremum {

    public static double findMin(Function func, double a, double b) {

        int N = 500;
        if(a>b) {
            double tmp = a;
            a = b;
            b = tmp;
        }

        double dx = (b-a)/N;

        double res = func.evaluate(a);
        for(double i=a; i<=b ; i+= dx )
        {
            res = min(res, func.evaluate(i));
        }

        return res;
    }

    public static double findMax(Function func, double a, double b) {


        int N = 500;
        if(a>b) {
            double tmp = a;
            a = b;
            b = tmp;
        }

        double dx = (b-a)/N;

        double res = func.evaluate(a);
        for(double i=a; i<=b ; i+= dx )
        {
            res = max(res, func.evaluate(i));
        }

        return res;
    }
}
