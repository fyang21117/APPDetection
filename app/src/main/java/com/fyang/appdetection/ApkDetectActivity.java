package com.fyang.appdetection;


//import android.app.ActionBar;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

//android上所有已安装的应用都会做一个备份，分别存放在三个地方：
//
//        1、系统签名的软件：/system/app
//
//        2、安装到内存上的非系统签名软件：/data/app
//
//        3、安装到sd卡上的非系统签名软件：/mnt/asec/包名-数字/pkg.apk

public class ApkDetectActivity extends AppCompatActivity {

    public static ListAdapter listAdapter;
    public ListView appsView;
    public static PackageManager packageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apkdetection);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("用户APP列表");

        packageManager = this.getPackageManager();
        appsView = findViewById(R.id.appsView);
        appsView.setDivider(new ColorDrawable(Color.BLACK));
        appsView.setDividerHeight(5);
        loadApps();
    }

    final List<HelpItem> apkList = new ArrayList<>();

    private void loadApps() {
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> mAllPackages = packageManager.getInstalledPackages(0);
        for(int i = 0; i < mAllPackages.size(); i ++)        {
            HelpItem apkItem = new HelpItem();
            PackageInfo packageInfo = mAllPackages.get(i);
            //区分出用户APP
            if(((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)){
                String  APKName = String.valueOf(packageInfo.applicationInfo.loadLabel(packageManager));
                String  APKPath = String.valueOf(packageInfo.applicationInfo.sourceDir);

                apkItem.setAPKname(APKName);
                apkItem.setAPKpath(APKPath);
                apkItem.setImage_photo(R.mipmap.ic_launcher);

                apkList.add(apkItem);
//                Log.i("MainPath:", APKName);
//                Log.i("MainPath:", APKPath);
            }
        }

        listAdapter = new MyAdapter(apkList, ApkDetectActivity.this);
        appsView.setAdapter(listAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
            break;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }
}


