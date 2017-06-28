/*
 * Copyright 2014 Hieu Rocker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.melodygram.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.melodygram.R;
import com.melodygram.model.ChatSticker;
import com.melodygram.model.Sticker;
import com.melodygram.singleton.AppController;

import java.util.ArrayList;

/**
 * Created by LALIT on 14-07-2016.
 */
public class StickersAdapter extends ArrayAdapter<ChatSticker> {
    private  AppController appController;
    public StickersAdapter(Context context, ArrayList<ChatSticker> data) {
        super(context, R.layout.sticker_item, data);
        appController = AppController.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = View.inflate(getContext(), R.layout.sticker_item, null);
            ViewHolder holder = new ViewHolder();
            holder.icon = (ImageView) v.findViewById(R.id.sticker_icon);
            v.setTag(holder);
        }
        ChatSticker sticker = getItem(position);
        ViewHolder holder = (ViewHolder) v.getTag();
        appController.displayUrlImage( holder.icon,sticker.getStickersPic(),null);
        return v;
    }



    class ViewHolder {
        ImageView icon;
    }
}