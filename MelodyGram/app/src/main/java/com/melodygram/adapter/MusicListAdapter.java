package com.melodygram.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.melodygram.R;
import com.melodygram.asyncTask.MusicDownloadAsync;
import com.melodygram.constants.ChatGlobalStates;
import com.melodygram.database.MusicDataSource;
import com.melodygram.model.Music;
import com.melodygram.singleton.AppController;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by LALIT on 23-06-2016.
 */
public class MusicListAdapter extends BaseAdapter implements MusicDownloadAsync.MusicInterfaceAsyncResponse {
    private List<Music> musicList;
    private Activity musicStoreActivity;
    private LayoutInflater mInflater;
    private AppController appController;
    private MusicDataSource musicDataSource;
    private int currentSelection = -1;

    public MusicListAdapter(List<Music> musicList, Activity musicStoreActivity) {
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
            viewHolder.trackPic = (CircleImageView) view.findViewById(R.id.track_pic);
            viewHolder.trackName = (TextView) view.findViewById(R.id.music_title);
            viewHolder.totalTracks = (TextView) view.findViewById(R.id.total_tracks);
            viewHolder.downloadMusic = (ImageView) view.findViewById(R.id.download_music);
            viewHolder.chatItemParent = (RelativeLayout) view.findViewById(R.id.parent_layout);
            viewHolder.downloadMusic.setVisibility(View.GONE);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Music musicCategory = musicList.get(position);
        viewHolder.trackName.setText(musicCategory.getName());
        musicDataSource.close();
        appController.displayUrlImage(viewHolder.trackPic, musicCategory.getPhoto(), null);
        if (currentSelection == position) {
            viewHolder.trackPic.setBorderColor(musicStoreActivity.getResources().getColor(R.color.app_color));
           viewHolder.chatItemParent.setBackgroundColor(musicStoreActivity.getResources().getColor(R.color.chat_user_pic_border));
            viewHolder.trackPic.setBorderWidth(1);
        } else {
           viewHolder.chatItemParent.setBackgroundColor(musicStoreActivity.getResources().getColor(R.color.white_overlay));
            viewHolder.trackPic.setBorderWidth(0);
        }
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
        ImageView downloadMusic;
        TextView trackName, totalTracks;
        CircleImageView trackPic;
        RelativeLayout chatItemParent;

    }

    public void setCurrentMusic(int currentSelection) {
        this.currentSelection = currentSelection;

    }
}
