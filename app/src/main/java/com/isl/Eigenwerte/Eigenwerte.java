package com.uni_stuttgart.isl.Eigenwerte;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;

import com.nishant.math.MathView;
import com.uni_stuttgart.isl.Intros.Intro_Eigenwerte;
import com.uni_stuttgart.isl.Intros.PrefManager;
import com.uni_stuttgart.isl.MainActivity;
import com.uni_stuttgart.isl.Points.PurePoints;
import com.uni_stuttgart.isl.R;
import com.uni_stuttgart.isl.Eigenwerte.MyCanvas;

public class Eigenwerte extends AppCompatActivity {
    private PrefManager prefManager;
    private Toolbar toolbar;
    private int dimMat;
    private MyCanvas myCanvas;
    // Objekt zum erzeugen eines Koordinaten Systems
    private CoordSystem coordSystem;
    // Welches Koordiantensystem soll gewählt werden (welche Quadranten)
    private int coordArt;

    private int sizeCanvas;
    private String m_Text;
    private boolean flag = true;
    private int final_size;

    private PurePoints points;
    private PurePoints polpoints;

    private float rot_angle = (float)Math.PI/3.f;

    private SeekBar angle_bar;

    private float shear_value = 0.5f;
    private SeekBar shear_bar;
    private TextView text_shear;

    private Spinner sel_modus;
    private TextView text_angle;

    private MathView disc_text;
    private TextView quest_text;
    private MathView text_sol;

    private Button showSolButton;

    private float vec_coords[] = {0.5f,-0.5f};
    private float poly_coords[] = {0.7f,-0.2f,0.2f,-0.6f};

    private boolean show_sol = false;

    private ScrollView scroollview;

    private CharSequence[] scenarios = {"SPIEGELUNG + STRECKUNG", "Spiegelung + Streckung", "Projektion", "Scherung", "Drehung"};

    private int modi = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_eigenwerte, menu);
        return true;
    }

    // Funktion zum erzeugen den Menüpunktes "oben-rechts"
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        // Bei Auswahl von "Introduction" start von Intro und speichern der aktuellen Konfiguration
        if (item.getItemId() == R.id.introduction) {
            // Beim erneuten
            prefManager.setReopenZerofindingIntro(false);
//            saveActivityToLog();
            Intent myIntent = new Intent(Eigenwerte.this, Intro_Eigenwerte.class);
            Eigenwerte.this.startActivity(myIntent);
            freeMemory();
            finish();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eigenwerte);

        prefManager = new PrefManager(this);

        // Erzeugen der Toolbar
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        // Zurückbutton, was soll beim "Click" passieren
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start der Main
                Intent myIntent = new Intent(Eigenwerte.this, MainActivity.class);
                Eigenwerte.this.startActivity(myIntent);
                freeMemory();
                finish();
            }
        });

        showSolButton = (Button) findViewById(R.id.show_sol);

        // Größe der "Fläche" auslesen
        myCanvas = (MyCanvas) findViewById(R.id.mycanvaseigenwerte);
        sizeCanvas = myCanvas.getLayoutParams().width;
        final_size = myCanvas.getLayoutParams().width;

        angle_bar = (SeekBar) findViewById(R.id.select_angel);
        shear_bar = (SeekBar) findViewById(R.id.select_shear);
        sel_modus = (Spinner) findViewById(R.id.select_modus);
        text_angle = (TextView) findViewById(R.id.TextDegree);
        text_shear = (TextView) findViewById(R.id.TextShear);


        disc_text = (MathView) findViewById(R.id.discr_text);
        text_sol = (MathView) findViewById(R.id.text_sol);
        quest_text = (TextView) findViewById(R.id.question);

        scroollview = (ScrollView) findViewById(R.id.EVscrollview);

        set_discr_text(modi);

        // Volles Koordinatensystem
        setCoordArt(4);


        myCanvas.post(new Runnable() {
            @Override
            public void run() {

                final_size = myCanvas.getHeight();

                // Anlegen des Koordinatensystems
                coordSystem = new CoordSystem((int) final_size, (int) final_size, myCanvas);
                // Koordinatensystem+Integrationspunkte der Darstellungsfläche übergen
                myCanvas.setCoordSystem(coordSystem);
                myCanvas.set_init();

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;


                ViewGroup.LayoutParams params = scroollview.getLayoutParams();
                params.width = width - final_size;
                params.height =final_size;
                scroollview.setLayoutParams(params);



                loadActivity();
            }

        });

    }

    private void loadActivity() {

        init_modi0();


        ArrayAdapter<CharSequence> adapter_SpinnerDimension = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, scenarios);
        // Specify the layout to use when the list of choices appears
        adapter_SpinnerDimension.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter_SpinnerDimension
        //to the spinner
        sel_modus.setAdapter(adapter_SpinnerDimension);

        sel_modus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                text_sol.setVisibility(View.GONE);
                switch (position) {
                    case 0:

                        break;

                    case 1:
                        scenarios[0] = "SPIEGELUNG + STRECKUNG";
                        if(modi!=0)
                            myCanvas.clear_paths();
                        modi = 0;
                        set_discr_text(modi);
                        myCanvas.set_modus(modi);
                        for(int i = 0 ; i < 5; i++) {
                            myCanvas.getPoints().getPoint(i).setIsPlaced(true);
                        }
                        init_modi0();

                        text_angle.setVisibility(View.GONE);
                        angle_bar.setVisibility(View.GONE);
                        shear_bar.setVisibility(View.GONE);
                        text_shear.setVisibility(View.GONE);


                        break;
                    case 2:
                        scenarios[0] = "PROJEKTION";
                        if(modi!=1)
                            myCanvas.clear_paths();
                        modi = 1;
                        set_discr_text(modi);
                        myCanvas.set_modus(modi);

                        for(int i = 0 ; i < 5; i++) {
                            myCanvas.getPoints().getPoint(i).setIsPlaced(true);
                        }
                        init_modi1();
                        text_angle.setVisibility(View.GONE);
                        angle_bar.setVisibility(View.GONE);
                        shear_bar.setVisibility(View.GONE);
                        text_shear.setVisibility(View.GONE);

                        break;
                    case 3:
                        scenarios[0] = "SCHERUNG";
                        if(modi!=2)
                            myCanvas.clear_paths();
                        modi = 2;
                        set_discr_text(modi);
                        myCanvas.set_modus(modi);

                        //only x axis
                        myCanvas.getPoints().getPoint(1).setIsPlaced(true);
                        myCanvas.getPoints().getPoint(3).setIsPlaced(true);
                        myCanvas.getPoints().getPoint(2).setIsPlaced(false);
                        myCanvas.getPoints().getPoint(2).setIsAvailable(false);
                        myCanvas.getPoints().getPoint(0).setIsPlaced(false);
                        myCanvas.getPoints().getPoint(0).setIsAvailable(false);
                        myCanvas.getPoints().getPoint(4).setIsPlaced(true); //and v

                        init_modi2();
                        text_angle.setVisibility(View.GONE);
                        angle_bar.setVisibility(View.GONE);
                        shear_bar.setVisibility(View.VISIBLE);
                        text_shear.setVisibility(View.VISIBLE);

                        break;
                    case 4:
                        scenarios[0] = "DREHUNG";
                        if(modi!=3) {
                            myCanvas.clear_paths();
                            text_angle.setText("Drehwinkel: " +  Math.round(rot_angle/(Math.PI)*180) + "° ");
                            int pval = (int)Math.round(rot_angle/(Math.PI)*180);
                            angle_bar.setProgress(pval);
                        }
                        modi = 3;
                        set_discr_text(modi);
                        myCanvas.set_modus(modi);

                        for(int i = 0 ; i < 4; i++) { //no axis
                            myCanvas.getPoints().getPoint(i).setIsPlaced(false);
                            myCanvas.getPoints().getPoint(i).setIsAvailable(false);
                        }
                        init_modi3();

                        myCanvas.getPoints().getPoint(4).setIsPlaced(false); //only v

                        text_angle.setVisibility(View.VISIBLE);
                        angle_bar.setVisibility(View.VISIBLE);
                        shear_bar.setVisibility(View.INVISIBLE);
                        text_shear.setVisibility(View.INVISIBLE);

                        break;
                }
                sel_modus.setSelection(0);
                myCanvas.invalidate();
                freeMemory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        angle_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {

                rot_angle = seekBar.getProgress();
                text_angle.setText("Drehwinkel: " +  Math.round(rot_angle) + "° ");
                rot_angle = rot_angle/180*(float)Math.PI;
                myCanvas.set_rotanlge(rot_angle);
                freeMemory();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                rot_angle = seekBar.getProgress();
                text_angle.setText("Drehwinkel: " +  Math.round(rot_angle) + "° ");
                rot_angle = rot_angle/180*(float)Math.PI;
                myCanvas.set_rotanlge(rot_angle);
                freeMemory();

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                rot_angle = seekBar.getProgress();
                text_angle.setText("Drehwinkel: " + Math.round(rot_angle) + "° ");
                rot_angle = rot_angle/180*(float)Math.PI;
                myCanvas.set_rotanlge(rot_angle);
                freeMemory();
            }
        });

        shear_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {

                shear_value = Float.valueOf(seekBar.getProgress())/100;
                myCanvas.set_shearvalue(shear_value);
                freeMemory();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                shear_value = Float.valueOf(seekBar.getProgress())/100;
                myCanvas.set_shearvalue(shear_value);
                freeMemory();

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                shear_value = Float.valueOf(seekBar.getProgress())/100;
                myCanvas.set_shearvalue(shear_value);
                freeMemory();
            }
        });

    }

    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }



    public String[] StringToStringVev(String s) {
        String[] splitString;

        splitString = s.split(" ");

        return splitString;
    }

    public double[] StringVecToDoubleVec(String[] s){
        double[] d = new double[s.length];

        for (int i = 0; i < s.length; i++) {
            d[i] = Double.parseDouble(s[i]);
        }

        return d;
    }

    public int getCoordArt() {
        return coordArt;
    }

    public void setCoordArt(int coordArt) {
        this.coordArt = coordArt;
    }

    public CoordSystem getCoordSystem() {
        return coordSystem;
    }

    void init_modi0() {

        float x0 = final_size/2;
        float y0 = final_size/2;
        float xLen = final_size/2;
        float yLen = final_size/2;

        polpoints = myCanvas.getPolPoints();
        polpoints.getPoint(0).setReelX(x0+poly_coords[0]*xLen);
        polpoints.getPoint(0).setX(x0+poly_coords[0]*xLen);
        polpoints.getPoint(0).setReelY(y0+poly_coords[1]*yLen);
        polpoints.getPoint(0).setY(y0+poly_coords[1]*yLen);
        polpoints.getPoint(1).setReelX(x0+poly_coords[2]*xLen);
        polpoints.getPoint(1).setX(x0+poly_coords[2]*xLen);
        polpoints.getPoint(1).setReelY(y0+poly_coords[3]*yLen);
        polpoints.getPoint(1).setY(y0+poly_coords[3]*yLen);
        coordSystem.draw_polygon1(x0, y0, (float)polpoints.getPoint(0).getX(), (float)polpoints.getPoint(0).getY(), (float)polpoints.getPoint(1).getX(), (float)polpoints.getPoint(1).getY());

        coordSystem.draw_polygon2();

        coordSystem.draw_v(x0+vec_coords[0]*xLen,y0+vec_coords[1]*yLen);
        coordSystem.draw_Av();

        points = myCanvas.getPoints();

        points.getPoint(0).setReelX(coordSystem.getX0()-coordSystem.getxLen()+10);
        points.getPoint(0).setReelY(coordSystem.getY0());
        points.getPoint(0).setX(coordSystem.getX0()-coordSystem.getxLen()+10);
        points.getPoint(0).setY(coordSystem.getY0());

        points.getPoint(1).setReelX(coordSystem.getX0());
        points.getPoint(1).setReelY(coordSystem.getY0()-coordSystem.getyLen()+10);
        points.getPoint(1).setX(coordSystem.getX0());
        points.getPoint(1).setY(coordSystem.getY0()-coordSystem.getyLen()+10);

        points.getPoint(2).setReelX(coordSystem.getX0()+coordSystem.getxLen()-10);
        points.getPoint(2).setReelY(coordSystem.getY0());
        points.getPoint(2).setX(coordSystem.getX0()+coordSystem.getxLen()-10);
        points.getPoint(2).setY(coordSystem.getY0());

        points.getPoint(3).setReelX(coordSystem.getX0());
        points.getPoint(3).setReelY(coordSystem.getY0()+coordSystem.getyLen()-10);
        points.getPoint(3).setX(coordSystem.getX0());
        points.getPoint(3).setY(coordSystem.getY0()+coordSystem.getyLen()-10);
    }


    void init_modi1() {

        float x0 = final_size/2;
        float y0 = final_size/2;
        float xLen = final_size/2;
        float yLen = final_size/2;

        polpoints = myCanvas.getPolPoints();
        polpoints.getPoint(0).setReelX(x0+poly_coords[0]*xLen);
        polpoints.getPoint(0).setX(x0+poly_coords[0]*xLen);
        polpoints.getPoint(0).setReelY(y0+poly_coords[1]*yLen);
        polpoints.getPoint(0).setY(y0+poly_coords[1]*yLen);
        polpoints.getPoint(1).setReelX(x0+poly_coords[2]*xLen);
        polpoints.getPoint(1).setX(x0+poly_coords[2]*xLen);
        polpoints.getPoint(1).setReelY(y0+poly_coords[3]*yLen);
        polpoints.getPoint(1).setY(y0+poly_coords[3]*yLen);
        coordSystem.draw_polygon1(x0, y0, (float)polpoints.getPoint(0).getX(), (float)polpoints.getPoint(0).getY(), (float)polpoints.getPoint(1).getX(), (float)polpoints.getPoint(1).getY());

        points = myCanvas.getPoints();

        points.getPoint(0).setReelX(coordSystem.getX0()-coordSystem.getxLen()+10);
        points.getPoint(0).setReelY(coordSystem.getY0());
        points.getPoint(0).setX(coordSystem.getX0()-coordSystem.getxLen()+10);
        points.getPoint(0).setY(coordSystem.getY0());

        points.getPoint(1).setReelX(10);
        points.getPoint(1).setReelY(coordSystem.getY0()-coordSystem.getyLen()+10);
        points.getPoint(1).setX(10);
        points.getPoint(1).setY(coordSystem.getY0()-coordSystem.getyLen()+10);

        points.getPoint(2).setReelX(coordSystem.getX0()+coordSystem.getxLen()-10);
        points.getPoint(2).setReelY(coordSystem.getY0());
        points.getPoint(2).setX(coordSystem.getX0()+coordSystem.getxLen()-10);
        points.getPoint(2).setY(coordSystem.getY0());

        points.getPoint(3).setReelX(coordSystem.getxSize()-10);
        points.getPoint(3).setReelY(coordSystem.getY0()+coordSystem.getyLen()-10);
        points.getPoint(3).setX(coordSystem.getxSize()-10);
        points.getPoint(3).setY(coordSystem.getY0()+coordSystem.getyLen()-10);


        coordSystem.draw_polygon2();

        coordSystem.draw_v(x0+vec_coords[0]*xLen,y0+vec_coords[1]*yLen);
        coordSystem.draw_Av();

    }


    void init_modi2() {

        float x0 = final_size/2;
        float y0 = final_size/2;
        float xLen = final_size/2;
        float yLen = final_size/2;

        polpoints = myCanvas.getPolPoints();
        polpoints.getPoint(0).setReelX(x0+poly_coords[0]*xLen);
        polpoints.getPoint(0).setX(x0+poly_coords[0]*xLen);
        polpoints.getPoint(0).setReelY(y0+poly_coords[1]*yLen);
        polpoints.getPoint(0).setY(y0+poly_coords[1]*yLen);
        polpoints.getPoint(1).setReelX(x0+poly_coords[2]*xLen);
        polpoints.getPoint(1).setX(x0+poly_coords[2]*xLen);
        polpoints.getPoint(1).setReelY(y0+poly_coords[3]*yLen);
        polpoints.getPoint(1).setY(y0+poly_coords[3]*yLen);
        coordSystem.draw_polygon1(x0, y0, (float)polpoints.getPoint(0).getX(), (float)polpoints.getPoint(0).getY(), (float)polpoints.getPoint(1).getX(), (float)polpoints.getPoint(1).getY());

        points = myCanvas.getPoints();


        points.getPoint(1).setReelX(10);
        points.getPoint(1).setReelY(coordSystem.getY0()-coordSystem.getyLen()+10);
        points.getPoint(1).setX(10);
        points.getPoint(1).setY(coordSystem.getY0()-coordSystem.getyLen()+10);

        points.getPoint(3).setReelX(coordSystem.getxSize()-10);
        points.getPoint(3).setReelY(coordSystem.getY0()+coordSystem.getyLen()-10);
        points.getPoint(3).setX(coordSystem.getxSize()-10);
        points.getPoint(3).setY(coordSystem.getY0()+coordSystem.getyLen()-10);

        coordSystem.draw_polygon2();

        coordSystem.draw_v(x0+vec_coords[0]*xLen,y0+vec_coords[1]*yLen);
        coordSystem.draw_Av();

    }


    void init_modi3() {

        float x0 = final_size/2;
        float y0 = final_size/2;
        float xLen = final_size/2;
        float yLen = final_size/2;

        myCanvas.set_rotanlge(rot_angle);
        polpoints = myCanvas.getPolPoints();
        coordSystem.draw_polygon1(x0, y0, (float)polpoints.getPoint(0).getX(), (float)polpoints.getPoint(0).getY(), (float)polpoints.getPoint(1).getX(), (float)polpoints.getPoint(1).getY());

        coordSystem.draw_polygon2();

        coordSystem.draw_v(x0+vec_coords[0]*xLen,y0+vec_coords[1]*yLen);
        coordSystem.draw_Av();

        points = myCanvas.getPoints();

    }


    public void set_discr_text(int i) {
        switch (i) {
            case 0:
                quest_text.setText("Frage: Finden Sie die Eigenvektoren zu 0.5 und -0.5?");
                disc_text.setText("Sie können das Koordinatensystem sowie den schwarzen Vekor $v$ bewegen. Der schwarze Vektor $v$ wird auf den gelben Vektor $Av$ abgebildet, indem er an der dünnen roten Linie gespiegelt und dann auch noch mit dem Faktor $\\\\frac12$ multipliziert wird. " +
                        "Dies ist eine lineare Abbildung; in einem Koordinatensystem, dessen Achsen durch die rote und die blaue Gerade gebildet werden, wird diese Abbildung durch eine Diagonalmatrix mit Diagonaleinträgen $\\\\frac12$ und $-\\\\frac{1}{2}$ beschrieben.");
                break;

            case 1:
                quest_text.setText("Frage: Finden Sie die Richtungen, in die Eigenvektoren zeigen?");
                disc_text.setText("Sie können die blaue und rote Achse, sowie den schwarzen Vektor $v$ bewegen. Das Beispiel zeigt eine Abbildung, die $v$ den Vektor $Bv$ (gelber Vektor) zuordnet (für eine geeignete Matrix $B$). " +
                        "Hier wird $v$ entlang der blauen Richtung auf die rote Gerade projiziert (Sie können diese Richtungen beide ändern).");

                break;

            case 2:
                quest_text.setText("Frage: Finden Sie die Richtungen, in die Eigenvektoren zeigen?");
                disc_text.setText("Hier wird der schwarze Vektor $v$ parallel zur roten Richtung geschert und es resultiert der gelbe Vektor (Sie können die rote Richtung und das Ausmaß der Scherung ändern).");

                break;

            case 3:
                quest_text.setText("Frage: Finden Sie einen Drehwinkel, zu dem es dann doch Eigenvektoren gibt?");
                disc_text.setText("Hier wird der schwarze Vektor $v$ um einen vorgegbenen Winkel um der Ursprung gedreht. Sie können sowohl den Vektor bewegen, als auch den Drehwinkel anpassen.");

                break;
        }
    }

    public void set_sol_text(int i) {
        text_sol.setVisibility(View.VISIBLE);
        switch (i) {
            case -1:
                text_sol.setText(""
                );
                break;

            case 0:
                text_sol.setText("Wenn man den schwarzen Vektor so dreht, dass er auf die rote Linie zu liegen kommt, fällt auch der Bildvektor $Av$ in diese Linie. " +
                        "Er zeigt dann in die gleiche Richtung wie der schwarze, ist aber nur halb so lang: Wir haben damit einen Eigenvektor zum Eigenwert $\\\\frac{1}{2}$ gefunden. " +
                        "Auch durch Streckung des Vektors v ändert sich nichts an dieser Beziehung (so lange Sie die Richtung beibehalten). " +
                        "Bewegt man den Vektor $v$ auf die blaue Gerade, so fällt der Bildvektor $Av$ wieder in die von $v$ aufgespannte Gerade, zeigt aber in die umgekehrte Richtung: Jetzt ist $v$ ein Eigenvektor zum Eigenwert $-\\\\frac{1}{2}$. " +
                        "Dass hier jeder Eigenvektor zum Eigenwert $\\\\frac{1}{2}$ auf jedem Eigenvektor zum Eigenwert $-\\\\frac{1}{2}$ senkrecht steht, ist ein Zufall (in der Tat eine Folge der Tatsache, dass die Matrix A symmetrisch ist)."
                );
                break;

            case 1:

                text_sol.setText("Vektoren, die entlang der roten Geraden zeigen, werden bei der Projektion überhaupt nicht verändert: " +
                        "Das sind Eigenvektoren zum Eigenwert $1$. Vektoren, die genau entlang der Projektionsrichtung zeigen, sind Eigenvektoren zum Eigenwert $0$: " +
                        "Solche Vektoren werden auf den Nullvektor abgebildet (das ist schwer zu sehen und etwas tüftelig einzustellen). "+
                        "Es gibt viele weitere Beispiele für $2\\\\times 2$-Matrizen mit Eigenwerten $0$ und $1$. In der Tat sind das genau diejenigen Matrizen $\\\\begin{pmatrix}a& b\\\\\\\\ c& d\\\\end{pmatrix}$, bei denen $a + d = 1$ und $ad − bc = 0$ gilt. " +
                        "In anderen Worten: Die Spur und Determinante von $B$ müssen die Bedingungen tr$(B) = 1$ (wobei tr$(B)$ die Spur von $B$ bereichnet) und det$(B) = 0$ erfüllen. Dies kann auch so formuliert werden: " +
                        "Das charakteristische Polynom $\\\\det(B − \\\\lambda I)$ (wobei $I$ die Einheitsmatrix bezeichnet) von $B$ ist gleich $\\\\lambda^2 − \\\\lambda.");
                break;

            case 2:
                text_sol.setText("Vektoren, die entlang der roten Geraden zeigen, werden bei der Scherung überhaupt nicht verändert: " +
                        "Das sind Eigenvektoren zum Eigenwert $1$. Andere Eigenvektoren gibt es hier nicht! " +
                        "Die charakteristische Eigenschaft der Matrizen, die Scherungen beschreiben, ist die Tatsache, dass sie den Eigenwert $1$ mit algebraischer Vielfachheit $2$ haben, während die geometrische Vielfachheit nur $1$ ist. " +
                        "Es gibt viele Beispiele für $2\\\\times 2$-Matrizen mit dieser Eigenschaft. In der Tat sind das genau diejenigen Matrizen $C=\\\\begin{pmatrix}a& b\\\\\\\\ c& d\\\\end{pmatrix}$, die nicht diagonal sind (d.h. mindestens einer der Einträge $b$, $c$ ist von Null verschieden) mit $a + d = 2$ und $ad − bc = 1$. " +
                        "In anderen Worten: Die Spur und Determinante einer solchen Matrix $C$ müssen die Bedingungen tr$(C) = 2$ (wobei tr$(B)$ die Spur von $B$ bereichnet) und det$(C) = 1$ erfüllen (und die Matrix ist nicht diagonalisierbar - das bedeutet jetzt einfach $C \\\\neq I$ (wobei $I$ die Einheitsmatrix bezeichnet)). Dies kann auch wie folgt formuliert werden: " +
                        "Das charakteristische Polynom det$(C − \\\\lambda I)$ von $C$ ist gleich $\\\\lambda^2 - 2\\\\lambda + 1 = (\\\\lambda − 1)^ 2$ ; aber $C \\\\neq I$.");

                break;

            case 3:
                text_sol.setText("Diese Abbildung besitzt keine reellen Eigenwerte. " +
                        "In ihrer Anfangskonfiguration zeigt die Zeichnung eine Drehung um $60^\\\\circ$ " +
                        "(im Bogenmaß also $\\\\frac{\\\\pi}{3}$) " +
                        "Es gibt viele weitere Beispiele von $2\\\\times 2$-Matrizenohne reelle Eigenwerte. In der Tat sind das genau diejenigen Matrizen $D = \\\\begin{pmatrix}a& b\\\\\\\\ c& d\\\\end{pmatrix}$, bei denen die charakteristische Gleichung $0 = $det$(D -\\\\lambda I) = \\\\lambda^2 - (a + d)\\\\lambda + (ad − bc)$ keine Lösung im Bereich der reellen Zahlen hat. " +
                        "Dies geschieht genau dann, wenn die Diskriminante $(a + d)^2 - 4(ad − bc)$ negativ ist; also dann, wenn $(a - d)^2 < -4bc$.");

                break;
        }
    }

    public void show_sol(View view) {
        if(!show_sol) {
            show_sol=true;
            showSolButton.setText("Verberge Lösung");
            set_sol_text(this.modi);
        }
        else {
            show_sol=false;
            showSolButton.setText("Zeige Lösung");
            set_sol_text(-1);
        }
    }
}