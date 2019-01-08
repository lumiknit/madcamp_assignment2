package io.madcamp.jh.madcamp_assignment2;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        initializeFacebook();
    }
    private void initializeFacebook() {
        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            public void onSuccess(LoginResult loginResult) {
                /* Done */
                Log.d("Test@FB", "onSucc");
                LoginHelper.removeDialog();
                LoginHelper.httpPostRegistered(LoginActivity.this);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("Facebook에 로그인을 하시지 않으시면 이 앱의 대부분의 기능을 이용할 수 없습니다.");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
                });
                builder.create().show();
                Log.d("Test@FB", "onCancel");
                LoginHelper.removeDialog();
            }

            @Override
            public void onError(FacebookException error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("Facebook Error");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
                });
                builder.create().show();
                Log.d("Test@FB", "onErr");
                LoginHelper.removeDialog();
            }
        });

        if(AccessToken.getCurrentAccessToken() == null) {
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    /* Done */
                    Log.d("Test@FB", "onSucc");
                    LoginHelper.removeDialog();
                    LoginHelper.httpPostRegistered(LoginActivity.this);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onCancel() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Facebook에 로그인을 하시지 않으시면 이 앱의 대부분의 기능을 이용할 수 없습니다.");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
                    });
                    builder.create().show();
                    Log.d("Test@FB", "onCancel");
                    LoginHelper.removeDialog();
                }

                @Override
                public void onError(FacebookException error) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Facebook Error");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
                    });
                    builder.create().show();
                    Log.d("Test@FB", "onErr");
                    LoginHelper.removeDialog();
                }
            });
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        } else {
            LoginHelper.httpPostRegistered(LoginActivity.this);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
