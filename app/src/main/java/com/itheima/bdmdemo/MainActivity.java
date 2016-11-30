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

import static android.util.Log.d;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private MapView mMapView;
    private BaiduMap mMap;

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
