package rpf5573.simpli.co.kr.deepmind.application.mainFields.out;

import static android.view.View.GONE;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.LayoutParams;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.afollestad.materialdialogs.MaterialDialog.ListCallback;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.blankj.utilcode.util.SizeUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rpf5573.simpli.co.kr.deepmind.R;
import rpf5573.simpli.co.kr.deepmind.application.entrance.registers.JokerInfoRegisterFragment;
import rpf5573.simpli.co.kr.deepmind.helper.hAlert;
import rpf5573.simpli.co.kr.deepmind.helper.hCallBack;
import rpf5573.simpli.co.kr.deepmind.helper.hColor;
import rpf5573.simpli.co.kr.deepmind.helper.hRequestQueue;
import rpf5573.simpli.co.kr.deepmind.model.mJokerInfo;
import rpf5573.simpli.co.kr.deepmind.model.mPlayer;
import rpf5573.simpli.co.kr.deepmind.model.mSettings;
import rpf5573.simpli.co.kr.deepmind.parent.StackFragment;

/**
 * Created by mac88 on 2017. 8. 19..
 */

public class OutListFragment extends StackFragment {

  /* ------------------------------------------------------------------ */
  //  Property
  /* ------------------------------------------------------------------ */

  //  constant
  /* ------------------------------------ */

  //  view component
  /* ------------------------------------ */
  ViewGroup root;
  RecyclerView outList;
  ViewGroup jokerInfoChoiceBottomSheetView;
  BottomSheetDialog jokerInfoChoiceBottomSheet;
  Button joker1InfoBuyBtn;
  Button joker2InfoBuyBtn;

  //  data
  /* ------------------------------------ */
  private int currentTeam = 0;
  Context context;
  ArrayList<mPlayer> players;
  ArrayList<mPlayer> jokers;
  LinearLayoutManager layoutManager;
  OutListAdapter adapter;


  /* ------------------------------------------------------------------ */
  //  Function
  /* ------------------------------------------------------------------ */

  //  init & life cycle & override
  /* ------------------------------------ */
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    root = (ViewGroup) inflater.inflate(R.layout.outlist_fragment, container, false);
    setup();
    return root;
  }
  @Override
  public boolean onBackPressedSupport() {

    this.pop();
    return true;
  }

  //  setup
  /* ------------------------------------ */
  private void setup() {
    context = getActivity();
    setupToolBar();
    setupRecyclerView();
    setupJokerInfoChoiceBottomSheet();
  }
  private void setupToolBar() {
    initToolbarNav(root, R.string.back);
    Button button = new Button(context);
    button.setText("조커정보");
    Toolbar.LayoutParams params2 = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
    params2.gravity = Gravity.END;
    button.setLayoutParams(params2);
    button.setTextColor(hColor.getColor(context, R.color.md_white_1000));
    // 이건 뭐냐,,, 그냥 바로 resourceId때리면 안되는거냐??
    TypedValue outValue = new TypedValue();
    getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
    button.setBackgroundResource(outValue.resourceId);
    button.setTextSize(SizeUtils.dp2px(5));
    button.setOnClickListener(jokerInfoChoiceBottomSheetShowBtnClickListener);
    toolbar.addView(button);

    String restrict = currentTeam + "조 활동제한";
    toolbar.setTitle(restrict);

    //우리팀이라면
    if (currentTeam == mSettings.instance.our_team) {
      toolbar.setTitle("우리조");
    }
    // 조커 정보를 사용하지 않는다면
    if ( ! mSettings.instance.options.joker_info ) {
      button.setVisibility(GONE);
    }
  }
  private void setupRecyclerView() {
    outList = (RecyclerView) root.findViewById(R.id.outlist__recyclerView);
    layoutManager = new LinearLayoutManager(context);
    outList.setLayoutManager(layoutManager);
    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, layoutManager.getOrientation());
    outList.addItemDecoration(dividerItemDecoration);
    adapter = new OutListAdapter(context, players);
    adapter.setTeam(currentTeam);
    outList.setAdapter(adapter);
  }
  private void setupJokerInfoChoiceBottomSheet() {
    jokerInfoChoiceBottomSheet = new BottomSheetDialog(context);
    jokerInfoChoiceBottomSheetView = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.outlist_bottom_sheet, null);
    jokerInfoChoiceBottomSheet.setContentView(jokerInfoChoiceBottomSheetView);
  }

  //  listener
  /* ------------------------------------ */
  private OnClickListener jokerInfoChoiceBottomSheetShowBtnClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      final int ourTeam = mSettings.instance.our_team;
      // 총5가지의 경우의 수가 나옵니다
      // 첫번째( 기본셋팅 ) - 조커1,2정보를 둘다 사지 않은 경우 - 이게 제일 흔하지!
      class SheetComponents {
        String title = "조커 정보 구매";
        String message = mSettings.instance.mapping_points.get("joker_info")+"포인트가 소모됩니다";
        String btn1Title = "조커1 정보 구매";
        OnClickListener btn1CallBack = new OnClickListener() {
          @Override
          public void onClick(View v) {
            buyJokerInfo(jokers.get(0).name, new hCallBack() {
              @Override
              public void call(String json) {
                Logger.d(json);
                JSONObject jsonObj = null;
                try {
                  jsonObj = new JSONObject(json);
                  int responseCode = jsonObj.getInt("response_code");
                  if ( responseCode == 201 ) {
                    jokers.get(0).joker_info.sold_by.add(ourTeam);
                    openJokerInfoDialog(1);
                  } else {
                    jokerInfoChoiceBottomSheet.hide();
                    hAlert.show(context, jsonObj.getString("error_message"));
                  }
                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            });
          }
        };
        String btn2Title = "조커2 정보 구매";
        OnClickListener btn2CallBack = new OnClickListener() {
          @Override
          public void onClick(View v) {
            buyJokerInfo(jokers.get(1).name, new hCallBack() {
              @Override
              public void call(String json) {
                Logger.d(json);
                JSONObject jsonObj = null;
                try {
                  jsonObj = new JSONObject(json);
                  int responseCode = jsonObj.getInt("response_code");
                  if ( responseCode == 201 ) {
                    jokers.get(1).joker_info.sold_by.add(ourTeam);
                    openJokerInfoDialog(2);
                  } else {
                    jokerInfoChoiceBottomSheet.hide();
                    hAlert.show(context, jsonObj.getString("error_message"));
                  }
                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            });
          }
        };
      }
      SheetComponents sheetComponents = new SheetComponents();

      // 두번째 경우 - 현재 이곳이 우리팀이라면~!
      if ( currentTeam == ourTeam ) {
        sheetComponents.title = "조커 정보 확인";
        sheetComponents.message = "우리조의 조커 정보를 확인해 보세요";
        sheetComponents.btn1Title = "정보확인";
        sheetComponents.btn1CallBack = new OnClickListener() {
          @Override
          public void onClick(View v) {
            openJokerInfoDialog(1);
          }
        };
        sheetComponents.btn2Title = "정보확인";
        sheetComponents.btn2CallBack = new OnClickListener() {
          @Override
          public void onClick(View v) {
            openJokerInfoDialog(2);
          }
        };
      }

      // 세번째 경우 - 우리팀이 조커1의 정보만 샀을때
      else if ( jokers.get(0).joker_info.didSoldBy(ourTeam) && !jokers.get(1).joker_info.didSoldBy(ourTeam) ) {
        sheetComponents.btn1Title = "정보확인";
        sheetComponents.btn1CallBack = new OnClickListener() {
          @Override
          public void onClick(View v) {
            openJokerInfoDialog(1);
          }
        };
      }

      // 네번째 경우 - 우리팀이 조커2의 조커정보만 샀을때
      else if ( !jokers.get(0).joker_info.didSoldBy(ourTeam) && jokers.get(1).joker_info.didSoldBy(ourTeam) ) {
        sheetComponents.btn2Title = "정보확인";
        sheetComponents.btn2CallBack = new OnClickListener() {
          @Override
          public void onClick(View v) {
            openJokerInfoDialog(2);
          }
        };
      }

      // 다섯번째 경우 - 둘다 샀을때
      else if ( jokers.get(0).joker_info.didSoldBy(ourTeam) && jokers.get(1).joker_info.didSoldBy(ourTeam) ) {
        sheetComponents.btn1Title = "정보확인";
        sheetComponents.btn2Title = "정보확인";
        sheetComponents.btn1CallBack = new OnClickListener() {
          @Override
          public void onClick(View v) {
            openJokerInfoDialog(1);
          }
        };
        sheetComponents.btn2CallBack = new OnClickListener() {
          @Override
          public void onClick(View v) {
            openJokerInfoDialog(2);
          }
        };
      }

      ((TextView) jokerInfoChoiceBottomSheetView.findViewById(R.id.outlist__bottomSheet__title)).setText(sheetComponents.title);
      ((TextView) jokerInfoChoiceBottomSheetView.findViewById(R.id.outlist__bottomSheet__message)).setText(sheetComponents.message);
      ((Button) jokerInfoChoiceBottomSheetView.findViewById(R.id.outlist__bottomSheet__joker1BuyBtn)).setText(sheetComponents.btn1Title);
      ((Button) jokerInfoChoiceBottomSheetView.findViewById(R.id.outlist__bottomSheet__joker1BuyBtn)).setOnClickListener(sheetComponents.btn1CallBack);
      ((Button) jokerInfoChoiceBottomSheetView.findViewById(R.id.outlist__bottomSheet__joker2BuyBtn)).setText(sheetComponents.btn2Title);
      ((Button) jokerInfoChoiceBottomSheetView.findViewById(R.id.outlist__bottomSheet__joker2BuyBtn)).setOnClickListener(sheetComponents.btn2CallBack);

      jokerInfoChoiceBottomSheet.show();
    }
  };
  private OnClickListener jokerInfoBuyBtnClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      jokerInfoChoiceBottomSheet.hide();
      if (v.equals(joker1InfoBuyBtn)) {
        mJokerInfo jokerInfo = jokers.get(0).joker_info;
        //구매 했는지 확인먼저 하겠습니다
        for(int i = 0; i < jokerInfo.sold_by.size(); i++) {
          if (jokerInfo.sold_by.get(i).equals(mSettings.instance.our_team)) {
            openJokerInfoDialog(1);
            return;
          }
        }
      } else {
        openJokerInfoDialog(2);
      }
    }
  };

  //  custom
  /* ------------------------------------ */
  public static OutListFragment newInstance() {
    return new OutListFragment();
  }
  public void setCurrentTeam(int team) {
    this.currentTeam = team;
  }
  private void openJokerInfoDialog(int order) {
    //내리고 시작하자
    jokerInfoChoiceBottomSheet.hide();
    String title = "조커"+order+"정보";
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
    MaterialDialog dialog = new Builder(context)
        .title(title)
        .adapter(new JokerInfoDialogListAdapter(context, jokers.get(order-1).joker_info.answers), linearLayoutManager)
        .build();
    RecyclerView jokerInfoList = dialog.getRecyclerView();
    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, linearLayoutManager.getOrientation());
    jokerInfoList.addItemDecoration(dividerItemDecoration);
    dialog.show();
  }
  private void buyJokerInfo(final String name, final hCallBack callBack) {
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
        String team = currentTeam + "";
        String ourTeam = mSettings.instance.our_team + "";
        params.put("team", team);
        params.put("name", name);
        params.put("buy_joker_info", "true");
        params.put("our_team", ourTeam);
        return params;
      }
    };
    hRequestQueue.getInstance(getActivity()).add(request);
  }
  public void setPlayers(ArrayList<mPlayer> players) {
    this.players = players;
    this.jokers = new ArrayList<mPlayer>();
    for( mPlayer player : this.players ) {
      if ( player.is_joker ) {
        jokers.add(player);
      }
    }
  }

  static class JokerInfoDialogListAdapter extends Adapter<JokerInfoDialogListAdapter.ViewHolder> {

    Context context;
    String[] answers;
    String[] questions = mSettings.instance.joker_info_questions;

    JokerInfoDialogListAdapter(Context context, String[] answers) {
      this.context = context;
      this.answers = answers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      Logger.d(parent);
      LinearLayout cell = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.outlist_jokerinfo_cell, parent, false);
      return new ViewHolder(cell);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      holder.question.setText(questions[position]);
      holder.answer.setText(answers[position]);
    }

    @Override
    public int getItemCount() {
      return questions.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
      TextView question;
      TextView answer;
      ViewHolder(View root) {
        super(root);
        question = (TextView) root.findViewById(R.id.outlist__jokerinfo__cell__question);
        answer = (TextView) root.findViewById(R.id.outlist__jokerinfo__cell__answer);
      }
    }
  }
}