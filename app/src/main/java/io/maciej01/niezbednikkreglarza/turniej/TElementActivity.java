package io.maciej01.niezbednikkreglarza.turniej;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import io.maciej01.niezbednikkreglarza.ActivitySwipeDetector;
import io.maciej01.niezbednikkreglarza.Article;
import io.maciej01.niezbednikkreglarza.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.Math.max;

/**
 * Created by Maciej on 2017-04-28.
 */

public class TElementActivity extends AppCompatActivity {
    public Turniej turniej;
    public Turniej turniej_old;
    public RecyclerView recyclerView;
    public TElementAdapter adapter;
    public AccelerateInterpolator bi = new AccelerateInterpolator();
    public WynikSwipe ws = new WynikSwipe();
    public DialogInterface.OnClickListener dialogClickListener;
    public AlertDialog.Builder ab;

    public class WynikSwipe extends ActivitySwipeDetector {

        public WynikSwipe() {
            super();
        }

        @Override
        public final void onLeftToRightSwipe() {
            super.onLeftToRightSwipe();
            onBackPressed();
        }

        public final void onRightToLeftSwipe() {
            super.onRightToLeftSwipe();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zawody_element_main);

        Intent i = getIntent();
        if (turniej == null) {
            turniej = (Turniej) i.getSerializableExtra("turniej");
            turniej_old = turniej.clone();
        }


        turniej.initTurniej();
        turniej.coJaUczynilem();

        recyclerView = (RecyclerView) findViewById(R.id.elementarka);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        setTextViews(true, 0, true);
        getSupportActionBar().setElevation(0);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.head_tablayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);

        tabLayout.addOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        setTextViews(false, tab.getPosition(), false);
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {}
                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {}
                }
        );
        getSupportActionBar().setElevation(0);

        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        seppuku();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //Do your No progress
                        break;
                }
            }
        };

        ab = new AlertDialog.Builder(this);


    }

    public void jedenzdrugim() {
        ab.setMessage(R.string.delete_sure).setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }
    public void kolejnyprzycisk() {
        Intent i = new Intent(TElementActivity.this, TEditActivity.class);
        i.putExtra("wynik", turniej);
        i.putExtra("rodzaj", "zmiana");
        startActivityForResult(i, 2001);
    }

    public void seppuku() {
        Intent intent = new Intent();
        intent.putExtra("wynik", turniej);
        intent.putExtra("wynik_old", turniej_old);
        intent.putExtra("dead", true);
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        super.dispatchTouchEvent(ev);
        return ws.onTouch(ev);
    }


    public void setTextViews(boolean nadapt, int kategoria, boolean settabs) {
        Log.v("telementact", "setTextViews called");
        adapter = new TElementAdapter(turniej, recyclerView, this, kategoria);
        if (nadapt) {recyclerView.setAdapter(adapter);} else {recyclerView.swapAdapter(adapter, true);}
        if ((adapter.getItemCount() == 1) || (adapter.getItemCount() == 0)) { // bo zawsze jest header
            ((TextView) findViewById(R.id.txtPustaElementarka)).setVisibility(VISIBLE);
        } else {
            Log.v("ilosc", Integer.toString(adapter.getItemCount()));
            ((TextView) findViewById(R.id.txtPustaElementarka)).setVisibility(GONE);
        }
        adapter.notifyDataSetChanged();
        if (settabs) {
            TabLayout tabLayout = (TabLayout) findViewById(R.id.head_tablayout);
            tabLayout.removeAllTabs();
            ArrayList<String> kategorie_modified = turniej.getKategorie();
            int vis = GONE;
            for (String a : kategorie_modified) {
                tabLayout.addTab(tabLayout.newTab().setText(a));
                vis = VISIBLE;
            }
            tabLayout.setVisibility(vis);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2001) {
            if (resultCode == RESULT_OK) {
                turniej = (Turniej) data.getSerializableExtra("wynik");
                turniej.initTurniej();
                turniej.coJaUczynilem();
                setTextViews(false, 0, true);
                turniej.save();
                Log.v("onactresult", "here comes dat turniej");
            }
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("wynik", turniej);
        intent.putExtra("wynik_old", turniej_old);
        intent.putExtra("dead", false);
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
