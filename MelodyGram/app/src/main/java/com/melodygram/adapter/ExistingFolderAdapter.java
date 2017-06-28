package com.melodygram.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.melodygram.R;
import com.melodygram.singleton.AppController;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by LALIT on 17-06-2016.
 */
public class ExistingFolderAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<String> list;
    private AppController appController;

    public ExistingFolderAdapter(Context context, ArrayList<String> list) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = list;
        appController = AppController.getInstance();
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(
                    R.layout.existing_folder_item, null);
            holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
            holder.deleteImage = (ImageView) convertView.findViewById(R.id.delete_button);
            holder.deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file = new File(list.get(position));
                    if (file != null && file.isFile()) {
                        file.delete();
                        list.remove(position);
                        notifyDataSetChanged();
                    }
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        appController.displayFileImage(holder.imageview, list.get(position),null);
        return convertView;
    }
}

class ViewHolder {
    ImageView imageview, deleteImage;
}