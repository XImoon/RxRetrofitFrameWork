package cn.ximoon.rxretrofitframework.bean;

/**
 * Created by XImoon on 16/9/14.
 */
public class BaseServiceResult<T> {

    public int errNum;
    public String errMsg;
    public T retData;

    @Override
    public String toString() {
        return "BaseServiceResult{" +
                "errNum=" + errNum +
                ", errMsg='" + errMsg + '\'' +
                ", retData=" + retData +
                '}';
    }
}
