package com.rvalerio.foregroundappchecker;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView tvPermission;
    private Button btUsagePermission;
    private Button btStartService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvPermission = (TextView) findViewById(R.id.permission_text);
        btUsagePermission = (Button) findViewById(R.id.usage_permission);
        btStartService = (Button) findViewById(R.id.service_toggle);

        if(!needsUsageStatsPermission()) {
            btUsagePermission.setVisibility(View.GONE);
            tvPermission.setText(R.string.usage_permission_granted);
        } else {
            btUsagePermission.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestUsageStatsPermission();
                }
            });
        }

        btStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForegroundToastService.start(getBaseContext());
                Toast.makeText(getBaseContext(), getString(R.string.service_started), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private boolean needsUsageStatsPermission() {
        return postLollipop() && !hasUsageStatsPermission(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void requestUsageStatsPermission() {
        if(!hasUsageStatsPermission(this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    private boolean postLollipop() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        return granted;
    }
}
