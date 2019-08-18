package com.coretera.clientview.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.coretera.clientview.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class setting {

    private static final String domainname = "cvm.coretera.co.th";

    public static void toastException(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static boolean checkNetworkConnection(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean result = false;
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            result = (activeNetwork != null) && (activeNetwork.isConnectedOrConnecting());
        }

        Log.d("CheckConnect", "checkNetworkConnection: ".concat(String.valueOf(result)));

        return result;
    }

    // ICMP
    public static boolean checkAccessInternet() {
        Boolean result = false;
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            result = (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        Log.d("CheckConnect", "checkAccessInternet: ".concat(String.valueOf(result)));

        return result;
    }

    public static boolean checkServerActive() {
        final String command = "ping -c 1 ".concat(domainname);
        boolean result = false;
        try {
            result = Runtime.getRuntime().exec(command).waitFor() == 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("CheckConnect", "checkServerActive: ".concat(String.valueOf(result)));

        return result;
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

//    public static void SaveIsSetConfig(Context context){
//        String sharedPrefName = context.getString(R.string.SharedPreferencesName);
//
//        // Get SharedPreferences
//        SharedPreferences shared = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
//
//        //Save SharedPreferences
//        SharedPreferences.Editor editor = shared.edit();
//        editor.putBoolean("isSetConfig", true);
//        editor.apply();
//    }
//    public static Boolean GetIsSetConfig(Context context){
//        String sharedPrefName = context.getString(R.string.SharedPreferencesName);
//
//        // Get SharedPreferences
//        SharedPreferences shared = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
//        return shared.getBoolean("isSetConfig", false);
//    }

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

    public static void SaveExternalStorageDirectoryHtml(Context context, String externalStorageDirectoryHtml){
        String sharedPrefName = context.getString(R.string.SharedPreferencesName);

        // Get SharedPreferences
        SharedPreferences shared = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);

        //Save SharedPreferences
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("externalStorageDirectoryHtml", externalStorageDirectoryHtml);
        editor.apply();
    }
    public static String GetExternalStorageDirectoryHtml(Context context){
        String sharedPrefName = context.getString(R.string.SharedPreferencesName);

        // Get SharedPreferences
        SharedPreferences shared = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        return shared.getString("externalStorageDirectoryHtml", "");
    }
}
