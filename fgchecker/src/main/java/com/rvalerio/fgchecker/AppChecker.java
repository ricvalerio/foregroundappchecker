package com.rvalerio.fgchecker;

import android.content.Context;

import com.rvalerio.fgchecker.detectors.Detector;
import com.rvalerio.fgchecker.detectors.LollipopDetector;
import com.rvalerio.fgchecker.detectors.PreLollipopDetector;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class AppChecker {
    private ScheduledExecutorService service;
    private Runnable runnable;

    private static final int DEFAULT_TIMEOUT = 1000;
    private int timeout = DEFAULT_TIMEOUT;
    private Listener defaultListener;
    private Map<String, Listener> listeners;

    public interface Listener {
        void onForeground(String process);
    }

    Detector detector;

    public AppChecker() {
        listeners = new HashMap<>();
        if(Utils.postLollipop())
            detector = new LollipopDetector();
        else
            detector = new PreLollipopDetector();
    }

    public AppChecker timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public AppChecker when(String packageName, Listener listener) {
        listeners.put(packageName, listener);
        return this;
    }

    public AppChecker other(Listener listener) {
        defaultListener = listener;
        return this;
    }

    public void start(Context context) {
        service = new ScheduledThreadPoolExecutor(1);
        runnable = createRunnable(context.getApplicationContext());
        service.schedule(runnable, timeout, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        service.shutdownNow();
        runnable = null;
        service = null;
    }

    private Runnable createRunnable(final Context context) {
        return new Runnable() {
            @Override
            public void run() {
                getForegroundAppAndNotify(context);
                service.schedule(createRunnable(context), timeout, TimeUnit.MILLISECONDS);
            }
        };
    }

    private void getForegroundAppAndNotify(Context context) {
        String foregroundApp = getForegroundApp(context);

        if(foregroundApp != null) {
            for (String packageName : listeners.keySet()) {
                if (packageName.toLowerCase().equals(foregroundApp)) {
                    listeners.get(packageName).onForeground(packageName);
                    return;
                }
            }
            if(defaultListener != null) {
                defaultListener.onForeground(foregroundApp);
            }
        }
    }

    public String getForegroundApp(Context context) {
        return detector.getForegroundApp(context);
    }
}
