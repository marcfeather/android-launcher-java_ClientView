package com.coretera.clientview.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.coretera.clientview.R;

import java.util.ArrayList;
import java.util.List;

public class WifiReceiver extends BroadcastReceiver {
    WifiManager wifiManager;
    //StringBuilder sb;
    ListView wifiDeviceList;
    public WifiReceiver(WifiManager wifiManager, ListView wifiDeviceList) {
        this.wifiManager = wifiManager;
        this.wifiDeviceList = wifiDeviceList;
    }
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            //sb = new StringBuilder();

            WifiInfo info = wifiManager.getConnectionInfo ();
            String ssidConnected  = info.getSSID().replace("\"", "");
            Log.d("WifiReceiver", "onReceive: ssid = ".concat(ssidConnected));

            List<ScanResult> wifiList = wifiManager.getScanResults();
            ArrayList<String> deviceList = new ArrayList<>();
            for (ScanResult scanResult : wifiList) {
                //sb.append("\n").append(scanResult.SSID).append(" - ").append(scanResult.capabilities);
                //deviceList.add(scanResult.SSID + " - " + scanResult.capabilities);

                if (scanResult.SSID.equals(ssidConnected)) {
                    scanResult.SSID = scanResult.SSID.concat(" - Connected");
                }

                if (!deviceList.contains(scanResult.SSID)) {
                    deviceList.add(scanResult.SSID);
                    Log.d("WifiReceiver", "onReceive: scanResult = ".concat(scanResult.SSID));
                }
            }
            //Toast.makeText(context, sb, Toast.LENGTH_SHORT).show();
            ArrayAdapter arrayAdapter = new ArrayAdapter(context, R.layout.wifi_item, deviceList.toArray());
            wifiDeviceList.setAdapter(arrayAdapter);
        }
    }
}
