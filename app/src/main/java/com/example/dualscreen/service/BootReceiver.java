package com.example.dualscreen.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.dualscreen.ui.CenterConsoleActivity;

/**
 * 开机自启动接收器
 * 设备开机后自动启动中控屏主界面
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.i(TAG, "收到开机广播，启动车机应用");

            // 启动车辆数据服务
            Intent serviceIntent = new Intent(context, VehicleDataService.class);
            context.startService(serviceIntent);

            // 启动中控屏主Activity
            Intent activityIntent = new Intent(context, CenterConsoleActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
        }
    }
}
