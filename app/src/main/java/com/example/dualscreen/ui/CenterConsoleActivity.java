package com.example.dualscreen.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.dualscreen.R;
import com.example.dualscreen.util.DisplayManagerHelper;

/**
 * 中控屏主Activity
 * 负责显示导航、媒体、车辆设置等主界面，并管理仪表屏的启动
 */
public class CenterConsoleActivity extends AppCompatActivity {
    private static final String TAG = "CenterConsoleActivity";

    private DisplayManager mDisplayManager;
    private Handler mHandler;

    // UI组件
    private CardView mCardNavigation;
    private CardView mCardMedia;
    private CardView mCardVehicle;
    private CardView mCardSettings;
    private TextView mTvTime;
    private TextView mTvSpeed;
    private ImageView mIvSignal;
    private ImageView mIvWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_console);

        mHandler = new Handler(Looper.getMainLooper());
        mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);

        initViews();
        initListeners();
        startInstrumentCluster();
        startClockUpdate();

        // 注册显示设备变化监听
        mDisplayManager.registerDisplayListener(mDisplayListener, null);

        // 注册车辆数据接收器
        registerReceiver(mVehicleDataReceiver,
                new IntentFilter(DisplayManagerHelper.ACTION_VEHICLE_DATA_UPDATE));

        Log.i(TAG, "中控屏初始化完成");
    }

    private void initViews() {
        mCardNavigation = findViewById(R.id.card_navigation);
        mCardMedia = findViewById(R.id.card_media);
        mCardVehicle = findViewById(R.id.card_vehicle);
        mCardSettings = findViewById(R.id.card_settings);
        mTvTime = findViewById(R.id.tv_time);
        mTvSpeed = findViewById(R.id.tv_speed_preview);
        mIvSignal = findViewById(R.id.iv_signal);
        mIvWifi = findViewById(R.id.iv_wifi);
    }

    private void initListeners() {
        // 导航卡片点击
        mCardNavigation.setOnClickListener(v -> {
            Intent intent = new Intent(this, NavigationActivity.class);
            startActivity(intent);
        });

        // 媒体卡片点击
        mCardMedia.setOnClickListener(v -> {
            Intent intent = new Intent(this, MediaActivity.class);
            startActivity(intent);
        });

        // 车辆信息卡片点击
        mCardVehicle.setOnClickListener(v -> {
            Intent intent = new Intent(this, VehicleSettingsActivity.class);
            startActivity(intent);
        });

        // 设置卡片点击
        mCardSettings.setOnClickListener(v -> {
            Toast.makeText(this, "系统设置", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * 启动仪表屏Activity到副屏
     */
    private void startInstrumentCluster() {
        Display secondaryDisplay = DisplayManagerHelper.getSecondaryDisplay(this);
        if (secondaryDisplay != null) {
            DisplayManagerHelper.launchActivityOnDisplay(
                    this,
                    secondaryDisplay.getDisplayId(),
                    InstrumentClusterActivity.class
            );
            Toast.makeText(this, "仪表屏已启动", Toast.LENGTH_SHORT).show();
        } else {
            Log.w(TAG, "未检测到副屏，仪表屏未启动");
            Toast.makeText(this, "未检测到仪表屏", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 时钟更新
     */
    private void startClockUpdate() {
        Runnable clockRunnable = new Runnable() {
            @Override
            public void run() {
                updateClock();
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.post(clockRunnable);
    }

    private void updateClock() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
        mTvTime.setText(sdf.format(new java.util.Date()));
    }

    /**
     * 显示设备变化监听器
     */
    private final DisplayManager.DisplayListener mDisplayListener = new DisplayManager.DisplayListener() {
        @Override
        public void onDisplayAdded(int displayId) {
            Log.d(TAG, "新屏幕连接: " + displayId);
            if (displayId != Display.DEFAULT_DISPLAY) {
                runOnUiThread(() -> {
                    Toast.makeText(CenterConsoleActivity.this, "仪表屏已连接", Toast.LENGTH_SHORT).show();
                    DisplayManagerHelper.launchActivityOnDisplay(
                            CenterConsoleActivity.this,
                            displayId,
                            InstrumentClusterActivity.class
                    );
                });
            }
        }

        @Override
        public void onDisplayRemoved(int displayId) {
            Log.d(TAG, "屏幕断开: " + displayId);
            if (displayId != Display.DEFAULT_DISPLAY) {
                runOnUiThread(() -> {
                    Toast.makeText(CenterConsoleActivity.this, "仪表屏已断开", Toast.LENGTH_SHORT).show();
                });
            }
        }

        @Override
        public void onDisplayChanged(int displayId) {
            Log.d(TAG, "屏幕参数变化: " + displayId);
        }
    };

    /**
     * 车辆数据广播接收器
     */
    private final BroadcastReceiver mVehicleDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            float speed = intent.getFloatExtra(DisplayManagerHelper.EXTRA_SPEED, 0);
            mTvSpeed.setText(String.format("%.0f km/h", speed));
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisplayManager.unregisterDisplayListener(mDisplayListener);
        unregisterReceiver(mVehicleDataReceiver);
        mHandler.removeCallbacksAndMessages(null);
    }
}
