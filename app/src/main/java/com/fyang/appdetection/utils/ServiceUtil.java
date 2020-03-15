package com.fyang.appdetection.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class ServiceUtil {
    private static final String tag = "ServiceUtil";

    /**
     * @param ctx         上下文环境
     * @param serviceName 判断是否正在运行的服务
     * @return true 运行	false 没有运行
     */
    public static boolean isRunning(Context ctx, String serviceName) {
        //1,获取activityMananger管理者对象,可以去获取当前手机正在运行的所有服务
        ActivityManager mAM = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        //2,获取手机中正在运行的服务集合(多少个服务)
        List<ActivityManager.RunningServiceInfo> runningServices = mAM.getRunningServices(1000);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            //4,获取每一个真正运行服务的名称
            Log.i(tag, "runningServiceInfo.service.getClassName() = "+runningServiceInfo.service.getClassName());

            if (serviceName.equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
