package org.cambridge.eltpoc.connections;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.cambridge.eltpoc.ELTApplication;
import org.cambridge.eltpoc.model.CLMSContentScore;
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

    public DownloadAsync(Context context, CLMSContentScore contentScore, String url,
                         String outputDirectory, String outputFile) {
        this.context = context;
        this.contentScore = contentScore;
        urlToDownload = url;
        this.outputDirectory = outputDirectory;
        this.outputFile = outputFile;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Downloading " + contentScore.getContentName());
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
        mProgressDialog.setProgress(progress);
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
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        String unzipped = outputFile.replace(".zip", "");
        RealmTransactionUtils.updateContentScoreUrl(context, contentScore, outputDirectory +
                "/" + unzipped);
        try {
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Unzipping files...");
            unzip(outputDirectory + "/" + outputFile, outputDirectory + "/" + unzipped);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mProgressDialog.dismiss();
        ELTApplication.getInstance().getLinkModel().setWebLink("file:///" +
                outputDirectory + "/" + unzipped + "/index.html");
        ELTApplication.getInstance().getLinkModel().setClassName(contentScore.getContentName());
        ELTApplication.getInstance().getLinkModel().notifyObservers();
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

//    private void unzip(String zipFile, String location) {
//        try  {
//            FileInputStream fin = new FileInputStream(zipFile);
//            ZipInputStream zin = new ZipInputStream(fin);
//            ZipEntry ze = null;
//            while ((ze = zin.getNextEntry()) != null) {
//                Log.v("Decompress", "Unzipping " + ze.getName());
//                if(ze.isDirectory())
//                    checkDirectory(ze.getName(), location);
//                else {
//                    mProgressDialog.setMessage("Unzipping...");
//                    mProgressDialog.setIndeterminate(true);
//                    FileOutputStream fout = new FileOutputStream(location + ze.getName());
//                    for (int c = zin.read(); c != -1; c = zin.read()) {
//                        fout.write(c);
//                    }
//                    zin.closeEntry();
//                    fout.close();
//                }
//            }
//            zin.close();
//        } catch(Exception e) {
//            Log.e("Decompress", "unzip", e);
//        }
//
//    }
//
//    private void checkDirectory(String dir, String location) {
//        File f = new File(location + dir);
//        if(!f.isDirectory()) {
//            f.mkdirs();
//        }
//    }
}
