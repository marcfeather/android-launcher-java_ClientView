package com.coretera.clientview;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.coretera.clientview.utility.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class StageActivity extends AppCompatActivity {

    private Context mContext;
    private WebView webView;

    private ProgressDialog mProgressDialog;

    String folderHtml;
    String StorezipFileLocation;
    String DirectoryName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_stage);

        mContext = getApplicationContext();

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        //webView.loadUrl("http://www.google.com");
        webView.setWebViewClient(new WebViewClient());

//        String folderHtml = setting.GetExternalStorageDirectoryHtml(mContext);
//        File directory = new File(folderHtml);
//        if (directory.exists()) {
//            File[] files = directory.listFiles();
//            if (files != null && files.length > 0) {
//                String filePath = folderHtml + "index.html";
//                File file = new File(filePath);
//                if(file.exists()){
//                    webView.loadUrl("file://" + filePath);
//                    //Log.d("GetHtml", "filePath: " + filePath);
//                }
//            }
//        }

        folderHtml = setting.GetExternalStorageDirectoryHtml(mContext);

        try {
            unzip();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
    }

    public void unzip() throws IOException
    {
        mProgressDialog = new ProgressDialog(StageActivity.this);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        StorezipFileLocation = folderHtml + "localweb.zip";
        DirectoryName = folderHtml;

        new UnZipTask().execute(StorezipFileLocation, DirectoryName);
    }

    private class UnZipTask extends AsyncTask<String, Void, Boolean>
    {
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


                UnzipUtil d = new UnzipUtil(StorezipFileLocation, DirectoryName);
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
            mProgressDialog.dismiss();

            File directory = new File(DirectoryName);
            if (directory.exists()) {
                File[] files = directory.listFiles();
                if (files != null && files.length > 0) {
                    String filePath = DirectoryName + "/localweb/index.html";
                    File file = new File(filePath);
                    if(file.exists()){
                        webView.loadUrl("file://" + filePath);
                        //Log.d("GetHtml", "filePath: " + filePath);
                    }
                }
            }
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
