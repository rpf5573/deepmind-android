package rpf5573.simpli.co.kr.deepmind.model;

/**
 * Created by mac88 on 2017. 8. 7..
 */

public class mPlayer {
  public String name;
  public Boolean is_outed;
  public int outed_by;
  public Boolean is_joker;
  public mJokerInfo joker_info;

  public mPlayer(String name, Boolean isOuted, int outedBy, Boolean isJoker, mJokerInfo jokerInfo) {
    this.name = name;
    this.is_outed = isOuted;
    this.outed_by = outedBy;
    this.is_joker = isJoker;
    this.joker_info = jokerInfo;
  }
}
