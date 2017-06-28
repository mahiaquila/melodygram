package com.melodygram.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.melodygram.R;
import com.melodygram.model.CountryModel;
import com.melodygram.view.SideBarView;


public class CountryAdapter extends BaseAdapter implements SectionIndexer {

    Activity activityRef;

    ArrayList<CountryModel> countryDataArrayList;

    public CountryAdapter(Activity activityRef, ArrayList<CountryModel> countryDataArrayList) {
        this.activityRef = activityRef;
        this.countryDataArrayList = countryDataArrayList;

    }
    @Override
    public int getCount() {
        return countryDataArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return countryDataArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) activityRef.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.country_list_item, null);
        TextView countryText = (TextView) convertView.findViewById(R.id.countryTextId);
        ImageView countrySelectedIcon = (ImageView) convertView.findViewById(R.id.countrySelectedIconId);
        countryText.setText(countryDataArrayList.get(position).getCountryName());

            countrySelectedIcon.setVisibility(View.GONE);

        return convertView;
    }
    String mSectionName = "";
    @Override
    public int getPositionForSection(int section) {
        for (int i = 0; i < countryDataArrayList.size(); i++) {
            mSectionName = countryDataArrayList.get(i).getCountryName();
            if (!mSectionName.equalsIgnoreCase(" ") && mSectionName != null && mSectionName.length() != 0) {

                final char firstChar = mSectionName.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        String[] chars = new String[SideBarView.mLetter.length];
        for (int i = 0; i < SideBarView.mLetter.length; i++) {
            chars[i] = String.valueOf(SideBarView.mLetter[i]);
        }

        return chars;
    }

}
