package com.testerhome.nativeandroid.networks;

import com.testerhome.nativeandroid.models.CollectTopicResonse;
import com.testerhome.nativeandroid.models.CreateReplyResponse;
import com.testerhome.nativeandroid.models.NotificationResponse;
import com.testerhome.nativeandroid.models.PraiseEntity;
import com.testerhome.nativeandroid.models.TopicDetailResponse;
import com.testerhome.nativeandroid.models.TopicReplyResponse;
import com.testerhome.nativeandroid.models.TopicsResponse;
import com.testerhome.nativeandroid.models.ToutiaoResponse;
import com.testerhome.nativeandroid.models.UserDetailResponse;
import com.testerhome.nativeandroid.models.UserResponse;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Bin Li on 2015/9/15.
 */
public interface TopicsService {

    @GET("/ads/toutiao.json")
    void getToutiao(Callback<ToutiaoResponse> callback);


    @GET("/topics.json")
    void getTopicsByType(@Query("type") String type,
                         @Query("offset") int offset,
                         Callback<TopicsResponse> callback);


    @GET("/topics.json")
    void getTopicsByNodeId(@Query("node_id") int nodeId,
                         @Query("offset") int offset,
                         Callback<TopicsResponse> callback);


    @GET("/topics/{id}.json")
    void getTopicById(@Path("id") String id,
                      Callback<TopicDetailResponse> callback);


    @GET("/users/{username}/topics.json")
    void getUserTopics(@Path("username") String username,
                       @Query("access_token") String accessToken,
                       @Query("offset") int offset,
                       Callback<TopicsResponse> callback);

    @GET("/users/{username}/favorites.json")
    void getUserFavorite(@Path("username") String username,
                       @Query("access_token") String accessToken,
                       @Query("offset") int offset,
                       Callback<TopicsResponse> callback);

    @GET("/users/{username}.json")
    void getUserInfo(@Path("username") String username,
                     @Query("access_token") String accessToken,
                     Callback<UserResponse> callback);


    @GET("/greet.json")
    void getCurrentUserInfo(
                     @Query("access_token") String accessToken,
                     Callback<UserDetailResponse> callback);

    @GET("/topics/{id}/replies.json")
    void getTopicsReplies(@Path("id") String id,
                          @Query("offset") int offset,
                          Callback<TopicReplyResponse> callback);
    @GET("/notifications.json")
    void getNotifications(@Query("access_token") String access_token,
                          @Query("offset") int offset,
                          Callback<NotificationResponse> callback);

    @POST("/topics/{id}/replies.json")
    void createReply(@Path("id") String id,
                     @Query("body") String body,
                     @Query("access_token") String accessToken,
                     Callback<CreateReplyResponse> callback);

    @POST("/topics/{id}/favorite.json")
    void collectTopic(@Path("id") String id,
                      @Query("access_token") String accessToken,
                      Callback<CollectTopicResonse> callback);

    @POST("/likes.json")
    void praiseTopic(@Query("obj_type") String objType,
                     @Query("obj_id") String objId,
                     @Query("access_token") String accessToken,
                     Callback<PraiseEntity> callback);
}
