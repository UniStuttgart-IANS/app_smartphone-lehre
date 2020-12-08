package com.uni_stuttgart.isl.Function;

/**
 * Created by nborg on 10.08.15.
 */
public class LagrangianPolynom implements PolynomialFunction {

    int degree;
    double[] coeff;
    double[] xCoord;

    //Default-Konstruktor mit Grad 0
    public LagrangianPolynom() {
        this.degree = 0;
        this.coeff = new double[1];
    }

    //Konstruktor mit spezifischem Grad
    public LagrangianPolynom(int degree) {
        this.degree = degree;
        this.coeff = new double[degree + 1];
    }

    public double antiderivation(double x){
        return 0;
    }

    //Auswertung des Polynoms an Stelle x
    public double evaluate(double x) {

        double[] temp_num_vec = new double[xCoord.length];
        double temp_num = 1;
        double temp_denom = 1;
        double temp = 0;
        double result = 0;

        for (int j = 0; j < xCoord.length; j++) {
            temp_num_vec[j] = x - xCoord[j];
        }

        for (int i = 0; i < xCoord.length; i++) {
            temp_num = 1;
            temp_denom = 1;
            for (int j = 0; j < xCoord.length; j++) {
                if (j != i) {
                    temp_denom = temp_denom * (xCoord[i] - xCoord[j]);
                    temp_num = temp_num * temp_num_vec[j];
                }
            }
            temp = temp_num / temp_denom;
            result = result + coeff[i] * temp;
        }

        return result;
    }

    //Auswertung der Ableitung des Polynoms mit dem einseitigen Differenzenquotioenten (vorwärts)
    public double derivation(double x) {
        double tmp1 = evaluate(x);
        double tmp2 = evaluate(x + 1e-8);

        return (tmp2 - tmp1) / 1e-8;
    }

    //Bestimme das Interpolationspolynom (Monombasis) zu den vorgegebenen Werten mit Polynomgrad degree
    public void interpolate(double[] xCoord, double[] yCoord) {
        this.xCoord = xCoord;
        this.coeff = yCoord;
    }
}
