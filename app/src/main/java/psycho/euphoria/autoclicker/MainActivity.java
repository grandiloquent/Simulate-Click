package psycho.euphoria.autoclicker;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import static android.os.Build.VERSION.SDK_INT;
import static psycho.euphoria.autoclicker.Shared.requestAccessibilityPermission;
import static psycho.euphoria.autoclicker.Utils.startScreenRecorder;

// 编辑运行配置 Deloy 下面 3 个复选都选上
public class MainActivity extends Activity {
    private static final int REQUEST_CODE_SCREEN_CAPTURE = 1;

    // 创建截屏请求
    private void createScreenshotRequest() {
        final MediaProjectionManager manager
                = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        final Intent permissionIntent = manager.createScreenCaptureIntent();
        startActivityForResult(permissionIntent, REQUEST_CODE_SCREEN_CAPTURE);

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_CODE_SCREEN_CAPTURE == requestCode) {
            if (resultCode != Activity.RESULT_OK) {
                // when no permission
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                return;
            }
            startScreenRecorder(this, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SDK_INT >= VERSION_CODES.M) {
            Shared.requestOverlayPermission(this);
        }
        stopService(new Intent(this, AutoService.class));
        if (Shared.isDeviceRooted()) {
            requestAccessibilityPermission(this, AutoService.class);
        } else {
            requestAccessibilityPermission(this);
        }
        if (SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                    startActivity(intent);
                } catch (Exception ex) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(intent);
                }
            }
        } else {
            if (checkSelfPermission(permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        permission.WRITE_EXTERNAL_STORAGE
                }, 0);

            }
        }
        if (checkSelfPermission(permission.KILL_BACKGROUND_PROCESSES) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    permission.KILL_BACKGROUND_PROCESSES
            }, 0);
        }
        createScreenshotRequest();
    }

}