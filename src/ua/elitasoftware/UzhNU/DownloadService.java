package ua.elitasoftware.UzhNU;

import android.app.*;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.app.DownloadManager.Request;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends IntentService {

    private String fullName;
    private String fileExtension;
    private DownloadManager dm;
    private long enqueue;
    public DownloadService(){
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //get file's name and download url
        String fileName = intent.getStringExtra(TimetablesFragment.FILE_NAME) + MainActivity.newVer;
        String fileURL = intent.getStringExtra(TimetablesFragment.FILE);
        fileExtension = intent.getStringExtra(TimetablesFragment.FILE_EXTENCION);

        try{
            URL url = new URL(fileURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            //get download folder path or create new one
            String folderName = getResources().getString(R.string.folderName);
            File appDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + folderName);
            if (!appDirectory.exists()){
                appDirectory.mkdirs();
            }

            //get file extension
            if (fileExtension == null){
                fileExtension = httpURLConnection.getHeaderField("Content-Disposition");
                fileExtension = fileExtension.substring(fileExtension.indexOf(".")+1, fileExtension.lastIndexOf("\""));
            }

            fullName = fileName + "." + fileExtension;
            httpURLConnection.disconnect();

            dm = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
            Intent downloadIntent = new Intent();
            downloadIntent.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
            Request request = new Request(Uri.parse(fileURL));
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS.concat(folderName), fullName)
                   .setNotificationVisibility(fileExtension.equals("apk") ? Request.VISIBILITY_VISIBLE :
                           Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                   .setMimeType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension))
                   .setTitle(fileName);
            enqueue = dm.enqueue(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
