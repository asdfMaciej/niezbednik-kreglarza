package io.maciej01.niezbednikkreglarza;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.orm.SugarContext;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.View.GONE;
import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Serializable {

    public ArrayList<Article> articles = new ArrayList<>();
    public MyAdapter adapter;
    public RecyclerView recyclerView;
    public Bundle bundle;
    public AccelerateInterpolator bi = new AccelerateInterpolator();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SugarContext.init(getApplicationContext());
        firstLaunch();
        setContentView(R.layout.activity_main);
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));
        findViewById(R.id.lapp1).setVisibility(View.VISIBLE);
        findViewById(R.id.lapp2).setVisibility(GONE);
        findViewById(R.id.lapp3).setVisibility(GONE);
        findViewById(R.id.lapp4).setVisibility(GONE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        handleIntent(getIntent());

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(MainActivity.this.openFileInput("stack.trace")));
            String line;
            String trace = "";
            while((line = reader.readLine()) != null) {
                trace += line+"\n";
            }
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            String subject = "Error report";
            String body = "Mail this to maciej.kaszkowiak@gmail.com: " + "\n" + trace + "\n";

            sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"maciej.kaszkowiak@gmail.com"});
            sendIntent.putExtra(Intent.EXTRA_TEXT, body);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            sendIntent.setType("message/rfc822");

            MainActivity.this.startActivity(Intent.createChooser(sendIntent, "Title:"));

            MainActivity.this.deleteFile("stack.trace");
        } catch(FileNotFoundException fnfe) {
            // ...
        } catch(IOException ioe) {
            // ...
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openZmiana(view);
            }
        });
        fab.setVisibility(View.VISIBLE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                updateNavBar();
            }});
        thread.run();

        recyclerView = (RecyclerView) findViewById(R.id.smieciarka);
        // w celach optymalizacji
        recyclerView.setHasFixedSize(true);

        // ustawiamy LayoutManagera
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        // ustawiamy animatora, który odpowiada za animację dodania/usunięcia elementów listy
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        articles = (ArrayList<Article>) Article.listAll(Article.class);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {//  | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //super.onMove(recyclerView, viewHolder, target);
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if (swipeDir == ItemTouchHelper.LEFT) {
                    Log.i("detector", "Swipe was left");
                    Article wynik = articles.get(viewHolder.getAdapterPosition());
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            co(); // xD?
                        }
                    }, 300);
                    openWynik(wynik);
                } else if (swipeDir == ItemTouchHelper.RIGHT) {
                    Log.i("detector", "Swipe was right");
                }

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        // tworzymy adapter oraz łączymy go z RecyclerView
        adapter = new MyAdapter(articles, recyclerView, this);
        recyclerView.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        if (adapter.getItemCount() == 0) {
            ((TextView) findViewById(R.id.txtPustaSmieciarka)).setVisibility(View.VISIBLE);
        } else {
            ((TextView) findViewById(R.id.txtPustaSmieciarka)).setVisibility(View.GONE);
        }
    }

    private void co() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
        }
    }


    public void updateNavBar() {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String spKlub = SP.getString("klub",getString(R.string.team_default));
        String spZawodnik = SP.getString("zawodnik",getString(R.string.player_default));

        if (spKlub.equals("1")) {
            spKlub = getString(R.string.team_default);
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0); // in case of null pointer reference
        TextView navZawodnik = (TextView)hView.findViewById(R.id.navZawodnik); // remember about
        TextView navKlub = (TextView)hView.findViewById(R.id.navKlub); //hView.findviewbyid
        navZawodnik.setText(spZawodnik);
        navKlub.setText(spKlub);

        Bitmap sup = new ImageSaver(getApplicationContext()).
                setFileName("twarz.png").
                setDirectoryName("images").load(8);
        if (sup != null) {
            CircularImageView twrz = (CircularImageView) hView.findViewById(R.id.navTwarz);
            twrz.setImageBitmap(sup);
        }
        sup = null;
        System.gc();
    }


    public void sortArticlesByWynik(ArrayList<Article> art, final int tryb) {

        Collections.sort(art, new Comparator<Article>() {
            @Override
            public int compare(Article o1, Article o2) {
                try {
                    if (tryb == 1) {
                        return ((Integer) Integer.parseInt(o1.getWynik())).compareTo(Integer.parseInt(o2.getWynik()));
                    } else if (tryb == 2) {
                        return ((Integer) Integer.parseInt(o2.getWynik())).compareTo(Integer.parseInt(o1.getWynik()));
                    } else if (tryb == 3) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        return sdf.parse(proDate(o1.getData())).compareTo(sdf.parse(proDate(o2.getData())));
                    } else if (tryb == 4) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        return sdf.parse(proDate(o2.getData())).compareTo(sdf.parse(proDate(o1.getData())));
                    } else {
                        return 0;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

    }

    public String proDate(String d1) {
        String[] a1 = d1.split("-");
        String rt = "";
        rt += a1[0] + "-";
        if (a1[1].length() == 1) { rt += "0"+a1[1]; } else { rt += a1[1]; } rt += "-";
        if (a1[2].length() == 1) { rt += "0"+a1[2]; } else { rt += a1[2]; }
        return rt;
    }
    @Override
    protected void onStop() {
        super.onStop();
        Article.deleteAll(Article.class); // :^^^))))

        for (Article a : articles) {
            a.save();
            Log.e("more", "equals or less copied");
        }

    }

    public void openWynik(Article wynik) {
        Intent intent;
        intent = new Intent(this, WynikActivity.class);
        intent.putExtra("wynik", wynik);
        intent.putExtra("disabled", false);
        startActivityForResult(intent, 2);//, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        //finish();
        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        //overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
    }

    public void losoweWyniki() {
        for (int i = 0; i < 20; ++i)
            articles.add(new Article());
    }

    public void firstLaunch() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    Intent i = new Intent(MainActivity.this, IntroActivity.class);
                    startActivity(i);

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();
    }

    public void openZmiana(View view) {
        Intent intent;
        intent = new Intent(this, ZmianaActivity.class);
        intent.putExtra("rodzaj", "nowy");
        intent.putExtra("swiped", false);
        //Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivityForResult(intent, 1);//, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());//, bundle);
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
        //view.getContext().startActivity(intent);
    }

    public int dajmicyferke(Article a) {
        for (int i = 0; i < articles.size(); i++) {
            if (articles.get(i).equals(a)) {
                return i;
            }
        }
        return 666;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        adapter.notifyDataSetChanged();
        if (requestCode == 1) { // nowe do dodania
            if (resultCode == RESULT_OK) {
                Article scores = (Article) data.getSerializableExtra("wynik");
                articles.add(0, scores);
                adapter.notifyItemInserted(0);
                recyclerView.smoothScrollToPosition(0);
            }
        } else if (requestCode == 2) { // powrot z wynikiem z wynik_main,wynikactivity
            Article scores = (Article) data.getSerializableExtra("wynik");
            Article scores_old = (Article) data.getSerializableExtra("wynik_old");
            boolean seppuku = (boolean) data.getSerializableExtra("dead");
            if (seppuku) {
                int index = dajmicyferke(scores_old);
                articles.remove(index);
                adapter.notifyItemRemoved(index);
                if (adapter.getItemCount() == 0) {
                    ((TextView) findViewById(R.id.txtPustaSmieciarka)).setVisibility(View.VISIBLE);
                } else {
                    ((TextView) findViewById(R.id.txtPustaSmieciarka)).setVisibility(View.GONE);
                }
                return;
            }
            if (scores.equals(scores_old)) {
                Log.e("komentarz", "taki sam");
                Log.e("komentarz", scores.getKomentarz());
                Log.e("komentarz", scores_old.getKomentarz());
            } else {
                // inne
                int index = dajmicyferke(scores_old);
                articles.set(index, scores);
                adapter.notifyItemChanged(index);
                Log.e("komentarz", "inny wynik");
                Article xd = Article.findById(Article.class, index);
                xd = scores;
                xd.save();
                recyclerView.smoothScrollToPosition(index);
            }
        } else if (requestCode == 3) { // aktualizacja ustawien
            updateNavBar();
        } else if (requestCode == 2001) {
            if (!articles.isEmpty()) {
                adapter.notifyItemRangeRemoved(0, articles.size());
            }
            articles = (ArrayList<Article>) Article.listAll(Article.class);
            adapter.notifyItemRangeInserted(0, articles.size());
            adapter.notifyDataSetChanged();
        }
        if (adapter.getItemCount() == 0) {
            ((TextView) findViewById(R.id.txtPustaSmieciarka)).setVisibility(View.VISIBLE);
        } else {
            ((TextView) findViewById(R.id.txtPustaSmieciarka)).setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.v("test", "onquerysub");
                adapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.v("test", "onquerychang");
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, MyPreferencesActivity.class);
            startActivityForResult(i, 3);
            return true;
        } else if (id == R.id.action_wynik) {
            sortArticlesByWynik(articles, 1);
            adapter.notifyDataSetChanged();
            return true;
        } else if (id == R.id.action_wynik2) {
            sortArticlesByWynik(articles, 2);
            adapter.notifyDataSetChanged();
            return true;
        } else if (id == R.id.action_data) {
            sortArticlesByWynik(articles, 3);
            adapter.notifyDataSetChanged();
            return true;
        } else if (id == R.id.action_data2) {
            sortArticlesByWynik(articles, 4);
            adapter.notifyDataSetChanged();
            return true;
        } else if (id == R.id.action_losowe) {
            losoweWyniki();
            adapter.notifyDataSetChanged();
            ((TextView) findViewById(R.id.txtPustaSmieciarka)).setVisibility(View.GONE);
        } else if (id == R.id.action_usun) {
            articles.clear();
            adapter.notifyDataSetChanged();
            ((TextView) findViewById(R.id.txtPustaSmieciarka)).setVisibility(View.VISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_wyniki) {
            //Intent i = new Intent(StatystykiActivity.this, ZmianaActivity.class);
            //finish();  //Kill the activity from which you will go to next activity
            //startActivity(i);
            // Handle the camera action
        } else if (id == R.id.nav_statystyki) {
            Intent i = new Intent(MainActivity.this, StatystykiActivity.class);
            i.putExtra("articles", articles);
            finish();
            startActivity(i);
        } else if (id == R.id.nav_ustawienia) {
            Intent i = new Intent(this, MyPreferencesActivity.class);
            startActivityForResult(i, 3);
            return true;
        } else if (id == R.id.nav_aplikacja) {
            Intent i = new Intent(this, KontaktActivity.class);
            finish();
            startActivity(i);
            return true;
        } else if (id == R.id.nav_narzedzia) {
            Intent i = new Intent(MainActivity.this, NarzedziaActivity.class);
            finish();
            startActivityForResult(i, 2001);
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
