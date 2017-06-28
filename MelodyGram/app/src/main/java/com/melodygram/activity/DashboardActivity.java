package com.melodygram.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.melodygram.R;
import com.melodygram.adapter.HomeFragmentPagerAdapter;
import com.melodygram.fragment.ChatFragment;
import com.melodygram.fragment.ContactsFragment;
import com.melodygram.fragment.ProfileFragment;
import com.melodygram.services.AppUpdateIntentService;
import com.melodygram.services.ContactUpdateSyncService;
import com.melodygram.singleton.AppController;
import com.melodygram.swipelistview.SwipeListViewTouchListener;
import com.melodygram.view.CustomViewPager;

import static android.Manifest.permission.READ_CONTACTS;
import static android.R.attr.name;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LALIT on 15-06-2016.
 */


public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private int pagerPosition;
    private PopupWindow popup;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView menuButton;
    TextView tvNotificationCount;
    private ChatFragment chatFragment;
    private ProfileFragment profileFragment;
    private ContactsFragment contactFragment;
    private static final int REQUEST_READ_CONTACTS = 0;
    private Tracker mTracker;
    private String TAG = DashboardActivity.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.dashboard_acitivty);
        //   Contacts.getLookupUri(getContentResolver(), Contacts.CONTENT_URI);

        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();
        // [END shared_tracker]

        getSupportActionBar().hide();
        Intent contactsUpdateService = new Intent(this, ContactUpdateSyncService.class);
        startService(contactsUpdateService);
        initialization();
        initTab();

    }



    private void initialization() {
         checkPermissions();
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tvNotificationCount = (TextView) findViewById(R.id.tv_notification_count);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        final TextView appNameTop = (TextView) findViewById(R.id.app_name);
        final RelativeLayout tabParentView = (RelativeLayout) findViewById(R.id.tab_parent);
        final RelativeLayout backButton = (RelativeLayout) findViewById(R.id.back_button_parent);
        final RelativeLayout profileScreenHeader = (RelativeLayout) findViewById(R.id.profile_screen_parent);
        backButton.setOnClickListener(this);
        menuButton = (ImageView) findViewById(R.id.menu_icon);
        //   menuButton.setVisibility(View.GONE);
        menuButton.setOnClickListener(this);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        //  viewPager.setPagingEnabled(false);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (profileFragment != null) {
                    profileFragment.updateStatusUserName();
                }

            }

            @Override
            public void onPageSelected(int position) {
                pagerPosition = position;
                menuButton.setVisibility(View.VISIBLE);

            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initTab() {
        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.tab_content, null);
        tabOne.setText(getString(R.string.contacts));
        tabOne.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.contacts, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);
        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.tab_content, null);
        tabTwo.setText(getString(R.string.chat));
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.chat_icon, 0, 0);
        tabTwo.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        tabLayout.getTabAt(1).setCustomView(tabTwo);
        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.tab_content, null);
        tabThree.setText(getString(R.string.profile));
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.profile, 0, 0);
        tabThree.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        tabLayout.getTabAt(2).setCustomView(tabThree);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button_parent:
                viewPager.setCurrentItem(1);
                break;
            case R.id.menu_icon:
                initPopUpWindow();
                break;
            case R.id.settings_button:
                if (popup != null && popup.isShowing())
                    popup.dismiss();
                Intent intent = new Intent(DashboardActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ContactsFragment(), "Contacts");
        adapter.addFrag(new ChatFragment(), "Chat");
        adapter.addFrag(new ProfileFragment(), "Profile");
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
//        String[] titleArray = {"Contacts", "Chats", "Profile"};
//        Fragment[] fragmentArray = {new ContactsFragment(), new ChatFragment(), new ProfileFragment()};
//        HomeFragmentPagerAdapter adapter = new HomeFragmentPagerAdapter(DashboardActivity.this.getSupportFragmentManager(), titleArray, fragmentArray);
//        viewPager.setAdapter(adapter);
//        tabLayout.setupWithViewPager(viewPager);

    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    contactFragment = (ContactsFragment) mFragmentList.get(position);
                    break;
                case 1:
                    chatFragment = (ChatFragment) mFragmentList.get(position);
                    break;
                case 2:
                    profileFragment = (ProfileFragment) mFragmentList.get(position);
                    break;
            }
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void initPopUpWindow() {
        if (popup != null && popup.isShowing()) {
            popup.dismiss();
        } else {
            LayoutInflater inflater = LayoutInflater.from(this);
            View popupView = inflater.inflate(R.layout.home_screen_popup, null);
            popupView.findViewById(R.id.settings_button).setOnClickListener(this);
            popup = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            popup.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
            popup.setOutsideTouchable(true);
            popup.setFocusable(true);
            popup.showAsDropDown(menuButton, -132, -20);
        }
    }
    private DashBoardBroadcastReceiver dashBoardBroadcastReceiver;

    public class DashBoardBroadcastReceiver extends BroadcastReceiver {
        public static final String ACTION = "com.melodygram.activity.DashBoardBroadcastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            String broadcastType = intent.getExtras().getString("broadcast_type");
            if (broadcastType.equalsIgnoreCase("contacts") && contactFragment != null) {
                contactFragment.getNewAppContacts();
            } else if (broadcastType.equalsIgnoreCase("chat") && chatFragment != null) {
                chatFragment.getServerList();
            }
        }
    }

    private void registerReceiver() {
        dashBoardBroadcastReceiver = new DashBoardBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(DashBoardBroadcastReceiver.ACTION);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(dashBoardBroadcastReceiver, intentFilter);
    }


    private void unRegisterReceiver() {

        try{
            if(dashBoardBroadcastReceiver!=null)
                unregisterReceiver(dashBoardBroadcastReceiver);
        }catch(Exception e)
        {

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "DashboardActivity: " + name);
        mTracker.setScreenName("Dashboard");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        if ( chatFragment != null) {
            chatFragment.getServerList();
        }
        registerReceiver();
        registerCountReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (viewPager.getCurrentItem() == 0) {
            if (contactFragment != null)
                contactFragment.getAppContacts();
        } else if (viewPager.getCurrentItem() == 1) {
            if (chatFragment != null)
                chatFragment.getServerList();
        }
    }

    @Override
    protected void onPause() {

        unRegisterReceiver();
        unRegisterCountReceiver();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (pagerPosition == 2) {
            viewPager.setCurrentItem(1);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("DashBoardActivity","Camera Dashboard");
        if(profileFragment!=null) {
            profileFragment.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.VIBRATE, Manifest.permission.CAMERA, Manifest.permission.SEND_SMS}, 100);
                    return;
                }
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            100);
                }
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            100);
                }
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            100);
                }
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            100);
                }
                if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.INTERNET}, 100);
                }
                if (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                            100);
                }
                if (checkSelfPermission(Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.VIBRATE}, 100);
                }
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.CAMERA}, 100);
                }
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.RECORD_AUDIO}, 100);
                }
                if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.SEND_SMS}, 100);
                }
                if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.SEND_SMS}, 100);
                }


            }
        }
    }

    private BadgeCountBroadcastReceiver badgeCountBroadcastReceiver;

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted , Access contacts here or do whatever you need.
            }
        }
    }

    private void registerCountReceiver() {
        badgeCountBroadcastReceiver = new BadgeCountBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BadgeCountBroadcastReceiver.ACTION);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(badgeCountBroadcastReceiver, intentFilter);
    }

    private void unRegisterCountReceiver() {

        try{
            if(badgeCountBroadcastReceiver!=null)
                unregisterReceiver(badgeCountBroadcastReceiver);
        }catch(Exception e)
        {

        }

    }

    public class BadgeCountBroadcastReceiver extends BroadcastReceiver {
        public static final String ACTION = "com.melodygram.activity.BatchBroadcastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                int badgeCount = Integer.parseInt(intent.getStringExtra("count"));

                if (badgeCount > 0 && String.valueOf(badgeCount) != null) {
                    tvNotificationCount.setVisibility(View.VISIBLE);
                    tvNotificationCount.setText(intent.getStringExtra("count"));
                } else {
                    tvNotificationCount.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}
