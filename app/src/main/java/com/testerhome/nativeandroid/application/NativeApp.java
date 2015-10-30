package com.testerhome.nativeandroid.application;

import android.app.Application;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.testerhome.nativeandroid.BuildConfig;
import com.testerhome.nativeandroid.auth.TesterHomeAccountService;
import com.testerhome.nativeandroid.models.OAuth;
import com.testerhome.nativeandroid.models.TesterUser;
import com.testerhome.nativeandroid.oauth2.AuthenticationService;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Bin Li on 2015/9/15.
 */
public class NativeApp extends Application {

    private static NativeApp instance;

    public static String TAG = "NativeApp";
    public static long timerTime = 82800;
    private Timer timer = null;

    public static NativeApp getInstance() {
        if (null == instance) {
            instance = new NativeApp();
        }
        return instance;
    }



    public void startTimer(){
        TesterUser testerUser = TesterHomeAccountService.getInstance(this).getActiveAccountInfo();
        Log.d(TAG, testerUser.getCreate_at() + "");
        Log.d(TAG, testerUser.getExpireDate() + "");
        Log.d(TAG,System.currentTimeMillis()+"");
        long leftTime = timerTime -(System.currentTimeMillis()/1000-testerUser.getCreate_at());
        if (leftTime < 0 ){
            leftTime = 0;
        }

        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                TesterUser testerUser = TesterHomeAccountService.getInstance(instance).getActiveAccountInfo();
                if (testerUser.getRefresh_token() != null) {
                    String url = AuthenticationService.ACCESS_TOKEN_URL;
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    OkHttpClient okHttpClient = new OkHttpClient();

                    RequestBody formBody = new FormEncodingBuilder()
                            .add("client_id", AuthenticationService.getAuthorize_client_id())
                            .add("grant_type", "refresh_token")
                            .add("client_secret", "3a20127eb087257ad7196098bfd8240746a66b0550d039eb2c1901c025e7cbea")
                            .add("refresh_token",testerUser.getRefresh_token())
                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .post(formBody)
                            .build();

                    Response response = null;
                    try {
                        response = okHttpClient.newCall(request).execute();

                        if (response.isSuccessful()) {
                            String responseStr = response.body().string();
                            Log.d(TAG,responseStr);
                            JSONObject resultJson = new JSONObject(responseStr);
                            int expiresIn = resultJson.has("expires_in") ? resultJson.getInt("expires_in") : 0;
                            String accessToken = resultJson.has("access_token") ? resultJson.getString("access_token") : null;
                            String refreshToken = resultJson.has("refresh_token") ? resultJson.getString("refresh_token") : null;
                            long createdAt = resultJson.has("created_at") ? resultJson.getLong("created_at") : 0;
                            if (expiresIn > 0 && accessToken != null) {
                                OAuth oAuth = new OAuth();
                                oAuth.setRefresh_token(refreshToken);
                                oAuth.setAccess_token(accessToken);
                                oAuth.setExpires_in(expiresIn);
                                oAuth.setCraete_at(createdAt);
                                testerUser.setCreate_at(createdAt);
                                testerUser.setExpireDate(expiresIn);
                                testerUser.setAccess_token(accessToken);
                                testerUser.setRefresh_token(refreshToken);
                                TesterHomeAccountService.getInstance(instance).updateAccountToken(oAuth);
                            }
                        } else {
                            Log.e("Tokenm", "error:" + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                }
            }
        };
        timer.schedule(timerTask, leftTime*1000);
    }


    public void cancelTimerTask(){
        Log.d(TAG,"i have cancel");
        if (timer != null ) {
            timer.cancel();
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();

        // initialize fresco with OK HTTP
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(this, new OkHttpClient())
                .build();
        Fresco.initialize(this, config);

        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        Log.d("NativeApp", "NativeApp   oncreate");
        startTimer();
    }

}
