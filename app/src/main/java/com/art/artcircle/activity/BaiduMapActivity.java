
package com.art.artcircle.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.art.artcircle.R;
import com.art.artcircle.adapter.BaiduMapAdatper;
import com.art.artcircle.constant.Constant;
import com.art.artcircle.constant.IntentConstant;
import com.art.artcircle.dialog.ProgressDialog;
import com.art.artcircle.manager.ArtCircleUserInfoManager;
import com.art.artcircle.utils.AnimatorUtils;
import com.art.artcircle.utils.AppUtils;
import com.art.artcircle.utils.BitmapUtils;
import com.art.artcircle.utils.DateUtils;
import com.art.artcircle.utils.DensityUtil;
import com.art.artcircle.utils.DialogUtils;
import com.art.artcircle.utils.FileUtils;
import com.art.artcircle.utils.NetUtils;
import com.art.artcircle.utils.StringUtils;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.CoordinateConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BaiduMapActivity extends Activity implements View.OnClickListener, BaiduMapAdatper.MyItemClickListener {
    private TextView mTitleView;
    private TextView mBackView;
    private TextView mSendView;

    private RecyclerView mRecyclerView;

    private ImageView mOriginal;

    private BaiduMapAdatper mAdatper;

    private LatLng mOriginalLL, mCurrentLL;//初始化时的经纬度和地图滑动时屏幕中央的经纬度

    static MapView mMapView;
    private GeoCoder mSearch;
    private LocationClient mLocClient;// 定位相关
    public MyLocationListenner myListener = new MyLocationListenner();

    private PoiSearch mPoiSearch;

    private List<PoiInfo> mDatas = new ArrayList<>();
    private PoiInfo mLastInfo;
    public static BaiduMapActivity mInstance;
    private ProgressDialog mProgressDialog;
    private BaiduMap mBaiduMap;
    private MapStatusUpdate mMapStatusUpdate;

    private boolean mChangeState = true;//当滑动地图时再进行附近搜索

    private LinearLayout mRefreshLinearLayout;
    private BaiduSDKReceiver mBaiduReceiver;

    private String mFileName;

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class BaiduSDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                DialogUtils.showShortPromptToast(mInstance, R.string.please_check);
            } else if (TextUtils.equals(intent.getAction(), SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                DialogUtils.showShortPromptToast(mInstance, R.string.Network_error);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        mInstance = this;
        setContentView(R.layout.activity_baidumap);
        init();
    }

    private void init() {
        mTitleView = (TextView) findViewById(R.id.tv_title);
        mTitleView.setText(R.string.location_message);
        mBackView = (TextView) findViewById(R.id.tv_back);
        mSendView = (TextView) findViewById(R.id.tv_right);
        mSendView.setText(R.string.button_send);
        mBackView.setOnClickListener(this);
        mSendView.setOnClickListener(this);
        mOriginal = (ImageView) findViewById(R.id.img_local_myself);
        mRecyclerView = (RecyclerView) findViewById(R.id.bmap_rcv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mMapView = (MapView) findViewById(R.id.bmap_view);
        mSearch = GeoCoder.newInstance();
        mRefreshLinearLayout = (LinearLayout) findViewById(R.id.ll_bmap_refresh);

        mAdatper = new BaiduMapAdatper();
        mRecyclerView.setAdapter(mAdatper);
        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra(IntentConstant.LATITUDE, 0);
        LocationMode mCurrentMode = LocationMode.NORMAL;
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
        mPoiSearch = PoiSearch.newInstance();
        mMapView.setLongClickable(true);
        // 隐藏百度logo ZoomControl
        int count = mMapView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ImageView || child instanceof ZoomControls) {
                child.setVisibility(View.INVISIBLE);
            }
        }
        // 隐藏比例尺
        mMapView.showScaleControl(false);
        if (!NetUtils.isNetworkConnected(this)) {
            DialogUtils.showShortPromptToast(BaiduMapActivity.this, R.string.network_unavailable);
        } else if (!AppUtils.isOPen(this)) {
            DialogUtils.showShortPromptToast(BaiduMapActivity.this, R.string.gps_unavailable);
        } else {
            if (latitude == 0) {
                mMapView = new MapView(this, new BaiduMapOptions());
                mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, null));
                mBaiduMap.setMyLocationEnabled(true);
                showMapWithLocationClient();
                setOnclick();
            } else {
                double longitude = intent.getDoubleExtra(IntentConstant.LONGITUDE, 0);
                String address = intent.getStringExtra(IntentConstant.ADDRESS);
                LatLng p = new LatLng(latitude, longitude);
                mMapView = new MapView(this, new BaiduMapOptions().mapStatus(new MapStatus.Builder().target(p).build()));
                mRecyclerView.setVisibility(View.GONE);
                mRefreshLinearLayout.setVisibility(View.GONE);
                mOriginal.setVisibility(View.GONE);
                showMap(latitude, longitude, address.split("|")[1]);
            }
        }
        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mBaiduReceiver = new BaiduSDKReceiver();
        registerReceiver(mBaiduReceiver, iFilter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_right:
                if (!NetUtils.isNetworkConnected(this)) {
                    DialogUtils.showShortPromptToast(BaiduMapActivity.this, R.string.network_unavailable);
                } else if (!AppUtils.isOPen(this)) {
                    DialogUtils.showShortPromptToast(BaiduMapActivity.this, R.string.gps_unavailable);
                } else {
                    sendLocation();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        View mSelectImgView = view.findViewById(R.id.img_location_checked);
        mAdatper.setSelection(position);
        mSelectImgView.setVisibility(View.VISIBLE);
        AnimatorUtils.startHeartBeat(mSelectImgView);
        mChangeState = false;
        PoiInfo info = mDatas.get(position);
        LatLng llA = info.location;
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(llA, 17.0f);
        mBaiduMap.animateMapStatus(u);
        mLastInfo = info;
    }

    /**
     * 设置点击事件
     */
    private void setOnclick() {
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                mChangeState = true;
            }
        });
        mOriginal.setOnClickListener(new MyOnClickListener());
        mAdatper.setOnItemClickListener(this);
        mPoiSearch.setOnGetPoiSearchResultListener(new MyGetPoiSearchResult());
        mBaiduMap.setOnMapStatusChangeListener(new MyMapStatusChangeListener());
        mSearch.setOnGetGeoCodeResultListener(new MyGetGeoCoderResultListener());
    }

    private boolean isSearchFinished;
    private boolean isGeoCoderFinished;

    private void refreshAdapter() {
        if (isSearchFinished && isGeoCoderFinished) {
            mAdatper.resetData(mDatas);
            mAdatper.notifyDataSetChanged();
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            mRefreshLinearLayout.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            isSearchFinished = false;
            isGeoCoderFinished = false;
        }
    }


    /**
     * 根据关键字查找附近的位置信息
     */
    private class MyGetPoiSearchResult implements OnGetPoiSearchResultListener {

        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult.getAllPoi() != null) {
                mDatas.addAll(poiResult.getAllPoi());
                isSearchFinished = true;
                refreshAdapter();
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }
    }

    /**
     * 根据经纬度进行反地理编码
     */
    private class MyGetGeoCoderResultListener implements OnGetGeoCoderResultListener {

        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR && mLastInfo != null) {
                if (!TextUtils.isEmpty(result.getAddress())) {
                    mLastInfo.address = result.getAddress();
                }
                if (result.getLocation() != null) {
                    mLastInfo.location = result.getLocation();
                }
                mLastInfo.name = "[位置]";
                mDatas.add(mLastInfo);
                mAdatper.setSelection(0);
                isGeoCoderFinished = true;
                refreshAdapter();
            }
        }
    }

    /**
     * 监听位置发生了变化
     */
    private class MyMapStatusChangeListener implements BaiduMap.OnMapStatusChangeListener {

        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {
            if (mChangeState) {
                mDatas.clear();
                mRefreshLinearLayout.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onMapStatusChange(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {
            if (mChangeState) {
                boolean isFirstLoad = true;
                if (isFirstLoad) {
                    mOriginalLL = mapStatus.target;
                }
                mCurrentLL = mapStatus.target;
                // 反Geo搜索
                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(mCurrentLL));
                mPoiSearch.searchNearby(new PoiNearbySearchOption().keyword("小区").location(mCurrentLL).radius(1000));
            }
        }
    }


    /**
     * 查看别人发过来，或者已经发送出去的位置信息
     *
     * @param latitude   维度
     * @param longtitude 经度
     * @param address    详细地址信息
     */
    private void showMap(double latitude, double longtitude, String address) {
        mSendView.setVisibility(View.GONE);
        LatLng llA = new LatLng(latitude, longtitude);
        OverlayOptions ooA = new MarkerOptions().position(llA).icon(BitmapDescriptorFactory
                .fromResource(R.drawable.icon_yourself_location))
                .zIndex(4).draggable(true);
        mBaiduMap.addOverlay(ooA);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(llA, 17.0f);
        mBaiduMap.animateMapStatus(u);
    }

    /**
     * 显示当前的位置信息
     */
    private void showMapWithLocationClient() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.showProgressDialog(this, getString(R.string.loading));
        }
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("gcj02");
        option.setIsNeedAddress(true);
        option.setScanSpan(10000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    @Override
    protected void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
        if (mLocClient != null) {
            mLocClient.stop();
        }
        super.onPause();
        mLastInfo = null;
    }


    @Override
    protected void onDestroy() {
        if (mLocClient != null) {
            mLocClient.stop();
        }
        if (mMapView != null) {
            mMapView.onDestroy();
        }
        unregisterReceiver(mBaiduReceiver);
        super.onDestroy();
    }


    /**
     * 监听函数，有新位置的时候，格式化成字符串，输出到屏幕中
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            mSendView.setEnabled(true);

            if (mLastInfo != null) {
                return;
            }
            mLastInfo = new PoiInfo();
            mBaiduMap.clear();

            mLastInfo.location = new LatLng(location.getLatitude(), location.getLongitude());
            mLastInfo.address = location.getAddrStr();
            mLastInfo.name = "[位置]";


            LatLng ll = new LatLng(location.getLatitude() - 0.0002, location.getLongitude());
            CoordinateConverter converter = new CoordinateConverter();//坐标转换工具类
            converter.coord(ll);//设置源坐标数据
            converter.from(CoordinateConverter.CoordType.COMMON);//设置源坐标类型
            LatLng convertLatLng = converter.convert();
//            OverlayOptions myselfOOA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory
//                    .fromResource(R.drawable.icon_yourself_location))
//                    .zIndex(4).draggable(true);
//            mBaiduMap.addOverlay(myselfOOA);
            mMapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
            mBaiduMap.animateMapStatus(mMapStatusUpdate);
        }
    }


    private class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (mCurrentLL != mOriginalLL) {
                mChangeState = true;
                mBaiduMap.animateMapStatus(mMapStatusUpdate);
            }
        }
    }

    public void sendLocation() {
        if (mLastInfo != null) {
            // 获取截图，回调异步方法获取图片数据，保存在指定目录下
            mFileName = StringUtils.getString(ArtCircleUserInfoManager.getArtCircleUserId(mInstance), Constant.UNDERLINE, DateUtils.getTimestampStr(), Constant.IMG_POSTFIX);
            final File dir = new File(Constant.SAVE_MAP_PICTURE_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            mBaiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
                @Override
                public void onSnapshotReady(Bitmap bitmap) {
                    File file = new File(dir, mFileName);
                    int height = DensityUtil.dip2px(mInstance, mInstance.getResources().getInteger(R.integer.chat_gif_img_default_min_height)) * bitmap.getWidth() / DensityUtil.dip2px(mInstance, mInstance.getResources().getInteger(R.integer.chat_default_max_height));
                    bitmap = Bitmap.createBitmap(bitmap, 0, (bitmap.getHeight() - height) / 2, bitmap.getWidth(), height);
                    BitmapUtils.compressBmpToFile(bitmap, file);
                    mHandler.sendEmptyMessage(0);
                }
            });
        } else {
            DialogUtils.showShortPromptToast(this, R.string.unable_to_get_loaction);
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (mLastInfo != null) {
                FileUtils.sendImgSaveBroadcast(mInstance, mFileName);//发送广播提示更新图库，就可以找到保存的图片
                Intent intent = getIntent();
                intent.putExtra(IntentConstant.LATITUDE, mLastInfo.location.latitude);
                intent.putExtra(IntentConstant.LONGITUDE, mLastInfo.location.longitude);
                intent.putExtra(IntentConstant.ADDRESS, mLastInfo.address);
                intent.putExtra(IntentConstant.NAME, mLastInfo.name);
                intent.putExtra(IntentConstant.LOCATION_IMG, mFileName);
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        }
    };
}
