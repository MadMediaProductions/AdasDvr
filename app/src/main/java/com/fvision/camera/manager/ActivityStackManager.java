package com.fvision.camera.manager;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Process;
import java.util.Stack;

public class ActivityStackManager {
    private static Stack<Activity> activityStack;
    private static ActivityStackManager instance = null;

    private ActivityStackManager() {
        activityStack = new Stack<>();
    }

    public static ActivityStackManager getManager() {
        if (instance == null) {
            synchronized (ActivityStackManager.class) {
                if (instance == null) {
                    instance = new ActivityStackManager();
                }
            }
        }
        return instance;
    }

    public void push(Activity activity) {
        activityStack.push(activity);
    }

    public Activity pop() {
        if (activityStack.isEmpty()) {
            return null;
        }
        return activityStack.pop();
    }

    public Activity peek() {
        if (activityStack.isEmpty()) {
            return null;
        }
        return activityStack.peek();
    }

    public void clearActivity() {
        while (!activityStack.isEmpty()) {
            Activity pop = activityStack.pop();
        }
    }

    public void remove(Activity activity) {
        if (activityStack.size() <= 0 || activity != activityStack.peek()) {
            activityStack.remove(activity);
        } else {
            activityStack.pop();
        }
    }

    public boolean contains(Activity activity) {
        return activityStack.contains(activity);
    }

    public void finishAllActivity() {
        while (!activityStack.isEmpty()) {
            activityStack.pop().finish();
        }
    }

    public void exitApp(Context context) {
        try {
            finishAllActivity();
            ((ActivityManager) context.getSystemService("activity")).restartPackage(context.getPackageName());
            ((NotificationManager) context.getSystemService("notification")).cancelAll();
            Process.killProcess(Process.myPid());
        } catch (Exception e) {
        }
    }
}
