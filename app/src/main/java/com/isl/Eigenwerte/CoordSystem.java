package com.uni_stuttgart.isl.Eigenwerte;

import android.widget.TextView;

import com.uni_stuttgart.isl.CoolStuff.NumLinAlg;
import com.uni_stuttgart.isl.Function.Function;
import com.uni_stuttgart.isl.Eigenwerte.MyCanvas;
import com.uni_stuttgart.isl.Points.PurePoint;
import com.uni_stuttgart.isl.Points.PurePoints;

/**
 * Created by ms
 */
public class CoordSystem {

    // Canvas auf dem gezeichnet werden soll
    MyCanvas myCanvas;
    // Koordinate des Mittepunktes (x_0,y_0)
    private float x0;
    private float y0;
    //Länge der Achsen x: x_0+x_len, y: y_0+y_len;
    private float xLen;
    private float yLen;

    private float xSize;
    private float ySize;

    private float[] rotMat = new float[4];
    private float[] invrotMat = new float[4];
    private float reflMat[] = {-0.5f, 0, 0, 0.5f};

    public  float rotdeg = 15;


    private float[] xy_pol0 = new float[6];
    private float[] xy_vec = new float[2];


    // Werte (Vektoren...) fürs Zeichnen des Koordinaten-System bestimmen
    public CoordSystem(float xSize, float ySize, MyCanvas myCanvas) {

        // Setzen des Koordinatenursprungs
        this.x0 = xSize/2;
        this.y0 = ySize/2;



        // Setzen der Achsenlänge
        this.xSize = xSize;
        this.ySize = ySize;

        // Setzen der Achsenlänge
        this.xLen = xSize/2;
        this.yLen = ySize/2;


        // Setzen des Canvas, auf dem gezeichnet werden soll
        this.myCanvas = myCanvas;

    }

    public void draw_polygon1(float tx1, float ty1, float tx2, float ty2, float tx3, float ty3) {

        PurePoints p = myCanvas.getPolPoints();
        p.getPoint(0).setX(tx2);
        p.getPoint(0).setReelX(tx2);
        p.getPoint(0).setY(ty2);
        p.getPoint(0).setReelY(ty2);
        p.getPoint(1).setX(tx3);
        p.getPoint(1).setReelX(tx3);
        p.getPoint(1).setY(ty3);
        p.getPoint(1).setReelY(ty3);


        myCanvas.drawPol1(tx1,ty1,tx2,ty2,tx3,ty3);
    }

    public void draw_v(float x1, float y1) {

        xy_vec[0] = x1;
        xy_vec[1] = y1;

        PurePoints points = myCanvas.getPoints();

        points.getPoint(4).setReelX(x1);
        points.getPoint(4).setX(x1);
        points.getPoint(4).setReelY(y1);
        points.getPoint(4).setY(y1);

        myCanvas.drawv();
    }

    public void draw_Av() {

        float x1 = reflMat[0]*(xy_vec[0]-x0) + reflMat[1]*(xy_vec[1]-y0)+x0;
        float y1 = reflMat[2]*(xy_vec[0]-x0) + reflMat[3]*(xy_vec[1]-y0)+y0;



        myCanvas.drawAv();
    }

    public void draw_polygon2() {

        float x1 = x0;
        float y1 = y0;

        float x2 = reflMat[0]*(xy_pol0[2]-x0) + reflMat[1]*(xy_pol0[3]-y0)+x0;
        float y2 = reflMat[2]*(xy_pol0[2]-x0) + reflMat[3]*(xy_pol0[3]-y0)+y0;

        float x3 = reflMat[0]*(xy_pol0[4]-x0) + reflMat[1]*(xy_pol0[5]-y0)+x0;
        float y3 = reflMat[2]*(xy_pol0[4]-x0) + reflMat[3]*(xy_pol0[5]-y0)+y0;

        PurePoints polpoints;
        polpoints = myCanvas.getPolPoints();
        polpoints.getPoint(2).setReelX(x2);
        polpoints.getPoint(2).setX(x2);
        polpoints.getPoint(2).setReelY(y2);
        polpoints.getPoint(2).setY(y2);
        polpoints.getPoint(3).setReelX(x3);
        polpoints.getPoint(3).setX(x3);
        polpoints.getPoint(3).setReelY(y3);
        polpoints.getPoint(3).setY(y3);


        myCanvas.drawPol2(x1,y1,x2,y2,x3,y3);
    }

    public void draw_xAxis(float x1, float y1, float x2, float y2) {

        myCanvas.drawxAxis(x1,y1,x2,y2);
    }

    public void draw_yAxis(float x1, float y1, float x2, float y2) {

        myCanvas.drawyAxis(x1,y1,x2,y2);
    }
    public double getYTransFromReel(double yReel){

        return -(yReel *yLen) + y0;
    }

    public double getXTransFromReel(double xReel){

        return (xReel *xLen) + x0;
    }

    public double getYTransFromNorm(double y){

        return (y0-y)/yLen;
    }

    public double getXTransFromNorm(double x){

        return (x-x0)/xLen;
    }

    public float getY0() {
        return y0;
    }


    public float getX0() {
        return x0;
    }

    public float getxLen() {
        return xLen;
    }
    public float getyLen() {
        return yLen;
    }
    public float getxSize() {
        return xSize;
    }
    public float getySize() {
        return ySize;
    }

}
