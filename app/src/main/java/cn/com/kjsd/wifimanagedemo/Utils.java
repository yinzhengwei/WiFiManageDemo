package cn.com.kjsd.wifimanagedemo;

/**
 * Created by yinzhengwei on 2018/3/21.
 */

public class Utils {

    interface NetWorkCallBack {
        void successful();
    }

    static NetWorkCallBack netWorkCallBack;

    public static void setNetWorkCallBack(NetWorkCallBack netWorkCallBacks) {
        netWorkCallBack = netWorkCallBacks;
    }


}
