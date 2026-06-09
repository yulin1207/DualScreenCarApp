package com.example.dualscreen.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dualscreen.R;
import com.example.dualscreen.util.DisplayManagerHelper;

/**
 * 仪表屏Activity
 * 显示车速、转速、电量、水温、导航提示等驾驶信息
 */
public class InstrumentClusterActivity extends AppCompatActivity {
    private static final String TAG = "InstrumentClusterActivity";

    // UI组件 - 速度表
    private TextView mTvSpeed;
    private TextView mTvSpeedUnit;
    private ProgressBar mPbSpeed;

    // UI组件 - 转速表
    private TextView mTvRpm;
    private ProgressBar mPbRpm;

    // UI组件 - 电量
    private ProgressBar mPbBattery;
    private TextView mTvBatteryPercent;
    private ImageView mIvBatteryIcon;

    // UI组件 - 水温
    private ProgressBar mPbTemperature;
    private TextView mTvTemperature;

    // UI组件 - 档位
    private TextView mTvGear;

    // UI组件 - 导航信息
    private View mNavContainer;
    private ImageView mIvTurnIcon;
    private TextView mTvTurnInfo;
    private TextView mTvNavDistance;
    private TextView mTvNavEta;

    // UI组件 - 警告信息
    private View mWarningContainer;
    private TextView mTvWarning;
    private ImageView mIvWarning;

    // UI组件 - 里程
    private TextView mTvOdometer;
    private TextView mTvTrip;

    private Handler mHandler;
    private boolean mNavActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrument_cluster);

        mHandler = new Handler(Looper.getMainLooper());

        initViews();
        registerReceivers();
        startOdometerUpdate();

        // 保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Log.i(TAG, "仪表屏初始化完成");
    }

    private void initViews() {
        // 速度表
        mTvSpeed = findViewById(R.id.tv_speed);
        mTvSpeedUnit = findViewById(R.id.tv_speed_unit);
        mPbSpeed = findViewById(R.id.pb_speed);

        // 转速表
        mTvRpm = findViewById(R.id.tv_rpm);
        mPbRpm = findViewById(R.id.pb_rpm);

        // 电量
        mPbBattery = findViewById(R.id.pb_battery);
        mTvBatteryPercent = findViewById(R.id.tv_battery_percent);
        mIvBatteryIcon = findViewById(R.id.iv_battery_icon);

        // 水温
        mPbTemperature = findViewById(R.id.pb_temperature);
        mTvTemperature = findViewById(R.id.tv_temperature);

        // 档位
        mTvGear = findViewById(R.id.tv_gear);

        // 导航信息
        mNavContainer = findViewById(R.id.nav_container);
        mIvTurnIcon = findViewById(R.id.iv_turn_icon);
        mTvTurnInfo = findViewById(R.id.tv_turn_info);
        mTvNavDistance = findViewById(R.id.tv_nav_distance);
        mTvNavEta = findViewById(R.id.tv_nav_eta);

        // 警告信息
        mWarningContainer = findViewById(R.id.warning_container);
        mTvWarning = findViewById(R.id.tv_warning);
        mIvWarning = findViewById(R.id.iv_warning);

        // 里程
        mTvOdometer = findViewById(R.id.tv_odometer);
        mTvTrip = findViewById(R.id.tv_trip);

        // 初始状态
        mNavContainer.setVisibility(View.GONE);
        mWarningContainer.setVisibility(View.GONE);
    }

    private void registerReceivers() {
        // 注册车辆数据接收器
        registerReceiver(mVehicleDataReceiver,
                new IntentFilter(DisplayManagerHelper.ACTION_VEHICLE_DATA_UPDATE));

        // 注册导航信息接收器
        registerReceiver(mNavigationReceiver,
                new IntentFilter(DisplayManagerHelper.ACTION_NAVIGATION_UPDATE));

        // 注册媒体信息接收器
        registerReceiver(mMediaReceiver,
                new IntentFilter(DisplayManagerHelper.ACTION_MEDIA_UPDATE));
    }

    /**
     * 车辆数据广播接收器
     */
    private final BroadcastReceiver mVehicleDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            float speed = intent.getFloatExtra(DisplayManagerHelper.EXTRA_SPEED, 0);
            int rpm = intent.getIntExtra(DisplayManagerHelper.EXTRA_RPM, 0);
            float battery = intent.getFloatExtra(DisplayManagerHelper.EXTRA_BATTERY, 50);
            float temperature = intent.getFloatExtra(DisplayManagerHelper.EXTRA_TEMPERATURE, 90);
            String gear = intent.getStringExtra(DisplayManagerHelper.EXTRA_GEAR);

            updateSpeed(speed);
            updateRpm(rpm);
            updateBattery(battery);
            updateTemperature(temperature);
            updateGear(gear);
        }
    };

    /**
     * 导航信息广播接收器
     */
    private final BroadcastReceiver mNavigationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String turnInfo = intent.getStringExtra(DisplayManagerHelper.EXTRA_TURN_INFO);
            String distance = intent.getStringExtra(DisplayManagerHelper.EXTRA_DISTANCE);
            String eta = intent.getStringExtra(DisplayManagerHelper.EXTRA_ETA);

            if (turnInfo != null && !turnInfo.isEmpty()) {
                mNavActive = true;
                mNavContainer.setVisibility(View.VISIBLE);
                mTvTurnInfo.setText(turnInfo);
                mTvNavDistance.setText(distance != null ? distance : "");
                mTvNavEta.setText(eta != null ? "预计 " + eta : "");

                // 根据转向信息设置图标
                updateTurnIcon(turnInfo);
            } else {
                mNavActive = false;
                mNavContainer.setVisibility(View.GONE);
            }
        }
    };

    /**
     * 媒体信息广播接收器
     */
    private final BroadcastReceiver mMediaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String title = intent.getStringExtra(DisplayManagerHelper.EXTRA_MEDIA_TITLE);
            String artist = intent.getStringExtra(DisplayManagerHelper.EXTRA_MEDIA_ARTIST);
            // 可在仪表屏显示当前播放的媒体信息（可选）
        }
    };

    private void updateSpeed(float speed) {
        mTvSpeed.setText(String.format("%.0f", speed));
        if (mPbSpeed != null) {
            mPbSpeed.setProgress((int) speed);
        }
    }

    private void updateRpm(int rpm) {
        mTvRpm.setText(String.valueOf(rpm));
        if (mPbRpm != null) {
            mPbRpm.setProgress(rpm / 100); // 转速表最大值通常8000，ProgressBar最大80
        }
    }

    private void updateBattery(float battery) {
        if (mPbBattery != null) {
            mPbBattery.setProgress((int) battery);
        }
        if (mTvBatteryPercent != null) {
            mTvBatteryPercent.setText(String.format("%.0f%%", battery));
        }
        // 电量低于15%显示警告
        if (battery < 15) {
            showWarning("电量不足，请及时充电");
        }
    }

    private void updateTemperature(float temperature) {
        if (mPbTemperature != null) {
            mPbTemperature.setProgress((int) temperature);
        }
        if (mTvTemperature != null) {
            mTvTemperature.setText(String.format("%.0f°C", temperature));
        }
        // 水温过高警告
        if (temperature > 110) {
            showWarning("水温过高，请检查冷却系统");
        }
    }

    private void updateGear(String gear) {
        if (gear != null && mTvGear != null) {
            mTvGear.setText(gear);
        }
    }

    private void updateTurnIcon(String turnInfo) {
        if (mIvTurnIcon == null) return;

        if (turnInfo.contains("左转")) {
            mIvTurnIcon.setImageResource(R.drawable.ic_turn_left);
        } else if (turnInfo.contains("右转")) {
            mIvTurnIcon.setImageResource(R.drawable.ic_turn_right);
        } else if (turnInfo.contains("直行")) {
            mIvTurnIcon.setImageResource(R.drawable.ic_turn_straight);
        } else if (turnInfo.contains("掉头")) {
            mIvTurnIcon.setImageResource(R.drawable.ic_turn_uturn);
        } else {
            mIvTurnIcon.setImageResource(R.drawable.ic_navigation);
        }
    }

    private void showWarning(String message) {
        mWarningContainer.setVisibility(View.VISIBLE);
        mTvWarning.setText(message);

        // 3秒后自动隐藏
        mHandler.postDelayed(() -> {
            if (!isFinishing()) {
                mWarningContainer.setVisibility(View.GONE);
            }
        }, 3000);
    }

    /**
     * 模拟里程更新
     */
    private void startOdometerUpdate() {
        final float[] totalDistance = {12580.5f};
        final float[] tripDistance = {256.3f};

        Runnable odometerRunnable = new Runnable() {
            @Override
            public void run() {
                // 根据车速模拟里程增加
                // 实际应从车辆CAN总线获取
                totalDistance[0] += 0.001f;
                tripDistance[0] += 0.001f;

                mTvOdometer.setText(String.format("总里程: %.1f km", totalDistance[0]));
                mTvTrip.setText(String.format("小计: %.1f km", tripDistance[0]));

                mHandler.postDelayed(this, 5000);
            }
        };
        mHandler.post(odometerRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mVehicleDataReceiver);
            unregisterReceiver(mNavigationReceiver);
            unregisterReceiver(mMediaReceiver);
        } catch (IllegalArgumentException e) {
            // 接收器可能未注册
        }
        mHandler.removeCallbacksAndMessages(null);
    }
}
