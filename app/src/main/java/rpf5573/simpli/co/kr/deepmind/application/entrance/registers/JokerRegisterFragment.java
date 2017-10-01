package rpf5573.simpli.co.kr.deepmind.application.entrance.registers;

import static android.support.constraint.ConstraintSet.BOTTOM;
import static android.support.constraint.ConstraintSet.RIGHT;
import static android.view.View.GONE;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.orhanobut.logger.Logger;
import me.yokeyword.fragmentation.SupportFragment;
import org.greenrobot.eventbus.EventBus;
import rpf5573.simpli.co.kr.deepmind.R;
import rpf5573.simpli.co.kr.deepmind.helper.hColor;
import rpf5573.simpli.co.kr.deepmind.helper.hStartBrotherEvent;
import rpf5573.simpli.co.kr.deepmind.model.mSettings;

/**
 * Created by mac88 on 2017. 7. 28..
 */

public class JokerRegisterFragment extends SupportFragment {
  /* ------------------------------------------------------------------ */
  //  Property
  /* ------------------------------------------------------------------ */

  //  constant
  /* ------------------------------------ */

  //  view component
  /* ------------------------------------ */
  private View root;
  public TextInputEditText joker1NameEditText;
  public TextInputEditText joker2NameEditText;
  private Button joker1InfoBtn;
  private Button joker2InfoBtn;
  private Button completeBtn;

  //  data
  /* ------------------------------------ */
  private JokerInfoRegisterFragment jokerInfoRegisterFragment;


  /* ------------------------------------------------------------------ */
  //  Function
  /* ------------------------------------------------------------------ */

  // override & init & life cycle
  /* ------------------------------------ */
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.joker_register_fragment, container, false);
  }
  @Override
  public void onStart() {
    super.onStart();
    root = this.getView();
    if ( root != null ) {
      root.setOnClickListener(viewClickListener);
      setup();
    }
  }

  //  setup
  /* ------------------------------------ */
  public void setup() {
    setupInputs();
    setupCompleteBtn();
  }
  public void setupInputs() {
    joker1NameEditText = (TextInputEditText) root.findViewById(R.id.register__joker1NameEditText);
    joker1InfoBtn = (Button)root.findViewById(R.id.register__joker1InfoBtn);
    joker1InfoBtn.setOnClickListener(jokerInfoBtnClickListener);
    joker2NameEditText = (TextInputEditText) root.findViewById(R.id.register__joker2NameEditText);
    joker2InfoBtn = (Button)root.findViewById(R.id.register__joker2InfoBtn);
    joker2InfoBtn.setOnClickListener(jokerInfoBtnClickListener);

    if ( ! mSettings.instance.options.joker_info ) {
      joker1InfoBtn.setVisibility(GONE);
      joker2InfoBtn.setVisibility(GONE);

      ConstraintLayout container1 = (ConstraintLayout)root.findViewById(R.id.register__joker1_container);
      ConstraintLayout container2 = (ConstraintLayout)root.findViewById(R.id.register__joker2_container);

      ConstraintSet set1 = new ConstraintSet();
      set1.clone(container1);
      set1.connect(R.id.register__joker1NameEditText, RIGHT, R.id.register__joker1_container, RIGHT);
      set1.applyTo(container1);

      ConstraintSet set2 = new ConstraintSet();
      set2.clone(container2);
      set2.connect(R.id.register__joker2NameEditText, RIGHT, R.id.register__joker2_container, RIGHT);
      set2.applyTo(container2);
    }

    joker1NameEditText.addTextChangedListener(new MyTextWatcher(joker1NameEditText, getActivity()));
    joker2NameEditText.addTextChangedListener(new MyTextWatcher(joker2NameEditText, getActivity()));
  }
  public void setupCompleteBtn() {
    completeBtn = (Button)root.findViewById(R.id.register__complete);
    completeBtn.setOnClickListener(completeBtnClickListener);
  }

  //  listener
  /* ------------------------------------ */
  View.OnClickListener viewClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      KeyboardUtils.hideSoftInput(getActivity());
    }
  };
  View.OnClickListener jokerInfoBtnClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      if (v.equals(joker1InfoBtn)) {
        Logger.d("joker1InfoBtn Click");
        openJokerInfoFragment(1);
      } else {
        Logger.d("joker2InfoBtn Click");
        openJokerInfoFragment(2);
      }
    }
  };
  public Button.OnClickListener completeBtnClickListener;

  //  custom
  /* ------------------------------------ */
  public void openJokerInfoFragment(int order) {
    KeyboardUtils.hideSoftInput(getActivity());
    if (jokerInfoRegisterFragment == null) {
      jokerInfoRegisterFragment = JokerInfoRegisterFragment.newInstance();
    }
    jokerInfoRegisterFragment.setCurrentOrder(order);
    EventBus.getDefault().post(new hStartBrotherEvent(jokerInfoRegisterFragment));
  }
  public String[] getJokerInfoAnswers(int order) {
    if (jokerInfoRegisterFragment == null) {
      return null;
    }
    return jokerInfoRegisterFragment.answers[order - 1];
  }
  public void setJokerInfoAnswers(int order, String[] answers) {
    if(jokerInfoRegisterFragment == null) {
      jokerInfoRegisterFragment = new JokerInfoRegisterFragment();
    }
    for(int i = 0; i < mSettings.instance.joker_info_questions.length; i++) {
      jokerInfoRegisterFragment.answers[order - 1][i] = answers[i];
    }
  }

}
