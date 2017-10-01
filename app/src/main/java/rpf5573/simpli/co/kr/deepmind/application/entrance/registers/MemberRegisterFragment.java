package rpf5573.simpli.co.kr.deepmind.application.entrance.registers;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Layout;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.google.android.flexbox.FlexboxLayout;
import com.orhanobut.logger.Logger;
import me.yokeyword.fragmentation.SupportFragment;
import rpf5573.simpli.co.kr.deepmind.R;
import rpf5573.simpli.co.kr.deepmind.model.mSettings;

/**
 * Created by mac88 on 2017. 7. 28..
 */

public class MemberRegisterFragment extends SupportFragment {

  /* ------------------------------------------------------------------ */
  //  Property
  /* ------------------------------------------------------------------ */

  //  constant
  /* ------------------------------------ */
  static class constant {
    static int editTextFontSize = 28;
  }

  //  view component
  /* ------------------------------------ */
  public TextInputEditText[] memberRegisterEditTexts = new TextInputEditText[mSettings.instance.team_player_counts[mSettings.instance.our_team-1] - 2];
  private ScrollView root;
  private FlexboxLayout container;


  /* ------------------------------------------------------------------ */
  //  Function
  /* ------------------------------------------------------------------ */

  //  init & life cycle
  /* ------------------------------------ */
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.member_register_fragment, container, false);
  }
  @Override
  public void onStart() {
    super.onStart();
    setup();
  }

  //  setup
  /* ------------------------------------ */
  private void setup() {
    root = (ScrollView) this.getView();
    Logger.d(memberRegisterEditTexts);
    setupContainer();
  }
  private void setupContainer() {
    container = (FlexboxLayout) root.findViewById(R.id.member_register_editTexts_container);
    FlexboxLayout.LayoutParams fl = new FlexboxLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
    Context context = getActivity();
    fl.setMargins(0, 40, 0, 0);
    for( int i = 0; i < memberRegisterEditTexts.length; i++ ) {
      TextInputEditText textInputEditText = makeTextInputEditText(i);
      memberRegisterEditTexts[i] = textInputEditText;
      fl.setFlexBasisPercent(0.5f);
      TextInputLayout textInputLayout = new TextInputLayout(context);
      textInputLayout.setLayoutParams(fl);
      textInputLayout.setHint("맴버"+(i+1));
      textInputLayout.addView(textInputEditText);
      textInputEditText.addTextChangedListener(new MyTextWatcher(textInputEditText, getActivity()));
      container.addView(textInputLayout);
    }
  }

  //  listener
  /* ------------------------------------ */

  //  custom
  /* ------------------------------------ */
  private TextInputEditText makeTextInputEditText(int position) {
    Context context = getActivity();
    TextInputEditText et = new TextInputEditText(context);
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
    et.setLayoutParams(layoutParams);
    et.setTextSize(TypedValue.COMPLEX_UNIT_SP, constant.editTextFontSize);
    et.setSingleLine(true);
    et.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    et.setId(R.id.register__memberNameEditText);
    return et;
  }
}
