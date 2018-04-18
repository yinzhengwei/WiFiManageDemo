package cn.com.kjsd.wifimanagedemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetWorkStateUtils extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            // 如果相等的话就说明网络状态发生了变化
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                //得到连接管理器对象
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo == null || !activeNetworkInfo.isAvailable()) {
                    Log.d("onReceive", "disconnect");
//                Toast.makeText(context, "successful", Toast.LENGTH_SHORT).show();
//                EventBus.getDefault().post(ContansUtils.NOCONECT_NETWORK)
                } else {
//                CommandMethod.closeNetworkWindow()
                    Log.d("onReceive", "successful");
                    Utils.netWorkCallBack.successful();
//                Toast.makeText(context, "disconnect", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }