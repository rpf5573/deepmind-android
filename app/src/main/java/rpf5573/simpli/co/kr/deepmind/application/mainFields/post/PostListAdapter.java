package rpf5573.simpli.co.kr.deepmind.application.mainFields.post;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.swipe.SwipeLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import rpf5573.simpli.co.kr.deepmind.R;
import rpf5573.simpli.co.kr.deepmind.application.entrance.registers.ContainerFragment;
import rpf5573.simpli.co.kr.deepmind.application.mainFields.post.PostListAdapter.ViewHolder;
import rpf5573.simpli.co.kr.deepmind.helper.hAlert;
import rpf5573.simpli.co.kr.deepmind.helper.hCallBack;
import rpf5573.simpli.co.kr.deepmind.helper.hColor;
import rpf5573.simpli.co.kr.deepmind.helper.hRequestQueue;
import rpf5573.simpli.co.kr.deepmind.model.mPlayer;
import rpf5573.simpli.co.kr.deepmind.model.mPostCrate;
import rpf5573.simpli.co.kr.deepmind.model.mSettings;

/**
 * Created by mac88 on 2017. 8. 30..
 */

class PostListAdapter extends Adapter<ViewHolder> {

  /* ------------------------------------------------------------------ */
  //  Property
  /* ------------------------------------------------------------------ */

  //  constant
  /* ------------------------------------ */

  //  view component
  /* ------------------------------------ */

  //  data
  /* ------------------------------------ */
  private mPostCrate postCrate;
  private Context context;
  private PostSelectionDelegate postSelectionDelegate;


  /* ------------------------------------------------------------------ */
  //  Function
  /* ------------------------------------------------------------------ */

  //  init & life cycle & override
  /* ------------------------------------ */
  PostListAdapter(Context context, mPostCrate postCrate, PostSelectionDelegate delegate) {
    this.context = context;
    this.postCrate = postCrate;
    this.postSelectionDelegate = delegate;
  }
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View cell = LayoutInflater.from(context).inflate(R.layout.postlist_fragment_cell, parent, false);
    cell.findViewById(R.id.postlist__cell__surface).setOnClickListener(swipeLayoutClickListener);
    cell.findViewById(R.id.postlist__cell__select).setOnClickListener(postSelectListener);
    return new ViewHolder(context, cell);
  }
  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    int post = position + 1;
    holder.setPosition(position);
    holder.setPost(post);
    holder.reset(swipeLayoutClickListener);

    if ( postCrate.didPostSelected(post) ) {
      holder.dim();
    }
    if (postCrate.hosts_of_each_post.get(position).size() > 0) {
      holder.setHosts(postCrate.hosts_of_each_post.get(position));
    }
  }
  @Override
  public int getItemCount() {
    return postCrate.count;
  }

  @Override
  public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
  }
  //  setup
  /* ------------------------------------ */

  //  listener
  /* ------------------------------------ */
  private OnClickListener postSelectListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      Logger.d(v.getId());
      final SwipeLayout swipeLayout = (SwipeLayout) v.getParent().getParent();
      int position = v.getId();
      final int post = position + 1;
      selectPost(post, new hCallBack() {
        @Override
        public void call(String json) {
          JSONObject jsonObj = null;
          swipeLayout.toggle(true);
          try {
            jsonObj = new JSONObject(json);
            int responseCode = jsonObj.getInt("response_code");
            if ( responseCode == 201 ) {
//              String value = jsonObj.getString("value");
//              Gson gson = new Gson();
//              Logger.d(postCrate.hosts_of_each_post);
//              PostListAdapter.this.notifyDataSetChanged();
              postCrate.updateNewPost(post);
              postSelectionDelegate.didSelect(post);
            } else {
              hAlert.show(context, jsonObj.getString("error_message"));
            }
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      });
    }
  };
  private OnClickListener swipeLayoutClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      ((SwipeLayout)(v.getParent())).toggle(true);
    }
  };

  //  custom
  /* ------------------------------------ */
  private void selectPost(final int post, final hCallBack callBack) {
    String url = hRequestQueue.BASE_URL + "/post.php";
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
        String team = mSettings.instance.our_team + "";
        String _post = post + "";
        params.put("team", team);
        params.put("update_post", "true");
        params.put("post", _post);
        return params;
      }
    };
    hRequestQueue.getInstance(context).add(request);
  }
  public int getCurrentPost() {
    return postCrate.current_post;
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    Context context;
    SwipeLayout root;
    View surface;
    Button select;
    TextView post;
    TextView hosts;
    ViewHolder(Context context, View root) {
      super(root);
      this.root = (SwipeLayout) root;
      this.context = context;
      this.surface = this.root.findViewById(R.id.postlist__cell__surface);
      select = (Button) this.root.findViewById(R.id.postlist__cell__select);
      post = (TextView) this.root.findViewById(R.id.postlist__cell__surface__post);
      hosts = (TextView) this.root.findViewById(R.id.postlist__cell__surface__hosts);
      TypedValue outValue = new TypedValue();
      this.context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
      select.setBackgroundResource(outValue.resourceId);
    }
    void setPosition(int position) {
      select.setId(position);
    }
    void setPost(int post) {
      String _post = post+" 포스트";
      this.post.setText(_post);
    }
    void setHosts(ArrayList<Integer> hosts) {
      StringBuilder hostList = new StringBuilder();
      hostList.append("진행중 : [");

      for ( int i = 0; i < hosts.size(); i++ ) {
        String _host = " "+hosts.get(i)+"조";
        if ( i > 5 ) {
          _host = "...";
          hostList.append(_host);
          break;
        }
        hostList.append(_host);
      }
      hostList.append("]");
      this.hosts.setText(hostList.toString());
    }
    void reset(OnClickListener swipeLayoutClickListener) {
      this.surface.setBackgroundColor(hColor.getColor(context, android.R.color.transparent));
      this.surface.setOnClickListener(swipeLayoutClickListener);
      this.root.setSwipeEnabled(true);
      hosts.setText("");
    }
    void dim() {
      this.surface.setBackgroundColor(hColor.getColor(context, R.color.md_orange_600));
      this.surface.setOnClickListener(null);
      this.root.setSwipeEnabled(false);
    }
  }
}
