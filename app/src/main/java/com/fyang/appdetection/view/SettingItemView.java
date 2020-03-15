package com.fyang.appdetection.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fyang.appdetection.R;


public class SettingItemView extends RelativeLayout {

    private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.fj.mobilesafe";
    private static final String tag = "SettingItemView";
    private CheckBox cb_box;
    private TextView tv_des;
    private String mDestitle;
    private String mDesoff;
    private String mDeson;

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View inflate = View.inflate(context, R.layout.setting_item_view, this);

        //自定义组合控件中的标题描述
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_des = (TextView) findViewById(R.id.tv_des);
        cb_box = (CheckBox) findViewById(R.id.cb_box);

        //获取自定义以及原生属性的操作,写在此处,AttributeSet attrs对象中获取
        initAttrs(attrs);
        //获取布局文件中定义的字符串,赋值给自定义组合控件的标题
        tv_title.setText(mDestitle);
    }

    /**
     * 返回属性集合中自定义属性属性值
     *
     * @param attrs 构造方法中维护好的属性集合
     */
    private void initAttrs(AttributeSet attrs) {
//        for (int i = 0; i < attrs.getAttributeCount(); i++) {
//            Log.i(tag, "name = " + attrs.getAttributeName(i));
//            Log.i(tag, "value = " + attrs.getAttributeValue(i));
//            Log.i(tag, "分割线 ================================= ");
//        }

        mDestitle = attrs.getAttributeValue(NAMESPACE, "destitle");
        mDesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
        mDeson = attrs.getAttributeValue(NAMESPACE, "deson");

    }


    /**
     * 判断是否开启的方法
     *
     * @return 返回当前SettingItemView是否选中状态    true开启(checkBox返回true)	false关闭(checkBox返回true)
     */
    public boolean isCheck() {
        //由checkBox的选中结果,决定当前条目是否开启
        return cb_box.isChecked();
    }

    /**
     * @param isCheck 是否作为开启的变量,由点击过程中去做传递
     */
    public void setCheck(boolean isCheck) {
        //当前条目在选择的过程中,cb_box选中状态也在跟随(isCheck)变化
        cb_box.setChecked(isCheck);
        if (isCheck) {
            //开启
            tv_des.setText("开启");
        } else {
            //关闭
            tv_des.setText("关闭");
        }
    }
}
