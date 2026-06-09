package com.example.dualscreen.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.example.dualscreen.util.DisplayManagerHelper;

/**
 * 车辆数据模拟服务
 * 模拟生成车速、转速、电量、水温等车辆数据，并通过广播发送
 * 实际项目中应替换为从CAN总线或Vehicle HAL获取真实数据
 */
public class VehicleDataService extends Service {
    private static final String TAG = "VehicleDataService";

    // 模拟数据
    private float mSpeed = 0f;
    private int mRpm = 0;
    private float mBattery = 65f;
    private float mTemperature = 88f;
    private String mGear = "P";

    private Handler mHandler;
    private boolean mIsRunning = false;

    // 模拟状态
    private boolean mAccelerating = false;
    private int mSimulationTick = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(Looper.getMainLooper());
        Log.i(TAG, "车辆数据服务已创建");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mIsRunning) {
            mIsRunning = true;
            startDataSimulation();
            Log.i(TAG, "车辆数据模拟已启动");
        }
        return START_STICKY;
    }

    /**
     * 启动数据模拟循环
     */
    private void startDataSimulation() {
        Runnable simulationRunnable = new Runnable() {
            @Override
            public void run() {
                simulateVehicleData();
                broadcastVehicleData();
                mHandler.postDelayed(this, 200); // 200ms更新一次（5Hz）
            }
        };
        mHandler.post(simulationRunnable);
    }

    /**
     * 模拟车辆数据变化
     */
    private void simulateVehicleData() {
        mSimulationTick++;

        // 模拟加速/减速循环
        if (mSimulationTick % 300 == 0) {
            mAccelerating = !mAccelerating;
        }

        // 模拟车速变化
        if (mAccelerating) {
            mSpeed += (float) (Math.random() * 3);
            if (mSpeed > 120) mSpeed = 120;
        } else {
            mSpeed -= (float) (Math.random() * 2);
            if (mSpeed < 0) mSpeed = 0;
        }

        // 模拟转速（与车速相关）
        if (mGear.equals("P") || mGear.equals("N")) {
            mRpm = 800 + (int) (Math.random() * 100); // 怠速
        } else {
            mRpm = (int) (mSpeed * 40 + 800 + Math.random() * 200);
            if (mRpm > 6000) mRpm = 6000;
        }

        // 模拟档位变化
        if (mSpeed < 1) {
            mGear = "P";
        } else if (mSpeed < 5) {
            mGear = "D1";
        } else if (mSpeed < 20) {
            mGear = "D2";
        } else if (mSpeed < 40) {
            mGear = "D3";
        } else if (mSpeed < 60) {
            mGear = "D4";
        } else if (mSpeed < 90) {
            mGear = "D5";
        } else {
            mGear = "D6";
        }

        // 模拟电量缓慢下降
        if (mSimulationTick % 500 == 0) {
            mBattery -= 0.1f;
            if (mBattery < 5) mBattery = 65; // 循环重置
        }

        // 模拟水温波动
        mTemperature = 88f + (float) (Math.random() * 4 - 2);
        if (mTemperature > 105) mTemperature = 105;
        if (mTemperature < 80) mTemperature = 80;
    }

    /**
     * 广播车辆数据
     */
    private void broadcastVehicleData() {
        Intent intent = new Intent(DisplayManagerHelper.ACTION_VEHICLE_DATA_UPDATE);
        intent.putExtra(DisplayManagerHelper.EXTRA_SPEED, mSpeed);
        intent.putExtra(DisplayManagerHelper.EXTRA_RPM, mRpm);
        intent.putExtra(DisplayManagerHelper.EXTRA_BATTERY, mBattery);
        intent.putExtra(DisplayManagerHelper.EXTRA_TEMPERATURE, mTemperature);
        intent.putExtra(DisplayManagerHelper.EXTRA_GEAR, mGear);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsRunning = false;
        mHandler.removeCallbacksAndMessages(null);
        Log.i(TAG, "车辆数据服务已销毁");
    }
}
