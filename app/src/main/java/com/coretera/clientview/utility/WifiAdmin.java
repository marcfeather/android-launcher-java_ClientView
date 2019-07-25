package com.coretera.clientview.utility;


import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import java.lang.reflect.Method;
import java.util.List;

public class WifiAdmin {
    public static final String SSID = "test";
    public static final String PassWord = "12345678";
    private final DhcpInfo mDhcpInfo;
    private WifiManager mWifiManager;//wifimanager object
    private WifiInfo mWifiInfo; // Define WifiInfo objects
    private List<ScanResult> mWifiList;  // Scanned list of network connections
    private List<WifiConfiguration> mWifiConfiguration;  // Network Connection List
    WifiManager.WifiLock mWifiLock;  // Define a WifiLock
    private static final int NOPASSWORD = 0;
    private static final int PASSWORD_WPA = 1;
    private static final int PASSWORD_WEP = 2;
    private static final int PASSWORD_WPA2 = 3;

    // constructor
    public WifiAdmin(Context context) {
        // Get the WifiManager object
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // Get WifiInfo objects
        mWifiInfo = mWifiManager.getConnectionInfo();
        mDhcpInfo = mWifiManager.getDhcpInfo();
    }

    //Open wifi
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    //Close WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    //Creating hot spots
    public void createAp() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
        try {
            WifiConfiguration apConfiguration = new WifiConfiguration();
            apConfiguration.SSID = WifiAdmin.SSID;
            apConfiguration.preSharedKey = WifiAdmin.PassWord;
            apConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(mWifiManager, apConfiguration, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Turn off WiFi Hotspots
    public void closeWifiAp() {
        if (isWifiApEnabled()) {
            try {
                Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);
                WifiConfiguration config = (WifiConfiguration) method.invoke(mWifiManager);
                Method method2 = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                method2.invoke(mWifiManager, config, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //Whether the Hot Spot Switch is Opened
    public boolean isWifiApEnabled() {
        try {
            Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(mWifiManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Check the current WIFI status
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    // Lock WifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    // Unlock WifiLock
    public void releaseWifiLock() {
        // Lock-in at Judgment
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }


    // Create a WifiLock
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    // Get a well-configured network
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }


    // Specify a configured network for connection
    public void connectConfiguration(int index) {
        // Index greater than the configured network index return
        if (index > mWifiConfiguration.size()) {
            return;
        }
        // Connect the network with the specified ID configured
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId, true);
    }

    /**
     * Scanning WIFI
     */
    public void startScan() {
        mWifiManager.startScan();
        // Scanning results obtained
        mWifiList = mWifiManager.getScanResults();
        // Get a configured network connection
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
    }

    // Get a list of networks
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    // View the scan results
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder.append("Index_" + new Integer(i + 1).toString() + ":");
            // Converting ScanResult information into a string package
            // These include BSSID, SSID, capabilities, frequency, level.
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }

    // BSSID Getting Access Points
    public String getSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
    }

    // Get the MAC address
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    // BSSID Getting Access Points
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }


    // Get the IP address
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    public int getServerIPAddress() {
        return (mWifiInfo == null) ? 0 : mDhcpInfo.serverAddress;
    }


    // Get the ID of the connection
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    // Get all the information packages for WifiInfo
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }


    // Add a network and connect
    public int addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        boolean b = mWifiManager.enableNetwork(wcgID, true);
        mWifiManager.reassociate();
        //LogUtils.e("b--" + b);
        return wcgID;
    }


    // Create wificonfig
    public WifiConfiguration createWifiConfig(String SSID, String Password, int Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();

        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";


        //If the device is larger than 6.0 configuration, there is no need for double quotation marks, and the connection is not possible with the addition of double quotation marks.
        if (Build.VERSION.SDK_INT >= 23) {
            config.SSID = SSID;
        } else {
            config.SSID = "\"" + SSID + "\"";
        }

        WifiConfiguration tempConfig = isExsits(SSID);
        if (tempConfig != null) {// Remove wifi that is automatically saved
            disconnectWifi(tempConfig.networkId);
        }

        if (Type == NOPASSWORD) // WIFICIPHER_NOPASS
        {
            config.hiddenSSID = true;
            //config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            //config.wepTxKeyIndex = 0;
        }
        if (Type == PASSWORD_WPA) // WIFICIPHER_WPA
        {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        if (Type == PASSWORD_WEP) // WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        return config;
    }

    /**
     * Judging whether wifi exists
     *
     * @param SSID
     * @return
     */
    public WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
//        if (!ArrayUtils.isEmpty(existingConfigs)) {
//            for (WifiConfiguration existingConfig : existingConfigs) {
//                if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
//                    return existingConfig;
//                }
//            }
//        }
        return null;
    }


    /**
     * Connecting Target Hotspots
     *
     * @param scanResult Hotspot Encryption
     */
    public int connectToTarget(ScanResult scanResult, String password) {
        int mNetworkID = 0;
        int password_type = 0;
        WifiConfiguration mTargetWifiCfg;
        if (scanResult != null) {
            if (scanResult.capabilities.contains("WPA") || scanResult.capabilities.contains("wpa")) {
                password_type = PASSWORD_WPA;
            } else if (scanResult.capabilities.contains("WEP") || scanResult.capabilities.contains("wep")) {
                password_type = PASSWORD_WEP;
            } else if (scanResult.capabilities.contains("WPA2") || scanResult.capabilities.contains("wpa2")) {
                password_type = PASSWORD_WPA2;
            } else {
                password_type = NOPASSWORD;
            }
        }
        //LogUtils.e(scanResult.SSID+"::::::::::::::::::::" + password_type); //password_type=1 WPA
        mTargetWifiCfg = createWifiConfig(scanResult.SSID, password, password_type);// Get wificonfig
        mNetworkID = addNetwork(mTargetWifiCfg);
        return mNetworkID;
    }

    public int connectToTarget(String SSID, String password) {
        int mNetworkID = 0;
        int password_type = PASSWORD_WPA;
        WifiConfiguration mTargetWifiCfg;
        mTargetWifiCfg = createWifiConfig(SSID, password, password_type);// Get wificonfig
        mNetworkID = addNetwork(mTargetWifiCfg);
        return mNetworkID;
    }


    // Disconnect the network with the specified ID
    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
        mWifiManager.removeNetwork(netId);
    }


    /**
     * Converting IP Address
     *
     * @param i
     * @return
     */
    public String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + ((i >> 24) & 0xFF);
    }
}
