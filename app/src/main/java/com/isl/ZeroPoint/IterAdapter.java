package com.uni_stuttgart.isl.ZeroPoint;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uni_stuttgart.isl.R;

import java.util.List;

/**
 * Created by nborg on 10.05.17.
 */

public class IterAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    List iterCountList;
    List xCoordList;
    List f_xList;
    List pointColor;
    Boolean colorBoolean = false;
    Context context;

    public IterAdapter(Context zerofinding, List iter, List x, List fx, List color) {
        // TODO Auto-generated constructor stub
        iterCountList = iter;
        xCoordList = x;
        f_xList = fx;
        context = zerofinding;
        pointColor = color;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return iterCountList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.iterlistviewitem, null);
        holder.iterView = (TextView) rowView.findViewById(R.id.iterViewItem_Iter);
        holder.xView = (TextView) rowView.findViewById(R.id.iterViewItem_x);
        holder.fxView = (TextView) rowView.findViewById(R.id.iterViewItem_fx);
        holder.colorView = (ImageView) rowView.findViewById(R.id.iterViewColor);
        //holder.colorView = (TextView) rowView.findViewById(R.id.iterViewColor);

        holder.iterView.setText("" + iterCountList.get(position));
        holder.xView.setText("" + xCoordList.get(position));
        holder.fxView.setText("" + f_xList.get(position));
        //holder.colorView.setBackgroundColor((int) pointColor.get(position));
        holder.colorView.setImageResource((int) pointColor.get(position));
        if (colorBoolean) {
            holder.colorView.setVisibility(View.VISIBLE);
        }
        else{
            holder.colorView.setVisibility(View.INVISIBLE);
        }

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You Clicked " + iterCountList.get(position), Toast.LENGTH_LONG).show();
            }
        });

        return rowView;
    }

    public List getIterCountList() {
        return iterCountList;
    }

    public class Holder {
        TextView iterView;
        TextView xView;
        TextView fxView;
        ImageView colorView;
    }

    public void setVisibility(boolean colorBoolean){
        this.colorBoolean = colorBoolean;
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.iterlistviewitem, null);
        holder.colorView = (ImageView) rowView.findViewById(R.id.iterViewColor);
        if (colorBoolean) {
            holder.colorView.setVisibility(View.VISIBLE);
        }
        else{
            holder.colorView.setVisibility(View.INVISIBLE);
        }

    }
}
