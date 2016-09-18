package cn.ximoon.rxretrofitframework.bean;

/**
 * Created by XImoon on 16/9/14.
 */
public class BaseServiceResult<T> {

    public int ret;
    public String msg;
    public T data;

    @Override
    public String toString() {
        return "BaseServiceResult{" +
                "ret=" + ret +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
