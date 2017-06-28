package com.melodygram.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.melodygram.R;
import com.melodygram.activity.MusicStoreActivity;
import com.melodygram.asyncTask.MusicDownloadAsync;
import com.melodygram.constants.ChatGlobalStates;
import com.melodygram.database.MusicDataSource;
import com.melodygram.model.FriendsModel;
import com.melodygram.model.MusicCategory;
import com.melodygram.singleton.AppController;

import java.util.List;

/**
 * Created by LALIT on 23-06-2016.
 */
public class MusicCategoryAdapter extends BaseAdapter implements MusicDownloadAsync.MusicInterfaceAsyncResponse {
    private List<MusicCategory> musicList;
    private Activity musicStoreActivity;
    private LayoutInflater mInflater;
    private AppController appController;
    private MusicDataSource musicDataSource;

    public MusicCategoryAdapter(List<MusicCategory> musicList, Activity musicStoreActivity) {
        this.musicList = musicList;
        this.musicStoreActivity = musicStoreActivity;
        mInflater = (LayoutInflater) musicStoreActivity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        appController = AppController.getInstance();
        musicDataSource = new MusicDataSource(musicStoreActivity);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.music_cat_list_item, null);
            viewHolder.trackPic = (ImageView) view.findViewById(R.id.track_pic);
            viewHolder.trackName = (TextView) view.findViewById(R.id.music_title);
            viewHolder.totalTracks = (TextView) view.findViewById(R.id.total_tracks);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        MusicCategory musicCategory = musicList.get(position);
        viewHolder.trackName.setText(musicCategory.getCatName());
        viewHolder.totalTracks.setText(musicCategory.getTotalTracks() + " " + musicStoreActivity.getResources().getString(R.string.track));
        musicDataSource.close();
        appController.displayUrlImage(viewHolder.trackPic, musicCategory.getTumbImage(), null);
        return view;
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public void asyncFinished(String id) {
        ChatGlobalStates.musicDownload.remove(id);
        notifyDataSetChanged();
    }

    public class ViewHolder {
        ImageView trackPic;
        TextView trackName, totalTracks;


    }
}
