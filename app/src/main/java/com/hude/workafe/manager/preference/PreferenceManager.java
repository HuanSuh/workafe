package com.hude.workafe.manager.preference;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by s_huan.suh on 2016-09-28.
 */

public class PreferenceManager {
    private Context context;
    private String file;
    private static final String DEFAULT_FILE = "WORKAFE";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {
        this.context = context;
        setFile(DEFAULT_FILE);
        setPref();
    }
    private void setPref() {
        if(context == null) return;
        pref = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        editor = pref.edit();
    }
    private void setContext(Context context) {
        this.context = context;
    }
    private void setFile(String file) {
        this.file = file;
    }
    public void commit() {
        if(editor != null) editor.commit();
    }
    public void putString(String key, String value) {
        if(editor == null) return;
        editor.putString(key, value);
        editor.commit();
    }
    public String getString(String key) {
        if(pref == null) return null;
        return pref.getString(key, null);
    }
    public String getString(String key, String defValue) {
        if(pref == null) return null;
        return pref.getString(key, defValue);
    }
    public void putBoolean(String key, Boolean value) {
        if(editor == null) return;
        editor.putBoolean(key, value);
        editor.commit();
    }
    public Boolean getBoolean(String key) {
        return getBoolean(key, false);
    }
    public Boolean getBoolean(String key, boolean defValue) {
        if(pref == null) return defValue;
        return pref.getBoolean(key, defValue);
    }
    public int getInt(String key) {
        return getInt(key, -1);
    }
    public int getInt(String key, int defValue) {
        if(pref == null) return defValue;
        return pref.getInt(key, defValue);
    }
    public long getLong(String key) {
        return getLong(key, -1);
    }
    public long getLong(String key, long defValue) {
        if(pref == null) return defValue;
        return pref.getLong(key, defValue);
    }
    public Set<String> getStringSet(String key) {
        return pref.getStringSet(key, new HashSet<String>());
    }
    public void addToSet(String key, String value) {
        if(pref == null) return;
        Set<String> stringSet = pref.getStringSet(key, new HashSet<String>());
        stringSet.add(value);
        put(key, stringSet);
    }
    public void put(String key, Object value) {
        put(key, value, true);
    }
    public void put(String key, Object value, boolean autoCommit) {
        if(editor == null) return;
        if(value == null) {
            editor.putString(key, null);
        } else if(value instanceof String) {
            editor.putString(key, (String) value);
        } else if(value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if(value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if(value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if(value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if(value instanceof Set) {
            editor.putStringSet(key, (Set<String>) value);
        }

        if(autoCommit) {
            editor.commit();
        }
    }
}