package com.melodygram.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.melodygram.R;
import com.melodygram.adapter.ExistingFolderAdapter;
import com.melodygram.constants.GlobalState;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by LALIT on 17-06-2016.
 */
public class ChooseFolderActivity extends MelodyGramActivity {
    private ArrayList<String> list;
    private File[] listFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.existing_folder);
        (findViewById(R.id.settings_button)).setVisibility(View.INVISIBLE);
        TextView headerText = (TextView) findViewById(R.id.header_name);
        headerText.setText(getString(R.string.old_profile));
        RelativeLayout backButton = (RelativeLayout) findViewById(R.id.header_back_button_parent);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getImages();
        ExistingFolderAdapter existingFolderAdapter = new ExistingFolderAdapter(this, list);
        GridView imageGrid = (GridView) findViewById(R.id.image_grid);
        imageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("path", list.get(position));
                setResult(RESULT_OK, intent);
                finish();

            }
        });
        imageGrid.setAdapter(existingFolderAdapter);
    }

    public void getImages() {
        list = new ArrayList<>();
        File file = new File(GlobalState.PROFILE_PICTURE);
        if (file.isDirectory()) {
            listFile = file.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                if(!listFile[i].getAbsolutePath().contains(".nomedia"))
                list.add(listFile[i].getAbsolutePath());
            }
        }
    }
}
