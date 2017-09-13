package cn.ximoon.rxretrofitframework.logic.util;

import android.content.Context;

import cn.ximoon.rxretrofitframework.NetApplication;

/**
 * Created by Admin on 2017/9/13.
 */

public class ApplicationUtil {

    public static Context getApplicationContext(){
        return NetApplication.getInstance().getApplicationContext();
    }
}
