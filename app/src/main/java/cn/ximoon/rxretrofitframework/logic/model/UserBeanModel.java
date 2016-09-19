package cn.ximoon.rxretrofitframework.logic.model;

import java.util.HashMap;
import java.util.Map;

import cn.ximoon.rxretrofitframework.bean.UserBean;
import cn.ximoon.rxretrofitframework.core.callback.NetResultCallback;
import cn.ximoon.rxretrofitframework.logic.processor.NetProcessor;

/**
 * Created by XImoon on 16/9/14.
 */
public class UserBeanModel {

    public NetProcessor getUser(NetResultCallback<UserBean> callback){
        Map<String, String> params = new HashMap<>();
        params.put("name", "username");
        params.put("psw", "userpsw");
        return NetProcessor.get()
                .onCallback(callback)
                .onRetry(true)
                .onClazz(UserBean.class)
                .onUrl("/get/user")
                .onQueryMap(params)
                .excute();
    }
}
