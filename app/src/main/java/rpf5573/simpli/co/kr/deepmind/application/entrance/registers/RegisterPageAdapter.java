package rpf5573.simpli.co.kr.deepmind.application.entrance.registers;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.orhanobut.logger.Logger;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by mac88 on 2017. 7. 28..
 */

class RegisterPageAdapter extends FragmentStatePagerAdapter {

  /* ------------------------------------------------------------------ */
  //  Property
  /* ------------------------------------------------------------------ */

  //  constant
  /* ------------------------------------ */

  //  view component
  /* ------------------------------------ */

  //  data
  /* ------------------------------------ */
  private int count = 0;
  private SupportFragment[] fragments = new SupportFragment[2];
  private String[] tabTitles = new String[2];

  /* ------------------------------------------------------------------ */
  //  Function
  /* ------------------------------------------------------------------ */

  //  init & life cycle & override
  /* ------------------------------------ */
  RegisterPageAdapter(FragmentManager fm) { super(fm); }
  @Override
  public SupportFragment getItem(int position) {
    Logger.d(position);
    return this.fragments[position];
  }
  @Override
  public int getCount() {
    return this.fragments.length;
  }
  @Override
  public CharSequence getPageTitle(int position) {
    Logger.d(position);
    return this.tabTitles[position];
  }

  //  setup
  /* ------------------------------------ */

  //  interface
  /* ------------------------------------ */

  //  handler
  /* ------------------------------------ */

  //  custom
  /* ------------------------------------ */
  public void addFragment(SupportFragment fm, String tabTitle) {

    fragments[count] = fm;
    tabTitles[count] = tabTitle;
    count++;
  }

}
