package rpf5573.simpli.co.kr.deepmind.application.mainFields.out;

import static android.R.attr.button;
import static android.R.attr.hasCode;
import static android.R.attr.resizeable;
import static android.R.attr.viewportHeight;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.swipe.SwipeLayout.SwipeListener;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.orhanobut.logger.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rpf5573.simpli.co.kr.deepmind.R;
import rpf5573.simpli.co.kr.deepmind.helper.hAlert;
import rpf5573.simpli.co.kr.deepmind.helper.hCallBack;
import rpf5573.simpli.co.kr.deepmind.helper.hColor;
import rpf5573.simpli.co.kr.deepmind.helper.hRequestQueue;
import rpf5573.simpli.co.kr.deepmind.model.mPlayer;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import rpf5573.simpli.co.kr.deepmind.model.mSettings;

/**
 * Created by mac88 on 2017. 8. 19..
 */

class OutListAdapter extends RecyclerSwipeAdapter<OutListAdapter.ViewHolder> {

  /* ------------------------------------------------------------------ */
  //  Property
  /* ------------------------------------------------------------------ */

  //  constant
  /* ------------------------------------ */

  //  view component
  /* ------------------------------------ */
  RecyclerView recyclerView;

  //  data
  /* ------------------------------------ */
  private Context context;
  private ArrayList<mPlayer> players;
  private int currentTeam = 0;


  /* ------------------------------------------------------------------ */
  //  Function
  /* ------------------------------------------------------------------ */

  //  init & life cycle & override
  /* ------------------------------------ */
  OutListAdapter(Context context, ArrayList<mPlayer> players) {
    this.context = context;
    this.players = players;
  }
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    SwipeLayout v = (SwipeLayout) LayoutInflater.from(context).inflate(R.layout.outlist_fragment_cell, parent, false);
    return new ViewHolder(v, context, swipeLayoutClickListener, outClickListener, swipeListener);
  }
  @Override
  public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    this.recyclerView = recyclerView;
    super.onAttachedToRecyclerView(recyclerView);
  }
  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {

    int ourTeam = mSettings.instance.our_team;
    holder.nameTextView.setText(players.get(position).name);
    holder.setPosition(position);

    holder.reset(swipeLayoutClickListener);
    if ( players.get(position).is_outed ) {
      holder.dim();
    }
  }
  @Override
  public int getItemCount() {
    return players.size();
  }
  @Override
  public int getSwipeLayoutResourceId(int position) {
    return R.layout.outlist_fragment_cell;
  }

  //  setup
  /* ------------------------------------ */

  //  listener
  /* ------------------------------------ */
  private OnClickListener swipeLayoutClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      ((SwipeLayout)(v.getParent())).toggle(true);
    }
  };
  private OnClickListener outClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      final SwipeLayout swipeLayout = (SwipeLayout) v.getParent().getParent();
      if (currentTeam == mSettings.instance.our_team) {
        hAlert.show(context, "자신의 조원을 활동제한시킬 수 없습니다");
        swipeLayout.close();
      } else {
        final int position = v.getId();
        String message = mSettings.instance.mapping_points.get("out_cost") + "점을 소모하고 활동제한 시키겠습니까?";
        hAlert.approval(context, message, new SingleButtonCallback() {
          @Override
          public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            final String targetName = players.get(position).name;
            out(targetName, new hCallBack() {
              @Override
              public void call(String json) {
                JSONObject jsonObj = null;
                try {
                  jsonObj = new JSONObject(json);
                  int responseCode = jsonObj.getInt("response_code");
                  if (responseCode == 201) {
                    hAlert.show(context, jsonObj.getString("success_message"));
                    players.get(position).is_outed = true;
                    OutListAdapter.this.notifyDataSetChanged();
                  } else {
                    swipeLayout.close();
                    hAlert.show(context, jsonObj.getString("error_message"));
                  }
                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            });
          }
        }, new SingleButtonCallback() {
          @Override
          public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            swipeLayout.toggle(true);
          }
        });
      }
    }
  };
  private SwipeListener swipeListener = new SwipeListener() {
    @Override
    public void onStartOpen(SwipeLayout layout) {
      Logger.d("called");
      int cell_count = recyclerView.getChildCount();
      for ( int i = 0; i < cell_count; i++ ) {
        SwipeLayout swipeLayout = (SwipeLayout)recyclerView.getChildAt(i);
        if ( ! swipeLayout.equals(layout) ) {
          swipeLayout.close();
        }
      }
    }

    @Override
    public void onOpen(SwipeLayout layout) {
      Logger.d("called");
    }

    @Override
    public void onStartClose(SwipeLayout layout) {
      Logger.d("called");
    }

    @Override
    public void onClose(SwipeLayout layout) {
      Logger.d("called");
    }

    @Override
    public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
      Logger.d("called");
    }

    @Override
    public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
      Logger.d("called");
    }
  };

  //  custom
  /* ------------------------------------ */
  static class ViewHolder extends RecyclerView.ViewHolder {
    TextView nameTextView;
    Button outBtn;
    View surface;
    SwipeLayout root;
    Context context;
    ImageView silence;
    ViewHolder(View root, Context context, OnClickListener swipeLayoutClickListener, OnClickListener outClickListener, SwipeListener swipeListener) {
      super(root);
      this.root = (SwipeLayout) root;
      this.context = context;
      surface = this.root.findViewById(R.id.outlist__cell__surface);

      this.root.findViewById(R.id.outlist__cell__surface).setOnClickListener(swipeLayoutClickListener);

      this.root.addSwipeListener(swipeListener);
      nameTextView = (TextView) this.root.findViewById(R.id.outlist__cell__name);
      outBtn = (Button) this.root.findViewById(R.id.outlist__cell__out);
      outBtn.setOnClickListener(outClickListener);
      silence = (ImageView) this.root.findViewById(R.id.outlist__cell__silence);
      TypedValue outValue = new TypedValue();
      this.context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
      outBtn.setBackgroundResource(outValue.resourceId);
    }
    public void setPosition(int position) {
      outBtn.setId(position);
    }
    void reset(OnClickListener swipeLayoutClickListener) {
      this.silence.setVisibility(View.GONE);
      this.surface.setOnClickListener(swipeLayoutClickListener);
      this.root.setSwipeEnabled(true);
    }
    void dim() {
      this.silence.setVisibility(View.VISIBLE);
      this.surface.setOnClickListener(null);
      this.root.setSwipeEnabled(false);
    }
  }
  private void out(final String name, final hCallBack callBack) {
    int ourTeam = mSettings.instance.our_team;
    String url = hRequestQueue.BASE_URL + "/out.php";
    StringRequest stringRequest = new StringRequest(Method.POST, url,
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
      protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = new HashMap<>();
        String ourTeam = mSettings.instance.our_team+"";
        String theTeam = currentTeam + "";
        params.put("out", "true");
        params.put("our_team", ourTeam);
        params.put("team", theTeam);
        params.put("name", name);
        return params;
      }
    };
    hRequestQueue.getInstance(context).add(stringRequest);
  }
  void setTeam(int team) {
    this.currentTeam = team;
  }
}
