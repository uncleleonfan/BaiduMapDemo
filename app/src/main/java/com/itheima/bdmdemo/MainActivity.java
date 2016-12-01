package com.itheima.bdmdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.ArrayList;
import java.util.List;

import static android.util.Log.d;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private MapView mMapView;
    private BaiduMap mMap;
    private LatLng mLatLng = new LatLng(22.581981, 113.929588);
    private BitmapDescriptor mMarkBitmap;

    private boolean showMarker = false;
    private boolean showCircle = false;
    private boolean showPoly = false;

    private PoiSearch mPoiSearch;

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
        //初始化位置
        translate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
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

        }
        return true;
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
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.infowindow, null);
            TextView viewById = (TextView) view.findViewById(R.id.infowindo_title);
            viewById.setText(marker.getTitle());
            InfoWindow infoWindow = new InfoWindow(view, marker.getPosition(), -70);
            mMap.showInfoWindow(infoWindow);
            return true;
        }
    };
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
            markPoiResult(poiResult);
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
}
