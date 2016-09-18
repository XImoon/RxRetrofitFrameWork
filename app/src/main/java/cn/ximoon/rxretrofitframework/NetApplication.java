package cn.ximoon.rxretrofitframework;

import android.app.Application;

/**
 * Created by XImoon on 16/9/14.
 */
public class NetApplication extends Application {

    private static NetApplication INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }

    public static NetApplication getInstance(){
        if (INSTANCE == null){
            synchronized (NetApplication.class){
                INSTANCE = new NetApplication();
            }
        }
        return INSTANCE;
    }
}
