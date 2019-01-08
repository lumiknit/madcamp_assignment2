package io.madcamp.jh.madcamp_assignment2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.JsonArray;

import java.util.Arrays;

import ai.fritz.core.Fritz;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class MainActivity extends AppCompatActivity {
    final int PERMISSION_REQ_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* 탭 초기화 */
        setupTabs();


        //initializeFritz();
    }

    private void setupTabs() {
        /* 필요한 View를 불러옴 */
        final TabLayout tabLayout = (TabLayout)findViewById(R.id.sliding_tabs);
        final ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);
        final TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        /* TabPagerAdapter 추가 */
        viewPager.setAdapter(adapter);
        /* tabLayout 초기화 */
        tabLayout.setupWithViewPager(viewPager);

//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int i, float v, int i1) { }
//            @Override
//            public void onPageSelected(int i) {
//                if(i == 2) {
//                    /* Load List */
//                    Tab3Fragment frag3 = (Tab3Fragment)adapter.getItem(2);
//                    frag3.updateMarkers();
//                }
//            }
//            @Override
//            public void onPageScrollStateChanged(int i) { }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_test:

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.github.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                Log.d("Test@Retrofit", "Built");

                MyService service = retrofit.create(MyService.class);
                Call<JsonArray> request = service.getUserRepositories("lumiknit");
                request.enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                        Log.d("Test@Retrofit", "Responsed");
                        Log.d("Test@Retrofit", response.body().toString());
                        Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<JsonArray> call, Throwable t) {
                        Log.d("Test@Retrofit", "Failed");
                        Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public static interface MyService {
        @GET("users/{user}/repos")
        Call<JsonArray> getUserRepositories(@Path("user") String userName);
    }
}
