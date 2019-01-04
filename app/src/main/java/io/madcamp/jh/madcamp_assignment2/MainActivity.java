package io.madcamp.jh.madcamp_assignment2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    final int PERMISSION_REQ_CODE = 1;
    CallbackManager callbackManager;
    AccessToken accessToken;
    LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* 탭 초기화 */
        setupTabs();

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile","email");
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //login success
                //name, email, unique key
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

        accessToken = AccessToken.getCurrentAccessToken();

        /*
        boolean isLoggedIn;
        if(isLoggedIn = accessToken != null && !accessToken.isExpired()){
            System.out.println("checkout!!!"+accessToken.getUserId());
            //{AccessToken token:ACCESS_TOKEN_REMOVED permissions:[public_profile, email]}
        }
        */

    }

    private void setupTabs() {
        /* 필요한 View를 불러옴 */
        ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.sliding_tabs);
        /* TabPagerAdapter 추가 */
        viewPager.setAdapter(new TabPagerAdapter(getSupportFragmentManager(), MainActivity.this));
        /* tabLayout 초기화 */
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
