# DualScreenCarApp - 车机多屏异显应用

## 项目概述

基于高通QCM6125 + Android 13 的车机多屏异显应用，实现中控屏和仪表屏独立运行不同应用。

## 项目结构

```
DualScreenCarApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/dualscreen/
│   │   │   ├── CarApplication.java              # 应用入口
│   │   │   ├── ui/
│   │   │   │   ├── CenterConsoleActivity.java   # 中控屏主界面
│   │   │   │   ├── InstrumentClusterActivity.java # 仪表屏界面
│   │   │   │   ├── NavigationActivity.java      # 导航界面
│   │   │   │   ├── MediaActivity.java           # 媒体播放界面
│   │   │   │   └── VehicleSettingsActivity.java # 车辆设置界面
│   │   │   ├── util/
│   │   │   │   └── DisplayManagerHelper.java    # 显示管理工具类
│   │   │   └── service/
│   │   │       ├── VehicleDataService.java      # 车辆数据模拟服务
│   │   │       └── BootReceiver.java            # 开机自启动
│   │   ├── res/
│   │   │   ├── layout/                          # 布局文件
│   │   │   ├── values/                          # 颜色、字符串、主题
│   │   │   └── drawable/                        # 图标和背景
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── README.md
```

## 核心功能

### 1. 双屏异显
- 中控屏（Display 0）：运行导航、媒体、设置等应用
- 仪表屏（Display 1）：显示车速、转速、油量、导航提示等驾驶信息
- 使用 `ActivityOptions.setLaunchDisplayId()` 实现Activity指定屏幕启动

### 2. 双屏通信
- 通过BroadcastReceiver实现跨屏数据同步
- 导航信息从中控屏发送到仪表屏显示
- 车辆数据通过广播发送到两个屏幕

### 3. 车辆数据模拟
- VehicleDataService模拟车速、转速、油量、水温等数据
- 实际项目中应替换为从CAN总线或Vehicle HAL获取真实数据

## 使用说明

### 编译安装
```bash
# 使用Android Studio或命令行编译
./gradlew assembleDebug

# 安装到设备
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 启动应用
```bash
# 启动中控屏主界面
adb shell am start -n com.example.dualscreen/.ui.CenterConsoleActivity

# 启动仪表屏（需在副屏上运行）
adb shell am start -n com.example.dualscreen/.ui.InstrumentClusterActivity --display 1
```

### 查看显示设备
```bash
adb shell dumpsys display
```

## 适配说明

### 系统要求
- Android 7.0+ (API 24+)
- 支持多屏显示的硬件平台（如高通QCM6125）
- 需要 `SYSTEM_ALERT_WINDOW` 权限或系统签名

### 硬件接口
- 主屏：MIPI_DSI 接口
- 副屏：DP1.4 over USB Type-C 接口

## 注意事项

1. **权限要求**：多屏异显需要系统级权限，建议作为系统应用安装
2. **生命周期管理**：正确处理屏幕热插拔和Activity重建
3. **性能优化**：双屏同时渲染对GPU有较高要求
4. **安全考虑**：仪表屏显示内容需符合车规安全标准
