package cn.ximoon.rxretrofitframework.logic.proxy;

import java.io.IOException;

import cn.ximoon.rxretrofitframework.core.manager.HttpManager;
import cn.ximoon.rxretrofitframework.core.server.NetServer;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.GzipSource;
import okio.Okio;

/**
 * Created by XImoon on 16/9/14.
 */
public class NetProxy {

    private HttpManager mManger;
    private String BASE_URL = "http://api.baidu.com";

    public NetProxy(){
        mManger = new HttpManager(BASE_URL, new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request.Builder builder = originalRequest.newBuilder();
                // HttpUrl originalHttpUrl = original.url();
                // String queryString = originalHttpUrl.encodeQuery();  // 获取url中的参数部分
                // String path = originalHttpUrl.url().getPath();       // 获取相对地址
                // Buffer buffer = new Buffer();
                // builder.body().writeTo(buffer);
                // String requestContent = buffer.readUtf8();  // 用于post请求获取form表单内容
                builder.addHeader("apikey", "");
                builder.addHeader("Accept-Encoding", "gzip");
                Request request = builder.build();
                return chain.proceed(request);
            }
        }, new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                Response.Builder builder = response.newBuilder();
                if (response.header("Content-Encoding", "").contains("gzip")){
                    BufferedSource bufferedSource = Okio.buffer(new GzipSource(response.body().source()));
                    String temStr = bufferedSource.readUtf8();
                    bufferedSource.close();
                    ResponseBody body = ResponseBody.create(MediaType.parse("application/json"), temStr);
                    builder.body(body);
                }else{
                    BufferedSource bufferedSource = Okio.buffer(response.body().source());
                    String temStr =bufferedSource.readUtf8();
                    bufferedSource.close();
                    ResponseBody body = ResponseBody.create(MediaType.parse("application/json"), temStr);
                    builder.body(body);
                }
                builder.removeHeader("Pragma");
                builder.header("Cache-Control","public , only-if-cached");
                return builder.build();
            }
        });
    }

    public NetServer getNetServer(){
        return mManger.getServer();
    }
}
