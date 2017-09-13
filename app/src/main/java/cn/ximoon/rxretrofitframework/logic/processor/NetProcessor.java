package cn.ximoon.rxretrofitframework.logic.processor;


import android.support.annotation.IntDef;
import android.util.Log;

import java.io.IOError;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.ximoon.rxretrofitframework.R;
import cn.ximoon.rxretrofitframework.core.callback.ErrorCode;
import cn.ximoon.rxretrofitframework.core.server.NetServer;
import cn.ximoon.rxretrofitframework.logic.Controller;
import cn.ximoon.rxretrofitframework.logic.listener.ServerResultCallbaclk;
import cn.ximoon.rxretrofitframework.logic.util.ApplicationUtil;
import cn.ximoon.rxretrofitframework.logic.util.NetParserXMLUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;


/**
 * Created by XImoon on 16/9/14.
 */
public class NetProcessor<E,T> {

    private ServerResultCallbaclk<T> mCallback;
    private Map<String, String> mQueryMap;
    private Map<String, String> mPostMap;
    private E mBody;
    private String mUrl;
    private Class<T> mClazz;
    @MethodType
    private int mMethodType;
    private boolean mNeedRetry = true;
    private Observer<T> mSubscriber;
    private Disposable mDisposable;
    private boolean isNeedCache;
    private NetServer mServer;
    private static final String TAG = "NetProcessor";

    /**
     * 获取GET请求方式的处理器
     *
     * @param <E, T> E为请求参数对象类型，T为响应内容对象类型
     * @return
     */
    public static <E, T> NetProcessor<E, T> get() {
        NetProcessor<E, T> netProcessor = new NetProcessor<E, T>();
        netProcessor.mMethodType = MethodType.METHOD_GET;
        return netProcessor;
    }

    /**
     * 获取POST请求方式的处理器
     *
     * @param <E, T> E为请求参数对象类型，T为响应内容对象类型
     * @return
     */
    public static <E, T> NetProcessor<E, T> post() {
        NetProcessor<E, T> netProcessor = new NetProcessor<E, T>();
        netProcessor.mPostMap = new HashMap<String, String>();
        netProcessor.mMethodType = MethodType.METHOD_POST;
        return netProcessor;
    }

    private NetProcessor() {
        mQueryMap = new HashMap<>();
        mServer = Controller.getInstance().getmNetProxy().getNetServer();
    }

    /**
     * 添加GET请求参数内容
     *
     * @param key   参数名称
     * @param value 参数内容
     * @return
     */
    public NetProcessor<E, T> putParam(String key, String value) {
        mQueryMap.put(key, value);
        return this;
    }

    public NetProcessor<E, T> putParam(E queryValue) {
        mBody = queryValue;
        return this;
    }

    /**
     * 添加POST请求参数内容
     *
     * @param key   参数名称
     * @param value 参数内容
     * @return
     */
    public NetProcessor<E, T> postParam(String key, String value) {
        if (mPostMap == null) {
            synchronized (this) {
                mPostMap = new HashMap<String, String>();
            }
        }
        mPostMap.put(key, value);
        return this;
    }

    /**
     * 设置GET请求方式的参数
     *
     * @param queryMap GET请求的参数内容
     * @return
     */
    public NetProcessor<E, T> onQueryMap(Map<String, String> queryMap) {
        this.mQueryMap.putAll(queryMap);
        return this;
    }

    /**
     * 设置POST请求的参数
     *
     * @param postMap POST请求的表单内容
     * @return
     */
    public NetProcessor<E, T> onPostMap(Map<String, String> postMap) {
        if (mPostMap == null) {
            mPostMap.putAll(postMap);
        }
        return this;
    }

    /**
     * 设置请求地址
     *
     * @param url 请求地址
     * @return
     */
    public NetProcessor<E, T> onUrl(String url) {
        this.mUrl = url;
        return this;
    }

    /**
     * 设置请求响应回调监听
     *
     * @param callback 监听器
     * @return
     */
    public NetProcessor<E, T> onCallback(ServerResultCallbaclk<T> callback) {
        this.mCallback = callback;
        return this;
    }

    /**
     * 设置解析类型
     *
     * @param clazz 类（LIST<E, T>即传递T的类型）
     * @return
     */
    public NetProcessor<E, T> onClazz(Class<T> clazz) {
        this.mClazz = clazz;
        return this;
    }

    /**
     * 是否需要重试策略
     *
     * @param needRetry TRUE即需要，FALSE即不需要
     * @return
     */
    public NetProcessor<E, T> onRetry(boolean needRetry) {
        this.mNeedRetry = needRetry;
        return this;
    }

    /**
     * 是否需要返回缓存结果
     *
     * @param needCache TRUE即需要，FALSE即不需要
     * @return
     */
    public NetProcessor<E, T> onCached(boolean needCache) {
        this.isNeedCache = needCache;
        return this;
    }

    /**
     * 执行API请求
     *
     * @return 处理器
     */
    public NetProcessor excute() {
        mCallback.onStart();
        Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                Call<ResponseBody> responseBody = null;
                switch (mMethodType) {
                    case MethodType.METHOD_GET:
                        if (null != mBody){
                            responseBody = mServer.getRequest(mUrl, NetParserXMLUtil.convertString(mBody));
                        }else {
                            responseBody = mServer.getRequest(mUrl, mQueryMap);
                        }
                        break;
                    case MethodType.METHOD_POST:
                        if (null != mBody){
                            responseBody = mServer.postRequest(mUrl, mBody);
                        }else {
                            responseBody = mServer.postRequest(mUrl, mPostMap);
                        }
                        break;
                }
                T t = NetParserXMLUtil.parserT(mClazz, responseBody.execute().body());
                if (null == t){
                    emitter.onError(new IOError(new IOException("parser error")));
                }else {
                    emitter.onNext(t);
                }
                emitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscrobeObserver());
        return this;
    }

    /**
     * 请求网络
     *
     * @param responseBody
     * @return
     */
    private Call<T> requestApi(Call<T> responseBody) {

        return responseBody;
    }

    /**
     * 取消请求
     */
    public void cancel() {
        if (null != mDisposable && mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    /**
     * 创建观察者
     *
     * @return 新的观察者
     */
    private Observer<T> getSubscrobeObserver() {
        mSubscriber = new Observer<T>() {

            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onNext(T result) {
                if (mDisposable.isDisposed()) {
                    return;
                }
                if (mCallback != null) {
                    mCallback.onSuccess(result);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (mCallback != null) {
                    mCallback.onFailed(ErrorCode.CODE_TIME_OUT, ApplicationUtil.getApplicationContext().getString(R.string.connect_time_out));
                    Log.e(TAG, "onError: " + e.getMessage());
                }
            }

            @Override
            public void onComplete() {

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
     *
     * @return 参数URL表示结果
     */
    private String getQuertString() {
        if (null != mQueryMap) {
            Set<String> set = mQueryMap.keySet();
            StringBuilder builder = new StringBuilder();
            for (String key : set) {
                builder.append("&").append(key).append("=").append(mQueryMap.get(key));
            }
            builder.deleteCharAt(0).insert(0, "\\?");
            return builder.toString();
        }
        return "";
    }

    /**
     * POST请求的参数拼接
     *
     * @return 参数URL表示结果
     */
    private String getPostString() {
        if (mPostMap == null) {
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
