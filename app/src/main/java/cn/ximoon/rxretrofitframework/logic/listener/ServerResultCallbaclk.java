package cn.ximoon.rxretrofitframework.logic.listener;

import cn.ximoon.rxretrofitframework.core.callback.NetResultCallback;

/**
 * Created by XImoon on 16/9/29.
 */
public abstract class ServerResultCallbaclk<T> implements NetResultCallback<T> {

    public void onCached(T t){

    }
}
