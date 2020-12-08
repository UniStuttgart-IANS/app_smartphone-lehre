package com.uni_stuttgart.isl.ZeroPoint;

import com.uni_stuttgart.isl.CoolStuff.NumLinAlg;
import com.uni_stuttgart.isl.Function.Function;
import com.uni_stuttgart.isl.Function.PolynomialFunction;

/**
 * Created by nborg on 17.08.15.
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
    // Defintionsbereich
    private double xMax;
    private double xMin;
    // Maximaler/Minimaler angenommener Wert meiner Funktion
    private double yMax;
    private double yMin;
    // Abstand der Skalierungsstriche (Tics)
    private float dTic = 30;
    // Länger der Skalierugsstriche (Tics)
    private float lenTic = 8;
    //Anzahl der Skalierungsstriche (Tics)
    private int numXTic;
    private int numYTic;
    // Art des Koordinatensystems (1 -> 1. Qaudrant, 2-> 1./4. Quadrant, 3-> 1./2. Qaudrant 4-> full)
    private int CoordArt;
    // Anzahl der Pixel in x,y Richtung
    private int numXPix;
    private int numYPix;
    //Hilfsprameter (numXPix gerade -> xFlag = 0, numXPix ungerade -> xFlag = 1)
    private int xFlag;
    private int yFlag;
    // Vektoren für die "echten" Funktionswerte
    private double[] xFunc;
    private double[] yFunc;
    // Skalierungsfaktor für die Transformation auf mein Koordinatensystem
    private double yScale;
    // Vektoren für die auf mein Koordinatensystem transformierten Funktionswerte
    private double[] xFuncTrans;
    private double[] yFuncTrans;
    // Vektoren für die Anfangs- Endpuntke der "Tics"  [x1, y1, x2, y2....]
    private float[] xTics;
    private float[] yTics;
    // Vektor mit den Koordinaten der Achsenpfeilen
    private float[] Arrows;
    // Hilfsparameter (nur positive x-Achse: 1, positive + negative x-Achse: 2)
    private int CoordArtFlag;
    // Zu plottende Funktion
    private Function function;
    // Zu plottender Gradient
    private PolynomialFunction gradient;
    private double[] xGrad;
    private double[] xGradTrans;
    private double[] yGrad;
    private double[] yGradTrans;

    // Werte (Vektoren...) fürs Zeichnen des Koordinaten-System bestimmen
    public CoordSystem(float x0, float y0, float xLen, float yLen, int CoordArt, MyCanvas myCanvas) {

        // Setzen des Koordinatenursprungs
        this.x0 = x0;
        this.y0 = y0;

        // Setzen der Achsenlänge
        this.xLen = xLen;
        this.yLen = yLen;

        // Setzen des Koordinatensystems
        this.CoordArt = CoordArt;

        // Setzen des Canvas, auf dem gezeichnet werden soll
        this.myCanvas = myCanvas;

        //Berechnung der Anzahl der Tics
        numXTic = (int) (xLen / dTic);
        numYTic = (int) (yLen / dTic);

        // Berechnung der Pixelanzahl für die x,y-Richtung
        numXPix = (int) xLen;
        numYPix = (int) yLen;

        // Anlegen der Vektoren für die Arrows/Tics
        Arrows = new float[16];
        xTics = new float[8 * numXTic];
        yTics = new float[8 * numYTic];

        // Gerade oder Ungerade Anzahl an Skalierungsstrichen
        if (numXTic % 2 == 0) {
            xFlag = 0;
        } else {
            xFlag = 1;
        }
        if (numYTic % 2 == 0) {
            yFlag = 0;
        } else {
            yFlag = 1;
        }

        // Art des Koordinatensystems (1 -> 1. Qaudrant, 2-> 1./4. Quadrant, 3-> 1./2. Qaudrant 4->full)
        if (CoordArt == 1 || CoordArt == 2) {
            CoordArtFlag = 1;
        }
        if (CoordArt == 3 || CoordArt == 4) {
            CoordArtFlag = 2;
        }
    }

    // Zeichnen einer Funktion
    public void drawFunc() {
        for (int i = 1; i < CoordArtFlag * numXPix + 1; i = i + 2) {
            myCanvas.drawXtoY_func((float) xFuncTrans[i - 1], (float) yFuncTrans[i - 1], (float) xFuncTrans[i], (float) yFuncTrans[i], i);
        }
    }

    // Zeichnen einer Funktion
    public void drawGrad() {
        for (int i = 1; i < CoordArtFlag * numXPix + 1; i = i + 2) {
            myCanvas.drawXtoY_grad((float) xGradTrans[i - 1], (float) yGradTrans[i - 1], (float) xGradTrans[i], (float) yGradTrans[i], i);
        }
    }


    public void setxFunc(double xMin, double xMax) {
        // Setzen des Definitionsbereiches
        this.xMin = xMin;
        this.xMax = xMax;

        // Verschiedene Vektoren für die X-Koordianten, je nachdem welches Koordinatensystem gewählt wurde
        if (CoordArt == 1 || CoordArt == 2) {

            // Anlegen der entsprechenden Vektoren (Länge: numXPix, da nur positive x-Achse)
            xFunc = new double[numXPix + 1];
            xFuncTrans = new double[numXPix + 1];
            for (int i = 0; i < numXPix + 1; i++) {
                // xFunc enthält die "echten" x-Werte, während xFuncTrans die auf das Koordiantensystem gemappten Werte enthält.
                // Hierbei gilt: Ein Pixel = ein x-Wert
                xFunc[i] = xMin + (1.0 * i) * xMax / numXPix;

                // xFuncTrans sind die x-Koordinaten zum plottten
                xFuncTrans[i] = x0 + i;
            }
        }
        if (CoordArt == 3 || CoordArt == 4) {
            // Anlegen der entsprechenden Vektoren (Länge: 2*numXPix, da positive UND negate x-Achse)
            xFunc = new double[2 * numXPix + 1];
            xFuncTrans = new double[2 * numXPix + 1];
            for (int i = 0; i < 2 * numXPix + 1; i++) {
                // xFunc enthält die "echten" x-Werte, während xFuncTrans die auf das Koordiantensystem gemappten Werte enthält.
                // Hierbei gilt: Ein Pixel = ein x-Wert
                xFunc[i] = xMin + (1.0 * i) * xMax / numXPix;
                // xFuncTrans sind die x-Koordinaten zum plottten
                xFuncTrans[i] = x0 - xLen + i;
            }
        }
    }

    public void setxGrad(double xMin, double xMax) {
        // Setzen des Definitionsbereiches
        this.xMin = xMin;
        this.xMax = xMax;

        // Verschiedene Vektoren für die X-Koordianten, je nachdem welches Koordinatensystem gewählt wurde
        if (CoordArt == 1 || CoordArt == 2) {

            // Anlegen der entsprechenden Vektoren (Länge: numXPix, da nur positive x-Achse)
            xGrad = new double[numXPix + 1];
            xGradTrans = new double[numXPix + 1];
            for (int i = 0; i < numXPix + 1; i++) {
                // xFunc enthält die "echten" x-Werte, während xFuncTrans die auf das Koordiantensystem gemappten Werte enthält.
                // Hierbei gilt: Ein Pixel = ein x-Wert
                xGrad[i] = xMin + (1.0 * i) * xMax / numXPix;

                // xFuncTrans sind die x-Koordinaten zum plottten
                xGradTrans[i] = x0 + i;
            }
        }
        if (CoordArt == 3 || CoordArt == 4) {
            // Anlegen der entsprechenden Vektoren (Länge: 2*numXPix, da positive UND negate x-Achse)
            xGrad = new double[2 * numXPix + 1];
            xGradTrans = new double[2 * numXPix + 1];
            for (int i = 0; i < 2 * numXPix + 1; i++) {
                // xGrad enthält die "echten" x-Werte, während xGradTrans die auf das Koordiantensystem gemappten Werte enthält.
                // Hierbei gilt: Ein Pixel = ein x-Wert
                xGrad[i] = xMin + (1.0 * i) * xMax / numXPix;
                // xGradTrans sind die x-Koordinaten zum plottten
                xGradTrans[i] = x0 - xLen + i;
            }
        }
    }


    public void drawAxis() {
        // Setzen der Koordinaten der Achsenpfeile
        setArrows();
        // Zeichnen der Achsenpfeile
        for (int i = 0; i < 4; i++) {
            myCanvas.drawXtoY_cs(Arrows[4 * i + 0], Arrows[4 * i + 1], Arrows[4 * i + 2], Arrows[4 * i + 3]);
        }

        // Zeichnen der Achsen
        switch (CoordArt) {
            case 1:
                // Zeichne y-Achse
                myCanvas.drawXtoY_cs(x0, y0, x0, y0 - yLen);
                // Zeichne x-Achse
                myCanvas.drawXtoY_cs(x0, y0, x0 + xLen, y0);
                // Zeichnen der Skalierungsstriche
                setXTics();
                setyTics();
                for (int i = 0; i < numXTic; i++) {
                    myCanvas.drawXtoY_cs(xTics[4 * i + 0], xTics[4 * i + 1], xTics[4 * i + 2], xTics[4 * i + 3]);
                }
                for (int i = 0; i < 2 * numYTic; i++) {
                    myCanvas.drawXtoY_cs(yTics[4 * i + 0], yTics[4 * i + 1], yTics[4 * i + 2], yTics[4 * i + 3]);
                }
                break;
            case 2:
                // Zeichne y-Achse
                myCanvas.drawXtoY_cs(x0, y0 + yLen, x0, y0 - yLen);
                // Zeichne x-Achse
                myCanvas.drawXtoY_cs(x0, y0, x0 + xLen, y0);
                // Zeichnen der Skalierungsstriche
                setXTics();
                for (int i = 0; i < numXTic; i++) {
                    myCanvas.drawXtoY_cs(xTics[4 * i + 0], xTics[4 * i + 1], xTics[4 * i + 2], xTics[4 * i + 3]);
                }
                setyTics();
                for (int i = 0; i < 2 * numYTic; i++) {
                    myCanvas.drawXtoY_cs(yTics[4 * i + 0], yTics[4 * i + 1], yTics[4 * i + 2], yTics[4 * i + 3]);
                }
                break;
            case 3:
                // Zeichne y-Achse
                myCanvas.drawXtoY_cs(x0, y0, x0, y0 - yLen);
                // Zeichne x-Achse
                myCanvas.drawXtoY_cs(x0 - xLen, y0, x0 + xLen, y0);
                // Zeichnen der Skalierungsstriche
                setXTics();
                for (int i = 0; i < 2 * numXTic; i++) {
                    myCanvas.drawXtoY_cs(xTics[4 * i + 0], xTics[4 * i + 1], xTics[4 * i + 2], xTics[4 * i + 3]);
                }
                setyTics();
                for (int i = 0; i < numYTic; i++) {
                    myCanvas.drawXtoY_cs(yTics[4 * i + 0], yTics[4 * i + 1], yTics[4 * i + 2], yTics[4 * i + 3]);
                }
                break;
            case 4:
                // Zeichne y-Achse
                myCanvas.drawXtoY_cs(x0, y0 + yLen, x0, y0 - yLen);
                // Zeichne x-Achse
                myCanvas.drawXtoY_cs(x0 - xLen, y0, x0 + xLen, y0);
                setXTics();
                setyTics();
                for (int i = 0; i < 2 * numXTic; i++) {
                    myCanvas.drawXtoY_cs(xTics[4 * i + 0], xTics[4 * i + 1], xTics[4 * i + 2], xTics[4 * i + 3]);
                }
                for (int i = 0; i < 2 * numYTic; i++) {
                    myCanvas.drawXtoY_cs(yTics[4 * i + 0], yTics[4 * i + 1], yTics[4 * i + 2], yTics[4 * i + 3]);
                }
                break;
        }
    }

    public void setArrows() {
        // Unterer Pfeil x-Axis
        Arrows[0] = x0 + xLen;
        Arrows[1] = y0;
        Arrows[2] = x0 + xLen - lenTic;
        Arrows[3] = y0 + lenTic;
        // Oberer Pfeil x-Axis
        Arrows[4] = x0 + xLen;
        Arrows[5] = y0;
        Arrows[6] = x0 + xLen - lenTic;
        Arrows[7] = y0 - lenTic;
        // Linker Pfeil y-Axis
        Arrows[8] = x0;
        Arrows[9] = y0 - yLen;
        Arrows[10] = x0 - lenTic;
        Arrows[11] = y0 - yLen + lenTic;
        // Rechter Pfeil y-Axis
        Arrows[12] = x0;
        Arrows[13] = y0 - yLen;
        Arrows[14] = x0 + lenTic;
        Arrows[15] = y0 - yLen + lenTic;
    }

    public void setXTics() {
        switch (CoordArt) {
            case 1:
                for (int i = 0; i < numXTic; i++) {
                    xTics[4 * i + 0] = x0 + (i + 1) * dTic;
                    xTics[4 * i + 1] = y0 + lenTic;
                    xTics[4 * i + 2] = x0 + (i + 1) * dTic;
                    xTics[4 * i + 3] = y0 - lenTic;
                }
                break;
            case 2:
                for (int i = 0; i < numXTic; i++) {
                    xTics[4 * i + 0] = x0 + (i + 1) * dTic;
                    xTics[4 * i + 1] = y0 + lenTic;
                    xTics[4 * i + 2] = x0 + (i + 1) * dTic;
                    xTics[4 * i + 3] = y0 - lenTic;
                }
                break;
            case 3:
                for (int i = 0; i < numXTic; i++) {
                    xTics[4 * i + 0] = x0 - (i + 1) * dTic;
                    xTics[4 * i + 1] = y0 + lenTic;
                    xTics[4 * i + 2] = x0 - (i + 1) * dTic;
                    xTics[4 * i + 3] = y0 - lenTic;
                }

                for (int i = numXTic; i < 2 * numXTic; i++) {
                    xTics[4 * i + 0] = x0 + (i + 1 - numXTic) * dTic;
                    xTics[4 * i + 1] = y0 + lenTic;
                    xTics[4 * i + 2] = x0 + (i + 1 - numXTic) * dTic;
                    xTics[4 * i + 3] = y0 - lenTic;
                }
                break;
            case 4:
                for (int i = 0; i < numXTic; i++) {
                    xTics[4 * i + 0] = x0 - (i + 1) * dTic;
                    xTics[4 * i + 1] = y0 + lenTic;
                    xTics[4 * i + 2] = x0 - (i + 1) * dTic;
                    xTics[4 * i + 3] = y0 - lenTic;
                }

                for (int i = numXTic; i < 2 * numXTic; i++) {
                    xTics[4 * i + 0] = x0 + (i + 1 - numXTic) * dTic;
                    xTics[4 * i + 1] = y0 + lenTic;
                    xTics[4 * i + 2] = x0 + (i + 1 - numXTic) * dTic;
                    xTics[4 * i + 3] = y0 - lenTic;
                }
                break;
        }
    }

    public void setyTics() {
        switch (CoordArt) {
            case 1:
                for (int i = 0; i < numYTic; i++) {
                    yTics[4 * i + 0] = x0 - lenTic;
                    yTics[4 * i + 1] = y0 - (i + 1) * dTic;
                    yTics[4 * i + 2] = x0 + lenTic;
                    yTics[4 * i + 3] = y0 - (i + 1) * dTic;
                }
                break;
            case 2:
                for (int i = 0; i < numYTic; i++) {
                    yTics[4 * i + 0] = x0 - lenTic;
                    yTics[4 * i + 1] = y0 + (i + 1) * dTic;
                    yTics[4 * i + 2] = x0 + lenTic;
                    yTics[4 * i + 3] = y0 + (i + 1) * dTic;
                }
                for (int i = numYTic; i < 2 * numYTic; i++) {
                    yTics[4 * i + 0] = x0 - lenTic;
                    yTics[4 * i + 1] = y0 - (i + 1 - numYTic) * dTic;
                    yTics[4 * i + 2] = x0 + lenTic;
                    yTics[4 * i + 3] = y0 - (i + 1 - numYTic) * dTic;
                }
                break;
            case 3:
                for (int i = 0; i < numYTic; i++) {
                    yTics[4 * i + 0] = x0 - lenTic;
                    yTics[4 * i + 1] = y0 - (i + 1) * dTic;
                    yTics[4 * i + 2] = x0 + lenTic;
                    yTics[4 * i + 3] = y0 - (i + 1) * dTic;
                }
                break;
            case 4:
                for (int i = 0; i < numYTic; i++) {
                    yTics[4 * i + 0] = x0 - lenTic;
                    yTics[4 * i + 1] = y0 + (i + 1) * dTic;
                    yTics[4 * i + 2] = x0 + lenTic;
                    yTics[4 * i + 3] = y0 + (i + 1) * dTic;
                }
                for (int i = numYTic; i < 2 * numYTic; i++) {
                    yTics[4 * i + 0] = x0 - lenTic;
                    yTics[4 * i + 1] = y0 - (i + 1 - numYTic) * dTic;
                    yTics[4 * i + 2] = x0 + lenTic;
                    yTics[4 * i + 3] = y0 - (i + 1 - numYTic) * dTic;
                }
                break;
        }
    }


    public double[] getxFunc() {
        return xFunc;
    }

    public void setyFunc(Function function) {
        this.function = function;
        // Verschiedene Vektoren für die Y-Koordianten, je nachdem welches Koordinatensystem gewählt wurde
        if (CoordArt == 1 || CoordArt == 2) {
            // Anlegen der entsprechenden Vektoren (Länge: numXPix, da nur positive y-Achse)
            yFunc = new double[numXPix + 1];
            for (int i = 0; i < numXPix + 1; i++) {
                // xFunc enthält die "echten" x-Werte, während xFuncTrans die uf das Koordiantensystem gemappten Werte enthält.
                // Hierbei gilt: Pro Pixel/x-Wert = ein y-Wert
                yFunc[i] = function.evaluate(xFunc[i]);
            }
        }
        if (CoordArt == 3 || CoordArt == 4) {
            // Anlegen der entsprechenden Vektoren (Länge: 2*numXPix, da positive UND negate y-Achse)
            yFunc = new double[2 * numXPix + 1];
            for (int i = 0; i < 2 * numXPix + 1; i++) {
                // yFunc enthält die "echten" y-Werte, während yFuncTrans die auf das Koordiantensystem gemappten Werte enthält.
                // Hierbei gilt: Pro Pixel/x-Wert = ein y-Wert
                yFunc[i] = function.evaluate(xFunc[i]);
            }
        }

        // Berechnen des Maximalen/Minimalen y-Wertes zur Bestimmung der Größe des Koordinatesystems
        yMax = NumLinAlg.getMax(yFunc);
        yMin = NumLinAlg.getMin(yFunc);

        // Überprüfung, ob der kleinste Wert im Betrag größer ist als der größte positive Wert (gleiche Achsenskalierung für positive wie negative Achse)
        if (yMax < Math.abs(yMin)) {
            yMax = Math.abs(yMin);
        }

        // Ausrechnen des Faktores um die "echte" y-Werte auf das Koordinatensystem zu mappen
        yScale = yLen / yMax;

        // Setzen der transformierten y-Werte, abhängig vom Koordinatensystem
        if (CoordArt == 1 || CoordArt == 2) {
            // Anlegen der entsprechenden Vektoren (Länge: numXPix, da nur positive y-Achse)
            yFuncTrans = new double[numXPix + 1];
            for (int i = 0; i < numXPix + 1; i++) {
                // xFunc enthält die "echten" x-Werte, während xFuncTrans die uf das Koordiantensystem gemappten Werte enthält.
                // Hierbei gilt: Pro Pixel/x-Wert = ein y-Wert
                yFuncTrans[i] = -(yFunc[i] * yScale) + y0;
            }
        }
        if (CoordArt == 3 || CoordArt == 4) {
            // Anlegen der entsprechenden Vektoren (Länge: 2*numXPix, da positive UND negate y-Achse)
            yFuncTrans = new double[2 * numXPix + 1];
            for (int i = 0; i < 2 * numXPix + 1; i++) {
                // yFunc enthält die "echten" y-Werte, während yFuncTrans die auf das Koordiantensystem gemappten Werte enthält.
                // Hierbei gilt: Pro Pixel/x-Wert = ein y-Wert
                yFuncTrans[i] = -(yFunc[i] * yScale) + y0;
            }
        }
    }

    public void setyGrad(PolynomialFunction gradient) {
        this.gradient = gradient;
        // Verschiedene Vektoren für die Y-Koordianten, je nachdem welches Koordinatensystem gewählt wurde
        if (CoordArt == 1 || CoordArt == 2) {
            // Anlegen der entsprechenden Vektoren (Länge: numXPix, da nur positive y-Achse)
            yGrad = new double[numXPix + 1];
            for (int i = 0; i < numXPix + 1; i++) {
                // xFunc enthält die "echten" x-Werte, während xFuncTrans die auf das Koordiantensystem gemappten Werte enthält.
                // Hierbei gilt: Pro Pixel/x-Wert = ein y-Wert
                yGrad[i] = gradient.evaluate(xGrad[i]);
            }
        }
        if (CoordArt == 3 || CoordArt == 4) {
            // Anlegen der entsprechenden Vektoren (Länge: 2*numXPix, da positive UND negate y-Achse)
            yGrad = new double[2 * numXPix + 1];
            for (int i = 0; i < 2 * numXPix + 1; i++) {
                // yFunc enthält die "echten" y-Werte, während yFuncTrans die auf das Koordiantensystem gemappten Werte enthält.
                // Hierbei gilt: Pro Pixel/x-Wert = ein y-Wert
                yGrad[i] = gradient.evaluate(xGrad[i]);
            }
        }

        // Berechnen des Maximalen/Minimalen y-Wertes zur Bestimmung der Größe des Koordinatesystems
        yMax = NumLinAlg.getMax(yFunc);
        yMin = NumLinAlg.getMin(yFunc);

        // Überprüfung, ob der kleinste Wert im Betrag größer ist als der größte positive Wert (gleiche Achsenskalierung für positive wie negative Achse)
        if (yMax < Math.abs(yMin)) {
            yMax = Math.abs(yMin);
        }

        // Ausrechnen des Faktores um die "echte" y-Werte auf das Koordinatensystem zu mappen
        yScale = yLen / yMax;

        // Setzen der transformierten y-Werte, abhängig vom Koordinatensystem
        if (CoordArt == 1 || CoordArt == 2) {
            // Anlegen der entsprechenden Vektoren (Länge: numXPix, da nur positive y-Achse)
            yGradTrans = new double[numXPix + 1];
            for (int i = 0; i < numXPix + 1; i++) {
                // xFunc enthält die "echten" x-Werte, während xFuncTrans die uf das Koordiantensystem gemappten Werte enthält.
                // Hierbei gilt: Pro Pixel/x-Wert = ein y-Wert
                yGradTrans[i] = -(yGrad[i] * yScale) + y0;
            }
        }
        if (CoordArt == 3 || CoordArt == 4) {
            // Anlegen der entsprechenden Vektoren (Länge: 2*numXPix, da positive UND negate y-Achse)
            yGradTrans = new double[2 * numXPix + 1];
            for (int i = 0; i < 2 * numXPix + 1; i++) {
                // yFunc enthält die "echten" y-Werte, während yFuncTrans die auf das Koordiantensystem gemappten Werte enthält.
                // Hierbei gilt: Pro Pixel/x-Wert = ein y-Wert
                yGradTrans[i] = -(yGrad[i] * yScale) + y0;
            }
        }
    }

    public double getYTransFromReel(double yReel) {
        yMax = NumLinAlg.getMax(yFunc);
        yMin = NumLinAlg.getMin(yFunc);
        yScale = yLen / yMax;

        return -(yReel * yScale) + y0;
    }

    public double getXTransFromReel(double xReel) {
        if (((xReel - xMin) * numXPix / xMax) > xFuncTrans.length || ((xReel - xMin) * numXPix / xMax) < 0) {
            return 10000;
        } else {
            return xFuncTrans[(int) ((xReel - xMin) * numXPix / xMax)];
        }
    }

    public float getY0() {
        return y0;
    }

    public double[] getyFuncTrans() {
        return yFuncTrans;
    }

    public float getX0() {
        return x0;
    }

    public float getxLen() {
        return xLen;
    }

    public int getCoordArtFlag() {
        return CoordArtFlag;
    }

    public Function getFunction() {
        return function;
    }

    public int getCoordArt() {
        return CoordArt;
    }

    public double[] getxFuncTrans() {
        return xFuncTrans;
    }

    public int getNumXPix() {
        return numXPix;
    }

    public double getReelXMax() {
        return xMax;
    }

    public PolynomialFunction getGradient() {
        return gradient;
    }

    public void setGradient(PolynomialFunction gradient) {
        this.gradient = gradient;
    }
}
