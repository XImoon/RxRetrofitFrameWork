package cn.ximoon.rxretrofitframework.core.server;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by XImoon on 16/9/14.
 */
public interface NetServer {

    @GET
    Call<ResponseBody> getRequest(@Url String url, @QueryMap Map<String, String> map);

    @FormUrlEncoded
    @POST
    Call<ResponseBody> postRequest(@Url String url, @QueryMap Map<String, String> queryMap, @FieldMap Map<String, String> postMap);
}
