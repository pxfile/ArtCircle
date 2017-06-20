package com.art.artcircle.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;

import com.art.artcircle.R;
import com.art.artcircle.manager.ArtCircleUserInfoManager;
import com.art.artcircle.utils.AppUtils;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;

/**
 * 开屏页
 */
public class SplashActivity extends BaseActivity {

    private static final int SleepTime = 2000;

    private View mContainerView;

    private Intent mIntent;

    @Override
    protected void initData() {
        mIntent = new Intent();
        isNeedDoubleClick = true;
        setSwipeBackEnable(false);

//        MobclickAgent.updateOnlineConfig(this);
    }

    @Override
    protected int setLayoutViewId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initContent() {
        mContainerView = findViewById(R.id.ll_login);
        View registerView = findViewById(R.id.tv_register);
        View loginView = findViewById(R.id.tv_login);

        setOnClickListener(registerView, loginView);
    }

    @Override
    protected void attachData() {
        mContainerView.setVisibility(AppUtils.isLogin(mContext) ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void refreshData() {
        if (AppUtils.isLogin(mContext)) {
            new Handler().post(new LoadUserData());
        } else if (!ArtCircleUserInfoManager.getFirstEnter(mContext)) {
            mIntent.setClass(mContext, GuidePageActivity.class);
            new Handler().postDelayed(new AutoStart(mIntent),0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_register:
                mIntent.setClass(mContext, RegisterActivity.class);
                jump(mIntent);
                finish();
                break;
            case R.id.tv_login:
                mIntent.setClass(mContext, LoginActivity.class);
                jump(mIntent);
                finish();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 加载用户数据
     */
    private class LoadUserData implements Runnable {

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            EMGroupManager.getInstance().loadAllGroups();
            EMChatManager.getInstance().loadAllConversations();
            long costTime = System.currentTimeMillis() - start;

            mIntent.setClass(mContext, MainActivity.class);
            new Handler().postDelayed(new AutoStart(mIntent), SleepTime - costTime);
        }
    }

    /**
     * 启动activity
     */
    private class AutoStart implements Runnable {
        private Intent intent;

        private AutoStart(Intent intent) {
            this.intent = intent;
        }

        @Override
        public void run() {
            jump(intent);
            finish();
        }
    }
}
