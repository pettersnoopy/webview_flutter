package io.flutter.plugins.webviewflutter;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import android.content.Context;

import java.io.File;
import android.util.Log;

public class DownLoadService extends IntentService {
    private static final String ACTION_FOO = "com.xw.xianwan.action.FOO";
    private static final String EXTRA_PARAM1 = "com.xw.xianwan.extra.PARAM1";
    DownloadManager downloadManager;
    String apkName = "";
    long mTaskId;

    public DownLoadService() {
        super("DownLoadService");
    }

    public static void startActionFoo(Context context, String param1) {
        Intent intent = new Intent(context, DownLoadService.class);
        intent.setAction("com.xw.xianwan.action.FOO");
        intent.putExtra("com.xw.xianwan.extra.PARAM1", param1);
        context.startService(intent);
        Log.i("startActionFoo", param1);
    }

    protected void onHandleIntent(Intent intent) {
        Log.i("onHandleIntent", "onHandleIntent");
        if (intent != null) {
            String action = intent.getAction();
            if ("com.xw.xianwan.action.FOO".equals(action)) {
                String param1 = intent.getStringExtra("com.xw.xianwan.extra.PARAM1");
                handleActionFoo(param1);
            }
        }
    }

    private void handleActionFoo(String param1) {
        downloadAPK(param1);
    }

    private void downloadAPK(String versionUrl) {
        Log.i("downloadAPK", versionUrl);
        String downloadPath;
        if (XWanUtils.hasSD()) {
            downloadPath = Environment.getExternalStorageDirectory() + "/51xianwan";
        } else {
            Toast.makeText(getApplicationContext(), "您还没有没有内存卡哦!", 0).show();
            return;
        }

        File file = new File(downloadPath);
        if (!file.exists()) {
            file.mkdir();
        }

        int last = versionUrl.lastIndexOf("/") + 1;
        this.apkName = versionUrl.substring(last);
        if (!this.apkName.contains(".apk")) {
            if (this.apkName.length() > 10) {
                this.apkName = this.apkName.substring(this.apkName.length() - 10);
            }
            this.apkName += ".apk";
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(versionUrl));
        request.setAllowedOverRoaming(false);
        request.setAllowedNetworkTypes(3);

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(versionUrl));
        request.setMimeType(mimeString);

        request.setNotificationVisibility(0);
        request.setVisibleInDownloadsUi(true);

        request.setDestinationInExternalPublicDir("/51xianwan/", this.apkName);

        this.downloadManager = ((DownloadManager) getSystemService("download"));
        this.mTaskId = this.downloadManager.enqueue(request);
        if (WebViewFlutterPlugin.mRegistrar.activity() != null) {
            WebViewFlutterPlugin.mRegistrar.activity().registerReceiver(new DownLoadReceiver(),
                    new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }

        SharedPreferences sp = getSharedPreferences("xw", 0);
        sp.edit().putLong("taskid", this.mTaskId).commit();
        sp.edit().putString("apkname", this.apkName).commit();
    }
}