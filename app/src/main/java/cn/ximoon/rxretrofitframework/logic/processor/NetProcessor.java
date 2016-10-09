package cn.ximoon.rxretrofitframework.logic.processor;


import android.support.annotation.IntDef;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.ximoon.rxretrofitframework.NetApplication;
import cn.ximoon.rxretrofitframework.R;
import cn.ximoon.rxretrofitframework.bean.QueryString;
import cn.ximoon.rxretrofitframework.core.server.NetServer;
import cn.ximoon.rxretrofitframework.logic.Controller;
import cn.ximoon.rxretrofitframework.logic.listener.ErrorCode;
import cn.ximoon.rxretrofitframework.logic.listener.NetServiceResult;
import cn.ximoon.rxretrofitframework.logic.listener.ServerResultCallbaclk;
import okhttp3.CacheControl;
import okhttp3.ResponseBody;
import retrofit2.Call;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;



/**
 * Created by XImoon on 16/9/14.
 */
public class NetProcessor<T> {

    private ServerResultCallbaclk<T> mCallback;
    private Map<String, String> mQueryMap;
    private Map<String, String> mPostMap;
    private String mUrl;
    private Class<T> mClazz;
    private @MethodType int mMethodType;
    private boolean mNeedRetry = true;
    private Subscriber<NetServiceResult<T>> mSubscriber;
    private boolean isNeedCache;
    private NetServer mServer;
    private static final String TAG = "NetProcessor";

    /**
     * 获取GET请求方式的处理器
     * @param <T> 所需类型
     * @return
     */
    public static <T> NetProcessor<T> get(){
        NetProcessor<T> netProcessor = new NetProcessor<T>();
        netProcessor.mMethodType = MethodType.METHOD_GET;
        return netProcessor;
    }

    /**
     * 获取POST请求方式的处理器
     * @param <T> 所需类型
     * @return
     */
    public static <T> NetProcessor<T> post(){
        NetProcessor<T> netProcessor = new NetProcessor<T>();
        netProcessor.mPostMap = new HashMap<String, String>();
        netProcessor.mMethodType = MethodType.METHOD_POST;
        return netProcessor;
    }

    private NetProcessor(){
        mQueryMap = new HashMap<>();
        mServer = Controller.getInstance().getmNetProxy().getNetServer();
    }

    /**
     * 添加GET请求参数内容
     * @param key 参数名称
     * @param value 参数内容
     * @return
     */
    public NetProcessor<T> putParam(String key, String value) {
        mQueryMap.put(key, value);
        return this;
    }

    /**
     * 添加POST请求参数内容
     * @param key 参数名称
     * @param value 参数内容
     * @return
     */
    public NetProcessor<T> postParam(String key, String value){
        if (mPostMap == null){
            synchronized (this){
                mPostMap = new HashMap<String, String>();
            }
        }
        mPostMap.put(key, value);
        return this;
    }

    /**
     * 设置GET请求方式的参数
     * @param queryMap GET请求的参数内容
     * @return
     */
    public NetProcessor<T> onQueryMap(Map<String, String> queryMap) {
        this.mQueryMap.putAll(queryMap);
        return this;
    }

    /**
     * 设置POST请求的参数
     * @param postMap POST请求的表单内容
     * @return
     */
    public NetProcessor<T> onPostMap(Map<String, String> postMap) {
        if (mPostMap == null){
            mPostMap.putAll(postMap);
        }
        return this;
    }

    /**
     * 设置请求地址
     * @param url 请求地址
     * @return
     */
    public NetProcessor<T> onUrl(String url) {
        this.mUrl = url;
        return this;
    }

    /**
     * 设置请求响应回调监听
     * @param callback 监听器
     * @return
     */
    public NetProcessor<T> onCallback(ServerResultCallbaclk<T> callback) {
        this.mCallback = callback;
        return this;
    }

    /**
     * 设置解析类型
     * @param clazz 类（LIST<T>即传递T的类型）
     * @return
     */
    public NetProcessor<T> onClazz(Class<T> clazz){
        this.mClazz = clazz;
        return this;
    }

    /**
     * 是否需要重试策略
     * @param needRetry TRUE即需要，FALSE即不需要
     * @return
     */
    public NetProcessor<T> onRetry(boolean needRetry){
        this.mNeedRetry = needRetry;
        return this;
    }

    /**
     * 是否需要返回缓存结果
     * @param needCache TRUE即需要，FALSE即不需要
     * @return
     */
    public NetProcessor<T> onCached(boolean needCache){
        this.isNeedCache = needCache;
        return this;
    }

    /**
     * 执行API请求
     * @return  处理器
     */
    public NetProcessor<T> excute() {
        mCallback.onStart();
        Observable.create(new Observable.OnSubscribe<NetServiceResult<T>>() {

            @Override
            public void call(Subscriber<? super NetServiceResult<T>> subscriber) {
                Call<ResponseBody> responseBody = null;
                String strJSON = "";
                if (isNeedCache) {
                    responseBody = requestApi(responseBody);
                    try {
                        strJSON = responseBody.execute().body().string();
                        subscriber.onNext(parserJson(strJSON, true));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                responseBody = requestApi(responseBody);
                try {
                    strJSON = responseBody.execute().body().string();
                    subscriber.onNext(parserJson(strJSON, false));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
                    @Override
                    public Observable<?> call(Observable<? extends Throwable> observable) {
                        return observable.flatMap(new Func1<Throwable, Observable<?>>() {
                            @Override public Observable<?> call(Throwable error) {
                                if (mNeedRetry) {
                                    mNeedRetry = false;
                                    // For IOExceptions, we  retry
                                    if (error instanceof IOException) {
                                        return Observable.just(null);
                                    }
                                }
                                // For anything else, don't retry
                                return Observable.error(error);
                            }
                        });
                    }
                })
                .subscribe(getSubscrobe());
        return this;
    }

    /**
     * 请求网络
     * @param responseBody
     * @return
     */
    private Call<ResponseBody> requestApi(Call<ResponseBody> responseBody) {
        switch (mMethodType) {
            case MethodType.METHOD_GET:
                responseBody = mServer.getRequest(CacheControl.FORCE_CACHE.toString(), mUrl, mQueryMap);
                break;
            case MethodType.METHOD_POST:
                responseBody = mServer.postRequest(CacheControl.FORCE_CACHE.toString(), mUrl, mPostMap, mQueryMap);
                break;
        }
        return responseBody;
    }

    /**
     * 取消请求
     */
    public void cancel(){
        if (mSubscriber != null && !mSubscriber.isUnsubscribed()){
            mSubscriber.unsubscribe();
            mCallback.cancel();
        }
    }

    /**
     * 创建观察者
     * @return 新的观察者
     */
    private Subscriber<NetServiceResult<T>> getSubscrobe(){
        mSubscriber = new Subscriber<NetServiceResult<T>>() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if (mCallback != null) {
                    mCallback.onFailed(ErrorCode.CODE_TIME_OUT, NetApplication.getInstance().getString(R.string.connect_time_out));
                    Log.e(TAG, "onError: " + e.getMessage());
                }
            }

            @Override
            public void onNext(NetServiceResult<T> tNetServiceResult) {
                if (mCallback != null) {
                    if (tNetServiceResult == null){
                        mCallback.onFailed(ErrorCode.CODE_RESPONSE_EMPTY, NetApplication.getInstance().getString(R.string.connect_time_out));
                        return;
                    }
                    Log.i(TAG, "onNext: " + tNetServiceResult.toString());
                    if (tNetServiceResult.errNum == ErrorCode.CODE_OK) {
                        if (tNetServiceResult.isFromCache){
                            mCallback.onCached(tNetServiceResult.retData);
                        } else {
                            mCallback.onSuccess(tNetServiceResult.retData);
                        }
                    }else{
                        mCallback.onFailed(tNetServiceResult.errNum, tNetServiceResult.errMsg);
                    }
                }
            }
        };
        return mSubscriber;
    }

    /**
     * 请求方式
     */
    @IntDef({MethodType.METHOD_GET, MethodType.METHOD_POST})
    public @interface MethodType {
        // GET请求
        int METHOD_GET = 0;
        // POST请求
        int METHOD_POST = 1;
    }

    /**
     * GET请求的参数拼接
     * @return 参数URL表示结果
     */
    private String getQuertString(){
        if (mQueryMap == null){
            return "";
        }
        List<QueryString> list = new ArrayList<>();
        QueryString query = null;
        Set<String> set = mQueryMap.keySet();
        StringBuilder queryBuilder = new StringBuilder();
        for (String key : set) {
            query = new QueryString(key, mQueryMap.get(key));
            list.add(query);
        }
        Collections.sort(list);
        int size = list.size();
        try {
            for (int i = 0; i < size; i++) {
                queryBuilder.append(list.get(i).toStringAddAnd( i == 0 ? false : true));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queryBuilder.toString();
    }

    /**
     * POST请求的参数拼接
     * @return 参数URL表示结果
     */
    private String getPostString(){
        if (mPostMap == null){
            return "";
        }
        Set<String> set = mPostMap.keySet();
        StringBuilder postBuilder = new StringBuilder();
        for (String key : set) {
            try {
                postBuilder.append("&").append(key).append("=").append(URLEncoder.encode(mPostMap.get(key), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        postBuilder.deleteCharAt(0);
        return postBuilder.toString();
    }

    /**
     * JSON解析
     * @param strJSON   JSON字符串
     * @param isFromCache   是否缓存结果
     * @return  协议结果
     */
    private NetServiceResult<T> parserJson(String strJSON, boolean isFromCache){
        NetServiceResult<T> netServiceResult = JSON.<NetServiceResult<T>>parseObject(strJSON, NetServiceResult.class);
        if (netServiceResult.retData != null) {
            if (netServiceResult.retData instanceof JSONObject) {
                netServiceResult.retData = JSON.parseObject(((JSONObject) netServiceResult.retData).toJSONString(), mClazz);
            } else if (netServiceResult.retData instanceof JSONArray) {
                netServiceResult.retData = (T) JSON.parseArray(((JSONArray) netServiceResult.retData).toJSONString(), mClazz);
            }
        }
        netServiceResult.isFromCache = isFromCache;
        return netServiceResult;
    }

}
