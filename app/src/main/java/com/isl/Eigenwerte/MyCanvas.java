package com.uni_stuttgart.isl.Eigenwerte;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.uni_stuttgart.isl.Points.PurePoint;
import com.uni_stuttgart.isl.Points.PurePoints;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;


/**
 * Created by ms.
 */


public class MyCanvas extends View {

    private Paint[] paints= new Paint[25];
    private Paint[] staticPaints= new Paint[25];

    private  float[] arrow= {0, 0, -30,-10, -30, 10};
    private  float[] rot_arrow= {0, 0, -30,-10, -30, 10};


    // points 0-4 coordinate system moving points
    // poin 5 moving point for vector v
    private int npoints = 5;
    private PurePoints points;
    private Paint[] pointPaint = new Paint[npoints];
    private int npolpoints = 4;
    private PurePoints polpoints;


    private int dimension;

    // Paint/Path für das Koordinaten-System
    private Paint csPaint;
    private Path csPath;

    private Path polygon0;
    private Path polygon1;
    private Path rotp1;
    private Path rotp2;
    private Path projp1;
    private Path projp2;
    private Paint paint_rot;
    private Paint paint_proj;
    private Path xaxis;
    private Path yaxis;

    private Path vArrow;
    private Path AvArrow;
    private Path v;
    private Path Av;

    private float rot_angle = (float)Math.PI/3.f;

    private Paint p_polygon0;
    private Paint p_polygon1;
    private Paint p_xaxis;
    private Paint p_yaxis;

    private Paint p_vArrow;
    private Paint p_AvArrow;
    private Paint p_v;
    private Paint p_Av;

    private float alpha = 0.5f*(float)Math.PI; //winkel zu neutralen x achse
    private float beta = 0.25f*(float)Math.PI; // winkel zw v und neutraler x achse
    private float gamma = 0.75f*(float)Math.PI; // winkel zw v und neutraler x achse

    boolean init;

    private int modi = 0;

    private float rot[] = {0, -1, 1, 0,  -1, 0 , 0, -1,  0 ,1, -1, 0};

    private float pol2[] = {0, 0, 0, 0};

    private float proj_mat[] = {0, 1, 0, 1};

    //private float shear_mat[] = {0, 1, -1, 2};

    private RectF rectF;

    private float proj_dir[] = {-1, 0};
    private float slope_proj_target = -1;
    private float shear_dx = -1;
    private float shear_dy = -1;
    private float shear_scale = 0.5f;



    // Anlegen eines Objekts der Klasse Coordsystem
    private CoordSystem coordSystem;

    private AlertDialog inputWindowDialog;
    private String addValueString;

    public MyCanvas(Context c, AttributeSet attrs) {
        super(c, attrs);

        points = new PurePoints(npoints);
        polpoints = new PurePoints(npolpoints);

        // Setzen der Parameter für das Koordinaten-System
        csPath = new Path();
        csPaint = new Paint();


        polygon0 = new Path();
        polygon1 = new Path();
        rotp1 = new Path();
        rotp2 = new Path();
        projp1 = new Path();
        projp2 = new Path();
        xaxis = new Path();
        yaxis = new Path();

        v = new Path();
        Av = new Path();
        vArrow = new Path();
        AvArrow = new Path();

        p_polygon0 = new Paint();
        p_polygon1 = new Paint();
        p_xaxis = new Paint();
        p_yaxis = new Paint();

        p_v = new Paint();
        p_Av = new Paint();
        p_vArrow = new Paint();
        p_AvArrow = new Paint();

        p_polygon0.setAntiAlias(true);
        p_polygon0.setColor(Color.rgb(255, 255, 196));
        p_polygon0.setStyle(Paint.Style.FILL_AND_STROKE);
        p_polygon0.setStrokeJoin(Paint.Join.ROUND);
        p_polygon0.setStrokeWidth(3F);
        p_polygon1.setAntiAlias(true);
        p_polygon1.setColor(Color.argb(85, 238, 25, 245));
        p_polygon1.setStyle(Paint.Style.FILL_AND_STROKE);
        p_polygon1.setStrokeJoin(Paint.Join.ROUND);
        p_polygon1.setStrokeWidth(13F);


        p_xaxis.setAntiAlias(true);
        p_xaxis.setColor(Color.BLUE);
        p_xaxis.setStyle(Paint.Style.FILL_AND_STROKE);
        p_xaxis.setStrokeJoin(Paint.Join.ROUND);
        p_xaxis.setStrokeWidth(3F);
        p_yaxis.setAntiAlias(true);
        p_yaxis.setColor(Color.RED);
        p_yaxis.setStyle(Paint.Style.FILL_AND_STROKE);
        p_yaxis.setStrokeJoin(Paint.Join.ROUND);
        p_yaxis.setStrokeWidth(3F);

        p_v.setAntiAlias(true);
        p_v.setColor(Color.BLACK);
        p_v.setStyle(Paint.Style.FILL_AND_STROKE);
        p_v.setStrokeJoin(Paint.Join.ROUND);
        p_v.setStrokeWidth(3F);
        p_Av.setAntiAlias(true);
        p_Av.setColor(Color.YELLOW);
        p_Av.setStyle(Paint.Style.FILL_AND_STROKE);
        p_Av.setStrokeJoin(Paint.Join.ROUND);
        p_Av.setStrokeWidth(3F);


        p_vArrow.setAntiAlias(true);
        p_vArrow.setColor(Color.BLACK);
        p_vArrow.setStyle(Paint.Style.FILL_AND_STROKE);
        p_vArrow.setStrokeJoin(Paint.Join.ROUND);
        p_vArrow.setStrokeWidth(3F);
        p_AvArrow.setAntiAlias(true);
        p_AvArrow.setColor(Color.YELLOW);
        p_AvArrow.setStyle(Paint.Style.FILL_AND_STROKE);
        p_AvArrow.setStrokeJoin(Paint.Join.ROUND);
        p_AvArrow.setStrokeWidth(3F);


        csPaint.setAntiAlias(true);
        csPaint.setColor(Color.BLACK);
        csPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        csPaint.setStrokeJoin(Paint.Join.ROUND);
        csPaint.setStrokeWidth(3F);

        paint_rot = new Paint();
        paint_rot.setAntiAlias(true);
        paint_rot.setColor(Color.rgb(255, 255, 196));
        paint_rot.setStyle(Paint.Style.STROKE);
        paint_rot.setStrokeJoin(Paint.Join.ROUND);
        paint_rot.setStrokeWidth(5F);

        paint_proj = new Paint();
        paint_proj.setAntiAlias(true);
        paint_proj.setColor(Color.argb(85, 238, 25, 245));
        paint_proj.setStyle(Paint.Style.STROKE);
        paint_proj.setStrokeJoin(Paint.Join.ROUND);
        paint_proj.setStrokeWidth(5F);


        for (int i = 0; i < npoints; i++) {
            pointPaint[i] = new Paint();
            pointPaint[i].setAntiAlias(true);
            pointPaint[i].setStyle(Paint.Style.FILL_AND_STROKE);
            pointPaint[i].setStrokeJoin(Paint.Join.ROUND);
            pointPaint[i].setStrokeWidth(5.5F);

            // Farben der Punkte definieren
            pointPaint[i].setColor(Color.BLACK);//rgb(210, 210, 210));       //Grey
        }

        for(int i=0; i < 25; i++) {
            paints[i] = new Paint();
            paints[i].setColor(Color.BLACK);
            paints[i].setTextSize(50);
            staticPaints[i] = new Paint();
            staticPaints[i].setColor(Color.GRAY);
            staticPaints[i].setTextSize(50);
        }



    }

    // Zeichen Routine
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Zeichnen des Koordinaten Systems
        for(int i = 0; i < npoints-1; i++) {
            if(points.getPoint(i).getIsPlaced())
                canvas.drawCircle((float) points.getPoint(i).getReelX(), (float) points.getPoint(i).getReelY(), 10, pointPaint[i]);
        }
        if(points.getNOPP()>0 && (modi ==0 ||modi==1)) {
            coordSystem.draw_xAxis((float) points.getPoint(0).getX(), (float) points.getPoint(0).getY(), (float) points.getPoint(2).getX(), (float) points.getPoint(2).getY());
            coordSystem.draw_yAxis((float) points.getPoint(1).getX(), (float) points.getPoint(1).getY(), (float) points.getPoint(3).getX(), (float) points.getPoint(3).getY());
        }
        if(points.getNOPP()>0 && modi==2) {
            coordSystem.draw_yAxis((float) points.getPoint(1).getX(), (float) points.getPoint(1).getY(), (float) points.getPoint(3).getX(), (float) points.getPoint(3).getY());
        }


        canvas.drawPath(polygon0,p_polygon0);
        if(init) {
            drawPoly2();
            drawv();
            drawAv();
        }
        canvas.drawPath(polygon1,p_polygon1);
        canvas.drawPath(xaxis,p_xaxis);
        canvas.drawPath(yaxis,p_yaxis);
        canvas.drawPath(v,p_v);
        canvas.drawPath(vArrow,p_vArrow);
        canvas.drawPath(Av,p_Av);
        canvas.drawPath(AvArrow,p_Av);

        if(modi==1 || modi==2) {
            canvas.drawPath(projp1,paint_proj);
            canvas.drawPath(projp2,paint_proj);
        }
        if(modi==3) {
            canvas.drawPath(rotp1,paint_rot);
            canvas.drawPath(rotp2,paint_rot);
        }


        invalidate();
    }



    // Routine für Aktionen bei TouchEvents
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x_action= event.getX();
        float y_action = event.getY();

            switch (event.getAction()) {
                // Einfacher Touch, keine Bewegung
                case MotionEvent.ACTION_DOWN:

                    // Aktivieren eine Punktes bei Touch in der Nähe
                    if (points.getPoint(points.getNearestPoint(x_action, y_action)).calcDiff(x_action, y_action) < 2000 && points.getPoint(points.getNearestPoint(x_action, y_action)).getIsPlaced() ) {
                        points.getPoint(points.getNearestPoint(x_action, y_action)).setActive(true);

                    }

                    break;
                // Bewegter Touch
                case MotionEvent.ACTION_MOVE:
                    // Schleife über alle Punkte
                    for (int j = 0; j < points.getCount(); j++) {
                        // Abfrage, ob es sich um den aktiven Punkt handelt.
                        if (points.getPoint(j).getActive()) {
                            if(modi ==0 )
                                touch_event_mod_0(x_action, y_action, j);
                            else if(modi ==1 )
                                touch_event_mod_1(x_action, y_action, j);
                            else if(modi ==2 )
                                touch_event_mod_2(x_action, y_action, j);
                            else if(modi ==3 )
                                touch_event_mod_3(x_action, y_action, j);
                        }

                    }

                    break;
                case MotionEvent.ACTION_UP:
                    for (int j = 0; j < points.getCount(); j++) {
                        // Abfrage, ob es sich um den aktiven Punkt handelt.
                        points.getPoint(j).setActive(false);

                    }

                    freeMemory();
            }

        invalidate();

        return true;
    }



    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }


    public AlertDialog getInputWindowDialog() {
        return inputWindowDialog;
    }

    public void setInputWindowDialog(AlertDialog inputWindowDialog) {
        this.inputWindowDialog = inputWindowDialog;
    }

    public int getDimension() {
        return dimension;
    }


    public void setDimension(int dimension) {
        this.dimension = dimension;
    }


    public void setCoordSystem(CoordSystem coordSystem) {
        this.coordSystem = coordSystem;
    }

    // Path festlegen für das Koordinaten-System
    public void drawXtoY_cs(float x1, float y1, float x2, float y2) {
        csPath.moveTo(x1, y1);
        csPath.lineTo(x2, y2);
    }


    public void drawPol1(float x1, float y1, float x2, float y2, float x3, float y3) {
        polygon0.reset();
        polygon0.moveTo(x1,y1);
        polygon0.lineTo(x2,y2);
        polygon0.lineTo(x3,y3);
        polygon0.lineTo(x1,y1);
    }

    public void drawPol2(float x1, float y1, float x2, float y2, float x3, float y3) {
        polygon1.reset();
        polygon1.moveTo(x1,y1);
        polygon1.lineTo(x2,y2);
        polygon1.lineTo(x3,y3);
        polygon1.lineTo(x1,y1);
    }


    public void drawPoly2() {
        polygon1.reset();

        float x0 = coordSystem.getX0();
        float y0 = coordSystem.getY0();
        polygon1.moveTo(x0,y0);

        if (modi == 0) {
            float cos2a = (float) Math.cos(2 * (2 * Math.PI - alpha));
            float sin2a = (float) Math.sin(2 * (2 * Math.PI - alpha));
            for (int i = 0; i < 2; i++) {
                float x = 0.5f * (cos2a * ((float) polpoints.getPoint(i).getX() - x0) + sin2a * ((float) polpoints.getPoint(i).getY() - y0)) + x0;
                float y = 0.5f * (sin2a * ((float) polpoints.getPoint(i).getX() - x0) - cos2a * ((float) polpoints.getPoint(i).getY() - y0)) + y0;

                polygon1.lineTo(x, y);
            }
        }

        else if (modi == 1) {

            float[] x = new float[2];
            float[] y = new float[2];
            for (int i = 0; i < 2; i++) {
                x[i] =  (proj_mat[0] * ((float) polpoints.getPoint(i).getX() - x0) + proj_mat[1] * ((float) polpoints.getPoint(i).getY() - y0)) + x0;
                y[i] =  ((proj_mat[2] * ((float) polpoints.getPoint(i).getX() - x0) + proj_mat[3] * ((float) polpoints.getPoint(i).getY() - y0)) + y0);

                polygon1.lineTo(x[i], y[i]);
            }
            projp1.reset();
            projp1.moveTo((float) polpoints.getPoint(0).getX() ,(float) polpoints.getPoint(0).getY() );
            projp1.lineTo(x[0],y[0]);

            projp2.reset();
            projp2.moveTo((float) polpoints.getPoint(1).getX() ,(float) polpoints.getPoint(1).getY() );
            projp2.lineTo(x[1],y[1]);

        }

        else if (modi ==2) {
            float[] x = new float[2];
            float[] y = new float[2];
            for (int i = 0; i < 2; i++) {
                x[i] =  ((float) polpoints.getPoint(i).getX() - x0);
                y[i] =  ((float) polpoints.getPoint(i).getY() - y0);

                float dist = (x[i]*shear_dy-y[i]*shear_dx)/(float)Math.sqrt(shear_dx*shear_dx+shear_dy*shear_dy);

                x[i] = x[i]+shear_scale*dist*shear_dx+x0;
                y[i] = y[i]+shear_scale*dist*shear_dy+y0;
                polygon1.lineTo(x[i], y[i]);
            }
            projp1.reset();
            projp1.moveTo((float) polpoints.getPoint(0).getX() ,(float) polpoints.getPoint(0).getY() );
            projp1.lineTo(x[0],y[0]);

            projp2.reset();
            projp2.moveTo((float) polpoints.getPoint(1).getX() ,(float) polpoints.getPoint(1).getY() );
            projp2.lineTo(x[1],y[1]);
        }

        else if (modi==3) {

            float cosd = (float) Math.cos( (2 * Math.PI - rot_angle));
            float sind = (float) Math.sin( (2 * Math.PI - rot_angle));
            for (int i = 0; i < 2; i++) {
                float x =  (cosd * ((float) polpoints.getPoint(i).getX() - x0) - sind * ((float) polpoints.getPoint(i).getY() - y0)) + x0;
                float y =  (sind * ((float) polpoints.getPoint(i).getX() - x0) + cosd * ((float) polpoints.getPoint(i).getY() - y0)) + y0;
                pol2[i] = x;
                pol2[i+1] = y;

                polygon1.lineTo(x, y);
            }

            rotp1.reset();
            rotp2.reset();

            float p0x =coordSystem.getX0();
            float p0y =coordSystem.getY0();
            float p1x =(float)polpoints.getPoint(0).getX();
            float p1y =(float)polpoints.getPoint(0).getY();
            float radius = (float)Math.sqrt(Math.pow((p0x-p1x),2)+Math.pow((p0y-p1y),2));
            rectF = new RectF(p0x - radius, p0y - radius, p0x + radius, p0y+ radius);

            float sweepAngle = rot_angle*180/(float)Math.PI;
            float startAngle = (180 / (float)Math.PI * (float)Math.atan2(p1y - p0y, p1x - p0x));
            rotp1.arcTo(rectF, startAngle, - sweepAngle, true);

            p1x =(float)polpoints.getPoint(1).getX();
            p1y =(float)polpoints.getPoint(1).getY();
            radius = (float)Math.sqrt(Math.pow((p0x-p1x),2)+Math.pow((p0y-p1y),2));
            rectF = new RectF(p0x - radius, p0y - radius, p0x + radius, p0y+ radius);

            sweepAngle = rot_angle*180/(float)Math.PI;
            startAngle = (180 / (float)Math.PI * (float)Math.atan2(p1y - p0y, p1x - p0x));
            rotp2.arcTo(rectF, startAngle, - sweepAngle, true);

        }

        polygon1.moveTo(x0,y0);
    }

    public void drawv() {
        float x1 = (float)points.getPoint(4).getX();
        float y1 = (float)points.getPoint(4).getY();

        v.reset();
        v.moveTo(coordSystem.getX0(),coordSystem.getY0());
        v.lineTo(x1,y1);

        points.getPoint(4).setPaint(pointPaint[4]);
        points.getPoint(4).setIsAvailable(true);
        points.getPoint(4).setIsPlaced(true);

        vArrow.reset();

        float cosb= (float)Math.cos((2*Math.PI-beta));
        float sinb= (float)Math.sin((2*Math.PI-beta));

        rot_arrow[0] = cosb* arrow[0] - sinb*arrow[1]+x1;
        rot_arrow[1] = sinb* arrow[0] + cosb*arrow[1]+y1;
        vArrow.moveTo(rot_arrow[0],rot_arrow[1]);

        for(int i = 1; i < 3; i++) {
            rot_arrow[2*i] = cosb* arrow[2*i] - sinb*arrow[2*i+1]+x1;
            rot_arrow[2*i+1] = sinb* arrow[2*i] + cosb*arrow[2*i+1]+y1;

            vArrow.lineTo(rot_arrow[2*i],rot_arrow[2*i+1]);
        }

        vArrow.lineTo(rot_arrow[0],rot_arrow[1]);
        for(int i = 0; i < 3; i++) {
            rot_arrow[2*i] -= x1;
            rot_arrow[2*i+1] -= y1;
        }

    }

    public void drawAv() {
        Av.reset();

        float x0 = coordSystem.getX0();
        float y0 = coordSystem.getY0();
        float x=0,y=0;


        Av.moveTo(x0,y0);
        float cos2a=0, sin2a=0;
        float cosd=0, sind=0;
        if (modi == 0) {
            cos2a = (float) Math.cos(2 * (2 * Math.PI - alpha));
            sin2a = (float) Math.sin(2 * (2 * Math.PI - alpha));

            x = 0.5f * (cos2a * ((float) points.getPoint(4).getX() - x0) + sin2a * ((float) points.getPoint(4).getY() - y0)) + x0;
            y = 0.5f * (sin2a * ((float) points.getPoint(4).getX() - x0) - cos2a * ((float) points.getPoint(4).getY() - y0)) + y0;
        }

        else if (modi == 1) {

            x =  (proj_mat[0] * ((float) points.getPoint(4).getX() - x0) + proj_mat[1] * ((float) points.getPoint(4).getY() - y0)) + x0;
            y =  (proj_mat[2] * ((float) points.getPoint(4).getX() - x0) + proj_mat[3] * ((float) points.getPoint(4).getY() - y0)) + y0;
        }

        else if (modi == 2) {

            x =  ((float) points.getPoint(4).getX() - x0);
            y =  ((float) points.getPoint(4).getY() - y0);

            float dist = (x*shear_dy-y*shear_dx)/(float)Math.sqrt(shear_dx*shear_dx+shear_dy*shear_dy);

            x = x+shear_scale*dist*shear_dx+x0;
            y = y+shear_scale*dist*shear_dy+y0;
        }

        else if (modi==3) {

            cosd = (float) Math.cos( (2 * Math.PI - rot_angle));
            sind = (float) Math.sin( (2 * Math.PI - rot_angle));

            x =  (cosd * ((float) points.getPoint(4).getX() - x0) - sind * ((float) points.getPoint(4).getY() - y0)) + x0;
            y =  (sind * ((float) points.getPoint(4).getX() - x0) + cosd * ((float) points.getPoint(4).getY() - y0)) + y0;

        }

        Av.lineTo(x, y);
        float x_tmp = x;
        float y_tmp = y;


        AvArrow.reset();

        if (modi == 0) {
            x = cos2a * rot_arrow[0] + sin2a * rot_arrow[1] + x_tmp;
            y = sin2a * rot_arrow[0] - cos2a * rot_arrow[1] + y_tmp;
            AvArrow.moveTo(x, y);

            for (int i = 1; i < 3; i++) {
                x = cos2a * rot_arrow[2 * i] + sin2a * rot_arrow[2 * i + 1] + x_tmp;
                y = sin2a * rot_arrow[2 * i] - cos2a * rot_arrow[2 * i + 1] + y_tmp;

                AvArrow.lineTo(x, y);
            }
            x = cos2a * rot_arrow[0] + sin2a * rot_arrow[1] + x_tmp;
            y = sin2a * rot_arrow[0] - cos2a * rot_arrow[1] + y_tmp;
            AvArrow.lineTo(x, y);
        }

        else if (modi == 1) {
            double gamma1 = gamma;
            if(y_tmp-y0>0)
                gamma1=gamma+Math.PI;
            cosd = (float) Math.cos( 2 * Math.PI - gamma1);
            sind = (float) Math.sin( 2 * Math.PI - gamma1);


            x = cosd * arrow[0] - sind * arrow[1] + x_tmp;
            y = sind * arrow[0] + cosd * arrow[1] + y_tmp;
            AvArrow.moveTo(x, y);

            for (int i = 1; i < 3; i++) {
                x = cosd * arrow[2 * i] - sind * arrow[2 * i + 1] + x_tmp;
                y = sind * arrow[2 * i] + cosd * arrow[2 * i + 1] + y_tmp;

                AvArrow.lineTo(x, y);
            }
            x = cosd * arrow[0] - sind * arrow[1] + x_tmp;
            y = sind * arrow[0] + cosd * arrow[1] + y_tmp;
            AvArrow.lineTo(x, y);
        }


        else if (modi == 2) {

            float help_x = 1;
            float help_y = 0;
            float nx = x_tmp-x0;
            float ny = y_tmp-y0;

            float arc = (help_x*nx + help_y*ny)/((float)Math.sqrt(help_x*help_x+help_y*help_y)*(float)Math.sqrt(nx*nx+ny*ny));

            arc = (float)Math.acos(arc);

            if(ny>0) arc = 2*(float)Math.PI-arc;

            arc=2f*(float)Math.PI-arc;
            float sina = (float)Math.sin(arc);
            float cosa = (float)Math.cos(arc);

            AvArrow.moveTo(x, y);

            for (int i = 1; i < 3; i++) {
                x = cosa * arrow[2 * i] - sina * arrow[2 * i + 1] + x_tmp;
                y = sina * arrow[2 * i] + cosa * arrow[2 * i + 1] + y_tmp;

                AvArrow.lineTo(x, y);
            }
            x = cosa * arrow[0] - sina * arrow[1] + x_tmp;
            y = sina * arrow[0] + cosa * arrow[1] + y_tmp;
            AvArrow.lineTo(x, y);
        }


        else if (modi == 3) {
            x = cosd * rot_arrow[0] - sind * rot_arrow[1] + x_tmp;
            y = sind * rot_arrow[0] + cosd * rot_arrow[1] + y_tmp;
            AvArrow.moveTo(x, y);

            for (int i = 1; i < 3; i++) {
                x = cosd * rot_arrow[2 * i] - sind * rot_arrow[2 * i + 1] + x_tmp;
                y = sind * rot_arrow[2 * i] + cosd * rot_arrow[2 * i + 1] + y_tmp;

                AvArrow.lineTo(x, y);
            }
            x = cosd * rot_arrow[0] - sind * rot_arrow[1] + x_tmp;
            y = sind * rot_arrow[0] + cosd * rot_arrow[1] + y_tmp;
            AvArrow.lineTo(x, y);
        }


    }

    public void drawxAxis(float x1, float y1, float x2, float y2) {
        xaxis.reset();
        xaxis.moveTo(x1,y1);
        xaxis.lineTo(x2,y2);

        points.getPoint(0).setReelX(x1);
        points.getPoint(0).setReelY(y1);
        points.getPoint(0).setX(x1);
        points.getPoint(0).setY(y1);
        points.getPoint(0).setPaint(pointPaint[0]);
        points.getPoint(0).setIsAvailable(true);
        points.getPoint(0).setIsPlaced(true);

        points.getPoint(2).setReelX(x2);
        points.getPoint(2).setReelY(y2);
        points.getPoint(2).setX(x2);
        points.getPoint(2).setY(y2);
        points.getPoint(2).setPaint(pointPaint[1]);
        points.getPoint(2).setIsAvailable(true);
        points.getPoint(2).setIsPlaced(true);
        invalidate();
    }

    public void drawyAxis(float x1, float y1, float x2, float y2) {
        yaxis.reset();
        yaxis.moveTo(x1,y1);
        yaxis.lineTo(x2,y2);


        points.getPoint(1).setReelX(x1);
        points.getPoint(1).setReelY(y1);
        points.getPoint(1).setX(x1);
        points.getPoint(1).setY(y1);
        points.getPoint(1).setPaint(pointPaint[1]);
        points.getPoint(1).setIsAvailable(true);
        points.getPoint(1).setIsPlaced(true);

        points.getPoint(3).setReelX(x2);
        points.getPoint(3).setReelY(y2);
        points.getPoint(3).setX(x2);
        points.getPoint(3).setY(y2);
        points.getPoint(3).setPaint(pointPaint[3]);
        points.getPoint(3).setIsAvailable(true);
        points.getPoint(3).setIsPlaced(true);
        invalidate();
    }

    public void clear_paths() {
        xaxis.reset();
        yaxis.reset();
        polygon0.reset();
        polygon1.reset();
        v.reset();
        Av.reset();
        rotp1.reset();
        rotp2.reset();
    }

    PurePoints getPoints(){ return  points;}
    PurePoints getPolPoints(){ return  polpoints;}

    void set_init(){ this.init=true;}

    void set_modus(int modus) { this.modi = modus;}

    void set_rotanlge(float angle) { this.rot_angle = angle;}

    void set_shearvalue(float shear) {this.shear_scale = shear;}

    void touch_event_mod_0(float x_action, float y_action, int j) {


        float x0 = coordSystem.getX0();
        float y0 = coordSystem.getY0();

        if(j<4) {
            float x_old = (float)points.getPoint(j).getReelX();
            float y_old = (float)points.getPoint(j).getReelY();
            if(x_action > coordSystem.getX0() && y_action > coordSystem.getY0()) {

                float dy = y_action - coordSystem.getY0();
                float dx = x_action - coordSystem.getX0();
                float m = dy/dx;
                float b = coordSystem.getY0()-m*coordSystem.getX0();
                float val1 = (coordSystem.getySize()-b)/m;
                float val2 = m * coordSystem.getxSize()+b;

                if( val1 > coordSystem.getX0() && val1 < coordSystem.getxSize()) {
                    points.getPoint(j).setReelX(val1);
                    points.getPoint(j).setReelY(coordSystem.getySize()-10);
                    points.getPoint(j).setX(val1);
                    points.getPoint(j).setY(coordSystem.getySize()-10);
                }
                else
                {
                    points.getPoint(j).setReelX(coordSystem.getxSize()-10);
                    points.getPoint(j).setReelY(val2);
                    points.getPoint(j).setX(coordSystem.getxSize()-10);
                    points.getPoint(j).setY(val2);
                }
            }
            else if(x_action > coordSystem.getX0() && y_action < coordSystem.getY0()) {
                float dy = y_action - coordSystem.getY0();
                float dx = x_action - coordSystem.getX0();
                float m = dy/dx;
                float b = coordSystem.getY0()-m*coordSystem.getX0();
                float val1 = -b/m;
                float val2 = coordSystem.getxSize()*m+b;

                if( val1 > coordSystem.getX0() && val1 < coordSystem.getxSize()) {
                    points.getPoint(j).setReelX(val1);
                    points.getPoint(j).setReelY(10);
                    points.getPoint(j).setX(val1);
                    points.getPoint(j).setY(10);
                }
                else
                {
                    points.getPoint(j).setReelX(coordSystem.getxSize()-10);
                    points.getPoint(j).setReelY(val2);
                    points.getPoint(j).setX(coordSystem.getxSize()-10);
                    points.getPoint(j).setY(val2);
                }
            }
            else if(x_action < coordSystem.getX0() && y_action > coordSystem.getY0()) {

                float dy = y_action - coordSystem.getY0();
                float dx = x_action - coordSystem.getX0();
                float m = dy/dx;
                float b = coordSystem.getY0()-m*coordSystem.getX0();
                float val1 = (coordSystem.getySize()-b)/m;
                float val2 = b;

                if( val1 > 0 && val1 < coordSystem.getX0()) {
                    points.getPoint(j).setReelX(val1);
                    points.getPoint(j).setReelY(coordSystem.getySize()-10);
                    points.getPoint(j).setX(val1);
                    points.getPoint(j).setY(coordSystem.getySize()-10);
                }
                else
                {
                    points.getPoint(j).setReelX(10);
                    points.getPoint(j).setReelY(val2);
                    points.getPoint(j).setX(10);
                    points.getPoint(j).setY(val2);
                }
            }
            else if(x_action < coordSystem.getX0() && y_action < coordSystem.getY0()) {

                float dy = y_action - coordSystem.getY0();
                float dx = x_action - coordSystem.getX0();
                float m = dy/dx;
                float b = coordSystem.getY0()-m*coordSystem.getX0();
                float val = (0-b)/m;

                if( b>0 && b < coordSystem.getY0()) {
                    points.getPoint(j).setReelX(10);
                    points.getPoint(j).setReelY(b);
                    points.getPoint(j).setX(10);
                    points.getPoint(j).setY(b);
                }
                else
                {
                    points.getPoint(j).setReelX(val);
                    points.getPoint(j).setReelY(10);
                    points.getPoint(j).setX(val);
                    points.getPoint(j).setY(10);
                }
            }

            x_action = (x_action-coordSystem.getX0())/coordSystem.getxLen();
            y_action = (y_action-coordSystem.getY0())/coordSystem.getyLen();


            for(int i = 1 ; i < 4 ; i++)
            {
                int idx_next = (j+i)%4;

                float x = (float)points.getPoint(j).getX()-coordSystem.getX0();
                float y = (float)points.getPoint(j).getY()-coordSystem.getY0();

                float x1 = rot[0+(i-1)*4]*x + rot[1+(i-1)*4]*y + coordSystem.getX0();
                float y1 = rot[2+(i-1)*4]*x + rot[3+(i-1)*4]*y + coordSystem.getY0();


                points.getPoint(idx_next).setReelX(x1);
                points.getPoint(idx_next).setX(x1);
                points.getPoint(idx_next).setReelY(y1);
                points.getPoint(idx_next).setY(y1);

            }

            float help_x = (float)points.getPoint(1).getX()-x0;
            float help_y = (float)points.getPoint(1).getY()-y0;
            float nx = coordSystem.getxLen();
            float ny = 0;
            float tmp;

            tmp = (help_x*nx+help_y*ny)/((float)Math.sqrt(help_x*help_x+help_y*help_y)*(float)Math.sqrt(nx*nx+ny*ny));

            alpha = (float)Math.acos(tmp);
            if(help_y>0) alpha = (float)Math.PI-alpha;
        }
        else
        {
            points.getPoint(4).setReelX(x_action);
            points.getPoint(4).setX(x_action);
            points.getPoint(4).setReelY(y_action);
            points.getPoint(4).setY(y_action);

            float help_x = x_action-x0;
            float help_y = y_action-y0;
            float nx = coordSystem.getxLen();
            float ny = 0;
            float tmp;

            tmp = (help_x*nx+help_y*ny)/((float)Math.sqrt(help_x*help_x+help_y*help_y)*(float)Math.sqrt(nx*nx+ny*ny));

            beta = (float)Math.acos(tmp);
            if(help_y>0) beta = 2*(float)Math.PI-beta;
        }
    }


    void touch_event_mod_1(float x_action, float y_action, int j) {


        float x0 = coordSystem.getX0();
        float y0 = coordSystem.getY0();

        if(j<4) {
            float x_old = (float)points.getPoint(j).getReelX();
            float y_old = (float)points.getPoint(j).getReelY();
            if(x_action > coordSystem.getX0() && y_action > coordSystem.getY0()) {

                float dy = y_action - coordSystem.getY0();
                float dx = x_action - coordSystem.getX0();
                float m = dy/dx;
                float b = coordSystem.getY0()-m*coordSystem.getX0();
                float val1 = (coordSystem.getySize()-b)/m;
                float val2 = m * coordSystem.getxSize()+b;

                if( val1 > coordSystem.getX0() && val1 < coordSystem.getxSize()) {
                    points.getPoint(j).setReelX(val1);
                    points.getPoint(j).setReelY(coordSystem.getySize()-10);
                    points.getPoint(j).setX(val1);
                    points.getPoint(j).setY(coordSystem.getySize()-10);
                }
                else
                {
                    points.getPoint(j).setReelX(coordSystem.getxSize()-10);
                    points.getPoint(j).setReelY(val2);
                    points.getPoint(j).setX(coordSystem.getxSize()-10);
                    points.getPoint(j).setY(val2);
                }
            }
            else if(x_action > coordSystem.getX0() && y_action < coordSystem.getY0()) {
                float dy = y_action - coordSystem.getY0();
                float dx = x_action - coordSystem.getX0();
                float m = dy/dx;
                float b = coordSystem.getY0()-m*coordSystem.getX0();
                float val1 = -b/m;
                float val2 = coordSystem.getxSize()*m+b;

                if( val1 > coordSystem.getX0() && val1 < coordSystem.getxSize()) {
                    points.getPoint(j).setReelX(val1);
                    points.getPoint(j).setReelY(10);
                    points.getPoint(j).setX(val1);
                    points.getPoint(j).setY(10);
                }
                else
                {
                    points.getPoint(j).setReelX(coordSystem.getxSize()-10);
                    points.getPoint(j).setReelY(val2);
                    points.getPoint(j).setX(coordSystem.getxSize()-10);
                    points.getPoint(j).setY(val2);
                }
            }
            else if(x_action < coordSystem.getX0() && y_action > coordSystem.getY0()) {

                float dy = y_action - coordSystem.getY0();
                float dx = x_action - coordSystem.getX0();
                float m = dy/dx;
                float b = coordSystem.getY0()-m*coordSystem.getX0();
                float val1 = (coordSystem.getySize()-b)/m;
                float val2 = b;

                if( val1 > 0 && val1 < coordSystem.getX0()) {
                    points.getPoint(j).setReelX(val1);
                    points.getPoint(j).setReelY(coordSystem.getySize()-10);
                    points.getPoint(j).setX(val1);
                    points.getPoint(j).setY(coordSystem.getySize()-10);
                }
                else
                {
                    points.getPoint(j).setReelX(10);
                    points.getPoint(j).setReelY(val2);
                    points.getPoint(j).setX(10);
                    points.getPoint(j).setY(val2);
                }
            }
            else if(x_action < coordSystem.getX0() && y_action < coordSystem.getY0()) {

                float dy = y_action - coordSystem.getY0();
                float dx = x_action - coordSystem.getX0();
                float m = dy/dx;
                float b = coordSystem.getY0()-m*coordSystem.getX0();
                float val = (0-b)/m;

                if( b>0 && b < coordSystem.getY0()) {
                    points.getPoint(j).setReelX(10);
                    points.getPoint(j).setReelY(b);
                    points.getPoint(j).setX(10);
                    points.getPoint(j).setY(b);
                }
                else
                {
                    points.getPoint(j).setReelX(val);
                    points.getPoint(j).setReelY(10);
                    points.getPoint(j).setX(val);
                    points.getPoint(j).setY(10);
                }
            }

            x_action = (x_action-coordSystem.getX0())/coordSystem.getxLen();
            y_action = (y_action-coordSystem.getY0())/coordSystem.getyLen();



            int idx_next = (j+2)%4;

            float x = (float)points.getPoint(j).getX()-coordSystem.getX0();
            float y = (float)points.getPoint(j).getY()-coordSystem.getY0();

            float x1 = rot[4]*x + rot[5]*y + coordSystem.getX0();
            float y1 = rot[6]*x + rot[7]*y + coordSystem.getY0();


            points.getPoint(idx_next).setReelX(x1);
            points.getPoint(idx_next).setX(x1);
            points.getPoint(idx_next).setReelY(y1);
            points.getPoint(idx_next).setY(y1);



            float help_x = (float)points.getPoint(1).getX()-x0;
            float help_y = (float)points.getPoint(1).getY()-y0;
            float nx = coordSystem.getxLen();
            float ny = 0;
            float tmp;

            tmp = (help_x*nx+help_y*ny)/((float)Math.sqrt(help_x*help_x+help_y*help_y)*(float)Math.sqrt(nx*nx+ny*ny));

            gamma = (float)Math.acos(tmp);
            if(help_y>0) gamma = 2*(float)Math.PI-gamma;

            if(j==0  || j==2) {//Projektionsrichtung berechnen
                float p1x = (float)points.getPoint(0).getX()-x0;
                float p1y = -((float)points.getPoint(0).getY()-y0);
                float p2x = (float)points.getPoint(2).getX()-x0;
                float p2y = -((float)points.getPoint(2).getY()-y0);
                proj_dir[0] = (p1x-p2x)/coordSystem.getxLen();
                proj_dir[1] = -(p1y-p2y)/coordSystem.getyLen();

            }
            else
            {
                float p1x = (float)points.getPoint(1).getX()-x0;
                float p1y = -((float)points.getPoint(1).getY()-y0);
                float p2x = (float)points.getPoint(3).getX()-x0;
                float p2y = -((float)points.getPoint(3).getY()-y0);
                slope_proj_target = (p1x-p2x)/(p1y-p2y);

            }
            float div = proj_dir[0]+slope_proj_target*proj_dir[1];
            proj_mat[0] = 1-proj_dir[0]/div;
            proj_mat[1] = -proj_dir[0]*slope_proj_target/div;
            proj_mat[2] = -proj_dir[1]/div;
            proj_mat[3] = 1-proj_dir[1]*slope_proj_target/div;

        }
        else
        {
            points.getPoint(4).setReelX(x_action);
            points.getPoint(4).setX(x_action);
            points.getPoint(4).setReelY(y_action);
            points.getPoint(4).setY(y_action);

            float help_x = x_action-x0;
            float help_y = y_action-y0;
            float nx = coordSystem.getxLen();
            float ny = 0;
            float tmp;

            tmp = (help_x*nx+help_y*ny)/((float)Math.sqrt(help_x*help_x+help_y*help_y)*(float)Math.sqrt(nx*nx+ny*ny));

            beta = (float)Math.acos(tmp);
            if(help_y>0) beta = 2*(float)Math.PI-beta;
        }

    }


    void touch_event_mod_2(float x_action, float y_action, int j) {


        float x0 = coordSystem.getX0();
        float y0 = coordSystem.getY0();

        if(j<4) {
            float x_old = (float)points.getPoint(j).getReelX();
            float y_old = (float)points.getPoint(j).getReelY();
            if(x_action > coordSystem.getX0() && y_action > coordSystem.getY0()) {

                float dy = y_action - coordSystem.getY0();
                float dx = x_action - coordSystem.getX0();
                float m = dy/dx;
                float b = coordSystem.getY0()-m*coordSystem.getX0();
                float val1 = (coordSystem.getySize()-b)/m;
                float val2 = m * coordSystem.getxSize()+b;

                if( val1 > coordSystem.getX0() && val1 < coordSystem.getxSize()) {
                    points.getPoint(j).setReelX(val1);
                    points.getPoint(j).setReelY(coordSystem.getySize()-10);
                    points.getPoint(j).setX(val1);
                    points.getPoint(j).setY(coordSystem.getySize()-10);
                }
                else
                {
                    points.getPoint(j).setReelX(coordSystem.getxSize()-10);
                    points.getPoint(j).setReelY(val2);
                    points.getPoint(j).setX(coordSystem.getxSize()-10);
                    points.getPoint(j).setY(val2);
                }
            }
            else if(x_action > coordSystem.getX0() && y_action < coordSystem.getY0()) {
                float dy = y_action - coordSystem.getY0();
                float dx = x_action - coordSystem.getX0();
                float m = dy/dx;
                float b = coordSystem.getY0()-m*coordSystem.getX0();
                float val1 = -b/m;
                float val2 = coordSystem.getxSize()*m+b;

                if( val1 > coordSystem.getX0() && val1 < coordSystem.getxSize()) {
                    points.getPoint(j).setReelX(val1);
                    points.getPoint(j).setReelY(10);
                    points.getPoint(j).setX(val1);
                    points.getPoint(j).setY(10);
                }
                else
                {
                    points.getPoint(j).setReelX(coordSystem.getxSize()-10);
                    points.getPoint(j).setReelY(val2);
                    points.getPoint(j).setX(coordSystem.getxSize()-10);
                    points.getPoint(j).setY(val2);
                }
            }
            else if(x_action < coordSystem.getX0() && y_action > coordSystem.getY0()) {

                float dy = y_action - coordSystem.getY0();
                float dx = x_action - coordSystem.getX0();
                float m = dy/dx;
                float b = coordSystem.getY0()-m*coordSystem.getX0();
                float val1 = (coordSystem.getySize()-b)/m;
                float val2 = b;

                if( val1 > 0 && val1 < coordSystem.getX0()) {
                    points.getPoint(j).setReelX(val1);
                    points.getPoint(j).setReelY(coordSystem.getySize()-10);
                    points.getPoint(j).setX(val1);
                    points.getPoint(j).setY(coordSystem.getySize()-10);
                }
                else
                {
                    points.getPoint(j).setReelX(10);
                    points.getPoint(j).setReelY(val2);
                    points.getPoint(j).setX(10);
                    points.getPoint(j).setY(val2);
                }
            }
            else if(x_action < coordSystem.getX0() && y_action < coordSystem.getY0()) {

                float dy = y_action - coordSystem.getY0();
                float dx = x_action - coordSystem.getX0();
                float m = dy/dx;
                float b = coordSystem.getY0()-m*coordSystem.getX0();
                float val = (0-b)/m;

                if( b>0 && b < coordSystem.getY0()) {
                    points.getPoint(j).setReelX(10);
                    points.getPoint(j).setReelY(b);
                    points.getPoint(j).setX(10);
                    points.getPoint(j).setY(b);
                }
                else
                {
                    points.getPoint(j).setReelX(val);
                    points.getPoint(j).setReelY(10);
                    points.getPoint(j).setX(val);
                    points.getPoint(j).setY(10);
                }
            }

            x_action = (x_action-coordSystem.getX0())/coordSystem.getxLen();
            y_action = (y_action-coordSystem.getY0())/coordSystem.getyLen();



            int idx_next = (j+2)%4;

            float x = (float)points.getPoint(j).getX()-coordSystem.getX0();
            float y = (float)points.getPoint(j).getY()-coordSystem.getY0();

            float x1 = rot[4]*x + rot[5]*y + coordSystem.getX0();
            float y1 = rot[6]*x + rot[7]*y + coordSystem.getY0();


            points.getPoint(idx_next).setReelX(x1);
            points.getPoint(idx_next).setX(x1);
            points.getPoint(idx_next).setReelY(y1);
            points.getPoint(idx_next).setY(y1);



            float help_x = (float)points.getPoint(1).getX()-x0;
            float help_y = (float)points.getPoint(1).getY()-y0;
            float nx = coordSystem.getxLen();
            float ny = 0;
            float tmp;

            tmp = (help_x*nx+help_y*ny)/((float)Math.sqrt(help_x*help_x+help_y*help_y)*(float)Math.sqrt(nx*nx+ny*ny));

            gamma = (float)Math.acos(tmp);
            if(help_y>0) gamma = 2*(float)Math.PI-gamma;

            float p1x = (float)points.getPoint(1).getX()-x0;
            float p1y = -((float)points.getPoint(1).getY()-y0);
            float p2x = (float)points.getPoint(3).getX()-x0;
            float p2y = -((float)points.getPoint(3).getY()-y0);
            shear_dx = (p1x-p2x)/coordSystem.getxSize();
            shear_dy = -(p1y-p2y)/coordSystem.getySize();

        }
        else
        {
            points.getPoint(4).setReelX(x_action);
            points.getPoint(4).setX(x_action);
            points.getPoint(4).setReelY(y_action);
            points.getPoint(4).setY(y_action);

            float help_x = x_action-x0;
            float help_y = y_action-y0;
            float nx = coordSystem.getxLen();
            float ny = 0;
            float tmp;

            tmp = (help_x*nx+help_y*ny)/((float)Math.sqrt(help_x*help_x+help_y*help_y)*(float)Math.sqrt(nx*nx+ny*ny));

            beta = (float)Math.acos(tmp);
            if(help_y>0) beta = 2*(float)Math.PI-beta;
        }

    }


    void touch_event_mod_3(float x_action, float y_action, int j) {


        float x0 = coordSystem.getX0();
        float y0 = coordSystem.getY0();

        if(j==4) {
            points.getPoint(4).setReelX(x_action);
            points.getPoint(4).setX(x_action);
            points.getPoint(4).setReelY(y_action);
            points.getPoint(4).setY(y_action);

            float help_x = x_action-x0;
            float help_y = y_action-y0;
            float nx = coordSystem.getxLen();
            float ny = 0;
            float tmp;

            tmp = (help_x*nx+help_y*ny)/((float)Math.sqrt(help_x*help_x+help_y*help_y)*(float)Math.sqrt(nx*nx+ny*ny));

            beta = (float)Math.acos(tmp);
            if(help_y>0) beta = 2*(float)Math.PI-beta;
        }
    }

}
