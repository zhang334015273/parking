package com.ctfo.parking.splash;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.ctfo.parking.R;
import com.ctfo.parking.home.HomeActivity;
import com.ctfo.parking.mine.LoginActivity;
import com.ctfo.parking.mine.MineActivity;
import com.ctfo.parking.near.NearActivity;
import com.ctfo.parking.util.UserSPUtil;

/**
 * 首页tab界面
 */
public class NaviTabActivity extends FragmentActivity {


    private HomeActivity homeActivity;
    private NearActivity nearActivity;
    private MineActivity mineActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navi_tab_activity);

        initView();

        initFragment();

    }

    /**
     * 初始化view
     */
    private void initView(){
        BottomNavigationView naviView = findViewById(R.id.navigation);
        //tab切换事件
        naviView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_home:
                        showFragment(homeActivity);
                    return true;
                    case R.id.navigation_near:
                        showFragment(nearActivity);
                    return true;
                    case R.id.navigation_mine:
                        //如果登录才会显示界面
                        if (UserSPUtil.isLogin()){
                            showFragment(mineActivity);
                        }else {
                            startActivity(new Intent(NaviTabActivity.this, LoginActivity.class));
                        }
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 初始化fragment
     */
    private void initFragment(){
        homeActivity = new HomeActivity();
        nearActivity = new NearActivity();
        mineActivity = new MineActivity();
        addFragment(homeActivity);
        addFragment(nearActivity);
        addFragment(mineActivity);
        //默认显示首页
        showFragment(homeActivity);
    }

    /**
     * 添加 fragment
     * @param fragment
     */
    private void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content, fragment);
        fragmentTransaction.commit();
    }

    /**
     * 显示 fragment
     * @param fragment
     */
    private void showFragment(Fragment fragment){
        hideAllFragment();
        getSupportFragmentManager().beginTransaction().show(fragment).commit();
    }

    /**
     * 隐藏
     * @param fragment
     */
    private void hideFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().hide(fragment).commit();
    }

    /**
     * 隐藏所有
     */
    private void hideAllFragment(){
        hideFragment(homeActivity);
        hideFragment(nearActivity);
        hideFragment(mineActivity);
    }


}
