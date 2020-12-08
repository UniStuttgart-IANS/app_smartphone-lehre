package com.uni_stuttgart.isl.Splittingsolver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.uni_stuttgart.isl.CoolStuff.NumLinAlg;


/**
 * Created by nborg on 08.04.16.
 */
public class SolverLib {

    public static double[] gs_solver(double[][] A, double[] b, double[] x, int iter_max, double eps) {
        int n = b.length, iter = 0, i, j;
        double res = 1000000000, temp;

        while (iter < iter_max && res > eps) {
            for (i = 0; i < n; i++) {
                temp = b[i];
                for (j = 0; j < n; j++) {
                    temp = temp - A[i][j] * x[j];
                }
                temp = temp / A[i][i];
                x[i] = x[i] + temp;
            }
            res = NumLinAlg.norm_res(x, A, b);
            iter = iter + 1;
        }

        Log.e("Test", "IterGS = " + iter);

        return x;
    }

    public static double[] sor_solver(double[][] A, double[] b, double[] x, int iter_max, double eps, double omega) {
        int n = b.length, iter = 0, i, j;
        double res = 1000000000, temp;
        double[] x_old = new double[n];

        while (iter < iter_max && res > eps) {
            x_old = x.clone();
            for (i = 0; i < n; i++) {
                temp = b[i];
                for (j = 0; j < n; j++) {
                    temp = temp - A[i][j] * x[j];
                }
                temp = temp / A[i][i];
                x[i] = x[i] + temp;
                x[i] = omega * x[i] + (1 - omega) * x_old[i];
            }
            res = NumLinAlg.norm_res(x, A, b);
            iter = iter + 1;
        }

        Log.e("Test", "IterSOR = " + iter);

        return x;
    }

    public static double[] mySolver(final double[][] A, final double[][] M, final double[] b, final double[] x, final int iter_max, final double eps, final double omega, final ProgressDialog progress, final TextView iterView, final TextView resView) {

        final int n = b.length;
        final double[][] invM = NumLinAlg.invert(M);
        iterView.setText("");
        resView.setText("");
        progress.setMax(100);
        progress.setProgress(0);


        final AsyncTask<Void, double[], double[]> task = new AsyncTask<Void, double[], double[]>() {

            @Override
            protected void onPreExecute() {
                // Cancelbar bei klicken neben das Dialogfeld
                progress.setCancelable(false);

                // Bei klicken auf das Cancel Feld
                progress.setButton(ProgressDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Cancel download task
                        cancel(true);
                    }
                });

                // Bei klicken neben das Dialogfenster
                progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // cancel AsyncTask
                        cancel(true);
                    }
                });
                progress.show();
            }

            @Override
            protected double[] doInBackground(Void... params) {
                double[] IterAndRes = new double[2];
                try {
                    double[] x_1, x_old, temp_vec = new double[n];
                    int iter = 0;
                    double res_old = NumLinAlg.norm_res(x, A, b);
                    double res = NumLinAlg.norm_res(x, A, b);

                    double c1 = progress.getMax() / (Math.log(res) - Math.log(eps));
                    double c0 = -Math.log(res) * c1;

                    x_1 = x.clone();

                    while (iter < iter_max && res > eps && !isCancelled()) {
                        x_old = x_1.clone();
                        res_old = res;

                        temp_vec = NumLinAlg.aMatVecbVec(-1, A, x_old, 1, b);
                        temp_vec = NumLinAlg.MatVec(invM, temp_vec);

                        x_1 = NumLinAlg.addVecVec(x_old, temp_vec);

                        res = NumLinAlg.norm_res(x_1, A, b);
                        iter = iter + 1;
//                        log("SolverRed: " + res);
//                        log("SolverIter: " + iter);

                        progress.setProgress(trafo(c1, c0, res));
                        IterAndRes[0] = iter;
                        IterAndRes[1] = res;
                    }
                    Log.e("Test", "Res = " + res);
                    Log.e("Test", "Iter = " + iter);

                    progress.setProgress(progress.getMax());
                    progress.dismiss();
                    return IterAndRes;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return IterAndRes;
            }

            @Override
            protected void onPostExecute(double[] result) {
                iterView.setText("#Iterationen: " + (int) result[0]);
                resView.setText("Residuum: " + (float) result[1]);

                //called on ui thread
                if (progress != null) {
                    progress.dismiss();
                }
            }

            @Override
            protected void onCancelled(double[] result) {
                iterView.setText("#Iterationen: " + (int) result[0]);
                resView.setText("Residuum: " + (float) result[1]);

                //called on ui thread
                if (progress != null) {
                    progress.dismiss();
                }
            }
        };
        task.execute();

        return x;
    }

    public static int trafo(double a, double b, double x) {
        return (int) -(a * Math.log(x) + b);
    }

    public static double[] jacobi_solver(double[][] A, double[] b, double[] x, int iter_max, double eps) {
        int N = b.length;
        int iter = 0;
        int i, j;
        double res = 1000000000;
        double[] x_old = new double[N];
        double temp = 0;

        while (iter < iter_max && res > eps) {
            x_old = x.clone();
            for (i = 0; i < N; i++) {
                temp = b[i];
                for (j = 0; j < N; j++) {
                    temp = temp - A[i][j] * x_old[j];
                }
                temp = temp / A[i][i];
                x[i] = x[i] + temp;
            }
            res = NumLinAlg.norm_res(x, A, b);
            iter = iter + 1;
        }

        Log.e("Test", "IterJAC = " + iter);

        return x;
    }


    public final static void log(String string) {
        Log.e("TAG", string);
    }

}
