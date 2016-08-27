package com.rvalerio.fgchecker.detectors;

import android.annotation.TargetApi;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;

import com.rvalerio.fgchecker.Utils;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class LollipopDetector implements Detector {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public String getForegroundApp(Context context) {
        if(!Utils.hasUsageStatsPermission(context))
            return null;

        String foregroundApp = null;

        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Service.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 60, time);
        if (stats != null) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : stats) {
                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (mySortedMap != null && !mySortedMap.isEmpty()) {
                foregroundApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
            }
        }

        return foregroundApp;
    }
}
