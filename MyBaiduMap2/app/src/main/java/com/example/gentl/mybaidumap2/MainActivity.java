package com.example.gentl.mybaidumap2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.*;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

public class MainActivity extends Activity {
    // 百度地图控件
    private TextureMapView mMapView = null;
    // 百度地图对象
    private BaiduMap bdMap;

    private Context context;
    //定位相关
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
    private boolean isFirstIn=true;
    private double mLatitude;
    private double mLongtitude;
    //自定义定位图标
    private BitmapDescriptor mIconLocation;
    private MyOrientationListener myOrientationListener;
    private float mCurrentX;
    private MyLocationConfiguration.LocationMode mLocationMode;


    //覆盖物相关
    private BitmapDescriptor mMarker;
    private RelativeLayout mMarkerLy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        this.context=this;
        init();//init view
        initLocation();
        initMarker();

        bdMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle extraInfo=marker.getExtraInfo();
                Info info=(Info) extraInfo.getSerializable("info");
                ImageView iv=(ImageView) mMarkerLy.findViewById(R.id.id_info_img);
                TextView distance=(TextView) mMarkerLy.findViewById(R.id.id_info_distance);
                TextView name=(TextView) mMarkerLy.findViewById(R.id.id_info_name);
                TextView zan=(TextView) mMarkerLy.findViewById(R.id.id_info_zan);
                iv.setImageResource(info.getImgId());
                distance.setText(info.getDistance());
                name.setText(info.getName());
                zan.setText(info.getZan()+"");

                InfoWindow infoWindow;
                TextView tv=new TextView(context);
                tv.setBackgroundResource(R.drawable.location_tips);
                tv.setPadding(30,20,30,50);
                tv.setText(info.getName());
                tv.setTextColor(Color.parseColor("#ffffff"));

                final LatLng latLng=marker.getPosition();
                Point p=bdMap.getProjection().toScreenLocation(latLng);
                p.y-=47;
                LatLng ll=bdMap.getProjection().fromScreenLocation(p);
                BitmapDescriptor tvBD = BitmapDescriptorFactory.fromView(tv);

                infoWindow=new InfoWindow(tvBD, ll, 0, new InfoWindow.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick() {
                    // TODO Auto-generated method stub
                        bdMap.hideInfoWindow();
                    }
                });
                bdMap.showInfoWindow(infoWindow);
                mMarkerLy.setVisibility(View.VISIBLE);
                return true;
            }
        });
        bdMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMarkerLy.setVisibility(View.GONE);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    private void initMarker(){
        mMarker=BitmapDescriptorFactory.fromResource(R.drawable.maker);
        mMarkerLy=(RelativeLayout) findViewById(R.id.id_maker_ly);
    }

    private void initLocation(){
        mLocationMode= MyLocationConfiguration.LocationMode.NORMAL;
        mLocationClient=new LocationClient(this);
        mLocationListener=new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);

        LocationClientOption option=new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);
        //初始化图标
        mIconLocation=BitmapDescriptorFactory
                .fromResource(R.drawable.arrow);
        myOrientationListener=new MyOrientationListener(context);
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX=x;
            }
        });
    }

    /**
     * 初始化方法
     */
    private void init() {

        mMapView = (TextureMapView) findViewById(R.id.bmapview);
        bdMap=mMapView.getMap();
        MapStatusUpdate msu= MapStatusUpdateFactory.zoomTo(15.0f);
        bdMap.setMapStatus(msu);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    //开始定位
    @Override
    protected void onStart() {
        super.onStart();
        //开启定位
        bdMap.setMyLocationEnabled(true);
        if(!mLocationClient.isStarted())
        mLocationClient.start();
        //开启方向传感器
        myOrientationListener.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //停止定位
        bdMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        //停止方向传感器
        myOrientationListener.stop();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

  @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.id_map_common:
                bdMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.id_map_site:
                bdMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.id_map_traffic:
                if(bdMap.isTrafficEnabled()){
                    bdMap.setTrafficEnabled(false);
                    item.setTitle("实时交通（）");
                }
                else{
                    bdMap.setTrafficEnabled(true);
                    item.setTitle("实时交通（on）");
                }
                break;
            case R.id.id_map_location:
                centerToMyLocation(mLatitude, mLongtitude);
                break;
            case R.id.id_map_mode_common:
                mLocationMode= MyLocationConfiguration.LocationMode.NORMAL;
                break;
            case R.id.id_map_mode_following:
                mLocationMode= MyLocationConfiguration.LocationMode.FOLLOWING;
                break;
            case R.id.id_map_mode_compass:
                mLocationMode= MyLocationConfiguration.LocationMode.COMPASS;
                break;
            case R.id.id_add_overlay:
                addOverlays(Info.infos);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
  }

    /**
     * 添加覆盖物
     */
    private void addOverlays(List<Info> infos){
        bdMap.clear();
        LatLng latLng=null;
        Marker marker=null;
        OverlayOptions options;
        for(Info info:infos){
            //设置经纬度
            latLng=new LatLng(info.getLatitude(),info.getLongtitude());
            //图标
            options=new MarkerOptions().position(latLng).icon(mMarker).zIndex(5);
            marker=(Marker) bdMap.addOverlay(options);
            Bundle arg0=new Bundle();
            arg0.putSerializable("info",info);
            marker.setExtraInfo(arg0);
        }
        MapStatusUpdate msu =MapStatusUpdateFactory.newLatLng(latLng);
        bdMap.setMapStatus(msu);
    }

    /**
     * 定位到我的位置
     * @param mLatitude
     * @param mLongtitude
     */
    private void centerToMyLocation(double mLatitude, double mLongtitude) {
        LatLng latlng = new LatLng(mLatitude, mLongtitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latlng);
        bdMap.animateMapStatus(msu);
    }

    private class MyLocationListener implements BDLocationListener{
        @Override
      public void onReceiveLocation(BDLocation location){
            MyLocationData data=new MyLocationData.Builder()
                    .direction(mCurrentX)
                    .accuracy(location.getRadius())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            bdMap.setMyLocationData(data);
           //设置自定义图标
            MyLocationConfiguration config=new MyLocationConfiguration(
                    mLocationMode,true,mIconLocation);
            bdMap.setMyLocationConfiguration(config);
            //定义坐标
            mLatitude=location.getLatitude();
            mLongtitude=location.getLongitude();
            if(isFirstIn){
                centerToMyLocation(location.getLatitude(), location.getLongitude());
                isFirstIn=false;

                Toast.makeText(context,location.getAddrStr(),
                        Toast.LENGTH_SHORT).show();
            }
        }

      @Override
      public void onConnectHotSpotMessage(String s, int i) {

      }
  }
}
