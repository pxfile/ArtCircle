package com.art.artcircle.activity;

import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.art.artcircle.R;
import com.art.artcircle.constant.EventEnum;
import com.art.artcircle.constant.IntentConstant;
import com.art.artcircle.constant.NetConstant;
import com.art.artcircle.utils.StringUtils;
import com.art.artcircle.utils.UmengUtils;

/**
 * Created by Administrator on 2015/9/12.
 */
public class ConnectionDistributeActivity extends BaseActivity {

    private WebView mWebView;

    private int mUserID;

    @Override
    protected void getIntentData() {
        mUserID = getIntent().getIntExtra(IntentConstant.USER_ID,-1);
    }

    @Override
    protected void initTitle() {
        TextView mTitle = (TextView) findViewById(R.id.tv_title);
        mTitle.setText(R.string.connection_distribute);
        TextView mBackView = (TextView) findViewById(R.id.tv_back);
        setOnClickListener(mBackView);
    }

    @Override
    protected void initContent() {
        mWebView = (WebView) findViewById(R.id.connection_distribute_wv);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(StringUtils.getString(NetConstant.CONNECTION_DISTRIBUTE,NetConstant.USER_ID,mUserID));
    }

    @Override
    protected void initFooter() {
        super.initFooter();
    }

    @Override
    protected void attachData() {
        super.attachData();
    }

    @Override
    protected int setLayoutViewId() {
        return R.layout.activity_connection_distribute;
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:
                UmengUtils.onEvent(mContext, EventEnum.CONNECTION_DISTRIBUTE_BACK_CLICK);
                finish();
                break;
            default:
                super.onClick(v);
                break;
        }

    }
}
