package com.huiying.cameramjpeg;

import android.os.Build;
import android.text.TextUtils;
import com.fvision.camera.bean.DevFileName;
import com.fvision.camera.manager.DevFileNameManager;
import com.fvision.camera.util.LogUtils;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SearchUSB {
    public String searchUsbPath() {
        String searchPath = null;
        List<String> paths = getMountPathList();
        if (paths != null && paths.size() > 0) {
            for (String path : paths) {
                searchPath = searchDir(path);
                if (!TextUtils.isEmpty(searchPath)) {
                    break;
                }
            }
        }
        if (TextUtils.isEmpty(searchPath)) {
            searchPath = searchDir("/storage/");
            LogUtils.d("runningLog", "/storage/ 下搜索到 " + searchPath);
        }
        if (TextUtils.isEmpty(searchPath)) {
            searchPath = searchDir("/mnt/");
            LogUtils.d("runningLog", "/mnt/ 下搜索到 " + searchPath);
        }
        if (!TextUtils.isEmpty(searchPath)) {
            return searchPath;
        }
        String searchPath2 = searchDir("/UsbStorage/");
        LogUtils.d("runningLog", "/UsbStorage/ 下搜索到 " + searchPath2);
        return searchPath2;
    }

    public boolean isInsertDevice() {
        String path = searchUsbPath();
        return path != null && !path.equals("");
    }

    public String searchDir(String path) {
        File file;
        File[] files;
        if (path == null || (file = new File(path)) == null || (files = file.listFiles()) == null || files.length <= 0) {
            return null;
        }
        for (File f : files) {
            if (f.isFile()) {
                Iterator<DevFileName> it = DevFileNameManager.getInstance().getDevList().iterator();
                while (it.hasNext()) {
                    DevFileName dev = it.next();
                    if (f.getName().equals(dev.getPreView())) {
                        String searchPath = f.getAbsolutePath();
                        DevFileNameManager.getInstance().setCurrentDev(dev);
                        return searchPath;
                    }
                }
                LogUtils.d("searchDir 卡住");
            } else if (!f.isDirectory()) {
                continue;
            } else {
                if (f.getAbsolutePath().contains("sdcard")) {
                    if (!Build.MODEL.equals("k80_bsp") && !Build.MODEL.equals("sp7731e_1h10_native") && !Build.BRAND.startsWith("FYT")) {
                        File[] ls = f.listFiles();
                        if (ls != null && ls.length > 20) {
                            return null;
                        }
                    }
                }
                int count = 0;
                String dir = f.getAbsolutePath();
                for (int i = 0; i < dir.length(); i++) {
                    if (dir.charAt(i) == '/') {
                        count++;
                    }
                }
                if (count <= 4) {
                    String path1 = searchDir(f.getAbsolutePath());
                    if (!TextUtils.isEmpty(path1)) {
                        return path1;
                    }
                } else {
                    continue;
                }
            }
        }
        return null;
    }

    public static List<String> getMountPathList() {
        List<String> pathList = new ArrayList<>();
        try {
            Process p = Runtime.getRuntime().exec("cat /proc/mounts");
            BufferedInputStream inputStream = new BufferedInputStream(p.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                String result = TextUtils.split(line, " ")[1];
                if (result.toLowerCase().contains("udisk") || result.toLowerCase().contains("usb")) {
                    File file = new File(result);
                    if (file.isDirectory() && file.canRead() && file.canWrite()) {
                        pathList.add(result);
                    }
                }
                if (p.waitFor() != 0 && p.exitValue() == 1) {
                }
            }
            bufferedReader.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            pathList.add("/storage");
        }
        return pathList;
    }
}
