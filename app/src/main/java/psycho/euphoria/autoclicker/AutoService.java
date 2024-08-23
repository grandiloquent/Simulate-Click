package psycho.euphoria.autoclicker;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;

public class AutoService extends AccessibilityService {
    private static final String BASE = "psycho.euphoria.autoclicker.";
    public static final String ACTION_START = BASE + "ACTION_START";
    public static final String EXTRA_RESULT_CODE = BASE + "EXTRA_RESULT_CODE";
    private static final Object sSync = new Object();
    Handler mHandler;
    private MediaProjectionManager mMediaProjectionManager;


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
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
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
        frameLayout.findViewById(R.id.action5).setOnClickListener(view -> {
            ClickUtils.screenShoot(mIntent, EXTRA_RESULT_CODE, getResources().getDisplayMetrics(), mMediaProjectionManager, mHandler);
        });
        frameLayout.findViewById(R.id.action_exit_to_app).setOnClickListener(view -> {
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            activityManager.killBackgroundProcesses("com.dragon.read");
            activityManager.killBackgroundProcesses("com.xiaomi.market");
            activityManager.killBackgroundProcesses("com.mini.cleanmaster");
            activityManager.killBackgroundProcesses("com.android.camera");
            activityManager.killBackgroundProcesses("com.android.systemui");
            activityManager.killBackgroundProcesses("com.xiaomi.finddevice");
            activityManager.killBackgroundProcesses("com.ss.android.ugc.aweme.lite");
            activityManager.killBackgroundProcesses("com.cat.readall");
            activityManager.killBackgroundProcesses("com.ss.android.article.lite");
            activityManager.killBackgroundProcesses("com.xs.fm.lite");
            activityManager.killBackgroundProcesses("com.kuaishou.nebula");
            java.nio.file.Path dirPath = Paths.get(new File(Environment.getExternalStorageDirectory(), "Android/data").getAbsolutePath());
            try {
                Files.walk(dirPath)
                        .map(java.nio.file.Path::toFile)
                        .sorted(Comparator.comparing(File::isDirectory))
                        .forEach(File::delete);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            windowManager.removeView(frameLayout);
            stopSelf();
        });
//
        windowManager.addView(frameLayout, layoutParams);

    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onRebind(Intent intent) {
        Log.e("B5aOx2", String.format("onRebind, %s", ""));
        super.onRebind(intent);
    }

    private Intent mIntent;

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        int result = START_STICKY;
        final String action = intent != null ? intent.getAction() : null;
        if (ACTION_START.equals(action)) {
            createNotificationChannel();
            mIntent = intent;
            //updateStatus();
        }
        return result;
    }

    private void createNotificationChannel() {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, MainActivity.class); //点击后跳转的界面，可以设置跳转数据
        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                //.setContentTitle("SMI InstantView") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText("is running......") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        /*以下是对Android 8.0的适配*/
        //普通notification适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("notification_id");
        }
        //前台服务notification适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("notification_id", "notification_name", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }
        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        startForeground(110, notification);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("B5aOx2", String.format("onUnbind, %s", ""));
        return super.onUnbind(intent);
    }
}