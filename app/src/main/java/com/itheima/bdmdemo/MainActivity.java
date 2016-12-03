package com.itheima.bdmdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.itheima.bdmdemo.overlay.PoiOverlay;
import com.itheima.bdmdemo.overlay.TransitRouteOverlay;

import java.util.ArrayList;
import java.util.List;

import static android.util.Log.d;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private MapView mMapView;
    private BaiduMap mMap;
    private LatLng mLatLng = new LatLng(22.581981, 113.929588);
    private BitmapDescriptor mMarkBitmap;

    private PoiSearch mPoiSearch;
    private RoutePlanSearch mSearch;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMap = mMapView.getMap();
        //构建Marker图标
        mMarkBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding);
        mMap.setOnMarkerClickListener(mOnMarkerClickListener);
        mMap.setOnMapClickListener(mOnMapClickListener);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);

        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener( routeListener);
        //初始化位置
//        translate();

        Log.d(TAG, "startLocaction");
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        initLocation();
        mLocationClient.start();

    }

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Log.d(TAG, bdLocation.getCity() + " " + bdLocation.getDistrict() + " " + bdLocation.getStreet());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mSearch.destroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.zoom_in:
                zoomIn();
                break;
            case R.id.zoom_out:
                zoomOut();
                break;
            case R.id.rotate:
                rotate();
                break;
            case R.id.translate:
                translate();
                break;
            case R.id.mark_overlay:
                addMarkerOverlay();
                break;
            case R.id.circle_overlay:
                addCircleOverlay();
                break;
            case R.id.poly_overlay:
                addPolyOverlay();
                break;
            case R.id.text_overlay:
                addTextOverlay();
                break;
            case R.id.cancle_overlay:
                mMap.clear();
                break;
            case R.id.poi_search:
                poiSearch();
                break;
            case R.id.route_plan:
                routePlan();
                break;

        }
        return true;
    }

    private void routePlan() {
/*        PlanNode stMassNode = PlanNode.withCityNameAndPlaceName("北京", "天安门");
        PlanNode enMassNode = PlanNode.withCityNameAndPlaceName("上海", "东方明珠");
        mSearch.masstransitSearch(new MassTransitRoutePlanOption().from(stMassNode).to(enMassNode));*/

        PlanNode stMassNode = PlanNode.withCityNameAndPlaceName("深圳", "兴东");
        PlanNode enMassNode = PlanNode.withCityNameAndPlaceName("深圳", "高新园");
        mSearch.transitSearch(new TransitRoutePlanOption().city("深圳").from(stMassNode).to(enMassNode));

    }

    private void poiSearch() {
        PoiNearbySearchOption poiNearbySearchOption = new PoiNearbySearchOption().keyword("超市").location(mLatLng).radius(300);
        mPoiSearch.searchNearby(poiNearbySearchOption);
    }

    private void addTextOverlay() {
        OverlayOptions overlayOptions = new TextOptions().text("黑马程序员").fontColor(Color.BLACK).fontSize(30).position(mLatLng);
        mMap.addOverlay(overlayOptions);
    }

    private void addPolyOverlay() {
        List<LatLng> latLngs = new ArrayList<LatLng>();
        latLngs.add(new LatLng(22.582803, 113.930234));
        latLngs.add(new LatLng(22.581843, 113.931209));
        latLngs.add(new LatLng(22.580417, 113.929435));
        latLngs.add(new LatLng(22.581372, 113.928563));
        OverlayOptions overlayOptions = new PolygonOptions().points(latLngs).stroke(new Stroke(5, Color.GREEN)).fillColor(0xAAFFFF00);
        mMap.addOverlay(overlayOptions);
    }

    private void addCircleOverlay() {
        OverlayOptions overlayOptions = new CircleOptions().center(mLatLng).fillColor(Color.BLUE).radius(30).stroke(new Stroke(5, Color.GREEN));//30m
        mMap.addOverlay(overlayOptions);
    }

    private void rotate() {
        MapStatus mapStatus = mMap.getMapStatus();
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.rotate(mapStatus.rotate + 90);//逆时针旋转
        MapStatusUpdate rotateUpdate = MapStatusUpdateFactory.newMapStatus(builder.build());
        mMap.animateMapStatus(rotateUpdate);
    }

    private void zoomOut() {
        mMap.animateMapStatus(MapStatusUpdateFactory.zoomOut());
        mMap.setOnMapStatusChangeListener(mOnMapStatusChangeListener);
    }

    private void zoomIn() {
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomIn();
        mMap.animateMapStatus(mapStatusUpdate);//动画方式改变地图状态
//                mMap.setMapStatus(mapStatusUpdate);//改变地图状态
        mMap.setOnMapStatusChangeListener(mOnMapStatusChangeListener);
    }

    private void addMarkerOverlay() {
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(mLatLng).icon(mMarkBitmap)
                .animateType(MarkerOptions.MarkerAnimateType.grow)
                .title("中粮商务公园");
        //在地图上添加Marker，并显示
        mMap.addOverlay(option);
    }

    private void translate() {
        MapStatus.Builder translateBuilder = new MapStatus.Builder();
        translateBuilder.target(mLatLng).zoom(18);
        mMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(translateBuilder.build()));
    }

    private BaiduMap.OnMapStatusChangeListener mOnMapStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {
        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {
            Toast.makeText(MainActivity.this, "onMapStatusChangeStart: " + mapStatus.zoom, Toast.LENGTH_SHORT).show();
        }


        @Override
        public void onMapStatusChange(MapStatus mapStatus) {
            d(TAG, "onMapStatusChange: ");
        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {
            Toast.makeText(MainActivity.this, "onMapStatusChangeFinish: " + mapStatus.zoom, Toast.LENGTH_SHORT).show();
        }
    };

    private BaiduMap.OnMarkerClickListener mOnMarkerClickListener = new BaiduMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
//            Toast.makeText(MainActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();
            showInfoWindow(marker.getTitle(), marker.getPosition());
            return true;
        }
    };

    private void showInfoWindow(String title, LatLng position) {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.infowindow, null);
        TextView viewById = (TextView) view.findViewById(R.id.infowindo_title);
        viewById.setText(title);
        InfoWindow infoWindow = new InfoWindow(view, position, -70);
        mMap.showInfoWindow(infoWindow);
    }

    private BaiduMap.OnMapClickListener mOnMapClickListener = new BaiduMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            mMap.hideInfoWindow();
        }

        @Override
        public boolean onMapPoiClick(MapPoi mapPoi) {
            return false;
        }
    };

    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){

        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            Toast.makeText(MainActivity.this, "附近有 " + poiResult.getTotalPoiNum() + "个超市", Toast.LENGTH_SHORT).show();
//            markPoiResult(poiResult);
            if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                return;
            }
            if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                mMap.clear();
                //创建PoiOverlay
                PoiOverlay overlay = new MyPoiOverlay(mMap);
                //设置overlay可以处理标注点击事件
                mMap.setOnMarkerClickListener(overlay);
                //设置PoiOverlay数据
                overlay.setData(poiResult);
                //添加PoiOverlay到地图中
                overlay.addToMap();
                overlay.zoomToSpan();
                return;
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };

    private void markPoiResult(PoiResult poiResult) {
        for (int i = 0; i < poiResult.getTotalPoiNum(); i++) {
            PoiInfo poiInfo = poiResult.getAllPoi().get(i);
            OverlayOptions overlayOptions = new MarkerOptions().position(poiInfo.location)
                    .icon(mMarkBitmap)
                    .animateType(MarkerOptions.MarkerAnimateType.drop)
                    .title(poiInfo.name);
            mMap.addOverlay(overlayOptions);
        }
    }

    private class MyPoiOverlay extends PoiOverlay {
        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }
        @Override
        public boolean onPoiClick(int index) {
            PoiInfo poiInfo = getPoiResult().getAllPoi().get(index);
            showInfoWindow(poiInfo.name, poiInfo.location);
            return true;
        }
    }

    OnGetRoutePlanResultListener routeListener = new OnGetRoutePlanResultListener(){
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                //result.getSuggestAddrInfo()
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                TransitRouteOverlay overlay = new MyTransitRouteOverlay(mMap);
                mMap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            }
            //同城公交
            Toast.makeText(MainActivity.this, "共有" + result.getRouteLines().size() + "公交线路", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult result) {
            //获取跨城综合公共交通线路规划结果
            Toast.makeText(MainActivity.this, "共有" + result.getRouteLines().size() + "跨城线路", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

        }

        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

        }
    };

    private class MyTransitRouteOverlay extends TransitRouteOverlay {

        /**
         * 构造函数
         *
         * @param baiduMap 该TransitRouteOverlay引用的 BaiduMap 对象
         */
        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.mipmap.icon_marka);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.mipmap.icon_markb);
        }
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
//        int span=1000;
//        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
//        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
//        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }


}
