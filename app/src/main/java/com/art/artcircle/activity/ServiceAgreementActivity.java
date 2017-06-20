package com.art.artcircle.activity;

import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.art.artcircle.R;
import com.art.artcircle.constant.NetConstant;
import com.art.artcircle.utils.NetUtils;

public class ServiceAgreementActivity extends BaseActivity {
    private WebView mWebView;

    @Override
    protected int setLayoutViewId() {
        return R.layout.activity_service_agreement;
    }

    @Override
    protected void initTitle() {
        TextView backView = (TextView) findViewById(R.id.tv_back);
        TextView titleView = (TextView) findViewById(R.id.tv_title);
        titleView.setText(getString(R.string.art_circle_service_agreement));
        setOnClickListener(backView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
        }
    }

    @Override
    protected void initContent() {
        mWebView = (WebView) findViewById(R.id.wv_service_agreement);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.loadUrl(NetConstant.SERVICE_AGREEMENT_URL);
    }
}
