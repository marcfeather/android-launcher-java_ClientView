package com.coretera.clientview;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.coretera.clientview.fragments.*;
import com.coretera.clientview.utility.*;

public class MainActivity extends AppCompatActivity implements Callback{

    private Context mContext;

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1
            , REQUEST_READ_EXTERNAL_STORAGE = 2
            , REQUEST_ACCESS_NETWORK_STATE = 3
            , REQUEST_INTERNET = 4
            , REQUEST_ACCESS_WIFI_STATE = 5
            , REQUEST_CHANGE_WIFI_STATE = 6;
//            , REQUEST_ACCESS_COARSE_LOCATION = 7
//            , REQUEST_ACCESS_FINE_LOCATION = 8;

    private static String[] PERMISSIONS_WRITE_EXTERNAL_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static String[] PERMISSIONS_READ_EXTERNAL_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE};
    private static String[] PERMISSIONS_ACCESS_NETWORK_STATE= {Manifest.permission.ACCESS_NETWORK_STATE};
    private static String[] PERMISSIONS_INTERNET = {Manifest.permission.INTERNET};
    private static String[] PERMISSIONS_ACCESS_WIFI_STATE = {Manifest.permission.ACCESS_WIFI_STATE};
    private static String[] PERMISSIONS_CHANGE_WIFI_STATE = {Manifest.permission.CHANGE_WIFI_STATE};
//    private static String[] PERMISSIONS_ACCESS_COARSE_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION};
//    private static String[] PERMISSIONS_ACCESS_FINE_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        try {
            verifyStoragePermissions(this);

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.contentContainer, new MainFragment())
                        .commit();
            }
        }catch (Exception e){
            setting.toastException(mContext, e.getMessage());
        }
    }

    @Override
    public void someEvent(Fragment fragment) {
        replaceFragment(fragment);
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Cannot write images to external storage", Toast.LENGTH_SHORT).show();
                }
            }
            case REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Cannot read images to external storage", Toast.LENGTH_SHORT).show();
                }
            }
            case REQUEST_ACCESS_NETWORK_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Cannot ACCESS NETWORK STATE", Toast.LENGTH_SHORT).show();
                }
            }
            case REQUEST_INTERNET: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Cannot ACCESS INTERNET", Toast.LENGTH_SHORT).show();
                }
            }
            case REQUEST_ACCESS_WIFI_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Cannot ACCESS_WIFI_STATE", Toast.LENGTH_SHORT).show();
                }
            }
            case REQUEST_CHANGE_WIFI_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Cannot CHANGE_WIFI_STATE", Toast.LENGTH_SHORT).show();
                }
            }
//            case REQUEST_ACCESS_COARSE_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length <= 0
//                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Cannot ACCESS_COARSE_LOCATION", Toast.LENGTH_SHORT).show();
//                }
//            }
//            case REQUEST_ACCESS_FINE_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length <= 0
//                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Cannot ACCESS_FINE_LOCATION", Toast.LENGTH_SHORT).show();
//                }
//            }
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     * <p/>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity the activity from which permissions are checked
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_WRITE_EXTERNAL_STORAGE, REQUEST_WRITE_EXTERNAL_STORAGE
            );
        }

        permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_READ_EXTERNAL_STORAGE, REQUEST_READ_EXTERNAL_STORAGE);
        }

        permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_ACCESS_NETWORK_STATE, REQUEST_ACCESS_NETWORK_STATE);
        }

        permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.INTERNET);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_INTERNET, REQUEST_INTERNET);
        }

        permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_WIFI_STATE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_ACCESS_WIFI_STATE, REQUEST_ACCESS_WIFI_STATE);
        }

        permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CHANGE_WIFI_STATE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_CHANGE_WIFI_STATE, REQUEST_CHANGE_WIFI_STATE);
        }

//        permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            // We don't have permission so prompt the user
//            ActivityCompat.requestPermissions(activity, PERMISSIONS_ACCESS_COARSE_LOCATION, REQUEST_ACCESS_COARSE_LOCATION);
//        }
//
//        permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            // We don't have permission so prompt the user
//            ActivityCompat.requestPermissions(activity, PERMISSIONS_ACCESS_FINE_LOCATION, REQUEST_ACCESS_FINE_LOCATION);
//        }
    }
}
