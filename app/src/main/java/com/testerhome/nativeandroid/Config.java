package com.testerhome.nativeandroid;

/**
 * Created by vclub on 15/9/15.
 */
public class Config {

    public static final String TOPICS_TYPE_RECENT = "recent";       // 最新
    public static final String TOPICS_TYPE_POPULAR = "popular";     // 热门的话题
    public static final String TOPICS_TYPE_NO_REPLY = "no_reply";   // 还没有任何回帖的
    public static final String TOPICS_TYPE_EXCELLENT = "excellent"; // 精华帖

    public static final String TOPICS_TYPE_LAST_ACTIVED = "last_actived";

    public static final int TOPIC_JOB_NODE_ID = 19;

    public static final String BASE_URL = "https://testerhome.com/api/v3";

    public static final String FOLLOW = "Follow";
    public static final String TOPIC = "Topic";

    public static final String PRAISE_TOPIC = "topic";
    public static final String PRAISE_REPLY = "reply";

    public static String getImageUrl(String imagePath){
        if (imagePath.startsWith("//testerhome.com")){
            return "https:".concat(imagePath);
        } else if (!imagePath.contains("https://testerhome.com")) {
            return "https://testerhome.com".concat(imagePath);
        }else{
            return imagePath;
        }

    }
}
