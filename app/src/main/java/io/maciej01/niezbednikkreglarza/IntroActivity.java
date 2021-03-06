package io.maciej01.niezbednikkreglarza;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;

/**
 * Created by Maciej on 2017-04-12.
 */

public class IntroActivity extends AppIntro2 {
    public static String FIRST_TITLE;
    public static String FIRST_DESC;

    public static String SECOND_TITLE;
    public static String SECOND_DESC;

    public static String THIRD_TITLE;
    public static String THIRD_DESC;

    public static String FOURTH_TITLE;
    public static String FOURTH_DESC;

    public static String FIFTH_TITLE;
    public static String FIFTH_DESC;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        initVariables();

        addSlide(AppIntro2Fragment.newInstance(FIRST_TITLE, FIRST_DESC, R.drawable.kuzelki, getResources().getColor(R.color.colorPrimary)));
        addSlide(AppIntro2Fragment.newInstance(SECOND_TITLE, SECOND_DESC, R.drawable.text_icon, getResources().getColor(R.color.colorTetradBlue)));
        addSlide(AppIntro2Fragment.newInstance(THIRD_TITLE, THIRD_DESC, R.drawable.photo_icon, getResources().getColor(R.color.colorTetradOrange)));
        addSlide(AppIntro2Fragment.newInstance(FOURTH_TITLE, FOURTH_DESC, R.drawable.add_icon, getResources().getColor(R.color.colorTetradRed)));
        addSlide(AppIntro2Fragment.newInstance(FIFTH_TITLE, FIFTH_DESC, R.drawable.chart_icon, getResources().getColor(R.color.colorTriadPink)));

        showSkipButton(true);
        setProgressButtonEnabled(true);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        5);
            }
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        5);
            }
        }
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        showSystemUI();
        finish();
        super.onSkipPressed(currentFragment);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        showSystemUI();
        finish();
        super.onDonePressed(currentFragment);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }

    private void initVariables() {
        FIRST_TITLE = getString(R.string.app_name);
        FIRST_DESC = getString(R.string.intro_desc1);
        SECOND_TITLE = getString(R.string.intro_title2);
        SECOND_DESC = getString(R.string.intro_desc2);
        THIRD_TITLE = getString(R.string.intro_title3);
        THIRD_DESC = getString(R.string.intro_desc3);
        FOURTH_TITLE = getString(R.string.intro_title4);
        FOURTH_DESC = getString(R.string.intro_desc4);
        FIFTH_TITLE = getString(R.string.intro_title5);
        FIFTH_DESC = getString(R.string.intro_desc5);
    }
    // This snippet hides the system bars.
    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }
}
