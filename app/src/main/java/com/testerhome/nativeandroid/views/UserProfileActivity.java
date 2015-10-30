package com.testerhome.nativeandroid.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.testerhome.nativeandroid.Config;
import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.application.NativeApp;
import com.testerhome.nativeandroid.auth.TesterHomeAccountService;
import com.testerhome.nativeandroid.models.TesterUser;
import com.testerhome.nativeandroid.views.base.BackBaseActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by vclub on 15/10/13.
 */
public class UserProfileActivity extends BackBaseActivity {

    @Bind(R.id.id_user_avatar)
    SimpleDraweeView userAvatar;
    @Bind(R.id.loginName)
    TextView userName;
    @Bind(R.id.id_company)
    TextView commanyName;
    @Bind(R.id.id_tag_line)
    TextView tagLine;

    private TesterUser mTesterHomeAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        setCustomTitle("个人资料");

        mTesterHomeAccount = TesterHomeAccountService.getInstance(this).getActiveAccountInfo();
        setupView();
    }

    private void setupView() {

        userAvatar.setImageURI(Uri.parse(Config.getImageUrl(mTesterHomeAccount.getAvatar_url())));
        userName.setText(mTesterHomeAccount.getLogin());
        commanyName.setText(mTesterHomeAccount.getCompany());
        tagLine.setText(mTesterHomeAccount.getTagline());
    }

    @OnClick(R.id.id_create_layout)
    void onTopicsClick() {
        startActivity(new Intent(this, AccountTopicsActivity.class));
    }

    @OnClick(R.id.id_collect_layout)
    void onFavoriteClick() {
        startActivity(new Intent(this, AccountFavoriteActivity.class));
    }

    @OnClick(R.id.id_notification_layout)
    void onNotificationClick() {
        startActivity(new Intent(this, AccountNotificationActivity.class));
    }

    @OnClick(R.id.id_logout_layout)
    void onLogoutClick() {
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().startSync();
        CookieManager.getInstance().removeSessionCookie();
        TesterHomeAccountService.getInstance(this).logout();
        NativeApp.getInstance().cancelTimerTask();
        this.finish();
    }
}
