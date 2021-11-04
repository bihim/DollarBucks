package com.tanvirhossen.dollarbucks;

import static com.tanvirhossen.dollarbucks.global.GlobalVals.ONESIGNAL_APP_ID;

import android.app.Application;

import com.onesignal.OneSignal;

public class ApplicationClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
    }
}
