package psycho.euphoria.autoclicker;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityService.GestureResultCallback;
import android.accessibilityservice.GestureDescription;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityManager;

import java.util.Random;

public class ClickUtils {

    @TargetApi(Build.VERSION_CODES.N)
    public void click(AccessibilityService accessibilityService, int x, int y) {
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path p = new Path();
        p.moveTo(x, y);
        builder.addStroke(new GestureDescription.StrokeDescription(p, 0, ViewConfiguration.getTapTimeout()));
        GestureDescription gesture = builder.build();
        boolean isDispatched = accessibilityService.dispatchGesture(gesture, new GestureResultCallback() {
            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
            }

            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
            }
        }, null);
    }

    public static void watchVideos(AccessibilityService accessibilityService) {
        new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                swipe(accessibilityService, getRandomNumber(300, 340), ClickUtils.getRandomNumber(1380, 1580), ClickUtils.getRandomNumber(340, 380), ClickUtils.getRandomNumber(380, 680));
                try {
                    Thread.sleep(ClickUtils.getRandomNumber(1, 10) * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public static void watchBooks(AccessibilityService accessibilityService) {
        new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                swipe(accessibilityService,
                        getRandomNumber(900, 980), getRandomNumber(1350, 1450),
                        getRandomNumber(200, 280), getRandomNumber(1250, 1400));
                try {
                    Thread.sleep(ClickUtils.getRandomNumber(5, 10) * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public static void watchSpecifyVideos(AccessibilityService accessibilityService) {
        new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                swipe(accessibilityService, getRandomNumber(300, 340), ClickUtils.getRandomNumber(380, 680), ClickUtils.getRandomNumber(340, 380), ClickUtils.getRandomNumber(1380, 1580));
                try {
                    Thread.sleep(ClickUtils.getRandomNumber(1, 10) * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }


    public static int getRandomNumber(int min, int max) {
        Random random = new Random(System.currentTimeMillis());
        return random.nextInt(max - min + 1) + min;
    }

    public static boolean isAccessibilityEnabled(Context context) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        return accessibilityManager.isEnabled();
    }

    public static void requestAccessibilityPermission(Context context) {
        if (!isAccessibilityEnabled(context)) return;
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        context.startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void swipe(AccessibilityService accessibilityService, int x1, int y1, int x2, int y2) {
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path p = new Path();
        p.moveTo(x1, y1);
        p.lineTo(x2, y2);
        builder.addStroke(new GestureDescription.StrokeDescription(p, ClickUtils.getRandomNumber(50, 100), ClickUtils.getRandomNumber(300, 500)));
//        Path q = new Path();
//        q.moveTo(x1, y1);
//        q.lineTo(x2, y2);
//        builder.addStroke(new GestureDescription.StrokeDescription(q, 100L, ViewConfiguration.getTapTimeout()));
        GestureDescription gesture = builder.build();
        boolean isDispatched = accessibilityService.dispatchGesture(gesture, new GestureResultCallback() {
            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
            }

            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
            }
        }, null);
    }

    public static void screenshot(Context context) {
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent data;
        int resultCode;
        MediaProjection mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);

    }
}