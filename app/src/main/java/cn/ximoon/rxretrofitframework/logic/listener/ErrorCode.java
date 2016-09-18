package cn.ximoon.rxretrofitframework.logic.listener;

/**
 * Created by XImoon on 16/9/14.
 */
public interface ErrorCode {

    static final int CODE_OK = 0;
    static final int CODE_TIME_OUT = -5000;
    static final int CODE_NET_ERROR = -5001;
    static final int CODE_RESPONSE_EMPTY = -5002;
}
