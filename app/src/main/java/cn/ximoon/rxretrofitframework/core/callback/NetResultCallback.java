package cn.ximoon.rxretrofitframework.core.callback;

/**
 * Created by XImoon on 16/9/14.
 */
public interface NetResultCallback<E> {

    void onStart();

    void onSuccess(E data);

    void onFailed(int code, String msg);

    void cancel();
}
