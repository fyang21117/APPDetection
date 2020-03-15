package com.fyang.appdetection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fyang.appdetection.utils.ConstantValue;
import com.fyang.appdetection.utils.Md5Util;
import com.fyang.appdetection.utils.SpUtil;
import com.fyang.appdetection.utils.ToastUtil;

public class HomeActivity extends Activity {
    private GridView gv_home;
    private String[] mTitleStrs;
    private int[] mDrawableIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.i("HomeActivity", "HomeActivity started!");

        initUI();
        //初始化数据的方法
        initData();

    }

    protected void showDialog() {
        String password = SpUtil.getString(this, ConstantValue.MOBILE_SAFE_PSD, "");

        if (TextUtils.isEmpty(password)) {
            //1,初始设置密码对话框
            //showSetPsdDialog();
        } else {
            //2,确认密码对话框
            //showConfirmPsdDialog();
        }
    }

    /**
     * 确认密码对话框
     */
    private void showConfirmPsdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final AlertDialog alertDialog = builder.create();

        final View inflate = View.inflate(this, R.layout.dialog_confirm_psd, null);
        alertDialog.setView(inflate, 0, 0, 0, 0);
        alertDialog.show();

        Button bt_submit = (Button) inflate.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) inflate.findViewById(R.id.bt_cancel);


        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_confirm_psd = (EditText) inflate.findViewById(R.id.et_confirm_psd);

                String confirmPsd = et_confirm_psd.getText().toString();

                if (!TextUtils.isEmpty(confirmPsd)) {
                    String psd = SpUtil.getString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, "");
                    confirmPsd = Md5Util.encoder(confirmPsd);
                    if (confirmPsd.equals(psd)) {
                        //进入应用手机防盗模块,开启一个新的activity
                        //Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        //startActivity(intent);
                        //跳转到新的界面以后需要去隐藏对话框
                        alertDialog.dismiss();
                    } else {
                        ToastUtil.show(getApplicationContext(), "密码错误");
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入密码");
                }
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    /**
     * 设置密码对话框
     */
    private void showSetPsdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.create();
        final View inflate = View.inflate(this, R.layout.dialog_set_psd, null);
        alertDialog.setView(inflate, 0, 0, 0, 0);

        alertDialog.show();

        Button bt_submit = (Button) inflate.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) inflate.findViewById(R.id.bt_cancel);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_set_psd = (EditText) inflate.findViewById(R.id.et_set_psd);
                EditText et_confirm_psd = (EditText) inflate.findViewById(R.id.et_confirm_psd);

                String psd = et_set_psd.getText().toString();
                String confirmPsd = et_confirm_psd.getText().toString();

                if (!TextUtils.isEmpty(psd) && !TextUtils.isEmpty(confirmPsd)) {
                    if (psd.equals(confirmPsd)) {
                       // Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        //startActivity(intent);
                        alertDialog.dismiss();
                        SpUtil.putString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, Md5Util.encoder(psd));
                    } else {
                        ToastUtil.show(getApplicationContext(), "确认密码错误");
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入密码");
                }

            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void initData() {
        mTitleStrs = new String[]{
                 "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具","APK检测"
        };

        mDrawableIds = new int[]{
                R.drawable.home_apps, R.drawable.home_taskmanager,
                R.drawable.home_netmanager,R.drawable.home_trojan,
                R.drawable.home_sysoptimize, R.drawable.home_tools,
                R.drawable.home_callmsgsafe
        };
        //九宫格控件设置数据适配器(等同ListView数据适配器)
        gv_home.setAdapter(new MyAdapter());
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(getApplicationContext(), AppManagerActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(getApplicationContext(), ProcessManagerActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(getApplicationContext(), TrafficActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(getApplicationContext(), AnitVirusActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(getApplicationContext(), BaseCacheClearActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(getApplicationContext(), AToolActivity.class));
                        break;
                    case 6:
                        startActivity(new Intent(getApplicationContext(), ApkDetectActivity.class));
                        break;
                }
            }
        });
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mTitleStrs.length;
        }

        @Override
        public Object getItem(int position) {
            return mTitleStrs[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View inflate = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
            TextView tv_title = (TextView) inflate.findViewById(R.id.tv_title);
            ImageView iv_icon = (ImageView) inflate.findViewById(R.id.iv_icon);

            tv_title.setText(mTitleStrs[position]);
            iv_icon.setBackgroundResource(mDrawableIds[position]);
            return inflate;
        }
    }

    private void initUI() {
        gv_home = (GridView) findViewById(R.id.gv_home);
    }
}
