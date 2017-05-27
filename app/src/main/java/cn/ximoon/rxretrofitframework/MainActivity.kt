package cn.ximoon.rxretrofitframework

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

import cn.ximoon.rxretrofitframework.bean.IPBean
import cn.ximoon.rxretrofitframework.logic.listener.ServerResultCallbaclk
import cn.ximoon.rxretrofitframework.logic.model.IPBeanModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        IPBeanModel.queryIp("", object : ServerResultCallbaclk<IPBean>() {

            override fun onCached(ipBean: IPBean) {
                Log.e(TAG, "onCached: " + ipBean.toString())
            }

            override fun onStart() {

            }

            override fun onSuccess(data: IPBean) {
                //                Toast.makeText(MainActivity.this, data.ip + data.province + data.city + data.carrier, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onSuccess: " + data.ip + data.province + data.city + data.district + data.carrier)
            }

            override fun onFailed(code: Int, msg: String) {
                //                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailed: " + code + msg)
            }

            override fun cancel() {

            }
        })
    }

    companion object {

        private val TAG = "MAINACTIVITY"
    }
}
