package com.coretera.clientview.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.coretera.clientview.Callback;
import com.coretera.clientview.R;
import com.coretera.clientview.utility.*;

import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ConfigFragment extends Fragment implements View.OnClickListener {

    Callback mCallback;
    Context mContext;

    private ListView wifiList;
    private WifiManager wifiManager;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    WifiReceiver receiverWifi;

    private Switch SwitchWifi;
    //private TextView TextWifiStatusValue;
    private TextView TextWifiSelected;
    private EditText EditTextPassword;
    private Button ButtonWifiConnect;
    private Button BtnScaleType_decrease, BtnScaleType_increase;

    private TextView mTextViewSlideTime, mTextViewVideoTime, mTextViewScaleType;
    private Integer slideTime, scaleTypeId, videoTime;
    public static final Integer length = 5;
    public static final Integer videoLength = 10;
    public static final Integer videoDefault = 30;

    private InputMethodManager inputMethodManager;

    private String networkType;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (Callback) context;
        }catch (Exception e){
            setting.toastException(mContext, e.getMessage());
        }
    }

    public ConfigFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_config, container, false);

        mContext = getContext();

        try {
            view.findViewById(R.id.button_done).setOnClickListener(this);
            view.findViewById(R.id.wifi_connect).setOnClickListener(this);
            view.findViewById(R.id.slide_time_decrease).setOnClickListener(this);
            view.findViewById(R.id.slide_time_increase).setOnClickListener(this);
            view.findViewById(R.id.scaleType_decrease).setOnClickListener(this);
            view.findViewById(R.id.scaleType_increase).setOnClickListener(this);
            view.findViewById(R.id.slide_videoTime_decrease).setOnClickListener(this);
            view.findViewById(R.id.slide_videoTime_increase).setOnClickListener(this);

            SwitchWifi = view.findViewById(R.id.SwitchWifi);
            //TextWifiStatusValue = view.findViewById(R.id.Text_Wifi_Status);
            mTextViewSlideTime = view.findViewById(R.id.slide_time_value);
            mTextViewScaleType = view.findViewById(R.id.scaleType_value);
            mTextViewVideoTime = view.findViewById(R.id.slide_videoTime_value);
            wifiList = view.findViewById(R.id.wifiList);
            TextWifiSelected = view.findViewById(R.id.wifi_selected);
            EditTextPassword = view.findViewById(R.id.wifi_Password);
            ButtonWifiConnect = view.findViewById(R.id.wifi_connect);

            BtnScaleType_decrease = view.findViewById(R.id.scaleType_decrease);
            BtnScaleType_increase = view.findViewById(R.id.scaleType_increase);

            SwitchWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //hide soft keyboard
                    UseSoftKeyboard(false);

                    if (isChecked && !wifiManager.isWifiEnabled()){
                        wifiManager.setWifiEnabled(true);
                        SwitchWifi.setText("On");
                        ClearBeforeScan(true);
                        ScanWifi();

                    }else if(!isChecked && wifiManager.isWifiEnabled()){
                        wifiManager.setWifiEnabled(false);
                        SwitchWifi.setText("Off");
                        ClearBeforeScan(false);
                    }
                }
            });

            wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Get the selected item text from ListView
                    String selectedItem = (String) parent.getItemAtPosition(position);

                    TextWifiSelected.setVisibility(View.VISIBLE);
                    TextWifiSelected.setText(selectedItem);

                    //setting.toastException(mContext, networkType);
                    networkType = getScanResultSecurity(wifiManager, selectedItem);
                    if (!networkType.equals("OPEN")) {
                        EditTextPassword.setVisibility(View.VISIBLE);
                        EditTextPassword.setText("");
                        EditTextPassword.requestFocus();
                    }else {
                        EditTextPassword.setVisibility(View.GONE);
                    }

                    ButtonWifiConnect.setVisibility(View.VISIBLE);

                    //show soft keyboard
                    UseSoftKeyboard(true);
                }
            });

        }catch (Exception e){
            setting.toastException(mContext, e.getMessage());
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null && wifiManager.isWifiEnabled()){
                SwitchWifi.setChecked(true);
                SwitchWifi.setText("On");
                ClearBeforeScan(true);
                ScanWifi();

            }else if(wifiManager != null && !wifiManager.isWifiEnabled()){
                SwitchWifi.setChecked(false);
                SwitchWifi.setText("Off");
            }

            SetValue();

//            ClearBeforeScan();
//            OpenWifi();
//            ScanWifi();

//            mWifiAdmin.openWifi();// Display the list the first time you come in
//            scanFlag = 1;
//            mWifiAdmin.startScan();//Start scanning to send notifications
//            //AppUtils.getInstance().showLoading(this);

        }catch (Exception e){
            setting.toastException(mContext, e.getMessage());
        }
    }

    private void SetValue() {
        slideTime = setting.GetSlideTime(getContext());
        if (slideTime == 0) { slideTime = length; }
        mTextViewSlideTime.setText(String.valueOf(slideTime));

        scaleTypeId = setting.GetImageScaleType(getContext());
        mTextViewScaleType.setText(getScaleTypeText(scaleTypeId));

        videoTime = setting.GetVideoTime(getContext());
        if (videoTime == 0) { videoTime = videoLength; }
        mTextViewVideoTime.setText(String.valueOf(videoTime));
    }

    private String getScaleTypeText(Integer scaleTypeId) {
        String ret = "";
        switch (scaleTypeId){
            case 1:
                ret = "CENTER";
                break;
            case 2:
                ret = "CENTER INSIDE";
                break;
            case 3:
                ret = "CENTER CROP";
                break;
            case 4:
                ret = "FIT START";
                break;
            case 5:
                ret = "FIT END";
                break;
            case 6:
                ret = "FIT CENTER";
                break;
            case 7:
                ret = "FIT XY";
                break;
            case 8:
                ret = "MATRIX";
                break;
            default:
                ret = "CENTER INSIDE";
                break;

        }
        return ret;
    }

    private void UseSoftKeyboard(Boolean value)
    {
        if (value) {
            //show soft keyboard
            inputMethodManager = (InputMethodManager) getContext()
                    .getApplicationContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.showSoftInput(EditTextPassword, 0);
            }
        }else {
            //hide soft keyboard
            if (inputMethodManager != null && getActivity().getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    public void onClick(View v) {
        try {
            Fragment fragment;
            switch (v.getId()) {
                case R.id.button_done:
                    //hide soft keyboard
                    UseSoftKeyboard(false);

                    //mConnectThread = null;

                    //setting.SaveIsSetConfig(getContext());

                    fragment = new MainFragment();
                    mCallback.someEvent(fragment);
                    break;

//                case R.id.wifi_scan:
//                    //ClearBeforeScan();
//                    //ScanWifi();
//
////                    mWifiAdmin.openWifi();
////                    mWifiAdmin.startScan();//Start scanning to send notifications
////                    //AppUtils.getInstance().showLoading(this);
////                    scanFlag = 1;
//                    break;

                case R.id.wifi_connect:
                    ConnectWifi();
                    break;

                case R.id.slide_time_decrease:
                    ChangeSlideTime(false);
                    break;

                case R.id.slide_time_increase:
                    ChangeSlideTime(true);
                    break;

                case R.id.scaleType_decrease:
                    ChangeScaleType(false);
                    break;

                case R.id.scaleType_increase:
                    ChangeScaleType(true);
                    break;

                case R.id.slide_videoTime_decrease:
                    ChangeVideoTime(false);
                    break;

                case R.id.slide_videoTime_increase:
                    ChangeVideoTime(true);
                    break;
            }

        }catch (Exception e){
            setting.toastException(mContext, e.getMessage());
        }
    }

//    @Override
//    protected void onPostResume() {
//        super.onPostResume();
//        receiverWifi = new WifiReceiver(wifiManager, wifiList);
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//        getActivity().registerReceiver(receiverWifi, intentFilter);
//        getWifi();
//    }

    private void ClearBeforeScan(Boolean value){
        if (value) {
            wifiList.setVisibility(View.VISIBLE);
        }else {
            wifiList.setVisibility(View.GONE);
        }

        TextWifiSelected.setVisibility(View.GONE);
        EditTextPassword.setVisibility(View.GONE);
        ButtonWifiConnect.setVisibility(View.GONE);

        //hide soft keyboard
        UseSoftKeyboard(false);
    }

//    private void OpenWifi() {
//        wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        if (!wifiManager.isWifiEnabled()) {
//            Toast.makeText(getContext(), "Turning WiFi ON...", Toast.LENGTH_LONG).show();
//            wifiManager.setWifiEnabled(true);
//        }
//    }

    private void ScanWifi() {
//        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                    getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
//        } else {
//            wifiManager.startScan();
//        }

        receiverWifi = new WifiReceiver(wifiManager, wifiList);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getActivity().registerReceiver(receiverWifi, intentFilter);
        getWifi();
    }

    private void getWifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Toast.makeText(getContext(), "version> = marshmallow", Toast.LENGTH_SHORT).show();
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "location turned off", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            } else {
                Toast.makeText(getContext(), "location turned on", Toast.LENGTH_SHORT).show();
                wifiManager.startScan();
            }
        } else {
            Toast.makeText(getContext(), "Wifi Scanning", Toast.LENGTH_SHORT).show();
            //ButtonWifiScan.setText("scanning..");
            //ButtonWifiScan.setEnabled(false);

            wifiManager.startScan();
        }
    }
//    @Override
//    public void onPause() {
//        super.onPause();
//        getActivity().unregisterReceiver(receiverWifi);
//    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "permission granted", Toast.LENGTH_SHORT).show();
                wifiManager.startScan();
            } else {
                Toast.makeText(getContext(), "permission not granted", Toast.LENGTH_SHORT).show();
                return;
            }
            break;
        }
    }

    private void ChangeSlideTime(Boolean add){
        if (!add && slideTime == length) {
            Toast.makeText(getContext(),"Min value is " + String.valueOf(slideTime), Toast.LENGTH_SHORT).show();
            return;
        }

        slideTime = (add) ? slideTime + length : slideTime - length;

        mTextViewSlideTime.setText(String.valueOf(slideTime));

        setting.SaveSlideTime(getContext(), slideTime);
    }

    private void ChangeScaleType(Boolean add){
        if (!add && scaleTypeId == 1) {
            BtnScaleType_decrease.setEnabled(false);
            return;
        }

        if (add && scaleTypeId == 8) {
            BtnScaleType_increase.setEnabled(false);
            return;
        }

        BtnScaleType_decrease.setEnabled(true);
        BtnScaleType_increase.setEnabled(true);

        scaleTypeId = (add) ? scaleTypeId + 1 : scaleTypeId - 1;

        mTextViewScaleType.setText(getScaleTypeText(scaleTypeId));

        setting.SaveImageScaleType(getContext(), scaleTypeId);
    }

    private void ChangeVideoTime(Boolean add){
        if (!add && videoTime == videoLength) {
            Toast.makeText(getContext(),"Min value is " + String.valueOf(videoTime), Toast.LENGTH_SHORT).show();
            return;
        }

        videoTime = (add) ? videoTime + videoLength : videoTime - videoLength;

        mTextViewVideoTime.setText(String.valueOf(videoTime));

        setting.SaveVideoTime(getContext(), videoTime);
    }

    private void ConnectWifi()
    {
        if (!networkType.equals("OPEN") && EditTextPassword.getText().toString().trim().equals("")) {
            Toast.makeText(getContext(), "Password not null", Toast.LENGTH_SHORT).show();
            return;
        }

        String networkSSID = TextWifiSelected.getText().toString();
        String networkPass = EditTextPassword.getText().toString();

        boolean result = false;

        switch (networkType)
        {
            case "WPA":
                result = ConnectToNetworkWPA(networkSSID, networkPass);
                break;
            case "WEP":
                result = ConnectToNetworkWEP(networkSSID, networkPass);
                break;
            case "OPEN":
                result = ConnectToNetworkOPEN(networkSSID, networkPass);
                break;
        }

        if (!result){
            Toast.makeText(getContext(), networkSSID + " Connection fail", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getContext(), networkSSID + " Connecting..", Toast.LENGTH_SHORT).show();
        //TextWifiStatusValue.setText(networkSSID + " " + getResources().getString(R.string.connected));

        ClearBeforeScan(true);


    }

    private String getScanResultSecurity(WifiManager wifiManager, String ssid) {
        List<ScanResult> networkList = wifiManager.getScanResults();
        for (ScanResult network : networkList) {
            if (network.SSID.equals(ssid)) {
                String Capabilities = network.capabilities;
                if (Capabilities.contains("WPA")) {
                    return "WPA";
                } else if (Capabilities.contains("WEP")) {
                    return "WEP";
                } else {
                    return "OPEN";
                }
            }
        }
        return null;
    }

    public boolean ConnectToNetworkOPEN( String networkSSID, String password )
    {
        try {
            WifiConfiguration conf = new WifiConfiguration();

            conf.SSID = String.format("\"%s\"", networkSSID);
            conf.preSharedKey = String.format("\"%s\"", password);

            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

            WifiManager wifiManager = (WifiManager)getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiManager.addNetwork(conf);

            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for( WifiConfiguration i : list ) {
                if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();

                    break;
                }
            }

            boolean changeHappen = wifiManager.saveConfiguration();
            int res = wifiManager.addNetwork(conf);
            if(res != -1 && changeHappen){
                Log.d(TAG, "### Change happen");
                return true;

            }else{
                Log.d(TAG, "*** Change NOT happen");
                return false;
            }

            //return true;

        } catch (Exception ex) {
            System.out.println(Arrays.toString(ex.getStackTrace()));
            return false;
        }
    }

    public boolean ConnectToNetworkWEP( String networkSSID, String password )
    {
        try {
            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain SSID in quotes
            conf.wepKeys[0] = "\"" + password + "\""; //Try it with quotes first

            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedGroupCiphers.set(WifiConfiguration.AuthAlgorithm.OPEN);
            conf.allowedGroupCiphers.set(WifiConfiguration.AuthAlgorithm.SHARED);


            WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int networkId = wifiManager.addNetwork(conf);

            if (networkId == -1){
                //Try it again with no quotes in case of hex password
                conf.wepKeys[0] = password;
                networkId = wifiManager.addNetwork(conf);
            }

            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for( WifiConfiguration i : list ) {
                if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();
                    break;
                }
            }

            boolean changeHappen = wifiManager.saveConfiguration();
            int res = wifiManager.addNetwork(conf);
            if(res != -1 && changeHappen){
                Log.d(TAG, "### Change happen");
                return true;

            }else{
                Log.d(TAG, "*** Change NOT happen");
                return false;
            }

            //WiFi Connection success, return true
            //return true;

        } catch (Exception ex) {
            System.out.println(Arrays.toString(ex.getStackTrace()));
            return false;
        }
    }

    public boolean ConnectToNetworkWPA( String networkSSID, String password )
    {
        try {
            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain SSID in quotes

            conf.preSharedKey = "\"" + password + "\"";

            conf.status = WifiConfiguration.Status.ENABLED;
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

            Log.d("connecting", conf.SSID + " " + conf.preSharedKey);

            WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiManager.addNetwork(conf);

            Log.d("after connecting", conf.SSID + " " + conf.preSharedKey);

            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for( WifiConfiguration i : list ) {
                if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();
                    Log.d("re connecting", i.SSID + " " + conf.preSharedKey);

                    break;
                }
            }

            boolean changeHappen = wifiManager.saveConfiguration();
            int res = wifiManager.addNetwork(conf);
            if(res != -1 && changeHappen){
                Log.d(TAG, "### Change happen");
                return true;

            }else{
                Log.d(TAG, "*** Change NOT happen");
                return false;
            }

            //WiFi Connection success, return true
            //return true;

        } catch (Exception ex) {
            System.out.println(Arrays.toString(ex.getStackTrace()));
            return false;
        }
    }

}
