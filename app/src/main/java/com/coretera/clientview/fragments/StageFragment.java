package com.coretera.clientview.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.coretera.clientview.MainActivity;
import com.coretera.clientview.R;
import com.coretera.clientview.utility.setting;

import java.io.File;


public class StageFragment extends Fragment {

    private Context mContext;
    private WebView webView;

    String folderHtml;

//    public static StageFragment newInstance() {
//        StageFragment fragment = new StageFragment();
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stage, container, false);

        mContext = getContext();

        //Intent intent = getIntent();
        //String contentSubPath = intent.getStringExtra("contentSubPath");
        String contentSubPath = setting.GetExternalStorageContentSubPath(mContext);
        Log.d("DebugStep", "contentSubPath: " + contentSubPath);

        webView = view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        //webView.loadUrl("http://www.google.com");
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //This is the filter
                if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (webView.canGoBack()) {
                        webView.goBack();
                        //Dlog.d(“canGoBack”);
                    } else {
                        //Dlog.d(“canNotGoBack”);
                        //((MainActivity)getActivity()).onBackPressed();
                        getActivity().onBackPressed();
                    }
                    return true;
                }
                return false;
            }
        });


        folderHtml = setting.GetExternalStorageDirectoryHtml(mContext);

        File directory = new File(folderHtml);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null && files.length > 0) {
                String filePath = folderHtml + contentSubPath + "/index.html";
                String filePath2 = folderHtml + contentSubPath + "/index.php";
                File file = new File(filePath);
                File file2 = new File(filePath2);
                if(file.exists()){
                    webView.loadUrl("file://" + filePath);
                    //Log.d("GetHtml", "filePath: " + filePath);
                }else if(file2.exists()){
                    webView.loadUrl("file://" + filePath2);
                    //Log.d("GetHtml", "filePath: " + filePath);
                }
            }
        }

        return view;
    }

//    public boolean canGoBack() {
//        return this.webView != null && this.webView.canGoBack();
//    }
//
//    public void goBack() {
//        if(this.webView != null) {
//            this.webView.goBack();
//        }
//    }

}
