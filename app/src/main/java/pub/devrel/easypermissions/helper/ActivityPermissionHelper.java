package pub.devrel.easypermissions.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

class ActivityPermissionHelper extends BaseFrameworkPermissionsHelper<Activity> {
    public ActivityPermissionHelper(Activity host) {
        super(host);
    }

    @SuppressLint({"NewApi"})
    public FragmentManager getFragmentManager() {
        return ((Activity) getHost()).getFragmentManager();
    }

    public void directRequestPermissions(int requestCode, @NonNull String... perms) {
        ActivityCompat.requestPermissions((Activity) getHost(), perms, requestCode);
    }

    public boolean shouldShowRequestPermissionRationale(@NonNull String perm) {
        return ActivityCompat.shouldShowRequestPermissionRationale((Activity) getHost(), perm);
    }

    public Context getContext() {
        return (Context) getHost();
    }
}
