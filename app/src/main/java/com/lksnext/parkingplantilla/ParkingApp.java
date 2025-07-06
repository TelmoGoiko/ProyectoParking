package com.lksnext.parkingplantilla;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import java.util.Locale;

public class ParkingApp extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(applyAppLocale(base));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applyAppLocale(this);
    }

    public static Context applyAppLocale(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String langCode = prefs.getString("app_language", Locale.getDefault().getLanguage());
        Locale locale;
        if (langCode.contains("-")) {
            String[] parts = langCode.split("-");
            locale = new Locale.Builder().setLanguage(parts[0]).setRegion(parts[1]).build();
        } else {
            locale = new Locale.Builder().setLanguage(langCode).build();
        }
        Locale.setDefault(locale);
        Resources res = context.getResources();
        Configuration config = res.getConfiguration();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
            return context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
            return context;
        }
    }
}
