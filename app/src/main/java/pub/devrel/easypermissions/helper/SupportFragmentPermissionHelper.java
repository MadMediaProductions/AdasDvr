package pub.devrel.easypermissions.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

class SupportFragmentPermissionHelper extends BaseSupportPermissionsHelper<Fragment> {
    public SupportFragmentPermissionHelper(@NonNull Fragment host) {
        super(host);
    }

    public FragmentManager getSupportFragmentManager() {
        return ((Fragment) getHost()).getChildFragmentManager();
    }

    public void directRequestPermissions(int requestCode, @NonNull String... perms) {
        ((Fragment) getHost()).requestPermissions(perms, requestCode);
    }

    public boolean shouldShowRequestPermissionRationale(@NonNull String perm) {
        return ((Fragment) getHost()).shouldShowRequestPermissionRationale(perm);
    }

    public Context getContext() {
        return ((Fragment) getHost()).getActivity();
    }
}
