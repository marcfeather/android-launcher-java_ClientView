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
import android.support.v4.app.Fragment;
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

public class MainFragment extends Fragment implements View.OnClickListener {

    Callback mCallback;
    Context mContext;

    ProgressBar mProgressBar;
    TextView mTextViewloading;
    Button mButtonUpdate, mButtonPlay;

    private ProgressDialog mProgressDialog;
    private String folder;

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
            view.findViewById(R.id.button_config).setOnClickListener(this);
            view.findViewById(R.id.button_update).setOnClickListener(this);
            view.findViewById(R.id.button_play).setOnClickListener(this);

            mProgressBar = view.findViewById(R.id.progressBar);
            mTextViewloading = view.findViewById(R.id.loading_text);
            mButtonUpdate = view.findViewById(R.id.button_update);
            mButtonPlay = view.findViewById(R.id.button_play);

        }catch (Exception e){
            setting.toastException(mContext, e.getMessage());
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //External directory path to save file
        folder = Environment.getExternalStorageDirectory() + File.separator + "ClientViewLauncher/";
        setting.SaveExternalStorageDirectory(mContext, folder);

        File directory = new File(folder);
        File[] files = directory.listFiles();

        if (files != null && files.length > 0) {
            mButtonPlay.setEnabled(true);
        }else {
            mButtonPlay.setEnabled(false);
        }

        mTextViewloading.setVisibility(View.VISIBLE);

        if (setting.GetIsSetConfig(getContext())){
            mProgressBar.setVisibility(View.VISIBLE);
            mTextViewloading.setText("Network connecting..");

            timer.start();

        }else {
            mTextViewloading.setText("Network not connect, Please config wifi");
        }
    }

    @Override
    public void onClick(View v) {
        try {
            timer.cancel();
            timer2.cancel();

            Fragment fragment;
            switch (v.getId()) {
                case R.id.button_config:
                    fragment = new ConfigFragment();
                    mCallback.someEvent(fragment);
                    break;

                case R.id.button_update:
                    UpdateContent();
                    break;

                case R.id.button_play:
                    PlayContent();
                    break;
            }
        }catch (Exception e){
            setting.toastException(mContext, e.getMessage());
        }
    }

    //timer
    CountDownTimer timer = new CountDownTimer(3000, 1000) {
        Boolean result;

        public void onTick(long millisUntilFinished) {
            try {
                if (setting.isConnectedToNetwork(mContext) && setting.isOnline()){
                    result = true;
                }else {
                    result = false;
                }

            }catch (Exception e){
                setting.toastException(mContext, e.getMessage());
            }
        }

        public void onFinish() {
            try {
                mProgressBar.setVisibility(View.GONE);

                if (result){
                    mTextViewloading.setText("Network connected");
                    mButtonUpdate.setEnabled(true);

                    timer2.start();

                }else {
                    mTextViewloading.setText("Network not connect, Please config wifi");
                    mButtonUpdate.setEnabled(false);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.VISIBLE);
                            mTextViewloading.setText("Network connecting..");

                            timer.start();

                        }
                    }, 3000);
                }

            }catch (Exception e){
                setting.toastException(mContext, e.getMessage());
            }
        }
    };

    //timer2
    CountDownTimer timer2 = new CountDownTimer(10000, 1000) {
        int countdown = 10;

        public void onTick(long millisUntilFinished) {
            try {
                mButtonPlay.setText("Play in " + String.valueOf(countdown));
                countdown--;

            }catch (Exception e){
                setting.toastException(mContext, e.getMessage());
            }
        }

        public void onFinish() {
            try {
                mButtonPlay.setText("Play");
                PlayContent();

            }catch (Exception e){
                setting.toastException(mContext, e.getMessage());
            }
        }
    };

    private void UpdateContent()
    {
        InitProgressDialog();

        //External directory path to save file
        //folder = Environment.getExternalStorageDirectory() + File.separator + "ClientViewLauncher/";
        //Create androiddeft folder if it does not exist
        File directory = new File(folder);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        //setting.SaveExternalStorageDirectory(mContext, folder);

        // Specify some url to download images
        //final String urlPath = "http://www.froove.com/fastaed.com/public_html/frove/cn1/1.jpg";
        //final URL url1 = stringToURL(urlPath + "1.jpg");
        final String urlPath = "http://www.freeimageslive.com/galleries/transtech/informationtechnology/pics/";
        final URL url1 = stringToURL(urlPath + "beige_keyboard.jpg");
        final URL url2 = stringToURL(urlPath + "computer_blank_screen.jpg");
        final URL url3 = stringToURL(urlPath + "computer_memory_dimm.jpg");
        final URL url4 = stringToURL(urlPath + "computer_memory.jpg");
        final URL url5 = stringToURL(urlPath + "ethernet_router.jpg");
//        final URL url6 = stringToURL(urlPath + "beige_keyboard.jpg");
//        final URL url7 = stringToURL(urlPath + "computer_blank_screen.jpg");
//        final URL url8 = stringToURL(urlPath + "computer_memory_dimm.jpg");
//        final URL url9 = stringToURL(urlPath + "computer_memory.jpg");
//        final URL url10 = stringToURL(urlPath + "ethernet_router.jpg");

        // Execute the async task
        new DownloadTask()
                .execute(
                        url1,
                        url2,
                        url3,
                        url4,
                        url5//,
//                                url6,
//                                url7,
//                                url8,
//                                url9,
//                                url10
                );
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
        mProgressDialog.setMessage("กรุณารอซักครู่, กำลังดึงรูปภาพจากเซิร์ฟเวอร์...");
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
    private class DownloadTask extends AsyncTask<URL,Integer,Boolean>{
        // Before the tasks execution
        protected void onPreExecute(){
            // Display the progress dialog on async task start
            mProgressDialog.show();
            mProgressDialog.setProgress(0);
        }

        // Do the task in background/non UI thread
        protected Boolean doInBackground(URL...urls){
            int count = urls.length;
            HttpURLConnection connection = null;
            InputStream inputStream;
            Bitmap bitmap;
            Boolean result = false;

            // Loop through the urls
            for(int i=0;i<count;i++){
                URL currentURL = urls[i];
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

                    // Convert BufferedInputStream to Bitmap object
                    bitmap = BitmapFactory.decodeStream(bufferedInputStream);

                    result = saveImageToInternalStorage(bitmap,i,folder);
                    if (!result) {
                        break;
                    }

                    // Publish the async task progress
                    // Added 1, because index start from 0
                    publishProgress((int) (((i+1) / (float) count) * 100));
                    if(isCancelled()){
                        break;
                    }

                }catch(IOException e){
                    e.printStackTrace();
                }finally{
                    // Disconnect the http url connection
                    connection.disconnect();
                }
            }

            return result;
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

            if (!result) {
                Toast.makeText(getActivity(), "พบความผิดพลาดในการบันทึกรูปภาพ", Toast.LENGTH_LONG).show();
            }

            //new InitImageViewTask().execute();

            mButtonPlay.setEnabled(true);
            PlayContent();
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
        String fileName = String.valueOf(index + 1) + ".jpg";

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

    private void PlayContent() {
        Fragment fragment = new PlayFragment();
        mCallback.someEvent(fragment);
    }
}
