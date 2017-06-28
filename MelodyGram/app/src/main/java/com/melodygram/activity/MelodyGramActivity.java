package com.melodygram.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * Created by LALIT on 10-08-2016.
 */
public class MelodyGramActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
    }
}
