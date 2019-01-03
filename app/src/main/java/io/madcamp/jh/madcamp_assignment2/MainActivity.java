package io.madcamp.jh.madcamp_assignment2;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    final int PERMISSION_REQ_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* 탭 초기화 */
        setupTabs();
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

}
