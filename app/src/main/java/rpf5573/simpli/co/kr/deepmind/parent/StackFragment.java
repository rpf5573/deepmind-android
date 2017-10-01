package rpf5573.simpli.co.kr.deepmind.parent;

import android.view.ViewGroup;
import me.yokeyword.fragmentation.SupportFragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import rpf5573.simpli.co.kr.deepmind.R;

/**
 * Created by mac88 on 2017. 8. 5..
 */

public class StackFragment extends SupportFragment {

  protected Toolbar toolbar;

  protected void initToolbarNav(ViewGroup contianer, int titleId) {
    toolbar = (Toolbar) contianer.findViewById(R.id.toolbar);
    toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_100);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        pop();
      }
    });
    toolbar.setTitle(getResources().getString(titleId));
  }
}
