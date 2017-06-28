package com.melodygram.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import com.melodygram.activity.ChatActivity;
import com.melodygram.activity.OtherUserProfileActivity;
import com.melodygram.chatinterface.FriendUpdateInterface;
import com.melodygram.chatinterface.InterFaceBadgeCount;
import com.melodygram.chatinterface.InterfaceChatProfileClick;
import com.melodygram.database.AppContactDataSource;
import com.melodygram.database.ContactDataSource;
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
import java.util.jar.Pack200;

/**
 * Created by LALIT on 23-06-2016.
 */
public class ChatListAdapter extends BaseAdapter implements Filterable {
    private List<FriendsModel> friendsList;
    private List<FriendsModel> fixedFriendsList;
    private FilterName filterName;
    private Activity context;
    private LayoutInflater mInflater;
    private AppController appController;
    private FriendUpdateInterface friendUpdateInterface;
    AppContactDataSource contactDataSource;
    InterFaceBadgeCount interFaceBadgeCount;
    InterfaceChatProfileClick interfaceChatProfileClick;
    public ChatListAdapter(List<FriendsModel> friendsList, Activity context, FriendUpdateInterface friendUpdateInterface,InterFaceBadgeCount interFaceBadgeCount,InterfaceChatProfileClick interfaceChatProfileClick) {
        this.friendsList = friendsList;
        fixedFriendsList = new ArrayList<>();
        fixedFriendsList.addAll(friendsList);
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        appController = AppController.getInstance();
        this.friendUpdateInterface = friendUpdateInterface;
        this.interfaceChatProfileClick =interfaceChatProfileClick;
        contactDataSource = new AppContactDataSource(context);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.chat_list_item, null);
            viewHolder.badgeCount = (TextView) view.findViewById(R.id.badge_count);
            viewHolder.msg_time = (TextView) view.findViewById(R.id.user_msg_time);
            viewHolder.userPic = (ImageView) view.findViewById(R.id.user_pic);
            viewHolder.userName = (EmojiconTextView) view.findViewById(R.id.friendNameId);
            viewHolder.userName.setEmojiconSize(70);
            viewHolder.userMessage = (EmojiconTextView) view.findViewById(R.id.friendLastChat);
            viewHolder.userMessage.setEmojiconSize(70);
            viewHolder.deleteButton = (LinearLayout) view.findViewById(R.id.delete_button);
            viewHolder.locateButton = (LinearLayout) view.findViewById(R.id.locate_button);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.locateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendUpdateInterface.updateTimestamp(position);
            }
        });

        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                friendUpdateInterface.deleteFriend(position);
            }
        });
        if (position == 0)
        {
            viewHolder.locateButton.setVisibility(View.INVISIBLE);
        }
        else
        {
            viewHolder.locateButton.setVisibility(View.VISIBLE);
        }
        FriendsModel friendsModel = friendsList.get(position);
        String badgeCount = friendsModel.getSeenCount();

        if (badgeCount != null && !badgeCount.equalsIgnoreCase("0"))
        {
            viewHolder.badgeCount.setVisibility(View.VISIBLE);
            viewHolder.badgeCount.setText(badgeCount);
        } else
        {
            viewHolder.badgeCount.setVisibility(View.GONE);
        }
        viewHolder.userName.setText(friendsModel.getFriendName());
        viewHolder.userMessage.setText(friendsModel.getFriendsLastchat());
        viewHolder.deleteButton.setTag(friendsModel.getFriendsUserId());
        viewHolder.locateButton.setTag(friendsModel.getFriendsUserId());
        String chatTime = DateTimeUtil.lastChatDateOrTime(
                friendsModel.getFriendsLastChatTime());

        viewHolder.msg_time.setText(chatTime);
        String picPrivacy = friendsModel.getProfilePicPrivacy();
        viewHolder.userPic.setImageResource(R.drawable.default_profile_pic);

        if (picPrivacy != null && picPrivacy.equalsIgnoreCase("0"))
            appController.displayUrlImage(viewHolder.userPic, friendsModel.getFriendsPicIconUrl(), null);
        else  if (picPrivacy != null && picPrivacy.equalsIgnoreCase("2"))
        {
                 contactDataSource.open();

            if (contactDataSource.isContactAvailableOrNot(friendsModel.getFriendsPhoneNumber())) {
                appController.displayUrlImage(viewHolder.userPic, friendsModel.getFriendsPicIconUrl(), null);
            }else {

                viewHolder.userPic.setImageResource(R.drawable.default_profile_pic);
            }
            contactDataSource.close();
        }else
        {
            viewHolder.userPic.setImageResource(R.drawable.default_profile_pic);
        }

    viewHolder.userPic.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            interfaceChatProfileClick.chatProfilePicClick(position);
        }
    });
        return view;
    }

    @Override
    public Filter getFilter() {
        if (filterName == null)
            filterName = new FilterName();
        return filterName;
    }

    @Override
    public int getCount() {
        return friendsList.size();
    }

    @Override
    public Object getItem(int position) {
        return friendsList.get(position);
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
        TextView badgeCount, msg_time;
        ImageView userPic;
        EmojiconTextView userName, userMessage;
        LinearLayout deleteButton, locateButton;

    }
}
