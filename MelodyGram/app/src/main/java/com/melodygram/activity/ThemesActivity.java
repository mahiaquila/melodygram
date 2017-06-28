package com.melodygram.activity;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.melodygram.R;
import com.melodygram.adapter.ThemesAdapter;


public class ThemesActivity extends MelodyGramActivity
{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes);
        initialize();
    }

    private void initialize()
    {
//        int[] themesList = { R.drawable.chat_default_bg,
//                R.drawable.one_theme,
//                R.drawable.two_theme,
//                R.drawable.three_theme,
//                R.drawable.four_theme,
//                R.drawable.five_theme,
//                R.drawable.six_theme, R.drawable.seven_theme, R.drawable.eight_theme, R.drawable.nine_theme, R.drawable.ten_theme, R.drawable.eleven_theme, R.drawable.twelve_theme, R.drawable.threeten_theme, R.drawable.fourteen_theme, R.drawable.fifteen_theme, R.drawable.sixteen_theme, R.drawable.seventeen_theme, R.drawable.eighteen_theme, R.drawable.nineteen_theme
//        };
//        ((TextView) findViewById(R.id.header_name)).setText(getResources().getString(R.string.chat_theme));
//        findViewById(R.id.settings_button).setVisibility(View.INVISIBLE);
//        findViewById(R.id.header_back_button_parent).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//                finish();
//            }
//        });
//
//        ViewPager viewPager = (ViewPager) findViewById(R.id.themes_pager);
//        ThemesAdapter themesAdapter = new ThemesAdapter(ThemesActivity.this,themesList);
//        viewPager.setAdapter(themesAdapter);

    }
}
