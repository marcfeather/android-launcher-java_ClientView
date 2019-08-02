package com.coretera.clientview.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.coretera.clientview.Callback;
import com.coretera.clientview.R;
import com.coretera.clientview.adapters.*;
import com.coretera.clientview.utility.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlayFragment extends Fragment {

    Callback mCallback;
    Context mContext;
    View view;

    private ProgressDialog mProgressDialog;

    private static ViewPager mPager;
    private static int currentPage = 0;

    int fileCount;
    CountDownTimer timerSlider, timerVideoStart;

    private LinearLayout mLayoutVideo;
    private VideoView mVideoView;

    private String videoFolder;

    private MediaController mMediaController;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (Callback) context;
        }catch (Exception e){
            setting.toastException(mContext, e.getMessage());
        }
    }

    public PlayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_play, container, false);

        mContext = getContext();

        try {
            mLayoutVideo = view.findViewById(R.id.layoutVideo);
            mVideoView = view.findViewById(R.id.videoView1);

            mMediaController = new MediaController(mContext);

            InitProgressDialog();

        }catch (Exception e){
            setting.toastException(mContext, e.getMessage());
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        try {
//            new InitImageViewTask().execute();
//
//        }catch (Exception e){
//            setting.toastException(mContext, e.getMessage());
//        }
    }

    @Override
    public void onPause() {
        super.onPause();

        timerSlider.cancel();
        timerVideoStart.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            new InitImageViewTask().execute();

        }catch (Exception e){
            setting.toastException(mContext, e.getMessage());
        }
    }

    private void InitProgressDialog() {
        // Initialize the progress dialog
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(false);
        // Progress dialog message
        mProgressDialog.setMessage("กำลังโหลดข้อมูล..");
        mProgressDialog.setCancelable(false);
    }

    private class InitImageViewTask extends AsyncTask<Void, Void, List<Bitmap>> {
        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected List<Bitmap> doInBackground(Void... values) {
            return GetLocalImage();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}

        @Override
        protected void onPostExecute(List<Bitmap> result) {
            if (result.size() == 0) {
                mCallback.someEvent(new MainFragment());

            }else {
                BindImageToView(result);
            }

            mProgressDialog.dismiss();
        }
    }

    private List<Bitmap> GetLocalImage() {
        List<Bitmap> listBitmap = new ArrayList<>();
        String folderImage = setting.GetExternalStorageDirectoryPicture(mContext);
        File directory = new File(folderImage);

        File[] files = directory.listFiles();
        Bitmap bitmap;

        fileCount = files.length;

        for(int i=0;i<fileCount;i++){
            bitmap = BitmapFactory.decodeFile(files[i].getPath());
            listBitmap.add(bitmap);
        }

        return listBitmap;
    }

    private void BindImageToView(List<Bitmap> listBitmap) {
        List<Bitmap> ImagesArray = new ArrayList<>();
        for(int i=0;i<listBitmap.size();i++)
            ImagesArray.add(listBitmap.get(i));

        currentPage = setting.GetCurrentPage(mContext);

        mPager = view.findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImage_Adapter(getActivity(),ImagesArray));
        // displaying selected image first
        mPager.setCurrentItem(currentPage);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) { }
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            public void onPageSelected(int position) {
                setting.SaveCurrentPage(mContext, position);

                timerSlider.cancel();
                timerSlider.start();
            }
        });

        mPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("Awesome Tag", "onTouch");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("Awesome Tag", "ACTION_DOWN");
                        timerSlider.cancel();
                        timerVideoStart.cancel();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("Awesome Tag", "ACTION_UP");
                        timerSlider.start();
                        checkVideoExists();
                        break;
                }
                return false;
            }
        });

        int sec = setting.GetSlideTime(mContext);
        if (sec == 0) { sec = ConfigFragment.length; }

        int secPlayVideo = setting.GetVideoTime(mContext);
        if (secPlayVideo == 0) { secPlayVideo = ConfigFragment.videoDefault; }

        automateSlider(sec);
        automateVideoPlayer(secPlayVideo);
    }

    private void automateSlider(int secTimeSlide) {
        timerSlider = new CountDownTimer((secTimeSlide * 1000), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                int nextSlider = setting.GetCurrentPage(mContext) + 1;

                if (nextSlider == fileCount) {
                    nextSlider = -1; // if it's last Image, let it go to the first image
                }

                mPager.setCurrentItem(nextSlider);

                start();
            }
        };

        timerSlider.start();
    }

    private void automateVideoPlayer(int secTimeSlide) {

        timerVideoStart = new CountDownTimer((secTimeSlide * 1000), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                timerSlider.cancel();
                mLayoutVideo.setVisibility(View.VISIBLE);
                mPager.setVisibility(View.GONE);

                settingVideoView();

                //mVideoView.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() +"/"+R.raw.rain_final));
            }
        };

        checkVideoExists();
    }

    private void checkVideoExists() {
        videoFolder = setting.GetExternalStorageDirectoryVideo(mContext);
        File directory = new File(videoFolder);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null && files.length > 0) {
//            for (File file : files) {
//                if(file.exists()){
//                    mVideoView.setVideoURI(file);
//                }
//            }

                String filePath = videoFolder + "video.mp4";
                File file = new File(filePath);
                if(file.exists()){
                    mVideoView.setVideoPath(filePath);
                    timerVideoStart.start();
                }
            }
        }
    }

    private void settingVideoView() {
        //set VideoView
        //mVideoView.setMediaController(new MediaController(mContext));
        mVideoView.setMediaController(mMediaController);
        mVideoView.requestFocus();
        mVideoView.start();

        mVideoView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent)
            {
//                if (mVideoView.isPlaying())
//                {
//                    mVideoView.pause();
//                    if (!getActivity().getActionBar().isShowing())
//                    {
//                        getActivity().getActionBar().show();
//                        mMediaController.show(0);
//                    }
//                    //position = mVideoView.getCurrentPosition();
//                    return false;
//                }
//                else
//                {
//                    if (getActivity().getActionBar().isShowing())
//                    {
//                        getActivity().getActionBar().hide();
//                        mMediaController.hide();
//                    }
//                    //mVideoView.seekTo(position);
//                    mVideoView.start();
//                    return false;
//                }

                mVideoView.stopPlayback();

                videoClosed();

                return false;
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer vmp) {
                videoClosed();
            }
        });
    }

    private void videoClosed(){
        mLayoutVideo.setVisibility(View.GONE);
        mPager.setVisibility(View.VISIBLE);

        timerSlider.start();
        timerVideoStart.start();
    }
}


