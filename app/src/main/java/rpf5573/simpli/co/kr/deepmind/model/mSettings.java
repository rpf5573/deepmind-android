package rpf5573.simpli.co.kr.deepmind.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mac88 on 2017. 7. 24..
 */

public class mSettings {
  public static mSettings instance = null;
  public Integer version;
  public Integer total_team_count;
  public mOptions options;
  public String[] joker_info_questions;
  public HashMap<String, Integer> mapping_points;
  public String whole_map_name;
  public int[] team_player_counts;
  public Integer our_team;
  public ArrayList<mBeaconInfo> beacon_infos;
}
