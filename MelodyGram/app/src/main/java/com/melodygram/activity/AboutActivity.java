package com.melodygram.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.melodygram.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by LALIT on 15-07-2016.
 */
public class AboutActivity extends MelodyGramActivity {

     TextView tvPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.splash_screen_layout);
        tvPrivacy=(TextView)findViewById(R.id.tv_privacy_policy);
        findViewById(R.id.about_parent).setVisibility(View.VISIBLE);
        PackageManager manager = getPackageManager();
        PackageInfo info;
        String version = null;
        try {
            info = manager.getPackageInfo(
                    getPackageName(), 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        tvPrivacy.setVisibility(View.VISIBLE);

        tvPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ReadFromAssets();
                Intent intent = new Intent(AboutActivity.this,PrivacyPolicyActivity.class);
                startActivity(intent);
            }
        });
        if (version != null)
            ((TextView) findViewById(R.id.version)).setText(getResources().getString(R.string.version_no) + " " + version);
    }


    private void ReadFromAssets()
    {
        AssetManager assetManager = getAssets();

        InputStream in = null;
        OutputStream out = null;
        File file = new File(getFilesDir(), "melodyGram_privacy_policy.pdf");
        try
        {
            in = assetManager.open("melodyGram_privacy_policy.pdf");
            out = openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);

            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e)
        {
            Log.e("tag", e.getMessage());
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(
                Uri.parse("file://" + getFilesDir() + "/melodyGram_privacy_policy.pdf"),
                "application/pdf");

        startActivity(intent);
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, read);
        }
    }
}
