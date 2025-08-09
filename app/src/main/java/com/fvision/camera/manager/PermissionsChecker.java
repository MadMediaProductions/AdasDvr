package com.fvision.camera.manager;

import android.content.Context;
import v4.ContextCompat;

public class PermissionsChecker {
    private final Context mContext;

    public PermissionsChecker(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public boolean lacksPermissions(String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(this.mContext, permission) == -1;
    }
}
