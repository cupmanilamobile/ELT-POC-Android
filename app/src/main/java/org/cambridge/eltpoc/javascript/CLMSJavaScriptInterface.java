package org.cambridge.eltpoc.javascript;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.webkit.JavascriptInterface;

import org.cambridge.eltpoc.download.DownloadReceiver;
import org.cambridge.eltpoc.download.DownloadService;
import org.cambridge.eltpoc.model.CLMSUser;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by mbaltazar on 6/10/15.
 */
public class CLMSJavaScriptInterface {
    private Activity activity;

    public CLMSJavaScriptInterface(Activity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void downloadContent() {
        ProgressDialog mProgressDialog = new ProgressDialog(this.activity);
        mProgressDialog.setMessage("Message");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();

        Intent intent = new Intent(this.activity, DownloadService.class);
        intent.putExtra(DownloadService.DOWNLOAD_URL, "http://content-poc.cambridgelms.org/touchstone2/p/sites/default/files/html_content_zip/UN_UVM_OWB_1B_LS_U06_E10_HTML5_GMV01.zip");
        intent.putExtra(DownloadService.DOWNLOAD_RECEIVER, new DownloadReceiver(new Handler(), mProgressDialog));
        intent.putExtra(DownloadService.DOWNLOAD_OUTPUT_DIR, activity.getFilesDir().getAbsolutePath());
        this.activity.startService(intent);
    }

    @JavascriptInterface
    public boolean authenticateLogin(String user, String password) {

        // To dismiss the dialog

        String urlString = "http://content-poc-api.cambridgelms.org/v1.0/authorize";

        HttpURLConnection urlConnection = null;
        URL url = null;
        JSONObject object = null;
        InputStream inStream = null;
        try {
            url = new URL(urlString.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            ContentValues values = new ContentValues();
            values.put("grant_type","password");
            values.put("client_id", "app" );
            values.put("username", user );
            values.put("password", password);


            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(values));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();
            inStream = urlConnection.getInputStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
            String temp, response = "";
            while ((temp = bReader.readLine()) != null) {
                response += temp;
            }

            object = (JSONObject) new JSONTokener(response).nextValue();
            System.out.println(response + "xxxxxxx");
            if(object.getString("access_token") != null){
                Realm realm = Realm.getInstance(this.activity);
                realm.beginTransaction();


//                RealmQuery<CLMSUser> query = realm.where(CLMSUser.class);
//
//                query.equalTo("username", "satya");
//
//                RealmResults<CLMSUser> result = query.findAll();
//                realm.commitTransaction();
//                if(result.isEmpty()) {
//                    realm.beginTransaction();
//                    CLMSUser realmUser = realm.createObject(CLMSUser.class);
//                    realmUser.setAccess_token(object.getString("access_token"));
//                    realmUser.setUsername(user);
//                    realmUser.setPassword(password);
//                    realm.commitTransaction();
//                }
                SharedPreferences preferences = activity.getApplicationContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                return true;
            }
            else {

            }
        } catch (Exception e) {

            AlertDialog alertDialog = new AlertDialog.Builder(this.activity).create();
            alertDialog.setTitle("Authentication Failed");
            alertDialog.setMessage(e.getMessage());
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            alertDialog.show();

            //this.mException = e;
        } finally {

            if (inStream != null) {
                try {
                    // this will close the bReader as well
                    inStream.close();
                } catch (IOException ignored) {
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }


       return false;
    }
    private String getQuery(ContentValues params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;



        for (Map.Entry<String,Object> entry : params.valueSet())
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
        }

        return result.toString();
    }
}