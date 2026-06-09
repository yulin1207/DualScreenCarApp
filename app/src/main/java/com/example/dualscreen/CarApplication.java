package com.example.dualscreen;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.example.dualscreen.service.VehicleDataService;

/**
 * 应用入口类
 * 负责初始化全局配置和启动后台服务
 */
public class CarApplication extends Application {
    private static final String TAG = "CarApplication";
    private static CarApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Log.i(TAG, "车机多屏异显应用启动");

        // 启动车辆数据模拟服务
        startVehicleDataService();
    }

    public static CarApplication getInstance() {
        return sInstance;
    }

    private void startVehicleDataService() {
        Intent intent = new Intent(this, VehicleDataService.class);
        startService(intent);
        Log.i(TAG, "车辆数据服务已启动");
    }
}
