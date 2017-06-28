package com.melodygram.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.melodygram.R;
import com.melodygram.chatinterface.FriendUpdateInterface;
import com.melodygram.database.FriendsDataSource;
import com.melodygram.emojicon.EmojiconTextView;
import com.melodygram.fragment.ChatFragment;
import com.melodygram.model.FriendsModel;
import com.melodygram.singleton.AppController;
import com.melodygram.utils.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by LALIT on 23-06-2016.
 */
public class BlockAdapter extends BaseAdapter {
    private List<FriendsModel> friendsList;
    private List<FriendsModel> fixedFriendsList;
    private FilterName filterName;
    private Activity context;
    private LayoutInflater mInflater;
    private AppController appController;
    private FriendUpdateInterface friendUpdateInterface;

    public BlockAdapter(List<FriendsModel> friendsList, Activity context) {
        this.friendsList = friendsList;
        fixedFriendsList = new ArrayList<>();
        fixedFriendsList.addAll(friendsList);
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        appController = AppController.getInstance();
        this.friendUpdateInterface = (FriendUpdateInterface) context;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.block_list_item, null);
            viewHolder.userPic = (ImageView) view.findViewById(R.id.user_pic);
            viewHolder.blockButton = (ImageView) view.findViewById(R.id.block_icon);
            viewHolder.userName = (EmojiconTextView) view.findViewById(R.id.user_name);
            viewHolder.userName.setEmojiconSize(40);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.blockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendUpdateInterface.deleteFriend(position);
            }
        });
        FriendsModel friendsModel = friendsList.get(position);
        viewHolder.userName.setText(friendsModel.getFriendName());
        appController.displayUrlImage(viewHolder.userPic, friendsModel.getFriendsPicIconUrl(), null);
        return view;
    }


    @Override
    public int getCount() {
        return friendsList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    private class FilterName extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            ArrayList<FriendsModel> list = new ArrayList<>();
            for (int i = 0; i < fixedFriendsList.size(); i++) {
                if (fixedFriendsList.get(i).getFriendAppName()
                        .toLowerCase(Locale.getDefault()).contains(constraint)) {
                    list.add(fixedFriendsList.get(i));
                }
            }
            results.count = list.size();
            results.values = list;
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            friendsList = (ArrayList<FriendsModel>) results.values;
            notifyDataSetChanged();
        }
    }

    class ViewHolder {
        ImageView userPic,blockButton;
        EmojiconTextView userName;
    }
}
