package com.testerhome.nativeandroid.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.testerhome.nativeandroid.Config;
import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.models.BannerEntity;
import com.testerhome.nativeandroid.models.TopicEntity;
import com.testerhome.nativeandroid.models.TopicsResponse;
import com.testerhome.nativeandroid.networks.RestAdapterUtils;
import com.testerhome.nativeandroid.networks.TopicsService;
import com.testerhome.nativeandroid.views.adapters.TopicsListAdapter;
import com.testerhome.nativeandroid.views.widgets.DividerItemDecoration;

import java.util.ArrayList;

import butterknife.Bind;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bin Li on 2015/9/16.
 */
public class TopicsListFragment extends BaseFragment implements Callback<TopicsResponse> {

    @Bind(R.id.rv_topic_list)
    RecyclerView recyclerViewTopicList;

    @Bind(R.id.srl_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private int mNextCursor = 0;

    private TopicsListAdapter mAdatper;

    private String type;
    private int nodeId;


    public static TopicsListFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString("type", type);
        TopicsListFragment fragment = new TopicsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static TopicsListFragment newInstance(int nodeId) {
        Bundle args = new Bundle();
        args.putInt("nodeId", nodeId);
        TopicsListFragment fragment = new TopicsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_topics;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        type = getArguments().getString("type");
        if (type == null) {
            nodeId = getArguments().getInt("nodeId");
        }

        loadTopics(true);
    }


    @Override
    protected void setupView() {
        mAdatper = new TopicsListAdapter(getActivity());
        mAdatper.setListener(new TopicsListAdapter.EndlessListener() {
            @Override
            public void onListEnded() {
                if (mNextCursor > 0) {
                    loadTopics(false);
                }
            }
        });

        recyclerViewTopicList.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewTopicList.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        recyclerViewTopicList.setAdapter(mAdatper);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mNextCursor = 0;
                loadTopics(false);
            }
        });

    }

    private void loadTopics(boolean showloading) {

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }

        if (showloading)
            showLoadingView();

        if (type != null) {
            RestAdapterUtils.getRestAPI(Config.BASE_URL, TopicsService.class, getActivity()).getTopicsByType(type,
                    mNextCursor * 20, this);
        } else {
            RestAdapterUtils.getRestAPI(Config.BASE_URL, TopicsService.class, getActivity()).getTopicsByNodeId(nodeId,
                    mNextCursor * 20, this);
        }

    }


    @Override
    public void success(TopicsResponse topicsResponse, Response response) {

        Log.e("cache", response.getHeaders().toString());

        hideLoadingView();
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }

        if (topicsResponse.getTopics().size() > 0) {
            if (mNextCursor == 0) {
                if (!TextUtils.isEmpty(type) && type.equals(Config.TOPICS_TYPE_RECENT)) {
                    topicsResponse.getTopics().add(0, new TopicEntity(TopicsListAdapter.TOPIC_LIST_TYPE_BANNER, new ArrayList<BannerEntity>()));
                }
                mAdatper.setItems(topicsResponse.getTopics());
            } else {
                mAdatper.addItems(topicsResponse.getTopics());
            }
            if (topicsResponse.getTopics().size() >= 20) {
                mNextCursor += 1;
            } else {
                mNextCursor = 0;
            }

        } else {
            mNextCursor = 0;
        }
    }

    @Override
    public void failure(RetrofitError error) {
        hideLoadingView();
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        Log.e("cache", "failure() called with: " + "error = [" + error + "]" + error.getResponse().getHeaders().toString());
    }
}
