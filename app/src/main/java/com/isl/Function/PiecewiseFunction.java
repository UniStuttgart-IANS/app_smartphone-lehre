package com.uni_stuttgart.isl.Function;

/**
 * Created by ms on 30.03.20.
 */

public class PiecewiseFunction implements Function {

    int num_functions;
    double[] range_values;
    int owner[]; //-1 if value is in left range, 1 if it is in right range
    Function[] functions;

    public PiecewiseFunction(int num, double[] val, int[] own) {
        this.functions = new Function[num];
        this.num_functions =num;
        this.range_values =val.clone();
        this.owner = own.clone();

    }
    public PiecewiseFunction(int num, double[] val, int[] own, Function[] funcs) {
        this.functions = funcs.clone();
        this.num_functions =num;
        this.range_values =val.clone();
        this.owner = own.clone();

    }

    public void add_function_i(Function func, int i) {
        functions[i] = func;
    }


    public void add_functions(Function[] funcs) {
        functions = funcs.clone();
    }

    public double antiderivation(double x){
        return 0;
    }

    public double evaluate(double x) {
        double val=0;
        for (int i=0; i < num_functions; i++) {
            if(x>range_values[i] && x< range_values[i+1]) {
                val = functions[i].evaluate(x);
            }
            if(x==range_values[i]) {
                if (owner[i]==-1)
                    val = functions[i-1].evaluate(x);
                else
                    val = functions[i].evaluate(x);
            }
        }
        return val;
    }

    //Auswertung der Ableitung des Polynoms an Stelle x
    public double derivation(double x) {

        double val=0;
        for (int i=0; i < num_functions; i++) {
            if(x>range_values[i] && x< range_values[i+1]) {
                val = functions[i].derivation(x);
            }
            if(x==range_values[i]) {
                if (owner[i]==-1)
                    val = functions[i-1].derivation(x);
                else
                    val = functions[i].derivation(x);
            }
        }
        return val;
    }



}