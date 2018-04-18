package cn.com.kjsd.wifimanagedemo;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * @Create by 2018-04-18
 * @Author yinzhengwei
 */

public class MainActivity extends AppCompatActivity {

    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;

    LinearLayout wifilist = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.setNetWorkCallBack(new Utils.NetWorkCallBack() {
            @Override
            public void successful() {
                onclick();
            }
        });

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.finish();
            }
        });

        wifilist = findViewById(R.id.wifilist);

        findViewById(R.id.buttonPanel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclick();
            }
        });
        onclick();
    }

    public void onclick() {

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        openNetCard();

        mWifiInfo = mWifiManager.getConnectionInfo();

        scan();

        wifilist.removeAllViews();

        if (listResult.size() == 0) {
            Toast.makeText(this, "没有发现任何可用WiFi，请检查机顶盒网口是否可用或者周边有无线路由器！", Toast.LENGTH_SHORT).show();
            return;
        }

        for (final ScanResult scanResult : listResult) {
//        for (final WifiConfiguration scanResult : wifiConfigList) {
            TextView textView = new TextView(MainActivity.this);
            textView.setTextSize(25);
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundColor(Color.BLUE);
            textView.setPadding(0, 30, 0, 0);
            textView.setRight(30);

            Log.d("getSSID", "getSSID = " + getSSID() + "    SSID=" + scanResult.SSID);

            if (getSSID().equals("\"" + scanResult.SSID + "\"")) {
                textView.setText(scanResult.SSID + "\t\t\t已连接");
            } else {
                textView.setText(scanResult.SSID);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        disconnectWifi(getNetworkId());
                        WifiConfiguration wifiConfiguration = CreateWifiInfo(scanResult.SSID, "ktvms@789", 3);
                        mWifiManager.addNetwork(wifiConfiguration);
                        mWifiManager.enableNetwork(wifiConfiguration.networkId, true);
                        fetchState();
                    }
                });
            }
            wifilist.addView(textView);
        }
    }

    public void fetchState() {
        switch (mWifiManager.getWifiState()) {
            case 0:
                Log.i("fetchState", "网卡正在关闭");
                break;
            case 1:
                Log.i("fetchState", "网卡已经关闭");
                break;
            case 2:
                Log.i("fetchState", "网卡正在打开");
                break;
            case 3:
                Log.i("fetchState", "网卡已经打开");
                onclick();
                break;
            default:
                Log.i("fetchState", "没有获取到状态");
                break;
        }
    }

    // 得到连接的ID
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    // 得到接入点的BSSID
    public String getSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
    }

    //打开Wi-Fi网卡
    @SuppressLint("MissingPermission")
    public void openNetCard() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    //关闭Wi-Fi网卡
    public void closeNetCard() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    //检查当前Wi-Fi网卡状态
    @SuppressLint("WrongConstant")
    public void checkNetCardState() {
        switch (mWifiManager.getWifiState()) {
            case 0:
                Log.i("", "网卡正在关闭");
                break;
            case 1:
                Log.i("", "网卡已经关闭");
                break;
            case 2:
                Log.i("", "网卡正在打开");
                break;
            case 3:
                Log.i("", "网卡已经打开");
                break;
            default:
                Log.i("", "没有获取到状态");
                break;
        }
    }

    //得到IP地址
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

//    //网络连接列表
//    private List<WifiConfiguration> wifiConfigList = new ArrayList();
//    //Wi-Fi配置列表
//    private List wifiConfigedSpecifiedList = new ArrayList();
//
//    public void setWifiConfigedSpecifiedList(String ssid) {
//        wifiConfigedSpecifiedList.clear();
//        for (WifiConfiguration item : wifiConfigList) {
//            if (item.SSID.equalsIgnoreCase("\"" + ssid + "\"") && item.preSharedKey != null) {
//                wifiConfigedSpecifiedList.add(item);
//            }
//        }
//    }

    //保存扫描结果列表
    public List<ScanResult> listResult = new ArrayList();
    List<WifiConfiguration> wifiConfigList;

    @SuppressLint("MissingPermission")
    public void scan() {
        //开始扫描
        mWifiManager.startScan();
        listResult = mWifiManager.getScanResults();
        //扫描配置列表
        wifiConfigList = mWifiManager.getConfiguredNetworks();
    }

    public void disconnectWifi(int newId) {
        //获取网络ID
        mWifiManager.disableNetwork(newId);
        //断开网络
        mWifiManager.disconnect();
    }

    //然后是一个实际应用方法，只验证过没有密码的情况：1没有密码2用wep加密3用wpa加密
    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.IsExsits(SSID);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        if (Type == 1) //WIFICIPHER_NOPASS
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 2) //WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 3) //WIFICIPHER_WPA
        {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private WifiConfiguration IsExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }


}
