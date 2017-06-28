package com.melodygram.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.melodygram.R;
import com.melodygram.activity.MusicFragmentActivity;
import com.melodygram.adapter.MusicCategoryAdapter;
import com.melodygram.database.MusicDataSource;
import com.melodygram.model.MusicCategory;

import java.util.ArrayList;

/**
 * Created by LALIT on 21-07-2016.
 */
public class MusicCategoryFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ArrayList<MusicCategory> catList;
    private MusicCategoryAdapter musicCategoryAdapter;
    private ListView catListView;
    private TextView titleTextView;
    private MusicFragmentActivity  musicFragmentActivity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_cat_layout, null);
        initView(view);
        return view;
    }
    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);

        if (context instanceof MusicFragmentActivity) {
            musicFragmentActivity = (MusicFragmentActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        musicFragmentActivity = null;
    }

    private void initView(View view) {
        catList = new ArrayList<>();
        titleTextView = (TextView) view.findViewById(R.id.title);
        catListView = (ListView) view.findViewById(R.id.cat_list);
        catListView.setOnItemClickListener(this);
        getAllMusicCatList();
    }

    private void getAllMusicCatList() {
        catList.clear();
        MusicDataSource musicDataSource = new MusicDataSource(getActivity());
        musicDataSource.open();
        ArrayList<MusicCategory> list = musicDataSource.getAllCategory();



        catList.addAll(list);
        musicDataSource.close();
        titleTextView.setText(getResources().getString(R.string.your_playlist) + "(" + catList.size() + " Albums)");
        musicCategoryAdapter = new MusicCategoryAdapter(catList, getActivity());
        catListView.setAdapter(musicCategoryAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getAllMusicCatList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        musicFragmentActivity.musicDownload.setVisibility(View.GONE);
        MusicListFragment musicListFragment = new MusicListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("CatId",catList.get(position).getCatId());
        bundle.putString("CatName",catList.get(position).getCatName());
        musicListFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(MusicListFragment.class.getName());
        fragmentTransaction.replace(R.id.container, musicListFragment).commit();
    }

}
