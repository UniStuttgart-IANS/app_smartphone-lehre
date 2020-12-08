package com.uni_stuttgart.isl.Quadrature;

import android.util.Log;

import com.uni_stuttgart.isl.Function.Function;


/**
 * Created by nborg on 07.08.15.
 */
public class NewtonCotes implements Quadrature {

    private int degree;
    private double[][] nodes = {{0, 1, 0, 0, 0, 0, 0}, {0, 1.0 / 2.0, 1, 0, 0, 0, 0}, {0, 1.0 / 3.0, 2.0 / 3.0, 1, 0, 0, 0}, {0, 1.0 / 4.0, 2.0 / 4.0, 3.0 / 4.0, 1, 0, 0}, {0, 1.0 / 5.0, 2.0 / 5.0, 3.0 / 5.0, 4.0 / 5.0, 1, 0}, {0, 1.0 / 6.0, 3.0 / 6.0, 3.0 / 6.0, 4.0 / 6.0, 5.0 / 6.0, 0}};
    private double[][] weights = {{1.0 / 2.0, 1.0 / 2.0, 0, 0, 0, 0, 0}, {1.0 / 6.0, 4.0 / 6.0, 1.0 / 6.0, 0, 0, 0, 0}, {1.0 / 8.0, 3.0 / 8.0, 3.0 / 8.0, 1.0 / 8.0, 0, 0, 0}, {7.0 / 90.0, 32.0 / 90.0, 12.0 / 90.0, 32.0 / 90.0, 7.0 / 90.0, 0, 0}, {19.0 / 288.0, 75.0 / 288.0, 50.0 / 288.0, 50.0 / 288.0, 75.0 / 288.0, 19 / 288.0, 0}, {41.0 / 840.0, 216.0 / 840.0, 27.0 / 840.0, 272.0 / 840.0, 27.0 / 840.0, 216.0 / 840.0, 41.0 / 840.0}};


    public NewtonCotes(int degree) {
        if (degree > 6) {
            Log.d("Debug", "Ordnung der Newton-Cotes-Formel zu gross");
        }
        this.degree = degree;
    }


    public double integrate(Function function, double a, double b) {
        return integrate(function, a, b, 1);
    }

    public double integrate(Function function, double a, double b, int n) {
        double result = 0;
        for (int i = 0; i < n; i++) {
            double start = a + (b - a) / n * i;
            double end = a + (b - a) / n * (i + 1);
            for (int j = 0; j <= degree; j++) {
                result = result + weights[degree - 1][j] * function.evaluate(start + (end - start) * nodes[degree - 1][j]);
            }
        }
        return (b - a) / n * result;
    }

    public double integrate_LP(Function function, double a, double b, double p) {
        return integrate_LP(function, a, b, p, 1);
    }

    public double integrate_LP(Function function, double a, double b, double p, int n) {
        double result = 0;
        for (int i = 0; i < n; i++) {
            double start = a + (b - a) / n * i;
            double end = a + (b - a) / n * (i + 1);
            for (int j = 0; j <= degree; j++) {
                result = result + weights[degree - 1][j] * Math.pow(Math.abs(function.evaluate(start + (end - start) * nodes[degree - 1][j])), p);
            }
        }
        return Math.pow((b - a) / n * result, 1.0 / p);
    }

    public double integrate_LP(Function function1, Function function2, double a, double b, double p) {
        return integrate_LP(function1, function2, a, b, p, 1);
    }

    public double integrate_LP(Function function1, Function function2, double a, double b, double p, int n) {
        double result = 0;
        for (int i = 0; i < n; i++) {
            double start = a + (b - a) / n * i;
            double end = a + (b - a) / n * (i + 1);
            for (int j = 0; j <= degree; j++) {
                result = result + weights[degree - 1][j] * Math.pow(Math.abs(function1.evaluate(start + (end - start) * nodes[degree - 1][j]) - function2.evaluate(start + (end - start) * nodes[degree - 1][j])), p);
            }
        }
        return Math.pow((b - a) / n * result, 1.0 / p);
    }

    public double integrate_infty(Function function1, Function function2, double a, double b, int n) {
        double result = 0;

        for (int i = 0; i < n; i++) {
            if (result < Math.abs(function1.evaluate(a + i * (b - a) / (n - 1)) - function2.evaluate(a + i * (b - a) / (n - 1)))) {
                result = Math.abs(function1.evaluate(a + i * (b - a) / (n - 1)) - function2.evaluate(a + i * (b - a) / (n - 1)));
            }
        }

        return result;
    }

}
