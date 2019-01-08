package io.madcamp.jh.madcamp_assignment2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.facebook.login.LoginManager;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* 탭 초기화 */
        setupTabs();
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

        /* Tab Change Detector */
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) { }
            @Override
            public void onPageSelected(int i) { }
            @Override
            public void onPageScrollStateChanged(int i) {
                if(i == 0) {
                    switch(tabLayout.getSelectedTabPosition()) {
                        case 0:
                            ((Tab1Fragment)adapter.getItem(0)).refresh();
                            break;
                        case 1:
                            ((Tab2Fragment)adapter.getItem(1)).refresh();
                            break;
                        case 2:
                            ((Tab3Fragment)adapter.getItem(2)).updateMarkers();
                            break;
                    }
                }
            }
        });
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
            case R.id.action_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("정말로 로그아웃 할거냥?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LoginManager.getInstance().logOut();
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
