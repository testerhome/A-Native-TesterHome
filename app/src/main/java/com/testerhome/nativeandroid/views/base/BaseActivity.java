package com.testerhome.nativeandroid.views.base;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.fragments.SettingsFragment;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vclub on 15/9/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Nullable
    @Bind(R.id.toolbar)
    protected
    Toolbar toolbar;

    @Nullable
    @Bind(R.id.toolbar_title)
    TextView customTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(enableTheme()){
            loadTheme();
        } else {
            setTheme(R.style.theme_light);
        }
    }

    public boolean enableTheme(){
        return false;
    }

    private void loadTheme(){
        if (PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsFragment.KEY_PREF_THEME, "0").equals("1")) {
            Log.e("theme", "is dark theme");
            setTheme(R.style.theme_dark);
        } else {
            Log.e("theme", "is light theme");
            setTheme(R.style.theme_light);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);

        setupToolbar();
    }

    protected void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    public void setCustomTitle(String title) {
        if (customTitle != null) {
            customTitle.setText(title);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onResume(this);
    }
}
