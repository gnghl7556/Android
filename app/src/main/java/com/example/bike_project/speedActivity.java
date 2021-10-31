package com.example.bike_project;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import android.widget.RelativeLayout;
import android.widget.Toast;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shashank.sony.fancytoastlib.FancyToast;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
public class speedActivity extends Activity implements
        MapView.MapViewEventListener,
        MapView.POIItemEventListener,
        MapView.CurrentLocationEventListener {

    private LocationListener locationListener;
    private EditText spEditext;
    private EditText epEditext;
    private Button findbutton;
    private Button findStartLocation;
    private Button mSearchbymap;
    private MapView mapView;
    private FloatingActionButton myposition;
    public LocationManager locationManager;
    private boolean mapsSelection = false;
    double latitude, longitude;
    RecyclerView recyclerView,recyclerView2;
    ArrayList<Document> documentArrayList = new ArrayList<>(); //지역명 검색 결과 리스트
    RelativeLayout mLoaderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speed_layout2);


        final Geocoder geocoder = new Geocoder(this); //카카오맵
        spEditext = (EditText) findViewById(R.id.etOrigin);
        epEditext = (EditText) findViewById(R.id.etDestination);
        findbutton = (Button) findViewById(R.id.btnFindPath);
        findStartLocation = (Button) findViewById(R.id.myFindPath);
        mSearchbymap = (Button) findViewById(R.id.myMapPath);
        myposition = (FloatingActionButton) findViewById(R.id.mypositions);
        mapView = (MapView) findViewById(R.id.map_view);

        mapView.setCurrentLocationEventListener(this);
        mapView.setHDMapTileEnabled(true); // 고해상도 지도 타일 사용
        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);

        recyclerView = findViewById(R.id.map_recyclerview1);
        recyclerView2 = findViewById(R.id.map_recyclerview2);
        mLoaderLayout = findViewById(R.id.loaderLayout);

        LocationAdapter locationAdapter = new LocationAdapter(documentArrayList, getApplicationContext(), spEditext, recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false); //레이아웃매니저 생성
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL)); //아래구분선 세팅
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(locationAdapter);
        LocationAdapter locationAdapter2 = new LocationAdapter(documentArrayList, getApplicationContext(), epEditext, recyclerView2);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false); //레이아웃매니저 생성
        recyclerView2.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL)); //아래구분선 세팅
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView2.setAdapter(locationAdapter2);

        //final String address = getIntent().getStringExtra("bluetooth_address");
        findbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sped1 = spEditext.getText().toString();
                String eped1 = epEditext.getText().toString();
                if (sped1 == null || sped1.length() == 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.input_start), Toast.LENGTH_SHORT).show();
                    return;
                } else if (eped1 == null || eped1.length() == 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.input_end), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    List<Address> list = null;
                    List<Address> list1 = null;
                    try {
                        list = geocoder.getFromLocationName(sped1, 10);
                        list1 = geocoder.getFromLocationName(eped1, 10);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (list != null && list1 != null) {
                        if (list.size() == 0 || list1.size() == 0) {
                            Toast.makeText(getApplicationContext(), getString(R.string.error_route), Toast.LENGTH_SHORT).show();
                        } else {
                            Address addr = list.get(0);
                            Address addr1 = list1.get(0);
                            double splat = addr.getLatitude();
                            double splon = addr.getLongitude();
                            double edlat = addr1.getLatitude();
                            double edlon = addr1.getLongitude();
                            try {
                                String navi = "daummaps://route?sp=" + splat + "," + splon + "&ep=" + edlat + "," + edlon + "&by=BICYCLE";
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(navi));
                                startActivity(intent);

                            } catch (Exception e) {
                                String navi = "https://play.google.com/store/apps/details?id=net.daum.android.map";
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(navi));
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), getString(R.string.navi_install), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }

            }
        });
        //현재 위치를 출발지로
        findStartLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    spEditext.setText("");

                    if (ContextCompat.checkSelfPermission(speedActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED &&
                            ContextCompat.checkSelfPermission(speedActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED) {
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                        locationListener = new LocationListener() {
                            @Override
                            public void onLocationChanged(android.location.Location location) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {
                            }

                            @Override
                            public void onProviderEnabled(String provider) {
                            }

                            @Override
                            public void onProviderDisabled(String provider) {

                            }
                        };

                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
                        String locationProvider = LocationManager.NETWORK_PROVIDER;
                        latitude = locationManager.getLastKnownLocation(locationProvider).getLatitude();
                        longitude = locationManager.getLastKnownLocation(locationProvider).getLongitude();

                        List<Address> list = null;
                        list = geocoder.getFromLocation(latitude, longitude, 10);
                        if (list != null) {
                            if (list.size() == 0)
                                Toast.makeText(getApplicationContext(), getString(R.string.error_route), Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getApplicationContext(), getString(R.string.set_current_loc), Toast.LENGTH_SHORT).show();
                            spEditext.setText(list.get(0).getAddressLine(0));
                            recyclerView.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.toString() + getString(R.string.error_route), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //지도에서 도착지 선택
        mSearchbymap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mapsSelection) {
                    mapsSelection = true;
                    mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
                    mapView.setShowCurrentLocationMarker(false);
                    Toast.makeText(getApplicationContext(), getString(R.string.set_map_loc), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.exit_map_loc), Toast.LENGTH_SHORT).show();
                    mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                    mapsSelection = false;
                }
            }
        });


        // editText 검색 텍스처이벤트
        spEditext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 입력하기 전에

                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() >= 1) {
                    // if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {

                    documentArrayList.clear();
                    locationAdapter.clear();
                    locationAdapter.notifyDataSetChanged();
                    ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<CategoryResult> call = apiInterface.getSearchLocation(getString(R.string.restapi_key), charSequence.toString(), 15);
                    call.enqueue(new Callback<CategoryResult>() {
                        @Override
                        public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                for (Document document : response.body().getDocuments()) {
                                    locationAdapter.addItem(document);
                                }
                                locationAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {

                        }
                    });
                    //}
                    //mLastClickTime = SystemClock.elapsedRealtime();
                } else {
                    if (charSequence.length() <= 0) {
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 입력이 끝났을 때
            }
        });

        spEditext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                } else {
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
        spEditext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FancyToast.makeText(getApplicationContext(), "검색리스트에서 장소를 선택해주세요", FancyToast.LENGTH_SHORT, FancyToast.INFO, true).show();
            }
        });

        // editText 검색 텍스처이벤트
        epEditext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 입력하기 전에
                recyclerView2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() >= 1) {
                    // if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {

                    documentArrayList.clear();
                    locationAdapter2.clear();
                    locationAdapter2.notifyDataSetChanged();
                    ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<CategoryResult> call = apiInterface.getSearchLocation(getString(R.string.restapi_key), charSequence.toString(), 15);
                    call.enqueue(new Callback<CategoryResult>() {
                        @Override
                        public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                for (Document document : response.body().getDocuments()) {
                                    locationAdapter2.addItem(document);
                                }
                                locationAdapter2.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {

                        }
                    });
                    //}
                    //mLastClickTime = SystemClock.elapsedRealtime();
                } else {
                    if (charSequence.length() <= 0) {
                        recyclerView2.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 입력이 끝났을 때
            }
        });

        epEditext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                } else {
                    recyclerView2.setVisibility(View.GONE);
                }
            }
        });
        epEditext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FancyToast.makeText(getApplicationContext(), "검색리스트에서 장소를 선택해주세요", FancyToast.LENGTH_SHORT, FancyToast.INFO, true).show();
            }
        });


        myposition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
            }
        });
    }



    @Override
    public void onMapViewInitialized(MapView mapView) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        if (mapsSelection) {
            Toast.makeText(getApplication(), getString(R.string.long_press), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
        if (mapsSelection) {
            Toast.makeText(getApplication(), getString(R.string.long_press), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
        Geocoder geocoder = new Geocoder(this);
        if (mapsSelection) {
            try {
                MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
                List<Address> list = null;
                list = geocoder.getFromLocation(mapPointGeo.latitude, mapPointGeo.longitude, 10);
                if (list != null) {
                    if (list.size() == 0)
                        Toast.makeText(getApplicationContext(), getString(R.string.error_address), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getApplicationContext(), getString(R.string.set_map_loc_ok), Toast.LENGTH_LONG).show();
                    epEditext.setText(list.get(0).getAddressLine(0));
                    recyclerView2.setVisibility(View.GONE);
                    mapsSelection = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.toString() + getString(R.string.error_default), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude, mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude), 2, true);
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
        mapView.setShowCurrentLocationMarker(false);
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {
    }
}
