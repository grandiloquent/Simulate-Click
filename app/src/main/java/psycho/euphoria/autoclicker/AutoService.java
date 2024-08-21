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
            ClickUtils.watchVideos(this);
        });
        frameLayout.findViewById(R.id.action2).setOnClickListener(view -> {
            ClickUtils.watchBooks(this);
        });
        frameLayout.findViewById(R.id.action3).setOnClickListener(view -> {
            ClickUtils.watchSpecifyVideos(this);
        });
        frameLayout.findViewById(R.id.action4).setOnClickListener(view -> {

        });
        frameLayout.findViewById(R.id.action_exit_to_app).setOnClickListener(view -> {
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




    @Override
    public void onInterrupt() {
    }

}