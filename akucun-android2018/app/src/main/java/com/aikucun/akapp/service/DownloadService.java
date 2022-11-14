package com.aikucun.akapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by micker on 2017/7/23.
 */

public class DownloadService extends IntentService {

    public static final String ACTION_DOWNLOAD = "com.example.filedownloaddemo.service.action.FOO";

    // TODO: Rename parameters
    public static final String DOWNLOAD_URL = "com.example.filedownloaddemo.service.extra.PARAM1";
    public static final String FILE_NAME = "com.example.filedownloaddemo.service.extra.PARAM2";

    public static final int UPDATE_PROGRESS = 8344;
    public static final int DOWNLOAD_ERROR  = 8444;
    public DownloadService() {
        super("DownloadService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        String urlToDownload = intent.getStringExtra(DOWNLOAD_URL);
        String fileName = intent.getStringExtra(FILE_NAME);
        ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
        try {
            URL url = new URL(urlToDownload);
            URLConnection connection = url.openConnection();
            connection.connect();
            // this will be useful so that you can show a typical 0-100% progress bar
            int fileLength = connection.getContentLength();
            // download the file
            InputStream input = new BufferedInputStream(connection.getInputStream());

            final String dir = Environment.getExternalStorageDirectory() + "/akucun/";
            File fileDir = new File(dir);
            if (!fileDir.exists())
            {
                fileDir.mkdirs();
            }

            final File file = new File(dir, fileName);
            if (file.exists())
            {
                file.delete();
            }
            file.createNewFile();

            OutputStream output = new FileOutputStream(file);
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


            Bundle resultData = new Bundle();
            resultData.putInt("progress" ,100);
            receiver.send(UPDATE_PROGRESS, resultData);

        } catch (IOException e) {
            e.printStackTrace();
            Bundle resultData = new Bundle();
            receiver.send(DOWNLOAD_ERROR, resultData);
        }
    }
}
