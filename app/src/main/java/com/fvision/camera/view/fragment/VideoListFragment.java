package com.fvision.camera.view.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import com.fvision.camera.R;
import com.fvision.camera.bean.FileBeans;
import com.fvision.camera.manager.CmdManager;
import com.fvision.camera.util.FileUtils;
import com.fvision.camera.utils.CameraStateUtil;
import com.fvision.camera.utils.CmdUtil;
import com.fvision.camera.utils.Const;
import com.fvision.camera.utils.LogUtils;
import com.fvision.camera.view.activity.PlaybackActivity;
import com.fvision.camera.view.adapter.HeaderListAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class VideoListFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_TYPE = "VIDEO_TYPE";
    private final int MAX_PAGE_SIZE = 18;
    private List<FileBeans> addFileList = new ArrayList();
    private int current_page = 0;
    private byte[] files = null;
    private HeaderListAdapter mAdapter;
    private int mColumnCount = 1;
    private List<FileBeans> mFileList = new ArrayList();
    private Handler mHandler = new Handler();
    /* access modifiers changed from: private */
    public OnListFragmentInteractionListener mListener;
    private StickyListHeadersListView recyclerView;
    private int videotype = 0;

    public interface OnListFragmentInteractionListener {
        void onDelete(FileBeans fileBeans);

        void onDownLoad(FileBeans fileBeans);

        void onListFragmentInteraction(FileBeans fileBeans);
    }

    public List<FileBeans> getFileList() {
        return this.mFileList;
    }

    public static VideoListFragment newInstance(byte[] filesByte, int type) {
        VideoListFragment fragment = new VideoListFragment();
        Bundle args = new Bundle();
        args.putByteArray(ARG_COLUMN_COUNT, filesByte);
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.files = getArguments().getByteArray(ARG_COLUMN_COUNT);
            this.videotype = getArguments().getInt(ARG_TYPE);
        }
    }

    public void resrefh() {
        this.files = ((PlaybackActivity) getActivity()).loadFile();
        loadFiles();
    }

    public void delItem(String fileName) {
        if (!TextUtils.isEmpty(fileName) && this.mFileList != null && this.mFileList.size() >= 1) {
            Iterator<FileBeans> it = this.mFileList.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                FileBeans file = it.next();
                if (file.fileName.equals(fileName)) {
                    this.mFileList.remove(file);
                    this.mAdapter.notifyDataSetChanged();
                    break;
                }
            }
            this.mAdapter.notifyDataSetChanged();
        }
    }

    public void addItem(FileBeans item) {
        if (this.mAdapter == null) {
            this.addFileList.add(item);
            return;
        }
        this.mFileList.add(item);
        this.mAdapter.notifyDataSetChanged();
    }

    private void loadFiles() {
        if (this.videotype == 1) {
            loadJpegFiles();
        } else if (this.files != null && this.files.length >= 1) {
            this.mFileList.clear();
            for (int i = 0; i * 6 < this.files.length; i++) {
                byte[] item = new byte[6];
                System.arraycopy(this.files, i * 6, item, 0, item.length);
                FileBeans file = byte2FileBean(item);
                file.fileIndex = i + 1;
                if (file.fileType == this.videotype && file.year > 1980) {
                    this.mFileList.add(file);
                }
            }
            for (FileBeans file2 : this.addFileList) {
                this.mFileList.add(file2);
            }
            Collections.sort(this.mFileList, new ComparatorValues());
            this.mAdapter.notifyDataSetChanged();
        }
    }

    private void loadFiles_JPG() {
        if (this.files != null && this.files.length >= 1) {
            this.mFileList.clear();
            for (int i = 0; i * 6 < this.files.length; i++) {
                byte[] item = new byte[6];
                System.arraycopy(this.files, i * 6, item, 0, item.length);
                FileBeans file = byte2FileBean(item);
                file.fileIndex = i + 1;
                if (file.fileType == this.videotype && file.year > 1980) {
                    this.mFileList.add(file);
                }
            }
            for (FileBeans file2 : this.addFileList) {
                this.mFileList.add(file2);
            }
            Collections.sort(this.mFileList, new ComparatorValues());
            this.mAdapter.notifyDataSetChanged();
        }
    }

    private void loadJpegFiles() {
        if (this.files != null && this.files.length >= 1) {
            this.mFileList.clear();
            File[] files2 = new File(Const.JPG_PATH).listFiles();
            if (files2 != null) {
                for (File f : files2) {
                    LogUtils.d("file path " + f.getPath());
                    if (!f.isDirectory()) {
                        FileBeans file = new FileBeans();
                        file.fileName = f.getName();
                        file.dayTime = f.lastModified();
                        file.fileType = 1;
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(file.dayTime);
                        file.year = calendar.get(1);
                        file.month = calendar.get(2);
                        file.day = calendar.get(5);
                        file.hour = calendar.get(11);
                        file.minute = calendar.get(12);
                        file.sencond = calendar.get(13);
                        LogUtils.d("jpeg file.dayTime " + file.dayTime + " format " + CameraStateUtil.longToString(file.dayTime, (String) null));
                        this.mFileList.add(file);
                    }
                }
                Collections.sort(this.mFileList, new ComparatorValues());
            }
        }
    }

    public static final class ComparatorValues implements Comparator<FileBeans> {
        public int compare(FileBeans o1, FileBeans o2) {
            if (o2.dayTime > o1.dayTime) {
                return 1;
            }
            return -1;
        }
    }

    private FileBeans byte2FileBean(byte[] filebyte) {
        if (filebyte.length != 6) {
            return null;
        }
        FileBeans file = new FileBeans();
        StringBuffer fileName = new StringBuffer();
        byte[] byte_type_num = new byte[2];
        byte[] byte_year_month_day = new byte[2];
        byte[] byte_hour_minute_second = new byte[2];
        System.arraycopy(filebyte, 0, byte_type_num, 0, byte_type_num.length);
        System.arraycopy(filebyte, 2, byte_year_month_day, 0, byte_year_month_day.length);
        System.arraycopy(filebyte, 4, byte_hour_minute_second, 0, byte_hour_minute_second.length);
        short file_type_num = CameraStateUtil.bytesToShort(byte_type_num, 0);
        short file_year_month_day = CameraStateUtil.bytesToShort(byte_year_month_day, 0);
        short file_hour_minute_second = CameraStateUtil.bytesToShort(byte_hour_minute_second, 0);
        file.year = (file_year_month_day >> 9) + 1980;
        file.month = (file_year_month_day >> 5) & 15;
        file.day = file_year_month_day & 31;
        file.hour = (file_hour_minute_second >> 11) & 31;
        file.minute = (file_hour_minute_second >> 5) & 63;
        file.sencond = (file_hour_minute_second & 31) * 2;
        if ((file_type_num & 4096) > 1) {
            fileName.append("LOK");
            file.fileType = 2;
        } else if ((32768 & file_type_num) > 1) {
            fileName.append("MOV");
            file.fileType = 0;
        } else if ((file_type_num & 16384) > 1) {
            fileName.append("PHO");
            file.fileType = 1;
        } else {
            fileName.append("OTHER");
        }
        fileName.append("" + CameraStateUtil.numFormat(file_type_num & 4095));
        Calendar calendar = Calendar.getInstance();
        calendar.set(file.year, file.month - 1, file.day, file.hour, file.minute, file.sencond);
        file.dayTime = calendar.getTimeInMillis();
        if ((file_type_num & 16384) > 1) {
            fileName.append(".jpg");
        } else {
            fileName.append(".avi");
        }
        file.fileName = fileName.toString();
        return file;
    }

    private short byte2short(byte[] files2, int startPos, int length) {
        int i = 2;
        if (length >= 2) {
            i = length;
        }
        byte[] newByte = new byte[i];
        System.arraycopy(files2, startPos, newByte, 0, length);
        return CameraStateUtil.bytesToShort(newByte, 0);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videoitem_list, container, false);
        if (view instanceof StickyListHeadersListView) {
            Context context = view.getContext();
            this.recyclerView = (StickyListHeadersListView) view;
            this.mAdapter = new HeaderListAdapter(getActivity(), this.mFileList, this.mListener);
            this.recyclerView.setAdapter(this.mAdapter);
            this.recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object item = parent.getItemAtPosition(position);
                    if ((item instanceof FileBeans) && VideoListFragment.this.mListener != null) {
                        VideoListFragment.this.mListener.onListFragmentInteraction((FileBeans) item);
                    }
                }
            });
            this.recyclerView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Object item = parent.getItemAtPosition(position);
                    if (!(item instanceof FileBeans)) {
                        return false;
                    }
                    VideoListFragment.this.showDeleteFile((FileBeans) item);
                    return true;
                }
            });
        }
        if (this.mFileList.size() <= 0) {
            if (CmdUtil.versionCompareTo("4.0") > 0) {
                loadFiles_JPG();
            } else {
                loadFiles();
            }
        }
        return view;
    }

    public void showDeleteFile(final FileBeans fileBeans) {
        new AlertDialog.Builder(getActivity()).setMessage(R.string.confirm_delete_file).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                VideoListFragment.this.deleteFile(fileBeans);
            }
        }).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).show();
    }

    /* access modifiers changed from: private */
    public void deleteFile(FileBeans fileBean) {
        boolean isDelSuccess;
        if (CmdUtil.versionCompareTo("4.0") > 0) {
            isDelSuccess = CmdManager.getInstance().deleteFile(fileBean.fileName);
        } else if (fileBean.fileType == 1) {
            isDelSuccess = FileUtils.deleteFile(Const.JPG_PATH + fileBean.fileName);
            Log.e("photo", isDelSuccess + "");
        } else {
            isDelSuccess = CmdManager.getInstance().deleteFile(fileBean.fileName);
            Log.e("photo1", isDelSuccess + "");
        }
        if (isDelSuccess) {
            this.mFileList.remove(fileBean);
            this.addFileList.remove(fileBean);
            this.mAdapter.notifyDataSetChanged();
            return;
        }
        Toast.makeText(getActivity(), R.string.delete_fail, 0).show();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof OnListFragmentInteractionListener) {
            this.mListener = (OnListFragmentInteractionListener) getActivity();
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            this.mListener = (OnListFragmentInteractionListener) context;
            return;
        }
        throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
    }

    public void onDetach() {
        super.onDetach();
        this.mListener = null;
    }
}
