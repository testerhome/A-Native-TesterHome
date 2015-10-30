package com.testerhome.nativeandroid.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.testerhome.nativeandroid.Config;
import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.auth.TesterHomeAccountService;
import com.testerhome.nativeandroid.fragments.HomeFragment;
import com.testerhome.nativeandroid.fragments.TopicsListFragment;
import com.testerhome.nativeandroid.models.TesterUser;
import com.testerhome.nativeandroid.views.base.BaseActivity;
import com.umeng.update.UmengUpdateAgent;

import butterknife.Bind;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Fragment homeFragment;
    private Fragment jobFragment;
    private Fragment topicFragment;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupView();

        UmengUpdateAgent.update(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserInfo();
    }

    private void setupView() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.realtabcontent, homeFragment).commit();

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);

        mAccountAvatar = (SimpleDraweeView) headerLayout.findViewById(R.id.sdv_account_avatar);
        mAccountAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAvatarClick();
            }
        });
        mAccountUsername = (TextView) headerLayout.findViewById(R.id.tv_account_username);
        mAccountEmail = (TextView) headerLayout.findViewById(R.id.tv_account_email);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // Handle navigation view item clicks here.

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        hideAllFragment(fragmentTransaction);


        int id = menuItem.getItemId();

        if (id == R.id.nav_home) {
            if (homeFragment == null) {
                homeFragment = new HomeFragment();
                fragmentTransaction.add(R.id.realtabcontent, homeFragment);
            }

            fragmentTransaction.show(homeFragment);
            if (toolbar != null) {
                toolbar.setTitle("社区");
            }
        } else if (id == R.id.nav_topic) {
            if (topicFragment == null) {
                topicFragment = TopicsListFragment.newInstance(Config.TOPICS_TYPE_LAST_ACTIVED);
                fragmentTransaction.add(R.id.realtabcontent, topicFragment);

            }
            fragmentTransaction.show(topicFragment);
            if (toolbar != null) {
                toolbar.setTitle("话题");
            }
        } else if (id == R.id.nav_job) {
            if (jobFragment == null) {
                jobFragment = TopicsListFragment.newInstance(Config.TOPIC_JOB_NODE_ID);
                fragmentTransaction.add(R.id.realtabcontent, jobFragment);

            }
            fragmentTransaction.show(jobFragment);
            if (toolbar != null) {
                toolbar.setTitle("招聘");
            }
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        drawer.closeDrawer(GravityCompat.START);

        fragmentTransaction.commit();
        return true;
    }

    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if (homeFragment != null) {
            fragmentTransaction.hide(homeFragment);
        }
        if (topicFragment != null) {
            fragmentTransaction.hide(topicFragment);
        }
        if (jobFragment != null) {
            fragmentTransaction.hide(jobFragment);
        }
    }

    SimpleDraweeView mAccountAvatar;

    TextView mAccountUsername;

    TextView mAccountEmail;

    void onAvatarClick() {
        if (mTesterHomeAccount != null && !TextUtils.isEmpty(mTesterHomeAccount.getLogin())) {
            startActivity(new Intent(this, UserProfileActivity.class));
        } else {
            startActivity(new Intent(this, WebViewActivity.class));
        }
    }

    TesterUser mTesterHomeAccount;

    private void updateUserInfo() {
        mTesterHomeAccount = TesterHomeAccountService.getInstance(this).getActiveAccountInfo();
        if (!TextUtils.isEmpty(mTesterHomeAccount.getLogin())) {
            mAccountAvatar.setImageURI(Uri.parse(Config.getImageUrl(mTesterHomeAccount.getAvatar_url())));
            mAccountUsername.setText(mTesterHomeAccount.getName());
            mAccountEmail.setText(mTesterHomeAccount.getEmail());
        } else {
            mAccountAvatar.setImageResource(R.mipmap.ic_launcher);
            mAccountUsername.setText("Android Studio");
            mAccountEmail.setText("android.studio@android.com");
        }
    }

    private static long back_pressed;

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerVisible(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (back_pressed + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
            } else {
                Toast.makeText(this, "再次点击退出", Toast.LENGTH_SHORT).show();
            }
            back_pressed = System.currentTimeMillis();
        }
    }
}
