package com.testerhome.nativeandroid.models;

import com.testerhome.nativeandroid.views.adapters.TopicsListAdapter;

import java.util.List;

/**
 * Created by Bin Li on 2015/9/15.
 */
public class TopicEntity {

    /**
     * id : 3339
     * title : TesterHome 与 fir.im 设立月度最佳文章奖金 [千元悬赏]
     * created_at : 2015-09-15T21:23:19.217+08:00
     * updated_at : 2015-09-15T23:38:25.218+08:00
     * replied_at : 2015-09-15T23:38:25.206+08:00
     * replies_count : 4
     * node_name : 活动沙龙
     * node_id : 24
     * last_reply_user_id : 1011
     * last_reply_user_login : doctorq
     * user : {"id":104,"login":"seveniruby","name":"思寒","avatar_url":"/user/large_avatar/104.jpg"}
     * deleted : false
     * abilities : {"update":false,"destroy":false}
     */

    private String id;
    private String title;
    private String created_at;
    private String updated_at;
    private String replied_at;
    private int replies_count;
    private String node_name;
    private int node_id;
    private int last_reply_user_id;
    private String last_reply_user_login;
    private UserEntity user;
    private boolean deleted;
    private AbilitiesEntity abilities;

    private int type = TopicsListAdapter.TOPIC_LIST_TYPE_TOPIC;
    private List<BannerEntity> banners;

    public TopicEntity() {
    }

    public TopicEntity(int type, List<BannerEntity> banners) {
        this.type = type;
        this.banners = banners;
    }

    public int getType() {
        return type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void setReplied_at(String replied_at) {
        this.replied_at = replied_at;
    }

    public void setReplies_count(int replies_count) {
        this.replies_count = replies_count;
    }

    public void setNode_name(String node_name) {
        this.node_name = node_name;
    }

    public void setNode_id(int node_id) {
        this.node_id = node_id;
    }

    public void setLast_reply_user_id(int last_reply_user_id) {
        this.last_reply_user_id = last_reply_user_id;
    }

    public void setLast_reply_user_login(String last_reply_user_login) {
        this.last_reply_user_login = last_reply_user_login;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setAbilities(AbilitiesEntity abilities) {
        this.abilities = abilities;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getReplied_at() {
        return replied_at;
    }

    public int getReplies_count() {
        return replies_count;
    }

    public String getNode_name() {
        return node_name;
    }

    public int getNode_id() {
        return node_id;
    }

    public int getLast_reply_user_id() {
        return last_reply_user_id;
    }

    public String getLast_reply_user_login() {
        return last_reply_user_login;
    }

    public UserEntity getUser() {
        return user;
    }

    public boolean getDeleted() {
        return deleted;
    }

    public AbilitiesEntity getAbilities() {
        return abilities;
    }


}
