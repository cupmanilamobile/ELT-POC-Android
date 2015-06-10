package org.cambridge.eltpoc.download;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by mbaltazar on 6/10/15.
 * http://stackoverflow.com/questions/3028306/download-a-file-with-android-and-showing-the-progress-in-a-progressdialog
 */
public class DownloadService extends IntentService {
    public static final int UPDATE_PROGRESS = 8344;
    public static final String DOWNLOAD_OUTPUT_DIR = "OUTPUT_DIR";
    public static final String DOWNLOAD_RECEIVER = "receiver";
    public static final String DOWNLOAD_URL = "url";

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String urlToDownload = intent.getStringExtra(DOWNLOAD_URL);
        ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra(DOWNLOAD_RECEIVER);
        String outputDirectory = intent.getStringExtra(DOWNLOAD_OUTPUT_DIR);

        try {
            URL url = new URL(urlToDownload);
            URLConnection connection = url.openConnection();
            connection.connect(); // java.net.UnknownHostException: Unable to resolve host "content-poc.cambridgelms.org": No address associated with hostname

            int fileLength = connection.getContentLength(); // This will be useful so that you can show a typical 0-100% progress bar

            // Download the file
            InputStream input = new BufferedInputStream(connection.getInputStream());
            OutputStream output = new FileOutputStream(outputDirectory);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;

                // publishing the progress....
                Bundle resultData = new Bundle();
                resultData.putInt("progress" ,(int) (total * 100 / fileLength));
                receiver.send(UPDATE_PROGRESS, resultData);
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bundle resultData = new Bundle();
        resultData.putInt("progress" ,100);
        receiver.send(UPDATE_PROGRESS, resultData);
    }
}
