package com.melodygram.shortcutbadger;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

/**
 * @author leolin
 */
public class DefaultBadger extends ShortcutBadger {
    private static final String INTENT_ACTION = "android.intent.action.BADGE_COUNT_UPDATE";
    private static final String INTENT_EXTRA_BADGE_COUNT = "badge_count";
    private static final String INTENT_EXTRA_PACKAGENAME = "badge_count_package_name";
    private static final String INTENT_EXTRA_ACTIVITY_NAME = "badge_count_class_name";

    public DefaultBadger(Context context) {
        super(context);
    }

    @Override
    protected void executeBadge(int badgeCount) {
        Intent intent = new Intent(INTENT_ACTION);
        intent.putExtra(INTENT_EXTRA_BADGE_COUNT, badgeCount);
        intent.putExtra(INTENT_EXTRA_PACKAGENAME, getContextPackageName());
        intent.putExtra(INTENT_EXTRA_ACTIVITY_NAME, getEntryActivityName());
        mContext.sendBroadcast(intent);
    }

    @Override
    public List<String> getSupportLaunchers() {
        return new ArrayList<String>(0);
    }
}