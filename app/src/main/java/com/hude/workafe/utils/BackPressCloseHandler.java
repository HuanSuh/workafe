package com.hude.workafe.utils;

import android.app.Activity;
import android.widget.Toast;

import com.hude.workafe.R;


/**
 * Created by s_huan.suh on 2016-09-29.
 */

public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private final int TIME_GAP = 2000;
    private Toast toast;
    private Activity activity;
    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + TIME_GAP) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + TIME_GAP) {
            activity.finish();
            toast.cancel();
        }
    }

    private void showGuide() {
        toast = Toast.makeText(activity, activity.getString(R.string.main_backkey_msg), Toast.LENGTH_SHORT);
        toast.show();
    }
}