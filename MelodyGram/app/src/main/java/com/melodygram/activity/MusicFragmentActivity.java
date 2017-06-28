package com.melodygram.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.melodygram.R;
import com.melodygram.chatinterface.MusicMergeInterface;
import com.melodygram.constants.GlobalState;
import com.melodygram.database.MusicDataSource;
import com.melodygram.fragment.MusicCategoryFragment;
import com.melodygram.fragment.MusicListFragment;
import com.melodygram.model.MusicCategory;
import com.melodygram.singleton.AppController;

import java.util.ArrayList;

/**
 * Created by LALIT on 21-07-2016.
 */
public class MusicFragmentActivity extends FragmentActivity implements View.OnClickListener, MusicMergeInterface {

    private TextView headerTextView;
    private String userName;
public ImageView musicDownload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.music_fragment_layout);
        initView();

    }
    private void initView() {
        userName = getIntent().getStringExtra("userName");
        musicDownload = (ImageView) findViewById(R.id.settings_button_image);
        musicDownload.setImageResource(R.drawable.cart);

        MusicDataSource musicDataSource = new MusicDataSource(MusicFragmentActivity.this);
        musicDataSource.open();
        ArrayList<MusicCategory> list = musicDataSource.getAllCategory();
        musicDataSource.close();

        if(list!=null && list.size()>0)
        {
            musicDownload.setVisibility(View.INVISIBLE);
        }else {
            musicDownload.setVisibility(View.VISIBLE);
        }
        findViewById(R.id.settings_button).setOnClickListener(this);
        findViewById(R.id.header_back_button_parent).setOnClickListener(this);
        findViewById(R.id.header_name).setOnClickListener(this);
        headerTextView = (TextView) findViewById(R.id.header_name);
        headerTextView.setText(userName);
        MusicCategoryFragment musicFragment = new MusicCategoryFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(MusicCategoryFragment.class.getName());
        fragmentTransaction.replace(R.id.container, musicFragment).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_back_button_parent:
                if (getSupportFragmentManager().getBackStackEntryCount() == 2) {
                    MusicListFragment fragment = (MusicListFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                    fragment.stopMusic();
                    fragment.stopRecording();
                    getSupportFragmentManager().popBackStackImmediate();
                    headerTextView.setText(userName);
                    musicDownload.setVisibility(View.VISIBLE);
                } else {
                    finish();
                }
                break;
            case R.id.header_name:
                break;
            case R.id.settings_button:
                Intent musicIntent = new Intent(MusicFragmentActivity.this, MusicStoreActivity.class);
                startActivityForResult(musicIntent, GlobalState.REQUEST_MUSIC_DOWNLOAD);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 2) {
            MusicListFragment fragment = (MusicListFragment) getSupportFragmentManager().findFragmentById(R.id.container);
            fragment.stopMusic();
            getSupportFragmentManager().popBackStack();
            headerTextView.setText(userName);
            musicDownload.setVisibility(View.VISIBLE);

        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        fragment.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void sendMusic(String path) {
        //set result with path
        Intent intent = new Intent();
        intent.putExtra("filePath",path);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    protected void onResume() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 2) {
            MusicListFragment fragment = (MusicListFragment) getSupportFragmentManager().findFragmentById(R.id.container);
            fragment.stopMusic();
            fragment.stopRecording();
        }
        super.onResume();


    }


}
