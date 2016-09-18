package cn.ximoon.rxretrofitframework.core.manager;

import java.io.File;

import cn.ximoon.rxretrofitframework.NetApplication;
import cn.ximoon.rxretrofitframework.core.server.NetServer;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by XImoon on 16/9/14.
 */
public class HttpManager {

    private Retrofit mRetrofit;
    private OkHttpClient mHttpClient;
    private NetServer mNetServer;

    public HttpManager(String url, Interceptor interceptor, Interceptor netInterceptor){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cache(new Cache(new File(NetApplication.getInstance().getApplicationContext().getCacheDir(), "caches"), 1024 * 1024 * 100));
        mHttpClient = builder.addInterceptor(interceptor).addNetworkInterceptor(netInterceptor).build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(mHttpClient)
                .build();
        mNetServer = mRetrofit.create(NetServer.class);
    }

    public NetServer getServer(){
        return mNetServer;
    }
}
