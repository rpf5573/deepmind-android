package rpf5573.simpli.co.kr.deepmind.application.entrance.registers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import com.orhanobut.logger.Logger;
import me.yokeyword.fragmentation.SupportActivity;
import org.greenrobot.eventbus.EventBus;
import rpf5573.simpli.co.kr.deepmind.R;

/**
 * Created by mac88 on 2017. 7. 24..
 */

public class RegisterActivity extends SupportActivity {

  /* ------------------------------------------------------------------ */
  //  Property
  /* ------------------------------------------------------------------ */

  //  view component
  /* ------------------------------------ */
  private View focusThief;
  private ViewPager viewPager;
  private TabLayout tabLayout;

  //  data
  /* ------------------------------------ */
  ContainerFragment containerFragment;


  /* ------------------------------------------------------------------ */
  //  Function
  /* ------------------------------------------------------------------ */

  // override & init & life cycle
  /* ------------------------------------ */
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setup();
  }


  //  setup
  /* ------------------------------------ */
  private void setup() {

    containerFragment = ContainerFragment.newInstance();
    setContentView(R.layout.register_activty);
    loadRootFragment(R.id.register__mainContainer, containerFragment);
  }

  @Override
  public void onBackPressedSupport() {
    super.onBackPressedSupport();

  }

  //  listener
  /* ------------------------------------ */

  //  custom
  /* ------------------------------------ */

}
