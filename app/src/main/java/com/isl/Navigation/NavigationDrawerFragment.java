package com.uni_stuttgart.isl.Navigation;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.uni_stuttgart.isl.Intros.Intro_EliminateLGS;
import com.uni_stuttgart.isl.Intros.Intro_Eigenwerte;
import com.uni_stuttgart.isl.Intros.Intro_Gaussian;
import com.uni_stuttgart.isl.Intros.Intro_Integration;
import com.uni_stuttgart.isl.Intros.Intro_Interpolation;
import com.uni_stuttgart.isl.Intros.Intro_Riemann;
import com.uni_stuttgart.isl.Intros.Intro_SplittingSolver;
import com.uni_stuttgart.isl.Intros.Intro_Zerofinding;
import com.uni_stuttgart.isl.R;
import com.uni_stuttgart.isl.ZeroPoint.Zerofinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment {

    public static final String PREF_FILE_NAME = "testpref";
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;

    private MyAdapter adapter;

    private View contrainerView;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    public static List<Information> getData() {
        List<Information> data = new ArrayList<>();
        int[] icons = {R.drawable.baseline_label_24, R.drawable.baseline_label_24, R.drawable.baseline_label_24,  R.drawable.baseline_label_24, R.drawable.baseline_label_24, R.drawable.baseline_label_24, R.drawable.baseline_label_24};
        String[] titels = {"Interpolation und Approximation", "Integration", "Nullstellenberechnung",  "Richardson Verfahren","Riemann Integration","LGS Verfahren","Eigenwerte/-vektoren"};
        for (int i = 0; i < titels.length && i < icons.length; i++) {
            Information current = new Information();
            current.iconID = icons[i];
            current.title = titels[i];
            data.add(current);
        }
        return data;
    }

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "true"));
        if (savedInstanceState != null) {
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        adapter = new MyAdapter(getActivity(), getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (position == 0) {
                    startActivity(new Intent(getActivity(), Intro_Interpolation.class));
                    getActivity().finish();
                } else if (position == 1) {
                    startActivity(new Intent(getActivity(), Intro_Integration.class));
                    getActivity().finish();
                } else if (position == 2) {
                    startActivity(new Intent(getActivity(), Intro_Zerofinding.class));
                    getActivity().finish();
                } else if (position == 3) {
                    startActivity(new Intent(getActivity(), Intro_SplittingSolver.class));
                    getActivity().finish();
                } else if (position == 4) {
                    startActivity(new Intent(getActivity(), Intro_Riemann.class));
                    getActivity().finish();
                } else if (position == 5) {
                    startActivity(new Intent(getActivity(), Intro_EliminateLGS.class));
                    getActivity().finish();
                } else if (position == 6) {
                    startActivity(new Intent(getActivity(), Intro_Eigenwerte.class));
                    getActivity().finish();
                }

            }

            @Override
            public void onLongClick(View view, int position) {
                if (position == 0) {
                    startActivity(new Intent(getActivity(), Intro_Interpolation.class));
                    getActivity().finish();
                } else if (position == 1) {
                    startActivity(new Intent(getActivity(), Intro_Integration.class));
                    getActivity().finish();
                } else if (position == 2) {
                    startActivity(new Intent(getActivity(), Zerofinding.class));
                    getActivity().finish();
                } else if (position == 3) {
                    startActivity(new Intent(getActivity(), Intro_SplittingSolver.class));
                    getActivity().finish();
                } else if (position == 4) {
                    startActivity(new Intent(getActivity(), Intro_Riemann.class));
                    getActivity().finish();
                } else if (position == 5) {
                    startActivity(new Intent(getActivity(), Intro_EliminateLGS.class));
                    getActivity().finish();
                } else if (position == 6) {
                    startActivity(new Intent(getActivity(), Intro_Eigenwerte.class));
                    getActivity().finish();
                }

            }
        }));
        return layout;
    }

    public void setUp(int fragmentID, DrawerLayout drawerLayout, final Toolbar toolbar) {
        contrainerView = getActivity().findViewById(fragmentID);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    saveToPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer + "");
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset < 0.9) {
                    toolbar.setAlpha(1 - slideOffset);
                }
            }
        };

        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(contrainerView);
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private ClickListener clickListener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }
        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

}
