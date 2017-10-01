package rpf5573.simpli.co.kr.deepmind.application.mainFields.post;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.LayoutParams;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import org.json.JSONException;
import org.json.JSONObject;
import rpf5573.simpli.co.kr.deepmind.R;
import rpf5573.simpli.co.kr.deepmind.helper.hAlert;
import rpf5573.simpli.co.kr.deepmind.helper.hCallBack;
import rpf5573.simpli.co.kr.deepmind.helper.hRequestQueue;
import rpf5573.simpli.co.kr.deepmind.model.mPlayer;
import rpf5573.simpli.co.kr.deepmind.model.mPostCrate;
import rpf5573.simpli.co.kr.deepmind.model.mSettings;
import rpf5573.simpli.co.kr.deepmind.parent.StackFragment;

/**
 * Created by mac88 on 2017. 8. 30..
 */

public class PostListFragment extends StackFragment implements PostSelectionDelegate {
  /* ------------------------------------------------------------------ */
  //  Property
  /* ------------------------------------------------------------------ */

  //  constant
  /* ------------------------------------ */

  //  view component
  /* ------------------------------------ */
  ViewGroup root;
  RecyclerView postList;

  //  data
  /* ------------------------------------ */
  Context context;
  PostListAdapter adapter;
  BackStackDelegate backStackDelegate;


  /* ------------------------------------------------------------------ */
  //  Function
  /* ------------------------------------------------------------------ */

  //  init & life cycle & override
  /* ------------------------------------ */
  public static PostListFragment newInstance(BackStackDelegate delegate) {
    PostListFragment postListFragment = new PostListFragment();
    postListFragment.backStackDelegate = delegate;
    return postListFragment;
  }
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    this.context = getActivity();
    root = (ViewGroup) inflater.inflate(R.layout.postlist_fragment, container, false);
    setup();
    return root;
  }
  @Override
  public boolean onBackPressedSupport() {
    this.pop();
    return true;
  }
  // detach에다 둔 이유는, toolbar의 뒤로가기 버튼을 눌러도 이게 실행되고, 물리적인 뒤로가기 버튼을 눌러도 이게 어쨋든 실행되니까~~
  @Override
  public void onDetach() {
    super.onDetach();
    Logger.d("called");
    if (adapter != null && getCurrentPost() > 0) {
      backStackDelegate.onBackStack(getCurrentPost());
    }
  }

  //  setup
  /* ------------------------------------ */
  private void setup() {
    setupToolBar();
    setupPostList();
  }
  private void setupToolBar() {
    initToolbarNav(root, R.string.postSelect);
    TextView title = (TextView) toolbar.getChildAt(1);
    Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    params.gravity = Gravity.CENTER_HORIZONTAL;
    title.setLayoutParams(params);
  }
  private void setupPostList() {
    postList = (RecyclerView) root.findViewById(R.id.postlist__recyclerView);
    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, layoutManager.getOrientation());
    postList.addItemDecoration(dividerItemDecoration);
    postList.setLayoutManager(layoutManager);
    getPosts(new hCallBack() {
      @Override
      public void call(String json) {
        JSONObject jsonObj = null;
        try {
          jsonObj = new JSONObject(json);
          int responseCode = jsonObj.getInt("response_code");
          if ( responseCode == 201 ) {
            String value = jsonObj.getString("value");
            Logger.d(value);
            Gson gson = new Gson();
            adapter = new PostListAdapter(context, gson.fromJson(value, mPostCrate.class), PostListFragment.this);
            postList.setAdapter(adapter);
          } else {
            hAlert.show(context, jsonObj.getString("error_message"));
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    });
  }
  @Override
  public void didSelect(int post) {
    Logger.d(post);
    pop();
  }

  //  listener
  /* ------------------------------------ */

  //  custom
  /* ------------------------------------ */
  private void getPosts(final hCallBack callBack) {
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
        String ourTeam = mSettings.instance.our_team + "";
        String totalTeamCount = mSettings.instance.total_team_count + "";
        params.put("team", ourTeam);
        params.put("get_posts", "true");
        params.put("total_team_count", totalTeamCount);
        return params;
      }
    };
    hRequestQueue.getInstance(getActivity()).add(request);
  }
  public int getCurrentPost() {
    if ( adapter == null ) {
      return 0;
    } else {
      return adapter.getCurrentPost();
    }
  }
}
