package com.coretera.clientview.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.coretera.clientview.Callback;
import com.coretera.clientview.R;
import com.coretera.clientview.StageActivity;
import com.coretera.clientview.utility.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static android.content.ContentValues.TAG;

//public class MainFragment extends Fragment implements View.OnClickListener {
public class MainFragment extends Fragment {

    Callback mCallback;
    Context mContext;

    ProgressBar mProgressBar;
    TextView mTextViewloading;
    TextView mTextCountdown;
    FloatingActionButton fab;

    CountDownTimer timerDelayFirst, timerToCheckNetwork, timerToUpdate, timerToPlay, timerDelay;

    private ProgressDialog mProgressDialog, mProgressDialog2, mProgressDialog3;
    private String pictureFolder, videoFolder, htmlFolder;

    Integer countConnect;
    Boolean isFirst = true;

    CheckDevicePerTask myCheckDevicePerTask = null;
    DownloadTask myDownloadTask = null;

    String IMEI_Number_Holder;
    TelephonyManager telephonyManager;

    String localPath;
    String contentName;
    String contentSubPath;
    String StorezipFileLocation;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (Callback) context;
        }catch (Exception e){
            setting.toastException(mContext, e.getMessage());
        }
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mContext = getContext();

        try {
            //view.findViewById(R.id.fab).setOnClickListener(this);

            mProgressBar = view.findViewById(R.id.progressBar);
            mTextViewloading = view.findViewById(R.id.loading_text);
            mTextCountdown = view.findViewById(R.id.countdown_text);
            fab = view.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();

                    cancelAllTimer();
                    mCallback.someEvent(new ConfigFragment());
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

        File directory;

//        //External Picture directory path to save file
//        pictureFolder = Environment.getExternalStorageDirectory() + File.separator + "ClientViewLauncher/Picture/";
//        setting.SaveExternalStorageDirectoryPicture(mContext, pictureFolder);
//        directory = new File(pictureFolder);
//        if (!directory.exists()) {
//            directory.mkdirs();
//        }

//        //External Video directory path to save file
//        videoFolder = Environment.getExternalStorageDirectory() + File.separator + "ClientViewLauncher/Video/";
//        setting.SaveExternalStorageDirectoryVideo(mContext, videoFolder);
//        directory = new File(videoFolder);
//        if (!directory.exists()) {
//            directory.mkdirs();
//        }

        //External HTML directory path to save file
        htmlFolder = Environment.getExternalStorageDirectory() + File.separator + "ClientViewLauncher/HTML/";
        setting.SaveExternalStorageDirectoryHtml(mContext, htmlFolder);
        directory = new File(htmlFolder);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private void startToCheck(){

        setting.SaveServerName(mContext, "http://www.dsm.coretera.co.th");

        setTimerDelayFirst(3);
        setTimerToCheckNetwork(3);
        setTimerDelay(3);
        setTimerToUpdateContent(6);
        setTimerToPlayContent(6);

        countConnect = 1;

        mTextViewloading.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mTextViewloading.setText("กำลังตรวจสอบการเชื่อมต่อเซิร์ฟเวอร์..".concat(" (ครั้งที่ ").concat(String.valueOf(countConnect)).concat(")"));
        mTextCountdown.setVisibility(View.GONE);

        Log.d(TAG, "DebugStep: startToCheck: isFirst = ".concat(String.valueOf(isFirst)));
        if (isFirst) {
            isFirst = false;
            //Delay 3 sec
            timerDelayFirst.start();
            Log.d(TAG, "DebugStep: startToCheck: timerDelayFirst.start()");
        }else {
            timerToCheckNetwork.start();
            Log.d(TAG, "DebugStep: startToCheck: timerToCheckNetwork.start()");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelAllTimer();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isFirst) {
            //cancelAllTimer();
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            if (myDownloadTask != null) {
                myDownloadTask.cancel(true);
            }
            //mCallback.someEvent(new ConfigFragment());
            //return;
        }

        startToCheck();
        Log.d(TAG, "onResume: startToCheck()");

        //PlayContent();
    }

    private void cancelAllTimer()
    {
        if (timerDelayFirst != null) {
            timerDelayFirst.cancel();
        }
        if (timerToCheckNetwork != null) {
            timerToCheckNetwork.cancel();
        }
        if (timerDelay != null) {
            timerDelay.cancel();
        }
        if (timerToUpdate != null) {
            timerToUpdate.cancel();
        }
        if (timerToPlay != null) {
            timerToPlay.cancel();
        }
    }

//    @Override
//    public void onClick(View v) {
//        try {
//            timer.cancel();
//            timer2.cancel();
//            timer3.cancel();
//
//            Fragment fragment;
//            switch (v.getId()) {
//                case R.id.fab:
//                    fragment = new ConfigFragment();
//                    mCallback.someEvent(fragment);
//                    break;
//
////                case R.id.button_update:
////                    UpdateContent();
////                    break;
////
////                case R.id.button_play:
////                    PlayContent();
////                    break;
//            }
//        }catch (Exception e){
//            setting.toastException(mContext, e.getMessage());
//        }
//    }

    //setTimerDelay
    private void setTimerDelayFirst(int sec) {
        timerDelayFirst = new CountDownTimer((sec * 1000), 1000) {
            int run = 0;
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "DebugStep: setTimerDelayFirst: onTick: ".concat(String.valueOf(run++)));
            }

            public void onFinish() {
                try {
                    timerToCheckNetwork.start();
                    Log.d(TAG, "DebugStep: setTimerDelayFirst: onFinish: timerToCheckNetwork.start()");

                }catch (Exception e){
                    setting.toastException(mContext, e.getMessage());
                }
            }
        };
    }

    //setTimerToCheckNetwork
    private void setTimerToCheckNetwork(int sec) {
        timerToCheckNetwork = new CountDownTimer((sec * 1000), 1000) {
            int run = 0;
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "DebugStep: setTimerToCheckNetwork: onTick: ".concat(String.valueOf(run++)));
            }

            public void onFinish() {
                try {
                    mProgressBar.setVisibility(View.GONE);

                    if (setting.checkNetworkConnection(mContext) && setting.checkAccessInternet()){// && setting.checkServerActive()){
//                        mTextViewloading.setText("อัพเดตข้อมูลอัตโนมัติภายใน");
//                        mTextCountdown.setVisibility(View.VISIBLE);
//                        timerToUpdate.start();
//                        Log.d(TAG, "DebugStep: setTimerToCheckNetwork: onFinish: timerToUpdate.start()");

                        mTextViewloading.setVisibility(View.GONE);
                        mTextCountdown.setVisibility(View.GONE);
                        CheckDevicePerUse();
                        Log.d(TAG, "DebugStep: setTimerToUpdateContent: onFinish: CheckDevicePerUse");

                    } else {
                        mTextViewloading.setText("การเชื่อมต่อเซิร์ฟเวอร์ครั้งที่ ".concat(String.valueOf(countConnect)).concat(" ไม่สำเร็จ กรุณาตั้งค่าระบบเพื่ออัพเดตข้อมูล"));
                        timerDelay.start();
                        Log.d(TAG, "DebugStep: setTimerToCheckNetwork: onFinish: timerDelay.start()");
                    }

                } catch (Exception e) {
                    setting.toastException(mContext, e.getMessage());
                }
            }
        };
    }

    //setTimerDelay
    private void setTimerDelay(int sec) {
        timerDelay = new CountDownTimer((sec * 1000), 1000) {
            int run = 0;
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "DebugStep: setTimerDelay: onTick: ".concat(String.valueOf(run++)));
            }

            public void onFinish() {
                try {
                    if (countConnect < 3) {
                        countConnect++;
                        //Log.d("TAG", "onFinish: setTimerDelay".concat(String.valueOf(countConnect)));
                        mProgressBar.setVisibility(View.VISIBLE);
                        mTextViewloading.setText("กำลังตรวจสอบการเชื่อมต่อเซิร์ฟเวอร์..".concat(" (ครั้งที่ ").concat(String.valueOf(countConnect)).concat(")"));
                        timerToCheckNetwork.start();
                        Log.d(TAG, "DebugStep: setTimerDelay: onFinish: timerToCheckNetwork.start()");

                    } else {
                        contentName = setting.GetExternalStorageContentZipName(mContext);
                        contentSubPath = contentName.replace(".zip","");

                        File directory = new File(htmlFolder);
                        if (directory.exists()) {
                            File[] files = directory.listFiles();
                            if (files != null && files.length > 0) {
                                //check file index to play html
                                File file2 = new File(htmlFolder + contentSubPath + "/index.html");
                                File file3 = new File(htmlFolder + contentSubPath + "/index.php");
                                if(file2.exists() || file3.exists()) {
                                    mTextViewloading.setVisibility(View.VISIBLE);
                                    mTextViewloading.setText("แสดงข้อมูลอัตโนมัติใน");
                                    mTextCountdown.setVisibility(View.VISIBLE);
                                    timerToPlay.start();
                                }
                            }
                            else {
                                mTextViewloading.setVisibility(View.VISIBLE);
                                mTextViewloading.setText("ไม่พบข้อมูลในเครื่อง กรุณาตรวจสอบการตั้งค่า");
                            }
                        }
                    }

                }catch (Exception e){
                    setting.toastException(mContext, e.getMessage());
                }
            }
        };
    }

    //setTimerToUpdateContent
    private void setTimerToUpdateContent(int sec) {
        timerToUpdate = new CountDownTimer((sec * 1000), 1000) {
            int run = 0;
            public void onTick(long millisUntilFinished) {
                mTextCountdown.setText(""+String.format("%d",
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                Log.d(TAG, "DebugStep: setTimerToUpdateContent: onTick: ".concat(String.valueOf(run++)));
            }

            public void onFinish() {
                try {
                    mTextViewloading.setVisibility(View.GONE);
                    mTextCountdown.setVisibility(View.GONE);
                    UpdateContent();
                    Log.d(TAG, "DebugStep: setTimerToUpdateContent: onFinish: UpdateContent");

                }catch (Exception e){
                    setting.toastException(mContext, e.getMessage());
                }
            }
        };
    }

    //setTimerToPlayContent
    private void setTimerToPlayContent(int sec) {
        timerToPlay = new CountDownTimer((sec * 1000), 1000) {
            int run = 0;
            public void onTick(long millisUntilFinished) {
                mTextCountdown.setText(""+String.format("%d",
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                Log.d(TAG, "DebugStep: setTimerToPlayContent: onTick: ".concat(String.valueOf(run++)));
            }

            public void onFinish() {
                try {
                    mTextViewloading.setVisibility(View.GONE);
                    mTextCountdown.setVisibility(View.GONE);
                    PlayContent();
                    Log.d(TAG, "DebugStep: setTimerToPlayContent: onFinish: PlayContent");

                }catch (Exception e){
                    setting.toastException(mContext, e.getMessage());
                }
            }
        };
    }

    private void CheckDevicePerUse() {
        InitProgressDialog2();

        //Get imei number
        if (ContextCompat.checkSelfPermission( mContext,android.Manifest.permission.READ_PHONE_STATE ) == PackageManager.PERMISSION_GRANTED )
        {
            telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                IMEI_Number_Holder = telephonyManager.getDeviceId();
                Log.d("DebugStep", "IMEI_Number_Holder: " + IMEI_Number_Holder);
            }
        }

        // Execute the async task
        myCheckDevicePerTask = new CheckDevicePerTask();
        myCheckDevicePerTask.execute();
    }

    private void UpdateContent() {
        InitProgressDialog();

        File directory;

//        //External Picture directory path to delete file
//        directory = new File(pictureFolder);
//        if (directory.exists()) {
//            File[] files = directory.listFiles();
//            for (File file : files) {
//                if(file.exists()){
//                    file.delete();
//                }
//            }
//        }
        
//        //External Video directory path to delete file
//        directory = new File(videoFolder);
//        if (directory.exists()) {
//            File[] files = directory.listFiles();
//            for (File file : files) {
//                if(file.exists()){
//                    file.delete();
//                }
//            }
//        }

        //External Html directory path to delete file
        directory = new File(htmlFolder);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                if(file.exists()){
                    file.delete();
                }
            }
        }

        // Execute the async task
        myDownloadTask = new DownloadTask();
        myDownloadTask.execute();
    }

    private void InitProgressDialog() {
        // Initialize the progress dialog
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(false);
        // Progress dialog horizontal style
        //mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // Progress dialog title
        mProgressDialog.setTitle("อัพเดตข้อมูล");
        // Progress dialog message
        mProgressDialog.setMessage("กรุณารอสักครู่, กำลังดึงข้อมูลจากเซิร์ฟเวอร์...");
        mProgressDialog.setCancelable(false);
    }

    private void InitProgressDialog2() {
        // Initialize the progress dialog 2
        mProgressDialog2 = new ProgressDialog(getActivity());
        mProgressDialog2.setIndeterminate(false);
        // Progress dialog title
        mProgressDialog2.setTitle("ตรวจสอบสิทธ์");
        // Progress dialog message
        mProgressDialog2.setMessage("กำลังตรวจสอบสิทธ์การใช้งานของเครื่อง...");
        mProgressDialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog2.setCancelable(false);
    }

    private void InitProgressDialog3() {
        // Initialize the progress dialog 3
        mProgressDialog3 = new ProgressDialog(getActivity());
        mProgressDialog3.setIndeterminate(false);
        mProgressDialog3.setMessage("กรุณารอสักครู่...");
        mProgressDialog3.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog3.setCancelable(false);
    }

    // Custom method to convert string to url
    protected URL stringToURL(String urlString){
        try{
            URL url = new URL(urlString);
            return url;
        }catch (Exception e){
            setting.toastException(mContext, e.getMessage());
        }
        return null;
    }

    private class CheckDevicePerTask extends AsyncTask<Void,Integer,Boolean>{
        // Before the tasks execution
        protected void onPreExecute(){
            // Display the progress dialog on async task start
            mProgressDialog2.show();
        }

        // Do the task in background/non UI thread
        protected Boolean doInBackground(Void... value){
            try{
                String url = setting.GetServerName(mContext);

                ArrayList<HashMap<String,String>> server_result = MysqlConnector.getContentPathByImei(url, IMEI_Number_Holder);
                Log.d("DebugStep", "server_result: " + server_result.size());
                if (server_result.size() == 0) {
                    return false;
                }

                String[] resultList = new String[server_result.size()];
                //Log.d("selectAllContent", server_result.size()+"");
                String[] columnArray = {"local_path", "contentName"};
                for(int i = 0;i<server_result.size();i++){
                    resultList[i] = server_result.get(i).get(columnArray[i]);
                }

                Log.d("DebugStep", "LocalPath: " + resultList[0]);
                Log.d("DebugStep", "ContentZipName: " + resultList[1]);

                setting.SaveExternalStorageLocalPath(mContext, resultList[0]);
                setting.SaveExternalStorageContentZipName(mContext, resultList[1]);

                return true;

            }catch (Exception e){
                Log.d("CheckDevicePerTask", "doInBackground: " + e.getMessage());
                return false;
            }
        }

        // When all async task done
        protected void onPostExecute(Boolean result){

            // Hide the progress dialog
            mProgressDialog2.dismiss();

            if (!result) {
                //Toast.makeText(getActivity(), "การตรวจสอบสิทธ์ผิดพลาด!", Toast.LENGTH_LONG).show();
                mTextViewloading.setVisibility(View.VISIBLE);
                mTextViewloading.setText("ไม่พบสิทธ์การใช้งานของเครื่อง");
                return;
            }

            mTextViewloading.setText("อัพเดตข้อมูลอัตโนมัติภายใน");
            mTextCountdown.setVisibility(View.VISIBLE);
            timerToUpdate.start();
            Log.d(TAG, "DebugStep: CheckDevicePerTask: onFinish: timerToUpdate.start()");
        }
    }

    /*
        First parameter URL for doInBackground
        Second parameter Integer for onProgressUpdate
        Third parameter List<Bitmap> for onPostExecute
     */
    private class DownloadTask extends AsyncTask<Void,Integer,Boolean>{
        // Before the tasks execution
        protected void onPreExecute(){
            // Display the progress dialog on async task start
            mProgressDialog.show();
            //mProgressDialog.setProgress(0);
        }

        // Do the task in background/non UI thread
        protected Boolean doInBackground(Void... value){
            try{
                HttpURLConnection connection = null;
                InputStream inputStream;
                //Bitmap bitmap;
                Boolean result = false;

                String url = setting.GetServerName(mContext);

//                ArrayList<HashMap<String,String>> server_result = MysqlConnector.selectAllContent(url);
//                String[] resultList = new String[server_result.size()];
//                //Log.d("selectAllContent", server_result.size()+"");
//                for(int i = 0;i<server_result.size();i++){
//                    resultList[i] = server_result.get(i).get("local_path");
//                }
//
//                int count = resultList.length;
//
//                count = count + 1; //have video
//
//                // Loop through the urls
//                for(int i=0;i<count;i++){
//                    String urlFullPath;
//
//                    //have video
//                    if (i == (count-1)) {
//                        urlFullPath = url + "/video/video.mp4";
//                    }else {
//                        urlFullPath = url + resultList[i];
//                    }
//
//                    //String urlFullPath = url + resultList[i];
//                    URL currentURL = stringToURL(urlFullPath);
//
//                    // So download the image from this url
//                    try{
//                        // Initialize a new http url connection
//                        connection = (HttpURLConnection) currentURL.openConnection();
//
//                        // Connect the http url connection
//                        connection.connect();
//
//                        // Get the input stream from http url connection
//                        inputStream = connection.getInputStream();
//
//                        // Initialize a new BufferedInputStream from InputStream
//                        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
//
//                        //have video
//                        if (i == (count-1)) {
//                            result = saveVideoToInternalStorage(bufferedInputStream, "video.mp4", videoFolder);
//                            if (!result) {
//                                //break;
//                            }
//
//                        }else {
//                            // Convert BufferedInputStream to Bitmap object
//                            bitmap = BitmapFactory.decodeStream(bufferedInputStream);
//                            bufferedInputStream.close();
//
//                            result = saveImageToInternalStorage(bitmap, i + 1, pictureFolder);
//                            if (!result) {
//                                //break;
//                            }
//                        }
//
//                        // Publish the async task progress
//                        // Added 1, because index start from 0
//                        publishProgress((int) (((i+1) / (float) count) * 100));
//                        if(isCancelled()){
//                            //break;
//                        }
//
//                    }catch(IOException e){
//                        e.printStackTrace();
//                    }finally{
//                        // Disconnect the http url connection
//                        connection.disconnect();
//                    }
//                }


//                ArrayList<HashMap<String,String>> server_result = MysqlConnector.getContentPathByImei(url, IMEI_Number_Holder);
//                Log.d("DebugStep", "server_result: " + server_result.size());
//                if (server_result.size() == 0) {
//                    return false;
//                }
//
//                String[] resultList = new String[server_result.size()];
//                //Log.d("selectAllContent", server_result.size()+"");
//                String[] columnArray = {"local_path", "contentName"};
//                for(int i = 0;i<server_result.size();i++){
//                    //resultList[i] = server_result.get(i).get("local_path");
//                    resultList[i] = server_result.get(i).get(columnArray[i]);
//                }

                //int count = resultList.length;

                String localPath = setting.GetExternalStorageLocalPath(mContext);
                String contentname = setting.GetExternalStorageContentZipName(mContext);

                String urlFullPath;
                //urlFullPath = url + "/contents/html/localweb.zip";

                // Loop through the urls
                //for(int i=0;i<count;i++){
                    //urlFullPath = url + resultList[i];
                    urlFullPath = url + localPath;
                    Log.d("DebugStep", "urlFullPath: " + urlFullPath);

                    URL currentURL = stringToURL(urlFullPath);
                    // So download the image from this url
                    try{
                        // Initialize a new http url connection
                        connection = (HttpURLConnection) currentURL.openConnection();

                        // Connect the http url connection
                        connection.connect();

                        // Get the input stream from http url connection
                        inputStream = connection.getInputStream();

                        // Initialize a new BufferedInputStream from InputStream
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                        //result = saveContentToInternalStorage(bufferedInputStream, "localweb.zip", htmlFolder);
                        result = saveContentToInternalStorage(bufferedInputStream, contentname, htmlFolder);

                        if (!result) {
                            //break;
                            return false;
                        }

                    }catch(IOException e){
                        e.printStackTrace();
                    }finally{
                        // Disconnect the http url connection
                        connection.disconnect();
                    }
                //}

                return result;

            }catch (Exception e){
                Log.d("DownloadTask", "doInBackground: " + e.getMessage());
                return false;
            }
        }

        // On progress update
        protected void onProgressUpdate(Integer... progress){
            // Update the progress bar
            mProgressDialog.setProgress(progress[0]);
        }

        // On AsyncTask cancelled
        protected void onCancelled(){

        }

        // When all async task done
        protected void onPostExecute(Boolean result){

            //if (myDownloadTask.isCancelled()){ return; }

            // Hide the progress dialog
            mProgressDialog.dismiss();

            setting.SaveCurrentPage(mContext, 0);

            if (!result) {
                Toast.makeText(getActivity(), "การอัพเดตผิดพลาด!", Toast.LENGTH_LONG).show();

//                //External Picture directory path to delete last file
//                File directory = new File(pictureFolder);
//                if (directory.exists()) {
//                    File[] files = directory.listFiles();
//                    File file = files[files.length-1];
//                    if(file.exists()){
//                        file.delete();
//                    }
//                }
            }

            try {
                unzip();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

//    // Custom method to save a bitmap into internal storage
//    protected Boolean saveImageToInternalStorage(Bitmap bitmap, int index, String folder){
////        // Initialize ContextWrapper
////        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
////
////        // Initializing a new file
////        // The bellow line return a directory in internal storage
////        File file = wrapper.getDir("Images",MODE_PRIVATE);
////
////        // Create a file to save the image
////        file = new File(file, "UniqueFileName"+ index+".jpg");
//
//        //Extract file name from URL
//        String fileName = String.valueOf(index) + ".jpg";
//
//        try{
//            // Initialize a new OutputStream
//            OutputStream stream = null;
//
//            //// If the output file exists, it can be replaced or appended to it
//            //stream = new FileOutputStream(file);
//            stream = new FileOutputStream(folder + fileName);
//
//            // Compress the bitmap
//            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
//
//            // Flushes the stream
//            stream.flush();
//
//            // Closes the stream
//            stream.close();
//
//        }catch (IOException e) // Catch the exception
//        {
//            e.printStackTrace();
//            return false;
//        }
//
//        return true;
//    }

    protected Boolean saveContentToInternalStorage(BufferedInputStream bufferedInputStream, String _fileName, String folder){
        //Extract file name from URL
        String fileName = _fileName;

        try{
            OutputStream stream = new FileOutputStream(folder + fileName);

            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(dataBuffer, 0, 1024)) != -1) {
                stream.write(dataBuffer, 0, bytesRead);
            }

            // Flushes the stream
            stream.flush();

            // Closes the stream
            stream.close();
            bufferedInputStream.close();

        }catch (IOException e) // Catch the exception
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void PlayContent() {
        //mCallback.someEvent(new PlayFragment());

        Intent intent = new Intent(getActivity(), StageActivity.class);
        intent.putExtra("contentSubPath", contentSubPath);
        startActivity(intent);
        getActivity().finish();
    }

    public void unzip() throws IOException
    {
        InitProgressDialog3();

        contentName = setting.GetExternalStorageContentZipName(mContext);

        //StorezipFileLocation = htmlFolder + "localweb.zip";
        StorezipFileLocation = htmlFolder + contentName;
        //DirectoryName = htmlFolder;
        new UnZipTask().execute(StorezipFileLocation, htmlFolder);
    }

    private class UnZipTask extends AsyncTask<String, Void, Boolean>
    {
        // Before the tasks execution
        protected void onPreExecute(){
            mProgressDialog3.show();
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected Boolean doInBackground(String... params)
        {
            String filePath = params[0];
            String destinationPath = params[1];

            File archive = new File(filePath);
            try
            {
                ZipFile zipfile = new ZipFile(archive);
                for (Enumeration e = zipfile.entries(); e.hasMoreElements();)
                {
                    ZipEntry entry = (ZipEntry) e.nextElement();
                    unzipEntry(zipfile, entry, destinationPath);
                }


                UnzipUtil d = new UnzipUtil(StorezipFileLocation, htmlFolder);
                d.unzip();

            }
            catch (Exception e)
            {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            mProgressDialog3.dismiss();

            File directory = new File(htmlFolder);
            if (directory.exists()) {
                File[] files = directory.listFiles();
                if (files != null && files.length > 0) {
                    //delete zip file
                    File file = new File(StorezipFileLocation);
                    if(file.exists()) {
                        file.delete();
                    }
                    //check file index to play html
                    contentName = setting.GetExternalStorageContentZipName(mContext);
                    contentSubPath = contentName.replace(".zip","");

                    File file2 = new File(htmlFolder + contentSubPath + "/index.html");
                    File file3 = new File(htmlFolder + contentSubPath + "/index.php");
                    if(file2.exists() || file3.exists()) {
                        mTextViewloading.setVisibility(View.VISIBLE);
                        mTextViewloading.setText("แสดงข้อมูลอัตโนมัติใน");
                        mTextCountdown.setVisibility(View.VISIBLE);
                        timerToPlay.start();
                    }
                }
                else {
                    mTextViewloading.setVisibility(View.VISIBLE);
                    mTextViewloading.setText("ไม่พบข้อมูลในเครื่อง กรุณาตรวจสอบการตั้งค่า");
                }
            }

//            //External Picture directory path to delete file
//            File directory = new File(pictureFolder);
//            if (directory.exists()) {
//                File[] files = directory.listFiles();
//                if (files.length > 0) {
//                    mTextViewloading.setVisibility(View.VISIBLE);
//                    mTextViewloading.setText("แสดงข้อมูลอัตโนมัติใน");
//                    mTextCountdown.setVisibility(View.VISIBLE);
//                    timerToPlay.start();
//
//                }else {
//                    mTextViewloading.setVisibility(View.VISIBLE);
//                    mTextViewloading.setText("ไม่พบข้อมูลในเครื่อง กรุณาตรวจสอบการตั้งค่า");
//                }
//            }

        }


        private void unzipEntry(ZipFile zipfile, ZipEntry entry,String outputDir) throws IOException
        {
            if (entry.isDirectory())
            {
                createDir(new File(outputDir, entry.getName()));
                return;
            }

            File outputFile = new File(outputDir, entry.getName());
            if (!outputFile.getParentFile().exists())
            {
                createDir(outputFile.getParentFile());
            }

            // Log.v("", "Extracting: " + entry);
            BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

            try
            {

            }
            finally
            {
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            }
        }

        private void createDir(File dir)
        {
            if (dir.exists())
            {
                return;
            }
            if (!dir.mkdirs())
            {
                throw new RuntimeException("Can not create dir " + dir);
            }
        }
    }
}
