package com.uni_stuttgart.isl.Solver;

/**
 * Created by nborg on 06.08.15.
 */

public class Solver {

    private double[][] A;
    private double[] b;
    private double[] x;
    private int dim;

    public void gauss() {
        double[] b_p = new double[dim];
        double[][] A_p = new double[dim][dim];
        for (int j = 0; j < dim; j++) {
            b_p[j] = b[j];
            for (int i = 0; i < dim; i++) {
                A_p[i][j] = A[i][j];
            }
        }

        int max = 0;
        for (int p = 0; p < dim; p++) {
            max = p;
            for (int i = p + 1; i < dim; i++) {
                if (Math.abs(A_p[i][p]) > Math.abs(A_p[max][p])) {
                    max = i;
                }
            }
            double[] temp = A_p[p];
            A_p[p] = A_p[max];
            A_p[max] = temp;
            double t = b_p[p];
            b_p[p] = b_p[max];
            b_p[max] = t;

            // pivot within A and b
            double alpha;
            for (int i = p + 1; i < dim; i++) {
                alpha = A_p[i][p] / A_p[p][p];
                b_p[i] -= alpha * b_p[p];
                for (int j = p; j < dim; j++) {
                    A_p[i][j] -= alpha * A_p[p][j];
                }
            }
        }
        // back substitution
        double sum;
        for (int i = dim - 1; i >= 0; i--) {
            sum = 0.0;
            for (int j = i + 1; j < dim; j++) {
                sum += A_p[i][j] * x[j];
            }
            x[i] = (b_p[i] - sum) / A_p[i][i];
        }
    }

    public int getDim() {
        return dim;
    }

    public void setDim(int dim) {
        this.dim = dim;
        A = new double[dim][dim];
        b = new double[dim];
        x = new double[dim];
    }

    public double[][] getA() {
        return A;
    }

    public void setA(double[][] A) {
        this.A = A;
    }

    public double[] getB() {
        return b;
    }

    public void setB(double[] b) {
        this.b = b;
    }

    public double[] getX() {
        return x;
    }


}
