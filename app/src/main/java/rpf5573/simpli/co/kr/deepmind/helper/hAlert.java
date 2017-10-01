package rpf5573.simpli.co.kr.deepmind.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.orhanobut.logger.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import rpf5573.simpli.co.kr.deepmind.R;

/**
 * Created by mac88 on 2017. 8. 28..
 */

public class hAlert {

  public static void show(Context context, String message) {
     MaterialDialog.Builder builder = new Builder(context)
         .title("알림")
         .content(message)
         .positiveColor(hColor.getColor(context, R.color.md_blue_400))
         .positiveText("확인");
     builder.show();
  }
  public static void show(Context context, String message, SingleButtonCallback callback) {
    MaterialDialog.Builder builder = new Builder(context)
        .title("알림")
        .content(message)
        .positiveColor(hColor.getColor(context, R.color.md_blue_400))
        .onPositive(callback)
        .positiveText("확인");
    builder.show();
  }
  public static void approval(Context context, String message, SingleButtonCallback positiveCB, SingleButtonCallback negativeCB) {
    MaterialDialog.Builder builder = new Builder(context)
        .title("확인")
        .content(message)
        .positiveText("예")
        .positiveColor(hColor.getColor(context, R.color.md_blue_400))
        .negativeText("아니오")
        .negativeColor(hColor.getColor(context, R.color.md_red_400));
    if (positiveCB != null) {
      builder.onPositive(positiveCB);
    }
    if (negativeCB != null) {
      builder.onNegative(negativeCB);
    }
    builder.show();
  }
}
