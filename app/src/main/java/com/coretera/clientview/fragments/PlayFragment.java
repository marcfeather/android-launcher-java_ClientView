package com.coretera.clientview.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

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
    CountDownTimer timer;

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
            InitProgressDialog();

        }catch (Exception e){
            setting.toastException(mContext, e.getMessage());
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        mProgressDialog.setMessage("โหลดรูปภาพ..");
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
        String folder = setting.GetExternalStorageDirectory(mContext);
        File directory = new File(folder);

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

                timer.cancel();
                timer.start();
            }
        });

        mPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("Awesome Tag", "onTouch");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("Awesome Tag", "ACTION_DOWN");
                        timer.cancel();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("Awesome Tag", "ACTION_UP");
                        timer.start();
                        break;
                }
                return false;
            }
        });

        int sec = setting.GetSlideTime(mContext);
        if (sec == 0) { sec = ConfigFragment.length; }

        automateSlider(sec);
    }

    private void automateSlider(int secTimeSlide) {
        timer = new CountDownTimer((secTimeSlide * 1000), 1000) {
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

        timer.start();
    }
}


