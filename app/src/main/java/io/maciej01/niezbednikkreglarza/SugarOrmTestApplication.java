package io.maciej01.niezbednikkreglarza;

/**
 * Created by Maciej on 2017-04-07.
 */

import android.content.res.Configuration;
import com.orm.SugarApp;
import com.orm.SugarContext;

public class SugarOrmTestApplication extends SugarApp {

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SugarContext.init(getApplicationContext());
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }
}