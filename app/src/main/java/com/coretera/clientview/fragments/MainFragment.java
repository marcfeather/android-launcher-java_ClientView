package com.coretera.clientview.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import com.coretera.clientview.utility.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

//public class MainFragment extends Fragment implements View.OnClickListener {
public class MainFragment extends Fragment {

    Callback mCallback;
    Context mContext;

    ProgressBar mProgressBar;
    TextView mTextViewloading;
    TextView mTextCountdown;
    FloatingActionButton fab;

    CountDownTimer timerToCheckNetwork, timerToUpdate, timerToPlay, timerDelay;

    private ProgressDialog mProgressDialog;
    private String pictureFolder, videoFolder;

    Integer countConnect = 0;

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

                    timerToCheckNetwork.cancel();
                    timerDelay.cancel();
                    timerToUpdate.cancel();
                    timerToPlay.cancel();

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

        //External Picture directory path to save file
        pictureFolder = Environment.getExternalStorageDirectory() + File.separator + "ClientViewLauncher/Picture/";
        setting.SaveExternalStorageDirectoryPicture(mContext, pictureFolder);
        File directory = new File(pictureFolder);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        //External Video directory path to save file
        videoFolder = Environment.getExternalStorageDirectory() + File.separator + "ClientViewLauncher/Video/";
        setting.SaveExternalStorageDirectoryVideo(mContext, videoFolder);
        directory = new File(videoFolder);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        countConnect++;

        mTextViewloading.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mTextViewloading.setText("กำลังตรวจสอบการเชื่อมต่อเซิร์ฟเวอร์..".concat(" (ครั้งที่ ").concat(String.valueOf(countConnect)).concat(")"));

        setTimerToCheckNetwork(3);
        setTimerDelay(3);
        setTimerToUpdateContent(6);
        setTimerToPlayContent(6);

        timerToCheckNetwork.start();
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

    //setTimerToCheckNetwork
    private void setTimerToCheckNetwork(int sec) {
        timerToCheckNetwork = new CountDownTimer((sec * 1000), 1000) {
            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
                try {
                    mProgressBar.setVisibility(View.GONE);

                    if (setting.isConnectedToNetwork(mContext) && setting.isOnline()){
                        mTextViewloading.setText("อัพเดตข้อมูลอัตโนมัติภายใน");
                        mTextCountdown.setVisibility(View.VISIBLE);
                        timerToUpdate.start();

                    } else {
                        mTextViewloading.setText("การเชื่อมต่อเซิร์ฟเวอร์ครั้งที่ ".concat(String.valueOf(countConnect)).concat(" ไม่สำเร็จ กรุณาตั้งค่าระบบเพื่ออัพเดตข้อมูล"));
                        timerDelay.start();
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
            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
                try {
                    if (countConnect < 3) {
                        countConnect++;
                        mProgressBar.setVisibility(View.VISIBLE);
                        mTextViewloading.setText("กำลังตรวจสอบการเชื่อมต่อเซิร์ฟเวอร์..".concat(" (ครั้งที่ ").concat(String.valueOf(countConnect)).concat(")"));
                        timerToCheckNetwork.start();

                    } else {
                        File directory = new File(pictureFolder);
                        File[] files = directory.listFiles();
                        if (files != null && files.length > 0) {
                            mTextViewloading.setText("แสดงข้อมูลอัตโนมัติใน");
                            mTextCountdown.setVisibility(View.VISIBLE);
                            timerToPlay.start();
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
            public void onTick(long millisUntilFinished) {
                mTextCountdown.setText(""+String.format("%d",
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                try {
                    mTextViewloading.setVisibility(View.GONE);
                    mTextCountdown.setVisibility(View.GONE);
                    UpdateContent();

                }catch (Exception e){
                    setting.toastException(mContext, e.getMessage());
                }
            }
        };
    }

    //setTimerToPlayContent
    private void setTimerToPlayContent(int sec) {
        timerToPlay = new CountDownTimer((sec * 1000), 1000) {
            public void onTick(long millisUntilFinished) {
                mTextCountdown.setText(""+String.format("%d",
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                try {
                    mTextViewloading.setVisibility(View.GONE);
                    mTextCountdown.setVisibility(View.GONE);
                    PlayContent();

                }catch (Exception e){
                    setting.toastException(mContext, e.getMessage());
                }
            }
        };
    }

    private void UpdateContent() {
        InitProgressDialog();

        //External Picture directory path to delete file
        File directory = new File(pictureFolder);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                if(file.exists()){
                    file.delete();
                }
            }
        }
        
        //External Video directory path to delete file
        directory = new File(videoFolder);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                if(file.exists()){
                    file.delete();
                }
            }
        }

        // Execute the async task
        new DownloadTask().execute();
    }

    private void InitProgressDialog() {
        // Initialize the progress dialog
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(false);
        // Progress dialog horizontal style
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // Progress dialog title
        mProgressDialog.setTitle("อัพเดตข้อมูล");
        // Progress dialog message
        mProgressDialog.setMessage("กรุณารอสักครู่, กำลังดึงข้อมูลจากเซิร์ฟเวอร์...");
        mProgressDialog.setCancelable(false);
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
            mProgressDialog.setProgress(0);
        }

        // Do the task in background/non UI thread
        protected Boolean doInBackground(Void... value){
            try{
                HttpURLConnection connection = null;
                InputStream inputStream;
                Bitmap bitmap;
                Boolean result = false;

                String url = "http://www.cvm.coretera.co.th";

                ArrayList<HashMap<String,String>> server_result = MysqlConnector.selectAllContent(url);
                String[] resultList = new String[server_result.size()];
                //Log.d("selectAllContent", server_result.size()+"");
                for(int i = 0;i<server_result.size();i++){
                    resultList[i] = server_result.get(i).get("local_path");
                }

                int count = resultList.length;

                count = count + 1; //have video

                // Loop through the urls
                for(int i=0;i<count;i++){
                    String urlFullPath;

                    //have video
                    if (i == (count-1)) {
                        urlFullPath = url + "/video/video.mp4";
                    }else {
                        urlFullPath = url + resultList[i];
                    }

                    //String urlFullPath = url + resultList[i];
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

                        //have video
                        if (i == (count-1)) {
                            result = saveVideoToInternalStorage(bufferedInputStream, "video.mp4", videoFolder);
                            if (!result) {
                                //break;
                            }

                        }else {
                            // Convert BufferedInputStream to Bitmap object
                            bitmap = BitmapFactory.decodeStream(bufferedInputStream);
                            bufferedInputStream.close();

                            result = saveImageToInternalStorage(bitmap, i + 1, pictureFolder);
                            if (!result) {
                                //break;
                            }
                        }

                        // Publish the async task progress
                        // Added 1, because index start from 0
                        publishProgress((int) (((i+1) / (float) count) * 100));
                        if(isCancelled()){
                            //break;
                        }

                    }catch(IOException e){
                        e.printStackTrace();
                    }finally{
                        // Disconnect the http url connection
                        connection.disconnect();
                    }
                }

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
            // Hide the progress dialog
            mProgressDialog.dismiss();

            setting.SaveCurrentPage(mContext, 0);

            if (!result) {
                //Toast.makeText(getActivity(), "พบความผิดพลาดในการบันทึกรูปภาพ", Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(), "การอัพเดตผิดพลาด!", Toast.LENGTH_LONG).show();
                return;
            }

            //new InitImageViewTask().execute();

            //mButtonPlay.setEnabled(true);
            //PlayContent();
            mTextViewloading.setVisibility(View.VISIBLE);
            mTextViewloading.setText("แสดงข้อมูลอัตโนมัติใน");
            mTextCountdown.setVisibility(View.VISIBLE);
            timerToPlay.start();
        }
    }

    // Custom method to save a bitmap into internal storage
    protected Boolean saveImageToInternalStorage(Bitmap bitmap, int index, String folder){
//        // Initialize ContextWrapper
//        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
//
//        // Initializing a new file
//        // The bellow line return a directory in internal storage
//        File file = wrapper.getDir("Images",MODE_PRIVATE);
//
//        // Create a file to save the image
//        file = new File(file, "UniqueFileName"+ index+".jpg");

        //Extract file name from URL
        String fileName = String.valueOf(index) + ".jpg";

        try{
            // Initialize a new OutputStream
            OutputStream stream = null;

            //// If the output file exists, it can be replaced or appended to it
            //stream = new FileOutputStream(file);
            stream = new FileOutputStream(folder + fileName);

            // Compress the bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);

            // Flushes the stream
            stream.flush();

            // Closes the stream
            stream.close();

        }catch (IOException e) // Catch the exception
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    protected Boolean saveVideoToInternalStorage(BufferedInputStream bufferedInputStream, String _fileName, String folder){
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
        mCallback.someEvent(new PlayFragment());
    }
}
