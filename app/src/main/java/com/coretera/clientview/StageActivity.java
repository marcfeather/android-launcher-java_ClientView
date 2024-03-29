package com.coretera.clientview;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.coretera.clientview.utility.*;

import java.io.File;

public class StageActivity extends AppCompatActivity {

    private Context mContext;
    private WebView webView;

    String folderHtml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_stage);

        mContext = getApplicationContext();

        Intent intent = getIntent();
        String contentSubPath = intent.getStringExtra("contentSubPath");
        Log.d("DebugStep", "contentSubPath: " + contentSubPath);

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        //webView.loadUrl("http://www.google.com");
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());


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

    }

//    @Override
//    protected void onUserLeaveHint() {
//        //text.setText("Home buton pressed");
//        Toast.makeText(StageActivity.this,"Home buton pressed", Toast.LENGTH_LONG).show();
//        super.onUserLeaveHint();
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
    }

}
