package rpf5573.simpli.co.kr.deepmind.parent;

import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import com.orhanobut.logger.Logger;
import me.yokeyword.fragmentation.SupportFragment;
import rpf5573.simpli.co.kr.deepmind.R;
import android.view.View;

/**
 * Created by mac88 on 2017. 8. 5..
 */

public class BaseFragment extends SupportFragment {
  protected Toolbar toolbar;
  protected void initToolbarNav(ViewGroup container, int titleId) {
    toolbar = (Toolbar) container.findViewById(R.id.toolbar);
    toolbar.setTitle(getResources().getString(titleId));
  }
}
