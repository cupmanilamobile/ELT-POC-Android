package org.cambridge.eltpoc.javascript;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.webkit.JavascriptInterface;

import org.cambridge.eltpoc.download.DownloadReceiver;
import org.cambridge.eltpoc.download.DownloadService;
import org.cambridge.eltpoc.model.CLMSClass;

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
        ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(this.activity);
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
    public CLMSClass clmsLogin(String username, String password) {
        CLMSClass c = new CLMSClass();
        c.setId(143);
        c.setClassName("Test class name");
        c.setClassRole("Moron");
        c.setCourseId(111);

        return c;
    }
}