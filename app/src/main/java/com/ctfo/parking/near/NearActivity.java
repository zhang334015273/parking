package com.ctfo.parking.near;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.cloud.CloudItem;
import com.amap.api.services.cloud.CloudItemDetail;
import com.amap.api.services.cloud.CloudResult;
import com.amap.api.services.cloud.CloudSearch;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.ctfo.parking.R;
import com.ctfo.parking.util.Constant;

import java.util.List;

public class NearActivity extends Fragment implements AMapLocationListener,CloudSearch.OnCloudSearchListener {

    private View view;
    private MapView mapView;
    private AMap aMap;
    private LinearLayout searchLayout;
    private EditText seachEdit;

    //声明mlocationClient对象
    private AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    private AMapLocationClientOption mLocationOption = null;
    //自定义定位小蓝点的Marker
    private Marker locationMarker;
    //中心点
    private LatLonPoint mCenterPoint;
    private String cityName;

    //云检索
    private String sortFiled;
    private List<CloudItem> mCloudItems;
    private CloudSearch mCloudSearch;
    private CloudSearch.Query mQuery;
    private CloudOverlay mPoiCloudOverlay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.near_avtivity,container,false);
        initView(savedInstanceState);

        return view;
    }

    private void initView(Bundle savedInstanceState){
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();

        //设置云检索
        mCloudSearch = new CloudSearch(this.getContext());
        mCloudSearch.setOnCloudSearchListener(this);

        //设置定位
        mlocationClient = new AMapLocationClient(this.getContext());
        mLocationOption = new AMapLocationClientOption();

        //设置定位监听与高精度，定位间隔
        mlocationClient.setLocationListener(this);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(2500);
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();


        //搜索按钮监听
        view.findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locationMarker!=null){
                    //进行云图检索
                    cloudSearchByBound(mCenterPoint);
                }
            }
        });

        searchLayout = view.findViewById(R.id.search_list_layout);
        seachEdit = view.findViewById(R.id.input_search);
        seachEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //进行 本地检索
                cloundSearchByLocal(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //默认距离搜索
        sortFiled = "_distance";

    }



    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(aMapLocation != null){
            if(aMapLocation.getErrorCode() == 0){
                LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                cityName = aMapLocation.getCity();
                //展示自定义定位小蓝点
                if(locationMarker == null) {
                    //首次定位
                    locationMarker = aMap.addMarker(new MarkerOptions().position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.near_location))
                            .anchor(0.5f, 0.5f));
                    mCenterPoint = new LatLonPoint(locationMarker.getPosition().latitude,locationMarker.getPosition().longitude);

                    //首次定位,选择移动到地图中心点并修改级别到15级
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                    //进行云图检索
                    cloudSearchByBound(mCenterPoint);
                } else {

                    LatLng curLatlng = locationMarker.getPosition();
                    if(curLatlng == null || !curLatlng.equals(latLng)) {
                        //locationMarker.setPosition(latLng);
                    }

                }
            }else{
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
                Toast.makeText(this.getContext(), "定位失败:"+aMapLocation.getErrorCode(), Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * 云图周边检索数据
     */
    private void cloudSearchByBound(LatLonPoint point){
        //周边5公里
        CloudSearch.SearchBound bound = new CloudSearch.SearchBound(new LatLonPoint(point.getLatitude(),point.getLongitude()), 5000);
        try{
            mQuery = new CloudSearch.Query(Constant.AMAP_PARKING_TABLE_ID,"",bound);
            mQuery.setPageSize(10);
            //_distance 距离      feeScale 收费标准   true false
            mQuery.setSortingrules(new CloudSearch.Sortingrules(sortFiled,true));
            mCloudSearch.searchCloudAsyn(mQuery);
        }catch (AMapException e){
            e.printStackTrace();
        }
    }

    /**
     * 本地检索
     */
    private void cloundSearchByLocal(String keyWord){
        CloudSearch.SearchBound bound = new CloudSearch.SearchBound(cityName);
        try {
            mQuery = new CloudSearch.Query(Constant.AMAP_PARKING_TABLE_ID, keyWord, bound);
            mQuery.setPageSize(10);
            mCloudSearch.searchCloudAsyn(mQuery);
        } catch (AMapException e) {
            e.printStackTrace();
        }
    }


    /**
     * 检索结果回调
     */
    public void onCloudSearched(CloudResult result, int rCode){
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {
                if (result.getQuery().equals(mQuery)) {

                    if(result.getBound().getShape().equals(CloudSearch.SearchBound.BOUND_SHAPE)){
                        //周边检索
                        mCloudItems = result.getClouds();

                        if (mCloudItems != null && mCloudItems.size() > 0) {
                            aMap.clear();
                            mPoiCloudOverlay = new CloudOverlay(aMap, mCloudItems);
                            mPoiCloudOverlay.removeFromMap();
                            mPoiCloudOverlay.addToMap();
                            mPoiCloudOverlay.zoomToSpan();

                        }
                    } else if(result.getBound().getShape().equals(CloudSearch.SearchBound.LOCAL_SHAPE)){
                        //本地检索
                        searchLayout.removeAllViews();
                        List<CloudItem> items = result.getClouds();
                        for(CloudItem item : items){
                            TextView itemTextView = new TextView(this.getContext());
                            ViewGroup.MarginLayoutParams lParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                            lParams.topMargin = 10;
                            lParams.bottomMargin = 10;
                            itemTextView.setLayoutParams(lParams);
                            itemTextView.setText(item.getTitle());
                            searchLayout.addView(itemTextView);
                        }
                    }

                }
            }
        } else {
            Toast.makeText(this.getContext(), "查询失败:"+rCode, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 精确检索结果回调
     */
    public void onCloudItemDetailSearched(CloudItemDetail item, int rCode){

    }


    /**
     * 以下生命周期需要重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if(mlocationClient != null){
            mlocationClient.onDestroy();
        }
    }
}
