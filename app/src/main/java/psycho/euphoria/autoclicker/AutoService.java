package psycho.euphoria.autoclicker;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class AutoService extends AccessibilityService {
    Handler mHandler;

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("B5aOx2", String.format("onUnbind, %s", ""));
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.e("B5aOx2", String.format("onRebind, %s", ""));
        super.onRebind(intent);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(getMainLooper());
        LayoutParams layoutParams = Shared.createOverlayLayoutParams();
        layoutParams.width = LayoutParams.WRAP_CONTENT;//getResources().getDisplayMetrics().widthPixels;
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        layoutParams.x = 100;
        layoutParams.y = 100;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        LinearLayout frameLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.main, null);
        frameLayout.setBackgroundColor(0XFFF2F2F2);
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        frameLayout.findViewById(R.id.action1).setOnClickListener(view -> {
            new Thread(() -> {
                    // 截屏 Photoshop 坐标
            for (int i = 0; i < 20; i++) {
                // 截屏 Photoshop 坐标
                click(1012, 1436);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                click(356, 1980);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                click(868, 1320);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            }).start();
        });
        frameLayout.findViewById(R.id.action2).setOnClickListener(view -> {
            mHandler.postDelayed(() -> {
                // 截屏 Photoshop 坐标
                for (int i = 0; i < 7; i++) {
                    // 截屏 Photoshop 坐标
                    click(807, 992 + 188* i);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, 100);
        });
        frameLayout.findViewById(R.id.action3).setOnClickListener(view -> {
            windowManager.removeView(frameLayout);
            stopSelf();
        });
//
        windowManager.addView(frameLayout, layoutParams);
//
//        LayoutParams layoutParams = Shared.createOverlayLayoutParams();
//        layoutParams.width = LayoutParams.MATCH_PARENT;//getResources().getDisplayMetrics().widthPixels;
//        layoutParams.height = LayoutParams.MATCH_PARENT;
//        FrameLayout frameLayout = new FrameLayout(this);
//        frameLayout.setBackgroundColor(0X33FF00FF);
//        WindowManager windowManager= (WindowManager) getSystemService(WINDOW_SERVICE);
//        windowManager.addView(frameLayout,layoutParams);
//        frameLayout.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                Log.e("B5aOx2", String.format("onTouch, %s",
//                        motionEvent.getRawX()+" "+motionEvent.getRawY()));
//                return false;
//            }
//        });
        Log.e("B5aOx2", String.format("onCreate, %s", ViewConfiguration.getTapTimeout()));
//        mHandler.postDelayed(() -> {
//            for (int i = 0; i < 6; i++) {
//                Log.e("B5aOx2", String.format("onCreate, %s", ""));
//                // 截屏 Photoshop 坐标
//                dispatch(860, 765 + 263 * i);
//                try {
//                    Thread.sleep(4000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 5000);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void click(int x, int y) {
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path p = new Path();
        p.moveTo(x, y);
        builder.addStroke(new GestureDescription.StrokeDescription(p, 0, ViewConfiguration.getTapTimeout()));
        GestureDescription gesture = builder.build();
        boolean isDispatched = dispatchGesture(gesture, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
            }
        }, null);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void swipe(int x1, int y1, int x2, int y2) {
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path p = new Path();
        p.moveTo(x1, y1);
        p.lineTo(x2, y2);
        builder.addStroke(new GestureDescription.StrokeDescription(p, 0, ViewConfiguration.getTapTimeout()));
        GestureDescription gesture = builder.build();
        boolean isDispatched = dispatchGesture(gesture, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
            }
        }, null);
    }

    @Override
    public void onInterrupt() {
    }

}