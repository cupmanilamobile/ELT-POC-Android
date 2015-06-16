package org.cambridge.eltpoc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;

import org.cambridge.eltpoc.api.TestHarnessService;
import org.cambridge.eltpoc.javascript.CLMSJavaScriptInterface;
import org.cambridge.eltpoc.model.CLMSUser;

import java.util.List;

import io.realm.Realm;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_main);
        this.initializeWebView();


        new Thread(new Runnable() {
            @Override
            public void run() {
                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint("http://content-poc-api.cambridgelms.org")
                        .build();

                TestHarnessService th = restAdapter.create(TestHarnessService.class);
                th.getBearerToken("password", "app", "satya", "password");
            }
        }).start();
////
//        Realm.deleteRealmFile(this);
//
//        Realm realm = Realm.getInstance(this);
//        Log.d("", "path: " + realm.getPath());
//
//        realm.beginTransaction();
//
//        CLMSClass c = realm.createObject(CLMSClass.class);
//        c.setId(938);
//        c.setClassName("Feeding Time: The Feeding Habits Selft-study");
//        c.setClassRole("Student");
//        c.setCourseId(890);

//        realm.commitTransaction();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Private methods */
    private void initializeWebView() {
        WebView webView = (WebView)findViewById(R.id.webview);
        CLMSJavaScriptInterface jsInterface = new CLMSJavaScriptInterface(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(jsInterface, "JSInterface");
//        webView.addJavascriptInterface(new CLMSJavaScriptInterface(this), "JSInterface");
        webView.loadUrl("file:///android_asset/www/index.html");

    }
}
