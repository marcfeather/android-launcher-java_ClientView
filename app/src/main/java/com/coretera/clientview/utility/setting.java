package com.coretera.clientview.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.coretera.clientview.R;

import java.io.IOException;

public class setting {

    public static void toastException(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = false;
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            isConnected = (activeNetwork != null) && (activeNetwork.isConnectedOrConnecting());
        }

        return isConnected;
    }

    // ICMP
    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            //Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            Process ipProcess = runtime.exec("ping  119.59.120.11");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

//    public static void SaveContentPath(Context context, String contentPath){
//        String sharedPrefName = context.getString(R.string.SharedPreferencesName);
//
//        // Get SharedPreferences
//        SharedPreferences shared = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
//
//        //Save SharedPreferences
//        SharedPreferences.Editor editor = shared.edit();
//        editor.putString("contentPath", contentPath);
//        editor.apply();
//    }
//    public static String GetContentPath(Context context){
//        String sharedPrefName = context.getString(R.string.SharedPreferencesName);
//
//        // Get SharedPreferences
//        SharedPreferences shared = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
//        return shared.getString("contentPath", "");
//    }

    public static void SaveIsSetConfig(Context context){
        String sharedPrefName = context.getString(R.string.SharedPreferencesName);

        // Get SharedPreferences
        SharedPreferences shared = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);

        //Save SharedPreferences
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean("isSetConfig", true);
        editor.apply();
    }
    public static Boolean GetIsSetConfig(Context context){
        String sharedPrefName = context.getString(R.string.SharedPreferencesName);

        // Get SharedPreferences
        SharedPreferences shared = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        return shared.getBoolean("isSetConfig", false);
    }

    public static void SaveSlideTime(Context context, Integer slideTime){
        String sharedPrefName = context.getString(R.string.SharedPreferencesName);

        // Get SharedPreferences
        SharedPreferences shared = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);

        //Save SharedPreferences
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt("slideTime", slideTime);
        editor.apply();
    }
    public static Integer GetSlideTime(Context context){
        String sharedPrefName = context.getString(R.string.SharedPreferencesName);

        // Get SharedPreferences
        SharedPreferences shared = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        return shared.getInt("slideTime", 0);
    }

    public static void SaveVideoTime(Context context, Integer videoTime){
        String sharedPrefName = context.getString(R.string.SharedPreferencesName);

        // Get SharedPreferences
        SharedPreferences shared = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);

        //Save SharedPreferences
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt("videoTime", videoTime);
        editor.apply();
    }
    public static Integer GetVideoTime(Context context){
        String sharedPrefName = context.getString(R.string.SharedPreferencesName);

        // Get SharedPreferences
        SharedPreferences shared = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        return shared.getInt("videoTime", 0);
    }

    public static void SaveImageScaleType(Context context, Integer imageScaleType){
        String sharedPrefName = context.getString(R.string.SharedPreferencesName);

        // Get SharedPreferences
        SharedPreferences shared = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);

        //Save SharedPreferences
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt("imageScaleType", imageScaleType);
        editor.apply();
    }
    public static Integer GetImageScaleType(Context context){
        String sharedPrefName = context.getString(R.string.SharedPreferencesName);

        // Get SharedPreferences
        SharedPreferences shared = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        return shared.getInt("imageScaleType", 0);
    }

    public static void SaveCurrentPage(Context context, int currentPage){
        String sharedPrefName = context.getString(R.string.SharedPreferencesName);

        // Get SharedPreferences
        SharedPreferences shared = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);

        //Save SharedPreferences
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt("currentPage", currentPage);
        editor.apply();
    }
    public static int GetCurrentPage(Context context){
        String sharedPrefName = context.getString(R.string.SharedPreferencesName);

        // Get SharedPreferences
        SharedPreferences shared = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        return shared.getInt("currentPage", 0);
    }

    public static void SaveExternalStorageDirectoryPicture(Context context, String externalStorageDirectoryPicture){
        String sharedPrefName = context.getString(R.string.SharedPreferencesName);

        // Get SharedPreferences
        SharedPreferences shared = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);

        //Save SharedPreferences
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("externalStorageDirectoryPicture", externalStorageDirectoryPicture);
        editor.apply();
    }
    public static String GetExternalStorageDirectoryPicture(Context context){
        String sharedPrefName = context.getString(R.string.SharedPreferencesName);

        // Get SharedPreferences
        SharedPreferences shared = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        return shared.getString("externalStorageDirectoryPicture", "");
    }

    public static void SaveExternalStorageDirectoryVideo(Context context, String externalStorageDirectoryVideo){
        String sharedPrefName = context.getString(R.string.SharedPreferencesName);

        // Get SharedPreferences
        SharedPreferences shared = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);

        //Save SharedPreferences
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("externalStorageDirectoryVideo", externalStorageDirectoryVideo);
        editor.apply();
    }
    public static String GetExternalStorageDirectoryVideo(Context context){
        String sharedPrefName = context.getString(R.string.SharedPreferencesName);

        // Get SharedPreferences
        SharedPreferences shared = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        return shared.getString("externalStorageDirectoryVideo", "");
    }
}
