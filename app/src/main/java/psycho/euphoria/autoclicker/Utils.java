package psycho.euphoria.autoclicker;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Utils {

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