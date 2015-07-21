package org.cambridge.eltpoc.connections;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.cambridge.eltpoc.model.CLMSContentScore;
import org.cambridge.eltpoc.model.CLMSModel;
import org.cambridge.eltpoc.util.RealmTransactionUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import io.realm.Realm;

/**
 * Created by etorres on 7/8/15.
 */
public class DownloadAsync extends AsyncTask<Object, Object, Object> {
    private Context context;
    private CLMSContentScore contentScore;
    private String urlToDownload;
    private String outputDirectory;
    private ProgressDialog mProgressDialog;
    private int progress = 0;
    private String outputFile;
    private CLMSModel webModel;
    private int courseId;

    public DownloadAsync(Context context, CLMSContentScore contentScore, String url,
                         String outputDirectory, String outputFile, CLMSModel webModel,
                         int courseId) {
        this.context = context;
        this.contentScore = contentScore;
        urlToDownload = url;
        this.outputDirectory = outputDirectory;
        this.outputFile = outputFile;
        this.webModel = webModel;
        this.courseId = courseId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Downloading " + contentScore.getContentName());
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setProgressNumberFormat(null);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
        mProgressDialog.setProgress(progress);
        if(progress == 100) {
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Unzipping files...");
        }
    }

    @Override
    protected Object doInBackground(Object... params) {
        try {
            URL url = new URL(urlToDownload);
            URLConnection connection = url.openConnection();
            connection.connect(); // java.net.UnknownHostException: Unable to resolve host "content-poc.cambridgelms.org": No address associated with hostname

            int fileLength = connection.getContentLength(); // This will be useful so that you can show a typical 0-100% progress bar

            // Download the file
            InputStream input = new BufferedInputStream(connection.getInputStream());
            OutputStream output = new FileOutputStream(outputDirectory + "/" + outputFile);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                progress = (int) ((float) ((float) (total * 100) / (float) fileLength));
                publishProgress();
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        progress = 100;
        publishProgress();
        String unzipped = outputFile.replace(".zip", "");
        try {
            unzip(outputDirectory + "/" + outputFile, outputDirectory + "/" + unzipped);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        mProgressDialog.dismiss();

        String unzipped = outputFile.replace(".zip", "");

        final CLMSContentScore contentScoreEdit = RealmTransactionUtils.getContentScore(context,
                contentScore.getClassId(), contentScore.getUnitId(), contentScore.getLessonId(),
                contentScore.getId());
        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        contentScoreEdit.setDownloadedFile(outputDirectory +
                "/" + unzipped);
        realm.copyToRealmOrUpdate(contentScoreEdit);
        realm.commitTransaction();


        File file = new File(outputDirectory + "/" + outputFile);
        file.delete();
        webModel.setCourseId(courseId);
        webModel.setContentScore(RealmTransactionUtils.cloneContentScore(contentScore));
        webModel.setWebOperation(CLMSModel.WEB_OPERATION.DOWNLOADED);
        webModel.notifyObservers();
    }

    private void unzip(String zipFilePath, String destinationPath) throws Exception {
        File archive = new File(zipFilePath);
        try {
            ZipFile zipfile = new ZipFile(archive);
            for (Enumeration e = zipfile.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                unzipEntry(zipfile, entry, destinationPath);
            }
        } catch (Exception e) {
            Log.e("Unzip zip", "Unzip exception", e);
        }
    }

    private void unzipEntry(ZipFile zipfile, ZipEntry entry, String outputDir) throws IOException {
        if (entry.isDirectory()) {
            createDir(new File(outputDir, entry.getName()));
            return;
        }
        File outputFile = new File(outputDir, entry.getName());
        if (!outputFile.getParentFile().exists()) {
            createDir(outputFile.getParentFile());
        }
        Log.v("ZIP E", "Extracting: " + entry);

        InputStream zin = zipfile.getInputStream(entry);
        BufferedInputStream inputStream = new BufferedInputStream(zin);
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
        try {
            try {
                for (int c = inputStream.read(); c != -1; c = inputStream.read())
                    outputStream.write(c);
            } finally {
                outputStream.close();
            }
        } finally {
            outputStream.close();
            inputStream.close();
        }
    }

    private void createDir(File dir) {
        if (dir.exists()) {
            return;
        }
        Log.v("ZIP E", "Creating dir " + dir.getName());
        if (!dir.mkdirs()) {
            throw new RuntimeException("Can not create dir " + dir);
        }
    }
}
