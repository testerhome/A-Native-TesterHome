package com.testerhome.nativeandroid.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.testerhome.nativeandroid.Config;
import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.auth.TesterHomeAccountService;
import com.testerhome.nativeandroid.fragments.MarkdownFragment;
import com.testerhome.nativeandroid.fragments.SettingsFragment;
import com.testerhome.nativeandroid.fragments.TopicReplyFragment;
import com.testerhome.nativeandroid.models.CollectTopicResonse;
import com.testerhome.nativeandroid.models.CreateReplyResponse;
import com.testerhome.nativeandroid.models.PraiseEntity;
import com.testerhome.nativeandroid.models.TesterUser;
import com.testerhome.nativeandroid.models.TopicDetailEntity;
import com.testerhome.nativeandroid.models.TopicDetailResponse;
import com.testerhome.nativeandroid.networks.TesterHomeApi;
import com.testerhome.nativeandroid.utils.DeviceUtil;
import com.testerhome.nativeandroid.utils.StringUtils;
import com.testerhome.nativeandroid.views.base.BackBaseActivity;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by vclub on 15/9/17.
 */
public class TopicDetailActivity extends BackBaseActivity {

    private String mTopicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);

        setCustomTitle("帖子详情");

        if (getIntent().hasExtra("topic_id")) {
            mTopicId = getIntent().getStringExtra("topic_id");

            setupView();
            loadInfo();
        } else {
            finish();
        }
    }


    @Override
    public boolean enableTheme() {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_topic_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            final Intent target = new Intent(Intent.ACTION_SEND);
            target.setType("text/plain");
            target.putExtra(Intent.EXTRA_TEXT, String.format("https://testerhome.com/topics/%s", mTopicId));
            startActivity(Intent.createChooser(target, "分享到"));
        }
        return super.onOptionsItemSelected(item);
    }

    @Bind(R.id.tab_layout)
    TabLayout tabLayoutTopicsTab;

    @Bind(R.id.view_pager)
    ViewPager viewPagerTopics;

    private TopicDetailPagerAdapter mAdapter;

    private void setupView() {
        mAdapter = new TopicDetailPagerAdapter(getSupportFragmentManager());
        viewPagerTopics.setAdapter(mAdapter);

        tabLayoutTopicsTab.setupWithViewPager(viewPagerTopics);
        tabLayoutTopicsTab.setTabsFromPagerAdapter(mAdapter);
    }

    private MarkdownFragment mMarkdownFragment;
    private TopicReplyFragment mTopicReplyFragment;

    public void updateReplyCount(int count) {
        if (tvDetailRepliesCount != null) {
            if (count > mTopicEntity.getReplies_count())
                tvDetailRepliesCount.setText(getString(R.string.reply_count_info, count));
        }
    }

    public class TopicDetailPagerAdapter extends FragmentPagerAdapter {

        private String[] typeName = {"帖子", "评论"};

        public TopicDetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (mMarkdownFragment == null) {
                    mMarkdownFragment = new MarkdownFragment();
                }
                return mMarkdownFragment;
            } else {
                if (mTopicReplyFragment == null)
                    mTopicReplyFragment = TopicReplyFragment.newInstance(mTopicId);
                return mTopicReplyFragment;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return typeName[position];
        }

        @Override
        public int getCount() {
            return typeName.length;
        }
    }

    @Bind(R.id.tv_detail_title)
    TextView tvDetailTitle;
    @Bind(R.id.sdv_detail_user_avatar)
    SimpleDraweeView sdvDetailUserAvatar;
    @Bind(R.id.tv_detail_name)
    TextView tvDetailName;
    @Bind(R.id.tv_detail_username)
    TextView tvDetailUsername;
    @Bind(R.id.tv_detail_publish_date)
    TextView tvDetailPublishDate;
    @Bind(R.id.tv_detail_replies_count)
    TextView tvDetailRepliesCount;

    TopicDetailEntity mTopicEntity;

    private void loadInfo() {
        TesterHomeApi.getInstance().getTopicsService().getTopicById(mTopicId,
                new Callback<TopicDetailResponse>() {
                    @Override
                    public void success(TopicDetailResponse topicDetailResponse, Response response) {
                        mTopicEntity = topicDetailResponse.getTopic();
                        tvDetailTitle.setText(mTopicEntity.getTitle());
                        tvDetailName.setText(mTopicEntity.getNode_name().concat("."));
                        tvDetailUsername.setText(TextUtils.isEmpty(mTopicEntity.getUser().getLogin()) ?
                                "匿名用户" : mTopicEntity.getUser().getName());
                        tvDetailPublishDate.setText(StringUtils.formatPublishDateTime(
                                mTopicEntity.getCreated_at()).concat(".")
                                .concat(mTopicEntity.getHits()).concat("次阅读"));
                        sdvDetailUserAvatar.setImageURI(Uri.parse(Config.getImageUrl(mTopicEntity.getUser().getAvatar_url())));

                        // 用户回复数
                        tvDetailRepliesCount.setText(getString(R.string.reply_count_info, mTopicEntity.getReplies_count()));

                        if (mMarkdownFragment != null) {
                            mMarkdownFragment.showWebContent(topicDetailResponse.getTopic().getBody_html());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Snackbar.make(mFabAddComment, "Error:" + error.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private TesterUser mCurrentUser;
    @Bind(R.id.tv_detail_collect)
    TextView tvDetailCollect;
    @Bind(R.id.tv_detail_praise)
    TextView tvDetailPraise;

    @OnClick(R.id.tv_detail_collect)
    void onDetailCollectClick() {
        if (mCurrentUser == null) {
            mCurrentUser = TesterHomeAccountService.getInstance(this).getActiveAccountInfo();
        }
        TesterHomeApi.getInstance().getTopicsService().collectTopic(mTopicId, mCurrentUser.getAccess_token(), new Callback<CollectTopicResonse>() {
            @Override
            public void success(CollectTopicResonse collectTopicResonse, Response response) {
                if (collectTopicResonse.getOk() == 1) {
                    Snackbar.make(mFabAddComment, "收藏成功", Snackbar.LENGTH_SHORT).show();
                    tvDetailCollect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_bookmark_off, 0, 0, 0);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Snackbar.make(mFabAddComment, "Error:" + error.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });

    }


    @OnClick(R.id.tv_detail_praise)
    void onDetailPraiseClick() {
        if (mCurrentUser == null) {
            mCurrentUser = TesterHomeAccountService.getInstance(this).getActiveAccountInfo();
        }
        TesterHomeApi.getInstance().getTopicsService().praiseTopic(Config.PRAISE_TOPIC, mTopicId, mCurrentUser.getAccess_token(), new Callback<PraiseEntity>() {
            @Override
            public void success(PraiseEntity praiseEntity, Response response) {
                Snackbar.make(mFabAddComment, "点赞成功", Snackbar.LENGTH_SHORT).show();
                tvDetailPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_red, 0, 0, 0);
            }

            @Override
            public void failure(RetrofitError error) {
                Snackbar.make(mFabAddComment, "Error:" + error.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    @Bind(R.id.llAddComment)
    View mAddCommentPanel;

    @Bind(R.id.fab_add_comment)
    FloatingActionButton mFabAddComment;

    @OnClick(R.id.fab_add_comment)
    void onFabClick() {
        mAddCommentPanel.setVisibility(View.VISIBLE);
        mFabAddComment.setVisibility(View.GONE);
    }

    @Bind(R.id.etComment)
    EditText mEtComment;

    // 回帖子
    @OnClick(R.id.btnSendComment)
    void onSendCommentClick() {
        mEtComment.setError(null);

        if (TextUtils.isEmpty(mEtComment.getText().toString())) {
            mEtComment.setError("请输入回复内容");
        } else {
            String replyBody = mEtComment.getText().toString();

            if (PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean(SettingsFragment.KEY_PREF_COMMENT_WITH_SNACK, true)) {
                replyBody = replyBody.concat("\n\n")
                        .concat("—— 来自TesterHome官方 [安卓客户端](http://fir.im/p9vs)");
            }

            if (mCurrentUser == null) {
                mCurrentUser = TesterHomeAccountService.getInstance(this).getActiveAccountInfo();
            }
            TesterHomeApi.getInstance().getTopicsService()
                    .createReply(mTopicId,
                            replyBody,
                            mCurrentUser.getAccess_token(),
                            new Callback<CreateReplyResponse>() {
                                @Override
                                public void success(CreateReplyResponse createReplyResponse, Response response) {
                                    if (createReplyResponse.getError() == null) {
                                        // 发送成功
                                        mEtComment.setText("");
                                        DeviceUtil.hideSoftInput(TopicDetailActivity.this);
                                        mAddCommentPanel.setVisibility(View.GONE);
                                        mTopicReplyFragment.refreshReply();
                                        // TODO: refresh reply count
                                        // updateReplyCount();
                                    } else {
                                        Snackbar.make(mFabAddComment,
                                                createReplyResponse.getError().toString(),
                                                Snackbar.LENGTH_SHORT)
                                                .show();
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Snackbar.make(mFabAddComment,
                                            error.getMessage(),
                                            Snackbar.LENGTH_SHORT)
                                            .show();
                                }
                            });
        }
    }
}
