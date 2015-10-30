package com.testerhome.nativeandroid.views;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.application.NativeApp;
import com.testerhome.nativeandroid.auth.TesterHomeAccountService;
import com.testerhome.nativeandroid.models.OAuth;
import com.testerhome.nativeandroid.models.UserDetailResponse;
import com.testerhome.nativeandroid.networks.TesterHomeApi;
import com.testerhome.nativeandroid.oauth2.AuthenticationService;
import com.testerhome.nativeandroid.views.base.BackBaseActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import retrofit.RetrofitError;

/**
 * Created by vclub on 15/9/18.
 */
public class WebViewActivity extends BackBaseActivity {

    private ProgressDialog pd;
    private String auth_code = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_base);
        setCustomTitle("登录");

        FrameLayout layout = (FrameLayout) findViewById(R.id.container);

        final WebView mWebView = new WebView(this);

        layout.addView(mWebView);

        mWebView.getSettings().getJavaScriptEnabled();
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(AuthenticationService.AUTHORIZATION_URL)) {
                    try {
                        auth_code = url.substring(url.lastIndexOf("/") + 1);
                        //Generate URL for requesting Access Token
                        // String accessTokenUrl = AuthenticationService.getAccessTokenUrl(auth_code = code);
                        //We make the request in a AsyncTask
                        new PostRequestAsyncTask().execute(AuthenticationService.ACCESS_TOKEN_URL);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (url.equals(AuthenticationService.HTTPS_BASEURL)) {
                    url = AuthenticationService.getAuthorizationUrl();
                    mWebView.setVisibility(View.INVISIBLE);
                }
//                return super.shouldOverrideUrlLoading(view, url);
                view.loadUrl(url);
                return true;
            }

        });

        mWebView.loadUrl(AuthenticationService.getAuthorizationUrl());

    }

    private class PostRequestAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(WebViewActivity.this, "", "Loading...", true);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            if (urls.length > 0) {

                String url = urls[0];
                OkHttpClient okHttpClient = new OkHttpClient();

                RequestBody formBody = new FormEncodingBuilder()
                        .add("client_id", AuthenticationService.getAuthorize_client_id())
                        .add("code", auth_code)
                        .add("grant_type", "authorization_code")
                        .add("redirect_uri", AuthenticationService.REDIRECT_URI)
                        .add("client_secret", "3a20127eb087257ad7196098bfd8240746a66b0550d039eb2c1901c025e7cbea")
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                Response response = null;
                try {
                    response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        //Convert the string result to a JSON Object
                        String responseStr = response.body().string();

                        Log.e("Tokenm", "response body:" + responseStr);
                        JSONObject resultJson = new JSONObject(responseStr);
                        //Extract data from JSON Response
                        int expiresIn = resultJson.has("expires_in") ? resultJson.getInt("expires_in") : 0;

                        String accessToken = resultJson.has("access_token") ? resultJson.getString("access_token") : null;
                        String refreshToken = resultJson.has("refresh_token") ? resultJson.getString("refresh_token") : null;
                        long createdAt = resultJson.has("created_at") ? resultJson.getLong("created_at") : 0;
                        Log.d("refreshToken",refreshToken);
                        Log.e("Tokenm", "access token:" + accessToken);
                        if (expiresIn > 0 && accessToken != null) {
                            Log.e("Authorize", "This is the access Token: " + accessToken + ". It will expires in " + expiresIn + " secs");

                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.SECOND, expiresIn);
                            long expireDate = calendar.getTimeInMillis();
                            OAuth oAuth = new OAuth();
                            oAuth.setRefresh_token(refreshToken);
                            oAuth.setAccess_token(accessToken);
                            oAuth.setExpires_in(expiresIn);
                            oAuth.setCraete_at(createdAt);
                            getUserInfo(accessToken, oAuth);

                            return true;
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
            return false;
        }

        @Override
        protected void onPostExecute(Boolean status) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
        }

    }


    private void getUserInfo(final String token, final OAuth oAuth) {
        TesterHomeApi.getInstance().getTopicsService().getCurrentUserInfo(token, new retrofit.Callback<UserDetailResponse>() {
            @Override
            public void success(UserDetailResponse userDetailResponse, retrofit.client.Response response) {
                if (userDetailResponse.getUser() != null) {
                    TesterHomeAccountService.getInstance(WebViewActivity.this)
                            .signIn(userDetailResponse.getUser().getLogin(), token, userDetailResponse.getUser(),oAuth);
                    WebViewActivity.this.finish();

                    NativeApp.getInstance().startTimer();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


}
