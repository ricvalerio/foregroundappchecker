# Foreground App Checker for android

Foreground application checker for android.

This library tries to provide an easy way to get the foreground application package name. It uses different techniques, adequate to the device's android version.

To use this library on Lollipop or above, you need to request permission for UsageStats.

In it's simplest form, and to get the package name of the foreground application, you can do like so:

```android
AppChecker appChecker = new AppChecker();
String packageName = appChecker.getForegroundApp();
```

If you would like to have this being checked on an interval, you can do like so:

```android
AppChecker appChecker = new AppChecker();
appChecker
    .other(new AppChecker.Listener() {
        @Override
        public void onForeground(String packageName) {
            // do something
        }
    )
    .timeout(1000)
    .start(this);
```

If you want to check for specific package names, it also provides a way to do so:

```android
AppChecker appChecker = new AppChecker();
appChecker
    .when("com.other.app", new AppChecker.Listener() {
        @Override
        public void onForeground(String packageName) {
            // do something
        }
    )
    .when("com.my.app", new AppChecker.Listener() {
        @Override
        public void onForeground(String packageName) {
            // do something
        }
    )
    .other(new AppChecker.Listener() {
        @Override
        public void onForeground(String packageName) {
            // do something
        }
    )
    .timeout(1000)
    .start(this);
```

For your convenience, I provide here code to request usage stats permission:

```android
    
void requestUsageStatsPermission() {
    if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP 
        && !hasUsageStatsPermission(this)) {
        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
    }
}

@TargetApi(Build.VERSION_CODES.KITKAT)
boolean hasUsageStatsPermission(Context context) {
    AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
    int mode = appOps.checkOpNoThrow("android:get_usage_stats",
            android.os.Process.myUid(), context.getPackageName());
    boolean granted = mode == AppOpsManager.MODE_ALLOWED;
    return granted;
}
```

and last but not least:

```xml
<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS"
    tools:ignore="ProtectedPermissions" />
```

This library is distributed under the Apache 2.0 license.
