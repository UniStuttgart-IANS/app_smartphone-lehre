package com.uni_stuttgart.isl.Points;

import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class Points {
    //
    private int count;
    private int[] sort;
    private ArrayList<Point> pointList;

    public Points() {
        this(0);
    }

    public Points(int count) {
        this.count = 0;
        pointList = new ArrayList<Point>();
        for (int i = 0; i < count; i++) {
            addPoint();
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Point addPoint() {
        this.pointList.add(new Point(count));
        this.count++;

        return pointList.get(count - 1);
    }

    public Point getPoint(int number) {
        return pointList.get(number);
    }

    public void setSwitchOn(int number) {
        for (int i = 0; i < count; i++) {
            if (i != number) {
                this.getPoint(i).setSwitchOnOff(false);
            }
        }
        this.getPoint(number).setSwitchOnOff(true);
    }

    public Point getActive() {
        int flag = 0;
        for (int i = 0; i < count; i++) {
            if (pointList.get(i).getActive()) {
                flag = i;
            }
        }

        return pointList.get(flag);
    }

    public int getNearestPoint(double x, double y) {
        double diff;
        double min = 0;
        int number = 0;

        for (int i = 0; i < count; i++) {
            diff = Math.pow(this.pointList.get(i).getX() - x, 2) + Math.pow(this.pointList.get(i).getY() - y, 2);
            if (i == 0 || diff < min) {
                number = i;
                min = diff;
            }
        }

        return number;
    }

    // getNumberOfPlacedPoints
    public int getNOPP() {
        int NOPP = 0;

        for (int i = 0; i < count; i++) {
            if (this.getPoint(i).getIsPlaced()) {
                NOPP++;
            }
        }
        return NOPP;
    }

    // getNumberOfAvailablePoints
    public int getNOAP() {
        int NOAP = 0;

        for (int i = 0; i < count; i++) {
            if (this.getPoint(i).getIsAvailable()) {
                NOAP++;
            }
        }
        return NOAP;
    }

    public void deletePoint(int pointnumber) {
        this.getPoint(pointnumber).setIsPlaced(false);
        this.getPoint(pointnumber).setActive(false);
//		this.getPoint(pointnumber).setSwitchOnOff(false);
    }

    public void removePoint(int pointnumber) {
        this.getPoint(pointnumber).setIsPlaced(false);
        this.getPoint(pointnumber).setActive(false);
        this.getPoint(pointnumber).setSwitchOnOff(false);
        this.getPoint(pointnumber).setIsAvailable(false);
        this.getPoint(pointnumber).getxView().setText("");
        this.getPoint(pointnumber).getyView().setText("");
        this.getPoint(pointnumber).getDeleteButton().setVisibility(View.GONE);
        this.getPoint(pointnumber).getSwitch().setVisibility(View.GONE);
        this.getPoint(pointnumber).getxView().setVisibility(View.GONE);
        this.getPoint(pointnumber).getyView().setVisibility(View.GONE);
    }

    // Gibt einen Vektor zurück, der in Abhängigkeit der x Werte die Points sortiert
    // BSP:. Punkt 2 hat den 3. kleinsten Wert--> sort[1] = 3)
    public void sortPoints() {
        sort = new int[this.getCount()];
        int flag = 1;

        for (int i = 0; i < this.getCount(); i++) {
            sort[i] = -1;
            if (this.getPoint(i).getIsPlaced()) {
                sort[i] = 0;
            }
        }

        for (int i = 0; i < this.getCount(); i++) {
            if (this.getPoint(i).getIsPlaced()) {
                for (int j = 0; j < this.getCount(); j++) {
                    if (this.getPoint(j).getIsPlaced()) {
                        if (this.getPoint(i).getReelX() > this.getPoint(j).getReelX()) {
                            sort[i] = sort[i] + 1;
                        }
                    }
                }
            }

        }

        setPointPositions();

        for (int i = 0; i < this.count; i++) {
            flag = 1;
            for (int j = 0; j < this.count; j++) {
                if (i != j && this.getPoint(i).getPosition() == this.getPoint(j).getPosition() && this.getPoint(i).getPosition() != -1) {
                    this.getPoint(j).setPosition(this.getPoint(i).getPosition() + flag);
                    flag = flag + 1;
                }
            }
        }
    }

    // Ordnet jedem Punkt seine Position zu (0 = nicht "placed")
    public void setPointPositions() {
        for (int i = 0; i < this.getCount(); i++) {
            this.getPoint(i).setPosition(sort[i]);
        }
    }

    // Gibt die Pointnumber zurück, dessen Point den nächst größeren x-Wert hat
    public int getNextPointNumber(int pointnumber) {
        int next = -2;
        int pointposition = this.getPoint(pointnumber).getPosition();

        for (int i = 0; i < this.getCount(); i++) {
            if (this.getPoint(i).getPosition() == pointposition + 1) {
                next = i;
                break;
            }
        }

        return next;
    }

    public int getFirstPointNumber() {
        int first = 0;

        for (int i = 0; i < this.getCount(); i++) {
            if (this.getPoint(i).getPosition() == 0) {
                first = i;
            }
        }

        return first;
    }

    public int getLastPointNumber() {
        int last = 0;

        for (int i = 0; i < this.getCount(); i++) {
            if (this.getPoint(i).getPosition() == this.getNOPP() - 1) {
                last = i;
            }
        }

        return last;
    }

}
