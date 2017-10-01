package rpf5573.simpli.co.kr.deepmind.model;

import java.util.ArrayList;

/**
 * Created by mac88 on 2017. 8. 7..
 */

public class mJokerInfo {
  public ArrayList<Integer> sold_by;
  public String[] answers;

  public mJokerInfo(ArrayList<Integer> soldBy, String[] answers) {
    this.sold_by = soldBy;
    this.answers = answers;
  }

  public Boolean didSoldBy(int team) {
    for(int i = 0; i < sold_by.size(); i++) {
      if (sold_by.get(i).equals(team)) {
        return true;
      }
    }
    return false;
  }
}
