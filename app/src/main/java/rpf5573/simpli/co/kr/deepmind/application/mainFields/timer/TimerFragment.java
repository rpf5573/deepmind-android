package rpf5573.simpli.co.kr.deepmind.application.mainFields.timer;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import me.yokeyword.fragmentation.SupportFragment;
import org.json.JSONException;
import org.json.JSONObject;
import rpf5573.simpli.co.kr.deepmind.R;
import rpf5573.simpli.co.kr.deepmind.helper.hAlert;
import rpf5573.simpli.co.kr.deepmind.helper.hCallBack;
import rpf5573.simpli.co.kr.deepmind.helper.hColor;
import rpf5573.simpli.co.kr.deepmind.helper.hRequestQueue;
import rpf5573.simpli.co.kr.deepmind.model.mSettings;

/**
 * Created by mac88 on 2017. 8. 10..
 */

public class TimerFragment extends SupportFragment {
  /* ------------------------------------------------------------------ */
  //  Property
  /* ------------------------------------------------------------------ */

  //  constant
  /* ------------------------------------ */

  //  view component
  /* ------------------------------------ */
  ViewGroup root;
  TextView minTV;
  TextView secTV;
  TextView miliTV;

  //  data
  /* ------------------------------------ */
  CountDownTimer countDownTimer;
  CountUpTimer countUpTimer;

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
    root = (ViewGroup) inflater.inflate(R.layout.timer_fragment, container, false);
    setup();
    return root;
  }

  @Override
  public void onSupportVisible() {
    super.onSupportVisible();
    Logger.d("called");
    if ( countDownTimer == null && countUpTimer == null ) {
      getTime(new hCallBack() {
        @Override
        public void call(String json) {
          Logger.d(json);
          JSONObject jsonObj = null;
          try {
            jsonObj = new JSONObject(json);
            int responseCode = jsonObj.getInt("response_code");
            if (responseCode == 201) {
              String json2 = jsonObj.getString("value");
              JSONObject jsonObject2 = new JSONObject(json2);
              int time = jsonObject2.getInt("time") * 1000;
              go(time);
            } else {
              stop();
              hAlert.show(getActivity(), jsonObj.getString("error_message"));
            }
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      });
    }
  }

  @Override
  public void onSupportInvisible() {
    Logger.d("called");
    super.onSupportInvisible();
    stop();
  }

  //  setup
  /* ------------------------------------ */
  private void setup() {
    setupTimerView();
  }

  private void setupTimerView() {
    minTV = (TextView) root.findViewById(R.id.timer__min);
    secTV = (TextView) root.findViewById(R.id.timer__sec);
    miliTV = (TextView) root.findViewById(R.id.timer__mili);
  }

  //  listener
  /* ------------------------------------ */

  //  custom
  /* ------------------------------------ */
  private void getTime(final hCallBack callBack) {
    String url = hRequestQueue.BASE_URL + "/timer.php";
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
        params.put("get_time", "true");
        params.put("team", ourTeam);
        return params;
      }
    };
    hRequestQueue.getInstance(getActivity()).add(request);
  }

  private void updateColor(@ColorInt int color) {
    minTV.setTextColor(color);
    secTV.setTextColor(color);
    miliTV.setTextColor(color);
  }

  private void go(int t) {
    Logger.d("called");
    Logger.d(t);
    if (t > 0) {
      updateColor(Color.BLUE);
      countDownTimer = new CountDownTimer(t, 62) {
        @Override
        public void onTick(long millisUntilFinished) {
          updateTimer((int) millisUntilFinished); // 남은 시간을 넣는다
        }
        @Override
        public void onFinish() {
          updateColor(Color.RED);
          countUpTimer = new CountUpTimer(0 , 62) {
            @Override
            public void onTick(long elapsedTime) {
              updateTimer((int)elapsedTime);
            }
          };
          countUpTimer.start();
        }
      };
      countDownTimer.start();
    } else {
      updateColor(Color.RED);
      countUpTimer = new CountUpTimer(Math.abs(t), 62) {
        @Override
        public void onTick(long elapsedTime) {
          updateTimer((int)elapsedTime);
        }
      };
      countUpTimer.start();
    }
  }
  private void stop() {
    Logger.d("called");
    minTV.setText("00");
    secTV.setText("00");
    miliTV.setText("00");
    updateColor(Color.BLACK);
    if (countDownTimer != null) {
      countDownTimer.cancel();
      countDownTimer = null;
    }
    if (countUpTimer != null) {
      countUpTimer.stop();
      countUpTimer = null;
    }
  }
  private void updateTimer(int time) {
    int t = time / 1000;
    int min = t / 60;
    int sec = t % 60;
    int mili = (time - t * 1000) / 10;

    minTV.setText((min < 10) ? "0" + min : "" + min);
    secTV.setText((sec < 10) ? "0" + sec : "" + sec);
    miliTV.setText((mili < 10) ? "0" + mili : "" + mili);
  }
}