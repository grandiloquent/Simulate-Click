package psycho.euphoria.autoclicker;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityService.GestureResultCallback;
import android.accessibilityservice.GestureDescription;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
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
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.Buffer;
import java.util.Random;

import static psycho.euphoria.autoclicker.Utils.compareColor;
import static psycho.euphoria.autoclicker.Utils.dumpColor;

public class ClickUtils {

    static boolean founded = false;

    @TargetApi(Build.VERSION_CODES.N)
    public static void click(AccessibilityService accessibilityService, int x, int y) {
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

    public static void screenShoot(final Intent intent, String name, final DisplayMetrics metrics, MediaProjectionManager mediaProjectionManager, Handler handler) {
        final int resultCode = intent.getIntExtra(name, 0);
        final MediaProjection projection = mediaProjectionManager.getMediaProjection(resultCode, intent);
        if (projection != null) {
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            final ImageReader ir = ImageReader.newInstance(width, height, 0x01, 1);
            VirtualDisplay vd = projection.createVirtualDisplay("screen", width, height, metrics.densityDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, ir.getSurface(), null, null);
            founded = false;
            ir.setOnImageAvailableListener(reader -> {
                projection.stop();
                if (founded) {
                    return;
                }
                founded = true;
                Image image = null;
                FileOutputStream fos = null;
                Bitmap bitmap = null;
                try {
                    image = ir.acquireLatestImage();
                    fos = new FileOutputStream(Shared.getUniqueFile(".jpg"));
                    final Image.Plane[] planes = image.getPlanes();
                    final Buffer buffer = planes[0].getBuffer().rewind();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * ir.getWidth();
                    bitmap = Bitmap.createBitmap(ir.getWidth() + rowPadding / pixelStride,
                            ir.getHeight(), Bitmap.Config.ARGB_8888);
                    // bitmap = Bitmap.createBitmap(finalWidth,finalHeight, Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);
                    bitmap.compress(CompressFormat.JPEG, 85, fos);
                    bitmap.recycle();
                    image.close();

                } catch (Exception e) {
                }

            }, handler);
        }

    }

    public static void screenShoot1(final Intent intent, String name, final DisplayMetrics metrics, MediaProjectionManager mediaProjectionManager, Handler handler, AccessibilityService accessibilityService) {
        final int resultCode = intent.getIntExtra(name, 0);
        final MediaProjection projection = mediaProjectionManager.getMediaProjection(resultCode, intent);
        if (projection != null) {
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            final ImageReader ir = ImageReader.newInstance(width, height, 0x01, 1);
            VirtualDisplay vd = projection.createVirtualDisplay("screen", width, height, metrics.densityDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, ir.getSurface(), null, null);
            //  @Override
            founded = false;
            ir.setOnImageAvailableListener(reader -> {
                Log.e("B5aOx2", String.format("screenShoot1, %s", "1"));
                projection.stop();
                if (founded) {
                    return;
                }
                founded = true;
                Image image = null;
                Bitmap bitmap = null;
                try {
                    image = ir.acquireLatestImage();
                    final Image.Plane[] planes = image.getPlanes();
                    final Buffer buffer = planes[0].getBuffer().rewind();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * ir.getWidth();
                    bitmap = Bitmap.createBitmap(ir.getWidth() + rowPadding / pixelStride,
                            ir.getHeight(), Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(CompressFormat.JPEG, 85, out);
                    bitmap.recycle();
                    Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                    int j1 = decoded.getPixel(789, 90);
                    int j2 = decoded.getPixel(790, 90);
                    Log.e("B5aOx2", String.format("screenShoot1, %s %s", j1, Color.red(j2)));
                    if (Color.red(j1) > 200 && Color.red(j2) > 200) {
                        //Log.e("B5aOx2", String.format("screenShoot1, %s", "3"));
                        click(accessibilityService, getRandomNumber(938, 948), getRandomNumber(78, 88));
                    }
                    j1 = decoded.getPixel(157, 735);
                    j2 = decoded.getPixel(32, 735);
                    int j3 = decoded.getPixel(1046, 735);
                    int j4 = decoded.getPixel(647, 735);
//                    Log.e("B5aOx2", String.format("screenShoot1, %s %s %s %s", Color.red(j4),
//                            Color.green(j4),
//                            Color.green(j2),
//                            Color.red(j2)));
                    if (Color.red(j1) > 250 && Color.green(j1) > 200 && Color.blue(j1) > 200 &&
                            Color.red(j4) > 250 && Color.green(j4) > 200 && Color.blue(j4) > 200 &&
                            Color.red(j2) < 100 && Color.red(j3) < 100) {
                        click(accessibilityService, getRandomNumber(500, 520), getRandomNumber(1190, 1200));
                    }
                    decoded.recycle();
                    image.close();

                } catch (Exception e) {
                }

            }, handler);
        }

    }

    public static void screenShoot2(final Intent intent, String name, final DisplayMetrics metrics, MediaProjectionManager mediaProjectionManager, Handler handler, AccessibilityService accessibilityService) {
        final int resultCode = intent.getIntExtra(name, 0);
        final MediaProjection projection = mediaProjectionManager.getMediaProjection(resultCode, intent);
        if (projection != null) {
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            final ImageReader ir = ImageReader.newInstance(width, height, 0x01, 1);
            VirtualDisplay vd = projection.createVirtualDisplay("screen", width, height, metrics.densityDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, ir.getSurface(), null, null);
            //  @Override
            founded = false;
            ir.setOnImageAvailableListener(reader -> {
                Log.e("B5aOx2", String.format("screenShoot1, %s", "1"));
                projection.stop();
                if (founded) {
                    return;
                }
                founded = true;
                Image image = null;
                Bitmap bitmap = null;
                try {
                    image = ir.acquireLatestImage();
                    final Image.Plane[] planes = image.getPlanes();
                    final Buffer buffer = planes[0].getBuffer().rewind();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * ir.getWidth();
                    bitmap = Bitmap.createBitmap(ir.getWidth() + rowPadding / pixelStride,
                            ir.getHeight(), Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(CompressFormat.JPEG, 85, out);
                    bitmap.recycle();
                    Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                    if (compareColor(40, decoded,
                            810, 79, -1185811,
                            816, 70, -659467,
                            928, 76, -263681)) {
                        Log.e("B5aOx2", String.format("screenShoot2, %s", "1"));
                        click(accessibilityService, getRandomNumber(821, 989), getRandomNumber(90, 117));
                    }
                    if (compareColor(20, decoded,
                            469, 1299, -10592674,
                            471, 1272, -11053225,
                            608, 1273, -394759)) {
                        Log.e("B5aOx2", String.format("screenShoot2, %s", "2"));
                        click(accessibilityService, getRandomNumber(293, 759), getRandomNumber(1173, 1254));
                    }
                    if (compareColor(20, decoded,
                            469, 1256, -7502973,
                            471, 1230, -8423306,
                            608, 1230, -131587)) {
                        Log.e("B5aOx2", String.format("screenShoot2, %s", "3"));
                        click(accessibilityService, getRandomNumber(285, 794), getRandomNumber(1142, 1245));
                    }
                    if (compareColor(20, decoded,
                            79, 127, -16711423,
                            71, 128, -1,
                            462, 1844, -13135929
                    ) || compareColor(20, decoded,
                            79, 127, -16711423,
                            71, 128, -1,
                            462, 1844, -1245185
                    )
                    ) {
                        Log.e("B5aOx2", String.format("screenShoot2, %s", "4"));
                        click(accessibilityService, getRandomNumber(49, 60), getRandomNumber(127, 137));
                        Thread.sleep(1000);
                    }
                    //=============================================================t
                    if (compareColor(60, decoded,
                            100, 77, -3085,
                            117, 73, -11,
                            150, 65, -1974813)) {
                        Log.e("B5aOx2", String.format("screenShoot2, %s", "5"));
                        click(accessibilityService, getRandomNumber(123, 394), getRandomNumber(62, 102));
                    }
                    if (compareColor(20, decoded,
                            481, 1216, -6447715,
                            479, 1238, -5789785,
                            597, 1216, -131587)) {
                        Log.e("B5aOx2", String.format("screenShoot2, %s", "6"));
                        click(accessibilityService, getRandomNumber(264, 836), getRandomNumber(1153, 1221));
                    }
                    //=============================================================k
                    decoded.recycle();
                    image.close();

                } catch (Exception e) {
                }

            }, handler);
        }

    }

    public static void screenShoot11(final Intent intent, String name, final DisplayMetrics metrics, MediaProjectionManager mediaProjectionManager, Handler handler, AccessibilityService accessibilityService) {
        final int resultCode = intent.getIntExtra(name, 0);
        final MediaProjection projection = mediaProjectionManager.getMediaProjection(resultCode, intent);
        if (projection != null) {
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            final ImageReader ir = ImageReader.newInstance(width, height, 0x01, 1);
            VirtualDisplay vd = projection.createVirtualDisplay("screen", width, height, metrics.densityDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, ir.getSurface(), null, null);
            //  @Override
            founded = false;
            ir.setOnImageAvailableListener(reader -> {
                projection.stop();
                if (founded) {
                    return;
                }
                founded = true;
                Image image = null;
                Bitmap bitmap = null;
                try {
                    image = ir.acquireLatestImage();
                    final Image.Plane[] planes = image.getPlanes();
                    final Buffer buffer = planes[0].getBuffer().rewind();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * ir.getWidth();
                    bitmap = Bitmap.createBitmap(ir.getWidth() + rowPadding / pixelStride,
                            ir.getHeight(), Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(CompressFormat.JPEG, 85, out);
                    bitmap.recycle();
                    Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                    if (compareColor(20, decoded,
                            834, 227, -10785,
                            797, 220, -444056,
                            925, 221, -10779)) {
                        Log.e("B5aOx2", String.format("screenShoot11, %s", "1"));
                        click(accessibilityService, getRandomNumber(835, 991), getRandomNumber(226, 292));
                        Thread.sleep(3000);
                    }
                    if (compareColor(40, decoded,
                            164, 148, -1315859,
                            166, 174, -1973275,
                            389, 148, -1)) {
                        Log.e("B5aOx2", String.format("screenShoot11, %s", "2"));
                        click(accessibilityService, getRandomNumber(121, 391), getRandomNumber(156, 201));
                        Thread.sleep(1000);
                    }
                    if (compareColor(20, decoded,
                            482, 1389, -5395027,
                            482, 1411, -5789785,
                            593, 1385, -131587)) {
                        Log.e("B5aOx2", String.format("screenShoot11, %s", "3"));
                        click(accessibilityService, getRandomNumber(479, 602), getRandomNumber(1528, 1555));
                        Thread.sleep(1000);
                    }
                    if (compareColor(20, decoded,
                            483, 1281, -6184543,
                            482, 1304, -7237231,
                            596, 1280, -1)) {
                        Log.e("B5aOx2", String.format("screenShoot11, %s", "4"));
                        click(accessibilityService, getRandomNumber(273, 810), getRandomNumber(1272, 1346));
                        Thread.sleep(1000);
                    }
//                    if (compareColor(20, decoded,
//                            945, 1975, -52377,
//                            964, 1981, -117147,
//                            959, 1998, -574876)) {
//                        Log.e("B5aOx2", String.format("screenShoot11, %s", "5"));
//                        click(accessibilityService, getRandomNumber(1011, 1025), getRandomNumber(144, 155));
//                    }
                    if (compareColor(40, decoded,
                            926, 341, -789001,
                            926, 358, -789001,
                            967, 356, -1052687,
                            929,330,-932012) || compareColor(40, decoded,
                            936, 345, -1,
                            954, 347, -1,
                            973, 346, -1,
                            926,298,-10824725)||compareColor(40, decoded,
                            924,340,-265221,
                            947,340,-513,
                            972,340,-513     ,
                            967,321,-9744147
                            )) {
                        Log.e("B5aOx2", String.format("screenShoot11, %s", "5"));
                        click(accessibilityService, getRandomNumber(1011, 1025), getRandomNumber(144, 155));

                    }
                    if (compareColor(20, decoded,
                            481, 1304, -7368817,
                            482, 1284, -6974059,
                            490, 1273, -1)) {
                        Log.e("B5aOx2", String.format("screenShoot11, %s", "6"));
                        click(accessibilityService, getRandomNumber(268, 796), getRandomNumber(1280, 1336));
                        Thread.sleep(1000);
                    }
//                    if (compareColor(20, decoded,
//                            190, 730, -1,
//                            190, 731, -1,
//                            190, 732, -1,
//                            190, 733, -1,
//                            190, 734, -1,
//                            190, 735, -1,
//                            190, 950, -1,
//                            190, 951, -1,
//                            890, 952, -1,
//                            890, 953, -1,
//                            890, 954, -1,
//                            890, 955, -1
//                    )) {
//                        Log.e("B5aOx2", String.format("screenShoot11, %s", "7"));
//                        click(accessibilityService, getRandomNumber(276, 804), getRandomNumber(1300, 1356));
//                        Thread.sleep(1000);
//                    }
                    if (compareColor(20, decoded,
                            288, 691, -1,
                            288, 692, -1,
                            288, 693, -1,
                            288, 694, -1,
                            288, 695, -1,
                            288, 696, -1,
                            850, 981, -1,
                            850, 982, -1,
                            850, 983, -1,
                            850, 984, -1,
                            850, 985, -1,
                            850, 986, -1
                    )
                            && !compareColor(20, decoded,
                            208, 691, -1,
                            208, 692, -1,
                            208, 693, -1,
                            208, 694, -1,
                            208, 695, -1,
                            208, 696, -1,
                            870, 981, -1,
                            870, 982, -1,
                            870, 983, -1,
                            870, 984, -1,
                            870, 985, -1,
                            870, 986, -1
                    )) {
                        Log.e("B5aOx2", String.format("screenShoot11, %s", "8"));
                        click(accessibilityService, getRandomNumber(515, 570), getRandomNumber(1515, 1525));
                        Thread.sleep(1000);
                    }
                    if (compareColor(20, decoded,
                            482, 1325, -6447715,
                            482, 1346, -6710887,
                            593, 1320, -1)) {
                        Log.e("B5aOx2", String.format("screenShoot2, %s", "3"));
                        click(accessibilityService, getRandomNumber(300, 780), getRandomNumber(1320, 1380));
                    }
                    if (compareColor(20, decoded,
                            482, 1325, -6447715,
                            482, 1346, -6710887,
                            593, 1320, -1)) {
                        Log.e("B5aOx2", String.format("screenShoot11, %s", "9"));
                        click(accessibilityService, getRandomNumber(480, 602), getRandomNumber(1456, 1483));
                    }
                    decoded.recycle();
                    image.close();

                } catch (Exception e) {
                }

            }, handler);
        }

    }

    public static void screenshot(Context context) {
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent data;
        int resultCode;
        //MediaProjection mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
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
}