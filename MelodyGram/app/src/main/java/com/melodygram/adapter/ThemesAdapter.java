package com.melodygram.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.melodygram.R;


/**
 * Created by LALIT on 14-07-2016.
 */
public class ThemesAdapter extends PagerAdapter {
    private LayoutInflater inflater;
    private int[] themesList;
    private Activity context;
    public ThemesAdapter(Activity context, int[] themesList) {
        inflater = LayoutInflater.from(context);
        this.themesList = themesList;
        this.context = context;

    }

    @Override
    public int getCount() {
        return themesList.length;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, final int position) {
        View itemView = inflater.inflate(R.layout.themes_layout, collection, false);
        ImageView themeImage = (ImageView) itemView.findViewById(R.id.theme_image);

        Button setButton = (Button) itemView.findViewById(R.id.set_theme_button);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getSharedPreferences("melodygramPref",
                        Context.MODE_PRIVATE).edit().putInt("theme",position).commit();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", position);
                context.setResult(Activity.RESULT_OK, returnIntent);
                context.finish();
            }
        });
        themeImage.setImageResource(themesList[position]);
        collection.addView(itemView);
        return itemView;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }
}
