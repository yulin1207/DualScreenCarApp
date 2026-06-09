package com.example.dualscreen.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dualscreen.R;
import com.example.dualscreen.util.DisplayManagerHelper;

/**
 * 导航Activity
 * 模拟导航功能，发送导航信息到仪表屏显示
 */
public class NavigationActivity extends AppCompatActivity {
    private static final String TAG = "NavigationActivity";

    private TextView mTvNavTitle;
    private TextView mTvCurrentRoad;
    private TextView mTvNextRoad;
    private ImageView mIvTurnIcon;
    private TextView mTvTurnInfo;
    private TextView mTvDistance;
    private TextView mTvEta;

    private Handler mHandler;
    private int mNavStep = 0;

    // 模拟导航路线
    private final String[][] NAV_STEPS = {
            {"直行", "沿当前道路直行", "500米", "15分钟"},
            {"左转", "左转进入建设大道", "200米", "14分钟"},
            {"右转", "右转进入中山路口", "300米", "13分钟"},
            {"直行", "沿中山大道直行", "1.2公里", "11分钟"},
            {"掉头", "前方掉头", "150米", "10分钟"},
            {"右转", "右转进入目的地停车场", "50米", "9分钟"},
            {"到达", "已到达目的地", "0米", "0分钟"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        mHandler = new Handler(Looper.getMainLooper());

        initViews();
        initListeners();
        startNavigationSimulation();
    }

    private void initViews() {
        mTvNavTitle = findViewById(R.id.tv_nav_title);
        mTvCurrentRoad = findViewById(R.id.tv_current_road);
        mTvNextRoad = findViewById(R.id.tv_next_road);
        mIvTurnIcon = findViewById(R.id.iv_nav_turn_icon);
        mTvTurnInfo = findViewById(R.id.tv_nav_turn_info);
        mTvDistance = findViewById(R.id.tv_nav_distance);
        mTvEta = findViewById(R.id.tv_nav_eta);

        mTvNavTitle.setText("导航至: 万达广场");
        mTvCurrentRoad.setText("当前: 解放大道");
    }

    private void initListeners() {
        findViewById(R.id.btn_nav_back).setOnClickListener(v -> finish());

        findViewById(R.id.btn_nav_stop).setOnClickListener(v -> {
            stopNavigationSimulation();
            sendNavigationUpdate("", "", "");
            finish();
        });
    }

    private void startNavigationSimulation() {
        mNavStep = 0;
        updateNavigationDisplay();

        Runnable navRunnable = new Runnable() {
            @Override
            public void run() {
                mNavStep++;
                if (mNavStep >= NAV_STEPS.length) {
                    mNavStep = 0; // 循环
                }
                updateNavigationDisplay();
                mHandler.postDelayed(this, 8000); // 每8秒切换一个导航步骤
            }
        };
        mHandler.postDelayed(navRunnable, 5000);
    }

    private void updateNavigationDisplay() {
        String[] step = NAV_STEPS[mNavStep];
        String turn = step[0];
        String info = step[1];
        String distance = step[2];
        String eta = step[3];

        mTvTurnInfo.setText(info);
        mTvDistance.setText("剩余 " + distance);
        mTvEta.setText("预计 " + eta + " 到达");

        // 更新转向图标
        updateTurnIcon(turn);

        // 发送导航信息到仪表屏
        sendNavigationUpdate(info, distance, eta);
    }

    private void updateTurnIcon(String turn) {
        int iconRes;
        switch (turn) {
            case "左转":
                iconRes = R.drawable.ic_turn_left;
                break;
            case "右转":
                iconRes = R.drawable.ic_turn_right;
                break;
            case "掉头":
                iconRes = R.drawable.ic_turn_uturn;
                break;
            case "到达":
                iconRes = R.drawable.ic_destination;
                break;
            case "直行":
            default:
                iconRes = R.drawable.ic_turn_straight;
                break;
        }
        mIvTurnIcon.setImageResource(iconRes);
    }

    private void sendNavigationUpdate(String turnInfo, String distance, String eta) {
        Intent intent = new Intent(DisplayManagerHelper.ACTION_NAVIGATION_UPDATE);
        intent.putExtra(DisplayManagerHelper.EXTRA_TURN_INFO, turnInfo);
        intent.putExtra(DisplayManagerHelper.EXTRA_DISTANCE, distance);
        intent.putExtra(DisplayManagerHelper.EXTRA_ETA, eta);
        sendBroadcast(intent);
    }

    private void stopNavigationSimulation() {
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopNavigationSimulation();
    }
}
