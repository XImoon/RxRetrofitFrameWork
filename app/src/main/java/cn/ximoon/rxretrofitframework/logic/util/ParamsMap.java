package cn.ximoon.rxretrofitframework.logic.util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import cn.ximoon.rxretrofitframework.base.bean.QueryString;

/**
 * Created by XImoon on 16/9/26.
 */
public class ParamsMap {

    private Map<String, String> params;

    public ParamsMap(boolean isNeedSort){
        if (isNeedSort){
            params = new TreeMap<>();
        }else {
            params = new HashMap<>();
        }
        // add common config
    }

    public Map<String, String> put(String key, String value){
        params.put(key, value);
        return params;
    }

    public Map<String, String> put(QueryString queryString){
        params.put(queryString.getName(), queryString.getValue());
        return params;
    }

    public Map<String, String> excute(){
        return params;
    }

}
