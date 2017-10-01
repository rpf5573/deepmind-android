package rpf5573.simpli.co.kr.deepmind.application.mainFields.point;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.yokeyword.fragmentation.SupportFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rpf5573.simpli.co.kr.deepmind.R;
import rpf5573.simpli.co.kr.deepmind.helper.hCallBack;
import rpf5573.simpli.co.kr.deepmind.helper.hEnum.activity;
import rpf5573.simpli.co.kr.deepmind.helper.hRequestQueue;
import rpf5573.simpli.co.kr.deepmind.model.mMessage;
import rpf5573.simpli.co.kr.deepmind.model.mSettings;

/**
 * Created by mac88 on 2017. 8. 10..
 */

public class PointFragment extends SupportFragment {
  /* ------------------------------------------------------------------ */
  //  Property
  /* ------------------------------------------------------------------ */

  //  constant
  /* ------------------------------------ */

  //  view component
  /* ------------------------------------ */
  ViewGroup root;
  PieChart chart;

  //  data
  /* ------------------------------------ */
  int[] colors;


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
    root = (ViewGroup) inflater.inflate(R.layout.point_fragment, container, false);

    setup();
    return root;
  }

  @Override
  public void onSupportVisible() {
    super.onSupportVisible();

    getPointsFromServer(new hCallBack() {
      @Override
      public void call(String json) {
        Logger.d(json);
        JSONObject jsonObj = null;
        try {
          jsonObj = new JSONObject(json);
          int responseCode = jsonObj.getInt("response_code");
          if (responseCode == 201) {
            JSONObject value = jsonObj.getJSONObject("value");
            JSONArray arr = value.getJSONArray("usable_points");
            int[] points = new int[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
              points[i] = arr.getInt(i);
            }
            Logger.d(points);
            drawChart(points, value.getInt("rank_point"), getActivity());
          } else {
            Logger.e("ERROR");
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    });

  }

  //  setup
  /* ------------------------------------ */
  private void setup() {
    setupColor();
    setupChart();
  }

  private void setupColor() {
    Context context = getActivity();
    colors = new int[15];
    colors[0] = ContextCompat.getColor(context, R.color.team_1);
    colors[1] = ContextCompat.getColor(context, R.color.team_2);
    colors[2] = ContextCompat.getColor(context, R.color.team_3);
    colors[3] = ContextCompat.getColor(context, R.color.team_4);
    colors[4] = ContextCompat.getColor(context, R.color.team_5);
    colors[5] = ContextCompat.getColor(context, R.color.team_6);
    colors[6] = ContextCompat.getColor(context, R.color.team_7);
    colors[7] = ContextCompat.getColor(context, R.color.team_8);
    colors[8] = ContextCompat.getColor(context, R.color.team_9);
    colors[9] = ContextCompat.getColor(context, R.color.team_10);
    colors[10] = ContextCompat.getColor(context, R.color.team_11);
    colors[11] = ContextCompat.getColor(context, R.color.team_12);
    colors[12] = ContextCompat.getColor(context, R.color.team_13);
    colors[13] = ContextCompat.getColor(context, R.color.team_14);
    colors[14] = ContextCompat.getColor(context, R.color.team_15);
  }

  private void setupChart() {
    chart = (PieChart) root.findViewById(R.id.pieChart);
    //PercentValue를 안쓸거야.
    chart.setUsePercentValues(false);
    chart.setExtraOffsets(5, 10, 5, 5);
    chart.setDrawCenterText(true);
    chart.setDescription(new Description());

    //오른쪽 레전드 설정
    Description description = new Description();
    description.setText("");
    chart.setDescription(description);
    chart.getLegend().setEnabled(false);

    // entry label styling
    chart.setEntryLabelColor(Color.WHITE);
    chart.setEntryLabelTypeface(Typeface.DEFAULT_BOLD);
    chart.setEntryLabelTextSize(12f);
  }

  //  listener
  /* ------------------------------------ */

  //  custom
  /* ------------------------------------ */
  private void drawChart(int[] points, int ourRankPoint, Context context) {
    List<PieEntry> entries = new ArrayList<>();
    int size = points.length;
    int i = 0;
    for (i = 0; i < size; i++) {
      entries.add(new PieEntry(points[i], (i + 1) + "조"));
    }

    PieDataSet dataSet = new PieDataSet(entries, "");
    dataSet.setSliceSpace(3f);
    dataSet.setSelectionShift(5f);

    dataSet.setColors(colors);

    PieData data = new PieData(dataSet);
    data.setValueFormatter(new DefaultValueFormatter(0));
    data.setValueTextSize(20f);
    data.setValueTextColor(Color.BLACK);
    chart.setData(data);
    // undo all highlights
    chart.highlightValues(null);
    chart.invalidate();
    chart.setCenterText(generateCenterSpannableText(ourRankPoint));
  }

  private void getPointsFromServer(final hCallBack callBack) {
    StringRequest request = new StringRequest(Method.POST, hRequestQueue.BASE_URL + "/point.php",
        new Listener<String>() {
          @Override
          public void onResponse(String response) {
            Logger.d(response);
            callBack.call(response);
          }
        }, new ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Logger.d(error);
      }
    }) {
      @Override
      protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("our_team", mSettings.instance.our_team + "");
        params.put("total_team_count", mSettings.instance.total_team_count + "");
        return params;
      }
    };
    hRequestQueue.getInstance(getActivity()).add(request);
  }

  private SpannableString generateCenterSpannableText(int ourRankPoint) {
    final String z = "generateCenterSpannableText";
    String sRankPoint = ourRankPoint + "점";
    int length = sRankPoint.length();
    SpannableString s = new SpannableString(sRankPoint);
    s.setSpan(new RelativeSizeSpan(2.8f), 0, length, 0);
    return s;
  }
}
