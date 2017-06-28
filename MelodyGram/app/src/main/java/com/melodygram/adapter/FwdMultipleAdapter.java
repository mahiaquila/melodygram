package com.melodygram.adapter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.melodygram.R;
import com.melodygram.model.FriendsModel;
import com.melodygram.singleton.AppController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class FwdMultipleAdapter extends BaseAdapter {
    private List<FriendsModel> friendsArrayList;
    private LinkedHashMap<String, FriendsModel> toBeSelectList;
    private LayoutInflater mInflater;
    private AppController appController;

    public FwdMultipleAdapter(Activity activityRef, List<FriendsModel> friendsArrayList, LinkedHashMap<String, FriendsModel> toBeSelectList) {
        this.friendsArrayList = friendsArrayList;
        this.toBeSelectList = toBeSelectList;
        appController = AppController.getInstance();
        mInflater = (LayoutInflater) activityRef.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return friendsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return friendsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolderItem {
        TextView appUserName;
        ImageView appUserPic;
        ImageView selectionButton,selectionButtonUnselected;
        RelativeLayout selectionParent;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolderItem viewHolder;
        View listRow = convertView;
        if (convertView == null) {
            listRow = mInflater.inflate(R.layout.multi_fwd_list_item, null);
            viewHolder = new ViewHolderItem();
            viewHolder.appUserName = (TextView) listRow.findViewById(R.id.appUserName);
            viewHolder.appUserPic = (ImageView) listRow.findViewById(R.id.appUserPic);
            viewHolder.selectionButton = (ImageView) listRow.findViewById(R.id.uncheckedIcon);
            viewHolder.selectionButtonUnselected = (ImageView) listRow.findViewById(R.id.uncheckedIconUnselected);
            viewHolder.selectionParent = (RelativeLayout) listRow.findViewById(R.id.selection_parent);

            listRow.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) listRow.getTag();
        }
        FriendsModel customer = friendsArrayList.get(position);
        viewHolder.appUserName.setText(customer.getFriendName());
        appController.displayUrlImage(viewHolder.appUserPic, customer.getFriendsPicIconUrl(), null);
        if (!toBeSelectList.containsKey(customer.getFriendsRoomId())) {
            viewHolder.selectionButton.setVisibility(View.GONE);
            viewHolder.selectionButtonUnselected.setVisibility(View.VISIBLE);
        } else {
            viewHolder.selectionButton.setVisibility(View.VISIBLE);
            viewHolder.selectionButtonUnselected.setVisibility(View.GONE);
        }
        viewHolder.selectionParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FriendsModel customerToSelect = friendsArrayList.get(position);
                if (viewHolder.selectionButtonUnselected.isShown())
                {
                    viewHolder.selectionButtonUnselected.setVisibility(View.GONE);
                    viewHolder.selectionButton.setVisibility(View.VISIBLE);
                    toBeSelectList.put(customerToSelect.getFriendsRoomId(), customerToSelect);
                }
                else {
                    viewHolder.selectionButtonUnselected.setVisibility(View.VISIBLE);
                    viewHolder.selectionButton.setVisibility(View.GONE);
                    toBeSelectList.remove(customerToSelect.getFriendsRoomId());
                }
            }
        });
        return listRow;
    }
}
