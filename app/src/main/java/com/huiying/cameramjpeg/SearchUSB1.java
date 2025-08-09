package com.huiying.cameramjpeg;

import android.text.TextUtils;
import com.fvision.camera.bean.DevFileName;
import com.fvision.camera.manager.DevFileNameManager;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SearchUSB1 {
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
        }
        if (TextUtils.isEmpty(searchPath)) {
            searchPath = searchDir("/mnt/");
        }
        if (TextUtils.isEmpty(searchPath)) {
            return searchDir("/UsbStorage/");
        }
        return searchPath;
    }

    public boolean isInsertDevice() {
        String path = searchUsbPath();
        return path != null && !path.equals("");
    }

    public String searchDir(String path) {
        if (path == null) {
            return null;
        }
        File file = new File(path);
        if (file == null) {
            return null;
        }
        File[] files = file.listFiles();
        if (files == null || files.length <= 0) {
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
                continue;
            } else if (!f.isDirectory()) {
                continue;
            } else if (f.getAbsolutePath().contains("sdcard") && f.listFiles() != null && f.listFiles().length > 10) {
                return null;
            } else {
                String path1 = searchDir(f.getAbsolutePath());
                if (!TextUtils.isEmpty(path1)) {
                    return path1;
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
