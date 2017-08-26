# Foreground App Checker for android

This library tries to provide an easy way to get the foreground application package name. It uses different techniques, adequate to the device's android version.

[ ![Download](https://api.bintray.com/packages/rvalerio/maven/foregroundappchecker/images/download.svg) ](https://bintray.com/rvalerio/maven/foregroundappchecker/_latestVersion)

Download
--------

```groovy
apply plugin: 'com.android.application'

repositories {
    // ...
    maven { url 'https://dl.bintray.com/rvalerio/maven' }
}

dependencies {
    // ...
    compile 'com.rvalerio:fgchecker:1.1.0'
}
```


Setup
-----
To use this library, you will need to request permissions in order to support different versions.

To support ICS until Lollipop, you need to request android.permission.GET_TASKS permission.

To support Lollipop or above, you need to request android.permission.PACKAGE_USAGE_STATS permission.

```xml
<uses-permission android:name="android.permission.GET_TASKS" />
<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS"
    tools:ignore="ProtectedPermissions" />
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


Usage
-----

In it's simplest form, and to get the package name of the foreground application, you can do like so:

```android
AppChecker appChecker = new AppChecker();
String packageName = appChecker.getForegroundApp(context);
```

If you would like to have this being checked on an interval, you can do like so:

```android
AppChecker appChecker = new AppChecker();
appChecker
    .whenAny(new AppChecker.Listener() {
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
            // do something when com.other.app is in the foreground
        }
    )
    .when("com.my.app", new AppChecker.Listener() {
        @Override
        public void onForeground(String packageName) {
            // do something when com.my.app is in the foreground
        }
    )
    .whenOther(new AppChecker.Listener() {
        @Override
        public void onForeground(String packageName) {
            // do something when none of the registered packages are in the foreground
        }
    )
    .whenAny(new AppChecker.Listener() {
        @Override
        public void onForeground(String packageName) {
            // do something everytime a scan for foreground app is run
        }
    )
    .timeout(1000)
    .start(this);
```

Callbacks are done on the UI thread. Keep in mind that callbacks are done every time there is a scan. Currently it does not do callbacks only when the foreground app changes.

Upcoming features
-----
- Add option to only callback when there is a package name change


This library is distributed under the Apache 2.0 license.

License
-------

    Copyright 2016 Ricardo Valério

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

