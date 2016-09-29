package cn.ximoon.rxretrofitframework.logic.model;

import java.util.HashMap;
import java.util.Map;

import cn.ximoon.rxretrofitframework.bean.IPBean;
import cn.ximoon.rxretrofitframework.logic.listener.ServerResultCallbaclk;
import cn.ximoon.rxretrofitframework.logic.processor.NetProcessor;

/**
 * Created by XImoon on 16/9/29.
 */
public class IPBeanModel {

    public static NetProcessor<IPBean> queryIp(String ip, ServerResultCallbaclk<IPBean> callback){
        Map<String, String> params = new HashMap<>();
        params.put("ip", ip);
        return NetProcessor.<IPBean>get()
                .onClazz(IPBean.class)
                .onQueryMap(params)
                .onCached(true)
                .onUrl("/apistore/iplookupservice/iplookup")
                .onCallback(callback)
                .excute();

    }
}
