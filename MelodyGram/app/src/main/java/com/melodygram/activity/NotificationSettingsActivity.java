package com.melodygram.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.melodygram.R;
import com.melodygram.singleton.AppController;

/**
 * Created by LALIT on 15-07-2016.
 */
public class NotificationSettingsActivity extends MelodyGramActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private ToggleButton soundButton, vibrateButton;
    private AppController appController;
    private ListView toneListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.notification_settings_layout);
        appController = AppController.getInstance();
        findViewById(R.id.settings_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.header_back_button_parent).setOnClickListener(this);
        ((TextView) findViewById(R.id.header_name)).setText(R.string.settings);
        toneListView = ((ListView) findViewById(R.id.tone_list));
        toneListView.setAdapter(new ToneAdapter());
        soundButton = (ToggleButton) findViewById(R.id.sound_on_off_button);
        soundButton.setOnCheckedChangeListener(this);
        vibrateButton = (ToggleButton) findViewById(R.id.vibrate_on_off_button);
        vibrateButton.setOnCheckedChangeListener(this);
        initToggleButton();
    }


    private void initToggleButton() {
        vibrateButton.setChecked(appController.getPrefs().getBoolean("vibrate", true));
        soundButton.setChecked(appController.getPrefs().getBoolean("sound", true));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_back_button_parent:
                finish();
                break;

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sound_on_off_button:
                appController.getPrefs().edit().putBoolean("sound", isChecked).commit();
                break;
            case R.id.vibrate_on_off_button:
                appController.getPrefs().edit().putBoolean("vibrate", isChecked).commit();

                break;
        }

    }


    private class ToneAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private int selected;
        int[] toneList = {R.raw.auto,R.raw.sent};
        public ToneAdapter() {
            mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            selected = appController.getPrefs().getInt("tone", R.raw.auto);
        }

        @Override
        public int getCount() {
            return toneList.length;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = mInflater.inflate(R.layout.tone_layout, null);
            TextView toneName = (TextView) convertView.findViewById(R.id.tone_name);
            final RadioButton countrySelectedIcon = (RadioButton) convertView.findViewById(R.id.radio_button);
            countrySelectedIcon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selected = toneList[position];
                        setTone(selected);
                    }
                    notifyDataSetChanged();
                }
            });
            String toneNameTemp = getResources().getResourceEntryName(toneList[position]);
            toneName.setText(toneNameTemp.substring(0, 1).toUpperCase() + toneNameTemp.substring(1));
            if (selected == toneList[position])
                countrySelectedIcon.setChecked(true);
            else {
                countrySelectedIcon.setChecked(false);
            }
            return convertView;
        }

        private void setTone(int value) {
            appController.getPrefs().edit().putInt("tone", value).commit();
        }
    }
}
