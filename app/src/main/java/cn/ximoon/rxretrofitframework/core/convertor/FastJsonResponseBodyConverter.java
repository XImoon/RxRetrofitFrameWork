package cn.ximoon.rxretrofitframework.core.convertor;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * 对相应内容进行json处理
 * @param <T>
 */
public class FastJsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Type type;

    public FastJsonResponseBodyConverter(Type type) {
        this.type = type;
    }

    /*
    * 转换方法
    */
    @Override
    public T convert(ResponseBody value) throws IOException {
        String jsonStr = value.string();
        T t = JSON.parseObject(jsonStr, type);
        return t;

    }
}