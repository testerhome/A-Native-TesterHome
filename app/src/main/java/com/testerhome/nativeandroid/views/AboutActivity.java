package com.testerhome.nativeandroid.views;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.views.base.BackBaseActivity;
import com.umeng.update.UmengUpdateAgent;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by vclub on 15/10/22.
 */
public class AboutActivity extends BackBaseActivity {

    @Bind(R.id.tv_app_version)
    TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setCustomTitle("关于");

        tvVersion.setText(getResources().getString(R.string.app_name)
                .concat("\nV")
                .concat(getAppInfo()));
    }

    private String getAppInfo() {

        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
            return String.format("%s build %s", info.versionName, info.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            // do nothing
        }

        return "";
    }


    @OnClick(R.id.btn_check_update)
    void onUpdateClick(){
        UmengUpdateAgent.forceUpdate(this);
    }
}
