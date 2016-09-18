package cn.ximoon.rxretrofitframework.logic;

import cn.ximoon.rxretrofitframework.logic.proxy.NetProxy;

/**
 * Created by XImoon on 16/9/14.
 */
public class Controller {

    private static Controller INSTANCE;
    private static NetProxy mNetProxy;

    private Controller() {
        INSTANCE = this;
        mNetProxy = new NetProxy();
    }

    public static Controller getInstance(){
        if (INSTANCE == null){
            synchronized (Controller.class){
                INSTANCE = new Controller();
            }
        }
        return INSTANCE;
    }

    public NetProxy getmNetProxy() {
        return mNetProxy;
    }
}
