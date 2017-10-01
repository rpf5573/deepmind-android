package rpf5573.simpli.co.kr.deepmind.model;

import com.orhanobut.logger.Logger;
import java.util.ArrayList;

/**
 * Created by mac88 on 2017. 8. 30..
 */

public class mPostCrate {
  public int count;
  public int current_post = 0;
  public ArrayList<Integer> selected_posts;
  public ArrayList<ArrayList<Integer>> hosts_of_each_post;

  public Boolean didPostSelected(int post) {
    if ( selected_posts != null && selected_posts.size() > 0 ) {
      for (int selectedPost : selected_posts) {
        if (post == selectedPost) {
          return true;
        }
      }
    }
    return false;
  }
  public void updateNewPost(int post) {
    int ourTeam = mSettings.instance.our_team;
    // 이전기록 삭제
    if (current_post > 0) { //과거의 기록이 있어야 과거의 기록을 지우지~
      ArrayList<Integer> newHostsOfEachPost = new ArrayList<Integer>();
      ArrayList<Integer> hosts = hosts_of_each_post.get(current_post - 1);
      for(int i = 0; i < hosts.size(); i++) {
        if ( hosts.get(i) == ourTeam ) {
          hosts.remove(i); //주소값으로 연결되어 있으니까, 여기서 삭제해줘도 됩니다
        }
      }
    }

    // 업데이트
    current_post = post;
    selected_posts.add(post);
    hosts_of_each_post.get(post-1).add(ourTeam);
  }
}
