package com.testerhome.nativeandroid.models;

/**
 * Created by vclub on 15/10/30.
 */
public class AdsEntity {
    private int _id;
    /**
     * url : /ad/2015/0ae813cc2a8d83361c4b36ed7ed7afef.jpg
     */

    private CoverEntity cover;
    private String created_at;
    private String topic_author;
    private String topic_id;
    private String topic_title;
    private String updated_at;

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setCover(CoverEntity cover) {
        this.cover = cover;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setTopic_author(String topic_author) {
        this.topic_author = topic_author;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
    }

    public void setTopic_title(String topic_title) {
        this.topic_title = topic_title;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public int get_id() {
        return _id;
    }

    public CoverEntity getCover() {
        return cover;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getTopic_author() {
        return topic_author;
    }

    public String getTopic_id() {
        return topic_id;
    }

    public String getTopic_title() {
        return topic_title;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public static class CoverEntity {
        private String url;

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }
}