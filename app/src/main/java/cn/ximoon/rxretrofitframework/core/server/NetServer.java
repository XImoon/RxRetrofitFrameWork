package cn.ximoon.rxretrofitframework.core.server;


import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by XImoon on 16/9/14.
 */
public interface NetServer{

    @GET
    Call<ResponseBody> getRequest(@Url String url, @QueryMap Map<String, String> map);

    @FormUrlEncoded
    @POST
    Call<ResponseBody> postRequest(@Url String url, @FieldMap Map<String, String> postMap);

    @GET("{url}/{body}")
    Call<ResponseBody> getRequest(@Path("url") String url, @Path("body") String body);

    @POST
    Call<ResponseBody> postRequest(@Url String url, @Body Object body);
}
