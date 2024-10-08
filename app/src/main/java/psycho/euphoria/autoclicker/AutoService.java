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
import android.graphics.Color;
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
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;

import static psycho.euphoria.autoclicker.ClickUtils.click;
import static psycho.euphoria.autoclicker.ClickUtils.swipe;
import static psycho.euphoria.autoclicker.Shared.requestAccessibilityPermission;
import static psycho.euphoria.autoclicker.Utils.checkIfColorIsRange;
import static psycho.euphoria.autoclicker.Utils.compareColor;
import static psycho.euphoria.autoclicker.Utils.getRandomNumber;

public class AutoService extends AccessibilityService {
    private static final String BASE = "psycho.euphoria.autoclicker.";
    public static final String ACTION_START = BASE + "ACTION_START";
    public static final String EXTRA_RESULT_CODE = BASE + "EXTRA_RESULT_CODE";
    public static final String ACTION_1 = BASE + "ACTION_1";
    public static final String ACTION_2 = BASE + "ACTION_2";
    public static final String ACTION_3 = BASE + "ACTION_3";
    public static final String ACTION_4 = BASE + "ACTION_4";
    public static final String ACTION_5 = BASE + "ACTION_5";
    public static final String ACTION_6 = BASE + "ACTION_6";
    public static final String ACTION_7 = BASE + "ACTION_7";
    public static final String ACTION_8 = BASE + "ACTION_8";
    public static final String ACTION_9 = BASE + "ACTION_9";
    public static final String ACTION_10 = BASE + "ACTION_10";
    public static final String ACTION_11 = BASE + "ACTION_11";
    public static final String ACTION_12 = BASE + "ACTION_12";
    public static final String ACTION_13 = BASE + "ACTION_13";
    public static final String ACTION_14 = BASE + "ACTION_14";
    public static final String ACTION_15 = BASE + "ACTION_15";
    private static final Object sSync = new Object();
    Handler mHandler;
    private MediaProjectionManager mMediaProjectionManager;
    private Intent mIntent;

    private void createNotificationChannel() {
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_small);
        notificationLayout.setOnClickPendingIntent(R.id.action1, PendingIntent.getService(this, 0, new Intent(this, AutoService.class)
                .setAction(ACTION_1), PendingIntent.FLAG_IMMUTABLE));
        notificationLayout.setOnClickPendingIntent(R.id.action2, PendingIntent.getService(this, 0, new Intent(this, AutoService.class)
                .setAction(ACTION_2), PendingIntent.FLAG_IMMUTABLE));
        notificationLayout.setOnClickPendingIntent(R.id.action3, PendingIntent.getService(this, 0, new Intent(this, AutoService.class)
                .setAction(ACTION_3), PendingIntent.FLAG_IMMUTABLE));
        notificationLayout.setOnClickPendingIntent(R.id.action4, PendingIntent.getService(this, 0, new Intent(this, AutoService.class)
                .setAction(ACTION_4), PendingIntent.FLAG_IMMUTABLE));
        notificationLayout.setOnClickPendingIntent(R.id.action5, PendingIntent.getService(this, 0, new Intent(this, AutoService.class)
                .setAction(ACTION_5), PendingIntent.FLAG_IMMUTABLE));
        notificationLayout.setOnClickPendingIntent(R.id.action6, PendingIntent.getService(this, 0, new Intent(this, AutoService.class)
                .setAction(ACTION_6), PendingIntent.FLAG_IMMUTABLE));
        notificationLayout.setOnClickPendingIntent(R.id.action7, PendingIntent.getService(this, 0, new Intent(this, AutoService.class)
                .setAction(ACTION_7), PendingIntent.FLAG_IMMUTABLE));
        notificationLayout.setOnClickPendingIntent(R.id.action8, PendingIntent.getService(this, 0, new Intent(this, AutoService.class)
                .setAction(ACTION_8), PendingIntent.FLAG_IMMUTABLE));
        notificationLayout.setOnClickPendingIntent(R.id.action9, PendingIntent.getService(this, 0, new Intent(this, AutoService.class)
                .setAction(ACTION_9), PendingIntent.FLAG_IMMUTABLE));
        notificationLayout.setOnClickPendingIntent(R.id.action10, PendingIntent.getService(this, 0, new Intent(this, AutoService.class)
                .setAction(ACTION_10), PendingIntent.FLAG_IMMUTABLE));
        notificationLayout.setOnClickPendingIntent(R.id.action11, PendingIntent.getService(this, 0, new Intent(this, AutoService.class)
                .setAction(ACTION_11), PendingIntent.FLAG_IMMUTABLE));
        notificationLayout.setOnClickPendingIntent(R.id.action12, PendingIntent.getService(this, 0, new Intent(this, AutoService.class)
                .setAction(ACTION_12), PendingIntent.FLAG_IMMUTABLE));
        notificationLayout.setOnClickPendingIntent(R.id.action13, PendingIntent.getService(this, 0, new Intent(this, AutoService.class)
                .setAction(ACTION_13), PendingIntent.FLAG_IMMUTABLE));
        notificationLayout.setOnClickPendingIntent(R.id.action14, PendingIntent.getService(this, 0, new Intent(this, AutoService.class)
                .setAction(ACTION_14), PendingIntent.FLAG_IMMUTABLE));
        notificationLayout.setOnClickPendingIntent(R.id.action15, PendingIntent.getService(this, 0, new Intent(this, AutoService.class)
                .setAction(ACTION_15), PendingIntent.FLAG_IMMUTABLE));
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
//        Intent nfIntent = new Intent(this, MainActivity.class); //点击后跳转的界面，可以设置跳转数据
//        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
//                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
//                //.setContentTitle("SMI InstantView") // 设置下拉列表里的标题
//                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
//                .setContentText("is running......") // 设置上下文内容
//                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
        builder.setContentTitle("笔记")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setCustomContentView(notificationLayout);
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
/*        LayoutParams layoutParams = Shared.createOverlayLayoutParams();
        layoutParams.width = LayoutParams.WRAP_CONTENT;//getResources().getDisplayMetrics().widthPixels;
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        layoutParams.x = 900;
        layoutParams.y = 200;
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
            new Thread(() -> {
                for (int i = 0; i < 1000; i++) {
                    ClickUtils.screenShoot1(mIntent, EXTRA_RESULT_CODE, getResources().getDisplayMetrics(), mMediaProjectionManager, mHandler, this);
                    try {
                        Thread.sleep(ClickUtils.getRandomNumber(10, 15) * 1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        });
        frameLayout.findViewById(R.id.action4).setOnClickListener(view -> {
            new Thread(() -> {
                for (int i = 0; i < 1000; i++) {
                    ClickUtils.screenShoot2(mIntent, EXTRA_RESULT_CODE, getResources().getDisplayMetrics(), mMediaProjectionManager, mHandler, this);
                    try {
                        Thread.sleep(ClickUtils.getRandomNumber(10, 15) * 1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        });
        frameLayout.findViewById(R.id.action5).setOnClickListener(view -> {
            ClickUtils.screenShoot(mIntent, EXTRA_RESULT_CODE, getResources().getDisplayMetrics(), mMediaProjectionManager, mHandler);
        });
        frameLayout.findViewById(R.id.action6).setOnClickListener(view -> {
            requestAccessibilityPermission(this);
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
        windowManager.addView(frameLayout, layoutParams);*/
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onRebind(Intent intent) {
        Log.e("B5aOx2", String.format("onRebind, %s", ""));
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        int result = START_NOT_STICKY;
        final String action = intent != null ? intent.getAction() : null;
        if (ACTION_START.equals(action)) {
            createNotificationChannel();
            mIntent = intent;
            //updateStatus();
        } else if (ACTION_1.equals(action)) {
            action1();
        } else if (ACTION_2.equals(action)) {
            action2();
        } else if (ACTION_3.equals(action)) {
            action3();
        } else if (ACTION_4.equals(action)) {
            action4();
        } else if (ACTION_5.equals(action)) {
            action5();
        } else if (ACTION_6.equals(action)) {
            action6();
        } else if (ACTION_7.equals(action)) {
            action7();
        } else if (ACTION_8.equals(action)) {
            action8();
        } else if (ACTION_9.equals(action)) {
            action9();
        } else if (ACTION_10.equals(action)) {
            action10();
        } else if (ACTION_11.equals(action)) {
            action11();
        } else if (ACTION_12.equals(action)) {
            action12();
        } else if (ACTION_13.equals(action)) {
            action13();
        } else if (ACTION_14.equals(action)) {
            action14();
        } else if (ACTION_15.equals(action)) {
            action15();
        }
        return result;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("B5aOx2", String.format("onUnbind, %s", ""));
        return super.onUnbind(intent);
    }

    void action1() {
        ClickUtils.watchVideos(this);
    }

    void action2() {
        ClickUtils.watchBooks(this);
    }

    void action3() {
        new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                Utils.key(mIntent, EXTRA_RESULT_CODE, getResources().getDisplayMetrics(), mMediaProjectionManager, mHandler, this, Keys.ACTION1);
                try {
                    Thread.sleep(ClickUtils.getRandomNumber(3, 5) * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    void action4() {
        new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                ClickUtils.screenShoot2(mIntent, EXTRA_RESULT_CODE, getResources().getDisplayMetrics(), mMediaProjectionManager, mHandler, this);
                try {
                    Thread.sleep(ClickUtils.getRandomNumber(5, 10) * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    void action5() {
        mHandler.postDelayed(() -> {
            ClickUtils.screenShoot(mIntent, EXTRA_RESULT_CODE, getResources().getDisplayMetrics(), mMediaProjectionManager, mHandler);
            Toast.makeText(this, "完成", Toast.LENGTH_SHORT).show();
        }, 3000);

    }

    void action6() {
        requestAccessibilityPermission(this);
    }

    void action7() {
        stopForeground(true);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    void action8() {
    }

    void action9() {
    }

    void action10() {
        new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                Utils.shoot(mIntent, EXTRA_RESULT_CODE, getResources().getDisplayMetrics(), mMediaProjectionManager, mHandler, this, (decoded) -> {
                    int number = getRandomNumber(0, 100);
                    if (number > 40 && compareColor(40, decoded,
                            955, 958, -1053205,
                            980, 968, -921619,
                            986, 946, -921619)) {
                        click(this, getRandomNumber(969, 1024), getRandomNumber(978, 1019));
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (
                            checkIfColorIsRange(decoded, 290, 1720, (red) -> {
                                return red > 200;
                            }, (green) -> {
                                return green < 80;
                            }, (blue) -> {
                                return blue < 100;
                            })) {
                        click(this, getRandomNumber(310, 550), getRandomNumber(1780, 1860));
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    swipe(this, getRandomNumber(300, 340), ClickUtils.getRandomNumber(1280, 1480), ClickUtils.getRandomNumber(340, 380), ClickUtils.getRandomNumber(380, 680));
                    return null;
                });
                try {
                    Thread.sleep(ClickUtils.getRandomNumber(5, 10) * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    void action11() {
    }

    void action12() {
        mHandler.postDelayed(() -> {
            new Thread(() -> {
                for (int i = 0; i < 1000; i++) {
                    try {
                        Keys.action121(this, mIntent, mMediaProjectionManager, mHandler);
                        Thread.sleep(ClickUtils.getRandomNumber(5, 10) * 1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }, 3000);
    }

    void action13() {
    }

    void action14() {
    }

    void action15() {
    }

}