package com.melodygram.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.melodygram.R;
import com.melodygram.adapter.CountryAdapter;
import com.melodygram.model.CountryModel;
import com.melodygram.view.SideBarView;
import com.melodygram.view.OverlayView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;




public class CountryActivity extends Activity implements OnClickListener {
    ListView countryListView;
    String jsonLocation;
    ArrayList<CountryModel> countryDataArrayList;
    CountryModel countryListItemBean;
    CountryAdapter countryAdapter;
    SideBarView letterSideBarView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_country);
        countryInitialization();
        countryFunctionalities();
    }

    private void countryInitialization() {
        RelativeLayout checkParrent = (RelativeLayout) findViewById(R.id.settings_button);
        checkParrent.setVisibility(View.INVISIBLE);
        RelativeLayout countryHeaderBackParrent = (RelativeLayout) findViewById(R.id.header_back_button_parent);
        TextView countryHeaderText = (TextView) findViewById(R.id.header_name);
        countryListView = (ListView) findViewById(R.id.countryListViewId);
        countryHeaderText.setText(getResources().getString(R.string.country));
        countryHeaderBackParrent.setOnClickListener(this);
    }

    private void countryFunctionalities() {
        countryDataArrayList = new ArrayList<>();
        try {
                jsonLocation = AssetJSONFile("countries.json", CountryActivity.this);
            JSONObject jsonobject = new JSONObject(jsonLocation);
            JSONArray jarray = (JSONArray) jsonobject.getJSONArray("items");
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jb = (JSONObject) jarray.get(i);
                String country = jb.getString("country");
                String code = jb.getString("code");
                countryListItemBean = new CountryModel();
                countryListItemBean.setCountryName(country);
                countryListItemBean.setCountryCode(code);
                countryDataArrayList.add(countryListItemBean);
                if (countryDataArrayList.size() > 0) {
                    if (countryAdapter == null) {
                        countryAdapter = new CountryAdapter(this, countryDataArrayList);
                        countryListView.setAdapter(countryAdapter);
                        final LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        TextView overlayTextView = (TextView) OverlayView.initOverlay(layoutInflater, (WindowManager) getSystemService(Context.WINDOW_SERVICE));
                        overlayTextView.setVisibility(View.INVISIBLE);
                        letterSideBarView = (SideBarView) findViewById(R.id.letterSideBarViewId);
                            letterSideBarView.setVisibility(View.VISIBLE);
                            letterSideBarView.setListView(countryListView);
                            letterSideBarView.setTextView(overlayTextView);

                    } else {
                        countryAdapter.notifyDataSetChanged();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        countryListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {

                Intent intent = getIntent();
              String  country = countryDataArrayList.get(position).getCountryName();
                String   code = countryDataArrayList.get(position).getCountryCode();
                intent.putExtra("country", country);
                intent.putExtra("code", code);
                setResult(RESULT_OK, intent);
                finish();

            }
        });

    }

    public static String AssetJSONFile(String filename, Context context) throws IOException {
        AssetManager manager = context.getAssets();
        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();
        return new String(formArray);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_back_button_parent:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
     if (resultCode == RESULT_OK) {
            if (requestCode == LoginActivity.COUNTRY_SELECTION_RESULT) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}
