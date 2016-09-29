package cn.ximoon.rxretrofitframework.logic.listener;

import cn.ximoon.rxretrofitframework.bean.BaseServiceResult;

/**
 * Created by XImoon on 16/9/29.
 */
public class NetServiceResult<T> extends BaseServiceResult<T> {

    public boolean isFromCache;
}
