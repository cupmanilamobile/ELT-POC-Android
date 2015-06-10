package org.cambridge.eltpoc.download;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by mbaltazar on 6/10/15.
 */
public class DownloadReceiver extends ResultReceiver {
    private ProgressDialog mProgressDialog;
    public DownloadReceiver(Handler handler, ProgressDialog mProgressDialog) {
        super(handler);
        this.mProgressDialog = mProgressDialog;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        if (resultCode == DownloadService.UPDATE_PROGRESS) {
            int progress = resultData.getInt("progress");
            mProgressDialog.setProgress(progress);
            if (progress == 100) {
                mProgressDialog.dismiss();
            }
        }
    }
}
