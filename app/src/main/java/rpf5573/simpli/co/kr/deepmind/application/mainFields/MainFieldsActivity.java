package rpf5573.simpli.co.kr.deepmind.application.mainFields;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.orhanobut.logger.Logger;
import java.lang.reflect.Method;
import me.yokeyword.fragmentation.SupportActivity;
import net.gotev.uploadservice.UploadService;
import rpf5573.simpli.co.kr.deepmind.BuildConfig;
import rpf5573.simpli.co.kr.deepmind.R;
import rpf5573.simpli.co.kr.deepmind.helper.hBottomNavigationViewHelper;

/**
 * Created by mac88 on 2017. 8. 9..
 */

public class MainFieldsActivity extends SupportActivity {

  /* ------------------------------------------------------------------ */
  //  Property
  /* ------------------------------------------------------------------ */

  //  constant
  /* ------------------------------------ */

  //  view component
  /* ------------------------------------ */

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
    setContentView(R.layout.mainfields_activity);
    Logger.d("called");
    loadRootFragment(R.id.mainFields__mainContainer, ContainerFragment.newInstance());
    setup();
  }

  //  setup
  /* ------------------------------------ */
  private void setup() {
    setupUploadService();
    setupFileUriExposure();
  }
  private void setupUploadService() {
    UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
  }
  private void setupFileUriExposure() {
    if(Build.VERSION.SDK_INT>=24){
      try{
        Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
        m.invoke(null);
      }catch(Exception e){
        e.printStackTrace();
      }
    }
  }

  //  listener
  /* ------------------------------------ */
  @Override
  public void onBackPressedSupport() {
    new MaterialDialog.Builder(this)
        .title("앱을 종료하시겠습니까?")
        .positiveText("Yes")
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override
          public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            finishAffinity();
          }
        })
        .negativeText("No")
        .negativeColor(Color.RED)
        .show();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }
  //  custom
  /* ------------------------------------ */

}