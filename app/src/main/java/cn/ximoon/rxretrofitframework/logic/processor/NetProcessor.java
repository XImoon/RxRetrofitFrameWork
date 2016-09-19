package cn.ximoon.rxretrofitframework.logic.processor;


import android.support.annotation.IntDef;
import android.util.Log;

import com.alibaba.fastjson.JSON;

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
import cn.ximoon.rxretrofitframework.bean.BaseServiceResult;
import cn.ximoon.rxretrofitframework.bean.QueryString;
import cn.ximoon.rxretrofitframework.core.callback.NetResultCallback;
import cn.ximoon.rxretrofitframework.core.server.NetServer;
import cn.ximoon.rxretrofitframework.logic.Controller;
import cn.ximoon.rxretrofitframework.logic.listener.ErrorCode;
import okhttp3.ResponseBody;
import retrofit2.Call;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * @Author: Zora
 * @Email: zorabai@meilapp.com
 * @CreateTime: 2016/8/9 16:45
 * @Description: 网络处理器，对响应进行相应处理
 */
public class NetProcessor<T> {

    private NetResultCallback<T> mCallback;
    private Map<String, String> mQueryMap;
    private Map<String, String> mPostMap;
    private String mUrl;
    private @MethodType int mMethodType;
    private boolean mNeedRetry = true;
    private Subscriber<BaseServiceResult<T>> mSubscriber;
    private NetServer mServer;
    private static final String TAG = "NetWorkProcessor";

    public NetProcessor(){
        mQueryMap = new HashMap<>();
        mServer = Controller.getInstance().getmNetProxy().getNetServer();
    }


    public NetProcessor putParam(String key, String value) {
        mQueryMap.put(key, value);
        return this;
    }

    public NetProcessor postParam(String key, String value){
        if (mPostMap == null){
            synchronized (this){
                mPostMap = new HashMap<String, String>();
            }
        }
        mPostMap.put(key, value);
        return this;
    }

    public NetProcessor onQueryMap(Map<String, String> queryMap) {
        this.mQueryMap.putAll(queryMap);
        return this;
    }

    public NetProcessor onPostMap(Map<String, String> postMap) {
        if (mPostMap == null){
            mPostMap.putAll(postMap);
        }
        return this;
    }

    public NetProcessor onUrl(String url) {
        this.mUrl = url;
        return this;
    }

    public NetProcessor onCallback(NetResultCallback<T> callback) {
        this.mCallback = callback;
        return this;
    }

    public NetProcessor onRetry(boolean needRetry){
        this.mNeedRetry = needRetry;
        return this;
    }

    public NetProcessor get(){
        this.mMethodType = MethodType.METHOD_GET;
        return this.excute();
    }

    public NetProcessor post(){
        this.mMethodType = MethodType.METHOD_POST;
        return this.excute();
    }

    private NetProcessor excute() {
        mCallback.onStart();
        Observable.create(new Observable.OnSubscribe<BaseServiceResult<T>>() {
            @Override
            public void call(Subscriber<? super BaseServiceResult<T>> subscriber) {
                Call<ResponseBody> responseBody = null;
                switch (mMethodType){
                    case MethodType.METHOD_GET:
                        responseBody = mServer.getRequest(mUrl, mQueryMap);
                        break;
                    case MethodType.METHOD_POST:
                        responseBody = mServer.postRequest(mUrl, mPostMap, mQueryMap);
                        break;
                }
                try {
                    String strJSON = responseBody.execute().body().string();
                    BaseServiceResult baseServiceResult = JSON.parseObject(strJSON, BaseServiceResult.class);
                    subscriber.onNext(baseServiceResult);
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

    public void cancel(){
        if (mSubscriber != null && !mSubscriber.isUnsubscribed()){
            mSubscriber.unsubscribe();
            mCallback.cancel();
        }
    }

    private Subscriber<BaseServiceResult<T>> getSubscrobe(){
        mSubscriber = new Subscriber<BaseServiceResult<T>>() {

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
            public void onNext(BaseServiceResult<T> tBaseServiceResult) {
                if (mCallback != null) {
                    if (tBaseServiceResult == null){
                        mCallback.onFailed(ErrorCode.CODE_RESPONSE_EMPTY, NetApplication.getInstance().getString(R.string.connect_time_out));
                        return;
                    }
                    Log.i(TAG, "onNext: " + tBaseServiceResult.toString());
                    if (tBaseServiceResult.ret == ErrorCode.CODE_OK) {
                        mCallback.onSuccess(tBaseServiceResult.data);
                    }else{
                        mCallback.onFailed(tBaseServiceResult.ret, tBaseServiceResult.msg);
                    }
                }
            }
        };
        return mSubscriber;
    }

    @IntDef({MethodType.METHOD_GET, MethodType.METHOD_POST})
    public @interface MethodType {
        // GET请求
        int METHOD_GET = 0;
        // POST请求
        int METHOD_POST = 1;
    }

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

}
