package rpf5573.simpli.co.kr.deepmind.helper;

import android.content.Context;
import com.android.volley.toolbox.Volley;

/**
 * Created by mac88 on 2017. 7. 21..
 */

public class hRequestQueue {
  private static com.android.volley.RequestQueue instance = null;
  public static String BASE_URL = "http://www.xn--1-on9eihq3ouuu.com/User";
  public static com.android.volley.RequestQueue getInstance(Context context){
    if(hRequestQueue.instance == null){
      hRequestQueue.instance = Volley.newRequestQueue(context);
    }
    return hRequestQueue.instance;
  }
}
