package rpf5573.simpli.co.kr.deepmind.application.mainFields;

import static android.view.View.GONE;

import android.Manifest;
import android.Manifest.permission;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.LayoutParams;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.blankj.utilcode.util.SizeUtils;
import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.Region;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.estimote.coresdk.service.BeaconManager.BeaconRangingListener;
import com.estimote.coresdk.service.BeaconManager.ServiceReadyCallback;
import com.orhanobut.logger.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.yokeyword.fragmentation.SupportFragment;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks;
import rpf5573.simpli.co.kr.deepmind.R;
import rpf5573.simpli.co.kr.deepmind.application.mainFields.map.MapFragment;
import rpf5573.simpli.co.kr.deepmind.application.mainFields.out.OutFragment;
import rpf5573.simpli.co.kr.deepmind.application.mainFields.point.PointFragment;
import rpf5573.simpli.co.kr.deepmind.application.mainFields.post.PostFragment;
import rpf5573.simpli.co.kr.deepmind.application.mainFields.timer.TimerFragment;
import rpf5573.simpli.co.kr.deepmind.helper.hAlert;
import rpf5573.simpli.co.kr.deepmind.helper.hBottomNavigationViewHelper;
import rpf5573.simpli.co.kr.deepmind.helper.hCallBack;
import rpf5573.simpli.co.kr.deepmind.helper.hColor;
import rpf5573.simpli.co.kr.deepmind.helper.hLoadingView;
import rpf5573.simpli.co.kr.deepmind.helper.hRequestQueue;
import rpf5573.simpli.co.kr.deepmind.helper.hStartBrotherEvent;
import rpf5573.simpli.co.kr.deepmind.model.mBeaconInfo;
import rpf5573.simpli.co.kr.deepmind.model.mSettings;
import rpf5573.simpli.co.kr.deepmind.parent.BaseFragment;

/**
 * Created by mac88 on 2017. 8. 10..
 */

public class ContainerFragment extends BaseFragment implements PermissionCallbacks {
  /* ------------------------------------------------------------------ */
  //  Property
  /* ------------------------------------------------------------------ */

  //  constant
  /* ------------------------------------ */
  static final int MAP = 0;
  static final int POINT = 1;
  static final int OUT = 2;
  static final int POST = 3;
  static final int TIMER = 4;
  static final int IMMEDIATE = 1;
  static final int NEAR = 2;
  static final int FAR = 3;
  static final int REQUESTCODE_LOCATION = 1079;
  static final int REQUESTCODE_BLUETOOTH = 1080;

  //  view component
  /* ------------------------------------ */
  ViewGroup root;
  BottomNavigationView bottomNavigationView;
  SupportFragment[] fragments = new SupportFragment[5];

  //  data
  /* ------------------------------------ */
  BeaconManager beaconManager;
  BeaconRegion region;
  BluetoothAdapter bluetoothAdapter;

  /* ------------------------------------------------------------------ */
  //  Function
  /* ------------------------------------------------------------------ */

  //  init & life cycle & override
  /* ------------------------------------ */
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    Logger.d("called");
    root = (ViewGroup) inflater.inflate(R.layout.mainfields_container_fragment, container, false);
    return root;
  }
  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    fragments[MAP] = new MapFragment();
    fragments[POINT] = new PointFragment();
    if ( mSettings.instance.options.player_list ) {
      fragments[OUT] = new OutFragment();
    }
    fragments[POST] = new PostFragment();
    fragments[TIMER] = new TimerFragment();

    setup();
  }
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }
  @Override
  public void onPermissionsGranted(int requestCode, List<String> perms) {
    hAlert.show(getActivity(), "감사합니다, 다시 비콘을 찾아주세요");
  }
  @Override
  public void onPermissionsDenied(int requestCode, List<String> perms) {
    if ( requestCode == REQUESTCODE_LOCATION ) {
      new AppSettingsDialog.Builder(this)
          .setNegativeButton("닫기")
          .setPositiveButton("설정")
          .setTitle("요청")
          .setRationale("원활한 교육 진행을 위해서 권한획득이 필요합니다. 설정창에서 권한 획득에 동의해 주세요")
          .build().show();
    }
  }
  @Override
  public boolean onBackPressedSupport() {

    if ( hLoadingView.isShowing() ) {
      hAlert.show(getActivity(), "무인 미션 찾기를 중지하겠습니다", new SingleButtonCallback() {
        @Override
        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
          stopBeacon();
          hLoadingView.hide();
        }
      });
      return true;
    }
    return false;
  }
  @Override
  public void onPause() {
    super.onPause();

    if ( beaconManager != null ) {
      stopBeacon();
    }
  }


  //  setup
  /* ------------------------------------ */
  private void setup() {
    if (mSettings.instance.options.player_list ) {
      loadMultipleRootFragment(R.id.mainFields__fragmentContainer, 0, fragments[MAP], fragments[POINT], fragments[OUT], fragments[POST], fragments[TIMER]);
    } else {
      loadMultipleRootFragment(R.id.mainFields__fragmentContainer, 0, fragments[MAP], fragments[POINT], fragments[POST], fragments[TIMER]);
    }
    EventBus.getDefault().register(this);
    setupToolBar();
    setupBottomNavigationView();
    setupBeacon();
  }
  private void setupToolBar() {
    initToolbarNav(root, R.string.app_name);
    if ( mSettings.instance.options.beacon ) {
      //TextView title = (TextView) toolbar.getChildAt(1);
      Button button = new Button(getActivity());
      button.setText("무인 미션 찾기");
      Toolbar.LayoutParams params2 = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
      params2.gravity = Gravity.END;
      button.setLayoutParams(params2);
      button.setTextColor(Color.WHITE);
      // 이건 뭐냐,,, 그냥 바로 resourceId때리면 안되는거냐??
      TypedValue outValue = new TypedValue();
      getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
      button.setBackgroundResource(outValue.resourceId);
      button.setTextSize(SizeUtils.dp2px(5));
      button.setOnClickListener(beaconFindingBtnClickListener);
      toolbar.addView(button);
    }
  }
  private void setupBottomNavigationView() {
    bottomNavigationView = (BottomNavigationView) root.findViewById(R.id.mainFields__bottom_navigation);
    if ( ! mSettings.instance.options.player_list ) {
      bottomNavigationView.getMenu().removeItem(R.id.mainFields__bottomNavigation__out);
    }
    hBottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
    bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
  }
  private void setupBeacon() {
    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    region = new BeaconRegion( "myRegion", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null );
  }

  //  listener
  /* ------------------------------------ */
  BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new OnNavigationItemSelectedListener() {
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
      switch (item.getItemId()) {
        case R.id.mainFields__bottomNavigation__map:
          showHideFragment(fragments[MAP]);
          break;
        case R.id.mainFields__bottomNavigation__point:
          showHideFragment(fragments[POINT]);
          break;
        case R.id.mainFields__bottomNavigation__out:
          showHideFragment(fragments[OUT]);
          break;
        case R.id.mainFields__bottomNavigation__post:
          showHideFragment(fragments[POST]);
          break;
        case R.id.mainFields__bottomNavigation__timer:
          showHideFragment(fragments[TIMER]);
          break;
      }
      return true;
    }
  };
  OnClickListener beaconFindingBtnClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {


      // 블루투스 체크
      if (bluetoothAdapter == null) {
        hAlert.show(getActivity(), "이 기기는 블루투스를 지원하지 않습니다");
      } else {
        if (!bluetoothAdapter.isEnabled()) {
          bluetoothEnable();
        } else {
          // 위치정보 체크
          String[] perms = {permission.ACCESS_COARSE_LOCATION};
          if (!EasyPermissions.hasPermissions(getActivity(), perms)) {
            EasyPermissions
                .requestPermissions(getActivity(), "위치정보 접근을 허용해주세요", REQUESTCODE_LOCATION, perms);
          } else {
            hLoadingView.show(getActivity());
            getCurrentPostFromServer(new hCallBack() {
              @Override
              public void call(String json) {
                Logger.d(json);
                JSONObject jsonObj = null;
                try {
                  jsonObj = new JSONObject(json);
                  int responseCode = jsonObj.getInt("response_code");
                  if (responseCode == 201) {
                    int currentPost = jsonObj.getInt("value");
                    findBeacon(currentPost, new hCallBack() {
                      @Override
                      public void call(String json) {
                        hLoadingView.hide();
                        final String url = json;
                        Logger.d(url);
                        stopBeacon();
                        hLoadingView.hide();
                        openBrowser(url);
                      }
                    });
                  } else {
                    hLoadingView.hide();
                    hAlert.show(getActivity(), jsonObj.getString("error_message"));
                  }
                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            });
          }
        }
      }
    }
  };
  @Subscribe
  public void startBrother(hStartBrotherEvent event) {
    start(event.targetFragment);
  }

  //  custom
  /* ------------------------------------ */
  public static ContainerFragment newInstance() {
    Bundle args = new Bundle();
    return new ContainerFragment();
  }
  public void settingBeacon() {

  }
  public void findBeacon( final int post, final hCallBack callBack ) {
    if (SystemRequirementsChecker.checkWithDefaultDialogs(getActivity())) {
      this.beaconManager = new BeaconManager(getActivity());
      beaconManager.setRangingListener(new BeaconRangingListener() {
        @Override
        public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<Beacon> list) {
          if ( ! list.isEmpty() ) {
            Beacon nearestBeacon = list.get(0);
            final int minor = nearestBeacon.getMinor();
            Logger.d(minor);
            if ( minor > 200 ) {
              final int txPower = nearestBeacon.getMeasuredPower();
              final int rssi = nearestBeacon.getRssi();
              final float distance = (float) calculateDistance(txPower, rssi);
              final int proximity = getProximity(distance);
              Logger.d("proximity ==> "+proximity);
              if ( proximity == IMMEDIATE ) {
                if ( minor == (post+200) ) {

                  final String url = mSettings.instance.beacon_infos.get(post - 1).url;
                  Logger.d(url);

                  callBack.call(url);

                }
              }
            }
          }
        }
      });
      beaconManager.connect(new ServiceReadyCallback() {
        @Override
        public void onServiceReady() {
          beaconManager.startRanging(region);
        }
      });
    }
  }
  private void getCurrentPostFromServer(final hCallBack callBack) {
    String url = hRequestQueue.BASE_URL + "/post.php";
    StringRequest request = new StringRequest(Request.Method.POST, url, new Listener<String>() {
      @Override
      public void onResponse(String response) {
        Logger.d(response);
        callBack.call(response);
      }
    }, new ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Logger.e(error.getMessage());
      }
    }) {
      @Override
      public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        String ourTeam = mSettings.instance.our_team + "";
        String totalTeamCount = mSettings.instance.total_team_count + "";
        params.put("team", ourTeam);
        params.put("get_current_post", "true");
        return params;
      }
    };
    hRequestQueue.getInstance(getActivity()).add(request);
  }
  public double calculateDistance(int txPower, double rssi) {
    if (rssi == 0) {
      return -1.0; // if we cannot determine distance, return -1.
    }
    double ratio = rssi*1.0/txPower;
    if (ratio < 1.0) {
      return Math.pow(ratio,10);
    }
    else {
      double accuracy =  (0.9)*Math.pow(ratio,7.7) + 0.1;
      return accuracy;
    }
  }
  public int getProximity( float distance ) {
    if ( distance <= 10 ) { return 1; }
    if ( distance <= 15 ) { return 2; }
    if ( distance <= 20 ) { return 3; }
    return 4;
  }
  private void stopBeacon() {
    beaconManager.stopRanging(this.region);
    beaconManager.disconnect();
  }
  public void bluetoothEnable(){
    Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    startActivityForResult(bluetoothIntent, REQUESTCODE_BLUETOOTH);
  }
  public void openBrowser(final String url) {
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        WebView webView = new WebView(getActivity());
        webView.loadUrl(url);
      }
    });
  }
}
