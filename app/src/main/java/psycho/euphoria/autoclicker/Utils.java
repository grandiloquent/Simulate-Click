package psycho.euphoria.autoclicker;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.hardware.display.VirtualDisplay.Callback;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import static psycho.euphoria.autoclicker.ClickUtils.click;
import static psycho.euphoria.autoclicker.Shared.requestAccessibilityPermission;

public class Utils {

    static boolean founded = false;

    public static boolean checkIfColorIsRange(Bitmap bitmap, int x, int y, Function<Integer, Boolean> fr,
                                              Function<Integer, Boolean> fg, Function<Integer, Boolean> fb) {
        int color = bitmap.getPixel(x, y);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        Log.e("B5aOx2", String.format("checkIfColorIsRange, %sx%s = %s = %s,%s,%s", x, y, color, red, green, blue));
        return fr.apply(red) && fg.apply(green) && fb.apply(blue);
        /*
        checkIfColorIsRange(decoded, 290, 1720, (red) -> {
                                return red > 200;
                            }, (green) -> {
                                return green < 80;
                            }, (blue) -> {
                                return blue < 100;

         */
    }

    public static boolean compareColor(int color, int red, int redOffset, int green, int greenOffset, int blue, int blueOffset) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        Log.e("B5aOx2", String.format("compareColor, %s %s %s %s", color, r, g, b));
        if (redOffset == 0 && r != red) {
            return false;
        }
        if (greenOffset == 0 && g != green) {
            return false;
        }
        if (blueOffset == 0 && b != blue) {
            return false;
        }
        if (r < red - redOffset || r > red + redOffset) {
            return false;
        }
        if (g < green - greenOffset || g > green + greenOffset) {
            return false;
        }
        if (b < blue - blueOffset || b > blue + blueOffset) {
            return false;
        }
        return true;
    }

    public static boolean compareColor(Bitmap bitmap, int x, int y, int red, int redOffset, int green, int greenOffset, int blue, int blueOffset) {
        return compareColor(bitmap.getPixel(x, y), red, redOffset, green, greenOffset, blue, blueOffset);
    }

    public static boolean compareColor(Bitmap bitmap, int x, int y, int color) {
        int j = bitmap.getPixel(x, y);
        return j == color;
    }

    public static boolean compareColor(Bitmap bitmap, int... values) {
        for (int i = 0; i < values.length; i += 3) {
            if (!compareColor(bitmap, values[i], values[i + 1], values[i + 2])) {
                return false;
            }
        }
        return true;
    }

    public static boolean compareColor(int offset, Bitmap bitmap, int... values) {
        for (int i = 0; i < values.length; i += 3) {
            int color = values[i + 2];
            int red = (color >> 16) & 0xFF;
            int green = (color >> 8) & 0xFF;
            int blue = color & 0xFF;
            int value = bitmap.getPixel(values[i], values[i + 1]);
            int r = (value >> 16) & 0xFF;
            int g = (value >> 8) & 0xFF;
            int b = value & 0xFF;
            //Log.e("B5aOx2", String.format("compareColor,%s %s %s %s %s %s %s %s %s %s", values[i], values[i + 1], color, value, red, r, green, g, blue, b));
            if (r < red - offset || r > red + offset ||
                    g < green - offset || g > green + offset ||
                    b < blue - offset || b > blue + offset
            ) {
                return false;
            }
        }
        return true;
    }

    public static void dumpColor(Bitmap bitmap, int x, int y) {
        int color = bitmap.getPixel(x, y);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        Log.e("B5aOx2", String.format("dumpColor, %s,%s,%s = %s,%s,%s", x, y, color, r, g, b));
    }

    public static void dumpColor(Bitmap bitmap, int... values) {
        for (int i = 0; i < values.length; i += 2) {
            dumpColor(bitmap, values[i], values[i + 1]);
        }
    }

    // adb shell am start -a android.settings.ACCESSIBILITY_SETTINGS
    // https://developer.android.com/guide/topics/ui/accessibility/service
    public static long getDirectorySize(File directory) {
        if (directory.isDirectory()) {
            long size = 0;
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    size += getDirectorySize(file);
                }
            }
            return size;
        } else if (directory.isFile()) {
            return directory.length();
        } else {
            return 0;
        }
    }

    public static int getRandomNumber(int min, int max) {
        Random random = new Random(System.currentTimeMillis());
        return random.nextInt(max - min + 1) + min;
    }

    public static void key(final Intent intent, String name, final DisplayMetrics metrics, MediaProjectionManager mediaProjectionManager, Handler handler, AccessibilityService accessibilityService, int[][][] actions) {
        shoot(intent, name, metrics, mediaProjectionManager,
                handler, accessibilityService, (decoded) -> {
                    for (int[][] p : actions) {
                        if (compareColor(p[0][0], decoded, p[1])) {
                            Log.e("B5aOx2", String.format("screenShoot11, %s %s", p[2][0], p[3][0]));
                            click(accessibilityService, getRandomNumber(
                                    p[2][0],
                                    p[2][1]
                            ), getRandomNumber(p[3][0], p[3][1]));
                            if (p.length > 4) {
                                try {
                                    Thread.sleep(p[4][0]);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            break;
                        }
                    }
                    return null;
                });
    }

    public static void shoot(final Intent intent, String name, final DisplayMetrics metrics, MediaProjectionManager mediaProjectionManager, Handler handler, AccessibilityService accessibilityService, Function<Bitmap, Void> action) {
        final int resultCode = intent.getIntExtra(name, 0);
        final MediaProjection projection = mediaProjectionManager.getMediaProjection(resultCode, intent);
        if (projection != null) {
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            final ImageReader ir = ImageReader.newInstance(width, height, 0x01, 1);
            VirtualDisplay.Callback callback = new Callback() {
                @Override
                public void onPaused() {
                    super.onPaused();
                }

                @Override
                public void onResumed() {
                    super.onResumed();
                }

                @Override
                public void onStopped() {
                    super.onStopped();
                }
            };
            VirtualDisplay vd = projection.createVirtualDisplay("screen", width, height, metrics.densityDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, ir.getSurface(), callback, handler);
            //  @Override
            founded = false;
            ImageReader.OnImageAvailableListener listener = reader -> {
                projection.stop();
                if (founded) {
                    return;
                }
                founded = true;
                Image image = null;
                Bitmap bitmap = null;
                Bitmap decoded = null;
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
                    decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                    action.apply(decoded);
                    decoded.recycle();


                } catch (Exception e) {
                } finally {
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                    if (decoded != null) {
                        decoded.recycle();
                    }
                    if (image != null) {
                        image.close();
                    }
                    //ir.discardFreeBuffers();
                    ir.setOnImageAvailableListener(null, null);
                    ir.close();
                    vd.release();
                }

            };
            ir.setOnImageAvailableListener(listener, handler);
        }
    }

    public static void startScreenRecorder(Context context, final int resultCode, final Intent data) {
        final Intent intent = new Intent(context, AutoService.class);
        intent.setAction(AutoService.ACTION_START);
        intent.putExtra(AutoService.EXTRA_RESULT_CODE, resultCode);
        intent.putExtras(data);
        context.startService(intent);

    }

    private void listLargestDirectories() {
        new Thread(() -> {
            File[] directories = new File(Environment.getExternalStorageDirectory(), "").listFiles(file -> file.isDirectory());
            List<Pair<String, Long>> data = new ArrayList<>();
            for (File dir : directories) {
                try {
                    data.add(Pair.create(dir.getName(), getDirectorySize(dir)));
                } catch (Exception ignored) {
                }
            }
            data.sort((s1, s2) -> {
                long dif = s1.second - s2.second;
                if (dif > 0)
                    return -1;
                else if (dif < 0)
                    return 1;
                return 0;
            });
            for (Pair<String, Long> p : data) {
                Log.e("B5aOx2", String.format("onCreate, %s == %s", p.first, p.second));
            }
        }).start();
    }
}