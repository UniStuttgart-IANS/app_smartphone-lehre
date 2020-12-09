package com.uni_stuttgart.isl.Splittingsolver;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by nborg on 27.06.16.
 */
public class MyYAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter {

    private DecimalFormat mFormat;

    public MyYAxisValueFormatter() {
        mFormat = new DecimalFormat("###,###,##0"); // use one decimal
    }

    @Override
    public String getFormattedValue(float value, AxisBase yAxis) {
        // write your logic here
        // access the YAxis object to get more information
        return "1E" + mFormat.format(value); // e.g. append a dollar-sign
    }
}
