package com.example.dualscreen.util;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.util.Log;
import android.view.Display;

/**
 * 显示设备管理工具类
 * 用于获取和管理多屏显示设备
 */
public class DisplayManagerHelper {
    private static final String TAG = "DisplayManagerHelper";

    // 广播Action定义
    public static final String ACTION_NAVIGATION_UPDATE = "com.example.dualscreen.NAVIGATION_UPDATE";
    public static final String ACTION_VEHICLE_DATA_UPDATE = "com.example.dualscreen.VEHICLE_DATA_UPDATE";
    public static final String ACTION_MEDIA_UPDATE = "com.example.dualscreen.MEDIA_UPDATE";

    // Extra键名
    public static final String EXTRA_TURN_INFO = "turn_info";
    public static final String EXTRA_DISTANCE = "distance";
    public static final String EXTRA_ETA = "eta";
    public static final String EXTRA_SPEED = "speed";
    public static final String EXTRA_RPM = "rpm";
    public static final String EXTRA_BATTERY = "battery";
    public static final String EXTRA_TEMPERATURE = "temperature";
    public static final String EXTRA_GEAR = "gear";
    public static final String EXTRA_MEDIA_TITLE = "media_title";
    public static final String EXTRA_MEDIA_ARTIST = "media_artist";
    public static final String EXTRA_MEDIA_PROGRESS = "media_progress";

    private DisplayManagerHelper() {
        // 工具类，禁止实例化
    }

    /**
     * 获取所有可用显示设备
     */
    public static Display[] getAllDisplays(Context context) {
        DisplayManager displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        if (displayManager != null) {
            Display[] displays = displayManager.getDisplays();
            Log.d(TAG, "系统共有 " + displays.length + " 个显示设备");
            for (Display display : displays) {
                Log.d(TAG, "  Display[" + display.getDisplayId() + "]: " +
                        display.getWidth() + "x" + display.getHeight() +
                        ", DPI=" + display.getRefreshRate() + "Hz");
            }
            return displays;
        }
        return new Display[0];
    }

    /**
     * 获取主屏（默认显示设备）
     */
    public static Display getPrimaryDisplay(Context context) {
        DisplayManager displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        if (displayManager != null) {
            return displayManager.getDisplay(Display.DEFAULT_DISPLAY);
        }
        return null;
    }

    /**
     * 获取副屏（非主屏的第一个可用屏幕）
     */
    public static Display getSecondaryDisplay(Context context) {
        Display[] displays = getAllDisplays(context);
        for (Display display : displays) {
            if (display.getDisplayId() != Display.DEFAULT_DISPLAY) {
                Log.i(TAG, "找到副屏: ID=" + display.getDisplayId() +
                        ", 分辨率=" + display.getWidth() + "x" + display.getHeight());
                return display;
            }
        }
        Log.w(TAG, "未检测到副屏");
        return null;
    }

    /**
     * 检查是否存在副屏
     */
    public static boolean hasSecondaryDisplay(Context context) {
        return getSecondaryDisplay(context) != null;
    }

    /**
     * 在指定屏幕上启动Activity
     *
     * @param context       上下文
     * @param displayId     目标屏幕ID
     * @param activityClass 要启动的Activity类
     */
    public static void launchActivityOnDisplay(Context context, int displayId, Class<?> activityClass) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                ActivityOptions options = ActivityOptions.makeBasic();
                options.setLaunchDisplayId(displayId);

                Intent intent = new Intent(context, activityClass);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                context.startActivity(intent, options.toBundle());
                Log.i(TAG, "已在屏幕 " + displayId + " 上启动 " + activityClass.getSimpleName());
            } catch (Exception e) {
                Log.e(TAG, "启动Activity到屏幕 " + displayId + " 失败: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "Android版本过低（需API 24+），不支持多屏启动");
        }
    }

    /**
     * 在副屏上启动Activity（便捷方法）
     */
    public static void launchActivityOnSecondaryDisplay(Context context, Class<?> activityClass) {
        Display secondaryDisplay = getSecondaryDisplay(context);
        if (secondaryDisplay != null) {
            launchActivityOnDisplay(context, secondaryDisplay.getDisplayId(), activityClass);
        } else {
            Log.w(TAG, "未找到副屏，无法启动 " + activityClass.getSimpleName());
        }
    }

    /**
     * 获取屏幕信息描述
     */
    public static String getDisplayInfo(Display display) {
        if (display == null) return "Display is null";
        return "Display[" + display.getDisplayId() + "]: " +
                display.getWidth() + "x" + display.getHeight() +
                ", RefreshRate=" + display.getRefreshRate() + "Hz" +
                ", DensityDPI=" + display.getMetrics().densityDpi;
    }
}
