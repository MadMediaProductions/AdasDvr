package com.fvision.camera.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.fvision.camera.R;
import com.fvision.camera.manager.PermissionsChecker;
import v4.app.ActivityCompat;

public class PermissionsActivity extends Activity {
    private static final String EXTRA_PERMISSIONS = "permission.extra_permission";
    private static final String PACKAGE_URL_SCHEME = "package:";
    public static final int PERMISSIONS_DENIED = 1;
    public static final int PERMISSIONS_GRANTED = 0;
    private static final int PERMISSION_REQUEST_CODE = 0;
    private boolean isRequireCheck;
    private PermissionsChecker mChecker;

    public static void startActivityForResult(Activity activity, int requestCode, String... permissions) {
        Intent intent = new Intent(activity, PermissionsActivity.class);
        intent.putExtra(EXTRA_PERMISSIONS, permissions);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, (Bundle) null);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null || !getIntent().hasExtra(EXTRA_PERMISSIONS)) {
            throw new RuntimeException("PermissionsActivity: Need to use static - startActivityForResult: Method Startup!");
        }
        requestWindowFeature(1);
        setContentView(R.layout.activity_permissions);
        this.mChecker = new PermissionsChecker(this);
        this.isRequireCheck = true;
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (this.isRequireCheck) {
            String[] permissions = getPermissions();
            if (this.mChecker.lacksPermissions(permissions)) {
                requestPermissions(permissions);
            } else {
                allPermissionsGranted();
            }
        } else {
            this.isRequireCheck = true;
        }
    }

    private String[] getPermissions() {
        return getIntent().getStringArrayExtra(EXTRA_PERMISSIONS);
    }

    private void requestPermissions(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, 0);
    }

    private void allPermissionsGranted() {
        setResult(0);
        finish();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != 0 || !hasAllPermissionsGranted(grantResults)) {
            this.isRequireCheck = false;
            showMissingPermissionDialog();
            return;
        }
        this.isRequireCheck = true;
        allPermissionsGranted();
    }

    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == -1) {
                return false;
            }
        }
        return true;
    }

    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.help);
        builder.setMessage(R.string.string_help_text);
        builder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                PermissionsActivity.this.setResult(1);
                PermissionsActivity.this.finish();
            }
        });
        builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                PermissionsActivity.this.startAppSettings();
            }
        });
        builder.show();
    }

    /* access modifiers changed from: private */
    public void startAppSettings() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }
}
