package rpf5573.simpli.co.kr.deepmind.application.entrance.registers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.blankj.utilcode.util.KeyboardUtils;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;
import rpf5573.simpli.co.kr.deepmind.R;
import rpf5573.simpli.co.kr.deepmind.application.mainFields.MainFieldsActivity;
import rpf5573.simpli.co.kr.deepmind.helper.hCallBack;
import rpf5573.simpli.co.kr.deepmind.helper.hColor;
import rpf5573.simpli.co.kr.deepmind.helper.hRequestQueue;
import rpf5573.simpli.co.kr.deepmind.helper.hStartBrotherEvent;
import rpf5573.simpli.co.kr.deepmind.model.mJokerInfo;
import rpf5573.simpli.co.kr.deepmind.model.mMessage;
import rpf5573.simpli.co.kr.deepmind.model.mPlayer;
import rpf5573.simpli.co.kr.deepmind.model.mSettings;
import rpf5573.simpli.co.kr.deepmind.parent.BaseFragment;

/**
 * Created by mac88 on 2017. 8. 5..
 */

public class ContainerFragment extends BaseFragment implements ViewPager.OnPageChangeListener {
  /* ------------------------------------------------------------------ */
  //  Property
  /* ------------------------------------------------------------------ */

  //  constant
  /* ------------------------------------ */

  //  view component
  /* ------------------------------------ */
  private ViewGroup root;
  private View focusThief;
  private ViewPager viewPager;
  private TabLayout tabLayout;

  //  data
  /* ------------------------------------ */
  MemberRegisterFragment memberRegisterFragment;
  JokerRegisterFragment jokerRegisterFragment;

  /* ------------------------------------------------------------------ */
  //  Function
  /* ------------------------------------------------------------------ */

  //  init & life cycle & override
  /* ------------------------------------ */
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    root = (ViewGroup) inflater.inflate(R.layout.register_container_fragment, container, false);
    setup();
    return root;
  }

  //  setup
  /* ------------------------------------ */
  private void setup() {
    EventBus.getDefault().register(this);
    focusThief = root.findViewById(R.id.focus_thief);
    initToolbarNav(root, R.string.app_name);
    setupViewPager();
    setupTabLayout();
    setupListeners();
  }
  private void setupViewPager() {
    viewPager = (ViewPager)root.findViewById(R.id.register__viewPager);
    RegisterPageAdapter pa = new RegisterPageAdapter(getFragmentManager());
    memberRegisterFragment = new MemberRegisterFragment();
    jokerRegisterFragment = new JokerRegisterFragment();
    pa.addFragment(memberRegisterFragment, "맴버");
    pa.addFragment(jokerRegisterFragment, "조커");
    viewPager.setAdapter(pa);
    viewPager.addOnPageChangeListener(this);
  }
  private void setupTabLayout() {
    tabLayout = (TabLayout)root.findViewById(R.id.register__tabLayout);
    tabLayout.setupWithViewPager(viewPager);
  }
  private void setupListeners() {
    jokerRegisterFragment.completeBtnClickListener = completeBtnClickListener;
  }

  //  listener
  /* ------------------------------------ */
  /* OnPageChangeListener */
  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
  @Override
  public void onPageSelected(int position) {
    KeyboardUtils.hideSoftInput(getActivity());
  }
  @Override
  public void onPageScrollStateChanged(int state) {}
  // 이 펑션이 실행되는게 신기하네!!!
  @Subscribe
  public void startBrother(hStartBrotherEvent event) {
    start(event.targetFragment);
  }
  Button.OnClickListener completeBtnClickListener = new Button.OnClickListener() {
    @Override
    public void onClick(View v) {
      Logger.d(mSettings.instance.options.test_mode);
      if (mSettings.instance.options.test_mode) {
        fillAllFields();
      }
      mPlayer[] players = validateAllFields();

      if (players != null) {
        List<mPlayer> playerList = Arrays.asList(players);
        Collections.shuffle(playerList);
        sendPlayersToServer(playerList, new hCallBack() {
          @Override
          public void call(String json) {
            try {
              JSONObject jsonObject = new JSONObject(json);
              if (jsonObject.getInt(mMessage.RESPONSE_CODE) == 201) {
                new MaterialDialog.Builder(getActivity()).title("메세지")
                    .positiveText("확인")
                    .content(jsonObject.getString(mMessage.SUCCESS_MESSAGE))
                    .onPositive(new SingleButtonCallback() {
                      @Override
                      public void onClick(@NonNull MaterialDialog dialog,
                          @NonNull DialogAction which) {

                        Logger.d(this);
                        Logger.d(ContainerFragment.this);

                        EventBus.getDefault().unregister(ContainerFragment.this);
                        Intent intent = new Intent(getActivity() , MainFieldsActivity.class);
                        startActivity(intent);
                      }
                    })
                    .show();
              } else {
                Logger.d(jsonObject.getString(mMessage.ERROR_MESSAGE));
              }
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        });
      }
    }
  };

  //  custom
  /* ------------------------------------ */
  public static ContainerFragment newInstance() {
    return new ContainerFragment();
  }
  private mPlayer[] validateAllFields() {
    MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(getActivity()).title("메세지")
        .positiveText("확인");

    String[] members = validateMember();
    if (members == null) {
      dialogBuilder
          .content("다시 확인해 주세요")
          .onPositive( new SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

              viewPager.setCurrentItem(0, true);
            }
          })
          .show();
      return null;
    }

    String[] jokers = validateJoker();
    if (jokers == null) {
      dialogBuilder
          .content("다시 확인해 주세요")
          .onPositive( new SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

            }
          })
          .show();
      return null;
    }

    ArrayList<mPlayer> players = new ArrayList<>();
    for(int i = 0; i < members.length; i++) {
      players.add(new mPlayer(members[i], false, 0, false, null));
    }

    if ( mSettings.instance.options.joker_info ) {
      String[][] jokerInfos = new String[2][];
      Logger.d(jokerInfos);
      for (int i = 1; i <= 2; i++) {
        final int z = i;
        jokerInfos[z-1] = validateJokerInfo(i);
        if (jokerInfos[z-1] == null) {
          dialogBuilder
              .content("조커 정보 입력에 공란이 있습니다")
              .onPositive( new SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                  jokerRegisterFragment.openJokerInfoFragment(z);
                }
              })
              .show();
          return null;
        }
      }
      for(int i = 0; i < jokers.length; i++) {
        mJokerInfo jokerInfo = new mJokerInfo(new ArrayList<Integer>(), jokerInfos[i]);
        players.add(new mPlayer(jokers[i], false, 0, true, jokerInfo));
      }
    } else {
      for(int i = 0; i < jokers.length; i++) {
        players.add(new mPlayer(jokers[i], false, 0, true, null));
      }
    }

    return players.toArray(new mPlayer[players.size()]);
  }
  private String[] validateMember() {
    String[] names = new String[memberRegisterFragment.memberRegisterEditTexts.length];
    for (int i = 0; i < memberRegisterFragment.memberRegisterEditTexts.length; i++) {
      TextInputEditText editText = memberRegisterFragment.memberRegisterEditTexts[i];
      String name = editText.getText().toString();
      if (name.isEmpty() || (editText.getError() != null)) {
        return null;
      } else {
        names[i] = name;
      }
    }
    return names;
  }
  private String[] validateJoker() {
    String[] names = new String[2];
    String joker1Name = jokerRegisterFragment.joker1NameEditText.getText().toString();
    String joker2Name = jokerRegisterFragment.joker2NameEditText.getText().toString();
    if (joker1Name.isEmpty() || joker2Name.isEmpty() || (jokerRegisterFragment.joker1NameEditText.getError() != null) || (jokerRegisterFragment.joker2NameEditText.getError() != null) ) {
      return null;
    }
    names[0] = joker1Name;
    names[1] = joker2Name;
    return names;
  }
  private String[] validateJokerInfo(int order) {
    String[] answers = jokerRegisterFragment.getJokerInfoAnswers(order);
    if (answers != null) {
      for (String answer : answers) {
        if (answer == null || answer.isEmpty()) {
          return null;
        }
      }
    }
    return answers;
  }
  private void fillAllFields() {
    fillMemberFields();
    fillJokerFields();
    if ( mSettings.instance.options.joker_info ) {
      fillJokerInfos();
    }
  }
  private void fillMemberFields() {
    String[] members = {"정윤석","박사랑","김향기","박준목","정다빈","김유정","조수민","정채은","심혜원","은원재","고주연","주민수","이현우","정민아","한예린","남지현","박지빈","백승도","박보영"};
    for(int i = 0; i < memberRegisterFragment.memberRegisterEditTexts.length; i++) {
      ((EditText)memberRegisterFragment.memberRegisterEditTexts[i].findViewById(R.id.register__memberNameEditText)).setText(members[i]);
    }
  }
  private void fillJokerFields() {
    String[] jokers = {"나조커", "나도조커"};
    jokerRegisterFragment.joker1NameEditText.setText(jokers[0]);
    jokerRegisterFragment.joker2NameEditText.setText(jokers[1]);
  }
  private void fillJokerInfos() {
    String[][] answers = {
        {
            "비 내리면산 부풀고산 부풀면개울물 넘친다.",
            "귀뚜라미 귀뜨르르 가느단 소리",
            "7년 후에 지구를 한바퀴 돌 수 있다. ",
            "신은 용기있는자를 결코 버리지 않는다",
            "피할수 없으면 즐겨라",
            "더많이 실험할수록 더나아진다"
        },
        {
            "푹푹 찌는 여름.",
            "아이스크림보다 생각나는 것이 있나.",
            "한 송이의 국화꽃",
            "너를 예로 들어",
            "문득 아름다운 것과 마주쳤을 때 지금 곁에 있으면 얼마나 좋을까 하고",
            "늦게 도착한 바람이 때를 놓치고, 책은 덮인다"
        }
    };
    jokerRegisterFragment.setJokerInfoAnswers(1, answers[0]);
    jokerRegisterFragment.setJokerInfoAnswers(2, answers[1]);
  }
  private void sendPlayersToServer(List<mPlayer> players, final hCallBack callBack) {
    Gson gson = new Gson();
    final String json = gson.toJson(players);
    Logger.d(json);

    StringRequest request = new StringRequest(Method.POST, hRequestQueue.BASE_URL+"/register.php",
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
        params.put("players", json);
        params.put("our_team", mSettings.instance.our_team+"");
        return params;
      }
    };
    hRequestQueue.getInstance(getActivity()).add(request);
  }
}