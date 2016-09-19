package cn.ximoon.rxretrofitframework.logic.listener;

/**
 * Created by XImoon on 16/9/14.
 */
public interface ErrorCode {

    int CODE_OK = 0;
    int CODE_TIME_OUT = -5000;
    int CODE_NET_ERROR = -5001;
    int CODE_RESPONSE_EMPTY = -5002;
}
