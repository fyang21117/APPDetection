package com.fyang.appdetection;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ApkDetectActivity extends AppCompatActivity {

    public static ListAdapter listAdapter;
    public ListView appsView;
    public static Intent actIntent;
    public static PackageManager packageManager;
    final List<HelpItem> appNamesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apkdetection);
        packageManager = this.getPackageManager();
        appsView = findViewById(R.id.appsView);
        loadApps();
    }


    private void loadApps() {
        appsView = findViewById(R.id.appsView);
        appsView.setDivider(new ColorDrawable(Color.BLACK));
        appsView.setDividerHeight(5);
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = getPackageManager().queryIntentActivities(intent, 0);
        for (int i = 0; i < apps.size(); i++) {
            HelpItem helpItem = new HelpItem();
            ResolveInfo info = apps.get(i);
            String packageName = info.activityInfo.packageName;
            CharSequence Actname = info.activityInfo.name;
            CharSequence Appname = info.activityInfo.loadLabel(getPackageManager());

            helpItem.setPackagename(packageName);
            helpItem.setActname(Actname);
            helpItem.setAppname(Appname);
            helpItem.setImage_photo(R.mipmap.ic_launcher);
            appNamesList.add(helpItem);
        }
        listAdapter = new MyAdapter(appNamesList, ApkDetectActivity.this);
        appsView.setAdapter(listAdapter);
    }
}
