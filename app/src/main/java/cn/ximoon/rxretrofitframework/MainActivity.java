package cn.ximoon.rxretrofitframework;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import cn.ximoon.rxretrofitframework.base.bean.IPBean;
import cn.ximoon.rxretrofitframework.logic.listener.ServerResultCallbaclk;
import cn.ximoon.rxretrofitframework.logic.model.IPBeanModel;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAINACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IPBeanModel.queryIp("", new ServerResultCallbaclk<IPBean>() {

            @Override
            public void onCached(IPBean ipBean) {
                Log.e(TAG, "onCached: " + ipBean.toString() );
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(IPBean data) {
//                Toast.makeText(MainActivity.this, data.ip + data.province + data.city + data.carrier, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onSuccess: " + data.ip + data.province + data.city + data.district + data.carrier);
            }

            @Override
            public void onFailed(int code, String msg) {
//                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailed: " + code + msg);
            }

            @Override
            public void cancel() {

            }
        });
    }
}
