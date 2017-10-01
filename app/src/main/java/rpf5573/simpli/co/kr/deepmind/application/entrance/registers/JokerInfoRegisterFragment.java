package rpf5573.simpli.co.kr.deepmind.application.entrance.registers;

import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.orhanobut.logger.Logger;
import java.util.ArrayList;
import java.util.List;
import rpf5573.simpli.co.kr.deepmind.R;
import rpf5573.simpli.co.kr.deepmind.model.mSettings;
import rpf5573.simpli.co.kr.deepmind.parent.StackFragment;

/**
 * Created by mac88 on 2017. 8. 4..
 */

public class JokerInfoRegisterFragment extends StackFragment {
  /* ------------------------------------------------------------------ */
  //  Property
  /* ------------------------------------------------------------------ */

  //  constant
  /* ------------------------------------ */

  //  view component
  /* ------------------------------------ */
  ViewGroup root;
  ViewGroup inputContainer;
  TextInputEditText[] editTexts = new TextInputEditText[mSettings.instance.joker_info_questions.length];

  //  data
  /* ------------------------------------ */
  String[] questions = mSettings.instance.joker_info_questions;
  private int currentOrder = 1;
  String[][] answers = new String[2][questions.length];

  /* ------------------------------------------------------------------ */
  //  Function
  /* ------------------------------------------------------------------ */

  //  init & life cycle & override
  /* ------------------------------------ */
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    root = (ViewGroup) inflater.inflate(R.layout.joker_info_fragment, container, false);
    Logger.d(answers);
    setup();
    return root;
  }
  @Override
  public void onDestroy() {
    super.onDestroy();
    KeyboardUtils.hideSoftInput(getActivity());
  }

  //  setup
  /* ------------------------------------ */
  private void setup() {
    setupToolBar();
    setupTextEditors();
    setupCompleteBtn();
    setupAnswerToEditText();
  }
  private void setupToolBar() {
    if (currentOrder == 1) {
      initToolbarNav(root, R.string.joker1_info);
    } else if (currentOrder == 2) {
      initToolbarNav(root, R.string.joker2_info);
    }
  }
  private void setupTextEditors() {
    inputContainer = (ViewGroup) root.findViewById(R.id.register__jokerInfoInputContainer);
    for(int i = 0; i < questions.length; i++) {
      TextInputLayout textInputLayout = makeTextInputLayout();
      editTexts[i] = makeTextInputEditText(i);
      textInputLayout.addView(editTexts[i]);
      inputContainer.addView(textInputLayout);
    }
  }
  private void setupCompleteBtn() {
    inputContainer.addView(makeCompleteBtn());
  }
  private void setupAnswerToEditText() {
    for(int i = 0; i < editTexts.length; i++) {
      editTexts[i].setText(answers[currentOrder - 1][i]);
    }
  }

  //  listener
  /* ------------------------------------ */
  private Button.OnClickListener jokerInfoInputCompleteListener = new Button.OnClickListener() {
    @Override
    public void onClick(View v) {

      boolean isAllFilled = true;
      for (int i = 0; i < editTexts.length; i++) {
        String answer = editTexts[i].getText().toString();
        if (answer.isEmpty()) {
          isAllFilled = false;
        }
        answers[currentOrder - 1][i] = answer;
      }

      if (!isAllFilled) {
        new MaterialDialog.Builder(getActivity())
            .title("메세지")
            .content("공란이 있습니다. 다시 확인해주세요")
            .positiveText("확인")
            .show();
      } else {
        new MaterialDialog.Builder(getActivity())
            .title("메세지")
            .content("조커 정보가 저장되었습니다")
            .positiveText("확인")
            .onPositive(new SingleButtonCallback() {
              @Override
              public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                KeyboardUtils.hideSoftInput(getActivity());
                JokerInfoRegisterFragment.this.pop();
              }
            })
            .show();
      }
    }
  };

  @Override
  public boolean onBackPressedSupport() {
    return super.onBackPressedSupport();
  }

  //  custom
  /* ------------------------------------ */
  public static JokerInfoRegisterFragment newInstance() {
    return new JokerInfoRegisterFragment();
  }
  public TextInputLayout makeTextInputLayout() {
    TextInputLayout textInputLayout = new TextInputLayout(getActivity());
    textInputLayout.setId(View.generateViewId());
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    params.topMargin = SizeUtils.dp2px(16);
    textInputLayout.setLayoutParams(params);
    return textInputLayout;
  }
  public TextInputEditText makeTextInputEditText(int position) {
    TextInputEditText editText = new TextInputEditText(getActivity());
    editText.setHint(questions[position]);
    editText.setHintTextColor(getResources().getColor(android.R.color.white));
    editText.setTextSize(SizeUtils.sp2px(8));
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    editText.setLayoutParams(params);
    return editText;
  }
  public Button makeCompleteBtn() {
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    Button btn = new Button(getActivity());
    params.topMargin = SizeUtils.dp2px(16);
    btn.setLayoutParams(params);
    btn.setText(getResources().getText(R.string.complete));
    btn.setOnClickListener(jokerInfoInputCompleteListener);
    return btn;
  }
  public void setCurrentOrder(int order) {
    currentOrder = order;
  }
}