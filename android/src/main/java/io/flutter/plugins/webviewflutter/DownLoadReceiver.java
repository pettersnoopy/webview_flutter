package com.xianwan.sdklibrary.view;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import androidx.core.content.FileProvider;

public class DownLoadReceiver extends BroadcastReceiver
{
    long mTaskId;
    String apkName = "";
    private Context mContext;
    DownloadManager downloadManager;

    public void onReceive(Context context, Intent intent)
    {
        this.mContext = context;
        long myDwonloadID = intent.getLongExtra("extra_download_id", -1L);
        SharedPreferences sPreferences = context.getSharedPreferences("xw", 0);
        this.mTaskId = sPreferences.getLong("taskid", 0L);
        this.apkName = sPreferences.getString("apkname", "");
        if (myDwonloadID != -1L) {
            this.mTaskId = myDwonloadID;
        }
        if (this.mTaskId == myDwonloadID)
            checkDownloadStatus(context);
    }

    protected void installAPK(Context context, File file)
    {
        if (!file.exists()) return;
        Intent intent = new Intent("android.intent.action.VIEW");

        int sdkVersion = context.getApplicationInfo().targetSdkVersion;
        Uri uri;
        if ((android.os.Build.VERSION.SDK_INT >= 24) && (sdkVersion >= 24)) {
            uri = FileProvider.getUriForFile(context, this.mContext.getApplicationContext().getPackageName() + ".fileProvider", file);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        else {
            uri = Uri.parse("file://" + file.toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");

        context.startActivity(intent);
    }

    private void checkDownloadStatus(Context context)
    {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(new long[] { this.mTaskId });
        this.downloadManager = ((DownloadManager)context.getSystemService("download"));
        Cursor c = this.downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex("status"));
            this.apkName = c.getString(c.getColumnIndex("title"));
            switch (status) {
                case 4:
                    Log.i("DownLoadService", ">>>下载暂停");
                case 1:
                    Log.i("DownLoadService", ">>>下载延迟");
                case 2:
                    Log.i("DownLoadService", ">>>正在下载");
                    break;
                case 8:
                    Log.i("DownLoadService", ">>>下载完成");

                    String downloadPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "51xianwan" + File.separator + this.apkName;

                    installAPK(context, new File(downloadPath));
                    break;
                case 16:
                    Log.i("DownLoadService", ">>>下载失败");
            }
        }
    }
}