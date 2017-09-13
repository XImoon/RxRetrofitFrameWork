package cn.ximoon.rxretrofitframework.base.bean;

import java.net.URLEncoder;

/**
 * Created by XImoon on 16/9/14.
 */
public class QueryString implements Comparable{

    private String name;
    private String value;

    public QueryString(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toStringAddAnd(boolean isAddAnd) throws Exception {
        return (isAddAnd ? "&" : "") + name + "=" + URLEncoder.encode(value, "UTF-8");
    }

    @Override
    public int compareTo(Object qs){
        QueryString queryString = (QueryString) qs;
        String name1 = queryString.getName();
        String name2 = this.getName();
        return name1.compareTo(name2) < 0 ? 1 : -1;
    }
}
