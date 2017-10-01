package rpf5573.simpli.co.kr.deepmind.application.mainFields.out;

import static android.view.View.GONE;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import rpf5573.simpli.co.kr.deepmind.R;
import rpf5573.simpli.co.kr.deepmind.helper.hAlert;
import rpf5573.simpli.co.kr.deepmind.helper.hCallBack;
import rpf5573.simpli.co.kr.deepmind.helper.hRequestQueue;
import rpf5573.simpli.co.kr.deepmind.helper.hStartBrotherEvent;
import rpf5573.simpli.co.kr.deepmind.model.mPlayer;
import rpf5573.simpli.co.kr.deepmind.model.mSettings;
import rpf5573.simpli.co.kr.deepmind.parent.BaseFragment;

/**
 * Created by mac88 on 2017. 8. 10..
 */

public class OutFragment extends BaseFragment implements OutTeamBtnClickHandleDelegate {
  /* ------------------------------------------------------------------ */
  //  Property
  /* ------------------------------------------------------------------ */

  //  constant
  /* ------------------------------------ */
  Context context;

  //  view component
  /* ------------------------------------ */
  ViewGroup root;
  RecyclerView recyclerView;

  //  data
  /* ------------------------------------ */
  OutListFragment outListFragment;
  OutFragmentAdapter adapter;
  RecyclerView.LayoutManager layoutManager;


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
    root = (ViewGroup) inflater.inflate(R.layout.out_fragment, container, false);
    setup();
    setupRecyclerView();
    return root;
  }
  @Override
  public void onOutTeamBtnClick(final int team) {

    int position = team - 1;
    final OutFragmentAdapter.ViewHolder viewHolder = (OutFragmentAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
    viewHolder.showIndicator();
    getPlayers(team, new hCallBack() {
      @Override
      public void call(String json) {
        JSONObject jsonObj = null;
        try {
          jsonObj = new JSONObject(json);
          int responseCode = jsonObj.getInt("response_code");
          if ( responseCode == 201 ) {
            String value = jsonObj.getString("value");
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<mPlayer>>(){}.getType();
            ArrayList<mPlayer> players = gson.fromJson(value, listType);
            if (outListFragment == null) {
              outListFragment = OutListFragment.newInstance();
            }
            outListFragment.setCurrentTeam(team);
            outListFragment.setPlayers(players);
            viewHolder.hideIndicator();
            EventBus.getDefault().post( new hStartBrotherEvent(outListFragment));
          } else {

            viewHolder.hideIndicator();
            hAlert.show(getActivity(), jsonObj.getString("error_message"));
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
    context = getActivity();
  }
  private void setupRecyclerView() {
    recyclerView = (RecyclerView) root.findViewById(R.id.out__recyclerView);
    recyclerView.setHasFixedSize(true);
    layoutManager = new GridLayoutManager(context, 2);
    recyclerView.setLayoutManager(layoutManager);
    ArrayList<Integer> items = new ArrayList<>();
    int totalTeamCount = mSettings.instance.total_team_count;
    for(int i = 1; i <= totalTeamCount; i++) {
      items.add(i);
    }
    adapter = new OutFragmentAdapter(items, context, this);
    recyclerView.setAdapter(adapter);
    ItemOffsetDecoration itemOffsetDecoration = new ItemOffsetDecoration(context, R.dimen.out_fragment_teamButtonOffset);
    recyclerView.addItemDecoration(itemOffsetDecoration);
  }

  //  listener
  /* ------------------------------------ */

  //  custom
  /* ------------------------------------ */
  private void getPlayers(final int team, final hCallBack callBack) {
    String url = hRequestQueue.BASE_URL + "/out.php";
    StringRequest request = new StringRequest(Method.POST, url, new Listener<String>() {
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
      public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        String _team = team + "";
        params.put("team", _team);
        params.put("get_players", "give");
        return params;
      }
    };
    hRequestQueue.getInstance(getActivity()).add(request);
  }

  private class ItemOffsetDecoration extends RecyclerView.ItemDecoration {
    private int itemOffset;
    ItemOffsetDecoration(int itemOffset) {
      this.itemOffset = itemOffset;
    }
    ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
      this(context.getResources().getDimensionPixelSize(itemOffsetId));
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
      super.getItemOffsets(outRect, view, parent, state);
      outRect.set(itemOffset, itemOffset, itemOffset, itemOffset);
    }
  }
}
