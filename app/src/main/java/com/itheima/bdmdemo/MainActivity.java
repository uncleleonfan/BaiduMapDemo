package com.itheima.bdmdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

import static android.util.Log.d;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private MapView mMapView;
    private BaiduMap mMap;
    private LatLng mLatLng = new LatLng(22.581981, 113.929588);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMap = mMapView.getMap();
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
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomIn();
                mMap.animateMapStatus(mapStatusUpdate);//动画方式改变地图状态
//                mMap.setMapStatus(mapStatusUpdate);//改变地图状态
                mMap.setOnMapStatusChangeListener(mOnMapStatusChangeListener);
                break;
            case R.id.zoom_out:
                mMap.animateMapStatus(MapStatusUpdateFactory.zoomOut());
                mMap.setOnMapStatusChangeListener(mOnMapStatusChangeListener);
                break;
            case R.id.rotate:
                MapStatus mapStatus = mMap.getMapStatus();
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.rotate(mapStatus.rotate + 90);//逆时针旋转
                MapStatusUpdate rotateUpdate = MapStatusUpdateFactory.newMapStatus(builder.build());
                mMap.animateMapStatus(rotateUpdate);
                break;
            case R.id.translate:
//                mMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(mLatLng));
//                mMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(18));
                MapStatus.Builder translateBuilder = new MapStatus.Builder();
                translateBuilder.target(mLatLng).zoom(18);
                mMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(translateBuilder.build()));
                break;
        }
        return true;
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
}
