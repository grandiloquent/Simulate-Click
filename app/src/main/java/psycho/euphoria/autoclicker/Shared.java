package psycho.euphoria.autoclicker;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityService.GestureResultCallback;
import android.accessibilityservice.GestureDescription;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;

public class Shared {
    // 操作成功
    public static final int SUCCESS = 0;
    // 换行符
    private static final String LINE_SEP = System.getProperty("line.separator");

    public static LayoutParams createOverlayLayoutParams() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.TYPE_APPLICATION_OVERLAY,
                    LayoutParams.FLAG_NOT_FOCUSABLE |
                            LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                            LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                            LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT);
        } else {
            return new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    LayoutParams.FLAG_NOT_FOCUSABLE |
                            LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                            LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                            LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT);
        }
    }

    public static void createOverlayView(Activity context, LayoutParams layoutParams, View view) {
        context.getWindowManager().addView(view, layoutParams);
    }

    public static CommandResult execCmd(final String[] commands, final boolean isRoot, final boolean isNeedResultMsg) {
        int result = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(result, null, null);
        }
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? "su" : "sh");
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) continue;
                os.write(command.getBytes());
                os.writeBytes(LINE_SEP);
                os.flush();
            }
            os.writeBytes("exit" + LINE_SEP);
            os.flush();
            result = process.waitFor();
            if (isNeedResultMsg) {
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
                errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));
                String line;
                if ((line = successResult.readLine()) != null) {
                    successMsg.append(line);
                    while ((line = successResult.readLine()) != null) {
                        successMsg.append(LINE_SEP).append(line);
                    }
                }
                if ((line = errorResult.readLine()) != null) {
                    errorMsg.append(line);
                    while ((line = errorResult.readLine()) != null) {
                        errorMsg.append(LINE_SEP).append(line);
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                os.close();
                successResult.close();
                errorResult.close();
            } catch (Exception e) {
            }
            if (process != null) {
                process.destroy();
            }
        }
        return new CommandResult(
                result,
                successMsg == null ? null : successMsg.toString(),
                errorMsg == null ? null : errorMsg.toString());
    }

    public static CommandResult execCmd(final String[] commands, final boolean isRoot) {
        return execCmd(commands, isRoot, true);
    }

    public static CommandResult execCmd(final String command, final boolean isRoot) {
        return execCmd(new String[]{command}, isRoot, true);
    }

    public static boolean isAccessibilitySettingsOn(Context context, Class<? extends android.accessibilityservice.AccessibilityService> clazz) {
        int accessibilityEnabled = 0;
        final String service = context.getPackageName() + "/" + clazz.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void longTapAt(float x, float y, AccessibilityService service) {
        Path clickPath = new Path();
        clickPath.moveTo(x, y);
        GestureDescription.StrokeDescription clickStroke = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            clickStroke = new GestureDescription.StrokeDescription(clickPath, 0, 1, true);
        }
        GestureDescription.Builder clickBuilder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            clickBuilder = new GestureDescription.Builder();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            clickBuilder.addStroke(clickStroke);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            service.dispatchGesture(clickBuilder.build(), null, null);
        }
    }

    public static void requestAccessibilityPermission(Context context) {
        if (!isAccessibilitySettingsOn(context, AutoService.class)) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            context.startActivity(intent);
        }

    }

    public static void requestAccessibilityPermission(Context ct, Class service) {
        String cmd1 = "settings put secure enabled_accessibility_services  " + ct.getPackageName() + "/" + service.getName();
        String cmd2 = "settings put secure accessibility_enabled 1";
        String[] cmds = new String[]{cmd1, cmd2};
        execCmd(cmds, true);
    }

    @TargetApi(VERSION_CODES.M)
    public static void requestOverlayPermission(Context context) {
        // <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
        if (!Settings.canDrawOverlays(context)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }

    public static void singleTapAt(float x, float y, AccessibilityService service) {
        Path clickPath = new Path();
        GestureDescription.Builder clickBuilder = new GestureDescription.Builder();
        clickPath.moveTo(x, y);

        clickBuilder.addStroke(
               new GestureDescription.StrokeDescription(
                        clickPath,
                        0,500));

        service.dispatchGesture(clickBuilder.build(),null,null);

    }

    public static void swipe(float x, float y, float toX, float toY, AccessibilityService service) {
        Path touchPath = new Path();
        touchPath.moveTo(x, y);
        touchPath.lineTo(toX, toY);
        GestureDescription.Builder gestureBuilder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            gestureBuilder = new GestureDescription.Builder();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            gestureBuilder.addStroke(new GestureDescription.StrokeDescription(touchPath, 0, 500));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            service.dispatchGesture(gestureBuilder.build(), null, null);
        }
    }

    public static class CommandResult {

        // 结果码
        public int result;
        // 成功信息
        public String successMsg;
        // 错误信息
        public String errorMsg;

        public CommandResult(final int result, final String successMsg, final String errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }

        /**
         * 判断是否执行成功
         *
         * @return
         */
        public boolean isSuccess() {
            return result == SUCCESS;
        }

        /**
         * 判断是否执行成功(判断 errorMsg)
         *
         * @return
         */
        public boolean isSuccess2() {
            if (result == SUCCESS && (errorMsg == null || errorMsg.length() == 0)) {
                return true;
            }
            return false;
        }

        /**
         * 判断是否执行成功(判断 successMsg)
         *
         * @return
         */
        public boolean isSuccess3() {
            if (result == SUCCESS && successMsg != null && successMsg.length() != 0) {
                return true;
            }
            return false;
        }

        /**
         * 判断是否执行成功(判断 successMsg) , 并且 successMsg 是否包含某个字符串
         *
         * @param contains
         * @return
         */
        public boolean isSuccess4(final String contains) {
            if (result == SUCCESS && successMsg != null && successMsg.length() != 0) {
                if (contains != null && contains.length() != 0 && successMsg.toLowerCase().contains(contains)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean isDeviceRooted() {
        String su = "su";
        String[] locations = {"/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
                "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/"};
        for (String location : locations) {
            if (new File(location + su).exists()) {
                return true;
            }
        }
        return false;
    }

    public static void requestRoot() {
        execCmd("exit", true);
    }
}