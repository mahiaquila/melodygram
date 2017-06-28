package com.melodygram.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.melodygram.R;
import com.melodygram.database.CategoryDataSource;
import com.melodygram.model.ChatSticker;
import com.melodygram.model.ChatStickerCategory;
import com.melodygram.model.Sticker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by LALIT on 14-06-2016.
 */




public class StickersFragment extends android.support.v4.app.Fragment implements ViewPager.OnPageChangeListener {
    private int mEmojiTabLastSelectedIndex = -1;
    private View[] mStickersTabs;

    private ArrayList<ChatSticker> stickerListOne = new ArrayList<>();
    private ArrayList<ChatSticker> stickerListTwo = new ArrayList<>();
    private ArrayList<ChatSticker> stickerListThree = new ArrayList<>();
    private ArrayList<ChatSticker> stickerListFour = new ArrayList<>();
    private ArrayList<ChatSticker> stickerListFive = new ArrayList<>();
    private OnStickerButtonClickedListener onStickerButtonClickedListener;
    private ViewPager stickerPager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stickers_layout, container, false);
        stickerPager = (ViewPager) view.findViewById(R.id.sticker_pager);
        stickerPager.addOnPageChangeListener(this);

        mStickersTabs = new View[6];
        mStickersTabs[0] = view.findViewById(R.id.sticker_0);
        mStickersTabs[1] = view.findViewById(R.id.sticker_1);
        mStickersTabs[2] = view.findViewById(R.id.sticker_2);
        mStickersTabs[3] = view.findViewById(R.id.sticker_3);
        mStickersTabs[4] = view.findViewById(R.id.sticker_4);
        mStickersTabs[5] = view.findViewById(R.id.emojis_button);
        for (int i = 0; i < mStickersTabs.length; i++) {
            final int position = i;
            mStickersTabs[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() != R.id.emojis_button)
                        stickerPager.setCurrentItem(position);
                    else {
                        // load stickers fragment
                        if (onStickerButtonClickedListener != null) {
                            onStickerButtonClickedListener.onEmojiConButtonClicked();
                        }
                    }
                }
            });
        }
        onPageSelected(0);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getActivity() instanceof OnStickerButtonClickedListener) {
            onStickerButtonClickedListener = (OnStickerButtonClickedListener) getActivity();
        } else if (getParentFragment() instanceof OnStickerButtonClickedListener) {
            onStickerButtonClickedListener = (OnStickerButtonClickedListener) getParentFragment();
        } else {
            throw new IllegalArgumentException(activity + " must implement interface " + OnStickerButtonClickedListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        onStickerButtonClickedListener = null;
        super.onDetach();
    }


    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        if (mEmojiTabLastSelectedIndex == i) {
            return;
        }
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                if (mEmojiTabLastSelectedIndex >= 0 && mEmojiTabLastSelectedIndex < mStickersTabs.length) {
                    mStickersTabs[mEmojiTabLastSelectedIndex].setSelected(false);
                }
                mStickersTabs[i].setSelected(true);
                mEmojiTabLastSelectedIndex = i;
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    private static class StickersPagerAdapter extends FragmentStatePagerAdapter {
        private List<StickersGridFragment> fragments;

        public StickersPagerAdapter(FragmentManager fm, List<StickersGridFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    /**
     * A class, that can be used as a TouchListener on any view (e.g. a Button).
     * It cyclically runs a clickListener, emulating keyboard-like behaviour. First
     * click is fired immediately, next before initialInterval, and subsequent before
     * normalInterval.
     * <p/>
     * <p>Interval is scheduled before the onClick completes, so it has to run fast.
     * If it runs slow, it does not generate skipped onClicks.
     */
    public static class RepeatListener implements View.OnTouchListener {

        private Handler handler = new Handler();

        private int initialInterval;
        private final int normalInterval;
        private final View.OnClickListener clickListener;

        private Runnable handlerRunnable = new Runnable() {
            @Override
            public void run() {
                if (downView == null) {
                    return;
                }
                handler.removeCallbacksAndMessages(downView);
                handler.postAtTime(this, downView, SystemClock.uptimeMillis() + normalInterval);
                clickListener.onClick(downView);
            }
        };

        private View downView;

        /**
         * @param initialInterval The interval before first click event
         * @param normalInterval  The interval before second and subsequent click
         *                        events
         * @param clickListener   The OnClickListener, that will be called
         *                        periodically
         */
        public RepeatListener(int initialInterval, int normalInterval, View.OnClickListener clickListener) {
            if (clickListener == null)
                throw new IllegalArgumentException("null runnable");
            if (initialInterval < 0 || normalInterval < 0)
                throw new IllegalArgumentException("negative interval");

            this.initialInterval = initialInterval;
            this.normalInterval = normalInterval;
            this.clickListener = clickListener;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downView = view;
                    handler.removeCallbacks(handlerRunnable);
                    handler.postAtTime(handlerRunnable, downView, SystemClock.uptimeMillis() + initialInterval);
                    clickListener.onClick(view);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    handler.removeCallbacksAndMessages(downView);
                    downView = null;
                    return true;
            }
            return false;
        }
    }



    public interface OnStickerButtonClickedListener {
        void onEmojiConButtonClicked();
    }

    public void getAllStickers()
    {
        CategoryDataSource categoryDataSource = new CategoryDataSource(getActivity());
        categoryDataSource.open();
        LinkedHashMap<String, ChatStickerCategory> list = categoryDataSource.getAllStickersCategory();
        categoryDataSource.close();
        int s = 0;
        for (String key : list.keySet()) {
            switch (s) {
                case 0:
                    stickerListOne.addAll(list.get(String.valueOf(key)).getStickerList());
                    break;
                case 1:
                    stickerListTwo.addAll(list.get(String.valueOf(key)).getStickerList());
                    break;
                case 2:
                    stickerListThree.addAll(list.get(String.valueOf(key)).getStickerList());
                    break;
                case 3:
                    stickerListFour.addAll(list.get(String.valueOf(key)).getStickerList());
                    break;
                case 4:
                    stickerListFive.addAll(list.get(String.valueOf(key)).getStickerList());
                    break;
                case 5:
                    break;
            }
            s++;
        }
        final StickersPagerAdapter stickersAdapter = new StickersPagerAdapter(getFragmentManager(), Arrays.asList(
                StickersGridFragment.newInstance(stickerListOne), StickersGridFragment.newInstance(stickerListTwo), StickersGridFragment.newInstance(stickerListThree), StickersGridFragment.newInstance(stickerListFour)
                , StickersGridFragment.newInstance(stickerListFive)));
        stickerPager.setAdapter(stickersAdapter);


    }
}
