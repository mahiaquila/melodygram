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

package com.melodygram.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.melodygram.R;
import com.melodygram.adapter.StickersAdapter;
import com.melodygram.model.ChatSticker;
import com.melodygram.model.Sticker;

import java.util.ArrayList;


/**
 * Created by LALIT on 15-06-2016.
 */
public class StickersGridFragment extends Fragment implements AdapterView.OnItemClickListener {
    private OnStickerClickedListener mOnStickerClickedListener;
    private ArrayList<ChatSticker> mData;

    protected static StickersGridFragment newInstance(ArrayList<ChatSticker> stickers) {
        StickersGridFragment emojiGridFragment = new StickersGridFragment();
        Bundle args = new Bundle();
        args.putSerializable("stickersicons", stickers);
        emojiGridFragment.setArguments(args);
        return emojiGridFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stickers_grid, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GridView gridView = (GridView) view.findViewById(R.id.stickers_grid);
        mData = ( ArrayList<ChatSticker>)getArguments().getSerializable("stickersicons");
        gridView.setAdapter(new StickersAdapter(view.getContext(), mData));
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("stickersicons", mData);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnStickerClickedListener) {
            mOnStickerClickedListener = (OnStickerClickedListener) activity;
        } else if(getParentFragment() instanceof OnStickerClickedListener) {
            mOnStickerClickedListener = (OnStickerClickedListener) getParentFragment();
        } else {
            throw new IllegalArgumentException(activity + " must implement interface " + OnStickerClickedListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        mOnStickerClickedListener = null;
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mOnStickerClickedListener != null) {
            mOnStickerClickedListener.onStickerClicked((ChatSticker) parent.getItemAtPosition(position));
        }
    }

    public interface OnStickerClickedListener {
        void onStickerClicked(ChatSticker sticker);
    }
}
