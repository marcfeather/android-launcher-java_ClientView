package com.coretera.clientview.fragments;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coretera.clientview.Callback;
import com.coretera.clientview.R;
import com.coretera.clientview.utility.*;

import java.util.List;

public class ConfigFragment extends Fragment implements View.OnClickListener {

    Callback mCallback;
    Context mContext;

    private ListView wifiList;
    private WifiManager wifiManager;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    WifiReceiver receiverWifi;

    private TextView TextWifiSelected;
    private EditText EditTextPassword;
    private Button ButtonWifiConnect;

    private TextView mTextViewSlideTime;
    private Integer slideTime;
    public static final Integer length = 5;

    private InputMethodManager inputMethodManager;

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
            view.findViewById(R.id.wifi_scan).setOnClickListener(this);
            view.findViewById(R.id.wifi_connect).setOnClickListener(this);
            view.findViewById(R.id.slide_time_decrease).setOnClickListener(this);
            view.findViewById(R.id.slide_time_increase).setOnClickListener(this);

            mTextViewSlideTime = view.findViewById(R.id.slide_time_value);
            wifiList = view.findViewById(R.id.wifiList);
            TextWifiSelected = view.findViewById(R.id.wifi_selected);
            EditTextPassword = view.findViewById(R.id.wifi_Password);
            ButtonWifiConnect = view.findViewById(R.id.wifi_connect);

            wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Get the selected item text from ListView
                    String selectedItem = (String) parent.getItemAtPosition(position);

                    TextWifiSelected.setVisibility(View.VISIBLE);
                    TextWifiSelected.setText(selectedItem);

                    EditTextPassword.setVisibility(View.VISIBLE);
                    EditTextPassword.setText("");
                    EditTextPassword.requestFocus();

                    ButtonWifiConnect.setVisibility(View.VISIBLE);

                    //show soft keyboard
                    UseSoftKeyboard(true);
                }
            });

            wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (!wifiManager.isWifiEnabled()) {
                Toast.makeText(getContext(), "Turning WiFi ON...", Toast.LENGTH_LONG).show();
                wifiManager.setWifiEnabled(true);
            }

        }catch (Exception e){
            setting.toastException(mContext, e.getMessage());
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            SetValue();
        }catch (Exception e){
            setting.toastException(mContext, e.getMessage());
        }
    }

    private void SetValue() {
        slideTime = setting.GetSlideTime(getContext());
        if (slideTime == 0) { slideTime = length; }
        mTextViewSlideTime.setText(String.valueOf(slideTime));
    }

    private void UseSoftKeyboard(Boolean value)
    {
        if (value) {
            //show soft keyboard
            inputMethodManager = (InputMethodManager) getContext()
                    .getApplicationContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(EditTextPassword, 0);
        }else {
            //hide soft keyboard
            if (inputMethodManager != null) {
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

                    setting.SaveIsSetConfig(getContext());

                    fragment = new MainFragment();
                    mCallback.someEvent(fragment);
                    break;

                case R.id.wifi_scan:
                    ClearBeforeScan();
                    ScanWifi();
                    break;

                case R.id.wifi_connect:
                    ConnectWifi();
                    break;

                case R.id.slide_time_decrease:
                    ChangeSlideTime(false);
                    break;

                case R.id.slide_time_increase:
                    ChangeSlideTime(true);
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

    private void ClearBeforeScan(){
        wifiList.setVisibility(View.VISIBLE);
        //receiverWifi.ClearAllItems();

        TextWifiSelected.setVisibility(View.GONE);
        EditTextPassword.setVisibility(View.GONE);
        ButtonWifiConnect.setVisibility(View.GONE);

        //hide soft keyboard
        UseSoftKeyboard(false);
    }

    private void ScanWifi()
    {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            wifiManager.startScan();
        }

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
            Toast.makeText(getContext(), "scanning", Toast.LENGTH_SHORT).show();
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

    private void ConnectWifi()
    {
        if (EditTextPassword.getText().toString().trim().equals("")) { return; }

        String networkSSID = TextWifiSelected.getText().toString();
        String networkPass = EditTextPassword.getText().toString();

        //Toast.makeText(getContext(),networkSSID, Toast.LENGTH_SHORT).show();
        //Toast.makeText(getContext(),networkPass, Toast.LENGTH_SHORT).show();

        WifiConfiguration conf = new WifiConfiguration();
//        conf.SSID = "\"" + networkSSID + "\"";
//        //conf.wepKeys[0] = "\"" + networkPass + "\"";
//        //conf.wepTxKeyIndex = 0;
//        conf.preSharedKey = "\""+ networkPass +"\"";

        conf.SSID = String.format("\"%s\"", networkSSID);
        conf.preSharedKey = String.format("\"%s\"", networkPass);

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

        ClearBeforeScan();
    }
}
