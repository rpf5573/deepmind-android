package rpf5573.simpli.co.kr.deepmind.application.entrance.login;

import android.Manifest;
import android.Manifest.permission;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintLayout.LayoutParams;
import android.support.design.widget.Snackbar;
import android.support.design.widget.Snackbar.Callback;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.blankj.utilcode.util.Utils;
import com.google.gson.Gson;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import java.security.acl.Permission;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.yokeyword.fragmentation.Fragmentation;
import org.json.JSONException;
import org.json.JSONObject;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks;
import rpf5573.simpli.co.kr.deepmind.BuildConfig;
import rpf5573.simpli.co.kr.deepmind.R;
import rpf5573.simpli.co.kr.deepmind.application.entrance.registers.RegisterActivity;
import rpf5573.simpli.co.kr.deepmind.application.mainFields.MainFieldsActivity;
import rpf5573.simpli.co.kr.deepmind.helper.hAlert;
import rpf5573.simpli.co.kr.deepmind.helper.hColor;
import rpf5573.simpli.co.kr.deepmind.helper.hEnum;
import rpf5573.simpli.co.kr.deepmind.helper.hEnum.activity;
import rpf5573.simpli.co.kr.deepmind.helper.hRequestQueue;
import rpf5573.simpli.co.kr.deepmind.model.mSettings;

/**
 * Created by mac88 on 2017. 7. 18..
 */

public class LogInActivity extends AppCompatActivity implements PermissionCallbacks {

  private static String TAG = "LogInActivity";

  /* ------------------------------------------------------------------ */
  //  Property
  /* ------------------------------------------------------------------ */

  //  constant
  /* ------------------------------------ */

  //  view component
  /* ------------------------------------ */
  EditText loginEditText;
  Button loginCompleteBtn;
  Toolbar toolbar;

  //  data
  /* ------------------------------------ */


  /* ------------------------------------------------------------------ */
  //  Function
  /* ------------------------------------------------------------------ */

  //  init & life cycle & override
  /* ------------------------------------ */
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login_activity);
    setup();
    final String[] perms = {permission.RECORD_AUDIO, permission.WRITE_EXTERNAL_STORAGE, permission.ACCESS_COARSE_LOCATION, permission.CAMERA};
    if ( ! EasyPermissions.hasPermissions(this, perms) ) {
      hAlert.show(this, "원활한 교육 진행을 위해서 권한획득이 필요합니다. <Allow> 버튼을 눌러주세요", new SingleButtonCallback() {
        @Override
        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
          EasyPermissions.requestPermissions(LogInActivity.this, "원활한 교육 진행을 위해서 권한획득이 필요합니다", 3325, perms);
        }
      });
    }
  }
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {

    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
  }
  @Override
  public void onPermissionsGranted(int requestCode, List<String> list) {

  }
  @Override
  public void onPermissionsDenied(int requestCode, List<String> list) {

    new AppSettingsDialog.Builder(this)
        .setNegativeButton("닫기")
        .setPositiveButton("설정")
        .setTitle("요청")
        .setRationale("원활한 교육 진행을 위해서 권한획득이 필요합니다. 설정창에서 권한 획득에 동의해 주세요")
        .build().show();
  }
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
      // Do something after user returned from app settings screen, like showing a Toast.
      Toast.makeText(this, "돌아왔습니다", Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);

    Logger.d(savedInstanceState);
  }

  //  setup
  /* ------------------------------------ */
  private void setup() {
    Logger.addLogAdapter(new AndroidLogAdapter());
    Utils.init(getApplicationContext());
    loginEditText = (EditText) findViewById(R.id.login__passwordEditText);
    loginCompleteBtn = (Button) findViewById(R.id.login__completeBtn);
    setupToolbar();
    setupFragmentation();
    setupCompleteBtn();
    setupIndicatorView();
  }
  public void setupToolbar() {
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.app_name);
  }
  public void setupFragmentation() {
    Fragmentation.builder()
        .stackViewMode(Fragmentation.SHAKE)
        .install();
  }
  public void setupCompleteBtn() {
    loginCompleteBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        loginCompleteBtn.setEnabled(false);
        hideKeyboard();
        final String password = loginEditText.getText().toString();
        if ( password.length() == 0 ) {
          Toast.makeText(getApplicationContext(), "비밀번호를 입력해 주세요", Toast.LENGTH_SHORT).show();
          loginCompleteBtn.setEnabled(true);
        } else {
          sendPasswordToServer(password);
        }
      }
    });
  }
  public void setupIndicatorView() {
    RelativeLayout v = (RelativeLayout) findViewById(R.id.overlay);
  }

  //  listener
  /* ------------------------------------ */

  //  custom
  /* ------------------------------------ */
  private ConstraintLayout makeConstraintLayout() {
    ConstraintLayout cl = new ConstraintLayout(this);
    cl.setId(R.id.login__container);
    return cl;
  }
  private EditText makeLoginEditText() {
    EditText et = new EditText(this);
    et.setHint("Password");
    et.setTextSize(24);
    et.setTextAlignment(EditText.TEXT_ALIGNMENT_CENTER);
    et.setId(R.id.login__passwordEditText);
    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
        LayoutParams.MATCH_PARENT,
        LayoutParams.WRAP_CONTENT
    );
    et.setLayoutParams(lp);
    return et;
  }
  private Button makeLoginButton() {
    Button btn = new Button(this);
    btn.setTextSize(24);
    btn.setText("완료");
    btn.setId(R.id.login__completeBtn);
    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
        LayoutParams.MATCH_PARENT,
        LayoutParams.WRAP_CONTENT
    );
    btn.setLayoutParams(lp);
    return btn;
  }
  private LinearLayout makeVerticalStackView() {
    LinearLayout ll = new LinearLayout(this);
    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    );
    ll.setLayoutParams(params);
    ll.setOrientation(LinearLayout.VERTICAL);
    ll.setId(R.id.login__verticalStackView);
    return ll;
  }
  public void handleFetchLoginResultMessage(String response) {
    Logger.d(response);
    Gson gson = new Gson();
    JSONObject jsonObj = null;
    try {
      jsonObj = new JSONObject( response );
      int responseCode = jsonObj.getInt("response_code");

      Logger.d(responseCode);

      if ( responseCode == 201 ) {
        mSettings.instance = gson.fromJson(jsonObj.getString("value"), mSettings.class);
        if ( mSettings.instance.options.player_list ) {
          goToActivity(activity.register);
        } else {
          goToActivity(activity.mainFields);
        }
      } else if ( responseCode == 202 ) {
        mSettings.instance = gson.fromJson(jsonObj.getString("value"), mSettings.class);
        goToActivity(activity.mainFields);
      } else {
        Toast.makeText(getApplicationContext(), jsonObj.getString("error_message"), Toast.LENGTH_SHORT).show();
        loginCompleteBtn.setEnabled(true);
      }
      loginCompleteBtn.setEnabled(true);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
  public void sendPasswordToServer(final String password) {
    String url = hRequestQueue.BASE_URL+"/login.php";
    hRequestQueue.getInstance(getApplicationContext())
        .add(new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                handleFetchLoginResultMessage(response);
              }
            }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError e) {
            e.printStackTrace();
          }
        }) {
          @Override
          public Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();
            params.put("password", password);
            return params;
          }
        });
  }
  private void goToActivity(hEnum.activity activity) {
    Intent next = new Intent(this , RegisterActivity.class);
    switch (activity) {
      case register:
        break;
      case mainFields:
        next = new Intent(this , MainFieldsActivity.class);
        break;
    }
    startActivity(next);
  }
  public void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(loginEditText.getWindowToken(), 0);
  }
}