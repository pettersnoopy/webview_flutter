package io.flutter.plugins.webviewflutter;

import android.content.Context;
import android.text.TextUtils;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.content.ComponentName;
import android.widget.Toast;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.database.Cursor;
import android.util.Log;
import android.net.Uri;
import androidx.core.content.FileProvider;
import java.io.File;
import android.os.Bundle;
import android.os.Environment;
import androidx.core.app.ActivityCompat;

class XWanUtils {
    public static boolean isApkInstalled(Context context, String packageName) {
        Log.i("isApkInstalled", packageName);
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    public static void openApp(Context context, String packagename) {
        Log.i("openApp", packagename);
        if (TextUtils.isEmpty(packagename)) {
            return;
        }
        PackageManager packageManager = context.getPackageManager();

        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }
        Intent intent = packageManager.getLaunchIntentForPackage(packagename);
        if (intent != null) {
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "手机还未安装该应用", 0).show();
        }
    }

    public static void installAPP(Context context, String url) {
        Log.i("installAPP", url);
        int last = url.lastIndexOf("/") + 1;
        String apkName = url.substring(last);
        if (!apkName.contains(".apk")) {
            if (apkName.length() > 10) {
                apkName = apkName.substring(apkName.length() - 10);
            }
            apkName = new StringBuilder().append(apkName).append(".apk").toString();
        }
        if (ActivityCompat.checkSelfPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            XWanUtils.checkDownloadStatus(context, url, apkName);
        } else {
            XWanUtils.openAppDetails(context);
        }
    }

    public static void checkDownloadStatus(Context context, final String url, String apkName) {
        Log.i("checkDownloadStatus", apkName);
        boolean isLoading = false;
        DownloadManager.Query query = new DownloadManager.Query();
        DownloadManager downloadManager = (DownloadManager)context.getSystemService("download");
        Cursor c = downloadManager.query(query);
        String downloadPath;
        while (c.moveToNext()) {
            String LoadingUrl = c.getString(c.getColumnIndex("uri"));
            if (url.equals(LoadingUrl)) {
                isLoading = true;
                int status = c.getInt(c.getColumnIndex("status"));
                switch (status) {
                    case 4:
                        Log.i("DownLoadService", ">>>下载暂停");
                    case 1:
                        Log.i("DownLoadService", ">>>下载延迟");
                    case 2:
                        long bytes_downloaded = c.getLong(c.getColumnIndex("bytes_so_far"));
                        long bytes_total = c.getLong(c.getColumnIndex("total_size"));
                        int progress = (int)(bytes_downloaded * 100L / bytes_total);
                        Toast.makeText(context, new StringBuilder().append("正在下载，已完成").append(progress).append("%").toString(), 0).show();
                        Log.i("DownLoadService", ">>>正在下载");
                        break;
                    case 8:
                        Log.i("DownLoadService", ">>>下载完成");
                        downloadPath = new StringBuilder().append(Environment.getExternalStorageDirectory().getAbsolutePath()).append(File.separator).append("51xianwan").append(File.separator).append(apkName).toString();
                        File file = new File(downloadPath);
                        if (file.exists()) {
                            XWanUtils.installAPK(context, new File(downloadPath), apkName);
                        } else {
                            isLoading = false;
                        }
                        break;
                    case 16:
                        Log.i("DownLoadService", ">>>下载失败");
                }

                break;
            }
        }
        c.close();
        if (!isLoading) {
            downloadPath = new StringBuilder().append(Environment.getExternalStorageDirectory().getAbsolutePath()).append(File.separator).append("51xianwan").append(File.separator).append(apkName).toString();
            File file = new File(downloadPath);
            Toast.makeText(context, "开始下载", 0).show();
            DownLoadService.startActionFoo(context, url);
        }
    }

    public static void installAPK(Context context, File file, String apkName) {
        Log.i("installAPK", apkName);
        if ((file == null) || (!file.exists())) return;
        Intent intent = new Intent("android.intent.action.VIEW");

        int sdkVersion = context.getApplicationInfo().targetSdkVersion;
        Uri uri;
        if ((android.os.Build.VERSION.SDK_INT >= 24) && (sdkVersion >= 24))
        {
            uri = FileProvider.getUriForFile(context, new StringBuilder().append(context.getPackageName()).append(".xWanFileProvider").toString(), file);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        else {
            uri = Uri.parse(new StringBuilder().append("file://").append(file.toString()).toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void openAppDetails(Context context) {
        Log.i("openAppDetails", "openAppDetails");
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    public static void Brower(Context context, String url) {
        Log.i("Brower", url);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            ComponentName componentName = intent.resolveActivity(context.getPackageManager());
            context.startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            Toast.makeText(context.getApplicationContext(), "没有匹配的程序", 0).show();
        }
    }

    public static boolean hasSD() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            return true;
        }
        return false;
    }
}