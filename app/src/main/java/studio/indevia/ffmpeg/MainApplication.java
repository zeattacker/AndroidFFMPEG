package studio.indevia.ffmpeg;

import android.app.Application;
import android.support.multidex.MultiDex;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
    }
}
