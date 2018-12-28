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
import android.widget.ListView;
import android.widget.RadioGroup;
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
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.services.cloud.CloudItem;
import com.amap.api.services.cloud.CloudItemDetail;
import com.amap.api.services.cloud.CloudResult;
import com.amap.api.services.cloud.CloudSearch;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.ctfo.parking.R;
import com.ctfo.parking.util.Constant;
import com.ctfo.parking.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
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
    private List<CloudItem> localCloudItems;


    //item detail view
    private LinearLayout itemDetailLayout;
    private TextView itemTitle;
    private TextView allFreeSpaces;
    private TextView totalVipSpaces;
    private TextView carSpaceSize;
    private TextView totalVipFreeSpaces;
    private TextView feeScale;

    private TextView pay;
    private TextView toHere;
    private CloudItem  itemDetail;



    //底部附近查询结果的view
    private TextView showResult;
    private LinearLayout boundResultLayout;
    private boolean isShowResult = false;
    private ListView resultListView;
    private BoundResultAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.near_avtivity,container,false);

        //默认距离搜索
        sortFiled = "feeScale";

        initView(savedInstanceState);

        return view;
    }

    private void initView(Bundle savedInstanceState){

        initMapView(savedInstanceState);

        initSearchView();

        initItemDetailView();

        initBoundResultView();

    }

    /**
     * 初始化底部布局
     */
    private void initBoundResultView(){
        showResult = view.findViewById(R.id.show_result);
        boundResultLayout = view.findViewById(R.id.bound_result_layout);
        resultListView = view.findViewById(R.id.list_view);
        adapter = new BoundResultAdapter(this.getContext());
        resultListView.setAdapter(adapter);
        showResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShowResult){
                    boundResultLayout.setVisibility(View.GONE);
                    showResult.setText("点击展开更多结果");
                }else{
                    boundResultLayout.setVisibility(View.VISIBLE);
                    showResult.setText("点击关闭结果");
                }
                isShowResult = !isShowResult;
            }
        });
        RadioGroup rg = view.findViewById(R.id.sort_tab_radio);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.sort_by_price:
                        sortFiled = "feeScale";
                        break;
                    case R.id.sort_by_distance:
                        sortFiled = "_distance";
                        break;
                }
                cloudSearchByBound(mCenterPoint);
            }
        });
    }

    /**
     *  初始化topitem详情view
     */
    private void initItemDetailView(){
        itemDetailLayout = view.findViewById(R.id.item_detail_layout);
        itemTitle = view.findViewById(R.id.item_title);
        allFreeSpaces = view.findViewById(R.id.all_free_spaces);
        totalVipSpaces = view.findViewById(R.id.total_vip_spaces);
        carSpaceSize = view.findViewById(R.id.car_space_size);
        totalVipFreeSpaces = view.findViewById(R.id.total_vip_free_spaces);
        feeScale = view.findViewById(R.id.fee_scale);
        pay = view.findViewById(R.id.pay);
        toHere = view.findViewById(R.id.to_here);

    }

    /**
     * 加载item数据
     * @param item
     */
    private void loadItemDetailData(CloudItem item){
        itemDetail = item;
        itemDetailLayout.setVisibility(View.VISIBLE);
        itemTitle.setText(StringUtil.formatString(item.getTitle(),"-"));
        HashMap<String,String> map = item.getCustomfield();
        if(map!= null){
            allFreeSpaces.setText(StringUtil.formatString(map.get("allFreeSpaces"),"-"));
            totalVipSpaces.setText(StringUtil.formatString(map.get("totalVipSpaces"),"-"));
            carSpaceSize.setText(StringUtil.formatString(map.get("carSpaceSize"),"-"));
            totalVipFreeSpaces.setText(StringUtil.formatString(map.get("totalVipFreeSpaces"),"-"));
            feeScale.setText(StringUtil.formatString(map.get("feeScale"),"-")+"元/小时");
        }else{
            allFreeSpaces.setText("-");
            totalVipSpaces.setText("-");
            carSpaceSize.setText("-");
            totalVipFreeSpaces.setText("-");
            feeScale.setText("-");
        }
    }

    /**
     * 初始化搜索view
     */
    private void initSearchView(){
        //搜索按钮监听
        view.findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locationMarker!=null){
                    //进行云图检索
                    itemDetailLayout.setVisibility(View.GONE);
                    searchLayout.removeAllViews();
                    overlayToMap(localCloudItems,CloudSearch.SearchBound.LOCAL_SHAPE);
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
                itemDetailLayout.setVisibility(View.GONE);
                if(s.length() > 0){
                    searchLayout.removeAllViews();
                    cloundSearchByLocal(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /**
     * 初始化地图view
     * @param savedInstanceState
     */
    private void initMapView(Bundle savedInstanceState){
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.getObject() instanceof  CloudItem){
                    loadItemDetailData((CloudItem)marker.getObject());
                    return true;
                }
                return false;
            }
        });

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
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
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
            mQuery.setPageSize(20);
            //_distance 距离      feeScale 收费标准   true false
            mQuery.setSortingrules(new CloudSearch.Sortingrules(sortFiled,false));
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
            mQuery.setPageSize(20);
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
                        adapter.setItems(mCloudItems);
                        adapter.notifyDataSetChanged();
                        resultListView.setSelection(0);
                        if (mCloudItems != null && mCloudItems.size() > 0) {
                            overlayToMap(mCloudItems,CloudSearch.SearchBound.BOUND_SHAPE);
                        }
                    } else if(result.getBound().getShape().equals(CloudSearch.SearchBound.LOCAL_SHAPE)){
                        //本地检索
                        searchLayout.removeAllViews();
                        localCloudItems = result.getClouds();
                        for(final CloudItem item : localCloudItems){
                            TextView itemTextView = new TextView(this.getContext());
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(0,5,10,5);
                            itemTextView.setLayoutParams(lp);
                            itemTextView.setText(item.getTitle());
                            itemTextView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    searchLayout.removeAllViews();
                                    List<CloudItem> items = new ArrayList<CloudItem>();
                                    items.add(item);
                                    overlayToMap(items,CloudSearch.SearchBound.LOCAL_SHAPE);

                                    loadItemDetailData(item);
                                }
                            });
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
     * 添加标注点
     * @param items
     */
    private void overlayToMap(List<CloudItem> items, String bound){
        aMap.clear();
        if(items!=null && items.size()>0){
            mPoiCloudOverlay = new CloudOverlay(aMap, items);
            mPoiCloudOverlay.removeFromMap();
            mPoiCloudOverlay.addToMap();
            if (bound.equals(CloudSearch.SearchBound.BOUND_SHAPE)){
                mPoiCloudOverlay.zoomToSpan(14);
            }else if(bound.equals(CloudSearch.SearchBound.LOCAL_SHAPE)){
                if (items.size()>1){
                    mPoiCloudOverlay.zoomToSpan(12);
                }else{
                    mPoiCloudOverlay.zoomToSpan(20);
                }
            }
        }
    }


    /**
     * 精确检索结果回调
     */
    public void onCloudItemDetailSearched(CloudItemDetail item, int rCode){

    }

    private void startNavi(CloudItem item){
        Poi start = new Poi("三元桥", new LatLng(39.96087,116.45798), "");
        Poi end = new Poi("北京站", new LatLng(39.904556, 116.427231), "B000A83M61");
        AmapNaviPage.getInstance().showRouteActivity(this.getContext(), new AmapNaviParams(start, null, end, AmapNaviType.DRIVER), null);
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
