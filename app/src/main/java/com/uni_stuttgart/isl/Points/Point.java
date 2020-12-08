package com.uni_stuttgart.isl.Points;

import android.graphics.Paint;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

public class Point {
    // Jeder Punkt hat eine eindeutige Zuordnung, hier einen Nummer
    private int number;

    // Jeder Punkt hat eine Position (der Größe der x Werte nach sortiert)
    private int position;

    // "Pixel"-Koordinaten
    private double x;
    private double y;

    // "Echte"-Koordinaten (Koordinatensystem-Koordinaten)
    private double reelX;
    private double reelY;

    // Vektor mir den Koordinaten der Vierecke um den Punkt selber
    private double[] rectCoords = new double[4];

    // Boolean, ob der Punkt platziert wurde
    private boolean isPlaced;
    // Boolean, ob der Punkt gerade aktiv ist
    private boolean isActive;
    // Boolean, ob der Punkt zur Verfügung steht
    private boolean isAvailable;

    // Switcher zum EIN/AUS schalter des Punktes (->isActive)
    private Switch switcher;

    // Textview mit den Koordinaten des Punktes (Echte Koordinaten)
    private TextView xView;
    private TextView yView;

    // Eigenschaften des Punktes beim Zeichnen (Farbe, Dicke...)
    private Paint paint;

    // Löschen Button
    private ImageButton deleteButton;




    public Point(int number) {
        this.number = number;
    }

    public void setDeleteButton(ImageButton deleteButton) {
        this.deleteButton = deleteButton;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getNumber() {
        return number;
    }

    public boolean getIsPlaced() {
        return isPlaced;
    }

    public void setIsPlaced(boolean isPlaced) {
        this.isPlaced = isPlaced;
    }

    public boolean getActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public Switch getSwitch() {
        return switcher;
    }

    public void setSwitcher(Switch switcher) {
        this.switcher = switcher;
    }

    public TextView getxView() {
        return xView;
    }

    public void setxView(TextView xView) {
        this.xView = xView;
    }

    public TextView getyView() {
        return yView;
    }

    public void setyView(TextView yView) {
        this.yView = yView;
    }

    public void setSwitchOnOff(boolean OnOff) {
        switcher.setChecked(OnOff);
        isActive = OnOff;
    }

    public double calcDiff(float x0, float y0) {
        double diff;
        return diff = Math.pow(x - x0, 2) + Math.pow(y - y0, 2);
    }

    public void setRectCoords() {
        rectCoords[0] = x - 10;
        rectCoords[1] = y - 10;
        rectCoords[2] = x + 10;
        rectCoords[3] = y + 10;
    }

    public double[] getRectCoords() {
        return rectCoords;
    }

    public double getReelX() {
        return reelX;
    }

    public void setReelX(double reelX) {
        this.reelX = reelX;
    }

    public double getReelY() {
        return reelY;
    }

    public void setReelY(double reelY) {
        this.reelY = reelY;
    }

    public ImageButton getDeleteButton() {
        return deleteButton;
    }

    public boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}


