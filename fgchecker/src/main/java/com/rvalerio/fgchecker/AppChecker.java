package com.rvalerio.fgchecker;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.rvalerio.fgchecker.detectors.Detector;
import com.rvalerio.fgchecker.detectors.LollipopDetector;
import com.rvalerio.fgchecker.detectors.PreLollipopDetector;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;



public class AppChecker {
    ScheduledExecutorService service;
    Runnable runnable;

    static final int DEFAULT_TIMEOUT = 1000;
    int timeout = DEFAULT_TIMEOUT;
    Listener defaultListener;
    Map<String, Listener> listeners;
    Detector detector;
    Handler handler;

    public interface Listener {
        void onForeground(String process);
    }


    public AppChecker() {
        listeners = new HashMap<>();
        handler = new Handler(Looper.getMainLooper());
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
        final String foregroundApp = getForegroundApp(context);

        if(foregroundApp != null) {
            for (String packageName : listeners.keySet()) {
                if (packageName.toLowerCase().equals(foregroundApp)) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listeners.get(foregroundApp).onForeground(foregroundApp);
                        }
                    });
                    return;
                }
            }
        }
        if(defaultListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    defaultListener.onForeground(foregroundApp);
                }
            });
        }
    }

    public String getForegroundApp(Context context) {
        return detector.getForegroundApp(context);
    }
}