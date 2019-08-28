package com.coretera.clientview.utility;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class MysqlConnector {

//    public static ArrayList<HashMap<String, String>> selectAllContent(String url) { //------------ Method to select data ---------------//
//
//        InputStream is = null;
//        String js_result = "";
//
//        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
//
//        try {
//
//            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//
//			/* Set to Http post*/
//
//			/* End set Value*/
//
//            HttpClient httpclient = new DefaultHttpClient();
//
//			/* Set URL*/
//            HttpPost httppost = new HttpPost(url + "/client_get_data.php"); //https://10.0.2.2/client_get_data.php
//			/* End Set URL*/
//
//            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
//            HttpResponse response = httpclient.execute(httppost);
//            HttpEntity entity = response.getEntity();
//            is = entity.getContent();
//        } catch (Exception e) {
//            Log.d("DebugStep", "Error in http connection " + e.toString());
//            return list;
//        }
//
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
//            Log.d("DebugStep", "BufferedReader: " + reader.readLine());
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                sb.append(line);
//            }
//            is.close();
//            js_result = sb.toString();
//        } catch (Exception e) {
//            Log.d("DebugStep", "Error converting result " + e.toString());
//            return list;
//        }
//
//        try {
//
//            final JSONArray jArray = new JSONArray(js_result);
//            for (int i = 0; i < jArray.length(); i++) {
//                JSONObject jo = jArray.getJSONObject(i);
//                HashMap<String, String> item = new HashMap<String,String>();
//                item.put("local_path", jo.get("local_path").toString());
//                Log.e("test",jo.get("local_path").toString());
//                list.add(item);
//            }
//        } catch (JSONException e) {
//            Log.d("DebugStep", "Error parsing data " + e.toString());
//            return list;
//        }
//
//        return list;
//    }

    public static ArrayList<HashMap<String, String>> getContentPathByImei(String url, String imei_number) { //------------ Method to select data ---------------//

        Log.d("DebugStep", "url: " + url);
        Log.d("DebugStep", "imei_number: " + imei_number);

        InputStream is = null;
        String js_result = "";

        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        try {

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			/* Set to Http post*/
            //nameValuePairs.add(new BasicNameValuePair("id", imei_number));
			/* End set Value*/

            HttpClient httpclient = new DefaultHttpClient();

            Log.d("DebugStep", "server file path: " + url + "/controllers/client_post_get_data.php");

			/* Set URL*/
            HttpPost httppost = new HttpPost(url + "/controllers/client_post_get_data.php"); //https://10.0.2.2/client_get_data.php
			/* End Set URL*/

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            if (is == null) {
                Log.d("DebugStep", "InputStream: Null");
            }
            Log.d("DebugStep", "InputStream: Not Null");
        } catch (Exception e) {
            Log.d("DebugStep", "Error in http connection " + e.toString());
            return list;
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            Log.d("DebugStep", "BufferedReader: " + reader.readLine());
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                Log.d("DebugStep", "readLine: " + line);
            }
            is.close();
            js_result = sb.toString();
        } catch (Exception e) {
            Log.d("DebugStep", "Error converting result " + e.toString());
            return list;
        }

        try {
            Log.d("DebugStep", "js_result: " + js_result);
            final JSONArray jArray = new JSONArray(js_result);
            Log.d("DebugStep", "jArray.length: " + jArray.length());
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jo = jArray.getJSONObject(i);
                HashMap<String, String> item = new HashMap<String,String>();
                item.put("local_path", jo.get("local_path").toString());
                //item.put("contentName", jo.get("contentName").toString());
                Log.d("DebugStep", "local_path: " + jo.get("local_path").toString());
                //Log.d("DebugStep", "contentName: " + jo.get("contentName").toString());
                list.add(item);
            }
        } catch (JSONException e) {
            Log.d("DebugStep", "Error parsing data " + e.toString());
            return list;
        }

        return list;
    }
}
