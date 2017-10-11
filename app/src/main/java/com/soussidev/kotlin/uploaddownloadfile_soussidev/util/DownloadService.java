package com.soussidev.kotlin.uploaddownloadfile_soussidev.util;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.soussidev.kotlin.uploaddownloadfile_soussidev.R;
import com.soussidev.kotlin.uploaddownloadfile_soussidev.api.ApiService;
import com.soussidev.kotlin.uploaddownloadfile_soussidev.api.RetroClient;
import com.soussidev.kotlin.uploaddownloadfile_soussidev.model.Download;
import com.soussidev.kotlin.uploaddownloadfile_soussidev.ui.RetrofitDownladFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by Soussi on 10/10/2017.
 */

public class DownloadService extends IntentService {
    private String Nfile;

    public DownloadService() {
        super("Download Service");
    }

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private int totalFileSize;
   // private static final String ROOT_URL = "http://10.0.2.2:8080/php_upload/";

    /**
     * @author Soussi
     *
     * @Fun onHandleIntent()
     *
     * @param intent
     *
     */
    @Override
    protected void onHandleIntent(Intent intent) {
       Nfile= intent.getStringExtra("Nfile");
        Log.d("Intent service", "onHandleIntent: "+Nfile.toString());

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.download_56)
                .setContentTitle("Download"+Nfile)
                .setContentText("Downloading File")
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());

        initDownload();

    }

    /**
     * @author Soussi
     *
     * @Fun initDownload()
     *
     *
     *
     */

    private void initDownload(){

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(ROOT_URL)
//                .build();
//
//        ApiService retrofitInterface = retrofit.create(ApiService.class);

        //Create Upload Server Client
        ApiService servicedownload = RetroClient.getApiServiceDownload();

        Call<ResponseBody> request = servicedownload.downloadFile(Nfile.toString());
        try {

            downloadFile(request.execute().body());

        } catch (IOException e) {

            e.printStackTrace();
           // Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            Log.e("soussidev", "initDownload: ",e );

        }
    }

    /**
     * @author Soussi
     *
     * @Fun downloadFile()
     *
     * @param body
     * @throws IOException
     *
     */

    private void downloadFile(ResponseBody body) throws IOException {

        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Nfile.trim());
        OutputStream output = new FileOutputStream(outputFile);
        long total = 0;
        long startTime = System.currentTimeMillis();
        int timeCount = 1;
        while ((count = bis.read(data)) != -1) {

            total += count;
            totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
            double current = Math.round(total / (Math.pow(1024, 2)));

            int progress = (int) ((total * 100) / fileSize);

            long currentTime = System.currentTimeMillis() - startTime;

            Download download = new Download();
            download.setTotalFileSize(totalFileSize);

            if (currentTime > 1000 * timeCount) {

                download.setCurrentFileSize((int) current);
                download.setProgress(progress);
                sendNotification(download);
                timeCount++;
            }

            output.write(data, 0, count);
        }
        onDownloadComplete();
        output.flush();
        output.close();
        bis.close();

    }

    /**
     * @author Soussi
     *
     * @Fun sendNotification()
     *
     * @param download
     *
     */

    private void sendNotification(Download download){

        sendIntent(download);
        notificationBuilder.setProgress(100,download.getProgress(),false);
        notificationBuilder.setContentText(String.format("Downloaded (%d/%d) MB",download.getCurrentFileSize(),download.getTotalFileSize()));
        notificationManager.notify(0, notificationBuilder.build());
    }

    /**
     * @author Soussi
     *
     * @Fun sendIntent()
     *
     * @param download
     *
     *
     */

    private void sendIntent(Download download){

        Intent intent = new Intent(RetrofitDownladFile.MESSAGE_PROGRESS);
        intent.putExtra("download",download);
        LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
    }

    /**
     * @author Soussi
     *
     * @Fun onDownloadComplete()
     *
     *
     *
     */

    private void onDownloadComplete(){

        Download download = new Download();
        download.setProgress(100);
        sendIntent(download);

        notificationManager.cancel(0);
        notificationBuilder.setProgress(0,0,false);
        notificationBuilder.setContentText("File Downloaded");
        notificationManager.notify(0, notificationBuilder.build());

    }

    /**
     * @author Soussi
     *
     * @Fun onTaskRemoved()
     *
     *@param rootIntent
     *
     */

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }

}
