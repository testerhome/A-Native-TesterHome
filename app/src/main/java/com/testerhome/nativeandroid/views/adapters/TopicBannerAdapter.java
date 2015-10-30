package com.testerhome.nativeandroid.views.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.testerhome.nativeandroid.Config;
import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.models.AdsEntity;
import com.testerhome.nativeandroid.views.TopicDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vclub on 15/10/28.
 */
public class TopicBannerAdapter extends PagerAdapter implements View.OnClickListener {

    private List<AdsEntity> mItems;

    public List<AdsEntity> getItems() {
        return mItems;
    }

    public void setItems(List<AdsEntity> items) {
        this.mItems = items;
        notifyDataSetChanged();
    }

    public TopicBannerAdapter() {
        mItems = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = getItemView(container, position);
        container.addView(view);
        return view;
    }

    public View getItemView(ViewGroup container, int position) {

        SimpleDraweeView imageView = (SimpleDraweeView)LayoutInflater.from(container.getContext()).inflate(R.layout.banner_view, container, false);


        // TODO: 15/10/28 加载正式图片
        imageView.setImageURI(Uri.parse(Config.getImageUrl(mItems.get(position).getCover().getUrl())));
        imageView.setTag(mItems.get(position).getTopic_id());
        imageView.setOnClickListener(this);

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public void onClick(View v) {
        String topicId = (String) v.getTag();
        v.getContext().startActivity(new Intent(v.getContext(), TopicDetailActivity.class)
                .putExtra("topic_id", topicId));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mItems.get(position).getTopic_title();
    }
}
