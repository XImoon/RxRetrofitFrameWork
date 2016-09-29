package cn.ximoon.rxretrofitframework.bean;

import java.io.Serializable;

/**
 * Created by XImoon on 16/9/29.
 */
public class IPBean implements Serializable{

    public String ip;
    public String country;
    public String province;
    public String city;
    public String district;
    public String carrier;

    @Override
    public String toString() {
        return "IPBean{" +
                "ip='" + ip + '\'' +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", carrier='" + carrier + '\'' +
                '}';
    }
}
