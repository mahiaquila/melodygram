package com.melodygram.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.os.AsyncTask;
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
import com.melodygram.constants.APIConstant;
import com.melodygram.constants.ChatGlobalStates;
import com.melodygram.constants.GlobalState;
import com.melodygram.database.DbSqliteHelper;
import com.melodygram.database.MusicDataSource;
import com.melodygram.model.FriendsModel;
import com.melodygram.model.Music;
import com.melodygram.model.MusicCategory;
import com.melodygram.singleton.AppController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LALIT on 23-06-2016.
 */
public class MusicStoreAdapter extends BaseAdapter implements MusicDownloadAsync.MusicInterfaceAsyncResponse {
    private List<MusicCategory> musicList;
    private MusicStoreActivity musicStoreActivity;
    private LayoutInflater mInflater;
    private AppController appController;
    private MusicDataSource musicDataSource;


    public MusicStoreAdapter(List<MusicCategory> musicList, MusicStoreActivity musicStoreActivity) {
        this.musicList = musicList;
        this.musicStoreActivity = musicStoreActivity;
        mInflater = (LayoutInflater) musicStoreActivity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        appController = AppController.getInstance();
        musicDataSource = new MusicDataSource(musicStoreActivity);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.music_store_list_item, null);
            viewHolder.trackPic = (ImageView) view.findViewById(R.id.track_pic);
            viewHolder.downloadButton = (ImageView) view.findViewById(R.id.download_button);
            viewHolder.trackName = (TextView) view.findViewById(R.id.music_title);
            viewHolder.totalTracks = (TextView) view.findViewById(R.id.total_tracks);
            viewHolder.freePaidTrack = (TextView) view.findViewById(R.id.free_paid_status);
            viewHolder.downloadProgress = (ProgressBar) view.findViewById(R.id.download_progress);
            viewHolder.tvProgressUpdateValue=(TextView)view.findViewById(R.id.tv_progress_updatevalue);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.musicStoreAdapter = this;
        final ViewHolder viewHolderFinal = viewHolder;
        viewHolder.downloadButton.setTag(position);
        viewHolder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatGlobalStates.musicDownload.put(musicList.get((Integer) v.getTag()).getCatId(), viewHolderFinal);
                musicStoreActivity.downloadMusic(0);
                new MusicDownloadAsync(musicList.get((Integer) v.getTag()), musicStoreActivity,viewHolder.tvProgressUpdateValue).execute();

            }
        });
        MusicCategory musicCategory = musicList.get(position);
        viewHolder.trackName.setText(musicCategory.getCatName());
        viewHolder.totalTracks.setText(musicCategory.getTotalTracks() + " " + musicStoreActivity.getResources().getString(R.string.track));
        if (musicCategory.getIsFree().equalsIgnoreCase("yes"))
            viewHolder.freePaidTrack.setText("Free");
        else
            viewHolder.freePaidTrack.setText(musicCategory.getCost());
        musicDataSource.open();
        if (musicDataSource.isCatAvailable(musicCategory.getCatId())) {
            viewHolder.downloadButton.setVisibility(View.GONE);
            viewHolder.downloadProgress.setVisibility(View.GONE);
        } else {
            if (ChatGlobalStates.musicDownload.containsKey(musicCategory.getCatId())) {
                ChatGlobalStates.musicDownload.put(musicCategory.getCatId(), viewHolderFinal);
                viewHolder.downloadProgress.setVisibility(View.VISIBLE);
                viewHolder.downloadButton.setVisibility(View.GONE);

            } else {
                viewHolder.downloadButton.setVisibility(View.VISIBLE);
                viewHolder.downloadProgress.setVisibility(View.GONE);
            }
        }
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
        TextView trackName, totalTracks, freePaidTrack;
        ImageView downloadButton;
        ProgressBar downloadProgress;
        public MusicStoreAdapter musicStoreAdapter;
        private TextView tvProgressUpdateValue;
    }




}
