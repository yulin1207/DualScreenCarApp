package com.example.dualscreen.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dualscreen.R;

/**
 * 车辆设置Activity
 * 显示和控制车辆相关设置
 */
public class VehicleSettingsActivity extends AppCompatActivity {

    private Switch mSwitchAutoLight;
    private Switch mSwitchAutoWiper;
    private Switch mSwitchLaneAssist;
    private Switch mSwitchAutoHold;
    private SeekBar mSeekBarBrightness;
    private TextView mTvBrightnessValue;
    private SeekBar mSeekBarVolume;
    private TextView mTvVolumeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_settings);

        initViews();
        initListeners();
    }

    private void initViews() {
        mSwitchAutoLight = findViewById(R.id.switch_auto_light);
        mSwitchAutoWiper = findViewById(R.id.switch_auto_wiper);
        mSwitchLaneAssist = findViewById(R.id.switch_lane_assist);
        mSwitchAutoHold = findViewById(R.id.switch_auto_hold);
        mSeekBarBrightness = findViewById(R.id.seek_bar_brightness);
        mTvBrightnessValue = findViewById(R.id.tv_brightness_value);
        mSeekBarVolume = findViewById(R.id.seek_bar_volume);
        mTvVolumeValue = findViewById(R.id.tv_volume_value);

        // 设置初始值
        mSwitchAutoLight.setChecked(true);
        mSwitchAutoWiper.setChecked(false);
        mSwitchLaneAssist.setChecked(true);
        mSwitchAutoHold.setChecked(false);
        mSeekBarBrightness.setProgress(80);
        mTvBrightnessValue.setText("80%");
        mSeekBarVolume.setProgress(60);
        mTvVolumeValue.setText("60%");
    }

    private void initListeners() {
        findViewById(R.id.btn_settings_back).setOnClickListener(v -> finish());

        mSwitchAutoLight.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showToast("自动大灯 " + (isChecked ? "开启" : "关闭"));
        });

        mSwitchAutoWiper.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showToast("自动雨刷 " + (isChecked ? "开启" : "关闭"));
        });

        mSwitchLaneAssist.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showToast("车道保持 " + (isChecked ? "开启" : "关闭"));
        });

        mSwitchAutoHold.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showToast("自动驻车 " + (isChecked ? "开启" : "关闭"));
        });

        mSeekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTvBrightnessValue.setText(progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                showToast("屏幕亮度已调整");
            }
        });

        mSeekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTvVolumeValue.setText(progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                showToast("音量已调整");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
