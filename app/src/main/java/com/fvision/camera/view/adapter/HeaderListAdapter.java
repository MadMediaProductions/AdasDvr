package com.fvision.camera.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.fvision.camera.R;
import com.fvision.camera.bean.FileBeans;
import com.fvision.camera.utils.CameraStateUtil;
import com.fvision.camera.view.fragment.VideoListFragment;
import java.text.SimpleDateFormat;
import java.util.List;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class HeaderListAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private Context mContext;
    private final VideoListFragment.OnListFragmentInteractionListener mListener;
    private final List<FileBeans> mValues;

    public HeaderListAdapter(Context context, List<FileBeans> items, VideoListFragment.OnListFragmentInteractionListener listener) {
        this.mValues = items;
        this.mListener = listener;
        this.mContext = context;
    }

    public int getCount() {
        if (this.mValues == null) {
            return 0;
        }
        return this.mValues.size();
    }

    public FileBeans getItem(int position) {
        return this.mValues.get(position);
    }

    public long getItemId(int position) {
        return this.mValues.get(position).sorttime;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.fragment_videoitem, (ViewGroup) null);
            holder.filename = (TextView) convertView.findViewById(R.id.filename);
            holder.delete = (TextView) convertView.findViewById(R.id.delete);
            holder.file_time = (TextView) convertView.findViewById(R.id.file_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FileBeans item = this.mValues.get(position);
        holder.file_time.setText(CameraStateUtil.longToString(item.dayTime, (String) null));
        holder.filename.setText(item.fileName);
        return convertView;
    }

    private String getTimeStr(int length) {
        return String.format("时长%02d分%02d秒", new Object[]{Integer.valueOf(length / 60), Integer.valueOf(length % 60)});
    }

    private String getSizeStr(int size) {
        return String.format("%.2fM", new Object[]{Double.valueOf(((double) size) / 1048576.0d)});
    }

    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.contact_header, parent, false);
            holder.text = (TextView) convertView;
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        new SimpleDateFormat(this.mContext.getResources().getString(R.string.day_format));
        holder.text.setText(CameraStateUtil.longToString(getItem(position).dayTime, this.mContext.getResources().getString(R.string.day_format)));
        return convertView;
    }

    public long getHeaderId(int position) {
        return (long) getItem(position).hour;
    }

    class HeaderViewHolder {
        TextView text;

        HeaderViewHolder() {
        }
    }

    class ViewHolder {
        TextView delete;
        TextView file_time;
        TextView filelength;
        TextView filename;
        TextView filesize;

        ViewHolder() {
        }
    }
}
