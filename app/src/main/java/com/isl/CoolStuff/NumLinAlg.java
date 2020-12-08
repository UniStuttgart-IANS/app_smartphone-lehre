package com.uni_stuttgart.isl.CoolStuff;

import android.widget.Toast;

import com.uni_stuttgart.isl.Function.Function;

import static java.security.AccessController.getContext;

/**
 * Created by nborg on 11.08.15.
 */
public class NumLinAlg {

    private static final double eps = 1e-10;

    // Funktion die zwei Matrizen miteinander multipliziert und das Produkt dann zurückgibt
    public static double[][] MatMat(double[][] Mat1, double[][] Mat2) {

        if (Mat1[0].length != Mat2.length) {
            throw new AssertionError("Matrix dimension does not match");
        }

        // Mat1T \in \R^(nxm) and Mat2 \in \R^(lxk) -> \R^(nxm) * \R^(lxk) = \in R^(lxk)
        int n = Mat1.length;
        int m = Mat1[0].length;
        int l = Mat2.length;
        int k = Mat2[0].length;

        double result[][] = new double[n][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < k; j++) {
                for (int h = 0; h < m; h++) {
                    result[i][j] = result[i][j] + Mat1[i][h] * Mat2[h][j];
                }
                //Log.d("Debug", "Result("+i+","+j+") = " + result[i][j]);
            }
        }
        return result;
    }


    public static boolean checkForSingularity(double[][] A) {
        double[][] A_copy = new double[A.length][A.length];

        A_copy = matCopy(A);

        boolean check = true;
        int N = A.length;

        for (int p = 0; p < N; p++) {
            // find pivot row and swap
            int max = p;
            for (int i = p + 1; i < N; i++) {
                if (Math.abs(A_copy[i][p]) > Math.abs(A_copy[max][p])) {
                    max = i;
                }
            }
            double[] temp = A_copy[p];
            A_copy[p] = A_copy[max];
            A_copy[max] = temp;

            // singular or nearly singular
            if (Math.abs(A_copy[p][p]) < eps) {
                check = false;
            }

            // pivot within A and b
            for (int i = p + 1; i < N; i++) {
                double alpha = A_copy[i][p] / A_copy[p][p];
                for (int j = p; j < N; j++) {
                    A_copy[i][j] -= alpha * A_copy[p][j];
                }
            }
        }

        return check;
    }

    public static double[][] MatTMat(double[][] Mat1T, double[][] Mat2) {

        if (Mat1T[0].length != Mat2[0].length) {
            throw new AssertionError("Matrix dimension does not match");
        }

        // Mat1T \in \R^(nxm) and Mat2 \in \R^(lxk)  -> \R^(mxn) * \R^(lxk) = \in R^(mxk)
        int n = Mat1T.length;
        int m = Mat1T[0].length;
        int l = Mat2.length;
        int k = Mat2[0].length;

        double result[][] = new double[n][m];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < k; j++) {
                for (int h = 0; h < n; h++) {
                    result[i][j] = result[i][j] + Mat1T[h][i] * Mat2[h][j];
                }
                //Log.d("Debug", "Result("+i+","+j+") = " + result[i][j]);
            }
        }
        return result;
    }

    public static double[] MatVec(double[][] Mat, double[] Vec) {

        if (Mat[1].length != Vec.length) {
            throw new AssertionError("Matrix dimension does not match");
        }

        int n = Vec.length;
        int m = Mat[0].length;

        double result[] = new double[m];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                result[i] = result[i] + Mat[i][j] * Vec[j];
            }
            //Log.d("Debug", "Result("+i+") = " + result[i]);
        }

        return result;
    }

    public static double[] MatTVec(double[][] Mat, double[] Vec) {

        if (Mat.length != Vec.length) {
            throw new AssertionError("Matrix dimension does not match");
        }

        int n = Mat.length; // = l
        int m = Mat[0].length;
        int l = Vec.length; // = n

        double result[] = new double[m];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                result[i] = result[i] + Mat[j][i] * Vec[j];
            }
            //Log.d("Debug", "Result("+i+") = " + result[i]);
        }

        return result;
    }

    public static double getMax(double[] vec) {
        double max = vec[0];
        int i;
        for (i = 0; i < vec.length; i++) {
            if (vec[i] > max) {
                max = vec[i];
            }
        }

        return max;
    }

    public static double getMin(double[] vec) {
        double min = vec[0];
        int i;
        for (i = 0; i < vec.length; i++) {
            if (vec[i] < min) {
                min = vec[i];
            }
        }
        return min;
    }

    public static double[] aMatVecbVec(double alpha, double[][] A, double[] x, double beta, double[] b) {
        int n = x.length, i, j;
        double[] temp = new double[n];
        for (i = 0; i < n; i++) {
            for (j = 0; j < n; j++) {
                temp[i] = temp[i] + A[i][j] * x[j];
            }
            temp[i] = alpha * temp[i] + beta * b[i];
        }
        return temp;
    }

    public static double norm2(double[] vec) {
        return Math.sqrt(dot(vec, vec));
    }

    public static double norm_res(double[] x, double[][] A, double[] b) {
        return norm2(aMatVecbVec(1, A, x, -1, b));
    }

    public static double dot(double[] vec1, double[] vec2) {
        int i;
        double value = 0;
        for (i = 0; i < vec1.length; i++) {
            value = value + vec1[i] * vec2[i];
        }
        return value;
    }

    public static double[] addVecVec(double[] a, double[] b) {
        int n = a.length;
        double[] temp = new double[n];

        for (int i = 0; i < n; i++) {
            temp[i] = a[i] + b[i];
        }

        return temp;
    }

    public static double[] vecCopy(double[] v) {
        double[] w = new double[v.length];

        for (int i = 0; i < v.length; i++) {
            w[i] = v[i];

        }

        return w;
    }

    public static double[][] matCopy(double[][] A) {
        double[][] B = new double[A.length][A.length];

        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A.length; j++) {
                B[i][j] = A[i][j];
            }
        }

        return B;
    }

    public static double[][] invert(double A[][]) {
        double[][] A_copy = new double[A.length][A.length];

        A_copy = matCopy(A);
        int n = A.length;
        double x[][] = new double[n][n];
        double b[][] = new double[n][n];
        int index[] = new int[n];
        for (int i = 0; i < n; ++i)
            b[i][i] = 1;

        // Transform the matrix into an upper triangle
        gaussian(A_copy, index);

        // Update the matrix b[i][j] with the ratios stored
        for (int i = 0; i < n - 1; ++i)
            for (int j = i + 1; j < n; ++j)
                for (int k = 0; k < n; ++k)
                    b[index[j]][k]
                            -= A_copy[index[j]][i] * b[index[i]][k];

        // Perform backward substitutions
        for (int i = 0; i < n; ++i) {
            x[n - 1][i] = b[index[n - 1]][i] / A_copy[index[n - 1]][n - 1];
            for (int j = n - 2; j >= 0; --j) {
                x[j][i] = b[index[j]][i];
                for (int k = j + 1; k < n; ++k) {
                    x[j][i] -= A_copy[index[j]][k] * x[k][i];
                }
                x[j][i] /= A_copy[index[j]][j];
            }
        }
        return x;
    }

// Method to carry out the partial-pivoting Gaussian
// elimination.  Here index[] stores pivoting order.

    public static void gaussian(double A[][], int index[]) {
        int n = index.length;
        double c[] = new double[n];

        // Initialize the index
        for (int i = 0; i < n; ++i)
            index[i] = i;

        // Find the rescaling factors, one from each row
        for (int i = 0; i < n; ++i) {
            double c1 = 0;
            for (int j = 0; j < n; ++j) {
                double c0 = Math.abs(A[i][j]);
                if (c0 > c1) c1 = c0;
            }
            c[i] = c1;
        }

        // Search the pivoting element from each column
        int k = 0;
        for (int j = 0; j < n - 1; ++j) {
            double pi1 = 0;
            for (int i = j; i < n; ++i) {
                double pi0 = Math.abs(A[index[i]][j]);
                pi0 /= c[index[i]];
                if (pi0 > pi1) {
                    pi1 = pi0;
                    k = i;
                }
            }

            // Interchange rows according to the pivoting order
            int itmp = index[j];
            index[j] = index[k];
            index[k] = itmp;
            for (int i = j + 1; i < n; ++i) {
                double pj = A[index[i]][j] / A[index[j]][j];

                // Record pivoting ratios below the diagonal
                A[index[i]][j] = pj;

                // Modify other elements accordingly
                for (int l = j + 1; l < n; ++l)
                    A[index[i]][l] -= pj * A[index[j]][l];
            }
        }
    }

    public static double Newton(double x, Function f){
        if (Math.abs(f.derivation(x)) > 1e-6) {
            return x - (f.evaluate(x) / f.derivation(x));
        }
        else{
            return 0;
        }
    }

    public static double QuasiNewton(double x, double x_0, Function f){
        if (Math.abs(f.derivation(x_0)) > 1e-6) {
            return x - (f.evaluate(x) / f.derivation(x_0));
        }
        else{
            return 0;
        }
    }

}
