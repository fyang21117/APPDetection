package com.fyang.appdetection;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fyang.appdetection.utils.ConstantValue;
import com.fyang.appdetection.utils.SpUtil;
import com.fyang.appdetection.utils.StreamUtil;
import com.fyang.appdetection.utils.ToastUtil;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    protected static final String tag = "MainActivity";
    //更新版本状态码
    protected static final int UPDATE_VERSION = 100;
    //进入主界面状态码
    protected static final int ENTER_HOME = 101;
    //url错误状态码
    protected static final int URL_ERROR = 401;

    private TextView tx_version_name;
    private RelativeLayout rl_root;

    private int mLocalVersionCode;

    private String versionDes;
    private String versionCode;
    private String downloadUrl;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION:
                    //弹出对话框,提示用户更新
                    showUpdateDialog();
                    break;
                case ENTER_HOME:
                    enterHome();
                    break;
                case URL_ERROR:
                    ToastUtil.show(getApplicationContext(), "网络错误");
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("test:", "fujie2");
        initUI();
        initData();
        //初始化动画
        initAnimation();

        //初始化数据库
        initDB();
        if(!SpUtil.getBoolean(this, ConstantValue.HAS_SHORTCUT, false)){
            //生成快捷方式
            initShortCut();
        }
    }

    /**
     * 生成快捷方式
     */
    private void initShortCut() {
        //1,给intent维护图标,名称
        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //维护图标
        //intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        //名称
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "黑马卫士");
        //2,点击快捷方式后跳转到的activity
        //2.1维护开启的意图对象
        Intent shortCutIntent = new Intent("android.intent.action.HOME");
        shortCutIntent.addCategory("android.intent.category.DEFAULT");

        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
        //3,发送广播
        sendBroadcast(intent);
        //4,告知sp已经生成快捷方式
        SpUtil.putBoolean(this, ConstantValue.HAS_SHORTCUT, true);
    }

    private void initDB() {
        //1,归属地数据拷贝过程
        initAddressDB("address.db");
        //2,常用号码数据库拷贝过程
        initAddressDB("commonnum.db");
        //3,拷贝病毒数据库
        initAddressDB("antivirus.db");
    }

    /**
     * 拷贝数据库值files文件夹下
     *
     * @param dbName 数据库名称
     */
    private void initAddressDB(String dbName) {
        //1,在files文件夹下创建同名dbName数据库文件过程
        File files = getFilesDir();
        File file = new File(files, dbName);
        if (file.exists()) {
            return;
        }
        InputStream stream = null;
        FileOutputStream fos = null;
        //2,输入流读取第三方资产目录下的文件
        try {
            stream = getAssets().open(dbName);
            //3,将读取的内容写入到指定文件夹的文件中去
            fos = new FileOutputStream(file);
            //4,每次的读取内容大小
            byte[] bs = new byte[1024];
            int temp = -1;
            while ((temp = stream.read(bs)) != -1) {
                fos.write(bs, 0, temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null && fos != null) {
                try {
                    stream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 添加淡入动画效果
     */
    private void initAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(2000);
        rl_root.startAnimation(alphaAnimation);
    }

    /**
     * 弹出对话框,提示用户更新
     */
    protected void showUpdateDialog() {
        //这里使用Activity的上文 不能使用Application的上下文
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("版本更新");
        builder.setMessage(versionDes);

        builder.setPositiveButton("稍后更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });

        builder.setNegativeButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //下载apk,apk链接地址,downloadUrl
                downloadApk();
            }
        });

        //点击取消事件监听
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //即使用户点击取消,也需要让其进入应用程序主界面
                enterHome();
                dialog.dismiss();
            }
        });

        builder.show();
    }

    protected void downloadApk() {
        ToastUtil.show(getApplicationContext(), "更新中...");
        //apk下载链接地址,放置apk的所在路径

        //1,判断sd卡是否可用,是否挂在上
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //得到sd卡路径
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mobilesafe.apk";
            RequestParams requestParams = new RequestParams(downloadUrl);
            // 为RequestParams设置文件下载后的保存路径
            requestParams.setSaveFilePath(path);
            // 下载完成后自动为文件命名
            requestParams.setAutoRename(true);

            x.http().get(requestParams, new Callback.ProgressCallback<File>() {
                @Override
                public void onSuccess(File result) {
                    Log.i(tag, "下载成功");
                    installApk(result);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.i(tag, "下载错误");
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    Log.i(tag, "取消下载");
                }

                @Override
                public void onFinished() {
                    Log.i(tag, "下载完成");
                }

                @Override
                public void onWaiting() {
                    Log.i(tag, "等待下载");
                }

                @Override
                public void onStarted() {
                    Log.i(tag, "开始下载");
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    Log.i(tag, "正在下载中......");
                    Log.i(tag, "下载中........");
                    Log.i(tag, "total = " + total);
                    Log.i(tag, "current = " + current);
                }
            });

        }
    }


    /**
     * 安装对应apk
     *
     * @param file 安装文件
     */
    protected void installApk(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        tx_version_name = (TextView) findViewById(R.id.tv_version_name);
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
    }

    /**
     * 进入主界面
     */
    protected void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        String versionName = getVersionName();
        tx_version_name.setText(versionName);

        //本地版本号
        mLocalVersionCode = getVersionCode();
        //3,获取服务器版本号(客户端发请求,服务端给响应,(json,xml))
        //http://www.oxxx.com/update74.json?key=value  返回200 请求成功,流的方式将数据读取下来
        //json中内容包含:
        /* 更新版本的版本名称
         * 新版本的描述信息
         * 服务器版本号
         * 新版本apk下载地址*/
        //判断设置信息
        if (SpUtil.getBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE, false)) {
            checkVersion();
        } else {
            handler.sendEmptyMessageDelayed(ENTER_HOME, 2000);
        }

    }

    /**
     * 检测版本号
     */
    private void checkVersion() {
        new Thread() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                Message msg = Message.obtain();
                try {
                    URL url = new URL("https://qutu02.com/app/game_open/debug/testJson");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setConnectTimeout(2000);
                    urlConnection.setReadTimeout(2000);

                    //默认就是get请求方式,
//					connection.setRequestMethod("POST");

                    if (urlConnection.getResponseCode() == 200) {
                        InputStream inputStream = urlConnection.getInputStream();
                        String requestJson = StreamUtil.streamToString(inputStream);
                        Log.i(tag, requestJson);

                        JSONObject jsonObject = new JSONObject(requestJson);
                        String versionName = jsonObject.getString("versionName");
                        versionDes = jsonObject.getString("versionDes");
                        versionCode = jsonObject.getString("versionCode");
                        downloadUrl = jsonObject.getString("downloadUrl");
                        Log.i(tag, versionName);
                        Log.i(tag, versionDes);
                        Log.i(tag, versionCode);
                        Log.i(tag, downloadUrl);

                        if (mLocalVersionCode < Integer.parseInt(versionCode)) {
                            msg.what = UPDATE_VERSION;
                        } else {
                            msg.what = ENTER_HOME;
                        }
                    }

                } catch (Exception e) {
                    msg.what = URL_ERROR;
                    e.printStackTrace();
                } finally {
                    //时间延时
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime < 2000) {
                        try {
                            Thread.sleep(2000 - (endTime - startTime));
                        } catch (Exception time_e) {
                            time_e.printStackTrace();
                        }
                    }
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 获取应用版本名称
     *
     * @return 应用版本名称 null 异常
     */
    private String getVersionName() {
        //得到包管理者
        PackageManager packageManager = getPackageManager();
        //获取包名信息
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            //获取版本
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用版本号
     *
     * @return 应用版本名称 0 异常
     */
    private int getVersionCode() {
        //得到包管理者
        PackageManager packageManager = getPackageManager();
        //获取包名信息
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            //获取版本
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
