package rpf5573.simpli.co.kr.deepmind.helper;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import com.orhanobut.logger.Logger;
import rpf5573.simpli.co.kr.deepmind.R;

/**
 * Created by mac88 on 2017. 7. 26..
 */

public class hLoadingView extends RelativeLayout {
  public static hLoadingView instance = null;

  public static void show(Activity activity) {
    if ( instance == null ) {
      LayoutInflater inflater = (LayoutInflater)activity.getLayoutInflater();
      instance = (hLoadingView)inflater.inflate(R.layout.loading_view, null);
      Logger.d( instance );
      WindowManager.LayoutParams params_wm = new WindowManager.LayoutParams(
          WindowManager.LayoutParams.MATCH_PARENT,
          WindowManager.LayoutParams.MATCH_PARENT
      );
      ViewGroup rv = (ViewGroup) activity.getWindow().getDecorView();
      rv.addView(instance);
    }
    instance.setVisibility(VISIBLE);
  }
  public static void hide() {
    instance.setVisibility(GONE);
  }
  public static boolean isShowing() {

    if ( instance == null || instance.getVisibility() == GONE ) {
      return false;
    } else {
      return true;
    }
  }
  public hLoadingView(Context context) {
    super(context);
  }
  public hLoadingView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }
}

