package rpf5573.simpli.co.kr.deepmind.application.entrance.registers;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import com.orhanobut.logger.Logger;
import rpf5573.simpli.co.kr.deepmind.R;

/**
 * Created by mac88 on 2017. 9. 11..
 */

public class MyTextWatcher implements TextWatcher {
  private TextInputEditText textInputEditText;
  public Context context;
  public MyTextWatcher( TextInputEditText editText, Context context ) {
    this.context = context;
    this.textInputEditText = editText;
  }
  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
  public void afterTextChanged(Editable editable) {
    int length = editable.length();
    this.validate(length);
  }
  private void validate(int length){
    if(length > 4){
      this.textInputEditText.setError(context.getString(R.string.invalidName));
    }else{
      textInputEditText.setError(null);
    }
  }
}