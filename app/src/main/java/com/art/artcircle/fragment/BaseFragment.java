package com.art.artcircle.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.art.artcircle.utils.UmengUtils;
import com.lidroid.xutils.http.HttpHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by star on 15/5/29.
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    protected Context mContext;
    protected Fragment mFragment;
    protected boolean mIsNeedLoadData;

    private long mKeyTime;
    private int mKeyCount;

    protected List<HttpHandler<String>> mHandlerList;

    // 页面根节点
    protected View mRootView;

    protected String mUMengPagerName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getArgumentsData();
        initBaseData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 初始化页面布局
        int res = setLayoutViewId();
        if (res != 0) {
            mRootView = inflater.inflate(res, null);
            initTitle();
            initContent();
            initFoot();
        }
        return mRootView;
    }

    protected abstract void initTitle();

    protected abstract void initFoot();

    @Override
    public void onResume() {
        if (mIsNeedLoadData) {
            loadData();
        } else {
            attachData();
        }
        super.onResume();
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onDestroyView() {
        for (HttpHandler<String> handler : mHandlerList) {
            if (handler != null && !handler.isCancelled()) {
                handler.cancel();
            }
        }
        super.onDestroyView();
    }

    /**
     * 获取Arguments数据
     */
    protected abstract void getArgumentsData();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 设置页面布局
     */
    protected abstract int setLayoutViewId();

    /**
     * 初始化内容布局
     */
    protected abstract void initContent();

    /**
     * 请求数据数据
     */
    protected abstract void loadData();

    /**
     * 为view控件绑定数据
     */
    protected abstract void attachData();

    /**
     * 统一为各种view添加点击事件
     */
    protected void setOnClickListener(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setOnClickListener(this);
            }
        }
    }

    /**
     * 统一为各种view确定可以点击
     */
    protected void setClickEnable(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setEnabled(true);
            }
        }
    }

    /**
     * 统一为各种view确定不可以点击
     */
    protected void setClickDisable(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setEnabled(false);
            }
        }
    }

    /**
     * 统一为各种view确定可见状态
     */
    protected void setViewVisible(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 统一为各种view确定不可见状态
     */
    protected void setViewInvisible(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 统一为各种view确定不可见状态
     */
    protected void setViewGone(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 跳转到activity
     */
    protected void jump(Intent intent) {
        startActivity(intent);
    }

    /**
     * 跳转到activity
     */
    protected void jump(Context context, Class<?> targetClass) {
        Intent intent = new Intent();
        intent.setClass(context, targetClass);
        jump(intent);
    }

    /**
     * 跳转到activity
     */
    protected void jump(Context context, Class<?> targetClass, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(context, targetClass);
        startActivityForResult(intent, requestCode);
    }

//    /**
//     * 显示debug信息
//     */
//    protected void showDebugInfo(Context context, String extraContent) {
//        if ((System.currentTimeMillis() - mKeyTime) > 500) {
//            mKeyCount = 0;
//        } else {
//            mKeyCount++;
//            if (mKeyCount > 10) {
//                DialogUtils.showDebugInfoDialog(context, extraContent);
//            }
//        }
//        mKeyTime = System.currentTimeMillis();
//    }

    private void initBaseData() {
        mContext = getActivity();
        mFragment = this;
        mIsNeedLoadData = true;
        mHandlerList = new ArrayList<>();
        initData();
    }
}
